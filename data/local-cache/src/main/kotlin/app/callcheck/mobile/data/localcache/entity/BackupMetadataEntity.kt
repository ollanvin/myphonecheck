package app.callcheck.mobile.data.localcache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 로컬 백업 이력을 저장하는 엔티티.
 * 각 백업 파일의 메타데이터를 기록한다.
 */
@Entity(tableName = "backup_metadata")
data class BackupMetadataEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "file_path") val filePath: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "size_bytes") val sizeBytes: Long,
    @ColumnInfo(name = "encrypted") val encrypted: Boolean = true,
    @ColumnInfo(name = "checksum") val checksum: String,
)
