# CallCheck 1.0 — 실전 소규모 검증안

## 목적

FakeSearchProvider 기반 합성 분기 테스트를 넘어서,
실제 성격이 다른 번호 유형에서 판정이 정확히 갈라지는지 검증한다.

**원칙:**
- 중앙 저장/중앙 처리 없음 (온디바이스 only)
- 대표님 로컬 테스트 세트 기반
- 에뮬레이터 + GSM 시뮬레이션 환경

---

## 1. 검증 아키텍처

현재 시스템의 판정은 두 축으로 결정된다:

```
DeviceEvidence (연락처, 통화 이력, SMS 이력)
    ↓
    + → DecisionEngine → Category / Risk / Action
    ↑
SearchEvidence (검색 결과 키워드, 신고 건수, 추세)
```

### 실전 검증이 필요한 이유

합성 테스트(FakeSearchProvider)는 **분기 구조**만 증명한다.
실전 검증은 다음을 추가로 확인해야 한다:

1. **DeviceEvidence 단독 판정력** — 검색 없이 디바이스 증거만으로 올바른 판정이 나오는가
2. **혼합 시그널 처리** — 택배인데 스팸 키워드도 섞여 있을 때 어떻게 판단하는가
3. **경계값 행동** — risk score가 0.3이나 0.6 경계에 걸릴 때 안정적인가
4. **시간 경과 효과** — 오래된 통화 이력 vs 최근 이력의 가중치 차이

---

## 2. 테스트 번호 세트 (로컬)

에뮬레이터에서 다음 시나리오를 순서대로 실행한다.
각 시나리오는 **디바이스 상태 세팅** + **GSM 착신** + **판정 확인**으로 구성된다.

### Tier 1: DeviceEvidence 단독 판정 (SearchProvider 결과 없음)

| # | 시나리오 | 디바이스 세팅 | 예상 카테고리 | 예상 risk |
|---|---------|-------------|-------------|----------|
| D1 | 저장된 연락처 | 연락처에 번호 저장 | KNOWN_CONTACT | LOW |
| D2 | 발신 이력 있는 번호 | adb로 outgoing call log 3건 삽입 (60s+) | BUSINESS_LIKELY | LOW |
| D3 | 착신만 있고 모두 짧음 | incoming 5건, 각 5초, 연결 0건 | INSUFFICIENT_EVIDENCE | MEDIUM |
| D4 | 거절 이력 2회 이상 | rejected 3건, 연결 0건 | INSUFFICIENT_EVIDENCE | MEDIUM+ |
| D5 | 완전 미지 번호 | 이력 없음, 연락처 없음 | INSUFFICIENT_EVIDENCE | UNKNOWN |

**검증 포인트:** SearchEvidence가 empty일 때 DeviceEvidence만으로 판정이 의미 있게 갈라지는가.

### Tier 2: SearchEvidence 단독 판정 (디바이스 이력 없음)

| # | 시나리오 | 검색 결과 특성 | 예상 카테고리 | 예상 risk |
|---|---------|-------------|-------------|----------|
| S1 | 순수 택배 검색 | 택배/배송 키워드만 | DELIVERY_LIKELY | LOW |
| S2 | 순수 기관 검색 | 병원/학교/기관 키워드만 | INSTITUTION_LIKELY | LOW |
| S3 | 순수 스팸 검색 | 광고/영업/텔레마케팅 키워드 | SALES_SPAM_SUSPECTED | MEDIUM |
| S4 | 순수 사기 검색 | 사기/피싱/보이스피싱 키워드 | SCAM_RISK_HIGH | HIGH |
| S5 | 기업 검색 | 회사/고객센터/대표번호 키워드 | BUSINESS_LIKELY | LOW |

**검증 포인트:** 키워드 분류기(SearchResultAnalyzer)가 정확한 클러스터를 반환하는가.

### Tier 3: 혼합 시그널 (디바이스 + 검색 충돌)

| # | 시나리오 | 디바이스 | 검색 | 예상 결과 |
|---|---------|--------|------|----------|
| M1 | 저장된 연락처 + 스팸 검색 | 연락처 저장 | 스팸 키워드 | KNOWN_CONTACT / LOW (연락처 우선) |
| M2 | 발신 이력 + 사기 검색 | outgoing 2건 | 사기 키워드 | SCAM_RISK_HIGH / HIGH (사기 우선, risk dampened) |
| M3 | 택배 검색 + 스팸 검색 혼합 | 없음 | 택배+스팸 키워드 혼재 | SALES_SPAM_SUSPECTED / MEDIUM (스팸 우선순위 > 택배) |
| M4 | 거절 이력 + 기관 검색 | rejected 3건 | 병원/기관 키워드 | INSTITUTION_LIKELY / LOW~MEDIUM |

**검증 포인트:** 우선순위 결정 트리가 혼합 시그널에서 올바른 카테고리를 선택하는가.

### Tier 4: 경계값 테스트

| # | 시나리오 | 조건 | 경계 | 예상 행동 |
|---|---------|------|------|----------|
| B1 | risk score ≈ 0.30 | 스팸만 (+0.25) + 미지번호(+0.15) = 0.40 | MEDIUM 확정 | SALES_SPAM_SUSPECTED |
| B2 | risk score ≈ 0.60 | 스캠(+0.40) + 미지(+0.15) + 검색량(+0.10) = 0.65 | HIGH 확정 | SCAM_RISK_HIGH |
| B3 | relationship ≈ 0.50 | outgoing 2건(+0.30) + connected 3건(+0.20) = 0.50 | BUSINESS_LIKELY 경계 | 카테고리 정확히 BUSINESS_LIKELY |
| B4 | 카테고리-risk floor | 스캠 시그널만(+0.40) → raw MEDIUM → floor HIGH | enforceCategoryRiskConsistency 동작 | SCAM_RISK_HIGH + HIGH (not MEDIUM) |

**검증 포인트:** 경계값에서 불안정하거나 예측 불가능한 판정이 나오지 않는가.

---

## 3. 검증 실행 방법

### 3.1 디바이스 증거 주입 (에뮬레이터)

```bash
# 연락처 추가
adb shell content insert --uri content://com.android.contacts/raw_contacts --bind account_type:s: --bind account_name:s:
adb shell content insert --uri content://com.android.contacts/data --bind raw_contact_id:i:1 --bind mimetype:s:vnd.android.cursor.item/phone_v2 --bind data1:s:+821012345678

# 통화 이력 추가
adb shell content insert --uri content://call_log/calls \
  --bind number:s:+821012345678 \
  --bind type:i:2 \
  --bind duration:i:120 \
  --bind date:l:$(date +%s000)

# type: 1=incoming, 2=outgoing, 3=missed, 5=rejected
```

### 3.2 GSM 착신 시뮬레이션

```bash
# 에뮬레이터에서 착신 시뮬레이션
adb shell gsm call +821012345678
```

### 3.3 판정 결과 수집

```bash
# logcat에서 판정 결과 필터링
adb logcat -s CallCheckScreening:* DecisionEngine:* SearchResultAnalyzer:*
```

### 3.4 결과 기록 양식

각 테스트 케이스별 기록:

```
테스트 ID: D1
번호: +821099990000
디바이스 세팅: 연락처 저장
검색 결과: empty
---
실제 카테고리: ___
실제 risk: ___
실제 action: ___
실제 confidence: ___
Notification 색상: ___
Notification 제목: ___
---
예상 일치 여부: PASS / FAIL
불일치 원인 (FAIL시): ___
```

---

## 4. 합격 기준

### 필수 합격 (Must Pass)

1. **카테고리-risk 일관성**: SCAM_RISK_HIGH는 반드시 HIGH, SALES_SPAM_SUSPECTED는 반드시 MEDIUM 이상
2. **저장 연락처 우선**: KNOWN_CONTACT + 어떤 검색 결과든 → KNOWN_CONTACT + LOW
3. **사기 우선**: 사기 시그널 존재 시 다른 시그널보다 우선 판정
4. **Notification 색상 정확**: HIGH=빨강, MEDIUM=노랑, LOW=초록, UNKNOWN=회색
5. **자동 차단 없음**: 모든 시나리오에서 Notification만 표시, 사용자 터치 전까지 차단 없음 (v1.0 철학)

### 권장 합격 (Should Pass)

1. **혼합 시그널 분리**: M1~M4 시나리오에서 우선순위 트리가 예상대로 동작
2. **경계값 안정**: B1~B4 시나리오에서 경계를 넘나들지 않음
3. **confidence 합리성**: 증거가 많을수록 confidence 높음, 충돌 시그널이면 낮음

### 실패 시 대응

| 실패 유형 | 대응 |
|----------|------|
| 카테고리 오분류 | determineCategory() 우선순위 트리 재검토 |
| risk 불일치 | enforceCategoryRiskConsistency() 규칙 보강 |
| 경계값 불안정 | 임계값(0.3, 0.6) 조정 또는 hysteresis 도입 |
| Notification 불일치 | colorForRisk() 매핑 재확인 |

---

## 5. 실행 일정 (제안)

| 단계 | 내용 | 소요 시간 |
|------|------|----------|
| 1 | LocalTestSearchProvider 구현 (Tier별 시나리오 라우팅) | 1시간 |
| 2 | 디바이스 증거 주입 스크립트 작성 | 30분 |
| 3 | Tier 1~2 실행 (단독 판정) | 1시간 |
| 4 | Tier 3~4 실행 (혼합/경계) | 1시간 |
| 5 | 결과 정리 + 자비스 리포트 | 30분 |

**총 예상: 4시간**

---

## 6. 참고: FakeSearchProvider 현황

현재 FakeSearchProvider의 라우팅이 E.164 번호 형식과 맞지 않는 상태입니다.
(이전 세션에서 `takeLast(4)` suffix 매칭으로 수정했으나, 디스크 파일이 원본 상태)

실전 검증 전에 FakeSearchProvider를 **LocalTestSearchProvider**로 교체하여
Tier별 시나리오에 맞는 검색 결과를 정확히 반환하도록 해야 합니다.

---

*작성: 비전 | 2026-03-24 | CallCheck 1.0 실전 검증안 v1*
