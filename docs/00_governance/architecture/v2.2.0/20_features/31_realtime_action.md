# 31. Real-time Action Engine

> 신규 v2.1.0. 수신 이벤트(Call/SMS/Push) 발생 시 밀리초 단위 즉시 조치.
> "수신 거절했는데 끊기지 않고 계속 울림" = 앱 가치 0. 본 §은 그 방지를 명문화.

## 31-1. 정의

수신 이벤트 도착 → InputAggregator 4-Layer 즉시 조회 → Action Decision → 즉시 실행.

목표: **모든 수신 처리는 사용자 가시 시각보다 먼저 결정 + 실행**.

## 31-2. Action 종류

| Action | Trigger | 메커니즘 |
|---|---|---|
| **Block** | 차단 목록 매칭 (Layer 2) | CallScreeningService.endCall + SmsReceiver consume |
| **Silent** | 스팸 후보 (Layer 3 매칭) | NotificationManager priority 조정 + 무음 |
| **Tag Display** | 태그 매칭 (Layer 2) | 알림에 태그 라벨 우선 표시 |
| **Label Display** | 사용자 라벨 매칭 (Layer 2) | 알림에 라벨 표시 |
| **Pass** | 매칭 없음 | OS 기본 동작 |

## 31-3. Call 처리

### 31-3-1. CallScreeningService (Android Q+)

```kotlin
class MyPhoneCheckCallScreeningService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        val number = phoneParser.parse(callDetails.handle.schemeSpecificPart)
        val decision = inputAggregator.aggregateForCall(number.e164)

        when (decision.action) {
            BLOCK -> respondToCall(callDetails, CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipNotification(true)
                .build())
            SILENT -> respondToCall(callDetails, CallResponse.Builder()
                .setSilenceCall(true)
                .build())
            else -> respondToCall(callDetails, CallResponse.Builder().build())
        }
    }
}
```

수신 거절 = `setDisallowCall(true) + setRejectCall(true)` → **즉시 통화 종료. 벨 0회 또는 1회 미만**.

### 31-3-2. Manifest 등록

```xml
<service android:name=".callscreening.MyPhoneCheckCallScreeningService"
         android:permission="android.permission.BIND_SCREENING_SERVICE">
    <intent-filter>
        <action android:name="android.telecom.CallScreeningService" />
    </intent-filter>
</service>
```

기본 발신자 식별 앱으로 등록 필요 (사용자 동의 후 시스템 설정 안내).

## 31-4. SMS 처리

### 31-4-1. BroadcastReceiver

```kotlin
class SmsBlockReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        for (msg in messages) {
            val decision = inputAggregator.aggregateForSms(msg.originatingAddress)
            if (decision.action == BLOCK) {
                abortBroadcast()  // SMS Inbox 도달 차단
            }
        }
    }
}
```

`abortBroadcast()` 호출 = **OS SMS Inbox 도달 전 차단**. 사용자 알림 0.

### 31-4-2. RECEIVE_SMS 권한 + Default SMS App 정책

Android 4.4+ Default SMS App 외 abortBroadcast 효과 제한. 다음 두 모드 지원:

- **Mode A (Default SMS)**: 사용자가 본 앱을 Default SMS App으로 설정. 완전 차단 가능.
- **Mode B (Observer)**: Inbox 진입 후 즉시 삭제 + 알림 무음. UX 약간 열림.

기본 = Mode B (사용자 진입 장벽 낮음). Mode A는 Settings 옵트인.

## 31-5. Push 처리

`:feature:push-trash` Stage 1-001 휴지통 모델 활용 + 즉시 cancel:

```kotlin
override fun onNotificationPosted(sbn: StatusBarNotification) {
    val source = sourceParser.parseSource(sbn)
    val decision = inputAggregator.aggregateForNotification(source.packageName)
    if (decision.action == BLOCK) {
        cancelNotification(sbn.key)  // 즉시 cancel + 휴지통 저장
    }
}
```

## 31-6. InputAggregator 즉시 응답 모드

```kotlin
suspend fun aggregateForCall(e164: String): CallActionDecision {
    return coroutineScope {
        val layer2 = async { layer2Repo.lookup(e164) }       // 라벨·태그·차단
        val layer1 = async { layer1Repo.lookupCallLog(e164) } // OS CallLog 빈도
        val layer3 = async { layer3Cache.lookup(e164) }       // 옵트인 출처

        // 우선순위: Layer 2 차단 > Layer 3 차단 > Layer 2 라벨 > Layer 3 라벨 > Pass
        val l2 = layer2.await()
        if (l2.isBlocked) return@coroutineScope CallActionDecision(BLOCK, l2)

        val l3 = layer3.await()
        if (l3.isBlocked) return@coroutineScope CallActionDecision(BLOCK, l3)

        val tag = l2.tag ?: l3.tag
        CallActionDecision(if (tag != null) TAG_DISPLAY else PASS, l2.merge(l3))
    }
}
```

목표 응답 시간: **50ms 이내** (CallScreeningService timeout = 5s, 여유 충분).

## 31-7. 헌법 정합

| 조 | 정합 |
|---|---|
| §1 Out-Bound Zero | 즉시 조치는 디바이스 로컬, 외부 통신 0 |
| §2 In-Bound Zero | OS 자원·캐시 활용, 원문 영구 저장 안 함 |
| §3 결정 중앙집중 금지 | 사용자 차단 목록·태그가 Action 결정. 시스템 강제 X |
| §4 자가 작동 | 네트워크 단절 시에도 Layer 2·1·3 캐시로 작동 |
| §5 정직성 | "차단됨"·"무음 처리됨"·"태그 표시됨" 사용자에게 명시 |
| §8 SIM-Oriented | SimContext.phoneRegion 정규화 후 조회 |

## 31-8. 모듈 매핑

- `:core:global-engine/decision/RealTimeActionEngine.kt` (신규)
- `:feature:call-screening` (신규 모듈)
- `:feature:sms-block` (신규 모듈)
- `:feature:push-trash` (기존, NLS 강화)

## 31-9. 사용자 대면 약속

> "수신 거절은 즉시 적용됩니다. 차단 번호는 벨 1회 울리지 않습니다.
> 모든 차단·태그 결정은 당신이 직접 설정한 것입니다."

## 31-10. cross-ref

- §30 InputAggregator 코어
- §32 Tag System
- §28 Initial Scan (Layer 2 베이스)
- 헌법 §1·§4
