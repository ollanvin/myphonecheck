package app.callcheck.mobile.data.localcache.repository;

/**
 * 사용자 통화 기록 Repository.
 *
 * 비즈니스 로직 계층:
 * - DAO 직접 호출 방지
 * - upsert 로직 (존재하면 업데이트, 없으면 신규 생성)
 * - 타입 안전 (UserCallTag, UserCallAction enum 사용)
 *
 * 서버 동기화: 없음 (온디바이스 전용)
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\t\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\tJ\u000e\u0010\n\u001a\u00020\u0006H\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u0016\u0010\f\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\tJ\u0018\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u0007\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\tJ\u000e\u0010\u000f\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u0012\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u00130\u0012J\u0012\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u00130\u0012J\u0016\u0010\u0015\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000e0\u00122\u0006\u0010\u0007\u001a\u00020\bJ\u001a\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u00130\u00122\u0006\u0010\u0017\u001a\u00020\u0018J\u0012\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u00130\u0012J>\u0010\u001a\u001a\u00020\u000e2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u001b\u001a\u00020\b2\u0006\u0010\u001c\u001a\u00020\u001d2\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\u001f\u001a\u0004\u0018\u00010\bH\u0086@\u00a2\u0006\u0002\u0010 J\u001e\u0010!\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\"\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010#J\u001e\u0010$\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0017\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010%R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006&"}, d2 = {"Lapp/callcheck/mobile/data/localcache/repository/UserCallRecordRepository;", "", "dao", "Lapp/callcheck/mobile/data/localcache/dao/UserCallRecordDao;", "(Lapp/callcheck/mobile/data/localcache/dao/UserCallRecordDao;)V", "blockNumber", "", "canonicalNumber", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAllRecords", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteRecord", "findByNumber", "Lapp/callcheck/mobile/data/localcache/entity/UserCallRecord;", "getRecordCount", "", "observeAll", "Lkotlinx/coroutines/flow/Flow;", "", "observeBlockedNumbers", "observeByNumber", "observeByTag", "tag", "Lapp/callcheck/mobile/core/model/UserCallTag;", "observeWithMemos", "recordCall", "displayNumber", "action", "Lapp/callcheck/mobile/core/model/UserCallAction;", "aiRiskLevel", "aiCategory", "(Ljava/lang/String;Ljava/lang/String;Lapp/callcheck/mobile/core/model/UserCallAction;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveMemo", "memo", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveTag", "(Ljava/lang/String;Lapp/callcheck/mobile/core/model/UserCallTag;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "local-cache_debug"})
public final class UserCallRecordRepository {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.data.localcache.dao.UserCallRecordDao dao = null;
    
    @javax.inject.Inject()
    public UserCallRecordRepository(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.localcache.dao.UserCallRecordDao dao) {
        super();
    }
    
    /**
     * 번호로 기록 조회
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object findByNumber(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.data.localcache.entity.UserCallRecord> $completion) {
        return null;
    }
    
    /**
     * 번호 실시간 관찰
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<app.callcheck.mobile.data.localcache.entity.UserCallRecord> observeByNumber(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber) {
        return null;
    }
    
    /**
     * 전체 기록 관찰 (최신순)
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<app.callcheck.mobile.data.localcache.entity.UserCallRecord>> observeAll() {
        return null;
    }
    
    /**
     * 태그별 필터
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<app.callcheck.mobile.data.localcache.entity.UserCallRecord>> observeByTag(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.UserCallTag tag) {
        return null;
    }
    
    /**
     * 차단 목록
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<app.callcheck.mobile.data.localcache.entity.UserCallRecord>> observeBlockedNumbers() {
        return null;
    }
    
    /**
     * 메모가 있는 기록만
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<app.callcheck.mobile.data.localcache.entity.UserCallRecord>> observeWithMemos() {
        return null;
    }
    
    /**
     * 전체 기록 수
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getRecordCount(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    /**
     * 통화 기록 저장 (upsert).
     *
     * - 이미 존재하면: callCount 증가 + lastAction/aiRiskLevel 업데이트
     * - 신규면: 새 레코드 생성
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object recordCall(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String displayNumber, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.UserCallAction action, @org.jetbrains.annotations.Nullable()
    java.lang.String aiRiskLevel, @org.jetbrains.annotations.Nullable()
    java.lang.String aiCategory, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.data.localcache.entity.UserCallRecord> $completion) {
        return null;
    }
    
    /**
     * 메모 저장
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object saveMemo(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String memo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * 태그 저장
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object saveTag(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.UserCallTag tag, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * 번호 차단
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object blockNumber(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * 기록 삭제
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteRecord(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * 전체 삭제 (사용자 명시적 요청)
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteAllRecords(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}