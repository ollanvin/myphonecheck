package app.myphonecheck.mobile.core.globalengine.search.external

/**
 * 사용자 trigger용 외부 검색 인텐트 (Architecture v2.0.0 §30).
 *
 * 본 앱은 검색을 실행하지 않음 — UI 레이어가 url을 Custom Tab으로 띄움.
 */
data class ExternalSearchIntent(val url: String)
