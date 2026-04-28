package app.myphonecheck.mobile.core.globalengine.search.publicfeed

/**
 * 공개 피드 출처 유형 (Architecture v2.1.0 §30-3-A-2 + §30-4).
 *
 * Layer 3 하위 분류 4유형:
 *  - A. SecurityIntelligence: 글로벌 보안 (Abuse.ch, PhishTank, OpenPhish)
 *  - B. GovernmentPublic: 정부·공공기관 (KISA, FBI IC3, ACMA)
 *  - C. CompetitorApp: 경쟁 앱 데이터 (더콜, 후후, 뭐야이번호, Whoscall)
 *  - D. TelcoBlocklist: 통신사 공개 차단
 */
sealed class FeedType {
    object SecurityIntelligence : FeedType() { override fun toString() = "SECURITY" }
    object GovernmentPublic : FeedType() { override fun toString() = "GOVERNMENT" }
    object CompetitorApp : FeedType() { override fun toString() = "COMPETITOR" }
    object TelcoBlocklist : FeedType() { override fun toString() = "TELCO" }
}
