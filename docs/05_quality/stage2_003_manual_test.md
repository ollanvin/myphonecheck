# Stage 2-003 수동 테스트 절차

**대상**: PR Stage 2-003 — MessageCheck 신규 + `:core:global-engine/parsing/message/`
**Architecture**: v2.0.0 §22 + §30 + 헌법 §8조 (SIM-Oriented Single Core)

---

## 0. 준비

- 디바이스 또는 에뮬레이터 (Android 8.0+).
- USIM 장착 권장.
- SMS Inbox에 메시지 1건 이상 (이상적으로 short sender 1건 + 일반 발신자 1건 + URL 포함 1건).

## 1. 빌드·설치

```powershell
.\gradlew assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## 2. 권한 동의

1. 앱 진입 → Settings → "MessageCheck" 카드 탭.
2. "SMS read permission required" 화면이 나오면 "Grant permission" 탭 → 시스템 다이얼로그 동의.
3. 동의 후 화면이 자동 갱신되어 발신자 인벤토리 + 최근 메시지 리스트 표시.

## 3. 발신자 인벤토리 확인

- "Sender Inventory" 섹션: 빈도순 정렬 확인.
- short sender (3~6자리 숫자) 항목에 "Short sender" 배지 표시.

## 4. 메시지 분류 확인

- "Recent Messages" 섹션 각 메시지에 카테고리 chip 표시:
  - **Spam candidate** (적색): URL 포함 + 본문 짧음 또는 URL 2개 이상.
  - **Payment** (녹색): short sender + 통화 패턴 (₩/$/€/USD 등).
  - **Notification** (청색): short sender + URL 없음.
  - **Normal** (회색): 위 조건 미해당.

## 5. 권한 거부 시

- 시스템 설정에서 READ_SMS 권한 거부 후 재진입.
- "SMS read permission required" 화면 노출 확인.

## 6. 회귀 — 다른 Surface 영향 0

- CardCheck 화면 진입 → 기존 동작 동일.
- CallCheck 화면 진입 → 기존 동작 동일 (Stage 2-002 회귀 0).
- 기타 Settings 진입 카드 동일 동작.

## 7. 데이터 정책 검증

- 앱 종료·재시작 후 동일 화면 진입 → SMS 본문이 영구 저장되지 않고 매번 ContentResolver 재조회.
- 헌법 §2 In-Bound Zero 정합 — 본문은 메모리 처리 + UI 스니펫(80자) 임시 보유만.

---

**검증 완료 조건**: 위 1~7 항목 모두 PASS, 영구 저장 0.
