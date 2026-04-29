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
 * Android Q+ CallScreeningService (Architecture v2.6.0 §11 정합).
 *
 * v2.6.0 §11 3액션 단일 책임 정합 (Stage 3-007 정정):
 *  - **사용자 명시 차단** 누른 번호만 BLOCK 작동 (자동 차단 영구 미포함, §3 정합)
 *  - `setRejectCall(false)` 영구 고정 — 거절은 시스템 dialer 책임 (§11 영구 미포함 액션)
 *  - `setSilenceCall(false)` 영구 고정 — 자동 무음 영구 미포함 (§11)
 *  - BLOCK = `setDisallowCall(true)` + `setSkipCallLog(true)` + `setSkipNotification(true)` 만
 *  - 그 외 = OS 기본 동작 (시스템 dialer 단독 작동, 우리 미간섭)
 *
 * RealTimeActionEngine은 사용자 차단 목록 (BlockListRepository) lookup 결과만 BLOCK 반환.
 * 자동 SILENT/SUSPICIOUS 분기는 본 v2.6.0 정합 이후 작동 안 함 (§11 영구 미포함).
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

        // v2.6.0 §11 정합: 사용자 명시 차단만 BLOCK. 그 외는 OS 기본 (시스템 dialer 단독).
        val response = when (decision.action) {
            ActionType.BLOCK -> CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(false)        // 헌법 §11 영구 고정 (거절은 시스템 dialer 책임)
                .setSilenceCall(false)       // 헌법 §11 영구 고정 (자동 무음 미포함)
                .setSkipCallLog(true)
                .setSkipNotification(true)
                .build()
            else -> CallResponse.Builder().build()  // OS 기본 동작 (시스템 dialer 단독)
        }

        respondToCall(callDetails, response)
    }
}
