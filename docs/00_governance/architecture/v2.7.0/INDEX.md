# INDEX — MyPhoneCheck Architecture v2.7.0 (Claude Code)

**총 파일 수**: 78 (v2.6.0 동일)
**원본**: v2.6.0 (3액션 단일 책임 MINOR)
**WO**: WO-V270-CONST-UTF8-ENFORCE
**작업 성격**: PATCH — §9-7 신설 (UTF-8 강제 + UTF-16 영구 금지). 코드 영역 인코딩 정공법 명문화. CI workflow 강제 검증 추가.
**헌법**: 11개 조항 (§9 본문 §9-7 추가, 조항 신설 0)
**선행 보존**: v1.7.1 / v1.8.0 / v1.9.0 / v2.0.0 / v2.1.0 / v2.2.0 / v2.3.0 / v2.4.0 / v2.5.0 / v2.6.0 모두 FROZEN

---

## 전체 문서 맵

```
v2.3.0/
│
├── INDEX.md                              ← 본 파일
├── README.md                             ← 폴더 목적 안내
├── 05_constitution.md                    ← §1 헌법 10조 (v2.0.0 §8조 + v2.2.0 §9·§10 + v2.3.0 §10-6 자체 머지 의무 신설)
├── 30_billing.md                         ← §31 Billing 구현
├── _audit_report.md                      ← 패키지 내부 감사 보조물 (v1.9.0)
│
├── 00_core/
│   ├── README.md
│   ├── 01_primary.md                     ← 비전 작성분 (placeholder)
│   ├── 02_secondary.md                   ← 세 기둥 + Six Surfaces 서문 (실질 콘텐츠)
│   └── 03_tertiary.md                    ← 비전 작성분 (placeholder)
│
├── 06_product_design/
│   ├── README.md
│   ├── 01_goose_vs_egg.md                ← §2
│   ├── 02_golden_egg.md                  ← §3
│   ├── 03_ux_domains.md                  ← §4
│   ├── 04_system_arch.md                 ← §5
│   └── 05_product_strategy.md            ← §17
│
├── 07_engine/
│   ├── README.md
│   ├── 01_three_layer.md                 ← §6
│   ├── 02_self_discovery.md              ← §7
│   ├── 03_nkb.md                         ← §8
│   ├── 04_analyzer.md                    ← §9
│   ├── 05_decision_formula.md            ← §10
│   ├── 06_cold_start.md                  ← §11
│   ├── 07_self_evolution.md              ← §12
│   └── 08_sla.md                         ← §14
│
├── 10_policy/
│   ├── README.md
│   ├── 01_permissions.md                 ← §1 + §24-6
│   ├── 02_sms_mode.md                    ← §2 + §18-4
│   ├── 03_special_access.md              ← §3 (비전 작성분)
│   ├── 04_data_safety.md                 ← §27-1/2/4/5
│   ├── 05_permissions_declaration.md     ← §27-3
│   ├── 06_store_policy.md                ← §33-2
│   └── 07_country_i18n.md               ← §28 cross-ref
│
├── 20_features/
│   ├── README.md
│   ├── 21_call.md                        ← §18-1~3 CallCheck
│   ├── 22_message.md                     ← §18-4 MessageCheck
│   ├── 23_mic.md                         ← §18-6 MicCheck
│   ├── 24_camera.md                      ← §18-7 CameraCheck
│   ├── 25_smoke_scenarios.md             ← §18-1~3 + §18-5/8~10 스모크런·폐기
│   ├── 26_push.md                        ← §26 PushCheck
│   ├── 27_card.md                        ← §27 CardCheck
│   ├── 28_initial_scan.md                ← §28 Initial Scan (v2.0.0 신설)
│   ├── 29_sim_oriented_core.md           ← §29 SIM-Oriented Single Core (v2.0.0 신설, 헌법 §8조)
│   ├── 30_core_global_engine.md          ← §30 :core:global-engine (v2.0.0 신설, v2.5.0 §30-4 검색 2축 정정)
│   ├── 31_realtime_action.md             ← §31 Real-time Action Engine (v2.1.0 신설)
│   └── 32_tag_system.md                  ← §32 Tag System (v2.1.0 신설)
│
├── 40_i18n/
│   ├── README.md
│   ├── 01_country_separation.md          ← §28
│   └── 02_strings_xml.md                 ← §25
│
├── 50_test_infra/
│   ├── README.md
│   ├── 01_test_infra.md                  ← §34
│   ├── 02_smoke_scenarios.md             ← §19 + §20
│   └── 03_permission_matrix.md           ← §34-1
│
├── 60_implementation/
│   ├── README.md
│   ├── 01_day_by_day.md                  ← §24
│   ├── 02_stage0_freeze.md               ← §33-1 + §23 FREEZE
│   ├── 03_interface_injection.md         ← §32
│   ├── 04_memory_budget.md               ← §30
│   ├── 05_repo_layout.md                 ← §23
│   └── 06_ci_cd.md                       ← §26
│
├── 70_business/
│   ├── README.md
│   ├── 01_business_model.md              ← §16
│   └── 02_kpi_mapping.md                 ← §0-B
│
├── 80_verification/
│   ├── README.md
│   ├── 01_dry_run_checklist.md           ← §19
│   ├── 02_success_criteria.md            ← §20
│   ├── 03_open_issues.md                 ← §21
│   ├── 04_round5_consensus.md            ← §22
│   └── 05_central_mapping_zero.md        ← §15
│
├── 90_declarations/
│   └── README.md                         ← v1.8.1 예정
│
├── 95_integration/
│   ├── README.md
│   ├── 01_six_surfaces_integration.md    ← §36 (v1.9.0 rename)
│   └── 02_infrastructure_reference.md    ← §35
│
└── appendix/
    ├── README.md
    ├── A_audit_log.md                    ← 비전 작성분 (placeholder)
    ├── B_patch_history.md                ← 비전 작성분 (placeholder)
    ├── C_limitations.md                  ← 비전 작성분 (placeholder)
    ├── D_version_matrix.md               ← 비전 작성분 (placeholder)
    └── E_vision_record.md                ← §Z
```


---

## 부록: 자체 감사 리포트

`_audit_report.md` — 이전 마이그레이션 라운드에서 보존된 자체 감사 결과.
