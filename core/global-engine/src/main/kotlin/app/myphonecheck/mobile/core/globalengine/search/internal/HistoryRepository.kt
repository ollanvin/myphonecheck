package app.myphonecheck.mobile.core.globalengine.search.internal

import app.myphonecheck.mobile.core.globalengine.search.MatchEntry
import app.myphonecheck.mobile.core.globalengine.search.QueryType

/**
 * 온디바이스 이력 저장소 인터페이스 (Architecture v2.0.0 §30).
 *
 * 실제 구현은 :data:local-cache (CallLog/SMS/사용자 라벨 캐시 통합).
 * 본 PR에서는 NoopHistoryRepository 기본 바인딩 — 후속 PR에서 실 구현 교체.
 */
interface HistoryRepository {
    suspend fun findByKey(key: String, type: QueryType): List<MatchEntry>
}
