package app.myphonecheck.mobile.data.localcache.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room v18 to v19: replace stub `hub_message` (id + createdAt only) with WO-DATA-MSG-001 schema.
 *
 * If `hub_message` already contains WO columns (fixed Migration17To18 path), this is a no-op.
 */
val Migration18To19 = object : Migration(18, 19) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val cursor = db.query("PRAGMA table_info(hub_message)")
        val columns = buildList {
            try {
                while (cursor.moveToNext()) {
                    add(cursor.getString(1))
                }
            } finally {
                cursor.close()
            }
        }
        when {
            columns.contains("sender_identifier") -> return
            columns.isEmpty() -> createHubMessageW001(db)
            else -> {
                db.execSQL("DROP TABLE IF EXISTS hub_message")
                createHubMessageW001(db)
            }
        }
    }
}

private fun createHubMessageW001(db: SupportSQLiteDatabase) {
    db.execSQL(
        "CREATE TABLE hub_message (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "sender_identifier TEXT NOT NULL, " +
            "sender_label TEXT, " +
            "content TEXT NOT NULL, " +
            "has_link INTEGER NOT NULL, " +
            "extracted_urls TEXT NOT NULL, " +
            "extracted_numbers TEXT NOT NULL, " +
            "category TEXT NOT NULL, " +
            "received_at INTEGER NOT NULL, " +
            "source TEXT NOT NULL" +
            ")",
    )
    db.execSQL(
        "CREATE INDEX IF NOT EXISTS index_hub_message_received_at ON hub_message(received_at)",
    )
    db.execSQL(
        "CREATE INDEX IF NOT EXISTS index_hub_message_sender_received ON hub_message(" +
            "sender_identifier, received_at)",
    )
}
