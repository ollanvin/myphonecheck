package app.myphonecheck.mobile.core.globalengine.search.publicfeed

import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 공개 피드 출처 메타데이터 카탈로그 (Architecture v2.1.0 §30-4).
 *
 * 본 PR 등록 출처:
 *  - A. SecurityIntelligence (글로벌 — 실제 활성): Abuse.ch URLhaus, PhishTank
 *  - B. GovernmentPublic (KR — placeholder URL): KISA Smishing
 *  - C. CompetitorApp (KR — placeholder, 라이선스 검토 필수): TheCall
 *  - D. TelcoBlocklist (KR — placeholder): KT
 *
 * 라이선스 정합 강제:
 *  - C·D 유형은 placeholder URL("<별도 확정>"). 실제 다운로드 라이선스·robots.txt·ToS 사전 검토 후 별도 PR.
 *  - 본 PR은 인프라 + A·B 글로벌·정부 출처만 실제 활성 가능.
 */
@Singleton
class FeedRegistry @Inject constructor() {

    private val sources: List<PublicFeedSource> = listOf(
        PublicFeedSource(
            id = "abusech_urlhaus",
            name = "Abuse.ch URLhaus",
            type = FeedType.SecurityIntelligence,
            countryScope = CountryScope.GLOBAL,
            license = "CC0",
            updateFrequency = UpdateFrequency.HOURLY,
            downloadUrl = "https://urlhaus.abuse.ch/downloads/csv_recent/",
            format = FeedFormat.CSV,
            dataType = FeedDataType.PHISHING_URL,
            description = "Malicious URLs for SMS/notification phishing detection",
            termsUrl = "https://urlhaus.abuse.ch/api/",
        ),
        PublicFeedSource(
            id = "phishtank",
            name = "PhishTank",
            type = FeedType.SecurityIntelligence,
            countryScope = CountryScope.GLOBAL,
            license = "CC-BY-SA",
            updateFrequency = UpdateFrequency.HOURLY,
            downloadUrl = "https://data.phishtank.com/data/online-valid.json",
            format = FeedFormat.JSON_ARRAY,
            dataType = FeedDataType.PHISHING_URL,
            description = "Verified phishing URLs",
            termsUrl = "https://www.phishtank.com/terms_of_use.php",
        ),
        PublicFeedSource(
            id = "kisa_smishing_kr",
            name = "KISA Smishing Reports (Korea)",
            type = FeedType.GovernmentPublic,
            countryScope = CountryScope.COUNTRY("KR"),
            license = "공공누리 제1유형",
            updateFrequency = UpdateFrequency.DAILY,
            downloadUrl = "<KISA 공식 피드 URL — 별도 확정 후 갱신>",
            format = FeedFormat.CSV,
            dataType = FeedDataType.SMS_PATTERN,
            description = "Korean SMS phishing patterns reported to KISA",
            termsUrl = "https://www.kogl.or.kr/info/license.do",
        ),
        PublicFeedSource(
            id = "thecall_kr_placeholder",
            name = "TheCall (Korea) — Placeholder",
            type = FeedType.CompetitorApp,
            countryScope = CountryScope.COUNTRY("KR"),
            license = "ToS only — 사전 검토 필수",
            updateFrequency = UpdateFrequency.ON_DEMAND,
            downloadUrl = "<별도 확정>",
            format = FeedFormat.JSON_ARRAY,
            dataType = FeedDataType.PHONE_NUMBER,
            description = "Competitor app's public spam number reports (placeholder)",
            termsUrl = "<해당 사이트 ToS URL>",
        ),
        PublicFeedSource(
            id = "kt_blocklist_placeholder",
            name = "KT Public Blocklist (Korea) — Placeholder",
            type = FeedType.TelcoBlocklist,
            countryScope = CountryScope.COUNTRY("KR"),
            license = "통신사 공식 공개",
            updateFrequency = UpdateFrequency.DAILY,
            downloadUrl = "<별도 확정>",
            format = FeedFormat.CSV,
            dataType = FeedDataType.PHONE_NUMBER,
            description = "KT publicly blocked numbers (placeholder)",
            termsUrl = null,
        ),
    )

    fun all(): List<PublicFeedSource> = sources

    fun recommendForSim(simContext: SimContext): List<PublicFeedSource> =
        sources.filter { it.countryScope.matches(simContext.countryIso) }

    fun byType(type: FeedType): List<PublicFeedSource> = sources.filter { it.type == type }

    fun byId(id: String): PublicFeedSource? = sources.find { it.id == id }

    /** Placeholder URL을 가진 출처는 실제 다운로드 불가 — 라이선스 검토 후 별도 PR. */
    fun isPlaceholder(source: PublicFeedSource): Boolean =
        source.downloadUrl.startsWith("<") && source.downloadUrl.endsWith(">")
}
