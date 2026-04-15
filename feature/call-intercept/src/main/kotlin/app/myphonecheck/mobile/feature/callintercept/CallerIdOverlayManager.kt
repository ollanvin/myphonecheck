package app.myphonecheck.mobile.feature.callintercept

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.telecom.TelecomManager
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import app.myphonecheck.mobile.core.model.ActionRecommendation
import app.myphonecheck.mobile.core.model.ActionState
import app.myphonecheck.mobile.core.model.DecisionResult
import app.myphonecheck.mobile.core.model.ImportanceLevel
import app.myphonecheck.mobile.core.model.ProductAccessTier
import app.myphonecheck.mobile.core.model.ProductStageFlags
import app.myphonecheck.mobile.core.model.RiskLevel
import app.myphonecheck.mobile.core.model.SearchStatus
import app.myphonecheck.mobile.core.model.TwoPhaseDecision
import app.myphonecheck.mobile.core.model.displayLabelKo
import app.myphonecheck.mobile.data.localcache.repository.NumberProfileSnapshot
import app.myphonecheck.mobile.feature.callintercept.R
import app.myphonecheck.mobile.feature.countryconfig.SignalSummaryLocalizer
import app.myphonecheck.mobile.feature.countryconfig.SupportedLanguage
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "CallerIdOverlay"

/**
 * 전화 수신 시 2초 결정형 오버레이를 표시한다.
 *
 * 설계 원칙 (v2 — 2-Second Decision):
 * - 번호 + 행동 문장 1줄 + 버튼 2단 구조
 * - 스크롤 없음. 정보 나열 금지.
 * - DO_NOT_MISS 강조 + PRO 유도 최소 게이팅
 * - ActionRecommendation 기반 Primary 버튼 동적 강조
 */
@Singleton
class CallerIdOverlayManager @Inject constructor() {

    private var overlayView: View? = null
    private var isAttached: Boolean = false
    private var pendingPromptNumber: String? = null
    private var pendingPromptContext: PendingPromptContext? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private val renderStartMsByNumber = ConcurrentHashMap<String, Long>()

    data class PendingPromptContext(
        val phoneNumber: String,
        val summary: String,
        val searchStatus: String,
    )

    /**
     * 오버레이를 표시한다.
     *
     * @param context Android Context
     * @param result 판정 결과
     * @param phoneNumber raw 번호
     * @param language 현재 기기 언어
     * @param localizer SignalSummary 로컬라이저
     * @param twoPhaseDecision 2-Phase 메타 (사용하지 않으나 호환성 유지)
     * @param userBlockCount 사용자가 해당 번호를 차단한 이력 횟수
     * @param numberProfileSnapshot 번호 프로필 스냅샷
     */
    fun showOverlay(
        context: Context,
        result: DecisionResult,
        phoneNumber: String,
        language: SupportedLanguage = SupportedLanguage.EN,
        localizer: SignalSummaryLocalizer = SignalSummaryLocalizer(),
        twoPhaseDecision: TwoPhaseDecision? = null,
        userBlockCount: Int = 0,
        numberProfileSnapshot: NumberProfileSnapshot? = null,
    ): Boolean {
        if (!canDrawOverlays(context)) {
            Log.w(TAG, "SYSTEM_ALERT_WINDOW not granted, cannot show overlay")
            return false
        }

        mainHandler.post {
            try {
                dismissOverlayInternal(context)

                val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                pendingPromptNumber = phoneNumber
                pendingPromptContext = PendingPromptContext(
                    phoneNumber = phoneNumber,
                    summary = result.summary,
                    searchStatus = searchStatusLabel(result),
                )
                renderStartMsByNumber[phoneNumber] = System.currentTimeMillis()

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

                val view = buildOverlayView(
                    context = context,
                    result = result,
                    phoneNumber = phoneNumber,
                    numberProfileSnapshot = numberProfileSnapshot,
                )
                overlayView = view
                wm.addView(view, params)
                isAttached = true
                Log.i(
                    TAG,
                    "overlay_rendered number=$phoneNumber risk=${result.riskLevel} " +
                        "action=${result.action} importance=${result.importanceLevel}",
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to show overlay", e)
                isAttached = false
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
        val view = overlayView ?: return
        if (!isAttached) {
            overlayView = null
            return
        }
        try {
            // Defense: check view is still attached to a window
            if (view.parent != null) {
                val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                wm.removeView(view)
            }
            Log.i(TAG, "Overlay dismissed")
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "Overlay dismiss: view not attached (already removed)", e)
        } catch (e: Exception) {
            Log.w(TAG, "Overlay dismiss error", e)
        } finally {
            overlayView = null
            isAttached = false
            pendingPromptNumber?.let { renderStartMsByNumber.remove(it) }
        }
    }

    fun isOverlayShowing(): Boolean = overlayView != null && isAttached

    fun rememberPostCallNumber(phoneNumber: String) {
        pendingPromptNumber = phoneNumber
        pendingPromptContext = PendingPromptContext(
            phoneNumber = phoneNumber,
            summary = "",
            searchStatus = "",
        )
    }

    fun consumePendingPromptNumber(): String? {
        val number = pendingPromptNumber
        pendingPromptNumber = null
        return number
    }

    fun consumePendingPromptContext(): PendingPromptContext? {
        val ctx = pendingPromptContext
        pendingPromptContext = null
        pendingPromptNumber = null
        return ctx
    }

    private fun canDrawOverlays(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    // ═══════════════════════════════════════════════════════
    // View Builder — 2-Second Decision UI
    // ═══════════════════════════════════════════════════════

    /**
     * 2초 결정형 오버레이 뷰를 구성한다.
     *
     * 레이아웃 순서:
     * 1. 전화번호 (18sp Bold)
     * 2. 행동 문장 1줄 (ActionRecommendation displayNameKo, 16sp Bold, 색상)
     * 3. 보조 정보 최대 1줄 (SearchStatus / ActionState / Labels)
     * 4. DO_NOT_MISS 강조 배지 (조건부)
     * 5. PRO 유도 문구 (조건부 — 게이팅)
     * 6. 면책 1줄 (9sp)
     * 7. 구분선
     * 8. 2단 액션 버튼
     */
    private fun buildOverlayView(
        context: Context,
        result: DecisionResult,
        phoneNumber: String,
        numberProfileSnapshot: NumberProfileSnapshot? = null,
    ): View {
        val bgColor = backgroundColorForRisk(result.riskLevel)
        val textColor = Color.WHITE
        val subtleColor = adjustAlpha(textColor, 0.75f)
        val actionState = numberProfileSnapshot?.actionState ?: ActionState.NONE
        val isDoNotMiss = result.importanceLevel == ImportanceLevel.DO_NOT_MISS
                || actionState == ActionState.DO_NOT_BLOCK

        val content = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL

            // ── 1. Phone number ──
            addView(TextView(context).apply {
                text = phoneNumber
                setTextColor(textColor)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = marginTop(context, 0)
            })

            // ── 2. Action sentence (1 line) ──
            val actionColor = actionHighlightColor(result.action)
            addView(TextView(context).apply {
                text = result.action.displayNameKo
                setTextColor(actionColor)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = marginTop(context, 4)
            })

            // ── 3. Secondary info (max 1 line) ──
            val secondaryParts = mutableListOf<String>()
            secondaryParts.add(result.summary)
            val statusLabel = searchStatusLabel(result)
            if (statusLabel.isNotBlank()) secondaryParts.add(statusLabel)
            actionState.displayLabelKo()?.let { secondaryParts.add(it) }
            val labelText = numberProfileSnapshot?.quickLabels
                ?.joinToString(" / ") { it.displayName }
                ?.takeIf { it.isNotBlank() }
            labelText?.let { secondaryParts.add(it) }

            addView(TextView(context).apply {
                text = secondaryParts.joinToString(" · ")
                setTextColor(subtleColor)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                gravity = Gravity.CENTER_HORIZONTAL
                maxLines = 2
                layoutParams = marginTop(context, 4)
            })

            // ── 4. DO_NOT_MISS badge (conditional) ──
            if (isDoNotMiss) {
                addView(TextView(context).apply {
                    text = context.getString(R.string.overlay_do_not_miss_badge)
                    setTextColor(Color.parseColor("#FFD54F"))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                    setTypeface(null, Typeface.BOLD)
                    gravity = Gravity.CENTER_HORIZONTAL
                    layoutParams = marginTop(context, 6)
                })
            }

            // ── 5. PRO upsell (conditional — gating) ──
            if (ProductStageFlags.DO_NOT_MISS_BEHAVIOR == ProductAccessTier.PREMIUM && !isDoNotMiss) {
                addView(TextView(context).apply {
                    text = context.getString(R.string.overlay_do_not_miss_pro_hint)
                    setTextColor(adjustAlpha(Color.parseColor("#FFD54F"), 0.70f))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                    gravity = Gravity.CENTER_HORIZONTAL
                    layoutParams = marginTop(context, 4)
                })
            }

            // ── 6. Disclaimer (1 line, 9sp) ──
            addView(TextView(context).apply {
                text = context.getString(R.string.overlay_disclaimer_short)
                setTextColor(adjustAlpha(textColor, 0.50f))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 9f)
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = marginTop(context, 8)
            })

            // ── 7. Divider ──
            addDivider(context, textColor)

            // ── 8. 2-row action buttons ──
            addView(buildActionButtons(context, phoneNumber, result.action))
        }

        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                setColor(bgColor)
                cornerRadius = dpToPx(context, 16).toFloat()
            }
            val padH = dpToPx(context, 20)
            setPadding(padH, dpToPx(context, 16), padH, dpToPx(context, 12))
            elevation = dpToPx(context, 8).toFloat()

            addView(content, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ))
        }
    }

    // ═══════════════════════════════════════════════════════
    // Action Buttons — 2-Row Structure
    // ═══════════════════════════════════════════════════════

    /**
     * 2단 액션 버튼 구조.
     *
     * Row 1 (Primary):  받기 | 거절
     * Row 2 (Secondary): 차단 | 중요표시
     *
     * Primary 강조: ActionRecommendation에 따라 동적 변경.
     * - ANSWER / ANSWER_WITH_CAUTION → 받기 강조 (Green)
     * - REJECT / BLOCK_REVIEW → 거절 강조 (Red)
     * - HOLD → 둘 다 중립 (Gray)
     */
    private fun buildActionButtons(
        context: Context,
        phoneNumber: String,
        recommendation: ActionRecommendation,
    ): LinearLayout {
        val answerEmphasis = recommendation == ActionRecommendation.ANSWER
                || recommendation == ActionRecommendation.ANSWER_WITH_CAUTION
        val rejectEmphasis = recommendation == ActionRecommendation.REJECT
                || recommendation == ActionRecommendation.BLOCK_REVIEW

        // Row 1: Primary — Answer / Reject
        val row1 = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_HORIZONTAL

            addView(actionButton(
                context = context,
                label = "\u2714 ${context.getString(R.string.overlay_action_answer)}",
                color = if (answerEmphasis) Color.parseColor("#2E7D32") else Color.parseColor("#555555"),
                onClick = { handleOverlayAction(context, "action_overlay_accept", phoneNumber) },
            ))
            addView(actionButton(
                context = context,
                label = "\u2716 ${context.getString(R.string.overlay_action_reject)}",
                color = if (rejectEmphasis) Color.parseColor("#C62828") else Color.parseColor("#E65100"),
                onClick = { handleOverlayAction(context, "action_reject", phoneNumber) },
            ))
        }

        // Row 2: Secondary — Block / Important
        val row2 = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_HORIZONTAL
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
            lp.topMargin = dpToPx(context, 6)
            layoutParams = lp

            addView(actionButton(
                context = context,
                label = "\u26D4 ${context.getString(R.string.overlay_action_block)}",
                color = Color.parseColor("#B71C1C"),
                onClick = { handleOverlayAction(context, "action_block", phoneNumber) },
            ))
            addView(actionButton(
                context = context,
                label = "\u2B50 ${context.getString(R.string.overlay_action_important)}",
                color = Color.parseColor("#F57F17"),
                onClick = { handleOverlayAction(context, "action_mark_do_not_miss", phoneNumber) },
            ))
        }

        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            addView(row1)
            addView(row2)
        }
    }

    private fun actionButton(
        context: Context,
        label: String,
        color: Int,
        onClick: () -> Unit,
    ): TextView {
        return TextView(context).apply {
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

            val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            lp.marginStart = dpToPx(context, 4)
            lp.marginEnd = dpToPx(context, 4)
            layoutParams = lp

            setOnClickListener { onClick() }
        }
    }

    // ═══════════════════════════════════════════════════════
    // Overlay Action Handler
    // ═══════════════════════════════════════════════════════

    @Suppress("DEPRECATION", "MissingPermission")
    private fun handleOverlayAction(context: Context, action: String, phoneNumber: String) {
        val renderStartedAtMs = renderStartMsByNumber.remove(phoneNumber)
        val actionLatencyMs = renderStartedAtMs?.let { System.currentTimeMillis() - it }
        Log.i(TAG, "overlay_action action=$action number=$phoneNumber latencyMs=$actionLatencyMs")

        when (action) {
            "action_overlay_accept" -> {
                try {
                    val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        telecomManager.acceptRingingCall()
                    } else {
                        val answerIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
                        context.sendBroadcast(answerIntent)
                    }
                    Log.i(TAG, "Call accepted via TelecomManager")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to accept call", e)
                }
                broadcastUserAction(context, "action_accept", phoneNumber)
                dismissOverlay(context)
            }
            "action_reject" -> {
                try {
                    val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        telecomManager.endCall()
                    }
                    Log.i(TAG, "Call rejected via TelecomManager")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to reject call", e)
                }
                broadcastUserAction(context, "action_reject", phoneNumber)
                dismissOverlay(context)
            }
            "action_block" -> {
                try {
                    val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        telecomManager.endCall()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to end call for block", e)
                }
                broadcastUserAction(context, "action_block", phoneNumber)
                dismissOverlay(context)
            }
            "action_mark_do_not_miss" -> {
                broadcastUserAction(context, "action_mark_do_not_miss", phoneNumber)
                // DO_NOT_MISS: overlay stays open so user can still answer/reject
                Log.i(TAG, "DO_NOT_MISS toggled for $phoneNumber, overlay remains")
            }
        }
    }

    /**
     * 사용자 행동을 CallActionReceiver로 브로드캐스트.
     * CallActionReceiver가 UserCallRecordRepository + BlocklistRepository로 기록.
     */
    private fun broadcastUserAction(context: Context, actionType: String, phoneNumber: String) {
        try {
            val intent = Intent("app.myphonecheck.mobile.ACTION_CALL").apply {
                setPackage(context.packageName)
                putExtra("extra_phone_number", phoneNumber)
                putExtra("action_type", actionType)
            }
            context.sendBroadcast(intent)
            Log.i(TAG, "Broadcast sent: $actionType for $phoneNumber")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to broadcast user action", e)
        }
    }

    // ═══════════════════════════════════════════════════════
    // View Helpers
    // ═══════════════════════════════════════════════════════

    private fun LinearLayout.addDivider(context: Context, textColor: Int) {
        addView(View(context).apply {
            setBackgroundColor(adjustAlpha(textColor, 0.25f))
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(context, 1),
            )
            lp.topMargin = dpToPx(context, 6)
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

    // ═══════════════════════════════════════════════════════
    // Style
    // ═══════════════════════════════════════════════════════

    private fun backgroundColorForRisk(riskLevel: RiskLevel): Int {
        return when (riskLevel) {
            RiskLevel.HIGH -> Color.parseColor("#C62828")
            RiskLevel.MEDIUM -> Color.parseColor("#E65100")
            RiskLevel.LOW -> Color.parseColor("#2E7D32")
            RiskLevel.UNKNOWN -> Color.parseColor("#424242")
        }
    }

    /** Primary 행동 버튼/문구 색상 — risk 축과 독립적으로 행동 추천을 표현 */
    private fun actionHighlightColor(action: ActionRecommendation): Int {
        return when (action) {
            ActionRecommendation.ANSWER -> Color.parseColor("#A5D6A7")          // Light green
            ActionRecommendation.ANSWER_WITH_CAUTION -> Color.parseColor("#FFE082") // Amber
            ActionRecommendation.REJECT -> Color.parseColor("#EF9A9A")          // Light red
            ActionRecommendation.BLOCK_REVIEW -> Color.parseColor("#EF5350")    // Red
            ActionRecommendation.HOLD -> Color.parseColor("#BDBDBD")            // Gray
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

    private fun searchStatusLabel(result: DecisionResult): String {
        return SearchStatus.fromDecisionResult(result).labelKo
    }
}

// ═══════════════════════════════════════════════════════
// Overlay UI Text — Android String Resources
// ═══════════════════════════════════════════════════════

/**
 * 오버레이 UI 텍스트 헬퍼.
 * Android string resources를 통해 로컬라이즈된 텍스트를 제공한다.
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

    fun trustGradeLabel(score: Int): String = when {
        score <= 30 -> context.getString(R.string.overlay_trust_danger)
        score <= 60 -> context.getString(R.string.overlay_trust_caution)
        else -> context.getString(R.string.overlay_trust_low_risk)
    }
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 