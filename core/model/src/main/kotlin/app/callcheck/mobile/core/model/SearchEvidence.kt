package app.callcheck.mobile.core.model

/**
 * 검색 결과에서 추출된 의미 신호 요약.
 *
 * provider 이름은 노출하지 않는다. 사용자에게는 "의미"만 보여준다.
 * ❌ "Google 3건, Naver 2건"
 * ✅ "외교부 대표번호 가능성", "사기/피싱 신고 다수"
 *
 * 검색 엔진은 "데이터 소스"일 뿐, UI에 노출되는 대상이 아님.
 */
data class SignalSummary(
    val signalDescription: String,  // "사기/피싱 신고 다수", "KT 고객센터 가능성" 등
    val resultCount: Int,           // 해당 신호를 뒷받침하는 결과 수
    val topSnippet: String?,        // 대표 스니펫 (1줄)
    val signalType: String?,        // "SPAM_REPORT", "OFFICIAL", "COMMUNITY" 등
)

/**
 * 인접 번호 참고 힌트.
 *
 * 수신된 번호의 정확 검색 결과가 없을 때,
 * 끝자리가 유사한 인접 번호의 검색 결과를 참고 정보로 제공합니다.
 *
 * UI 표시 예시:
 * "유사 번호 02-1234-5670이 '서울시청'으로 검색됩니다 (결과 3건)"
 * "이 번호와 같은 대역(끝 1자리)의 번호가 검색된 결과입니다"
 *
 * 주의: 이 정보는 "참고"이며, 확정 판단이 아닙니다.
 * 사용자가 최종 판단을 내릴 수 있도록 보조 정보로만 사용합니다.
 */
data class AdjacentNumberHint(
    /** 검색된 인접 번호의 대표 엔티티 (예: "서울시청", "KT 고객센터") */
    val matchedEntity: String?,

    /** 인접 번호 검색 결과 건수 */
    val resultCount: Int,

    /** 인접 범위 설명 (예: "끝 1자리 대역 (10번호)") */
    val rangeDescription: String,

    /** 인접 번호 검색에서 추출된 키워드 클러스터 */
    val keywordClusters: List<String>,

    /** 인접 번호 검색 대표 스니펫 (최대 3개) */
    val topSnippets: List<String>,

    /** 인접 번호 검색의 신호 요약 */
    val signalSummaries: List<SignalSummary>,
)

/**
 * Evidence gathered from on-device web scan enrichment.
 *
 * This model normalizes raw search results into decision-ready signals:
 * search intensity, keyword clusters, repeated entities, and source types.
 *
 * signalSummaries: 검색 결과에서 추출한 의미 신호 목록.
 * provider 이름은 내부 처리에만 사용하며 UI에 절대 노출하지 않음.
 */
data class SearchEvidence(
    // Search intensity (approximate result count from providers)
    val recent30dSearchIntensity: Int?,
    val recent90dSearchIntensity: Int?,
    val searchTrend: SearchTrend,

    // Keyword clusters found in search results
    val keywordClusters: List<String>,

    // Repeated entity names (company, brand, courier, institution)
    val repeatedEntities: List<String>,

    // Source types where this number appears
    val sourceTypes: List<String>,

    // Top snippets for display (max 5)
    val topSnippets: List<String>,

    // Per-provider result summaries for transparent display
    val signalSummaries: List<SignalSummary> = emptyList(),

    // 인접 번호 참고 힌트 (정확 검색 결과 0건 시 제공)
    val adjacentNumberHint: AdjacentNumberHint? = null,
) {
    companion object {
        fun empty() = SearchEvidence(
            recent30dSearchIntensity = null,
            recent90dSearchIntensity = null,
            searchTrend = SearchTrend.NONE,
            keywordClusters = emptyList(),
            repeatedEntities = emptyList(),
            sourceTypes = emptyList(),
            topSnippets = emptyList(),
            signalSummaries = emptyList(),
            adjacentNumberHint = null,
        )
    }

    val isEmpty: Boolean
        get() = keywordClusters.isEmpty() && repeatedEntities.isEmpty() &&
                topSnippets.isEmpty() && recent30dSearchIntensity == null &&
                signalSummaries.isEmpty() && adjacentNumberHint == null

    val hasDeliverySignal: Boolean
        get() = keywordClusters.any { it in DELIVERY_KEYWORDS }

    val hasInstitutionSignal: Boolean
        get() = keywordClusters.any { it in INSTITUTION_KEYWORDS }

    val hasBusinessSignal: Boolean
        get() = keywordClusters.any { it in BUSINESS_KEYWORDS }

    val hasSpamSignal: Boolean
        get() = keywordClusters.any { it in SPAM_KEYWORDS }

    val hasScamSignal: Boolean
        get() = keywordClusters.any { it in SCAM_KEYWORDS }
}

private val DELIVERY_KEYWORDS = setOf(
    "delivery", "courier", "shipping", "logistics", "parcel", "package",
    "택배", "배송", "배달", "물류", "송장",
)

private val INSTITUTION_KEYWORDS = setOf(
    "hospital", "clinic", "school", "university", "government", "office",
    "administration", "reservation",
    "병원", "학교", "학원", "기관", "관공서", "예약", "진료", "접수",
)

private val BUSINESS_KEYWORDS = setOf(
    "company", "corporation", "representative", "branch", "customer service",
    "회사", "기업", "대표번호", "고객센터", "지점",
)

private val SPAM_KEYWORDS = setOf(
    "spam", "telemarketing", "advertisement", "ad", "sales", "marketing",
    "광고", "영업", "텔레마케팅", "홍보", "판매", "보험", "마케팅",
)

private val SCAM_KEYWORDS = setOf(
    "scam", "phishing", "fraud", "loan", "investment", "fake", "warning",
    "사기", "보이스피싱", "피싱", "대출", "투자", "리딩방",
    "사칭", "가짜", "허위", "스팸", "경고", "주의", "조심",
)
