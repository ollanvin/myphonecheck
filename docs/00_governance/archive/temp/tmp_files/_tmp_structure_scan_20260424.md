# MPC Structure Scan 20260424

WO: WO-MPC-STRUCTURE-SCAN-001. 로컬 스캔 시각: 2026-04-24 (Cursor). 원격 API는 `repos/ollanvin/myphonecheck` 기본 브랜치(`main`) `contents` 응답 기준.

## 1. 로컬 레포 상태

- **현재 브랜치:** `feature/prd-pricing-patch` (origin과 동기화됨)
- **git status 요약:** 추적 파일 1건 삭제됨(`docs/07_relay/queue/WO-DISCOVERY-004__codex__queue.md`). 추적되지 않은 항목: `_tmp_cursor_inbox.md`, `_tmp_planner.md`, `docs/01_architecture/myphonecheck_integrated_bundle_v1.6.1.md`, `docs/06_discovery/summary_*.md`(2건), `docs/07_relay/done/*DISCOVERY-004*`(2건), `docs/scope-test/`, `myphonecheck-backup-pre-filter-20260422/` 등.
- **최근 커밋 5건:**
  - `9b172e8` docs(prd): update pricing to USD 2.49/month per memory #11
  - `df664ec` fix(queue): WO-DISCOVERY-005 UTF-8 BOM-less LF (was UTF-16 LE)
  - `f6b7bc8` discovery(executing-OS): WO-DISCOVERY-002 — 443 .md 전수 요약
  - `d826041` queue: WO-DISCOVERY-002 in_progress
  - `d80087d` queue: WO-DISCOVERY-002~005 발굴 5축 가동

## 2. 전체 폴더 트리 (3단)

(필터: `.git`, `node_modules`, `build`, `.gradle`, `.idea` 제외. 출력 약 724행.)

```

FullName                                                                                                                                      Mode         KB
--------                                                                                                                                      ----         --
C:\Users\user\Dev\ollanvin\myphonecheck\.claude                                                                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\.git                                                                                                  d--h--        0
C:\Users\user\Dev\ollanvin\myphonecheck\.github                                                                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\.gradle                                                                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\.idea                                                                                                 d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\.kotlin                                                                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\app                                                                                                   d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\build-logic                                                                                           d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core                                                                                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data                                                                                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs                                                                                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature                                                                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\gradle                                                                                                d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs                                                                                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422                                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\scripts                                                                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\tools                                                                                                 d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\.gitattributes                                                                                        -a----      1.2
C:\Users\user\Dev\ollanvin\myphonecheck\.gitignore                                                                                            -a----      0.5
C:\Users\user\Dev\ollanvin\myphonecheck\01_guard_fail.png                                                                                     -a----     25.1
C:\Users\user\Dev\ollanvin\myphonecheck\02_scanning.png                                                                                       -a----     25.1
C:\Users\user\Dev\ollanvin\myphonecheck\03_home_4card.png                                                                                     -a----     25.1
C:\Users\user\Dev\ollanvin\myphonecheck\assembleDebug.log                                                                                     -a----     95.5
C:\Users\user\Dev\ollanvin\myphonecheck\build.gradle.kts                                                                                      -a----      1.7
C:\Users\user\Dev\ollanvin\myphonecheck\check.log                                                                                             -a----    116.7
C:\Users\user\Dev\ollanvin\myphonecheck\gradle.properties                                                                                     -a----      0.5
C:\Users\user\Dev\ollanvin\myphonecheck\gradlew                                                                                               -a----      8.5
C:\Users\user\Dev\ollanvin\myphonecheck\gradlew.bat                                                                                           -a----      2.8
C:\Users\user\Dev\ollanvin\myphonecheck\gradlew_unix                                                                                          -a----      8.5
C:\Users\user\Dev\ollanvin\myphonecheck\ic_launcher_playstore.png                                                                             -a----     32.8
C:\Users\user\Dev\ollanvin\myphonecheck\lint-results-debug.html                                                                               -a----    445.6
C:\Users\user\Dev\ollanvin\myphonecheck\lint-results-debug.txt                                                                                -a----       90
C:\Users\user\Dev\ollanvin\myphonecheck\local.properties                                                                                      -a----      0.1
C:\Users\user\Dev\ollanvin\myphonecheck\local.properties.template                                                                             -a----      0.4
C:\Users\user\Dev\ollanvin\myphonecheck\settings.gradle.kts                                                                                   -a----      1.2
C:\Users\user\Dev\ollanvin\myphonecheck\_tmp_cursor_inbox.md                                                                                  -a----      1.4
C:\Users\user\Dev\ollanvin\myphonecheck\_tmp_planner.md                                                                                       -a----      1.5
C:\Users\user\Dev\ollanvin\myphonecheck\.claude\settings.local.json                                                                           -a----      5.7
C:\Users\user\Dev\ollanvin\myphonecheck\.github\workflows                                                                                     d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\.github\workflows\android-ci.yml                                                                      -a----      0.8
C:\Users\user\Dev\ollanvin\myphonecheck\.github\workflows\contract-freeze-check.yml                                                           -a----      0.6
C:\Users\user\Dev\ollanvin\myphonecheck\.kotlin\errors                                                                                        d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\.kotlin\sessions                                                                                      d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\.kotlin\errors\errors-1776178249167.log                                                               -a----      6.5
C:\Users\user\Dev\ollanvin\myphonecheck\app\build                                                                                             d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\app\src                                                                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\app\store-assets                                                                                      d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\app\build.gradle.kts                                                                                  -a----      4.7
C:\Users\user\Dev\ollanvin\myphonecheck\app\proguard-rules.pro                                                                                -a----      2.4
C:\Users\user\Dev\ollanvin\myphonecheck\app\src\debug                                                                                         d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\app\src\main                                                                                          d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\app\src\debug\kotlin                                                                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\app\src\debug\AndroidManifest.xml                                                                     -a----      0.6
C:\Users\user\Dev\ollanvin\myphonecheck\app\src\main\kotlin                                                                                   d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\app\src\main\res                                                                                      d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\app\src\main\AndroidManifest.xml                                                                      -a----      4.8
C:\Users\user\Dev\ollanvin\myphonecheck\app\store-assets\ic_launcher_512.png                                                                  -a----     32.8
C:\Users\user\Dev\ollanvin\myphonecheck\build-logic\.gradle                                                                                   d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\build-logic\convention                                                                                d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\build-logic\settings.gradle.kts                                                                       -a----      0.5
C:\Users\user\Dev\ollanvin\myphonecheck\build-logic\convention\build                                                                          d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\build-logic\convention\src                                                                            d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\build-logic\convention\build.gradle.kts                                                               -a----        1
C:\Users\user\Dev\ollanvin\myphonecheck\build-logic\convention\src\main                                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core\common                                                                                           d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core\model                                                                                            d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core\security                                                                                         d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core\util                                                                                             d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core\common\build                                                                                     d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core\common\src                                                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core\common\build.gradle.kts                                                                          -a----      0.4
C:\Users\user\Dev\ollanvin\myphonecheck\core\common\FREEZE.md                                                                                 -a----      0.5
C:\Users\user\Dev\ollanvin\myphonecheck\core\common\src\main                                                                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core\common\src\test                                                                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core\model\build                                                                                      d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core\model\src                                                                                        d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core\model\build.gradle.kts                                                                           -a----      1.1
C:\Users\user\Dev\ollanvin\myphonecheck\core\model\proguard-rules.pro                                                                         -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\core\model\src\main                                                                                   d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core\security\build                                                                                   d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core\security\src                                                                                     d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core\security\build.gradle.kts                                                                        -a----      1.3
C:\Users\user\Dev\ollanvin\myphonecheck\core\security\consumer-rules.pro                                                                      -a----        1
C:\Users\user\Dev\ollanvin\myphonecheck\core\security\proguard-rules.pro                                                                      -a----      1.1
C:\Users\user\Dev\ollanvin\myphonecheck\core\security\src\main                                                                                d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core\util\build                                                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core\util\src                                                                                         d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core\util\build.gradle.kts                                                                            -a----      1.2
C:\Users\user\Dev\ollanvin\myphonecheck\core\util\proguard-rules.pro                                                                          -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\core\util\src\main                                                                                    d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\core\util\src\test                                                                                    d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\calllog                                                                                          d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\contacts                                                                                         d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\local-cache                                                                                      d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\search                                                                                           d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\sms                                                                                              d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\calllog\build                                                                                    d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\calllog\src                                                                                      d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\calllog\build.gradle.kts                                                                         -a----      1.4
C:\Users\user\Dev\ollanvin\myphonecheck\data\calllog\proguard-rules.pro                                                                       -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\data\calllog\src\main                                                                                 d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\contacts\build                                                                                   d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\contacts\src                                                                                     d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\contacts\build.gradle.kts                                                                        -a----      1.4
C:\Users\user\Dev\ollanvin\myphonecheck\data\contacts\proguard-rules.pro                                                                      -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\data\contacts\src\main                                                                                d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\local-cache\build                                                                                d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\local-cache\schemas                                                                              d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\local-cache\src                                                                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\local-cache\build.gradle.kts                                                                     -a----      2.4
C:\Users\user\Dev\ollanvin\myphonecheck\data\local-cache\proguard-rules.pro                                                                   -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\data\local-cache\schemas\app.myphonecheck.mobile.data.localcache.db.MyPhoneCheckDatabase              d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\local-cache\src\main                                                                             d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\local-cache\src\test                                                                             d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\search\build                                                                                     d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\search\src                                                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\search\build.gradle.kts                                                                          -a----      2.1
C:\Users\user\Dev\ollanvin\myphonecheck\data\search\proguard-rules.pro                                                                        -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\data\search\src\main                                                                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\search\src\test                                                                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\sms\build                                                                                        d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\sms\src                                                                                          d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\data\sms\build.gradle.kts                                                                             -a----      1.4
C:\Users\user\Dev\ollanvin\myphonecheck\data\sms\proguard-rules.pro                                                                           -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\data\sms\src\main                                                                                     d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance                                                                                    d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture                                                                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product                                                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering                                                                                   d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\04_operations                                                                                    d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality                                                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_discovery                                                                                     d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history                                                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay                                                                                         d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\scope-test                                                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\ARCHITECTURE.md                                                                                  -a----     21.4
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance\patches                                                                            d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance\__pycache__                                                                        d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance\docs-index.md                                                                      -a----      3.1
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance\project-governance.md                                                              -a----        9
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance\_workorder_stage0_hotfix_java17_e3b05e.txt                                         -a----     10.1
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance\patches\PATCH_v1.7.md                                                              -a----      0.9
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance\__pycache__\build_architecture_v152.cpython-314.pyc                                -a----     36.2
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance\__pycache__\md_normalizer.cpython-314.pyc                                          -a----      3.3
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\legacy                                                                           d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\stage1_research                                                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\global-single-core-system.md                                                     -a----      3.1
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\myphonecheck_base_architecture_v1.md                                             -a----      8.3
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\myphonecheck_integrated_bundle_v1.6.1.md                                         -a----    323.1
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\legacy\ARCHITECTURE_ONDEVICE_SEARCH.md                                           -a----      4.6
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\legacy\SECURITY_ARCHITECTURE_V1.docx                                             -a----     18.1
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\stage1_research\01_current_state.md                                              -a----      9.5
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\stage1_research\02_module_candidates.md                                          -a----      7.9
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\stage1_research\03_dependency_audit.md                                           -a----     10.3
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\stage1_research\04_test_strategy.md                                              -a----      6.6
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\stage1_research\05_ci_cd_roadmap.md                                              -a----      7.6
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\assets                                                                                d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\specs                                                                                 d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\assets\callcheck_icon_1024.png                                                        -a----    614.8
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\assets\icon_A_phone_signal.png                                                        -a----     18.2
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\assets\icon_B_phone_check.png                                                         -a----     16.5
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\assets\icon_C_phone_radar.png                                                         -a----     36.2
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\assets\icon_plans_comparison.png                                                      -a----     45.1
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\assets\icon_plan_a.png                                                                -a----     26.5
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\assets\icon_plan_b.png                                                                -a----     21.8
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\assets\icon_plan_c.png                                                                -a----     22.9
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\specs\GLOBAL_CASE_COVERAGE_V1.md                                                      -a----     10.2
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\specs\PRD_CALLCHECK_V1.md                                                             -a----       10
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\build_reports                                                                     d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\index                                                                             d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\integration                                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\DO_NOT_MISS_IMPLEMENTATION.md                                                     -a----      6.9
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\IMPORTANCE_AXIS_IMPLEMENTATION.md                                                 -a----      9.3
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\number-profile-label-tag-design.md                                                -a----      1.7
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\build_reports\P1_BUILD_FIX_REPORT.md                                              -a----      5.3
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\build_reports\RINGING_VISIBILITY_REPORT.md                                        -a----      7.8
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\index\FILES_MANIFEST.txt                                                          -a----      9.3
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\index\FILE_INDEX.md                                                               -a----      7.1
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\integration\IMPLEMENTATION_SUMMARY.md                                             -a----     14.8
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\integration\INTEGRATION_GUIDE.md                                                  -a----      9.3
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\integration\README_IMPLEMENTATION.md                                              -a----        9
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\integration\SPEC_2026-04-14_core_rebuild.md                                       -a----     29.3
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\integration\SPEC_2026-04-14_core_rebuild_v2.md                                    -a----     18.4
C:\Users\user\Dev\ollanvin\myphonecheck\docs\04_operations\setup                                                                              d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\04_operations\codex-work-instruction-global-core.md                                              -a----      1.5
C:\Users\user\Dev\ollanvin\myphonecheck\docs\04_operations\PROJECT_STATUS.md                                                                  -a----     18.7
C:\Users\user\Dev\ollanvin\myphonecheck\docs\04_operations\relay_protocol_v1.md                                                               -a----      6.2
C:\Users\user\Dev\ollanvin\myphonecheck\docs\04_operations\setup\SETUP_COMPLETE.md                                                            -a----      8.1
C:\Users\user\Dev\ollanvin\myphonecheck\docs\04_operations\setup\setup_github.bat                                                             -a----      7.2
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\reports                                                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\test-rig                                                                              d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\audit_stage0_hotfix_java17_20260422.md                                                -a----     19.2
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\emulator-validation-checklist.md                                                      -a----      1.2
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\filter_repo_stage0_hotfix_20260422.md                                                 -a----     11.6
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\hygiene_stage0_hotfix_20260422.md                                                     -a----     14.9
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\stage1_push_trash_manual_test.md                                                      -a----        1
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\reports\architecture_conformance_myphonecheck_base_architecture_v1.md                 -a----       29
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\reports\QA_FULL_MATRIX_V1.md                                                          -a----      6.7
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\reports\REAL_WORLD_VERIFICATION_PLAN.md                                               -a----        8
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\reports\VERIFICATION_REPORT.md                                                        -a----      6.6
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\test-rig\01-SETUP-GUIDE.md                                                            -a----      1.8
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\test-rig\02-auto-test.ps1                                                             -a----      8.4
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\test-rig\03-run-test.bat                                                              -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_discovery\inventory_cursor_myphonecheck.md                                                    -a----   2621.7
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_discovery\summary_executing-OS.md                                                             -a----     15.3
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_discovery\summary_myphonecheck_full.md                                                        -a----     14.4
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_discovery\summary_web.md                                                                      -a----      5.2
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive                                                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\2026-04-15-global-core-branchpoint.md                                                 -a----      1.7
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports                                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-14-global-single-core-snapshot.md                                     -a----      3.3
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\COMPLETION_REPORT.txt                                                         -a----     13.9
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done                                                                                    d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\failed                                                                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\in_progress                                                                             d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\needs_approval                                                                          d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\queue                                                                                   d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\.gitkeep                                                                                -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\.gitkeep                                                                           -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\REPORT-WO-AUDIT-001__codex__done.md                                                -a----      4.4
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\REPORT-WO-CLEANUP-003__claudecode__done.md                                         -a----      3.9
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\REPORT-WO-DISCOVERY-004__codex__done.md                                            -a----      1.5
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\REPORT-WO-GOV-005__claudecode__done.md                                             -a----      2.1
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\REPORT-WO-RELAY-001__claudecode__done.md                                           -a----      0.6
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\REPORT-WO-STAGE1-001__cursor__done.md                                              -a----      0.8
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\scripts_folder_snapshot_20260422.txt                                               -a----      2.1
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\TECH-VERIFICATION-NLS.md                                                           -a----      3.4
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\WO-AUDIT-001__codex__done.md                                                       -a----        4
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\WO-CLEANUP-003__claudecode__done.md                                                -a----      7.2
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\WO-DISCOVERY-002__cursor__done.md                                                  -a----      3.5
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\WO-DISCOVERY-004__codex__done.md                                                   -a----      4.7
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\WO-GOV-005__claudecode__done.md                                                    -a----       10
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\WO-STAGE1-001__cursor__done.md                                                     -a----     13.5
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\failed\.gitkeep                                                                         -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\in_progress\.gitkeep                                                                    -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\needs_approval\.gitkeep                                                                 -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\needs_approval\ALERT-WO-AUDIT-001-FAILURES.md                                           -a----      1.2
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\queue\.gitkeep                                                                          -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\queue\WO-DISCOVERY-003__claudecode__queue.md                                            -a----      2.2
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\queue\WO-DISCOVERY-005__cowork__queue.md                                                -a----      3.2
C:\Users\user\Dev\ollanvin\myphonecheck\docs\scope-test\CURSOR-SCOPE-REPORT-001.md                                                            -a----      5.2
C:\Users\user\Dev\ollanvin\myphonecheck\feature\billing                                                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\call-intercept                                                                                d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\country-config                                                                                d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\decision-engine                                                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\decision-ui                                                                                   d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\device-evidence                                                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\message-intercept                                                                             d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\privacy-check                                                                                 d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\push-trash                                                                                    d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\search-enrichment                                                                             d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\settings                                                                                      d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\billing\build                                                                                 d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\billing\src                                                                                   d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\billing\build.gradle.kts                                                                      -a----      2.1
C:\Users\user\Dev\ollanvin\myphonecheck\feature\billing\proguard-rules.pro                                                                    -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\feature\billing\src\main                                                                              d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\call-intercept\build                                                                          d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\call-intercept\src                                                                            d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\call-intercept\build.gradle.kts                                                               -a----      1.7
C:\Users\user\Dev\ollanvin\myphonecheck\feature\call-intercept\DELIVERABLES.md                                                                -a----      9.6
C:\Users\user\Dev\ollanvin\myphonecheck\feature\call-intercept\IMPLEMENTATION.md                                                              -a----     11.3
C:\Users\user\Dev\ollanvin\myphonecheck\feature\call-intercept\proguard-rules.pro                                                             -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\feature\call-intercept\QUICK_REFERENCE.md                                                             -a----       11
C:\Users\user\Dev\ollanvin\myphonecheck\feature\call-intercept\README.md                                                                      -a----      8.4
C:\Users\user\Dev\ollanvin\myphonecheck\feature\call-intercept\src\main                                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\country-config\build                                                                          d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\country-config\src                                                                            d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\country-config\build.gradle.kts                                                               -a----      1.6
C:\Users\user\Dev\ollanvin\myphonecheck\feature\country-config\proguard-rules.pro                                                             -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\feature\country-config\src\main                                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\country-config\src\test                                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\decision-engine\build                                                                         d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\decision-engine\src                                                                           d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\decision-engine\build.gradle.kts                                                              -a----      1.7
C:\Users\user\Dev\ollanvin\myphonecheck\feature\decision-engine\DECISION_ENGINE_README.md                                                     -a----        8
C:\Users\user\Dev\ollanvin\myphonecheck\feature\decision-engine\IMPLEMENTATION_CHECKLIST.md                                                   -a----      8.2
C:\Users\user\Dev\ollanvin\myphonecheck\feature\decision-engine\proguard-rules.pro                                                            -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\feature\decision-engine\src\main                                                                      d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\decision-engine\src\test                                                                      d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\decision-ui\build                                                                             d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\decision-ui\src                                                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\decision-ui\build.gradle.kts                                                                  -a----      1.9
C:\Users\user\Dev\ollanvin\myphonecheck\feature\decision-ui\proguard-rules.pro                                                                -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\feature\decision-ui\src\main                                                                          d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\device-evidence\build                                                                         d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\device-evidence\src                                                                           d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\device-evidence\build.gradle.kts                                                              -a----      1.5
C:\Users\user\Dev\ollanvin\myphonecheck\feature\device-evidence\proguard-rules.pro                                                            -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\feature\device-evidence\src\main                                                                      d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\message-intercept\build                                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\message-intercept\src                                                                         d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\message-intercept\build.gradle.kts                                                            -a----      1.5
C:\Users\user\Dev\ollanvin\myphonecheck\feature\message-intercept\proguard-rules.pro                                                          -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\feature\message-intercept\src\main                                                                    d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\privacy-check\build                                                                           d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\privacy-check\src                                                                             d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\privacy-check\build.gradle.kts                                                                -a----      1.4
C:\Users\user\Dev\ollanvin\myphonecheck\feature\privacy-check\proguard-rules.pro                                                              -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\feature\privacy-check\src\main                                                                        d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\push-trash\build                                                                              d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\push-trash\src                                                                                d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\push-trash\build.gradle.kts                                                                   -a----      1.8
C:\Users\user\Dev\ollanvin\myphonecheck\feature\push-trash\proguard-rules.pro                                                                 -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\push-trash\src\main                                                                           d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\push-trash\src\test                                                                           d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\search-enrichment\build                                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\search-enrichment\src                                                                         d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\search-enrichment\build.gradle.kts                                                            -a----      1.5
C:\Users\user\Dev\ollanvin\myphonecheck\feature\search-enrichment\proguard-rules.pro                                                          -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\feature\search-enrichment\src\main                                                                    d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\settings\build                                                                                d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\settings\src                                                                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\feature\settings\build.gradle.kts                                                                     -a----      1.9
C:\Users\user\Dev\ollanvin\myphonecheck\feature\settings\proguard-rules.pro                                                                   -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\feature\settings\src\main                                                                             d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\gradle\wrapper                                                                                        d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\gradle\libs.versions.toml                                                                             -a----      6.6
C:\Users\user\Dev\ollanvin\myphonecheck\gradle\wrapper\gradle-wrapper.jar                                                                     -a----     42.4
C:\Users\user\Dev\ollanvin\myphonecheck\gradle\wrapper\gradle-wrapper.properties                                                              -a----      0.2
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene                                                                                          d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\ci_android_lastcommit.txt                                                                        -a----      0.4
C:\Users\user\Dev\ollanvin\myphonecheck\logs\ci_freeze_allhistory.txt                                                                         -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\ci_freeze_decoded.txt                                                                            -a----      0.6
C:\Users\user\Dev\ollanvin\myphonecheck\logs\ci_freeze_java_scan.txt                                                                          -a----      0.1
C:\Users\user\Dev\ollanvin\myphonecheck\logs\ci_freeze_lastcommit.txt                                                                         -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\ci_java_version.txt                                                                              -a----      0.1
C:\Users\user\Dev\ollanvin\myphonecheck\logs\ci_java_version_v2.txt                                                                           -a----      0.1
C:\Users\user\Dev\ollanvin\myphonecheck\logs\ci_status.txt                                                                                    -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\ci_tracked_files.txt                                                                             -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\compileDebug_nodaemon.txt                                                                        -a----    177.3
C:\Users\user\Dev\ollanvin\myphonecheck\logs\core_common_clean.txt                                                                            -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\core_common_clean2.txt                                                                           -a----      0.5
C:\Users\user\Dev\ollanvin\myphonecheck\logs\core_common_compile_info.txt                                                                     -a----     64.5
C:\Users\user\Dev\ollanvin\myphonecheck\logs\core_common_diff_1commit.txt                                                                     -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\core_common_diff_working.txt                                                                     -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\core_common_git_history.txt                                                                      -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\core_common_kapt_plugin.txt                                                                      -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\core_common_kapt_scan.txt                                                                        -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\core_common_status.txt                                                                           -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\core_common_tasks.txt                                                                            -a----      3.4
C:\Users\user\Dev\ollanvin\myphonecheck\logs\core_common_test.txt                                                                             -a----     66.1
C:\Users\user\Dev\ollanvin\myphonecheck\logs\core_common_tracked.txt                                                                          -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\file_buildlogic_convention_build.gradle.kts.txt                                                  -a----        1
C:\Users\user\Dev\ollanvin\myphonecheck\logs\file_buildlogic_settings.gradle.kts.txt                                                          -a----      0.5
C:\Users\user\Dev\ollanvin\myphonecheck\logs\file_core_common_build.gradle.kts.txt                                                            -a----      0.4
C:\Users\user\Dev\ollanvin\myphonecheck\logs\file_gradle.properties.txt                                                                       -a----      0.5
C:\Users\user\Dev\ollanvin\myphonecheck\logs\file_root_build.gradle.kts.txt                                                                   -a----      1.7
C:\Users\user\Dev\ollanvin\myphonecheck\logs\file_settings.gradle.kts.txt                                                                     -a----      1.2
C:\Users\user\Dev\ollanvin\myphonecheck\logs\foojay_check.txt                                                                                 -a----      0.2
C:\Users\user\Dev\ollanvin\myphonecheck\logs\freeze_hash.txt                                                                                  -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\logs\git_head.txt                                                                                     -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\git_log.txt                                                                                      -a----      0.4
C:\Users\user\Dev\ollanvin\myphonecheck\logs\git_status.txt                                                                                   -a----      5.3
C:\Users\user\Dev\ollanvin\myphonecheck\logs\governance_update.txt                                                                            -a----      0.7
C:\Users\user\Dev\ollanvin\myphonecheck\logs\gradlew_stop.txt                                                                                 -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\gradlew_stop_pre6.txt                                                                            -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\gradle_properties_keys.txt                                                                       -a----      0.2
C:\Users\user\Dev\ollanvin\myphonecheck\logs\grep_jvmtarget17.txt                                                                             -a----      1.2
C:\Users\user\Dev\ollanvin\myphonecheck\logs\grep_jvmtarget_old.txt                                                                           -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\grep_toolchain17.txt                                                                             -a----      0.2
C:\Users\user\Dev\ollanvin\myphonecheck\logs\grep_toolchain21_leftover.txt                                                                    -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\grep_version17.txt                                                                               -a----      3.6
C:\Users\user\Dev\ollanvin\myphonecheck\logs\grep_version_old.txt                                                                             -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\javaToolchains.txt                                                                               -a----      0.7
C:\Users\user\Dev\ollanvin\myphonecheck\logs\java_processes_before.txt                                                                        -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\lckfiles_before.txt                                                                              -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\lockfiles_before.txt                                                                             -a----      0.7
C:\Users\user\Dev\ollanvin\myphonecheck\logs\openfiles_scan.txt                                                                               -a----      0.6
C:\Users\user\Dev\ollanvin\myphonecheck\logs\runtime_cp.txt                                                                                   -a----      1.7
C:\Users\user\Dev\ollanvin\myphonecheck\logs\runtime_cp_android_scan.txt                                                                      -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\test_results_xml.txt                                                                             -a----      0.4
C:\Users\user\Dev\ollanvin\myphonecheck\logs\toolchain_trace.txt                                                                              -a----        2
C:\Users\user\Dev\ollanvin\myphonecheck\logs\version_agp_kotlin.txt                                                                           -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\version_gradle.txt                                                                               -a----      0.1
C:\Users\user\Dev\ollanvin\myphonecheck\logs\version_matrix.md                                                                                -a----      0.9
C:\Users\user\Dev\ollanvin\myphonecheck\logs\workorder_hash.txt                                                                               -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\branch_status.txt                                                                        -a----      0.4
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\build_artifact_leak.txt                                                                  -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\categoryA_add.txt                                                                        -a----      0.9
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\categoryC_leak_check.txt                                                                 -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\classification.md                                                                        -a----     12.8
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\core_common_add.txt                                                                      -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\core_common_inventory.txt                                                                -a----      1.6
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\core_common_staged.txt                                                                   -a----        1
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\freeze_yml_add.txt                                                                       -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\freeze_yml_bom_check.txt                                                                 -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\freeze_yml_staged.txt                                                                    -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\freeze_yml_syntax.txt                                                                    -a----      0.6
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\gitignore_add.txt                                                                        -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\gitignore_core_check.txt                                                                 -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\gitignore_logs_check.txt                                                                 -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\git_fetch.txt                                                                            -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\git_head_before.txt                                                                      -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\git_head_final.txt                                                                       -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\git_log_before.txt                                                                       -a----      0.7
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\git_status_before.txt                                                                    -a----      6.1
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\git_status_final.txt                                                                     -a----      0.6
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\git_status_porcelain_before.txt                                                          -a----      5.2
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\hprof_history.txt                                                                        -a----      0.2
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\logs_add.txt                                                                             -a----      6.1
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\logs_hygiene_add.txt                                                                     -a----      0.5
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\modified_files.txt                                                                       -a----      2.8
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\push_result.txt                                                                          -a----      1.2
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\remote_sync_before.txt                                                                   -a----      5.3
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\staged_files.txt                                                                         -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\staged_final.txt                                                                         -a----      7.6
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\stash_contents.txt                                                                       -a----        9
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\stash_list.txt                                                                           -a----      0.1
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\untracked_after_stage.txt                                                                -a----        2
C:\Users\user\Dev\ollanvin\myphonecheck\logs\hygiene\untracked_files.txt                                                                      -a----      4.9
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\.claude                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\.git                                                          d--h--        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\.github                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\.gradle                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\.idea                                                         d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\.kotlin                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\app                                                           d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\build-logic                                                   d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core                                                          d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data                                                          d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs                                                          d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\gradle                                                        d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs                                                          d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\scripts                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\.gitattributes                                                -a----      1.2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\.gitignore                                                    -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\01_guard_fail.png                                             -a----     25.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\02_scanning.png                                               -a----     25.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\03_home_4card.png                                             -a----     25.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\assembleDebug.log                                             -a----     95.5
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\build.gradle.kts                                              -a----      1.7
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\check.log                                                     -a----    116.7
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\gradle.properties                                             -a----      0.5
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\gradlew                                                       -a----      8.5
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\gradlew.bat                                                   -a----      2.8
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\gradlew_unix                                                  -a----      8.5
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\ic_launcher_playstore.png                                     -a----     32.8
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\java_pid384076.hprof                                          -a---- 713249.2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\java_pid397536.hprof                                          -a---- 709495.6
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\java_pid399920.hprof                                          -a---- 705238.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\java_pid415016.hprof                                          -a---- 708980.5
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\lint-results-debug.html                                       -a----    445.6
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\lint-results-debug.txt                                        -a----       90
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\local.properties                                              -a----      0.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\local.properties.template                                     -a----      0.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\settings.gradle.kts                                           -a----      1.2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\.claude\settings.local.json                                   -a----      5.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\.github\workflows                                             d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\.github\workflows\android-ci.yml                              -a----      0.8
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\.github\workflows\contract-freeze-check.yml                   -a----      0.6
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\.kotlin\errors                                                d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\.kotlin\sessions                                              d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\.kotlin\errors\errors-1776178249167.log                       -a----      6.5
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\app\build                                                     d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\app\src                                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\app\store-assets                                              d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\app\build.gradle.kts                                          -a----      4.6
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\app\proguard-rules.pro                                        -a----      2.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\app\src\debug                                                 d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\app\src\main                                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\app\store-assets\ic_launcher_512.png                          -a----     32.8
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\build-logic\.gradle                                           d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\build-logic\convention                                        d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\build-logic\settings.gradle.kts                               -a----      0.5
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\build-logic\convention\build                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\build-logic\convention\src                                    d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\build-logic\convention\build.gradle.kts                       -a----        1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\common                                                   d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\model                                                    d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\security                                                 d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\util                                                     d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\common\build                                             d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\common\src                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\common\build.gradle.kts                                  -a----      0.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\common\FREEZE.md                                         -a----      0.5
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\model\build                                              d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\model\src                                                d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\model\build.gradle.kts                                   -a----      1.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\model\proguard-rules.pro                                 -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\security\build                                           d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\security\src                                             d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\security\build.gradle.kts                                -a----      1.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\security\consumer-rules.pro                              -a----      0.9
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\security\proguard-rules.pro                              -a----      1.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\util\build                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\util\src                                                 d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\util\build.gradle.kts                                    -a----      1.2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\core\util\proguard-rules.pro                                  -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\calllog                                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\contacts                                                 d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\local-cache                                              d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\search                                                   d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\sms                                                      d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\calllog\build                                            d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\calllog\src                                              d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\calllog\build.gradle.kts                                 -a----      1.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\calllog\proguard-rules.pro                               -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\contacts\build                                           d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\contacts\src                                             d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\contacts\build.gradle.kts                                -a----      1.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\contacts\proguard-rules.pro                              -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\local-cache\build                                        d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\local-cache\schemas                                      d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\local-cache\src                                          d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\local-cache\build.gradle.kts                             -a----      1.7
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\local-cache\proguard-rules.pro                           -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\search\build                                             d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\search\src                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\search\build.gradle.kts                                  -a----      2.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\search\proguard-rules.pro                                -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\sms\build                                                d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\sms\src                                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\sms\build.gradle.kts                                     -a----      1.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\data\sms\proguard-rules.pro                                   -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\00_governance                                            d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\01_architecture                                          d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\02_product                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\03_engineering                                           d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\04_operations                                            d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\05_quality                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\06_history                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\ARCHITECTURE.md                                          -a----     21.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\00_governance\__pycache__                                d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\00_governance\docs-index.md                              -a----      3.2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\00_governance\project-governance.md                      -a----      8.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\00_governance\_workorder_stage0_hotfix_java17_e3b05e.txt -a----     10.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\01_architecture\legacy                                   d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\01_architecture\global-single-core-system.md             -a----      2.2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\01_architecture\myphonecheck_base_architecture_v1.md     -a----      6.9
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\02_product\assets                                        d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\02_product\specs                                         d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\03_engineering\build_reports                             d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\03_engineering\index                                     d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\03_engineering\integration                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\03_engineering\DO_NOT_MISS_IMPLEMENTATION.md             -a----      7.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\03_engineering\IMPORTANCE_AXIS_IMPLEMENTATION.md         -a----      9.6
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\03_engineering\number-profile-label-tag-design.md        -a----      1.7
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\04_operations\setup                                      d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\04_operations\codex-work-instruction-global-core.md      -a----      1.5
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\04_operations\PROJECT_STATUS.md                          -a----     19.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\05_quality\reports                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\05_quality\test-rig                                      d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\05_quality\audit_stage0_hotfix_java17_20260422.md        -a----     19.2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\05_quality\emulator-validation-checklist.md              -a----      1.2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\05_quality\hygiene_stage0_hotfix_20260422.md             -a----     14.9
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\06_history\archive                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\06_history\2026-04-15-global-core-branchpoint.md         -a----      1.7
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\billing                                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\call-intercept                                        d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\country-config                                        d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\decision-engine                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\decision-ui                                           d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\device-evidence                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\message-intercept                                     d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\privacy-check                                         d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\push-intercept                                        d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\search-enrichment                                     d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\settings                                              d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\billing\build                                         d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\billing\src                                           d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\billing\build.gradle.kts                              -a----      2.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\billing\proguard-rules.pro                            -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\call-intercept\build                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\call-intercept\src                                    d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\call-intercept\build.gradle.kts                       -a----      1.7
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\call-intercept\DELIVERABLES.md                        -a----      9.6
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\call-intercept\IMPLEMENTATION.md                      -a----     11.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\call-intercept\proguard-rules.pro                     -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\call-intercept\QUICK_REFERENCE.md                     -a----       11
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\call-intercept\README.md                              -a----      8.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\country-config\build                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\country-config\src                                    d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\country-config\build.gradle.kts                       -a----      1.6
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\country-config\proguard-rules.pro                     -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\decision-engine\build                                 d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\decision-engine\src                                   d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\decision-engine\build.gradle.kts                      -a----      1.7
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\decision-engine\DECISION_ENGINE_README.md             -a----        8
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\decision-engine\IMPLEMENTATION_CHECKLIST.md           -a----      8.2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\decision-engine\proguard-rules.pro                    -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\decision-ui\build                                     d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\decision-ui\src                                       d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\decision-ui\build.gradle.kts                          -a----      1.9
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\decision-ui\proguard-rules.pro                        -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\device-evidence\build                                 d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\device-evidence\src                                   d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\device-evidence\build.gradle.kts                      -a----      1.5
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\device-evidence\proguard-rules.pro                    -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\message-intercept\build                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\message-intercept\src                                 d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\message-intercept\build.gradle.kts                    -a----      1.5
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\message-intercept\proguard-rules.pro                  -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\privacy-check\build                                   d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\privacy-check\src                                     d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\privacy-check\build.gradle.kts                        -a----      1.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\privacy-check\proguard-rules.pro                      -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\push-intercept\src                                    d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\push-intercept\build.gradle.kts                       -a----      1.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\push-intercept\proguard-rules.pro                     -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\search-enrichment\build                               d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\search-enrichment\src                                 d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\search-enrichment\build.gradle.kts                    -a----      1.5
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\search-enrichment\proguard-rules.pro                  -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\settings\build                                        d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\settings\src                                          d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\settings\build.gradle.kts                             -a----      1.9
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\feature\settings\proguard-rules.pro                           -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\gradle\wrapper                                                d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\gradle\libs.versions.toml                                     -a----      6.6
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\gradle\wrapper\gradle-wrapper.jar                             -a----     42.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\gradle\wrapper\gradle-wrapper.properties                      -a----      0.2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\filter                                                   d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene                                                  d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\ci_android_lastcommit.txt                                -a----      0.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\ci_freeze_allhistory.txt                                 -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\ci_freeze_decoded.txt                                    -a----      0.6
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\ci_freeze_java_scan.txt                                  -a----      0.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\ci_freeze_lastcommit.txt                                 -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\ci_java_version.txt                                      -a----      0.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\ci_java_version_v2.txt                                   -a----      0.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\ci_status.txt                                            -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\ci_tracked_files.txt                                     -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\compileDebug_nodaemon.txt                                -a----    177.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\core_common_clean.txt                                    -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\core_common_clean2.txt                                   -a----      0.5
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\core_common_compile_info.txt                             -a----     64.5
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\core_common_diff_1commit.txt                             -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\core_common_diff_working.txt                             -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\core_common_git_history.txt                              -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\core_common_kapt_plugin.txt                              -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\core_common_kapt_scan.txt                                -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\core_common_status.txt                                   -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\core_common_tasks.txt                                    -a----      3.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\core_common_test.txt                                     -a----     66.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\core_common_tracked.txt                                  -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\file_buildlogic_convention_build.gradle.kts.txt          -a----        1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\file_buildlogic_settings.gradle.kts.txt                  -a----      0.5
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\file_core_common_build.gradle.kts.txt                    -a----      0.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\file_gradle.properties.txt                               -a----      0.5
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\file_root_build.gradle.kts.txt                           -a----      1.7
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\file_settings.gradle.kts.txt                             -a----      1.2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\foojay_check.txt                                         -a----      0.2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\freeze_hash.txt                                          -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\git_head.txt                                             -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\git_log.txt                                              -a----      0.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\git_status.txt                                           -a----      5.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\governance_update.txt                                    -a----      0.7
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\gradlew_stop.txt                                         -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\gradlew_stop_pre6.txt                                    -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\gradle_properties_keys.txt                               -a----      0.2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\grep_jvmtarget17.txt                                     -a----      1.2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\grep_jvmtarget_old.txt                                   -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\grep_toolchain17.txt                                     -a----      0.2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\grep_toolchain21_leftover.txt                            -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\grep_version17.txt                                       -a----      3.6
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\grep_version_old.txt                                     -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\javaToolchains.txt                                       -a----      0.7
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\java_processes_before.txt                                -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\lckfiles_before.txt                                      -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\lockfiles_before.txt                                     -a----      0.7
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\openfiles_scan.txt                                       -a----      0.6
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\runtime_cp.txt                                           -a----      1.7
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\runtime_cp_android_scan.txt                              -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\test_results_xml.txt                                     -a----      0.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\toolchain_trace.txt                                      -a----        2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\version_agp_kotlin.txt                                   -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\version_gradle.txt                                       -a----      0.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\version_matrix.md                                        -a----      0.9
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\workorder_hash.txt                                       -a----      0.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\filter\robocopy.txt                                      -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\branch_status.txt                                -a----      0.4
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\build_artifact_leak.txt                          -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\categoryA_add.txt                                -a----      0.9
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\categoryC_leak_check.txt                         -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\classification.md                                -a----     12.8
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\core_common_add.txt                              -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\core_common_inventory.txt                        -a----      1.6
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\core_common_staged.txt                           -a----        1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\freeze_yml_add.txt                               -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\freeze_yml_bom_check.txt                         -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\freeze_yml_staged.txt                            -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\freeze_yml_syntax.txt                            -a----      0.6
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\gitignore_add.txt                                -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\gitignore_core_check.txt                         -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\gitignore_logs_check.txt                         -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\git_fetch.txt                                    -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\git_head_before.txt                              -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\git_head_final.txt                               -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\git_log_before.txt                               -a----      0.7
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\git_status_before.txt                            -a----      6.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\git_status_final.txt                             -a----      0.6
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\git_status_porcelain_before.txt                  -a----      5.2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\hprof_history.txt                                -a----      0.2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\logs_add.txt                                     -a----      6.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\logs_hygiene_add.txt                             -a----      0.5
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\modified_files.txt                               -a----      2.8
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\push_result.txt                                  -a----      1.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\remote_sync_before.txt                           -a----      5.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\staged_files.txt                                 -a----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\staged_final.txt                                 -a----      7.8
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\stash_contents.txt                               -a----      9.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\stash_list.txt                                   -a----      0.1
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\untracked_after_stage.txt                        -a----        2
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\logs\hygiene\untracked_files.txt                              -a----      4.9
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\scripts\local-test-rig                                        d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\scripts\local-test-rig\README.md                              -a----      1.6
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\scripts\local-test-rig\run_emulator_myphonecheck.ps1          -a----      9.3
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\scripts\local-test-rig\run_frozen_tests.ps1                   -a----      2.8
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\scripts\local-test-rig\run_live_validation.ps1                -a----     12.9
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\scripts\local-test-rig\run_provider_capture.ps1               -a----      6.6
C:\Users\user\Dev\ollanvin\myphonecheck\scripts\local-test-rig                                                                                d-----        0
C:\Users\user\Dev\ollanvin\myphonecheck\scripts\local-test-rig\README.md                                                                      -a----      1.6
C:\Users\user\Dev\ollanvin\myphonecheck\scripts\local-test-rig\run_emulator_myphonecheck.ps1                                                  -a----      9.3
C:\Users\user\Dev\ollanvin\myphonecheck\scripts\local-test-rig\run_frozen_tests.ps1                                                           -a----      2.8
C:\Users\user\Dev\ollanvin\myphonecheck\scripts\local-test-rig\run_live_validation.ps1                                                        -a----     12.9
C:\Users\user\Dev\ollanvin\myphonecheck\scripts\local-test-rig\run_provider_capture.ps1                                                       -a----      6.6



```

## 3. docs 폴더 내용 (전 파일)

**항목 수(파일+디렉터리 재귀):** 128

```

FullName                                                                                                                          KB LastWriteTime         
--------                                                                                                                          -- -------------         
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance                                                                         0 2026-04-23 오전 7:57:07 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance\__pycache__                                                             0 2026-04-22 오후 3:05:39 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance\__pycache__\build_architecture_v152.cpython-314.pyc                  36.2 2026-04-22 오후 3:05:39 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance\__pycache__\md_normalizer.cpython-314.pyc                             3.3 2026-04-22 오후 2:45:32 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance\_workorder_stage0_hotfix_java17_e3b05e.txt                           10.1 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance\docs-index.md                                                         3.1 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance\patches                                                                 0 2026-04-23 오전 7:57:21 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance\patches\PATCH_v1.7.md                                                 0.9 2026-04-23 오전 7:57:21 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance\project-governance.md                                                   9 2026-04-23 오전 7:57:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture                                                                       0 2026-04-24 오후 1:57:05 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\global-single-core-system.md                                        3.1 2026-04-23 오전 7:56:43 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\legacy                                                                0 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\legacy\ARCHITECTURE_ONDEVICE_SEARCH.md                              4.6 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\legacy\SECURITY_ARCHITECTURE_V1.docx                               18.1 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\myphonecheck_base_architecture_v1.md                                8.3 2026-04-23 오후 9:09:56 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\myphonecheck_integrated_bundle_v1.6.1.md                          323.1 2026-04-24 오후 1:59:24 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\stage1_research                                                       0 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\stage1_research\01_current_state.md                                 9.5 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\stage1_research\02_module_candidates.md                             7.9 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\stage1_research\03_dependency_audit.md                             10.3 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\stage1_research\04_test_strategy.md                                 6.6 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\stage1_research\05_ci_cd_roadmap.md                                 7.6 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product                                                                            0 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\assets                                                                     0 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\assets\callcheck_icon_1024.png                                         614.8 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\assets\icon_A_phone_signal.png                                          18.2 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\assets\icon_B_phone_check.png                                           16.5 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\assets\icon_C_phone_radar.png                                           36.2 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\assets\icon_plan_a.png                                                  26.5 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\assets\icon_plan_b.png                                                  21.8 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\assets\icon_plan_c.png                                                  22.9 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\assets\icon_plans_comparison.png                                        45.1 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\specs                                                                      0 2026-04-23 오전 7:55:50 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\specs\GLOBAL_CASE_COVERAGE_V1.md                                        10.2 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\02_product\specs\PRD_CALLCHECK_V1.md                                                 10 2026-04-24 오전 10:32:16
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering                                                                        0 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\build_reports                                                          0 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\build_reports\P1_BUILD_FIX_REPORT.md                                 5.3 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\build_reports\RINGING_VISIBILITY_REPORT.md                           7.8 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\DO_NOT_MISS_IMPLEMENTATION.md                                        6.9 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\IMPORTANCE_AXIS_IMPLEMENTATION.md                                    9.3 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\index                                                                  0 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\index\FILE_INDEX.md                                                  7.1 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\index\FILES_MANIFEST.txt                                             9.3 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\integration                                                            0 2026-04-23 오전 7:56:28 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\integration\IMPLEMENTATION_SUMMARY.md                               14.8 2026-04-24 오전 10:32:22
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\integration\INTEGRATION_GUIDE.md                                     9.3 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\integration\README_IMPLEMENTATION.md                                   9 2026-04-24 오전 10:32:22
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\integration\SPEC_2026-04-14_core_rebuild.md                         29.3 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\integration\SPEC_2026-04-14_core_rebuild_v2.md                      18.4 2026-04-23 오전 7:56:28 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\03_engineering\number-profile-label-tag-design.md                                   1.7 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\04_operations                                                                         0 2026-04-23 오전 7:06:18 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\04_operations\codex-work-instruction-global-core.md                                 1.5 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\04_operations\PROJECT_STATUS.md                                                    18.7 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\04_operations\relay_protocol_v1.md                                                  6.2 2026-04-23 오전 7:06:18 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\04_operations\setup                                                                   0 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\04_operations\setup\SETUP_COMPLETE.md                                               8.1 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\04_operations\setup\setup_github.bat                                                7.2 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality                                                                            0 2026-04-23 오전 7:37:11 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\audit_stage0_hotfix_java17_20260422.md                                  19.2 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\emulator-validation-checklist.md                                         1.2 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\filter_repo_stage0_hotfix_20260422.md                                   11.6 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\hygiene_stage0_hotfix_20260422.md                                       14.9 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\reports                                                                    0 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\reports\architecture_conformance_myphonecheck_base_architecture_v1.md     29 2026-04-23 오후 9:09:56 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\reports\QA_FULL_MATRIX_V1.md                                             6.7 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\reports\REAL_WORLD_VERIFICATION_PLAN.md                                    8 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\reports\VERIFICATION_REPORT.md                                           6.6 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\stage1_push_trash_manual_test.md                                           1 2026-04-23 오전 7:37:18 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\test-rig                                                                   0 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\test-rig\01-SETUP-GUIDE.md                                               1.8 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\test-rig\02-auto-test.ps1                                                8.4 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\test-rig\03-run-test.bat                                                 0.3 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_discovery                                                                          0 2026-04-23 오전 10:44:01
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_discovery\inventory_cursor_myphonecheck.md                                    2621.7 2026-04-23 오전 9:09:37 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_discovery\summary_executing-OS.md                                               15.3 2026-04-23 오전 10:35:37
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_discovery\summary_myphonecheck_full.md                                          14.4 2026-04-24 오전 10:32:39
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_discovery\summary_web.md                                                         5.2 2026-04-23 오전 10:44:01
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history                                                                            0 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\2026-04-15-global-core-branchpoint.md                                    1.7 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive                                                                    0 2026-04-23 오전 7:59:36 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-14-global-single-core-snapshot.md                        3.3 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports                                                    0 2026-04-23 오전 7:59:49 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\ARCHITECTURE-ALL.zip                            23.4 2026-04-21 오후 5:15:39 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\ARCHITECTURE-cardspend.md                       17.7 2026-04-21 오후 4:56:34 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\ARCHITECTURE-constitution.md                     7.7 2026-04-21 오후 4:54:19 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\ARCHITECTURE-executing-OS.md                    16.1 2026-04-21 오후 4:53:47 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\ARCHITECTURE-myphonecheck.md                    21.4 2026-04-21 오후 4:52:30 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\MyPhoneCheck_Architecture_v1.4_disc.docx        43.6 2026-04-22 오후 12:48:35
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\MyPhoneCheck_Architecture_v1.5.1_ac9e0c.docx   104.6 2026-04-22 오후 2:27:58 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\MyPhoneCheck_Architecture_v1.5.2_0e40dd.docx   123.3 2026-04-22 오후 2:45:09 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\MyPhoneCheck_Patches_v1.5.2-patch_bc64b4.docx   30.1 2026-04-22 오후 2:36:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\MyPhoneCheck_Structure_Analysis_v4.docx         22.1 2026-04-17 오후 1:53:43 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\MyPhoneCheck_Work_Order_v4.3.docx                 12 2026-04-17 오후 2:22:21 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\synthesis_2026-04-22_pre-stage1.md              22.4 2026-04-22 오후 10:01:18
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\COMPLETION_REPORT.txt                                           13.9 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay                                                                              0 2026-04-23 오전 7:06:09 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\.gitkeep                                                                     0 2026-04-23 오전 7:06:09 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done                                                                         0 2026-04-23 오전 10:44:40
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\.gitkeep                                                                0 2026-04-23 오전 7:06:09 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\REPORT-WO-AUDIT-001__codex__done.md                                   4.4 2026-04-23 오전 7:19:05 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\REPORT-WO-CLEANUP-003__claudecode__done.md                            3.9 2026-04-23 오전 8:01:27 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\REPORT-WO-DISCOVERY-004__codex__done.md                               1.5 2026-04-23 오전 10:44:35
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\REPORT-WO-GOV-005__claudecode__done.md                                2.1 2026-04-23 오전 7:58:10 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\REPORT-WO-RELAY-001__claudecode__done.md                              0.6 2026-04-23 오전 7:06:29 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\REPORT-WO-STAGE1-001__cursor__done.md                                 0.8 2026-04-23 오전 7:37:18 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\scripts_folder_snapshot_20260422.txt                                  2.1 2026-04-23 오전 7:59:28 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\TECH-VERIFICATION-NLS.md                                              3.4 2026-04-23 오전 7:37:09 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\WO-AUDIT-001__codex__done.md                                            4 2026-04-23 오전 7:19:24 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\WO-CLEANUP-003__claudecode__done.md                                   7.2 2026-04-23 오전 7:06:18 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\WO-DISCOVERY-002__cursor__done.md                                     3.5 2026-04-23 오전 10:32:36
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\WO-DISCOVERY-004__codex__done.md                                      4.7 2026-04-23 오전 10:31:36
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\WO-GOV-005__claudecode__done.md                                        10 2026-04-23 오전 7:06:18 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\WO-STAGE1-001__cursor__done.md                                       13.5 2026-04-23 오전 7:06:18 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\failed                                                                       0 2026-04-23 오전 7:06:09 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\failed\.gitkeep                                                              0 2026-04-23 오전 7:06:10 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\in_progress                                                                  0 2026-04-23 오전 10:44:40
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\in_progress\.gitkeep                                                         0 2026-04-23 오전 7:06:09 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\needs_approval                                                               0 2026-04-23 오전 7:53:12 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\needs_approval\.gitkeep                                                      0 2026-04-23 오전 7:06:10 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\needs_approval\ALERT-WO-AUDIT-001-FAILURES.md                              1.2 2026-04-23 오전 7:53:12 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\queue                                                                        0 2026-04-23 오전 10:45:22
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\queue\.gitkeep                                                               0 2026-04-23 오전 7:06:09 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\queue\WO-DISCOVERY-003__claudecode__queue.md                               2.2 2026-04-23 오전 10:41:19
C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\queue\WO-DISCOVERY-005__cowork__queue.md                                   3.2 2026-04-23 오전 10:41:19
C:\Users\user\Dev\ollanvin\myphonecheck\docs\ARCHITECTURE.md                                                                    21.4 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\scope-test                                                                            0 2026-04-24 오전 9:23:27 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\scope-test\CURSOR-SCOPE-REPORT-001.md                                               5.2 2026-04-24 오전 9:23:27 



```

## 4. governance 계열 폴더 존재 여부

| 후보 경로 | 존재 |
|-----------|------|
| `docs/00_governance` | **Yes** — `project-governance.md`, `docs-index.md`, `_workorder_stage0_hotfix_java17_e3b05e.txt`, `patches/PATCH_v1.7.md`, `__pycache__/*.pyc` |
| `docs/00-governance` | No |
| `docs/governance` | No |
| `docs/00_architecture` | No |
| `docs/architecture` | No |
| `docs/01_architecture` | **Yes** — `myphonecheck_base_architecture_v1.md`, `myphonecheck_integrated_bundle_v1.6.1.md`, `global-single-core-system.md`, `legacy/`, `stage1_research/` |
| `docs/99_drafts` | No |
| `docs/99_archive` | No |

**추가:** 번호 체계 아래 `docs/06_history/archive/` 등에 과거 import·스냅샷이 별도 존재.

## 5. 아키텍처·인프라 문서 흔적

(패턴: 파일명에 Architecture|architecture|630dda|v1.5|v1.6|Infra|Governance|CONSTITUTION|헌법 — **로컬 워킹 트리** 기준, `.git` 제외. 최신 수정 시각 내림차순.)

**630dda:** 현재 워킹 트리에는 `*630dda*` 파일 **없음**. 로그·`project-governance.md`에 `docs/00_governance/MyPhoneCheck_Architecture_v1.6.1_630dda.docx` 언급만 존재.

**Infra:** 위 파일명 패턴 매칭으로는 `Infra` 접두 파일 **없음**.

```

FullName                                                                                                                                                                 KB LastWriteTime         
--------                                                                                                                                                                 -- -------------         
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\myphonecheck_integrated_bundle_v1.6.1.md                                                                 323.1 2026-04-24 오후 1:59:24 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\05_quality\reports\architecture_conformance_myphonecheck_base_architecture_v1.md                                            29 2026-04-23 오후 9:09:56 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\myphonecheck_base_architecture_v1.md                                                                       8.3 2026-04-23 오후 9:09:56 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance\project-governance.md                                                                                          9 2026-04-23 오전 7:57:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\ARCHITECTURE.md                                                                                                           21.4 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\legacy\SECURITY_ARCHITECTURE_V1.docx                                                                      18.1 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\01_architecture\legacy\ARCHITECTURE_ONDEVICE_SEARCH.md                                                                     4.6 2026-04-22 오후 9:12:02 
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\00_governance\project-governance.md                                                8.4 2026-04-22 오후 7:29:12 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\MyPhoneCheck_Architecture_v1.5.2_0e40dd.docx                                          123.3 2026-04-22 오후 2:45:09 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\MyPhoneCheck_Patches_v1.5.2-patch_bc64b4.docx                                          30.1 2026-04-22 오후 2:36:02 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\MyPhoneCheck_Architecture_v1.5.1_ac9e0c.docx                                          104.6 2026-04-22 오후 2:27:58 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\MyPhoneCheck_Architecture_v1.4_disc.docx                                               43.6 2026-04-22 오후 12:48:35
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\ARCHITECTURE-cardspend.md                                                              17.7 2026-04-21 오후 4:56:34 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\ARCHITECTURE-constitution.md                                                            7.7 2026-04-21 오후 4:54:19 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\ARCHITECTURE-executing-OS.md                                                           16.1 2026-04-21 오후 4:53:47 
C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports\ARCHITECTURE-myphonecheck.md                                                           21.4 2026-04-21 오후 4:52:30 
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\ARCHITECTURE.md                                                                   21.4 2026-04-21 오후 3:46:08 
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\05_quality\reports\architecture_conformance_myphonecheck_base_architecture_v1.md  29.2 2026-04-21 오후 3:04:01 
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\01_architecture\myphonecheck_base_architecture_v1.md                               6.9 2026-04-21 오후 2:56:26 
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\01_architecture\legacy\SECURITY_ARCHITECTURE_V1.docx                              18.1 2026-04-13 오전 11:21:33
C:\Users\user\Dev\ollanvin\myphonecheck\myphonecheck-backup-pre-filter-20260422\docs\01_architecture\legacy\ARCHITECTURE_ONDEVICE_SEARCH.md                             4.7 2026-04-10 오후 3:40:10 



```

## 6. 원격 레포 최상위

**API:** `GET /repos/ollanvin/myphonecheck/contents/` (ref=main, gh 기본)

**최상위 디렉터리/파일 이름(일부):** `.gitattributes`, `.github`, `.gitignore`, `app`, `build-logic`, `build.gradle.kts`, `core`, `data`, `docs`, `feature`, `gradle`, `gradle.properties`, `gradlew`, `gradlew.bat`, `gradlew_unix`, `ic_launcher_playstore.png`, `local.properties.template`, `logs`, `scripts`, `settings.gradle.kts`, 루트 png/log/html 등.

**`docs` 폴더(원격 main):** 존재. 항목: `00_governance`, `01_architecture`, `02_product`, `03_engineering`, `04_operations`, `05_quality`, `06_discovery`, `06_history`, `07_relay`, `ARCHITECTURE.md` (디렉터리 9 + 파일 1).

**로컬 대비:** 로컬에는 미추적 `docs/scope-test/`, `docs/01_architecture/myphonecheck_integrated_bundle_v1.6.1.md`, `docs/06_discovery/summary_*.md` 등이 원격 JSON 목록에는 없음(미푸시/미추적).

## 7. 판단용 힌트 (관측만, 판단·권고 없음)

- **페어 저장 경로 후보(실제 존재한 경로):** `docs/01_architecture/`(`myphonecheck_base_architecture_v1.md` 등), `docs/00_governance/`(거버넌스·패치), 루트 `docs/ARCHITECTURE.md`, `docs/06_history/archive/2026-04-imports/`(DOCX·ARCHITECTURE-*.md), 로컬만 `myphonecheck-backup-pre-filter-20260422/docs/` 동형 트리.
- **파일명 패턴(관측):** `myphonecheck_*_architecture_v*.md`, `myphonecheck_integrated_bundle_v*.md`, `ARCHITECTURE.md`, `ARCHITECTURE-*.md`, `MyPhoneCheck_Architecture_v*_*.docx`, `PATCH_v*.md`, `architecture_conformance_*.md`.
- **중복·병렬:** `docs/ARCHITECTURE.md`와 `docs/01_architecture/*` 동시 존재. DOCX 아키텍처는 `06_history/archive/2026-04-imports` 및 통합 MD(로컬)가 병행. `630dda` 명명은 문서/로그에만 남고 실파일은 현재 트리에 없음.