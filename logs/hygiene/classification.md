# Working Tree Classification (A/B/C)

HEAD before: cca259cc92e88bb6ca4a8656726320d5f9d82d50
Counts: modified 50 / untracked 115 / staged 0

Classification rule (per work order Section 2-1):
- A: Stage 0-hotfix essential (Java 17 files, Foojay, core/common freeze module, hotfix governance, audit outputs)
- B: IDE/tool residue to add to .gitignore
- C: everything else (stashed, not committed)

Note on cross-cutting files: `app/build.gradle.kts` and `settings.gradle.kts` contain both Java 17 hotfix hunks and push-intercept removal hunks (v1.1 architecture). Per file-level classification and the work order's prohibition on interactive git add, these files stage as A in full.

## Category A — Commit

### A.1 Modified .gradle.kts (Java 17)
| 파일경로 | 상태 | 근거 |
|----------|------|------|
| app/build.gradle.kts                                | M | VERSION_17 + jvmTarget "17" (+ push ref comment-out) |
| build-logic/convention/build.gradle.kts             | M | VERSION_17 + jvmToolchain(17) |
| build-logic/convention/src/main/kotlin/AndroidApplicationConventionPlugin.kt | M | compileOptions VERSION_17 |
| build-logic/convention/src/main/kotlin/AndroidLibraryConventionPlugin.kt      | M | compileOptions VERSION_17 |
| build-logic/settings.gradle.kts                     | M | Foojay resolver 0.9.0 |
| build.gradle.kts                                    | M | jvmToolchain(17) subproject + KotlinCompile jvmTarget "17" |
| core/model/build.gradle.kts                         | M | VERSION_17 + jvmTarget "17" |
| core/security/build.gradle.kts                      | M | VERSION_17 + jvmTarget "17" |
| core/util/build.gradle.kts                          | M | VERSION_17 + jvmTarget "17" |
| data/calllog/build.gradle.kts                       | M | VERSION_17 + jvmTarget "17" |
| data/contacts/build.gradle.kts                      | M | VERSION_17 + jvmTarget "17" |
| data/local-cache/build.gradle.kts                   | M | VERSION_17 + jvmTarget "17" |
| data/search/build.gradle.kts                        | M | VERSION_17 + jvmTarget "17" |
| data/sms/build.gradle.kts                           | M | VERSION_17 + jvmTarget "17" |
| feature/billing/build.gradle.kts                    | M | VERSION_17 + jvmTarget "17" |
| feature/call-intercept/build.gradle.kts             | M | VERSION_17 + jvmTarget "17" |
| feature/country-config/build.gradle.kts             | M | VERSION_17 + jvmTarget "17" |
| feature/decision-engine/build.gradle.kts            | M | VERSION_17 + jvmTarget "17" |
| feature/decision-ui/build.gradle.kts                | M | VERSION_17 + jvmTarget "17" |
| feature/device-evidence/build.gradle.kts            | M | VERSION_17 + jvmTarget "17" |
| feature/message-intercept/build.gradle.kts          | M | VERSION_17 + jvmTarget "17" |
| feature/privacy-check/build.gradle.kts              | M | VERSION_17 + jvmTarget "17" |
| feature/search-enrichment/build.gradle.kts          | M | VERSION_17 + jvmTarget "17" |
| feature/settings/build.gradle.kts                   | M | VERSION_17 + jvmTarget "17" |
| gradle.properties                                   | M | Java 17 toolchain auto-download + Stage 0-hotfix comment |
| gradle/libs.versions.toml                           | M | kotlin-jvm plugin + junit-jupiter + kotest (core:common 의존) |
| settings.gradle.kts                                 | M | Foojay resolver 0.9.0 (+ push removal comment) |

### A.2 Governance / Audit
| 파일경로 | 상태 | 근거 |
|----------|------|------|
| docs/00_governance/project-governance.md             | M | hotfix e3b05e + Foojay 반영 |
| docs/00_governance/_workorder_stage0_hotfix_java17_e3b05e.txt | ?? | 이 hotfix의 원 워크오더 |
| docs/05_quality/audit_stage0_hotfix_java17_20260422.md | ?? | 본 감사의 정식 보고서 |

### A.3 CI Gate
| 파일경로 | 상태 | 근거 |
|----------|------|------|
| .github/workflows/contract-freeze-check.yml | ?? | Stage 0 FREEZE 검증 워크플로 (미추적 → 추적) |

### A.4 core/common freeze module
| 파일경로 | 상태 | 근거 |
|----------|------|------|
| core/common/FREEZE.md                               | ?? | 계약 동결 마커 |
| core/common/build.gradle.kts                        | ?? | kotlin.jvm + jvmToolchain(17) |
| core/common/src/main/kotlin/app/myphonecheck/core/common/checker/Checker.kt               | ?? | 계약 Kotlin |
| core/common/src/main/kotlin/app/myphonecheck/core/common/engine/DecisionEngineContract.kt | ?? | 계약 Kotlin |
| core/common/src/main/kotlin/app/myphonecheck/core/common/identifier/IdentifierType.kt     | ?? | 계약 Kotlin |
| core/common/src/main/kotlin/app/myphonecheck/core/common/risk/DamageEstimate.kt           | ?? | 계약 Kotlin |
| core/common/src/main/kotlin/app/myphonecheck/core/common/risk/DamageType.kt               | ?? | 계약 Kotlin |
| core/common/src/main/kotlin/app/myphonecheck/core/common/risk/RiskKnowledge.kt            | ?? | 계약 Kotlin |
| core/common/src/main/kotlin/app/myphonecheck/core/common/risk/RiskLevel.kt                | ?? | 계약 Kotlin |
| core/common/src/main/kotlin/app/myphonecheck/core/common/risk/SearchEvidence.kt           | ?? | 계약 Kotlin |
| core/common/src/test/kotlin/app/myphonecheck/core/common/CheckerContractTest.kt           | ?? | 계약 테스트 |
| core/common/src/test/kotlin/app/myphonecheck/core/common/FreezeMarkerTest.kt              | ?? | FREEZE marker 테스트 |
| core/common/src/test/kotlin/app/myphonecheck/core/common/IdentifierTypeTest.kt            | ?? | 계약 테스트 |
| core/common/src/test/kotlin/app/myphonecheck/core/common/RiskKnowledgeContractTest.kt     | ?? | 계약 테스트 |

### A.5 Audit evidence logs (logs/ hierarchy)
55 logs/*.txt + logs/*.md + logs/hygiene/*.txt — all audit/hygiene execution evidence.

## Category B — .gitignore reinforcement

| 파일경로 | 상태 | 근거 |
|----------|------|------|
| .claude/settings.local.json | ?? | Claude Code local config — should never be committed |

## Category C — Stash (non-hotfix residue)

### C.1 Push engine removal (Architecture v1.1)
| 파일경로 | 상태 | 근거 |
|----------|------|------|
| app/src/main/AndroidManifest.xml                                                         | M | Manifest cleanup |
| app/src/main/kotlin/app/myphonecheck/mobile/legacy/ForcePhoneListener.kt                 | M | App-side cleanup |
| app/src/main/kotlin/app/myphonecheck/mobile/navigation/MyPhoneCheckNavHost.kt            | M | App-side cleanup |
| app/src/main/kotlin/app/myphonecheck/mobile/worker/WeeklyReportWorker.kt                 | M | App-side cleanup |
| core/model/src/main/kotlin/app/myphonecheck/mobile/core/model/ConclusionCategory.kt      | M | Push enum purge |
| core/model/src/main/kotlin/app/myphonecheck/mobile/core/model/InterceptEventType.kt      | M | Push enum purge |
| core/model/src/main/kotlin/app/myphonecheck/mobile/core/model/PushEvidence.kt            | D | Push model delete |
| data/local-cache/src/main/kotlin/app/myphonecheck/mobile/data/localcache/dao/PushStatsDao.kt      | D | Push DAO delete |
| data/local-cache/src/main/kotlin/app/myphonecheck/mobile/data/localcache/db/MyPhoneCheckDatabase.kt | M | DB schema v12 (push removal) |
| data/local-cache/src/main/kotlin/app/myphonecheck/mobile/data/localcache/di/LocalCacheModule.kt    | M | DI cleanup |
| data/local-cache/src/main/kotlin/app/myphonecheck/mobile/data/localcache/entity/MessageHubEntity.kt | M | Schema migration |
| data/local-cache/src/main/kotlin/app/myphonecheck/mobile/data/localcache/entity/PushStatsEntity.kt  | D | Push entity delete |
| feature/call-intercept/src/main/kotlin/app/myphonecheck/mobile/feature/callintercept/MyPhoneCheckScreeningService.kt | M | Call intercept update |
| feature/country-config/src/main/kotlin/app/myphonecheck/mobile/feature/countryconfig/PricingPolicy.kt                | M | Country config update |
| feature/country-config/src/test/kotlin/app/myphonecheck/mobile/feature/countryconfig/Global190CountryPricingTest.kt  | M | Test update |
| feature/country-config/src/test/kotlin/app/myphonecheck/mobile/feature/countryconfig/RuntimeConnectionEvidenceCountryConfigTest.kt | M | Test update |
| feature/decision-engine/src/main/kotlin/app/myphonecheck/mobile/feature/decisionengine/ActionMapper.kt | M | Decision engine update |
| feature/push-intercept/build.gradle.kts                                                  | D | Module removal |
| feature/push-intercept/proguard-rules.pro                                                | D | Module removal |
| feature/push-intercept/src/main/AndroidManifest.xml                                      | D | Module removal |
| feature/push-intercept/src/main/kotlin/app/myphonecheck/mobile/feature/pushintercept/PushCheckEngine.kt     | D | Module removal |
| feature/push-intercept/src/main/kotlin/app/myphonecheck/mobile/feature/pushintercept/PushInterceptService.kt | D | Module removal |
| data/local-cache/schemas/app.myphonecheck.mobile.data.localcache.db.MyPhoneCheckDatabase/12.json | ?? | Room schema snapshot (push-removal migration) |

### C.2 Other workorders & governance residue
| 파일경로 | 상태 | 근거 |
|----------|------|------|
| docs/00_governance/_workorder_stage0_contracts_f1a85c.txt | ?? | Stage 0 계약 WO (본 hotfix 범위 밖) |
| docs/00_governance/_workorder_v153_4cad42.txt             | ?? | v1.5.3 WO |
| docs/00_governance/_workorder_v160_four_surfaces_212359.txt | ?? | v1.6.0 WO |
| docs/00_governance/_workorder_v161_patch_cursor_6827a2.txt | ?? | v1.6.1 WO |
| docs/00_governance/_extracted_workorders_v15.txt          | ?? | 추출 산출물 |
| docs/00_governance/_rebuild_workorder_b1b980.txt          | ?? | 재생성 산출물 |
| docs/00_governance/_rebuild_workorder_v152_435ef4.txt     | ?? | 재생성 산출물 |
| docs/00_governance/_v152_patches_extracted.txt            | ?? | 추출 산출물 |
| docs/00_governance/_v15_patches_extracted.txt             | ?? | 추출 산출물 |
| docs/00_governance/_build_v41.py                          | ?? | 문서 빌드 스크립트 |
| docs/00_governance/_extract_docx.py                       | ?? | 문서 추출 스크립트 |
| docs/00_governance/build_architecture_v151.py             | ?? | 아키텍처 빌드 스크립트 |
| docs/00_governance/build_architecture_v152.py             | ?? | 〃 |
| docs/00_governance/build_architecture_v153.py             | ?? | 〃 |
| docs/00_governance/build_architecture_v160.py             | ?? | 〃 |
| docs/00_governance/build_architecture_v161.py             | ?? | 〃 |
| docs/00_governance/md_normalizer.py                       | ?? | 정규화 스크립트 |
| docs/00_governance/MyPhoneCheck_Architecture_v1.3_extracted.md | ?? | 아키텍처 초안 |
| docs/00_governance/MyPhoneCheck_Architecture_v1.4_full.md      | ?? | 〃 |
| docs/00_governance/MyPhoneCheck_Architecture_v1.5.1_7d23b4.docx | ?? | 〃 |
| docs/00_governance/MyPhoneCheck_Architecture_v1.5.1_draft.md    | ?? | 〃 |
| docs/00_governance/MyPhoneCheck_Architecture_v1.5.1_fd908b.docx | ?? | 〃 |
| docs/00_governance/MyPhoneCheck_Architecture_v1.5.2_9f1d43.docx | ?? | 〃 |
| docs/00_governance/MyPhoneCheck_Architecture_v1.5.2_draft.md    | ?? | 〃 |
| docs/00_governance/MyPhoneCheck_Architecture_v1.5.3_758158.docx | ?? | 〃 |
| docs/00_governance/MyPhoneCheck_Architecture_v1.5.3_draft.md    | ?? | 〃 |
| docs/00_governance/MyPhoneCheck_Architecture_v1.6.0_b7bb60.docx | ?? | 〃 |
| docs/00_governance/MyPhoneCheck_Architecture_v1.6.0_draft.md    | ?? | 〃 |
| docs/00_governance/MyPhoneCheck_Architecture_v1.6.1_630dda.docx | ?? | 〃 |
| docs/00_governance/MyPhoneCheck_Architecture_v1.6.1_draft.md    | ?? | 〃 |
| docs/00_governance/MyPhoneCheck_Architecture_v4.1_final.md      | ?? | 〃 |
| docs/00_governance/MyPhoneCheck_Patches_v1.5.3-patch_6bb29e.docx | ?? | 패치 아카이브 |
| docs/00_governance/MyPhoneCheck_Patches_v1.6.0-patch_03b734.docx | ?? | 패치 아카이브 |
| docs/00_governance/MyPhoneCheck_Patches_v1.6.1-patch_61225a.docx | ?? | 패치 아카이브 |
| docs/00_governance/v1.4_disc_final_full.md                      | ?? | 아키텍처 문서 |

## Category counts (file-level)
- A: 27 modified + 17 untracked (core/common, workorder, audit, freeze-check.yml) + ~55 logs/ = ~99 files
- B: 1 (.claude/settings.local.json)
- C: 22 modified + ~36 untracked = ~58 files

## Cross-check vs audit Section 2-2
- VERSION_17 hit paths in `.gradle.kts` (logs/grep_version17.txt): 24 files (excluding core/common which is untracked, plus root build.gradle.kts and build-logic/convention/build.gradle.kts which use jvmToolchain(17) without VERSION_17).
- A.1 modified .gradle.kts count: 27 files — includes gradle.properties, build-logic/settings.gradle.kts, settings.gradle.kts, build-logic/convention files not in VERSION_17 list, root build.gradle.kts (jvmToolchain only), libs.versions.toml.
- Difference is consistent: VERSION_17 is one subset of Stage 0-hotfix artifacts; A.1 is the union with Foojay/toolchain/catalog files.
