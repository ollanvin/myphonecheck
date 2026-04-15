package app.myphonecheck.mobile

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import app.myphonecheck.mobile.core.util.DevicePatternProfileBootstrapper
import app.myphonecheck.mobile.core.util.GlobalNumberEngineProfileStore
import app.myphonecheck.mobile.worker.WeeklyReportScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyPhoneCheckApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Do not start foreground services from Application.onCreate().
        // Android 12+ blocks this path and can crash app launch.

        // SQLCipher native library 초기화 — Room DB 개방 전 반드시 호출되어야 함.
        // sqlcipher-android 4.6.0+ 공식 권장 방식: System.loadLibrary("sqlcipher").
        // (이전 android-database-sqlcipher의 SQLiteDatabase.loadLibs()는 deprecated)
        Log.i("MyPhoneCheckApp", "Loading SQLCipher native libs...")
        System.loadLibrary("sqlcipher")
        Log.i("MyPhoneCheckApp", "SQLCipher native libs loaded successfully")

        // 주간 보안 리포트 스케줄링
        GlobalNumberEngineProfileStore.initialize(this)
        DevicePatternProfileBootstrapper.refreshIfNeededAsync(this)
        WeeklyReportScheduler.schedule(this)
    }
}
