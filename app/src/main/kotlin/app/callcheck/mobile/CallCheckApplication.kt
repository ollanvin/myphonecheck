package app.callcheck.mobile

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CallCheckApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize any application-level dependencies here if needed
        // Hilt will inject dependencies where required
    }
}
