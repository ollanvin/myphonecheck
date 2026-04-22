package app.myphonecheck.mobile.feature.pushtrash.repository

import android.app.Notification
import android.graphics.drawable.Icon
import android.os.Build
import android.service.notification.StatusBarNotification
import app.myphonecheck.mobile.data.localcache.dao.BlockedAppDao
import app.myphonecheck.mobile.data.localcache.dao.BlockedChannelDao
import app.myphonecheck.mobile.data.localcache.dao.AppObservationCount
import app.myphonecheck.mobile.data.localcache.dao.ChannelObservationCount
import app.myphonecheck.mobile.data.localcache.dao.PushNotificationObservationDao
import app.myphonecheck.mobile.data.localcache.dao.TrashedNotificationDao
import app.myphonecheck.mobile.data.localcache.entity.BlockedAppEntity
import app.myphonecheck.mobile.data.localcache.entity.BlockedChannelEntity
import app.myphonecheck.mobile.data.localcache.entity.TrashedNotificationEntity
import app.myphonecheck.mobile.data.localcache.entity.PushNotificationObservationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushTrashRepository @Inject constructor(
    private val blockedChannelDao: BlockedChannelDao,
    private val blockedAppDao: BlockedAppDao,
    private val trashedNotificationDao: TrashedNotificationDao,
    private val observationDao: PushNotificationObservationDao,
) {

    sealed class Decision {
        data object Allow : Decision()
        data object Block : Decision()
    }

    val trashedItems: Flow<List<TrashedNotificationEntity>> = trashedNotificationDao.observeAll()

    fun trashedCountSince(sinceMillis: Long): Flow<Int> =
        trashedNotificationDao.observeCountSince(sinceMillis)

    suspend fun decide(packageName: String, channelId: String?): Decision = withContext(Dispatchers.IO) {
        val app = blockedAppDao.find(packageName)
        when (app?.mode) {
            MODE_ALL_BLOCKED -> return@withContext Decision.Block
            MODE_ALL_ALLOWED -> return@withContext Decision.Allow
            else -> Unit
        }
        val ch = channelId?.takeIf { it.isNotBlank() } ?: return@withContext Decision.Allow
        if (blockedChannelDao.isBlocked(packageName, ch)) {
            Decision.Block
        } else {
            Decision.Allow
        }
    }

    suspend fun recordNotificationObserved(
        packageName: String,
        channelId: String?,
        postedAt: Long,
    ) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        observationDao.deleteOlderThan(now - SEVEN_DAYS_MS)
        observationDao.insert(
            PushNotificationObservationEntity(
                packageName = packageName,
                channelId = channelId ?: "",
                postedAt = postedAt,
            ),
        )
    }

    suspend fun recordTrashed(sbn: StatusBarNotification) = withContext(Dispatchers.IO) {
        val n = sbn.notification
        val title = n.extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        val text = n.extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            n.channelId?.takeIf { it.isNotBlank() }
        } else {
            null
        }
        trashedNotificationDao.insert(
            TrashedNotificationEntity(
                packageName = sbn.packageName,
                channelId = channelId,
                title = title,
                text = text,
                iconResId = extractSmallIconRes(n),
                postedAt = sbn.postTime,
                capturedAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun ruleCount(): Int = withContext(Dispatchers.IO) {
        blockedChannelDao.countAll() + blockedAppDao.countAllBlocked()
    }

    suspend fun restoreTrashed(entry: TrashedNotificationEntity) = withContext(Dispatchers.IO) {
        val pkg = entry.packageName
        val ch = entry.channelId
        if (!ch.isNullOrBlank()) {
            blockedChannelDao.delete(pkg, ch)
        } else {
            blockedAppDao.delete(pkg)
        }
        trashedNotificationDao.deleteById(entry.id)
    }

    suspend fun deleteTrashed(entry: TrashedNotificationEntity) = withContext(Dispatchers.IO) {
        trashedNotificationDao.deleteById(entry.id)
    }

    suspend fun observedAppsLast7Days(): List<AppObservationCount> =
        withContext(Dispatchers.IO) {
            observationDao.appStatsSince(System.currentTimeMillis() - SEVEN_DAYS_MS)
        }

    suspend fun observedChannelsLast7Days(): List<ChannelObservationCount> =
        withContext(Dispatchers.IO) {
            observationDao.channelStatsSince(System.currentTimeMillis() - SEVEN_DAYS_MS)
        }

    suspend fun setAppMode(packageName: String, mode: String) = withContext(Dispatchers.IO) {
        blockedAppDao.upsert(
            BlockedAppEntity(
                packageName = packageName,
                mode = mode,
                blockedAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun clearAppMode(packageName: String) = withContext(Dispatchers.IO) {
        blockedAppDao.delete(packageName)
    }

    suspend fun setChannelBlocked(packageName: String, channelId: String, blocked: Boolean) =
        withContext(Dispatchers.IO) {
            if (blocked) {
                blockedChannelDao.insert(
                    BlockedChannelEntity(
                        packageName = packageName,
                        channelId = channelId,
                        blockedAt = System.currentTimeMillis(),
                    ),
                )
            } else {
                blockedChannelDao.delete(packageName, channelId)
            }
        }

    suspend fun getAppBlock(packageName: String): BlockedAppEntity? = withContext(Dispatchers.IO) {
        blockedAppDao.find(packageName)
    }

    suspend fun isChannelBlocked(packageName: String, channelId: String): Boolean =
        withContext(Dispatchers.IO) {
            blockedChannelDao.isBlocked(packageName, channelId)
        }

    private fun extractSmallIconRes(notification: Notification): Int? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return null
        val icon: Icon = notification.smallIcon ?: return null
        return if (icon.type == Icon.TYPE_RESOURCE) icon.resId else null
    }

    companion object {
        const val MODE_ALL_BLOCKED = "all_blocked"
        const val MODE_ALL_ALLOWED = "all_allowed"
        private const val SEVEN_DAYS_MS = 7L * 24L * 60L * 60L * 1000L
    }
}
