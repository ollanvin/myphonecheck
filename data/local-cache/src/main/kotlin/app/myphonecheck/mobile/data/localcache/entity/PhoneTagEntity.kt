package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 휘발성 사용자 메모 (Architecture v2.1.0 §32 Tag System).
 *
 * type 값: 코어 IdentifierType enum 이름 (PHONE_E164 / SMS_SENDER / NOTIFICATION_PACKAGE).
 * priority 값: 코어 TagPriority enum 이름 (REMIND_ME / PENDING / SUSPICIOUS / ARCHIVE).
 *
 * 헌법 정합:
 *  - §1 Out-Bound Zero: 디바이스 로컬만.
 *  - §2 In-Bound Zero: 사용자 입력만 저장 (자동 채우기 0).
 *  - §3 결정권 중앙집중 금지: 사용자가 부여·해제.
 */
@Entity(
    tableName = "phone_tag",
    indices = [Index("priority"), Index("lastSeenAtMillis")],
)
data class PhoneTagEntity(
    @PrimaryKey
    val identifierKey: String,
    val identifierType: String,
    val tagText: String,
    val priority: String,
    val createdAtMillis: Long,
    val lastSeenAtMillis: Long?,
    val seenCount: Int,
)
