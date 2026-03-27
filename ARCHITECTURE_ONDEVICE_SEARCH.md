# CallCheck 온디바이스 검색 아키텍처

## 헌법 (절대 원칙)

- **서버 없음**
- **중앙 요청 없음**
- **공용 API 의존 없음**
- **비용 발생 없음**
- **사용자 수 증가와 비용 무관**

검색은 API가 아니라 **"디바이스가 직접 하는 행위"**다.

---

## 확정 구조

```
CallCheck App
 ├─ Device Evidence (로컬)
 │    ├─ 연락처 DB
 │    ├─ 통화 이력
 │    ├─ SMS 메타데이터
 │    └─ 사용자 태그/메모
 │
 ├─ On-Device Web Scan (직접 HTTP)
 │    ├─ Google 스크래핑 (google.com/search?q=번호)
 │    ├─ Naver 스크래핑 (search.naver.com/search.naver?query=번호)
 │    ├─ Baidu 스크래핑 (baidu.com/s?wd=번호)
 │    └─ HTML 수신 → 텍스트 추출 → 의미 분석
 │
 ├─ Local Parsing Engine
 │    ├─ 키워드 클러스터링 (사기/스팸/택배/기관/기업)
 │    ├─ 발신처 추정 (반복 엔터티 추출)
 │    ├─ 소스 유형 분류 (스팸신고/뉴스/커뮤니티/공식)
 │    └─ 의미 신호 요약 생성 (SignalSummary)
 │
 └─ Decision Engine
      ├─ Relationship Score (0.0–1.0)
      ├─ Risk Score (0.0–1.0)
      ├─ 7개 PRD 카테고리 판정
      ├─ 4단계 리스크 레벨
      └─ 3개 지원 근거 생성
```

---

## 수정 내역 (이번 작업)

### 1. 삭제된 파일
| 파일 | 이유 |
|------|------|
| `GoogleCustomSearchProvider.kt` | Google API 사용. 헌법 위반. |
| `GenericWebSearchProvider.kt` | 범용 API 래퍼. 헌법 위반. |

### 2. 신규 생성 파일
| 파일 | 역할 |
|------|------|
| `BaiduScrapingSearchProvider.kt` | 중국 시장 온디바이스 스크래핑 |
| `HttpClientFactory.kt` | 공용 OkHttp 클라이언트 팩토리 |

### 3. 구조 변경
| 항목 | Before | After |
|------|--------|-------|
| `ProviderSummary` | providerName 노출 ("Google 3건") | `SignalSummary` — 의미만 노출 ("사기/피싱 신고 다수") |
| SearchModule DI | Google + Naver | Google + Naver + Baidu (병렬) |
| Google KDoc | "API로 전환 필요" | "API 절대 금지. 파싱 방식 변경 또는 fallback" |
| Naver KDoc | "Naver Search API 전환 필요" | "API 절대 금지. 디바이스 직접 수행" |
| Overlay UI | "Google: 스팸 5건" | "사기/피싱 신고 다수" |

---

## Fallback 구조

```
Google 스크래핑
  ├─ 성공 → 결과 병합
  └─ 실패 → 무시 (Naver/Baidu 결과로 보강)

Naver 스크래핑
  ├─ 성공 → 결과 병합
  └─ 실패 → 무시 (Google/Baidu 결과로 보강)

Baidu 스크래핑
  ├─ 성공 → 결과 병합
  └─ 실패 → 무시

전체 검색 실패
  └─ Device Evidence만으로 판정 (정상 동작)
      ├─ confidence: 0.70 (device only)
      └─ 카테고리: device 기반 또는 INSUFFICIENT_EVIDENCE
```

**핵심: 검색은 보강 요소지, 필수 요소가 아님.**

---

## UI 의미 노출 원칙

### 금지 (절대 하지 않음)
- ❌ "Google 3건"
- ❌ "Naver 2건"
- ❌ "Baidu 1건"
- ❌ 검색 엔진 이름 UI 노출

### 허용 (의미만 표시)
- ✅ "외교부 대표번호 가능성"
- ✅ "KT 고객센터 가능성"
- ✅ "사기/피싱 신고 다수"
- ✅ "택배/배송 업체 가능성"
- ✅ "스팸/광고 전화 의심"

---

## 검색 엔진별 스크래핑 전략

### Google (`GoogleScrapingSearchProvider`)
- URL: `https://www.google.com/search?q={번호}&hl=ko&num=10`
- 한계: JS 렌더링으로 결과 제한적
- 대응: 파싱 패턴 다변화, User-Agent 로테이션
- **절대 금지**: Google Custom Search API로 도망

### Naver (`NaverScrapingSearchProvider`)
- URL: `https://search.naver.com/search.naver?query={번호}&where=web`
- 강점: 한국 전화번호 검색 최적, SSR 유지
- 파싱: SDS 2024+ 컴포넌트 구조, nocr 링크 추출
- **절대 금지**: Naver Search API로 도망

### Baidu (`BaiduScrapingSearchProvider`)
- URL: `https://www.baidu.com/s?wd={번호}&rn=10`
- 강점: 중국 시장 커버리지, SSR 유지
- 파싱: h3.t 제목 블록, c-abstract 스니펫 추출
- **절대 금지**: Baidu API로 도망

---

## 타임아웃 구조

| 단계 | 타임아웃 |
|------|----------|
| 전체 검색 (SearchProviderRegistry) | 3,000ms |
| 개별 Provider | 2,500ms |
| Enrichment 전체 (Repository) | 1,500ms |
| Decision Engine | <50ms |

---

## 한 줄 결론

> 검색은 API가 아니라 **"디바이스가 직접 하는 행위"**여야 한다.
> 스크래핑 안 되면 → 파싱 방식을 바꾸거나 → 다른 신호로 대체.
> 절대 "API로 도망" 가면 안 된다.
