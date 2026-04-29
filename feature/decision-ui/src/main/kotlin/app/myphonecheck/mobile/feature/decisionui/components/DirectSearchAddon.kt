package app.myphonecheck.mobile.feature.decisionui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.myphonecheck.core.common.risk.RiskTier
import app.myphonecheck.mobile.core.globalengine.search.SearchInput

/**
 * Surface UI에 minimal addition으로 통합되는 직접 검색 addon (v2.5.0 §direct-search 정합).
 *
 * 단일 input 케이스 (CallCheck / CardCheck / MicCheck / CameraCheck):
 *   DirectSearchAddon(input, tier, surfaceContext, handler)
 *
 * 다중 input 케이스 (MessageCheck / PushCheck):
 *   MultiInputDirectSearchAddon(candidates, tier, surfaceContext, handler)
 *
 * 두 addon 모두 SimAiSearchRegistry의 SIM 기준 후보군 메뉴 + Custom Tab launch.
 */
@Composable
fun DirectSearchAddon(
    input: SearchInput,
    tier: RiskTier,
    surfaceContext: SurfaceContext,
    handler: DirectSearchHandler,
    modifier: Modifier = Modifier,
) {
    var showAiMenu by remember { mutableStateOf(false) }

    DirectSearchButton(
        input = input,
        tier = tier,
        surfaceContext = surfaceContext,
        onTap = { showAiMenu = true },
        modifier = modifier,
    )

    if (showAiMenu) {
        SimBasedAiMenu(
            input = input,
            surfaceContext = surfaceContext,
            candidates = handler.getAiCandidates(),
            lastSelectedMode = handler.getLastSelectedMode(),
            onSelect = { action ->
                handler.launch(action, input)
                showAiMenu = false
            },
            onDismiss = { showAiMenu = false },
        )
    }
}

/**
 * 다중 input 케이스 — 본문 파싱으로 PhoneNumber/Url/MessageBody 다중 후보가 있을 때.
 * 1) DirectSearchButton 탭 → SearchInputPicker 표시 (1개 후보면 skip)
 * 2) input 선택 → SimBasedAiMenu 표시
 * 3) AI mode 선택 → Custom Tab launch
 */
@Composable
fun MultiInputDirectSearchAddon(
    candidates: List<SearchInput>,
    tier: RiskTier,
    surfaceContext: SurfaceContext,
    handler: DirectSearchHandler,
    modifier: Modifier = Modifier,
) {
    require(candidates.isNotEmpty()) { "candidates must not be empty" }

    var showInputPicker by remember { mutableStateOf(false) }
    var showAiMenu by remember { mutableStateOf(false) }
    var selectedInput by remember { mutableStateOf<SearchInput?>(null) }

    DirectSearchButton(
        input = candidates.first(),
        tier = tier,
        surfaceContext = surfaceContext,
        onTap = {
            if (candidates.size > 1) {
                showInputPicker = true
            } else {
                selectedInput = candidates.first()
                showAiMenu = true
            }
        },
        modifier = modifier,
    )

    if (showInputPicker) {
        SearchInputPicker(
            candidates = candidates,
            onSelect = { input ->
                selectedInput = input
                showInputPicker = false
                showAiMenu = true
            },
            onDismiss = { showInputPicker = false },
        )
    }

    val current = selectedInput
    if (showAiMenu && current != null) {
        SimBasedAiMenu(
            input = current,
            surfaceContext = surfaceContext,
            candidates = handler.getAiCandidates(),
            lastSelectedMode = handler.getLastSelectedMode(),
            onSelect = { action ->
                handler.launch(action, current)
                showAiMenu = false
            },
            onDismiss = { showAiMenu = false },
        )
    }
}
