package app.myphonecheck.mobile.data.localcache.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * v12 → v13 마이그레이션 (Architecture v1.9.0 §27 CardCheck 신설).
 *
 * 신규 테이블만 추가. 기존 데이터 무손상.
 *  - card_transaction (CardTransactionEntity)
 *  - card_source_label (CardSourceLabelEntity)
 *
 * 인덱스: card_transaction.timestamp, currencyCode, sourceId.
 */
object Migration12To13 : Migration(12, 13) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // card_transaction
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS card_transaction (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                sourceId TEXT NOT NULL,
                sourceLabel TEXT NOT NULL,
                cardIdentifier TEXT,
                amount INTEGER NOT NULL,
                currencyCode TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                merchantName TEXT,
                source TEXT NOT NULL,
                confidence TEXT NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_card_transaction_timestamp " +
                "ON card_transaction(timestamp)",
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_card_transaction_currencyCode " +
                "ON card_transaction(currencyCode)",
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_card_transaction_sourceId " +
                "ON card_transaction(sourceId)",
        )

        // card_source_label
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS card_source_label (
                sourceId TEXT PRIMARY KEY NOT NULL,
                label TEXT NOT NULL,
                createdAt INTEGER NOT NULL
            )
            """.trimIndent(),
        )
    }
}
