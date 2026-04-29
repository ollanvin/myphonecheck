package app.myphonecheck.mobile.feature.settings.v2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
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
import app.myphonecheck.mobile.core.globalengine.simcontext.SimChangeResult
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.feature.settings.R
import app.myphonecheck.mobile.core.globalengine.simcontext.UiLanguagePreference
import java.util.Locale

private val ScreenBg = Color(0xFF0D1B2A)
private val CardBg = Color(0xFF1B2838)
private val Accent = Color(0xFF4FC3F7)
private val Warn = Color(0xFFFFB74D)
private val TextSubtle = Color(0xFFB0BEC5)

@Composable
fun SettingsV2Route(
    onBack: () -> Unit,
    viewModel: SettingsV2ViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val language by viewModel.languagePreference.collectAsState()
    val optIn by viewModel.publicFeedOptIn.collectAsState()
    val feedSources = viewModel.availableFeedSources()

    SettingsV2Screen(
        state = state,
        language = language,
        publicFeedOptIn = optIn,
        feedSources = feedSources,
        isPlaceholder = viewModel::isFeedPlaceholder,
        onBack = onBack,
        onSelectLanguage = viewModel::setLanguagePreference,
        onRescan = viewModel::rescan,
        onResetBase = viewModel::resetBase,
        onApplyNewSim = viewModel::applyNewSim,
        onKeepPrevious = viewModel::keepPreviousSim,
        onResetAndRescan = viewModel::resetAndRescan,
        onToggleFeedOptIn = viewModel::setPublicFeedOptIn,
    )
}

@Composable
private fun SettingsV2Screen(
    state: SettingsV2UiState,
    language: UiLanguagePreference,
    publicFeedOptIn: Set<String>,
    feedSources: List<app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedSource>,
    isPlaceholder: (app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedSource) -> Boolean,
    onBack: () -> Unit,
    onSelectLanguage: (UiLanguagePreference) -> Unit,
    onRescan: () -> Unit,
    onResetBase: () -> Unit,
    onApplyNewSim: (SimContext) -> Unit,
    onKeepPrevious: () -> Unit,
    onResetAndRescan: () -> Unit,
    onToggleFeedOptIn: (String, Boolean) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().background(ScreenBg)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Text(
                    text = stringResource(R.string.settings_v2_title),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            LanguagePreferenceSection(language, onSelectLanguage)

            SimContextSection(state, onApplyNewSim, onKeepPrevious, onResetAndRescan)

            BaseDataSection(state, onRescan, onResetBase)

            PublicFeedOptInSection(
                sources = feedSources,
                optedInIds = publicFeedOptIn,
                simCountryIso = state.currentSim?.countryIso.orEmpty(),
                isPlaceholder = isPlaceholder,
                onToggle = onToggleFeedOptIn,
            )

            ConstitutionSection()

            PrivacyPromiseSection()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) { content() }
    }
}

@Composable
private fun LanguagePreferenceSection(
    current: UiLanguagePreference,
    onSelect: (UiLanguagePreference) -> Unit,
) {
    SectionCard {
        SectionTitle(stringResource(R.string.settings_v2_language_title))
        val sysLang = Locale.getDefault().displayLanguage
        RadioRow(
            selected = current == UiLanguagePreference.SIM_BASED,
            label = stringResource(R.string.settings_v2_language_sim_based, sysLang),
            onClick = { onSelect(UiLanguagePreference.SIM_BASED) },
        )
        RadioRow(
            selected = current == UiLanguagePreference.DEVICE_SYSTEM,
            label = stringResource(R.string.settings_v2_language_device_system, sysLang),
            onClick = { onSelect(UiLanguagePreference.DEVICE_SYSTEM) },
        )
        RadioRow(
            selected = current == UiLanguagePreference.ENGLISH,
            label = stringResource(R.string.settings_v2_language_english),
            onClick = { onSelect(UiLanguagePreference.ENGLISH) },
        )
    }
}

@Composable
private fun SimContextSection(
    state: SettingsV2UiState,
    onApplyNewSim: (SimContext) -> Unit,
    onKeepPrevious: () -> Unit,
    onResetAndRescan: () -> Unit,
) {
    SectionCard {
        SectionTitle(stringResource(R.string.settings_v2_sim_title))
        val sim = state.currentSim
        if (sim == null) {
            Text(
                text = stringResource(R.string.settings_v2_sim_unknown),
                color = TextSubtle,
                fontSize = 13.sp,
            )
            return@SectionCard
        }
        InfoRow(stringResource(R.string.settings_v2_sim_country), sim.countryIso)
        InfoRow(stringResource(R.string.settings_v2_sim_operator), sim.operatorName.ifEmpty { "—" })
        InfoRow(stringResource(R.string.settings_v2_sim_currency), sim.currency.currencyCode)
        InfoRow(stringResource(R.string.settings_v2_sim_phone_region), sim.phoneRegion)
        InfoRow(stringResource(R.string.settings_v2_sim_timezone), sim.timezone.id)

        val change = state.simChange
        if (change is SimChangeResult.CountryChanged || change is SimChangeResult.OperatorChanged) {
            Spacer(modifier = Modifier.height(12.dp))
            val (prev, curr) = when (change) {
                is SimChangeResult.CountryChanged -> change.previous to change.current
                is SimChangeResult.OperatorChanged -> change.previous to change.current
                else -> return@SectionCard
            }
            Text(
                text = stringResource(R.string.settings_v2_sim_change_title),
                color = Warn,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(
                    R.string.settings_v2_sim_change_desc,
                    "${prev.countryIso} · ${prev.operatorName}",
                    "${curr.countryIso} · ${curr.operatorName}",
                ),
                color = TextSubtle,
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onApplyNewSim(curr) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.settings_v2_sim_change_apply))
            }
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(
                onClick = onKeepPrevious,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.settings_v2_sim_change_keep))
            }
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(
                onClick = onResetAndRescan,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.settings_v2_sim_change_reset))
            }
        }
    }
}

@Composable
private fun BaseDataSection(
    state: SettingsV2UiState,
    onRescan: () -> Unit,
    onResetBase: () -> Unit,
) {
    SectionCard {
        SectionTitle(stringResource(R.string.settings_v2_base_title))
        Text(
            text = stringResource(R.string.settings_v2_base_call_count, state.callCount),
            color = TextSubtle, fontSize = 13.sp,
        )
        Text(
            text = stringResource(R.string.settings_v2_base_sms_count, state.smsCount),
            color = TextSubtle, fontSize = 13.sp,
        )
        Text(
            text = stringResource(R.string.settings_v2_base_package_count, state.packageCount),
            color = TextSubtle, fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRescan,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.settings_v2_base_rescan))
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(
            onClick = onResetBase,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.settings_v2_base_reset))
        }
    }
}

@Composable
private fun PublicFeedOptInSection(
    sources: List<app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedSource>,
    optedInIds: Set<String>,
    simCountryIso: String,
    isPlaceholder: (app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedSource) -> Boolean,
    onToggle: (String, Boolean) -> Unit,
) {
    SectionCard {
        SectionTitle(stringResource(R.string.settings_v2_public_feed_title))
        Text(
            text = stringResource(R.string.settings_v2_public_feed_desc),
            color = TextSubtle, fontSize = 12.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (sources.isEmpty()) {
            Text(
                text = stringResource(R.string.settings_v2_public_feed_empty),
                color = TextSubtle, fontSize = 12.sp,
            )
            return@SectionCard
        }

        val global = sources.filter {
            it.countryScope is app.myphonecheck.mobile.core.globalengine.search.publicfeed.CountryScope.GLOBAL
        }
        val country = sources.filter {
            val cs = it.countryScope
            cs is app.myphonecheck.mobile.core.globalengine.search.publicfeed.CountryScope.COUNTRY &&
                cs.iso.equals(simCountryIso, ignoreCase = true)
        }
        val others = sources - global.toSet() - country.toSet()

        if (global.isNotEmpty()) {
            Text(
                text = stringResource(R.string.settings_v2_feed_section_global),
                color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
            )
            global.forEach { src ->
                FeedToggleRow(src, optedInIds.contains(src.id), isPlaceholder(src), onToggle)
            }
        }

        if (country.isNotEmpty()) {
            Text(
                text = stringResource(R.string.settings_v2_feed_section_country, simCountryIso),
                color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            )
            country.forEach { src ->
                FeedToggleRow(src, optedInIds.contains(src.id), isPlaceholder(src), onToggle)
            }
        }

        if (others.isNotEmpty()) {
            Text(
                text = stringResource(R.string.settings_v2_feed_section_other),
                color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            )
            others.forEach { src ->
                FeedToggleRow(src, optedInIds.contains(src.id), isPlaceholder(src), onToggle)
            }
        }
    }
}

@Composable
private fun FeedToggleRow(
    source: app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedSource,
    optedIn: Boolean,
    placeholder: Boolean,
    onToggle: (String, Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(
                text = source.name,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = stringResource(
                    R.string.settings_v2_feed_license_format,
                    source.license,
                    source.updateFrequency.name,
                ),
                color = TextSubtle,
                fontSize = 10.sp,
            )
            if (placeholder) {
                Text(
                    text = stringResource(R.string.settings_v2_feed_placeholder_warning),
                    color = Warn,
                    fontSize = 10.sp,
                )
            }
        }
        androidx.compose.material3.Switch(
            checked = optedIn,
            enabled = !placeholder,
            onCheckedChange = { onToggle(source.id, it) },
        )
    }
}

@Composable
private fun ConstitutionSection() {
    SectionCard {
        SectionTitle(stringResource(R.string.settings_v2_constitution_title))
        listOf(
            R.string.settings_v2_constitution_a1,
            R.string.settings_v2_constitution_a2,
            R.string.settings_v2_constitution_a3,
            R.string.settings_v2_constitution_a4,
            R.string.settings_v2_constitution_a5,
            R.string.settings_v2_constitution_a6,
            R.string.settings_v2_constitution_a7,
            R.string.settings_v2_constitution_a8,
        ).forEach { resId ->
            Text(
                text = stringResource(resId),
                color = TextSubtle,
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 2.dp),
            )
        }
    }
}

@Composable
private fun PrivacyPromiseSection() {
    SectionCard {
        SectionTitle(stringResource(R.string.settings_v2_promise_title))
        listOf(
            R.string.settings_v2_promise_p1,
            R.string.settings_v2_promise_p2,
            R.string.settings_v2_promise_p3,
            R.string.settings_v2_promise_p4,
        ).forEach { resId ->
            Text(
                text = stringResource(resId),
                color = Accent,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 2.dp),
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, color = TextSubtle, fontSize = 13.sp)
        Text(text = value, color = Accent, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun RadioRow(selected: Boolean, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = label, color = Color.White, fontSize = 13.sp)
    }
}
