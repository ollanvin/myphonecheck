# MyPhoneCheck 1.0 — Ringing 중 가시성 검증 리포트

## 1. 검증 결과

### 테스트 환경
- Pixel 에뮬레이터 (AOSP, Android 14)
- MyPhoneCheck Notification: PRIORITY_HIGH, CATEGORY_CALL, colorized

### 화면 캡처 결과

**알림 패널을 내린 상태:**
- Incoming Call UI (상단) + MyPhoneCheck Notification (바로 아래) 동시 표시 ✅
- "위험 높음 / 스팸/사기 위험 높음 / 자세히·거절·차단" 모두 보임

**알림 패널을 내리지 않은 상태 (일반 사용자 기본 화면):**
- 전화 앱 전체화면 UI만 표시
- MyPhoneCheck 결과 **전혀 안 보임** ❌
- 상태바에 아이콘만 표시 (인지 불가)

### 결론
> **판정은 ringing 중에 완료되지만, 사용자 인지는 ringing 중에 보장되지 않음**

---

## 2. 현재 구조의 한계

| 문제 | 설명 |
|------|------|
| 전체화면 가림 | Android 인커밍콜 UI가 전체화면을 차지, Notification 안 보임 |
| 패널 내림 필요 | 사용자가 직접 알림 패널을 내려야 결과 확인 가능 |
| OEM 차이 | 삼성/샤오미/OPPO 등 커스텀 전화 앱은 Notification 가시성이 더 떨어질 수 있음 |
| Heads-up 한계 | PRIORITY_HIGH heads-up 알림은 일부 기기에서 인커밍콜 UI 위에 뜨지 않음 |

---

## 3. 업계 정석 해법

### Truecaller / Whoscall / Hiya 공통 패턴: SYSTEM_ALERT_WINDOW 오버레이

모든 주요 Caller ID 앱은 `SYSTEM_ALERT_WINDOW` 권한으로 전화 앱 위에 직접 UI를 띄움.

```
┌─────────────────────────────┐
│   Android Incoming Call UI   │ ← 전화 앱 전체화면
│                              │
│  ┌────────────────────────┐  │
│  │  MyPhoneCheck 오버레이     │  │ ← SYSTEM_ALERT_WINDOW
│  │  ⚠ 위험 높음           │  │
│  │  스팸/사기 위험 높음    │  │
│  │  신뢰도: 85%           │  │
│  └────────────────────────┘  │
│                              │
│    [Decline]    [Answer]     │ ← 전화 앱 버튼 (그대로 보임)
└─────────────────────────────┘
```

### 기술 요구사항

| 항목 | 내용 |
|------|------|
| 권한 | `android.permission.SYSTEM_ALERT_WINDOW` |
| 권한 취득 | `Settings.canDrawOverlays()` → `ACTION_MANAGE_OVERLAY_PERMISSION` |
| 자동 부여 조건 | 기본 전화 앱 또는 기본 Caller ID 앱으로 설정 시 일부 기기에서 자동 부여 |
| Window Type | `TYPE_APPLICATION_OVERLAY` (API 26+) |
| 표시 시점 | `onScreenCall()` → 판정 완료 직후 |
| 제거 시점 | 사용자가 수신/거절 후, 또는 전화 종료 시 (PhoneStateListener) |
| OEM 호환성 | 삼성/샤오미/OPPO 등에서도 동작 (Truecaller 검증 완료된 패턴) |

### Android 버전별 전략

| Android 버전 | 방식 |
|-------------|------|
| 8.0+ (API 26) | TYPE_APPLICATION_OVERLAY |
| 11+ (API 30) | 동일 + Bubble API 대안 가능 |
| 전 버전 공통 | SYSTEM_ALERT_WINDOW 권한 필수 |

---

## 4. 수정안: CallerIdOverlayManager

### 아키텍처

```
MyPhoneCheckScreeningService
    │
    ├── assessThenAllow()
    │       │
    │       ├── 1. DecisionEngine.evaluate()
    │       ├── 2. CallerIdOverlayManager.showOverlay(result)  ← NEW
    │       ├── 3. DecisionNotificationManager.show(result)     ← 유지 (fallback)
    │       └── 4. respondAllow()
    │
    └── onDestroy()
            └── CallerIdOverlayManager.dismissOverlay()
```

### CallerIdOverlayManager 설계

```kotlin
class CallerIdOverlayManager @Inject constructor() {

    private var overlayView: View? = null

    /**
     * 전화 앱 위에 판정 결과 오버레이를 표시한다.
     *
     * 권한: SYSTEM_ALERT_WINDOW 필수
     * Window type: TYPE_APPLICATION_OVERLAY
     * 위치: 화면 상단 (전화 앱 Decline/Answer 버튼 방해 안 함)
     */
    fun showOverlay(context: Context, result: DecisionResult, phoneNumber: String) {
        if (!Settings.canDrawOverlays(context)) {
            Log.w(TAG, "SYSTEM_ALERT_WINDOW not granted, falling back to notification")
            return
        }

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP
        }

        overlayView = buildOverlayView(context, result, phoneNumber)
        wm.addView(overlayView, params)
    }

    fun dismissOverlay(context: Context) {
        overlayView?.let {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.removeView(it)
            overlayView = null
        }
    }

    private fun buildOverlayView(
        context: Context,
        result: DecisionResult,
        phoneNumber: String,
    ): View {
        // 프로그래밍 방식으로 View 생성
        // - 배경색: risk level별 (빨강/노랑/초록/회색)
        // - 제목: result.riskLevel.displayNameKo
        // - 내용: result.category.summaryKo
        // - 신뢰도: XX%
        // 높이: wrap_content (전화 앱 버튼 가리지 않음)
    }
}
```

### 권한 요청 흐름 (앱 첫 실행 시)

```
MainActivity
    │
    ├── 1. CALL_SCREENING role 요청
    ├── 2. 알림 권한 요청
    └── 3. SYSTEM_ALERT_WINDOW 권한 요청 ← NEW
            │
            └── Settings.canDrawOverlays() == false?
                    → startActivity(ACTION_MANAGE_OVERLAY_PERMISSION)
```

### Notification은 유지 (fallback)

오버레이 권한이 없을 때 → 기존 Notification으로 fallback.
따라서 현재 코드를 제거하지 않고, 오버레이를 추가 레이어로 얹는 구조.

---

## 5. 전화 종료 시 오버레이 제거

### 방법 1: PhoneStateListener (deprecated but works)

```kotlin
telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
// CALL_STATE_IDLE → dismissOverlay()
// CALL_STATE_OFFHOOK → dismissOverlay() (사용자가 받음)
```

### 방법 2: TelecomManager CallState callback (API 31+)

```kotlin
telecomManager.registerTelecomCallback(executor, callback)
```

### 방법 3: onDestroy() 활용 (현재 가능)

MyPhoneCheckScreeningService.onDestroy()에서 dismissOverlay() 호출.
단, respondToCall() 후 즉시 onDestroy()가 오므로 오버레이도 즉시 사라질 수 있음.
→ 별도 dismiss 타이밍 관리 필요 (전화 종료 시까지 유지).

---

## 6. 구현 우선순위

| 순서 | 항목 | 난이도 | 효과 |
|------|------|--------|------|
| 1 | CallerIdOverlayManager 기본 구현 | 중 | 핵심 가시성 해결 |
| 2 | SYSTEM_ALERT_WINDOW 권한 요청 흐름 | 하 | 권한 없으면 동작 불가 |
| 3 | 오버레이 UI 디자인 (risk별 색상/레이아웃) | 중 | 사용자 즉시 인지 |
| 4 | 전화 종료 시 dismiss 타이밍 관리 | 중 | 오버레이 잔류 방지 |
| 5 | OEM별 호환성 테스트 | 고 | 삼성/샤오미 등 |

예상 소요: 핵심 구현 4~6시간, OEM 테스트 별도

---

*작성: 비전 | 2026-03-24*
*참고: [Android CallScreeningService](https://developer.android.com/reference/android/telecom/CallScreeningService), [Truecaller overlay pattern](https://medium.com/@harshalp2120/building-a-caller-id-and-spam-blocker-app-for-android-in-2023-b8587b03604a)*
