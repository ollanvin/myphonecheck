# Stage 2-002 수동 테스트 절차

**대상**: PR Stage 2-002 — CallCheck 신규 + `:core:global-engine/parsing/phone/`
**Architecture**: v2.0.0 §21 + §30 + 헌법 §8조 (SIM-Oriented Single Core)

---

## 0. 준비

- 디바이스 또는 에뮬레이터 (Android 8.0+).
- USIM 장착 권장 (SIM 부재 시 시스템 Locale country fallback 동작 검증 가능).
- 기존 통화 이력 1건 이상 (없으면 사전에 발신/수신 1회).

## 1. 빌드·설치

```powershell
.\gradlew assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## 2. 권한 동의

1. 앱 진입 → Settings → "CallCheck" 카드 탭.
2. "Call log permission required" 화면이 나오면 "Grant permission" 탭 → 시스템 다이얼로그 동의.
3. 동의 후 화면이 자동 갱신되어 최근 통화 리스트 표시.

## 3. 한국 SIM 정규화 검증 (KR SIM 기준)

- 통화 항목 표시 번호가 NATIONAL 양식인지 확인 (예: `010-1234-5678`).
- 해외 발신자(예: `+1...`) 통화 항목은 INTERNATIONAL 양식으로 표시.

## 4. SIM 변경 시뮬레이션 (선택)

- 가능한 환경: 듀얼 SIM 디바이스 또는 SIM 교체.
- 미국 SIM 장착 후 동일 화면 진입 → 동일 한국 번호가 INTERNATIONAL 양식으로 표시되는지 확인.
- 헌법 §8조 SIM-Oriented Single Core 작동 검증.

## 5. 권한 거부 시

- 시스템 설정에서 READ_CALL_LOG 권한 거부 후 재진입.
- "Call log permission required" 화면 노출 확인.

## 6. 빈 이력

- 통화 이력 0건 디바이스에서 진입 → "No recent calls" 빈 상태 카드 표시.

## 7. 회귀 — 다른 Surface 영향 0

- CardCheck 화면 진입 → 기존 동작 동일 (SMS/Push 거래 표시).
- Settings의 다른 진입 카드 (Backup, PushTrash 등) 동일 동작.

---

**검증 완료 조건**: 위 1~7 항목 모두 PASS, 영구 저장 0 (헌법 §2 In-Bound Zero — CallLog는 OS 자원 그대로).
