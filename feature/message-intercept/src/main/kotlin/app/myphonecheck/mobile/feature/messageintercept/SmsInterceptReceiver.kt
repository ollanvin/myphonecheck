package app.myphonecheck.mobile.feature.messageintercept

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import app.myphonecheck.mobile.core.model.ActionRecommendation
import app.myphonecheck.mobile.core.model.ConclusionCategory
import app.myphonecheck.mobile.core.model.DecisionResult
import app.myphonecheck.mobile.core.model.IdentifierAnalysisInput
import app.myphonecheck.mobile.core.model.IdentifierChannel
import app.myphonecheck.mobile.core.model.IdentifierMessageMetadata
import app.myphonecheck.mobile.core.model.ImportanceLevel
import app.myphonecheck.mobile.core.model.RiskLevel
import app.myphonecheck.mobile.core.model.SearchEvidence
import app.myphonecheck.mobile.core.util.PhoneNumberNormalizer
import app.myphonecheck.mobile.data.contacts.ContactsDataSource
import app.myphonecheck.mobile.data.localcache.dao.MessageHubDao
import app.myphonecheck.mobile.data.localcache.entity.MessageHubEntity
import app.myphonecheck.mobile.data.localcache.repository.NumberProfileRepository
import app.myphonecheck.mobile.feature.callintercept.CallInterceptRepository
import app.myphonecheck.mobile.feature.messageintercept.router.IngestRouter
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.security.MessageDigest
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

class SmsInterceptReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SmsInterceptEntryPoint {
        fun messageHubDao(): MessageHubDao
        fun callInterceptRepository(): CallInterceptRepository
        fun contactsDataSource(): ContactsDataSource
        fun numberProfileRepository(): NumberProfileRepository
        fun ingestRouter(): IngestRouter
    }

    private companion object {
        private const val TAG = "SmsInterceptReceiver"
        private const val DEDUP_WINDOW_MS = 5_000L
        private val recentMessages = ConcurrentHashMap<String, Long>()
        private val urlPattern = Regex("""https?://[^\s<>"{}|\\^`\[\]]+""", RegexOption.IGNORE_CASE)
        private val shortLinkDomains = setOf(
            "bit.ly", "t.co", "tinyurl.com", "goo.gl", "ow.ly",
            "is.gd", "buff.ly", "adf.ly", "bl.ink", "rb.gy",
            "han.gl", "me2.do", "vo.la", "url.kr", "zpr.io",
        )
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) return

        val pendingResult = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        scope.launch {
            try {
                val entryPoint = EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    SmsInterceptEntryPoint::class.java,
                )

                for (smsMessage in messages) {
                    val sender = smsMessage.displayOriginatingAddress ?: continue
                    val body = smsMessage.displayMessageBody ?: continue

                    processSms(
                        dao = entryPoint.messageHubDao(),
                        callInterceptRepository = entryPoint.callInterceptRepository(),
                        contactsDataSource = entryPoint.contactsDataSource(),
                        numberProfileRepository = entryPoint.numberProfileRepository(),
                        ingestRouter = entryPoint.ingestRouter(),
                        sender = sender,
                        body = body,
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in SMS processing", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun processSms(
        dao: MessageHubDao,
        callInterceptRepository: CallInterceptRepository,
        contactsDataSource: ContactsDataSource,
        numberProfileRepository: NumberProfileRepository,
        ingestRouter: IngestRouter,
        sender: String,
        body: String,
    ) {
        try {
            val now = System.currentTimeMillis()
            val dedupKey = sha256Short("$sender|$body")
            val lastSeen = recentMessages[dedupKey]
            if (lastSeen != null && now - lastSeen < DEDUP_WINDOW_MS) {
                Log.d(TAG, "Dedup skip sender=$sender")
                return
            }
            recentMessages[dedupKey] = now
            cleanupDedupMap(now)

            val deviceCountry = Locale.getDefault().country.ifBlank { "ZZ" }
            val normalizedNumber = PhoneNumberNormalizer.formatE164(sender, deviceCountry)
            val senderKey = normalizedNumber ?: sender
            val isSavedContact = normalizedNumber?.let { contactsDataSource.isContactSaved(it) } ?: false
            normalizedNumber?.let { numberProfileRepository.touchSmsInteraction(it, now) }
            val actionState = normalizedNumber
                ?.let { numberProfileRepository.getSnapshot(it)?.actionState }

            val detectedLinks = detectLinks(body)
            val longestLinkLength = detectedLinks.maxOfOrNull { it.length } ?: 0
            val hasShortLink = detectedLinks.any { link ->
                shortLinkDomains.any { domain -> link.contains(domain, ignoreCase = true) }
            }

            val decision = if (!isSavedContact && normalizedNumber != null) {
                callInterceptRepository.analyzeIdentifier(
                    IdentifierAnalysisInput(
                        normalizedNumber = normalizedNumber,
                        deviceCountryCode = deviceCountry,
                        channel = IdentifierChannel.SMS,
                        isSavedContact = false,
                        actionState = actionState,
                        messageMetadata = IdentifierMessageMetadata(
                            hasUrl = detectedLinks.isNotEmpty(),
                            urlCount = detectedLinks.size,
                            longestUrlLength = longestLinkLength,
                            hasShortLink = hasShortLink,
                        ),
                    ),
                )
            } else {
                null
            }
            val fallbackDecision = decision.orFallback()
            val searchEvidence = decision?.searchEvidence ?: SearchEvidence.empty()

            val entity = MessageHubEntity(
                packageName = senderKey,
                appLabel = sender,
                channelId = null,
                title = senderKey,
                text = body,
                detectedLinks = detectedLinks
                    .takeIf { it.isNotEmpty() }
                    ?.let { JSONArray(it).toString() },
                linkCount = detectedLinks.size,
                riskLevel = fallbackDecision.riskLevel.name,
                category = fallbackDecision.category.name,
                action = fallbackDecision.action.name,
                confidence = fallbackDecision.confidence,
                importanceLevel = fallbackDecision.importanceLevel.name,
                importanceReason = fallbackDecision.importanceReason,
                summary = decision?.summary ?: if (isSavedContact) {
                    "저장된 연락처입니다"
                } else {
                    "번호 기반 확인 정보가 충분하지 않습니다"
                },
                reasons = buildStructuredMeta(
                    searchSummary = buildSearchSummary(searchEvidence),
                    similarNumberText = buildSimilarNumberText(searchEvidence),
                    linkWarning = buildLinkWarning(
                        linkCount = detectedLinks.size,
                        longestLinkLength = longestLinkLength,
                        hasShortLink = hasShortLink,
                    ),
                ),
                promotionKeywordHits = 0,
                isNightTime = false,
                isBlocked = dao.isBlockedSender(senderKey),
                receivedAt = now,
            )

            dao.insert(entity)
            Log.d(
                TAG,
                "SMS saved sender=$senderKey links=${detectedLinks.size} analyzed=${decision != null}",
            )

            ingestRouter.routeSms(
                senderNumber = sender,
                body = body,
                receivedAt = now,
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error processing SMS from $sender", e)
        }
    }

    private fun detectLinks(text: String): List<String> =
        urlPattern.findAll(text)
            .map { it.value.trimEnd('.', ',', ')', ']', ';', ':') }
            .distinct()
            .toList()

    private fun buildSearchSummary(searchEvidence: SearchEvidence): String? {
        if (searchEvidence.isEmpty) return null
        return searchEvidence.signalSummaries.firstOrNull()?.signalDescription
            ?: searchEvidence.repeatedEntities.firstOrNull()?.let { "$it 관련 검색 기록" }
            ?: searchEvidence.topSnippets.firstOrNull()
            ?: searchEvidence.keywordClusters.firstOrNull()?.let { "검색 키워드 $it 확인" }
    }

    private fun buildSimilarNumberText(searchEvidence: SearchEvidence): String =
        when {
            (searchEvidence.adjacentNumberHint?.resultCount ?: 0) > 0 -> "있음"
            !searchEvidence.isEmpty -> "없음"
            else -> "확인 안 됨"
        }

    private fun buildLinkWarning(
        linkCount: Int,
        longestLinkLength: Int,
        hasShortLink: Boolean,
    ): String? {
        if (linkCount <= 0) return null
        val shortLinkLabel = if (hasShortLink) "단축 링크 포함" else "단축 링크 없음"
        return "링크 ${linkCount}개, 최대 ${longestLinkLength}자, $shortLinkLabel"
    }

    private fun buildStructuredMeta(
        searchSummary: String?,
        similarNumberText: String,
        linkWarning: String?,
    ): String? {
        val tokens = buildList {
            searchSummary?.let { add("SEARCH=${it.replace("|", "/")}") }
            add("SIMILAR=$similarNumberText")
            linkWarning?.let { add("LINK=${it.replace("|", "/")}") }
        }
        return tokens.takeIf { it.isNotEmpty() }?.joinToString("|")
    }

    private fun cleanupDedupMap(now: Long) {
        val iterator = recentMessages.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (now - entry.value > DEDUP_WINDOW_MS * 2) {
                iterator.remove()
            }
        }
    }

    private fun sha256Short(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hash.take(8).joinToString("") { "%02x".format(it) }
    }

    private fun DecisionResult?.orFallback(): DecisionResult = this ?: DecisionResult(
        riskLevel = RiskLevel.UNKNOWN,
        category = ConclusionCategory.INSUFFICIENT_EVIDENCE,
        action = ActionRecommendation.HOLD,
        confidence = 0f,
        summary = "번호 기반 확인 정보가 충분하지 않습니다",
        reasons = emptyList(),
        importanceLevel = ImportanceLevel.UNKNOWN,
        importanceReason = "no_importance_rule_matched",
        deviceEvidence = null,
        searchEvidence = null,
    )
}
