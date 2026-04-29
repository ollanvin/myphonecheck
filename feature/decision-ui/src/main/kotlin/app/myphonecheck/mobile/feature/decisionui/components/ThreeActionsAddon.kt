package app.myphonecheck.mobile.feature.decisionui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.myphonecheck.mobile.core.globalengine.search.SearchInput

/**
 * 3액션 통합 addon (v2.6.0 §11 정합).
 *
 * Surface Screen에 minimal addition으로 통합되는 차단/태그/검색 통합 wrapper.
 *
 * - [차단 / 해제 toggle] — Block 액션
 * - [태그] — Tag 액션 (TagInputDialog)
 * - [🔍 검색] — Direct Search 액션 (SimBasedAiMenu 자동 표시)
 *
 * 단일 input 케이스 (CallCheck / CardCheck): SearchInput.PhoneNumber 직접 사용.
 * 다중 input 케이스 (MessageCheck / PushCheck): SearchInputPicker 거치도록 별 wrapper 작성 가능.
 */
@Composable
fun ThreeActionsAddon(
    input: SearchInput,
    surfaceContext: SurfaceContext,
    handler: DirectSearchHandler,
    isBlocked: Boolean,
    currentTag: String?,
    onBlockToggle: (Boolean) -> Unit,
    onTagSet: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAiMenu by remember { mutableStateOf(false) }

    ActionButtonRow(
        input = input,
        isBlocked = isBlocked,
        currentTag = currentTag,
        onBlockToggle = onBlockToggle,
        onTagSet = onTagSet,
        onDirectSearchTap = { showAiMenu = true },
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
