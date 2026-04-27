package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 사용자 라벨링 캐시 (Architecture v1.9.0 §27-3-1).
 *
 * 시드 데이터 0 — 모든 라벨은 사용자 확인 결과로만 형성.
 * 한 번 라벨링된 sourceId는 자동 처리 (SourceDetector.classify에서 Known 반환).
 */
@Entity(tableName = "card_source_label")
data class CardSourceLabelEntity(
    @PrimaryKey val sourceId: String,
    val label: String,
    val createdAt: Long,
)
