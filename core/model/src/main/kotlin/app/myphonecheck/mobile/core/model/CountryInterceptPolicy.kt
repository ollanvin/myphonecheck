package app.myphonecheck.mobile.core.model

/**
 * 국가별 인터셉트 정책.
 *
 * 190개국 동시 출시의 핵심: 번호 파싱만이 아니라,
 * 국가별로 달라지는 인터셉트 행동을 정의하는 정책 테이블.
 *
 * 국가별로 달라지는 것:
 * - 긴급번호 목록
 * - 짧은번호 정상 여부 (서비스번호)
 * - 국제전화 빈도 (높은 국가 vs 낮은 국가)
 * - 스팸/사기 주요 패턴 시간대
 * - 위험 가중 여부 (스팸 다발 국가)
 *
 * 이 정책으로:
 * - 어떤 번호는 무조건 스킵
 * - 어떤 패턴은 위험 가중
 * - 어떤 시간대는 주의 강화
 *
 * 100% 온디바이스, 서버 전송 없음.
 */
data class CountryInterceptPolicy(
    /** ISO 3166-1 alpha-2 국가 코드 */
    val countryCode: String,

    /** 국가 전화 다이얼 코드 (예: "82", "1", "44") */
    val dialCode: String,

    /** 긴급번호 목록 — 판정/알림/오버레이 완전 스킵 */
    val emergencyNumbers: Set<String>,

    /** 서비스 단축번호 — 자동 스킵 (114, 1588-xxxx 등) */
    val serviceShortNumbers: Set<String> = emptySet(),

    /** 스킵 패턴: 이 정규식에 매칭되면 판정 스킵 (서비스번호 포맷) */
    val skipPatterns: List<Regex> = emptyList(),

    /** 위험 가중 패턴: 매칭 시 riskScore에 가산 */
    val riskPatterns: List<RiskPattern> = emptyList(),

    /** 스팸 주요 시간대 (시작 시, 종료 시). 이 시간대에는 주의 강화 */
    val spamPeakHours: IntRange? = null,

    /** 이 국가에서 국제 전화가 일상적인지 (true = 국제전화 위험 가중 비활성) */
    val internationalCallCommon: Boolean = false,

    /** 위험 가중 국가 (스팸/사기 다발) */
    val elevatedRiskCountry: Boolean = false,

    /** 스팸 다발 접두어 (예: "070" VoIP in KR) */
    val spamPrefixes: Set<String> = emptySet(),

    /** VoIP 접두어 (예: "070" in KR, "050" in JP) */
    val voipPrefixes: Set<String> = emptySet(),
)

/**
 * 번호 패턴별 위험 가중.
 */
data class RiskPattern(
    /** 매칭 정규식 (E.164 기준) */
    val pattern: Regex,
    /** riskScore에 가산할 값 */
    val riskBoost: Float,
    /** 설명 (디버그용) */
    val description: String,
)
