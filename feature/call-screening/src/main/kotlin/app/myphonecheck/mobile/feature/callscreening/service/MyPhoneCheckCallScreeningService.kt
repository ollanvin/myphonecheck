package app.myphonecheck.mobile.feature.callscreening.service

import android.telecom.Call
import android.telecom.CallScreeningService
import app.myphonecheck.mobile.core.globalengine.decision.ActionType
import app.myphonecheck.mobile.core.globalengine.decision.RealTimeActionEngine
import app.myphonecheck.mobile.core.globalengine.parsing.phone.PhoneNumberParser
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Android Q+ CallScreeningService (Architecture v2.1.0 §31-3).
 *
 * 수신 통화 → SimContext 정규화 + RealTimeActionEngine 50ms 결정 → CallResponse.
 *
 * BLOCK = setDisallowCall(true) + setRejectCall(true) + setSkipNotification(true)
 *   → 벨 0회 또는 1회 미만 즉시 종료 (헌법 §3 사용자 차단 목록 기반).
 * SILENT = setSilenceCall(true) → 무음 처리, CallLog 보존.
 * 그 외 = OS 기본 동작.
 */
@AndroidEntryPoint
class MyPhoneCheckCallScreeningService : CallScreeningService() {

    @Inject lateinit var phoneParser: PhoneNumberParser
    @Inject lateinit var simContextProvider: SimContextProvider
    @Inject lateinit var actionEngine: RealTimeActionEngine

    override fun onScreenCall(callDetails: Call.Details) {
        val rawNumber = callDetails.handle?.schemeSpecificPart
        val key = if (!rawNumber.isNullOrEmpty()) {
            val parsed = phoneParser.parse(rawNumber, simContextProvider.resolve())
            if (parsed.isValid) parsed.e164 else rawNumber
        } else {
            ""
        }

        // CallScreeningService timeout 5s — RealTimeActionEngine 50ms 자체 timeout 적용.
        val decision = runBlocking { actionEngine.decideForCall(key) }

        val response = when (decision.action) {
            ActionType.BLOCK -> CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipNotification(true)
                .setSkipCallLog(false)  // CallLog는 남김 — 사용자 검토 가능 (헌법 §5).
                .build()
            ActionType.SILENT -> CallResponse.Builder()
                .setSilenceCall(true)
                .build()
            else -> CallResponse.Builder().build()
        }

        respondToCall(callDetails, response)
    }
}
