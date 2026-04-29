# DO_NOT_MISS User Action Implementation

## Overview

Users can now mark a number as DO_NOT_MISS to ensure the next incoming call or SMS reflects maximum importance (ImportanceLevel.DO_NOT_MISS).

## How It Works

### Data Model

**NumberProfileEntity** (already existing):
- `doNotMissFlag: Boolean` — Tracks if number is marked as DO_NOT_MISS
- `blockState: String` — Maps to NumberProfileBlockState (NONE, BLOCKED, DO_NOT_BLOCK)

**When user marks a number as DO_NOT_MISS:**
1. `NumberProfileRepository.toggleDoNotMiss(number)` is called
2. This toggles the blockState between NONE and DO_NOT_BLOCK
3. DO_NOT_BLOCK state becomes ActionState.DO_NOT_BLOCK in the decision engine
4. Decision engine returns ImportanceLevel.DO_NOT_MISS for next call/SMS

### Rule Priority
The decision engine checks DO_NOT_BLOCK first (highest priority):
```
if (actionState == ActionState.DO_NOT_BLOCK)
    → ImportanceLevel.DO_NOT_MISS
```

This ensures marked numbers are always shown as DO_NOT_MISS regardless of other signals.

## Implementation Details

### Repository Layer

**NumberProfileRepository** (`data/local-cache/src/.../NumberProfileRepository.kt`)

New method:
```kotlin
suspend fun toggleDoNotMiss(normalizedNumber: String)
```

- Gets current blockState
- Toggles between DO_NOT_BLOCK (when enabled) and NONE (when disabled)
- Updates NumberProfile and associated labels

### View Model Layer

**MessageHubViewModel** (`app/src/main/kotlin/.../MessageHubViewModel.kt`)

New method:
```kotlin
fun toggleDoNotMiss(number: String) {
    viewModelScope.launch {
        numberProfileRepository.toggleDoNotMiss(number)
    }
}
```

Exposes repository action via UI-friendly coroutine scope.

### Action Receiver Layer

**CallActionReceiver** (`feature/call-intercept/src/.../CallActionReceiver.kt`)

New action handler:
```kotlin
private fun handleMarkDoNotMissAction(context: Context, phoneNumber: String)
```

Processes broadcast intent with action `"action_mark_do_not_miss"`:
- Calls `toggleDoNotMiss()` on the repository
- Does NOT dismiss notification (user can perform other actions)
- Logs the action

## Usage Paths

### Path 1: Message/SMS UI
Users can toggle DO_NOT_MISS via message view:
```kotlin
// In message UI or activity
messageHubViewModel.toggleDoNotMiss(number)
```

This is the primary UI path for managing DO_NOT_MISS status.

### Path 2: Broadcast Intent (Notification/Overlay)
From notification or overlay:
```kotlin
val intent = Intent(context, CallActionReceiver::class.java).apply {
    action = "action_mark_do_not_miss"
    putExtra(EXTRA_PHONE_NUMBER, phoneNumber)
}
context.sendBroadcast(intent)
```

### Path 3: Direct Repository Call (Programmatic)
From any background service or worker:
```kotlin
numberProfileRepository.toggleDoNotMiss(number)
```

## UI Integration Status

### Message UI ✅
Ready to wire the `toggleDoNotMiss()` action from:
- Quick action buttons in message list
- Detail view menu
- Long-press context menu

### Call Overlay (Optional)
Could add a 4th button, but currently minimal change (no modifications).

### Notification (Optional)
Could add as 4th action button in notification, but currently minimal change.

## Data Persistence

State is automatically persisted to Room database:
- `NumberProfileEntity.blockState` updated in `number_profiles` table
- Synced with `QuickLabel.DO_NOT_BLOCK` for UI consistency
- Survives app restart and background kill

## Next Incoming Call/SMS Flow

```
Next Call/SMS arrives
  ↓
CallInterceptRepository.analyzeIdentifier()
  ↓
DecisionEngine.evaluate()
  ├─ Fetches ActionState from NumberProfile
  ├─ IF actionState == DO_NOT_BLOCK:
  │    → ImportanceLevel.DO_NOT_MISS
  │    → reason: "action_state_do_not_block"
  └─ Returns DecisionResult with importance
  ↓
CallerIdOverlayManager.showOverlay()
  └─ Displays: "Importance  DO_NOT_MISS (action_state_do_not_block)"
  ↓
DecisionNotificationManager.showDecisionNotification()
  └─ Shows notification with importance context
```

## Backward Compatibility

- Existing users: `doNotMissFlag` defaults to false (no action)
- Existing code: No breaking changes to ActionState enum
- Schema: Already supports the field (database v8 and v9+)

## Testing Checklist

- [ ] Wire toggleDoNotMiss() in message UI
- [ ] Test toggle: disabled → enabled → disabled
- [ ] Verify blockState changes in database
- [ ] Next call/SMS shows DO_NOT_MISS importance
- [ ] Overlay displays importance correctly
- [ ] Broadcast intent action works from receiver
- [ ] Persist and survive app restart

## Future Enhancements

1. **UI Affordance**
   - Add "★ Mark as DO_NOT_MISS" button/menu item in message UI
   - Visual indicator when number is marked (star icon)
   - Toast/snackbar confirmation

2. **Notification Enhancement**
   - Add 4th notification action button
   - Customize notification priority/badge when DO_NOT_MISS

3. **Analytics**
   - Track how many users mark as DO_NOT_MISS
   - Correlate with user actions afterward

4. **Smart Suggestions**
   - Auto-suggest DO_NOT_MISS for: family, VIP customers, work contacts
   - ML model to predict "should not miss" contacts

## Files Modified

1. **Repository Layer**
   - `data/local-cache/src/.../NumberProfileRepository.kt` — Added `toggleDoNotMiss()` method

2. **ViewModel Layer**
   - `app/src/main/kotlin/.../MessageHubViewModel.kt` — Added `toggleDoNotMiss()` method

3. **Broadcast Receiver Layer**
   - `feature/call-intercept/src/.../CallActionReceiver.kt` — Added `handleMarkDoNotMissAction()` handler

4. **No Model Changes** (infrastructure already exists)
   - `core/model/ActionState.kt` — No change (DO_NOT_BLOCK already exists)
   - `data/local-cache/.../NumberProfileEntity.kt` — No change (doNotMissFlag already exists)
   - `data/local-cache/.../NumberProfileBlockState.kt` — No change (DO_NOT_BLOCK already exists)

## Usage Example

### In Message UI ViewModel or Activity:
```kotlin
// Toggle DO_NOT_MISS for a number
fun onMarkDoNotMissClicked(number: String) {
    messageHubViewModel.toggleDoNotMiss(number)
    showToast("DO_NOT_MISS updated")
}

// In message list item click listener
messageItem.setOnMarkDoNotMissListener {
    messageHubViewModel.toggleDoNotMiss(it.senderNumber)
}
```

### In Message Detail Screen:
```kotlin
// Show/hide DO_NOT_MISS indicator based on profile
val isDoNotMiss = numberProfileSnapshot?.actionState == ActionState.DO_NOT_BLOCK
if (isDoNotMiss) {
    showIndicator("⭐ This is marked DO_NOT_MISS")
}

// Button to toggle
markDoNotMissButton.setOnClickListener {
    messageHubViewModel.toggleDoNotMiss(number)
    updateIndicator()
}
```

## Minimal Change Philosophy

This implementation:
- ✅ Adds zero dependencies
- ✅ Reuses existing data structures (NumberProfile, ActionState, BlockState)
- ✅ No UI redesign (UI wiring is optional)
- ✅ No architecture changes
- ✅ Available immediately for programmatic use (repositories, services)
- ✅ Integrates seamlessly with existing decision engine
