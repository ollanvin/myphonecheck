package app.myphonecheck.mobile.feature.settings

/**
 * 사용자 영구 설정.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 고급설정 Override 정책 (기기 컨텍스트 동기화 원칙)             │
 * ├──────────────────────────────────────────────────────────────┤
 * │                                                              │
 * │ 【기본 동작: 자동 — UI 없음】                                 │
 * │ • language = "auto" → 기기 locale 자동 동기화                │
 * │ • countryOverride = null → SIM > Network > Locale 자동 탐지 │
 * │                                                              │
 * │ 【고급설정: 수동 오버라이드 — 딥 설정에서만 노출】             │
 * │ • language = "ko" / "en" / "ja" / ... → 수동 지정            │
 * │ • countryOverride = "KR" / "US" / ... → 수동 지정            │
 * │                                                              │
 * │ 【UI 노출 위치】                                              │
 * │ • 설정 > 고급 설정 > 언어 오버라이드                          │
 * │ • 설정 > 고급 설정 > 국가 오버라이드                          │
 * │ • 기본 설정 화면에는 표시하지 않음                             │
 * │                                                              │
 * │ 【연동 모듈】                                                 │
 * │ • LanguageContextProvider.setAppSettingOverride()             │
 * │   → language 필드의 값이 "auto"가 아니면 호출                │
 * │ • CountryConfigProvider.getConfig()                          │
 * │   → countryOverride가 null이 아니면 해당 국가 설정 강제 적용 │
 * │                                                              │
 * │ 【복원 동작】                                                 │
 * │ • language = "auto" 설정 시 LanguageContextProvider에        │
 * │   null을 전달하여 기기 자동 동기화 모드 복귀                   │
 * │ • countryOverride = null 설정 시 SIM/Network/Locale 자동 탐지│
 * │                                                              │
 * └──────────────────────────────────────────────────────────────┘
 */
data class AppSettings(

    /**
     * 언어 설정.
     * - "auto" (기본값): 기기 locale 자동 동기화
     * - ISO 639-1 코드 ("ko", "en", "ja", "zh", "ru", "es", "ar"): 수동 오버라이드
     */
    val language: String = "auto",

    /**
     * 국가 오버라이드.
     * - null (기본값): SIM > Network > Locale 자동 탐지
     * - ISO 3166-1 alpha-2 코드 ("KR", "US", "JP" 등): 수동 오버라이드
     */
    val countryOverride: String? = null,

    /**
     * 판정 결과 표시 수준.
     * - "normal": 표준 (카테고리 + 위험도 + 주요 신호)
     * - "detailed": 상세 (기기 기록 + 웹 스캔 + 신호 목록)
     * - "minimal": 최소 (위험도 배지만)
     */
    val evidenceDisplayLevel: String = "normal",
)
