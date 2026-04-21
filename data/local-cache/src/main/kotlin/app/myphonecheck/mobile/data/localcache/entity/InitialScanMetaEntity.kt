package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Initial Scan 완료 메타데이터 엔티티.
 *
 * 단일 레코드 — PK 고정값 1로 항상 1건만 유지.
 * Camera/Mic baseline + 디바이스 온보드 컨텍스트가
 * 모두 성공적으로 저장된 경우에만 completed=true.
 *
 * 온디바이스 전용, 서버 전송 없음.
 */
@Entity(tableName = "initial_scan_meta")
data class InitialScanMetaEntity(
    /** 고정 PK — 항상 1 (단일 레코드) */
    @PrimaryKey
    val id: Int = 1,

    /** Initial Scan 완전 완료 여부 */
    @ColumnInfo(name = "completed")
    val completed: Boolean = false,

    /** Initial Scan 버전 — 스키마 변경 시 재스캔 강제용 */
    @ColumnInfo(name = "scan_version")
    val scanVersion: Int = 0,

    /** 완료 시각 (epoch ms) */
    @ColumnInfo(name = "completed_at")
    val completedAt: Long = 0L,

    // ── 디바이스 온보드 컨텍스트 baseline ──

    /** SIM/Network/Locale 기반 국가 코드 (ISO 3166-1 alpha-2, 예: "KR") */
    @ColumnInfo(name = "baseline_country")
    val baselineCountry: String? = null,

    /** 시스템/앱 표시 언어 (ISO 639-1, 예: "ko") */
    @ColumnInfo(name = "baseline_language")
    val baselineLanguage: String? = null,

    /** 시스템 타임존 (IANA, 예: "Asia/Seoul") */
    @ColumnInfo(name = "baseline_timezone")
    val baselineTimezone: String? = null,

    /** PhoneNumberNormalizer 기본 region (ISO 3166-1, 예: "KR") */
    @ColumnInfo(name = "baseline_number_region")
    val baselineNumberRegion: String? = null,

    /** baseline 생성 시각 (epoch ms) */
    @ColumnInfo(name = "baseline_generated_at")
    val baselineGeneratedAt: Long = 0L,
)
