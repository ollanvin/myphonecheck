# DecisionEngine Implementation

## Overview

The DecisionEngine is the core evaluation system of the MyPhoneCheck Android app. It analyzes incoming calls using device evidence (call history, contacts) and search evidence (web results, threat indicators) to provide risk assessment and actionable recommendations.

## Architecture

### Three-Axis Scoring System

The engine evaluates calls across three independent scoring dimensions:

#### 1. Relationship Score (0.0 - 1.0)
Measures how well-known and trusted a contact is based on device evidence.

**Scoring inputs:**
- Saved contact: +1.0 (immediate classification)
- Call frequency >= 3: +0.4
- Call frequency >= 1: +0.2
- SMS/message history: +0.15
- Recent contact (within 7 days): +0.15
- Older contact (within 30 days): +0.1
- Longer average call duration (>60s): +0.1

**Use case:** "This caller is familiar and trustworthy"

#### 2. Risk Score (0.0 - 1.0)
Measures how likely the call is spam, scam, fraud, or harassment based on negative signals.

**Device-based signals:**
- No history at all: +0.2
- User marked as spam: +0.3
- Already blocked: +0.4

**Search-based signals:**
- SCAM indicators: +0.4
- FRAUD indicators: +0.35
- SPAM indicators: +0.3
- FINANCE/LOAN keywords: +0.25
- TELEMARKETING: +0.2
- HARASSMENT: +0.3
- Negative sentiment: +0.15
- High enrichment score: +0.1

**Use case:** "This caller is likely malicious"

#### 3. Category Classification
Assigns one of 11 conclusion categories using a priority decision tree:

1. **SAFE_KNOWN** - Saved contact
2. **FRAUD_WARNING** - Scam/fraud risk (risk > 0.6)
3. **SCAM_ALERT** - General scam/threat alert (risk > 0.6)
4. **TELEMARKETING** - Telemarketing indicators detected
5. **HARASSMENT** - Harassment/threat indicators detected
6. **SPAM_LIKELY** - Likely spam (risk 0.3-0.6)
7. **SAFE_BUSINESS** - Business contact with communication history
8. **SAFE_ORGANIZATION** - Known institution/business
9. **SUSPICIOUS_UNKNOWN** - Low risk but unknown
10. **UNVERIFIABLE** - Cannot verify caller
11. **UNKNOWN** - Insufficient evidence

### Confidence Calculation

Confidence (0.0 - 1.0) reflects assessment reliability:

| Evidence | Base Confidence | Conditions |
|----------|-----------------|------------|
| Both device + search | 0.85f | Signals aligned: +0.1; conflicting: -0.15 |
| Device only | 0.75f | High relationship (>0.7): +0.1 |
| Search only | 0.65f | High risk (>0.6): +0.1 |
| No evidence | 0.30f | - |
| **Category boost** | | |
| SAFE_KNOWN | 0.95f | Highest confidence |
| FRAUD_WARNING | 0.80f | High risk is more reliable |
| SCAM_ALERT | 0.75f | - |
| UNKNOWN | 0.40f | Lowest confidence |

## File Structure

```
feature/decision-engine/
├── src/main/kotlin/app/callcheck/mobile/feature/decisionengine/
│   ├── DecisionEngine.kt              # Interface
│   ├── DecisionEngineImpl.kt           # Core implementation (multi-axis scoring)
│   ├── RiskBadgeMapper.kt             # Risk score → RiskLevel enum
│   ├── ActionMapper.kt                # Category + Risk → UserAction
│   ├── SummaryGenerator.kt            # Localized summaries and reasons
│   └── di/
│       └── DecisionEngineModule.kt    # Hilt dependency injection
├── src/test/kotlin/app/callcheck/mobile/feature/decisionengine/
│   └── DecisionEngineImplTest.kt      # Comprehensive unit tests
└── build.gradle.kts
```

## Usage

### Dependency Injection

The DecisionEngine is provided as a singleton via Hilt:

```kotlin
class MyViewModel @Inject constructor(
    private val decisionEngine: DecisionEngine
) : ViewModel() {
    fun evaluateCall(
        phoneNumber: String,
        deviceEvidence: DeviceEvidence?,
        searchEvidence: SearchEvidence?
    ) {
        val result = decisionEngine.evaluate(
            phoneNumber = phoneNumber,
            deviceEvidence = deviceEvidence,
            searchEvidence = searchEvidence
        )
        // Use result.riskLevel, actionRecommendation, etc.
    }
}
```

### Evaluation Result

```kotlin
val result: DecisionResult = decisionEngine.evaluate(
    phoneNumber = "+1234567890",
    deviceEvidence = deviceEvidence,
    searchEvidence = searchEvidence
)

// Access results
println(result.riskLevel)                      // RiskLevel.HIGH
println(result.riskScore)                      // 0.65f
println(result.conclusionCategory)             // SCAM_ALERT
println(result.actionRecommendation.primaryAction)  // BLOCK
println(result.actionRecommendation.confidenceScore) // 0.75f
```

## Localization

SummaryGenerator supports:
- **English** (default): "Scam/fraud risk high"
- **Korean**: "스팸/사기 위험 높음"

Generate summaries:

```kotlin
val summary = summaryGenerator.generateSummary(
    category = result.conclusionCategory,
    riskLevel = result.riskLevel,
    language = "ko"  // "en" for English
)

val reasons = summaryGenerator.generateReasons(
    category = result.conclusionCategory,
    deviceEvidence = result.deviceEvidence,
    searchEvidence = result.searchEvidence,
    language = "ko"
)
```

## Edge Cases Handled

1. **Both evidence sources null**
   - Creates empty defaults
   - Returns UNKNOWN category with low confidence
   - Recommendation: MUTE or DO_NOTHING

2. **Only device evidence**
   - Uses call history and contact status
   - Confidence: 0.65-0.85f
   - Handles recent vs. old contacts

3. **Only search evidence**
   - Relies on threat indicators and trends
   - Confidence: 0.65-0.75f
   - High risk searches are more reliable

4. **Conflicting signals**
   - Device history good, search results bad (or vice versa)
   - Reduces confidence to 0.70f
   - Errs on side of caution

5. **Multiple risk factors**
   - Risk score accumulates (capped at 1.0)
   - Each indicator adds incrementally
   - Example: SCAM + FRAUD + HIGH_ENRICHMENT = CRITICAL

6. **No history + no search results**
   - Defaults to UNKNOWN classification
   - Confidence: 0.3f (very low)
   - Recommendation: MUTE (safest default)

## Testing

Run comprehensive unit tests:

```bash
./gradlew feature:decision-engine:test
```

Test coverage includes:
- Saved contacts (100% confidence)
- Scam detection (high risk signals)
- Spam/telemarketing classification
- Business contact recognition
- Organization/institution detection
- Conflicting signal handling
- Edge cases (null inputs, empty evidence)
- Korean/English localization
- Risk score mapping to RiskLevel
- Confidence calculations

## Integration Points

### With DeviceEvidenceRepository
```kotlin
val deviceEvidence = deviceEvidenceRepository.getEvidenceFor(phoneNumber)
```

### With SearchEnrichmentRepository
```kotlin
val searchEvidence = searchEnrichmentRepository.enrichPhoneNumber(phoneNumber)
```

### With CallInterceptor
```kotlin
val decision = decisionEngine.evaluate(
    phoneNumber,
    deviceEvidence,
    searchEvidence
)

if (decision.riskLevel == RiskLevel.CRITICAL) {
    callInterceptor.blockCall(phoneNumber)
}
```

## Performance Characteristics

- **Evaluation time:** < 50ms (no I/O, local scoring)
- **Memory:** ~2-5KB per evaluation
- **Singleton instance:** ~1KB (reused across app)
- **Thread-safe:** No mutable state, @Inject constructor

## Future Enhancements

1. **ML-based risk scoring** - Replace rule-based with trained model
2. **Temporal patterns** - Weight recent calls higher
3. **Network analysis** - Detect call spoofing clusters
4. **User feedback loop** - Improve scores from user corrections
5. **Country-specific rules** - MyPhoneCheck supports global markets
6. **Personalization** - Per-user risk thresholds
7. **Explainability** - Detailed score breakdown UI

## References

- **Models:** `core/model/src/main/kotlin/app/callcheck/mobile/core/model/`
- **Device Evidence:** `feature/device-evidence/`
- **Search Enrichment:** `feature/search-enrichment/`
- **Hilt DI:** `feature/decision-engine/src/main/kotlin/.../di/`
