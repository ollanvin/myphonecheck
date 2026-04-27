package app.myphonecheck.mobile.core.globalengine.search

import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext

/**
 * 검색 3대 축 입력/출력 모델 (Architecture v2.0.0 §30).
 *
 * 헌법 정합:
 *  - §1 Out-Bound Zero: EXTERNAL = Custom Tab 사용자 trigger only,
 *                        PUBLIC_FEED = 사용자 옵트인 다운로드 후 디바이스 캐시.
 *  - §2 In-Bound Zero: 외부 검색 결과 미수신, 공개 피드 캐시 후 가공.
 *  - §3 결정 중앙집중 금지: 검색은 후보 노출만, 결정은 사용자.
 *  - §8 SIM-Oriented: SearchContext가 SimContext 캡슐화.
 */
data class SearchQuery(
    val key: String,
    val type: QueryType,
    val context: SearchContext,
)

enum class QueryType {
    PHONE_NUMBER,           // CallCheck
    SMS_SENDER,             // MessageCheck
    NOTIFICATION_PACKAGE,   // PushCheck
    APP_PACKAGE,            // MicCheck/CameraCheck
}

data class SearchContext(
    val sim: SimContext,
)

data class SearchResult(
    val source: SearchSource,
    val matches: List<MatchEntry>,
    val confidence: SearchConfidence,
)

enum class SearchSource { INTERNAL, EXTERNAL, PUBLIC_FEED }

data class MatchEntry(
    val sourceId: String,
    val description: String,
    val severity: Severity?,
)

/**
 * 위협 레벨 — 공개 피드 결과 분류용. 내부/외부 결과는 null.
 */
enum class Severity { LOW, MEDIUM, HIGH, CRITICAL }

/**
 * 검색 결과 신뢰도 — 축에 따라 의미 다름.
 *  - INTERNAL: 사용자 본인 이력 = HIGH.
 *  - PUBLIC_FEED: 공개 피드 = 출처에 따라 MEDIUM 기본, 캐시 신선도에 따라 LOW.
 *  - EXTERNAL: 사용자가 trigger한 검색 = N/A (본 enum 미사용).
 */
enum class SearchConfidence { HIGH, MEDIUM, LOW }
