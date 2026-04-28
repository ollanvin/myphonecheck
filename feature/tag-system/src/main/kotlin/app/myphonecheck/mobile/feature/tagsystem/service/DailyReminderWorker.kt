package app.myphonecheck.mobile.feature.tagsystem.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.myphonecheck.mobile.feature.tagsystem.repository.RoomTagRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * 일일 리마인드 워커 (Architecture v2.1.0 §32-5-3).
 *
 * REMIND_ME priority 태그 중 7일 이상 미수신 항목을 알림으로 표시.
 * 본 PR 범위: pending 조회만 + Result.success() 반환. 실제 NotificationManager 알림 발행은 후속 PR
 * (사용자 옵트인 + NotificationChannel 설정 통합 후).
 *
 * WorkManager 등록은 후속 PR — 본 PR은 worker 클래스 + Result만 명문화.
 */
@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val tagRepository: RoomTagRepository,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val sevenDaysAgo = System.currentTimeMillis() - REMINDER_THRESHOLD_MILLIS
        val pending = tagRepository.pendingReminders(sevenDaysAgo)
        // 본 PR: pending 갯수 확인까지. 알림 표시는 후속 PR.
        return if (pending.isNotEmpty()) Result.success() else Result.success()
    }

    companion object {
        const val WORK_NAME = "daily_tag_reminder"
        const val REMINDER_THRESHOLD_MILLIS = 7L * 24 * 60 * 60 * 1000L
    }
}
