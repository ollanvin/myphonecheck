package app.myphonecheck.mobile.core.globalengine.search.publicfeed

import app.myphonecheck.mobile.core.globalengine.search.MatchEntry
import app.myphonecheck.mobile.core.globalengine.search.SearchQuery

/**
 * 공개 피드 출처 인터페이스 (Architecture v2.0.0 §30).
 *
 * 예시: "KISA 스미싱 신고", "Abuse.ch URLhaus", 통신사 차단번호 DB.
 *
 * 헌법 §1 Out-Bound Zero 정합:
 *  - isOptedIn() = false 시 본 출처는 동작 0.
 *  - 다운로드는 별도 워커 (사용자 옵트인 후 백그라운드, 본 PR 범위 외).
 */
interface PublicFeedSource {
    val id: String
    val name: String
    suspend fun isOptedIn(): Boolean
    suspend fun lookup(query: SearchQuery): List<MatchEntry>
}
