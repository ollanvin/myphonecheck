# 17. 제품 전략 (Product Strategy) — One Core Engine, Six Surfaces

**원본 출처**: v1.7.1 §17 (1784–1843)
**v2.0.0 Layer**: Core
**의존**: `00_core/01_primary.md`
**변경 이력**: v1.9.0 → v2.0.0 MAJOR 승격에서 §17 본질 정확화 — Decision Engine을 코어 내부 모듈로 재정의 (One Core Engine, 모든 Surface 단일 코어 사용). v1.9.0의 위협 평가 Surface 한정 해석 정정.
**파일 경로**: `docs/00_governance/architecture/v2.0.0/06_product_design/05_product_strategy.md`

---

# 17. 제품 전략 (Product Strategy) — One Core Engine, Six Surfaces

본 장은 헌법 8조와 황금알 정의(§3)가 **제품 로드맵 차원**에서 어떻게 구현되는지 명시한다.

## 17-1. One Core Engine 원칙 재선언 (v2.0.0 정확화)

- **엔진은 하나**: 모든 Surface는 단일 코어 `:core:global-engine`을 사용한다 (§30 본문). Decision Engine은 코어 내부 모듈 (`decision/InputAggregator.kt`)이다.
- **위협 평가·가치 추출·인벤토리 모두 동일 코어 활용**: 위협 평가 Surface(CallCheck/MessageCheck/PushCheck)와 가치 추출 Surface(CardCheck), 권한 인벤토리 Surface(MicCheck/CameraCheck) 모두 같은 코어. 자체 파서·매핑 코드 금지.
- **엔진 분기 금지**: "CallCheck 전용 코어" 같은 분기 금지. 입력 타입으로 분기하되, 동일 코어 내부에서 처리.
- **Surface는 Value Extraction Layer**: Surface는 사용자 가치 추출 단위. 코어 결과를 Surface별 UI·UX로 가공.
- **확장은 Surface 추가로**: 새 기능 추가 시 기존 코어는 유지하고, 코어 활용 형태로 새 Surface 추가.
- **헌법 8조 정합**: 모든 Surface가 SimContext 기반 동작 (§29). Surface별 자체 국가·통화 매핑 = 헌법 위반.

> **v1.9.0 → v2.0.0 정정**: v1.9.0 §17-1에서 비전이 "Decision Engine 공유"를 위협 평가 Surface 한정으로 해석한 것은 본질 모호. v2.0.0에서 모든 Surface가 단일 코어 사용하는 것으로 정확화. 코드 마이그레이션은 Stage 2-001 ~ 2-005 후속 (§30-8 표 참조).

## 17-2. v1.9.0 Six Surfaces

| # | Surface | 입력 원천 | 출력 | 구현 상태 (v1.9.0) |
|---|---|---|---|---|
| 1 | CallCheck | `TelephonyManager.CALL_STATE_RINGING` 전화번호 | CallRisk (4속성) | 기준 Surface |
| 2 | MessageCheck | MMS/SMS 발신번호 + URL + 기관명 (Mode A/B) | MessageRisk (4속성) | 정식 |
| 3 | MicCheck | RECORD_AUDIO 보유 앱 목록 | List<MicPermissionEntry> | 정식 (단순 관리자) |
| 4 | CameraCheck | CAMERA 보유 앱 목록 | List<CameraPermissionEntry> | 정식 (단순 관리자) |
| 5 | PushCheck | NotificationListenerService 알림 메타데이터 | Trash Item (Stage 1 규칙 기반 휴지통; PushRisk 4속성 통합은 Phase 2+ 후속) | **정식 승격** (Stage 1-001 구현 완료 반영) |
| 6 | CardCheck | 카드 결제 SMS/Push | CardSummary (월별 카드 사용액) | **Stage 1-002 구현 완료** (PR #14, `e6a76ad`). Stage 2-001에서 코어 엔진 마이그레이션 |

## 17-3. 정식 승격 + 후행 Surface 구분

| Surface | 개요 | 현재 상태 | 이관 이력 |
|---|---|---|---|
| **PushCheck (푸시 휴지통)** | NotificationListenerService 기반, 스팸 지정 발신자 알림 자동 cancel → 자체 DB 저장 → 휴지통 UI. Stage 1 시점 **규칙 기반** (앱/채널 차단 규칙 매칭). 코어 엔진 활용은 **Stage 2-004 후속 마이그레이션**. PushRisk 4속성 통합은 코어 통합 후. | **정식 Surface** (Stage 1 규칙 기반) | Stage 1-001 cursor 구현 완료 반영 |
| **CardCheck** | 카드 결제 SMS/Push에서 카드명·금액·일시·가맹점을 추출하여 월별 합계 표시 | **정식 Surface** | 카드스펜드 별도 앱 폐기 결정에 따라 통합 |
| **AppSecurityWatch** | 신규 앱 설치 시 과거 보안 사고 이력 자동 검색·경고 + 기존 앱 신규 CVE/침해 사고 실시간 감지·알림 + NVD CVE·CISA KEV·Have I Been Pwned 조회 + Decision Engine 기반 앱 평판 판정 | 후행 | **Patch 31로 MicCheck/CameraCheck에서 분리 이관** |
| UrlCheck | 브라우저 공유 URL 검사 | 후행 | — |

본 v1.9.0에서는 **Six Surfaces가 정식 스펙**이다. AppSecurityWatch·UrlCheck는 후행 Surface로 유지한다.

**이관 근거 (Patch 31)**: MicCheck/CameraCheck는 대표님 지시(2026-04-24)에 따라 "리스트 + 최근 사용 + 회수 버튼" 3기능의 **단순 관리자**로 축소되었다. 이관된 감시·CVE·침해 판정 기능은 **별도의 구체적 Surface**로 재탄생하는 것이 헌법 7조 "구체적 Surface 원칙"에 정합하다.

**PushCheck cross-ref**: `통합운영설계안_v1.docx §2.9`의 "실제 격리 + UI 필수"는 본 v1.9.0에서 정식 Surface로 승격되었다. 구현 상세는 `20_features/26_push.md` 참조.

**CardCheck cross-ref**: 카드스펜드 별도 앱 폐기 결정에 따라 CardCheck는 MyPhoneCheck 안으로 통합된다. 구현 상세는 `20_features/27_card.md` 참조.

## 17-3-A. v2.0.0 신규 정식 항목

| 항목 | 위치 | 정합 헌법 | 비고 |
|---|---|---|---|
| **Initial Scan** | §28 본문 | 1·2·3·8조 | 최초 론칭 6 Surface 베이스데이터 일괄 구축 |
| **SIM-Oriented Single Core** | §29 본문 + 헌법 §8조 | 8조 (신설) | SIM = 국가·통화·전화번호 단일 진실원, UI 언어 3단 fallback |
| **`:core:global-engine`** | §30 본문 | 1·2·3·7·8조 | 모든 Surface 단일 코어, 검색 3대 축 통합 |

본 3 항목은 v2.0.0 신설. 코드 마이그레이션은 Stage 2-001 ~ 2-005 후속 (§30-8 표 참조).

## 17-4. Phase 로드맵

| Phase | 범위 | 검증 게이트 |
|---|---|---|
| Phase 0: Foundation | Stage 0 Contracts (5 파일 / 22 시그니처) | FreezeMarkerTest 22개 PASS ✓ (Patch 37 통일, 이미 완료) |
| Phase 0-hotfix | Java 17 전체 승격 | CI 빌드 PASS ✓ (이미 완료) |
| Phase 1: CallCheck MVP | 4속성 출력 · NKB · 3계층 소싱 · L3 경로 | 스모크런 1~5, 11 PASS |
| Phase 2: MessageCheck | MessageRisk + 3중 평가 + URL probe | 스모크런 6~7 PASS |
| Phase 3: MicCheck / CameraCheck | `MicPermissionEntry`/`CameraPermissionEntry` + PackageManager 스캔 (단순 관리자, Patch 30·37) | 스모크런 8~9 PASS |
| Phase 3.5: PushCheck | NLS + Room DB v12 + 휴지통 UI | Stage 1-001 완료 반영 |
| Phase 4: Billing 통합 | Play Billing + 구독 상태 UX | 스모크런 10 PASS |
| Phase 4.5: CardCheck | 카드 결제 SMS/Push 파서 + 월별 카드 합계 | Stage 1-002 완료 (PR #14, `e6a76ad`) → Stage 2-001 코어 마이그레이션 예정 |
| Phase 5: 다국어·접근성 | 190개국 strings.xml + TalkBack | SmokeRun12 (다국어 렌더 확인) |
| Phase 6: 안정화 + Store | 90일 Vitals 모니터링 + 스토어 심사 대응 | Play Console 승인 |
| Phase 7: iOS 진입 | v2.0.0 이후 | 별도 MAJOR 계획 |

## 17-5. Rule 3 충돌 해소

헌법·메모리·입력 자료가 충돌할 때 다음 규칙:

- **Rule 1**: 정식 문서 간 충돌 → 후시간 우선
- **Rule 2**: 대화록 내부 충돌 → 위쪽(최근) 우선
- **Rule 3**: 정식 문서 vs 대화록 충돌 → 비전 판정 후 대표님 확인

본 v1.6.1 재작성에서 적용한 Rule 3 사례:
- v1.5.1 원문 "사용자 1.5 USD 가격" ↔ 메모리 #11 "$2.49/월" → 메모리 후시간, $2.49 채택 (v1.6.1·v1.6.2 모두 승계)
- v1.5.1 원문 "PrivacyCheck 포함" ↔ v1.6.0 Patch 21 "PrivacyCheck 폐기 → MicCheck/CameraCheck" → v1.6.0 후시간, 폐기 반영
- v1.5.1 원문 "BROADCAST_SMS 권한" ↔ Patch 17 "제거" → Patch 후시간, 제거 반영
