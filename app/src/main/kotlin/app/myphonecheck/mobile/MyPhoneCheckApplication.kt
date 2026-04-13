package app.myphonecheck.mobile

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
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

        // 주간 보안 리포트 스케줄링
        WeeklyReportScheduler.schedule(this)
    }
}
