# CallScreeningService - Quick Reference

## Files Created

### Core Service
- `MyPhoneCheckScreeningService.kt` - Main Android CallScreeningService implementation

### Repository Pattern
- `CallInterceptRepository.kt` - Interface defining the decision pipeline contract
- `CallInterceptRepositoryImpl.kt` - Complete implementation with parallel execution and timeouts

### Supporting Services
- `DecisionNotificationManager.kt` - Handles notification creation and display
- `CallActionReceiver.kt` - BroadcastReceiver for notification action buttons
- `BlocklistRepository.kt` - Interface for blocklist management

### Evidence Providers (Interfaces)
- `DeviceEvidenceProvider.kt` - Local device evidence (contacts, history, blocklist)
- `SearchEvidenceProvider.kt` - Web search evidence (threat indicators)
- `DecisionEngine.kt` - Risk scoring and recommendation generation

### Hilt DI
- `di/CallInterceptModule.kt` - Provides CallInterceptRepository binding

### Documentation
- `IMPLEMENTATION.md` - Comprehensive architecture and design documentation
- `QUICK_REFERENCE.md` - This file

---

## Integration Checklist

### 1. Manifest Updates ✓
- [x] Updated AndroidManifest.xml with MyPhoneCheckScreeningService
- [x] Updated AndroidManifest.xml with CallActionReceiver
- [x] All required permissions already present

### 2. Hilt Configuration
- [ ] Ensure app uses Hilt (@HiltAndroidApp in Application class)
- [ ] CallInterceptModule automatically loaded by Hilt

### 3. Dependencies to Implement
These are injected but need to be implemented elsewhere:
- [ ] `DeviceEvidenceProvider` - Implement in data layer (contacts, call log, blocklist)
- [ ] `SearchEvidenceProvider` - Implement in data layer (web search API calls)
- [ ] `DecisionEngine` - Implement in feature/decision-engine module
- [ ] `BlocklistRepository` - Implement in data layer (local storage)

### 4. Next Steps
1. Implement the provider interfaces in appropriate modules
2. Create blocking list data layer with Room database
3. Integrate web search API (or use existing service)
4. Set up decision engine scoring algorithm
5. Unit test each component
6. Integration test the full flow
7. Manual testing on physical device

---

## Key Design Decisions

### 1. Timeout Strategy
- Device evidence: 1s (must be fast, local queries)
- Search evidence: 3s (optional, can timeout gracefully)
- Overall service: 5s (Android hard limit)
- Search timeout does NOT fail the pipeline (degrades gracefully)

### 2. Parallel Execution
- Device evidence and search evidence run in parallel
- Decision engine waits for both (with timeouts)
- Reduces latency from sequential execution

### 3. Error Handling
- All exceptions caught and logged
- Defaults to SAFE/LOW risk decision on error
- Fail-safe: always allow call if anything fails
- Notification still shown for transparency

### 4. Notification Actions
- Answer: User ignores warning and takes call
- Reject: User hangs up the call
- Block: User adds number to blocklist
- All actions logged for analytics and learning

### 5. Phone Number Normalization
- Uses libphonenumber for E164 formatting
- Handles emergency numbers specially (always allow)
- Detects private/blocked caller ID
- Gracefully handles parse failures

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│              Android Telecom Framework                       │
│                  (System Level)                              │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ├─ Incoming Call Event
                     │
        ┌────────────▼──────────────┐
        │  MyPhoneCheckScreeningService │
        │   (CallScreeningService)   │
        └────────────┬───────────────┘
                     │
         ┌───────────┴───────────┐
         │  Phone Number         │
         │  Extraction &         │
         │  Normalization        │
         └───────────┬───────────┘
                     │
         ┌───────────┴──────────────────────────┐
         │ Filter: Emergency/Private/Blocked?   │
         └───────────┬──────────────────────────┘
                     │
        ┌────────────▼──────────────────────────┐
        │   CallInterceptRepository              │
        │   (Orchestrator)                       │
        │   - 5s hard timeout                    │
        └────────────┬──────────────────────────┘
                     │
        ┌────────────┴──────────────────────────┐
        │   Parallel Execution                  │
        └────────────┬──────────────────────────┘
                     │
      ┌──────────────┴──────────────┐
      │                             │
      ▼                             ▼
┌──────────────────┐         ┌──────────────────┐
│  Device Evidence │         │ Search Evidence  │
│  Provider        │         │ Provider         │
│  (1s timeout)    │         │ (3s timeout)     │
└──────────┬───────┘         └────────┬─────────┘
           │                         │
      ┌────┴─────────────────────────┴────┐
      │   Decision Engine                 │
      │   - Risk Scoring                  │
      │   - Recommendation Generation     │
      │   - Confidence Calculation        │
      └────┬──────────────────────────────┘
           │
      ┌────▼─────────────────────────────┐
      │   Make Screening Decision        │
      │   - Allow / Reject / Block       │
      └────┬──────────────────────────────┘
           │
      ┌────▼────────────────────────┐
      │ Show Notification            │
      │ - Risk Level Summary         │
      │ - Action Buttons             │
      │ - Color Coded                │
      └────┬─────────────────────────┘
           │
      ┌────▼────────────────────────┐
      │ User Action (Async)          │
      │ - Answer / Reject / Block    │
      │ - CallActionReceiver         │
      └────┬─────────────────────────┘
           │
      ┌────▼────────────────────────┐
      │ Blocklist / Analytics        │
      │ - Add to blocklist           │
      │ - Log decision override      │
      └──────────────────────────────┘
```

---

## Code Example: Integration in App Module

```kotlin
// In app/src/main/kotlin/app/callcheck/mobile/MyPhoneCheckApplication.kt
// (Already using Hilt)

@HiltAndroidApp
class MyPhoneCheckApplication : Application() {
    // Hilt automatically provides all dependencies
    // including CallInterceptRepository via CallInterceptModule
}
```

No additional code needed in Application class - Hilt handles dependency injection automatically.

---

## Testing Strategies

### Unit Tests (Local)
```kotlin
// Test phone number extraction
@Test
fun testEmergencyNumberDetection() { ... }

// Test timeout handling
@Test
fun testDecisionPipelineTimeout() { ... }

// Test risk level mapping
@Test
fun testRiskLevelToAction() { ... }
```

### Integration Tests (Instrumented)
```kotlin
// Test full service with mock providers
@RunWith(AndroidJUnit4::class)
class MyPhoneCheckScreeningServiceTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: CallInterceptRepository
    // Test full flow
}
```

### Manual Testing
1. Set MyPhoneCheck as default call screening app
   - Settings > Apps > Default apps > Call screening
2. Make test calls to your device
3. Verify notifications appear
4. Test action button responses
5. Check blocklist persistence

---

## Common Issues & Solutions

### Issue: Service not receiving calls
- Solution: Set MyPhoneCheck as default call screening app in settings

### Issue: Notifications not appearing
- Solution: Check notification channel created, check POST_NOTIFICATIONS permission granted

### Issue: Timeout occurring frequently
- Solution: Optimize DeviceEvidenceProvider/SearchEvidenceProvider performance

### Issue: High memory usage
- Solution: Check for coroutine leaks, ensure proper scope cancellation

### Issue: Phone numbers not normalizing
- Solution: Ensure libphonenumber dependency is included and up-to-date

---

## Performance Targets

| Metric | Target | Hard Limit |
|--------|--------|-----------|
| Overall Decision Time | 3s | 5s |
| Device Evidence | <1s | 1s |
| Search Evidence | <3s | 3s |
| Notification Display | <100ms | N/A |
| Memory per Service | <5MB | N/A |
| CPU per Call | <10% | N/A |

---

## Security Notes

- Service protected by `BIND_SCREENING_SERVICE` permission (system only)
- Notifications contain no sensitive data
- Blocklist stored locally, encrypted at rest
- All user actions logged for accountability
- Emergency numbers never blocked or filtered

---

## Future Enhancements

- [ ] Machine learning for confidence scoring
- [ ] Community crowd-sourced reports integration
- [ ] Verified business number API
- [ ] Custom user-defined rules
- [ ] Per-category statistics
- [ ] Multi-language support
- [ ] VoIP support
- [ ] Backend sync for blocklist
