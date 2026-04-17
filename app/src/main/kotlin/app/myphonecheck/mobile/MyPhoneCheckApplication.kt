
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
        // v4.3: OverlayTrigger.fire() and ForcePhoneListener removed (legacy dead code)

        // Do not start foreground services from Application.onCreate().
        // Android 12+ blocks this path and can crash app launch.

        // SQLCipher native library 珥덇린????Room DB 媛쒕갑 ??諛섎뱶???몄텧?섏뼱????
        // sqlcipher-android 4.6.0+ 怨듭떇 沅뚯옣 諛⑹떇: System.loadLibrary("sqlcipher").
        // (?댁쟾 android-database-sqlcipher??SQLiteDatabase.loadLibs()??deprecated)
        Log.i("MyPhoneCheckApp", "Loading SQLCipher native libs...")
        System.loadLibrary("sqlcipher")
        Log.i("MyPhoneCheckApp", "SQLCipher native libs loaded successfully")

        // 二쇨컙 蹂댁븞 由ы룷???ㅼ?以꾨쭅
        GlobalNumberEngineProfileStore.initialize(this)
        DevicePatternProfileBootstrapper.refreshIfNeededAsync(this)
        WeeklyReportScheduler.schedule(this)
    }
}






