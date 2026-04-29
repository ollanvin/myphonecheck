package app.myphonecheck.mobile.data.localcache.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room v17 → v18: create `hub_message` (WO-DATA-MSG-001).
 *
 * Earlier stub builds used `(id, createdAt)` only; installs that already migrated with that
 * layout are upgraded via [Migration18To19].
 */
val Migration17To18 = object : Migration(17, 18) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS hub_message (" +
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
}
