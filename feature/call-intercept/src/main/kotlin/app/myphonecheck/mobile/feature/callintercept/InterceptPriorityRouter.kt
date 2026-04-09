package app.myphonecheck.mobile.feature.callintercept

import android.util.Log
import app.myphonecheck.mobile.core.model.InterceptRoute
import app.myphonecheck.mobile.core.model.PreJudgeResult
import app.myphonecheck.mobile.core.model.UserCallAction
import app.myphonecheck.mobile.data.localcache.repository.PreJudgeCacheRepository
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "InterceptPriorityRouter"

/**
 * 인터셉트 우선순위 라우터.
 *
 * 전화 수신 순간 (onScreenCall → processIncomingCall 진입 직후)
 * 번호의 특성을 빠르게 평가하여 "어느 깊이로 분석할지" 결정.
 *
 * 입력: 정규화된 번호, 국가, PreJudge 캐시, 반복 이력
 * 출력: InterceptRoute (SKIP/INSTANT/LIGHT/FULL)
 *
 * 성능: < 1ms (Room lookup은 호출자가 먼저 수행)
 * 100% 온디바이스, 서버 전송 없음.
 *
 * 우선순위 규칙 (위에서부터 먼저 매칭되면 반환):
 *
 * ┌─────────────────────────────────────────────────────────────┐
 * │ Priority │ Condition                        │ Route         │
 * ├──────────┼──────────────────────────────────┼───────────────┤
 * │ P0       │ 긴급번호/비공개/null              │ SKIP          │
 * │ P1       │ 저장 연락처 (PreJudge hit+safe)   │ INSTANT       │
 * │ P2       │ PreJudge hit + fresh + usable     │ INSTANT       │
 * │ P3       │ 반복 안전 (answered 3+)           │ INSTANT       │
 * │ P4       │ 심야+국제+반복미수신              │ FULL          │
 * │ P5       │ VoIP 또는 위험 국가 정책          │ FULL          │
 * │ P6       │ PreJudge hit + decayed            │ FULL          │
 * │ P7       │ 국내+주간+첫수신                  │ LIGHT         │
 * │ P8       │ 그 외                             │ FULL          │
 * └──────────┴──────────────────────────────────┴───────────────┘
 */
@Singleton
class InterceptPriorityRouter @Inject constructor() {

    /**
     * 수신 번호의 인터셉트 경로를 결정.
     *
     * @param normalizedNumber E.164 정규화 번호
     * @param preJudge Tier 0 PreJudge 캐시 결과 (null = 캐시 miss)
     * @param isSavedContact 저장된 연락처 여부 (DeviceEvidence에서 빠르게 판단 가능)
     * @param isInternational 국제 전화 여부
     * @param isVoip VoIP 경로 여부
     * @param currentHour 현재 시간 (0~23)
     * @param recentCallCount 최근 1시간 내 이 번호의 수신 횟수
     * @param lastUserAction 마지막 사용자 행동 (answered/rejected/blocked/missed)
     * @param totalAnsweredCount 이 번호에 대한 총 수신 횟수
     * @param countryRiskElevated 국가별 정책에서 위험 가중 대상인지
     */
    fun route(
        normalizedNumber: String,
        preJudge: PreJudgeResult?,
        isSavedContact: Boolean = false,
        isInternational: Boolean = false,
        isVoip: Boolean = false,
        currentHour: Int = 12,
        recentCallCount: Int = 0,
        lastUserAction: UserCallAction? = null,
        totalAnsweredCount: Int = 0,
        countryRiskElevated: Boolean = false,
    ): InterceptRoute {
        // P1: 저장 연락처 + PreJudge에서 안전 판정
        if (isSavedContact) {
            Log.d(TAG, "P1 INSTANT: saved contact $normalizedNumber")
            return InterceptRoute.INSTANT
        }

        // P2: PreJudge hit + fresh + usable
        if (preJudge != null && preJudge.isUsable()) {
            val ageDays = (System.currentTimeMillis() - preJudge.lastJudgedAtMs) / (24 * 60 * 60 * 1000f)
            if (ageDays <= PreJudgeResult.FRESH_DAYS) {
                Log.d(TAG, "P2 INSTANT: fresh PreJudge hit (${ageDays}d, conf=${preJudge.effectiveConfidence()})")
                return InterceptRoute.INSTANT
            }
        }

        // P3: 반복 안전 — 사용자가 3회 이상 수신한 번호
        if (totalAnsweredCount >= 3 && lastUserAction == UserCallAction.ANSWERED) {
            Log.d(TAG, "P3 INSTANT: repeat safe pattern (answered=${totalAnsweredCount})")
            return InterceptRoute.INSTANT
        }

        // P4: 심야 + 국제 + 반복 미수신 → 고위험 경로
        val isMidnight = currentHour in 0..5
        val isRecentMissed = lastUserAction == UserCallAction.MISSED || lastUserAction == UserCallAction.REJECTED
        if (isMidnight && isInternational && (recentCallCount >= 2 || isRecentMissed)) {
            Log.d(TAG, "P4 FULL: midnight+international+repeat/missed")
            return InterceptRoute.FULL
        }

        // P5: VoIP 또는 국가별 위험 가중
        if (isVoip || countryRiskElevated) {
            Log.d(TAG, "P5 FULL: VoIP=$isVoip, countryRisk=$countryRiskElevated")
            return InterceptRoute.FULL
        }

        // P6: PreJudge hit이지만 decay됨 → 재평가 필요
        if (preJudge != null && preJudge.isUsable()) {
            // Fresh가 아닌 usable = decayed 상태 → 풀 파이프라인으로 갱신
            Log.d(TAG, "P6 FULL: decayed PreJudge (conf=${preJudge.effectiveConfidence()})")
            return InterceptRoute.FULL
        }

        // P7: 국내 + 주간 + 첫 수신 → 경량 판단
        val isDaytime = currentHour in 8..21
        val isFirstCall = preJudge == null && totalAnsweredCount == 0 && recentCallCount == 0
        if (!isInternational && isDaytime && isFirstCall) {
            Log.d(TAG, "P7 LIGHT: domestic+daytime+first")
            return InterceptRoute.LIGHT
        }

        // P8: 나머지 전부 → 풀 파이프라인
        Log.d(TAG, "P8 FULL: default")
        return InterceptRoute.FULL
    }
}
