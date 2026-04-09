# MyPhoneCheck Global Case Coverage Report V1

**작성일**: 2026-03-27
**대상 버전**: MyPhoneCheck 1.0
**범위**: 191개국 동시 출시 기준 전수 검증

---

## 1. 번호 유형별 기대 동작

### 1.1 저장된 연락처 (Saved Contact)

| 시나리오 | Relationship Score | Risk Score | Category | RiskLevel | Action |
|---|---|---|---|---|---|
| 저장 연락처, 위험 신호 없음 | 1.0 | 0.0 | KNOWN_CONTACT | LOW | SAFE_LIKELY |
| 저장 연락처, 스팸 신호 있음 | 1.0 | 0.025 (0.25×0.1) | KNOWN_CONTACT | LOW | SAFE_LIKELY |
| 저장 연락처, **스캠 신호 있음** | 1.0 | 0.04 (0.40×0.1) | **SCAM_RISK_HIGH** | **LOW** | **CAUTION** |

**설계 근거 (V1.1 수정)**: 저장 연락처는 기본적으로 최우선 category. 단, **스푸핑 방어**: 검색에서 강한 스캠 신호(hasScamSignal)가 감지된 경우 KNOWN_CONTACT가 아닌 SCAM_RISK_HIGH로 분류. 번호 스푸핑 시나리오(연락처 "엄마"지만 실제 사기범)에서 사용자를 보호. risk score는 0.1배 감쇄 유지되므로 RiskLevel은 LOW지만, category가 SCAM_RISK_HIGH이므로 ActionMapper에서 CAUTION 이상을 반환.

### 1.2 미저장 번호 — 통화 기록 있음

| 시나리오 | Relationship Score | Risk Score | Category | RiskLevel |
|---|---|---|---|---|
| 발신 3회 이상 + 장통화 | 0.55~0.70 | 0.0 | BUSINESS_LIKELY | LOW |
| 수신만 1회, 통화 연결됨 | 0.10 | 0.0 | INSUFFICIENT_EVIDENCE | UNKNOWN |
| 수신만, 거절 2회 이상 | 0.0 | 0.10 | INSUFFICIENT_EVIDENCE→SALES_SPAM | UNKNOWN→LOW |
| 짧은 통화 3회+, 장통화 없음 | 0.10~0.25 | 0.10 | (search에 따라) | LOW~MEDIUM |

### 1.3 미저장 번호 — 통화 기록 없음 (첫 수신)

| 시나리오 | Relationship Score | Risk Score | Category | RiskLevel |
|---|---|---|---|---|
| 검색 결과 없음 | 0.0 | 0.0 | INSUFFICIENT_EVIDENCE | UNKNOWN |
| 검색: 배달 키워드 | 0.0 | 0.0 | DELIVERY_LIKELY | LOW |
| 검색: 스팸 신호 | 0.0 | 0.25 | SALES_SPAM_SUSPECTED | MEDIUM |
| 검색: 스캠 신호 | 0.0 | 0.40 | SCAM_RISK_HIGH | HIGH |
| 검색: 기관 키워드 | 0.0 | 0.0 | INSTITUTION_LIKELY | LOW |

**핵심 변경 (V1)**: 기존에는 "첫 수신 + 기록 없음 → +0.15 risk"가 적용되어 MEDIUM(주의)으로 올라갈 수 있었음. **Unknown ≠ Danger 원칙**에 따라 제거. 이제 검색 증거가 없으면 risk=0.0 → UNKNOWN.

### 1.4 비공개/차단 번호 (Private/Blocked)

| 시나리오 | 처리 |
|---|---|
| "private", "blocked", "unknown" 포함 | ALLOW without assessment |
| *67 | ALLOW without assessment |

**근거**: 비공개 번호는 정규화 불가. 판정 시도 자체가 무의미. 즉시 ALLOW.

### 1.5 긴급번호

| 번호 | 국가 | 처리 |
|---|---|---|
| 911 | US/CA | 즉시 ALLOW |
| 112 | EU/글로벌 | 즉시 ALLOW |
| 119 | KR(소방) | 즉시 ALLOW |
| 110 | KR(경찰)/JP(경찰) | 즉시 ALLOW |
| 999 | UK/MY/SG | 즉시 ALLOW |

### 1.6 짧은 번호 (Short Codes)

| 유형 | 예시 | 처리 |
|---|---|---|
| SMS 단축번호 (4-6자리) | 1234, 15880 | PhoneNumberNormalizer 통과 시 정상 판정 |
| 서비스 번호 | 114, 1588-xxxx | libphonenumber valid → 정상 판정 |
| 정규화 실패 | 너무 짧은 번호 | normalize()=null → rawNumber 그대로 판정 |

### 1.7 국제 전화

| 시나리오 | 처리 |
|---|---|
| +국가코드 포함 (E.164) | PhoneNumberNormalizer가 국가 자동 감지 |
| 국제 접두사 없는 외국 번호 | deviceCountry로 파싱 시도 → 실패 시 null → rawNumber 사용 |

---

## 2. 수정 내역

### 2.1 PhoneNumberNormalizer (core/util)

**이전**: 모든 함수의 defaultCountry = "US" 하드코딩
**이후**: defaultCountry = "ZZ" (unknown region)

| 변경 | 이유 |
|---|---|
| default "US" → "ZZ" | 한국 SIM에서 "02-555-0199"를 US 번호로 파싱하는 오류 방지 |
| extractAreaCode: 길이 3 하드코딩 → `getLengthOfGeographicalAreaCode()` | KR(02→길이1~2), JP(03→길이1), US(212→길이3) 등 국가별 상이 |
| `extractAreaCodeFromParsed()` 파라미터에서 countryCode 제거 | libphonenumber가 parsedNumber에서 자동 판별 |

### 2.2 MyPhoneCheckScreeningService (feature/call-intercept)

**이전**: `formatE164(rawNumber)` — defaultCountry "US" 사용
**이후**: `formatE164(rawNumber, deviceCountry)` — 디바이스 국가 전달

| 변경 | 이유 |
|---|---|
| CountryConfigProvider 주입 추가 | 디바이스 국가 감지 필요 |
| `deviceCountry` 필드 추가 | onCreate에서 한 번 감지, 이후 재사용 |
| 모든 formatE164 호출에 deviceCountry 전달 | 로컬 번호 정규화 정확도 보장 |
| build.gradle.kts에 `:feature:country-config` 의존성 추가 | CountryConfigProvider import 필요 |

### 2.3 DecisionEngineImpl (feature/decision-engine)

**이전**: zero history → +0.15, null device → +0.15
**이후**: 정보 부재에 대한 리스크 가산 완전 제거

| 변경 | 이유 |
|---|---|
| `!device.isSavedContact && !device.hasAnyHistory → +0.15` 제거 | Unknown ≠ Danger 원칙 위반 |
| `device == null → +0.15` 제거 | 디바이스 증거 없음 ≠ 위험 |

**결과**: 미저장 + 기록 없음 + 검색 결과 없음 → risk=0.0 → UNKNOWN (기존: risk=0.15 → MEDIUM 가능)

### 2.4 CountryConfigProviderImpl (feature/country-config)

**이전**: 4개국(KR/US/JP/CN)만 지원, 미지원 국가 → US config 반환
**이후**: 동적 fallback config 생성

| 변경 | 이유 |
|---|---|
| `createDefaultConfig()` → `createFallbackConfig(countryCode)` | 미지원 국가에 US config 강제 적용 금지 |
| libphonenumber 기반 phonePrefix 자동 추출 | 191개국 전화 접두사 자동 대응 |
| Google + Truecaller 기본 검색 프로바이더 | 글로벌 접근 가능 서비스 |
| 영어 키워드 사전 기본 제공 | 전 세계 공통 영어 키워드로 기본 커버 |

---

## 3. 191개국 대응 전략

### 3.1 국가 감지 우선순위

```
SIM 국가 → Network 국가 → System Locale → "ZZ" (fallback)
```

**ZZ fallback 보완 (V1.1 추가)**:
"ZZ" 상태에서도 번호가 `+`로 시작하면 libphonenumber `inferCountryFromNumber()`로 국가 추정.

```
ZZ + "+82-2-555-0199" → inferCountryFromNumber() → "KR" → 정상 정규화
ZZ + "02-555-0199" → + 없음 → 추정 불가 → rawNumber 그대로 → UNKNOWN
ZZ + "+1-212-555-0199" → inferCountryFromNumber() → "US" → 정상 정규화
```

이 보완으로 SIM 없는 기기(VoIP, 해외 로밍), Wi-Fi 전용 태블릿에서도 국제 포맷 번호는 정상 처리.

### 3.2 키워드 사전 커버리지

| 티어 | 국가 | 사전 언어 | 검색 프로바이더 |
|---|---|---|---|
| Tier 1 | KR | 한국어 | Naver, Nate, Daum |
| Tier 1 | US | 영어 | Google, Truecaller, Whitepages |
| Tier 1 | JP | 일본어 | Yahoo, Google, LINE |
| Tier 1 | CN | 중국어 | Baidu, QQ, Sina |
| Tier 2 | 기타 187개국 | 영어 | Google, Truecaller |

### 3.3 V2 확장 계획

- ES (스페인어): 멕시코, 스페인, 콜롬비아, 아르헨티나 등 20개국
- FR (프랑스어): 프랑스, 캐나다(퀘벡), 아프리카 프랑코폰
- DE (독일어): 독일, 오스트리아, 스위스
- PT (포르투갈어): 브라질, 포르투갈
- AR (아랍어): 사우디, UAE, 이집트 등
- HI (힌디어): 인도

---

## 4. 엣지 케이스 처리 매트릭스

| 케이스 | 정규화 | 판정 | UI 표시 |
|---|---|---|---|
| "+82-2-555-0199" (한국 유선) | ✅ E.164 성공 | 정상 판정 | Ring + 결과 |
| "02-555-0199" (deviceCountry=KR) | ✅ E.164 성공 | 정상 판정 | Ring + 결과 |
| "02-555-0199" (deviceCountry=US) | ❌ US 번호로 파싱 시도 → 실패 또는 오파싱 | rawNumber 사용 | Ring + UNKNOWN |
| "02-555-0199" (deviceCountry=ZZ) | ❌ 파싱 불가 | rawNumber 사용 | Ring + UNKNOWN |
| "+1-212-555-0199" (US 번호, deviceCountry=KR) | ✅ +로 시작 → 국제 포맷 자동 감지 | 정상 판정 | Ring + 결과 |
| "private" | SKIP | ALLOW without assessment | **알림 없음, 오버레이 없음** (완전 무개입) |
| "911" | SKIP | 즉시 ALLOW | **알림 없음, 오버레이 없음** (완전 무개입) |
| "1588-1234" (KR 서비스) | deviceCountry=KR → ✅ | 정상 판정 | Ring + 결과 |
| "" (빈 문자열) | SKIP | ALLOW without assessment | 알림 없음 |
| null | SKIP | ALLOW without assessment | 알림 없음 |

---

## 5. Confidence 체계 (정보 부재 반영)

Unknown ≠ Danger 원칙에 따라, 정보 부재는 risk 가산이 아닌 confidence 저하로 처리:

| 증거 조합 | Base Confidence |
|---|---|
| Device + Search 모두 있음 | 0.85 |
| Device만 있음 | 0.70 |
| Search만 있음 | 0.60 |
| 둘 다 없음 | 0.30 |

| Category | Confidence Floor/Ceiling |
|---|---|
| KNOWN_CONTACT | floor 0.95 |
| SCAM_RISK_HIGH (scam signal 있음) | floor 0.80 |
| INSUFFICIENT_EVIDENCE | ceiling 0.40 |

---

## 6. 검증 체크리스트

- [x] PhoneNumberNormalizer: "US" 하드코딩 제거 → "ZZ" 기본값
- [x] PhoneNumberNormalizer: area code 길이 3 하드코딩 → getLengthOfGeographicalAreaCode()
- [x] PhoneNumberNormalizer: inferCountryFromNumber() 추가 (ZZ fallback 보완)
- [x] PhoneNumberNormalizer: getPhonePrefix() 추가 (국가→접두사 변환)
- [x] MyPhoneCheckScreeningService: CountryConfigProvider 주입 + deviceCountry 감지
- [x] MyPhoneCheckScreeningService: formatE164()에 deviceCountry 전달
- [x] MyPhoneCheckScreeningService: ZZ fallback — 번호 prefix 기반 국가 추정 로직 추가
- [x] MyPhoneCheckScreeningService: private/emergency UI 정책 명확화 (완전 무개입 문서화)
- [x] DecisionEngineImpl: zero history +0.15 제거
- [x] DecisionEngineImpl: null device +0.15 제거
- [x] DecisionEngineImpl: KNOWN_CONTACT + hasScamSignal → SCAM_RISK_HIGH 우선 (스푸핑 방어)
- [x] CountryConfigProvider: 동적 fallback config 생성
- [x] CountryConfigProvider: libphonenumber 기반 phonePrefix 자동 추출
- [x] build.gradle.kts: call-intercept에 country-config 의존성 추가
- [x] RingState: fromAction(ActionRecommendation) 추가 (접점 통일)
- [x] RingSystem: action 기반 emoji/labelKo/labelEn 오버로드 추가
- [x] 전 UI 접점 action 기반 통일 (Ring/Overlay/Widget/Notification)
- [x] WidgetDecisionItem: action 필드 추가
- [x] QA 전수 매트릭스: 10/10 PASS (접점 100% 일관성)
- [x] 빌드 검증 (compileDebugKotlin 성공, 에러 0개)
