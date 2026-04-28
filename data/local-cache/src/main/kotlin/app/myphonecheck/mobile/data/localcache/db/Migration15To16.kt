package app.myphonecheck.mobile.data.localcache.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * v15 → v16 마이그레이션 (Architecture v2.1.0 §32 Tag System).
 *
 * 신규 테이블만 추가. 기존 데이터 무손상.
 *  - phone_tag (PhoneTagEntity)
 */
object Migration15To16 : Migration(15, 16) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS phone_tag (
                identifierKey TEXT PRIMARY KEY NOT NULL,
                identifierType TEXT NOT NULL,
                tagText TEXT NOT NULL,
                priority TEXT NOT NULL,
                createdAtMillis INTEGER NOT NULL,
                lastSeenAtMillis INTEGER,
                seenCount INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_phone_tag_priority ON phone_tag(priority)",
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_phone_tag_lastSeenAtMillis ON phone_tag(lastSeenAtMillis)",
        )
    }
}
