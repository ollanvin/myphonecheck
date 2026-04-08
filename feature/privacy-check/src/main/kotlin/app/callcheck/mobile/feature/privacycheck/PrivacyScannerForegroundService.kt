package app.callcheck.mobile.feature.privacycheck

import android.app.AppOpsManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Android 12+ 전용: AppOpsManager 활성 카메라/마이크 콜백을 구독하고
 * 세션이 끝날 때 [PrivacyCheckCollector.collect]로 히스토리를 기록한다.
 *
 * API 31 미만에서는 [onCreate]에서 즉시 중지한다.
 */
@AndroidEntryPoint
class PrivacyScannerForegroundService : Service() {

    @Inject
    lateinit var collector: PrivacyCheckCollector

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val activeSince = ConcurrentHashMap<String, Long>()

    private var appOps: AppOpsManager? = null

    private val opListener = AppOpsManager.OnOpActiveChangedListener { op, _, packageName, active ->
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return@OnOpActiveChangedListener
        if (packageName.isNullOrEmpty()) return@OnOpActiveChangedListener
        val permType = when (op) {
            AppOpsManager.OPSTR_CAMERA -> "CAMERA"
            AppOpsManager.OPSTR_RECORD_AUDIO -> "MICROPHONE"
            else -> return@OnOpActiveChangedListener
        }
        val key = "$packageName|$permType"
        if (active) {
            activeSince[key] = System.currentTimeMillis()
        } else {
            val start = activeSince.remove(key) ?: return@OnOpActiveChangedListener
            val durationSec = ((System.currentTimeMillis() - start) / 1000L).coerceAtLeast(1L)
            scope.launch {
                collector.collect(packageName, permType, durationSec)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            stopSelf()
            return
        }
        createChannel()
        startAsForeground()
        registerWatcher()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?) = null

    override fun onDestroy() {
        unregisterWatcher()
        scope.cancel()
        super.onDestroy()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = getSystemService(NotificationManager::class.java) ?: return
        val ch = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.privacy_foreground_channel_name),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = getString(R.string.privacy_foreground_channel_desc)
            setShowBadge(false)
        }
        nm.createNotificationChannel(ch)
    }

    private fun startAsForeground() {
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle(getString(R.string.privacy_foreground_notification_title))
            .setContentText(getString(R.string.privacy_foreground_notification_text))
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
            )
        } else {
            @Suppress("DEPRECATION")
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun registerWatcher() {
        val ops = getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: run {
            Log.e(TAG, "AppOpsManager unavailable")
            return
        }
        appOps = ops
        val executor = ContextCompat.getMainExecutor(this)
        try {
            ops.startWatchingActive(
                arrayOf(
                    AppOpsManager.OPSTR_CAMERA,
                    AppOpsManager.OPSTR_RECORD_AUDIO,
                ),
                executor,
                opListener,
            )
            Log.i(TAG, "Watching CAMERA / RECORD_AUDIO active ops")
        } catch (e: Exception) {
            Log.e(TAG, "startWatchingActive failed", e)
        }
    }

    private fun unregisterWatcher() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
        try {
            appOps?.stopWatchingActive(opListener)
        } catch (e: Exception) {
            Log.w(TAG, "stopWatchingActive", e)
        }
        appOps = null
    }

    private companion object {
        private const val TAG = "PrivacyScannerFg"
        private const val CHANNEL_ID = "callcheck_privacy_scanner"
        private const val NOTIFICATION_ID = 7101
    }
}
