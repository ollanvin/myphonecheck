# MyPhoneCheck Call Interception Implementation

## Overview

This document describes the complete implementation of the CallScreeningService for MyPhoneCheck Android app. The service intercepts incoming calls and provides real-time risk assessment with user-friendly notifications and actions.

## Architecture

### Core Components

#### 1. MyPhoneCheckScreeningService
**File:** `MyPhoneCheckScreeningService.kt`

The main Android system service that intercepts incoming calls. Extends `android.telecom.CallScreeningService`.

**Responsibilities:**
- Receives incoming call notifications from Android's telecom framework
- Extracts phone number from Call.Details
- Normalizes phone numbers using PhoneNumberNormalizer
- Filters emergency numbers (911, 112, 119) and private callers
- Orchestrates the decision pipeline with 5-second timeout
- Makes screening decisions (allow/reject/block)
- Shows decision notifications

**Key Methods:**
- `onScreenCall(callDetails: Call.Details)` - Entry point for incoming calls
- `processIncomingCall()` - Main decision pipeline
- `processPrivateCall()` - Handles private/blocked caller scenarios
- `makeScreeningDecision()` - Translates risk level to Android screening action
- `respondAllowCall()` / `respondRejectCall()` - Sends screening response to system

**Edge Cases Handled:**
- Null/blank phone numbers (allows call)
- Emergency numbers (always allows)
- Private/blocked caller IDs (triggers decision pipeline)
- Timeout (allows call with timeout notification)
- Exceptions (fail-safe allows call)

**Android Constraints:**
- Must respond within 5 seconds (strict Android requirement)
- Uses coroutines with explicit timeout
- Service scope managed with SupervisorJob for safe cancellation

---

#### 2. CallInterceptRepository (Interface)
**File:** `CallInterceptRepository.kt`

Defines the contract for the complete call intercept pipeline.

**Key Method:**
```kotlin
suspend fun processIncomingCall(context: IncomingNumberContext): DecisionResult
```

Orchestrates:
1. Device evidence gathering (1s timeout)
2. Search enrichment (3s timeout, gracefully degrades on timeout)
3. Decision engine execution

Returns full DecisionResult with risk level and recommendation.

---

#### 3. CallInterceptRepositoryImpl
**File:** `CallInterceptRepositoryImpl.kt`

Production implementation of CallInterceptRepository.

**Key Features:**
- **Parallel Execution**: Launches device evidence and search tasks concurrently
- **Progressive Results**: Device evidence prioritized; search results optional
- **Timeout Handling**: Each component has independent timeout
  - Device evidence: 1s timeout
  - Search evidence: 3s timeout (gracefully degrades)
  - Overall service: 5s timeout
- **Graceful Degradation**: Returns safe decision if any component fails
- **Proper Error Handling**: Catches all exceptions and returns defaults

**Flow:**
```
Device Evidence (1s timeout)    ─┐
                                  ├─→ Decision Engine → DecisionResult
Search Evidence (3s timeout)    ─┘
                    (gracefully times out)
```

---

#### 4. DecisionNotificationManager
**File:** `DecisionNotificationManager.kt`

Manages showing decision notifications as system overlays.

**Responsibilities:**
- Creates notification channel (Android 8+)
- Builds rich notifications with decision info and actions
- Handles notification actions via PendingIntent
- Manages notification lifecycle

**Notification Features:**
- Title: Risk level (e.g., "High Risk", "Safe Contact")
- Body: Conclusion category + risk level + confidence score
- Color-coded: SAFE=green, LOW=orange, MEDIUM=yellow, HIGH=red, CRITICAL=dark red
- Action Buttons: Answer, Reject, Block
- Big text style for detailed information
- Heads-up notification (high priority)

**Methods:**
- `showDecisionNotification()` - Display main decision notification
- `showTimeoutNotification()` - Display timeout notification
- `dismissNotification()` - Remove notification from user

---

#### 5. CallActionReceiver
**File:** `CallActionReceiver.kt`

BroadcastReceiver handling notification action buttons.

**Actions Handled:**
- `ACTION_ANSWER` - Log that user answered despite warning
- `ACTION_REJECT` - Log that user rejected the call
- `ACTION_BLOCK` - Add number to blocklist and dismiss

**Implementation Details:**
- Uses `@AndroidEntryPoint` for Hilt dependency injection
- Manages blocklist operations asynchronously
- Logs user overrides for analytics
- Safe error handling with proper logging

---

#### 6. BlocklistRepository (Interface)
**File:** `BlocklistRepository.kt`

Contract for managing blocked phone numbers.

**Methods:**
- `addToBlocklist()` - Add number with reason and timestamp
- `isBlocked()` - Check if number is blocked
- `removeFromBlocklist()` - Unblock a number
- `getBlockedNumbers()` - Retrieve all blocked numbers

---

#### 7. Support Interfaces

**DeviceEvidenceProvider** (`DeviceEvidenceProvider.kt`)
- Gathers local evidence: contacts, call history, blocklist, spam reports
- Must complete within 1 second

**SearchEvidenceProvider** (`SearchEvidenceProvider.kt`)
- Performs web search for threat indicators: spam reports, fraud warnings, reviews
- Must complete within 3 seconds, gracefully degrades on timeout

**DecisionEngine** (`DecisionEngine.kt`)
- Generates risk scoring and recommendations
- Combines device + search evidence
- Produces DecisionResult with confidence score

---

## Hilt Dependency Injection

**File:** `di/CallInterceptModule.kt`

Provides bindings for the CallInterceptRepository:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class CallInterceptModule {
    @Binds
    @Singleton
    abstract fun bindCallInterceptRepository(
        impl: CallInterceptRepositoryImpl
    ): CallInterceptRepository
}
```

**Important:** Services with CallScreeningService base class use `@AndroidEntryPoint` annotation for Hilt support.

---

## Manifest Configuration

### AndroidManifest.xml Updates

```xml
<!-- Call Screening Service (Android 10+) -->
<service
    android:name=".feature.callintercept.MyPhoneCheckScreeningService"
    android:exported="true"
    android:permission="android.permission.BIND_SCREENING_SERVICE">
    <intent-filter>
        <action android:name="android.telecom.InCallService" />
        <action android:name="android.telecom.CallScreeningService" />
    </intent-filter>
</service>

<!-- Call Action Receiver for notification actions -->
<receiver
    android:name=".feature.callintercept.CallActionReceiver"
    android:exported="true">
    <intent-filter>
        <action android:name="action_answer" />
        <action android:name="action_reject" />
        <action android:name="action_block" />
    </intent-filter>
</receiver>
```

### Required Permissions
Already present in manifest:
- `READ_PHONE_STATE` - Detect incoming calls
- `READ_CALL_LOG` - Check call history
- `ANSWER_PHONE_CALLS` - Programmatic call control
- `MODIFY_PHONE_STATE` - System call control
- `READ_CONTACTS` - Check contact list
- `READ_SMS` - Optional: message history
- `POST_NOTIFICATIONS` - Show notifications (Android 13+)
- `INTERNET` - Web search
- `VIBRATE` - Notification vibration

---

## Call Flow Sequence

```
1. Android System detects incoming call
   ↓
2. MyPhoneCheckScreeningService.onScreenCall() invoked
   ↓
3. Extract and normalize phone number
   ├─→ Emergency number? → ALLOW (safe path)
   ├─→ Private number? → processPrivateCall()
   └─→ Normal number? → processIncomingCall()
   ↓
4. Launch parallel tasks (with timeouts):
   ├─→ Device evidence (1s timeout)
   └─→ Search evidence (3s timeout)
   ↓
5. Run Decision Engine with results
   ↓
6. Make screening decision (allow/reject/block)
   ↓
7. Show notification with decision + actions
   ↓
8. User chooses action
   ↓
9. CallActionReceiver handles action
   ├─→ Answer: Log override
   ├─→ Reject: Log override
   └─→ Block: Add to blocklist, log override
```

---

## Timeout Strategy

| Component | Timeout | Behavior |
|-----------|---------|----------|
| Device Evidence | 1s | Completes synchronously or returns defaults |
| Search Evidence | 3s | Optional; degrades gracefully if timeout |
| Overall Pipeline | 5s | Hard Android limit; allows call if exceeded |
| Service Response | 5s | Must respond to Android before timeout |

---

## Risk Level Mapping

| Risk Level | User Action | Color | Notification |
|------------|-------------|-------|--------------|
| SAFE | ANSWER | Green | "Safe Contact" |
| LOW | DO_NOTHING | Orange | "Low Risk" |
| MEDIUM | MUTE | Yellow | "Medium Risk" |
| HIGH | REJECT | Red | "High Risk" |
| CRITICAL | BLOCK | Dark Red | "Critical Risk" |

---

## Error Handling Strategy

### Phone Number Processing
- Null/blank → Allow call (safe)
- Invalid format → Attempt normalization
- Normalization fails → Pass raw number to pipeline
- All errors logged with context

### Evidence Gathering
- Device evidence failure → Return defaults, continue
- Search evidence timeout → Use device evidence only
- Search evidence exception → Return defaults, continue

### Decision Engine
- Any exception → Return LOW risk with low confidence
- Timeout → Return LOW risk decision
- Missing data → Use available evidence, low confidence

### Pipeline Timeout
- Exceeds 5s → Immediately respond ALLOW
- Show timeout notification to user
- Log for debugging

### Notification Failures
- Channel creation fails → Try fallback
- Notification send fails → Log, continue
- Action handling fails → Log, no crash

---

## Testing Considerations

### Unit Tests
- Phone number extraction and normalization
- Emergency number detection
- Private number detection
- Timeout handling with coroutine tests
- Risk level to action mapping

### Integration Tests
- Full MyPhoneCheckScreeningService flow
- Notification creation and actions
- Hilt injection setup
- Blocklist operations

### System/Manual Tests
- Actually make incoming calls to test device
- Verify notifications appear
- Test action button responses
- Verify decision logging

---

## Performance Characteristics

- **Memory**: Single coroutine scope per service instance
- **CPU**: Parallel execution minimizes latency
- **Network**: Search evidence made in background
- **Latency**: Target 3s, max 5s response

---

## Security Considerations

- Service requires `BIND_SCREENING_SERVICE` permission (system only)
- Notifications include no sensitive data (just phone number and risk)
- Blocklist stored locally, synced to backend
- All user actions logged for accountability
- Emergency numbers never blocked

---

## Future Enhancements

1. **Machine Learning**: Confidence scoring based on patterns
2. **Community Reports**: Integration with user crowd-sourced reports
3. **Caller Verification**: API integration with verified business numbers
4. **Custom Blocklists**: User-defined rules and whitelist
5. **Statistics**: Per-category call statistics
6. **Internationalization**: Multi-language notifications
7. **A/B Testing**: Different decision strategies by region
8. **VoIP Support**: SIP protocol handling
