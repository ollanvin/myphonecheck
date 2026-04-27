package app.myphonecheck.mobile.feature.cardcheck.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.myphonecheck.mobile.feature.cardcheck.R
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

private val ScreenBg = Color(0xFF0D1B2A)
private val CardBg = Color(0xFF1B2838)
private val Accent = Color(0xFF4FC3F7)
private val TextSubtle = Color(0xFFB0BEC5)

@Composable
fun CardCheckRoute(
    onBack: () -> Unit,
    viewModel: CardCheckViewModel = hiltViewModel(),
) {
    val month by viewModel.selectedMonth.collectAsState()
    val includeLow by viewModel.includeLow.collectAsState()
    val totals by viewModel.monthlyTotals.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    CardCheckScreen(
        month = month,
        includeLow = includeLow,
        totals = totals,
        transactions = transactions,
        onBack = onBack,
        onSelectMonth = viewModel::selectMonth,
        onToggleIncludeLow = viewModel::toggleIncludeLow,
    )
}

@Composable
private fun CardCheckScreen(
    month: MonthOffset,
    includeLow: Boolean,
    totals: List<app.myphonecheck.mobile.data.localcache.dao.CardTransactionMonthlyTotal>,
    transactions: List<app.myphonecheck.mobile.data.localcache.entity.CardTransactionEntity>,
    onBack: () -> Unit,
    onSelectMonth: (MonthOffset) -> Unit,
    onToggleIncludeLow: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().background(ScreenBg)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Text(
                    text = stringResource(R.string.card_check_title),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            UserPromiseCard()
            Spacer(modifier = Modifier.height(12.dp))

            MonthSelector(
                selected = month,
                onSelect = onSelectMonth,
            )
            Spacer(modifier = Modifier.height(8.dp))

            FilterChip(
                selected = includeLow,
                onClick = onToggleIncludeLow,
                label = { Text(stringResource(R.string.card_check_include_low_confidence)) },
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (totals.isEmpty() && transactions.isEmpty()) {
                EmptyStateCard()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Text(
                            text = stringResource(R.string.card_check_section_totals),
                            color = TextSubtle,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 4.dp),
                        )
                    }
                    items(totals, key = { "${it.sourceId}|${it.currencyCode}" }) { total ->
                        CurrencyCardView(total)
                    }
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.card_check_section_transactions),
                            color = TextSubtle,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 4.dp),
                        )
                    }
                    items(transactions, key = { it.id }) { tx ->
                        TransactionRow(tx)
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthSelector(
    selected: MonthOffset,
    onSelect: (MonthOffset) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = selected == MonthOffset.CURRENT,
            onClick = { onSelect(MonthOffset.CURRENT) },
            label = { Text(stringResource(R.string.card_check_month_current)) },
        )
        FilterChip(
            selected = selected == MonthOffset.PREVIOUS,
            onClick = { onSelect(MonthOffset.PREVIOUS) },
            label = { Text(stringResource(R.string.card_check_month_previous)) },
        )
    }
}

@Composable
private fun CurrencyCardView(
    total: app.myphonecheck.mobile.data.localcache.dao.CardTransactionMonthlyTotal,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = total.sourceLabel.ifEmpty { total.sourceId },
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = total.currencyCode + " · " +
                        stringResource(
                            R.string.card_check_transaction_count,
                            total.transactionCount,
                        ),
                    color = TextSubtle,
                    fontSize = 12.sp,
                )
            }
            Text(
                text = formatAmount(total.totalMinorUnits, total.currencyCode),
                color = Accent,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun TransactionRow(
    tx: app.myphonecheck.mobile.data.localcache.entity.CardTransactionEntity,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.merchantName ?: tx.sourceLabel,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = (tx.cardIdentifier?.let { "**$it · " } ?: "") +
                        tx.currencyCode + " · " +
                        confidenceLabel(tx.confidence),
                    color = TextSubtle,
                    fontSize = 11.sp,
                )
            }
            Text(
                text = formatAmount(tx.amount, tx.currencyCode),
                color = Accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun confidenceLabel(value: String): String = when (value) {
    "HIGH" -> stringResource(R.string.card_check_confidence_high)
    "MEDIUM" -> stringResource(R.string.card_check_confidence_medium)
    else -> stringResource(R.string.card_check_confidence_low)
}

@Composable
private fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.card_check_empty_title),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.card_check_empty_desc),
                color = TextSubtle,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun UserPromiseCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF22364A)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Text(
            text = stringResource(R.string.card_check_user_promise),
            color = Color.White,
            fontSize = 11.sp,
            modifier = Modifier.padding(12.dp),
        )
    }
}

/**
 * minor units → 사용자 디스플레이 문자열.
 *
 * ICU NumberFormat (currency style) — 디바이스 로케일 기준 자동 포맷.
 */
private fun formatAmount(minorUnits: Long, currencyCode: String): String {
    return runCatching {
        val currency = Currency.getInstance(currencyCode)
        val fractionDigits = currency.defaultFractionDigits.coerceAtLeast(0)
        val divisor = Math.pow(10.0, fractionDigits.toDouble())
        val majorAmount = if (fractionDigits == 0) minorUnits.toDouble() else minorUnits.toDouble() / divisor
        val nf = NumberFormat.getCurrencyInstance(Locale.getDefault())
        nf.currency = currency
        nf.minimumFractionDigits = fractionDigits
        nf.maximumFractionDigits = fractionDigits
        nf.format(majorAmount)
    }.getOrDefault("$minorUnits $currencyCode")
}
