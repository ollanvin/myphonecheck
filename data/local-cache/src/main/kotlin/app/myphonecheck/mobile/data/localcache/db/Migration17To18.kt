package app.myphonecheck.mobile.data.localcache.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room v17 → v18 마이그레이션 (Stage 3-007 빌드 그래프 정합 정정).
 *
 * hub_message 테이블 추가 stub. 실 hub message 영역은 별 시리즈.
 */
val Migration17To18 = object : Migration(17, 18) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS hub_message (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "createdAt INTEGER NOT NULL" +
                ")"
        )
    }
}
