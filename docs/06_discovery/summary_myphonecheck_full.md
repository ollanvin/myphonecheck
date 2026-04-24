# Summary — myphonecheck full (Codex, WO-DISCOVERY-004)
- 작성: Codex CLI
- 작성 시각: 2026-04-23
- 스캔 대상: `C:\Users\user\Dev\ollanvin\myphonecheck`
- 기준: 워크오더의 "비전 미정독 58개" 범위를 우선으로 보되, 실제 현재 트리는 릴레이/디스커버리 문서가 추가되어 더 커져 있음
- 해석 원칙: 헌법·governance·specs는 FULL, engineering/quality/operations는 요지 중심, history/archive는 1줄 중심

## 1. 헌법성 진술

- `docs/ARCHITECTURE.md`는 제품을 100% 온디바이스 방어 앱으로 정의하고, 서버 전송 없음, SHA-256 기반 PII 로그 금지, 2-Phase UX, 190개국 정책, Ring 디자인 시스템을 핵심 원칙으로 둔다.
- `docs/02_product/specs/PRD_CALLCHECK_V1.md`는 Android only, 중앙 전화번호 DB 금지, 온디바이스 우선, 검색 증거는 2차 보강, 3초 UX, 단일 글로벌 플랜, 광고 금지, B2B 금지를 비협상 규칙으로 둔다.
- `docs/03_engineering/integration/SPEC_2026-04-14_core_rebuild.md`는 금액 기반 가치표현 금지, 저장번호 완전 제외, Play Billing 전용, 100% 온디바이스, 공식 API 우선이라는 강한 제약을 건다.
- `docs/03_engineering/integration/SPEC_2026-04-14_core_rebuild_v2.md`는 위 원칙을 유지하면서도 "무료 경험 → 가치 체감 → 끊기면 불안" 전환 설계를 공식화한다. 다만 금액 표현 금지는 다시 고정한다.
- `docs/03_engineering/number-profile-label-tag-design.md`는 저장되지 않은 번호도 연락처 저장 없이 관계 관리가 가능해야 하며, 검색 상태와 사용자 관계 시그널은 절대 한 줄 의미로 합치지 말라고 못 박는다.
- `docs/02_product/specs/GLOBAL_CASE_COVERAGE_V1.md`는 unknown을 danger로 처리하지 말아야 한다는 원칙과 저장 연락처 스푸핑 예외 처리까지 규정한다.
- `docs/04_operations/codex-work-instruction-global-core.md`는 190개국 단일 코어, country-specific core branching 금지, MessageCheck는 core extension, PushCheck 비활성, shared constitution과 project docs 분리를 다시 선언한다.
- `web/callcheck/docs/00_governance/myphonecheck-global-core-common-principles-draft.md`와 합치면 현재 살아 있는 헌법 축은 다음 8개로 압축된다:
- 190-country single core
- country-specific feature branching 금지
- 3개 고정 레이어
- CallCheck only judgment core
- MessageCheck is extension, not separate engine
- PushCheck disabled or 재정의 대상
- user is final decision-maker
- fixed search-status wording only

## 2. 도구·역할·워커 관련 진술

- `docs/04_operations/relay_protocol_v1.md`는 `docs/07_relay/{queue,in_progress,done,failed,needs_approval}` 구조와 tool별 역할 분업을 정의한다.
- Codex 역할은 "크로스체크 전담", Claude Code는 감사·정리·거버넌스, Cursor는 구현 전담으로 나뉜다.
- `docs/04_operations/codex-work-instruction-global-core.md`는 Codex 작업의 우선순위를 global single-core 전환, docs 루트 집중, shared constitution 분리로 둔다.
- `scripts/local-test-rig/README.md`는 테스트 체계를 `L1 문서/정적 검증`, `L2 에뮬레이터`, `L3 실기기/실전 검증` 3층으로 정리한다.
- `docs/01_architecture/stage1_research/04_test_strategy.md`는 다시 `L1~L5` 테스트 피라미드로 재정의하며, 현재는 L1 중심, L3/L4는 후순위로 본다.

## 3. 마일스톤·진행상태·결정

- 초기 산출물 계열 (`docs/04_operations/setup/SETUP_COMPLETE.md`, `docs/04_operations/PROJECT_STATUS.md`, `docs/03_engineering/index/FILE_INDEX.md`)은 "멀티모듈 Android 프로젝트 기초가 거의 다 깔렸다"는 시점을 기록한다.
- `docs/03_engineering/integration/IMPLEMENTATION_SUMMARY.md`, `README_IMPLEMENTATION.md`, `INTEGRATION_GUIDE.md`는 settings/billing/country-config 통합을 한 번의 큰 구현 묶음으로 정리한다.
- `docs/05_quality/audit_stage0_hotfix_java17_20260422.md`, `hygiene_stage0_hotfix_20260422.md`, `filter_repo_stage0_hotfix_20260422.md`는 Stage 0 hotfix에서 Java 17 정렬, hygiene, filter-repo 이력 정리를 PASS로 기록한다.
- `docs/01_architecture/stage1_research/03_dependency_audit.md`, `04_test_strategy.md`, `05_ci_cd_roadmap.md`는 Stage 1 전 사전조사 패키지다.
- `docs/07_relay/done/*` 및 `docs/07_relay/queue/*`는 이제 이 저장소가 "문서 저장소"를 넘어서 tool relay bus로도 쓰이고 있음을 보여준다.

## 4. 미해결·의사결정 대기·이슈

- 가격: 비전 판정(2026-04-24) 및 `WO-PATCH-PRD-PRICING-001`로 `PRD_CALLCHECK_V1.md`·연동 문서를 **USD 2.49/month 단일**에 맞췄다. 과거 `USD 1/month` 문구는 PRD 본문에서 폐기됨(히스토리·아카이브 문서에는 잔존 가능).
- PushCheck 정의가 흔들린다. 오래된 아키텍처/구현 문서는 active spam engine처럼 쓰고, 최신 governance 계열은 disabled 또는 push-trash 재정의로 이동했다.
- search 전략도 단일하지 않다. `docs/ARCHITECTURE.md`는 8개 스크레이핑 provider를 설명하지만, SPEC v2는 초기 릴리즈에서 Custom Tab 전용과 스크래핑 금지를 선언한다.
- 테스트 전략은 정리되어 있으나 실제 `src/androidTest`는 비어 있고, visual regression도 아직 도입 전이다.
- CI는 JDK 17 기준으로 정리됐지만, Paparazzi 2.x/AGP 9.x/Kotlin 2.3.x 같은 후속 업그레이드는 아직 정책화되지 않았다.

## 5. 사업 정보

- 제품 포지션은 "caller ID DB"가 아니라 "decision-support app"이다.
- 글로벌 출시 범위는 대체로 190개국으로 고정되어 있으나 일부 문서는 191개국으로 서술한다.
- 과금 모델은 광고 없는 단일 구독 플랜이다.
- 수익 문서의 최신 방향은 금액 절감 주장 대신 실측 카운트, 위험 링크 건수, 탐지 횟수 같은 관측값만 쓰는 것이다.
- 취소 경로를 물리적으로 크게 두고, 무료 체험 이후 자동 전환을 쓰되 과장 문구는 금지하는 방향이 일관되게 보인다.

## 6. 기술 정보

- 현재 설계 축은 멀티모듈 Android + Kotlin + Compose + Hilt + Room(SQLCipher) + Play Billing 조합이다.
- `docs/ARCHITECTURE.md` 기준 코드베이스는 app/core/data/feature의 19모듈 맵을 갖고 있으며 `feature/search-enrichment`는 아직 비어 있다.
- call-intercept 계열 문서는 `CallScreeningService` 기반 실시간 수신 파이프라인, 병렬 증거 수집, notification/action routing을 설명한다.
- decision-engine 계열 문서는 score-based engine을 설명하지만, 일부 README는 최신 governance보다 오래된 risk/category 체계를 들고 있다.
- `docs/03_engineering/DO_NOT_MISS_IMPLEMENTATION.md`와 `IMPORTANCE_AXIS_IMPLEMENTATION.md`는 사용자 후속행동/우선축 설계를 붙인 구현 문서다.
- `docs/05_quality/stage1_push_trash_manual_test.md`와 Stage 1 research 문서는 push-trash, dependency, CI, test 전략으로 설계가 옮겨가는 흔적이다.

## 7. 파일별 1줄 요약

- `docs/ARCHITECTURE.md` — 현 코드 기준 역설계 아키텍처 총괄, 가장 넓은 현재상태 문서
- `docs/02_product/specs/PRD_CALLCHECK_V1.md` — 제품 요구사항 원본, 가격은 §13 및 상단 메타와 **2.49 USD/월**로 정렬됨
- `docs/02_product/specs/GLOBAL_CASE_COVERAGE_V1.md` — 번호 유형별 기대동작과 Unknown ≠ Danger 원칙
- `docs/03_engineering/integration/SPEC_2026-04-14_core_rebuild.md` — 코어 재설계 v1 기준선
- `docs/03_engineering/integration/SPEC_2026-04-14_core_rebuild_v2.md` — 수익 전환 설계 보강본
- `docs/03_engineering/integration/README_IMPLEMENTATION.md` — settings/billing/country-config 구현 묶음 요약
- `docs/03_engineering/integration/INTEGRATION_GUIDE.md` — 통합 가이드
- `docs/03_engineering/integration/IMPLEMENTATION_SUMMARY.md` — 구현 결과 요약
- `docs/03_engineering/index/FILE_INDEX.md` — 파일 인덱스
- `docs/03_engineering/number-profile-label-tag-design.md` — unsaved number relationship memory 설계
- `docs/03_engineering/DO_NOT_MISS_IMPLEMENTATION.md` — 놓치지 않기 사용자 액션 설계
- `docs/03_engineering/IMPORTANCE_AXIS_IMPLEMENTATION.md` — 중요도 축 구현 메모
- `docs/03_engineering/build_reports/P1_BUILD_FIX_REPORT.md` — P1 빌드 환경 수정 리포트
- `docs/03_engineering/build_reports/RINGING_VISIBILITY_REPORT.md` — ringing visibility 검증 리포트
- `docs/04_operations/setup/SETUP_COMPLETE.md` — 프로젝트 초기 구축 완료 선언
- `docs/04_operations/PROJECT_STATUS.md` — 초기 기준 최종 상태 보고
- `docs/04_operations/codex-work-instruction-global-core.md` — Codex 전용 글로벌 코어 지침
- `docs/04_operations/relay_protocol_v1.md` — 도구 간 릴레이 규약
- `docs/05_quality/audit_stage0_hotfix_java17_20260422.md` — Java 17 hotfix 감사
- `docs/05_quality/hygiene_stage0_hotfix_20260422.md` — 저장소 hygiene 정리 보고
- `docs/05_quality/filter_repo_stage0_hotfix_20260422.md` — filter-repo 및 원격 정리 보고
- `docs/05_quality/emulator-validation-checklist.md` — 다음 에뮬레이터 검증 체크리스트
- `docs/05_quality/stage1_push_trash_manual_test.md` — push trash 수동 디바이스 테스트
- `docs/05_quality/test-rig/01-SETUP-GUIDE.md` — 로컬 테스트 리그 셋업 가이드
- `docs/05_quality/reports/VERIFICATION_REPORT.md` — 종합 코드 검증 보고
- `docs/05_quality/reports/REAL_WORLD_VERIFICATION_PLAN.md` — 소규모 실전 검증 계획
- `docs/05_quality/reports/QA_FULL_MATRIX_V1.md` — QA 전수 매트릭스
- `docs/05_quality/reports/architecture_conformance_myphonecheck_base_architecture_v1.md` — base architecture 합치성 점검
- `docs/06_history/2026-04-15-global-core-branchpoint.md` — 글로벌 코어 전환 분기점 기록
- `docs/06_history/archive/2026-04-14-global-single-core-snapshot.md` — 글로벌 단일 코어 스냅샷
- `docs/06_history/archive/2026-04-imports/ARCHITECTURE-constitution.md` — constitution 아키텍처 수입본
- `docs/06_history/archive/2026-04-imports/ARCHITECTURE-executing-OS.md` — executing-OS 아키텍처 수입본
- `docs/06_history/archive/2026-04-imports/ARCHITECTURE-myphonecheck.md` — 과거 myphonecheck 아키텍처 수입본
- `docs/06_history/archive/2026-04-imports/ARCHITECTURE-cardspend.md` — cardspend 아키텍처 수입본
- `docs/06_history/archive/2026-04-imports/synthesis_2026-04-22_pre-stage1.md` — 헌법군 + 신규정보 합성본
- `docs/01_architecture/stage1_research/03_dependency_audit.md` — dependency 및 Stage 1 후보 라이브러리 조사
- `docs/01_architecture/stage1_research/04_test_strategy.md` — L1~L5 테스트 피라미드 초안
- `docs/01_architecture/stage1_research/05_ci_cd_roadmap.md` — 현재 CI와 Stage 2/3 CD 청사진
- `feature/call-intercept/README.md` — call screening 기능 개요
- `feature/call-intercept/QUICK_REFERENCE.md` — call screening 빠른 참조
- `feature/call-intercept/IMPLEMENTATION.md` — call screening 상세 구현 문서
- `feature/call-intercept/DELIVERABLES.md` — call screening 납품 정리
- `feature/decision-engine/DECISION_ENGINE_README.md` — decision engine 설명서, 다만 최신 규약과 일부 충돌
- `feature/decision-engine/IMPLEMENTATION_CHECKLIST.md` — decision engine 구현 체크리스트
- `scripts/local-test-rig/README.md` — 로컬 테스트 리그 3층 운영 가이드
- `docs/07_relay/*` — 현재 세션형 워크오더/보고 레이어. 제품 설계 본문보다는 운영 제어면에 가깝다
- `docs/06_discovery/*` — 인벤토리/발견 요약 산출물. 본 문서와 동급 메타 문서다

## 8. 발견 + 예상 밖

- 오래된 "구현 완료" 문서가 많지만, 최신 governance/patch는 그 위에 다시 제품 원칙을 바꾸고 있다. 즉 구현 문서 신뢰도는 작성 시점에 크게 좌우된다.
- search 전략은 "직접 스크레이핑"과 "Custom Tab 전용" 두 계열이 같은 저장소 안에 공존한다.
- call-intercept와 decision-engine 문서는 완성형 production-ready 톤이 강하지만, 최신 Stage 1 research 문서는 오히려 미완료/재배치 전제를 솔직하게 드러낸다.
- relay protocol 도입으로 이 repo는 순수 제품 repo가 아니라 협업 버스 역할도 겸한다.

## 9. 비전 정독 권장 TOP 5

1. `docs/ARCHITECTURE.md` — 현재 코드 기준 가장 넓은 구조 설명
2. `docs/02_product/specs/PRD_CALLCHECK_V1.md` — 제품 원형 요구사항의 출발점
3. `docs/03_engineering/integration/SPEC_2026-04-14_core_rebuild_v2.md` — 수익/표현/UI 전환의 최신 강한 의도
4. `docs/03_engineering/number-profile-label-tag-design.md` — relationship memory와 fixed search wording 핵심
5. `docs/01_architecture/stage1_research/03_dependency_audit.md` + `04_test_strategy.md` + `05_ci_cd_roadmap.md` — Stage 1 진입 직전의 기술적 현실점검

## 10. Cross-File Conflicts

- **가격 충돌(해소됨, 2026-04-24)**: PRD·연동 요약을 **USD 2.49/month**로 통일. 아카이브·과거 합성본에만 구 문구가 남을 수 있음.
- **PushCheck 정의 충돌**: `docs/ARCHITECTURE.md`, 오래된 spec/README는 Push를 active engine처럼 취급; 최신 patch/governance는 disabled 또는 push-trash 재정의.
- **검색 방식 충돌**: `docs/ARCHITECTURE.md`는 8개 웹 스크레이핑 provider; `SPEC_2026-04-14_core_rebuild_v2.md`는 초기 릴리즈에서 Custom Tab 전용과 스크래핑 금지.
- **판단 주체 충돌**: `feature/call-intercept/README.md`는 allow/reject/block 자동 결정을 강하게 암시; 최신 common principles는 사용자가 최종 결정자.
- **Risk model 충돌**: `feature/decision-engine/DECISION_ENGINE_README.md`는 no history에 risk 가산(+0.2); `GLOBAL_CASE_COVERAGE_V1.md`는 Unknown ≠ Danger로 이를 제거.
- **Category naming 충돌**: `DECISION_ENGINE_README.md`의 `SAFE_KNOWN`, `FRAUD_WARNING` 계열과 PRD/coverage/current governance의 `KNOWN_CONTACT`, `BUSINESS_LIKELY`, `SCAM_RISK_HIGH` 계열이 다르다.
- **국가 수 충돌**: `GLOBAL_CASE_COVERAGE_V1.md`는 191개국, 최신 governance/common principles는 190개국.
- **MessageCheck 위치 충돌**: `docs/ARCHITECTURE.md`는 4개 독립 방어 엔진 톤; latest common principles는 MessageCheck를 독립 엔진이 아닌 CallCheck extension으로 본다.

한 줄 판정: 현재 `myphonecheck` 문서군은 "최신 헌법/거버넌스"와 "과거 구현 완료 문서"가 겹쳐 있는 상태이며, 기준 문서 우선순위를 명확히 두지 않으면 쉽게 충돌한다.
