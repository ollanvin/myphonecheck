package app.callcheck.mobile.feature.messageintercept

import app.callcheck.mobile.core.model.MessageEvidence

/**
 * SMS 텍스트 분석기.
 *
 * SMS 본문에서 위험 신호를 온디바이스로 추출합니다.
 * 외부 API 호출 없음. 패턴 매칭만 사용.
 *
 * 분석 항목:
 * 1. URL 추출 (http/https + 단축 URL)
 * 2. 사칭 키워드 매칭 (은행/택배/정부기관)
 * 3. 피싱 키워드 매칭 (비밀번호/인증번호/계좌)
 * 4. 긴급성 유도 표현 매칭
 * 5. 금융 키워드 매칭
 */
object MessageTextAnalyzer {

    /** URL 추출 정규식 */
    private val URL_PATTERN = Regex(
        """https?://[^\s<>"{}|\\^`\[\]]+""",
        RegexOption.IGNORE_CASE,
    )

    /** 단축 URL 도메인 */
    private val SHORTENED_DOMAINS = setOf(
        "bit.ly", "t.co", "tinyurl.com", "goo.gl", "ow.ly",
        "is.gd", "buff.ly", "adf.ly", "bl.ink", "rb.gy",
        "han.gl", "me2.do", "vo.la", "url.kr", "zpr.io",
    )

    /** 사칭 키워드 (기관/서비스 사칭) */
    private val IMPERSONATION_KEYWORDS = setOf(
        // 한국어
        "국민은행", "신한은행", "하나은행", "우리은행", "농협",
        "카카오뱅크", "토스", "국세청", "건강보험", "국민연금",
        "경찰", "검찰", "법원", "우체국", "택배", "CJ대한통운",
        "로젠택배", "한진택배", "롯데택배",
        // 영어
        "bank", "police", "court", "irs", "tax",
        "fedex", "ups", "dhl", "usps",
    )

    /** 피싱 키워드 (개인정보/인증 유도) */
    private val PHISHING_KEYWORDS = setOf(
        // 한국어
        "비밀번호", "인증번호", "계좌번호", "주민등록",
        "본인확인", "보안카드", "OTP", "인증코드",
        "개인정보", "확인 바랍니다", "접속하세요",
        // 영어
        "password", "verify", "confirm", "account",
        "ssn", "credential", "login", "click here",
    )

    /** 긴급성 유도 키워드 */
    private val URGENCY_KEYWORDS = setOf(
        // 한국어
        "즉시", "지금", "긴급", "마감", "오늘까지",
        "24시간", "48시간", "만료", "취소됩니다",
        "정지됩니다", "차단됩니다", "삭제됩니다",
        // 영어
        "immediately", "urgent", "expire", "suspend",
        "cancel", "within 24", "last chance",
    )

    /** 금융 유도 키워드 */
    private val FINANCIAL_KEYWORDS = setOf(
        // 한국어
        "대출", "투자", "입금", "송금", "환급",
        "수익", "배당", "코인", "주식", "리딩방",
        "보험료", "미납", "연체", "체납",
        // 영어
        "loan", "invest", "transfer", "deposit",
        "crypto", "bitcoin", "profit", "dividend",
    )

    /**
     * SMS 본문을 분석하여 MessageEvidence를 생성합니다.
     *
     * @param sender 발신자 번호/이름
     * @param body SMS 본문
     * @param isSavedContact 저장된 연락처 여부
     * @return 분석된 MessageEvidence
     */
    fun analyze(
        sender: String,
        body: String,
        isSavedContact: Boolean,
    ): MessageEvidence {
        val lowerBody = body.lowercase()

        // URL 추출
        val urls = URL_PATTERN.findAll(body).map { it.value }.toList()
        val hasShortenedUrl = urls.any { url ->
            SHORTENED_DOMAINS.any { domain -> url.contains(domain, ignoreCase = true) }
        }

        // 키워드 매칭
        val impersonationHits = IMPERSONATION_KEYWORDS.count { lowerBody.contains(it.lowercase()) }
        val phishingHits = PHISHING_KEYWORDS.count { lowerBody.contains(it.lowercase()) }
        val urgencyHits = URGENCY_KEYWORDS.count { lowerBody.contains(it.lowercase()) }
        val financialHits = FINANCIAL_KEYWORDS.count { lowerBody.contains(it.lowercase()) }

        return MessageEvidence(
            sender = sender,
            isSavedContact = isSavedContact,
            body = body,
            extractedUrls = urls,
            hasShortenedUrl = hasShortenedUrl,
            impersonationKeywordHits = impersonationHits,
            phishingKeywordHits = phishingHits,
            urgencyKeywordHits = urgencyHits,
            financialKeywordHits = financialHits,
            receivedAtMillis = System.currentTimeMillis(),
        )
    }
}
