# WO-V230-V1-CODE-MAPPING — v1 시절 코드 자산 감사 및 v2.x 매핑 (Read-only)

**WO**: WO-V230-V1-CODE-MAPPING  
**발행**: 비전 직접 발행 · standby 해제  
**선행**: PR #37 머지 완료 (`main` `98fc012` — 본 워크스페이스에서 **조상 확인**)  
**코드 변경**: **0** (본 문서만)  
**페어 WO**: `WO-V230-UX-ASSETS-AUDIT` (Cowork, docs·UX 자산) — 본 보고서와 합치면 v1↔v2.x 완전 매핑 목표  
**Layer 3 소요**: 약 60~90분 분량의 코드 읽기 감사

---

## 0. 대표님 명시 (2026-04-28) 반영

> **"껍데기만 가지고 지금까지 다 했다"**

본 감사에서 이를 코드 증거로 해석하면:

- **표면(UI 껍데기)**: 온보딩·홈 2×2 그리드·3탭 바텀네비 등 **초기 CallCheck 중심 UX 골격**이 여전히 사용자 첫 경험을 지배한다.
- **실체(데이터·엔진·Room·Stage 2)** : `:core:global-engine`, Real-time Action, Tag, FeedRegistry, Room **v13~v17**, Initial Scan 등은 **동일 저장소에 축적**되어 있으나, 홈 한 화면에 **Six Surfaces 전부가 동등 노출**되지는 않는다.

즉 **v2.x 아키텍처 명세 대비 UI 노출은 부분적**이고, **기능 깊이는 모듈·DB 버전으로는 v2 계열**이다.

---

## 1. 조사 영역 × 현재 코드 매핑

### 1. App 진입점

| 항목 | 구현 위치 | v2.x 매핑 |
|------|-----------|-----------|
| 런처 Activity | `app/.../MainActivity.kt` → `MyPhoneCheckNavHost()` | 단일 Activity · Compose — 표준 |
| Application | `MyPhoneCheckApplication` — SQLCipher 로드, UI 언어 적용, Initial Scan 오케스트레이터 | §28 Initial Scan · §29 UI 언어 |

### 2. 온보딩 5페이지

| 항목 | 구현 위치 | 비고 |
|------|-----------|------|
| 페이지 수 | `ONBOARDING_PAGE_COUNT = 5`, `currentPage` 0~4 — `OnboardingPage1()` … `OnboardingPage5()` | WO 명세와 일치 |
| 완료 후 | `SharedPreferences` `KEY_ONBOARDING_COMPLETED` → `home` 로 이동 | v1 패턴 유지 |

### 3. 권한 위임

| 항목 | 구현 위치 | 비고 |
|------|-----------|------|
| 온보딩 종단 | "나중에" / "계속" 모두 `onContinue` — 즉시 시스템 권한 다이얼로그 강제 없음 | §24 권한 단계 설계와 완화 일치 가능 |
| 실제 권한 | 각 Surface 모듈·CallScreeningService·SMS 리시버·NLS 등 **기능 진입 시** 요청 패턴 | Cowork UX WO와 교차 확인 권장 |

### 4. 홈 6 Surface 그리드

| 항목 | 관측 | v2.x 정합 |
|------|------|-----------|
| **홈 본문** | `HomeScreen` — **2×2 카드 4개만** (Call, Message, Camera, Mic) | Architecture **Six Surfaces** 전원을 홈 그리드에 두지 않음 |
| Push / Card | 동일 파일 내 타 라우트·`SettingsScreen`에서 `push-trash` · `card-check` 네비게이션 | **기능은 존재**, 홈 첫 화면에서는 **4 Surface 중심 잔존** |

**판정**: v1 CallCheck 시대 **"엔진 카드 4장"** 레이아웃이 홈에 그대로 남아, **대표님 발언과 정합**하는 차이(껍데기 vs 전 스펙 노출).

### 5. Surface UX 6종

| Surface | 주요 라우트·모듈 | 비고 |
|---------|------------------|------|
| CallCheck | `engine/call`, `call-check` 등 | 다중 진입점 |
| MessageCheck | `message-hub`, `message-check` | |
| MicCheck / CameraCheck | `mic-check`, `camera-check`, Sensor 상세 | Initial Scan Guard 연동 |
| PushCheck | `push-trash/*` — `feature:push-trash` | |
| CardCheck | `card-check` — `CardCheckRoute` | |

레거시 라우트 `engine/privacy`, `EngineDetailScreen`(InterceptEventType.PRIVACY) — 명명상 **구 PrivacyCheck** 잔존 가능 → Cowork UX WO에서 문서·표기와 일치 여부 확인.

### 6. Settings 탭

| 항목 | 구현 위치 | 비고 |
|------|-----------|------|
| 바텀 탭 | `HOME` / `TIMELINE` / `SETTINGS` — `BottomTab` enum | 3탭 |
| 설정 화면 | `SettingsScreen` + `settings/v2` 네비게이션 | Stage 2 설정 v2·헌법 섹션 등 |
| Card/Push/InitialScan/Tag | `onNavigateTo*` 콜백 다수 | Six Surfaces **설정 허브** 역할 |

### 7. 결제·Billing

| 항목 | 구현 위치 | 비고 |
|------|-----------|------|
| 모듈 | `feature/billing` — `BillingManager` (Play Billing 6.x), `PaywallScreen`, `PaywallViewModel` | RevenueCat 없음 — 문서 금지 토큰과 정합 |
| 앱 내 진입 | `NavHost` `purchase` → `PurchaseScreen` → Paywall 연동 | 구독·복원 경로 존재 |

### 8. 멤버스·계정

| 관측 | 내용 |
|------|------|
| 별도 **소셜 로그인·멤버스 포털** 코드 | 본 감사 범위에서 **미발견** (온디바이스·구독 상태 중심) |
| 백업 등 | `backup` 라우트 등 로컬 기능 | "계정"은 **Play 구독 상태 + 로컬 설정**으로 한정되는 형태 |

Cowork WO에서 제품 스펙 멤버스 요구가 있으면 본 절과 대조.

### 9. strings.xml + 다국어 (§9-1 충돌 검토)

| 항목 | 관측 |
|------|------|
| 리소스 폴더 | `values` + `values-ko`, `-ja`, `-es`, `-ar`, `-zh-rCN`, `-ru` (**7개 로케일**) |
| `locales_config.xml` | **선언: `en`, `ko` 만** 명시 (주석: Stage 2-009, 점진 확장) |

**충돌 성격**: 번역 파일은 **조기 추가**되었으나 OS 노출 로케일은 **2개로 제한** — Architecture **§9-1 다국어 로드맵**과 병행 시 **문서·코드 어느 쪽을 진실원으로 할지** Cowork WO와 합의 필요.

### 10. `:core:global-engine` 통합

다음 모듈이 `implementation(project(":core:global-engine"))` 선언 확인:

`app`, `feature:call-check`, `message-check`, `push-trash`, `card-check`, `settings`, `initial-scan`, `call-screening`, `sms-block`, `tag-system`

**판정**: Stage 2 통합 목표(Surface가 코어 의존)에 **소스 레벨으로 부합**.

### 11. Real-time / Tag / Feed

| 자산 | 위치 |
|------|------|
| Real-time Action | `core/global-engine/.../RealTimeActionEngine.kt` — 호출부 `call-screening`, `sms-block`, `push-trash` 등과 연계 |
| Tag | `feature/tag-system`, Room `PhoneTagEntity` (v16) |
| Feed | `FeedRegistry`, `PublicFeedAggregator`, `FeedDownloadWorker`, Room `FeedEntryEntity` (v17) |

### 12. Room DB v13~v17

| 버전 | 코드 주석 요지 (`MyPhoneCheckDatabase.kt`) |
|------|---------------------------------------------|
| v13 | CardTransaction, CardSourceLabel (Stage 1-002) |
| v14 | Initial Scan 베이스 (Call/SMS/Package/Sim 스냅샷) |
| v15 | `blocked_identifier` (Real-time Action) |
| v16 | `phone_tag` (Tag System) |
| v17 | `feed_entry` (공개 피드 캐시) |

현재 `@Database(version = 17, exportSchema = true)` — 스키마 JSON이 `data/local-cache/schemas/.../` 다수 보존.

---

## 2. 종합 판정

| 등급 | 요약 |
|------|------|
| **구조** | v2.x **데이터·코어·Room·피드** 축은 저장소에 존재 — **"껍데기만"은 홈·첫 UX 관점 표현으로 타당** |
| **갭** | **홈 = 4카드** vs **Six Surfaces 전면 노출** 기대 · **locales_config vs 7개 strings** 불일치 |
| **다음** | Cowork `WO-V230-UX-ASSETS-AUDIT` 산출물과 병합 후, 홈 IA·문자열 로드맵 **단일 표준** 확정 권고 |

---

## 3. 자체 머지 의무 (§10-6) — 운영 메모

워크오더에 명시된 커맨드 예시:

```bash
gh pr merge <PR> --squash --delete-branch --auto
```

본 Cursor WO는 **코드 변경 없음**(문서 산출물 전용). 산출물을 담은 브랜치에 대해 PR이 열려 있으면 **헌법 §10-6**에 따라 워커가 `gh pr merge $PR_NUM --squash --delete-branch --auto`로 자체 머지한다.

---

## §9. WO 완료 보고

```markdown
### §9. WO-V230-V1-CODE-MAPPING 완료

| 항목 | 내용 |
|------|------|
| WO ID | WO-V230-V1-CODE-MAPPING |
| 선행 | PR #37 · `main` `98fc012` 조상 확인 |
| 산출물 | `docs/05_quality/v1_code_assets_mapping.md` |
| 코드 변경 | **0** |
| 12영역 | 본문 표 참조 — 홈 4카드 vs Six Surfaces · locales 이중 구조가 주요 갭 |
| 페어 WO | Cowork `WO-V230-UX-ASSETS-AUDIT` 결과와 병합 권고 |
| self_merge_executed | **YES** |
| self_merge_note | PR [#39](https://github.com/ollanvin/myphonecheck/pull/39) (`audit/v1-code-mapping` → `main`) · 스쿼시 머지 커밋 `aa5450eb` · 명령 `gh pr merge 39 --squash --delete-branch --auto` |

상태: **완료** · §10-6 자체 머지 이행
```

---

**문서 끝**
