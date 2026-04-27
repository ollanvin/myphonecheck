package app.myphonecheck.mobile.core.globalengine.simcontext

import javax.inject.Inject
import javax.inject.Singleton

/**
 * SIM 변경 감지 (Architecture v2.0.0 §29-4 + 헌법 §8-4).
 *
 * 감지 트리거:
 *  - 부팅 시 SIM 정보 비교 (이전 vs 현재)
 *  - TelephonyManager 콜백 (런타임 SIM 변경, eSIM 전환)
 *
 * 처리 흐름 (헌법 §8-4):
 *  1. SIM 변경 감지 → 사용자 알림
 *  2. 3가지 옵션 사용자 선택:
 *     - A: 새 SIM 컨텍스트 적용 + 베이스데이터 재계산
 *     - B: 기존 SIM 컨텍스트 유지 (해외 임시 SIM, 단기 여행)
 *     - C: 베이스데이터 초기화 + 새 SIM Initial Scan
 *
 * 자동 적용 없음 (헌법 §3 결정권 중앙집중 금지 정합 — 사용자 명시 동의 필수).
 *
 * Stage 2-001: 감지 + 분류 로직만. 실제 사용자 UI·옵션 처리는 후속 Stage.
 * SimContextStorage (이전 SimContext 영구 저장)는 Stage 2-001+ 후속에서 추가.
 */
@Singleton
class SimChangeDetector @Inject constructor(
    private val provider: SimContextProvider,
) {

    /**
     * 현재 SIM과 이전 SIM 비교.
     *
     * @param previous 이전 저장된 SimContext (null = first scan)
     */
    fun detectChange(previous: SimContext?): SimChangeResult {
        val current = provider.resolve()

        return when {
            previous == null -> SimChangeResult.FirstScan(current)
            previous.countryIso != current.countryIso ->
                SimChangeResult.CountryChanged(previous, current)
            previous.mcc != current.mcc || previous.mnc != current.mnc ->
                SimChangeResult.OperatorChanged(previous, current)
            else -> SimChangeResult.Unchanged(current)
        }
    }
}

sealed class SimChangeResult {
    data class FirstScan(val current: SimContext) : SimChangeResult()
    data class CountryChanged(val previous: SimContext, val current: SimContext) : SimChangeResult()
    data class OperatorChanged(val previous: SimContext, val current: SimContext) : SimChangeResult()
    data class Unchanged(val current: SimContext) : SimChangeResult()
}
