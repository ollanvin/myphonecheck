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
import app.callcheck.mobile.R
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
        val uiText = OverlayUiText(context)

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
            val reasons = buildTopReasons(result, language, localizer, uiText, context)
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
        context: Context,
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
                .map { mapClusterToLocalized(it, context) }
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

    private fun mapClusterToLocalized(cluster: String, context: Context): String {
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
        return OverlayUiText(context).clusterLabel(key)
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
// Overlay UI 텍스트 — Android String Resources
// ══════════════════════════════════════════════

/**
 * 오버레이 UI 텍스트 헬퍼.
 * Android string resources를 통해 로컬라이즈된 텍스트를 제공한다.
 * Context를 받아 locale을 자동으로 처리한다.
 */
internal class OverlayUiText(private val context: Context) {
    val deviceHistory: String get() = context.getString(R.string.overlay_device_history)
    val webScanResult: String get() = context.getString(R.string.overlay_web_scan_result)
    val noHistory: String get() = context.getString(R.string.overlay_no_history)
    val noSearchResult: String get() = context.getString(R.string.overlay_no_search_result)
    val actionAnswer: String get() = context.getString(R.string.overlay_action_answer)
    val actionReject: String get() = context.getString(R.string.overlay_action_reject)
    val actionBlock: String get() = context.getString(R.string.overlay_action_block)
    val smsExists: String get() = context.getString(R.string.overlay_sms_exists)

    fun formatIncoming(count: Int) = context.getString(R.string.overlay_incoming_fmt, count)
    fun formatOutgoing(count: Int) = context.getString(R.string.overlay_outgoing_fmt, count)
    fun formatLongCall(count: Int) = context.getString(R.string.overlay_long_call_fmt, count)
    fun formatShortCall(count: Int) = context.getString(R.string.overlay_short_call_fmt, count)
    fun formatRejected(count: Int) = context.getString(R.string.overlay_rejected_fmt, count)

    fun formatDaysAgo(days: Int): String = when {
        days == 0 -> context.getString(R.string.overlay_today)
        days == 1 -> context.getString(R.string.overlay_yesterday)
        days <= 7 -> context.getString(R.string.overlay_days_ago_fmt, days)
        days <= 30 -> context.getString(R.string.overlay_weeks_ago_fmt, days / 7)
        else -> context.getString(R.string.overlay_months_ago_fmt, days / 30)
    }

    fun oneWordVerdict(riskLevel: RiskLevel): String = when (riskLevel) {
        RiskLevel.HIGH -> context.getString(R.string.overlay_verdict_high)
        RiskLevel.MEDIUM -> context.getString(R.string.overlay_verdict_medium)
        RiskLevel.LOW -> context.getString(R.string.overlay_verdict_low)
        RiskLevel.UNKNOWN -> context.getString(R.string.overlay_verdict_unknown)
    }

    fun clusterLabel(key: String): String = when (key) {
        "DELIVERY" -> context.getString(R.string.overlay_cluster_delivery)
        "INSTITUTION" -> context.getString(R.string.overlay_cluster_institution)
        "BUSINESS" -> context.getString(R.string.overlay_cluster_business)
        "SPAM" -> context.getString(R.string.overlay_cluster_spam)
        "SCAM" -> context.getString(R.string.overlay_cluster_scam)
        else -> key
    }
}
