# 스모크런 시나리오 + 폐기·삭제 기록

**원본 출처**: v1.7.1 §18-1~3 + §18-5 + §18-8~10 (0줄)
**v1.8.0 Layer**: Feature
**의존**: `06_product_design/04_system_arch.md`
**변경 이력**: 본 파일은 v1.7.1 §18-1~3 + §18-5 + §18-8~10 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cowork/20_features/25_smoke_scenarios.md`

---

# 18. 스모크런 시나리오 + Four Surfaces 본문

스모크런(Smoke Run)은 **구현 완료 즉시 수행**하는 제품 작동 검증. v1.5.2에서 11개 시나리오 정의, v1.6.0에서 Four Surfaces 관련 시나리오 확장, v1.6.1에서 Surface 본문 완성(Patch 25).

## 18-1. 스모크런 11개 시나리오

| # | 이름 | 목적 | Surface | SLA 레벨 |
|---|---|---|---|---|
| SmokeRun01 | 기본 Cold Start | 설치 직후 Day 0 부트 | 공통 | L1 |
| SmokeRun02 | 착신 오버레이 | 전화 수신 시 4속성 표시 | CallCheck | L1 |
| SmokeRun03 | Softmax 분포 | 신호 100개에서 topConfidence 산출 | 엔진 | L1 |
| SmokeRun04 | 사용자 Override | "안심 표시" 후 NKB 재계산 | 엔진 | L1 |
| SmokeRun05 | 연락처 상호작용 | 연락처 등록 번호는 SAFE 초기값 | Cold Start | L1 |
| SmokeRun06 | MessageCheck 3중 평가 | 발신번호 + URL + 기관명 평가 결합 | MessageCheck | L1 |
| SmokeRun07 | MessageCheck 시나리오 | "쿠팡 배송 알림" 사칭 SMS 검출 | MessageCheck | L1 |
| SmokeRun08 | MicCheck 기본 | RECORD_AUDIO 보유 앱 스캔 + 평가 | MicCheck | L1 |
| SmokeRun09 | CameraCheck 기본 | CAMERA 보유 앱 스캔 + 평가 | CameraCheck | L1 |
| SmokeRun10 | Billing 주기 | 구독 구매 → 상태 업데이트 → 만료 감지 | Billing | L1 |
| SmokeRun11 | L3 Offline 기준선 | 비행기 모드 + NKB 200건에서 p95 ≤ 5ms | 공통 | **L3** |

## 18-2. SmokeRun01: 기본 Cold Start

**조건**: 신규 설치, 모든 권한 허용, 네트워크 연결
**절차**:
1. 앱 최초 실행
2. 온보딩 슬라이드 4개 → "시작하기"
3. 권한 순차 요청 (READ_PHONE_STATE·READ_CALL_LOG·READ_SMS·READ_CONTACTS)
4. Cold Start 6단계 실행 (§11)
5. Self-Discovery 실행 → ClusterProfile 생성 확인
6. 메인 화면 진입

**검증 포인트**:
- NKB에 초기 엔트리 500~1000건 생성 (통화·문자·연락처 기반)
- ClusterProfile 1건 저장, discoveredEngines 비어있지 않음
- 전체 Cold Start 소요 시간 ≤ 30초

## 18-3. SmokeRun02: 착신 오버레이

**조건**: L1, NKB 캐시 있는 번호 "+821012345678" (MEDIUM Risk)
**절차**:
1. 에뮬레이터 또는 실기기에서 해당 번호로 통화 수신
2. 오버레이 렌더 시점 측정
3. FourAttributeCard 내용 확인
4. "차단 / 신고 / 안심" 3버튼 탭 → UserAction 기록 확인

**검증 포인트**:
- 수신 감지 → 오버레이 렌더 ≤ 500ms (p95)
- 4속성 모두 표시 (null·plaintext 없음)
- 버튼 탭 시 NKB 재계산 트리거

## 18-5. PrivacyCheck 폐기 기록 (Patch 21)

v1.5.x에서 언급되었던 `PrivacyCheck` Surface는 **v1.6.0 Patch 21로 폐기**되었다. 폐기 사유:
- 추상적 범주 ("개인정보 위험")
- 구체적 Surface 원칙(제7조 구현) 위반
- MicCheck(§18-6) + CameraCheck(§18-7)로 **세분화·구체화**

이후 버전에서 PrivacyCheck 부활 금지. 구체적 기능은 새로운 Surface(§17-3 후행)로 신설.

## 18-8. (§18-8 JustificationClassifier 삭제 — Patch 30)

v1.6.0~v1.6.1-patch 시점 §18-8에 있던 `JustificationClassifier`와 `AppCategory` 기반 자동 분류 로직은 **Patch 30으로 완전 삭제**되었다.

삭제 사유:
- 대표님 정의 기능 범위를 넘어섬 (사용자 직접 판단이 원칙)
- 카테고리 매핑이 "본사 매핑 아님"이라고 주장했으나, 실제로는 JUSTIFIED/SUSPICIOUS 라벨을 디바이스가 생성 → 헌법 3조 해석 여지
- 단순 리스트·시각·회수 버튼으로 충분

이후 버전에서 부활 금지. 앱 평판 판정이 필요하면 `AppSecurityWatch` Surface(§17-3 후행)에서 별도 설계.

## 18-9. SmokeRun10: Billing 주기

**조건**: L1, Play Billing Library v7 연동
**절차**:
1. 앱 시작 → 구독 상태 조회 (BillingClient.queryPurchasesAsync)
2. 미구독 상태 → "구독" 버튼 표시
3. 버튼 탭 → Play 구독 시트 → 결제
4. 결제 완료 → BillingClient 콜백 수신 → 로컬 구독 상태 업데이트
5. 앱 재시작 → 상태 복원 확인
6. 30일 경과 모사 (테스트 환경) → 갱신 수신 처리
7. 구독 취소 → 만료 시각까지 유지 → 만료 시각 이후 해제

**검증 포인트**:
- BillingClient 오프라인 대응 (네트워크 복귀 시 sync)
- Purchase Token 로컬 저장 (자체 영수증 검증 서버 없음 — 헌법 정합)
- 구독 상태 UI 즉시 반영

## 18-10. SmokeRun11: L3 Offline 기준선 (SLA-14-2 검증)

**조건**: 비행기 모드, NKB에 200건 엔트리 존재
**절차**:
1. 앱 실행
2. 비행기 모드 진입 → SlaLevelDetector가 L3 감지 → 상단 배너 표시
3. CallCheck 통화 수신 (에뮬레이터 트리거) → NKB Hit 경로로 4속성 출력
4. MessageCheck SMS 수신 → 발신번호 NKB Hit → MessageRisk 출력
5. MicCheckScreen 진입 → 이미 스캔된 앱 목록 표시 (외부 조회 없이)
6. 응답 시간 측정

**검증 포인트**:
- NKB Hit p95 ≤ 5ms (JMH 또는 Espresso timer)
- Stale 엔트리는 STALE_OFFLINE 플래그로 그대로 표시
- 사용자 경험 저하 없음 (UI 동일, 배너만 추가)

---

