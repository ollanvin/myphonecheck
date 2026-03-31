package app.callcheck.mobile.data.localcache.dao;

/**
 * 사용자 통화 기록 DAO.
 *
 * 모든 쿼리는 로컬 전용. 서버 동기화 없음.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0010\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0018\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\u0004J \u0010\r\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u0014\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00130\u0012H\'J\u0014\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00130\u0012H\'J\u0018\u0010\u0015\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\u00122\u0006\u0010\u0006\u001a\u00020\u0007H\'J\u001c\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00130\u00122\u0006\u0010\u0017\u001a\u00020\u0007H\'J\u0014\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00130\u0012H\'J\u0016\u0010\u0019\u001a\u00020\u00032\u0006\u0010\u001a\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u001bJ(\u0010\u001c\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u001d\u001a\u00020\u00072\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u00a7@\u00a2\u0006\u0002\u0010\u001eJ(\u0010\u001f\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010 \u001a\u00020\u00072\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u00a7@\u00a2\u0006\u0002\u0010\u001eJ(\u0010!\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0017\u001a\u00020\u00072\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u00a7@\u00a2\u0006\u0002\u0010\u001eJ\u0016\u0010\"\u001a\u00020\u000f2\u0006\u0010\u001a\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u001b\u00a8\u0006#"}, d2 = {"Lapp/callcheck/mobile/data/localcache/dao/UserCallRecordDao;", "", "deleteAll", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteByNumber", "canonicalNumber", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "findByNumber", "Lapp/callcheck/mobile/data/localcache/entity/UserCallRecord;", "getRecordCount", "", "incrementCallCount", "updatedAt", "", "(Ljava/lang/String;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "observeAll", "Lkotlinx/coroutines/flow/Flow;", "", "observeBlockedNumbers", "observeByNumber", "observeByTag", "tag", "observeWithMemos", "update", "record", "(Lapp/callcheck/mobile/data/localcache/entity/UserCallRecord;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateAction", "action", "(Ljava/lang/String;Ljava/lang/String;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateMemo", "memo", "updateTag", "upsert", "local-cache_debug"})
@androidx.room.Dao()
public abstract interface UserCallRecordDao {
    
    /**
     * 정규화 번호로 단건 조회
     */
    @androidx.room.Query(value = "SELECT * FROM user_call_records WHERE canonical_number = :canonicalNumber LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object findByNumber(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.data.localcache.entity.UserCallRecord> $completion);
    
    /**
     * 정규화 번호로 실시간 관찰
     */
    @androidx.room.Query(value = "SELECT * FROM user_call_records WHERE canonical_number = :canonicalNumber LIMIT 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<app.callcheck.mobile.data.localcache.entity.UserCallRecord> observeByNumber(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber);
    
    /**
     * 전체 기록 (최신순)
     */
    @androidx.room.Query(value = "SELECT * FROM user_call_records ORDER BY updated_at DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<app.callcheck.mobile.data.localcache.entity.UserCallRecord>> observeAll();
    
    /**
     * 태그별 필터
     */
    @androidx.room.Query(value = "SELECT * FROM user_call_records WHERE tag = :tag ORDER BY updated_at DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<app.callcheck.mobile.data.localcache.entity.UserCallRecord>> observeByTag(@org.jetbrains.annotations.NotNull()
    java.lang.String tag);
    
    /**
     * 차단 목록
     */
    @androidx.room.Query(value = "SELECT * FROM user_call_records WHERE last_action = \'blocked\' ORDER BY updated_at DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<app.callcheck.mobile.data.localcache.entity.UserCallRecord>> observeBlockedNumbers();
    
    /**
     * 전체 기록 수
     */
    @androidx.room.Query(value = "SELECT COUNT(*) FROM user_call_records")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getRecordCount(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    /**
     * 메모가 있는 기록만
     */
    @androidx.room.Query(value = "SELECT * FROM user_call_records WHERE memo IS NOT NULL AND memo != \'\' ORDER BY updated_at DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<app.callcheck.mobile.data.localcache.entity.UserCallRecord>> observeWithMemos();
    
    /**
     * 새 기록 삽입 (충돌 시 교체)
     */
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object upsert(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.localcache.entity.UserCallRecord record, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    /**
     * 기존 기록 업데이트
     */
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.localcache.entity.UserCallRecord record, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * 메모만 업데이트
     */
    @androidx.room.Query(value = "UPDATE user_call_records SET memo = :memo, updated_at = :updatedAt WHERE canonical_number = :canonicalNumber")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateMemo(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String memo, long updatedAt, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * 태그만 업데이트
     */
    @androidx.room.Query(value = "UPDATE user_call_records SET tag = :tag, updated_at = :updatedAt WHERE canonical_number = :canonicalNumber")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateTag(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String tag, long updatedAt, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * 행동만 업데이트
     */
    @androidx.room.Query(value = "UPDATE user_call_records SET last_action = :action, updated_at = :updatedAt WHERE canonical_number = :canonicalNumber")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateAction(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String action, long updatedAt, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * 통화 횟수 증가
     */
    @androidx.room.Query(value = "UPDATE user_call_records SET call_count = call_count + 1, updated_at = :updatedAt WHERE canonical_number = :canonicalNumber")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object incrementCallCount(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, long updatedAt, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * 번호별 삭제
     */
    @androidx.room.Query(value = "DELETE FROM user_call_records WHERE canonical_number = :canonicalNumber")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteByNumber(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * 전체 삭제 (사용자 명시적 요청 시에만)
     */
    @androidx.room.Query(value = "DELETE FROM user_call_records")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * 사용자 통화 기록 DAO.
     *
     * 모든 쿼리는 로컬 전용. 서버 동기화 없음.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}