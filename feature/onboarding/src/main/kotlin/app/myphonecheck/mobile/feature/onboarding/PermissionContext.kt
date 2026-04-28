package app.myphonecheck.mobile.feature.onboarding

import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

/**
 * Permission probes for onboarding Page 5 only.
 * (Mirrors app NavHost helpers — refactor isolation; Settings keeps app-local extensions.)
 */
internal fun Context.hasDrawOverlayPermission(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)

internal fun Context.hasUsageStatsPermission(): Boolean {
    val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName,
        )
    } else {
        @Suppress("DEPRECATION")
        appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName,
        )
    }
    return mode == AppOpsManager.MODE_ALLOWED
}

internal fun Context.hasReadPhoneStatePermission(): Boolean =
    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) ==
        PackageManager.PERMISSION_GRANTED
