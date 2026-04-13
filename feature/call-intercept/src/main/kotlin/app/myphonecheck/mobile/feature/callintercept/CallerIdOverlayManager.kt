package app.myphonecheck.mobile.feature.callintercept

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
import android.widget.ScrollView
import android.widget.TextView
import app.myphonecheck.mobile.feature.callintercept.R
import app.myphonecheck.mobile.core.model.DecisionResult
import app.myphonecheck.mobile.core.model.RiskLevel
import app.myphonecheck.mobile.core.model.TwoPhaseDecision
import app.myphonecheck.mobile.core.util.DecisionReasoningFormatter
import app.myphonecheck.mobile.core.util.DecisionReasoningFormatter.Lang
import app.myphonecheck.mobile.feature.countryconfig.SignalSummaryLocalizer
import app.myphonecheck.mobile.feature.countryconfig.SupportedLanguage
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
     * @param twoPhaseDecision 2-Phase 메타(즉시/확정). null이면 Phase UI 생략.
     */
    fun showOverlay(
        context: Context,
        result: DecisionResult,
        phoneNumber: String,
        language: SupportedLanguage = SupportedLanguage.EN,
        localizer: SignalSummaryLocalizer = SignalSummaryLocalizer(),
        twoPhaseDecision: TwoPhaseDecision? = null,
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

                overlayView = buildOverlayView(context, result, phoneNumber, language, localizer, twoPhaseDecision)
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
     * 전화 판단 오버레이 — 엔진 출력 전부 노출(요약 숨김 없음).
     * 스크롤 + 하단 고정 액션 버튼.
     */
    private fun buildOverlayView(
        context: Context,
        result: DecisionResult,
        phoneNumber: String,
        language: SupportedLanguage,
        localizer: SignalSummaryLocalizer,
        twoPhaseDecision: TwoPhaseDecision? = null,
    ): View {
        val bgColor = backgroundColorForRisk(result.riskLevel)
        val textColor = Color.WHITE
        val subtleColor = adjustAlpha(textColor, 0.80f)
        val uiText = OverlayUiText(context)
        val lang = if (language == SupportedLanguage.KO) Lang.KO else Lang.EN
        val categoryText = localizer.localizeCategory(result.category.name, context)
        val confidencePercent = DecisionReasoningFormatter.confidencePercent(result.confidence)
        val riskTri = DecisionReasoningFormatter.riskTriLabel(result.riskLevel, lang)

        val scrollContent = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL

            val verdict = uiText.oneWordVerdict(result.riskLevel)
            addView(TextView(context).apply {
                text = verdict
                setTextColor(textColor)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
            })

            addView(TextView(context).apply {
                text = context.getString(R.string.overlay_risk_confidence_line, riskTri, confidencePercent)
                setTextColor(textColor)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = marginTop(context, 4)
            })

            addView(TextView(context).apply {
                text = "$categoryText  \u00B7  $phoneNumber"
                setTextColor(subtleColor)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = marginTop(context, 2)
            })

            twoPhaseDecision?.let { tp ->
                addDivider(context, textColor)
                for (line in buildPhaseDescriptionLines(context, tp, lang)) {
                    addView(TextView(context).apply {
                        text = line
                        setTextColor(subtleColor)
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                        layoutParams = marginTop(context, 2)
                    })
                }
                tp.takeIf { it.hasPhaseConflict() }?.let {
                    addView(TextView(context).apply {
                        text = context.getString(R.string.overlay_phase_conflict_note)
                        setTextColor(Color.WHITE)
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                        setTypeface(null, Typeface.BOLD)
                        layoutParams = marginTop(context, 4)
                    })
                }
            }

            addDivider(context, textColor)

            val titleIds = listOf(
                R.string.overlay_section_report,
                R.string.overlay_section_pattern,
                R.string.overlay_section_behavior,
                R.string.overlay_section_search,
            )
            val bodies = DecisionReasoningFormatter.sectionBodiesInOrder(result, lang)
            for (i in titleIds.indices) {
                addView(TextView(context).apply {
                    text = context.getString(titleIds[i])
                    setTextColor(textColor)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                    setTypeface(null, Typeface.BOLD)
                    layoutParams = marginTop(context, 6)
                })
                addView(TextView(context).apply {
                    text = bodies[i]
                    setTextColor(subtleColor)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                    layoutParams = marginTop(context, 1)
                })
            }

            addView(TextView(context).apply {
                text = context.getString(R.string.overlay_section_judgment_basis)
                setTextColor(textColor)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                setTypeface(null, Typeface.BOLD)
                layoutParams = marginTop(context, 10)
            })
            addView(TextView(context).apply {
                text = DecisionReasoningFormatter.judgmentBasisMultiline(result, lang)
                setTextColor(subtleColor)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                layoutParams = marginTop(context, 2)
            })

            if (DecisionReasoningFormatter.useGlobalDataBanner(result)) {
                addView(TextView(context).apply {
                    text = context.getString(R.string.overlay_global_data_banner)
                    setTextColor(adjustAlpha(textColor, 0.9f))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                    setTypeface(null, Typeface.ITALIC)
                    gravity = Gravity.CENTER_HORIZONTAL
                    layoutParams = marginTop(context, 8)
                })
            }
        }

        val maxScrollH = (context.resources.displayMetrics.heightPixels * 0.5f).toInt()
        val scroll = MaxHeightScrollView(context, maxScrollH).apply {
            isFillViewport = false
            addView(scrollContent)
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

            addView(scroll, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ))
            addDivider(context, textColor)
            addView(buildActionButtons(context, phoneNumber, uiText))
        }
    }

    private fun buildPhaseDescriptionLines(
        context: Context,
        two: TwoPhaseDecision,
        lang: Lang,
    ): List<String> {
        val p1 = two.phase1
        val c1 = DecisionReasoningFormatter.confidencePercent(p1.confidence)
        val r1 = DecisionReasoningFormatter.riskTriLabel(p1.riskLevel, lang)
        val line1 = when (lang) {
            Lang.KO -> context.getString(R.string.overlay_phase1_line, r1, p1.summary, c1)
            Lang.EN -> "Immediate: $r1 · ${p1.summary} ($c1%)"
        }
        val line2 = two.phase2?.let { p2 ->
            val c2 = DecisionReasoningFormatter.confidencePercent(p2.confidence)
            val r2 = DecisionReasoningFormatter.riskTriLabel(p2.riskLevel, lang)
            when (lang) {
                Lang.KO -> context.getString(R.string.overlay_phase2_done_line, r2, p2.summary, c2)
                Lang.EN -> "Final: $r2 · ${p2.summary} ($c2%)"
            }
        } ?: when (lang) {
            Lang.KO -> context.getString(R.string.overlay_phase2_absent)
            Lang.EN -> "Final: not run (immediate judgment only)"
        }
        return listOf(line1, line2)
    }

    private class MaxHeightScrollView(
        context: Context,
        private val maxHeightPx: Int,
    ) : ScrollView(context) {
        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val h = View.MeasureSpec.makeMeasureSpec(maxHeightPx, View.MeasureSpec.AT_MOST)
            super.onMeasure(widthMeasureSpec, h)
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
                val intent = Intent("app.myphonecheck.mobile.ACTION_CALL").apply {
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
}
