package app.callcheck.mobile.feature.callintercept;

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
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002Jj\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\n2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u000e2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u000e2\b\b\u0002\u0010\u0013\u001a\u00020\n\u00a8\u0006\u0014"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/InterceptPriorityRouter;", "", "()V", "route", "Lapp/callcheck/mobile/core/model/InterceptRoute;", "normalizedNumber", "", "preJudge", "Lapp/callcheck/mobile/core/model/PreJudgeResult;", "isSavedContact", "", "isInternational", "isVoip", "currentHour", "", "recentCallCount", "lastUserAction", "Lapp/callcheck/mobile/core/model/UserCallAction;", "totalAnsweredCount", "countryRiskElevated", "call-intercept_debug"})
public final class InterceptPriorityRouter {
    
    @javax.inject.Inject()
    public InterceptPriorityRouter() {
        super();
    }
    
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
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.core.model.InterceptRoute route(@org.jetbrains.annotations.NotNull()
    java.lang.String normalizedNumber, @org.jetbrains.annotations.Nullable()
    app.callcheck.mobile.core.model.PreJudgeResult preJudge, boolean isSavedContact, boolean isInternational, boolean isVoip, int currentHour, int recentCallCount, @org.jetbrains.annotations.Nullable()
    app.callcheck.mobile.core.model.UserCallAction lastUserAction, int totalAnsweredCount, boolean countryRiskElevated) {
        return null;
    }
}