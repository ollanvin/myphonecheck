package app.myphonecheck.mobile.worker

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

private const val TAG = "WeeklyReportScheduler"

/**
 * WeeklyReportWorker 스케줄러.
 *
 * 매주 월요일 09:00에 실행되도록 주기적 작업을 등록합니다.
 * WorkManager의 PeriodicWorkRequest (7일 간격) 사용.
 *
 * 제약 조건:
 * - 배터리 부족 시 미실행 (requiresBatteryNotLow)
 * - 네트워크 불필요 (온디바이스 전용)
 *
 * KEEP 정책: 이미 등록된 동일 이름 작업이 있으면 유지.
 * 앱 재설치/업데이트 시 자동 재등록.
 */
object WeeklyReportScheduler {

    /**
     * 주간 리포트 작업을 WorkManager에 등록합니다.
     * Application.onCreate()에서 호출.
     */
    fun schedule(context: Context) {
        try {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            // 다음 월요일 09:00까지의 초기 지연 계산
            val initialDelayMinutes = calculateInitialDelayMinutes()

            val workRequest = PeriodicWorkRequestBuilder<WeeklyReportWorker>(
                7, TimeUnit.DAYS,
            )
                .setConstraints(constraints)
                .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WeeklyReportWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest,
            )

            Log.i(TAG, "Weekly report scheduled (initial delay: ${initialDelayMinutes}min)")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule weekly report", e)
        }
    }

    /**
     * 다음 월요일 09:00까지의 분 단위 지연을 계산합니다.
     */
    private fun calculateInitialDelayMinutes(): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // 이미 지난 월요일이면 다음 주로
            if (before(now) || this == now) {
                add(Calendar.WEEK_OF_YEAR, 1)
            }
        }

        val diffMillis = target.timeInMillis - now.timeInMillis
        return (diffMillis / (60 * 1000)).coerceAtLeast(1)
    }
}
