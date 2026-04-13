package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

/**
 * 푸시 알림 통계 엔티티.
 *
 * 앱별 + 날짜별 알림 통계를 원자적으로 집계합니다.
 * PushInterceptService가 알림 수신 시 atomic increment로 업데이트.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 복합 PK: (packageName, dateKey)                              │
 * │ dateKey = "yyyy-MM-dd" 형식 (디바이스 타임존 기준)            │
 * │ 매일 자정에 새 row 자동 생성 → 일별 추이 그래프 가능          │
 * │                                                              │
 * │ 온디바이스 전용 — 서버 전송 없음                              │
 * └──────────────────────────────────────────────────────────────┘
 *
 * 집계 필드:
 * - totalCount: 총 알림 수
 * - nightCount: 야간(22:00~07:00) 알림 수
 * - promotionCount: 프로모션 키워드 포함 알림 수
 * - linkCount: 링크 포함 알림 수
 * - highRiskCount: HIGH 위험도 판정 알림 수
 */
@Entity(
    tableName = "push_stats",
    primaryKeys = ["package_name", "date_key"],
    indices = [
        Index(value = ["date_key"]),
        Index(value = ["package_name"]),
    ],
)
data class PushStatsEntity(
    /** 발신 앱 패키지명 */
    @ColumnInfo(name = "package_name")
    val packageName: String,

    /** 앱 표시 이름 */
    @ColumnInfo(name = "app_label")
    val appLabel: String,

    /** 날짜 키 (yyyy-MM-dd, 디바이스 타임존) */
    @ColumnInfo(name = "date_key")
    val dateKey: String,

    /** 총 알림 수 */
    @ColumnInfo(name = "total_count")
    val totalCount: Int = 0,

    /** 야간 알림 수 (22:00~07:00) */
    @ColumnInfo(name = "night_count")
    val nightCount: Int = 0,

    /** 프로모션 키워드 포함 알림 수 */
    @ColumnInfo(name = "promotion_count")
    val promotionCount: Int = 0,

    /** 링크 포함 알림 수 */
    @ColumnInfo(name = "link_count")
    val linkCount: Int = 0,

    /** HIGH 위험도 판정 알림 수 */
    @ColumnInfo(name = "high_risk_count")
    val highRiskCount: Int = 0,

    /** 마지막 업데이트 시각 (epoch millis) */
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
)
