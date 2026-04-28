# Stage 2-007 수동 테스트 절차

**대상**: PR Stage 2-007 — Tag System (volatile memo)
**Architecture**: v2.1.0 §32 + 헌법 §1/§2/§3/§5

---

## 0. 준비

- 디바이스 (Android 8.0+).
- 본 PR 빌드 설치. 기존 v15 → v16 마이그레이션 자동.

## 1. 빌드·설치

```powershell
.\gradlew assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## 2. Room v15 → v16 마이그레이션

기존 v15 DB 보유 디바이스에서 갱신 설치:
- 앱 부팅 정상.
- adb logcat에서 Room migration 오류 없음.
- 기존 데이터(blocked_identifier, sim_context_snapshot 등) 그대로.
- 신규 `phone_tag` 테이블 생성됨.

## 3. Tag List 진입

1. 앱 → Settings → "Tag System" 카드 (LocalOffer 아이콘) 탭.
2. 빈 상태 메시지 표시 (본 PR은 태그 추가 UI 미통합).
3. Privacy Promise 카드 표시: "Tags are stored only on this device. Delete anytime."

## 4. Real-time Action 통합 검증 (개발 도구)

본 PR은 UI에서 태그 추가가 아직 없으므로 직접 Repository 호출 또는 디버그 메뉴로 태그 1건 등록 후:
- SUSPICIOUS 태그 등록한 번호로 발신 → CallScreeningService에서 SILENT decision (PR #27 통합).
- REMIND_ME / PENDING 태그 → TAG_DISPLAY decision (UI 통합은 후속 PR에서 알림 라벨 표시).

## 5. priority별 그룹

직접 4개 priority 태그 등록 후 Tag List 진입:
- SUSPICIOUS / PENDING / REMIND_ME / ARCHIVE 4개 섹션, 각 색상 코드.
- 각 항목: key, tagText, lastSeen, Delete 버튼.

## 6. 회귀 — Real-time Action 동작 동일

- BlockedIdentifier 차단 번호 발신 → BLOCK 동일 (PR #27 회귀 0).
- SmsBlockReceiver / push-trash NLS 동작 동일.
- BlockList 우선순위(BLOCK) > Tag(SUSPICIOUS = SILENT) 보존.

## 7. WorkManager DailyReminderWorker

본 PR: worker 클래스 + pendingReminders 조회. 실제 알림 발행 + WorkManager schedule은 후속 PR.

검증: `:feature:tag-system:testDebugUnitTest` 또는 향후 통합 테스트.

## 8. 헌법 정합

- §1 Out-Bound Zero: Room 디바이스 로컬 phone_tag.
- §2 In-Bound Zero: 사용자 입력만 저장 (자동 채우기 0).
- §3 결정 중앙집중 금지: 사용자 직접 부여·해제.
- §5 정직성: "이 번호 태그 있음" UI 명시 + Privacy Promise 카드.

---

**검증 완료 조건**: 1~8 PASS, 회귀 0.

## 후속 PR 항목

- CallCheck/MessageCheck/PushTrash 화면 길게 누르기 → 태그 추가 다이얼로그 통합.
- 알림 빌더에 priority별 라벨/색상 코드 추가 (SUSPICIOUS = 빨강, PENDING = 주황, REMIND_ME = 노랑).
- WorkManager schedule 등록 (Application onCreate).
- DailyReminderWorker NotificationManager 알림 발행.
- BlockList 등록/해제 UI.
