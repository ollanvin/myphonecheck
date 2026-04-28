package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.Entity
import androidx.room.Index

/**
 * 사용자 차단 식별자 (Architecture v2.1.0 §31 Real-time Action).
 *
 * type 값: PHONE_E164 / SMS_SENDER / NOTIFICATION_PACKAGE (코어 IdentifierType enum 이름).
 * (key, type) 복합 PK — 동일 식별자가 여러 type에서 차단 가능.
 *
 * 헌법 §3 결정권 중앙집중 금지: source 필드는 항상 사용자 의도 기록 (기본 "user").
 */
@Entity(
    tableName = "blocked_identifier",
    primaryKeys = ["key", "type"],
    indices = [Index("type"), Index("addedAtMillis")],
)
data class BlockedIdentifierEntity(
    val key: String,
    val type: String,
    val addedAtMillis: Long,
    val source: String,
)
