package app.callcheck.mobile.feature.callintercept

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.telecom.TelecomManager
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import app.callcheck.mobile.core.model.DecisionResult
import app.callcheck.mobile.core.model.DeviceEvidence
import app.callcheck.mobile.core.model.RiskLevel
import app.callcheck.mobile.core.model.SearchEvidence
import app.callcheck.mobile.feature.countryconfig.SignalSummaryLocalizer
import app.callcheck.mobile.feature.countryconfig.SupportedLanguage
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "CallerIdOverlay"

/**
 * 전화 앱 위에 판정 결과 오버레이를 표시한다.
 *
 * 글로벌 대응:
 * - 모든 UI 텍스트는 SupportedLanguage + SignalSummaryLocalizer를 통해 로컬라이즈
 * - 번호는 raw 형식 유지, 의미 문구만 locale에 맞게 변환
 * - 검색 엔진 이름(Google, Naver 등)은 UI에 절대 노출하지 않음
 *
 * 대상: 미저장 번호만. 저장된 연락처는 이 매니저에 도달하지 않음.
 */
@Singleton
class CallerIdOverlayManager @Inject constructor() {

    private var overlayView: View? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * 오버레이를 표시한다.
     *
     * @param context Android Context
     * @param result 판정 결과
     * @param phoneNumber raw 번호 (기기 원본 그대로)
     * @param language 현재 기기 언어
     * @param localizer SignalSummary 로컬라이저
     * @param phaseLabel 2-Phase UX 라벨. null이면 표시 안 함.
     *        예: "즉시 판단", "추가 확인됨 — 위험 상승", "추가 확인됨 — 위험 하락"
     */
    fun showOverlay(
        context: Context,
        result: DecisionResult,
        phoneNumber: String,
        language: SupportedLanguage = SupportedLanguage.EN,
        localizer: SignalSummaryLocalizer = SignalSummaryLocalizer(),
        phaseLabel: String? = null,
    ): Boolean {
        if (!canDrawOverlays(context)) {
            Log.w(TAG, "SYSTEM_ALERT_WINDOW not granted, cannot show overlay")
            return false
        }

        mainHandler.post {
            try {
                dismissOverlayInternal(context)

                val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

                val horizontalMargin = dpToPx(context, 12)
                val screenWidth = context.resources.displayMetrics.widthPixels
                val params = WindowManager.LayoutParams(
                    screenWidth - (horizontalMargin * 2),
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                    PixelFormat.TRANSLUCENT,
                ).apply {
                    gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                    y = dpToPx(context, 200)
                }

                overlayView = buildOverlayView(context, result, phoneNumber, language, localizer, phaseLabel)
                wm.addView(overlayView, params)
                Log.i(TAG, "Overlay shown for $phoneNumber: ${result.riskLevel} (lang=${language.code})")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to show overlay", e)
            }
        }
        Log.i(TAG, "Overlay requested for $phoneNumber (posted to main thread)")
        return true
    }

    fun dismissOverlay(context: Context) {
        mainHandler.post {
            dismissOverlayInternal(context)
        }
    }

    private fun dismissOverlayInternal(context: Context) {
        overlayView?.let {
            try {
                val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                wm.removeView(it)
                Log.i(TAG, "Overlay dismissed")
            } catch (e: Exception) {
                Log.w(TAG, "Overlay dismiss error (may already be removed)", e)
            }
            overlayView = null
        }
    }

    fun isOverlayShowing(): Boolean = overlayView != null

    private fun canDrawOverlays(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    // ══════════════════════════════════════════════
    // View Builder
    // ══════════════════════════════════════════════

    /**
     * 1초 인지 오버레이 — 프리미엄 설계.
     *
     * 구조:
     * ┌────────────────────────────────┐
     * │ [색상 배경]                       │
     * │   ██ 한 단어 판정 (24sp Bold)     │
     * │   카테고리 · 번호 · 신뢰도%       │
     * │   ── 구분선 ──                   │
     * │   • 근거 1 (최대 2줄)            │
     * │   • 근거 2                       │
     * │   ── 구분선 ──                   │
     * │   [수신] [거절] [차단]            │
     * └────────────────────────────────┘
     *
     * 1초 인지의 핵심: 색상(배경) + 한 단어(HERO) → 0.3초 판단.
     * 나머지 정보는 보조. 근거는 최대 2줄로 제한.
     */
    private fun buildOverlayView(
        context: Context,
        result: DecisionResult,
        phoneNumber: String,
        language: SupportedLanguage,
        localizer: SignalSummaryLocalizer,
        phaseLabel: String? = null,
    ): View {
        val bgColor = backgroundColorForRisk(result.riskLevel)
        val textColor = Color.WHITE
        val subtleColor = adjustAlpha(textColor, 0.80f)
        val uiText = OverlayUiText.forLanguage(language)

        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                setColor(bgColor)
                cornerRadius = dpToPx(context, 16).toFloat()
            }
            val padH = dpToPx(context, 20)
            setPadding(padH, dpToPx(context, 16), padH, dpToPx(context, 12))
            elevation = dpToPx(context, 8).toFloat()

            // ── HERO: 한 단어 판정 (1초 인지의 핵심) ──
            val verdict = uiText.oneWordVerdict(result.riskLevel)
            addView(TextView(context).apply {
                text = verdict
                setTextColor(textColor)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
            })

            // ── INFO LINE: 카테고리 · 번호 · 신뢰도 ──
            val categoryText = localizer.localizeCategory(result.category.name, language)
            val confidencePercent = (result.confidence * 100).toInt()
            addView(TextView(context).apply {
                text = "$categoryText  \u00B7  $phoneNumber  \u00B7  ${confidencePercent}%"
                setTextColor(subtleColor)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = marginTop(context, 2)
            })

            // ── 2-Phase UX: Phase 상태 태그 ──
            // phaseLabel이 null이면 표시하지 않음 (즉시 판단 = 단일 Phase)
            if (phaseLabel != null) {
                addView(TextView(context).apply {
                    text = phaseLabel
                    setTextColor(Color.WHITE)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                    setTypeface(null, Typeface.BOLD)
                    gravity = Gravity.CENTER_HORIZONTAL
                    background = GradientDrawable().apply {
                        setColor(adjustAlpha(Color.BLACK, 0.35f))
                        cornerRadius = dpToPx(context, 10).toFloat()
                    }
                    val tagPadH = dpToPx(context, 10)
                    val tagPadV = dpToPx(context, 3)
                    setPadding(tagPadH, tagPadV, tagPadH, tagPadV)
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                    ).apply {
                        gravity = Gravity.CENTER_HORIZONTAL
                        topMargin = dpToPx(context, 4)
                    }
                    layoutParams = lp
                })
            }

            // ── Divider ──
            addDivider(context, textColor)

            // ── REASONS: 최대 2줄 (device + search 통합) ──
            val reasons = buildTopReasons(result, language, localizer, uiText)
            for (reason in reasons) {
                addView(TextView(context).apply {
                    text = reason
                    setTextColor(subtleColor)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    layoutParams = marginTop(context, 1)
                })
            }

            // ── Divider ──
            if (reasons.isNotEmpty()) {
                addDivider(context, textColor)
            }

            // ── Action Buttons ──
            addView(buildActionButtons(context, phoneNumber, uiText))
        }
    }

    /**
     * 1초 인지를 위한 핵심 근거 추출 — 최대 2줄.
     *
     * 우선순위:
     * 1. 웹 스캔 signal summary (가장 의미 있는 근거)
     * 2. 기기 기록 요약 (통화 이력 기반)
     * 3. 카테고리 기반 fallback
     */
    private fun buildTopReasons(
        result: DecisionResult,
        language: SupportedLanguage,
        localizer: SignalSummaryLocalizer,
        uiText: OverlayUiText,
    ): List<String> {
        val reasons = mutableListOf<String>()

        // 1. Search signal — 최우선 근거
        val searchEvidence = result.searchEvidence
        if (searchEvidence != null && !searchEvidence.isEmpty) {
            val signals = searchEvidence.signalSummaries
            if (signals.isNotEmpty()) {
                // 가장 의미 있는 signal description 1개
                reasons.add("\uD83D\uDD0D ${signals.first().signalDescription}")
            }

            // 발신처 특정 (entity) — 2번째 근거
            if (reasons.size < 2) {
                val entity = extractIdentifiedEntity(searchEvidence)
                if (entity != null) {
                    reasons.add("\uD83C\uDFE2 $entity")
                } else if (signals.size > 1) {
                    reasons.add("\uD83D\uDD0D ${signals[1].signalDescription}")
                }
            }
        }

        // 2. Device evidence — search 근거가 부족하면 보충
        if (reasons.size < 2) {
            val deviceEvidence = result.deviceEvidence
            if (deviceEvidence != null && deviceEvidence.hasAnyHistory) {
                val deviceSummary = buildDeviceOneLiner(deviceEvidence, uiText)
                if (deviceSummary.isNotEmpty()) {
                    reasons.add("\uD83D\uDCF1 $deviceSummary")
                }
            }
        }

        // 3. Cluster fallback — 아무 근거도 없으면
        if (reasons.isEmpty() && searchEvidence != null) {
            val clusters = searchEvidence.keywordClusters
                .take(2)
                .map { mapClusterToLocalized(it, language) }
                .distinct()
            if (clusters.isNotEmpty()) {
                reasons.add("\uD83D\uDCCA ${clusters.joinToString(", ")}")
            }
        }

        return reasons.take(2) // 절대 2줄 초과 금지
    }

    /**
     * 기기 기록 한 줄 요약.
     */
    private fun buildDeviceOneLiner(evidence: DeviceEvidence, uiText: OverlayUiText): String {
        val parts = mutableListOf<String>()
        val totalIn = evidence.incomingCount + evidence.missedCount
        if (totalIn > 0) parts.add(uiText.formatIncoming(totalIn))
        if (evidence.outgoingCount > 0) parts.add(uiText.formatOutgoing(evidence.outgoingCount))
        if (evidence.rejectedCount >= 2) parts.add(uiText.formatRejected(evidence.rejectedCount))
        val days = evidence.recentDaysContact
        if (days != null) parts.add(uiText.formatDaysAgo(days))
        return parts.joinToString(" \u00B7 ")
    }

    // buildDeviceDetail, buildSignalLines — 제거됨.
    // 1초 인지 설계에서 buildTopReasons()로 통합.

    private fun extractIdentifiedEntity(evidence: SearchEvidence): String? {
        val signals = evidence.signalSummaries
        for (signal in signals) {
            val snippet = signal.topSnippet ?: continue
            val entity = extractEntityFromSnippet(snippet)
            if (entity != null) return entity
        }

        for (snippet in evidence.topSnippets.take(3)) {
            val entity = extractEntityFromSnippet(snippet)
            if (entity != null) return entity
        }

        val repeated = evidence.repeatedEntities.firstOrNull()
        if (repeated != null && repeated.length >= 2 && !repeated.matches(Regex("\\d+"))) {
            return repeated
        }

        return null
    }

    private fun extractEntityFromSnippet(snippet: String): String? {
        if (snippet.isBlank()) return null

        val parts = snippet.split(":", limit = 2)
        val titlePart = parts[0].trim()
        val descPart = if (parts.size > 1) parts[1].trim() else ""

        val cleanTitle = titlePart
            .replace(Regex("[0-9\\-+()\\s]{5,}"), " ")
            .replace(Regex("\\s*[-/|·]\\s*"), " ")
            .trim()

        val excludePatterns = listOf("더콜", "whoscall", "truecaller", "스팸 전화번호부", "전화번호부")
        val filteredTitle = excludePatterns.fold(cleanTitle) { acc, pattern ->
            acc.replace(pattern, "", ignoreCase = true).trim()
        }

        val meaningfulDesc = descPart
            .replace(Regex("[0-9\\-+()]{5,}"), "")
            .replace(Regex("더콜에서.*조회된.*"), "")
            .replace(Regex("에 대한 자세한.*"), "")
            .trim()

        return when {
            meaningfulDesc.length >= 4 -> meaningfulDesc.take(60)
            filteredTitle.length >= 2 -> filteredTitle.take(60)
            else -> null
        }
    }

    // ══════════════════════════════════════════════
    // Action Buttons
    // ══════════════════════════════════════════════

    private fun buildActionButtons(
        context: Context,
        phoneNumber: String,
        uiText: OverlayUiText,
    ): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_HORIZONTAL

            val buttonSpecs = listOf(
                Triple("\u2714 ${uiText.actionAnswer}", "action_overlay_accept", Color.parseColor("#2E7D32")),
                Triple("\u2716 ${uiText.actionReject}", "action_reject", Color.parseColor("#E65100")),
                Triple("\u26D4 ${uiText.actionBlock}", "action_block", Color.parseColor("#B71C1C")),
            )

            for ((label, action, color) in buttonSpecs) {
                addView(TextView(context).apply {
                    text = label
                    setTextColor(Color.WHITE)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                    setTypeface(null, Typeface.BOLD)
                    gravity = Gravity.CENTER
                    background = GradientDrawable().apply {
                        setColor(color)
                        cornerRadius = dpToPx(context, 8).toFloat()
                    }
                    val btnPadH = dpToPx(context, 14)
                    val btnPadV = dpToPx(context, 8)
                    setPadding(btnPadH, btnPadV, btnPadH, btnPadV)

                    val lp = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f,
                    )
                    lp.marginStart = dpToPx(context, 4)
                    lp.marginEnd = dpToPx(context, 4)
                    layoutParams = lp

                    setOnClickListener {
                        handleOverlayAction(context, action, phoneNumber)
                    }
                })
            }
        }
    }

    @Suppress("DEPRECATION", "MissingPermission")
    private fun handleOverlayAction(context: Context, action: String, phoneNumber: String) {
        Log.i(TAG, "Overlay action: $action for $phoneNumber")

        when (action) {
            "action_overlay_accept" -> {
                try {
                    val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        telecomManager.acceptRingingCall()
                    } else {
                        val answerIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
                        context.sendBroadcast(answerIntent)
                    }
                    Log.i(TAG, "Call accepted via TelecomManager")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to accept call", e)
                }
                dismissOverlay(context)
            }
            "action_reject" -> {
                try {
                    val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        telecomManager.endCall()
                    }
                    Log.i(TAG, "Call rejected via TelecomManager")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to reject call", e)
                }
                dismissOverlay(context)
            }
            "action_block" -> {
                try {
                    val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        telecomManager.endCall()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to end call for block", e)
                }
                val intent = Intent("app.callcheck.mobile.ACTION_CALL").apply {
                    setPackage(context.packageName)
                    putExtra("action_type", "action_block")
                    putExtra("phone_number", phoneNumber)
                }
                context.sendBroadcast(intent)
                dismissOverlay(context)
            }
        }
    }

    // ══════════════════════════════════════════════
    // Localization Helpers
    // ══════════════════════════════════════════════

    private fun localizeRiskLevel(riskLevel: RiskLevel, language: SupportedLanguage): String {
        return when (language) {
            SupportedLanguage.KO -> riskLevel.displayNameKo
            else -> riskLevel.displayNameEn
        }
    }

    private fun mapClusterToLocalized(cluster: String, language: SupportedLanguage): String {
        val lower = cluster.lowercase()
        val key = when {
            lower in setOf("delivery", "courier", "shipping", "logistics", "parcel", "package",
                "택배", "배송", "배달", "물류", "송장") -> "DELIVERY"
            lower in setOf("hospital", "clinic", "school", "university", "government", "office",
                "administration", "reservation", "병원", "학교", "학원", "기관", "관공서",
                "예약", "진료", "접수") -> "INSTITUTION"
            lower in setOf("company", "corporation", "representative", "branch", "customer service",
                "회사", "기업", "대표번호", "고객센터", "지점") -> "BUSINESS"
            lower in setOf("spam", "telemarketing", "advertisement", "ad", "sales",
                "광고", "영업", "텔레마케팅", "홍보") -> "SPAM"
            lower in setOf("scam", "phishing", "fraud", "loan", "investment",
                "사기", "보이스피싱", "피싱", "대출", "투자", "리딩방") -> "SCAM"
            else -> return cluster
        }
        return OverlayUiText.forLanguage(language).clusterLabel(key)
    }

    // ══════════════════════════════════════════════
    // View Helpers
    // ══════════════════════════════════════════════

    private fun LinearLayout.addDivider(context: Context, textColor: Int) {
        addView(View(context).apply {
            setBackgroundColor(adjustAlpha(textColor, 0.25f))
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(context, 1),
            )
            lp.topMargin = dpToPx(context, 8)
            lp.bottomMargin = dpToPx(context, 6)
            layoutParams = lp
        })
    }

    private fun marginTop(context: Context, dp: Int): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        ).apply {
            topMargin = dpToPx(context, dp)
        }
    }

    private fun marginStart(context: Context, startDp: Int, topDp: Int): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        ).apply {
            marginStart = dpToPx(context, startDp)
            topMargin = dpToPx(context, topDp)
        }
    }

    // ══════════════════════════════════════════════
    // Style
    // ══════════════════════════════════════════════

    private fun backgroundColorForRisk(riskLevel: RiskLevel): Int {
        return when (riskLevel) {
            RiskLevel.HIGH -> Color.parseColor("#C62828")
            RiskLevel.MEDIUM -> Color.parseColor("#E65100")
            RiskLevel.LOW -> Color.parseColor("#2E7D32")
            RiskLevel.UNKNOWN -> Color.parseColor("#424242")
        }
    }

    private fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).toInt()
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics,
        ).toInt()
    }
}

// ══════════════════════════════════════════════
// Overlay UI 텍스트 — 언어별 템플릿
// ══════════════════════════════════════════════

/**
 * 오버레이 UI 텍스트 번들.
 * 각 언어별 정적 인스턴스를 제공한다.
 */
internal data class OverlayUiText(
    val deviceHistory: String,
    val webScanResult: String,
    val noHistory: String,
    val noSearchResult: String,
    val actionAnswer: String,
    val actionReject: String,
    val actionBlock: String,
    val smsExists: String,
    private val incomingFmt: String,
    private val outgoingFmt: String,
    private val longCallFmt: String,
    private val shortCallFmt: String,
    private val rejectedFmt: String,
    private val todayStr: String,
    private val yesterdayStr: String,
    private val daysAgoFmt: String,
    private val weeksAgoFmt: String,
    private val monthsAgoFmt: String,
    private val clusters: Map<String, String>,
    /** 1초 인지: RiskLevel → 한 단어 판정 */
    private val verdicts: Map<RiskLevel, String>,
) {
    /** 한 단어 판정 반환. 0.3초 인지 핵심. */
    fun oneWordVerdict(riskLevel: RiskLevel): String = verdicts[riskLevel] ?: riskLevel.displayNameEn
    fun formatIncoming(count: Int) = incomingFmt.replace("{n}", count.toString())
    fun formatOutgoing(count: Int) = outgoingFmt.replace("{n}", count.toString())
    fun formatLongCall(count: Int) = longCallFmt.replace("{n}", count.toString())
    fun formatShortCall(count: Int) = shortCallFmt.replace("{n}", count.toString())
    fun formatRejected(count: Int) = rejectedFmt.replace("{n}", count.toString())
    fun formatDaysAgo(days: Int): String = when {
        days == 0 -> todayStr
        days == 1 -> yesterdayStr
        days <= 7 -> daysAgoFmt.replace("{n}", days.toString())
        days <= 30 -> weeksAgoFmt.replace("{n}", (days / 7).toString())
        else -> monthsAgoFmt.replace("{n}", (days / 30).toString())
    }
    fun clusterLabel(key: String): String = clusters[key] ?: key

    companion object {
        fun forLanguage(language: SupportedLanguage): OverlayUiText {
            return when (language) {
                SupportedLanguage.KO -> KO
                SupportedLanguage.JA -> JA
                SupportedLanguage.ZH -> ZH
                SupportedLanguage.RU -> RU
                SupportedLanguage.ES -> ES
                SupportedLanguage.AR -> AR
                else -> EN
            }
        }

        private val KO = OverlayUiText(
            deviceHistory = "기기 기록",
            webScanResult = "웹 스캔 결과",
            noHistory = "기록 없음 (첫 수신)",
            noSearchResult = "검색 결과 없음",
            actionAnswer = "수신",
            actionReject = "거절",
            actionBlock = "차단",
            smsExists = "SMS 있음",
            incomingFmt = "수신 {n}회",
            outgoingFmt = "발신 {n}회",
            longCallFmt = "장기통화 {n}회",
            shortCallFmt = "짧은통화 {n}회",
            rejectedFmt = "거절 {n}회",
            todayStr = "오늘",
            yesterdayStr = "어제",
            daysAgoFmt = "{n}일 전",
            weeksAgoFmt = "{n}주 전",
            monthsAgoFmt = "{n}개월 전",
            clusters = mapOf(
                "DELIVERY" to "택배/배송",
                "INSTITUTION" to "기관/공공",
                "BUSINESS" to "기업/고객센터",
                "SPAM" to "광고/영업",
                "SCAM" to "사기/피싱",
            ),
            verdicts = mapOf(
                RiskLevel.HIGH to "위험",
                RiskLevel.MEDIUM to "주의",
                RiskLevel.LOW to "안전",
                RiskLevel.UNKNOWN to "확인중",
            ),
        )

        private val EN = OverlayUiText(
            deviceHistory = "Device History",
            webScanResult = "Web Scan Results",
            noHistory = "No history (first call)",
            noSearchResult = "No search results",
            actionAnswer = "Answer",
            actionReject = "Reject",
            actionBlock = "Block",
            smsExists = "SMS exists",
            incomingFmt = "{n} incoming",
            outgoingFmt = "{n} outgoing",
            longCallFmt = "{n} long calls",
            shortCallFmt = "{n} short calls",
            rejectedFmt = "{n} rejected",
            todayStr = "Today",
            yesterdayStr = "Yesterday",
            daysAgoFmt = "{n} days ago",
            weeksAgoFmt = "{n} weeks ago",
            monthsAgoFmt = "{n} months ago",
            clusters = mapOf(
                "DELIVERY" to "Delivery",
                "INSTITUTION" to "Institution",
                "BUSINESS" to "Business",
                "SPAM" to "Spam/Sales",
                "SCAM" to "Scam/Phishing",
            ),
            verdicts = mapOf(
                RiskLevel.HIGH to "Danger",
                RiskLevel.MEDIUM to "Caution",
                RiskLevel.LOW to "Safe",
                RiskLevel.UNKNOWN to "Checking",
            ),
        )

        private val JA = OverlayUiText(
            deviceHistory = "端末履歴",
            webScanResult = "Web検索結果",
            noHistory = "履歴なし（初着信）",
            noSearchResult = "検索結果なし",
            actionAnswer = "応答",
            actionReject = "拒否",
            actionBlock = "ブロック",
            smsExists = "SMSあり",
            incomingFmt = "着信{n}回",
            outgoingFmt = "発信{n}回",
            longCallFmt = "長時間通話{n}回",
            shortCallFmt = "短時間通話{n}回",
            rejectedFmt = "拒否{n}回",
            todayStr = "今日",
            yesterdayStr = "昨日",
            daysAgoFmt = "{n}日前",
            weeksAgoFmt = "{n}週間前",
            monthsAgoFmt = "{n}ヶ月前",
            clusters = mapOf(
                "DELIVERY" to "配送",
                "INSTITUTION" to "公共機関",
                "BUSINESS" to "企業",
                "SPAM" to "広告/営業",
                "SCAM" to "詐欺",
            ),
            verdicts = mapOf(
                RiskLevel.HIGH to "危険",
                RiskLevel.MEDIUM to "注意",
                RiskLevel.LOW to "安全",
                RiskLevel.UNKNOWN to "確認中",
            ),
        )

        private val ZH = OverlayUiText(
            deviceHistory = "设备记录",
            webScanResult = "网络搜索结果",
            noHistory = "无记录（首次来电）",
            noSearchResult = "无搜索结果",
            actionAnswer = "接听",
            actionReject = "拒接",
            actionBlock = "拉黑",
            smsExists = "有短信",
            incomingFmt = "来电{n}次",
            outgoingFmt = "去电{n}次",
            longCallFmt = "长通话{n}次",
            shortCallFmt = "短通话{n}次",
            rejectedFmt = "拒接{n}次",
            todayStr = "今天",
            yesterdayStr = "昨天",
            daysAgoFmt = "{n}天前",
            weeksAgoFmt = "{n}周前",
            monthsAgoFmt = "{n}个月前",
            clusters = mapOf(
                "DELIVERY" to "快递",
                "INSTITUTION" to "公共机构",
                "BUSINESS" to "企业",
                "SPAM" to "广告/推销",
                "SCAM" to "诈骗",
            ),
            verdicts = mapOf(
                RiskLevel.HIGH to "危险",
                RiskLevel.MEDIUM to "注意",
                RiskLevel.LOW to "安全",
                RiskLevel.UNKNOWN to "检查中",
            ),
        )

        private val RU = OverlayUiText(
            deviceHistory = "История устройства",
            webScanResult = "Результаты поиска",
            noHistory = "Нет записей (первый звонок)",
            noSearchResult = "Нет результатов",
            actionAnswer = "Ответ",
            actionReject = "Откл.",
            actionBlock = "Блок",
            smsExists = "Есть SMS",
            incomingFmt = "Входящих: {n}",
            outgoingFmt = "Исходящих: {n}",
            longCallFmt = "Долгих: {n}",
            shortCallFmt = "Коротких: {n}",
            rejectedFmt = "Отклонено: {n}",
            todayStr = "Сегодня",
            yesterdayStr = "Вчера",
            daysAgoFmt = "{n} дн. назад",
            weeksAgoFmt = "{n} нед. назад",
            monthsAgoFmt = "{n} мес. назад",
            clusters = mapOf(
                "DELIVERY" to "Доставка",
                "INSTITUTION" to "Учреждение",
                "BUSINESS" to "Бизнес",
                "SPAM" to "Спам",
                "SCAM" to "Мошенничество",
            ),
            verdicts = mapOf(
                RiskLevel.HIGH to "Опасно",
                RiskLevel.MEDIUM to "Внимание",
                RiskLevel.LOW to "Безопасно",
                RiskLevel.UNKNOWN to "Проверка",
            ),
        )

        private val ES = OverlayUiText(
            deviceHistory = "Historial del dispositivo",
            webScanResult = "Resultados de búsqueda",
            noHistory = "Sin historial (primera llamada)",
            noSearchResult = "Sin resultados",
            actionAnswer = "Contestar",
            actionReject = "Rechazar",
            actionBlock = "Bloquear",
            smsExists = "SMS existente",
            incomingFmt = "{n} entrantes",
            outgoingFmt = "{n} salientes",
            longCallFmt = "{n} llamadas largas",
            shortCallFmt = "{n} llamadas cortas",
            rejectedFmt = "{n} rechazadas",
            todayStr = "Hoy",
            yesterdayStr = "Ayer",
            daysAgoFmt = "Hace {n} días",
            weeksAgoFmt = "Hace {n} semanas",
            monthsAgoFmt = "Hace {n} meses",
            clusters = mapOf(
                "DELIVERY" to "Entrega",
                "INSTITUTION" to "Institución",
                "BUSINESS" to "Empresa",
                "SPAM" to "Spam",
                "SCAM" to "Fraude",
            ),
            verdicts = mapOf(
                RiskLevel.HIGH to "Peligro",
                RiskLevel.MEDIUM to "Precaución",
                RiskLevel.LOW to "Seguro",
                RiskLevel.UNKNOWN to "Verificando",
            ),
        )

        private val AR = OverlayUiText(
            deviceHistory = "سجل الجهاز",
            webScanResult = "نتائج البحث",
            noHistory = "لا سجل (أول مكالمة)",
            noSearchResult = "لا نتائج",
            actionAnswer = "رد",
            actionReject = "رفض",
            actionBlock = "حظر",
            smsExists = "رسالة موجودة",
            incomingFmt = "واردة {n}",
            outgoingFmt = "صادرة {n}",
            longCallFmt = "مكالمات طويلة {n}",
            shortCallFmt = "مكالمات قصيرة {n}",
            rejectedFmt = "مرفوضة {n}",
            todayStr = "اليوم",
            yesterdayStr = "أمس",
            daysAgoFmt = "منذ {n} أيام",
            weeksAgoFmt = "منذ {n} أسابيع",
            monthsAgoFmt = "منذ {n} أشهر",
            clusters = mapOf(
                "DELIVERY" to "توصيل",
                "INSTITUTION" to "مؤسسة",
                "BUSINESS" to "شركة",
                "SPAM" to "إعلانات",
                "SCAM" to "احتيال",
            ),
            verdicts = mapOf(
                RiskLevel.HIGH to "خطر",
                RiskLevel.MEDIUM to "تنبيه",
                RiskLevel.LOW to "آمن",
                RiskLevel.UNKNOWN to "جاري التحقق",
            ),
        )
    }
}
