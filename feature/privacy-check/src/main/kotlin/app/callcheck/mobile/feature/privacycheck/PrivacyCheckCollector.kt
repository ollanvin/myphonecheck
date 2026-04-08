package app.callcheck.mobile.feature.privacycheck

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
import app.callcheck.mobile.core.model.PrivacyEvidence
import app.callcheck.mobile.data.localcache.dao.PrivacyHistoryDao
import app.callcheck.mobile.data.localcache.entity.PrivacyHistoryEntity
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PrivacyCheck 수집기.
 *
 * Android 12+ (API 31) AppOpsManager를 활용하여
 * 카메라/마이크 접근 히스토리를 수집하고 이상을 탐지합니다.
 *
 * ═══════════════════════════════════════════════
 * 이상 탐지 조건:
 * 1. 비활동 시간대 접근 (기본 00:00~06:00, 디바이스 TimeZone)
 * 2. 설치 30일 미만 앱
 * 3. 해당 앱이 이 권한을 과거에 사용한 이력 없음
 * ═══════════════════════════════════════════════
 *
 * 이상 탐지 시:
 * - Room DB(privacy_history)에 저장 (isAnomaly = true)
 * - 로컬 푸시 알림 발송 (제목/내용은 strings.xml)
 * - 액션1: 내 활동 맞음 → userVerified = "CONFIRMED"
 * - 액션2: 아님 → 해당 앱 권한 설정 화면
 *
 * 원칙:
 * - Android 12 미만 → 즉시 리턴 (데이터 수집 안 함)
 * - 서버 전송 절대 없음 — 온디바이스 전용
 * - 자동 차단/권한 해제 절대 없음
 */
@Singleton
class PrivacyCheckCollector @Inject constructor(
    private val context: Context,
    private val privacyHistoryDao: PrivacyHistoryDao,
) {
    private companion object {
        private const val TAG = "PrivacyCheckCollector"

        private const val CHANNEL_ID = "callcheck_privacy_alerts"
        private const val NOTIFICATION_ID_BASE = 50_000

        /** 비활동 시간대 경계 (디바이스 TimeZone 기준) */
        private const val INACTIVE_HOUR_START = 0   // 자정
        private const val INACTIVE_HOUR_END = 6     // 06시

        /** 신규 앱 기준 (일) */
        private const val NEW_APP_THRESHOLD_DAYS = 30

        /** 자체 앱 무시 */
        private const val SELF_PACKAGE = "app.callcheck.mobile"

        /** 시스템 앱 무시 */
        private val SYSTEM_IGNORE = setOf(
            "android",
            "com.android.systemui",
            "com.android.camera",
            "com.android.camera2",
            "com.google.android.GoogleCamera",
        )

        /** 알려진 정상 카메라/마이크 앱 (이상 탐지 예외) */
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
     * 센서 접근 이벤트를 수집하고 이상을 판별합니다.
     *
     * Android 12 미만에서는 즉시 리턴합니다.
     *
     * @param packageName 센서 접근 앱 패키지명
     * @param permissionType "CAMERA" 또는 "MICROPHONE"
     * @param durationSec 센서 사용 지속 시간 (초)
     */
    suspend fun collect(
        packageName: String,
        permissionType: String,
        durationSec: Long,
    ) {
        // Android 12 미만 → 데이터 수집 안 함
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return

        // 자체 앱 및 시스템 앱 무시
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

        // 이상 탐지 판별
        val isAnomaly = !isKnownSafe && detectAnomaly(
            packageName = packageName,
            permissionType = permissionType,
            daysSinceInstall = daysSinceInstall,
        )

        // Room DB 저장
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

        // 이상 탐지 시 로컬 푸시 알림
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
     * 이상 탐지 로직.
     *
     * 조건 (하나라도 해당하면 이상):
     * 1. 비활동 시간대 접근 (디바이스 TimeZone 기준 00:00~06:00)
     * 2. 설치 30일 미만 앱
     * 3. 해당 앱이 이 권한을 과거에 사용한 이력 없음
     */
    private suspend fun detectAnomaly(
        packageName: String,
        permissionType: String,
        daysSinceInstall: Int,
    ): Boolean {
        // 조건1: 비활동 시간대 (디바이스 TimeZone 자동 반영)
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (currentHour in INACTIVE_HOUR_START until INACTIVE_HOUR_END) {
            Log.d(TAG, "[Anomaly] Inactive hour access: $packageName at $currentHour:00")
            return true
        }

        // 조건2: 신규 앱 (설치 30일 미만)
        if (daysSinceInstall in 0 until NEW_APP_THRESHOLD_DAYS) {
            Log.d(TAG, "[Anomaly] New app access: $packageName (installed $daysSinceInstall days ago)")
            return true
        }

        // 조건3: 최초 사용 (과거 이력 없음)
        val hasHistory = privacyHistoryDao.hasHistory(packageName, permissionType)
        if (!hasHistory) {
            Log.d(TAG, "[Anomaly] First-time access: $packageName/$permissionType")
            return true
        }

        return false
    }

    /**
     * 이상 탐지 로컬 푸시 알림 발송.
     *
     * 액션1: "내 활동 맞음" → userVerified = "CONFIRMED"
     * 액션2: "아님" → 앱 설정 화면 이동
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

        // 액션1: 내 활동 맞음 → BroadcastReceiver로 CONFIRMED 처리
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

        // 액션2: 아님 → 해당 앱 권한 설정 화면
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

    companion object {
        /** BroadcastReceiver 액션: 사용자가 "내 활동 맞음" 선택 */
        const val ACTION_PRIVACY_CONFIRM =
            "app.callcheck.mobile.action.PRIVACY_CONFIRM"

        /** Extra: PrivacyHistoryEntity ID */
        const val EXTRA_ENTITY_ID = "entity_id"
    }
}
