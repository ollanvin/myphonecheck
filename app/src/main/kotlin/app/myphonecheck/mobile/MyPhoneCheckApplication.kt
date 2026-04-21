
package app.myphonecheck.mobile

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import app.myphonecheck.mobile.core.util.DevicePatternProfileBootstrapper
import app.myphonecheck.mobile.core.util.GlobalNumberEngineProfileStore
import app.myphonecheck.mobile.feature.privacycheck.InitialScanOrchestrator
import app.myphonecheck.mobile.worker.WeeklyReportScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyPhoneCheckApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var initialScanOrchestrator: InitialScanOrchestrator

    /** Application 수명 스코프 — 비동기 초기화용 */
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {

        super.onCreate()
        // v4.3: OverlayTrigger.fire() and ForcePhoneListener removed (legacy dead code)

        // Do not start foreground services from Application.onCreate().
        // Android 12+ blocks this path and can crash app launch.

        // SQLCipher native library — Room DB 개방 전 반드시 선행되어야 함
        Log.i(TAG, "Loading SQLCipher native libs...")
        System.loadLibrary("sqlcipher")
        Log.i(TAG, "SQLCipher native libs loaded successfully")

        // 중앙 보안 리포트 초기화
        GlobalNumberEngineProfileStore.initialize(this)
        DevicePatternProfileBootstrapper.refreshIfNeededAsync(this)
        WeeklyReportScheduler.schedule(this)

        // Initial Scan — baseline 생성 (재실행 규칙 적용)
        // shouldRun() 결과에 따라 실행/스킵 결정.
        // Home 진입마다 전체 재생성 금지 — 여기서 1회만 실행.
        applicationScope.launch {
            try {
                if (initialScanOrchestrator.shouldRun()) {
                    Log.i(TAG, "InitialScan: launching baseline scan...")
                    initialScanOrchestrator.runInitialScan()
                    Log.i(TAG, "InitialScan: baseline scan complete")
                } else {
                    Log.i(TAG, "InitialScan: baseline already valid — skipping")
                }
            } catch (e: Exception) {
                Log.e(TAG, "InitialScan: failed", e)
            }
        }
    }

    private companion object {
        const val TAG = "MyPhoneCheckApp"
    }
}
