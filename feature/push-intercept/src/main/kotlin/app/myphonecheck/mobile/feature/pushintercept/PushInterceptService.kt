package app.myphonecheck.mobile.feature.pushintercept

import android.app.Notification
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import app.myphonecheck.mobile.data.localcache.dao.MessageHubDao
import app.myphonecheck.mobile.data.localcache.dao.PushStatsDao
import app.myphonecheck.mobile.data.localcache.entity.MessageHubEntity
import app.myphonecheck.mobile.data.localcache.entity.PushStatsEntity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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
 * 4. URL 링크 탐지 (정규식)
 * 5. PushCheckEngine.evaluate() 호출
 * 6. 결과를 Room DB(message_hub)에 저장
 * 7. 차단된 발신자의 알림 자동 캔슬
 * ═══════════════════════════════════════════════
 *
 * 권한 요구사항:
 * - BIND_NOTIFICATION_LISTENER_SERVICE
 * - 사용자가 설정에서 직접 활성화 필요
 *
 * 원칙:
 * - 알림 내용은 디바이스 외부로 절대 전송하지 않음
 * - 차단된 발신자 알림만 자동 캔슬 (사용자 명시 차단)
 * - 사용자에게 판단 보조 정보 제공
 *
 * DI 방식:
 * - NotificationListenerService는 시스템이 인스턴스를 생성하므로
 *   @AndroidEntryPoint 사용 불가.
 * - EntryPointAccessors로 Hilt 그래프에서 직접 주입.
 */
class PushInterceptService : NotificationListenerService() {

    /** Hilt EntryPoint — NotificationListenerService용 DI 접근점 */
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PushInterceptEntryPoint {
        fun messageHubDao(): MessageHubDao
        fun pushStatsDao(): PushStatsDao
    }

    private companion object {
        private const val TAG = "PushInterceptService"
        private const val PUSH_CHECK_ENABLED = false

        /** 자체 앱 알림 무시 */
        private const val SELF_PACKAGE = "app.myphonecheck.mobile"

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

        /**
         * URL 탐지 정규식.
         *
         * RFC 3986 기반 — http(s) 스키마 URL + 스키마 없는 도메인 패턴 모두 탐지.
         * 피싱/스미싱 링크 식별의 기반.
         */
        private val URL_REGEX = Regex(
            """(?:https?://[^\s<>"{}|\\^`\[\]]+)|""" +
                """(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\.)+[a-zA-Z]{2,}(?:/[^\s<>"{}|\\^`\[\]]*)?)""",
            RegexOption.IGNORE_CASE,
        )
    }

    /**
     * 앱별 알림 통계 (온디바이스 메모리).
     * key = packageName
     */
    private val appStats = ConcurrentHashMap<String, AppNotificationStats>()

    /** 서비스 전용 코루틴 스코프 (IO 디스패처) */
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** Room DAO (lazy — 서비스 연결 후 Hilt 그래프 접근) */
    private val messageHubDao: MessageHubDao by lazy {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            PushInterceptEntryPoint::class.java,
        )
        entryPoint.messageHubDao()
    }

    /** 푸시 통계 DAO (lazy) */
    private val pushStatsDao: PushStatsDao by lazy {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            PushInterceptEntryPoint::class.java,
        )
        entryPoint.pushStatsDao()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        serviceScope.cancel()
    }

override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (!PUSH_CHECK_ENABLED) return
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
        if (!PUSH_CHECK_ENABLED) return
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

        // URL 링크 탐지
        val combinedText = listOfNotNull(title, text).joinToString(" ")
        val detectedLinks = detectLinks(combinedText)
        val linkCount = detectedLinks.size

        // 프로모션 키워드 매칭
        val lowered = combinedText.lowercase()
        val promotionHits = PROMOTION_KEYWORDS.count { lowered.contains(it) }

        // 통계 업데이트
        val stats = appStats.getOrPut(packageName) { AppNotificationStats() }
        stats.recordNotification(System.currentTimeMillis())

        // 야간 여부 확인
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val isNightTime = hour >= 22 || hour < 7

        // PushEvidence 생성
        val evidence = app.myphonecheck.mobile.core.model.PushEvidence(
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
            "(risk=${result.riskLevel}, 24h=${evidence.countLast24h}건, links=$linkCount)")

        // Room DB 저장 + 차단 발신자 자동 캔슬
        serviceScope.launch {
            try {
                // 차단된 발신자 확인
                val isBlocked = messageHubDao.isBlockedSender(packageName)

                // 엔티티 생성 및 저장
                val entity = MessageHubEntity(
                    packageName = packageName,
                    appLabel = appLabel,
                    channelId = channelId,
                    title = title,
                    text = text,
                    detectedLinks = if (detectedLinks.isNotEmpty()) {
                        detectedLinks.joinToString(",")
                    } else null,
                    linkCount = linkCount,
                    riskLevel = result.riskLevel.name,
                    category = result.category.name,
                    action = result.action.name,
                    confidence = result.confidence,
                    summary = result.summary,
                    reasons = if (result.reasons.isNotEmpty()) {
                        result.reasons.joinToString("|")
                    } else null,
                    promotionKeywordHits = promotionHits,
                    isNightTime = isNightTime,
                    isBlocked = isBlocked,
                    receivedAt = evidence.receivedAtMillis,
                )

                messageHubDao.insert(entity)
                Log.d(TAG, "[PushCheck] DB saved: $appLabel (id=$packageName, blocked=$isBlocked)")

                // 푸시 통계 원자적 증가
                recordPushStats(
                    packageName = packageName,
                    appLabel = appLabel,
                    isNightTime = isNightTime,
                    hasPromotion = promotionHits > 0,
                    hasLinks = linkCount > 0,
                    isHighRisk = result.riskLevel == app.myphonecheck.mobile.core.model.RiskLevel.HIGH,
                )

                // 차단된 발신자의 알림 자동 캔슬
                if (isBlocked) {
                    cancelNotification(sbn.key)
                    Log.d(TAG, "[PushCheck] Auto-cancelled blocked sender: $appLabel")
                }
            } catch (e: Exception) {
                Log.e(TAG, "[PushCheck] DB save failed for $appLabel", e)
            }
        }
    }

    /**
     * 푸시 통계 원자적 기록.
     *
     * ensureRow → incrementXxx 패턴으로 동시성 안전.
     * dateKey = yyyy-MM-dd (디바이스 타임존).
     */
    private suspend fun recordPushStats(
        packageName: String,
        appLabel: String,
        isNightTime: Boolean,
        hasPromotion: Boolean,
        hasLinks: Boolean,
        isHighRisk: Boolean,
    ) {
        try {
            val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                .format(System.currentTimeMillis())

            // row가 없으면 생성 (INSERT OR IGNORE)
            pushStatsDao.ensureRow(
                PushStatsEntity(
                    packageName = packageName,
                    appLabel = appLabel,
                    dateKey = dateKey,
                ),
            )

            // 원자적 증가
            pushStatsDao.incrementTotal(packageName, dateKey)
            if (isNightTime) pushStatsDao.incrementNight(packageName, dateKey)
            if (hasPromotion) pushStatsDao.incrementPromotion(packageName, dateKey)
            if (hasLinks) pushStatsDao.incrementLink(packageName, dateKey)
            if (isHighRisk) pushStatsDao.incrementHighRisk(packageName, dateKey)

            Log.d(TAG, "[PushStats] Recorded: $appLabel ($dateKey)")
        } catch (e: Exception) {
            Log.w(TAG, "[PushStats] Record failed (non-fatal): ${e.message}")
        }
    }

    /**
     * 텍스트에서 URL을 탐지한다.
     *
     * @param text 분석 대상 텍스트
     * @return 탐지된 URL 리스트 (중복 제거)
     */
    private fun detectLinks(text: String): List<String> {
        if (text.isBlank()) return emptyList()
        return URL_REGEX.findAll(text)
            .map { it.value.trimEnd('.', ',', ')', ']', ';', ':') }
            .distinct()
            .toList()
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
     * 앱 재시작 시 초기화되며, 영구 저장은 Room DB(message_hub)로 이관 완료.
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
