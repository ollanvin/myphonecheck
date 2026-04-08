package app.callcheck.mobile.data.localcache.db

import androidx.room.Database
import androidx.room.RoomDatabase
import app.callcheck.mobile.data.localcache.dao.BackupMetadataDao
import app.callcheck.mobile.data.localcache.dao.MessageHubDao
import app.callcheck.mobile.data.localcache.dao.PreJudgeCacheDao
import app.callcheck.mobile.data.localcache.dao.UserCallRecordDao
import app.callcheck.mobile.data.localcache.entity.BackupMetadataEntity
import app.callcheck.mobile.data.localcache.entity.MessageHubEntity
import app.callcheck.mobile.data.localcache.entity.PreJudgeCacheEntry
import app.callcheck.mobile.data.localcache.entity.UserCallRecord

/**
 * CallCheck 로컬 데이터베이스.
 *
 * 저장 대상:
 *  - UserCallRecord: 사용자 메모, 태그, 행동 기록 (영구 저장)
 *  - PreJudgeCacheEntry: Tier 0 사전 판단 영속 캐시 (0ms 판단용)
 *  - BackupMetadataEntity: 백업 이력 메타데이터
 *  - MessageHubEntity: MessageCheck 허브 메시지 기록
 *
 * 서버 동기화: 없음 (온디바이스 전용)
 */
@Database(
    entities = [
        UserCallRecord::class,
        PreJudgeCacheEntry::class,
        BackupMetadataEntity::class,
        MessageHubEntity::class,
    ],
    version = 4,
    exportSchema = true,
)
abstract class CallCheckDatabase : RoomDatabase() {
    abstract fun userCallRecordDao(): UserCallRecordDao
    abstract fun preJudgeCacheDao(): PreJudgeCacheDao
    abstract fun backupMetadataDao(): BackupMetadataDao
    abstract fun messageHubDao(): MessageHubDao

    companion object {
        const val DATABASE_NAME = "callcheck_user_records.db"
    }
}
