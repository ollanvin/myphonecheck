package app.myphonecheck.mobile.feature.decisionui.components

import app.myphonecheck.mobile.core.globalengine.search.external.ExternalMode

/**
 * 6 Surface 식별자 (v2.5.0 §direct-search-* 정합).
 * UI 레이어가 Surface별 placement context 인지 위해 전달.
 */
enum class SurfaceContext { CALL, MESSAGE, MIC, CAMERA, PUSH, CARD }

/**
 * 사용자가 SimBasedAiMenu에서 선택한 액션.
 *
 * - AiSearch: SIM 기준 AI 모드 선택 (NAVER_AI / GOOGLE_AI_MODE / BING_COPILOT 등)
 * - GenericFallback: AI 미작동 시 PLAIN 모드 fallback (사용자 SIM의 1순위 후보 → PLAIN 변환)
 * - Cancel: 메뉴 닫기, 동작 없음
 */
sealed class DirectSearchAction {
    data class AiSearch(val mode: ExternalMode) : DirectSearchAction()
    object GenericFallback : DirectSearchAction()
    object Cancel : DirectSearchAction()
}
