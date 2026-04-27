# Stage 2-004 수동 테스트 절차

**대상**: PR Stage 2-004 — PushCheck → `:core:global-engine/parsing/notification/`
**Architecture**: v2.0.0 §26 + §30 + 헌법 §8조

---

## 0. 준비

- 디바이스 또는 에뮬레이터 (Android 8.0+).
- Stage 1-001 휴지통 동작 검증을 위해 사전에 `feature:push-trash` 차단 규칙 1건 이상 설정.

## 1. 빌드·설치

```powershell
.\gradlew assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## 2. NotificationListenerService 권한

1. 앱 진입 → Settings → "Push Trash" 진입.
2. "알림 접근 권한" 시스템 다이얼로그 동의 (NotificationListenerService 활성화).

## 3. 회귀 — Stage 1-001 휴지통 동작 동일

차단 규칙이 설정된 앱에서 알림 발생:
- 차단 규칙 매칭 시 알림이 즉시 사라지고 휴지통(`PushTrashBin`)에 기록.
- 차단 규칙 미매칭 시 알림 정상 노출.
- 본 앱 자체 알림은 휴지통에 기록되지 않음 (자기 자신 필터).

기존 Stage 1-001 검증 절차와 동일 결과 — 회귀 0.

## 4. 코어 source 정규화 검증 (소프트 검증)

본 PR의 변경: `PushTrashNotificationListener.onNotificationPosted`가 `NotificationSourceParser.parseSource(sbn)`를 통해 `packageName`/`channelId`/`postTime`을 추출.

검증 방법:
- 알림 1건 발생시키고 휴지통 또는 인벤토리에 기록된 packageName이 발신 앱과 일치하는지 확인.
- channelId가 발신 앱이 사용한 채널과 일치 (Android 8.0+).

## 5. CardCheck 협업 hook (옵션)

`NotificationFeatureExtractor`는 본 PR에서 코어에 신설됐으나 listener에서 호출되지 않음 (회귀 0 유지). CardCheck 후속 통합 시 사용 예정.

## 6. 다른 Surface 회귀 0

- CallCheck, MessageCheck, CardCheck 화면 진입 → 기존 동작 동일.

---

**검증 완료 조건**: 1~6 항목 PASS, 휴지통 동작 회귀 0.
