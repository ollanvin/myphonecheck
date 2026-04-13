package app.myphonecheck.mobile.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.myphonecheck.mobile.data.localcache.dao.MessageHubDao
import app.myphonecheck.mobile.data.localcache.dao.PrivacyHistoryDao
import app.myphonecheck.mobile.data.localcache.dao.PushStatsDao
import app.myphonecheck.mobile.data.localcache.dao.UserCallRecordDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val TAG = "WeeklyReportWorker"

/**
 * 주간 보안 리포트 생성 Worker.
 *
 * 매주 월요일 09:00에 WorkManager가 자동 실행.
 * 지난 7일간의 4대 지표를 집계하여 로컬 저장.
 *
 * ═══════════════════════════════════════════════
 * 4대 집계 지표:
 * 1. CallCheck: 위험/주의/낮은위험 판정 건수
 * 2. PushCheck: 총 알림/야간/프로모션/링크포함/고위험 건수
 * 3. PrivacyCheck: 이상 탐지 건수 + 미확인 건수
 * 4. MessageCheck: 링크 포함 메시지/고위험 메시지 건수
 * ═══════════════════════════════════════════════
 *
 * 출력:
 * - WeeklyReportEntity (로컬 DB 저장)
 * - Notification으로 요약 표시
 *
 * 네트워크 전송: 없음 (온디바이스 전용)
 */
@HiltWorker
class WeeklyReportWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val userCallRecordDao: UserCallRecordDao,
    private val pushStatsDao: PushStatsDao,
    private val privacyHistoryDao: PrivacyHistoryDao,
    private val messageHubDao: MessageHubDao,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.i(TAG, "Weekly report generation started")

        return try {
            val report = generateWeeklyReport()
            Log.i(TAG, "Weekly report generated: $report")

            // Notification으로 요약 표시
            showReportNotification(report)

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Weekly report generation failed", e)
            Result.retry()
        }
    }

    private suspend fun generateWeeklyReport(): WeeklyReport {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        val endDate = dateFormat.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, -7)
        val startDate = dateFormat.format(cal.time)
        val startMillis = cal.timeInMillis

        // 1. CallCheck 집계
        val callRecords = userCallRecordDao.getAllOnce()
        val recentCalls = callRecords.filter { it.updatedAt >= startMillis }
        val highRiskCalls = recentCalls.count { it.aiRiskLevel == "HIGH" }
        val mediumRiskCalls = recentCalls.count { it.aiRiskLevel == "MEDIUM" }
        val lowRiskCalls = recentCalls.count { it.aiRiskLevel == "LOW" }
        val blockedCalls = recentCalls.count { it.lastAction == "blocked" }

        // 2. PushCheck 집계
        val pushTotal = pushStatsDao.getTotalAggregation(startDate, endDate)
        val totalPush = pushTotal?.totalCount ?: 0
        val nightPush = pushTotal?.nightCount ?: 0
        val promotionPush = pushTotal?.promotionCount ?: 0
        val linkPush = pushTotal?.linkCount ?: 0
        val highRiskPush = pushTotal?.highRiskCount ?: 0

        // 3. PrivacyCheck 집계
        val privacyAll = privacyHistoryDao.getAllOnce()
        val recentPrivacy = privacyAll.filter { it.usedAt >= startMillis }
        val anomalyCount = recentPrivacy.count { it.isAnomaly }
        val unverifiedCount = recentPrivacy.count {
            it.isAnomaly && it.userVerified == "UNVERIFIED"
        }

        // 4. MessageCheck 집계
        val messagesAll = messageHubDao.getAllOnce()
        val recentMessages = messagesAll.filter { it.receivedAt >= startMillis }
        val linkMessages = recentMessages.count { it.linkCount > 0 }
        val highRiskMessages = recentMessages.count { it.riskLevel == "HIGH" }

        return WeeklyReport(
            periodStart = startDate,
            periodEnd = endDate,
            // CallCheck
            totalCalls = recentCalls.size,
            highRiskCalls = highRiskCalls,
            mediumRiskCalls = mediumRiskCalls,
            lowRiskCalls = lowRiskCalls,
            blockedCalls = blockedCalls,
            // PushCheck
            totalPush = totalPush,
            nightPush = nightPush,
            promotionPush = promotionPush,
            linkPush = linkPush,
            highRiskPush = highRiskPush,
            // PrivacyCheck
            privacyAnomalies = anomalyCount,
            privacyUnverified = unverifiedCount,
            // MessageCheck
            linkMessages = linkMessages,
            highRiskMessages = highRiskMessages,
        )
    }

    private fun showReportNotification(report: WeeklyReport) {
        try {
            val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as android.app.NotificationManager

            // 채널 생성 (Android O+)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channel = android.app.NotificationChannel(
                    CHANNEL_ID,
                    "Weekly Security Report",
                    android.app.NotificationManager.IMPORTANCE_LOW,
                ).apply {
                    description = "주간 보안 리포트 알림"
                }
                nm.createNotificationChannel(channel)
            }

            val summaryText = buildString {
                append("전화 ${report.totalCalls}건")
                if (report.highRiskCalls > 0) append(" (위험 ${report.highRiskCalls})")
                append(" · 알림 ${report.totalPush}건")
                if (report.highRiskPush > 0) append(" (위험 ${report.highRiskPush})")
                if (report.privacyAnomalies > 0) {
                    append(" · 권한 이상 ${report.privacyAnomalies}건")
                }
            }

            val notification = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                android.app.Notification.Builder(applicationContext, CHANNEL_ID)
            } else {
                @Suppress("DEPRECATION")
                android.app.Notification.Builder(applicationContext)
            }
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("주간 보안 리포트 (${report.periodStart} ~ ${report.periodEnd})")
                .setContentText(summaryText)
                .setAutoCancel(true)
                .build()

            nm.notify(NOTIFICATION_ID, notification)
            Log.i(TAG, "Report notification shown: $summaryText")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to show report notification (non-fatal)", e)
        }
    }

    companion object {
        const val WORK_NAME = "weekly_security_report"
        private const val CHANNEL_ID = "weekly_report_channel"
        private const val NOTIFICATION_ID = 9001
    }
}

/**
 * 주간 보안 리포트 데이터.
 * 온디바이스 전용 — 서버 전송 없음.
 */
data class WeeklyReport(
    val periodStart: String,
    val periodEnd: String,
    // CallCheck
    val totalCalls: Int,
    val highRiskCalls: Int,
    val mediumRiskCalls: Int,
    val lowRiskCalls: Int,
    val blockedCalls: Int,
    // PushCheck
    val totalPush: Int,
    val nightPush: Int,
    val promotionPush: Int,
    val linkPush: Int,
    val highRiskPush: Int,
    // PrivacyCheck
    val privacyAnomalies: Int,
    val privacyUnverified: Int,
    // MessageCheck
    val linkMessages: Int,
    val highRiskMessages: Int,
)
