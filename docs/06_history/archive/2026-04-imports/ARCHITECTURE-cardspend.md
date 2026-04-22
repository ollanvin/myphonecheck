# CardSpend Architecture

> 역설계 기준일: 2026-04-21
> 소스: https://github.com/ollanvin/cardspend
> 작성: 비전 (코드베이스 전수 스캔 기반)

---

## 1. 제품 정의

CardSpend는 **신용카드 실적 관리 + 결제 리스크 추적 Android 앱**이다. 카드사 알림(PUSH/SMS/카카오)을 실시간 캡처하여 거래를 파싱하고, 카드별 월 실적 달성률·결제 위험도를 계산하며, 암호화 백업·Google Play 구독·카드 OCR 스캔을 제공한다.

기술 스택:

| 항목 | 스택 |
|------|------|
| 언어 | Kotlin 1.9.22, JVM Target 17 |
| UI | Jetpack Compose (BOM 2024.06.00), Material 3 |
| DI | Hilt 2.50 |
| DB | Room 2.6.1 (스키마 v14) |
| 네비게이션 | Compose Navigation 2.7.6 |
| 백그라운드 | WorkManager 2.9.0 |
| 결제 | Google Play Billing 6.1.0 |
| OCR | CameraX 1.3.1 + ML Kit Korean Text Recognition 16.0.0 |
| 암호화 | AES-256-GCM + PBKDF2-HMAC-SHA256 |
| 빌드 | compileSdk 35, minSdk 26, targetSdk 35 |

---

## 2. 모듈 구조

단일 앱 모듈 구조 (모노리스):

```
cardspend/
└── android/app/
    └── src/main/java/com/idolab/cardspend/
        ├── CardSpendApplication.kt          # @HiltAndroidApp, WorkManager 초기화
        ├── MainActivity.kt                  # 단일 Activity (Compose)
        │
        ├── ui/                              # UI 레이어
        │   ├── Navigation.kt                # NavHost, 14개 라우트
        │   ├── theme/                       # Color.kt, Theme.kt
        │   ├── screen/
        │   │   ├── HomeScreen.kt            # 대시보드 (카드 목록 + 리스크)
        │   │   ├── DetailScreen.kt          # 카드별 거래 내역
        │   │   ├── CardFormScreen.kt        # 카드 추가/수정
        │   │   ├── CardSetupScreen.kt       # 카드 설정 위저드
        │   │   ├── CardManagementScreen.kt  # 카드 관리
        │   │   ├── CardScanScreen.kt        # 카드 OCR 스캔
        │   │   ├── AccountSetupScreen.kt    # 계좌 연결
        │   │   ├── AccountManagementScreen.kt
        │   │   ├── BackupScreen.kt          # 백업/복원
        │   │   ├── ReviewListScreen.kt      # 애매한 거래 검토
        │   │   ├── OnboardingScreen.kt      # 초기 설정
        │   │   ├── SettingsScreen.kt        # 앱 설정
        │   │   └── DevMenuScreen.kt         # (DEBUG only)
        │   └── viewmodel/
        │       ├── HomeViewModel.kt
        │       ├── DetailViewModel.kt
        │       ├── CardSetupViewModel.kt
        │       ├── CardManagementViewModel.kt
        │       ├── CardScanViewModel.kt
        │       ├── AccountSetupViewModel.kt
        │       ├── AccountManagementViewModel.kt
        │       ├── BackupViewModel.kt
        │       ├── ReviewListViewModel.kt
        │       ├── OnboardingViewModel.kt
        │       ├── SettingsViewModel.kt
        │       └── DevMenuViewModel.kt
        │
        ├── notification/                    # 알림 파싱 파이프라인
        │   ├── engine/                      # v2 파싱 엔진
        │   │   ├── ParsingEngine.kt         # 파이프라인 오케스트레이터
        │   │   ├── NotificationNormalizer.kt # Stage 1: 정규화
        │   │   ├── MessageTypeClassifier.kt  # Stage 2: 메시지 분류
        │   │   ├── AmountExtractor.kt        # Stage 3: 금액 추출
        │   │   ├── MerchantExtractor.kt      # Stage 4: 가맹점 추출
        │   │   ├── CardAccountMatcher.kt     # Stage 5: 카드/계좌 매칭
        │   │   ├── ParseResult.kt            # 파싱 결과 계약
        │   │   ├── ParseLog.kt               # 감사 로그 Entity
        │   │   └── GenericFallbackParser.kt  # Stage 7: 폴백
        │   ├── parser/                      # 레거시 파서 + 프로세서
        │   │   ├── NotificationProcessor.kt  # 메시지 타입별 라우터
        │   │   ├── CardPaymentParser.kt      # 레거시 카드 파서
        │   │   ├── BankNotificationParser.kt # 레거시 은행 파서
        │   │   ├── BillingPreviewParser.kt   # 레거시 청구 파서
        │   │   ├── TransactionDeduplicator.kt   # v1 중복 제거
        │   │   └── TransactionDeduplicatorV2.kt # v2 중복 제거
        │   ├── listener/
        │   │   ├── CardNotificationListenerService.kt # NotificationListenerService
        │   │   └── SmsReceiver.kt            # SMS BroadcastReceiver
        │   ├── ReminderWorker.kt             # WorkManager 리마인더
        │   ├── ReminderScheduler.kt          # 스케줄 설정
        │   ├── ReminderNotificationManager.kt # 푸시 발송
        │   └── ReminderDeduplicator.kt       # 리마인더 중복 방지
        │
        ├── domain/                          # 도메인 레이어
        │   ├── usecase/
        │   │   ├── CardRiskCalculator.kt     # 실적 리스크 계산
        │   │   └── PaymentRiskCalculator.kt  # 결제 리스크 계산
        │   └── model/
        │       ├── CardRiskStatus.kt         # RISK / CAUTION / SAFE
        │       ├── PaymentRiskStatus.kt      # PAYMENT_RISK ~ PAYMENT_OK
        │       ├── CardWithProgress.kt       # 카드 + 달성률
        │       ├── CardType.kt               # TARGET / NORMAL
        │       ├── CardStatus.kt             # ACTIVE / INACTIVE / HIDDEN
        │       └── RiskPolicy.kt             # 리스크 정책
        │
        ├── data/                            # 데이터 레이어
        │   ├── db/
        │   │   └── CardSpendDatabase.kt      # Room DB v14
        │   ├── entity/
        │   │   ├── CardEntity.kt             # cards 테이블
        │   │   ├── TransactionEntity.kt      # transactions 테이블
        │   │   ├── AccountEntity.kt          # accounts 테이블
        │   │   ├── ExclusionRuleEntity.kt    # exclusion_rules 테이블
        │   │   └── BackupMetadataEntity.kt   # backup_metadata 테이블
        │   ├── dao/
        │   │   ├── CardDao.kt
        │   │   ├── TransactionDao.kt
        │   │   ├── AccountDao.kt
        │   │   ├── ExclusionRuleDao.kt
        │   │   ├── ParseLogDao.kt
        │   │   └── BackupMetadataDao.kt
        │   ├── repository/
        │   │   └── CardRepository.kt
        │   └── preferences/
        │       ├── ReminderPreferences.kt
        │       ├── SubscriptionPreferences.kt
        │       ├── InitialScanPreferences.kt
        │       ├── BackupPreferences.kt
        │       └── UsageTracker.kt
        │
        ├── backup/                          # 백업/복원 시스템
        │   ├── BackupCore.kt                # DB → JSON → 암호화 → .csb
        │   ├── RestoreCore.kt               # .csb → 복호화 → JSON → DB
        │   ├── BackupFileManager.kt         # 파일 I/O
        │   └── crypto/
        │       ├── AesGcmEncryptor.kt        # AES-256-GCM
        │       ├── BackupPassphraseKdf.kt    # PBKDF2-HMAC-SHA256
        │       └── CsbV2Format.kt            # v2 바이너리 포맷
        │
        ├── billing/                         # 구독 결제
        │   ├── BillingManager.kt            # Google Play Billing
        │   ├── EntitlementManager.kt        # 프리미엄 기능 접근
        │   └── SubscriptionManager.kt       # 구독 상태
        │
        ├── scan/                            # 카드 OCR
        │   └── CardOcrProcessor.kt          # ML Kit 한국어 OCR
        │
        └── di/                              # Hilt 모듈
            └── DatabaseModule.kt            # Room DB + DAO 제공
```

---

## 3. Room 데이터베이스 스키마

버전: 14 (exportSchema: false)

### 3.1 테이블 구조

#### cards (CardEntity)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | Int (PK, auto) | |
| name | String | 카드 별명 |
| issuer | String | 카드사 코드 (SAMSUNG, HYUNDAI, SHINHAN, KB, LOTTE, HANA, WOORI, NH) |
| monthlyTarget | Long | 월 목표 실적 (원) |
| billingStartDay | Int | 실적 시작일 |
| billingEndDay | Int | 실적 종료일 |
| paymentDueDay | Int | 결제일 |
| linkedAccountId | Long? | FK → accounts |
| expectedPayment | Long | 결제 예정 금액 |
| expectedPaymentDate | String? | 결제 예정일 |
| cardType | Enum | TARGET (실적 목표) / NORMAL (결제만) |
| cardStatus | Enum | ACTIVE / INACTIVE / HIDDEN |
| lastFourDigits | String? | 카드 끝 4자리 |
| reminderEnabled | Boolean | 리마인더 활성화 |
| benefitType | Enum? | DISCOUNT / CASHBACK / POINT |
| benefitValue | Double? | 혜택 수치 |
| periodType | Enum | MONTHLY / CUSTOM |
| colorIndex | Int | UI 색상 |
| sortOrder | Int | 정렬 순서 |
| needsSetup | Boolean | 초기 설정 필요 |
| createdAt | Long | 생성 시각 |

#### transactions (TransactionEntity)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | Long (PK, auto) | |
| cardId | Int (FK → cards, CASCADE) | |
| amount | Long | 거래 금액 (원) |
| merchantName | String | 가맹점 |
| transactionTime | Long | 거래 시각 (epoch) |
| type | Enum | APPROVED / CANCELLED |
| installmentMonths | Int | 할부 개월 |
| source | Enum | PUSH / SMS / KAKAO |
| deduplicationKey | String (unique) | "{금액}_{카드ID}_{가맹점}_{시간}" |
| countsTowardTarget | Boolean | 실적 포함 여부 |
| isManuallyClassified | Boolean | 수동 분류 |
| needsReview | Boolean | 검토 필요 |
| matchedTransactionId | Long? | 취소 매칭 ID |
| cancelMatchFailed | Boolean | 취소 매칭 실패 |
| rawNotificationText | String | 원문 (백업 시 scrub) |
| createdAt | Long | 저장 시각 |

인덱스: cardId, transactionTime, deduplicationKey (unique)

#### accounts (AccountEntity)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | Long (PK, auto) | |
| bankName | String | 은행명 |
| accountAlias | String | 별명 |
| nickname | String? | 닉네임 |
| accountNumber | String | 계좌번호 |
| balance | Long | 잔액 |
| lastUpdated | Long | 잔액 갱신 시각 |
| needsSetup | Boolean | 초기 설정 필요 |
| createdAt | Long | |

#### exclusion_rules (ExclusionRuleEntity)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | Long (PK, auto) | |
| cardId | Int? (FK → cards, CASCADE) | null = 글로벌 규칙 |
| category | Enum | TAX / INSURANCE / GIFT_CARD / TRANSPORT / APARTMENT / TUITION / CUSTOM |
| keyword | String | 제외 키워드 |
| isSystem | Boolean | 시스템 기본 규칙 |
| isEnabled | Boolean | |
| createdAt | Long | |

#### parse_logs (ParseLog)

감사 추적용 — 파싱 전 과정 기록:

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | Long (PK, auto) | |
| rawText | String | 원문 |
| normalizedText | String | 정규화 결과 |
| source | String | PUSH / SMS / KAKAO |
| parserId | String | 파서 ID |
| messageType | String | 분류 결과 |
| confidence | Double | 최종 신뢰도 |
| confidenceLevel | String | HIGH / MID / LOW / REJECT |
| dedupResult | String | UNIQUE / STRICT_DUPLICATE / SOFT_DUPLICATE / SUSPICIOUS |
| outcome | String | SAVED / DUPLICATE_SKIPPED / REJECTED / FAILED / LEGACY_FALLBACK |
| failureReason | String? | 실패 사유 |

#### backup_metadata (BackupMetadataEntity)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | Long (PK, auto) | |
| filePath | String | 백업 파일 경로 |
| createdAt | Long | |
| sizeBytes | Long | |
| encrypted | Boolean | |
| checksum | String | SHA-256 |

### 3.2 마이그레이션 이력

| 버전 | 변경 |
|------|------|
| 10→11 | backup_metadata 테이블 생성 |
| 11→12 | CardEntity: billingEndDay, benefitRate, benefitCashback, benefitPoints 추가 |
| 12→13 | CardEntity: benefitType, benefitValue, periodType / AccountEntity: needsSetup, nickname 추가 |
| 13→14 | CardEntity: lastFourDigits 추가 |

---

## 4. 알림 파싱 파이프라인

### 4.1 소스 → 프로세서 흐름

```
┌─────────────────────────────────────────────────────────────────┐
│ 알림 소스                                                        │
├───────────────────────┬────────────────────┬────────────────────┤
│ CardNotificationListener │ SmsReceiver      │ (카카오: PUSH 경유) │
│ 카드사 앱 8종 감시:      │ 발신번호 매칭:     │                    │
│ 삼성/현대/신한/KB/       │ 15881000→삼성     │                    │
│ 롯데/하나/우리/NH        │ 15772100→현대     │                    │
│ 은행 앱 8종 + 핀테크 5종 │ ...8종 전체        │                    │
└───────────┬─────────────┴─────────┬────────┴────────────────────┘
            │                       │
            ▼                       ▼
┌──────────────────────────────────────────────────────────────┐
│ NotificationProcessor.processWithEngine(rawText, source, pkg)│
│ → ParsingEngine.parse() — 10단계 파이프라인                    │
└──────────────────────────────────────────────────────────────┘
```

### 4.2 파싱 엔진 10단계

Stage 1: NORMALIZATION (공백/통화/시간/동의어 통일)
Stage 2: CLASSIFICATION (HARD/SOFT negative + positive 가중치 매칭 → MessageType)
Stage 3: AMOUNT (5단계 패턴 + 12종 외화 + 문맥 부스팅)
Stage 4: MERCHANT (subtract-and-select 방식)
Stage 5: CARD/ACCOUNT (카드사 코드 + 끝4자리 + 은행 매칭)
Stage 6: CONFIDENCE (가중합산 → HIGH≥0.90 / MID≥0.70 / LOW≥0.50 / REJECT<0.50)
Stage 7: FALLBACK (저신뢰도 시 레거시 파서 체인)
Stage 8: DEDUP (Strict 금액+가맹점 / Soft ±10분 / Suspicious)
Stage 9: RISK CALC (CardRiskCalculator + PaymentRiskCalculator)
Stage 10: PERSIST (TransactionEntity + ParseLog + CardEntity/AccountEntity 갱신)

---

## 5. 리스크 계산 엔진

### 5.1 실적 리스크 (CardRiskCalculator)

```
riskScore = remainingAmount / daysLeft (원/일)
RISK    : remainingAmount > 0 AND daysLeft ≤ 7 AND riskScore ≥ 30,000
CAUTION : remainingAmount > 0 AND daysLeft ≤ 14
SAFE    : 나머지
```

### 5.2 결제 리스크 (PaymentRiskCalculator)

```
PAYMENT_RISK    : balance < expectedPayment
PAYMENT_WARNING : balance < expectedPayment × 1.2
PAYMENT_OK      : 나머지
```

---

## 6. 백업/복원 시스템

암호화: PBKDF2-HMAC-SHA256 (310K iterations) → AES-256-GCM → CSB v2 바이너리

CSB v2 레이아웃: Magic "CSB2" + KDF ID + iterations + salt + IV + ciphertext
복원: v2 (passphrase) + v1 (Android Keystore) 호환

---

## 7. 구독 결제: Google Play Billing 6.1.0, Product "cardspend_premium"

## 8. 카드 OCR: CameraX + ML Kit Korean Text Recognition, 8개 카드사 패턴

## 9. WorkManager 리마인더: 아침/오후/저녁 3회 + D-3/D-1/D-0 결제일

## 10. 화면 네비게이션: 14개 Compose route

## 11. Hilt DI: DatabaseModule + @AndroidEntryPoint 4종 + @Singleton 6종 + @HiltWorker

---

## 12. 기능 현황

| # | 기능 | 상태 |
|---|------|------|
| 1 | 알림 캡처 (PUSH 8+8+5종) | **DONE** |
| 2 | SMS 캡처 (발신번호 8종) | **DONE** |
| 3 | 파싱 엔진 v2 (10단계) | **DONE** |
| 4 | 레거시 파서 폴백 | **DONE** |
| 5 | 중복 제거 v2 | **DONE** |
| 6 | ParseLog 감사 추적 | **DONE** |
| 7 | 실적 리스크 계산 | **DONE** |
| 8 | 결제 리스크 계산 | **DONE** |
| 9 | Room DB v14 | **DONE** |
| 10 | 실적 제외 규칙 (7 카테고리) | **DONE** |
| 11 | 취소 거래 매칭 | **DONE** |
| 12 | 백업 AES-256-GCM + CSB v2 | **DONE** |
| 13 | 복원 (v1 + v2) | **DONE** |
| 14 | Google Play Billing | **DONE** |
| 15 | 카드 OCR 스캔 | **DONE** |
| 16 | WorkManager 리마인더 | **DONE** |
| 17 | Compose UI 14+ 화면 | **DONE** |
| 18 | 외화 거래 파싱 (12종) | **DONE** |
| 19 | 미인식 거래 목록 화면 | **TODO** (v1.1) |

---

## 13. 코드 레벨 문제

| # | 위치 | 문제 | 심각도 |
|---|------|------|--------|
| P0 | HomeScreen.kt:445 | 미인식 거래 목록 화면 미구현 | LOW |
| P1 | AesGcmEncryptor.kt | 키 zero-fill 책임이 caller 전가 | MEDIUM |
| P2 | CardSpendDatabase.kt | exportSchema = false | LOW |
| P3 | SmsReceiver.kt | intent-filter priority 999 충돌 가능 | LOW |
| P4 | RestoreCore.kt | 전체 DELETE→INSERT ANR 위험 | MEDIUM |

---

## 14. 아키텍처 레벨 문제

| # | 문제 | 권장 |
|---|------|------|
| A0 | 단일 앱 모듈 | 현재 규모에서는 수용 가능 |
| A1 | 레거시 파서 3종 + v2 엔진 공존 | v2 안정화 후 레거시 제거 |
| A2 | ParseLog Entity 컬럼 30+ | 감사 목적상 필요. deleteOldLogs() 활용 |