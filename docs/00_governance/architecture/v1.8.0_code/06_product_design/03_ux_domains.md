# 4. 제품 도메인 (UX 상황별 정의)

**원본 출처**: v1.7.1 §4 (51줄)
**v1.8.0 Layer**: Policy
**의존**: `00_core/01_primary.md` + `06_product_design/02_golden_egg.md`
**변경 이력**: 본 파일은 v1.7.1 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_code/06_product_design/03_ux_domains.md`

---

# 4. 제품 도메인 (UX 상황별 정의)

사용자가 황금알을 받는 4가지 UX 상황을 정의한다. 각 상황마다 4속성이 어떻게 표현되는지 명시한다.

## 4-1. 착신 화면 (IncomingCallScreen)

통화 수신 시 **오버레이**로 표시. 전체 화면이 아닌 상단 플로팅 카드.

- 트리거: `TelephonyManager.CALL_STATE_RINGING`
- 표시 시점: 수신 ≤ 500ms 이내
- 포함 내용: FourAttributeCard (4속성) + 3버튼 (차단 / 신고 / 안심)
- 종료 조건: 수신자 응답 · 거부 · 타임아웃

## 4-2. 통화 중 화면 (InCallScreen)

통화 응답 시 표시되는 전화 앱의 **기본 화면을 보강**한다(교체 아님). 하단 고정 띠로 Risk 요약만 유지.

- 포함 내용: RiskBadge + 1줄 요약 (이유 설명)
- 사용자 조치 버튼: 없음 (통화 방해 금지)
- 통화 중 자동 갱신: 없음 (배터리·개인정보 고려)

## 4-3. 통화 종료 화면 (PostCallScreen)

통화 종료 직후 **전체 카드**로 표시. 차단·신고·연락처 저장·안심 표시 중 선택.

- 트리거: `CALL_STATE_IDLE` + 직전 CALL_STATE_OFFHOOK
- 표시 시점: 종료 ≤ 1초 이내
- 포함 내용: FourAttributeCard + 4버튼 (차단 / 신고 / 저장 / 안심)
- 사용자 기록: `UserAction` Entity에 행동 저장 (§8-2)

## 4-4. 통화 로그 화면 (CallLogScreen)

과거 통화 이력 + 각 번호의 Risk 정보 **합쳐서 표시**. 기본 앱 통화 로그 대체 아님, 별도 앱 탭.

- 정렬: 최근순 · Risk 높음순 선택 가능
- 각 엔트리: 번호 · 시각 · 통화 시간 · 위험도 · 예상 손해 · 이유 1줄
- 상세 진입: 탭 시 FourAttributeCard 전체

## 4-5. Four Surfaces 공통 UX 원칙

| 원칙 | 내용 |
|---|---|
| 일관성 | 4 Surface 모두 FourAttributeCard 규격 준수 |
| 비침습성 | 전체 화면 탈취 금지, 오버레이·하단 띠·탭 내부만 허용 |
| 사용자 조치 우선 | Risk 정보 + 3버튼 이상 항상 노출 |
| 다국어 | strings.xml 템플릿 기반, 하드코딩 절대 금지 (메모리 #1) |
| 다크/라이트 | 시스템 설정 자동 추종 |
| 접근성 | TalkBack 대응, 최소 터치 48dp |

---
