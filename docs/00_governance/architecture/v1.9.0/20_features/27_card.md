# 27. CardCheck — 월별 카드 사용액 관리

> **신규 Surface (v1.9.0)**: SMS/Push 데이터를 재활용하여 카드 결제 패턴만 추출, 월별·카드별 사용액을 사용자에게 표시.
> 카드스펜드 별도 앱 폐기 결정에 따라 MyPhoneCheck CardCheck로 통합.

---

## 27-1. 정의

**CardCheck**는 사용자의 카드 결제 SMS 및 Push 알림에서 결제 정보(카드명·금액·일시·가맹점)를 파싱하여 월별·카드별 합계를 관리하는 Surface.

핵심 가치: **사용자가 별도 가계부 앱 없이도 자신의 월별 카드 지출을 한눈에 파악**.

## 27-2. 데이터 소스 — Producer/Consumer 모델

CardCheck는 **순수 소비자 (Pure Consumer)**:

- 자체 데이터 소스 없음
- 새 권한 요청 0 (MessageCheck/PushCheck가 받은 것 활용)
- 새 외부 통신 0

```
SMS Repository (Android OS) ──┬──→ MessageCheck (자기 파서, 스팸 검증)
                              └──→ CardCheck (자기 파서, 거래 추출)

Notification (NLS, PushCheck) ──→ CardCheck (카드사 앱 알림 필터)
```

**중요**: CardCheck는 MessageCheck 파서를 호출하지 않음. 동일 SMS를 두 Surface가 각자 자기 관점으로 파싱.

이유:
- 단일 책임 원칙 (SRP)
- 한쪽 변경 시 다른 쪽 영향 차단
- 빅테크 표준 (Producer/Consumer 분리)

## 27-3. 파싱 패턴

**카드사명 (한국 주요)**:

- KB국민, 신한, 삼성, 현대, 롯데, 우리, 하나
- NH농협, BC, 카카오뱅크, 토스뱅크
- 기타 카드사 (확장 가능)

**추출 필드**:

- `cardName`: 카드사명 (필수)
- `amount`: 결제 금액 (정수, 통화 표기)
- `timestamp`: 결제 일시 (long, epoch ms)
- `merchantName`: 가맹점명 (nullable)
- `source`: SMS 또는 NOTIFICATION

## 27-4. 저장 (Room DB)

**신규 entity**: `CardTransaction`

```kotlin
@Entity(tableName = "card_transaction")
data class CardTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardName: String,
    val amount: Long,        // KRW 정수 (소수점 처리는 통화별)
    val timestamp: Long,     // epoch ms
    val merchantName: String?,
    val source: String       // "SMS" or "NOTIFICATION"
)
```

**헌법 2조 정합**: 원문 SMS/Push 폐기, 추출 정보만 영구 저장.

## 27-5. 집계 — 월 단위

- **월 경계**: 매월 1일 00:00:00 ~ 말일 23:59:59
- **타임존**: 디바이스 시스템 timezone 적용 (앱팩토리 철칙)
- **집계 단위**: 카드별 총액

## 27-6. UI

- **카드별 월 사용액 카드뷰**: 카드사 logo + 이번 달 합계
- **월 선택**: 이번 달 / 지난 달 / 임의 월
- **카드별 결제 내역 리스트**: 시간순 정렬, 가맹점·금액·일시
- **헌법 6조 정합**: 가공·예측 없이 측정값 그대로 표시

`FourAttributeCard` 미사용 (위협 평가 Surface가 아니므로). 카드사별 자체 UI 카드 사용.

## 27-7. Stage 1-002 범위 (예정, 본 v1.9.0 머지 후 별도 PR)

1. SMS Repository 접근 (READ_SMS 권한 — MessageCheck가 이미 보유)
2. 카드 결제 SMS 파싱 (정규식 + 카드사별 패턴)
3. NotificationListener에서 카드사 앱 알림 추출
4. Room DB 저장 + 월별 집계
5. 월별·카드별 UI

## 27-8. 비범위 (Stage 2+)

- 예산 설정·알림
- 카테고리 자동 분류
- 통계 그래프
- 다중 통화 (KRW 외)
- 카드 추천·비교

## 27-9. 모듈 매핑

- `:feature:card-check` (신규, Stage 1-002에서 작성)
- `:data:local-cache` (CardTransaction entity 추가)
- 의존: `:data:sms` (기존), `:feature:push-trash` (기존)

## 27-10. 헌법 정합성

**기준 헌법**: `docs/00_governance/architecture/v1.9.0/05_constitution.md` (MyPhoneCheck product 헌법 7개 조항)

CardCheck는 위 7개 조항 모두에 직접 정합한다.

| 헌법 (조 + 정식 명칭) | 정합 여부 | 근거 |
|---|---|---|
| 제1조 Out-Bound Zero (사용자 데이터 외부 전송 금지) | OK | 새 외부 통신 0. SMS/Push 자체는 OS가 이미 받은 사용자 디바이스 자원이며 카드사 서버로 재전송하지 않음 |
| 제2조 In-Bound Zero (외부 원문 영구 저장 금지) | OK | SMS/Push 원문은 메모리 내 파서가 처리 후 폐기, 추출 필드(cardName·amount·timestamp·merchantName·source)만 영구 저장 |
| 제3조 결정권 중앙집중 금지 | OK | 디바이스 로컬 파싱·집계 + 카드사별 패턴 매칭. 본사 분류·중앙 결정 0 |
| 제4조 자가 작동 (Self-Operation, L3 기준선) | OK | 네트워크 단절 시에도 SMS Repository·NLS·Room DB·UI 모두 작동 (L3 기준선 충족) |
| 제5조 정직성 (Honesty) | OK | 카드 사용액 측정값 그대로 표시, 가공·예측 없음. 추출 실패·모호 시 사용자에게 ambiguous 플래그 노출 (헌법 §5-1) |
| 제6조 가격 정직성 (Pricing Honesty) | OK | 카드 결제 금액은 SMS/Push에 표시된 측정값 그대로 사용 (가공·예측·환산·반올림 없음). 사용자가 직접 본 영수증·SMS와 1:1 일치 보장 |
| 제7조 디바이스 오리엔티드 거위 (Device-Oriented Goose) | OK | 모든 처리 온디바이스. 본사 운영 0, 본사 매핑 0, 카드사 서버 직접 통신 0 (SMS/Push 재활용만) |

## 27-11. 사용자 대면 약속

CardCheck 화면 또는 onboarding에 다음 문구 명시 필수:

> **"모든 카드 데이터는 디바이스 내에서만 처리되며, 어떤 외부 서버로도 전송되지 않습니다.**
> **SMS·알림은 OS가 이미 받은 것을 읽기만 하며, 원문은 가공 후 즉시 폐기됩니다."**

이 약속은 헌법 1조·2조 정합성을 사용자에게 직접 전달하는 신뢰 메시지.

## 27-12. cross-ref

- 카드스펜드 폐기 결정: 메모리 #2 (figma 자료 보존, 코드 폐기 별도 작업)
- Stage 1-002 워크오더: 본 v1.9.0 머지 후 발행 예정
- §17-1 Surface 정의 (v1.9.0 정정 — 거래 추출 Surface 분류)
- §36-3-B Surface 확장 정책 CardCheck 사례
