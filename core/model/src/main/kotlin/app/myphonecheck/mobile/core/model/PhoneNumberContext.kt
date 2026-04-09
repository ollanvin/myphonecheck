package app.myphonecheck.mobile.core.model

/**
 * 전화번호 문맥(Phone Number Context).
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 핵심 원칙: 번호 "표준화" 모듈이 아니라 번호 "문맥 빌더"      │
 * ├──────────────────────────────────────────────────────────────┤
 * │ • rawNumber는 기기 원본 — 절대 덮어쓰지 않는다              │
 * │ • deviceCanonicalNumber는 비교/중복 판별 전용                │
 * │ • searchVariants는 검색 엔진에 전달할 다양한 표현            │
 * │ • deviceCountryCode는 SIM/Network/Locale에서 자동 탐지      │
 * │ • sourceContext는 번호 유입 경로                             │
 * └──────────────────────────────────────────────────────────────┘
 *
 * 데이터 흐름:
 * ```
 * Incoming Call / CallLog / SMS / Contact
 *        │
 *        ▼
 *  rawNumber (기기 원본, 그대로 보존)
 *        │
 *        ├──▶ deviceCanonicalNumber  (E.164 또는 digits-only, 비교/중복 전용)
 *        │
 *        └──▶ searchVariants         (검색 엔진 쿼리용 변형 목록)
 *              ├─ rawNumber 그대로
 *              ├─ digits only (하이픈/공백 제거)
 *              ├─ national format
 *              ├─ E.164 format (파싱 성공 시)
 *              └─ 짧은 번호 원본 (4자리 이하)
 * ```
 */
data class PhoneNumberContext(

    /**
     * 기기가 제공한 원본 번호.
     * CallScreeningService, CallLog, SMS 등에서 그대로 가져온 값.
     * 절대 정규화하거나 덮어쓰지 않는다.
     */
    val rawNumber: String,

    /**
     * 비교/중복 판별 전용 정규화 번호.
     * - 파싱 성공 시: E.164 형식 (예: "+8215881234")
     * - 파싱 실패 시: digits-only (숫자만 추출)
     * - 빈 번호 시: 빈 문자열
     *
     * rawNumber를 대체하지 않는다. 오직 동일 번호 비교에만 사용.
     */
    val deviceCanonicalNumber: String,

    /**
     * 검색 엔진 쿼리에 사용할 번호 변형 목록.
     * 검색 정확도를 높이기 위해 여러 표현을 생성한다.
     * 최소 1개(rawNumber 자체)는 항상 포함.
     */
    val searchVariants: List<String>,

    /**
     * 기기에서 탐지된 국가 코드 (ISO 3166-1 alpha-2).
     * SIM → Network → Locale 순서로 탐지.
     * 탐지 실패 시 null.
     */
    val deviceCountryCode: String?,

    /**
     * 번호 유입 경로.
     */
    val sourceContext: NumberSourceContext,

    /**
     * libphonenumber 파싱 성공 여부.
     * false인 경우 짧은 번호(114, 1345 등)이거나 비표준 형식.
     * 검색은 파싱 실패와 무관하게 searchVariants로 진행.
     */
    val isParseable: Boolean,
)
