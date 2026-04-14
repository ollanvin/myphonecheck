package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "number_profiles",
    indices = [
        Index(value = ["last_interaction_at"]),
        Index(value = ["block_state"]),
    ],
)
data class NumberProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "normalized_number")
    val normalizedNumber: String,
    @ColumnInfo(name = "last_interaction_at")
    val lastInteractionAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "last_call_at")
    val lastCallAt: Long? = null,
    @ColumnInfo(name = "last_sms_at")
    val lastSmsAt: Long? = null,
    @ColumnInfo(name = "quick_labels")
    val quickLabels: String = "",
    @ColumnInfo(name = "do_not_miss_flag")
    val doNotMissFlag: Boolean = false,
    @ColumnInfo(name = "block_state")
    val blockState: String = NumberProfileBlockState.NONE.storageKey,
    @ColumnInfo(name = "user_memo_short")
    val userMemoShort: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
)
