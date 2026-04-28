# Stage 2-006 수동 테스트 절차

**대상**: PR Stage 2-006 — Real-time Action Engine
**Architecture**: v2.1.0 §31 + 헌법 §1/§2/§3/§4/§5/§8

---

## 0. 준비

- 디바이스 (Android Q+ 권장, CallScreeningService 지원).
- USIM 장착.
- 본 PR 빌드 설치 (이전 v14 → v15 마이그레이션 자동).

## 1. 빌드·설치

```powershell
.\gradlew assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## 2. Room v14 → v15 마이그레이션 검증

기존 v14 DB 보유 디바이스에서 갱신 설치:
- 앱 부팅 정상.
- adb logcat에서 Room migration 오류 없음.
- 기존 데이터(UserCallRecord, CardTransaction, CallBase, SmsBase, PackageBase, SimContextSnapshot 등) 그대로.
- 신규 `blocked_identifier` 테이블 생성됨.

## 3. CallScreeningService 등록

1. 시스템 설정 → 기본 앱 → 발신자 식별/스팸 차단 앱 → MyPhoneCheck 선택.
2. (또는 후속 PR에서 앱 내 안내 흐름 추가)

## 4. Call BLOCK 검증

사전: BlockListRepository에 차단 번호 1건 등록 (현재는 코드 또는 후속 UI).

검증:
- 해당 번호로 발신 → **벨 0회 또는 1회 미만** 즉시 종료.
- CallLog에 "차단됨" 로그 남김 (setSkipCallLog=false).
- 사용자 알림 0 (setSkipNotification=true).

## 5. Call SILENT (Tag SUSPICIOUS)

본 PR은 Tag System §32 코드 미구현 (NoopTagRepository) — 동작 0.
후속 PR (§32 Tag System 구현) 후 검증.

## 6. SMS BLOCK 검증

차단 발신자(SMS_SENDER) 등록 후:
- 해당 발신자에서 SMS 수신 → `SmsBlockReceiver` priority 999 + `abortBroadcast()`.
- Mode A (Default SMS App): OS Inbox 도달 0, 알림 0.
- Mode B (Observer): ordered broadcast 환경에서 abort 시도, 그 외 환경에서는 inbox 도달 후 후속 처리 필요 (후속 PR).

## 7. Push BLOCK 검증

차단 packageName 등록 후 NotificationListener 권한 부여:
- 해당 앱에서 알림 발생 → 즉시 `cancelNotification()` + 휴지통 저장.
- 사용자 알림 표시 시간 거의 0.

## 8. 회귀 — 기존 push-trash 차단 규칙 동작 동일

BlockList 매칭 안 되는 경우 → 기존 `PushTrashRepository.decide()` 차단 규칙 그대로:
- 기존 차단 채널/앱 → 휴지통 저장 + cancel.
- 허용 → OS 알림 표시.

## 9. 50ms timeout 검증

DB가 비정상적으로 느릴 때(예: 디스크 부하):
- RealTimeActionEngine `withTimeoutOrNull(50)` → null → PASS fallback.
- BLOCK 후보를 PASS 처리하는 결과 = OS 기본 동작 = **헌법 §3 정합** (자동 차단 결정 회피).

## 10. 헌법 정합 검증

- §1 Out-Bound Zero: 모든 결정 디바이스 로컬, 외부 통신 0.
- §2 In-Bound Zero: 식별자만 lookup, 통화 본문/SMS 본문 영구 저장 0.
- §3 결정 중앙집중 금지: 차단 결정은 사용자가 BlockListRepository에 명시 등록.
- §4 자가 작동: 네트워크 단절 환경에서 동작.
- §5 정직성: "차단됨" CallLog 보존, BLOCK 사실 사용자 검토 가능.
- §8 SIM-Oriented: CallScreeningService에서 `phoneParser.parse(raw, simContext)` 적용.

---

**검증 완료 조건**: 1~10 PASS, 회귀 0, 외부 통신 0.

## 후속 PR 항목

- BlockList 등록/해제 UI (Settings 또는 통화/SMS 화면).
- §32 Tag System 코드 구현 (Room v16 tag entity + UI).
- §30-4-4 Competitor Feeds 출처 통합.
- Mode A/B 사용자 안내 흐름.
- Default Dialer setup screen.
