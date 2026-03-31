package app.callcheck.mobile.feature.callintercept;

/**
 * 전화 앱 위에 판정 결과 오버레이를 표시한다.
 *
 * 글로벌 대응:
 * - 모든 UI 텍스트는 SupportedLanguage + SignalSummaryLocalizer를 통해 로컬라이즈
 * - 번호는 raw 형식 유지, 의미 문구만 locale에 맞게 변환
 * - 검색 엔진 이름(Google, Naver 등)은 UI에 절대 노출하지 않음
 *
 * 대상: 미저장 번호만. 저장된 연락처는 이 매니저에 도달하지 않음.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0088\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000bH\u0002J\u0010\u0010\f\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\u000eH\u0002J \u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0018\u0010\u0017\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J<\u0010\u001a\u001a\u00020\u00062\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 2\n\b\u0002\u0010!\u001a\u0004\u0018\u00010\u0014H\u0002J.\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00140#2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0010\u0010$\u001a\u00020%2\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u000e\u0010&\u001a\u00020\'2\u0006\u0010\u0011\u001a\u00020\u0012J\u0010\u0010(\u001a\u00020\'2\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u0018\u0010)\u001a\u00020\b2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010*\u001a\u00020\bH\u0002J\u0012\u0010+\u001a\u0004\u0018\u00010\u00142\u0006\u0010,\u001a\u00020\u0014H\u0002J\u0012\u0010-\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0018\u001a\u00020.H\u0002J \u0010/\u001a\u00020\'2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u00100\u001a\u00020\u00142\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u0006\u00101\u001a\u00020%J\u0018\u00102\u001a\u00020\u00142\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u001d\u001a\u00020\u001eH\u0002J\u0018\u00103\u001a\u00020\u00142\u0006\u00104\u001a\u00020\u00142\u0006\u0010\u001d\u001a\u00020\u001eH\u0002J \u00105\u001a\u0002062\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u00107\u001a\u00020\b2\u0006\u00108\u001a\u00020\bH\u0002J\u0018\u00109\u001a\u0002062\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010*\u001a\u00020\bH\u0002J>\u0010:\u001a\u00020%2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u0013\u001a\u00020\u00142\b\b\u0002\u0010\u001d\u001a\u00020\u001e2\b\b\u0002\u0010\u001f\u001a\u00020 2\n\b\u0002\u0010!\u001a\u0004\u0018\u00010\u0014J\u001c\u0010;\u001a\u00020\'*\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010<\u001a\u00020\bH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006="}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CallerIdOverlayManager;", "", "()V", "mainHandler", "Landroid/os/Handler;", "overlayView", "Landroid/view/View;", "adjustAlpha", "", "color", "factor", "", "backgroundColorForRisk", "riskLevel", "Lapp/callcheck/mobile/core/model/RiskLevel;", "buildActionButtons", "Landroid/widget/LinearLayout;", "context", "Landroid/content/Context;", "phoneNumber", "", "uiText", "Lapp/callcheck/mobile/feature/callintercept/OverlayUiText;", "buildDeviceOneLiner", "evidence", "Lapp/callcheck/mobile/core/model/DeviceEvidence;", "buildOverlayView", "result", "Lapp/callcheck/mobile/core/model/DecisionResult;", "language", "Lapp/callcheck/mobile/feature/countryconfig/SupportedLanguage;", "localizer", "Lapp/callcheck/mobile/feature/countryconfig/SignalSummaryLocalizer;", "phaseLabel", "buildTopReasons", "", "canDrawOverlays", "", "dismissOverlay", "", "dismissOverlayInternal", "dpToPx", "dp", "extractEntityFromSnippet", "snippet", "extractIdentifiedEntity", "Lapp/callcheck/mobile/core/model/SearchEvidence;", "handleOverlayAction", "action", "isOverlayShowing", "localizeRiskLevel", "mapClusterToLocalized", "cluster", "marginStart", "Landroid/widget/LinearLayout$LayoutParams;", "startDp", "topDp", "marginTop", "showOverlay", "addDivider", "textColor", "call-intercept_debug"})
public final class CallerIdOverlayManager {
    @org.jetbrains.annotations.Nullable()
    private android.view.View overlayView;
    @org.jetbrains.annotations.NotNull()
    private final android.os.Handler mainHandler = null;
    
    @javax.inject.Inject()
    public CallerIdOverlayManager() {
        super();
    }
    
    /**
     * 오버레이를 표시한다.
     *
     * @param context Android Context
     * @param result 판정 결과
     * @param phoneNumber raw 번호 (기기 원본 그대로)
     * @param language 현재 기기 언어
     * @param localizer SignalSummary 로컬라이저
     * @param phaseLabel 2-Phase UX 라벨. null이면 표시 안 함.
     *       예: "즉시 판단", "추가 확인됨 — 위험 상승", "추가 확인됨 — 위험 하락"
     */
    public final boolean showOverlay(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.DecisionResult result, @org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.countryconfig.SupportedLanguage language, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.countryconfig.SignalSummaryLocalizer localizer, @org.jetbrains.annotations.Nullable()
    java.lang.String phaseLabel) {
        return false;
    }
    
    public final void dismissOverlay(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    private final void dismissOverlayInternal(android.content.Context context) {
    }
    
    public final boolean isOverlayShowing() {
        return false;
    }
    
    private final boolean canDrawOverlays(android.content.Context context) {
        return false;
    }
    
    /**
     * 1초 인지 오버레이 — 프리미엄 설계.
     *
     * 구조:
     * ┌────────────────────────────────┐
     * │ [색상 배경]                       │
     * │   ██ 한 단어 판정 (24sp Bold)     │
     * │   카테고리 · 번호 · 신뢰도%       │
     * │   ── 구분선 ──                   │
     * │   • 근거 1 (최대 2줄)            │
     * │   • 근거 2                       │
     * │   ── 구분선 ──                   │
     * │   [수신] [거절] [차단]            │
     * └────────────────────────────────┘
     *
     * 1초 인지의 핵심: 색상(배경) + 한 단어(HERO) → 0.3초 판단.
     * 나머지 정보는 보조. 근거는 최대 2줄로 제한.
     */
    private final android.view.View buildOverlayView(android.content.Context context, app.callcheck.mobile.core.model.DecisionResult result, java.lang.String phoneNumber, app.callcheck.mobile.feature.countryconfig.SupportedLanguage language, app.callcheck.mobile.feature.countryconfig.SignalSummaryLocalizer localizer, java.lang.String phaseLabel) {
        return null;
    }
    
    /**
     * 1초 인지를 위한 핵심 근거 추출 — 최대 2줄.
     *
     * 우선순위:
     * 1. 웹 스캔 signal summary (가장 의미 있는 근거)
     * 2. 기기 기록 요약 (통화 이력 기반)
     * 3. 카테고리 기반 fallback
     */
    private final java.util.List<java.lang.String> buildTopReasons(app.callcheck.mobile.core.model.DecisionResult result, app.callcheck.mobile.feature.countryconfig.SupportedLanguage language, app.callcheck.mobile.feature.countryconfig.SignalSummaryLocalizer localizer, app.callcheck.mobile.feature.callintercept.OverlayUiText uiText) {
        return null;
    }
    
    /**
     * 기기 기록 한 줄 요약.
     */
    private final java.lang.String buildDeviceOneLiner(app.callcheck.mobile.core.model.DeviceEvidence evidence, app.callcheck.mobile.feature.callintercept.OverlayUiText uiText) {
        return null;
    }
    
    private final java.lang.String extractIdentifiedEntity(app.callcheck.mobile.core.model.SearchEvidence evidence) {
        return null;
    }
    
    private final java.lang.String extractEntityFromSnippet(java.lang.String snippet) {
        return null;
    }
    
    private final android.widget.LinearLayout buildActionButtons(android.content.Context context, java.lang.String phoneNumber, app.callcheck.mobile.feature.callintercept.OverlayUiText uiText) {
        return null;
    }
    
    @kotlin.Suppress(names = {"DEPRECATION", "MissingPermission"})
    private final void handleOverlayAction(android.content.Context context, java.lang.String action, java.lang.String phoneNumber) {
    }
    
    private final java.lang.String localizeRiskLevel(app.callcheck.mobile.core.model.RiskLevel riskLevel, app.callcheck.mobile.feature.countryconfig.SupportedLanguage language) {
        return null;
    }
    
    private final java.lang.String mapClusterToLocalized(java.lang.String cluster, app.callcheck.mobile.feature.countryconfig.SupportedLanguage language) {
        return null;
    }
    
    private final void addDivider(android.widget.LinearLayout $this$addDivider, android.content.Context context, int textColor) {
    }
    
    private final android.widget.LinearLayout.LayoutParams marginTop(android.content.Context context, int dp) {
        return null;
    }
    
    private final android.widget.LinearLayout.LayoutParams marginStart(android.content.Context context, int startDp, int topDp) {
        return null;
    }
    
    private final int backgroundColorForRisk(app.callcheck.mobile.core.model.RiskLevel riskLevel) {
        return 0;
    }
    
    private final int adjustAlpha(int color, float factor) {
        return 0;
    }
    
    private final int dpToPx(android.content.Context context, int dp) {
        return 0;
    }
}