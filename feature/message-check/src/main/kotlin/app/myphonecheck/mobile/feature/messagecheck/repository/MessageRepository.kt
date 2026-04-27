package app.myphonecheck.mobile.feature.messagecheck.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Telephony
import androidx.core.content.ContextCompat
import app.myphonecheck.mobile.core.globalengine.parsing.message.MessageClassifier
import app.myphonecheck.mobile.core.globalengine.parsing.message.MessageParseResult
import app.myphonecheck.mobile.core.globalengine.parsing.message.SenderRegistry
import app.myphonecheck.mobile.core.globalengine.parsing.message.SmsPatternExtractor
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SMS Inbox 읽기 + 코어 SmsPatternExtractor + MessageClassifier 정규화 (Architecture v2.0.0 §22).
 *
 * 헌법 정합:
 *  - 1조 Out-Bound Zero: 디바이스 로컬만.
 *  - 2조 In-Bound Zero: SMS 본문 영구 저장 0 — 메모리 처리 + UI 일부 스니펫만.
 *  - 8조 SIM-Oriented Single Core: SimContextProvider + 코어 분류기만 사용.
 */
@Singleton
class MessageRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val patternExtractor: SmsPatternExtractor,
    private val classifier: MessageClassifier,
    private val senderRegistry: SenderRegistry,
    private val simContextProvider: SimContextProvider,
) {

    fun hasPermission(): Boolean = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_SMS,
    ) == PackageManager.PERMISSION_GRANTED

    /**
     * SMS Inbox 최근 limit 건 조회 + 분류. SenderRegistry 갱신.
     * 권한 없거나 결과 0건이면 빈 리스트.
     */
    fun readRecentMessages(limit: Int = 200): List<MessageEntry> {
        if (!hasPermission()) return emptyList()
        val simContext = simContextProvider.resolve()
        senderRegistry.clear()

        val projection = arrayOf(
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE,
        )
        val sortOrder = "${Telephony.Sms.DATE} DESC LIMIT $limit"

        val cursor = context.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            projection,
            null,
            null,
            sortOrder,
        ) ?: return emptyList()

        val entries = mutableListOf<MessageEntry>()
        cursor.use { c ->
            val addressIdx = c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
            val bodyIdx = c.getColumnIndexOrThrow(Telephony.Sms.BODY)
            val dateIdx = c.getColumnIndexOrThrow(Telephony.Sms.DATE)
            while (c.moveToNext()) {
                val sender = c.getString(addressIdx) ?: ""
                val body = c.getString(bodyIdx) ?: ""
                val date = c.getLong(dateIdx)

                val features = patternExtractor.extract(sender, body, simContext)
                val category = classifier.classify(features)
                val result = MessageParseResult(features, category)
                senderRegistry.record(result, date)

                entries += MessageEntry(
                    sender = sender,
                    bodySnippet = body.take(SNIPPET_MAX_LEN),
                    timestampMillis = date,
                    category = category,
                    hasUrl = features.hasUrl,
                    isShortSender = features.isShortSender,
                )
            }
        }
        return entries
    }

    fun senderInventory() = senderRegistry.snapshot()

    private companion object {
        const val SNIPPET_MAX_LEN = 80
    }
}
