# 0-B. KPI 매핑 (Honesty Audit Log — KPI/SLA/CONST 약속 추적)

**원본 출처**: v1.7.1 §0-B 전문 재복사 (KPI 매핑 관점, `appendix/A_audit_log.md`와 동일 원본)
**v1.8.0 Layer**: Business
**의존**: `appendix/A_audit_log.md` (원본 동일) + `70_business/01_business_model.md`
**변경 이력**: 본 파일은 v1.7.1 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_code/70_business/02_kpi_mapping.md`

---

## 0-B. 정직성 감사 로그 (Honesty Audit Log)

본 로그는 0-A의 약속이 본문·코드에서 실제 이행되었는지 추적한다.

| 약속 ID | 약속 내용 | 구현 증거 위치 | 검증 방법 | 검토자 |
|---|---|---|---|---|
| CONST-1 | Universal Truth Defense | §1-1 + §17 + §36 | 4 Surface 통합 테스트 PASS | 대표님 |
| CONST-2 | In-Bound Zero | §1-2 + §6-2/6-3 | `scripts/verify-network-policy.sh` (Phase 1 실행 예정, 현재 대기) | 비전, 헐크, 자비스 |
| CONST-3 | Out-Bound Zero | §1-3 + §6-2/6-3 | `scripts/verify-no-server.sh` (Phase 1 실행 예정, 현재 대기) | 비전, 헐크 |
| CONST-4 | 자가 작동 | §1-4 + §19 (권한 정책) | UserConsentTest (Phase 1 실행 예정) | 스타크 |
| CONST-5 | 정직성 | §1-5 + 0-B/0-C/0-D | 본 로그 자체 | 비전 |
| CONST-6 | 가격 정직성 | §1-6 + BillingManager.kt + §31 | Play Console net revenue (Phase 4 실측 대기) | 스타크 |
| CONST-7 | 디바이스 오리엔티드 거위 | §1-7 + §5 + §17 | 본사 매핑 0건 검증 체크리스트 (§15) | 비전 |
| SLA-14-2 | L3 NKB Hit p95 ≤ 5ms | §14-2 | SmokeRun11 PASS | 자비스 |
| KPI-16-2 | net ARPU $1.49 (gross $2.49) | §16-2 + BillingManager.kt | Play Console net revenue | 스타크 |
| MODEL-FREEZE | Data Model Frozen | §8-X | MigrationCompatTest PASS | 헐크 |
| CONTRACT-DEC | Decision Contract | §10-X | DecisionContractTest PASS | 헐크 |
| MEM-2KB | NKB 엔트리 ≤ 2KB | §30-X | Memory Profiler 측정 | 헐크 |
| STAGE0-FREEZE | Stage 0 5 파일 FREEZE (4 핵심 계약 + FreezeMarker 어노테이션) | §33-1-0, §33-1-1~§33-1-5 | FreezeMarkerTest PASS (22 tests, 22 시그니처) | 비전 |
| JAVA17 | JDK 17 toolchain | §26 | CI 빌드 PASS | Cursor |

## 0-B-1. 검토자 지적 → 패치 반영 추적

| 라운드 | 검토자 | 지적 | 반영 패치 | 상태 |
|---|---|---|---|---|
| 1차 | 자비스 | BROADCAST_SMS Play 정책 위반 | v1.5.3 Patch 17 (Default SMS Handler 재설계로 대체) | 완료 |
| 2차 | 자비스 | ExtractedSignal numberE164 누락 | v1.5.1 Patch 03 | 완료 |
| 2차 | 자비스 | 글로벌 vs 언어 분리 | v1.5.1 Patch 05 + §28 | 완료 |
| 3차 | 헐크 | BROADCAST_SMS 런타임 선언 잔존 | v1.5.3 Patch 17 | 완료 |
| 4차 | 대표님 | "왜 콜첵만 있지? 문자첵? 카메라첵? 마이크첵은?" | v1.6.0 Patch 18~22 (Four Surfaces 신설) | 완료 |
| 4차 | 헐크 | 정책 제약으로 제품 범위 축소 비판 | v1.6.0 | 완료 |
| 4차 | 자비스 | Patch 17이 막은 건 BROADCAST_SMS 한 방식뿐 | v1.6.0 MessageCheck 복원 | 완료 |
| 5차 | 대표님 | 권한 타령 재발 | v1.6.1 Patch 23~24 (§34-1, 부록 A 권한 정당화 삭제) | 완료 |
| 5차 | 비전 자기점검 | Surface 본문 축약 | v1.6.1 Patch 25 (본문 완성) | 완료 |
| 5차 | 비전 자기점검 | Manifest 누락 | v1.6.1 Patch 26 | 완료 |

## 0-B-2. Patch 감사 로그 (Patch 17~38)

| PATCH | 내용 | 위치 | 검증 | 사유 |
|---|---|---|---|---|
| PATCH-17 | BROADCAST_SMS 제거 | §24-6, §34-1 | AndroidManifest scan | 헐크 라운드 3 (2026-04-22) |
| PATCH-18 | MessageCheck 범위 복원 (발신번호+URL+기관사칭) | §18-4, §3-6 | v1.6.0 워크오더 §3 | 대표님 2026-04-22 질책 반영 |
| PATCH-19 | MicCheck 신설 | §18-6 | v1.6.0 워크오더 §4 | 비전 자기 오류 4건 인정 |
| PATCH-20 | CameraCheck 신설 | §18-7 | v1.6.0 워크오더 §4 | 비전 자기 오류 4건 인정 |
| PATCH-21 | PrivacyCheck 폐기 | §18-5 삭제 | MicCheck/CameraCheck로 대체 | 구체적 Surface 원칙 |
| PATCH-22 | §36 Four Surfaces 통합 섹션 | §36 | 신규 H1 | One Engine, Four Surfaces 원칙 |
| PATCH-23 | §34-1 RECORD_AUDIO/CAMERA 행 삭제 | §34-1 | table diff | 비전 워크오더 구멍 인정 |
| PATCH-24 | 부록 A §A-3/§A-4 삭제 | 부록 A | section diff | 비전 워크오더 구멍 인정 |
| PATCH-25 | Surface 본문 완성 (MessageRisk/AppPermissionRisk 등) | §18-4/6/7 | data class + scenario | 커서 축약 정정 |
| PATCH-26 | Manifest 권한 정합 | §24-6 (QUERY_ALL_PACKAGES, PACKAGE_USAGE_STATS) | manifest scan | 커서 누락 정정 |
| PATCH-27 | H1 중복 제거 | §18-6/§18-7 | pandoc 파싱 검증 | 커서 구조 오류 정정 |
| PATCH-28 | 패치 묶음 본문 보강 | v1.6.1-patch 묶음 | paragraph count | 커서 축약 정정 |
| PATCH-29 | MessageCheck Mode A/B 2-모드 아키텍처 | §18-4 | 자비스 Default SMS 모호성 지적 | 권한 0 기본 경로 + Default SMS 선택 경로 |
| PATCH-30 | MicCheck/CameraCheck 단순 관리자 축소 | §18-6/§18-7 | 대표님 2026-04-24 지시 | "리스트+이력+회수 버튼" 3기능으로 축소, 평판·CVE·Justification 삭제 |
| PATCH-31 | AppSecurityWatch 후행 Surface 신설 | §17-3 | 메모리 #13 CVE 감시 이관처 | MicCheck/CameraCheck에서 분리, 구체 Surface 원칙 정합 |
| PATCH-32 | Data Safety 정직 재선언 | §27 전면 재작성 | 자비스 허위 disclosure 지적 | "수집 0" 폐기, "외부 전송 0 + On-device only" 정확 표기 |
| PATCH-33 | Permissions Declaration 본문 신설 | §27-3 | Play 통과 전략 | 권한별 core user benefit + less-invasive 거절 사유 + 사용자 고지 |
| PATCH-34 | NKB 암호화 SQLCipher + Keystore | §27-5 + §8-0 | Lane 1 D06 복원 | 코웍 87a9a3 §8-0 흡수 + AES-256-GCM 하드웨어 키 |
| PATCH-35 | DO_NOT_MISS 처분 정책 신설 | §8-2 + §3-4 + §21 | Lane 1 D05 복원 | 코웍 87a9a3 §17-6-5 흡수, 4 Surface 공통 규칙 |
| PATCH-36 | QUERY_ALL_PACKAGES 제거 + `<queries>` 블록 | §24-6 | 자비스 대안 2 수용 | Intent 기반 Package Visibility 최소화 |
| PATCH-37 | **7-워커 통합 평가 P0·P1 정정** | §3, §17-3, §24-6, §33-2, §34-1, §10-X, §33-1-4, §0-B, §0-B-2, §Z-10 신설 | 7-워커 평가 (Claude Code·Cursor·Codex·코웍·헐크·자비스·스타크) | P0 6건 + P1 8건 반영. §3/§17-3 AppPermissionRisk 잔존 제거, §34-1 QUERY_ALL_PACKAGES 잔존 제거, DecisionEngineContract 타입 통일, KPI-16-2 표기 정정, FREEZE 22/21/24 3-way 통일, 헤더 Patch 17~37 갱신 |
| **PATCH-38** | **Play Integrity API classicRequest 로컬 무결성 검증** | **§31-2 강화 + §27-3-8 신설 + §34-1 권한 매트릭스** | **2차 외부 검증 라운드 스타크 유효 지적** | **크랙/루팅/Frida 환경 결제 토큰 변조 방어선 보강. Google Play 공식 API `classicRequest` 로컬 전용 모드 사용 (서버 0 유지). 헌법 1조 "스토어 공식 API 허용" 범위 내 반영.** |

**MINOR 승격 기록 (2026-04-24 저녁)**: Patch 37 반영 시점에 대표님 지시로 **v1.6.2 → v1.7.0 MINOR 에스컬레이션**. 사유: §3/§17-3 표 구조 변경 + DecisionEngineContract 타입명 재정의는 Semver MINOR 수준. PATCH 번호는 연속 유지, 버전만 MINOR 승격. 헌법·데이터 모델 핵심 시그니처는 무변경.

**버전 승격 기록 (2026-04-24 저녁, v1.6.1→v1.6.2)**: Patch 29~36을 모두 담은 2차 재작성 완료 시점에, 대표님 지시로 **v1.6.1 → v1.6.2 PATCH 승격**. 승격은 Patch 번호를 추가하지 않으며, 헌법·데이터 모델·Stage 0 FREEZE 시그니처 무변경. 승격 사유 및 기존 v1.6.1 산출물과의 구분은 §Z-1, §0-A 버전 매트릭스 참조.
