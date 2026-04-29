package app.myphonecheck.mobile.feature.decisionui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.myphonecheck.mobile.core.globalengine.search.SearchInput
import app.myphonecheck.mobile.core.globalengine.search.external.ExternalMode
import app.myphonecheck.mobile.feature.decisionui.R

/**
 * SIM 기준 AI 검색 후보 메뉴 (v2.5.0 §direct-search 정합).
 *
 * 헌법 §1 정합: 최소 2개 보장 + 사용자 자율 결정.
 * 후보군은 SimAiSearchRegistry.getCandidates() 결과 (KR=Naver/Google/Bing, JP=Yahoo/Google/Bing 등).
 *
 * 4축 메뉴 (FourAxisMenu, v2.4.0) 폐기 — AI Mode가 (구) 공공 + 경쟁사 reverse 자체 통합.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimBasedAiMenu(
    @Suppress("UNUSED_PARAMETER") input: SearchInput,
    @Suppress("UNUSED_PARAMETER") surfaceContext: SurfaceContext,
    candidates: List<ExternalMode>,
    lastSelectedMode: ExternalMode?,
    onSelect: (DirectSearchAction) -> Unit,
    onDismiss: () -> Unit,
) {
    require(candidates.size >= 2) {
        "AI search candidates must be >= 2 (헌법 §1 v2.5.0 정합)"
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.direct_search_menu_title),
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(Modifier.height(16.dp))

            // SIM 기준 AI 검색 후보 (최소 2개)
            candidates.forEach { mode ->
                MenuItem(
                    icon = iconForMode(mode),
                    label = labelForMode(mode),
                    isDefault = mode == lastSelectedMode,
                    onClick = {
                        onSelect(DirectSearchAction.AiSearch(mode))
                        onDismiss()
                    },
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 일반 검색 fallback (PLAIN 모드)
            MenuItem(
                icon = Icons.Default.Search,
                label = stringResource(R.string.direct_search_fallback),
                isDefault = false,
                onClick = {
                    onSelect(DirectSearchAction.GenericFallback)
                    onDismiss()
                },
            )
        }
    }
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    label: String,
    isDefault: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
        if (isDefault) {
            Spacer(Modifier.width(8.dp))
            Text("(Last)", style = MaterialTheme.typography.bodySmall)
        }
    }
}

// Material Icons Extended 미포함 — 단일 Search 아이콘 사용 (Stage 3-004-REV).
// 후속 PR에서 mode별 brand 아이콘 추가 가능 (extended dep 추가 후).
private fun iconForMode(@Suppress("UNUSED_PARAMETER") mode: ExternalMode): ImageVector =
    Icons.Default.Search

@Composable
private fun labelForMode(mode: ExternalMode): String = stringResource(
    when (mode) {
        ExternalMode.GOOGLE_AI_MODE -> R.string.direct_search_mode_google_ai
        ExternalMode.BING_COPILOT -> R.string.direct_search_mode_bing_copilot
        ExternalMode.NAVER_AI -> R.string.direct_search_mode_naver_ai
        ExternalMode.YAHOO_JAPAN_AI -> R.string.direct_search_mode_yahoo_japan_ai
        ExternalMode.BAIDU_AI -> R.string.direct_search_mode_baidu_ai
        else -> R.string.direct_search_fallback
    },
)
