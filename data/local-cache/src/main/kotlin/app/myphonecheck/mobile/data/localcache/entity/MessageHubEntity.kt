package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * MessageCheck Hub 메시지 엔티티.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 핵심 설계 원칙:                                              │
 * │  • 출처 = SMS BroadcastReceiver (v1.1: PUSH 제거됨)           │
 * │  • 메시지 원문 + 판단 결과 + 탐지된 링크 저장                  │
 * │  • 서버 전송 절대 없음 — 온디바이스 전용                      │
 * │  • 차단된 발신자 자동 식별 지원                                │
 * └──────────────────────────────────────────────────────────────┘
 *
 * 인덱스:
 *  - package_name: 앱별 필터링
 *  - risk_level: 위험도별 필터링
 *  - received_at: 최신순 정렬
 *  - is_blocked: 차단 목록 빠른 조회
 */
@Entity(
    tableName = "message_hub",
    indices = [
        Index(value = ["package_name"]),
        Index(value = ["risk_level"]),
        Index(value = ["received_at"]),
        Index(value = ["is_blocked"]),
    ]
)
data class MessageHubEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 발신 앱 패키지명 */
    @ColumnInfo(name = "package_name")
    val packageName: String,

    /** 앱 표시 이름 (사용자 가독용) */
    @ColumnInfo(name = "app_label")
    val appLabel: String,

    /** 알림 채널 ID (nullable, Android O 미만은 null) */
    @ColumnInfo(name = "channel_id")
    val channelId: String? = null,

    /** 알림 제목 */
    @ColumnInfo(name = "title")
    val title: String? = null,

    /** 알림 본문 */
    @ColumnInfo(name = "text")
    val text: String? = null,

    /** 본문에서 탐지된 URL 목록 (JSON 직렬화, 예: ["https://...", "http://..."]) */
    @ColumnInfo(name = "detected_links")
    val detectedLinks: String? = null,

    /** 탐지된 링크 수 */
    @ColumnInfo(name = "link_count")
    val linkCount: Int = 0,

    /** AI 판단 위험도 (LOW, MEDIUM, HIGH, UNKNOWN) */
    @ColumnInfo(name = "risk_level")
    val riskLevel: String,

    /** AI 판단 카테고리 (ConclusionCategory.name) */
    @ColumnInfo(name = "category")
    val category: String,

    /** AI 판단 행동 권고 (ActionRecommendation.name) */
    @ColumnInfo(name = "action")
    val action: String,

    /** AI 신뢰도 (0.0 ~ 1.0) */
    @ColumnInfo(name = "confidence")
    val confidence: Float,

    /** 중요도 축 (UNKNOWN, NORMAL, IMPORTANT, DO_NOT_MISS) */
    @ColumnInfo(name = "importance_level")
    val importanceLevel: String = "UNKNOWN",

    /** 중요도 규칙 근거 */
    @ColumnInfo(name = "importance_reason")
    val importanceReason: String? = null,

    /** AI 판단 요약 (한 줄) */
    @ColumnInfo(name = "summary")
    val summary: String,

    /** AI 판단 근거 (JSON 직렬화 리스트) */
    @ColumnInfo(name = "reasons")
    val reasons: String? = null,

    /** 프로모션 키워드 매칭 수 */
    @ColumnInfo(name = "promotion_keyword_hits")
    val promotionKeywordHits: Int = 0,

    /** 야간 시간대 수신 여부 */
    @ColumnInfo(name = "is_night_time")
    val isNightTime: Boolean = false,

    /** 사용자가 이 발신자를 차단 표시했는지 */
    @ColumnInfo(name = "is_blocked")
    val isBlocked: Boolean = false,

    /** 사용자 메모 (자유 텍스트) */
    @ColumnInfo(name = "user_memo")
    val userMemo: String? = null,

    /** 수신 시각 (epoch millis) */
    @ColumnInfo(name = "received_at")
    val receivedAt: Long = System.currentTimeMillis(),

    /** 레코드 생성 시각 (epoch millis) */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)
