package app.callcheck.mobile.feature.callintercept;

/**
 * UserCallRecord → LocalLearningSignal 변환.
 *
 * Room DB에서 번호별 사용자 과거 행동을 조회하고,
 * DecisionEngine이 이해할 수 있는 LocalLearningSignal로 변환.
 *
 * 성능: Room 조회 1회 (인덱스 기반, <5ms)
 * 프라이버시: 온디바이스 전용, 서버 전송 없음.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0018\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/LocalLearningProvider;", "", "userCallRecordRepository", "Lapp/callcheck/mobile/data/localcache/repository/UserCallRecordRepository;", "(Lapp/callcheck/mobile/data/localcache/repository/UserCallRecordRepository;)V", "getSignal", "Lapp/callcheck/mobile/core/model/LocalLearningSignal;", "canonicalNumber", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "call-intercept_debug"})
public final class LocalLearningProvider {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.data.localcache.repository.UserCallRecordRepository userCallRecordRepository = null;
    
    @javax.inject.Inject()
    public LocalLearningProvider(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.localcache.repository.UserCallRecordRepository userCallRecordRepository) {
        super();
    }
    
    /**
     * 번호에 대한 로컬 학습 신호를 조회.
     *
     * @param canonicalNumber E.164 정규화 번호
     * @return LocalLearningSignal (기록 없으면 null)
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getSignal(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.core.model.LocalLearningSignal> $completion) {
        return null;
    }
}