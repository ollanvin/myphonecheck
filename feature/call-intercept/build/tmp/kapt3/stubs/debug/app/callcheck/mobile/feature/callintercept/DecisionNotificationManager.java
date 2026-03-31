package app.callcheck.mobile.feature.callintercept;

/**
 * 판단 재료 Notification 매니저.
 *
 * 제품 철학: 행동 대행이 아니라 판단 재료 노출.
 *
 * 색상 체계: [RingSystem] 단일 소스 참조.
 * - SAFE_LIKELY → 초록 (#4CAF50)
 * - CAUTION    → 노랑 (#FFC107)
 * - RISK_HIGH  → 빨강 (#F44336)
 * - UNKNOWN    → 회색 (#808080)
 *
 * Notification 구성:
 * - 제목: 이모지 + 판단 상태 + 전화번호
 * - 내용: 카테고리 요약 (한 줄 결론)
 * - 확장: 근거 최대 3개
 * - 액션: 거절 / 차단 / 자세히 보기
 * - 면책: RingSystem.DISCLAIMER_KO
 *
 * "수신(Answer)" 버튼은 의도적으로 제외.
 * 수신 행동은 시스템 콜 UI가 담당합니다.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J2\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000eH\u0002J \u0010\u000f\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0002J(\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\n2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0013\u001a\u00020\fH\u0002J\u0016\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\nJ\u0010\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u0010\u0010\u0017\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\nH\u0002J(\u0010\u0018\u001a\u00020\u00152\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\r\u001a\u00020\u000eJ\u0016\u0010\u0019\u001a\u00020\u00152\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\n\u00a8\u0006\u001a"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/DecisionNotificationManager;", "", "()V", "buildDecisionNotification", "Landroid/app/Notification;", "context", "Landroid/content/Context;", "result", "Lapp/callcheck/mobile/core/model/DecisionResult;", "phoneNumber", "", "notificationId", "", "phaseUpgraded", "", "buildTimeoutNotification", "createActionPI", "Landroid/app/PendingIntent;", "action", "requestCode", "dismissNotification", "", "ensureChannel", "generateId", "showDecisionNotification", "showTimeoutNotification", "call-intercept_debug"})
public final class DecisionNotificationManager {
    
    @javax.inject.Inject()
    public DecisionNotificationManager() {
        super();
    }
    
    /**
     * 판단 결과 Notification 표시.
     *
     * @param phaseUpgraded Phase 2에서 위험도가 상승한 경우 true.
     *       true이면 "추가 확인됨" 강화 문구를 Notification에 추가.
     */
    public final void showDecisionNotification(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.DecisionResult result, @org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, boolean phaseUpgraded) {
    }
    
    public final void showTimeoutNotification(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber) {
    }
    
    public final void dismissNotification(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber) {
    }
    
    private final android.app.Notification buildDecisionNotification(android.content.Context context, app.callcheck.mobile.core.model.DecisionResult result, java.lang.String phoneNumber, int notificationId, boolean phaseUpgraded) {
        return null;
    }
    
    private final android.app.Notification buildTimeoutNotification(android.content.Context context, java.lang.String phoneNumber, int notificationId) {
        return null;
    }
    
    private final android.app.PendingIntent createActionPI(android.content.Context context, java.lang.String action, java.lang.String phoneNumber, int requestCode) {
        return null;
    }
    
    private final void ensureChannel(android.content.Context context) {
    }
    
    private final int generateId(java.lang.String phoneNumber) {
        return 0;
    }
}