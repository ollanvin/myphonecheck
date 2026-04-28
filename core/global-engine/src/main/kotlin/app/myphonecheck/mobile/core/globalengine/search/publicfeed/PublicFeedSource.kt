package app.myphonecheck.mobile.core.globalengine.search.publicfeed

/**
 * 공개 피드 출처 메타데이터 (Architecture v2.1.0 §30-4 검색 4축).
 *
 * v2.0.0 PR #22 시점 interface(id/name/isOptedIn/lookup) → v2.1.0 PR #29 data class로 본질 확장.
 * 옵트인 여부는 [FeedOptInProvider]가 별도 조회. lookup은 [PublicFeedAggregator] + [PublicFeedCache]가 담당.
 *
 * 예시: Abuse.ch URLhaus, PhishTank, KISA Smishing, 더콜·후후·Whoscall 등.
 *
 * 헌법 §1 Out-Bound Zero 정합:
 *  - 옵트인된 출처만 다운로드.
 *  - 다운로드 후 디바이스 캐시 lookup만 — 외부 통신 0.
 */
data class PublicFeedSource(
    val id: String,
    val name: String,
    val type: FeedType,
    val countryScope: CountryScope,
    val license: String,
    val updateFrequency: UpdateFrequency,
    val downloadUrl: String,
    val format: FeedFormat,
    val dataType: FeedDataType,
    val description: String,
    val termsUrl: String? = null,
    val requiresUserOptIn: Boolean = true,
)
