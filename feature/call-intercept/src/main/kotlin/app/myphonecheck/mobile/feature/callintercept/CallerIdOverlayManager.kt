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
import app.myphonecheck.mobile.core.model.ActionState
import app.myphonecheck.mobile.core.model.DecisionResult
import app.myphonecheck.mobile.core.model.RiskLevel
import app.myphonecheck.mobile.core.model.SearchStatus
import app.myphonecheck.mobile.core.model.SimilarNumberResult
import app.myphonecheck.mobile.core.model.TwoPhaseDecision
import app.myphonecheck.mobile.core.model.displayLabelKo
import app.myphonecheck.mobile.core.util.DecisionReasoningFormatter
import app.myphonecheck.mobile.core.util.DecisionReasoningFormatter.Lang
import app.myphonecheck.mobile.core.util.TrustScoreCalculator
import app.myphonecheck.mobile.data.localcache.repository.NumberProfileSnapshot
import app.myphonecheck.mobile.feature.countryconfig.SignalSummaryLocalizer
import app.myphonecheck.mobile.feature.countryconfig.SupportedLanguage
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "CallerIdOverlay"

/**
 * ?꾪솕 ???꾩뿉 ?먯젙 寃곌낵 ?ㅻ쾭?덉씠瑜??쒖떆?쒕떎.
 *
 * 湲濡쒕쾶 ???
 * - 紐⑤뱺 UI ?띿뒪?몃뒗 SupportedLanguage + SignalSummaryLocalizer瑜??듯빐 濡쒖뺄?쇱씠利? * - 踰덊샇??raw ?뺤떇 ?좎?, ?섎? 臾멸뎄留?locale??留욊쾶 蹂?? * - 寃???붿쭊 ?대쫫(Google, Naver ??? UI???덈? ?몄텧?섏? ?딆쓬
 *
 * ??? 誘몄???踰덊샇留? ??λ맂 ?곕씫泥섎뒗 ??留ㅻ땲????꾨떖?섏? ?딆쓬.
 */
@Singleton
class CallerIdOverlayManager @Inject constructor() {

    private var overlayView: View? = null
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
     * ?ㅻ쾭?덉씠瑜??쒖떆?쒕떎.
     *
     * @param context Android Context
     * @param result ?먯젙 寃곌낵
     * @param phoneNumber raw 踰덊샇 (湲곌린 ?먮낯 洹몃?濡?
     * @param language ?꾩옱 湲곌린 ?몄뼱
     * @param localizer SignalSummary 濡쒖뺄?쇱씠?
     * @param twoPhaseDecision 2-Phase 硫뷀?(利됱떆/?뺤젙). null?대㈃ Phase UI ?앸왂.
     * @param userBlockCount ?ъ슜?먭? ??踰덊샇瑜?李⑤떒???꾩쟻 ?잛닔 (?숈뒿 諛섏쁺)
     * @param savedTag ?댁쟾????ν븳 ?쒓렇 (?덉쑝硫??곷떒 利됱떆 ?쒖떆)
     * @param savedMemo ?댁쟾????ν븳 硫붾え (?덉쑝硫??곷떒 利됱떆 ?쒖떆)
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

                overlayView = buildOverlayView(
                    context = context,
                    result = result,
                    phoneNumber = phoneNumber,
                    language = language,
                    localizer = localizer,
                    twoPhaseDecision = twoPhaseDecision,
                    userBlockCount = userBlockCount,
                    numberProfileSnapshot = numberProfileSnapshot,
                )
                wm.addView(overlayView, params)
                Log.i(
                    TAG,
                    "overlay_rendered number=$phoneNumber risk=${result.riskLevel} " +
                        "actionStateReused=${numberProfileSnapshot?.actionState != ActionState.NONE} " +
                        "lang=${language.code}",
                )
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
            pendingPromptNumber?.let { renderStartMsByNumber.remove(it) }
        }
    }

    fun isOverlayShowing(): Boolean = overlayView != null

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
        val context = pendingPromptContext
        pendingPromptContext = null
        pendingPromptNumber = null
        return context
    }

    private fun canDrawOverlays(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    // ?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧
    // View Builder
    // ?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧

    /**
     * ?꾪솕 ?먮떒 ?ㅻ쾭?덉씠 ??寃곕줎 ??洹쇨굅 ???몃? 援ъ“.
     *
     * [寃곕줎] ?좊ː???먯닔 + ?됱긽 (0.5珥????먮떒 媛??
     * [洹쇨굅] ?먮떒 洹쇨굅 3以??붿빟
     * [?몃?] ?좎궗踰덊샇 寃??寃곌낵 + 湲곗〈 4?뱀뀡 遺꾩꽍
     * [?됰룞] 利됱떆 ?됰룞 踰꾪듉: ?섏떊 / 嫄곗젅 / 李⑤떒
     */
    private fun buildOverlayView(
        context: Context,
        result: DecisionResult,
        phoneNumber: String,
        language: SupportedLanguage,
        localizer: SignalSummaryLocalizer,
        twoPhaseDecision: TwoPhaseDecision? = null,
        userBlockCount: Int = 0,
        numberProfileSnapshot: NumberProfileSnapshot? = null,
    ): View {
        val trustScore = TrustScoreCalculator.calculate(result, userBlockCount)
        val bgColor = TrustScoreCalculator.gradeColor(trustScore)
        val textColor = Color.WHITE
        val subtleColor = adjustAlpha(textColor, 0.80f)
        val uiText = OverlayUiText(context)
        val lang = if (language == SupportedLanguage.KO) Lang.KO else Lang.EN
        val categoryText = localizer.localizeCategory(result.category.name, context)
        val actionStateText = numberProfileSnapshot?.actionState?.displayLabelKo()
        val labelText = numberProfileSnapshot?.quickLabels
            ?.joinToString(" / ") { it.displayName }
            ?.takeIf { it.isNotBlank() }
        val detailText = numberProfileSnapshot?.detailTags
            ?.joinToString(" / ") { it.tagName }
            ?.takeIf { it.isNotBlank() }
        val keySignalsText = result.reasons.take(2).joinToString(" 쨌 ").takeIf { it.isNotBlank() }

        // ?좎궗踰덊샇 寃곌낵 異붿텧
        val similarResults = SimilarNumberResult.fromAdjacentHint(
            result.searchEvidence?.adjacentNumberHint
        )

        val scrollContent = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL

            // ?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧
            // [寃곕줎] ?좊ː???먯닔 ??0.5珥??먮떒
            // ?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧

            // ??λ맂 ?쒓렇/硫붾え媛 ?덉쑝硫?理쒖긽???쒖떆
            addView(TextView(context).apply {
                text = result.summary
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = marginTop(context, 2)
            })

            addView(TextView(context).apply {
                text = searchStatusLabel(result)
                setTextColor(Color.parseColor("#FFE082"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = marginTop(context, 4)
            })

            actionStateText?.let {
                addView(TextView(context).apply {
                    text = it
                    setTextColor(Color.parseColor("#A5D6A7"))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    setTypeface(null, Typeface.BOLD)
                    gravity = Gravity.CENTER_HORIZONTAL
                    layoutParams = marginTop(context, 2)
                })
            }

            if (!labelText.isNullOrBlank() || !detailText.isNullOrBlank()) {
                addView(TextView(context).apply {
                    text = listOf(labelText, detailText).filterNotNull().joinToString(" / ")
                    setTextColor(Color.YELLOW)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    gravity = Gravity.CENTER_HORIZONTAL
                    layoutParams = marginTop(context, 2)
                })
            }

            keySignalsText?.let {
                addView(TextView(context).apply {
                    text = it
                    setTextColor(subtleColor)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                    gravity = Gravity.CENTER_HORIZONTAL
                    layoutParams = marginTop(context, 4)
                })
            }

            if (false) {
                addView(TextView(context).apply {
                    val labelText = numberProfileSnapshot?.quickLabels.orEmpty()
                        .joinToString(" / ") { it.displayName }
                    val detailText = numberProfileSnapshot?.detailTags.orEmpty()
                        .joinToString(" / ") { it.tagName }
                    val actionStateText = when (numberProfileSnapshot?.actionState ?: ActionState.NONE) {
                        ActionState.BLOCKED -> "李⑤떒 ?곹깭"
                        ActionState.DO_NOT_BLOCK -> "李⑤떒 湲덉?"
                        ActionState.NONE -> ""
                    }
                    val memoText = numberProfileSnapshot?.userMemoShort.orEmpty()
                    text = listOf(labelText, detailText, actionStateText, memoText)
                        .filter { it.isNotBlank() }
                        .joinToString(" / ")
                    setTextColor(Color.YELLOW)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    setTypeface(null, Typeface.BOLD)
                    gravity = Gravity.CENTER_HORIZONTAL
                    layoutParams = marginTop(context, 2)
                })
            }

            // ???レ옄濡??좊ː???먯닔 ?쒖떆
            addView(TextView(context).apply {
                text = "$trustScore"
                setTextColor(textColor)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f)
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
            })

            // ?깃툒 ?쇰꺼 (?꾪뿕 / 二쇱쓽 / ?꾪뿕 ?좏샇 ?곸쓬)
            addView(TextView(context).apply {
                text = uiText.trustGradeLabel(trustScore)
                setTextColor(textColor)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = marginTop(context, 2)
            })

            addView(TextView(context).apply {
                text = "$categoryText  \u00B7  $phoneNumber"
                setTextColor(subtleColor)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = marginTop(context, 2)
            })

            // ?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧
            // [洹쇨굅] ?먮떒 洹쇨굅 3以??붿빟
            // ?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧

            addDivider(context, textColor)

            // 湲곗〈 reasons (理쒕? 3媛? ?쒖떆
            val reasons = result.reasons.take(3)
            if (reasons.isNotEmpty()) {
                for (reason in reasons) {
                    addView(TextView(context).apply {
                        text = "\u2022 $reason"
                        setTextColor(subtleColor)
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                        layoutParams = marginTop(context, 2)
                    })
                }
            } else {
                addView(TextView(context).apply {
                    text = if (TrustScoreCalculator.isUnverified(result)) {
                        context.getString(R.string.overlay_unverified_note)
                    } else {
                        result.summary
                    }
                    setTextColor(subtleColor)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                    layoutParams = marginTop(context, 2)
                })
            }

            // 2-Phase ?붿빟 (?덉쑝硫?
            twoPhaseDecision?.let { tp ->
                for (line in buildPhaseDescriptionLines(context, tp, lang)) {
                    addView(TextView(context).apply {
                        text = line
                        setTextColor(adjustAlpha(textColor, 0.70f))
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                        layoutParams = marginTop(context, 1)
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

            // ?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧
            // [?몃?] ?좎궗踰덊샇 寃??寃곌낵 + 遺꾩꽍 ?뱀뀡
            // ?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧

            addDivider(context, textColor)

            // ?좎궗踰덊샇 寃??寃곌낵 (?덉쑝硫?
            if (similarResults.isNotEmpty()) {
                addView(TextView(context).apply {
                    text = context.getString(R.string.overlay_section_similar_numbers)
                    setTextColor(textColor)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                    setTypeface(null, Typeface.BOLD)
                    layoutParams = marginTop(context, 4)
                })
                for (sr in similarResults) {
                    val orgText = sr.estimatedOrg?.let { " \u2014 $it" } ?: ""
                    addView(TextView(context).apply {
                        text = "${sr.pattern}$orgText (${sr.searchSummary})"
                        setTextColor(subtleColor)
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                        layoutParams = marginTop(context, 1)
                    })
                }
                addDivider(context, textColor)
            }

            // 湲곗〈 4?뱀뀡 遺꾩꽍 (Report, Pattern, Behavior, Search)
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
            Lang.EN -> "Immediate: $r1 쨌 ${p1.summary} ($c1%)"
        }
        val line2 = two.phase2?.let { p2 ->
            val c2 = DecisionReasoningFormatter.confidencePercent(p2.confidence)
            val r2 = DecisionReasoningFormatter.riskTriLabel(p2.riskLevel, lang)
            when (lang) {
                Lang.KO -> context.getString(R.string.overlay_phase2_done_line, r2, p2.summary, c2)
                Lang.EN -> "Final: $r2 쨌 ${p2.summary} ($c2%)"
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

    // ?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧
    // Action Buttons
    // ?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧

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
        val renderStartedAtMs = renderStartMsByNumber.remove(phoneNumber)
        val actionLatencyMs = renderStartedAtMs?.let { System.currentTimeMillis() - it }
        Log.i(TAG, "overlay_action action=$action number=$phoneNumber latencyMs=$actionLatencyMs")

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
                // ?섏떊 ?됰룞??UserCallRecord??湲곕줉
                broadcastUserAction(context, "action_accept", phoneNumber)
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
                // 嫄곗젅 ?됰룞??UserCallRecord??湲곕줉
                broadcastUserAction(context, "action_reject", phoneNumber)
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
                // 李⑤떒 ?됰룞??UserCallRecord??湲곕줉
                broadcastUserAction(context, "action_block", phoneNumber)
                dismissOverlay(context)
            }
        }
    }

    /**
     * ?ъ슜???됰룞??CallActionReceiver濡?釉뚮줈?쒖틦?ㅽ듃.
     * CallActionReceiver媛 UserCallRecordRepository + BlocklistRepository濡?湲곕줉.
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

    // ?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧
    // View Helpers
    // ?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧

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

    // ?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧
    // Style
    // ?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧

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

    private fun searchStatusLabel(result: DecisionResult): String {
        return SearchStatus.fromDecisionResult(result).labelKo
    }
}

// ?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧
// Overlay UI ?띿뒪????Android String Resources
// ?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧?먥븧

/**
 * ?ㅻ쾭?덉씠 UI ?띿뒪???ы띁.
 * Android string resources瑜??듯빐 濡쒖뺄?쇱씠利덈맂 ?띿뒪?몃? ?쒓났?쒕떎.
 * Context瑜?諛쏆븘 locale???먮룞?쇰줈 泥섎━?쒕떎.
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

    /** 0~100 ?좊ː???먯닔 ???깃툒 ?쇰꺼 ('?덉쟾' ?쒗쁽 ?덈? 湲덉?) */
    fun trustGradeLabel(score: Int): String = when {
        score <= 30 -> context.getString(R.string.overlay_trust_danger)
        score <= 60 -> context.getString(R.string.overlay_trust_caution)
        else -> context.getString(R.string.overlay_trust_low_risk)
    }
}
