# Stage 2-010 — v2.1.0 통합 검증

본 문서는 Stage 2-001 ~ 2-009 누적 작업의 통합 정합성 검증 보고. v2.1.0 시리즈 마무리.

---

## 1. 모든 Surface가 :core:global-engine 의존

확인 명령:
```powershell
Select-String -Path "feature/*/build.gradle.kts" -Pattern "core:global-engine"
```

기대 결과 (8 모듈):
- :feature:card-check ✅ (Stage 2-001, PR #18)
- :feature:call-check ✅ (Stage 2-002, PR #19)
- :feature:message-check ✅ (Stage 2-003, PR #20)
- :feature:push-trash ✅ (Stage 2-004, PR #21 + Stage 2-006 강화)
- :feature:initial-scan ✅ (Initial Scan PR #23)
- :feature:settings ✅ (Stage 2-005 PR #22 SimContextStorage + PR #24 Settings v2 + PR #29 FeedRegistry + PR #30 Locale)
- :feature:call-screening ✅ (Stage 2-006, PR #27)
- :feature:sms-block ✅ (Stage 2-006, PR #27)
- :feature:tag-system ✅ (Stage 2-007, PR #28)

## 2. 자체 파서 잔존 검증

각 Surface 모듈에 자체 파싱 코드가 0인지 확인 — Stage 2-001~004에서 모두 코어 추출 완료:

- CardCheck: 자체 `parser/` 디렉토리 0 (PR #18에서 currency parser 코어 이전).
- CallCheck: 자체 phone parsing 0 (PR #19, PhoneNumberParser 코어 사용).
- MessageCheck: 자체 SMS pattern 0 (PR #20, SmsPatternExtractor + MessageClassifier 코어 사용).
- PushCheck: 자체 notification source 정규화 0 (PR #21, NotificationSourceParser 코어 사용).

## 3. RealTimeActionEngine 통합 (PR #27)

3개 진입점 모두 RealTimeActionEngine 사용:
- CallScreeningService (Android Q+, BIND_SCREENING_SERVICE)
- SmsBlockReceiver (intent-filter priority 999, abortBroadcast)
- NotificationListenerService (push-trash 강화, BLOCK 시 cancelNotification)

50ms timeout (call/SMS) / 100ms (notification) — withTimeoutOrNull → null 시 PASS fallback (헌법 §3 정합).

## 4. Tag System 통합 (PR #28)

- TagListScreen 진입 카드 (Settings 직후, LocalOffer 아이콘).
- 4 priority 색상 코드: SUSPICIOUS (적색) / PENDING (주황) / REMIND_ME (노랑) / ARCHIVE (회색).
- RealTimeActionEngine.tagRepo 의존 → RoomTagRepository(@Binds in TagSystemModule) 활성.
- 매칭 시 ActionDecision: SUSPICIOUS=SILENT / PENDING·REMIND_ME=TAG_DISPLAY / ARCHIVE=PASS(tag 보존).

후속 PR 항목:
- CallCheck/MessageCheck/PushTrash 길게 누르기 → 태그 추가 통합.
- WorkManager schedule + DailyReminderWorker NotificationManager 알림 발행.

## 5. FeedRegistry 옵트인 흐름 (PR #29)

- Settings v2 → Public Feed 섹션.
- SIM countryIso 기반 자동 그룹: Global / Your Country / Other Countries.
- 5 default 출처:
  - SecurityIntelligence (글로벌, 실 URL): Abuse.ch URLhaus, PhishTank.
  - GovernmentPublic (KR, placeholder): KISA Smishing.
  - CompetitorApp (KR, placeholder + 라이선스 검토 필수): TheCall.
  - TelcoBlocklist (KR, placeholder): KT.
- placeholder URL("<...>") 출처: Switch disabled + 경고 라벨.
- FeedDownloadWorker는 placeholder skip + 옵트인 출처만 다운로드.

후속 PR 항목:
- WorkManager schedule 등록 (Application onCreate, HOURLY/DAILY).
- PublicFeedCache Room v17 통합 (인메모리 → 영구).
- Competitor/Telco 실 URL 활성화 (라이선스 검토 후).

## 6. UI 언어 3단 fallback (PR #30)

- Application.onCreate: DataStore preference 즉시 적용 (runBlocking 1회 first()).
- Settings v2.setLanguagePreference: DataStore 저장 직후 즉시 Applicator.apply.
- AppCompatDelegate.setApplicationLocales가 Activity recreate 자동 트리거.
- res/xml/locales_config.xml: en/ko 명시 (점진 확장).
- AppLocalesMetadataHolderService autoStoreLocales="true" 백포트 (AppCompat 1.6.0+).
- CountryToLanguageMap 44개국 BCP-47 매핑 (WO 명시 27+ 충족).

## 7. 헌법·약속 통합 화면 (PR #24 ConstitutionSection + PrivacyPromiseSection)

ConstitutionSection 8조 표시 (사용자 대면 영문):
1. Out-Bound Zero — your data never leaves the device unless you tap a button.
2. In-Bound Zero — message and call bodies are not stored.
3. No central authority — every block or trust decision is yours.
4. Self-operating — works offline without network.
5. Honest — results show measured values as they are.
6. Honest pricing — fees are transparent.
7. Device-Oriented Goose — defends what is on your device.
8. SIM-Oriented Single Core — your SIM is the single source of truth.

PrivacyPromiseSection 4 약속 (영문):
- Your SIM is the standard.
- All processing stays inside your device.
- External searches happen only when you tap.
- Public feeds activate only after you opt in.

후속 PR 항목 (선택):
- §31 Real-time Action 약속: "수신 거절은 즉시 적용됩니다. 차단 번호는 벨 1회 울리지 않습니다."
- §32 Tag System 약속: "태그는 디바이스 안에서만 보관됩니다. 언제든 삭제할 수 있습니다."

## 8. assembleDebug 회귀 검증

```powershell
.\gradlew assembleDebug
```

기대: BUILD SUCCESSFUL.

검증 시점 (PR #30 머지 후): PASS.

## 9. 모든 단위 테스트 PASS

```powershell
.\gradlew testDebugUnitTest
```

PR #30 시점 누적 결과: **190/190 PASS**
- core/global-engine: 145
- feature/tag-system: 10
- feature/call-screening: 5
- feature/initial-scan: 7
- feature/call-check: 4
- feature/message-check: 3
- feature/push-trash: 8
- feature/settings: 8

## 10. Room DB 버전 매트릭스 (Stage 2 시리즈)

| 버전 | PR | 신규 entity |
|---|---|---|
| v13 | #14 (Stage 1-002) | CardTransaction, CardSourceLabel |
| v14 | #23 (Initial Scan) | CallBase, SmsBase, PackageBase, SimContextSnapshot |
| v15 | #27 (Stage 2-006) | BlockedIdentifier |
| v16 | #28 (Stage 2-007) | PhoneTag |
| v17 | #29 (Stage 2-008) | FeedEntry |

schemas/{13~17}.json 모두 자동 생성 + 보존.

---

## 11. 카드스펜드 별도 앱 폐기 안내 (별도 레포)

CardCheck v1.9.0 신설 사실에 따라 카드스펜드 별도 앱 폐기.

### 보존
- `C:\Users\user\ollanvin\figma\cardspend\` (피그마 자료, 메모리 #2).
- 디자인 자료는 카드스펜드 → MyPhoneCheck CardCheck 마이그레이션 참조용.

### 폐기 (별도 레포 작업)
- 카드스펜드 GitHub 레포 (있다면) → archive 또는 삭제.
- 카드스펜드 Play Store 출시 (있었다면) → 비공개 전환 + 사용자 마이그레이션 안내.

### 마이그레이션 안내
- 사용자가 있었다면 MyPhoneCheck CardCheck 안내.
- 데이터 마이그레이션 경로 (있었다면).

본 사항은 별도 워크오더로 처리 (myphonecheck 측 PR 영역 외).

---

## 검증 결과

- 1~9 모두 PASS → v2.1.0 통합 정합성 확인.
- Stage 2 시리즈 (Stage 2-001 ~ 2-010) 완료.
- 일부 항목은 후속 PR 명시 (CallCheck/MessageCheck 길게 누르기 통합, WorkManager schedule, Competitor 실 URL 등) — 본 통합 검증 범위 외.

---

## 후속 트랙 (myphonecheck 측 영역 외 또는 별도 PR)

- 카드스펜드 GitHub 레포 폐기·archive.
- 카드스펜드 Play Store 비공개 전환.
- Infrastructure v1.2 승격 (Architecture v2.1.0 정합).
- Cursor + Cowork 일괄 크로스 체크 (Stage 2-001~010 통합 감사).
- Competitor Apps 라이선스 검토 후 실 URL 활성화.
- KISA 공식 피드 URL 확정.
