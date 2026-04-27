package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * SIM 컨텍스트 스냅샷 (Architecture v2.0.0 §28 + 헌법 §8조).
 *
 * Initial Scan 또는 SIM 변경 감지 시 기록. 가장 최근 1건만 의미 있음
 * (단일 진실원). id=1 고정 키 사용 — 새 스냅샷이 덮어쓰기.
 */
@Entity(tableName = "sim_context_snapshot")
data class SimContextSnapshotEntity(
    @PrimaryKey
    val id: Long,
    val mcc: String,
    val mnc: String,
    val countryIso: String,
    val operatorName: String,
    val currencyCode: String,
    val phoneRegion: String,
    val timezoneId: String,
    val capturedAtMillis: Long,
) {
    companion object {
        const val SINGLETON_ID: Long = 1L
    }
}
