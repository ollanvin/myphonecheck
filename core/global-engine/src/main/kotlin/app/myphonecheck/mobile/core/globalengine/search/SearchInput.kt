package app.myphonecheck.mobile.core.globalengine.search

import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext

/**
 * 검색 입력 영역 (Architecture v2.5.0 헌법 §1 정합).
 * One Engine, N Inputs — 모든 input 타입에 대해 동일 2축 패턴.
 */
sealed class SearchInput {
    abstract val timestamp: Long

    /** 폰 번호 (Stage 1~3 본질). */
    data class PhoneNumber(
        val value: String,
        val simContext: SimContext,
        override val timestamp: Long = System.currentTimeMillis(),
    ) : SearchInput()

    /** URL/링크 (Stage 4 영역). */
    data class Url(
        val value: String,
        val surfaceContext: String,  // CALL / MESSAGE / PUSH / CARD
        override val timestamp: Long = System.currentTimeMillis(),
    ) : SearchInput()

    /** SMS/메시지 본문 (Stage 4 영역). */
    data class MessageBody(
        val text: String,
        val extractedUrls: List<String>,
        val extractedNumbers: List<String>,
        override val timestamp: Long = System.currentTimeMillis(),
    ) : SearchInput()

    /** 앱 패키지명 (MicCheck/CameraCheck/PushCheck). */
    data class AppPackage(
        val packageName: String,
        override val timestamp: Long = System.currentTimeMillis(),
    ) : SearchInput()
}

/**
 * AI 검색 모드에 전달할 query 문자열 변환.
 * input 타입별로 적절한 query 생성.
 */
fun SearchInput.toAiSearchQuery(): String = when (this) {
    is SearchInput.PhoneNumber -> value
    is SearchInput.Url -> value
    is SearchInput.MessageBody -> {
        when {
            extractedUrls.isNotEmpty() -> extractedUrls.first()
            extractedNumbers.isNotEmpty() -> extractedNumbers.first()
            else -> text.take(100)
        }
    }
    is SearchInput.AppPackage -> "$packageName security CVE"
}
