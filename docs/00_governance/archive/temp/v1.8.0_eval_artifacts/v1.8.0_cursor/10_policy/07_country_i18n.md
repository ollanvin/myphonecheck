# 28. 국가 / 언어 분리 (Patch 05)

**원본 출처**: v1.7.1 §28 (3344–3400)
**v1.8.0 Layer**: Policy
**의존**: `00_core/01_primary.md`
**변경 이력**: 본 파일은 v1.7.1 §28 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cursor/10_policy/07_country_i18n.md`

---

# 28. 국가 / 언어 분리 (Patch 05)

v1.5.1 Patch 05에서 명시된 원칙: **언어 ≠ 국가**. 사용자 locale과 SIM·Network country는 독립 축.

## 28-1. 분리 축

| 축 | 소스 | 용도 |
|---|---|---|
| SIM Country | `TelephonyManager.simCountryIso` | 번호 E.164 변환·스팸 DB 조회 |
| Network Country | `TelephonyManager.networkCountryIso` | 로밍 감지·Self-Discovery ClusterProfile |
| User Locale | `context.resources.configuration.locales[0]` | UI 언어·strings.xml 선택 |
| Phone Locale | `Locale.getDefault()` | Kotlin 기본 locale |
| TimeZone | `TimeZone.getDefault()` | `lastUsedAt` 등 시각 포맷 |

## 28-2. 흔한 오해 시나리오

| 상황 | 문제 | 해결 |
|---|---|---|
| 한국 거주 영어 사용자 | SIM=KR이지만 locale=en → 영어 UI + 한국 스팸 DB | 분리 유지, 자연스러움 |
| 해외 출장 중 한국 SIM | Network=JP, SIM=KR → 일본 로밍 감지 | ClusterProfile은 KR 유지, 일본 한시 조회 우선 |
| 다중 SIM 디바이스 | SIM 1개만 반영 | SubscriptionManager로 활성 SIM 선택 |
| SIM 없는 태블릿 | simCountryIso=null | Network 또는 locale 기반 |
| VPN 사용자 | Network이 실제 위치와 불일치 | SIM 우선, VPN 신경 쓰지 않음 |

## 28-3. Cluster 생성 규칙 (§7-3 연결)

```kotlin
fun deriveCountryCode(simIdentity: SimIdentity): String? {
    // 우선순위: SIM > Network > Locale 추론
    return simIdentity.simCountryIso
        ?: simIdentity.networkCountryIso
        ?: extractCountryFromLocale(simIdentity.locale)
}

fun extractCountryFromLocale(localeTag: String): String? {
    // "en_US" → "US", "ko_KR" → "KR"
    // "en" (country 없음) → null
    return Locale.forLanguageTag(localeTag.replace("_", "-"))
        .country
        .takeIf { it.isNotEmpty() }
}
```

## 28-4. UI 언어 vs 판정 언어

- **UI 언어**: 사용자 locale (strings.xml)
- **판정 언어 (FeatureExtractor 키워드)**: `KeywordLoader.loadForLocale(locale)` — 동일 locale 사용

**주의**: 판정 언어와 검색 엔진 언어는 별개. 사용자가 한국 거주·영어 locale이어도, 한국 SIM이라면 Self-Discovery가 한국 검색 엔진을 probe할 수 있다.

---

# 29. (의도적 공백 — v1.5.x 계보 번호 정합)

v1.5.x에서 29번은 사용되지 않았다. 버전 계보 일관성을 위해 공백 유지.

---
