package app.myphonecheck.mobile.core.globalengine.search.internal

import app.myphonecheck.mobile.core.globalengine.search.QueryType
import app.myphonecheck.mobile.core.globalengine.search.SearchConfidence
import app.myphonecheck.mobile.core.globalengine.search.SearchInput
import app.myphonecheck.mobile.core.globalengine.search.SearchResult
import app.myphonecheck.mobile.core.globalengine.search.SearchSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 내부 검색 — 온디바이스 이력만 조회 (Architecture v2.5.0 §30, 헌법 §1 Out-Bound Zero).
 *
 * v2.5.0 정정: SearchInput sealed class 정합. input 타입별 분기.
 * 결과는 사용자 본인 이력이므로 confidence = HIGH.
 */
@Singleton
class OnDeviceHistorySearch @Inject constructor(
    private val historyRepository: HistoryRepository,
) {
    suspend fun search(input: SearchInput): SearchResult = when (input) {
        is SearchInput.PhoneNumber -> searchByKey(input.value, QueryType.PHONE_NUMBER)
        is SearchInput.Url -> searchByKey(input.value, QueryType.PHONE_NUMBER) // Stage 4 본격 구현 전까지 폰 번호 인터페이스 재사용
        is SearchInput.MessageBody -> emptyResult()  // Stage 4 본격 구현
        is SearchInput.AppPackage -> searchByKey(input.packageName, QueryType.APP_PACKAGE)
    }

    private suspend fun searchByKey(key: String, type: QueryType): SearchResult {
        val matches = historyRepository.findByKey(key, type)
        return SearchResult(
            source = SearchSource.INTERNAL,
            matches = matches,
            confidence = SearchConfidence.HIGH,
        )
    }

    private fun emptyResult(): SearchResult = SearchResult(
        source = SearchSource.INTERNAL,
        matches = emptyList(),
        confidence = SearchConfidence.LOW,
    )
}
