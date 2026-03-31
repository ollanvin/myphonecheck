package app.callcheck.mobile.feature.callintercept;

/**
 * Orchestrates the full call intercept decision pipeline.
 *
 * Stage 9 아키텍처:
 * 1. InterceptPriorityRouter — 분석 깊이 결정 (SKIP/INSTANT/LIGHT/FULL)
 * 2. CountryInterceptPolicy — 국가별 정책 적용
 * 3. 2-Phase Scoring — Phase 1 즉시 판단 + Phase 2 확정 판단
 * 4. InterceptOutcomeLearner — 사용자 행동 기반 학습 루프
 *
 * Total target: 3 seconds optimal, 4.5-second hard limit.
 * Returns partial results if search enrichment times out.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J \u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0007J \u0010\b\u001a\u00020\t2\u0006\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0007\u00a8\u0006\n"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CallInterceptRepository;", "", "processIncomingCall", "Lapp/callcheck/mobile/core/model/DecisionResult;", "normalizedNumber", "", "deviceCountryCode", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "processIncomingCallTwoPhase", "Lapp/callcheck/mobile/core/model/TwoPhaseDecision;", "call-intercept_debug"})
public abstract interface CallInterceptRepository {
    
    /**
     * 수신 전화 처리 (기존 호환).
     * TwoPhaseDecision의 finalResult()를 반환.
     *
     * @param normalizedNumber  비교/검색용 canonical 번호
     * @param deviceCountryCode 기기 탐지 국가 코드 (ISO 3166-1 alpha-2).
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object processIncomingCall(@org.jetbrains.annotations.NotNull()
    java.lang.String normalizedNumber, @org.jetbrains.annotations.Nullable()
    java.lang.String deviceCountryCode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.core.model.DecisionResult> $completion);
    
    /**
     * 수신 전화 처리 (2-Phase).
     * Phase 1 즉시 판단 + Phase 2 확정 판단을 모두 포함.
     *
     * @param normalizedNumber  비교/검색용 canonical 번호
     * @param deviceCountryCode 기기 탐지 국가 코드 (ISO 3166-1 alpha-2).
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object processIncomingCallTwoPhase(@org.jetbrains.annotations.NotNull()
    java.lang.String normalizedNumber, @org.jetbrains.annotations.Nullable()
    java.lang.String deviceCountryCode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.core.model.TwoPhaseDecision> $completion);
}