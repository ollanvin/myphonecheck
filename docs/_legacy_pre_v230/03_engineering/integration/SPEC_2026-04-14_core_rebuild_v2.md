# SPEC v2: 2026-04-14 Core Rebuild — 수익 설계 보강본

**작성일**: 2026-04-14
**작성자**: 비전
**승인 검토**: 자비스 (수익 설계 보강 지시)
**승인자**: 대표님 (founder@idolab.ai)
**기준 문서**: `SPEC_2026-04-14_core_rebuild.md` (v1)
**관계**: v1은 기준선으로 유지, v2는 수익 설계 보강본으로 별도 관리
**원칙 유지**: 100% 온디바이스, Play Billing 전용, 금액 표현 금지, Custom Tab 전용 1차 릴리즈

---

## 1. v1 대비 핵심 변경

v1은 기능·구조·기술 완성도는 충분하지만 "왜 결제해야 하는지"가 약했다. v2는 **무료 경험 → 가치 체감 → 끊기면 불안**의 전환 루프를 설계에 내장한다.

### 1.1 변경 요약 (4건)

| # | 패키지 | 변경 성격 | v1 상태 | v2 상태 |
| - | ------ | --------- | ------- | ------- |
| ①  | P1 구독 UX | **전환 앵커 신규** | 자동 구독·취소 버튼만 강조 | 구독 화면 최상단에 개인 성과 카드 (실측 카운트 기반) 고정 노출 |
| ②  | P5 CallCheck | **표현 전환** | "Google 검색 결과" 중립 표기 | "이 번호와 유사한 번호에서 유사 패턴이 발견되었습니다" — 근거 카운트 동반 |
| ③  | P6 PushCheck | **CTA 연결** | 통계만 표시 | 통계 카드 하단에 "이 앱 알림 차단" 버튼 상시 노출 + 기간·카운트 문구 |
| ④  | P2 i18n | **전략 재조정** | 70개 언어 동시 목표 | T1 20개 완벽 우선 → T2·T3 점진 확장 (번역 품질 > 언어 수) |

### 1.2 유지 원칙 (절대 변경 금지)

- **금액 표현 금지** — 카운트·일수·횟수만. "XX원 절약", "XX원 피해" 등 일체 금지
- **Custom Tab 전용 1차 릴리즈** — Google 검색 스크래핑 금지, Custom Search API 도입은 백로그로 격리
- **저장번호 완전 제외** — P3 공통 인프라 기반, CallCheck·MessageCheck·PushCheck 공통 적용
- **빅테크 방식** — 랜덤/임시/땜방/우회 금지, SDK·플랫폼 공식 API만 사용

### 1.3 v1 대비 삭제·제외 항목

없음. v2는 v1 위에 **덧붙이는 보강**이며 v1의 어떤 내용도 삭제하지 않는다.

---

## 2. 전환 설계 원칙

### 2.1 왜 바꾸는가

v1은 "무엇을 할지"를 완성했지만 "왜 결제할지"를 설계하지 않았다. 결과:

- 사용자는 무료 체험 중 가치를 체감해도 종료 시점에 **이별 비용(loss aversion)** 을 느끼지 못한다
- 취소 버튼을 크게 만든 것은 **사용자 신뢰**를 위한 것이지 전환을 위한 것이 아니다
- "좋은 앱"과 "돈 버는 앱"의 차이는 **가치 체감의 시각화**에 있다

### 2.2 무엇이 수익적으로 부족했는가

| v1 약점 | 증상 | v2 대응 |
| ------- | ---- | ------- |
| 가치 체감 미시각화 | 사용자는 앱이 뭘 했는지 모름 | **P1 전환 앵커**: 구독 화면 최상단에 실측 카운트 카드 |
| 근거 표시 부족 | "왜 이 번호가 위험한가?" 답 없음 | **P5 위험 패턴 표현**: 유사 prefix·차단 신고 실측 카운트 제시 |
| 행동 유도 단절 | 통계만 보고 끝 | **P6 CTA 연결**: 통계 카드 = 1클릭 차단 버튼 |

### 2.3 어떤 실측 지표만 쓰는가 (CRITICAL RULE 유지)

v2 전 영역에서 사용 허용되는 지표는 오직 다음 4종뿐이다. 이 외 일체 금지.

1. **차단된 의심 통화 수** — 사용자가 거절/차단한 카운트, DB 실측
2. **반복 메시지 수** — 동일 송신자·유사 본문 반복 카운트, DB 실측
3. **위험 링크 메시지 수** — LinkSafetyScorer 임계값 초과 메시지 카운트, DB 실측
4. **알림 송신자 통계** — 앱별·기간별 알림 발생량, DB 실측

**금지 표현 예시**:
- ❌ "이번 달 XX원 절약"
- ❌ "연간 피해 예방 가치 XX만원"
- ❌ "평균 사용자 대비 N% 안전"
- ❌ "이 기능의 가치는 XX원"

**허용 표현 예시**:
- ✅ "최근 7일 동안 의심 전화 18건 탐지"
- ✅ "이 번호와 유사한 번호에서 유사 패턴이 발견되었습니다"
- ✅ "이 앱은 지난 30일간 120건의 광고 알림을 보냈습니다"
- ✅ "이 기능은 구독 시 유지됩니다"

### 2.4 실측 원칙

모든 문구의 숫자는 **DB 쿼리 결과** 또는 **OS 공식 API 반환값**에 기반한다. 추정·평균·가중치·외삽 일체 금지.

- 데이터 부족 시: 기간을 확장해서라도 실측 0이 아닌 값을 찾는다 (7일 → 30일 → 90일 순). 그래도 0이면 카드 자체를 숨긴다 ("0건입니다" 허위 안심 유도 금지).
- 기간 표기는 반드시 명시 ("최근 N일"). 기간 생략 금지.

---

## 3. 패키지별 변경 상세

### 3.1 P1 구독 UX — 전환 앵커 신규

#### 3.1.1 변경 전 (v1)

- SubscriptionScreen 타이틀 "구독", 무료/유료 분기 제거
- RED 최대 크기 취소 버튼 하단 고정
- 재구독 체험 차단
- 국가 티어 UI 제거

#### 3.1.2 변경 후 (v2 추가분)

**① 개인 성과 카드 (구독 화면 최상단 고정)**

```
┌────────────────────────────────┐
│ 무료 체험 종료까지 2일 남음       │ ← D-day 카운트 (체험 중에만)
│ ──────────────────────────     │
│ 최근 7일 동안                   │
│ 의심 전화 18건 탐지              │
│ 위험 링크 메시지 4건 확인         │
│                                │
│ 이 기능은 구독 시 유지됩니다      │
└────────────────────────────────┘
[  구독 계속하기 (프라이머리 버튼) ]
────────────────────────────────
[  구독 취소 (RED, 하단 고정)    ]
```

- 카드는 Composable 신규: `SubscriptionValueAnchorCard`
- 표시 지표: 본 문서 2.3 허용 4종 중 해당 기간 실측 0이 아닌 최대 2종
- 기간 자동 확장 규칙: 7일 실측 0 → 30일 → 90일 → 모든 기간. 그래도 0이면 카드 전체 숨김
- 하단 "이 기능은 구독 시 유지됩니다" 문구 고정
- 금액·% 일체 없음

**② D-day 카운트 (전환 트리거 시간 제한) — v2.1 보강**

- 카드 최상단에 "무료 체험 종료까지 N일 남음" 라인 고정
- **체험 중에만 표시**, 정기 구독자·비구독자에게는 라인 전체 숨김 (오표시 금지)
- 1일 이하 남으면 "오늘 종료" / 시간 단위 "N시간 남음"으로 자동 전환
- 종료일 계산: `trialStartEpochMs + trialDurationMs - now`
  - `trialStartEpochMs`: Play Billing `Purchase.purchaseTime` 또는 최초 구독 시 `SharedPreferences` 저장 시각
  - `trialDurationMs`: `ProductDetails.subscriptionOfferDetails[].pricingPhases.pricingPhaseList[0].billingPeriod`(ISO 8601 `P7D` 등) 파싱
- 허위 위협 금지: 실제 종료 후에는 "무료 체험이 종료되었습니다" 중립 문구 (과장·재촉 표현 금지)

#### 3.1.3 추가 쿼리

```kotlin
// data/local-cache/.../dao/CallHubDao.kt
@Query("""
    SELECT COUNT(*) FROM call_hub
    WHERE action IN ('REJECTED', 'BLOCKED')
      AND timestamp >= :sinceEpochMs
""")
suspend fun countBlockedSince(sinceEpochMs: Long): Int

// data/local-cache/.../dao/MessageHubDao.kt
@Query("""
    SELECT COUNT(*) FROM message_hub
    WHERE hasRiskyLink = 1
      AND timestamp >= :sinceEpochMs
""")
suspend fun countRiskyLinkMessagesSince(sinceEpochMs: Long): Int

@Query("""
    SELECT COUNT(*) FROM message_hub
    WHERE repeatCount >= :threshold
      AND timestamp >= :sinceEpochMs
""")
suspend fun countRepeatMessagesSince(sinceEpochMs: Long, threshold: Int = 3): Int
```

**D-day 카운트 계산 로직** (쿼리 아님, ViewModel 내부):

```kotlin
// feature/billing/.../SubscriptionViewModel.kt
private fun computeTrialDday(
    trialStartEpochMs: Long,
    trialDurationMs: Long,
    nowEpochMs: Long,
): TrialCountdown {
    val endMs = trialStartEpochMs + trialDurationMs
    val remainingMs = endMs - nowEpochMs
    return when {
        remainingMs <= 0 -> TrialCountdown.Ended
        remainingMs < TimeUnit.DAYS.toMillis(1) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(remainingMs).toInt()
            if (hours <= 0) TrialCountdown.EndsToday
            else TrialCountdown.HoursLeft(hours)
        }
        else -> TrialCountdown.DaysLeft(
            TimeUnit.MILLISECONDS.toDays(remainingMs).toInt()
        )
    }
}

sealed class TrialCountdown {
    object Ended : TrialCountdown()
    object EndsToday : TrialCountdown()
    data class HoursLeft(val hours: Int) : TrialCountdown()
    data class DaysLeft(val days: Int) : TrialCountdown()
    object NotOnTrial : TrialCountdown()  // 정기 구독자·비구독자
}
```

**billingPeriod 파싱**:
```kotlin
// ISO 8601 period "P7D", "P1M", "P1Y" 파싱
import java.time.Period
val duration = Period.parse(billingPeriod)
val durationMs = duration.days * 86_400_000L +
                 duration.months * 30L * 86_400_000L +
                 duration.years * 365L * 86_400_000L
```

#### 3.1.4 영향 파일

- `feature/billing/src/main/kotlin/.../SubscriptionScreen.kt` — `SubscriptionValueAnchorCard` 신규 Composable 삽입, D-day 라인 최상단
- `feature/billing/src/main/kotlin/.../SubscriptionViewModel.kt` — `loadValueAnchor()` + `computeTrialDday()` 메서드
- `feature/billing/src/main/kotlin/.../SubscriptionValueAnchorState.kt` (신규) — UI 상태 sealed class
- `feature/billing/src/main/kotlin/.../TrialCountdown.kt` (신규) — D-day sealed class
- `feature/billing/src/main/kotlin/.../PlayBillingRepository.kt` — `Purchase.purchaseTime` 노출 + `billingPeriod` 파싱
- `data/local-cache/src/main/kotlin/.../dao/CallHubDao.kt` — `countBlockedSince()` 추가
- `data/local-cache/src/main/kotlin/.../dao/MessageHubDao.kt` — `countRiskyLinkMessagesSince()`, `countRepeatMessagesSince()` 추가
- `data/local-cache/src/main/kotlin/.../entity/MessageHubEntity.kt` — `hasRiskyLink: Boolean`, `repeatCount: Int` 컬럼 확인·추가 (기존 스키마 검수 필요)
- `feature/billing/src/main/res/values/strings.xml` + values-XX — 앵커 카드 문구 키 추가 (`value_anchor_*`, `trial_countdown_*`)

---

### 3.2 P5 CallCheck — 위험 패턴 표현 전환

#### 3.2.1 변경 전 (v1)

- 오버레이에 Google 검색 결과 요약 표시 (Chrome Custom Tab 1차)
- 유사번호 검색 (prefix 매칭)
- 태그·메모

#### 3.2.2 변경 후 (v2 추가분)

**① 오버레이 상단 메시지 재설계**

- 기존: "Google 검색으로 확인" — 중립·수동
- 신규: "이 번호와 유사한 번호에서 유사 패턴이 발견되었습니다" — 근거 기반·능동

**② 근거 카운트 필수 표기**

메시지 하단에 실측 카운트 2종 병기:

```
┌──────────────────────────────────────┐
│ ⚠ 이 번호와 유사한 번호에서             │
│   유사 패턴이 발견되었습니다              │
│                                      │
│ 유사 prefix 번호 3건                    │
│ 차단·거절 이력 7건                      │
└──────────────────────────────────────┘
```

- 카운트 0이면 해당 줄 숨김, 메시지는 "이 번호에 대한 신고 이력이 없습니다"로 중립 전환
- 허위 불안 유도 금지: 근거 없을 때 경고 문구 출력 절대 금지

**③ 검색 기능은 v1 그대로 유지**

- Chrome Custom Tab 전용
- Custom Search API는 백로그

#### 3.2.3 추가 쿼리

```kotlin
// data/local-cache/.../dao/CallHubDao.kt
@Query("""
    SELECT COUNT(DISTINCT phoneNumber) FROM call_hub
    WHERE phoneNumber LIKE :prefixPattern
      AND phoneNumber != :excludeExact
""")
suspend fun countSimilarPrefixNumbers(prefixPattern: String, excludeExact: String): Int

@Query("""
    SELECT COUNT(*) FROM call_hub
    WHERE phoneNumber = :number
      AND action IN ('REJECTED', 'BLOCKED')
""")
suspend fun countBlockedEventsForNumber(number: String): Int
```

`prefixPattern`은 E.164 정규화 번호의 앞 6~8자리를 `LIKE '+821012%'` 형태로 생성 (국가 코드 + 사업자 prefix 범위).

#### 3.2.4 영향 파일

- `feature/decision-ui/src/main/kotlin/.../overlay/CallOverlayContent.kt` — 상단 메시지 영역 재설계, 근거 카운트 섹션 추가
- `feature/call-intercept/src/main/kotlin/.../CallerIdOverlayManager.kt` — ViewModel에 카운트 주입
- `feature/call-intercept/src/main/kotlin/.../CallerIdOverlayViewModel.kt` (신규 또는 확장) — `loadRiskPatternEvidence(number)` 메서드
- `data/local-cache/src/main/kotlin/.../dao/CallHubDao.kt` — `countSimilarPrefixNumbers()`, `countBlockedEventsForNumber()` 추가
- `feature/call-intercept/src/main/res/values/strings.xml` + values-XX — `risk_pattern_*` 키 추가 (카운트 0·≥1 분기 문구)

---

### 3.3 P6 PushCheck — 푸시 휴지통 (2026-04-22 재정의)

#### 3.3.1 기존 v2.1 P6 (폐기)

통계 표시 + 1클릭 차단 모델. 2026-04-22 대표님 판단으로 "통계만 보여주는 반쪽 기능"이라 폐기. 본 문서에 이력으로만 남김.

#### 3.3.2 신규 모델: 푸시 휴지통

**핵심 사상**: 스팸 알림은 시스템 차원에서 완전 격리하되, 사용자가 나중에 되돌아볼 수 있는 "휴지통"에 보관.

**대형 플랫폼 처리 (Notification Channel ID 기반)**:
- 앱별로 Android Notification Channel ID 목록 자동 수집
- 사용자가 채널 단위로 허용/차단 (체크박스 UI)
- 차단된 채널의 알림은 수신 즉시 `NotificationListenerService.cancelNotification()` + Room DB에 저장
- 앱 내장 매핑 테이블(상위 30~50개 앱)로 채널 ID → 한글 라벨 변환

**중소형 앱 처리 (앱 단위)**:
- 채널 분리 안 한 앱 또는 단일 채널 앱: "전체 허용" 또는 "전체 차단" 2선택
- 기본값: "전체 허용" (사용자 명시적 차단 전까지 개입 안 함)
- 알림 본문 파싱 금지. 메타데이터만 사용.

**기술 요구사항**:
- `NotificationListenerService` 권한 (런타임 수동 허용 필요)
- `cancelNotification()`이 채널 ID 기반으로 정확히 작동하는지 기술 검증 필요 (Stage 1 초기에 수행)
- 차단 알림은 시스템 노출 0 (소리·진동·상단바·락스크린 전부 제로)
- 휴지통 UI는 MyPhoneCheck 앱 내부에만 존재

**유지보수성 원칙**:
- 알림 본문 파싱 금지 (다국어·오탐 리스크)
- 앱 내장 매핑 테이블은 앱 업데이트로 갱신 (원격 서버 없음)
- 채널 매핑 없는 앱은 원본 채널 이름 그대로 표시

**서버 의존성**: 0. 디바이스 완결형.

---

### 3.4 P2 i18n — T1 우선 전략

#### 3.4.1 변경 전 (v1)

- 70개 언어 동시 목표 (T1 20 + T2 30 + T3 20)
- W1~W3 감사·UI·번역 동시 진행

#### 3.4.2 변경 후 (v2)

**전략 재조정**: 번역 품질 > 언어 수

- **W1~W4**: T1 20개 언어 완벽 완성
  - 역번역 검수 비율을 v1의 10%에서 **30%로 상향**
  - T1 = en, ko, ja, zh-CN, zh-TW, es, pt-BR, fr, de, it, ru, ar, hi, id, vi, th, tr, nl, pl, sv
- **T2·T3은 별도 릴리즈 사이클로 분리**
  - T1 릴리즈 후 사용자 분포(Play Console `countryCode` 통계) 기준 상위 10개국부터 점진 확장
  - T2·T3 번역은 T1 안정화 이후 백로그로 이관

**근거**:
- 번역 품질 미흡한 상태에서 언어 수만 늘리면 **전환율 하락 + 리뷰 평점 하락 + 유지보수 비용 증가**
- Play Console 실측 데이터 기반 우선순위가 과도한 선제 번역보다 효율적
- T1 20개만으로 Play Store 상위 사용자 약 85% 이상 커버 (Google 공식 언어 분포 근거)

#### 3.4.3 영향 파일

- `app/build.gradle.kts` — `resConfigs` T1 20개로 1차 축소 (T2·T3 추가 시 확장)
- `app/src/main/res/values-XX/strings.xml` — T1 20개만 유지, T2·T3 임시 placeholder 제거
- `docs/I18N_STRATEGY.md` (신규) — T1 확정 리스트, 역번역 검수 비율, T2 확장 트리거 기준(Play Console 통계 기준점) 명문화

#### 3.4.4 타임라인 재조정

| 주차 | v1 계획 | v2 수정 계획 |
| --- | ------- | ----------- |
| W1  | P1 + i18n 감사 | P1 + i18n T1 감사 |
| W2  | i18n Phase B·C (70개) | i18n T1 번역 발주 + Settings 언어 UI |
| W3  | P3 + P4 | i18n T1 역번역 검수 30% + P3 + P4 |
| W4  | P5 CallCheck | P5 CallCheck |
| W5~ | 이후 v1 동일 | 이후 v1 동일 (T2·T3 W8 이후 별도 사이클) |

---

## 4. 확정 완료 (v2.1 — 대표님·자비스 승인)

v2 초안의 결정 대기 4건은 자비스 권고에 따라 2026-04-14 확정 완료됐다.

| # | 항목 | 확정값 | 근거 |
| - | ---- | ------ | ---- |
| 1 | P1 개인 성과 카드 | **자동 확장(7일→30일→90일→전체) + 0이면 숨김** | "0건" 노출은 가치 없음 표시 → 전환 하락. 숨김이 UX 자연스러움 유지 |
| 2 | P5 0건 문구 | **"이 번호에 대한 신고 이력이 없습니다" 중립 문구** | 허위 불안 유도 금지. 신뢰 = 장기 수익 |
| 3 | P6 CTA 문구 | **도미넌트 카테고리 자동 선택** | 사용자 이해도·클릭률·행동 연결 자연스러움 확보 |
| 4 | i18n T1 리스트 | **20개 언어 현행 확정** (en, ko, ja, zh-CN, zh-TW, es, pt-BR, fr, de, it, ru, ar, hi, id, vi, th, tr, nl, pl, sv) | 이미 글로벌 커버 충분. 번역 품질이 우선 |

### 4.1 v2.1 추가 보강 (자비스 지시)

**전환 트리거 시간 제한** — 구독 화면 개인 성과 카드 최상단에 **D-day 카운트 라인** 추가.

- 표시 조건: 무료 체험 중인 사용자에게만 노출
- 문구: "무료 체험 종료까지 N일 남음" / "오늘 종료" / "N시간 남음" / "무료 체험이 종료되었습니다"
- 상세 스펙: 본 문서 3.1.2 ②번 블록 참조
- 효과 근거: 시간 제한 표시 = loss aversion 자극 → 전환율 상승 (Google Play 공식 가이드 및 업계 통설)
- 과장·재촉 표현 금지 원칙은 유지 (예: "지금 결제 안 하면 위험합니다" 따위 금지)

---

## 5. 변경 이력

| 일자       | 버전 | 변경                                                         | 작성자 |
| ---------- | ---- | ------------------------------------------------------------ | ------ |
| 2026-04-14 | v1   | 초안 작성                                                    | 비전   |
| 2026-04-14 | v2   | 수익 설계 보강 (자비스 지시 4건 반영)                        | 비전   |
| 2026-04-14 | v2.1 | 4건 확정 (자비스 권고 승인) + D-day 카운트 전환 트리거 보강 | 비전   |
