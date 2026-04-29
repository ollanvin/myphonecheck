# MyPhoneCheck Android - Integration Guide

## Quick Start

### 1. Add to Navigation

```kotlin
// In MyPhoneCheckNavHost.kt
composable("settings") {
    SettingsScreen()
}

composable("paywall") {
    PaywallScreen()
}
```

### 2. Billing Integration in Main Activity

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize billing manager on app start
        val billingManager: BillingManager by inject()
        billingManager.initialize()

        setContent {
            MyPhoneCheckTheme {
                MyPhoneCheckNavHost()
            }
        }
    }
}
```

### 3. Country Detection on Launch

```kotlin
// In any ViewModel or use case
@HiltViewModel
class AppViewModel @Inject constructor(
    private val countryConfigProvider: CountryConfigProvider,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    init {
        viewModelScope.launch {
            val detectedCountry = countryConfigProvider.detectCountry(context)
            val config = countryConfigProvider.getConfig(detectedCountry)
            // Use config for decision engine, UI strings, etc.
        }
    }
}
```

### 4. Settings in Decision Engine

```kotlin
@HiltViewModel
class DecisionViewModel @Inject constructor(
    private val decisionEngine: DecisionEngine,
    private val settingsRepository: SettingsRepository,
    private val countryConfigProvider: CountryConfigProvider,
) : ViewModel() {

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                // Get country - override or auto-detect
                val countryCode = settings.countryOverride
                    ?: countryConfigProvider.detectCountry(context)
                val config = countryConfigProvider.getConfig(countryCode)

                // Use config keywords for decision evaluation
                decisionEngine.setCountryConfig(config)
            }
        }
    }
}
```

## Module Dependencies

```
App
├── feature:billing
│   ├── core:model
│   ├── core:util
│   └── Play Billing 7.x
├── feature:settings
│   ├── core:model
│   ├── core:util
│   └── SharedPreferences
├── feature:country-config
│   ├── core:model
│   └── core:util
└── feature:decision-ui
    ├── feature:country-config (for UiStrings)
    └── feature:settings (for user prefs)
```

## Feature Usage Examples

### Billing - Check Subscription Status

```kotlin
class PaywallViewModel @Inject constructor(
    private val billingManager: BillingManager,
) : ViewModel() {

    val subscriptionState: StateFlow<SubscriptionState> =
        billingManager.subscriptionState

    fun checkStatus() {
        viewModelScope.launch {
            billingManager.querySubscriptionStatus()
        }
    }
}
```

### Settings - Update User Preference

```kotlin
viewModel.updateLanguage("ko")
viewModel.updateCountryOverride("US")
viewModel.updateEvidenceDisplayLevel("detailed")
```

### Country Config - Get Keywords for Spam Detection

```kotlin
val config = countryConfigProvider.getConfig("KR")
val scamKeywords = config.keywordDictionary.scam
val deliveryKeywords = config.keywordDictionary.delivery

// Use in decision engine
if (callNotes.contains(scamKeywords)) {
    riskLevel = CRITICAL
}
```

### Country Config - Get Localized Strings

```kotlin
val config = countryConfigProvider.getConfig("KR")
val uiStrings = config.uiStrings

// Display risk level
Text(text = uiStrings.riskLevelHigh) // "높음"

// Display call type
Text(text = uiStrings.callTypeScam) // "보이스피싱"
```

## Manifest Permissions

Add to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.READ_CALL_LOG" />
<uses-permission android:name="android.permission.INTERNET" />

<!-- For API 30+ -->
<queries>
    <intent>
        <action android:name="android.intent.action.DIAL" />
    </intent>
</queries>
```

## Testing Examples

### Test Billing Manager

```kotlin
@Test
fun testSubscriptionStateFlow() = runTest {
    val billingManager = BillingManager(context)
    billingManager.initialize()

    // Observe state changes
    billingManager.subscriptionState.test {
        // Verify initial state
        assertEquals(SubscriptionState.Loading, awaitItem())

        // Trigger query
        billingManager.querySubscriptionStatus()

        // Verify result
        val result = awaitItem()
        assertTrue(result is SubscriptionState.Active ||
                   result is SubscriptionState.NotPurchased)
    }
}
```

### Test Settings Repository

```kotlin
@Test
fun testSettingsPersistence() = runTest {
    val repo = SettingsRepositoryImpl(context)

    repo.updateLanguage("en")
    repo.updateCountryOverride("US")

    repo.settings.test {
        val settings = awaitItem()
        assertEquals("en", settings.language)
        assertEquals("US", settings.countryOverride)
    }
}
```

### Test Country Detection

```kotlin
@Test
fun testCountryDetection() {
    val provider = CountryConfigProviderImpl()
    val country = provider.detectCountry(context)

    assertTrue(country.length == 2)
    assertTrue(country in listOf("KR", "US", "JP", "CN", "US"))
}
```

## Common Patterns

### Pattern 1: Apply Settings to Decision Engine

```kotlin
viewModelScope.launch {
    combine(
        settingsRepository.settings,
        countryConfigProvider.detectCountry(context)
    ) { settings, detectedCountry ->
        val country = settings.countryOverride ?: detectedCountry
        countryConfigProvider.getConfig(country)
    }.collect { config ->
        decisionEngine.updateConfig(config)
    }
}
```

### Pattern 2: Show Paywall if Not Subscribed

```kotlin
when (subscriptionState) {
    SubscriptionState.NotPurchased -> {
        PaywallScreen()
    }
    SubscriptionState.Active -> {
        MainAppContent()
    }
    else -> {
        LoadingScreen()
    }
}
```

### Pattern 3: Respect User Evidence Display Preference

```kotlin
val displayLevel = settings.evidenceDisplayLevel
when (displayLevel) {
    "minimal" -> {
        // Show only conclusion + risk level
        ConclusionText(result)
        RiskBadge(result.riskLevel)
    }
    "normal" -> {
        // Show conclusion + risk + top 3 reasons
        ConclusionText(result)
        RiskBadge(result.riskLevel)
        ReasonsList(result.reasons.take(3))
    }
    "detailed" -> {
        // Show everything
        ConclusionText(result)
        RiskBadge(result.riskLevel)
        ReasonsList(result.reasons)
        ExpandableDetailSection(result.evidence)
    }
}
```

## Configuration

### Enable/Disable Modules

Each module is independently injectable via Hilt. To disable a feature:

```kotlin
// Remove from settings.gradle.kts
// include(":feature:billing")

// Or make conditional in app's AndroidManifest.xml
<activity
    android:name=".PaywallActivity"
    android:enabled="false" />
```

### Add New Search Provider

```kotlin
// In CountryConfigProviderImpl
private fun createCustomConfig(): CountryConfig {
    return CountryConfig(
        countryCode = "XX",
        language = "xx",
        phonePrefix = "+XX",
        searchProviderPriority = listOf(
            "custom-provider",  // Add here
            "google",
            "truecaller"
        ),
        keywordDictionary = ...,
        uiStrings = ...
    )
}
```

### Customize Paywall

```kotlin
// PaywallScreen is fully modular
// Change pricing:
Text(text = "$2/month")

// Change value props:
ValuePropositionItem(
    icon = Icons.Default.Custom,
    title = "Custom Feature",
    description = "Custom description"
)

// Add/remove sections as needed
```

## Performance Considerations

### Billing Manager
- Async operations via Coroutines
- Singleton pattern prevents multiple connections
- Automatic cleanup on ViewModel clear

### Settings Repository
- SharedPreferences (fast, simple)
- StateFlow emissions only on changes
- No expensive I/O in main thread

### Country Config
- In-memory configs (no network)
- Efficient string matching with Sets
- Lazy detection (only on demand)

## Troubleshooting

### Billing Not Connecting
```
Check:
1. BillingManager.initialize() called
2. Google Play Services installed on device
3. Test account in Play Console Testers
4. Billing permission in manifest
5. Play Billing dependency version
```

### Settings Not Persisting
```
Check:
1. SettingsRepository injected (not created directly)
2. Coroutines launched in proper scope
3. SharedPreferences file not disabled
4. Update calls awaited
```

### Country Not Detected
```
Check:
1. SIM card installed (for SIM detection)
2. Network connectivity (for network detection)
3. System locale set correctly
4. Fallback to US working
```

### Theme Not Applied
```
Check:
1. MyPhoneCheckTheme wrapping Composable
2. LocalMyPhoneCheckColorScheme provided
3. No MaterialTheme overriding colors
4. Text styles using MyPhoneCheckTheme.typography
```

## Next Steps

1. **Add Google Play Product**: Configure "myphonecheck_monthly" in Play Console
2. **Test Billing**: Use test cards in development
3. **Deploy Settings**: Settings screen in main navigation
4. **Integrate Keywords**: Use CountryConfig in DecisionEngine
5. **Localize Strings**: Add more UiStrings for future countries
6. **Monitor Analytics**: Track subscription rates, settings changes
