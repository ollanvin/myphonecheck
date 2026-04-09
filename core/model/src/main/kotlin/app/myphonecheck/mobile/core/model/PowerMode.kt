package app.myphonecheck.mobile.core.model

/**
 * MyPhoneCheck 3단 전력 모드.
 *
 * 목표: "배터리 사용량 0에 가까운 앱"
 *
 * 전환 조건:
 * - SLEEP → IDLE: 전화 수신 BroadcastReceiver 트리거
 * - IDLE → ACTIVE: CallScreeningService.onScreenCall() 진입
 * - ACTIVE → IDLE: 판단 완료 후 5초 대기
 * - IDLE → SLEEP: 30초 무활동
 *
 * 각 모드별 리소스 사용:
 * - SLEEP: CPU 0%, 메모리 최소, 네트워크 0
 * - IDLE: CPU <1%, 인메모리 캐시 유지, Tier 0 lookup 준비
 * - ACTIVE: CPU 필요량, 네트워크 허용, 풀 파이프라인 실행
 */
enum class PowerMode {
    SLEEP,
    IDLE,
    ACTIVE,
    ;

    companion object {
        const val ACTIVE_TO_IDLE_DELAY_MS = 5_000L
        const val IDLE_TO_SLEEP_DELAY_MS = 30_000L
    }
}
