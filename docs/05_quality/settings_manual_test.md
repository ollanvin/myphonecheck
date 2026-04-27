# Settings v2 수동 테스트 절차

**대상**: PR Settings UI 통합 (v2.0.0 §29 정책 100% UI 반영)
**Architecture**: v2.0.0 §29 + 헌법 §1/§2/§3/§8

---

## 0. 준비

- 디바이스 또는 에뮬레이터 (Android 8.0+).
- Initial Scan 1회 실행 (PR #23) — base data + sim_context_snapshot 보유 권장.

## 1. 빌드·설치

```powershell
.\gradlew assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## 2. v2 Settings 진입

1. 앱 → Settings → "v2.0.0 Privacy Settings" 카드 (Shield 아이콘) 탭.
2. 5개 섹션 확인: Language / SIM / Base Data / Public Feed / Constitution / Promise.

## 3. UI Language 3단 fallback

1. Language 섹션 → 3개 라디오 중 선택.
2. DataStore에 preference 저장됨 (재진입 시 동일 항목 선택 상태 유지).
3. 후속 PR에서 실제 Locale 적용 (현재는 preference만 기록).

## 4. SIM Information 섹션

- Country / Operator / Currency / Phone region / Timezone 표시.
- SIM 미장착 시 "No SIM detected".

## 5. SIM 변경 3-옵션 (§29-4)

조건: 이전 SimContextSnapshot과 다른 SIM 장착 후 진입.
- "SIM change detected" 경고 + 이전/현재 표시.
- 3 버튼:
  - **Apply New SIM** — 새 SIM 저장 + Initial Scan 재실행.
  - **Keep Previous** — 기존 컨텍스트 유지, Storage 갱신 안 함.
  - **Reset and Rescan** — base 초기화 + Initial Scan.

## 6. Base Data 섹션

- 현재 call/sms/package count 표시.
- "Rescan device" → InitialScanService.execute() 재실행.
- "Reset base data" → Room v14 4개 테이블 clear.

## 7. Public Feed Opt-in

- 현재 옵트인된 출처 ID 목록.
- 본 PR은 출처 0 — "No public feed sources are wired" 표시.
- 후속 PR에서 실제 출처(KISA/Abuse.ch 등) 토글 추가.

## 8. Constitution + Promise 섹션

- 헌법 8조 사용자 대면 버전 표시.
- Privacy Promise 4 문장 (SIM 기준 / 디바이스 처리 / 외부 검색 trigger / 공개 피드 옵트인).

## 9. 회귀 — 기존 Surface 동작 동일

- CardCheck, CallCheck, MessageCheck, PushTrash, InitialScan 진입 → 기존 동작.
- 기존 Settings 카드(Backup, PushTrash 등) 정상.

---

**검증 완료 조건**: 1~9 PASS, 회귀 0, DataStore 영구 저장 확인.
