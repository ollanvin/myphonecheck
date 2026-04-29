package app.myphonecheck.mobile.feature.decisionui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.myphonecheck.core.common.risk.RiskTier
import app.myphonecheck.mobile.core.globalengine.search.SearchInput
import app.myphonecheck.mobile.feature.decisionui.R

/**
 * "🔍 Direct Search" 버튼 (v2.5.0 §direct-search 정합).
 *
 * Tier별 시각 강조:
 *  - Unknown: prominent (primary color) — 사용자 의사결정 보조 강조
 *  - 그 외: secondary (surfaceVariant) — 사용자 선택 보조
 *
 * 헌법 §1 정합: 탭 시 SimBasedAiMenu 표시 → Custom Tab 사용자 직접 진입.
 */
@Composable
fun DirectSearchButton(
    @Suppress("UNUSED_PARAMETER") input: SearchInput,
    tier: RiskTier,
    @Suppress("UNUSED_PARAMETER") surfaceContext: SurfaceContext,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val emphasis = tier == RiskTier.Unknown

    Button(
        onClick = onTap,
        modifier = modifier.heightIn(min = 44.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (emphasis) MaterialTheme.colorScheme.primary
                             else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (emphasis) MaterialTheme.colorScheme.onPrimary
                           else MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    ) {
        Icon(Icons.Default.Search, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(stringResource(R.string.direct_search_button_label))
    }
}
