# 6. 3계층 소싱 상세 (Three-Layer Sourcing Detail)

**원본 출처**: v1.7.1 §6 (72줄)
**v1.8.0 Layer**: Engine
**의존**: `06_product_design/04_system_arch.md` + `07_engine/02_self_discovery.md`
**변경 이력**: 본 파일은 v1.7.1 §6 (72줄) 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_codex/07_engine/01_three_layer.md`

---


## 6-1. Decision Engine이 3계층을 통합하는 순서

Decision Engine은 `evaluate(query: IdentifierType): RiskKnowledge` 호출 시 다음 순서로 3계층을 통합한다.

```
1. NKB Lookup (Layer 1)
   - Hit & not Stale → 즉시 반환 (L3 경로)
   - Hit but Stale → 반환하되 백그라운드 재검증 큐 등록
   - Miss → 2단계 진행

2. 외부 probe (Layer 2 + Layer 3 병렬)
   - Layer 2: 일반 검색 (쿼터 남은 경우)
   - Layer 3: 공공 공신력 API (우선)
   - 타임아웃 2초, 먼저 도착한 결과부터 처리

3. SearchResultAnalyzer
   - 각 소스별 featureCounts 추출
   - Tier 가중치 적용 (T1 0.3 / T2 0.5 / T3 0.8 / T4 1.0)

4. ConflictResolver
   - Softmax 정규화
   - topCategory · topConfidence · isAmbiguous 결정

5. NKB Write
   - featureCounts + 메타만 저장
   - 원문은 폐기

6. RiskKnowledge 반환 (Surface Layer로)
```

## 6-2. 네트워크 정책

- **네트워크 진입점**: Layer 2·3 내부 HttpClientProvider (Ktor, timeout 1초 기본)
- **Surface Layer 직접 네트워크 금지**: R5 규칙
- **도메인 화이트리스트**: 빌드 시 고정 (검색 엔진 시드 + 공공 API 도메인)
- **DoH/DoT 선택**: 대표님 판단 필요 (초기 시스템 DNS)
- **AppTracking**: 디바이스 ID·광고 ID 사용 금지 (제1조)

## 6-3. 원문 폐기 보장

- Kotlin 레벨: `RawSearchResult` 데이터 클래스는 Decision Engine 내부 스코프에서만 생성. Surface·NKB로 반환 금지 (컴파일 타임 visibility).
- 테스트: `ExtractedSignal`의 필드에 `rawSnippet: String`이 **존재하지 않음**을 `FreezeMarkerTest`가 검증 (§33-1-1).
- 정적 분석: Detekt 규칙 `ForbiddenRawField`로 금지 필드 자동 탐지.

## 6-4. 3계층 데이터 Freshness (Tier별 maxAge)

| Tier | 출처 분류 | maxAge | Stale 시 동작 |
|---|---|---|---|
| Tier 1 | 커뮤니티/포럼 | 30일 | Stale 플래그 + 다음 조회 시 재검증 |
| Tier 2 | 일반 사이트 | 90일 | Stale 플래그 + 백그라운드 재검증 |
| Tier 3 | 정부/공식 (KISA·경찰청·금감원) | 180일 | Stale 플래그만, 그대로 사용 |
| Tier 4 | 회사 공식 (스토어·공식 홈페이지) | 365일 | Stale 플래그만, 그대로 사용 |

**Stale은 삭제 트리거가 아니다.** 사용자 자산이므로 유지하고, 신뢰도만 낮춘다.

## 6-5. 쿼터·요금 관리

| 소스 | 쿼터 | 요금 |
|---|---|---|
| Google Programmable Search API | 100/일 무료, 10K/일 유료 ($5/1K) | 대표님 판단 필요 (초기 무료 범위 내 운영) |
| Bing Search API | 1K/월 무료 | 백업 옵션 |
| KISA·경찰청·금감원 | 확인 필요 (공공 데이터 개방 정책) | 기본 무료 |
| NVD CVE | 무료 | 무료 |
| CISA KEV | 무료 | 무료 |
| Have I Been Pwned | 유료 ($3.50/월 최소) | 있으면 좋음 |

사용자당 쿼터 초과 시 **Custom Tab 수동 검색으로 전환** (Layer 2 UX 1차 진입). 제품 가치 저하 없음.

---

