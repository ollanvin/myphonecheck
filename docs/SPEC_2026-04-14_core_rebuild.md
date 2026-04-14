# SPEC: 2026-04-14 Core Rebuild

**작성일**: 2026-04-14
**승인자**: 대표님 (founder@idolab.ai)
**작성자**: 비전
**범위**: 제품 코어 재설계 — 구독 UX / i18n / CallCheck / MessageCheck / PushCheck / PrivacyCheck / 초기설정
**전제**: 100% 온디바이스, 중앙 서버 없음, Play Billing 전용, 운영비 0원
**대상 빌드**: Debug + Release (190개국 배포)

---

## 0. 핵심 원칙 (전 패키지 공통)

### 0.1 금지 표현 (CRITICAL RULE)

돈 기반 피해 축소 표현은 UI/문구/로그/마케팅/리포트 어느 곳에도 **절대 사용 금지**.

- 금지: "XX원 절약했어요", "XX만원 피해 예방", "월 XX원 가치", "연간 XX원 효과", 금액 단위 숫자
- 이유: 실측 불가. 추정 기반 수치는 사기·허위광고 소지. 대표님 원칙 위배.

### 0.2 허용 지표 (유일 표현)

다음 4종 지표만 사용 가능:

1. **차단된 의심 통화 수** — 사용자가 명시적으로 거절/차단한 카운트
2. **반복 메시지 수** — 동일 송신자·유사 본문 반복 카운트
3. **위험 링크 메시지 수** — LinkSafetyScorer 임계값 초과 링크 포함 메시지 카운트
4. **알림 송신자 통계** — 앱별·시간대별 알림 발생량 (사용자 판단 재료용)

### 0.3 빅테크 방식 준수

- 랜덤/임시/땜방/우회/부분/강제 우회 금지
- SDK·플랫폼 공식 API 사용
- A/B 테스트 또는 feature flag 도입 시 `BuildConfig` 또는 Gradle product flavor 사용
- 실험 서버 없음 — 모든 판단 로직은 온디바이스 결정

### 0.4 저장번호(Contacts) 완전 제외 원칙

- 연락처에 저장된 번호는 CallCheck·MessageCheck·PushCheck 전 구간 **자동 화이트리스트**
- 오버레이·알림·대시보드 집계 전부 제외
- 판단 근거: 저장번호 = 사용자 신뢰 선언. 추가 판단 불필요.

---

## 1. 패키지 개요 및 구현 순서

| 순서 | 패키지                        | 난이도 | 종속성       | 예상 작업량 |
| ---- | ----------------------------- | ------ | ------------ | ----------- |
| P1   | 구독 UX 재설계                | ★★★    | Play Billing | 2~3일       |
| P2   | i18n 전면 감사                | ★★★★   | 전 모듈      | 4~5일       |
| P3   | 저장번호 필터링 (공통 인프라) | ★★     | P5, P6 선행  | 1일         |
| P4   | MessageCheck 축소             | ★★     | P3           | 1~2일       |
| P5   | CallCheck 코어                | ★★★★★  | P3, P2       | 5~7일       |
| P6   | PushCheck 통계 엔진           | ★★★★   | P3           | 3~4일       |
| P7   | PrivacyCheck 강화             | ★★★    | —            | 2~3일       |

**구현 순서 선정 근거**: 돈·법적 리스크가 큰 P1 → 190개국 i18n 감사 P2 (전 후속 패키지 번역 키 필요) → 공통 인프라 P3 → UI P4 → 코어 CallCheck P5 → 통계 P6 → 보안 UX P7.

---

## 2. P1. 구독 UX 재설계

### 2.1 목표

- 기존 "Premium" 브랜드 제거, 무료/유료 구분 표현 제거
- 체험(trial) 종료 후 자동 구독 전환, 동일 기기 재구독 시 무료 체험 불가
- 취소(언제든지 해지) 경로를 물리적으로 가장 크고 눈에 띄게
- 국가 티어 UI 제거

### 2.2 변경사항

| 항목                    | 현재 상태                    | 목표 상태                                                                      |
| ----------------------- | ---------------------------- | ------------------------------------------------------------------------------ |
| 화면 제목               | "Premium" / "프리미엄"       | "구독" (단일, 모든 언어)                                                       |
| 무료/유료 분기          | 무료 플랜 vs 프리미엄 선택   | 분기 제거. 체험 → 유료 단일 경로                                               |
| 체험 후 전환            | 명시 필요 UI 있음            | 자동 전환. 체험 시작 시 "체험 종료 시 자동 결제" 고지 1회                      |
| 재구독 체험             | 가능                         | 동일 기기 재구독 시 체험 제공 안 함 (Play Billing SKU에 `freeTrialPeriod` 생략 + 로컬 onboarding flag) |
| 취소 버튼               | 일반 링크/텍스트             | **RED 색 / 최대 크기 / 하단 고정 / 원클릭**                                    |
| 국가 티어 UI            | Tier A/B/C 별 가격 표기      | 삭제. Play Billing이 국가별 가격 자동 처리                                     |

### 2.3 영향 파일

- `feature/billing/src/main/kotlin/.../SubscriptionScreen.kt` — 타이틀·무료 분기 제거
- `feature/billing/src/main/kotlin/.../BillingViewModel.kt` — `queryPurchasesAsync` 기반 재구독 체험 차단
- `feature/billing/src/main/kotlin/.../PlayBillingRepository.kt` — SKU 쿼리, 오퍼 필터링 (freeTrial 오퍼 제외 분기)
- `feature/country-config/...` — Tier 관련 Composable·데이터 **제거 대상 검토** (대표님 명시 제거)
- `app/src/main/res/values*/strings.xml` (190개국) — `premium_*` 키 전면 `subscribe_*`로 교체
- `feature/billing/src/main/res/layout/` — 취소 버튼 스타일 (if any, Compose 기준 `Modifier.fillMaxWidth().height(72.dp).background(Color.Red)`)

### 2.4 기술 세부

**재구독 체험 차단 로직**:
```kotlin
// BillingViewModel
val purchaseHistory = billingClient.queryPurchaseHistoryAsync(
    QueryPurchaseHistoryParams.newBuilder()
        .setProductType(BillingClient.ProductType.SUBS)
        .build()
)
val hasEverSubscribed = purchaseHistory.purchaseHistoryRecordList
    ?.any { it.products.contains(SUBSCRIPTION_SKU) } ?: false

val offerToken = if (hasEverSubscribed) {
    productDetails.subscriptionOfferDetails
        ?.firstOrNull { it.pricingPhases.pricingPhaseList.all { phase -> phase.priceAmountMicros > 0 } }
        ?.offerToken
} else {
    productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
}
```

**취소 버튼 Deep Link**:
```kotlin
val managePackageUri = "https://play.google.com/store/account/subscriptions?sku=$SKU&package=$packageName"
context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(managePackageUri)))
```

### 2.5 검증 기준

- [ ] SubscriptionScreen 타이틀 "구독"만 표시
- [ ] 체험 완료 내역 있는 기기에서 "무료 체험" 문구·오퍼 **절대** 노출 안 됨
- [ ] 취소 버튼이 화면 하단 고정, 빨간색, 높이 72dp 이상, 탭 한 번으로 Play Store 구독 관리 진입
- [ ] 국가별 가격은 `ProductDetails.subscriptionOfferDetails[0].pricingPhases.pricingPhaseList[0].formattedPrice` 그대로 표시 (Tier UI 없음)

### 2.6 난이도 및 리스크

**★★★ (중)**. Play Billing SDK 표준 API만 사용, 이슈 발생 시 Google 공식 문서 경로 존재.

**리스크**: 재구독 체험 차단은 *동일 Google 계정 + 동일 기기*에서만 100% 보장. 계정 전환 시 Play 서버 기록은 남으므로 Play가 자동 차단하나, 완전 무결하진 않음. 추가 보강 시 `InstallerID` 기반 SharedPreferences hash 저장 고려.

---

## 3. P2. i18n 전면 감사 (190개국)

### 3.1 목표

- 모든 UI 문자열 키 기반 (하드코딩 0건)
- 기기 locale 자동 탐지 + 설정 화면 수동 오버라이드
- 190개국 언어 커버리지 (최소 UN 공식 6개 언어 + 앱 진출 대상 주요 언어)

### 3.2 현재 상태

- 기존 7개 언어 지원: en / ko / ja / zh / es / fr / de (가정. 실확인 필요)
- `values-XX/strings.xml` 다수 이미 존재
- CallCheckNavHost·MessageCheck 등 하드코딩 제거 커밋 존재 (`84ba0d1`, `7cd58b1`)

### 3.3 목표 언어 세트 (T1 / T2 / T3)

**T1 (필수, Google Play 상위 20국 + 한국)** — 20개:
en, ko, ja, zh-CN, zh-TW, es, pt-BR, fr, de, it, ru, ar, hi, id, vi, th, tr, nl, pl, sv

**T2 (확장, 진출 후보국)** — 30개:
fil, ms, bn, ta, te, ur, fa, he, el, cs, da, fi, hu, no, ro, sk, uk, bg, hr, sr, sl, lt, lv, et, az, kk, ka, hy, sw, am

**T3 (장기, UN 공식 + 소수 거점)** — 20개:
af, ca, eu, gl, is, mt, mk, sq, bs, mn, my, km, lo, ne, si, gu, kn, ml, mr, pa

**합계 70개 언어 커버**. 190개국은 locale fallback 규칙으로 공식 7개 언어 중 하나로 자동 매핑.

### 3.4 구현 전략

**Phase A** — 감사:
1. 전 모듈 `*.kt` 파일에서 하드코딩 한글·영문 리터럴 grep
2. `strings.xml` 키 충돌·누락 감사 스크립트 작성
3. `values-default` 기준 키 수 == 모든 `values-XX` 키 수 보증

**Phase B** — 수동 오버라이드 UI:
1. Settings → "언어" 카드 신규
2. `AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("ko"))` 사용 (AndroidX 표준)
3. 기본값: 기기 locale 자동 반영 (빈 LocaleList)

**Phase C** — 번역 공급:
1. 표준 XLIFF 2.0 export 스크립트
2. 전문 번역 서비스 납품 (Gengo / Crowdin / DeepL Pro API)
3. 역번역 검수 (영문 ↔ 각 언어 샘플 10% 검수)

### 3.5 영향 파일

- 전 모듈 `src/main/res/values/strings.xml` (현재 기준)
- `app/src/main/res/values-XX/strings.xml` (70개 신규/업데이트)
- `feature/settings/...SettingsScreen.kt` — 언어 선택 카드 추가
- `app/src/main/AndroidManifest.xml` — `android:configChanges="locale|layoutDirection"` 확인
- `app/build.gradle.kts` — `resConfigs("en", "ko", "ja", ...)` 허용 언어 리스트

### 3.6 검증 기준

- [ ] `grep -rn "strings\.xml"` 대신 전 모듈 하드코딩 리터럴 0건
- [ ] 70개 언어 각각 `strings.xml`의 키 수가 base `values/strings.xml`과 일치
- [ ] 기기 언어 아랍어(ar)·히브리어(he)로 설정 시 RTL 레이아웃 정상
- [ ] 수동 오버라이드 → 앱 재시작 → 선택 언어 유지

### 3.7 난이도

**★★★★ (고)**. 언어 수가 많고, 전문 번역·감수 필요. 기술적으론 표준 Android i18n. 번역 품질이 주 리스크.

---

## 4. P3. 저장번호 필터링 공통 인프라

### 4.1 목표

- `data/contacts` 모듈에 단일 API `isContactSaved(phoneNumber: String): Boolean`
- CallCheck·MessageCheck·PushCheck가 **단일 호출점**으로 참조
- 화이트리스트 매칭 시 이후 파이프라인 완전 스킵 (오버레이·알림·집계 전부)

### 4.2 변경사항

| 영역                   | 현재                   | 목표                                                               |
| ---------------------- | ---------------------- | ------------------------------------------------------------------ |
| 필터 위치              | 여러 곳 중복 (가능성)  | `ContactsRepository.isContactSaved()` 단일 호출                    |
| 번호 정규화            | 모듈별 상이            | `PhoneNumberUtil.normalize(E.164)` 공통 유틸                       |
| 호출 타이밍            | 결정 엔진 내부         | 파이프라인 진입 즉시 (pre-filter)                                  |
| 캐싱                   | 없음 추정              | `LruCache<String, Boolean>(512)` + ContentObserver 무효화          |

### 4.3 영향 파일

- `data/contacts/src/main/kotlin/.../ContactsRepository.kt` — `isContactSaved()` public API 추가
- `data/contacts/src/main/kotlin/.../PhoneNumberNormalizer.kt` (신규) — libphonenumber 래퍼
- `feature/call-intercept/...CallActionReceiver.kt` — 진입부 pre-filter
- `feature/message-intercept/...SmsInterceptReceiver.kt` — 진입부 pre-filter
- `feature/push-intercept/...` — sender 추출 후 pre-filter

### 4.4 기술 세부

**libphonenumber 사용** (Google 공식):
```kotlin
// build.gradle.kts
implementation("com.googlecode.libphonenumber:libphonenumber:8.13.55")

// PhoneNumberNormalizer
fun normalize(raw: String, defaultRegion: String): String? = try {
    val phoneUtil = PhoneNumberUtil.getInstance()
    val parsed = phoneUtil.parse(raw, defaultRegion)
    phoneUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164)
} catch (e: NumberParseException) { null }
```

**ContactsRepository**:
```kotlin
@Singleton
class ContactsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val normalizer: PhoneNumberNormalizer,
) {
    private val cache = LruCache<String, Boolean>(512)

    suspend fun isContactSaved(rawNumber: String, defaultRegion: String): Boolean {
        val e164 = normalizer.normalize(rawNumber, defaultRegion) ?: rawNumber
        cache.get(e164)?.let { return it }

        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(e164)
        )
        val exists = context.contentResolver.query(uri, arrayOf(ContactsContract.PhoneLookup._ID), null, null, null)
            ?.use { it.moveToFirst() } ?: false
        cache.put(e164, exists)
        return exists
    }
}
```

### 4.5 검증 기준

- [ ] 연락처 저장 번호 호출 수신 시 오버레이·알림·DB 저장 모두 스킵
- [ ] 미저장 번호만 MessageHub·CallHub·PushStats DB 적재
- [ ] 연락처 추가 후 5분 내 해당 번호 화이트리스트 반영 (ContentObserver)

### 4.6 난이도

**★★ (저)**. Google 공식 라이브러리 사용, 표준 ContactsContract API.

---

## 5. P4. MessageCheck 축소 (UI 단순화)

### 5.1 목표

- 메시지 본문 전체 표시 금지, 최대 5줄 샘플 + 말줄임
- 각 메시지별 액션 3종: **삭제** / **차단** / **삭제+차단 둘 다**
- 링크 포함 메시지는 Google 검색 바로가기 제공 (온디바이스 크롬 인텐트)
- 저장번호 제외 (P3 의존)

### 5.2 변경사항

| 항목                   | 현재                   | 목표                                                 |
| ---------------------- | ---------------------- | ---------------------------------------------------- |
| 본문 표시              | 전체                   | 최대 5줄, 넘치면 말줄임                              |
| 액션 버튼              | 확인만                 | 삭제 / 차단 / 삭제+차단                              |
| 링크 처리              | 단순 표시              | 링크별 Google 검색 인텐트 + LinkSafetyScorer 점수    |
| 저장번호 필터          | 없음                   | P3 pre-filter 적용                                   |

### 5.3 영향 파일

- `feature/message-intercept/src/main/kotlin/.../MessageHubScreen.kt` — 리스트 아이템 Composable 개편
- `feature/message-intercept/src/main/kotlin/.../MessageDetailScreen.kt` — 상세 뷰 액션 3종
- `data/local-cache/...MessageHubDao.kt` — `deleteById()` / `markBlocked()` / `deleteAndBlock()` 메서드 추가
- `feature/message-intercept/src/main/kotlin/.../MessageHubViewModel.kt` — 액션 상태머신

### 5.4 기술 세부

**삭제**: `ContentResolver.delete(Telephony.Sms.CONTENT_URI, "_id=?", arrayOf(smsId))` — `SEND_RESPOND_VIA_MESSAGE` 권한 + 기본 SMS 앱 지정 필요. **대안**: 앱 내부 MessageHubEntity만 삭제 (시스템 SMS는 보존). 빅테크 표준은 후자. 시스템 SMS 삭제는 사용자 수동.

**차단**: 송신자 번호를 `blocked_senders` 테이블에 삽입. 향후 수신 시 `MessageHubDao.isBlockedSender()` 검사로 스킵.

**삭제+차단**: 트랜잭션 내 두 작업 원자적 실행.

**링크 Google 검색**:
```kotlin
val query = Uri.encode(urlOrHost)
Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$query"))
```

### 5.5 검증 기준

- [ ] 긴 메시지(10줄 이상) → 5줄 + "..." 표시
- [ ] 삭제 버튼 탭 → 앱 DB에서 제거, 시스템 SMS는 유지
- [ ] 차단 버튼 탭 → 이후 동일 송신자 SMS 수신 시 `MessageHubDao.insert()` 스킵 로그 확인
- [ ] 저장번호로 SMS 수신 → MessageHub에 아예 노출 안 됨

### 5.6 난이도

**★★ (저)**. 표준 CRUD + 기존 ViewModel 확장.

---

## 6. P5. CallCheck 코어 (최대 난이도)

### 6.1 목표

- 저장 안 된 번호로 **수신 통화 진행 중** 오버레이 표시
- 오버레이 내용: 번호 / 태그·메모 / **Google 검색 결과 요약 (CORE)** / 유사번호(prefix 기반)
- 오버레이 액션: **수신** / **거절** / **차단**
- 번호 포맷은 기기 연락처·통화기록 기반 190개국 prefix 자동 추출

### 6.2 변경사항

| 영역                   | 현재                      | 목표                                                        |
| ---------------------- | ------------------------- | ----------------------------------------------------------- |
| 수신 오버레이          | 일부 구현                 | 미저장 번호 전용 + 3종 액션 버튼                            |
| 번호 검색 요약         | 없음/PoC                  | **핵심 기능**: 번호 Google 검색 → 상위 3건 snippet 요약     |
| 유사번호 검색          | 구현됨 (`259c71a`)        | prefix 매칭 정합성 재검증                                   |
| 태그·메모              | 구현됨 (`259c71a`)        | 오버레이·상세 뷰 통합                                       |
| 번호 포맷              | 일부 고정                 | 기기 `ContactsContract` + `CallLog` prefix 동적 추출        |

### 6.3 영향 파일

- `feature/call-intercept/src/main/kotlin/.../MyPhoneCheckScreeningService.kt` — CallScreeningService 유지
- `feature/call-intercept/src/main/kotlin/.../CallerIdOverlayManager.kt` — 3종 버튼 + 요약 영역
- `feature/decision-ui/src/main/kotlin/.../overlay/CallOverlayContent.kt` — 오버레이 UI 3종 액션
- `feature/search-enrichment/src/main/kotlin/.../GoogleSearchSummarizer.kt` (신규) — WebView 백그라운드 / 서버리스 HTML 파싱
- `data/search/src/main/kotlin/.../PhonePrefixExtractor.kt` (신규) — ContactsContract + CallLog 순회 → prefix 세트
- `feature/call-intercept/src/main/kotlin/.../CallActionReceiver.kt` — 수신/거절/차단 액션 핸들러 (기존 존재)

### 6.4 기술 세부

**번호 검색 요약 (CORE 기능)**:

온디바이스 원칙이므로 서버 API 호출 금지. 대안:
- 방법 A: **사용자 동의 기반 WebView** 백그라운드 로드 → HTML 파싱
- 방법 B: 인텐트로 Chrome Custom Tab 오픈 (사용자 직접 확인)

빅테크 방식 선택: **방법 B + 방법 A 병행**.
- 오버레이에는 "Google 검색으로 확인" 버튼 + 로컬에 미리 파싱된 요약 (최근 24시간 캐시)
- WebView는 백그라운드 GONE 상태로 로드, HTML `<div class="VwiC3b">` snippet 추출
- **법적 리스크 주의**: Google 검색 스크래핑은 ToS 위반 소지. 대안으로 사용자가 직접 검색한 결과를 공유받는 경로 또는 공식 Custom Search API (일 100건 무료, 초과 시 유료) 고려.

**결론**: P5 중 "번호 검색 요약"은 별도 법적 검토 후 결정. 초기 릴리즈는 **Chrome Custom Tab 오픈만 제공** (사용자 직접 검색). 이후 공식 Custom Search JSON API 도입을 프리미엄 기능으로 분리.

**prefix 추출**:
```kotlin
class PhonePrefixExtractor @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun extractRegionPrefixes(): Set<String> {
        val prefixes = mutableSetOf<String>()
        // Contacts
        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            null, null, null
        )?.use { c ->
            while (c.moveToNext()) {
                val raw = c.getString(0) ?: continue
                PhoneNumberUtil.getInstance().parse(raw, Locale.getDefault().country)
                    .countryCode.toString().let { prefixes.add(it) }
            }
        }
        // CallLog 동일 패턴
        return prefixes
    }
}
```

### 6.5 검증 기준

- [ ] 저장 안 된 번호로 수신 통화 중 2초 내 오버레이 표시
- [ ] 오버레이 3종 버튼(수신/거절/차단) 각각 동작
- [ ] 차단 시 `blocked_callers` 테이블 삽입 + 이후 동일 번호 자동 거절
- [ ] 저장 번호 수신 시 오버레이 0건 (원칙 0.4)
- [ ] prefix 추출이 기기 locale 기준 최소 1개 국가 코드 반환

### 6.6 난이도

**★★★★★ (최상)**. CallScreeningService 제약 + 오버레이 권한 + 통화 상태 복잡도 + 검색 요약 법적 리스크. 주의 깊은 설계 필요.

---

## 7. P6. PushCheck 통계 엔진

### 7.1 목표

- 알림을 **무시 결정**하는 엔진이 아니라 **통계 수집 엔진**으로 재정의
- 송신자별 일/주/월 발생량 + 광고/할인/정보 분류
- 앱 단위 "이 앱 알림 차단" 버튼 (Android NotificationListenerService 기반)
- 저장번호 연계 (P3)

### 7.2 변경사항

| 영역                   | 현재                        | 목표                                                  |
| ---------------------- | --------------------------- | ----------------------------------------------------- |
| 엔진 역할              | 무시할지 판단               | 발생량·분류 집계 + 사용자 판단 재료 제공              |
| 분류                   | 없음/PoC                    | 광고 / 할인 / 정보 3종 (키워드 + 패턴 기반 온디바이스) |
| 앱 차단                | OS 설정 직진                | 앱 내 카드에서 원클릭 → `NotificationListenerService` 차단 리스트 등록 |
| 통계 화면              | 일부 구현 (`259c71a`)       | 일/주/월 세그먼트 + 앱·송신자 상위 N                  |

### 7.3 영향 파일

- `feature/push-intercept/src/main/kotlin/.../PushListenerService.kt` — 분류 로직 삽입
- `feature/push-intercept/src/main/kotlin/.../PushClassifier.kt` (신규) — 광고/할인/정보 분류
- `data/local-cache/.../PushStatsDao.kt` — 기존 + 세그먼트 집계 쿼리
- `data/local-cache/.../PushStatsEntity.kt` — category 컬럼 추가 (ads/discount/info)
- `feature/push-intercept/.../PushStatsScreen.kt` — 일/주/월 탭 + 차단 버튼

### 7.4 기술 세부

**분류 로직** (온디바이스, 간이 규칙 기반):
```kotlin
enum class PushCategory { ADS, DISCOUNT, INFO, OTHER }

fun classify(title: String, body: String, locale: Locale): PushCategory {
    val text = "$title $body".lowercase(locale)
    val discountPattern = Regex("""\b(\d{1,2}%|할인|sale|off|쿠폰|coupon)\b""")
    val adsPattern = Regex("""\b(광고|ad|sponsored|promo|프로모션)\b""")
    val infoPattern = Regex("""\b(안내|알림|공지|notice|update)\b""")
    return when {
        discountPattern.containsMatchIn(text) -> PushCategory.DISCOUNT
        adsPattern.containsMatchIn(text) -> PushCategory.ADS
        infoPattern.containsMatchIn(text) -> PushCategory.INFO
        else -> PushCategory.OTHER
    }
}
```

패턴은 70개 언어별 키워드 사전 필요 (P2 의존).

**앱 차단**:
```kotlin
// PushListenerService onNotificationPosted
val pkg = sbn.packageName
if (blockedAppsDao.isBlocked(pkg)) {
    cancelNotification(sbn.key)  // NotificationListenerService의 표준 메서드
}
```

### 7.5 검증 기준

- [ ] 알림 수신 시 PushStatsEntity insert + 분류 컬럼 채워짐
- [ ] 통계 화면 "일" 탭에서 오늘 0시~23시59분 집계
- [ ] 앱 차단 버튼 탭 → 동일 앱 다음 알림부터 즉시 제거 (시스템 알림창에서 사라짐)
- [ ] 70개 언어 분류 패턴 최소 키워드 각 언어당 5개 이상

### 7.6 난이도

**★★★★ (고)**. NotificationListenerService 수명주기 + 70개 언어 분류 사전 구축이 주 작업량.

---

## 8. P7. PrivacyCheck 강화

### 8.1 목표

- 카메라·마이크 권한 보유 앱 리스트 신규
- 하루 1회 새로고침, 신규 감지 앱에 **NEW 마커**
- 각 앱별 "권한 해제" 버튼 (시스템 설정 Deep Link)
- 총 개수 + 전일 대비 증감
- 사용량 기준 정렬 (최근 30일 접근 횟수)

### 8.2 변경사항

| 영역              | 현재                   | 목표                                                 |
| ----------------- | ---------------------- | ---------------------------------------------------- |
| 권한별 앱 리스트  | 일부 구현 (`259c71a`)  | 카메라·마이크·위치 권한별 분리 리스트                |
| 갱신 주기         | 앱 진입 시 갱신        | Daily WorkManager + 진입 시 수동 갱신 허용           |
| NEW 마커          | 없음                   | 전일 스냅샷 diff 기준 신규 앱에 뱃지                 |
| 권한 해제 버튼    | 없음 → **필수**        | `ACTION_APPLICATION_DETAILS_SETTINGS` Deep Link      |
| 통계              | 총 개수만              | 총 개수 + 전일 대비 증감 (+N / -N)                   |
| 정렬              | 설치일 또는 알파벳     | `UsageStatsManager` 최근 30일 접근 횟수 내림차순     |

### 8.3 영향 파일

- `feature/privacy-check/src/main/kotlin/.../PermissionGrantedCollector.kt` — 권한별 분리 수집
- `feature/privacy-check/src/main/kotlin/.../PrivacyCheckCollector.kt` — daily 워커 트리거
- `feature/privacy-check/src/main/kotlin/.../PrivacyHistoryScreen.kt` — UI 재설계
- `data/local-cache/.../PrivacyHistoryDao.kt` — 스냅샷 diff 쿼리 추가
- `data/local-cache/.../PrivacyHistoryEntity.kt` — snapshotDate 컬럼 / isNew 플래그
- `app/src/main/kotlin/.../worker/PrivacyDailyWorker.kt` (신규) — WorkManager PeriodicWorkRequest 24h

### 8.4 기술 세부

**일일 갱신**:
```kotlin
val dailyWork = PeriodicWorkRequestBuilder<PrivacyDailyWorker>(24, TimeUnit.HOURS)
    .setConstraints(
        Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
    )
    .build()
WorkManager.getInstance(context).enqueueUniquePeriodicWork(
    "privacy-daily-scan",
    ExistingPeriodicWorkPolicy.KEEP,
    dailyWork
)
```

**NEW 마커 판정**:
```kotlin
suspend fun markNewApps() {
    val today = LocalDate.now().toString()
    val yesterday = LocalDate.now().minusDays(1).toString()
    val todaySet = dao.getPackagesBySnapshot(today).toSet()
    val yesterdaySet = dao.getPackagesBySnapshot(yesterday).toSet()
    val newPackages = todaySet - yesterdaySet
    dao.markAsNew(newPackages.toList(), today)
}
```

**사용량 정렬** (UsageStatsManager 권한 필요):
```kotlin
val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
val end = System.currentTimeMillis()
val start = end - TimeUnit.DAYS.toMillis(30)
val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end)
val countByPackage = stats.groupBy { it.packageName }
    .mapValues { it.value.sumOf { s -> s.totalTimeInForeground } }
```

### 8.5 검증 기준

- [ ] 카메라·마이크·위치 3개 리스트 각각 표시
- [ ] 어제 없던 신규 앱에 "NEW" 뱃지 표시
- [ ] "권한 해제" 버튼 탭 → 해당 앱 설정 화면 진입
- [ ] 전일 대비 증감 (+N / -N) 상단 카드 표시
- [ ] 사용량 많은 앱 상단 정렬

### 8.6 난이도

**★★★ (중)**. `UsageStatsManager`는 특수 권한(`PACKAGE_USAGE_STATS`)이 필요하고 사용자가 설정에서 수동 허용해야 함. 이미 앱에 있는 권한 플로우 재활용 가능.

---

## 9. 전체 구현 타임라인 (제안)

| 주차     | 작업                                               | 산출물                                |
| -------- | -------------------------------------------------- | ------------------------------------- |
| W1       | P1 구독 UX (코드) + P2 Phase A (감사)              | SubscriptionScreen 리팩토, 하드코딩 감사 리포트 |
| W2       | P2 Phase B·C (번역 공급 외주 발주 + 설정 UI)       | 70개 언어 `strings.xml` 초안          |
| W3       | P3 공통 인프라 + P4 MessageCheck 축소              | ContactsRepository API, MessageCheck UI |
| W4       | P5 CallCheck 코어 (오버레이·액션·prefix)          | CallOverlay 3종 버튼 완성             |
| W5       | P5 CallCheck 검색 요약 (법적 검토 + Custom Tab 도입) | Chrome Custom Tab 경로 + 1차 릴리즈 가능 상태 |
| W6       | P6 PushCheck 통계 엔진                             | 일/주/월 차트 + 분류 사전             |
| W7       | P7 PrivacyCheck 강화                               | NEW 마커 + 권한 해제 버튼             |
| W8       | 통합 QA + 번역 역검수 + Play Console 내부 테스트     | 내부 테스트 트랙 배포                 |

**합계 8주**. 190개국 번역 외주 리드타임에 따라 W2 확장 가능.

---

## 10. 리스크 및 결정 필요 사항

### 10.1 기술 리스크

| 항목                      | 영향      | 대응                                               |
| ------------------------- | --------- | -------------------------------------------------- |
| Google 검색 스크래핑 ToS  | 법적      | Custom Tab 전환 + 공식 Custom Search API 유료 도입 검토 |
| CallScreeningService 제한 | 기능      | Android 10+ 디바이스 한정, fallback InCallService  |
| 70개 언어 번역 품질       | UX        | 역번역 검수 10%, 사용자 신고 채널                  |
| Play Billing 재구독 체험  | 매출      | Google 계정 전환 우회 가능성 (완화: 로컬 hash)     |

### 10.2 대표님 결정 필요

1. **검색 요약 전략**: Custom Tab 전용 vs Custom Search API 유료 도입
2. **번역 외주 예산**: Gengo / Crowdin / DeepL Pro 중 선택
3. **P1 재구독 체험 차단**: 100% 보장 불가 허용 여부
4. **P6 분류 카테고리**: 광고/할인/정보 3종 고정 vs 추후 "뉴스" 추가 여지
5. **릴리즈 전략**: 패키지별 순차 릴리즈 vs 일괄 릴리즈 (W8 최종 1회)

---

## 11. 변경 이력

| 일자       | 변경       | 작성자 |
| ---------- | ---------- | ------ |
| 2026-04-14 | 초안 작성  | 비전   |
