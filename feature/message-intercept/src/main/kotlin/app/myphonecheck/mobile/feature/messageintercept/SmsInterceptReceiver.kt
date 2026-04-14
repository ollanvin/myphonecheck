package app.myphonecheck.mobile.feature.messageintercept

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import app.myphonecheck.mobile.data.localcache.dao.MessageHubDao
import app.myphonecheck.mobile.data.localcache.entity.MessageHubEntity
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
import java.util.Calendar
import java.util.concurrent.ConcurrentHashMap

/**
 * MessageCheck SMS 수신 리시버.
 *
 * SMS 수신 시 메시지를 분석하여 판단 결과를 생성하고 Room DB에 저장합니다.
 * 자동 차단하지 않으며, 사용자에게 판단 보조 정보만 제공합니다.
 *
 * 동작 방식:
 * 1. SMS_RECEIVED 브로드캐스트 수신
 * 2. 발신자/본문 추출
 * 3. 중복 체크 (같은 발신자 + 같은 본문 + 5초 이내 = skip)
 * 4. MessageTextAnalyzer로 분석
 * 5. MessageCheckEngine으로 판단
 * 6. MessageHubEntity 생성 → Room DB 저장
 *
 * 원칙:
 * - SMS를 차단하거나 가로채지 않음 (abortBroadcast 호출 금지)
 * - 외부 네트워크 전송 금지
 * - 온디바이스 분석만 수행
 *
 * DI 방식:
 * - BroadcastReceiver의 onReceive는 동기 메서드.
 * - goAsync()로 비동기 작업 가능 시간 확보 (~10초).
 * - EntryPointAccessors로 Hilt 그래프에서 DAO 주입.
 */
class SmsInterceptReceiver : BroadcastReceiver() {

    /** Hilt EntryPoint — BroadcastReceiver용 DI 접근점 */
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SmsInterceptEntryPoint {
        fun messageHubDao(): MessageHubDao
    }

    private companion object {
        private const val TAG = "SmsInterceptReceiver"

        /** SMS 출처 식별용 패키지명 */
        private const val SMS_PACKAGE_NAME = "sms"

        /** 중복 방지 윈도우 (밀리초) — 같은 발신자 + 같은 본문이 이 시간 내 재수신되면 skip */
        private const val DEDUP_WINDOW_MS = 5_000L

        /**
         * 중복 방지 맵.
         * key = SHA-256("$sender|$body") 앞 16자
         * value = 마지막 수신 시각 (epoch millis)
         *
         * BroadcastReceiver 인스턴스는 매 수신마다 재생성될 수 있으므로
         * static(companion) 레벨에서 유지한다.
         */
        private val recentMessages = ConcurrentHashMap<String, Long>()
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) return

        // goAsync()로 비동기 작업 시간 확보
        val pendingResult = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        scope.launch {
            try {
                val dao = EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    SmsInterceptEntryPoint::class.java,
                ).messageHubDao()

                for (smsMessage in messages) {
                    val sender = smsMessage.displayOriginatingAddress ?: continue
                    val body = smsMessage.displayMessageBody ?: continue

                    processSms(dao, sender, body)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in SMS processing", e)
            } finally {
                pendingResult.finish()
            }
        }

        // 절대 abortBroadcast() 호출 금지 — SMS 전달을 차단하지 않음
    }

    /**
     * 개별 SMS 분석 및 DB 저장.
     *
     * 1. 중복 체크
     * 2. MessageTextAnalyzer 분석
     * 3. MessageCheckEngine 판단
     * 4. MessageHubEntity 생성 및 저장
     */
    private suspend fun processSms(
        dao: MessageHubDao,
        sender: String,
        body: String,
    ) {
        try {
            val now = System.currentTimeMillis()

            // ── 중복 방지 (SHA-256 축약키) ──
            val dedupKey = sha256Short("$sender|$body")
            val lastSeen = recentMessages[dedupKey]
            if (lastSeen != null && (now - lastSeen) < DEDUP_WINDOW_MS) {
                Log.d(TAG, "[MessageCheck] Dedup skip: sender=$sender (within ${DEDUP_WINDOW_MS}ms)")
                return
            }
            recentMessages[dedupKey] = now
            cleanupDedupMap(now)

            // ── 분석 ──
            val evidence = MessageTextAnalyzer.analyze(
                sender = sender,
                body = body,
                isSavedContact = false,
            )

            val result = MessageCheckEngine.evaluate(evidence)

            // ── 야간 시간대 확인 (22:00 ~ 07:00) ──
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val isNightTime = hour >= 22 || hour < 7

            // ── 링크 탐지 ──
            val detectedLinks = evidence.extractedUrls
            val linkCount = detectedLinks.size

            // ── 차단 여부 확인 ──
            val isBlocked = dao.isBlockedSender(SMS_PACKAGE_NAME)

            // ── MessageHubEntity 생성 ──
            val entity = MessageHubEntity(
                packageName = SMS_PACKAGE_NAME,
                appLabel = sender,
                channelId = null,
                title = sender,
                text = body,
                detectedLinks = if (detectedLinks.isNotEmpty()) {
                    JSONArray(detectedLinks).toString()
                } else null,
                linkCount = linkCount,
                riskLevel = result.riskLevel.name,
                category = result.category.name,
                action = result.action.name,
                confidence = result.confidence,
                summary = result.summary,
                reasons = if (result.reasons.isNotEmpty()) {
                    result.reasons.joinToString("|")
                } else null,
                promotionKeywordHits = 0,
                isNightTime = isNightTime,
                isBlocked = isBlocked,
                receivedAt = now,
            )

            // ── DB 저장 ──
            val insertedId = dao.insert(entity)

            Log.d(
                TAG,
                "[MessageCheck] DB saved: sender=$sender, " +
                    "category=${result.category}, risk=${result.riskLevel}, " +
                    "links=$linkCount, night=$isNightTime, id=$insertedId",
            )

            // TODO: 위험도 HIGH일 시 알림 표시 (POST_NOTIFICATIONS 권한 필요)

        } catch (e: Exception) {
            Log.e(TAG, "[MessageCheck] Error processing SMS from $sender", e)
        }
    }

    /**
     * 중복 방지 맵에서 만료된 엔트리를 정리한다.
     * 메모리 누수 방지를 위해 DEDUP_WINDOW_MS 초과 항목을 제거.
     */
    private fun cleanupDedupMap(now: Long) {
        val iterator = recentMessages.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (now - entry.value > DEDUP_WINDOW_MS * 2) {
                iterator.remove()
            }
        }
    }

    /**
     * SHA-256 해시의 앞 16자를 반환한다.
     * hashCode() 대비 충돌 확률이 극히 낮다.
     */
    private fun sha256Short(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hash.take(8).joinToString("") { "%02x".format(it) }
    }
}
