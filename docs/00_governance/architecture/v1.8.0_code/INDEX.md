# v1.8.0_code — INDEX (Claude Code 워커 결과물)

**WO**: WO-V180-MIGRATE-002
**워커**: Claude Code
**발행자**: 비전
**작성일**: 2026-04-24 (KST)
**브랜치 예약**: `feature/v180-claudecode`
**파일 경로 루트**: `docs/00_governance/architecture/v1.8.0_code/`

---

## 총계 (실측)

- **총 .md 파일 수**: 68 (이관 본문 55 + README 13)
- **총 크기**: 304,906 B (약 297.76 KB)
- **placeholder 수**: 4 (00_core/01·02·03 + 10_policy/03_special_access)
- **빈 Layer**: 90_declarations (v1.8.1 예정)
- **원본 §0 매핑**: `appendix/A~D.md`로 §0-A~§0-F 분산 이관 (누락 방지)

크기 목표(240 KB ± 30 KB = 210~270 KB) 대비 +27.76 KB 초과.
초과 원인은 본 INDEX § "매핑표 지시 중복 이관 목록" 참조. 감사 리포트 `_audit_report.md` §2.3에 상세.

---

## 전체 문서 맵

### Core & Top-level

- `INDEX.md` (본 파일)
- `README.md` — 본 폴더 목적 안내 (워커·시각·브랜치 명시)
- `_audit_report.md` — Claude Code 특화 무결성 감사 리포트
- `05_constitution.md` — §1 헌법 7조 전문
- `30_billing.md` — §31 Billing 구현 전문

### 00_core/ (Core — 비전 작성분 placeholder)

- `README.md`
- `01_primary.md` (PLACEHOLDER)
- `02_secondary.md` (PLACEHOLDER)
- `03_tertiary.md` (PLACEHOLDER)

### 06_product_design/ (Policy/Design)

- `README.md`
- `01_goose_vs_egg.md` — §2
- `02_golden_egg.md` — §3
- `03_ux_domains.md` — §4
- `04_system_arch.md` — §5
- `05_product_strategy.md` — §17

### 07_engine/ (Engine)

- `README.md`
- `01_three_layer.md` — §6
- `02_self_discovery.md` — §7
- `03_nkb.md` — §8
- `04_analyzer.md` — §9
- `05_decision_formula.md` — §10
- `06_cold_start.md` — §11
- `07_self_evolution.md` — §12
- `08_sla.md` — §14

### 10_policy/ (Policy)

- `README.md`
- `01_permissions.md` — §24-6 Manifest 권한
- `02_sms_mode.md` — §18-4 MessageCheck Mode A/B (중복: `20_features/22_message.md`와 동일 원본)
- `03_special_access.md` (PLACEHOLDER)
- `04_data_safety.md` — §27-1·2·4·5
- `05_permissions_declaration.md` — §27-3
- `06_store_policy.md` — §33-2
- `07_country_i18n.md` — §28 (중복: `40_i18n/01_country_separation.md`와 동일 원본)

### 20_features/ (Feature — Four Surfaces)

- `README.md`
- `21_call.md` — §18-0·1·2·3 (CallCheck)
- `22_message.md` — §18-4 (MessageCheck, Mode A/B)
- `23_mic.md` — §18-6 (MicCheck)
- `24_camera.md` — §18-7 (CameraCheck)
- `25_smoke_scenarios.md` — §18 전문

### 40_i18n/ (i18n)

- `README.md`
- `01_country_separation.md` — §28
- `02_strings_xml.md` — §25

### 50_test_infra/ (Test)

- `README.md`
- `01_test_infra.md` — §34 전문
- `02_smoke_scenarios.md` — §18-0·1·2·3·9·10 + §19 + §20 (테스트 관점)
- `03_permission_matrix.md` — §34-1

### 60_implementation/ (Implementation)

- `README.md`
- `01_day_by_day.md` — §24
- `02_stage0_freeze.md` — §33-1 + §23-4 FREEZE.md 블록
- `03_interface_injection.md` — §32
- `04_memory_budget.md` — §30
- `05_repo_layout.md` — §23 전문
- `06_ci_cd.md` — §26

### 70_business/ (Business)

- `README.md`
- `01_business_model.md` — §16
- `02_kpi_mapping.md` — §0-B (중복: `appendix/A_audit_log.md`와 동일 원본)

### 80_verification/ (Verification)

- `README.md`
- `01_dry_run_checklist.md` — §19
- `02_success_criteria.md` — §20
- `03_open_issues.md` — §21
- `04_round5_consensus.md` — §22
- `05_central_mapping_zero.md` — §15 + "서버 코드·문서 패턴 탐지"

### 90_declarations/ (Declarations — v1.8.1 예정)

- `README.md` (빈 Layer 명시)

### 95_integration/ (Integration)

- `README.md`
- `01_four_surfaces_integration.md` — §36
- `02_infrastructure_reference.md` — §35

### appendix/ (Appendix)

- `README.md`
- `A_audit_log.md` — §0-B 전문
- `B_patch_history.md` — §0-C + §0-E + §0-F
- `C_limitations.md` — §0-D
- `D_version_matrix.md` — §0-A
- `E_vision_record.md` — §Z

---

## 원본 섹션 → 파일 매핑표

| 원본 섹션 | 원본 줄수 | v1.8.0_code/ 내 목적지 |
|---|---|---|
| §0-A 버전 매트릭스 | 45 | `appendix/D_version_matrix.md` |
| §0-B 정직성 감사 로그 | 67 | `appendix/A_audit_log.md` + `70_business/02_kpi_mapping.md` (동일 원본, 중복) |
| §0-C 정책 모니터링 로그 | 12 | `appendix/B_patch_history.md` (§0-E·§0-F와 통합) |
| §0-D 검증 불가 한계 로그 | 15 | `appendix/C_limitations.md` |
| §0-E 빌드 무결성 | 15 | `appendix/B_patch_history.md` |
| §0-F 인프라 운영 참조 | 18 | `appendix/B_patch_history.md` |
| §1 헌법 7조 | 179 | `05_constitution.md` |
| §2 황금알 vs 거위 | 42 | `06_product_design/01_goose_vs_egg.md` |
| §3 황금알 정의 | 77 | `06_product_design/02_golden_egg.md` |
| §4 제품 도메인 | 51 | `06_product_design/03_ux_domains.md` |
| §5 시스템 아키텍처 | 104 | `06_product_design/04_system_arch.md` |
| §6 3계층 소싱 | 72 | `07_engine/01_three_layer.md` |
| §7 Self-Discovery | 128 | `07_engine/02_self_discovery.md` |
| §8 NKB 데이터 설계 | 314 | `07_engine/03_nkb.md` |
| §9 SearchResultAnalyzer | 105 | `07_engine/04_analyzer.md` |
| §10 Decision Engine 수식 | 148 | `07_engine/05_decision_formula.md` |
| §11 Cold Start | 63 | `07_engine/06_cold_start.md` |
| §12 자가 진화 | 50 | `07_engine/07_self_evolution.md` |
| §13 (공백) | — | 이관 대상 아님 |
| §14 SLA 4단계 | 63 | `07_engine/08_sla.md` |
| §15 + 서버 탐지 | 20 + 47 | `80_verification/05_central_mapping_zero.md` |
| §16 비즈니스 모델 | 65 | `70_business/01_business_model.md` |
| §17 제품 전략 | 63 | `06_product_design/05_product_strategy.md` |
| §18 스모크런 + 4 Surface | 617 | `20_features/25_smoke_scenarios.md` (전문) + `20_features/21~24_*.md` (분할) + `50_test_infra/02_smoke_scenarios.md` + `10_policy/02_sms_mode.md` (§18-4) |
| §19 드라이런 체크리스트 | 52 | `80_verification/01_dry_run_checklist.md` + `50_test_infra/02_smoke_scenarios.md` |
| §20 성공 기준 | 37 | `80_verification/02_success_criteria.md` + `50_test_infra/02_smoke_scenarios.md` |
| §21 Open Issues | 44 | `80_verification/03_open_issues.md` |
| §22 Round 5 합의 | 16 | `80_verification/04_round5_consensus.md` |
| §23 프로젝트 구조 + FREEZE | 112 | `60_implementation/05_repo_layout.md` (전문) + `60_implementation/02_stage0_freeze.md` (§23-4 FREEZE.md 블록 재복사) |
| §24 Day-by-Day | 221 | `60_implementation/01_day_by_day.md` + `10_policy/01_permissions.md` (§24-6 재복사) |
| §25 strings.xml | 91 | `40_i18n/02_strings_xml.md` |
| §26 CI/CD | 108 | `60_implementation/06_ci_cd.md` |
| §27 Data Safety + Permissions Declaration | 199 | `10_policy/04_data_safety.md` + `10_policy/05_permissions_declaration.md` |
| §28 국가/언어 분리 | 52 | `40_i18n/01_country_separation.md` + `10_policy/07_country_i18n.md` (중복) |
| §29 (공백) | — | 이관 대상 아님 |
| §30 Memory Budget | 57 | `60_implementation/04_memory_budget.md` |
| §31 Billing 구현 | 249 | `30_billing.md` |
| §32 Interface Injection | 42 | `60_implementation/03_interface_injection.md` |
| §33 Store Policy + Stage 0 | 212 | `10_policy/06_store_policy.md` (§33-2) + `60_implementation/02_stage0_freeze.md` (§33-1) |
| §34 테스트 인프라 | 51 | `50_test_infra/01_test_infra.md` + `50_test_infra/03_permission_matrix.md` (§34-1 재복사) |
| §35 인프라 운영 참조 | 90 | `95_integration/02_infrastructure_reference.md` |
| §36 Four Surfaces 통합 | 149 | `95_integration/01_four_surfaces_integration.md` |
| §Z 비전 작성 기록 | 304 | `appendix/E_vision_record.md` |

---

## 매핑표 지시 중복 이관 목록 (크기 초과 설명)

다음 항목들은 WO §3-1 매핑표가 명시적으로 지시한 중복 이관이다:

| 원본 | 1차 목적지 | 2차 목적지 (중복) | 중복 크기 |
|---|---|---|---|
| §0-B | `appendix/A_audit_log.md` | `70_business/02_kpi_mapping.md` | ≈ 5 KB |
| §18-4 | `20_features/22_message.md` | `10_policy/02_sms_mode.md` | ≈ 9.6 KB |
| §18 전문 | `20_features/25_smoke_scenarios.md` (§18 전체) | `20_features/21_call.md` + `22_message.md` + `23_mic.md` + `24_camera.md` (분할) | ≈ 20 KB |
| §24-6 | `60_implementation/01_day_by_day.md` | `10_policy/01_permissions.md` | ≈ 3.8 KB |
| §28 | `40_i18n/01_country_separation.md` | `10_policy/07_country_i18n.md` | ≈ 2 KB |
| §34-1 | `50_test_infra/01_test_infra.md` | `50_test_infra/03_permission_matrix.md` | ≈ 1.8 KB |
| §23-4 FREEZE.md 블록 | `60_implementation/05_repo_layout.md` | `60_implementation/02_stage0_freeze.md` | < 1 KB |
| §18-1·2·3·9·10 | `20_features/25_smoke_scenarios.md` | `50_test_infra/02_smoke_scenarios.md` | ≈ 4 KB |

**합계**: 약 47 KB 중복. 크기 초과분(+27.76 KB)은 전부 매핑표 지시 중복 + README(≈ 11 KB) + §0 보존 이관(≈ 12 KB)에서 발생.

---

## 의도적 누락 (이관 대상 아님)

| 섹션 | 상태 |
|---|---|
| §13 | 원본 의도적 공백 (v1.5.x 계보 번호 점프) |
| §29 | 원본 의도적 공백 (v1.5.x 계보 번호 정합) |

---

## 표준 헤더 블록

모든 이관 파일 최상단에는 WO §4-4 표준 헤더 블록이 부착되어 있다:

```markdown
# <원본 § 섹션 제목 그대로>

**원본 출처**: v1.7.1 §XX (YY줄)
**v1.8.0 Layer**: <Core | Policy | Feature | Engine | Business | Verification | Integration | Implementation | i18n | Test | Appendix>
**의존**: ...
**변경 이력**: 본 파일은 v1.7.1 §XX 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_code/<path>`

---
```

그 아래부터 원본 텍스트가 한 글자 변경 없이 복사됨.

---

**END OF INDEX**
