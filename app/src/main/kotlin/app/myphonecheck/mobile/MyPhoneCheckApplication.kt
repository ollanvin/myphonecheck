package app.myphonecheck.mobile

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import app.myphonecheck.mobile.feature.privacycheck.PrivacyScannerForegroundService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyPhoneCheckApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.startForegroundService(
                this,
                Intent(this, PrivacyScannerForegroundService::class.java),
            )
        }
    }
}
