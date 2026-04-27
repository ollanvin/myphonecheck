package app.myphonecheck.mobile.feature.initialscan.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Telephony
import androidx.core.content.ContextCompat
import app.myphonecheck.mobile.core.globalengine.parsing.message.MessageCategory
import app.myphonecheck.mobile.core.globalengine.parsing.message.MessageClassifier
import app.myphonecheck.mobile.core.globalengine.parsing.message.SmsPatternExtractor
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.data.localcache.entity.SmsBaseEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SMS Inbox → SmsBaseEntity 빈도/우세 카테고리 (Architecture v2.0.0 §28).
 *
 * 헌법 §2 In-Bound Zero: SMS 본문 0, 발신자별 메타데이터 + 우세 카테고리.
 */
@Singleton
class SmsInboxScanner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val patternExtractor: SmsPatternExtractor,
    private val classifier: MessageClassifier,
) {

    fun hasPermission(): Boolean = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_SMS,
    ) == PackageManager.PERMISSION_GRANTED

    suspend fun scan(simContext: SimContext, limit: Int = 2000): List<SmsBaseEntity> =
        withContext(Dispatchers.IO) {
            if (!hasPermission()) return@withContext emptyList()

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
            ) ?: return@withContext emptyList()

            val accumulator = mutableMapOf<String, SenderAccumulator>()
            cursor.use { c ->
                val addressIdx = c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
                val bodyIdx = c.getColumnIndexOrThrow(Telephony.Sms.BODY)
                val dateIdx = c.getColumnIndexOrThrow(Telephony.Sms.DATE)
                while (c.moveToNext()) {
                    val sender = c.getString(addressIdx) ?: continue
                    val body = c.getString(bodyIdx).orEmpty()
                    val date = c.getLong(dateIdx)

                    val features = patternExtractor.extract(sender, body, simContext)
                    val category = classifier.classify(features)

                    val existing = accumulator[sender]
                    accumulator[sender] = if (existing == null) {
                        SenderAccumulator(
                            sender = sender,
                            isShort = features.isShortSender,
                            count = 1,
                            lastMillis = date,
                            categoryCounts = mutableMapOf(category to 1),
                        )
                    } else {
                        existing.categoryCounts[category] =
                            (existing.categoryCounts[category] ?: 0) + 1
                        existing.copy(
                            count = existing.count + 1,
                            lastMillis = maxOf(existing.lastMillis, date),
                        )
                    }
                }
            }
            accumulator.values.map {
                val dominant = it.categoryCounts.maxByOrNull { e -> e.value }?.key ?: MessageCategory.NORMAL
                SmsBaseEntity(
                    sender = it.sender,
                    isShortSender = it.isShort,
                    messageCount = it.count,
                    lastSeenMillis = it.lastMillis,
                    dominantCategory = dominant.name,
                )
            }
        }

    private data class SenderAccumulator(
        val sender: String,
        val isShort: Boolean,
        val count: Int,
        val lastMillis: Long,
        val categoryCounts: MutableMap<MessageCategory, Int>,
    )
}
