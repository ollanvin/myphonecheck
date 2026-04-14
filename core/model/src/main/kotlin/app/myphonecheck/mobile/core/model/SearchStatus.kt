package app.myphonecheck.mobile.core.model

/**
 * Fixed search-result status shown separately from user labels and tags.
 */
enum class SearchStatus(val labelKo: String) {
    MATCH_FOUND("검색 결과 일치 정보 있음"),
    NO_MATCH("검색 결과 일치 정보 없음"),
    INSUFFICIENT("검색 결과 확인 불충분"),
    NOT_CHECKED("아직 검색 확인 안 됨");

    companion object {
        fun fromDecisionResult(result: DecisionResult): SearchStatus =
            when {
                result.searchEvidence == null -> NOT_CHECKED
                !result.searchEvidence.isEmpty -> MATCH_FOUND
                result.category == ConclusionCategory.INSUFFICIENT_EVIDENCE -> INSUFFICIENT
                else -> NO_MATCH
            }

        fun fromStoredMessage(
            searchSummary: String?,
            fallbackSummary: String,
        ): SearchStatus = when {
            !searchSummary.isNullOrBlank() -> MATCH_FOUND
            fallbackSummary.contains("연락처") -> NOT_CHECKED
            fallbackSummary.contains("충분", ignoreCase = true) ||
                fallbackSummary.contains("부족", ignoreCase = true) -> INSUFFICIENT
            else -> NO_MATCH
        }
    }
}
