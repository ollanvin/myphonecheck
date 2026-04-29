package app.myphonecheck.mobile.core.globalengine.search.publicfeed

import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 공개 피드 출처 메타데이터 카탈로그 (Architecture v2.1.0 §30-4).
 *
 * 활성화 정책 (PR #34, WO-V210-FEEDS-ACTIVATE):
 *  - 활성 (UI 노출, requiresUserOptIn=true): 라이선스 검토 통과 출처만.
 *  - 비노출 (requiresUserOptIn=false): 라이선스 미결 또는 사업개발 트랙 출처.
 *
 * 활성 출처 (8):
 *  - SecurityIntelligence 글로벌 2: Abuse.ch URLhaus (CC0), PhishTank (CC-BY-SA)
 *  - GovernmentPublic KR 2: KISA Phishing URLs + Recent (data.go.kr 「제한 없음」)
 *
 * 비노출 출처 (5):
 *  - kisa_smishing_kr: 발신번호 전용 placeholder, Phase 2-B 미결
 *  - thecall_kr / whowho_kr / moaff_kr: 한국 3사, Phase 2-A YELLOW~RED
 *  - whoscall_global: 글로벌, Phase 2-A RED 무허가 / GREEN 공식 계약 후
 *  - kt_blocklist_kr: 통신사 placeholder, 추가 검토
 *
 * 활성화 결정 기록: docs/05_quality/feeds_activation_record.md
 */
@Singleton
class FeedRegistry @Inject constructor() {

    private val sources: List<PublicFeedSource> = listOf(

        // === A. SecurityIntelligence (글로벌, 활성) ===
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
            description = "Malicious URLs for SMS/notification phishing detection (Source: Abuse.ch)",
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
            description = "Verified phishing URLs (Source: PhishTank)",
            termsUrl = "https://www.phishtank.com/terms_of_use.php",
        ),

        // === B. GovernmentPublic — KISA (한국, 활성, Phase 2-B 보고서 정합) ===
        PublicFeedSource(
            id = "kisa_phishing_url_kr",
            name = "KISA Phishing URLs (Korea)",
            type = FeedType.GovernmentPublic,
            countryScope = CountryScope.COUNTRY("KR"),
            license = "data.go.kr 「제한 없음」 (출처 표시 권장)",
            updateFrequency = UpdateFrequency.DAILY,
            downloadUrl = "https://www.data.go.kr/data/15109780/fileData.do",
            format = FeedFormat.CSV,
            dataType = FeedDataType.PHISHING_URL,
            description = "Korean phishing URL list from KISA via data.go.kr (Source: KISA / 공공데이터포털)",
            termsUrl = "https://www.data.go.kr/data/15109780/fileData.do",
        ),
        PublicFeedSource(
            id = "kisa_phishing_url_recent_kr",
            name = "KISA Phishing URLs - Recent (Korea)",
            type = FeedType.GovernmentPublic,
            countryScope = CountryScope.COUNTRY("KR"),
            license = "data.go.kr 「제한 없음」 (출처 표시 권장)",
            updateFrequency = UpdateFrequency.DAILY,
            downloadUrl = "https://www.data.go.kr/data/15143094/fileData.do",
            format = FeedFormat.CSV,
            dataType = FeedDataType.PHISHING_URL,
            description = "KISA phishing URLs - recent updates (Source: KISA / 공공데이터포털)",
            termsUrl = "https://www.data.go.kr/data/15143094/fileData.do",
        ),

        // === B. GovernmentPublic — KISA Smishing (비노출, Phase 2-B 미결) ===
        // Placeholder — 발신번호 전용 데이터셋 별도 검토 중.
        // Phase 2-B 보고서 §3 미결 항목. 추가 후속 WO에서 확정 후 활성화.
        PublicFeedSource(
            id = "kisa_smishing_kr",
            name = "KISA Smishing Reports (Korea) — Placeholder",
            type = FeedType.GovernmentPublic,
            countryScope = CountryScope.COUNTRY("KR"),
            license = "검토 중",
            updateFrequency = UpdateFrequency.ON_DEMAND,
            downloadUrl = "",
            format = FeedFormat.CSV,
            dataType = FeedDataType.SMS_PATTERN,
            description = "Placeholder — 스미싱 발신번호 전용 벌크 데이터셋 추가 조회 중 (Phase 2-B §3 미결)",
            termsUrl = null,
            requiresUserOptIn = false,
        ),

        // === C. CompetitorApp 항목 폐기 (Stage 3-000, 헌법 §1 v2.4.0 정합) ===
        // 경쟁사 비공식 API/scraper 통합 영구 금지. 경쟁사 Reverse Lookup은 후속 Stage 3-002
        // `core/global-engine/search/competitor/` 디렉토리에서 Custom Tab 사용자 직접 진입으로 처리.

        // === D. TelcoBlocklist — KT (한국, 비노출, 추가 검토) ===
        PublicFeedSource(
            id = "kt_blocklist_kr",
            name = "KT Public Blocklist (Korea) — Placeholder",
            type = FeedType.TelcoBlocklist,
            countryScope = CountryScope.COUNTRY("KR"),
            license = "통신사 공식 공개 — 추가 검토",
            updateFrequency = UpdateFrequency.DAILY,
            downloadUrl = "",
            format = FeedFormat.CSV,
            dataType = FeedDataType.PHONE_NUMBER,
            description = "KT publicly blocked numbers (placeholder, 추가 검토 후 활성)",
            termsUrl = null,
            requiresUserOptIn = false,
        ),
    )

    fun all(): List<PublicFeedSource> = sources

    fun recommendForSim(simContext: SimContext): List<PublicFeedSource> =
        sources.filter { it.countryScope.matches(simContext.countryIso) }

    fun byType(type: FeedType): List<PublicFeedSource> = sources.filter { it.type == type }

    fun byId(id: String): PublicFeedSource? = sources.find { it.id == id }

    /**
     * Placeholder = downloadUrl 빈 문자열 또는 `<...>` 양식.
     * 사용자 노출과는 별도 — 표시 여부는 [PublicFeedSource.requiresUserOptIn] 기준.
     */
    fun isPlaceholder(source: PublicFeedSource): Boolean {
        if (source.downloadUrl.isBlank()) return true
        return source.downloadUrl.startsWith("<") && source.downloadUrl.endsWith(">")
    }
}
