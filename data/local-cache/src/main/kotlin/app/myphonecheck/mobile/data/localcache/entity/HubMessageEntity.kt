package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Hub message minimal stub (Stage 3-007 빌드 그래프 정합 정정).
 *
 * main 브랜치에 LocalCacheModule + MyPhoneCheckDatabase 가 HubMessageDao를 참조하나
 * 실 entity/dao/migration 파일이 누락된 상태에서 머지됨 (다른 작업 진행 중).
 * 본 PR은 unit test 컴파일 그래프 정합 위해 minimal stub 작성. 실 hub message 영역은 별 시리즈.
 */
@Entity(tableName = "hub_message")
data class HubMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
)
