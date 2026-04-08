package app.callcheck.mobile.core.model

import android.content.Context
import app.callcheck.mobile.core.model.R

/**
 * RingSystem — CallCheck 전체 제품의 단일 상태 소스 (Single Source of Truth).
 *
 * 모든 UI 접점(앱 내부, 오버레이, 알림, 위젯)은 이 객체만 참조합니다.
 * 색상, 라벨, 설명이 여기서 결정되면 전체 제품이 동일하게 표현됩니다.
 *
 * 설계 원칙:
 * - Compose 의존성 없음 (ARGB Int 기반)
 * - Compose 레이어에서는 확장 함수로 Color 변환
 * - 앱, 오버레이, 알림, 위젯 전부 동일 값 사용
 *
 * 색상 체계 (디자인 시스템 V1 기준):
 * - Primary Triad: Safe(#4CAF50), Caution(#FFC107), Danger(#F44336)
 * - Extended: Loading(3색 그래디언트), Unknown(#808080)
 */
object RingSystem {

    // ============================================================
    // 상태별 ARGB 색상 (0xAARRGGBB)
    // ============================================================

    /** Safe: 초록. 안전 추정. */
    const val COLOR_SAFE: Int = 0xFF4CAF50.toInt()

    /** Caution: 노랑. 주의 필요. */
    const val COLOR_CAUTION: Int = 0xFFFFC107.toInt()

    /** Danger: 빨강. 위험 감지. */
    const val COLOR_DANGER: Int = 0xFFF44336.toInt()

    /** Unknown: 회색. 판단 근거 부족. */
    const val COLOR_UNKNOWN: Int = 0xFF808080.toInt()

    /** Loading: 시안. 분석 진행 중 (단색 필요 시). */
    const val COLOR_LOADING: Int = 0xFF00BCD4.toInt()

    // ============================================================
    // 상태별 라벨 (Context 기반 다국어)
    // ============================================================

    /**
     * 현재 언어 설정에 따른 RiskLevel 라벨.
     * 알림, 위젯, 오버레이에서 동일하게 사용.
     * Android 문자열 리소스에서 로드됨.
     */
    fun label(context: Context, riskLevel: RiskLevel): String = when (riskLevel) {
        RiskLevel.LOW -> context.getString(R.string.ring_risk_low)
        RiskLevel.MEDIUM -> context.getString(R.string.ring_risk_medium)
        RiskLevel.HIGH -> context.getString(R.string.ring_risk_high)
        RiskLevel.UNKNOWN -> context.getString(R.string.ring_risk_unknown)
    }

    /**
     * 상태별 한국어 라벨 (레거시).
     * labelKo는 더 이상 사용되지 않습니다. label(context, riskLevel)을 사용하세요.
     *
     * @deprecated label(Context, RiskLevel)을 사용하세요
     */
    @Deprecated("Use label(context, riskLevel) instead", replaceWith = ReplaceWith("label(context, riskLevel)"))
    fun labelKo(riskLevel: RiskLevel): String = when (riskLevel) {
        RiskLevel.LOW -> "안전 추정"
        RiskLevel.MEDIUM -> "주의"
        RiskLevel.HIGH -> "위험 높음"
        RiskLevel.UNKNOWN -> "판단 불가"
    }

    /**
     * 상태별 영어 라벨 (레거시).
     * labelEn은 더 이상 사용되지 않습니다. label(context, riskLevel)을 사용하세요.
     *
     * @deprecated label(Context, RiskLevel)을 사용하세요
     */
    @Deprecated("Use label(context, riskLevel) instead", replaceWith = ReplaceWith("label(context, riskLevel)"))
    fun labelEn(riskLevel: RiskLevel): String = when (riskLevel) {
        RiskLevel.LOW -> "Likely Safe"
        RiskLevel.MEDIUM -> "Caution"
        RiskLevel.HIGH -> "High Risk"
        RiskLevel.UNKNOWN -> "Unknown"
    }

    // ============================================================
    // 색상 매핑
    // ============================================================

    /**
     * RiskLevel에 대응하는 ARGB 색상 반환.
     *
     * @deprecated color(ActionRecommendation)을 사용하세요.
     * riskLevel은 중간값이므로 action 기반이 정확합니다.
     */
    fun color(riskLevel: RiskLevel): Int = when (riskLevel) {
        RiskLevel.LOW -> COLOR_SAFE
        RiskLevel.MEDIUM -> COLOR_CAUTION
        RiskLevel.HIGH -> COLOR_DANGER
        RiskLevel.UNKNOWN -> COLOR_UNKNOWN
    }

    /**
     * ActionRecommendation에 대응하는 ARGB 색상 반환.
     * 모든 UI 접점의 주 색상 소스.
     */
    fun color(action: ActionRecommendation): Int = when (action) {
        ActionRecommendation.ANSWER -> COLOR_SAFE
        ActionRecommendation.ANSWER_WITH_CAUTION -> COLOR_CAUTION
        ActionRecommendation.REJECT -> COLOR_DANGER
        ActionRecommendation.BLOCK_REVIEW -> COLOR_DANGER
        ActionRecommendation.HOLD -> COLOR_UNKNOWN
    }

    // ============================================================
    // ActionRecommendation 기반 라벨 (Context 기반 다국어)
    // ============================================================

    /**
     * ActionRecommendation에 대응하는 현재 언어 라벨.
     * 모든 UI 접점에서 동일하게 사용.
     * Android 문자열 리소스에서 로드됨.
     */
    fun label(context: Context, action: ActionRecommendation): String = when (action) {
        ActionRecommendation.ANSWER -> context.getString(R.string.ring_action_answer)
        ActionRecommendation.ANSWER_WITH_CAUTION -> context.getString(R.string.ring_action_answer_caution)
        ActionRecommendation.REJECT -> context.getString(R.string.ring_action_reject)
        ActionRecommendation.BLOCK_REVIEW -> context.getString(R.string.ring_action_block_review)
        ActionRecommendation.HOLD -> context.getString(R.string.ring_action_hold)
    }

    /**
     * ActionRecommendation에 대응하는 한국어 라벨 (레거시).
     * labelKo는 더 이상 사용되지 않습니다. label(context, action)을 사용하세요.
     *
     * @deprecated label(Context, ActionRecommendation)을 사용하세요
     */
    @Deprecated("Use label(context, action) instead", replaceWith = ReplaceWith("label(context, action)"))
    fun labelKo(action: ActionRecommendation): String = when (action) {
        ActionRecommendation.ANSWER -> "안전 추정"
        ActionRecommendation.ANSWER_WITH_CAUTION -> "주의 권고"
        ActionRecommendation.REJECT -> "위험 의심"
        ActionRecommendation.BLOCK_REVIEW -> "고위험 감지"
        ActionRecommendation.HOLD -> "판단 보류"
    }

    /**
     * ActionRecommendation에 대응하는 영어 라벨 (레거시).
     * labelEn은 더 이상 사용되지 않습니다. label(context, action)을 사용하세요.
     *
     * @deprecated label(Context, ActionRecommendation)을 사용하세요
     */
    @Deprecated("Use label(context, action) instead", replaceWith = ReplaceWith("label(context, action)"))
    fun labelEn(action: ActionRecommendation): String = when (action) {
        ActionRecommendation.ANSWER -> "Likely Safe"
        ActionRecommendation.ANSWER_WITH_CAUTION -> "Caution Advised"
        ActionRecommendation.REJECT -> "Risk Suspected"
        ActionRecommendation.BLOCK_REVIEW -> "High Risk Detected"
        ActionRecommendation.HOLD -> "Pending Review"
    }

    // ============================================================
    // ActionRecommendation 기반 이모지 (전 접점 통일)
    // ============================================================

    /**
     * ActionRecommendation에 대응하는 상태 이모지.
     * 알림 제목, 위젯 텍스트에서 사용.
     */
    fun emoji(action: ActionRecommendation): String = when (action) {
        ActionRecommendation.ANSWER -> "\uD83D\uDFE2"                // 🟢
        ActionRecommendation.ANSWER_WITH_CAUTION -> "\uD83D\uDFE1"  // 🟡
        ActionRecommendation.REJECT -> "\uD83D\uDD34"                // 🔴
        ActionRecommendation.BLOCK_REVIEW -> "\uD83D\uDD34"          // 🔴
        ActionRecommendation.HOLD -> "\u26AA"                         // ⚪
    }

    // ============================================================
    // 면책 문구 (Context 기반 다국어)
    // ============================================================

    /**
     * 현재 언어 설정에 따른 면책 문구.
     * 모든 UI 접점에서 동일하게 노출.
     * Android 문자열 리소스에서 로드됨.
     */
    fun disclaimer(context: Context): String =
        context.getString(R.string.ring_disclaimer)

    /**
     * 현재 언어 설정에 따른 상세 면책 문구.
     * 앱 내부 상세 화면용.
     * Android 문자열 리소스에서 로드됨.
     */
    fun disclaimerDetail(context: Context): String =
        context.getString(R.string.ring_disclaimer_detail)

    /**
     * 면책 문구 (한국어, 레거시).
     * DISCLAIMER_KO는 더 이상 사용되지 않습니다. disclaimer(context)를 사용하세요.
     *
     * @deprecated disclaimer(Context)를 사용하세요
     */
    @Deprecated("Use disclaimer(context) instead", replaceWith = ReplaceWith("disclaimer(context)"))
    const val DISCLAIMER_KO: String =
        "이 앱은 판단을 돕습니다. 최종 선택은 사용자에게 있습니다."

    /**
     * 면책 문구 (영어, 레거시).
     * DISCLAIMER_EN은 더 이상 사용되지 않습니다. disclaimer(context)를 사용하세요.
     *
     * @deprecated disclaimer(Context)를 사용하세요
     */
    @Deprecated("Use disclaimer(context) instead", replaceWith = ReplaceWith("disclaimer(context)"))
    const val DISCLAIMER_EN: String =
        "This app assists your judgment. The final decision is yours."

    /**
     * 상세 면책 문구 (한국어, 레거시).
     * DISCLAIMER_DETAIL_KO는 더 이상 사용되지 않습니다. disclaimerDetail(context)를 사용하세요.
     *
     * @deprecated disclaimerDetail(Context)를 사용하세요
     */
    @Deprecated("Use disclaimerDetail(context) instead", replaceWith = ReplaceWith("disclaimerDetail(context)"))
    const val DISCLAIMER_DETAIL_KO: String =
        "이 결과는 디바이스 이력 및 웹 검색 기반 요약이며 정확성을 보장하지 않습니다. " +
        "CallCheck는 판단 보조 도구이며, 통화 수신·거절·차단의 최종 결정은 사용자에게 있습니다."

    /**
     * 상세 면책 문구 (영어, 레거시).
     * DISCLAIMER_DETAIL_EN은 더 이상 사용되지 않습니다. disclaimerDetail(context)를 사용하세요.
     *
     * @deprecated disclaimerDetail(Context)를 사용하세요
     */
    @Deprecated("Use disclaimerDetail(context) instead", replaceWith = ReplaceWith("disclaimerDetail(context)"))
    const val DISCLAIMER_DETAIL_EN: String =
        "This result is based on device history and web search analysis. Accuracy is not guaranteed. " +
        "CallCheck is a decision-support tool. The final decision to answer, reject, or block is yours."

    // ============================================================
    // 알림용 이모지 (텍스트 기반 접점)
    // ============================================================

    /**
     * RiskLevel에 대응하는 상태 이모지.
     * 알림 제목, 위젯 텍스트에서 색상 대신 사용.
     */
    fun emoji(riskLevel: RiskLevel): String = when (riskLevel) {
        RiskLevel.LOW -> "\uD83D\uDFE2"    // 🟢
        RiskLevel.MEDIUM -> "\uD83D\uDFE1" // 🟡
        RiskLevel.HIGH -> "\uD83D\uDD34"    // 🔴
        RiskLevel.UNKNOWN -> "\u26AA"        // ⚪
    }
}
