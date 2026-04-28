package app.myphonecheck.mobile.data.localcache.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * v16 → v17 마이그레이션 (Architecture v2.1.0 §30-4 공개 피드 캐시).
 *
 * 신규 테이블만 추가. 기존 데이터 무손상.
 *  - feed_entry (FeedEntryEntity, 4유형 출처 공통 캐시)
 */
object Migration16To17 : Migration(16, 17) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS feed_entry (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                sourceId TEXT NOT NULL,
                matchKey TEXT NOT NULL,
                description TEXT NOT NULL,
                severity TEXT,
                downloadedAtMillis INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_feed_entry_sourceId ON feed_entry(sourceId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_feed_entry_matchKey ON feed_entry(matchKey)")
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_feed_entry_downloadedAtMillis ON feed_entry(downloadedAtMillis)",
        )
    }
}
