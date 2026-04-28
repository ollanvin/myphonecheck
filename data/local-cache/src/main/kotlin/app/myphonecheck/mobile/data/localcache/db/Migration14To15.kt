package app.myphonecheck.mobile.data.localcache.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * v14 → v15 마이그레이션 (Architecture v2.1.0 §31 Real-time Action).
 *
 * 신규 테이블만 추가. 기존 데이터 무손상.
 *  - blocked_identifier (BlockedIdentifierEntity, 사용자 차단 목록)
 */
object Migration14To15 : Migration(14, 15) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS blocked_identifier (
                key TEXT NOT NULL,
                type TEXT NOT NULL,
                addedAtMillis INTEGER NOT NULL,
                source TEXT NOT NULL,
                PRIMARY KEY(key, type)
            )
            """.trimIndent(),
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_blocked_identifier_type ON blocked_identifier(type)",
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_blocked_identifier_addedAtMillis ON blocked_identifier(addedAtMillis)",
        )
    }
}
