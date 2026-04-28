# 9. SearchResultAnalyzer (Tier A → Tier C 변환)

**원본 출처**: v1.7.1 §9 (1217–1320)
**v1.8.0 Layer**: Engine
**의존**: `00_core/01_primary.md`
**변경 이력**: 본 파일은 v1.7.1 §9 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cursor/07_engine/04_analyzer.md`

---

# 9. SearchResultAnalyzer (Tier A → Tier C 변환)

외부 검색 원문(Tier A)을 featureCounts(Tier C)로 변환하는 엔진. 원문은 메모리에서만 처리, 즉시 폐기.

## 9-1. 입력·출력 계약

```kotlin
interface SearchResultAnalyzer {
    /**
     * 외부 검색 결과를 ExtractedSignal로 변환.
     *
     * @param rawResult 외부 검색 원문 (메모리만, 저장 금지)
     * @param tier 출처 Tier (1~4)
     * @return ExtractedSignal (featureCounts만 포함, 원문 없음)
     */
    fun analyze(rawResult: RawSearchResult, tier: Int): ExtractedSignal
}

// Decision Engine 내부 스코프에서만 생성 가능
internal data class RawSearchResult(
    val url: String,
    val title: String,
    val snippet: String,  // 메모리 only, NKB·Surface 저장 금지
    val sourceDomain: String
)
```

## 9-2. Tier 분류 규칙

`TierClassifier`가 `sourceDomain`을 기준으로 Tier 판정.

| Tier | 도메인 패턴 | 가중치 | 예시 |
|---|---|---|---|
| Tier 4 | 회사 공식 (앱스토어·공식 홈페이지) | 1.0 | `google.com/about`, `apple.com`, `company.co.kr` |
| Tier 3 | 정부·공공기관 | 0.8 | `*.gov.kr`, `*.go.kr`, `kisa.or.kr`, `police.go.kr` |
| Tier 2 | 일반 사이트·언론·블로그 | 0.5 | 일반 뉴스, 블로그, 쇼핑몰 |
| Tier 1 | 커뮤니티·포럼·UGC | 0.3 | 네이버 카페, 디시인사이드, 레딧 |

도메인 패턴이 모호한 경우 기본 Tier 2.

## 9-3. FeatureExtractor (키워드 카운팅)

strings.xml에 정의된 **scamKeywords / adKeywords / officialTerms** 사전 기반 단순 카운팅.

```kotlin
class FeatureExtractor(
    private val keywordLoader: KeywordLoader
) {
    fun extract(rawResult: RawSearchResult): Map<FeatureType, Int> {
        val text = "${rawResult.title} ${rawResult.snippet}".lowercase()
        val locale = Locale.getDefault()
        val keywords = keywordLoader.loadForLocale(locale)

        return mapOf(
            FeatureType.SCAM_KEYWORD to countMatches(text, keywords.scam),
            FeatureType.AD_KEYWORD to countMatches(text, keywords.ad),
            FeatureType.OFFICIAL_DOMAIN_HIT to if (isOfficialDomain(rawResult.sourceDomain)) 1 else 0,
            FeatureType.URL_RISK_INDICATOR to countSuspiciousUrlPatterns(rawResult.snippet),
            FeatureType.USER_REVIEW_NEGATIVE to countMatches(text, keywords.userNegative),
            FeatureType.USER_REVIEW_POSITIVE to countMatches(text, keywords.userPositive),
            FeatureType.PHONE_FORMAT_SUSPICIOUS to countSuspiciousPhonePatterns(rawResult.snippet)
        )
    }
}
```

## 9-4. KeywordLoader (strings.xml 로드)

**하드코딩 금지** (메모리 #1). 키워드는 strings.xml 다국어 자원.

```kotlin
class KeywordLoader(private val context: Context) {
    data class KeywordSet(
        val scam: List<String>,
        val ad: List<String>,
        val officialTerms: List<String>,
        val userNegative: List<String>,
        val userPositive: List<String>
    )

    fun loadForLocale(locale: Locale): KeywordSet {
        // res/values-ko/keywords.xml, res/values-en/keywords.xml 등에서 로드
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        val localizedContext = context.createConfigurationContext(config)
        return KeywordSet(
            scam = localizedContext.resources.getStringArray(R.array.scam_keywords).toList(),
            ad = localizedContext.resources.getStringArray(R.array.ad_keywords).toList(),
            officialTerms = localizedContext.resources.getStringArray(R.array.official_terms).toList(),
            userNegative = localizedContext.resources.getStringArray(R.array.user_negative).toList(),
            userPositive = localizedContext.resources.getStringArray(R.array.user_positive).toList()
        )
    }
}
```

## 9-5. 원문 폐기 보장 (재명시)

- `RawSearchResult.snippet`은 `FeatureExtractor.extract()` 호출 후 스코프 이탈 → GC 대상
- 반환값 `Map<FeatureType, Int>`에는 원문 없음
- `ExtractedSignal` 생성 시 `RawSearchResult` 참조 없음
- 테스트: `RawSearchResult` 클래스의 `internal` 가시성 검증

---
