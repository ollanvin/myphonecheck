# 29. SIM-Oriented Single Core — SIM 기준 단일 코어

> **신규 정책 (v2.0.0)**: 모든 국가·지역별 차별화는 SIM 카드를 단일 진실원으로 한다.
> 헌법 제8조 정식 명문화. 기준 헌법: `docs/00_governance/architecture/v2.0.0/05_constitution.md`

---

## 29-1. 정의

**SimContext**는 디바이스 SIM 카드 정보 기반 단일 컨텍스트. 모든 Surface가 본 컨텍스트로 동작.

```kotlin
data class SimContext(
    val mcc: String,                    // Mobile Country Code (예: "450")
    val mnc: String,                    // Mobile Network Code (예: "08")
    val countryIso: String,             // ISO 3166-1 alpha-2 (예: "KR")
    val operatorName: String,           // 통신사 이름 (예: "SK Telecom")
    val currency: java.util.Currency,   // ISO 4217 (countryIso → 매핑)
    val phoneRegion: String,            // libphonenumber Region (countryIso 그대로)
    val timezone: java.util.TimeZone    // SIM 기반 추론 또는 디바이스 timezone
)
```

## 29-2. 적용 영역

| Surface | SimContext 활용 |
|---|---|
| CallCheck | `phoneRegion` → libphonenumber 정규화 / 발신자 식별 |
| MessageCheck | `countryIso` → 국가별 SMS 패턴 (코어 내부) |
| MicCheck/CameraCheck | (앱 인벤토리 중심, 직접 활용 적음) |
| PushCheck | 알림 source 정규화 컨텍스트 |
| CardCheck | `currency` → 결제 통화 default 추론 (멀티 통화 지원 유지) |

## 29-3. UI 언어 선택 — 3단 fallback

**UI 언어만** 사용자 선택 가능. 다른 영역은 SIM 그대로.

```
[1순위 default] SIM 기반 언어
   ↓ 사용자 변경
[2순위] 디바이스 시스템 언어
   ↓ 사용자 변경
[3순위] English (만국 공통)
```

**시나리오**:

| 사용자 | SIM | 디바이스 시스템 | 선택 | 결과 UI |
|---|---|---|---|---|
| 한국인 한국 거주 | KR | ko | 1순위 (default) | 한국어 |
| 한국인 미국 출장 (US SIM) | US | ko | 2순위 (사용자 선택) | 한국어 |
| 다국적 사용자 | DE | en | 3순위 (사용자 선택) | English |
| 일본인 일본 거주 | JP | ja | 1순위 (default) | 일본어 |
| 한국인 다국적 사업자 (US SIM) | US | en | 3순위 (선택) | English |

**구현**:
- `:core:global-engine/sim-context/UiLanguageResolver.kt`
- 사용자 설정 화면: 3단 라디오 (SIM / 디바이스 시스템 / English)
- Android 시스템 `Locale.getDefault()` 직접 사용 금지 (UI 언어 resolve 영역 외)
- 사용자 선택 결과는 디바이스 로컬 DataStore에 저장 (헌법 1조)

## 29-4. SIM 변경 추적 (해외 여행·SIM 교체·eSIM 대응)

**감지 트리거**:
- 부팅 시 SIM 정보 비교 (이전 vs 현재)
- TelephonyManager 콜백 (런타임 SIM 변경, eSIM 전환)

**처리 흐름**:

```
[SIM 변경 감지]
   ↓
[사용자 알림: "새 SIM 발견 (US → KR)"]
   ↓
[3가지 옵션 사용자 선택]
   ├─ A: 새 SIM 컨텍스트 적용 + 베이스데이터 재계산
   ├─ B: 기존 SIM 컨텍스트 유지 (해외 임시 SIM, 단기 여행)
   └─ C: 베이스데이터 초기화 + 새 SIM Initial Scan (완전 새 출발)
```

옵션 A·B·C 모두 사용자 명시 동의 필요. 자동 적용 없음 (헌법 3조).

## 29-5. SIM 부재 처리 (WiFi-only 태블릿 등)

WiFi-only 태블릿 또는 SIM 미장착:
- `countryIso` fallback: 디바이스 시스템 Locale country
- 사용자에게 명시: "SIM 미감지. 디바이스 설정 사용."
- 재SIM 장착 시 컨텍스트 재계산 동의 요청

**fallback 사용 시 사용자에게 명시**:
- Settings 화면 상단 배너: "SIM 부재 — 디바이스 설정 기준 운영 중"
- 정확도 한계 공지 (헌법 5조 정직성)

## 29-6. 헌법 정합

| 조 | 정합 사유 |
|---|---|
| 1 Out-Bound Zero | SIM 정보 외부 전송 0, 사용자 선택도 디바이스 로컬 |
| 2 In-Bound Zero | SimContext는 추출·캐싱, 원본 시스템 자원 읽기만 |
| 3 결정권 중앙집중 금지 | 디바이스 로컬 SimContext, 외부 결정 없음. v2.0.0 강화: 코어 엔진은 본 조 비대상 |
| 4 자가 작동 | 네트워크 단절 시에도 SimContext 작동 |
| 5 정직성 | SIM 결과 그대로, 추정·예측 없음. SIM 부재 시 fallback 명시 |
| 6 가격 정직성 | (UI 영역 외) — currency는 SIM 그대로 |
| 7 Device-Oriented Goose | 모든 처리 온디바이스 |
| **8 SIM-Oriented Single Core** | **본 §의 본질** |

## 29-7. 모듈 매핑

```
:core:global-engine/
└── sim-context/
    ├── SimContext.kt              # 데이터 클래스
    ├── SimContextProvider.kt      # @Singleton, TelephonyManager 의존
    ├── UiLanguageResolver.kt      # 3단 fallback resolver
    ├── SimChangeDetector.kt       # 부팅·런타임 변경 감지
    └── CountryCurrencyMapper.kt   # countryIso → ISO 4217 매핑
```

`:core:global-engine`은 §30 본문 참조.

## 29-8. 사용자 대면 약속

> **"당신의 SIM 카드가 기준입니다.**
> **국가·통화·전화번호 양식은 SIM 정보를 그대로 사용합니다.**
> **UI 언어는 SIM·디바이스 시스템·English 중 선택할 수 있습니다."**

## 29-9. v1.x → v2.0.0 변경 정정

v1.7.1 ~ v1.9.0에서 비전이 명문화하지 않은 사항:
- 시스템 Locale 의존 가정 (SIM 기준 명시 부재)
- 국가 결정의 단일 진실원 부재 (Surface별 자체 매핑 허용 해석 가능)
- UI 언어 fallback 정책 부재

v2.0.0에서 모두 정정 (§28 + §29 + §30 + 헌법 §8조 신설).

## 29-10. cross-ref

- §28 Initial Scan (SimContext 확정 우선 단계)
- 헌법 §8조 SIM-Oriented Single Core
- §30 :core:global-engine 모듈 구조
- §95 Six Surfaces Integration (SimContext 활용 다이어그램)
