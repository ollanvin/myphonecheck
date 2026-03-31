package app.callcheck.mobile.data.localcache.db;

/**
 * CallCheck 로컬 데이터베이스.
 *
 * 저장 대상:
 * - UserCallRecord: 사용자 메모, 태그, 행동 기록 (영구 저장)
 * - PreJudgeCacheEntry: Tier 0 사전 판단 영속 캐시 (0ms 판단용)
 *
 * 서버 동기화: 없음 (온디바이스 전용)
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \u00072\u00020\u0001:\u0001\u0007B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&\u00a8\u0006\b"}, d2 = {"Lapp/callcheck/mobile/data/localcache/db/CallCheckDatabase;", "Landroidx/room/RoomDatabase;", "()V", "preJudgeCacheDao", "Lapp/callcheck/mobile/data/localcache/dao/PreJudgeCacheDao;", "userCallRecordDao", "Lapp/callcheck/mobile/data/localcache/dao/UserCallRecordDao;", "Companion", "local-cache_debug"})
@androidx.room.Database(entities = {app.callcheck.mobile.data.localcache.entity.UserCallRecord.class, app.callcheck.mobile.data.localcache.entity.PreJudgeCacheEntry.class}, version = 2, exportSchema = true)
public abstract class CallCheckDatabase extends androidx.room.RoomDatabase {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DATABASE_NAME = "callcheck_user_records.db";
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.data.localcache.db.CallCheckDatabase.Companion Companion = null;
    
    public CallCheckDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract app.callcheck.mobile.data.localcache.dao.UserCallRecordDao userCallRecordDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract app.callcheck.mobile.data.localcache.dao.PreJudgeCacheDao preJudgeCacheDao();
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lapp/callcheck/mobile/data/localcache/db/CallCheckDatabase$Companion;", "", "()V", "DATABASE_NAME", "", "local-cache_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}