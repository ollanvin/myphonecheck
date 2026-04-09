package app.myphonecheck.mobile.feature.decisionui.ring

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Decision Ring — MyPhoneCheck의 핵심 엔진 UI.
 *
 * 설계 철학: "행성 = 전화 / 궤도 = 판단 / 색 = 결과"
 *
 * 이 컴포넌트는 전화 수신 시 판단 결과를 원형 궤도로 시각화한다.
 * RingState에 따라 색상, 두께, 글로우, 애니메이션이 자동으로 결정된다.
 *
 * 사용법:
 * ```
 * DecisionRing(
 *     state = RingState.SAFE,
 *     size = DecisionRingDefaults.DECISION_CARD_SIZE,
 * ) {
 *     // 중앙 컨텐츠: 전화번호, 판단 요약 등
 *     PhoneNumberHeader(...)
 *     ConclusionText(...)
 * }
 * ```
 *
 * 크기 변형:
 * - Decision Card: 280dp (전체 판단 화면)
 * - Home: 200dp (홈 대시보드)
 * - History: 24dp (히스토리 아이템)
 *
 * @param state 현재 링 상태 (LOADING, SAFE, CAUTION, DANGER, UNKNOWN)
 * @param size 링 전체 크기 (직경)
 * @param modifier 외부 Modifier
 * @param content 링 중앙에 배치될 컨텐츠 슬롯
 */
@Composable
fun DecisionRing(
    state: RingState,
    size: Dp,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val config = remember(state) { RingStateConfigs.get(state) }
    val density = LocalDensity.current

    // ---- 애니메이션 계산 ----
    val infiniteTransition = rememberInfiniteTransition(label = "ring_animation")

    // 회전 애니메이션 (LOADING, SAFE, UNKNOWN)
    val rotationAngle by if (config.rotationDurationMs > 0) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = config.rotationDurationMs,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Restart,
            ),
            label = "ring_rotation",
        )
    } else {
        animateFloatAsState(
            targetValue = 0f,
            label = "ring_rotation_static",
        )
    }

    // 펄스 알파 애니메이션 (CAUTION, DANGER)
    val pulseAlpha by if (config.pulseDurationMs > 0) {
        infiniteTransition.animateFloat(
            initialValue = config.pulseMinAlpha,
            targetValue = config.pulseMaxAlpha,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = config.pulseDurationMs,
                    easing = androidx.compose.animation.core.FastOutSlowInEasing,
                ),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "ring_pulse_alpha",
        )
    } else {
        animateFloatAsState(
            targetValue = 1f,
            label = "ring_pulse_alpha_static",
        )
    }

    // 펄스 스케일 애니메이션 (DANGER 전용)
    val pulseScale by if (config.pulseMinScale != config.pulseMaxScale) {
        infiniteTransition.animateFloat(
            initialValue = config.pulseMinScale,
            targetValue = config.pulseMaxScale,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = config.pulseDurationMs,
                    easing = androidx.compose.animation.core.FastOutSlowInEasing,
                ),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "ring_pulse_scale",
        )
    } else {
        animateFloatAsState(
            targetValue = 1f,
            label = "ring_pulse_scale_static",
        )
    }

    // 상태 전환 시 색상 페이드 (Loading→결과 전환 시 600ms/400ms)
    val transitionAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = when (state) {
                RingState.DANGER -> 400
                else -> 600
            },
            easing = androidx.compose.animation.core.FastOutSlowInEasing,
        ),
        label = "ring_transition_alpha",
    )

    // ---- 렌더링 ----
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        // Ring Canvas (배경 레이어)
        Canvas(
            modifier = Modifier.size(size),
        ) {
            val ringWidthPx = with(density) { config.ringWidth.toPx() }
            val glowRadiusPx = with(density) { config.glowRadius.toPx() }

            // 글로우 효과 렌더링 (glowRadius > 0 일 때만)
            if (glowRadiusPx > 0f && config.glowAlpha > 0f) {
                drawRingGlow(
                    state = state,
                    config = config,
                    ringWidthPx = ringWidthPx,
                    glowRadiusPx = glowRadiusPx,
                    alpha = pulseAlpha * config.glowAlpha * transitionAlpha,
                    rotationAngle = rotationAngle,
                )
            }

            // 메인 링 렌더링
            scale(pulseScale) {
                rotate(rotationAngle) {
                    drawRing(
                        state = state,
                        config = config,
                        ringWidthPx = ringWidthPx,
                        alpha = pulseAlpha * transitionAlpha,
                    )
                }
            }
        }

        // 중앙 컨텐츠 슬롯 (전경 레이어)
        Box(
            modifier = Modifier
                .size(size - config.ringWidth * 2 - 16.dp), // 링 안쪽 여백
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}

// ============================================================
// Private 렌더링 함수
// ============================================================

/**
 * 메인 링 그리기.
 * 상태에 따라 단색 Arc, 그래디언트 SweepGradient, 또는 점선을 렌더링.
 */
private fun DrawScope.drawRing(
    state: RingState,
    config: RingStateConfig,
    ringWidthPx: Float,
    alpha: Float,
) {
    val strokeInset = ringWidthPx / 2f
    val arcSize = Size(
        width = size.width - ringWidthPx,
        height = size.height - ringWidthPx,
    )
    val topLeft = Offset(strokeInset, strokeInset)

    when (state) {
        RingState.LOADING -> {
            // 3색 그래디언트 SweepGradient
            val brush = Brush.sweepGradient(
                colors = RingStateConfigs.gradientColors,
                center = Offset(size.width / 2f, size.height / 2f),
            )
            drawArc(
                brush = brush,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                alpha = alpha,
                style = Stroke(
                    width = ringWidthPx,
                    cap = StrokeCap.Round,
                ),
            )
        }

        RingState.UNKNOWN -> {
            // 점선 링
            val dashEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(
                    ringWidthPx * 3f, // dash 길이
                    ringWidthPx * 2f, // gap 길이
                ),
                phase = 0f,
            )
            drawArc(
                color = config.ringColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                alpha = alpha,
                style = Stroke(
                    width = ringWidthPx,
                    cap = StrokeCap.Round,
                    pathEffect = dashEffect,
                ),
            )
        }

        else -> {
            // 단색 원형 링 (SAFE, CAUTION, DANGER)
            drawArc(
                color = config.ringColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                alpha = alpha,
                style = Stroke(
                    width = ringWidthPx,
                    cap = StrokeCap.Round,
                ),
            )
        }
    }
}

/**
 * 링 외곽 글로우 효과.
 * 링보다 넓고 투명한 추가 원호를 그려서 발광 효과 구현.
 */
private fun DrawScope.drawRingGlow(
    state: RingState,
    config: RingStateConfig,
    ringWidthPx: Float,
    glowRadiusPx: Float,
    alpha: Float,
    rotationAngle: Float,
) {
    val glowStrokeWidth = ringWidthPx + glowRadiusPx * 2
    val strokeInset = glowStrokeWidth / 2f
    val arcSize = Size(
        width = size.width - glowStrokeWidth,
        height = size.height - glowStrokeWidth,
    )
    val topLeft = Offset(strokeInset, strokeInset)

    when (state) {
        RingState.LOADING -> {
            rotate(rotationAngle) {
                val brush = Brush.sweepGradient(
                    colors = RingStateConfigs.gradientColors.map {
                        it.copy(alpha = alpha)
                    },
                    center = Offset(size.width / 2f, size.height / 2f),
                )
                drawArc(
                    brush = brush,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(
                        width = glowStrokeWidth,
                        cap = StrokeCap.Round,
                    ),
                )
            }
        }

        else -> {
            drawArc(
                color = config.ringColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                alpha = alpha,
                style = Stroke(
                    width = glowStrokeWidth,
                    cap = StrokeCap.Round,
                ),
            )
        }
    }
}
