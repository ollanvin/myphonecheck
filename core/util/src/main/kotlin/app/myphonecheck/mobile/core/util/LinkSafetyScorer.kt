package app.myphonecheck.mobile.core.util

/**
 * 링크 안전도 점수 계산기.
 *
 * URL에서 도메인을 추출하고 위험 신호를 분석하여
 * 0~100 점수를 산출합니다.
 *
 * ═══════════════════════════════════════════════
 * 점수 체계 (0~100, 높을수록 안전):
 * - 100: 위험 신호 없음
 * - 감점 요소:
 *   - URL 단축기 사용: -25
 *   - 위험 키워드 포함: 키워드당 -15
 *   - 비표준 포트 사용: -10
 *   - IP 주소 직접 사용: -20
 *   - HTTP (비암호화): -15
 *   - 과도한 서브도메인 (4+): -10
 *   - 의심 TLD (.xyz, .top 등): -10
 * ═══════════════════════════════════════════════
 *
 * 원칙:
 * - 외부 브라우저 열지 않음 (URL 접속 없음)
 * - 순수 문자열 분석만 수행
 * - 네트워크 요청 없음 (온디바이스 전용)
 * - '안전' 확정 표현 금지 — 높은 점수도 "위험 신호 적음"
 */
object LinkSafetyScorer {

    /**
     * URL 안전도 점수 산출.
     *
     * @param url 분석 대상 URL
     * @return LinkSafetyResult (점수 + 감지된 위험 신호 목록)
     */
    fun score(url: String): LinkSafetyResult {
        val signals = mutableListOf<String>()
        var deduction = 0

        val normalized = url.trim()

        // 1. 프로토콜 확인
        val isHttp = normalized.startsWith("http://", ignoreCase = true)
        if (isHttp) {
            signals.add("HTTP (unencrypted)")
            deduction += 15
        }

        // 2. 도메인 추출
        val domain = extractDomain(normalized)
        if (domain == null) {
            // 도메인 추출 실패 → 비정상 URL
            return LinkSafetyResult(
                score = 20,
                signals = listOf("Invalid URL format"),
                domain = null,
                isShortener = false,
            )
        }

        // 3. IP 주소 직접 사용
        if (isIpAddress(domain)) {
            signals.add("Direct IP address")
            deduction += 20
        }

        // 4. URL 단축기 감지
        val isShortener = domain.lowercase() in URL_SHORTENERS
        if (isShortener) {
            signals.add("URL shortener")
            deduction += 25
        }

        // 5. 위험 키워드 (도메인 + 전체 경로)
        val lowerUrl = normalized.lowercase()
        for (keyword in DANGER_KEYWORDS) {
            if (keyword in lowerUrl) {
                signals.add("Suspicious keyword: $keyword")
                deduction += 15
            }
        }

        // 6. 비표준 포트
        val port = extractPort(normalized)
        if (port != null && port !in STANDARD_PORTS) {
            signals.add("Non-standard port: $port")
            deduction += 10
        }

        // 7. 과도한 서브도메인
        val subdomainCount = domain.count { it == '.' }
        if (subdomainCount >= 4) {
            signals.add("Excessive subdomains ($subdomainCount levels)")
            deduction += 10
        }

        // 8. 의심 TLD
        val tld = domain.substringAfterLast(".")
        if (tld.lowercase() in SUSPICIOUS_TLDS) {
            signals.add("Suspicious TLD: .$tld")
            deduction += 10
        }

        // 9. 과도한 경로 깊이 (5+)
        val pathDepth = normalized.substringAfter(domain)
            .count { it == '/' }
        if (pathDepth > 5) {
            signals.add("Deep path ($pathDepth levels)")
            deduction += 5
        }

        val finalScore = (100 - deduction).coerceIn(0, 100)

        return LinkSafetyResult(
            score = finalScore,
            signals = signals,
            domain = domain,
            isShortener = isShortener,
        )
    }

    /**
     * 여러 URL을 일괄 분석.
     * 가장 낮은 점수를 대표 점수로 반환.
     */
    fun scoreMultiple(urls: List<String>): LinkBatchResult {
        if (urls.isEmpty()) {
            return LinkBatchResult(
                overallScore = 100,
                results = emptyList(),
            )
        }

        val results = urls.map { score(it) }
        val minScore = results.minOf { it.score }

        return LinkBatchResult(
            overallScore = minScore,
            results = results,
        )
    }

    /**
     * 점수 등급 라벨.
     * '안전' 확정 표현 절대 금지.
     */
    fun gradeLabel(score: Int): String = when {
        score <= 30 -> "위험"
        score <= 60 -> "주의"
        else -> "위험 신호 적음"
    }

    fun gradeLabelEn(score: Int): String = when {
        score <= 30 -> "Danger"
        score <= 60 -> "Caution"
        else -> "Low risk signals"
    }

    // ══════════════════════════════════════════════
    // URL 파싱 유틸리티
    // ══════════════════════════════════════════════

    /**
     * URL에서 도메인 추출.
     * 프로토콜, 포트, 경로 제거.
     */
    fun extractDomain(url: String): String? {
        val trimmed = url.trim()

        // 프로토콜 제거
        val withoutProtocol = when {
            trimmed.startsWith("https://", ignoreCase = true) -> trimmed.substring(8)
            trimmed.startsWith("http://", ignoreCase = true) -> trimmed.substring(7)
            else -> trimmed
        }

        // 경로/쿼리/프래그먼트 제거
        val domainPart = withoutProtocol.substringBefore("/")
            .substringBefore("?")
            .substringBefore("#")

        // 포트 제거
        val domain = domainPart.substringBefore(":")

        // 인증 정보 제거 (user:pass@domain)
        val cleanDomain = if ("@" in domain) domain.substringAfter("@") else domain

        return cleanDomain.takeIf { it.isNotBlank() && it.contains(".") }
    }

    private fun extractPort(url: String): Int? {
        val regex = Regex("""://[^/:]+:(\d+)""")
        return regex.find(url)?.groupValues?.get(1)?.toIntOrNull()
    }

    private fun isIpAddress(domain: String): Boolean {
        // IPv4
        val ipv4Regex = Regex("""^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$""")
        if (ipv4Regex.matches(domain)) return true

        // IPv6 (대괄호 포함)
        if (domain.startsWith("[") && domain.endsWith("]")) return true

        return false
    }

    // ══════════════════════════════════════════════
    // 정적 데이터
    // ══════════════════════════════════════════════

    /** URL 단축 서비스 도메인 */
    private val URL_SHORTENERS = setOf(
        "bit.ly", "tinyurl.com", "t.co", "goo.gl", "ow.ly",
        "is.gd", "buff.ly", "adf.ly", "bl.ink", "lnkd.in",
        "rb.gy", "cutt.ly", "shorturl.at", "me2.do", "vo.la",
        "han.gl", "url.kr", "me2.kr", "buly.kr",
        // 일본
        "urx.blue", "urx.red",
        // 중국
        "dwz.cn", "url.cn",
    )

    /** 위험 키워드 (피싱/스미싱 패턴) */
    private val DANGER_KEYWORDS = setOf(
        "login", "signin", "verify", "confirm", "update",
        "secure", "account", "banking", "password", "credential",
        "wallet", "suspend", "urgent", "blocked", "unauthorized",
        // 한국어
        "입금", "송금", "당첨", "환급", "택배조회",
        // 일본어
        "振込", "当選", "確認",
    )

    /** 의심 TLD */
    private val SUSPICIOUS_TLDS = setOf(
        "xyz", "top", "click", "loan", "work", "date",
        "racing", "win", "bid", "stream", "gq", "cf",
        "tk", "ml", "ga", "buzz", "monster",
    )

    /** 표준 웹 포트 */
    private val STANDARD_PORTS = setOf(80, 443, 8080, 8443)
}

/**
 * 단일 URL 안전도 분석 결과.
 */
data class LinkSafetyResult(
    /** 안전도 점수 (0~100, 높을수록 위험 신호 적음) */
    val score: Int,
    /** 감지된 위험 신호 목록 */
    val signals: List<String>,
    /** 추출된 도메인 (null이면 파싱 실패) */
    val domain: String?,
    /** URL 단축기 여부 */
    val isShortener: Boolean,
)

/**
 * 복수 URL 일괄 분석 결과.
 */
data class LinkBatchResult(
    /** 대표 점수 (가장 낮은 점수) */
    val overallScore: Int,
    /** 개별 결과 */
    val results: List<LinkSafetyResult>,
)
