package app.myphonecheck.mobile.core.globalengine.search.publicfeed

import app.myphonecheck.mobile.core.globalengine.search.MatchEntry
import app.myphonecheck.mobile.core.globalengine.search.SearchQuery
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 공개 피드 캐시 (Architecture v2.0.0 §30).
 *
 * 본 PR에서는 인메모리 NoOp — 후속 PR에서 Room/파일 시스템 캐시 구현.
 * Aggregator는 본 캐시를 통해 디바이스 로컬 lookup만 수행 (헌법 §1 Out-Bound Zero).
 */
@Singleton
class PublicFeedCache @Inject constructor() {

    private val store: MutableMap<String, MutableList<MatchEntry>> = mutableMapOf()

    fun put(sourceId: String, key: String, entries: List<MatchEntry>) {
        val composite = compositeKey(sourceId, key)
        store[composite] = entries.toMutableList()
    }

    fun lookup(sourceId: String, query: SearchQuery): List<MatchEntry> {
        return store[compositeKey(sourceId, query.key)].orEmpty()
    }

    fun clear() = store.clear()

    private fun compositeKey(sourceId: String, key: String): String = "$sourceId|$key"
}
