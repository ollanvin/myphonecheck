package app.myphonecheck.mobile.feature.initialscan.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CallLog
import androidx.core.content.ContextCompat
import app.myphonecheck.mobile.core.globalengine.parsing.phone.PhoneNumberParser
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.data.localcache.entity.CallBaseEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CallLog → CallBaseEntity 정규화 스캐너 (Architecture v2.0.0 §28).
 *
 * 헌법 §2 In-Bound Zero: 통화 본문/녹음 0, E.164 정규화 + 빈도/마지막 시각만.
 */
@Singleton
class CallLogScanner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val phoneParser: PhoneNumberParser,
) {

    fun hasPermission(): Boolean = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_CALL_LOG,
    ) == PackageManager.PERMISSION_GRANTED

    suspend fun scan(simContext: SimContext, limit: Int = 1000): List<CallBaseEntity> =
        withContext(Dispatchers.IO) {
            if (!hasPermission()) return@withContext emptyList()

            val projection = arrayOf(
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
            )
            val sortOrder = "${CallLog.Calls.DATE} DESC LIMIT $limit"
            val cursor = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                sortOrder,
            ) ?: return@withContext emptyList()

            val accumulator = mutableMapOf<String, CallAccumulator>()
            val now = System.currentTimeMillis()
            cursor.use { c ->
                val numberIdx = c.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
                val dateIdx = c.getColumnIndexOrThrow(CallLog.Calls.DATE)
                while (c.moveToNext()) {
                    val raw = c.getString(numberIdx) ?: continue
                    val date = c.getLong(dateIdx)
                    val parsed = phoneParser.parse(raw, simContext)
                    if (!parsed.isValid) continue

                    val existing = accumulator[parsed.e164]
                    accumulator[parsed.e164] = if (existing == null) {
                        CallAccumulator(
                            e164 = parsed.e164,
                            regionCode = parsed.regionCode,
                            numberType = parsed.numberType,
                            count = 1,
                            lastMillis = date,
                            firstSeen = now,
                        )
                    } else {
                        existing.copy(
                            count = existing.count + 1,
                            lastMillis = maxOf(existing.lastMillis, date),
                        )
                    }
                }
            }
            accumulator.values.map {
                CallBaseEntity(
                    e164 = it.e164,
                    regionCode = it.regionCode,
                    callCount = it.count,
                    lastCallMillis = it.lastMillis,
                    numberType = it.numberType,
                    firstSeenMillis = it.firstSeen,
                )
            }
        }

    private data class CallAccumulator(
        val e164: String,
        val regionCode: String,
        val numberType: String,
        val count: Int,
        val lastMillis: Long,
        val firstSeen: Long,
    )
}
