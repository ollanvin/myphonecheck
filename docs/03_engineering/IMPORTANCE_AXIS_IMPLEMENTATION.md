# Importance Axis Implementation

## Overview

The Importance Axis has been successfully introduced and integrated into the MyPhoneCheck decision engine. This axis is **independent from risk assessment** and determines how critical it is for the user to not miss an interaction.

### Core Concept

- **Risk Axis**: How dangerous is this interaction? (0.0–1.0)
- **Relationship Axis**: How well-known is this contact? (0.0–1.0)
- **Importance Axis**: How much should the user not miss this? (UNKNOWN, NORMAL, IMPORTANT, DO_NOT_MISS)

## Implementation Status

### ✅ Completed

1. **ImportanceLevel Enum** (`core/model/src/main/kotlin/.../ImportanceLevel.kt`)
   - `UNKNOWN` - No importance rule matched
   - `NORMAL` - Repeated interaction (3–7 interactions)
   - `IMPORTANT` - High-value interactions (8+ calls/texts OR saved contact)
   - `DO_NOT_MISS` - User explicitly marked DO_NOT_BLOCK

2. **DecisionResult Extension** (`core/model/src/main/kotlin/.../DecisionResult.kt`)
   - Added field: `importanceLevel: ImportanceLevel = ImportanceLevel.UNKNOWN`
   - Added field: `importanceReason: String = ""`
   - Updated fallback: Provides safe defaults

3. **Decision Engine Logic** (`feature/decision-engine/src/main/kotlin/.../DecisionEngineImpl.kt`)
   - `determineImportance()` method evaluates rules in priority order:
     1. **DO_NOT_BLOCK Action State** → `DO_NOT_MISS`
     2. **Saved Contact** → `IMPORTANT`
     3. **Repeated Interaction** (≥8) → `IMPORTANT` with reason
     4. **Repeated Interaction** (≥3) → `NORMAL` with reason
     5. **Default** → `UNKNOWN`

4. **Call Flow Integration** (`feature/call-intercept/...`)
   - `MyPhoneCheckScreeningService` receives `DecisionResult` with `importanceLevel`
   - Passes to `CallerIdOverlayManager.showOverlay()`
   - Passed to `DecisionNotificationManager.showDecisionNotification()`

5. **SMS Flow Integration** (`feature/message-intercept/...`)
   - `SmsInterceptReceiver` evaluates importance through decision engine
   - Stores `importanceLevel` and `importanceReason` in `MessageHubEntity`
   - Available in message UI and logs

6. **UI Exposure**

   **Overlay Manager** (`feature/call-intercept/.../CallerIdOverlayManager.kt`)
   - `PendingPromptContext` includes `importanceLevel` and `importanceReason`
   - `buildOverlayView()` displays importance if not `UNKNOWN`:
     ```kotlin
     if (result.importanceLevel != ImportanceLevel.UNKNOWN) {
         addView(TextView(context).apply {
             text = "Importance  ${result.importanceLevel.name} (${result.importanceReason})"
             setTextColor(Color.parseColor("#B39DDB"))
             setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
             gravity = Gravity.CENTER_HORIZONTAL
             layoutParams = marginTop(context, 2)
         })
     }
     ```

   **Message Storage** (`data/local-cache/.../MessageHubEntity.kt`)
   - Fields: `importanceLevel: String = "UNKNOWN"`
   - Fields: `importanceReason: String? = null`
   - Persisted in local database schema (v9+)

   **Activity Integration** (`app/src/main/kotlin/.../NonContactQuickLabelActivity.kt`)
   - Receives `importanceLevel` and `importanceReason` from intent extras
   - Available for UI display and processing

## Importance Rules

### Rule 1: User-Marked DO_NOT_BLOCK
```
If actionState == DO_NOT_BLOCK
  → ImportanceLevel.DO_NOT_MISS
  Reason: "action_state_do_not_block"
```
**Rationale**: User explicitly wants to not miss this contact.

### Rule 2: Saved Contact
```
If deviceEvidence.isSavedContact == true
  → ImportanceLevel.IMPORTANT
  Reason: "saved_contact"
```
**Rationale**: Saved contacts deserve prioritized attention.

### Rule 3: High Repeated Interaction
```
If totalInteractions (calls + SMS) >= REPEATED_IMPORTANT_THRESHOLD (8)
  → ImportanceLevel.IMPORTANT
  Reason: "repeated_interaction_high($count)"
```
**Rationale**: Frequent contacts are likely important to the user.

### Rule 4: Medium Repeated Interaction
```
If totalInteractions (calls + SMS) >= REPEATED_NORMAL_THRESHOLD (3)
  → ImportanceLevel.NORMAL
  Reason: "repeated_interaction($count)"
```
**Rationale**: Some communication history indicates relevance.

### Rule 5: Default
```
Else
  → ImportanceLevel.UNKNOWN
  Reason: "no_importance_rule_matched"
```

## Data Flow

### Call Lifecycle
```
MyPhoneCheckScreeningService.onScreenCall()
  ↓
CallInterceptRepository.analyzeIdentifier()
  ↓
DecisionEngine.evaluate()
  ├─ calculateRelationshipScore()
  ├─ calculateRiskScore()
  ├─ determineCategory()
  ├─ determineImportance() ← NEW
  └─ return DecisionResult {
       importanceLevel, importanceReason, ...
     }
  ↓
CallerIdOverlayManager.showOverlay(result)
  └─ buildOverlayView() displays importance if not UNKNOWN
  ↓
DecisionNotificationManager.showDecisionNotification(result)
```

### SMS Lifecycle
```
SmsInterceptReceiver.onReceive()
  ↓
CallInterceptRepository.analyzeIdentifier()
  ↓
DecisionEngine.evaluate() → returns DecisionResult with importance
  ↓
MessageHubEntity.insert() {
  importanceLevel = result.importanceLevel.name
  importanceReason = result.importanceReason
}
  ↓
Message UI reads from MessageHubEntity
  └─ NonContactQuickLabelActivity receives importance in intent extras
```

## Testing

### Unit Tests Added
(`feature/decision-engine/src/test/.../DecisionEngineImplTest.kt`)

5 new test cases ensure importance logic correctness:

1. **savedContact_returnsImportanceImportant**
   - Saved contacts always get IMPORTANT
   - Validates reason = "saved_contact"

2. **noEvidence_returnsImportanceUnknown**
   - No evidence → UNKNOWN
   - Validates safe default behavior

3. **repeatedInteractionAboveImportantThreshold_returnsImportanceImportant**
   - 8+ interactions → IMPORTANT
   - Validates high interaction detection

4. **repeatedInteractionAboveNormalThreshold_returnsImportanceNormal**
   - 3–7 interactions → NORMAL
   - Validates medium interaction detection

5. **doNotBlockActionState_returnsImportanceDoNotMiss**
   - DO_NOT_BLOCK action state → DO_NOT_MISS
   - Validates user-explicit importance

All tests validate both `importanceLevel` and `importanceReason` fields.

## Architecture Notes

### No UI Redesign
- Overlay and notification UI remain unchanged structurally
- Importance is an **additional visual hint** (purple text, 11sp)
- Existing three-button layout (Answer/Reject/Block) intact
- No new screens or dialogs added

### Backward Compatibility
- DecisionResult fields have safe defaults
- Fallback decision provides valid importance defaults
- Existing code unaffected by new fields

### Performance
- Importance determination is O(1) — simple logical checks
- No additional external calls or I/O
- Fits within ~50ms decision budget

### Storage (SMS)
- MessageHubEntity schema updated to include importance fields
- Persisted in local database for historical analysis
- Available for future analytics and user preferences

## Usage in UI

### Overlay Display (Call Intercept)
When overlay is shown with a call decision, if importance is not UNKNOWN:
```
🟢 안전한 번호입니다
판정 진행 중...
Importance IMPORTANT (saved_contact)  ← Shows here
```

### Message Storage (SMS Intercept)
When SMS is saved to MessageHub:
```
MessageHubEntity {
  importanceLevel: "IMPORTANT"
  importanceReason: "saved_contact"
  ...
}
```

## Future Enhancements

1. **Importance-Based Notification Priority**
   - DO_NOT_MISS → PRIORITY_MAX, vibration + lights + sound
   - IMPORTANT → PRIORITY_HIGH, vibration
   - NORMAL → PRIORITY_DEFAULT
   - UNKNOWN → PRIORITY_LOW

2. **User Preferences**
   - Allow user to override importance for specific contacts
   - "Mark as Important" / "Mark as Unimportant"
   - Persisted in user profile

3. **Analytics & Learning**
   - Track which importance levels users interact with
   - Feedback loop to refine thresholds
   - Personalized importance scoring

4. **Multi-Language Support**
   - Localize importance reasons for display
   - Integrate with existing SignalSummaryLocalizer

## Files Modified

### Core Model
- `core/model/src/.../ImportanceLevel.kt` — Enum definition
- `core/model/src/.../DecisionResult.kt` — Added fields

### Decision Engine
- `feature/decision-engine/src/.../DecisionEngineImpl.kt` — Added `determineImportance()` logic
- `feature/decision-engine/src/test/.../DecisionEngineImplTest.kt` — Added 5 tests

### Call Flow
- `feature/call-intercept/src/.../CallerIdOverlayManager.kt` — Display in overlay
- `feature/call-intercept/src/.../MyPhoneCheckScreeningService.kt` — Passes to managers

### Message Flow
- `feature/message-intercept/src/.../SmsInterceptReceiver.kt` — Stores importance
- `data/local-cache/src/.../MessageHubEntity.kt` — Added fields
- `data/local-cache/schemas/.../MyPhoneCheckDatabase/9.json` — Schema update

### Activity Integration
- `app/src/main/kotlin/.../NonContactQuickLabelActivity.kt` — Receives importance

## Verification Checklist

- [x] ImportanceLevel enum created with 4 values
- [x] DecisionResult extended with importance fields
- [x] Importance rules implemented (5 rules)
- [x] Call flow integration complete
- [x] SMS flow integration complete
- [x] Overlay displays importance when not UNKNOWN
- [x] Message storage persists importance
- [x] Unit tests added and passing
- [x] Backward compatibility maintained
- [x] Performance within budget
- [x] No UI redesign required
- [x] No architecture changes
- [x] Minimal safe changes
