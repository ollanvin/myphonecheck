package app.callcheck.mobile.feature.pushintercept

import android.app.Notification
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import java.util.Calendar
import java.util.concurrent.ConcurrentHashMap

/**
 * PushCheck 알림 리스너 서비스.
 *
 * NotificationListenerService를 상속하여
 * 디바이스에 수신되는 모든 알림을 온디바이스로 감시합니다.
 *
 * ═══════════════════════════════════════════════
 * 동작 방식:
 * 1. onNotificationPosted() — 알림 수신 시 호출
 * 2. 앱/채널 정보 수집
 * 3. 빈도/패턴 통계 업데이트
 * 4. PushCheckEngine.evaluate() 호출
 * 5. 결과를 로컬 저장소에 기록
 * ═══════════════════════════════════════════════
 *
 * 권한 요구사항:
 * - BIND_NOTIFICATION_LISTENER_SERVICE
 * - 사용자가 설정에서 직접 활성화 필요
 *
 * 원칙:
 * - 알림 내용은 디바이스 외부로 절대 전송하지 않음
 * - 알림을 자동으로 차단/삭제하지 않음
 * - 사용자에게 판단 보조 정보만 제공
 */
class PushInterceptService : NotificationListenerService() {

    private companion object {
        private const val TAG = "PushInterceptService"

        /** 자체 앱 알림 무시 */
        private const val SELF_PACKAGE = "app.callcheck.mobile"

        /** 시스템 앱 무시 대상 */
        private val SYSTEM_IGNORE = setOf(
            "android",
            "com.android.systemui",
            "com.android.providers.downloads",
        )

        /** 프로모션 키워드 (다국어) */
        private val PROMOTION_KEYWORDS = setOf(
            // 한국어
            "할인", "쿠폰", "이벤트", "세일", "특가", "무료", "혜택",
            "프로모션", "적립", "포인트", "마감", "한정", "기간한정",
            // 영어
            "sale", "discount", "coupon", "offer", "deal", "free",
            "promo", "limited", "exclusive", "reward", "cashback",
            // 일본어
            "セール", "割引", "クーポン", "無料", "限定", "ポイント",
        )
    }

    /**
     * 앱별 알림 통계 (온디바이스 메모리).
     * key = packageName
     */
    private val appStats = ConcurrentHashMap<String, AppNotificationStats>()

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return

        val packageName = sbn.packageName ?: return

        // 자체 앱 및 시스템 앱 무시
        if (packageName == SELF_PACKAGE) return
        if (packageName in SYSTEM_IGNORE) return

        try {
            processNotification(sbn)
        } catch (e: Exception) {
            Log.e(TAG, "Error processing notification from $packageName", e)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // 알림 제거 시 상호작용 기록 업데이트 가능
        // 현재는 별도 처리 없음
    }

    private fun processNotification(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val notification = sbn.notification ?: return

        // 앱 정보 수집
        val appLabel = getAppLabel(packageName)
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.channelId
        } else null

        // 알림 텍스트 추출
        val extras = notification.extras
        val title = extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        val text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString()

        // 프로모션 키워드 매칭
        val combinedText = listOfNotNull(title, text).joinToString(" ").lowercase()
        val promotionHits = PROMOTION_KEYWORDS.count { combinedText.contains(it) }

        // 통계 업데이트
        val stats = appStats.getOrPut(packageName) { AppNotificationStats() }
        stats.recordNotification(System.currentTimeMillis())

        // 야간 여부 확인
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val isNightTime = hour >= 22 || hour < 7

        // PushEvidence 생성
        val evidence = app.callcheck.mobile.core.model.PushEvidence(
            packageName = packageName,
            appLabel = appLabel,
            channelId = channelId,
            channelName = null, // API 제한으로 채널 이름 직접 접근 어려움
            title = title,
            text = text,
            countLast24h = stats.countLast24h(),
            countLast7d = stats.countLast7d(),
            promotionKeywordHits = promotionHits,
            isNightTime = isNightTime,
            interactionRate = stats.interactionRate,
            receivedAtMillis = System.currentTimeMillis(),
        )

        // 판단 엔진 호출
        val result = PushCheckEngine.evaluate(evidence)

        Log.d(TAG, "[PushCheck] $appLabel: ${result.category.summaryKo} " +
            "(risk=${result.riskLevel}, 24h=${evidence.countLast24h}건)")

        // TODO: 결과를 로컬 DB에 저장 + 필요 시 사용자에게 로컬 알림
    }

    private fun getAppLabel(packageName: String): String {
        return try {
            val pm = applicationContext.packageManager
            val appInfo: ApplicationInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getApplicationInfo(packageName, 0)
            }
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName.substringAfterLast(".")
        }
    }

    /**
     * 앱별 알림 통계 (온디바이스 메모리 전용).
     *
     * 최근 7일간의 알림 타임스탬프를 유지합니다.
     * 앱 재시작 시 초기화되며, 영구 저장은 Room DB로 이관 예정.
     */
    internal class AppNotificationStats {
        private val timestamps = mutableListOf<Long>()
        var interactionRate: Float = 0f
            private set
        private var totalInteractions: Int = 0
        private var totalNotifications: Int = 0

        @Synchronized
        fun recordNotification(timestampMillis: Long) {
            timestamps.add(timestampMillis)
            totalNotifications++
            // 7일 이상 지난 기록 제거
            val cutoff = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L
            timestamps.removeAll { it < cutoff }
        }

        @Synchronized
        fun recordInteraction() {
            totalInteractions++
            interactionRate = if (totalNotifications > 0) {
                totalInteractions.toFloat() / totalNotifications
            } else 0f
        }

        @Synchronized
        fun countLast24h(): Int {
            val cutoff = System.currentTimeMillis() - 24 * 60 * 60 * 1000L
            return timestamps.count { it >= cutoff }
        }

        @Synchronized
        fun countLast7d(): Int = timestamps.size
    }
}
