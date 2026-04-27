package app.myphonecheck.mobile.feature.callcheck.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CallLog
import androidx.core.content.ContextCompat
import app.myphonecheck.mobile.core.globalengine.parsing.phone.PhoneNumberParser
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android CallLog 읽기 + 코어 PhoneNumberParser 정규화 (Architecture v2.0.0 §21).
 *
 * 헌법 정합:
 *  - 1조 Out-Bound Zero: 디바이스 로컬만.
 *  - 2조 In-Bound Zero: CallLog 영구 저장 안 함 — OS 자원 그대로.
 *  - 8조 SIM-Oriented Single Core: SimContextProvider + PhoneNumberParser만 사용.
 */
@Singleton
class CallLogRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val phoneParser: PhoneNumberParser,
    private val simContextProvider: SimContextProvider,
) {

    fun hasPermission(): Boolean = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_CALL_LOG,
    ) == PackageManager.PERMISSION_GRANTED

    /**
     * 최근 통화 limit 건 조회. 권한 없거나 결과 0건이면 빈 리스트.
     */
    fun readRecentCalls(limit: Int = 100): List<CallEntry> {
        if (!hasPermission()) return emptyList()
        val simContext = simContextProvider.resolve()

        val projection = arrayOf(
            CallLog.Calls.NUMBER,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.TYPE,
        )
        val sortOrder = "${CallLog.Calls.DATE} DESC LIMIT $limit"

        val cursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            null,
            null,
            sortOrder,
        ) ?: return emptyList()

        val entries = mutableListOf<CallEntry>()
        cursor.use { c ->
            val numberIdx = c.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
            val dateIdx = c.getColumnIndexOrThrow(CallLog.Calls.DATE)
            val durIdx = c.getColumnIndexOrThrow(CallLog.Calls.DURATION)
            val typeIdx = c.getColumnIndexOrThrow(CallLog.Calls.TYPE)
            while (c.moveToNext()) {
                val raw = c.getString(numberIdx) ?: ""
                entries += buildEntry(
                    raw = raw,
                    timestamp = c.getLong(dateIdx),
                    duration = c.getLong(durIdx),
                    typeInt = c.getInt(typeIdx),
                    simContext = simContext,
                )
            }
        }
        return entries
    }

    private fun buildEntry(
        raw: String,
        timestamp: Long,
        duration: Long,
        typeInt: Int,
        simContext: SimContext,
    ): CallEntry {
        val parsed = phoneParser.parse(raw, simContext)
        val display = if (parsed.isValid) {
            if (parsed.regionCode == simContext.phoneRegion) parsed.national else parsed.international
        } else {
            raw
        }
        return CallEntry(
            rawNumber = raw,
            displayNumber = display,
            e164 = parsed.e164,
            isValid = parsed.isValid,
            regionCode = parsed.regionCode,
            numberType = parsed.numberType,
            timestampMillis = timestamp,
            durationSeconds = duration,
            direction = mapDirection(typeInt),
        )
    }

    private fun mapDirection(typeInt: Int): CallDirection = when (typeInt) {
        CallLog.Calls.INCOMING_TYPE -> CallDirection.INCOMING
        CallLog.Calls.OUTGOING_TYPE -> CallDirection.OUTGOING
        CallLog.Calls.MISSED_TYPE -> CallDirection.MISSED
        CallLog.Calls.REJECTED_TYPE -> CallDirection.REJECTED
        CallLog.Calls.BLOCKED_TYPE -> CallDirection.BLOCKED
        CallLog.Calls.VOICEMAIL_TYPE -> CallDirection.VOICEMAIL
        else -> CallDirection.UNKNOWN
    }
}
