package app.myphonecheck.mobile.core.globalengine.search.publicfeed

/**
 * 공개 피드 출처 유형 (Architecture v2.4.0 §30-3-A-2 + §30-4).
 *
 * Layer 3 하위 분류 (헌법 §1 v2.4.0 정합으로 CompetitorApp 폐기, Stage 3-000):
 *  - A. SecurityIntelligence: 글로벌 보안 (Abuse.ch, PhishTank, OpenPhish)
 *  - B. GovernmentPublic: 정부·공공기관 (KISA, FBI IC3, ACMA)
 *  - D. TelcoBlocklist: 통신사 공개 차단
 *
 * 경쟁사 데이터(축 4)는 Custom Tab 사용자 직접 진입(`search/competitor/`, 후속 Stage 3-002)으로
 * 분리. 공개 피드 통합 영역 아님 (헌법 §1 명문 금지: 경쟁사 비공식 API/scraper 통합).
 */
sealed class FeedType {
    object SecurityIntelligence : FeedType() { override fun toString() = "SECURITY" }
    object GovernmentPublic : FeedType() { override fun toString() = "GOVERNMENT" }
    object TelcoBlocklist : FeedType() { override fun toString() = "TELCO" }
}
