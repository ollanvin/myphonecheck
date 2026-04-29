# 26. PushCheck — 푸시 휴지통 (Push Trash)

> **정식 Surface 승격**: v1.6.0~v1.8.0에서 "Phase 후행"으로 분류됐던 PushCheck를 v1.9.0에서 정식 Surface로 승격.
> Stage 1-001에서 cursor가 `feature/push-trash` 모듈로 구현 완료한 사실을 반영.

---

## 26-1. 정의

**PushCheck**는 사용자가 스팸으로 지정한 발신자(앱)의 알림을 자동 격리하여 알림 지옥에서 해방시키는 Surface.

핵심 가치: **알림이 시각적·청각적으로 노출되지 않으면서, 사용자가 원할 때 휴지통에서 확인·복원할 수 있는 격리 모델**.

## 26-2. 데이터 소스

- **NotificationListenerService (NLS)**: 시스템 전체 알림 수신
- **Room DB v12**: 격리된 알림(title·text 본문 포함) 영구 저장
- **자체 매핑 테이블**: 채널 ID → 사용자 정의 라벨

새 외부 통신: **0** (헌법 1조 정합)

**헌법 2조 (In-Bound Zero) 해석 명확화**: OS 알림 본문(title, text)은 휴지통 복원·검색 기능을 위해 디바이스 내부에 보존된다. 헌법 2조 (In-Bound Zero)는 **외부 검색 엔진에서 받은 HTML/JSON 원문**에 적용되며, OS NotificationListener를 통한 **디바이스 내부 알림 본문은 본 조 비대상**이다. 알림 본문은 사용자 자신의 디바이스 자원으로 외부 원문이 아니다.

## 26-3. 작동 흐름

```
[알림 도착]
   ↓
[NotificationListenerService 가로채기]
   ↓
[스팸 지정 앱 여부 확인]
   ├─ Yes → [알림 cancel + Room DB 저장]
   └─ No  → [통과 (시스템 기본 표시)]
```

## 26-4. 사용자 경험

- **격리**: 스팸 지정 앱의 알림은 시각·청각적 노출 0
- **휴지통 UI**: 채널 단위 차단 설정 + 휴지통 화면
- **복원·삭제**: 사용자가 휴지통 열어서 알림 단위로 복원 또는 삭제 가능
- **5개 이상 시드 매핑**: 주요 메신저·쇼핑·뉴스 앱 채널 라벨 사전 정의

## 26-5. Stage 1 완료 범위 (Stage 1-001 cursor 구현)

> **v1.9.0 시점 PushCheck Stage 1 = 규칙 기반 휴지통** (앱/채널 차단 규칙으로 격리 여부 결정). Decision Engine 통합 (NKB·Softmax·Tier 가중치 기반 위협 평가)은 **Stage 2+ 후속 작업**으로 위임.

1. NLS 권한 요청 UX
2. 알림 수신 → Room DB 저장
3. 채널 단위 차단 설정 UI
4. 휴지통 화면 (복원·삭제)
5. 채널 매핑 테이블 시드 (5개 이상)

모두 완료 (cursor 구현, PR 머지 완료).

## 26-6. 비범위 (Stage 2+로 위임)

- 키워드 학습 보조 분류
- 알림 카테고리 자동 추천
- 매핑 테이블 확장 (상위 30~50개)
- 통계 그래프

## 26-7. 모듈 매핑

- `:feature:push-trash` (구현 완료, Stage 1-001)
- `:data:local-cache` (Room DB v12, 새 entity 추가됨)
- `:app` (NavHost route, settings card)

## 26-8. 헌법 정합성

**기준 헌법**: `docs/00_governance/architecture/v1.9.0/05_constitution.md` (MyPhoneCheck product 헌법 7개 조항)

PushCheck는 위 7개 조항 중 **6개 조항에 직접 정합**한다 (제6조 가격 정직성은 PushCheck와 무관하므로 본 표에서 제외).

| 헌법 (조 + 정식 명칭) | 정합 여부 | 근거 |
|---|---|---|
| 제1조 Out-Bound Zero (사용자 데이터 외부 전송 금지) | OK | NLS는 OS API. 알림 메타데이터·원문 모두 외부 전송 0 |
| 제2조 In-Bound Zero (외부 원문 영구 저장 금지) | OK (비대상) | OS 알림 본문은 디바이스 내부 자원이며 외부 원문 아님. 헌법 2조는 외부 검색 원문에 적용 (§26-2 해석 명확화 참조). 휴지통 DB에 보존되는 알림 본문은 사용자 자신의 디바이스 자원 |
| 제3조 결정권 중앙집중 금지 | OK | 격리 여부 판단은 사용자 차단 규칙 + 디바이스 로컬 채널 매핑. 본사 fallback 0 |
| 제4조 자가 작동 (Self-Operation, L3 기준선) | OK | 네트워크 단절 시에도 NLS·Room DB·휴지통 UI 모두 작동 (L3 기준선 충족) |
| 제5조 정직성 (Honesty) | OK | 격리 사실을 사용자에게 명시 + 사용자가 휴지통에서 직접 복원·삭제 가능 (격리·복원 투명성 보장) |
| 제7조 디바이스 오리엔티드 거위 (Device-Oriented Goose) | OK | NLS는 Android 표준 메커니즘. 본사 운영 0, 본사 매핑 0, 모든 처리 온디바이스 |

**제6조 가격 정직성 비대상 사유**: PushCheck는 알림 격리 Surface로 구독·결제·net ARPU 측정과 직접 관련이 없다. 가격 정직성은 §16 (Billing) + §31 (Pricing) 영역에서 측정·검증된다.

## 26-9. cross-ref

- 통합운영설계안 v1 §2.9 ("실제 격리 + UI 필수" 확정)
- Stage 1-001 cursor 보고서: `docs/07_relay/done/REPORT-WO-STAGE1-001__cursor__done.md`
- 기술 검증: `docs/07_relay/done/TECH-VERIFICATION-NLS.md`
- 수동 테스트 절차: `docs/05_quality/stage1_push_trash_manual_test.md`
- §17-1 Surface 정의 (v1.9.0 정정 — Stage 1 PushCheck = 규칙 기반 휴지통, Stage 2+ DE 통합 후속)
- §36-3-A Surface 확장 정책 PushCheck 사례 (Stage 1 규칙 기반 한정)

---

## §direct-search. "직접 검색" 버튼 spec (v2.4.0 신설, 2026-04-29 대표님 결정)

### 목적

Risk Tier "Unknown" 영역 사용자 의사결정 보조. 옛 MyPhoneCheck v1에서 "확실하게 피싱·스팸이라고 판단하기 어려운 애매한 번호"가 사용자에게 가장 답답한 영역이었던 점 정정.

본 Surface에서 사용자가 수신/거절/차단 외 **"직접 확인" 옵션**을 행사할 수 있도록 "🔍 직접 검색" 버튼을 prominent 배치한다.

### 동작

```
[🔍 직접 검색] 탭
  ↓
SIM 기준 AI 검색 후보 메뉴 (최소 2개) 표시
  - 본문 파싱 결과: 폰 번호 → SearchInput.PhoneNumber, 앱 패키지명 → SearchInput.AppPackage
  - SimAiSearchRegistry가 SIM countryIso 기반 AI 후보군 추출
  - 예) KR SIM: Naver AI / Google AI Mode (udm=50) / Bing Copilot
  - 예) Global default: Google AI Mode / Bing Copilot
  - 사용자 자율 결정, 고정 우선순위 없음, 마지막 선택 기억
  - 모두 Custom Tab 직접 진입 (헌법 §1 정합)
  ↓
사용자가 검색 결과 보고 본인 판단 (AI 모드가 공공 + 경쟁사 reverse 자체 통합)
  ↓
사용자 행동:
  - 수신
  - 거절
  - 차단
  - 태그 추가 (다음 동일 query 시 NKB로 흡수 → score 갱신, 헌법 §1·§2 정합 = 온디바이스만)
```

### 헌법 정합

- §1 Out-Bound Zero: Custom Tab 사용자 직접 진입 = 사용자 본인 의지 외부 검색 (우리 송신 0)
- §3 결정권 중앙집중 금지: 우리는 검색 채널 제공만, 행동 결정은 사용자
- §5 정직성: Tier Unknown을 정직히 표시 + 직접 검색 채널 제공

### UX 가이드

- 버튼 색상: Tier 색상과 대비되는 중립색 (회색 또는 파랑)
- 버튼 위치: Tier 표시 바로 옆 또는 아래 (사용자 시선 자연 흐름)
- 버튼 크기: 최소 44dp (Material Design 터치 타겟)
- 버튼 레이블: "🔍 직접 검색" (다국어 strings.xml)
- SIM 기준 AI 후보 메뉴 시트: BottomSheet 또는 Dialog (Material 3)
- 마지막 선택 기억: SharedPreferences 로컬 저장 (헌법 §1·§2 정합)

### §direct-search-push. PushCheck 배치 위치

**휴지통 항목별**:
- 위치: 휴지통 UI 각 항목 우측에 "🔍 검색" 아이콘
- 검색 대상: 알림 발신 앱 패키지명 (SearchInput.AppPackage, 1차) / 알림 본문 발신자 번호 (SearchInput.PhoneNumber, 2차, 본문 파싱)
- 후보군: SIM 기준 AI 검색 후보 (메뉴 최소 2개) — input 타입 무관 동일 패턴, AI 모드가 자체 통합

**모듈**: `:feature:push-trash` (Stage 1-001 완료)
