package app.callcheck.mobile.core.model

/**
 * 메시지(SMS) 분석 증거.
 *
 * SMS 수신 시 수집되는 메시지 데이터를
 * 판단 엔진이 소비할 수 있는 형태로 정규화한 모델.
 *
 * 판단 기준:
 * - 발신자: 저장된 연락처 여부, 번호 유형
 * - 내용: 사칭 키워드, 피싱 링크, 금융 위장
 * - 링크: URL 포함 여부, 단축 URL 여부, 의심 도메인
 * - 패턴: 긴급성 유도 표현, 개인정보 요청
 */
data class MessageEvidence(
    /** 발신자 번호 또는 이름 */
    val sender: String,

    /** 발신자가 저장된 연락처인지 */
    val isSavedContact: Boolean,

    /** 메시지 본문 */
    val body: String,

    /** 본문에서 추출된 URL 리스트 */
    val extractedUrls: List<String>,

    /** 단축 URL 포함 여부 (bit.ly, t.co 등) */
    val hasShortenedUrl: Boolean,

    /** 사칭 키워드 매칭 수 (은행, 택배, 정부기관 사칭) */
    val impersonationKeywordHits: Int,

    /** 피싱 키워드 매칭 수 (비밀번호, 인증번호, 계좌 등) */
    val phishingKeywordHits: Int,

    /** 긴급성 유도 표현 매칭 수 (즉시, 지금, 긴급 등) */
    val urgencyKeywordHits: Int,

    /** 금융 관련 키워드 매칭 수 (대출, 투자, 입금 등) */
    val financialKeywordHits: Int,

    /** 수신 시각 (epoch millis) */
    val receivedAtMillis: Long,
) {
    companion object {
        fun empty(sender: String) = MessageEvidence(
            sender = sender,
            isSavedContact = false,
            body = "",
            extractedUrls = emptyList(),
            hasShortenedUrl = false,
            impersonationKeywordHits = 0,
            phishingKeywordHits = 0,
            urgencyKeywordHits = 0,
            financialKeywordHits = 0,
            receivedAtMillis = System.currentTimeMillis(),
        )
    }

    /** 사기/피싱 위험 점수 (0.0~1.0) */
    val scamScore: Float
        get() {
            var score = 0f
            // 사칭 키워드
            if (impersonationKeywordHits >= 2) score += 0.3f
            else if (impersonationKeywordHits >= 1) score += 0.15f
            // 피싱 키워드
            if (phishingKeywordHits >= 2) score += 0.3f
            else if (phishingKeywordHits >= 1) score += 0.15f
            // 단축 URL
            if (hasShortenedUrl) score += 0.15f
            // 긴급성 유도
            if (urgencyKeywordHits >= 2) score += 0.15f
            // 금융 유도
            if (financialKeywordHits >= 2) score += 0.2f
            else if (financialKeywordHits >= 1) score += 0.1f
            // 저장된 연락처면 점수 대폭 감소
            if (isSavedContact) score *= 0.3f
            return score.coerceIn(0f, 1f)
        }

    /** URL 포함 여부 */
    val hasLinks: Boolean
        get() = extractedUrls.isNotEmpty()

    /** 저장된 연락처의 일반 메시지인지 */
    val isLikelySafe: Boolean
        get() = isSavedContact && phishingKeywordHits == 0 && impersonationKeywordHits == 0
}
