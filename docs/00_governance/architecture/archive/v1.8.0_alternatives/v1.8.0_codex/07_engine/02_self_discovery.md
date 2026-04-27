# 7. Self-Discovery (환경 자가 발견)

**원본 출처**: v1.7.1 §7 (128줄)
**v1.8.0 Layer**: Engine
**의존**: `07_engine/01_three_layer.md` + `07_engine/03_nkb.md`
**변경 이력**: 본 파일은 v1.7.1 §7 (128줄) 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_codex/07_engine/02_self_discovery.md`

---


본사 매핑 0 원칙(제3조·제7조)을 지키면서 국가별 최적 검색 엔진·공식 도메인을 찾는 방법.

## 7-1. Search Engine Self-Discovery

디바이스가 **보편적 글로벌 검색 엔진 후보 시드**를 빌드에 포함하고, 런타임에 직접 probe하여 응답한 엔진만 채택한다.

```kotlin
suspend fun probeSearchEngines(simIdentity: SimIdentity): List<SearchEngineRef> {
    // 후보 시드 (글로벌 검색 엔진 일반 목록, 매핑 아님)
    val candidates = listOf(
        "https://www.google.com",
        "https://www.bing.com",
        "https://duckduckgo.com",
        "https://yandex.com",
        "https://www.baidu.com",
        "https://www.naver.com",
        "https://www.yahoo.co.jp"
        // ... 글로벌 일반 후보. 본사가 "이 SIM = 이 엔진" 매핑 안 함.
    )

    // 디바이스가 직접 probe (1초 timeout)
    val responsive = candidates.parallelMap { url ->
        val start = System.currentTimeMillis()
        try {
            val response = httpClient.head(url) {
                timeout { requestTimeoutMillis = 1000 }
            }
            if (response.status.isSuccess()) {
                SearchEngineRef(
                    domain = url,
                    responseTimeMs = System.currentTimeMillis() - start
                )
            } else null
        } catch (e: Exception) {
            null  // 응답 없거나 차단된 엔진은 채택 안 함
        }
    }.filterNotNull()

    // 응답 시간 기준 정렬 (빠른 것 우선)
    return responsive.sortedBy { it.responseTimeMs }
}
```

**참고**: 후보 도메인 시드 리스트는 빌드 시 디바이스에 포함되지만, 이는 "매핑"이 아니라 "probe 대상 후보"이다. 후보를 probe해서 응답한 것만 사용하므로, 본사가 "한국 = 네이버"라고 단정하지 않는다. 시드는 보편적 글로벌 검색 엔진 도메인 목록일 뿐이며, 어느 것이 어느 환경에 적합한지는 디바이스가 결정한다.

## 7-2. Official Domain Self-Discovery

각국 정부·공공 도메인도 동일 원리.

```kotlin
suspend fun probeOfficialDomains(simIdentity: SimIdentity): List<DomainRef> {
    val countryCode = simIdentity.simMcc?.toCountryCode()
        ?: simIdentity.networkCountryIso
        ?: extractCountryFromLocale(simIdentity.locale)

    // 일반적 정부 TLD 패턴 (글로벌 표준)
    val tldCandidates = buildList {
        countryCode?.let {
            add(".gov.${it.lowercase()}")  // 예: .gov.kr
            add(".go.${it.lowercase()}")    // 예: .go.kr (일부 국가)
        }
        add(".gov")  // 미국 정부
        add(".gob")  // 스페인어권 정부
        add(".gouv") // 프랑스어권 정부
    }

    val responsive = tldCandidates.parallelMap { tld ->
        val testDomains = listOf("www$tld", "police$tld")
        testDomains.firstNotNullOfOrNull { domain ->
            try {
                val response = httpClient.head("https://$domain") {
                    timeout { requestTimeoutMillis = 1000 }
                }
                if (response.status.isSuccess()) {
                    DomainRef(tld = tld, sample = domain)
                } else null
            } catch (e: Exception) { null }
        }
    }.filterNotNull()

    return responsive
}
```

## 7-3. ClusterProfile 생성

```kotlin
fun generateClusterId(simIdentity: SimIdentity): String {
    // 환경 특성을 해시화 (본사 사전 정의 0)
    val components = listOf(
        simIdentity.simMcc ?: "",
        simIdentity.networkCountryIso ?: "",
        simIdentity.locale.split("_")[0]  // 언어만
    )
    val hash = components.joinToString("|").sha256().take(12)
    return "auto_$hash"
    // 예: "auto_a3f9b2c1d4e7"
    // 본사가 "CL_KR", "CL_EN" 같은 사전 정의 안 함. 디바이스가 동적 생성.
}
```

## 7-4. Self-Discovery의 헌법 정합성

- 본사가 "한국 SIM이면 네이버"라고 매핑하지 않음 → 제3조 정합
- 디바이스가 직접 ping해서 응답 받은 것만 사용 → 제7조 정합
- 어느 국가 SIM에서도 동일 코드 작동 → 190개국 자동 대응 (메모리 헌법)
- 국가 차단/검열로 일부 엔진 작동 안 해도, 응답한 다른 엔진으로 graceful degradation
- L3 상황(네트워크 0)에서는 probe 자체가 실패하지만, 기존 ClusterProfile이 NKB에 영구 저장되어 있으므로 거위는 멈추지 않음 (제4조 정합)

## 7-5. probe 주기 정책

| 이벤트 | 동작 |
|---|---|
| 앱 최초 실행 (Cold Start) | 전체 probe, ClusterProfile 생성 |
| SIM 변경 감지 | 전체 probe, 새 ClusterProfile 생성 (기존 보존) |
| 7일 경과 (WorkManager) | 재검증 probe, 응답 시간 갱신 |
| 사용자 조치 "검색 엔진 변경" | 해당 엔진만 재 probe |

## 7-6. 사용자 오버라이드

"어느 검색 엔진을 쓸지" 사용자가 수동 선택 가능. 설정 화면 `ClusterEditScreen` (§23 프로젝트 구조의 `feature/settings`).

이는 제3조 정합: 최종 결정은 디바이스 + 사용자이지 중앙이 아니다.

---

