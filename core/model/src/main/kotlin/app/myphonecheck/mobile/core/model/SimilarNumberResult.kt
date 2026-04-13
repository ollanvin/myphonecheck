package app.myphonecheck.mobile.core.model

/**
 * 유사번호 검색 결과 (UI 표시용).
 *
 * AdjacentNumberHint의 데이터를 사용자 친화적으로 변환한 뷰 모델.
 * 기관 대표번호 패턴, 인접 번호 대역 매칭 결과를 통합한다.
 *
 * 표시 원칙:
 * - '안전' 확정 표현 절대 금지
 * - 검색 결과 없음 = 안전 처리 금지 → '미확인/주의' 상태
 * - 0~100 단일 신뢰도 점수 체계
 */
data class SimilarNumberResult(
    /** 검색에 사용된 패턴 (예: "1588-****", "010-1234-****") */
    val pattern: String,

    /** 검색 요약 (예: "끝 1자리 대역에서 3건 검색됨") */
    val searchSummary: String,

    /** 추정 기관/기업명 (있으면 표시, 없으면 null) */
    val estimatedOrg: String?,

    /** 신뢰도 점수 0~100 */
    val confidenceScore: Int,
) {
    companion object {
        /**
         * AdjacentNumberHint에서 SimilarNumberResult 목록을 생성한다.
         */
        fun fromAdjacentHint(hint: AdjacentNumberHint?): List<SimilarNumberResult> {
            if (hint == null || hint.resultCount == 0) return emptyList()

            val score = when {
                hint.signalSummaries.any { it.signalType == "SCAM" } -> 15
                hint.signalSummaries.any { it.signalType == "SPAM" } -> 35
                hint.signalSummaries.any { it.signalType == "SPAM_REPORT" } -> 40
                hint.signalSummaries.any { it.signalType == "INSTITUTION" } -> 75
                hint.signalSummaries.any { it.signalType == "BUSINESS" } -> 70
                hint.signalSummaries.any { it.signalType == "DELIVERY" } -> 70
                else -> 50 // 미확인
            }

            return listOf(
                SimilarNumberResult(
                    pattern = hint.rangeDescription,
                    searchSummary = "${hint.resultCount}건 검색됨",
                    estimatedOrg = hint.matchedEntity,
                    confidenceScore = score,
                )
            )
        }
    }
}
