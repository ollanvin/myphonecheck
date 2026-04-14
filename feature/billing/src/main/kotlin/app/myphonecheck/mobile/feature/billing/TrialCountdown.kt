package app.myphonecheck.mobile.feature.billing

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.util.concurrent.TimeUnit

sealed class TrialCountdown {
    data object NotApplicable : TrialCountdown()
    data class Remaining(val days: Int) : TrialCountdown()
}

fun computeTrialDday(
    purchaseTimeMillis: Long,
    trialDurationDays: Int = DEFAULT_TRIAL_DAYS,
    nowMillis: Long = System.currentTimeMillis(),
): TrialCountdown {
    if (purchaseTimeMillis <= 0L || nowMillis < purchaseTimeMillis) {
        return TrialCountdown.NotApplicable
    }

    val elapsedMillis = nowMillis - purchaseTimeMillis
    val totalTrialMillis = TimeUnit.DAYS.toMillis(trialDurationDays.toLong())
    val remainingMillis = totalTrialMillis - elapsedMillis
    if (remainingMillis <= 0L) return TrialCountdown.NotApplicable

    val oneDayMillis = TimeUnit.DAYS.toMillis(1)
    val remainingDays = ((remainingMillis + oneDayMillis - 1) / oneDayMillis).toInt()
    return TrialCountdown.Remaining(remainingDays)
}

@Composable
fun TrialCountdown(
    state: TrialCountdown,
    modifier: Modifier = Modifier,
) {
    val remaining = state as? TrialCountdown.Remaining ?: return

    Text(
        text = "무료 체험 종료까지 ${remaining.days}일 남음",
        modifier = modifier,
        color = Color(0xFF34C759),
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

private const val DEFAULT_TRIAL_DAYS: Int = 30
