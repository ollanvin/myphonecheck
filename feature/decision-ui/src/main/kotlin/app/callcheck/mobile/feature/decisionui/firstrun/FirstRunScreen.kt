package app.callcheck.mobile.feature.decisionui.firstrun

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.callcheck.mobile.feature.decisionui.components.ActionButtonRow
import app.callcheck.mobile.feature.decisionui.components.DisclaimerText
import app.callcheck.mobile.feature.decisionui.components.PhoneNumberHeader
import app.callcheck.mobile.feature.decisionui.ring.DecisionRing
import app.callcheck.mobile.feature.decisionui.ring.DecisionRingDefaults
import app.callcheck.mobile.feature.decisionui.ring.RingState
import app.callcheck.mobile.feature.decisionui.theme.CallCheckTheme
import kotlinx.coroutines.delay

/**
 * 첫 실행 체험 화면.
 *
 * 설명 0. 슬라이드 0. 텍스트 소개 0.
 * 앱 열자마자 가짜 전화 상황을 즉시 연출.
 *
 * 플로우:
 *   1. 번호 등장 + Ring LOADING (3색 그래디언트 회전)
 *   2. 1.5초 후 → DANGER 결과 전환 (빨강 펄스)
 *   3. 0.6초 후 → 액션 버튼 페이드인
 *   4. 사용자가 아무 버튼 누름 → 메인 진입
 *
 * @param onComplete 체험 완료 콜백. 메인 화면으로 전환.
 */
@Composable
fun FirstRunScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // ── 상태 관리 ──
    var phase by remember { mutableStateOf(FirstRunPhase.LOADING) }
    var showButtons by remember { mutableStateOf(false) }
    var showHeader by remember { mutableStateOf(false) }

    // ── 자동 전환 타이머 ──
    LaunchedEffect(Unit) {
        // Phase 1: 번호 등장 (즉시)
        showHeader = true

        // Phase 2: 분석 대기
        delay(FakeCallScenario.LOADING_DURATION_MS)

        // Phase 3: 결과 전환
        phase = FirstRunPhase.RESULT

        // Phase 4: 버튼 등장
        delay(FakeCallScenario.BUTTON_FADE_DELAY_MS)
        showButtons = true
    }

    // ── 현재 상태에 따른 Ring 설정 ──
    val ringState = when (phase) {
        FirstRunPhase.LOADING -> RingState.LOADING
        FirstRunPhase.RESULT -> RingState.DANGER
    }

    val scenario = FakeCallScenario

    // ── UI ──
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CallCheckTheme.colors.darkBackground),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // ── 상단: 전화번호 (페이드인) ──
            AnimatedVisibility(
                visible = showHeader,
                enter = fadeIn(animationSpec = tween(400)),
            ) {
                PhoneNumberHeader(
                    phoneNumber = scenario.FAKE_PHONE_NUMBER,
                    modifier = Modifier,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── 중앙: Decision Ring ──
            DecisionRing(
                state = ringState,
                size = DecisionRingDefaults.DECISION_CARD_SIZE,
            ) {
                // Ring 내부 컨텐츠
                when (phase) {
                    FirstRunPhase.LOADING -> {
                        // 로딩 중: 분석 메시지
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "분석 중",
                                color = CallCheckTheme.colors.textPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "판단 근거 수집 중...",
                                color = CallCheckTheme.colors.textSecondary,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    FirstRunPhase.RESULT -> {
                        // 결과: 판단 요약 + 이유
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 12.dp),
                        ) {
                            Text(
                                text = scenario.dangerResult.summary,
                                color = CallCheckTheme.colors.riskCritical,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = 26.sp,
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            scenario.dangerResult.reasons.take(3).forEach { reason ->
                                Text(
                                    text = "· $reason",
                                    color = CallCheckTheme.colors.textSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 2.dp),
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── 하단: 액션 버튼 (슬라이드 + 페이드인) ──
            AnimatedVisibility(
                visible = showButtons,
                enter = fadeIn(animationSpec = tween(500)) +
                    slideInVertically(
                        animationSpec = tween(500),
                        initialOffsetY = { it / 4 },
                    ),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    ActionButtonRow(
                        recommendation = scenario.dangerResult.action,
                        onReject = onComplete,
                        onBlock = onComplete,
                        onDetail = onComplete,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DisclaimerText(
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

/**
 * 첫 실행 체험 단계.
 */
private enum class FirstRunPhase {
    /** 분석 진행 중. Ring LOADING. */
    LOADING,

    /** 결과 노출. Ring DANGER. */
    RESULT,
}

// ============================================================
// Preview
// ============================================================

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0F0F0F,
    widthDp = 380,
    heightDp = 800,
    name = "First Run - Full Flow",
)
@Composable
private fun FirstRunScreenPreview() {
    CallCheckTheme {
        FirstRunScreen(
            onComplete = {},
        )
    }
}
