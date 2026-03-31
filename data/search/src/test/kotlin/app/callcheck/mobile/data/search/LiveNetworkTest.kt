package app.callcheck.mobile.data.search

/**
 * JUnit 카테고리 마커: 실제 네트워크 접근이 필요한 테스트.
 *
 * ┌─────────────────────────────────────────────────────────┐
 * │ 3층 테스트 역할 분리 구조                                │
 * ├─────────────────────────────────────────────────────────┤
 * │ 1층 VM:     Frozen/정적/빌드 → LiveNetworkTest 제외     │
 * │ 2층 로컬PC: Live Validation  → LiveNetworkTest 포함     │
 * │ 3층 테스트리그: 무인 반복     → 전체 포함                │
 * └─────────────────────────────────────────────────────────┘
 *
 * 이 인터페이스가 @Category로 붙은 테스트는:
 * - VM에서는 자동 제외 (build.gradle.kts excludeCategories 설정)
 * - 로컬 PC에서만 실행 (run_live_validation.ps1)
 *
 * 적용 대상:
 * - RealWorldValidationSetTest
 * - NationalProviderEndToEndTest
 */
interface LiveNetworkTest
