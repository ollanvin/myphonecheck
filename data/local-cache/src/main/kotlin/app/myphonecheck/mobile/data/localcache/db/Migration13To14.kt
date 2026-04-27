package app.myphonecheck.mobile.data.localcache.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * v13 → v14 마이그레이션 (Architecture v2.0.0 §28 Initial Scan).
 *
 * 신규 테이블만 추가. 기존 데이터 무손상.
 *  - call_base_entry        (CallBaseEntity)
 *  - sms_base_entry         (SmsBaseEntity)
 *  - package_base_entry     (PackageBaseEntity)
 *  - sim_context_snapshot   (SimContextSnapshotEntity, 단일 row)
 */
object Migration13To14 : Migration(13, 14) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS call_base_entry (
                e164 TEXT PRIMARY KEY NOT NULL,
                regionCode TEXT NOT NULL,
                callCount INTEGER NOT NULL,
                lastCallMillis INTEGER NOT NULL,
                numberType TEXT NOT NULL,
                firstSeenMillis INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS sms_base_entry (
                sender TEXT PRIMARY KEY NOT NULL,
                isShortSender INTEGER NOT NULL,
                messageCount INTEGER NOT NULL,
                lastSeenMillis INTEGER NOT NULL,
                dominantCategory TEXT NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS package_base_entry (
                packageName TEXT PRIMARY KEY NOT NULL,
                appLabel TEXT NOT NULL,
                sensitivePermissionsCsv TEXT NOT NULL,
                installedAtMillis INTEGER NOT NULL,
                firstScannedMillis INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS sim_context_snapshot (
                id INTEGER PRIMARY KEY NOT NULL,
                mcc TEXT NOT NULL,
                mnc TEXT NOT NULL,
                countryIso TEXT NOT NULL,
                operatorName TEXT NOT NULL,
                currencyCode TEXT NOT NULL,
                phoneRegion TEXT NOT NULL,
                timezoneId TEXT NOT NULL,
                capturedAtMillis INTEGER NOT NULL
            )
            """.trimIndent(),
        )
    }
}
