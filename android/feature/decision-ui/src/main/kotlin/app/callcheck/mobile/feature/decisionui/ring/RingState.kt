package app.callcheck.mobile.feature.decisionui.ring

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.callcheck.mobile.core.model.ActionRecommendation
import app.callcheck.mobile.core.model.RiskLevel

/**
 * Decision Ring의 5가지 상태.
 *
 * 설계 철학:
 *   행성 = 전화 / 궤도 = 판단 / 색 = 결과
 *
 * 각 상태는 고유한 시각적 표현(색, 두께, 글로우, 애니메이션)을 가진다.
 * UI에 노출되는 색은 Primary Triad 3색(Safe/Caution/Danger) 기준.
 */
enum class RingState {
    /**
     * 판단 진행 중.
     * 3색 그래디언트(Safe→Caution→Danger) 회전.
     * 결과가 아직 없으므로 모든 가능성을 시각화.
     */
    LOADING,

    /**
     * 안전 판단. 수신 권장.
     * 초록(#4CAF50) 고정 + 부드러운 시계방향 회전.
     */
    SAFE,

    /**
     * 주의 필요. 거절 권장.
     * 노랑(#FFC107) 고정 + 느린 펄스.
     */
    CAUTION,

    /**
     * 위험 감지. 차단 권장.
     * 빨강(#F44336) 고정 + 빠른 펄스 + 진동.
     */
    DANGER,

    /**
     * 판단 근거 부족.
     * 회색(#808080) 점선 회전.
     */
    UNKNOWN;

    companion object {
        /**
         * RiskLevel → RingState 매핑.
         * 엔진 결과(4단계)를 UI 상태(5단계)로 변환.
         *
         * @deprecated fromAction()을 사용하세요.
         * riskLevel은 중간값이므로 action 기반 매핑이 정확합니다.
         * 하위 호환을 위해 유지하지만, 신규 코드에서는 fromAction() 사용.
         */
        fun fromRiskLevel(riskLevel: RiskLevel): RingState = when (riskLevel) {
            RiskLevel.LOW -> SAFE
            RiskLevel.MEDIUM -> CAUTION
            RiskLevel.HIGH -> DANGER
            RiskLevel.UNKNOWN -> UNKNOWN
        }

        /**
         * ActionRecommendation → RingState 매핑.
         *
         * ActionRecommendation은 Decision Engine의 최종 출력입니다.
         * Category + RiskLevel 조합을 반영하므로, riskLevel 단독보다 정확합니다.
         *
         * 예: DELIVERY_LIKELY + LOW → action=CAUTION → RingState.CAUTION (노랑)
         *     riskLevel만 보면 LOW → SAFE (초록) — 이것은 잘못된 표현
         *
         * 모든 UI 접점(Ring, Overlay, Widget, Notification)은 이 함수를 사용해야 합니다.
         */
        fun fromAction(action: ActionRecommendation): RingState = when (action) {
            ActionRecommendation.SAFE_LIKELY -> SAFE
            ActionRecommendation.CAUTION -> CAUTION
            ActionRecommendation.RISK_HIGH -> DANGER
            ActionRecommendation.UNKNOWN -> UNKNOWN
        }
    }
}

/**
 * RingState별 시각적 설정 값.
 *
 * 디자인 시스템 V1 Section 3.3 기준:
 * - ringColor: 링 색상
 * - ringWidth: 링 두께 (상태별 가변)
 * - glowRadius: 외곽 글로우 반경
 * - glowAlpha: 글로우 투명도
 * - rotationDurationMs: 회전 애니메이션 주기 (0 = 회전 없음)
 * - pulseDurationMs: 펄스 애니메이션 주기 (0 = 펄스 없음)
 * - pulseMinAlpha: 펄스 최소 투명도
 * - pulseMaxAlpha: 펄스 최대 투명도
 * - pulseMinScale: 펄스 최소 스케일 (Danger 전용)
 * - pulseMaxScale: 펄스 최대 스케일 (Danger 전용)
 * - isDashed: 점선 여부 (Unknown 전용)
 */
data class RingStateConfig(
    val ringColor: Color,
    val ringWidth: Dp,
    val glowRadius: Dp,
    val glowAlpha: Float,
    val rotationDurationMs: Int,
    val pulseDurationMs: Int,
    val pulseMinAlpha: Float,
    val pulseMaxAlpha: Float,
    val pulseMinScale: Float,
    val pulseMaxScale: Float,
    val isDashed: Boolean,
)

/**
 * 상태별 설정 레지스트리.
 * 디자인 시스템 V1 Section 3.3 + Section 7.1 정확 반영.
 */
object RingStateConfigs {

    /** Loading: 3색 그래디언트 — 개별 색상 대신 gradientColors 사용 */
    val gradientColors = listOf(
        Color(0xFF4CAF50), // Safe Green
        Color(0xFFFFC107), // Caution Amber
        Color(0xFFF44336), // Danger Red
        Color(0xFF4CAF50), // Safe Green (루프 연결)
    )

    private val configs = mapOf(
        RingState.LOADING to RingStateConfig(
            ringColor = Color.Transparent, // 그래디언트 사용
            ringWidth = 5.dp,
            glowRadius = 10.dp,
            glowAlpha = 0.35f,
            rotationDurationMs = 2000,
            pulseDurationMs = 0,
            pulseMinAlpha = 1f,
            pulseMaxAlpha = 1f,
            pulseMinScale = 1f,
            pulseMaxScale = 1f,
            isDashed = false,
        ),
        RingState.SAFE to RingStateConfig(
            ringColor = Color(0xFF4CAF50),
            ringWidth = 4.dp,
            glowRadius = 8.dp,
            glowAlpha = 0.30f,
            rotationDurationMs = 3000,
            pulseDurationMs = 0,
            pulseMinAlpha = 1f,
            pulseMaxAlpha = 1f,
            pulseMinScale = 1f,
            pulseMaxScale = 1f,
            isDashed = false,
        ),
        RingState.CAUTION to RingStateConfig(
            ringColor = Color(0xFFFFC107),
            ringWidth = 5.dp,
            glowRadius = 12.dp,
            glowAlpha = 0.40f,
            rotationDurationMs = 0,
            pulseDurationMs = 1500,
            pulseMinAlpha = 0.70f,
            pulseMaxAlpha = 1f,
            pulseMinScale = 1f,
            pulseMaxScale = 1f,
            isDashed = false,
        ),
        RingState.DANGER to RingStateConfig(
            ringColor = Color(0xFFF44336),
            ringWidth = 6.dp,
            glowRadius = 16.dp,
            glowAlpha = 0.50f,
            rotationDurationMs = 0,
            pulseDurationMs = 800,
            pulseMinAlpha = 0.60f,
            pulseMaxAlpha = 1f,
            pulseMinScale = 0.97f,
            pulseMaxScale = 1.03f,
            isDashed = false,
        ),
        RingState.UNKNOWN to RingStateConfig(
            ringColor = Color(0xFF808080),
            ringWidth = 3.dp,
            glowRadius = 0.dp,
            glowAlpha = 0f,
            rotationDurationMs = 2000,
            pulseDurationMs = 0,
            pulseMinAlpha = 1f,
            pulseMaxAlpha = 1f,
            pulseMinScale = 1f,
            pulseMaxScale = 1f,
            isDashed = true,
        ),
    )

    /**
     * 상태별 설정 조회.
     * 등록되지 않은 상태는 존재하지 않으므로 강제 반환.
     */
    fun get(state: RingState): RingStateConfig = configs.getValue(state)
}
