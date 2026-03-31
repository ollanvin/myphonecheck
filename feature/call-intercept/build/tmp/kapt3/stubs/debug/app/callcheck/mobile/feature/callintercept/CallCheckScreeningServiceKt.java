package app.callcheck.mobile.feature.callintercept;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u001a\n\u0000\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\"\u0014\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\b\n\u0000\u0012\u0004\b\u0007\u0010\b\"\u000e\u0010\t\u001a\u00020\u0002X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"EMERGENCY_NUMBERS", "", "", "SCREENING_TIMEOUT_MS", "", "SKIP_UI_COMPLETELY", "", "getSKIP_UI_COMPLETELY$annotations", "()V", "TAG", "call-intercept_debug"})
public final class CallCheckScreeningServiceKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "CallCheckScreening";
    private static final long SCREENING_TIMEOUT_MS = 4500L;
    
    /**
     * 긴급번호 목록.
     * 이 번호들은 판정, 알림, 오버레이 모두 완전 스킵.
     * 사용자 안전 최우선 — 어떤 지연도 허용하지 않음.
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> EMERGENCY_NUMBERS = null;
    
    /**
     * UI 정책 상수.
     *
     * SKIP_UI_COMPLETELY: 판정 스킵 시 알림/오버레이도 완전 스킵
     * - 긴급번호: 판정 0ms, 알림 없음, 오버레이 없음
     * - Private/Blocked: 판정 0ms, 알림 없음, 오버레이 없음
     * - Null/Blank: 판정 0ms, 알림 없음, 오버레이 없음
     *
     * 이 정책은 CallCheckScreeningService의 early return 구조로 보장됨:
     * respondAllow() 후 즉시 return → assessThenAllow()(Notification 발행)에 도달하지 않음
     */
    private static final boolean SKIP_UI_COMPLETELY = true;
    
    /**
     * UI 정책 상수.
     *
     * SKIP_UI_COMPLETELY: 판정 스킵 시 알림/오버레이도 완전 스킵
     * - 긴급번호: 판정 0ms, 알림 없음, 오버레이 없음
     * - Private/Blocked: 판정 0ms, 알림 없음, 오버레이 없음
     * - Null/Blank: 판정 0ms, 알림 없음, 오버레이 없음
     *
     * 이 정책은 CallCheckScreeningService의 early return 구조로 보장됨:
     * respondAllow() 후 즉시 return → assessThenAllow()(Notification 발행)에 도달하지 않음
     */
    @kotlin.Suppress(names = {"unused"})
    @java.lang.Deprecated()
    private static void getSKIP_UI_COMPLETELY$annotations() {
    }
}