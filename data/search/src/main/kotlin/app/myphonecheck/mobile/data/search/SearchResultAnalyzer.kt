package app.myphonecheck.mobile.data.search

import app.myphonecheck.mobile.core.model.SignalSummary
import app.myphonecheck.mobile.core.model.SearchEvidence
import app.myphonecheck.mobile.core.model.SearchTrend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 온디바이스 검색 결과 분석기.
 *
 * Raw 검색 결과를 PRD-aligned SearchEvidence로 변환한다.
 *
 * ═══════════════════════════════════════════════════
 * SignalSummary 생성 룰셋 (의미 신호 판단 기준)
 * ═══════════════════════════════════════════════════
 *
 * [Phase 1] 신호 수집 — 6개 카테고리 독립 감지
 *
 * [Rule 1] 사기/피싱 신호 (최우선)
 * - 조건: SCAM_KEYWORDS(한/영/일/중/러 5개 언어) 중 1개 이상 존재
 * - 출력 (건수별 강도 조절):
 *   5건+: "사기/피싱 다수 신고 확인 — 수신 위험"
 *   2건+: "사기/피싱 신고 확인 — 수신 주의"
 *   1건:  "사기/피싱 관련 정보 있음 — 주의 필요"
 * - signalType: "SCAM"
 *
 * [Rule 2] 스팸/광고 신호
 * - 조건: SPAM_KEYWORDS 중 1개 이상 AND 사기 신호 없음
 * - 출력: 3건+: "광고/영업 전화 — 거절 권장" / "광고/영업 전화 의심 — 주의 필요"
 * - signalType: "SPAM"
 *
 * [Rule 3] 택배/배송 신호
 * - 조건: DELIVERY_KEYWORDS(5개 언어) 중 1개 이상
 * - 출력: "{엔터티} — 택배/배송 관련 전화" 또는 "택배/배송 업체 전화 — 배송 확인 권장"
 * - signalType: "DELIVERY"
 *
 * [Rule 4] 기관/공공기관 신호
 * - 조건: INSTITUTION_KEYWORDS(5개 언어) 중 1개 이상
 * - 출력: "{엔터티} 공공기관 전화 — 수신 안전" 또는 "공공기관/의료기관 전화 — 수신 안전"
 * - signalType: "INSTITUTION"
 *
 * [Rule 5] 기업/고객센터 신호
 * - 조건: BUSINESS_KEYWORDS(5개 언어) 중 1개 이상
 * - 출력: "{엔터티} 고객센터 — 수신 안전" 또는 "기업 대표번호/고객센터 — 수신 안전"
 * - signalType: "BUSINESS"
 *
 * [Rule 6] 스팸 신고 사이트 등록 신호
 * - 조건: SPAM_REPORT_DOMAINS(한/일/글로벌) 출현
 * - signalType: "SPAM_REPORT"
 *
 * [Phase 2] 혼합 신호 충돌 해소 (Conflict Resolution)
 *
 * 긍정 신호(BUSINESS/INSTITUTION/DELIVERY)와 부정 신호(SCAM/SPAM/SPAM_REPORT)가
 * 동시에 존재할 때 아래 우선순위 규칙으로 최종 문장을 결정한다.
 *
 * [CR-1] 강한 사기 신호(SCAM 5건+): 부정 신호 무조건 우선 — "수신 위험"
 * [CR-2] 약한 부정(SPAM_REPORT <3건) + 강한 긍정 엔터티:
 *        → "{엔터티} 관련 번호 — 수신 안전 (일부 신고 이력 있음)"
 * [CR-3] 양쪽 모두 존재 + 엔터티 있음:
 *        → "{엔터티} 관련 번호로 확인되나 신고 이력 있음 — 수신 주의"
 * [CR-4] 엔터티 없음: 부정 신호 우선 정렬
 *
 * 최대 출력: 3개 SignalSummary. 키워드 5개 언어 190개국 지원
 *
 * ═══════════════════════════════════════════════════
 * SignalSummary 문장 강도 표준 (Text Intensity Standard)
 * ═══════════════════════════════════════════════════
 *
 * 모든 SignalSummary 문장은 아래 6단계 강도 표현 중 하나를 사용한다.
 * 각 표현의 사용 조건을 엄격히 준수하여 과단정/과모호를 방지한다.
 *
 * ┌───────────────┬───────────────────────────────────────────────────┐
 * │ 강도 표현      │ 사용 조건                                         │
 * ├───────────────┼───────────────────────────────────────────────────┤
 * │ 수신 안전      │ 긍정 신호만 존재하고 부정 신호 없음                 │
 * │               │ 또는 CR-2에서 약한 부정이 강한 긍정에 흡수된 경우    │
 * │               │ 예: "삼성서울병원 공공기관 전화 — 수신 안전"         │
 * │               │ 예: "신세계백화점 관련 번호 — 수신 안전             │
 * │               │      (일부 신고 이력 있음)"                         │
 * ├───────────────┼───────────────────────────────────────────────────┤
 * │ 참고 필요      │ 약한 부정 신호(SPAM_REPORT 1~2건)만 존재,          │
 * │               │ 긍정 신호 없음                                     │
 * │               │ 예: "스팸 신고 사이트에 등록됨 — 참고 필요"          │
 * ├───────────────┼───────────────────────────────────────────────────┤
 * │ 주의 필요      │ 부정 신호 1건 존재(SCAM 1건 또는 SPAM 1~2건)       │
 * │               │ 또는 혼합 신호에서 엔터티 미확인                     │
 * │               │ 예: "사기/피싱 관련 정보 있음 — 주의 필요"           │
 * │               │ 예: "광고/영업 전화 관련 정보 있음 — 주의 필요"      │
 * ├───────────────┼───────────────────────────────────────────────────┤
 * │ 수신 주의      │ 부정 신호 2건+ 존재(SCAM 2~4건)                    │
 * │               │ 또는 CR-3 혼합(긍정+부정 모두 존재, 엔터티 확인)     │
 * │               │ 예: "사기/피싱 신고 확인 — 수신 주의"               │
 * │               │ 예: "ABC보험 관련 번호로 확인되나                    │
 * │               │      신고 이력 있음 — 수신 주의"                     │
 * ├───────────────┼───────────────────────────────────────────────────┤
 * │ 수신 위험      │ 강한 부정 신호(SCAM 5건+)만 허용                   │
 * │               │ CR-1 조건 충족 시에만 사용                          │
 * │               │ 예: "사기/피싱 다수 신고 확인 — 수신 위험"           │
 * │               │ ❌ 금지: 1~4건 SCAM에서 사용 금지                   │
 * │               │ ❌ 금지: 정상 기관/기업 번호에 절대 사용 금지         │
 * ├───────────────┼───────────────────────────────────────────────────┤
 * │ 거절 권장      │ SPAM 3건+ (사기 아닌 광고/영업 전화)               │
 * │               │ 예: "광고/영업 전화 — 거절 권장"                    │
 * ├───────────────┼───────────────────────────────────────────────────┤
 * │ 확인 권장      │ 택배/배송 신호 — 행동 유도형                       │
 * │               │ 예: "CJ대한통운 택배/배송 전화 — 배송 확인 권장"     │
 * │               │ 예: "택배/배송 업체 전화 — 배송 확인 권장"           │
 * └───────────────┴───────────────────────────────────────────────────┘
 *
 * 강도 서열: 수신 안전 < 참고 필요 < 주의 필요 < 수신 주의 < 수신 위험
 *           (확인 권장, 거절 권장은 별도 행동 유도 계열)
 *
 * ═══════════════════════════════════════════════════
 * 혼합 신호 우선순위 표 (Mixed Signal Priority Table)
 * ═══════════════════════════════════════════════════
 *
 * 긍정 신호와 부정 신호가 동시 감지될 때 적용하는 충돌 해소 규칙.
 * CR-1 ~ CR-5 순서대로 평가하며, 최초 매칭 규칙에서 결정한다.
 *
 * ┌──────┬────────────────────┬────────────────────┬──────────────────┬──────────────────────────┐
 * │ 규칙  │ 부정 조건            │ 긍정 조건            │ 엔터티           │ 결과                      │
 * ├──────┼────────────────────┼────────────────────┼──────────────────┼──────────────────────────┤
 * │ CR-1 │ SCAM 5건+           │ (무관)              │ (무관)           │ 부정 우선 → 수신 위험      │
 * │ CR-2 │ SPAM_REPORT만       │ 긍정 >= 부정        │ 있음             │ 긍정 우선 → 수신 안전      │
 * │      │ (<3건, SCAM/SPAM 無)│                    │                  │ + "(일부 신고 이력 있음)"   │
 * │ CR-3 │ 부정 존재            │ 긍정 존재           │ 있음             │ 혼합 → 수신 주의           │
 * │      │                    │                    │                  │ "{엔터티} 확인되나         │
 * │      │                    │                    │                  │  신고 이력 있음"           │
 * │ CR-4 │ 부정 존재            │ 긍정 존재           │ 없음             │ 부정 우선 정렬             │
 * │ CR-5 │ 없음                │ 존재               │ (무관)           │ 긍정 그대로 반환           │
 * │      │ 존재                │ 없음               │ (무관)           │ 부정 그대로 반환           │
 * └──────┴────────────────────┴────────────────────┴──────────────────┴──────────────────────────┘
 *
 * 신호 분류:
 * - 긍정: BUSINESS, INSTITUTION, DELIVERY
 * - 부정: SCAM, SPAM, SPAM_REPORT
 * - 병합: BUSINESS_WITH_REPORT (CR-2), MIXED (CR-3)
 *
 * ═══════════════════════════════════════════════════
 */
class SearchResultAnalyzer {

    private companion object {
        // ═══════════════════════════════════════════
        // 문장 강도 표준 상수 (Text Intensity Constants)
        // ═══════════════════════════════════════════
        // 모든 SignalSummary 문장의 접미 표현은 아래 상수만 사용한다.
        // 하드코딩된 문자열 대신 이 상수를 참조하여 일관성을 보장한다.

        /** 긍정 신호만 존재, 부정 없음. 또는 CR-2 흡수 */
        const val INTENSITY_SAFE = "수신 안전"

        /** 약한 부정만 존재 (SPAM_REPORT 1~2건), 긍정 없음 */
        const val INTENSITY_REFERENCE = "참고 필요"

        /** 부정 1건 존재 또는 약한 혼합 */
        const val INTENSITY_CAUTION_LIGHT = "주의 필요"

        /** 부정 2~4건 존재 또는 CR-3 혼합 */
        const val INTENSITY_CAUTION = "수신 주의"

        /** 강한 부정 (SCAM 5건+), CR-1 전용 */
        const val INTENSITY_DANGER = "수신 위험"

        /** 광고/영업 3건+, 행동 유도형 */
        const val INTENSITY_REJECT = "거절 권장"

        /** 택배/배송, 행동 유도형 */
        const val INTENSITY_VERIFY = "배송 확인 권장"

        // ═══════════════════════════════════════════
        // 다국어 키워드 사전
        // KR/JP/CN/RU/EN 5개 언어 지원
        // ═══════════════════════════════════════════

        val DELIVERY_KEYWORDS = setOf(
            // 한국어
            "택배", "배송", "배달", "물류", "송장", "배송조회",
            "cj대한통운", "한진", "롯데택배", "우체국", "쿠팡",
            // 영어
            "courier", "delivery", "shipping", "logistics", "parcel",
            "tracking", "express", "dhl", "fedex", "ups",
            // 일본어
            "配送", "配達", "宅配", "荷物", "追跡",
            "ヤマト", "佐川", "日本郵便",
            // 중국어
            "快递", "配送", "物流", "包裹", "运单",
            "顺丰", "圆通", "中通",
            // 러시아어
            "доставка", "посылка", "курьер", "отправление",
        )

        val INSTITUTION_KEYWORDS = setOf(
            // 한국어
            "병원", "의원", "클리닉", "예약", "진료",
            "학교", "관공서", "교육청", "구청", "시청", "기관", "공공기관",
            // 영어
            "hospital", "clinic", "medical", "government", "school", "university",
            "municipality", "district office",
            // 일본어
            "病院", "クリニック", "役所", "市役所", "区役所",
            "学校", "窓口", "受付", "お問い合わせ",
            // 중국어
            "医院", "政府", "学校", "机关", "服务中心",
            // 러시아어
            "больница", "поликлиника", "школа", "администрация",
        )

        val BUSINESS_KEYWORDS = setOf(
            // 한국어
            "회사", "기업", "대표번호", "고객센터", "본사", "지점", "상담",
            // 영어
            "company", "corporation", "customer service", "branch",
            "headquarters", "call center", "support",
            // 일본어
            "会社", "企業", "代表番号", "お客様", "カスタマー",
            "本社", "サポート", "故障",
            // 중국어
            "公司", "企业", "客服", "总部", "服务热线",
            // 러시아어
            "компания", "офис", "поддержка", "горячая линия",
        )

        val SPAM_KEYWORDS = setOf(
            // 한국어
            "광고", "영업", "마케팅", "홍보", "판매", "영업전화", "보험",
            // 영어 (단, "sales"는 오탐 방지를 위해 제거 — 일반 기업에서 너무 흔함)
            "advertising", "marketing", "telemarketing", "insurance",
            "unwanted call", "robocall",
            // 일본어
            "広告", "営業", "セールス", "勧誘", "迷惑電話",
            // 중국어
            "广告", "推销", "骚扰电话",
            // 러시아어
            "реклама", "спам", "нежелательный",
        )

        val SCAM_KEYWORDS = setOf(
            // 한국어
            "사기", "피싱", "보이스피싱", "사칭", "가짜", "대출", "투자",
            "리딩방", "주의", "조심", "경고", "스팸",
            // 영어
            "scam", "phishing", "fraud", "fake", "loan shark",
            "dangerous", "threat", "warning", "spam",
            // 일본어
            "詐欺", "フィッシング", "偽", "不審", "迷惑",
            "注意", "警告", "危険",
            // 중국어
            "诈骗", "钓鱼", "欺诈", "虚假", "骗子",
            // 러시아어
            "мошенничество", "обман", "фишинг",
        )

        val SPAM_REPORT_DOMAINS = setOf(
            // 한국
            "thecall.co.kr",
            // 글로벌
            "whoscall.com", "truecaller.com",
            "shouldianswer.com", "tellows.com",
            "findwhocallsyou.com", "whocallsinfo.com", "callfilter.app",
            // 일본
            "jpnumber.com", "meiwaku.com",
            // 중국
            "baidu.com/s?wd=骚扰",
        )

        val NEWS_DOMAINS = setOf(
            "naver.com", "daum.net", "yonhapnews.co.kr", "bbc.com", "cnn.com",
            "nhk.or.jp", "asahi.com",
        )

        val COMMUNITY_DOMAINS = setOf(
            "cafe.naver.com", "clien.net", "ppomppu.co.kr", "reddit.com",
            "detail.chiebukuro.yahoo.co.jp",
        )

        val BLOG_DOMAINS = setOf(
            "blog.naver.com", "tistory.com", "medium.com", "brunch.co.kr",
        )
    }

    /**
     * Analyze raw search results into PRD SearchEvidence.
     */
    suspend fun analyzeSearchResults(
        rawResults: List<RawSearchResult>,
    ): SearchEvidence = withContext(Dispatchers.Default) {
        if (rawResults.isEmpty()) {
            return@withContext SearchEvidence.empty()
        }

        val allText = rawResults.joinToString(" ") { "${it.title} ${it.snippet}".lowercase() }

        // 1. Keyword clusters — which categories are present?
        val keywordClusters = buildKeywordClusters(allText)

        // 2. Repeated entities — company/brand names that appear 2+ times
        val repeatedEntities = extractRepeatedEntities(rawResults)

        // 3. Source types
        val sourceTypes = rawResults.map { classifySource(it.domain) }.distinct()

        // 4. Top snippets (max 5)
        val topSnippets = rawResults.take(5).map { "${it.title}: ${it.snippet}" }

        // 5. Search intensity (approximate from result count)
        val resultCount = rawResults.size
        val intensity30d = resultCount  // simplified: all results assumed recent
        val intensity90d = resultCount

        // 6. Trend direction
        val searchTrend = estimateTrend(rawResults)

        // 7. Signal summaries — provider 이름 숨기고 의미 신호만 추출
        val signalSummaries = buildSignalSummaries(rawResults, keywordClusters, repeatedEntities)

        SearchEvidence(
            recent30dSearchIntensity = intensity30d,
            recent90dSearchIntensity = intensity90d,
            searchTrend = searchTrend,
            keywordClusters = keywordClusters,
            repeatedEntities = repeatedEntities,
            sourceTypes = sourceTypes,
            topSnippets = topSnippets,
            signalSummaries = signalSummaries,
        )
    }

    private fun buildKeywordClusters(allText: String): List<String> {
        val clusters = mutableListOf<String>()

        // Order matters: scam first, then spam, then positive categories
        if (SCAM_KEYWORDS.any { it in allText }) {
            val matched = SCAM_KEYWORDS.filter { it in allText }
            clusters.addAll(matched.take(3))
        }
        if (SPAM_KEYWORDS.any { it in allText }) {
            val matched = SPAM_KEYWORDS.filter { it in allText }
            clusters.addAll(matched.take(3))
        }
        if (DELIVERY_KEYWORDS.any { it in allText }) {
            val matched = DELIVERY_KEYWORDS.filter { it in allText }
            clusters.addAll(matched.take(2))
        }
        if (INSTITUTION_KEYWORDS.any { it in allText }) {
            val matched = INSTITUTION_KEYWORDS.filter { it in allText }
            clusters.addAll(matched.take(2))
        }
        if (BUSINESS_KEYWORDS.any { it in allText }) {
            val matched = BUSINESS_KEYWORDS.filter { it in allText }
            clusters.addAll(matched.take(2))
        }

        return clusters.distinct()
    }

    private fun extractRepeatedEntities(results: List<RawSearchResult>): List<String> {
        // title 단어 중 2회+ 등장하는 것 추출
        val titleWords = results.flatMap { it.title.split(" ", "/", "-", ":") }
            .filter { it.length >= 2 }
            .map { it.trim() }
            .filter { it.isNotBlank() }

        return titleWords.groupingBy { it }
            .eachCount()
            .filter { it.value >= 2 }
            .keys
            .take(5)
            .toList()
    }

    /**
     * 반복 엔터티 중 UI에 노출할 대표 엔터티를 선택한다.
     *
     * 필터링 기준:
     * - URL 조각(www., http, 도메인) 제외
     * - 숫자 전용 토큰 제외
     * - CJK 문자(한/중/일) 포함 엔터티 우선
     * - 길이가 긴 것 우선 (더 구체적)
     */
    private fun findPrimaryEntity(
        repeatedEntities: List<String>,
        results: List<RawSearchResult>,
    ): String? {
        return repeatedEntities
            .filter { entity ->
                entity.length >= 2
                        && !entity.matches(Regex("\\d+"))           // 숫자만으로 구성
                        && !entity.matches(Regex("[a-z.]+"))        // 영문 도메인 조각
                        && !entity.startsWith("www.")
                        && !entity.startsWith("http")
                        && !entity.contains(".")                     // 도메인 포함 제외
            }
            .maxByOrNull { entity ->
                // CJK 문자 포함 시 가산점
                val cjkBonus = if (entity.any { it.code >= 0x3000 }) 10 else 0
                entity.length + cjkBonus
            }
    }

    private fun classifySource(domain: String): String {
        val lower = domain.lowercase()
        return when {
            SPAM_REPORT_DOMAINS.any { lower.contains(it) } -> "SPAM_REPORT"
            NEWS_DOMAINS.any { lower.contains(it) } -> "NEWS"
            COMMUNITY_DOMAINS.any { lower.contains(it) } -> "COMMUNITY"
            BLOG_DOMAINS.any { lower.contains(it) } -> "BLOG"
            lower.contains(".gov") || lower.contains(".org") -> "OFFICIAL"
            else -> "UNKNOWN"
        }
    }

    /**
     * 의미 신호 요약 생성 (Phase 1: 수집 + Phase 2: 충돌 해소).
     *
     * provider 이름은 내부 처리에만 사용.
     * UI에는 행동결정형 의미 문장만 노출한다.
     *
     * ❌ "Google 3건, Naver 2건"
     * ✅ "사기/피싱 다수 신고 확인 — 수신 위험"
     * ✅ "CJ대한통운 — 택배/배송 관련 전화"
     * ✅ "신세계백화점 관련 번호 — 수신 안전 (일부 신고 이력 있음)"
     *
     * 행동결정형 원칙:
     * - 문장은 "무엇인지" + "어떻게 행동할지"를 함께 전달
     * - 과도한 단정 금지: "의심" 대신 "관련 정보 있음"
     * - 엔터티(기관/기업명)가 있으면 반드시 포함
     * - 혼합 신호 충돌 시 Phase 2에서 해소
     */
    private fun buildSignalSummaries(
        results: List<RawSearchResult>,
        keywordClusters: List<String>,
        repeatedEntities: List<String>,
    ): List<SignalSummary> {
        if (results.isEmpty()) return emptyList()

        // ════════════════════════════════════
        // Phase 1: 신호 수집 (독립 감지)
        // ════════════════════════════════════

        val summaries = mutableListOf<SignalSummary>()
        val allText = results.joinToString(" ") { "${it.title} ${it.snippet}".lowercase() }
        val primaryEntity = findPrimaryEntity(repeatedEntities, results)

        // ── 1. 사기/피싱 신호 (최우선) ──
        val scamMatches = SCAM_KEYWORDS.filter { it in allText }
        if (scamMatches.isNotEmpty()) {
            val scamResults = results.filter { r ->
                val text = "${r.title} ${r.snippet}".lowercase()
                SCAM_KEYWORDS.any { it in text }
            }
            val desc = when {
                scamResults.size >= 5 -> "사기/피싱 다수 신고 확인 — $INTENSITY_DANGER"
                scamResults.size >= 2 -> "사기/피싱 신고 확인 — $INTENSITY_CAUTION"
                else -> "사기/피싱 관련 정보 있음 — $INTENSITY_CAUTION_LIGHT"
            }
            summaries.add(
                SignalSummary(
                    signalDescription = desc,
                    resultCount = scamResults.size,
                    topSnippet = scamResults.firstOrNull()?.let { "${it.title}: ${it.snippet}" },
                    signalType = "SCAM",
                )
            )
        }

        // ── 2. 스팸/광고 신호 ──
        val spamMatches = SPAM_KEYWORDS.filter { it in allText }
        if (spamMatches.isNotEmpty() && scamMatches.isEmpty()) {
            val spamResults = results.filter { r ->
                val text = "${r.title} ${r.snippet}".lowercase()
                SPAM_KEYWORDS.any { it in text }
            }
            val desc = when {
                spamResults.size >= 3 -> "광고/영업 전화 — $INTENSITY_REJECT"
                else -> "광고/영업 전화 관련 정보 있음 — $INTENSITY_CAUTION_LIGHT"
            }
            summaries.add(
                SignalSummary(
                    signalDescription = desc,
                    resultCount = spamResults.size,
                    topSnippet = spamResults.firstOrNull()?.let { "${it.title}: ${it.snippet}" },
                    signalType = "SPAM",
                )
            )
        }

        // ── 3. 택배/배송 신호 ──
        if (DELIVERY_KEYWORDS.any { it in allText }) {
            val deliveryResults = results.filter { r ->
                val text = "${r.title} ${r.snippet}".lowercase()
                DELIVERY_KEYWORDS.any { it in text }
            }
            val entityHint = primaryEntity
            val desc = if (entityHint != null) {
                "$entityHint 택배/배송 전화 — $INTENSITY_VERIFY"
            } else {
                "택배/배송 업체 전화 — $INTENSITY_VERIFY"
            }
            summaries.add(
                SignalSummary(
                    signalDescription = desc,
                    resultCount = deliveryResults.size,
                    topSnippet = deliveryResults.firstOrNull()?.let { "${it.title}: ${it.snippet}" },
                    signalType = "DELIVERY",
                )
            )
        }

        // ── 4. 기관/공공기관 신호 ──
        if (INSTITUTION_KEYWORDS.any { it in allText }) {
            val instResults = results.filter { r ->
                val text = "${r.title} ${r.snippet}".lowercase()
                INSTITUTION_KEYWORDS.any { it in text }
            }
            val entityHint = primaryEntity
            val desc = if (entityHint != null) {
                "$entityHint 공공기관 전화 — $INTENSITY_SAFE"
            } else {
                "공공기관/의료기관 전화 — $INTENSITY_SAFE"
            }
            summaries.add(
                SignalSummary(
                    signalDescription = desc,
                    resultCount = instResults.size,
                    topSnippet = instResults.firstOrNull()?.let { "${it.title}: ${it.snippet}" },
                    signalType = "INSTITUTION",
                )
            )
        }

        // ── 5. 기업/고객센터 신호 ──
        if (BUSINESS_KEYWORDS.any { it in allText }) {
            val bizResults = results.filter { r ->
                val text = "${r.title} ${r.snippet}".lowercase()
                BUSINESS_KEYWORDS.any { it in text }
            }
            val entityHint = primaryEntity
            val desc = if (entityHint != null) {
                "$entityHint 고객센터 — $INTENSITY_SAFE"
            } else {
                "기업 대표번호/고객센터 — $INTENSITY_SAFE"
            }
            summaries.add(
                SignalSummary(
                    signalDescription = desc,
                    resultCount = bizResults.size,
                    topSnippet = bizResults.firstOrNull()?.let { "${it.title}: ${it.snippet}" },
                    signalType = "BUSINESS",
                )
            )
        }

        // ── 6. 스팸 신고 사이트 기반 신호 ──
        val spamReportResults = results.filter { classifySource(it.domain) == "SPAM_REPORT" }
        if (spamReportResults.isNotEmpty()) {
            val desc = when {
                spamReportResults.size >= 3 -> "스팸 신고 다수 확인 — $INTENSITY_CAUTION"
                else -> "스팸 신고 사이트에 등록됨 — $INTENSITY_REFERENCE"
            }
            summaries.add(
                SignalSummary(
                    signalDescription = desc,
                    resultCount = spamReportResults.size,
                    topSnippet = spamReportResults.firstOrNull()?.let { "${it.title}: ${it.snippet}" },
                    signalType = "SPAM_REPORT",
                )
            )
        }

        // ════════════════════════════════════
        // Phase 2: 혼합 신호 충돌 해소
        // ════════════════════════════════════

        return resolveConflicts(summaries, primaryEntity)
    }

    /**
     * 혼합 신호 충돌 해소 (Conflict Resolution).
     *
     * 긍정 신호(BUSINESS/INSTITUTION/DELIVERY)와 부정 신호(SCAM/SPAM/SPAM_REPORT)가
     * 동시에 존재할 때 최종 노출 문장을 결정한다.
     *
     * ┌─────────────────────────────────────────────────────────┐
     * │ CR-1: 강한 SCAM (5건+) → 부정 무조건 우선              │
     * │ CR-2: 약한 부정만 + 강한 긍정 엔터티 → 긍정 우선       │
     * │       + "(일부 신고 이력 있음)" 부기                    │
     * │ CR-3: 양쪽 존재 + 엔터티 있음 → 혼합 문장              │
     * │ CR-4: 엔터티 없음 → 부정 우선 정렬                     │
     * │ CR-5: 충돌 없음 → 그대로 반환                          │
     * └─────────────────────────────────────────────────────────┘
     */
    private fun resolveConflicts(
        summaries: MutableList<SignalSummary>,
        primaryEntity: String?,
    ): List<SignalSummary> {
        val positiveTypes = setOf("BUSINESS", "INSTITUTION", "DELIVERY")
        val negativeTypes = setOf("SCAM", "SPAM", "SPAM_REPORT")

        val positiveSignals = summaries.filter { it.signalType in positiveTypes }
        val negativeSignals = summaries.filter { it.signalType in negativeTypes }

        // CR-5: 충돌 없으면 그대로 반환
        if (positiveSignals.isEmpty() || negativeSignals.isEmpty()) {
            return summaries.take(3)
        }

        // CR-1: 강한 SCAM (5건+) → 부정이 압도
        val strongScam = negativeSignals.any { it.signalType == "SCAM" && it.resultCount >= 5 }
        if (strongScam) {
            // 부정 우선, 긍정은 후순위
            return (negativeSignals + positiveSignals).take(3)
        }

        val negativeStrength = negativeSignals.sumOf { it.resultCount }
        val positiveStrength = positiveSignals.sumOf { it.resultCount }

        // CR-2: 약한 부정만(SPAM_REPORT <3건, SCAM/SPAM 없음) + 긍정 엔터티 존재
        val onlyWeakNegative = negativeSignals.all {
            it.signalType == "SPAM_REPORT" && it.resultCount < 3
        }

        if (onlyWeakNegative && primaryEntity != null && positiveStrength >= negativeStrength) {
            // 긍정이 우선, 부정은 부기(참고 사항)로 통합
            val bestPositive = positiveSignals.first()
            val mergedDesc = "$primaryEntity 관련 번호 — $INTENSITY_SAFE (일부 신고 이력 있음)"
            return listOf(
                SignalSummary(
                    signalDescription = mergedDesc,
                    resultCount = positiveStrength + negativeStrength,
                    topSnippet = bestPositive.topSnippet,
                    signalType = "BUSINESS_WITH_REPORT",
                )
            )
        }

        // CR-3: 양쪽 모두 존재 + 엔터티 있음 → 혼합 문장
        if (primaryEntity != null) {
            val hybridDesc = "$primaryEntity 관련 번호로 확인되나 신고 이력 있음 — $INTENSITY_CAUTION"
            return listOf(
                SignalSummary(
                    signalDescription = hybridDesc,
                    resultCount = positiveStrength + negativeStrength,
                    topSnippet = negativeSignals.firstOrNull()?.topSnippet,
                    signalType = "MIXED",
                )
            )
        }

        // CR-4: 엔터티 없음 → 부정 우선 정렬
        return (negativeSignals + positiveSignals).take(3)
    }

    private fun estimateTrend(results: List<RawSearchResult>): SearchTrend {
        // Simplified: if many results, trend is increasing; few = low; none = none
        return when {
            results.size >= 10 -> SearchTrend.INCREASING
            results.size >= 3 -> SearchTrend.STABLE
            results.isNotEmpty() -> SearchTrend.LOW
            else -> SearchTrend.NONE
        }
    }
}
