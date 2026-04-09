package app.myphonecheck.mobile.core.model

/**
 * ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
 * ┃           기기 컨텍스트 동기화 원칙 (Device Context Sync)       ┃
 * ┃                   MyPhoneCheck Global Architecture                ┃
 * ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
 *
 * MyPhoneCheck는 190개국 동시 출시를 목표로 하며,
 * 각 국가/언어/번호 형식을 앱이 자체 정의하지 않고
 * 기기에 이미 존재하는 정보에 완전히 종속하는 전략을 채택한다.
 *
 * ═══════════════════════════════════════════════════════════════
 * 원칙 1: 전화번호 — 기기 원본이 진실의 원천
 * ═══════════════════════════════════════════════════════════════
 *
 * rawNumber (기기 제공 원본)
 *   → deviceCanonicalNumber (비교/중복 전용, E.164 or digits-only)
 *   → searchVariants (검색용 변형 목록)
 *
 * • rawNumber는 절대 정규화 결과로 덮어쓰지 않는다.
 * • 짧은 번호(114, 1345 등)는 파싱 실패해도 검색은 진행한다.
 * • 번호 형식 규칙을 앱이 정의하지 않는다.
 *   libphonenumber이 파싱하면 활용하고, 못 하면 raw 그대로 사용.
 *
 * 담당 모듈: [PhoneNumberContextBuilder] (core:util)
 *
 * ═══════════════════════════════════════════════════════════════
 * 원칙 2: 국가 — SIM > Network > Locale 자동 탐지
 * ═══════════════════════════════════════════════════════════════
 *
 * 국가 코드 탐지 우선순위:
 *   1. SIM 카드 국가 (TelephonyManager.simCountryIso)
 *   2. 네트워크 국가 (TelephonyManager.networkCountryIso)
 *   3. 시스템 Locale 국가 (Locale.getDefault().country)
 *   4. "US" 폴백
 *
 * • 국가 선택 UI는 기본적으로 없다.
 * • 딥 설정에서만 수동 오버라이드 가능.
 *
 * 담당 모듈: [CountryConfigProviderImpl] (feature:country-config)
 *
 * ═══════════════════════════════════════════════════════════════
 * 원칙 3: 언어 — 기기 Locale 자동 동기화
 * ═══════════════════════════════════════════════════════════════
 *
 * 언어 결정 우선순위:
 *   1. 앱 설정 (수동 오버라이드, 딥 설정에서만)
 *   2. OS/App Locale (Configuration.getLocales())
 *   3. Device Locale (Locale.getDefault())
 *   4. EN 폴백
 *
 * 지원 언어: KO, EN, JA, ZH, RU, ES, AR (7개)
 * ZH는 향후 ZH_HANS/ZH_HANT 분리 확장 여지.
 *
 * • 언어 선택 UI는 기본적으로 없다 (기기 자동 동기화).
 * • 딥 설정에서만 수동 오버라이드 가능.
 *
 * 담당 모듈: [LanguageContextProvider] (feature:country-config)
 *
 * ═══════════════════════════════════════════════════════════════
 * 원칙 4: SignalSummary — 분석 엔진은 언어 중립 유지
 * ═══════════════════════════════════════════════════════════════
 *
 * SearchResultAnalyzer는 언어에 의존하지 않는다.
 *   → intensity 상수 키 + ConclusionCategory enum 반환
 *   → SignalSummaryLocalizer가 최종 사용자 대면 텍스트 결정
 *
 * 로컬라이저는 "번역기"가 아니라 "언어별 템플릿 선택기".
 * 각 언어는 자체 표현 체계를 갖는다.
 *
 * 담당 모듈: [SignalSummaryLocalizer] (feature:country-config)
 *
 * ═══════════════════════════════════════════════════════════════
 * 전체 데이터 흐름 (Global Pipeline)
 * ═══════════════════════════════════════════════════════════════
 *
 * ```
 * [Incoming Call]
 *       │
 *       ▼
 * PhoneNumberContextBuilder.build(rawNumber, deviceCountryCode, INCOMING_CALL)
 *       │  → PhoneNumberContext (raw + canonical + searchVariants)
 *       ▼
 * CountrySearchRouter.route(phoneNumberContext)
 *       │  → 국가별 검색 전략 결정
 *       ▼
 * SearchProvider.search(searchVariants)
 *       │  → SearchEvidence 수집
 *       ▼
 * SearchResultAnalyzer.analyze(evidences, keywordDictionary)
 *       │  → SignalSummary (intensity key + category)
 *       ▼
 * SignalSummaryLocalizer.localize(intensityKey, categoryKey, language)
 *       │  → 사용자 대면 로컬라이즈 텍스트
 *       ▼
 * CallerIdOverlay / Notification
 * ```
 *
 * ═══════════════════════════════════════════════════════════════
 * 연동 현황 (Cycle 9 기준)
 * ═══════════════════════════════════════════════════════════════
 *
 * 구현 완료:
 * - PhoneNumberContextBuilder (core:util) — 28 tests, 0 fail
 * - SupportedLanguage enum (feature:country-config) — 18 tests, 0 fail
 * - LanguageContextProvider interface + Impl (feature:country-config) — 10 tests, 0 fail
 * - SignalSummaryLocalizer (feature:country-config) — 24 tests, 0 fail
 *
 * 연동 대기 (에뮬레이터/실기기 검증 필요):
 * - MyPhoneCheckScreeningService에 PhoneNumberContextBuilder 적용
 * - CallerIdOverlayManager에 SignalSummaryLocalizer 적용
 * - DecisionNotificationManager에 SignalSummaryLocalizer 적용
 *
 * ⚠️ 런타임 연동은 에뮬레이터에서 별도 재검증 필요.
 */
object DeviceContextSyncPrinciple
