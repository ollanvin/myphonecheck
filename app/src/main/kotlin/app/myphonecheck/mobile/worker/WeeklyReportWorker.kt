package app.myphonecheck.mobile.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.myphonecheck.mobile.R
import app.myphonecheck.mobile.data.localcache.dao.MessageHubDao
import app.myphonecheck.mobile.data.localcache.dao.PrivacyHistoryDao
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
 * 3대 집계 지표 (v1.1: PushCheck 제거):
 * 1. CallCheck: 위험/주의/낮은위험 판정 건수
 * 2. PrivacyCheck: 이상 탐지 건수 + 미확인 건수
 * 3. MessageCheck: 링크 포함 메시지/고위험 메시지 건수
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

        // PushCheck: REMOVED per v1.1 Architecture

        // 2. PrivacyCheck 집계
        val privacyAll = privacyHistoryDao.getAllOnce()
        val recentPrivacy = privacyAll.filter { it.usedAt >= startMillis }
        val anomalyCount = recentPrivacy.count { it.isAnomaly }
        val unverifiedCount = recentPrivacy.count {
            it.isAnomaly && it.userVerified == "UNVERIFIED"
        }

        // 3. MessageCheck 집계
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
                    description = applicationContext.getString(R.string.weekly_report_channel_desc)
                }
                nm.createNotificationChannel(channel)
            }

            val ctx = applicationContext
            val summaryText = buildString {
                append(ctx.getString(R.string.weekly_report_calls_fmt, report.totalCalls))
                if (report.highRiskCalls > 0) append(ctx.getString(R.string.weekly_report_calls_risk_fmt, report.highRiskCalls))
                // PushCheck notification removed per v1.1
                if (report.privacyAnomalies > 0) {
                    append(ctx.getString(R.string.weekly_report_privacy_fmt, report.privacyAnomalies))
                }
            }

            val notification = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                android.app.Notification.Builder(applicationContext, CHANNEL_ID)
            } else {
                @Suppress("DEPRECATION")
                android.app.Notification.Builder(applicationContext)
            }
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(ctx.getString(R.string.weekly_report_title_fmt, report.periodStart, report.periodEnd))
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
    // PushCheck: REMOVED per v1.1
    // PrivacyCheck
    val privacyAnomalies: Int,
    val privacyUnverified: Int,
    // MessageCheck
    val linkMessages: Int,
    val highRiskMessages: Int,
)
