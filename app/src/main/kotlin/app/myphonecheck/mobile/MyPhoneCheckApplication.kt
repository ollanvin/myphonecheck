package app.myphonecheck.mobile

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyPhoneCheckApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Do not start foreground services from Application.onCreate().
        // Android 12+ blocks this path and can crash app launch.
    }
}
