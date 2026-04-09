package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 사용자 통화 기록 엔티티.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 핵심 설계 원칙:                                              │
 * │  • 키 = canonicalNumber (E.164 정규화)                       │
 * │  • AI 판단(RiskLevel)과 독립된 사용자 판단 축적               │
 * │  • TTL 캐시(SearchResult)와 완전 분리                        │
 * │  • 사용자 메모/태그만 영구 저장                               │
 * │  • 서버 전송 절대 없음 — 온디바이스 전용                      │
 * └──────────────────────────────────────────────────────────────┘
 *
 * 인덱스:
 *  - canonical_number: 빠른 번호 조회
 *  - tag: 태그별 필터링
 *  - updated_at: 최신순 정렬
 */
@Entity(
    tableName = "user_call_records",
    indices = [
        Index(value = ["canonical_number"], unique = true),
        Index(value = ["tag"]),
        Index(value = ["updated_at"]),
    ]
)
data class UserCallRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** E.164 정규화 번호 (예: +821012345678) */
    @ColumnInfo(name = "canonical_number")
    val canonicalNumber: String,

    /** 사용자가 보는 원본 번호 (예: 010-1234-5678) */
    @ColumnInfo(name = "display_number")
    val displayNumber: String,

    /** 사용자 태그 (safe, spam, business, personal, delivery, custom) */
    @ColumnInfo(name = "tag")
    val tag: String? = null,

    /** 사용자 메모 (자유 텍스트) */
    @ColumnInfo(name = "memo")
    val memo: String? = null,

    /** 마지막 행동 (answered, rejected, blocked, missed) */
    @ColumnInfo(name = "last_action")
    val lastAction: String? = null,

    /** AI가 마지막으로 판단한 리스크 레벨 (참조용) */
    @ColumnInfo(name = "ai_risk_level")
    val aiRiskLevel: String? = null,

    /** AI가 마지막으로 판단한 카테고리 (참조용) */
    @ColumnInfo(name = "ai_category")
    val aiCategory: String? = null,

    /** 이 번호의 총 통화 횟수 */
    @ColumnInfo(name = "call_count")
    val callCount: Int = 1,

    /** 최초 기록 시각 (epoch millis) */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    /** 최종 수정 시각 (epoch millis) */
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
)
