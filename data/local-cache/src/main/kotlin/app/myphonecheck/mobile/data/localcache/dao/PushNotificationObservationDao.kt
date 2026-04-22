package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.PushNotificationObservationEntity

@Dao
interface PushNotificationObservationDao {

    @Insert
    suspend fun insert(entity: PushNotificationObservationEntity)

    @Query("DELETE FROM push_notification_observations WHERE postedAt < :cutoffMillis")
    suspend fun deleteOlderThan(cutoffMillis: Long)

    @Query(
        """
        SELECT packageName, channelId, COUNT(*) AS notificationCount
        FROM push_notification_observations
        WHERE postedAt >= :sinceMillis
        GROUP BY packageName, channelId
        """,
    )
    suspend fun channelStatsSince(sinceMillis: Long): List<ChannelObservationCount>

    @Query(
        """
        SELECT packageName, COUNT(*) AS notificationCount
        FROM push_notification_observations
        WHERE postedAt >= :sinceMillis
        GROUP BY packageName
        """,
    )
    suspend fun appStatsSince(sinceMillis: Long): List<AppObservationCount>
}

data class ChannelObservationCount(
    val packageName: String,
    val channelId: String,
    val notificationCount: Int,
)

data class AppObservationCount(
    val packageName: String,
    val notificationCount: Int,
)
