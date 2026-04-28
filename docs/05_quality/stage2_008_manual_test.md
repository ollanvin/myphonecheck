# Stage 2-008 수동 테스트 절차

**대상**: PR Stage 2-008 — FeedRegistry + 4 source types + Competitor Apps
**Architecture**: v2.1.0 §30-3-A + §30-4 + 헌법 §1/§2/§3/§5/§8

---

## 0. 준비

- 디바이스 (Android 8.0+).
- 본 PR 빌드 설치. 기존 v16 → v17 마이그레이션 자동.

## 1. 빌드·설치

```powershell
.\gradlew assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## 2. Room v16 → v17 마이그레이션

기존 v16 DB 보유 디바이스에서 갱신 설치:
- 앱 부팅 정상.
- Room migration 오류 없음.
- 기존 데이터 그대로.
- 신규 `feed_entry` 테이블 생성됨 (sourceId/matchKey/description/severity/downloadedAtMillis 컬럼).

## 3. Settings v2 Public Feed Opt-in 섹션

1. Settings → "v2.0.0 Privacy Settings" → "Public Feed Opt-in" 섹션.
2. SIM = KR 디바이스에서 3 그룹 표시:
   - **Global Sources**: Abuse.ch URLhaus, PhishTank
   - **Your Country: KR**: KISA Smishing, TheCall (Placeholder), KT (Placeholder)
   - **Other Countries**: (KR-only 출처가 모두 표시되므로 본 PR에서는 비어있음)
3. 각 행: 출처 이름 + 라이선스·갱신주기 + Switch.

## 4. CompetitorApp 경고 표시

- TheCall (Korea) — Placeholder 행:
  - "Third-party app data — review terms before opt-in" 노란색 경고.
  - "Placeholder — license review pending, opt-in disabled" 노란색 경고.
  - Switch는 enabled=false (회색, 토글 불가).

## 5. Placeholder 토글 disabled

- KISA Smishing (URL "<별도 확정>") → placeholder → Switch disabled.
- KT (Placeholder) → 동일.
- Abuse.ch URLhaus / PhishTank → 실 URL → Switch enabled.

## 6. SIM = US 시뮬레이션

- US SIM 또는 Settings의 SimContext 변경 후 진입:
  - **Global Sources**: 2개 (Abuse.ch, PhishTank).
  - **Your Country: US**: (등록 출처 없음 — 비어있음).
  - **Other Countries**: KR 출처 3개 (KISA, TheCall, KT) — 토글 가능 (단, placeholder는 disabled).

## 7. 옵트인 토글 + 영구 저장

1. Abuse.ch URLhaus Switch 토글 ON.
2. 앱 재시작 → Settings 재진입 → 동일 ON 상태 유지 (DataStore 영구 저장).
3. 토글 OFF → 재시작 → OFF 유지.

## 8. PublicFeedAggregator 동작

본 PR은 FeedDownloadWorker schedule 등록 0 (후속 PR). 수동 trigger:
- 옵트인된 출처가 FeedRegistry.byId() 조회 정상.
- PublicFeedCache.lookup(sourceId, query)는 본 PR 인메모리 (Room v17 통합은 후속 PR).
- Hilt 그래프: PreferenceFeedOptInProvider → FeedOptInProvider 바인딩 → PublicFeedAggregator inject 정상.

## 9. 회귀 — 다른 Surface 동작 동일

- CardCheck/CallCheck/MessageCheck/PushTrash/InitialScan/Tag List 진입 → 기존 동작.
- Real-time Action Engine (PR #27) BlockList 차단 동작 동일.
- Tag System (PR #28) UI/Repository 동일.

## 10. 헌법 정합

- §1 Out-Bound Zero: 옵트인된 실 URL 출처만 다운로드 (placeholder/opt-out 0).
- §2 In-Bound Zero: 라이선스 정합 데이터만 캐싱.
- §3 결정 중앙집중 금지: 사용자 옵트인 명시 동의 (placeholder는 자동 disabled).
- §5 정직성: 라이선스·갱신 주기·placeholder 상태 모두 사용자에게 표시.
- §8 SIM-Oriented: countryIso 기반 자동 그룹 추천.

---

**검증 완료 조건**: 1~10 PASS, 회귀 0, placeholder opt-in 차단.

## 후속 PR 항목

- WorkManager schedule 등록 (Application onCreate, HOURLY/DAILY).
- PublicFeedCache Room v17 통합 (인메모리 → 영구 캐시).
- Custom Tab Terms link 통합.
- Competitor Feeds (TheCall/후후/Whoscall 등) 라이선스 검토 후 실 URL 활성화.
- Telco Blocklist 실 URL 활성화.
- KISA 공식 피드 URL 확정.
