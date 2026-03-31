package app.callcheck.mobile.core.model

/**
 * 국가별 검색 프로바이더 레지스트리.
 *
 * 190개국 × (검색엔진 우선순위 + 현지어 쿼리 + 파싱 규칙 + 2초 SLA) 전수 정의.
 *
 * 자비스 기준:
 * - 중국에서 Google 금지
 * - 한국에서 Naver 1순위
 * - 일본에서 Yahoo Japan 1순위
 * - 러시아에서 Yandex 1순위
 * - 2초 안에 결과 못 받으면 "현재까지 결과" 강제 표시
 *
 * 4-Tier 체계:
 * - Tier A: 현지 검색엔진 강국 (KR, CN, JP, RU, CZ 등)
 * - Tier B: Google + 현지 디렉토리 병행 (DE, FR, BR 등)
 * - Tier C: Google 중심 (US, CA, AU, GB 등)
 * - Tier D: Google fallback (나머지 전체)
 *
 * 100% 온디바이스 설정. 서버 전송 없음.
 */
data class CountrySearchConfig(
    /** ISO 3166-1 alpha-2 국가코드 */
    val countryCode: String,

    /** 검색 품질 티어 */
    val tier: SearchTier,

    /** 1순위 검색엔진 */
    val primaryEngine: SearchEngine,

    /** 2순위 검색엔진 */
    val secondaryEngine: SearchEngine,

    /** 3순위 — 전화번호 디렉토리/로컬 커뮤니티 */
    val tertiarySource: SearchEngine,

    /** 금지 엔진 목록 (이 국가에서 절대 사용 불가) */
    val bannedEngines: Set<SearchEngine> = emptySet(),

    /** 검색 쿼리 현지화 */
    val queryLocalization: QueryLocalization,

    /** 결과 파싱 규칙 */
    val parsingRules: ParsingRules,

    /** 타임아웃 정책 (ms) */
    val timeoutPolicy: TimeoutPolicy = TimeoutPolicy(),

    /** fallback 순서 (1순위 실패 시) */
    val fallbackOrder: List<SearchEngine> = listOf(secondaryEngine, tertiarySource),
)

/**
 * 검색 품질 티어.
 */
enum class SearchTier {
    /** 현지 검색엔진 강국 — 최고 품질 */
    TIER_A,
    /** Google + 현지 디렉토리 병행 */
    TIER_B,
    /** Google 중심 */
    TIER_C,
    /** Google fallback */
    TIER_D,
}

/**
 * 검색 엔진.
 */
enum class SearchEngine(
    val displayName: String,
    val baseUrl: String,
) {
    // ── 글로벌 ──
    GOOGLE("Google", "https://www.google.com/search"),
    BING("Bing", "https://www.bing.com/search"),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/"),

    // ── 한국 ──
    NAVER("Naver", "https://search.naver.com/search.naver"),
    DAUM("Daum", "https://search.daum.net/search"),
    NATE("Nate", "https://search.nate.com/search/all.html"),

    // ── 중국 ──
    BAIDU("Baidu", "https://www.baidu.com/s"),
    SOGOU("Sogou", "https://www.sogou.com/web"),
    QIHOO360("360 Search", "https://www.so.com/s"),

    // ── 일본 ──
    YAHOO_JAPAN("Yahoo Japan", "https://search.yahoo.co.jp/search"),

    // ── 러시아 ──
    YANDEX("Yandex", "https://yandex.ru/search/"),

    // ── 체코 ──
    SEZNAM("Seznam", "https://search.seznam.cz/"),

    // ── 전화번호 디렉토리 ──
    TRUECALLER("Truecaller", "https://www.truecaller.com/search"),
    WHITEPAGES("Whitepages", "https://www.whitepages.com/phone/"),
    THECALL_KR("더콜", "https://www.thecall.co.kr/bbs/board.php"),
    WHOSCALL("Whoscall", "https://whoscall.com/"),
    TELLOWS("Tellows", "https://www.tellows.com/num/"),
    SHOULDIANSWER("Should I Answer", "https://www.shouldianswer.com/phone-number/"),
    GETCONTACT("GetContact", "https://getcontact.com/"),
    SYNC_ME("Sync.ME", "https://sync.me/search/"),

    // ── 로컬 커뮤니티 ──
    NAVER_CAFE_KR("Naver Cafe", "https://section.cafe.naver.com/ca-fe/home/search/articles"),
    CHIEBUKURO_JP("Yahoo 知恵袋", "https://chiebukuro.yahoo.co.jp/search/"),
    BAIDU_ZHIDAO_CN("百度知道", "https://zhidao.baidu.com/search"),

    // ── fallback ──
    NONE("None", ""),
}

/**
 * 검색 쿼리 현지화.
 *
 * 국가별 현지어 쿼리 템플릿 + 키워드 사전.
 */
data class QueryLocalization(
    /** 주 언어 코드 (ISO 639-1) */
    val languageCode: String,

    /** 쿼리 템플릿 목록. {number}가 전화번호로 치환됨. */
    val queryTemplates: List<String>,

    /** 위험 키워드 (스팸, 사기, 보이스피싱 등) */
    val riskKeywords: Set<String>,

    /** 정상 업종 키워드 (택배, 병원, 은행 등) */
    val safeKeywords: Set<String>,

    /** 기관/공공 키워드 */
    val institutionKeywords: Set<String>,

    /** stopword (검색 결과에서 무시할 단어) */
    val stopwords: Set<String> = emptySet(),
)

/**
 * 결과 파싱 규칙.
 */
data class ParsingRules(
    /** 위험 키워드 가중치 (높을수록 해당 키워드 발견 시 위험도 상승) */
    val riskKeywordWeight: Float = 0.15f,

    /** 안전 키워드 가중치 */
    val safeKeywordWeight: Float = 0.10f,

    /** 기관 키워드 가중치 */
    val institutionKeywordWeight: Float = 0.12f,

    /** 검색 결과 최대 수집 건수 */
    val maxResultsToCollect: Int = 5,

    /** snippet에서 번호 매칭 확인 필요 여부 */
    val requireNumberInSnippet: Boolean = false,

    /** 신뢰도 기본 가중치 (엔진 품질에 따라 다름) */
    val baseConfidenceWeight: Float = 1.0f,
)

/**
 * 타임아웃 정책.
 *
 * 2초 SLA 강제.
 */
data class TimeoutPolicy(
    /** 1순위 엔진 타임아웃 (ms) */
    val primaryTimeoutMs: Long = 1200L,

    /** 2순위 엔진 타임아웃 (ms) */
    val secondaryTimeoutMs: Long = 600L,

    /** 3순위 소스 타임아웃 (ms) */
    val tertiaryTimeoutMs: Long = 400L,

    /** 전체 SLA 한계 (ms) — 이 시간 안에 무조건 UI 표시 */
    val hardDeadlineMs: Long = 2000L,

    /** 중간 결과 표시 시점 (ms) — 이 시간까지의 결과를 먼저 표시 */
    val earlyDisplayMs: Long = 1500L,
)
