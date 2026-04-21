# Architecture Conformance Report — MyPhoneCheck Base Architecture v1

**Repository (browse):** https://github.com/ollanvin/myphonecheck  
**Blob URL 규칙:** 아래 모든 파일 링크는 `https://github.com/ollanvin/myphonecheck/blob/main/<리포지토리 루트 기준 경로>` 형식이다. 브랜치가 `main`이 아니면 URL의 `main`을 해당 브랜치명으로 바꾼다.

**본 보고서 파일:** https://github.com/ollanvin/myphonecheck/blob/main/docs/05_quality/reports/architecture_conformance_myphonecheck_base_architecture_v1.md

---

## 1. Report purpose

이 보고서는 고정 기준 문서 [myphonecheck_base_architecture_v1.md](https://github.com/ollanvin/myphonecheck/blob/main/docs/01_architecture/myphonecheck_base_architecture_v1.md)(이하 **Base Architecture**)와 **현재 로컬 워크트리**의 앱 코드·리소스·구성·문서를 대조하여 **구현 적합성(conformance)** 을 판정한다.

- **분석 시점:** 로컬 워크트리 (원격 저장소 https://github.com/ollanvin/myphonecheck 와 동기화 여부는 브랜치·푸시 상태에 따름).
- **판정 스케일:** 일치 / 부분 일치 / 불일치 / 미구현 / 근거 부족.
- **코드 변경 없음:** 본 문서만 산출한다.

---

## 2. Reviewed scope

| 영역 | 포함 (절대 URL) |
|------|-----------------|
| 앱 코드 | [app/](https://github.com/ollanvin/myphonecheck/tree/main/app), [feature/](https://github.com/ollanvin/myphonecheck/tree/main/feature), [core/](https://github.com/ollanvin/myphonecheck/tree/main/core), [data/](https://github.com/ollanvin/myphonecheck/tree/main/data), [build-logic/](https://github.com/ollanvin/myphonecheck/tree/main/build-logic) |
| 리소스 | [app/src/main/res/](https://github.com/ollanvin/myphonecheck/tree/main/app/src/main/res) |
| 구성 | [settings.gradle.kts](https://github.com/ollanvin/myphonecheck/blob/main/settings.gradle.kts), [app/build.gradle.kts](https://github.com/ollanvin/myphonecheck/blob/main/app/build.gradle.kts), [AndroidManifest.xml](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/AndroidManifest.xml), [gradle/libs.versions.toml](https://github.com/ollanvin/myphonecheck/blob/main/gradle/libs.versions.toml) |
| 문서 | [docs/](https://github.com/ollanvin/myphonecheck/tree/main/docs), 특히 [01_architecture/](https://github.com/ollanvin/myphonecheck/tree/main/docs/01_architecture), [02_product/](https://github.com/ollanvin/myphonecheck/tree/main/docs/02_product), [FILE_INDEX.md](https://github.com/ollanvin/myphonecheck/blob/main/docs/03_engineering/index/FILE_INDEX.md) |

**제외:** 원격 GitHub와의 커밋 단위 동일성 검증, 런타임 동작·에뮬 검증.

---

## 3. Fixed architecture reference

**기준 문서:** [docs/01_architecture/myphonecheck_base_architecture_v1.md](https://github.com/ollanvin/myphonecheck/blob/main/docs/01_architecture/myphonecheck_base_architecture_v1.md)

핵심 기준 요약:

- **제품:** 결정 지원 앱, **4 체크 유닛**(Call / Message / Mic / Camera), 190국+ 론칭 목표.
- **철학:** 증거 제시, 사용자 최종 결정; 외부 검색 + 내부 이력; 국가 차이는 정책·표현.
- **IA:** Home, Settings, **유닛별 상세(히스토리/타임라인)**.
- **초기 스캔:** 권한 직후 즉시 — **통화·문자·마이크·카메라** 범위 명시.
- **레이어:** Presentation / Decision·Domain / Data / Device Integration.
- **구독:** 글로벌 단일, **~1.5 USD**, **첫 1개월 무료**, **31일째 과금**, 디바이스당 무료 1회 1개월.

보조 참고(충돌 시 Base Architecture 우선): [global-single-core-system.md](https://github.com/ollanvin/myphonecheck/blob/main/docs/01_architecture/global-single-core-system.md), [PRD_CALLCHECK_V1.md](https://github.com/ollanvin/myphonecheck/blob/main/docs/02_product/specs/PRD_CALLCHECK_V1.md).

---

## 4. Repository structure summary

- **모듈:** [settings.gradle.kts](https://github.com/ollanvin/myphonecheck/blob/main/settings.gradle.kts) — `app`, `core:model|util|security`, `feature:call-intercept|device-evidence|search-enrichment|decision-engine|decision-ui|settings|billing|country-config|push-intercept|message-intercept|privacy-check`, `data:contacts|calllog|sms|search|local-cache`, `build-logic`.
- **내비게이션:** [MyPhoneCheckNavHost.kt](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/kotlin/app/myphonecheck/mobile/navigation/MyPhoneCheckNavHost.kt) — 온보딩 → `home` / 바텀 `home`·`timeline`·`settings`, 라우트 `engine/call`, `message-hub`, `camera-check`, `mic-check`, `call-detail/{number}` 등.
- **Claude/에이전트 흔적:** [FILE_INDEX.md](https://github.com/ollanvin/myphonecheck/blob/main/docs/03_engineering/index/FILE_INDEX.md) 등에 생성 이력 언급.

---

## 5. Architecture conformance summary

| Base 섹션 | 판정 | 한 줄 |
|-----------|------|--------|
| 4 체크 유닛(제품 정의) | **부분 일치** | 홈 2×2 카드로 4유닛 노출; 온보딩·엔진 라우트에 **Push** 흔적 잔존. |
| 철학(증거·비대행) | **부분 일치** | [MyPhoneCheckScreeningService.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/call-intercept/src/main/kotlin/app/myphonecheck/mobile/feature/callintercept/MyPhoneCheckScreeningService.kt), [CallInterceptRepositoryImpl.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/call-intercept/src/main/kotlin/app/myphonecheck/mobile/feature/callintercept/CallInterceptRepositoryImpl.kt), [DecisionEngineImpl.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/decision-engine/src/main/kotlin/app/myphonecheck/mobile/feature/decisionengine/DecisionEngineImpl.kt)이 결정 보조에 맞음; 권한·스토어 정책으로 **디바이스 통화/문자 원천**과 긴장. |
| IA(Home·Settings·유닛 상세) | **부분 일치** | Home·Settings·유닛 상세·통합 Timeline 존재; Base의 “유닛별 타임라인”과 **완전 1:1**은 아님. |
| 태그 시스템 | **부분 일치** | Room·모델에 태그·프로필 구조 있음; “한 화면 근거 묶음”은 **구현 깊이별 상이**. |
| 초기 스캔 | **불일치** | 코드상 초기 스캔은 **baseline + Mic/Camera** 중심; Base §8의 **통화·문자 이력을 동일 배치에 포함**하는 설명과 불일치. |
| 레이어 4분 | **일치** | 모듈 경계가 Presentation·Decision·Data·Device에 대응. |
| 글로벌 확장 | **부분 일치** | [SearchProviderRegistry.kt](https://github.com/ollanvin/myphonecheck/blob/main/core/model/src/main/kotlin/app/myphonecheck/mobile/core/model/SearchProviderRegistry.kt), `CountrySearchConfig`, `CountryConfigProvider`, 다국어 `values-*` ([res 트리](https://github.com/ollanvin/myphonecheck/tree/main/app/src/main/res)) 존재. |
| 구독 모델 | **부분 일치** | Play Billing·체험 카운트다운 있음; **1.5 USD·31일째 과금**은 코드 상수로 확정되지 않음. |
| 제로 비용·제로 저장 | **검토 대상** | 온디바이스 Room·백업·검색 네트워크 등 Base §12·§13과 **정의 정합 필요**. |

**총평(한 줄):**  
4유닛·오버레이·판단 파이프라인·태그·글로벌 검색 골격은 **대체로 Base 방향과 맞으나**, **초기 스캔 범위**, **통화/문자 권한 정책과 디바이스 증거**, **구독 수치·온보딩 서사(4위협 vs 4유닛)** 에서 **명시적 간극**이 있다.

---

## 6. Unit-by-unit findings

### 6.1 Call Check

| Base 요구 | 판정 | 근거 |
|-----------|------|------|
| 수신 2~3초 내 오버레이 | **부분 일치** | [CallOverlayContent.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/decision-ui/src/main/kotlin/app/myphonecheck/mobile/feature/decisionui/overlay/CallOverlayContent.kt) 주석(수신 시 오버레이), [AndroidManifest.xml](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/AndroidManifest.xml) `SYSTEM_ALERT_WINDOW`, [MyPhoneCheckScreeningService.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/call-intercept/src/main/kotlin/app/myphonecheck/mobile/feature/callintercept/MyPhoneCheckScreeningService.kt)의 `SCREENING_TIMEOUT_MS = 4500L`(목표 2~3초와 상수는 별도 검증 필요). |
| 외부 검색 + 내부 이력 + 태그 | **부분 일치** | [CallInterceptRepositoryImpl.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/call-intercept/src/main/kotlin/app/myphonecheck/mobile/feature/callintercept/CallInterceptRepositoryImpl.kt)이 디바이스 증거 + 검색 + 학습 신호; [NumberProfileEntity.kt](https://github.com/ollanvin/myphonecheck/blob/main/data/local-cache/src/main/kotlin/app/myphonecheck/mobile/data/localcache/entity/NumberProfileEntity.kt) `quickLabels`, [UserCallRecord.kt](https://github.com/ollanvin/myphonecheck/blob/main/data/local-cache/src/main/kotlin/app/myphonecheck/mobile/data/localcache/entity/UserCallRecord.kt) 태그는 Data에 존재. **READ_CALL_LOG 제거**([AndroidManifest.xml](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/AndroidManifest.xml))로 **시스템 통화 로그 기반 이력**은 정책상 제한 가능. |
| 수신/거절/차단 | **부분 일치** | 오버레이·알림 액션 문자열 `overlay_action_*` — [strings.xml](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/res/values/strings.xml); 실제 통화 제어는 OS·권한에 종속. |

### 6.2 Message Check

| Base 요구 | 판정 | 근거 |
|-----------|------|------|
| 문자 수신 후 2~3초 내 결과 | **부분 일치** | [SmsInterceptReceiver.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/message-intercept/src/main/kotlin/app/myphonecheck/mobile/feature/messageintercept/SmsInterceptReceiver.kt)가 수신 처리·판정·저장; 지연 목표는 상수로 Base와 1:1 매칭되지 않음. |
| 발신 번호 중심 | **일치** | `processSms` 흐름에서 발신 번호 기준. |
| 수신/거절/차단/태그 | **부분 일치** | MessageHub·엔티티 존재; **READ_SMS 제거**([AndroidManifest.xml](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/AndroidManifest.xml)) 주석으로 SMS **본문/메타** 접근이 제한될 수 있음 — `SmsMetadataDataSource` 동작은 권한과 결합. |

### 6.3 Mic Check

| Base 요구 | 판정 | 근거 |
|-----------|------|------|
| 마이크 권한 앱 목록 | **일치** | [MicCheckScanner.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/privacy-check/src/main/kotlin/app/myphonecheck/mobile/feature/privacycheck/MicCheckScanner.kt) — `PackageManager`/`AppOpsManager` 기반. |
| 최근 사용 기록 | **일치** | `UsageStatsManager` 경로([MicCheckScanner.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/privacy-check/src/main/kotlin/app/myphonecheck/mobile/feature/privacycheck/MicCheckScanner.kt) + [PrivacyScannerEngine.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/privacy-check/src/main/kotlin/app/myphonecheck/mobile/feature/privacycheck/PrivacyScannerEngine.kt)). |
| 권한/차단/사용 기록 | **부분 일치** | 권한·사용 기록 **근거 있음**; **앱별 차단 UI**는 본 섹션 스캔 범위에서 **명시적 근거 부족**(상세 리스트는 [MyPhoneCheckNavHost.kt](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/kotlin/app/myphonecheck/mobile/navigation/MyPhoneCheckNavHost.kt) 내 `SensorCheckDetailScreen`에 granted/recent 위주). |
| 앱 vs 사용자 직접 사용 구분 | **근거 부족** | 스캐너는 패키지·usage 중심; Base 문구의 “사용자 직접 사용” 구분을 입증하는 **별도 데이터 모델 명칭**은 추가 확인 필요. |

### 6.4 Camera Check

| Base 요구 | 판정 | 근거 |
|-----------|------|------|
| 카메라 권한 앱 목록 등 | **Mic와 동형** | [CameraCheckScanner.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/privacy-check/src/main/kotlin/app/myphonecheck/mobile/feature/privacycheck/CameraCheckScanner.kt)(동일 패턴), [InitialScanOrchestrator.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/privacy-check/src/main/kotlin/app/myphonecheck/mobile/feature/privacycheck/InitialScanOrchestrator.kt)에서 Camera 병렬 스캔. |
| 차단 기능 | **부분 일치·근거 부족** | Mic와 동일. |

---

## 7. Cross-cutting findings

### 7.1 Tag system

| 항목 | 판정 | 근거 |
|------|------|------|
| 연락처 없이 앱 내부 관계 | **일치** | [NumberProfileEntity.kt](https://github.com/ollanvin/myphonecheck/blob/main/data/local-cache/src/main/kotlin/app/myphonecheck/mobile/data/localcache/entity/NumberProfileEntity.kt)(`quick_labels`, `user_memo_short`), [UserCallRecord.kt](https://github.com/ollanvin/myphonecheck/blob/main/data/local-cache/src/main/kotlin/app/myphonecheck/mobile/data/localcache/entity/UserCallRecord.kt)(tag, memo), [DetailTagEntity.kt](https://github.com/ollanvin/myphonecheck/blob/main/data/local-cache/src/main/kotlin/app/myphonecheck/mobile/data/localcache/entity/DetailTagEntity.kt), [QuickLabel.kt](https://github.com/ollanvin/myphonecheck/blob/main/data/local-cache/src/main/kotlin/app/myphonecheck/mobile/data/localcache/entity/QuickLabel.kt) enum. |
| 외부 동기화 분리 | **부분 일치** | Room 온디바이스 저장 명시([UserCallRecord.kt](https://github.com/ollanvin/myphonecheck/blob/main/data/local-cache/src/main/kotlin/app/myphonecheck/mobile/data/localcache/entity/UserCallRecord.kt) 주석); 백업 UI는 [strings.xml](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/res/values/strings.xml) `backup_restore_*` — **“외부 주소록과 분리”**와 **백업 기능**의 제품 메시지 정합은 별도 검토. |
| 수신 시 검색+이력+태그 한 화면 | **부분 일치** | `CallDetailScreen` 등([MyPhoneCheckNavHost.kt](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/kotlin/app/myphonecheck/mobile/navigation/MyPhoneCheckNavHost.kt))에서 결합 가능; 완전한 “단일 카드” 수준은 화면별 상이. |

### 7.2 Initial scan

| Base 요구 | 판정 | 근거 |
|-----------|------|------|
| 설치 직후 권한 시 즉시 | **부분 일치** | [MyPhoneCheckApplication.kt](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/kotlin/app/myphonecheck/mobile/MyPhoneCheckApplication.kt)에서 `initialScanOrchestrator.runInitialScan()` 호출. |
| 통화·문자·마이크·카메라 포함 | **불일치** | [InitialScanOrchestrator.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/privacy-check/src/main/kotlin/app/myphonecheck/mobile/feature/privacycheck/InitialScanOrchestrator.kt) — **DeviceBaseline + Camera + Mic**만 수행; **통화/문자 이력 벌크 스캔**은 본 오케스트레이터에 **없음**. |

### 7.3 Overlay / interaction

| 항목 | 판정 | 근거 |
|------|------|------|
| 통화 오버레이 | **일치** | [AndroidManifest.xml](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/AndroidManifest.xml) `SYSTEM_ALERT_WINDOW`, [CallOverlayContent.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/decision-ui/src/main/kotlin/app/myphonecheck/mobile/feature/decisionui/overlay/CallOverlayContent.kt), [MyPhoneCheckScreeningService.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/call-intercept/src/main/kotlin/app/myphonecheck/mobile/feature/callintercept/MyPhoneCheckScreeningService.kt) 코멘트·오버레이 매니저 연동. |
| 문자 UI | **부분 일치** | Message 허브·수신 처리 존재; Call과 동일 UX인 “오버레이”와의 동등성은 **부분**. |

### 7.4 Local / device-first data usage

| 항목 | 판정 | 근거 |
|------|------|------|
| 온디바이스 우선 | **부분 일치** | [DeviceEvidenceRepositoryImpl.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/device-evidence/src/main/kotlin/app/myphonecheck/mobile/feature/deviceevidence/DeviceEvidenceRepositoryImpl.kt), Room, 스캐너 “온디바이스 전용” 주석([MicCheckScanner.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/privacy-check/src/main/kotlin/app/myphonecheck/mobile/feature/privacycheck/MicCheckScanner.kt)). |
| 통화/SMS 원천 | **불일치·긴장** | Manifest: `READ_CALL_LOG`, `READ_SMS` **DENY**([AndroidManifest.xml](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/AndroidManifest.xml)); [DeviceEvidenceRepositoryImpl.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/device-evidence/src/main/kotlin/app/myphonecheck/mobile/feature/deviceevidence/DeviceEvidenceRepositoryImpl.kt)은 여전히 `CallLogDataSource`·`SmsMetadataDataSource` 사용 — **권한 거부 시 증거 공백** 가능. |

### 7.5 Global expansion readiness

| 항목 | 판정 | 근거 |
|------|------|------|
| 로케일/언어 | **일치** | [app/src/main/res](https://github.com/ollanvin/myphonecheck/tree/main/app/src/main/res) 하위 `values/`, `values-ko/`, `values-ja/` 등; `LanguageContextProvider`([country-config 모듈](https://github.com/ollanvin/myphonecheck/tree/main/feature/country-config/src/main/kotlin/app/myphonecheck/mobile/feature/countryconfig)). |
| 검색 플랫폼 | **일치** | [SearchProviderRegistry.kt](https://github.com/ollanvin/myphonecheck/blob/main/core/model/src/main/kotlin/app/myphonecheck/mobile/core/model/SearchProviderRegistry.kt), `CountrySearchConfig` 문서화된 티어. |
| 국가별 표현 | **부분 일치** | [feature/country-config](https://github.com/ollanvin/myphonecheck/tree/main/feature/country-config), 정책 프로바이더; 완전 커버리지는 코드만으로 단정하지 않음. |

### 7.6 Subscription / readiness traces

| Base 요구 | 판정 | 근거 |
|-----------|------|------|
| 글로벌 단일 요금 | **부분 일치** | [BillingManager.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/billing/src/main/kotlin/app/myphonecheck/mobile/feature/billing/BillingManager.kt) `SUBSCRIPTION_PRODUCT_ID = "myphonecheck_monthly"`. |
| ~1.5 USD | **근거 부족** | 가격은 Play 콘솔·`ProductDetails`; 저장소 Kotlin에 **고정 USD 문자열 없음**. |
| 첫 1개월 무료 / 31일째 과금 | **불일치** | [TrialCountdown.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/billing/src/main/kotlin/app/myphonecheck/mobile/feature/billing/TrialCountdown.kt) `DEFAULT_TRIAL_DAYS = 30`; **31일째** 과금 로직은 본 파일에서 **확인되지 않음**. |
| 디바이스당 1회 1개월 | **근거 부족** | 별도 정책 모듈 추가 확인 필요. |

---

## 8. UI / asset findings

| 항목 | 판정 | 근거 |
|------|------|------|
| App icon | **일치** | [AndroidManifest.xml](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/AndroidManifest.xml) `android:icon="@mipmap/ic_launcher"` — [mipmap 리소스](https://github.com/ollanvin/myphonecheck/tree/main/app/src/main/res/mipmap-anydpi-v26). |
| Splash / launcher | **부분 일치** | 런처 아이콘·테마 지정됨; **별도 스플래시 Activity**는 본 스코프에서 미확인. |
| Onboarding | **부분 일치** | `OnboardingScreen`([MyPhoneCheckNavHost.kt](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/kotlin/app/myphonecheck/mobile/navigation/MyPhoneCheckNavHost.kt)), [strings.xml](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/res/values/strings.xml) `onboarding_page*`; **4대 위협에 Push 포함**(`threat_push_title`) — Base **4유닛(Call/Message/Mic/Camera)** 과 **서사 불일치**. |
| Home | **일치** | `HomeScreen` 2×2: Call, Message, Camera, Mic — 동일 파일 [MyPhoneCheckNavHost.kt](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/kotlin/app/myphonecheck/mobile/navigation/MyPhoneCheckNavHost.kt). |
| Settings | **일치** | `settings` 라우트·[SettingsScreen](https://github.com/ollanvin/myphonecheck/blob/main/feature/settings/src/main/kotlin/app/myphonecheck/mobile/feature/settings/SettingsScreen.kt)(모듈 내). |
| Timeline / history | **부분 일치** | 바텀 `Timeline`, `TimelineScreen`, `call-detail`; **유닛별 전용 타임라인**과는 구조적으로 다름 — [MyPhoneCheckNavHost.kt](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/kotlin/app/myphonecheck/mobile/navigation/MyPhoneCheckNavHost.kt). |

---

## 9. Gap list

### 9.1 Missing (Base 대비)

- 초기 스캔에 **통화·문자 이력 벌크 수집**을 Base §8처럼 **동일 오케스트레이션에 편입**한 흔적 — **미구현**(현 [InitialScanOrchestrator.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/privacy-check/src/main/kotlin/app/myphonecheck/mobile/feature/privacycheck/InitialScanOrchestrator.kt) 범위 외).
- Base 구독의 **31일째 과금·1.5 USD**를 코드 상수·문서화된 빌드 설정으로 **확인할 수 없음** — **근거 부족**.

### 9.2 Partial

- Call/Message **디바이스 증거** vs **권한 DENY**([AndroidManifest.xml](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/AndroidManifest.xml)).
- Mic/Camera **차단 기능** UI·설정.
- **태그+검색+이력**의 단일 화면 통합 수준.
- **체험 30일**([TrialCountdown.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/billing/src/main/kotlin/app/myphonecheck/mobile/feature/billing/TrialCountdown.kt)) vs Base **31일 과금** 표현.

### 9.3 Conflicting

- Base §8 초기 스캔 범위 vs [InitialScanOrchestrator.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/privacy-check/src/main/kotlin/app/myphonecheck/mobile/feature/privacycheck/InitialScanOrchestrator.kt) 실제 범위.
- 온보딩 **4위협(Push 포함)**([strings.xml](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/res/values/strings.xml)) vs Base **4체크 유닛**(Push 비핵심).
- Base 구독 수치·일차 vs `DEFAULT_TRIAL_DAYS = 30`([TrialCountdown.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/billing/src/main/kotlin/app/myphonecheck/mobile/feature/billing/TrialCountdown.kt)) 구현.

---

## 10. Risk list

1. **권한 정책(v4.3 DENY)과 PRD/Base의 “통화·문자 이력” 기대** 불일치로 **판단 품질·신뢰** 리스크 — [AndroidManifest.xml](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/AndroidManifest.xml), [DeviceEvidenceRepositoryImpl.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/device-evidence/src/main/kotlin/app/myphonecheck/mobile/feature/deviceevidence/DeviceEvidenceRepositoryImpl.kt).
2. **초기 스캔 범위 불일치**로 사용자 기대(“설치 직후 전체 베이스”)와 실제 **Mic/Camera 중심** 괴리 — [InitialScanOrchestrator.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/privacy-check/src/main/kotlin/app/myphonecheck/mobile/feature/privacycheck/InitialScanOrchestrator.kt).
3. **구독·체험 기간** 문서·스토어·앱 내 숫자 **불일치** 시 스토어 정책·CS 리스크 — [TrialCountdown.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/billing/src/main/kotlin/app/myphonecheck/mobile/feature/billing/TrialCountdown.kt), [BillingManager.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/billing/src/main/kotlin/app/myphonecheck/mobile/feature/billing/BillingManager.kt).
4. **온보딩 서사(Push)** 와 **제품 4유닛** 불일치로 **메시징 혼선** 리스크 — [strings.xml](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/res/values/strings.xml).
5. **백업/암호화**(`backup_restore_*`, [strings.xml](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/res/values/strings.xml)) 문구와 Base **제로 저장 재검토** 항목 간 **정합** 필요.

---

## 11. Next development priorities

1. **초기 스캔:** Base §8과 구현을 맞출지, Base 문서를 현재 [InitialScanOrchestrator.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/privacy-check/src/main/kotlin/app/myphonecheck/mobile/feature/privacycheck/InitialScanOrchestrator.kt)에 맞출지 **의사결정·단일 문서화**.
2. **READ_CALL_LOG / READ_SMS:** 정책·스토어·증거 파이프라인 **트레이드오프 문서화** 후 [DeviceEvidenceRepositoryImpl.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/device-evidence/src/main/kotlin/app/myphonecheck/mobile/feature/deviceevidence/DeviceEvidenceRepositoryImpl.kt) 입력 계약 고정.
3. **온보딩:** 4위협 카피를 **Call / Message / Mic / Camera** 로 정렬하거나 Base를 “위협 카테고리”로 확장해 **한 축으로 통일** — [strings.xml](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/res/values/strings.xml).
4. **구독:** Play 가격·무료 기간·과금 시작일을 Base §11과 **동기화**(코드 상수 또는 원격 설정 + 문서) — [BillingManager.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/billing/src/main/kotlin/app/myphonecheck/mobile/feature/billing/BillingManager.kt), [TrialCountdown.kt](https://github.com/ollanvin/myphonecheck/blob/main/feature/billing/src/main/kotlin/app/myphonecheck/mobile/feature/billing/TrialCountdown.kt).
5. **유닛별 히스토리:** Base IA와 같이 **유닛 전용 타임라인**을 둘지, 현 **통합 Timeline**([MyPhoneCheckNavHost.kt](https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/kotlin/app/myphonecheck/mobile/navigation/MyPhoneCheckNavHost.kt))을 기준으로 Base를 개정할지 결정.

---

## 12. Appendix: file evidence map

아래는 주제별로 브라우저에 그대로 붙여 넣을 수 있는 **blob** 또는 **tree** URL이다.

| 주제 | URL |
|------|-----|
| Base 기준 | https://github.com/ollanvin/myphonecheck/blob/main/docs/01_architecture/myphonecheck_base_architecture_v1.md |
| 글로벌 싱글 코어 | https://github.com/ollanvin/myphonecheck/blob/main/docs/01_architecture/global-single-core-system.md |
| 모듈 구조 | https://github.com/ollanvin/myphonecheck/blob/main/settings.gradle.kts |
| 권한·오버레이 | https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/AndroidManifest.xml |
| 내비·홈 4카드 | https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/kotlin/app/myphonecheck/mobile/navigation/MyPhoneCheckNavHost.kt |
| 통화 스크리닝 | https://github.com/ollanvin/myphonecheck/blob/main/feature/call-intercept/src/main/kotlin/app/myphonecheck/mobile/feature/callintercept/MyPhoneCheckScreeningService.kt |
| 인터셉트 파이프라인 | https://github.com/ollanvin/myphonecheck/blob/main/feature/call-intercept/src/main/kotlin/app/myphonecheck/mobile/feature/callintercept/CallInterceptRepositoryImpl.kt |
| 판정 엔진 | https://github.com/ollanvin/myphonecheck/blob/main/feature/decision-engine/src/main/kotlin/app/myphonecheck/mobile/feature/decisionengine/DecisionEngineImpl.kt |
| 디바이스 증거 | https://github.com/ollanvin/myphonecheck/blob/main/feature/device-evidence/src/main/kotlin/app/myphonecheck/mobile/feature/deviceevidence/DeviceEvidenceRepositoryImpl.kt |
| 문자 수신 | https://github.com/ollanvin/myphonecheck/blob/main/feature/message-intercept/src/main/kotlin/app/myphonecheck/mobile/feature/messageintercept/SmsInterceptReceiver.kt |
| 앱 진입·초기 스캔 트리거 | https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/kotlin/app/myphonecheck/mobile/MyPhoneCheckApplication.kt |
| 초기 스캔 오케스트레이터 | https://github.com/ollanvin/myphonecheck/blob/main/feature/privacy-check/src/main/kotlin/app/myphonecheck/mobile/feature/privacycheck/InitialScanOrchestrator.kt |
| Mic 스캔 | https://github.com/ollanvin/myphonecheck/blob/main/feature/privacy-check/src/main/kotlin/app/myphonecheck/mobile/feature/privacycheck/MicCheckScanner.kt |
| Camera 스캔 | https://github.com/ollanvin/myphonecheck/blob/main/feature/privacy-check/src/main/kotlin/app/myphonecheck/mobile/feature/privacycheck/CameraCheckScanner.kt |
| 태그 enum | https://github.com/ollanvin/myphonecheck/blob/main/data/local-cache/src/main/kotlin/app/myphonecheck/mobile/data/localcache/entity/QuickLabel.kt |
| 번호 프로필 | https://github.com/ollanvin/myphonecheck/blob/main/data/local-cache/src/main/kotlin/app/myphonecheck/mobile/data/localcache/entity/NumberProfileEntity.kt |
| 사용자 통화 기록 | https://github.com/ollanvin/myphonecheck/blob/main/data/local-cache/src/main/kotlin/app/myphonecheck/mobile/data/localcache/entity/UserCallRecord.kt |
| 상세 태그 | https://github.com/ollanvin/myphonecheck/blob/main/data/local-cache/src/main/kotlin/app/myphonecheck/mobile/data/localcache/entity/DetailTagEntity.kt |
| 검색 글로벌 레지스트리 | https://github.com/ollanvin/myphonecheck/blob/main/core/model/src/main/kotlin/app/myphonecheck/mobile/core/model/SearchProviderRegistry.kt |
| Billing | https://github.com/ollanvin/myphonecheck/blob/main/feature/billing/src/main/kotlin/app/myphonecheck/mobile/feature/billing/BillingManager.kt |
| 체험 카운트다운 | https://github.com/ollanvin/myphonecheck/blob/main/feature/billing/src/main/kotlin/app/myphonecheck/mobile/feature/billing/TrialCountdown.kt |
| 오버레이 UI | https://github.com/ollanvin/myphonecheck/blob/main/feature/decision-ui/src/main/kotlin/app/myphonecheck/mobile/feature/decisionui/overlay/CallOverlayContent.kt |
| 문자열·온보딩 | https://github.com/ollanvin/myphonecheck/blob/main/app/src/main/res/values/strings.xml |
| 에이전트 산출 흔적 | https://github.com/ollanvin/myphonecheck/blob/main/docs/03_engineering/index/FILE_INDEX.md |
| 리포지토리 루트(트리) | https://github.com/ollanvin/myphonecheck/tree/main |

---

*End of report.*
