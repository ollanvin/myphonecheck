package app.callcheck.mobile.data.localcache.db

import androidx.room.Database
import androidx.room.RoomDatabase
import app.callcheck.mobile.data.localcache.dao.UserCallRecordDao
import app.callcheck.mobile.data.localcache.entity.UserCallRecord

/**
 * CallCheck 로컬 데이터베이스.
 *
 * 저장 대상:
 *  - UserCallRecord: 사용자 메모, 태그, 행동 기록 (영구 저장)
 *
 * 저장하지 않는 것:
 *  - SearchResult: TTL 캐시 전용, 별도 관리
 *  - AI 판단 결과: 매 호출마다 재계산
 *
 * 서버 동기화: 없음 (온디바이스 전용)
 */
@Database(
    entities = [UserCallRecord::class],
    version = 1,
    exportSchema = true,
)
abstract class CallCheckDatabase : RoomDatabase() {
    abstract fun userCallRecordDao(): UserCallRecordDao

    companion object {
        const val DATABASE_NAME = "callcheck_user_records.db"
    }
}
