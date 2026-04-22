package app.myphonecheck.mobile.feature.pushtrash.util

import android.content.Context
import android.provider.Settings

object NotificationListenerState {

    fun isEnabled(context: Context): Boolean {
        val flat = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners",
        ) ?: return false
        val pkg = context.packageName
        return flat.contains(pkg)
    }
}
