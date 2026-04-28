package app.myphonecheck.mobile.feature.settings.v2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.FeedRegistry
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedSource
import app.myphonecheck.mobile.core.globalengine.simcontext.SimChangeDetector
import app.myphonecheck.mobile.core.globalengine.simcontext.SimChangeResult
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextProvider
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextStorage
import app.myphonecheck.mobile.core.globalengine.simcontext.UiLanguageApplicator
import app.myphonecheck.mobile.core.globalengine.simcontext.UiLanguagePreference
import app.myphonecheck.mobile.feature.initialscan.repository.BaseDataRepository
import app.myphonecheck.mobile.feature.initialscan.service.InitialScanService
import app.myphonecheck.mobile.feature.settings.v2.repository.UserPreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Settings v2 ViewModel (Architecture v2.0.0 §29).
 *
 * SIM 정보 + 변경 감지 + 베이스 카운트 + 사용자 선호 통합 노출.
 */
@HiltViewModel
class SettingsV2ViewModel @Inject constructor(
    private val simContextProvider: SimContextProvider,
    private val simContextStorage: SimContextStorage,
    private val simChangeDetector: SimChangeDetector,
    private val baseDataRepository: BaseDataRepository,
    private val initialScanService: InitialScanService,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val feedRegistry: FeedRegistry,
    private val uiLanguageApplicator: UiLanguageApplicator,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsV2UiState())
    val state: StateFlow<SettingsV2UiState> = _state.asStateFlow()

    val languagePreference: StateFlow<UiLanguagePreference> =
        userPreferenceRepository.uiLanguagePreferenceFlow
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiLanguagePreference.SIM_BASED)

    val publicFeedOptIn: StateFlow<Set<String>> =
        userPreferenceRepository.publicFeedOptInFlow
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    /** FeedRegistry 카탈로그 — Stage 2-008(§30-4) 4 FeedType 출처. */
    fun availableFeedSources(): List<PublicFeedSource> = feedRegistry.all()

    fun isFeedPlaceholder(source: PublicFeedSource): Boolean = feedRegistry.isPlaceholder(source)

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val current = simContextProvider.resolve()
            val previous = simContextStorage.loadPrevious()
            val change = simChangeDetector.detectChange(previous)
            val callCount = baseDataRepository.callCount()
            val smsCount = baseDataRepository.smsCount()
            val packageCount = baseDataRepository.packageCount()
            _state.value = SettingsV2UiState(
                currentSim = current,
                previousSim = previous,
                simChange = change,
                callCount = callCount,
                smsCount = smsCount,
                packageCount = packageCount,
            )
        }
    }

    fun setLanguagePreference(pref: UiLanguagePreference) {
        viewModelScope.launch {
            userPreferenceRepository.setUiLanguagePreference(pref)
            // Stage 2-009 (PR #30): preference 저장 직후 즉시 Locale 적용.
            // AppCompatDelegate.setApplicationLocales가 Activity recreate 트리거.
            uiLanguageApplicator.apply(pref, simContextProvider.resolve())
        }
    }

    fun setPublicFeedOptIn(sourceId: String, optIn: Boolean) {
        viewModelScope.launch { userPreferenceRepository.setPublicFeedOptIn(sourceId, optIn) }
    }

    fun rescan() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { initialScanService.execute() }
            refresh()
        }
    }

    fun resetBase() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { baseDataRepository.clear() }
            refresh()
        }
    }

    fun applyNewSim(newContext: SimContext) {
        viewModelScope.launch {
            simContextStorage.saveCurrent(newContext)
            withContext(Dispatchers.IO) { initialScanService.execute() }
            refresh()
        }
    }

    fun keepPreviousSim() {
        viewModelScope.launch {
            // 사용자가 명시적으로 기존 컨텍스트 유지 — Storage는 갱신하지 않음.
            refresh()
        }
    }

    fun resetAndRescan() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                baseDataRepository.clear()
                initialScanService.execute()
            }
            refresh()
        }
    }
}

data class SettingsV2UiState(
    val currentSim: SimContext? = null,
    val previousSim: SimContext? = null,
    val simChange: SimChangeResult? = null,
    val callCount: Int = 0,
    val smsCount: Int = 0,
    val packageCount: Int = 0,
)
