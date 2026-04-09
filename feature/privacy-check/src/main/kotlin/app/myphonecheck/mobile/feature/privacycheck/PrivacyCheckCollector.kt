package app.myphonecheck.mobile.feature.privacycheck

import android.app.AppOpsManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import app.myphonecheck.mobile.core.model.PrivacyEvidence
import app.myphonecheck.mobile.data.localcache.dao.PrivacyHistoryDao
import app.myphonecheck.mobile.data.localcache.entity.PrivacyHistoryEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PrivacyCheck ?섏쭛湲?
 *
 * Android 12+ (API 31) AppOpsManager瑜??쒖슜?섏뿬
 * 移대찓??留덉씠???묎렐 ?덉뒪?좊━瑜??섏쭛?섍퀬 ?댁긽???먯??⑸땲??
 *
 * ?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧??
 * ?댁긽 ?먯? 議곌굔:
 * 1. 鍮꾪솢???쒓컙? ?묎렐 (湲곕낯 00:00~06:00, ?붾컮?댁뒪 TimeZone)
 * 2. ?ㅼ튂 30??誘몃쭔 ??
 * 3. ?대떦 ?깆씠 ??沅뚰븳??怨쇨굅???ъ슜???대젰 ?놁쓬
 * ?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧??
 *
 * ?댁긽 ?먯? ??
 * - Room DB(privacy_history)?????(isAnomaly = true)
 * - 濡쒖뺄 ?몄떆 ?뚮┝ 諛쒖넚 (?쒕ぉ/?댁슜? strings.xml)
 * - ?≪뀡1: ???쒕룞 留욎쓬 ??userVerified = "CONFIRMED"
 * - ?≪뀡2: ?꾨떂 ???대떦 ??沅뚰븳 ?ㅼ젙 ?붾㈃
 *
 * ?먯튃:
 * - Android 12 誘몃쭔 ??利됱떆 由ы꽩 (?곗씠???섏쭛 ????
 * - ?쒕쾭 ?꾩넚 ?덈? ?놁쓬 ???⑤뵒諛붿씠???꾩슜
 * - ?먮룞 李⑤떒/沅뚰븳 ?댁젣 ?덈? ?놁쓬
 */
@Singleton
class PrivacyCheckCollector @Inject constructor(
    @ApplicationContext private val context: Context,
    private val privacyHistoryDao: PrivacyHistoryDao,
) {
    companion object {
        const val ACTION_PRIVACY_CONFIRM = "app.myphonecheck.mobile.action.PRIVACY_CONFIRM"
        const val EXTRA_ENTITY_ID = "entity_id"
        private const val TAG = "PrivacyCheckCollector"

        private const val CHANNEL_ID = "myphonecheck_privacy_alerts"
        private const val NOTIFICATION_ID_BASE = 50_000

        /** 鍮꾪솢???쒓컙? 寃쎄퀎 (?붾컮?댁뒪 TimeZone 湲곗?) */
        private const val INACTIVE_HOUR_START = 0   // ?먯젙
        private const val INACTIVE_HOUR_END = 6     // 06??

        /** ?좉퇋 ??湲곗? (?? */
        private const val NEW_APP_THRESHOLD_DAYS = 30

        /** ?먯껜 ??臾댁떆 */
        private const val SELF_PACKAGE = "app.myphonecheck.mobile"

        /** ?쒖뒪????臾댁떆 */
        private val SYSTEM_IGNORE = setOf(
            "android",
            "com.android.systemui",
            "com.android.camera",
            "com.android.camera2",
            "com.google.android.GoogleCamera",
        )

        /** ?뚮젮吏??뺤긽 移대찓??留덉씠????(?댁긽 ?먯? ?덉쇅) */
        private val KNOWN_SAFE_APPS = setOf(
            "com.samsung.android.app.camera",
            "com.huawei.camera",
            "com.google.android.GoogleCamera",
            "us.zoom.videomeetings",
            "com.google.android.apps.meetings",
            "com.microsoft.teams",
            "com.kakao.talk",
            "org.telegram.messenger",
            "com.whatsapp",
            "jp.naver.line.android",
        )
    }

    /**
     * ?쇱꽌 ?묎렐 ?대깽?몃? ?섏쭛?섍퀬 ?댁긽???먮퀎?⑸땲??
     *
     * Android 12 誘몃쭔?먯꽌??利됱떆 由ы꽩?⑸땲??
     *
     * @param packageName ?쇱꽌 ?묎렐 ???⑦궎吏紐?
     * @param permissionType "CAMERA" ?먮뒗 "MICROPHONE"
     * @param durationSec ?쇱꽌 ?ъ슜 吏???쒓컙 (珥?
     */
    suspend fun collect(
        packageName: String,
        permissionType: String,
        durationSec: Long,
    ) {
        // Android 12 誘몃쭔 ???곗씠???섏쭛 ????
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return

        // ?먯껜 ??諛??쒖뒪????臾댁떆
        if (packageName == SELF_PACKAGE) return
        if (packageName in SYSTEM_IGNORE) return

        try {
            processAccess(packageName, permissionType, durationSec)
        } catch (e: Exception) {
            Log.e(TAG, "Error collecting privacy history for $packageName", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private suspend fun processAccess(
        packageName: String,
        permissionType: String,
        durationSec: Long,
    ) {
        val now = System.currentTimeMillis()
        val appLabel = getAppLabel(packageName)
        val daysSinceInstall = getDaysSinceInstall(packageName)
        val isKnownSafe = packageName in KNOWN_SAFE_APPS

        // ?댁긽 ?먯? ?먮퀎
        val isAnomaly = !isKnownSafe && detectAnomaly(
            packageName = packageName,
            permissionType = permissionType,
            daysSinceInstall = daysSinceInstall,
        )

        // Room DB ???
        val entity = PrivacyHistoryEntity(
            appPackage = packageName,
            appLabel = appLabel,
            permissionType = permissionType,
            usedAt = now,
            durationSec = durationSec,
            isAnomaly = isAnomaly,
        )

        val insertedId = privacyHistoryDao.insert(entity)
        Log.d(TAG, "[PrivacyCheck] Saved: $appLabel/$permissionType " +
            "(anomaly=$isAnomaly, days=$daysSinceInstall)")

        // ?댁긽 ?먯? ??濡쒖뺄 ?몄떆 ?뚮┝
        if (isAnomaly) {
            sendAnomalyNotification(
                entityId = insertedId,
                packageName = packageName,
                appLabel = appLabel,
                permissionType = permissionType,
            )
        }
    }

    /**
     * ?댁긽 ?먯? 濡쒖쭅.
     *
     * 議곌굔 (?섎굹?쇰룄 ?대떦?섎㈃ ?댁긽):
     * 1. 鍮꾪솢???쒓컙? ?묎렐 (?붾컮?댁뒪 TimeZone 湲곗? 00:00~06:00)
     * 2. ?ㅼ튂 30??誘몃쭔 ??
     * 3. ?대떦 ?깆씠 ??沅뚰븳??怨쇨굅???ъ슜???대젰 ?놁쓬
     */
    private suspend fun detectAnomaly(
        packageName: String,
        permissionType: String,
        daysSinceInstall: Int,
    ): Boolean {
        // 議곌굔1: 鍮꾪솢???쒓컙? (?붾컮?댁뒪 TimeZone ?먮룞 諛섏쁺)
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (currentHour in INACTIVE_HOUR_START until INACTIVE_HOUR_END) {
            Log.d(TAG, "[Anomaly] Inactive hour access: $packageName at $currentHour:00")
            return true
        }

        // 議곌굔2: ?좉퇋 ??(?ㅼ튂 30??誘몃쭔)
        if (daysSinceInstall in 0 until NEW_APP_THRESHOLD_DAYS) {
            Log.d(TAG, "[Anomaly] New app access: $packageName (installed $daysSinceInstall days ago)")
            return true
        }

        // 議곌굔3: 理쒖큹 ?ъ슜 (怨쇨굅 ?대젰 ?놁쓬)
        val hasHistory = privacyHistoryDao.hasHistory(packageName, permissionType)
        if (!hasHistory) {
            Log.d(TAG, "[Anomaly] First-time access: $packageName/$permissionType")
            return true
        }

        return false
    }

    /**
     * ?댁긽 ?먯? 濡쒖뺄 ?몄떆 ?뚮┝ 諛쒖넚.
     *
     * ?≪뀡1: "???쒕룞 留욎쓬" ??userVerified = "CONFIRMED"
     * ?≪뀡2: "?꾨떂" ?????ㅼ젙 ?붾㈃ ?대룞
     */
    private fun sendAnomalyNotification(
        entityId: Long,
        packageName: String,
        appLabel: String,
        permissionType: String,
    ) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as? NotificationManager ?: return

        ensureNotificationChannel(nm)

        val sensorName = when (permissionType) {
            "CAMERA" -> context.getString(R.string.privacy_sensor_camera)
            "MICROPHONE" -> context.getString(R.string.privacy_sensor_microphone)
            else -> permissionType
        }

        val title = when (permissionType) {
            "CAMERA" -> context.getString(R.string.privacy_notification_title_camera)
            "MICROPHONE" -> context.getString(R.string.privacy_notification_title_mic)
            else -> context.getString(R.string.privacy_notification_title_camera)
        }
        val content = context.getString(R.string.privacy_anomaly_content, appLabel, sensorName)

        // ?≪뀡1: ???쒕룞 留욎쓬 ??BroadcastReceiver濡?CONFIRMED 泥섎━
        val confirmIntent = Intent(ACTION_PRIVACY_CONFIRM).apply {
            setPackage(context.packageName)
            putExtra(EXTRA_ENTITY_ID, entityId)
        }
        val confirmPending = PendingIntent.getBroadcast(
            context,
            entityId.toInt(),
            confirmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        // ?≪뀡2: ?꾨떂 ???대떦 ??沅뚰븳 ?ㅼ젙 ?붾㈃
        val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val settingsPending = PendingIntent.getActivity(
            context,
            entityId.toInt() + 1,
            settingsIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setColor(0xFFF44336.toInt()) // RingSystem.COLOR_DANGER
            .addAction(
                android.R.drawable.ic_menu_save,
                context.getString(R.string.privacy_action_confirmed),
                confirmPending,
            )
            .addAction(
                android.R.drawable.ic_menu_manage,
                context.getString(R.string.privacy_action_denied),
                settingsPending,
            )
            .build()

        nm.notify(NOTIFICATION_ID_BASE + entityId.toInt(), notification)
        Log.d(TAG, "[PrivacyCheck] Anomaly notification sent: $appLabel/$permissionType")
    }

    private fun ensureNotificationChannel(nm: NotificationManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        if (nm.getNotificationChannel(CHANNEL_ID) != null) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.privacy_channel_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = context.getString(R.string.privacy_channel_description)
            enableVibration(true)
            enableLights(true)
            setShowBadge(true)
        }

        nm.createNotificationChannel(channel)
    }

    private fun getAppLabel(packageName: String): String {
        return try {
            val pm = context.packageManager
            val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getApplicationInfo(packageName, 0)
            }
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName.substringAfterLast(".")
        }
    }

    private fun getDaysSinceInstall(packageName: String): Int {
        return try {
            val installTime = context.packageManager
                .getPackageInfo(packageName, 0).firstInstallTime
            ((System.currentTimeMillis() - installTime) / (24 * 60 * 60 * 1000L))
                .toInt().coerceAtLeast(0)
        } catch (e: Exception) {
            -1
        }
    }
}
