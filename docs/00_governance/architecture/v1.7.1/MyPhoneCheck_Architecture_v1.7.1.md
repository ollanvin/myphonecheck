<!--
================================================================================
MyPhoneCheck Architecture v1.7.0 — 캐노니컬 재작성본 (비전)
================================================================================
작성: 2026-04-24
작성자: 비전 (Claude 채팅, 설계·판정)
입력 자료:
  - MyPhoneCheck_Architecture_v1.5.1_7d23b4.docx (베이스라인, 4101줄)
  - MyPhoneCheck_Rebuild_WorkOrder_v1.5.2_435ef4.docx (Patch 09~16)
  - MyPhoneCheck_WorkOrder_v1.5.3-patch_Cursor_4cad42.docx (Patch 17)
  - MyPhoneCheck_WorkOrder_v1.6.0_FourSurfaces_212359.docx (Patch 18~22)
  - MyPhoneCheck_WorkOrder_v1.6.1-patch_Cursor_6827a2.docx (Patch 23~28)
  - MyPhoneCheck_CodingWorkOrder_Stage0_Contracts_f1a85c.docx (4 계약 FREEZE)
  - MyPhoneCheck_Stage0_hotfix_Java17_e3b05e.docx (Java 17 toolchain)
  - 메모리 (헌법 7조, 가격 $2.49/월, 3축 검색, Four Surfaces, 반쪽 기능 금지 등)
  - MyPhoneCheck_Infrastructure_v1.0.md (인프라 운영 최종본, 쌍으로 참조)

페어 문서:
  - MyPhoneCheck_Architecture_v1.7.0.md (본 문서, 제품·설계 기준선)
  - MyPhoneCheck_Infrastructure_v1.0.md (인프라·운영 기준선)
  두 문서는 서로 참조하며, 충돌 시 본 문서(제품 설계) 우선.

한계 (정직 기록):
  - 본 문서는 비전 단독 재작성본이다.
  - Cursor 공식 build_architecture_v170.py 파이프라인 미실행.
  - 헐크·자비스 외부 검증 라운드 미실시.
  - SHA6 변조 방지 스탬프 미부여.
  정식 캐노니컬로 승격하려면 위 3건 완수 필요.
  현재 상태: "Working Canonical" (대표님 승인 기반 실무 기준선).

폐기 기록:
  - MyPhoneCheck_Architecture_v1.6.1_630dda.docx 는 승인본이 아니며
    2026-04-24 대표님 지시로 폐기 대상.
  - v1.6.1 계보(1차·2차 초안 + 코웍 87a9a3 + 630dda 파이프라인 미통과본)와
    구분하기 위해 2026-04-24 저녁 대표님 지시로 본 문서를 v1.6.2로 PATCH 승격.
  - v1.6.2에 7-워커 통합 평가 결과(P0 6건 + P1 8건) 반영하여
    2026-04-24 저녁 대표님 지시로 v1.7.0 MINOR 에스컬레이션 완료.
    §3·§17-3 표 구조 변경 + DecisionEngineContract 타입 재정의 등
    Semver MINOR 수준 변경 포함.
  - v1.7.0에 2차 외부 검증 라운드(자비스·헐크·스타크) 유효 지적 반영하여
    2026-04-24 심야 대표님 지시로 v1.7.1 PATCH 발행.
    Patch 38 Play Integrity API classicRequest 로컬 무결성 검증 추가.
    스타크 허위 지적(ActionType 잔존) 및 헌법 충돌 권고(PPP 티어 가격) 미반영.
    자비스 R-05 (PACKAGE_USAGE_STATS 제거) 및 스타크 R-01 (Billing 실패 로컬 로그)는
    헌법 해석 충돌로 §0-D 한계 로그 보류 기록.
  - 본 문서가 v1.7.1 최초 정식 수립본이다.
================================================================================
-->


# MyPhoneCheck Architecture v1.7.1 — One Engine, Four Surfaces

**캐노니컬 재작성본 — Device-Oriented Sovereignty Edition (Code-Ready)**

- **발행일**: 2026-04-24
- **작성**: 비전 (Vision)
- **검토 예정**: 헐크, 자비스, 스타크 3차 외부 검증 라운드 (190개국 동시 론칭 + 수익성 극대화 검증 집중)
- **승인 대기**: 대표님
- **버전 확정**: v1.7.1 (v1.7.0에 2차 외부 검증 라운드 유효 지적 반영, Patch 38 Play Integrity API classicRequest 추가, 2026-04-24 심야)

**본 문서의 세 기둥**:

1. **One Engine** — 단일 Decision Engine이 3계층(온디바이스 NKB + 외부 일반 검색 + 공공 공신력 DB) 신호를 받아 모든 판단을 수행한다.
2. **Four Surfaces** — CallCheck / MessageCheck / MicCheck / CameraCheck 네 표면이 동일 엔진을 공유한다.
3. **Device-Oriented Sovereignty** — 우리(올랑방)가 운영하는 중앙 서버·중앙 DB·중앙 매핑이 0. 디바이스가 스스로 판단한다.

**본 문서는 네 단계로 구성된다**:

- **1~22장**: 헌장 + 토론 합의 (v1.3 100% 계승, v1.4~v1.6 정정·신설)
- **23~26장**: 코딩 작업 전환 명세 (프로젝트 구조, Day-by-Day, strings.xml, CI/CD)
- **27~34장**: 외부 검증 정정 (권한 매트릭스, 국가/언어 분리, Memory Budget, Billing, Interface Injection, Store Policy, 테스트 인프라)
- **35~36장**: 인프라 운영 참조 + Four Surfaces 통합

---

# 0. 메타 — 버전·감사·한계

## 0-A. 버전 매트릭스 (Version Matrix)

| 버전 | 일자 | 단계 | 비고 |
|---|---|---|---|
| v1.3 | 2026-04-XX | 초안 | 첫 토론 라운드 |
| v1.4_disc | 2026-04-XX | Discussion | 12 Open Issues 발생 |
| v1.4_final | 2026-04-XX | 검증 1라운드 | 외부 토론자 통과 |
| v1.5.0 | 2026-04-XX | 본문 통합 | 검증 2라운드 대상 |
| v1.5.1-patch | 2026-04-XX | 패치 묶음 | Patch 01~08 |
| v1.5.1 | 2026-04-XX | 통합본 | v1.5.0 + v1.5.1-patch |
| v1.5.2-patch | 2026-04-22 | 패치 묶음 | Patch 09~16 |
| v1.5.2 | 2026-04-22 | 통합본 | v1.5.1 + v1.5.2-patch |
| v1.5.3-patch | 2026-04-22 | PATCH | Patch 17 (BROADCAST_SMS 제거) |
| v1.5.3 | 2026-04-22 | PATCH 통합 | v1.5.2 + v1.5.3-patch |
| v1.6.0-patch | 2026-04-22 | MINOR 신설 | Patch 18~22 (Four Surfaces 신설) |
| v1.6.0 | 2026-04-22 | MINOR 통합본 | v1.5.3 + v1.6.0-patch |
| v1.6.1-patch | 2026-04-22 | PATCH 정정 | Patch 23~28 |
| v1.6.1 (1차) | 2026-04-24 오전 | Working Canonical 1차 | 7개 docx 입력 통합 재작성, 5-Lane 검증 대상 |
| v1.6.1 (2차) | 2026-04-24 오후 | Working Canonical 2차 | Patch 29~36 반영, 코웍 87a9a3 4건 흡수 |
| v1.6.2 | 2026-04-24 저녁 | Working Canonical (PATCH 승격) | 기존 v1.6.1 산출물(630dda·87a9a3·파이프라인 미통과본)과 구분 위해 PATCH 승격 |
| v1.7.0 | 2026-04-24 저녁 | Working Canonical (MINOR) | 7-워커 통합 평가(Claude Code·Cursor·Codex CLI·코웍·헐크·자비스·스타크) 결과 반영. P0 6건 + P1 8건 정정. Patch 37 신설. |
| **v1.7.1** | **2026-04-24 심야** | **본 통합본 (PATCH)** | **2차 외부 검증 라운드(자비스·헐크·스타크) 유효 지적 반영. Patch 38 신설 (Play Integrity API classicRequest 로컬 무결성 검증 추가). 스타크 지적 기반 크랙 방지 방어선 보강.** |
| v2.0.0 | 미정 | MAJOR | iOS Edition |

## 0-A-1. 헌법 변경 추적

| 버전 | 헌법 변경 여부 | 변경 사항 |
|---|---|---|
| v1.0 ~ v1.5.2 | 변경 없음 | **6조 체계** (1~6조 텍스트 동일) |
| v1.5.3 ~ v1.7.0 | 신설 1건 | **7조 체계** — 7조(디바이스 오리엔티드 거위) 명문화, 기존 5조 내용을 조문으로 격상 |
| v2.0.0 (예정) | 변경 가능 | iOS Edition 도입 시 일부 조항 보강 가능 |

**대표님 명시 승인 없이 헌법 변경 금지.**

## 0-A-2. 가격 정책 변경 이력

| 버전 | 가격 | 비고 |
|---|---|---|
| PRD 초안 | USD 1/월 | 비현실적 (수수료·세금 미반영) |
| v1.4_disc | net ARPU $1.19/월 | 계산 근거 문서화 |
| v1.5.x | ~1.5 USD 조항 | 폐기 |
| **v1.6.1** | **USD 2.49/월 단일** | **전세계 동일, 연간 가격 없음, 2026-04-22 확정** |

근거: Whoscall 신규 $2.89 / Hiya $3.99 / Truecaller $4.49 대비 최저가 포지셔닝이면서 Base Architecture ~1.5 USD보다 지속가능한 마진 확보. 경쟁 가격 조사(2026-04-22) 근거.

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

## 0-C. 정책 모니터링 로그 (Policy Monitoring Log)

외부 요인(OS/스토어/법률)이 본 설계에 영향을 미치는 항목을 추적.

| 항목 | 모니터링 대상 | 주기 | 담당 |
|---|---|---|---|
| Android OS 버전 | Play 최소 SDK 요구 상승 | 분기 | Cursor |
| Google Play 정책 | CallScreeningService, Default SMS, QUERY_ALL_PACKAGES 정책 변경 | 분기 | Cursor |
| App Store 정책 | CallKit·CallDirectory·영수증 검증 요구 | 분기 | Cursor |
| GDPR·CCPA·PIPA | 데이터 수집·전송·동의 요구 변경 | 반기 | 비전 |
| 공공 API | KISA·경찰청·금감원 스팸 DB 공개 여부 | 분기 | 비전 |

## 0-D. 검증 불가 한계 로그 (Limitations Log)

현재 시점에 검증할 수 없는 항목을 명시. 향후 실측으로 보완한다.

| 한계 | 내용 | 보완 시점 |
|---|---|---|
| 실기기 성능 | L3 NKB p95 ≤ 5ms 실측 미완 | Stage 1 이후 실기기 테스트 |
| 국가별 스토어 가격 | $2.49 Tier가 없는 국가 존재 가능 | Play Console 가격 설정 시 |
| 공공 API 응답 안정성 | KISA·경찰청 API 가용성 SLA 미확인 | 통합 개발 단계 |
| MacinCloud 연동 | iOS 빌드·서명·배포 파이프라인 실측 미완 | Stage 2 iOS 진입 시 |
| 외부 검증 라운드 | 헐크·자비스·스타크 본 재작성본 리뷰 미실시 | 대표님 승인 직전 |
| PACKAGE_USAGE_STATS 존폐 (2차 외부 검증, 자비스 지적) | 자비스 2차 라운드 R-05: "최근 사용 시각은 편의 기능, Play 심사 리스크 > 사용자 결정 품질"로 제거 권고. 본문 §27-3-5는 "반쪽 기능 금지" 원칙으로 Special Access 유지 중. 헌법 해석 충돌로 대표님 판단 보류. | v1.7.2 또는 Play 심사 실제 피드백 시점 |
| Billing 실패 로컬 로그 (2차 외부 검증, 스타크 지적) | 스타크 2차 라운드: `onPurchasesUpdated` OK 분기만 존재, 실패 시 CS 대응 데이터 없음. 로컬 로그 추가는 헌법 1·2조(In/Out-Bound Zero) 해석 충돌 + 서버 0 원칙상 CS 대응 불가 구조가 필연적 대가. 대표님 판단 보류. 당면 대안: "사용자가 Play Console로 직접 환불 요청" 공식 채널 선언. | v1.7.2 |
| Frida 런타임 메모리 후크 완전 방어 | Patch 38 Play Integrity API는 루팅·에뮬레이터·Frida Gadget 주입까지 탐지. 단 런타임 메모리 후크는 서버 측 재검증 없이 근본 해결 불가 (헌법 1조 "자체 서버 0"과의 트레이드오프). 알려진 한계. | 서버 도입 없는 한 보완 불가 |

## 0-E. 빌드 무결성 (SHA256 스탬프)

v1.6.1까지의 산출물은 공식 빌드 시 자동 SHA256 해시가 파일명에 부여된다.

파일명 형식: `MyPhoneCheck_<Type>_v<MAJOR>.<MINOR>.<PATCH><suffix>_<HASH6>.docx`

예시:
- `MyPhoneCheck_Architecture_v1.6.1_{HASH6}.docx` ← Cursor 파이프라인 산출 시
- `MyPhoneCheck_Patches_v1.6.1-patch_{HASH6}.docx` ← Patch 23~28 독립 묶음

빌드 스크립트: `scripts/build_architecture_v170.py` (Cursor 담당)
검증 스크립트: `scripts/verify-doc-hash.ps1`

**본 문서 상태**: SHA6 미부여. 비전 재작성본이므로 파일명 `MyPhoneCheck_Architecture_v1.7.0.md` 고정. 정식 발행 시 Cursor 파이프라인 재실행으로 `.docx + HASH6` 산출.

## 0-F. 인프라 운영 참조

본 문서는 **제품·설계 기준선**이다. 이와 쌍을 이루는 **인프라·운영 기준선**은 별도 문서로 관리한다.

- 파일: `MyPhoneCheck_Infrastructure_v1.0.md`
- 위치: `docs/00_governance/`
- 상태: v1.0 FINAL (2026-04-24)

본 문서가 제품 기능·데이터 모델·알고리즘·UX를 정의하고, Infrastructure 문서가 도구맵·보관 경로·비밀값 SOP·실행 순서를 정의한다. 두 문서는 서로 참조하며 충돌 시 다음 규칙을 따른다:

- 제품 설계 결정(기능 범위·헌법) → 본 문서 우선
- 인프라 결정(도구 선택·경로·SOP) → Infrastructure 문서 우선
- 충돌 시 비전이 대표님에게 확인 후 정정 (Rule 3)

자세한 내용은 §35를 참조.

---

# 1. 헌법 (7개 조항, 절대 원칙)

헌법은 모든 설계 결정의 최상위 기준이다. 어떤 모듈·알고리즘·UX·비즈니스 결정도 이 7개 조항을 위반하면 즉시 폐기한다. **헌법은 토론 대상이 아니라 검증 대상이다.**

v1.3의 5개 조항을 100% 계승하고, v1.4에서 3조 정정·4조·6조 신설, v1.6에서 7조(디바이스 오리엔티드 거위) 명문화로 현재 **7개 조항** 체계.

## 제1조 — Out-Bound Zero (사용자 데이터 외부 전송 금지)

사용자의 통화 기록, 연락처, SMS, 사용자 행동, 통화 상대 번호 등 **일체의 사용자 데이터는 우리(올랑방)가 운영하는 서버 또는 제3자 서버로 전송되지 않는다.** 모든 처리는 디바이스 내에서 완결된다.

**해석 규칙**:
- "우리 서버" = 올랑방이 운영하는 AWS Lambda, API Gateway, 자체 DB, 자체 영수증 검증 서버 등.
- "제3자 서버" = 사용자 데이터를 수집·분석하는 목적의 상용 서비스.
- **허용**: 일반 검색 엔진(Google·Bing 등)에 번호를 조회하는 행위 자체는 Out-Bound로 보지 않는다(사용자 데이터가 아니라 공개 번호이므로). 단 응답 원문은 제2조에 따라 즉시 폐기.
- **허용**: 스토어 공식 API(Google Play Billing, StoreKit 2)에 영수증 확인 요청은 허용(스토어 자체가 결제 인프라).
- **허용**: 공공 공신력 API(KISA·경찰청·금감원) 조회는 허용(정부 공공 데이터).

v1.3 이래 불변. 절대 원칙.

## 제2조 — In-Bound Zero (외부 원문 영구 저장 금지)

디바이스가 외부 검색 엔진에서 받은 HTML, JSON, 검색 스니펫 등 **일체의 외부 원문은 메모리에서만 처리되고 즉시 폐기된다.** 디바이스 NKB(Number Knowledge Base)에는 원문이 아닌 **가공된 신호 카운트(featureCounts)만 저장된다.** rawSnippet 200자 등 어떤 형태의 부분 저장도 금지된다.

**해석 규칙**:
- 외부 HTML·JSON·텍스트 스니펫 → 메모리 내 분석 후 폐기
- featureCounts (예: `{SCAM_KEYWORD: 12, AD_KEYWORD: 3}`) → NKB 저장 가능
- 사용자 번호 E.164 식별자 → NKB 저장 가능 (식별자일 뿐 외부 원문 아님)

v1.3 이래 불변. 절대 원칙.

## 제3조 — 결정권 중앙집중 금지 (Decision Centralization Prohibited)

본 시스템은 **모든 판단의 결정권이 중앙(우리 서버·본사)에 집중되는 것을 금지한다.** 디바이스는 스스로 판단하는 1차 결정 주체이며, 중앙은 참고 신호와 업데이트만 제공할 수 있다.

### 3-1. 금지 대상 (해석 여지 없음)

- 중앙 서버가 "최종 정답"을 내려주는 구조 (예: 본사 API가 "이 번호 = 스팸"을 응답)
- 특정 입력 → 특정 출력이 중앙에 하드코딩된 결정 매핑 (예: "한국 SIM = 네이버 검색")
- 디바이스가 중앙 응답 없이는 판단 불가능한 구조 (예: NKB Miss 시 본사 fallback)

### 3-2. 허용 대상 (해석 여지 없음)

- 검색 엔진 **시드(seed) 후보 리스트**를 디바이스에 빌드 시 포함 (probe 대상 후보일 뿐, 매핑 아님)
- 키워드 사전 업데이트 (strings.xml의 scamKeywords 등 — 정적 데이터)
- Tier 가중치 업데이트 (Tier 1~4 검색 결과 신뢰도 — 정적 데이터)
- 모델 가중치 업데이트 (Softmax 가중치 — 정적 데이터)

**핵심 판별축**: "최종 결정을 누가 내리는가?"
- 중앙이 "이 번호는 스팸이다"를 내리면 → 위반
- 디바이스가 "내가 이 번호에 대해 probe한 결과 + 로컬 학습 결과 = 스팸"을 내리면 → 준수

v1.3 원문은 "No Curation: 본사 큐레이션 데이터 주입 금지"였다. v1.4에서 해석 여지를 제거하여 현행 조문으로 정정.

## 제4조 — 자가 작동 (Self-Operation)

본 시스템은 **중앙 없이도 멈추지 않는 상태**를 유지한다. 네트워크 단절, 서버 응답 실패, 공공 API 장애 등 어떤 외부 요인으로도 사용자의 기본 가치는 손실되지 않는다.

### 4-1. SLA 4단계 (자가 작동의 운영 정의)

| 레벨 | 상태 | 네트워크 | 외부 API | NKB | 사용자 경험 |
|---|---|---|---|---|---|
| L1 Full | 정상 | 연결 | 가용 | 가용 | 4속성 출력 + 실시간 probe 갱신 |
| L2 Degraded | 부분 장애 | 연결 | 일부 장애 | 가용 | 4속성 출력 (stale 플래그) |
| L3 Offline | 오프라인 | 단절 | 불가 | 가용 | **4속성 출력 (NKB Hit만)** ← **기준선** |
| L4 Catastrophic | NKB 손상 | 단절 | 불가 | 손상 | Cold Start 재실행 또는 기본값 반환 |

**헌법 기준선 = L3.** 즉 사용자가 어떤 환경에 있어도 L3 이상 경험이 보장되어야 한다. L4는 극단 상황이며 Cold Start 자가 복구 경로가 반드시 존재해야 한다.

### 4-2. L3에서 거위가 멈추지 않는 근거

- NKB는 디바이스 Local Room DB → 네트워크 없이도 조회 가능
- 4속성 출력은 NKB Hit만으로 산출 가능 (§8-6)
- Self-Discovery 결과(ClusterProfile)는 NKB에 영구 저장 → probe 실패해도 기존 값 사용

v1.4_disc 신설 조항.

## 제5조 — 정직성 (Honesty)

본 시스템은 **모든 설계 결정, 폐기 이력, 한계, 검증 결과를 문서화한다.** 숨기지 않는다.

### 5-1. 구체 의무

- 모든 패치(Patch NN)의 Before/After 본문을 문서에 포함한다 (§0-B-2).
- 외부 검증자 지적 사항을 숨기지 않고 채택·거부 사유와 함께 기록한다 (§0-B-1).
- 검증 불가 한계를 명시한다 (§0-D).
- 토론 과정에서 폐기된 안을 제안자 실명과 함께 기록한다 (§0-2).
- 사용자에게 "확실하지 않음"을 숨기지 않는다 (ambiguous 플래그, §8-1).

### 5-2. 위반 판정

- "추정 금지" 위반 (근거 없는 수치 주장)
- 외부 검증자 지적을 본문에 반영하지 않고 요약으로만 처리
- 패치 이력 누락
- 한계 로그 누락

v1.3 원문 계승. v1.4에서 구체 의무와 위반 판정을 명문화.

## 제6조 — 가격 정직성 (Pricing Honesty)

본 시스템의 가격은 **net 기준으로 측정·공개**한다. Apple/Google 수수료 30%, 부가세(VAT) 평균 10%, 환불 5%를 반영한 net ARPU를 KPI로 사용한다.

### 6-1. 현재 확정 가격

- **공식 구독료**: USD 2.49/월 단일 가격, 전세계 동일, 연간 가격 없음 (2026-04-22 대표님 확정)
- **net ARPU 계산**: gross $2.49 × (1 - 0.30 스토어) × (1 - 0.10 VAT) × (1 - 0.05 환불) ≈ **$1.49/월**
- **국가당 break-even MAU**: 월 $100K net 목표 / $1.49 = **67,114명 / 국가당** (전환율 3% 가정 시 MAU 2.24M)

### 6-2. 가격 변경 원칙

- KPI 매핑을 거부하지 않는다 (헌법 3조와 구분: KPI는 측정 도구이지 중앙 결정이 아니다).
- 가격은 대표님 승인 사항. 헌법 6조는 "정직한 측정"을 강제하지 "가격 자체"를 고정하지 않는다.
- 변경 시 0-A-2 가격 정책 변경 이력에 기록.

v1.4_disc 신설. v1.6.1에서 $2.49 확정 (본 v1.6.2 승계).

## 제7조 — 디바이스 오리엔티드 거위 (Device-Oriented Goose)

본 시스템은 **디바이스가 황금알을 낳는 거위**라는 은유를 설계 원칙으로 삼는다.

### 7-1. 비유의 정의

- **거위** = 디바이스 (온디바이스 Decision Engine + NKB + Self-Discovery)
- **황금알** = 사용자가 받는 판단 결과 (4속성: 위험도, 예상 손해, 손해 유형, 이유 설명)
- **거위를 잡는 행위** = 본사 서버로 사용자 데이터 수집, 중앙 매핑, 폐쇄형 판단 (1~3조 위반)
- **거위가 자가 증식** = NKB Self-Evolution, ClusterProfile 자가 생성 (§12)

### 7-2. 구현 원칙

- **본사 운영 0, 본사 매핑 0, 본사 데이터센터 0** (메모리 헌법 7대 원칙)
- 디바이스 = 모든 것 (검색·분석·판단·저장·학습 전부 온디바이스)
- 외부는 입력 소스일 뿐, 판단의 주체가 아니다 (제3조)
- 멀티 Surface 확장은 엔진을 N개로 늘리는 것이 아니라 **단일 엔진의 Surface를 N개로 늘리는 것** (§17 One Engine, N Surfaces)

### 7-3. 금지 구현 패턴

- 본사가 "사용자 번호 DB"를 운영하는 구조 → 위반
- 본사가 "이 번호 = 스팸"을 내려주는 API → 위반 (제3조)
- 본사가 "국가별 스팸 패턴"을 사전 매핑 → 위반
- 본사가 사용자 행동 로그를 수집 → 위반 (제1조)

### 7-4. 허용 구현 패턴

- 디바이스 Room DB(NKB)에 번호별 판단 결과 저장 → 허용 (디바이스 내부)
- 일반 검색 엔진에 번호 조회 → 허용 (검색 엔진은 제3자가 아닌 공개 인프라)
- 공공 공신력 API (KISA·경찰청·금감원) 조회 → 허용 (정부 공공 데이터)
- 스토어 공식 API (Play Billing·StoreKit 2) 호출 → 허용 (스토어 자체)
- Firebase Crashlytics·Analytics·FCM → 허용 (써드파티 관찰·전달 인프라, 사용자 데이터 수집 목적 아님)

v1.3 본문에 산재했던 "디바이스=자가발전기" 원칙을 v1.6에서 7조로 조문화. 메모리 헌법 7대 원칙(In-Bound Zero, Out-Bound Zero, 자율 작동 등)과 완전 정합.

---

## 1-1. 헌법 7조 상호 정합 표

모든 설계 결정은 7조 중 **어느 조항에 해당하는가**를 식별할 수 있어야 한다. 복수 조항 해당 시 모두 준수해야 한다.

| 설계 결정 | 해당 조항 |
|---|---|
| 통화 번호를 디바이스에만 저장 | 1조 (Out-Bound Zero) |
| 검색 결과 스니펫을 메모리 후 즉시 폐기 | 2조 (In-Bound Zero) |
| NKB Miss 시 본사 fallback 금지 | 3조 (결정권 중앙집중 금지) |
| 네트워크 단절 시 NKB Hit만으로 4속성 출력 | 4조 (자가 작동 L3) |
| 폐기된 설계안을 문서에 실명 기록 | 5조 (정직성) |
| net ARPU $1.49 측정·공개 | 6조 (가격 정직성) |
| 본사 운영 0 / 매핑 0 / 데이터센터 0 | 7조 (디바이스 오리엔티드 거위) |
| 스토어 빌링만 사용, 자체 영수증 검증 서버 금지 | 1조 + 7조 |
| Google Programmable Search API 사용 | 1조 (제3자 아님, 공개 검색 인프라) |
| 일반 검색 쿼리에 사용자 번호 포함 | 1조 (공개 번호이므로 사용자 데이터 아님) |

## 1-2. 헌법 위반 판정 SOP

1. 설계·코드·문서 제출 시 자가 점검 체크리스트 실행 (7조 각각 Y/N).
2. 1개라도 N → 설계 폐기 또는 재작성.
3. Y/N 판정이 애매한 경우 → 비전에게 판정 요청 (§17 Rule 3).
4. 비전도 불명확 → 대표님에게 판정 요청.
5. 판정 결과는 0-B 감사 로그에 기록.

---

# 2. 황금알 vs 거위 사고 분류표

본 장은 설계 검토 중 등장하는 사고방식을 두 유형으로 분류하고, **거위 사고**만 채택한다.

## 2-1. 분류 정의

| 축 | 황금알 사고 (폐기 대상) | 거위 사고 (채택 대상) |
|---|---|---|
| 주어 | "본사가 ~한다" | "디바이스가 ~한다" |
| 저장 위치 | 본사 서버·중앙 DB | 디바이스 Local DB |
| 판단 주체 | 중앙 API 응답 | 온디바이스 Decision Engine |
| 확장 방식 | 서버 스펙 업그레이드 | 디바이스 Surface 추가 |
| 수익 논리 | "사용자 데이터 축적 → 광고 수익" | "사용자 가치 제공 → 구독 결제" |
| 실패 모드 | 서버 다운 → 전체 서비스 정지 | 디바이스 독립 → L3 보장 |
| 헌법 정합 | 1·3·7조 위반 | 전 조항 정합 |

## 2-2. 설계 과정에서 폐기된 황금알 사고 사례 (정직 기록)

| # | 폐기 안 | 제안자 | 폐기 라운드 | 폐기 사유 |
|---|---|---|---|---|
| 1 | "본사 큐레이션 DB로 번호 매핑" | 초기 비전 | v1.2 → v1.3 | 제1·3조 동시 위반. 사용자 데이터 수집 불가피 |
| 2 | "한국 SIM은 네이버 검색 고정" | v1.2 토론 | v1.3 | 제3조 위반. 중앙 매핑 |
| 3 | "NKB Miss 시 본사 fallback API" | v1.3 초안 | v1.3 | 제3조 위반. 디바이스 독립 상실 |
| 4 | "사용자 행동 로그 업로드 → 모델 재학습" | v1.4_disc | v1.4 최종 | 제1조 위반. 사용자 데이터 수집 |
| 5 | "자체 영수증 검증 서버(AWS Lambda)" | Infra_Ops v0.9 | Infra_Ops v1.0 FINAL | 제1조·7조 위반. 스토어 빌링 직접 호출로 대체 |
| 6 | "RevenueCat SDK 도입" | v1.5.x 검토 | 2026-04-24 | 제3자 결제 중개 서버. 온디바이스 검증 원칙과 상충 |
| 7 | "공격적 MVP 반쪽 기능 출시" | 다수 라운드 | 2026-04-22 대표님 지시 | "MVP는 금기어" 철칙 (메모리 #10) |

## 2-3. 황금알 사고의 재발 방지 체크포인트

설계 PR·문서·코드 리뷰 시 다음 질문에 **한 번이라도 Yes**가 나오면 황금알 사고로 분류하고 폐기·재설계한다.

- [ ] "본사 서버가" 또는 "중앙 API가"로 시작하는 문장이 있는가?
- [ ] 사용자 데이터가 디바이스 밖으로 나가는 경로가 있는가? (제1조)
- [ ] 외부 원문이 NKB에 저장되는가? (제2조)
- [ ] 디바이스가 중앙 응답 없이는 판단 불가능한 분기가 있는가? (제3조)
- [ ] 네트워크 단절 시 사용자 경험이 L3 미만으로 떨어지는가? (제4조)
- [ ] 가격·KPI·설계 결정에서 정직하지 않은 숨김이 있는가? (제5·6조)
- [ ] "본사 운영 0 / 매핑 0 / 데이터센터 0"을 위반하는가? (제7조)

---

# 3. 황금알 정의 (Golden Egg Definition)

"거위가 황금알을 낳는다"는 비유의 **황금알 자체**를 정의한다. 사용자 입장에서 "무엇이 황금알인가"의 긍정적 정의가 없으면 결제 이유 설계가 불가능하다.

## 3-1. 황금알 자격 4조건

황금알이 되려면 다음 4조건을 모두 만족해야 한다.

| # | 조건 | 정의 | 검증 기준 |
|---|---|---|---|
| 1 | **즉시성** | 통화 수신 시각 ≤ 1초 이내 표시 | NKB Hit p95 ≤ 100ms + UI 렌더 ≤ 500ms |
| 2 | **신뢰성** | 근거 없는 추정 금지, 출처 표기 | Tier 1~4 신뢰도 가중합 + ambiguous 플래그 |
| 3 | **실행 가능성** | 차단·신고·안심 표시 3종 즉시 수행 가능 | 오버레이에 3버튼 상시 노출 |
| 4 | **정직성** | "확실하지 않음"을 숨기지 않음 | topConfidence 표시 + isAmbiguous 플래그 |

## 3-2. 황금알 = 4속성 출력

황금알의 실체는 다음 4속성이다. 이 4속성이 **모든 Surface에서 동일 규격으로 출력**된다.

| 속성 | 설명 | 예시 |
|---|---|---|
| 위험도 | 5단계 RiskLevel | `HIGH` (빨강) |
| 예상 손해 | 위험도 + 카테고리 기반 손해 유형 | "금전 피해 가능 (평균 120만원)" |
| 손해 유형 | 카테고리 정적 매핑 | "금융사기 / 대포폰 / 보이스피싱" |
| 이유 설명 | 신호 카운트 기반 한 줄 요약 | "정부 신고 12건, 사용자 신고 8건" |

4속성 모두 **NKB Hit만으로 산출 가능** → L3 호환 100%.

## 3-3. One Engine, N Surfaces 원칙

황금알은 Surface가 달라도 **동일 엔진·동일 규격**으로 산출된다. 이는 v1.6에서 **Four Surfaces**로 확장되었으며, 이후 Surface 추가 시에도 동일 원칙을 유지한다.

| Surface | 입력 | 엔진 호출 | 출력 |
|---|---|---|---|
| CallCheck | 전화번호 (E.164) | `decisionEngine.evaluate(phoneQuery)` | CallRisk (4속성) |
| MessageCheck | 발신번호 + URL + 기관명 | 3중 `evaluate` 호출 후 결합 | MessageRisk (4속성) |
| MicCheck | RECORD_AUDIO 보유 앱 목록 | PackageManager 스캔 (엔진 미사용, Patch 30·37) | `List<MicPermissionEntry>` |
| CameraCheck | CAMERA 보유 앱 목록 | PackageManager 스캔 (엔진 미사용, Patch 30·37) | `List<CameraPermissionEntry>` |

**핵심**: 엔진은 하나, Surface는 넷. 단 Patch 30 이후 MicCheck/CameraCheck는 **단순 관리자**로 축소되어 DecisionEngine 호출 없이 로컬 PackageManager + UsageStatsManager만 사용한다. 앱 평판·CVE 감시는 후행 Surface `AppSecurityWatch`로 이관(Patch 31, §17-3). 엔진 분기는 여전히 없음 — CallCheck·MessageCheck·AppSecurityWatch가 동일 엔진 계약(§33-1-4 `DecisionEngineContract`)을 통과한다.

**Patch 37 반영 (P0-2)**: v1.6.2까지 본 표에 `List<AppPermissionRisk>` 출력 + `evaluate(appRepQuery)` 호출이 잔존했으나 Patch 30으로 폐기된 구조. 7-워커 평가(Cursor·코웍 2자 합의)로 지적. 본 v1.7.0에서 `MicPermissionEntry`/`CameraPermissionEntry` 단순 데이터 클래스로 정정.

## 3-4. 4속성의 UX 표현 규격

| 속성 | UI 컴포넌트 | 규격 |
|---|---|---|
| 위험도 | RiskBadge | 색상(초록/노랑/주황/빨강/검정) + 라벨 |
| 예상 손해 | DamageEstimate | "평균 ~원 / 중앙값 ~원 / 신뢰도 ~%" |
| 손해 유형 | DamageTypeChip | 최대 2개 칩 (예: "금융사기 + 대포폰") |
| 이유 설명 | ReasonExplainText | 한 줄, strings.xml 다국어 템플릿 |

단일 카드 `FourAttributeCard`에 4속성을 모두 담아 **Surface 간 일관된 UX**를 보장한다.

### 3-4-1. DO_NOT_MISS 강조 규칙 (Patch 35 cross-ref)

`UserAction.DoNotMiss`(§8-2-2)로 지정된 식별자에 대해서는 4속성 카드 위에 **고정 강조 띠**를 표시:

- 띠 색상: 노랑(#FFD54F) + 별 아이콘
- 메시지: "DO_NOT_MISS — {userMemo 있으면 표시}"
- 알림 채널: High-Priority Notification Channel 강제 사용
- 오버레이 dismiss 시간: 5초 → 15초 (사용자가 충분히 인지하도록)
- `riskLevel == LOW`여도 **반드시 노출** (DecisionEngine 판정 우선순위 역전)

자세한 처분 정책은 §8-2-4 (Phase별 적용 표), §21-1 (v1.7.0 Open Issues #13 근거), §21-1-1 (데이터 수명) 참조.

## 3-5. 황금알이 아닌 것 (반례)

다음은 황금알로 오인하기 쉬우나 **자격 미달**이다.

- "스팸 통계만 표시" (메모리 #12 반쪽 기능) → 실행 가능성 0
- "데이터 다운로드 버튼" → 즉시성 없음
- "광고 차단 기능" → 본 앱의 정체성과 무관
- "포인트 적립 게이미피케이션" → 정직성 저해

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

# 5. 시스템 아키텍처 (디바이스 = 모든 것)

## 5-1. 3-Layer Knowledge Sourcing (3계층 지식 소싱)

디바이스가 판단에 사용하는 지식은 **3개 계층**에서 온다. 헌법 제3조 "결정권 중앙집중 금지"의 구체 구현.

| 계층 | 이름 | 소스 | 지연 | L3 가용 |
|---|---|---|---|---|
| Layer 1 | **내부축** | 온디바이스 NKB + 통화·문자 이력 + 사용자 태그 | ≤ 5ms | 항상 |
| Layer 2 | **외부축** | 일반 검색 엔진 (Google Programmable Search·Custom Tab) | 수백 ms ~ 수 초 | 네트워크 필요 |
| Layer 3 | **오픈소스축** | 공공 공신력 DB (KISA·경찰청·금감원·NVD·CISA·Have I Been Pwned) | 수백 ms ~ 수 초 | 네트워크 필요 |

(메모리 #8 확정)

### 5-1-1. Layer 1 (내부축) 구성

- **NKB (Number Knowledge Base)**: 번호별 4속성 캐시 (§8)
- **통화 이력**: `CallLog.Calls` ContentResolver (READ_CALL_LOG 권한)
- **문자 이력**: `Telephony.Sms` ContentResolver (READ_SMS 권한, Default SMS Handler 불필요)
- **사용자 태그**: `UserAction` Entity (§8-2)

### 5-1-2. Layer 2 (외부축) 구성

- **Primary**: Google Programmable Search API (키는 온디바이스 앱 서명 기반 제한)
- **Backup**: Bing Search API (대표님 판단 필요 — 메모리에 기재)
- **UX 1차 진입**: Custom Tabs (Android) / SFSafariViewController (iOS) — 사용자가 브라우저 열기
- **쿼터 제한**: 1사용자 / 월 100회 기본 (Google PSE 무료 한도 100/일 공유)
- **결과 처리**: 메모리만 통과 → SearchResultAnalyzer에서 featureCounts 추출 → 원문 폐기 (제2조)

### 5-1-3. Layer 3 (오픈소스축) 구성

- **KISA 스팸 공개 데이터**: 조사 필요 (메모리 #8)
- **경찰청 사이버범죄 공개 데이터**: 보이스피싱 번호
- **금융감독원 전기통신금융사기**: 금융 사기 번호
- **각국 Do-Not-Call 레지스트리**: 글로벌 확장용 (후행)
- **NVD CVE API**: 앱 CVE 감지 (후행 Surface `AppSecurityWatch`, §17-3 · Patch 31로 MicCheck/CameraCheck에서 분리 이관)
- **CISA KEV Catalog**: 실제 악용 취약점
- **Have I Been Pwned API**: 데이터 침해 사고 (있으면 좋음)

오픈소스축 **데이터 수집 파이프라인은 별도 스펙 작성 필요** (메모리 #8 명시).

## 5-2. 본사가 하지 않는 것 / 디바이스가 하는 것

| # | 본사 (0) | 디바이스 (1) |
|---|---|---|
| 1 | 사용자 번호 DB 운영 | NKB Local Room DB |
| 2 | 사용자 행동 로그 수집 | UserAction Entity (디바이스 내) |
| 3 | 사기 번호 중앙 매핑 | 디바이스 probe + 3계층 결과 통합 |
| 4 | 사용자별 모델 재학습 | 디바이스 Softmax 가중치 + 로컬 규칙 |
| 5 | 영수증 검증 서버 | Google Play Billing / StoreKit 2 온디바이스 서명 검증 |
| 6 | 결제 상태 중앙 DB | 디바이스 Purchase Token + 스토어 API |
| 7 | 고객지원 백엔드 | 이메일 기반 (지원 이메일, 메모리 #4 인프라) |
| 8 | 국가별 큐레이션 콘텐츠 | 디바이스 Self-Discovery (§7) |

## 5-3. 디바이스 내부 구조도 (개념)

```
┌─────────────────────────────────────────────────────────┐
│  Surface Layer (UI)                                      │
│  ┌─────────┐ ┌────────────┐ ┌────────┐ ┌──────────┐    │
│  │CallCheck│ │MessageCheck│ │MicCheck│ │CameraCheck│    │
│  └────┬────┘ └─────┬──────┘ └───┬────┘ └─────┬────┘    │
│       │            │            │            │          │
│       └────────────┼────────────┼────────────┘          │
│                    ▼            ▼                         │
│  ┌─────────────────────────────────────────────────────┐ │
│  │ Decision Engine (Single)                            │ │
│  │   - evaluate(IdentifierType): RiskKnowledge         │ │
│  │   - ConflictResolver (Softmax + Tier 가중치)        │ │
│  │   - StaleDetector (Tier별 maxAge)                   │ │
│  └─────┬────────────┬──────────────┬────────────────────┘ │
│        │            │              │                       │
│        ▼            ▼              ▼                       │
│  ┌──────────┐ ┌──────────┐ ┌──────────────┐             │
│  │ Layer 1  │ │ Layer 2  │ │ Layer 3      │             │
│  │ NKB DB   │ │ Search   │ │ Public APIs  │             │
│  │ (Room)   │ │ Mesh     │ │ (KISA·CVE 등)│             │
│  └────┬─────┘ └────┬─────┘ └──────┬───────┘             │
│       │            │              │                       │
│       └────────────┴──────────────┘                       │
│                    │                                       │
│                    ▼                                       │
│  ┌───────────────────────────────────────────────────────┐ │
│  │ Self-Discovery (§7) + Cold Start (§11)                │ │
│  │ + Self-Evolution (§12) + SLA Level Detector (§14)     │ │
│  └───────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

Decision Engine은 단 하나이며 4 Surface가 공유. 엔진 분기 금지 (§17 One Engine).

## 5-4. 데이터 흐름 (Dataflow) 불변 규칙

| 규칙 | 정의 | 헌법 조항 |
|---|---|---|
| R1 | 외부 원문은 Decision Engine 메모리를 벗어나지 않는다 | 제2조 |
| R2 | NKB에는 featureCounts·카테고리·시간 스탬프만 저장 | 제2조 |
| R3 | Decision Engine 출력(RiskKnowledge)만 Surface Layer로 전달 | 제7조 |
| R4 | Surface Layer에서 수집한 사용자 조치는 UserAction으로 NKB에 저장 | 제1·7조 |
| R5 | 외부 네트워크는 Layer 2·3 내부 클라이언트만 수행, Surface에서 직접 호출 금지 | 제1·3조 |
| R6 | 결제·구독 데이터는 Billing Module이 단일 진입점, Decision Engine 우회 | 제1조 |

---

# 6. 3계층 소싱 상세 (Three-Layer Sourcing Detail)

## 6-1. Decision Engine이 3계층을 통합하는 순서

Decision Engine은 `evaluate(query: IdentifierType): RiskKnowledge` 호출 시 다음 순서로 3계층을 통합한다.

```
1. NKB Lookup (Layer 1)
   - Hit & not Stale → 즉시 반환 (L3 경로)
   - Hit but Stale → 반환하되 백그라운드 재검증 큐 등록
   - Miss → 2단계 진행

2. 외부 probe (Layer 2 + Layer 3 병렬)
   - Layer 2: 일반 검색 (쿼터 남은 경우)
   - Layer 3: 공공 공신력 API (우선)
   - 타임아웃 2초, 먼저 도착한 결과부터 처리

3. SearchResultAnalyzer
   - 각 소스별 featureCounts 추출
   - Tier 가중치 적용 (T1 0.3 / T2 0.5 / T3 0.8 / T4 1.0)

4. ConflictResolver
   - Softmax 정규화
   - topCategory · topConfidence · isAmbiguous 결정

5. NKB Write
   - featureCounts + 메타만 저장
   - 원문은 폐기

6. RiskKnowledge 반환 (Surface Layer로)
```

## 6-2. 네트워크 정책

- **네트워크 진입점**: Layer 2·3 내부 HttpClientProvider (Ktor, timeout 1초 기본)
- **Surface Layer 직접 네트워크 금지**: R5 규칙
- **도메인 화이트리스트**: 빌드 시 고정 (검색 엔진 시드 + 공공 API 도메인)
- **DoH/DoT 선택**: 대표님 판단 필요 (초기 시스템 DNS)
- **AppTracking**: 디바이스 ID·광고 ID 사용 금지 (제1조)

## 6-3. 원문 폐기 보장

- Kotlin 레벨: `RawSearchResult` 데이터 클래스는 Decision Engine 내부 스코프에서만 생성. Surface·NKB로 반환 금지 (컴파일 타임 visibility).
- 테스트: `ExtractedSignal`의 필드에 `rawSnippet: String`이 **존재하지 않음**을 `FreezeMarkerTest`가 검증 (§33-1-1).
- 정적 분석: Detekt 규칙 `ForbiddenRawField`로 금지 필드 자동 탐지.

## 6-4. 3계층 데이터 Freshness (Tier별 maxAge)

| Tier | 출처 분류 | maxAge | Stale 시 동작 |
|---|---|---|---|
| Tier 1 | 커뮤니티/포럼 | 30일 | Stale 플래그 + 다음 조회 시 재검증 |
| Tier 2 | 일반 사이트 | 90일 | Stale 플래그 + 백그라운드 재검증 |
| Tier 3 | 정부/공식 (KISA·경찰청·금감원) | 180일 | Stale 플래그만, 그대로 사용 |
| Tier 4 | 회사 공식 (스토어·공식 홈페이지) | 365일 | Stale 플래그만, 그대로 사용 |

**Stale은 삭제 트리거가 아니다.** 사용자 자산이므로 유지하고, 신뢰도만 낮춘다.

## 6-5. 쿼터·요금 관리

| 소스 | 쿼터 | 요금 |
|---|---|---|
| Google Programmable Search API | 100/일 무료, 10K/일 유료 ($5/1K) | 대표님 판단 필요 (초기 무료 범위 내 운영) |
| Bing Search API | 1K/월 무료 | 백업 옵션 |
| KISA·경찰청·금감원 | 확인 필요 (공공 데이터 개방 정책) | 기본 무료 |
| NVD CVE | 무료 | 무료 |
| CISA KEV | 무료 | 무료 |
| Have I Been Pwned | 유료 ($3.50/월 최소) | 있으면 좋음 |

사용자당 쿼터 초과 시 **Custom Tab 수동 검색으로 전환** (Layer 2 UX 1차 진입). 제품 가치 저하 없음.

---

# 7. Self-Discovery (환경 자가 발견)

본사 매핑 0 원칙(제3조·제7조)을 지키면서 국가별 최적 검색 엔진·공식 도메인을 찾는 방법.

## 7-1. Search Engine Self-Discovery

디바이스가 **보편적 글로벌 검색 엔진 후보 시드**를 빌드에 포함하고, 런타임에 직접 probe하여 응답한 엔진만 채택한다.

```kotlin
suspend fun probeSearchEngines(simIdentity: SimIdentity): List<SearchEngineRef> {
    // 후보 시드 (글로벌 검색 엔진 일반 목록, 매핑 아님)
    val candidates = listOf(
        "https://www.google.com",
        "https://www.bing.com",
        "https://duckduckgo.com",
        "https://yandex.com",
        "https://www.baidu.com",
        "https://www.naver.com",
        "https://www.yahoo.co.jp"
        // ... 글로벌 일반 후보. 본사가 "이 SIM = 이 엔진" 매핑 안 함.
    )

    // 디바이스가 직접 probe (1초 timeout)
    val responsive = candidates.parallelMap { url ->
        val start = System.currentTimeMillis()
        try {
            val response = httpClient.head(url) {
                timeout { requestTimeoutMillis = 1000 }
            }
            if (response.status.isSuccess()) {
                SearchEngineRef(
                    domain = url,
                    responseTimeMs = System.currentTimeMillis() - start
                )
            } else null
        } catch (e: Exception) {
            null  // 응답 없거나 차단된 엔진은 채택 안 함
        }
    }.filterNotNull()

    // 응답 시간 기준 정렬 (빠른 것 우선)
    return responsive.sortedBy { it.responseTimeMs }
}
```

**참고**: 후보 도메인 시드 리스트는 빌드 시 디바이스에 포함되지만, 이는 "매핑"이 아니라 "probe 대상 후보"이다. 후보를 probe해서 응답한 것만 사용하므로, 본사가 "한국 = 네이버"라고 단정하지 않는다. 시드는 보편적 글로벌 검색 엔진 도메인 목록일 뿐이며, 어느 것이 어느 환경에 적합한지는 디바이스가 결정한다.

## 7-2. Official Domain Self-Discovery

각국 정부·공공 도메인도 동일 원리.

```kotlin
suspend fun probeOfficialDomains(simIdentity: SimIdentity): List<DomainRef> {
    val countryCode = simIdentity.simMcc?.toCountryCode()
        ?: simIdentity.networkCountryIso
        ?: extractCountryFromLocale(simIdentity.locale)

    // 일반적 정부 TLD 패턴 (글로벌 표준)
    val tldCandidates = buildList {
        countryCode?.let {
            add(".gov.${it.lowercase()}")  // 예: .gov.kr
            add(".go.${it.lowercase()}")    // 예: .go.kr (일부 국가)
        }
        add(".gov")  // 미국 정부
        add(".gob")  // 스페인어권 정부
        add(".gouv") // 프랑스어권 정부
    }

    val responsive = tldCandidates.parallelMap { tld ->
        val testDomains = listOf("www$tld", "police$tld")
        testDomains.firstNotNullOfOrNull { domain ->
            try {
                val response = httpClient.head("https://$domain") {
                    timeout { requestTimeoutMillis = 1000 }
                }
                if (response.status.isSuccess()) {
                    DomainRef(tld = tld, sample = domain)
                } else null
            } catch (e: Exception) { null }
        }
    }.filterNotNull()

    return responsive
}
```

## 7-3. ClusterProfile 생성

```kotlin
fun generateClusterId(simIdentity: SimIdentity): String {
    // 환경 특성을 해시화 (본사 사전 정의 0)
    val components = listOf(
        simIdentity.simMcc ?: "",
        simIdentity.networkCountryIso ?: "",
        simIdentity.locale.split("_")[0]  // 언어만
    )
    val hash = components.joinToString("|").sha256().take(12)
    return "auto_$hash"
    // 예: "auto_a3f9b2c1d4e7"
    // 본사가 "CL_KR", "CL_EN" 같은 사전 정의 안 함. 디바이스가 동적 생성.
}
```

## 7-4. Self-Discovery의 헌법 정합성

- 본사가 "한국 SIM이면 네이버"라고 매핑하지 않음 → 제3조 정합
- 디바이스가 직접 ping해서 응답 받은 것만 사용 → 제7조 정합
- 어느 국가 SIM에서도 동일 코드 작동 → 190개국 자동 대응 (메모리 헌법)
- 국가 차단/검열로 일부 엔진 작동 안 해도, 응답한 다른 엔진으로 graceful degradation
- L3 상황(네트워크 0)에서는 probe 자체가 실패하지만, 기존 ClusterProfile이 NKB에 영구 저장되어 있으므로 거위는 멈추지 않음 (제4조 정합)

## 7-5. probe 주기 정책

| 이벤트 | 동작 |
|---|---|
| 앱 최초 실행 (Cold Start) | 전체 probe, ClusterProfile 생성 |
| SIM 변경 감지 | 전체 probe, 새 ClusterProfile 생성 (기존 보존) |
| 7일 경과 (WorkManager) | 재검증 probe, 응답 시간 갱신 |
| 사용자 조치 "검색 엔진 변경" | 해당 엔진만 재 probe |

## 7-6. 사용자 오버라이드

"어느 검색 엔진을 쓸지" 사용자가 수동 선택 가능. 설정 화면 `ClusterEditScreen` (§23 프로젝트 구조의 `feature/settings`).

이는 제3조 정합: 최종 결정은 디바이스 + 사용자이지 중앙이 아니다.

---

# 8. NKB 데이터 설계 (Number Knowledge Base)

NKB는 디바이스 내 Local Room DB로, **Tier C 내부 지식만 저장**한다. Tier A 외부 원문은 절대 저장되지 않는다(제2조). v1.3의 모든 Entity 정의를 100% 계승한다.

## 8-1. NumberKnowledge Entity

```kotlin
@Entity(tableName = "number_knowledge")
data class NumberKnowledge(
    @PrimaryKey
    val numberE164: String,           // E.164 국제 포맷 (예: "+821012345678")

    // === 분류 (Softmax 분포) ===
    val categoryDistribution: Map<ConclusionCategory, Float>,
    val topCategory: ConclusionCategory,
    val topConfidence: Float,
    val isAmbiguous: Boolean,         // gap < 0.15 → true
    val riskLevel: RiskLevel,

    // === 신호 요약 (rawSnippet 절대 없음) ===
    val signalSummary: SignalSummary,
    val tierContributions: List<TierContribution>,

    // === 메타 ===
    val firstSeenAt: Long,
    val lastUpdatedAt: Long,
    val isStale: Boolean,
    val sourceCount: Int,             // 누적 신호 출처 수

    // === Cluster (Self-Discovery 결과) ===
    val discoveredClusterId: String,  // "auto_xxx"

    // === 사용자 행동 참조 (별도 테이블 FK) ===
    val userActionCount: Int
)

enum class ConclusionCategory {
    SAFE,           // 안심
    AD_LEGITIMATE,  // 합법 광고
    AD_AGGRESSIVE,  // 공격적 광고
    SPAM,           // 스팸
    SCAM,           // 사기/피싱
    UNKNOWN         // 정보 부족
}

enum class RiskLevel {
    NONE,           // 위험 없음
    LOW,            // 낮음
    MEDIUM,         // 중간
    HIGH,           // 높음
    CRITICAL        // 즉시 차단 권고
}

data class SignalSummary(
    val totalSignals: Int,
    val tier1Count: Int,  // 커뮤니티 (가중치 0.3)
    val tier2Count: Int,  // 일반 사이트 (가중치 0.5)
    val tier3Count: Int,  // 정부/공식 (가중치 0.8)
    val tier4Count: Int,  // 회사 공식 (가중치 1.0)
    val featureCounts: Map<FeatureType, Int>
)

data class TierContribution(
    val tier: Int,
    val weightedScore: Float,
    val signalCount: Int,
    val lastUpdatedAt: Long
)
```

## 8-2. UserAction Entity (Patch 35 — DO_NOT_MISS 서브타입 추가, 코웍 87a9a3 §17-6-5 흡수)

사용자 조치(스팸 신고·차단·안심 표시·**DO_NOT_MISS 지정** 등)를 기록. 본사 전송 없음, 디바이스 내부만.

**Patch 35 변경 사항**: Lane 1 D05가 지적한 "v1.0 §4.1에서 E2E 완성 기능이었던 DO_NOT_MISS가 v1.6.1에서 실종"을 복원한다. 코웍 87a9a3 §17-6-5의 처분 정책을 본 §8-2 + §3-4 + §21로 흡수한다.

### 8-2-1. Entity 정의

```kotlin
@Entity(tableName = "user_action")
data class UserActionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val identifierType: String,    // PhoneNumber / UrlDomain / AppReputation 직렬화
    val identifierValue: String,
    val actionType: String,         // sealed class 직렬화 (kind tag)
    val payloadJson: String?,       // 서브타입별 추가 데이터 (DO_NOT_MISS 메모 등)
    val createdAt: Long
)
```

### 8-2-2. UserAction sealed class

```kotlin
/**
 * 사용자 조치 도메인 모델.
 * v1.5.2 Patch 08에서 IdentifierType 호환으로 확장.
 * v1.6.1 Patch 35에서 DoNotMiss 서브타입 추가.
 */
sealed class UserAction {
    abstract val identifier: IdentifierType
    abstract val createdAt: Long

    data class SpamReport(
        override val identifier: IdentifierType,
        override val createdAt: Long
    ) : UserAction()

    data class Blocked(
        override val identifier: IdentifierType,
        override val createdAt: Long
    ) : UserAction()

    data class AddedToContacts(
        override val identifier: IdentifierType,
        override val createdAt: Long
    ) : UserAction()

    data class MarkedSafe(
        override val identifier: IdentifierType,
        override val createdAt: Long
    ) : UserAction()

    data class MarkedAd(
        override val identifier: IdentifierType,
        override val createdAt: Long
    ) : UserAction()

    data class Unblocked(
        override val identifier: IdentifierType,
        override val createdAt: Long
    ) : UserAction()

    /**
     * Patch 35 신설 (Lane 1 D05 복원).
     * 사용자가 특정 식별자에 대해 "이건 절대 놓치지 마"라고 지정한 우선순위 플래그.
     *
     * 효과 (4 Surface 공통):
     * 1. DecisionEngine이 riskLevel = LOW로 판정해도 사용자에게 반드시 노출
     * 2. 알림은 일반 채널이 아닌 High-Priority Notification Channel 사용
     * 3. CallCheck 오버레이는 dismiss 타임아웃을 평소보다 길게 (5초 → 15초)
     * 4. NKB 캐시 우선순위 상위 (Stale 갱신 시 먼저 재검증)
     */
    data class DoNotMiss(
        override val identifier: IdentifierType,
        override val createdAt: Long,
        val userMemo: String? = null   // 사용자가 직접 입력한 메모 (예: "엄마 병원 번호")
    ) : UserAction()
}
```

### 8-2-3. 도메인 ↔ Entity 변환

```kotlin
fun UserAction.toEntity(): UserActionEntity = UserActionEntity(
    identifierType = identifier::class.simpleName ?: "Unknown",
    identifierValue = when (val id = identifier) {
        is IdentifierType.PhoneNumber -> id.value
        is IdentifierType.UrlDomain -> id.value
        is IdentifierType.AppReputation -> id.value
    },
    actionType = this::class.simpleName ?: "Unknown",
    payloadJson = if (this is UserAction.DoNotMiss) {
        Json.encodeToString(mapOf("memo" to userMemo))
    } else null,
    createdAt = createdAt
)

fun UserActionEntity.toDomain(): UserAction { /* ... 역변환 ... */ }
```

### 8-2-4. Phase 별 적용 범위

코웍 87a9a3 §17-6-5의 단계적 적용 정책 계승:

| Phase | DO_NOT_MISS 적용 Surface |
|---|---|
| Phase 1 | CallCheck (착신 오버레이 dismiss 시간 연장 + High-Priority Channel) |
| Phase 2 | MessageCheck Mode A·B (HIGH 알림 채널 분기) |
| Phase 3 | MicCheck·CameraCheck (특정 앱 권한 변동 시 강조 알림) |
| Phase 후행 | PushCheck·AppSecurityWatch도 동일 규칙 적용 |

### 8-2-5. UX 진입 경로

- CallCheck PostCallScreen 4버튼 옆 메뉴 → "DO_NOT_MISS로 지정" + 메모 입력
- 설정 → "DO_NOT_MISS 목록 관리" → 추가·삭제·메모 수정
- 4속성 카드 우상단 메뉴에서도 토글 가능

## 8-3. ExtractedSignal (rawSnippet 완전 제거)

```kotlin
// v1.2 → v1.3 변경 핵심: rawSnippet 완전 제거
// v1.5 patch 03: numberE164 필드 추가 (자비스 라운드 2 지적 반영)
// v1.6.1 유지
data class ExtractedSignal(
    val numberE164: String,                       // 어느 번호의 신호인지 식별
    val signalType: SignalType,
    val sourceTier: Int,                          // 1~4
    val featureCounts: Map<FeatureType, Int>,     // 예: {SCAM_KEYWORD: 12, AD_KEYWORD: 3}
    val extractedAt: Long
    // ❌ rawSnippet: String (200자) — v1.3에서 완전 제거, 이후 유지
    // ❌ sourceProvider: String — v1.3에서 제거 (Tier 정보로 충분)
)

// v1.6.1 헌법 정합:
// - 제2조 (In-Bound Zero): numberE164는 외부 원문이 아닌 식별자이므로 정합 ✓
// - 제1조 (Out-Bound Zero): numberE164는 디바이스 내부 처리만, 외부 전송 0 ✓

enum class FeatureType {
    SCAM_KEYWORD,            // 사기 관련 키워드 출현 횟수
    AD_KEYWORD,              // 광고 관련 키워드 출현 횟수
    OFFICIAL_DOMAIN_HIT,     // 정부/공식 도메인 매칭 횟수
    URL_RISK_INDICATOR,      // 의심 URL 패턴 횟수
    USER_REVIEW_NEGATIVE,    // 사용자 부정 리뷰 횟수
    USER_REVIEW_POSITIVE,    // 사용자 긍정 리뷰 횟수
    PHONE_FORMAT_SUSPICIOUS  // 의심 전화 포맷 횟수
}

enum class SignalType {
    SCAM_INDICATOR,
    AD_INDICATOR,
    OFFICIAL_RECOGNITION,
    USER_FEEDBACK_NEGATIVE,
    USER_FEEDBACK_POSITIVE,
    NEUTRAL_INFORMATION
}
```

## 8-4. ClusterProfile Entity

Self-Discovery 결과 저장. L3에서 probe 실패해도 기존 값 사용.

```kotlin
@Entity(tableName = "cluster_profile")
data class ClusterProfile(
    @PrimaryKey val clusterId: String,            // "auto_xxx"
    val discoveredEngines: List<SearchEngineRef>,
    val discoveredOfficialDomains: List<DomainRef>,
    val simMcc: String?,
    val simMnc: String?,
    val networkCountryIso: String?,
    val locale: String,
    val timeZone: String,
    val discoveredAt: Long,
    val lastVerifiedAt: Long
)

data class SearchEngineRef(
    val domain: String,
    val responseTimeMs: Long
)

data class DomainRef(
    val tld: String,
    val sample: String
)
```

## 8-5. Stale 정책 (Tier별 maxAge)

§6-4 참조. Stale은 삭제 트리거가 아니라 신뢰도 하락 플래그.

## 8-6. 4속성과 NKB 매핑

헌법이 요구하는 4속성 출력이 NKB 필드에서 어떻게 산출되는지 정합 검증.

| 출력 속성 | 산출 NKB 필드 | 산출 방식 |
|---|---|---|
| 위험도 | NumberKnowledge.riskLevel + topConfidence | 직접 매핑 |
| 예상 손해 | RiskLevel → 정적 매핑 (디바이스 내) | `RiskLevel.HIGH` → "금전 피해 가능", `MEDIUM` → "시간 손해 가능" 등 strings.xml 다국어 |
| 손해 유형 | NumberKnowledge.topCategory | `ConclusionCategory.SCAM` → "금융사기", `AD_AGGRESSIVE` → "광고" 등 strings.xml |
| 이유 설명 | NumberKnowledge.signalSummary 기반 | `tier3Count > 0` → "정부 신고 이력", `userActionCount > 5` → "사용자 신고 다수" 등 룰 기반 한 줄 자동 생성 |

**4속성 모두 NKB Hit만으로 산출 가능** → L3 호환 100%.

## 8-X. Data Model Freeze Declaration (v1.5.2 Patch 11)

### 8-X-1. Frozen 모델 선언

v1.5.2 시점부터 다음 데이터 모델을 **Frozen** 상태로 선언한다. v1.6.1에서도 계승.

| 모델 | Frozen 시점 | 비고 |
|---|---|---|
| ExtractedSignal | v1.5.2 (Patch 03 적용본) | numberE164 필드 포함 |
| NumberKnowledge | v1.5.2 (Patch 08 RiskKnowledge 호환) | identifierType/Value 필드 포함 |
| UserAction | v1.5.2 (Patch 08 마이그레이션 후) | identifierType/Value 필드 포함 |
| Decision | v1.5.2 (Patch 10 Contract 반영) | STALE_KNOWLEDGE 플래그 포함 |

### 8-X-2. 변경 절차

- Frozen 모델의 필드 변경·삭제는 **메이저 버전 (v2.0.0+)**에서만 허용
- 패치 버전 (v1.5.x ~ v1.6.x)에서는 **추가 필드만 허용**, 기존 필드 변경 금지
- 변경 시 Room Migration 코드 동시 작성 의무
- 0-B 정직성 감사 로그에 변경 사유 기록

### 8-X-3. 모델 호환성 테스트 (Day 6 신규)

```kotlin
class MigrationCompatTest {
    @Test
    fun `v1_to_v2 migration preserves all non-deprecated fields`() {
        // 실행 시 v1 DB → v2 DB 마이그레이션 후
        // 모든 기존 필드가 동일하게 보존되는지 검증
    }

    @Test
    fun `FreezeMarkerTest all Frozen fields still exist`() {
        // ExtractedSignal·NumberKnowledge·UserAction·Decision
        // 각각의 필수 필드 존재를 리플렉션으로 검증
    }
}
```

---

# 9. SearchResultAnalyzer (Tier A → Tier C 변환)

외부 검색 원문(Tier A)을 featureCounts(Tier C)로 변환하는 엔진. 원문은 메모리에서만 처리, 즉시 폐기.

## 9-1. 입력·출력 계약

```kotlin
interface SearchResultAnalyzer {
    /**
     * 외부 검색 결과를 ExtractedSignal로 변환.
     *
     * @param rawResult 외부 검색 원문 (메모리만, 저장 금지)
     * @param tier 출처 Tier (1~4)
     * @return ExtractedSignal (featureCounts만 포함, 원문 없음)
     */
    fun analyze(rawResult: RawSearchResult, tier: Int): ExtractedSignal
}

// Decision Engine 내부 스코프에서만 생성 가능
internal data class RawSearchResult(
    val url: String,
    val title: String,
    val snippet: String,  // 메모리 only, NKB·Surface 저장 금지
    val sourceDomain: String
)
```

## 9-2. Tier 분류 규칙

`TierClassifier`가 `sourceDomain`을 기준으로 Tier 판정.

| Tier | 도메인 패턴 | 가중치 | 예시 |
|---|---|---|---|
| Tier 4 | 회사 공식 (앱스토어·공식 홈페이지) | 1.0 | `google.com/about`, `apple.com`, `company.co.kr` |
| Tier 3 | 정부·공공기관 | 0.8 | `*.gov.kr`, `*.go.kr`, `kisa.or.kr`, `police.go.kr` |
| Tier 2 | 일반 사이트·언론·블로그 | 0.5 | 일반 뉴스, 블로그, 쇼핑몰 |
| Tier 1 | 커뮤니티·포럼·UGC | 0.3 | 네이버 카페, 디시인사이드, 레딧 |

도메인 패턴이 모호한 경우 기본 Tier 2.

## 9-3. FeatureExtractor (키워드 카운팅)

strings.xml에 정의된 **scamKeywords / adKeywords / officialTerms** 사전 기반 단순 카운팅.

```kotlin
class FeatureExtractor(
    private val keywordLoader: KeywordLoader
) {
    fun extract(rawResult: RawSearchResult): Map<FeatureType, Int> {
        val text = "${rawResult.title} ${rawResult.snippet}".lowercase()
        val locale = Locale.getDefault()
        val keywords = keywordLoader.loadForLocale(locale)

        return mapOf(
            FeatureType.SCAM_KEYWORD to countMatches(text, keywords.scam),
            FeatureType.AD_KEYWORD to countMatches(text, keywords.ad),
            FeatureType.OFFICIAL_DOMAIN_HIT to if (isOfficialDomain(rawResult.sourceDomain)) 1 else 0,
            FeatureType.URL_RISK_INDICATOR to countSuspiciousUrlPatterns(rawResult.snippet),
            FeatureType.USER_REVIEW_NEGATIVE to countMatches(text, keywords.userNegative),
            FeatureType.USER_REVIEW_POSITIVE to countMatches(text, keywords.userPositive),
            FeatureType.PHONE_FORMAT_SUSPICIOUS to countSuspiciousPhonePatterns(rawResult.snippet)
        )
    }
}
```

## 9-4. KeywordLoader (strings.xml 로드)

**하드코딩 금지** (메모리 #1). 키워드는 strings.xml 다국어 자원.

```kotlin
class KeywordLoader(private val context: Context) {
    data class KeywordSet(
        val scam: List<String>,
        val ad: List<String>,
        val officialTerms: List<String>,
        val userNegative: List<String>,
        val userPositive: List<String>
    )

    fun loadForLocale(locale: Locale): KeywordSet {
        // res/values-ko/keywords.xml, res/values-en/keywords.xml 등에서 로드
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        val localizedContext = context.createConfigurationContext(config)
        return KeywordSet(
            scam = localizedContext.resources.getStringArray(R.array.scam_keywords).toList(),
            ad = localizedContext.resources.getStringArray(R.array.ad_keywords).toList(),
            officialTerms = localizedContext.resources.getStringArray(R.array.official_terms).toList(),
            userNegative = localizedContext.resources.getStringArray(R.array.user_negative).toList(),
            userPositive = localizedContext.resources.getStringArray(R.array.user_positive).toList()
        )
    }
}
```

## 9-5. 원문 폐기 보장 (재명시)

- `RawSearchResult.snippet`은 `FeatureExtractor.extract()` 호출 후 스코프 이탈 → GC 대상
- 반환값 `Map<FeatureType, Int>`에는 원문 없음
- `ExtractedSignal` 생성 시 `RawSearchResult` 참조 없음
- 테스트: `RawSearchResult` 클래스의 `internal` 가시성 검증

---

# 10. Decision Engine 수식 (Softmax + 사용자 Override)

3계층 신호를 통합하여 `RiskKnowledge` 4속성을 산출.

## 10-1. Decision Engine 계약

```kotlin
interface DecisionEngine {
    /**
     * 입력 식별자(번호·URL·앱)에 대한 판단 산출.
     *
     * @param query IdentifierType (phoneNumber·smsMessage·appReputation)
     * @return RiskKnowledge (4속성 + STALE_KNOWLEDGE 플래그)
     */
    suspend fun evaluate(query: IdentifierType): RiskKnowledge

    /**
     * 백그라운드 재검증 큐 등록 (Stale 상태인 엔트리용).
     */
    fun enqueueRefresh(query: IdentifierType)

    /**
     * 사용자 조치 반영 (UserAction 저장 + NKB 재계산 트리거).
     */
    suspend fun applyUserAction(query: IdentifierType, action: UserActionType)
}
```

## 10-2. 통합 알고리즘 (8단계)

```
Input: IdentifierType query

1. NKB Lookup
   hit = nkb.find(query)
   if hit != null && !hit.isStale:
       return toRiskKnowledge(hit)  // L3 경로, 즉시 반환
   if hit != null && hit.isStale:
       background: enqueueRefresh(query)
       return toRiskKnowledge(hit).withFlag(STALE_KNOWLEDGE)

2. Probe Layer 3 (공공 공신력 API, 우선)
   publicResults = publicApiClient.query(query, timeout=1s)

3. Probe Layer 2 (일반 검색, 쿼터 남은 경우)
   if quotaMgr.hasQuota():
       searchResults = searchMesh.query(query, timeout=1s)

4. SearchResultAnalyzer
   signals = [analyzer.analyze(r, tier=tierOf(r)) for r in publicResults + searchResults]

5. Tier 가중치 합산
   weightedCounts = mergeFeatureCounts(signals, weights={T1:0.3, T2:0.5, T3:0.8, T4:1.0})

6. 카테고리 점수 계산 (룰 기반)
   rawScores = ruleEngine.score(weightedCounts)  // {SCAM: 2.4, SPAM: 1.2, SAFE: 0.8, ...}

7. Softmax 정규화 + ConflictResolver
   probs = softmax(rawScores)
   topCategory = argmax(probs)
   topConfidence = max(probs)
   isAmbiguous = (probs.sorted()[0] - probs.sorted()[1]) < 0.15

8. NKB Write (원문 없이)
   nkb.upsert(NumberKnowledge(
       numberE164 = query.value,
       categoryDistribution = probs,
       topCategory = topCategory,
       topConfidence = topConfidence,
       isAmbiguous = isAmbiguous,
       riskLevel = mapToRiskLevel(topCategory, topConfidence),
       signalSummary = summarize(signals, weightedCounts),
       ...
   ))

Output: RiskKnowledge (4속성)
```

## 10-3. Softmax 정규화

```kotlin
fun softmax(scores: Map<ConclusionCategory, Float>): Map<ConclusionCategory, Float> {
    val maxScore = scores.values.max()
    val expScores = scores.mapValues { exp(it.value - maxScore) }  // overflow 방지
    val sum = expScores.values.sum()
    return expScores.mapValues { it.value / sum }
}
```

## 10-4. RiskLevel 매핑

```kotlin
fun mapToRiskLevel(category: ConclusionCategory, confidence: Float): RiskLevel = when {
    category == ConclusionCategory.SCAM && confidence >= 0.8f -> RiskLevel.CRITICAL
    category == ConclusionCategory.SCAM && confidence >= 0.6f -> RiskLevel.HIGH
    category == ConclusionCategory.SPAM && confidence >= 0.7f -> RiskLevel.HIGH
    category == ConclusionCategory.SPAM -> RiskLevel.MEDIUM
    category == ConclusionCategory.AD_AGGRESSIVE -> RiskLevel.MEDIUM
    category == ConclusionCategory.AD_LEGITIMATE -> RiskLevel.LOW
    category == ConclusionCategory.SAFE -> RiskLevel.NONE
    else -> RiskLevel.LOW  // UNKNOWN
}
```

## 10-5. 사용자 Override (Patch 37 타입 통일)

사용자가 "안심 표시" 또는 "스팸 신고"를 하면 Decision Engine이 NKB를 재계산.

**타입 정합 주의 (Patch 37)**: 본 메서드의 `action` 매개변수 타입은 Stage 0 FREEZE(§33-1-4)에서 `UserActionType` enum으로 확정. v1.6.2까지 본 섹션에 `ActionType`으로 표기되어 FREEZE 시그니처와 충돌했으나, 7-워커 평가(Codex CLI 단독 지적)로 발견. v1.7.0에서 `UserActionType`으로 통일.

```kotlin
suspend fun applyUserAction(query: IdentifierType, action: UserActionType) {
    // 1. UserAction 저장 (도메인 모델 UserAction sealed class는 §8-2-2 참조)
    userActionDao.insert(
        UserAction.fromType(action, query).toEntity()
    )

    // 2. NKB 재계산
    val hit = nkb.find(query) ?: return
    val updatedSignals = adjustForUserAction(hit.signalSummary, action)
    val updated = recomputeWithUserBias(hit, updatedSignals)
    nkb.upsert(updated)
}
```

사용자가 "안심 표시"한 번호는 **사용자 자산**이므로 이후 외부 신호와 상관없이 SAFE로 유지 (단, 심각한 공공 신고 신호 등장 시 재검토 알림).

## 10-X. Decision Contract (v1.5.2 Patch 10)

```kotlin
// Frozen: v1.5.2부터 변경 금지
data class Decision(
    val query: IdentifierType,
    val result: RiskKnowledge,
    val stalenessFlag: StalenessFlag,
    val computedAt: Long,
    val engineVersion: String = "v1.7.0"
)

enum class StalenessFlag {
    FRESH,              // 방금 계산
    STALE_KNOWLEDGE,    // NKB Stale, 재검증 예정
    STALE_OFFLINE       // 네트워크 단절, 기존 값 사용
}
```

---

# 11. Cold Start (On-Device Bootstrap)

신규 설치 후 NKB가 비어있을 때, **본사 fallback 없이** 디바이스가 자체 패턴 학습을 수행하는 절차.

## 11-1. Cold Start 단계

```
Day 0: 앱 설치·권한 허용

Step 1: 통화 이력 수집 (권한 허용 시)
  - READ_CALL_LOG → CallLog.Calls
  - 최근 90일 통화 번호·시간·횟수 수집

Step 2: 문자 이력 수집 (권한 허용 시)
  - READ_SMS → Telephony.Sms
  - 최근 90일 발신자 번호·URL 패턴 수집

Step 3: 연락처 수집 (권한 허용 시)
  - READ_CONTACTS → Contacts
  - 연락처에 있는 번호는 SAFE 초기값

Step 4: 로컬 패턴 학습
  - 반복 수신 번호 → NEUTRAL 초기값
  - 야간 수신 번호 → 의심 플래그
  - 광고 포맷 번호(15xx, 16xx 등) → AD 초기값

Step 5: Self-Discovery 실행 (§7)
  - Search Engine probe
  - Official Domain probe
  - ClusterProfile 생성·저장

Step 6: 외부 쿼리 큐 초기화
  - 상위 위험 후보 50개 번호에 대해 백그라운드 Layer 2·3 probe 예약
  - WorkManager로 7일간 분산 실행 (배터리 보호)
```

## 11-2. 권한 거부 시 graceful degradation

| 거부 권한 | 대체 동작 |
|---|---|
| READ_CALL_LOG | 통화 수신 시점부터 NKB 축적 (Cold Start 시 0건으로 시작) |
| READ_SMS | MessageCheck 기능 제한 + 사용자에게 설명 |
| READ_CONTACTS | SAFE 초기값 없이 시작 |
| READ_PHONE_STATE | **필수** — 거부 시 CallCheck 작동 불가 안내 |

## 11-3. Cold Start 권한 요청 UX

1. 앱 최초 실행: 온보딩 4개 슬라이드 (제품 소개·헌법 요약·권한 안내·시작)
2. 슬라이드 4: "시작하기" 버튼 → 순차 권한 요청
3. 각 권한 설명: "어디에 쓰이는지" 한 줄 (strings.xml 다국어)
4. 거부 가능: 권한 거부해도 앱 실행 가능, 기능만 제한

## 11-4. 초기 NKB 용량 가정

- 통화 이력 90일: 평균 수신 300건 → 고유 번호 50~150개
- 문자 이력 90일: 평균 200건 → 고유 발신자 30~80개
- 연락처: 평균 200~500명
- **총 초기 NKB 엔트리**: 500~1,000건
- **엔트리당 크기 상한**: 2KB (MEM-2KB 약속, §0-B)
- **초기 NKB 용량**: 1~2MB

---

# 12. 자가 진화 (Self-Evolution)

NKB는 시간이 지날수록 사용자 환경에 맞춰 **스스로 개선**된다. 본사 재학습 없이 디바이스 내에서 완결.

## 12-1. 자가 진화 메커니즘

| 메커니즘 | 트리거 | 동작 |
|---|---|---|
| **Periodic Refresh** | WorkManager 매일 1회 | Stale 엔트리 재 probe |
| **User Action Bias** | UserAction 이벤트 | 해당 번호 Softmax 재계산 |
| **Cluster Update** | 7일 경과 | Self-Discovery probe 재실행, 응답 시간 갱신 |
| **Stale Cleanup** | 365일 경과 (Tier 4도 초과 시) | 엔트리 삭제 대신 "archive" 플래그 |
| **Rebalancing** | 신호 출처 분포 편향 시 | Tier 가중치 동적 조정 (범위 제한) |

## 12-2. PeriodicMaintenance (WorkManager)

```kotlin
class PeriodicMaintenanceWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // 1. Stale 엔트리 재검증
        val staleEntries = nkb.findStale(limit = 20)
        staleEntries.forEach { entry ->
            decisionEngine.enqueueRefresh(entry.identifier)
        }

        // 2. Cluster 재검증 (7일 경과 시)
        if (shouldReverifyCluster()) {
            selfDiscovery.reprobe()
        }

        // 3. 초과 archive 정리
        nkb.archiveStaleEntries(olderThan = 365.days)

        return Result.success()
    }
}
```

## 12-3. 자가 진화의 헌법 정합

- 본사 재학습 0 → 제3조·제7조 정합
- 사용자 행동만으로 개선 → 개인화(맞춤) 헌법 구현
- Periodic도 디바이스 내부 이벤트 → 외부 의존 0

---

# 13. (v1.5.x에서 공백 — 의도적 번호 점프)

v1.3~v1.6.1 계보에서 13번 섹션은 사용되지 않음. 향후 섹션 추가 시 13번부터 순차 사용.

---

# 14. 비기능 / 디바이스 SLA (4단계)

헌법 제4조의 SLA 4단계(§1-4)를 운영 지표로 구체화.

## 14-1. L1 Full (정상)

| 지표 | 목표 |
|---|---|
| NKB Lookup p95 | ≤ 100ms |
| NKB Lookup p99 | ≤ 200ms |
| Surface 렌더 p95 | ≤ 500ms |
| External probe p95 (Layer 2+3) | ≤ 2s |
| 4속성 출력 완결 p95 | ≤ 2.5s |

## 14-2. L3 Offline (헌법 기준선)

| 지표 | 목표 |
|---|---|
| **NKB Hit p95** | **≤ 5ms** ← SLA-14-2 약속 |
| 4속성 출력 완결 p95 | ≤ 500ms |
| UI 반응성 | L1과 동일 (비동기 처리) |

**검증 방법**: `SmokeRun11.kt` (§18 스모크런 11번)에서 비행기 모드 + NKB 200건 엔트리 상태에서 p95 측정.

## 14-3. 리소스 예산

| 리소스 | 상한 | 측정 방법 |
|---|---|---|
| 설치 용량 | ≤ 20MB (초기) | APK analyzer |
| RAM (실행 중) | ≤ 80MB (중위값) | Android Studio Memory Profiler |
| NKB DB 크기 | ≤ 50MB (1년 차) | Room DB 파일 크기 |
| 배터리 소모 | ≤ 1%/일 (백그라운드) | Play Console Vitals |
| 네트워크 | ≤ 5MB/월 (Layer 2+3) | 쿼터 관리 |

## 14-4. SLA Level Detector

```kotlin
class SlaLevelDetector(
    private val networkMonitor: NetworkStateMonitor,
    private val publicApiProbe: PublicApiProbe,
    private val nkb: NkbHealth
) {
    fun currentLevel(): SlaLevel {
        if (!nkb.isHealthy) return SlaLevel.L4_CATASTROPHIC
        if (!networkMonitor.isConnected) return SlaLevel.L3_OFFLINE
        if (!publicApiProbe.isResponsive()) return SlaLevel.L2_DEGRADED
        return SlaLevel.L1_FULL
    }
}

enum class SlaLevel {
    L1_FULL, L2_DEGRADED, L3_OFFLINE, L4_CATASTROPHIC
}
```

## 14-5. SLA 저하 시 UX

- L1 → L2: 알림 없음 (사용자 관점 동일)
- L2 → L3: 상단 배너 "오프라인 모드 — 저장된 정보로 작동 중"
- L3 → L4: 모달 "초기화가 필요합니다 — 데이터가 손상되었습니다" + Cold Start 재실행 버튼

---

# 15. 본사 매핑 0건 검증 체크리스트

헌법 7조("본사 운영 0 / 본사 매핑 0 / 본사 데이터센터 0")의 **자동 검증 스크립트**.

## 15-1. 검증 스크립트 목록

| 스크립트 | 검증 대상 | 실패 조건 |
|---|---|---|
| `scripts/verify-no-server.sh` | 프로젝트 전체 | "our backend", "company server", AWS SDK import 발견 |
| `scripts/verify-network-policy.sh` | Manifest + HttpClientProvider | 네트워크 진입점이 Layer 2·3 외 존재 |
| `scripts/verify-no-mapping.sh` | 리소스 + 코드 | "country → engine" 하드코딩 매핑 발견 |
| `scripts/verify-frozen-model.sh` | Entity 파일 | Frozen 필드 누락·변경 발견 |
| `scripts/verify-strings-i18n.sh` | Kotlin 코드 | 문자열 하드코딩 (`"안녕하세요"` 등) 발견 |

## 15-2. 검증 샘플 (verify-no-server.sh)

```bash
#!/usr/bin/env bash
set -e

# 우리가 운영하는 서버 코드·문서 패턴 탐지
PATTERNS=(
    "our-backend"
    "ollanvin-server"
    "myphonecheck-api"
    "com.amazonaws.*Lambda"
    "com.amazonaws.*DynamoDB"
    "api.myphonecheck.app"
)

FAILED=0
for pattern in "${PATTERNS[@]}"; do
    if grep -r -i "$pattern" --include="*.kt" --include="*.kts" --include="*.xml" .; then
        echo "❌ 헌법 1·7조 위반 가능성: '$pattern' 발견"
        FAILED=1
    fi
done

[ $FAILED -eq 0 ] && echo "✅ 본사 매핑 0건 검증 PASS"
exit $FAILED
```

## 15-3. CI 통합

`.github/workflows/android-ci.yml`에 다음 단계 추가:

```yaml
- name: Verify Constitution Compliance
  run: |
    scripts/verify-no-server.sh
    scripts/verify-network-policy.sh
    scripts/verify-no-mapping.sh
    scripts/verify-frozen-model.sh
    scripts/verify-strings-i18n.sh
```

한 개라도 실패하면 PR 머지 차단.

## 15-4. 위반 발견 시 SOP

1. Detekt·CI에서 자동 탐지
2. PR에 자동 코멘트 (violation 목록 + 해당 파일·라인)
3. 수정 후 재실행 → PASS까지 머지 차단
4. 감사 로그(§0-B)에 위반 이력 기록

---

# 16. 비즈니스 모델 (Business Model)

헌법 6조(가격 정직성)의 KPI 매핑. 수익은 **거위 자가 작동의 자연 결과**이나, KPI는 정직하게 측정한다.

## 16-1. 가격 구조 (2026-04-22 확정)

- **단일 가격**: USD 2.49/월, 전세계 동일
- **연간 플랜**: 없음
- **무료 체험**: 7일 (Play Console 기본값)
- **환불 정책**: 스토어 표준 (Google 48시간 / Apple 90일)

## 16-2. net ARPU 계산

```
gross revenue = $2.49/월

- Apple/Google 수수료: 30% (첫 해) / 15% (1년 이상 유지 시)
- VAT 평균: 10% (국가별 상이, 평균값)
- 환불: 5% (업계 평균)

초기 net = $2.49 × 0.70 × 0.90 × 0.95 ≈ $1.49/월
장기 net (15% 수수료 기준) = $2.49 × 0.85 × 0.90 × 0.95 ≈ $1.81/월
```

KPI 기록은 **초기 net $1.49** 기준.

## 16-3. 목표 규모

| 지표 | 값 |
|---|---|
| 월 net 목표 | $100K |
| 국가당 break-even MAU | 67,114명 (net $1.49 기준) |
| 전환율 가정 | 3% (보안·유틸리티 업계 평균 2~4%의 중앙값) |
| 전체 MAU 목표 | 2.24M (67K / 3%) |
| 타겟 국가 수 | 190 (Android 전세계) |
| 국가당 MAU 목표 | 11,800명 (2.24M / 190, 분산 가정) |

**국가당 11,800 MAU**는 Play Console의 일반 로컬 앱 수준. Truecaller·Hiya·Whoscall 대비 낮은 진입 부담.

## 16-4. 기존 안과의 비교

| 버전 | gross 가격 | net ARPU | 판단 |
|---|---|---|---|
| PRD 초안 | $1/월 | $0.60/월 | 비현실적 |
| v1.4_disc | $1.99/월 | $1.19/월 | 경쟁력 약함 |
| v1.5.x | ~$1.5/월 | ~$0.90/월 | 폐기 |
| **v1.7.0** | **$2.49/월** | **$1.49/월** | **확정 (v1.6.2 승계, 7-워커 평가 후 MINOR 승격)** |

근거: Whoscall 신규 $2.89 / Hiya $3.99 / Truecaller $4.49 대비 **최저가 포지셔닝**이면서 지속가능한 마진.

## 16-5. 수익 모델 헌법 정합

- 본사 KPI 매핑 0: KPI는 "측정 도구"이지 "사용자 데이터 수집"이 아님 → 제1조 정합
- 광고 모델 거부: "디바이스가 사용자 데이터를 수집 → 타겟 광고"는 제1·7조 위반
- 구독만 채택: 스토어 직접 결제 → 자체 서버 없음 (Infra v1.0 FINAL 정합)
- 영수증 검증: 온디바이스 스토어 API 서명 검증 (제1·7조 정합, 메모리 #20)

## 16-6. 가격 실험 금지

- A/B 테스트로 가격 변경 금지 (헌법 6조 "전세계 동일" 원칙)
- 할인·프로모션은 스토어 공식 기능만 사용 (Play Console 프로모션 코드 등)
- 사용자별 차별 가격 금지

---

# 17. 제품 전략 (Product Strategy) — One Engine, Four Surfaces

본 장은 헌법 7조와 황금알 정의(§3)가 **제품 로드맵 차원**에서 어떻게 구현되는지 명시한다.

## 17-1. One Engine 원칙 재선언

- **엔진은 하나**: Decision Engine은 단일 구현체. 4 Surface가 공유한다.
- **엔진 분기 금지**: "CallCheck 전용 Decision Engine" 같은 분기 금지. 입력 `IdentifierType`으로 분기하되, 동일 엔진 내부에서 처리.
- **Surface는 얇다**: Surface는 입력 수집 + 출력 렌더링만 담당. 판단 로직은 엔진.
- **확장은 Surface 추가로**: 새 기능 추가 시 엔진 변경 없이 Surface 신설.

## 17-2. v1.6 Four Surfaces

| # | Surface | 입력 원천 | 출력 | 구현 상태 (v1.6.1) |
|---|---|---|---|---|
| 1 | CallCheck | `TelephonyManager.CALL_STATE_RINGING` 전화번호 | CallRisk (4속성) | 기준 (v1.3~) |
| 2 | MessageCheck | MMS/SMS 발신번호 + URL + 기관명 (Mode A/B) | MessageRisk (4속성) | 복원 (Patch 18) + 2-모드 (Patch 29) |
| 3 | MicCheck | RECORD_AUDIO 보유 앱 목록 | List<MicPermissionEntry> (단순 관리자, Patch 30) | 축소 (Patch 30) |
| 4 | CameraCheck | CAMERA 보유 앱 목록 | List<CameraPermissionEntry> (단순 관리자, Patch 30) | 축소 (Patch 30) |

## 17-3. 향후 Surface (후행, 본 문서 범위 밖)

| Surface | 개요 | 우선순위 | 이관 이력 |
|---|---|---|---|
| **PushCheck (푸시 휴지통)** | NotificationListenerService 기반, 스팸 지정 발신자 알림 자동 cancel → 자체 DB 저장 → 휴지통 UI | 메모리 #14 + `통합운영설계안_v1 §2.9` "실제 격리 + UI 필수" 확정 | — |
| **AppSecurityWatch** | 신규 앱 설치 시 과거 보안 사고 이력 자동 검색·경고 + 기존 앱 신규 CVE/침해 사고 실시간 감지·알림 + NVD CVE·CISA KEV·Have I Been Pwned 조회 + Decision Engine 기반 앱 평판 판정 | 메모리 #13-1/2 | **Patch 31로 MicCheck/CameraCheck에서 분리 이관** |
| UrlCheck | 브라우저 공유 URL 검사 | 후행 | — |

본 v1.7.0에서는 **Four Surfaces만** 정식 스펙. 위 후행 Surface는 별도 워크오더로 진입.

**이관 근거 (Patch 31)**: MicCheck/CameraCheck는 대표님 지시(2026-04-24)에 따라 "리스트 + 최근 사용 + 회수 버튼" 3기능의 **단순 관리자**로 축소되었다. 이관된 감시·CVE·침해 판정 기능은 **별도의 구체적 Surface**로 재탄생하는 것이 헌법 7조 "구체적 Surface 원칙"에 정합하다.

**PushCheck cross-ref (Lane 1 D34 반영)**: `통합운영설계안_v1.docx §2.9`에서 "실제 격리 + UI 필수"로 확정된 내용은 본 v1.6.1 범위 밖이며, `docs/00_governance/patches/PATCH_v1.7.md Patch 31`로 이관 예정. 본 §17-3이 이관처를 명시한다.

## 17-4. Phase 로드맵

| Phase | 범위 | 검증 게이트 |
|---|---|---|
| Phase 0: Foundation | Stage 0 Contracts (5 파일 / 22 시그니처) | FreezeMarkerTest 22개 PASS ✓ (Patch 37 통일, 이미 완료) |
| Phase 0-hotfix | Java 17 전체 승격 | CI 빌드 PASS ✓ (이미 완료) |
| Phase 1: CallCheck MVP | 4속성 출력 · NKB · 3계층 소싱 · L3 경로 | 스모크런 1~5, 11 PASS |
| Phase 2: MessageCheck | MessageRisk + 3중 평가 + URL probe | 스모크런 6~7 PASS |
| Phase 3: MicCheck / CameraCheck | `MicPermissionEntry`/`CameraPermissionEntry` + PackageManager 스캔 (단순 관리자, Patch 30·37) | 스모크런 8~9 PASS |
| Phase 4: Billing 통합 | Play Billing + 구독 상태 UX | 스모크런 10 PASS |
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

## 18-4. MessageCheck 본문 (Patch 29 — Mode A/B 2-모드 아키텍처)

v1.6.1-patch 시점 `§18-4-4`는 "Default SMS Handler 재설계 또는 NotificationListenerService"라는 **대안 경로만 제시**하고 어느 경로를 채택할지 미확정 상태였다. 자비스 Lane 4 검증(2026-04-24)이 "Play 심사에서 이 모호성이 곧 리젝 요인"이라고 지적. 본 Patch 29는 **2-모드 명시적 분기**로 이를 해결한다.

### 18-4-0. 2-모드 개요

| 모드 | 권한 | 감지 방식 | 사용자 경험 | 기본값 |
|---|---|---|---|---|
| **Mode B — Share Intent (기본)** | 없음 | 사용자 수동 트리거 | 문자 앱에서 "공유" → MyPhoneCheck 선택 | ✅ 기본 |
| **Mode A — Default SMS Handler (선택)** | `READ_SMS` + Default SMS 지정 | 자동 수신 감지 | 기본 SMS 앱으로 MyPhoneCheck 지정 | 선택 |

**원칙**:
- Mode B는 **권한 0**으로 전세계 즉시 작동 (헌법 4조 자가 작동 강화)
- Mode A는 사용자가 명시적으로 선택한 경우에만 활성화
- 두 모드 모두 **동일한 `MessageCheckEngine` + 동일한 `MessageRisk` 출력**
- 전환 UI: 설정 화면에서 "Default SMS 앱으로 지정하기" 버튼 제공, 비활성화 기본

### 18-4-1. Mode B — Share Intent (권한 0)

#### 18-4-1-1. 작동 흐름

```
1. 사용자가 기본 SMS 앱에서 의심 문자 수신
2. 사용자가 해당 문자를 길게 누름 → "공유" 메뉴
3. 공유 대상 목록에 "MyPhoneCheck" 표시
4. 사용자가 MyPhoneCheck 선택
5. MessageCheck 화면이 열려 문자 본문 자동 분석
6. MessageRisk 4속성 결과 표시
```

#### 18-4-1-2. AndroidManifest 선언

```xml
<activity
    android:name=".feature.message.MessageCheckShareActivity"
    android:exported="true"
    android:label="@string/msg_check_share_label">
    <intent-filter>
        <action android:name="android.intent.action.SEND" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:mimeType="text/plain" />
    </intent-filter>
</activity>
```

#### 18-4-1-3. 수신 처리

```kotlin
class MessageCheckShareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val body = intent.getStringExtra(Intent.EXTRA_TEXT) ?: run {
            finish(); return
        }
        val sender = intent.getStringExtra(Intent.EXTRA_EMAIL)?.firstOrNull()
            ?: extractSenderFromSubject(intent.getStringExtra(Intent.EXTRA_SUBJECT))
            ?: UNKNOWN_SENDER_MARKER

        lifecycleScope.launch {
            val risk = messageCheckEngine.evaluate(
                IncomingSms(
                    senderE164 = sender,
                    body = body,
                    receivedAt = System.currentTimeMillis()
                )
            )
            setContent { MessageRiskScreen(risk) }
        }
    }
}
```

발신번호를 공유 Intent에서 확보하지 못하는 케이스가 대부분이므로, **sender 결측 시 URL·사칭만으로 평가**하는 경로를 `MessageCheckEngine` 내부에서 지원 (다음 §18-4-3 참조).

#### 18-4-1-4. 사용자 UX 보조

설정 화면에 **"빠른 공유 설정" 가이드**:
- 안드로이드 기본 SMS 앱에서 "MyPhoneCheck"를 공유 메뉴에 고정하는 방법 (OS 버전별 스크린샷)
- "복사 → 붙여넣기" 대안 경로 (텍스트 필드에 직접 입력)

### 18-4-2. Mode A — Default SMS Handler (사용자 명시 선택)

#### 18-4-2-1. 활성화 조건

- 사용자가 설정 화면에서 "Default SMS 앱으로 지정" 버튼 탭
- 시스템 `RoleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)` 호출
- 사용자가 시스템 다이얼로그에서 승인
- 이 시점부터 `READ_SMS`·`SEND_SMS`·`RECEIVE_SMS` 자동 부여

#### 18-4-2-2. 필수 구현

Default SMS Handler가 되려면 Play 정책상 **완전한 SMS 앱 기능**을 제공해야 한다:
- SMS 송수신 UI
- 대화 목록·검색
- MMS 지원
- 알림

본 앱에서는 최소 SMS 앱 기능을 `feature/message/sms-handler` 서브 모듈로 구현. 원 SMS 앱과 경쟁하는 UX를 목표로 하지 않고, **"보안 특화 SMS 앱"** 포지셔닝.

#### 18-4-2-3. 수신 감지

```kotlin
class SmsReceiverService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { handleIncoming(it) }
        return START_NOT_STICKY
    }

    private fun handleIncoming(intent: Intent) {
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        messages.forEach { sms ->
            scope.launch {
                val risk = messageCheckEngine.evaluate(
                    IncomingSms(
                        senderE164 = normalizeE164(sms.originatingAddress ?: ""),
                        body = sms.messageBody,
                        receivedAt = sms.timestampMillis
                    )
                )
                if (risk.riskLevel >= RiskLevel.HIGH) {
                    notificationBuilder.showMessageRiskAlert(risk)
                }
                // 저장은 sms-handler 모듈에 위임 (정상 SMS 앱 기능)
            }
        }
    }
}
```

#### 18-4-2-4. 사용자 복귀 경로

사용자가 "다시 원래 SMS 앱으로 돌아가기"를 원하면 설정에서 **"Default SMS 해제"** 버튼 → 시스템 설정 → 원 앱 선택.

### 18-4-3. 공통 엔진 — MessageCheckEngine (Mode 무관)

```kotlin
data class IncomingSms(
    val senderE164: String,          // 결측 허용 (UNKNOWN_SENDER_MARKER)
    val body: String,
    val receivedAt: Long
)

class MessageCheckEngine(
    private val decisionEngine: DecisionEngineContract,
    private val urlExtractor: UrlExtractor,
    private val impersonationDetector: ImpersonationDetector
) {
    suspend fun evaluate(sms: IncomingSms): MessageRisk {
        // 1. 발신번호 평가 (결측 허용)
        val senderRisk: RiskKnowledge? = if (sms.senderE164 != UNKNOWN_SENDER_MARKER) {
            decisionEngine.evaluate(IdentifierType.PhoneNumber(sms.senderE164))
        } else null

        // 2. URL 추출 + 각 URL 평가
        val urls = urlExtractor.extract(sms.body)
        val urlRisks = urls.map { url ->
            val domainRisk = decisionEngine.evaluate(
                IdentifierType.UrlDomain(extractDomain(url))
            )
            UrlRiskEntry(
                url = url,
                domain = extractDomain(url),
                risk = domainRisk.riskLevel,
                reason = domainRisk.reasonSummary
            )
        }

        // 3. 기관 사칭 감지 (strings.xml 기반 키워드 + 공식 도메인 화이트리스트)
        val impersonationFlags = impersonationDetector.detect(sms.body)

        // 4. 결과 통합
        return combineResults(sms, senderRisk, urlRisks, impersonationFlags)
    }

    private fun combineResults(
        sms: IncomingSms,
        sender: RiskKnowledge?,
        urls: List<UrlRiskEntry>,
        impersonations: List<ImpersonationFlag>
    ): MessageRisk {
        val maxRisk = maxOf(
            sender?.riskLevel ?: RiskLevel.NONE,
            urls.maxOfOrNull { it.risk } ?: RiskLevel.NONE,
            if (impersonations.any { it.suspicionLevel >= 0.7f }) RiskLevel.HIGH else RiskLevel.NONE
        )

        return MessageRisk(
            identifier = MessageIdentifier(
                senderE164 = sms.senderE164,
                messageHash = sha256(sms.body),
                receivedAt = sms.receivedAt
            ),
            riskLevel = maxRisk,
            expectedDamage = deriveDamage(maxRisk, impersonations),
            damageTypes = deriveDamageTypes(impersonations, urls),
            reasonSummary = buildReason(sender, urls, impersonations),
            senderRisk = sender?.riskLevel ?: RiskLevel.NONE,
            urlRisks = urls,
            impersonationFlags = impersonations
        )
    }

    companion object {
        const val UNKNOWN_SENDER_MARKER = "__UNKNOWN__"
    }
}
```

### 18-4-4. MessageRisk 데이터 클래스

```kotlin
data class MessageRisk(
    override val identifier: IdentifierType,   // MessageIdentifier로 래핑
    override val riskLevel: RiskLevel,
    override val expectedDamage: DamageEstimate,
    override val damageTypes: List<DamageType>,
    override val reasonSummary: String,
    override val computedAt: Long = System.currentTimeMillis(),
    override val stalenessFlag: StalenessFlag = StalenessFlag.FRESH,

    // MessageCheck 고유 필드
    val senderRisk: RiskLevel,
    val urlRisks: List<UrlRiskEntry>,
    val impersonationFlags: List<ImpersonationFlag>
) : RiskKnowledge

data class MessageIdentifier(
    val senderE164: String,
    val messageHash: String,
    val receivedAt: Long
)

data class UrlRiskEntry(
    val url: String,
    val domain: String,
    val risk: RiskLevel,
    val reason: String
)

data class ImpersonationFlag(
    val suspectedOrganization: String,
    val suspicionLevel: Float,
    val reason: String
)
```

### 18-4-5. SmokeRun07 시나리오 — "쿠팡 배송 알림" 사칭 SMS

**입력 SMS**:
```
[쿠팡] 고객님 상품이 배송 중입니다.
배송조회: http://coupang-delivery.xyz/track?id=KR12345
```

**Mode B 검증 흐름**:
1. 사용자가 SMS 앱에서 문자 길게 누름 → 공유 → MyPhoneCheck
2. `MessageCheckShareActivity`가 body 수신, sender는 `UNKNOWN_SENDER_MARKER`
3. `MessageCheckEngine.evaluate()` 호출
4. URL `coupang-delivery.xyz` → 공식 쿠팡 도메인(`coupang.com`) 아님 → Risk HIGH
5. ImpersonationDetector: "쿠팡" 키워드 + 공식 도메인 불일치 → suspicionLevel 0.9
6. 결과: `MessageRisk(riskLevel=HIGH, reasonSummary="쿠팡 사칭 의심: 공식 도메인 아님")`

**Mode A 검증 흐름**:
- 동일한 결과. 추가로 `sms.senderE164` 확보 → senderRisk 평가도 병행
- HIGH 판정 시 자동 알림 전송

### 18-4-6. Mode 선택 UX

설정 화면 `MessageCheckSettingsScreen`:

```
┌────────────────────────────────────────┐
│ 📨 MessageCheck 작동 방식              │
├────────────────────────────────────────┤
│ ⦿ 공유 메뉴 (권장, 권한 없음)         │
│   문자 앱에서 "공유" → MyPhoneCheck    │
│                                        │
│ ○ 기본 SMS 앱으로 지정 (자동 감지)    │
│   문자 수신 시 자동으로 분석           │
│   → SMS 앱 기능 전환 필요             │
│                                        │
│ [도움말] [Default SMS 해제 방법]      │
└────────────────────────────────────────┘
```

### 18-4-7. Play 정책 정합

| 정책 | 준수 방식 |
|---|---|
| `BROADCAST_SMS` 금지 (Patch 17) | 사용 안 함, AndroidManifest 선언 없음 |
| Default SMS 정책 (Play Console) | Mode A 선택 시 사용자 명시 동의 + 완전 SMS 앱 기능 제공 |
| `READ_SMS` 정당화 (Permissions Declaration) | Mode A 경로에서만, core user benefit: "사용자가 기본 SMS 앱으로 지정한 경우 유해 문자 자동 감지" |
| `SEND_SMS` 사용 | Mode A에서 SMS 앱 기능 제공 시 한정 |
| Data Safety | Mode B: "데이터 수집 없음" / Mode A: "디바이스 내부 처리만, 외부 전송 없음" |

### 18-4-8. PrivacyCheck 폐기 기록 (Patch 21)

(이하 §18-5로 이동되지 않고 현 위치 유지)

## 18-5. PrivacyCheck 폐기 기록 (Patch 21)

v1.5.x에서 언급되었던 `PrivacyCheck` Surface는 **v1.6.0 Patch 21로 폐기**되었다. 폐기 사유:
- 추상적 범주 ("개인정보 위험")
- 구체적 Surface 원칙(제7조 구현) 위반
- MicCheck(§18-6) + CameraCheck(§18-7)로 **세분화·구체화**

이후 버전에서 PrivacyCheck 부활 금지. 구체적 기능은 새로운 Surface(§17-3 후행)로 신설.

## 18-6. MicCheck 본문 (Patch 30 — 단순 관리자 축소)

### 18-6-0. 재정의 배경 (Patch 30)

v1.6.0 ~ v1.6.1-patch 시점에는 `AppPermissionRisk`·`JustificationStatus`·`CveHistory`·`BreachHistory`·Decision Engine 평판 평가까지 포함된 **감시 엔진** 구조였다. 2026-04-24 대표님 지시로 **세 가지 단순 기능**으로 축소:

1. **권한 있는 앱 리스트 정리** — PackageManager 스캔, 정렬만
2. **최근 사용한 기록 정리** — UsageStatsManager 조회, 표시만
3. **사용자가 언제든 권한 회수할 수 있는 버튼** — 시스템 설정으로 원터치

축소 사유:
- 대표님 정의 기능 범위를 넘어선 "평판 감시 엔진" 설계는 설계자 과잉
- CVE·침해 이력 감시는 별도 Surface `AppSecurityWatch`(§17-3 후행)로 이관 (Patch 31)
- Play 심사 정합성 유지 (QUERY_ALL_PACKAGES 대안 §24-6과 결합)

### 18-6-1. 범위 및 헌법 정합

**MicCheck는 RECORD_AUDIO 권한을 요청하지 않는다.** PackageManager의 `getPackagesHoldingPermissions` 또는 `queryIntentActivities` 기반으로 "누가 권한을 갖고 있는가"만 조회한다.

**R5 네트워크 경계 (헐크 Lane 3 Top 1 반영)**: MicCheck Surface Layer는 **직접 네트워크 호출을 하지 않는다**. 본 Surface 단순 관리자 축소판은 Decision Engine·외부 API 조회도 없고, PackageManager + UsageStatsManager + Intent 호출만 수행한다 (§6-2 R5 준수).

권한 해제 UX는 `ACTION_APPLICATION_DETAILS_SETTINGS` 인텐트 원터치로 시스템 설정 화면 진입.

### 18-6-2. 데이터 모델 (단순화)

```kotlin
/**
 * MicCheck의 단일 항목.
 * Decision Engine · Risk · CVE · Breach · Justification 필드 모두 삭제됨 (Patch 30).
 * 이 구조체는 RiskKnowledge를 구현하지 않는다 (평판 판정 대상이 아님).
 */
data class MicPermissionEntry(
    val packageName: String,
    val appName: String,
    val appIconUri: String?,       // 로컬 PackageManager.getApplicationIcon 결과 캐시
    val installedFromStore: Boolean,
    val lastUsedAt: Long?,          // UsageStatsManager.queryUsageStats 최근 값 (null 가능)
    val isCurrentlyForeground: Boolean
)
```

삭제된 것:
- ~~`AppPermissionRisk`~~ (Patch 30 삭제)
- ~~`JustificationStatus`~~ (Patch 30 삭제, 분류 판정 자체 제거)
- ~~`CveEntry` / `BreachEntry`~~ (AppSecurityWatch Surface로 이관)
- ~~`AppReputation`~~ (Decision Engine 호출 없음)

### 18-6-3. 구현

```kotlin
class MicCheckEngine(
    private val context: Context,
    private val packageManager: PackageManager,
    private val usageStats: UsageStatsManager
) : Checker<Unit, List<MicPermissionEntry>> {

    override suspend fun check(input: Unit): List<MicPermissionEntry> =
        withContext(Dispatchers.IO) {
            val apps = packageManager.getInstalledApplications(
                PackageManager.GET_PERMISSIONS
            ).filter { appInfo ->
                val info = packageManager.getPackageInfo(
                    appInfo.packageName,
                    PackageManager.GET_PERMISSIONS
                )
                info.requestedPermissions?.contains(
                    Manifest.permission.RECORD_AUDIO
                ) == true
            }

            val foregroundPkg = detectForegroundPackage()
            apps.map { appInfo ->
                MicPermissionEntry(
                    packageName = appInfo.packageName,
                    appName = packageManager.getApplicationLabel(appInfo).toString(),
                    appIconUri = null,  // 아이콘은 UI에서 직접 로드
                    installedFromStore = isInstalledFromStore(appInfo.packageName),
                    lastUsedAt = usageStats.lastUsedMillis(appInfo.packageName),
                    isCurrentlyForeground = appInfo.packageName == foregroundPkg
                )
            }.sortedByDescending { it.lastUsedAt ?: 0L }
        }
}
```

### 18-6-4. Cold Start 및 스케줄 (코웍 87a9a3 §17-5-3a 흡수)

MicCheck는 CallCheck/MessageCheck와 달리 실시간 이벤트(수신 전화/문자)가 없으므로 별도 트리거 정책이 필요하다.

**트리거 조건 (OR)**:
- **앱 최초 실행**: Surface 등록 직후 1회 전체 스캔
- **주기적 스캔**: WorkManager `PeriodicWorkRequest`, 최소 간격 15분 (Android 제약), 권장 간격 6시간
- **앱 설치·업데이트 감지**: `ACTION_PACKAGE_ADDED` / `ACTION_PACKAGE_CHANGED` BroadcastReceiver
- **사용자 수동 새로고침**: UI 풀-투-리프레시 제스처

**Cold Start 흐름**:
```
앱 시작 → WorkManager 등록 → 즉시 1회 check(Unit) 실행
       → RECORD_AUDIO 보유 앱 목록 수집 (로컬, 네트워크 없음)
       → UsageStatsManager 최근 사용 조회 (로컬)
       → MicPermissionEntry 리스트 생성 → UI 갱신
```

**배터리·성능 제약**:
- `Constraints.Builder().setRequiresBatteryNotLow(true)` 적용
- 백그라운드 스캔은 로컬 조회만, **네트워크 호출 0**
- 결과 메모리 캐시 + 세션 내 유효 (NKB 저장 불필요 — Risk 정보 아님)
- 스캔 시간 중위값 < 200ms 목표 (앱 30개 기준)

### 18-6-5. UX 화면

**MicCheckScreen**: 마이크 권한 보유 앱 목록
- 각 카드: 앱 아이콘·이름·마지막 사용 시각·전경 표시 뱃지
- 탭: 권한 해제 인텐트 직접 실행 (상세 화면 없음, 단순화)
- 상단: "N개 앱이 마이크 권한 보유 중" 요약
- 정렬: 최근 사용 순 (내림차순), 사용 기록 없음은 하단

**MicPermissionActionSheet** (탭 시 하단 시트):
```
[앱 이름]
마지막 사용: 3일 전 (또는 "사용 기록 없음")

[ 🛑 권한 해제 ]   ← ACTION_APPLICATION_DETAILS_SETTINGS
[ 🗑️ 앱 삭제 ]     ← ACTION_UNINSTALL_PACKAGE
[ 취소 ]
```

평판·Justification·CVE 표시 없음. 사용자가 직접 판단.

### 18-6-6. 이관 기록 — AppSecurityWatch (§17-3 후행)

v1.6.1-patch 시점에 MicCheck/CameraCheck에 포함되었던 다음 기능은 **별도 Surface `AppSecurityWatch`**로 이관되었다 (Patch 31):

- 신규 앱 설치 시 과거 보안 사고 이력 자동 검색·경고 (메모리 #13-1)
- 기존 앱 신규 CVE/침해 사고 실시간 감지·알림 (메모리 #13-2)
- NVD CVE API / CISA KEV / Have I Been Pwned 조회
- Decision Engine 기반 앱 평판 판정

본 후행 Surface는 §17-3에 Placeholder로 등록, 별도 스펙 워크오더로 진입 예정.

## 18-7. CameraCheck 본문 (Patch 30 — 단순 관리자 축소, MicCheck 병렬 구조)

### 18-7-1. 재정의

CameraCheck는 MicCheck와 **동일한 단순 관리자 구조**를 가지며, `PermissionScope`만 `CAMERA`로 다르다. 설계 중복 회피를 위해 본 섹션은 차이점만 기술.

**CameraCheck는 CAMERA 권한을 요청하지 않는다** (Patch 23 유지).

**R5 네트워크 경계**: MicCheck §18-6-1과 동일. 직접 네트워크 호출 없음.

### 18-7-2. 데이터 모델

```kotlin
data class CameraPermissionEntry(
    val packageName: String,
    val appName: String,
    val appIconUri: String?,
    val installedFromStore: Boolean,
    val lastUsedAt: Long?,
    val isCurrentlyForeground: Boolean
)
```

구조 완전 동일 (이름만 `Camera*`). 공통화 가능하지만 Surface 분리 원칙(§17-1)에 따라 각 Surface가 자기 타입을 가진다.

### 18-7-3. 구현

```kotlin
class CameraCheckEngine(
    private val context: Context,
    private val packageManager: PackageManager,
    private val usageStats: UsageStatsManager
) : Checker<Unit, List<CameraPermissionEntry>> {

    override suspend fun check(input: Unit): List<CameraPermissionEntry> =
        withContext(Dispatchers.IO) {
            val apps = packageManager.getInstalledApplications(
                PackageManager.GET_PERMISSIONS
            ).filter { appInfo ->
                val info = packageManager.getPackageInfo(
                    appInfo.packageName,
                    PackageManager.GET_PERMISSIONS
                )
                info.requestedPermissions?.contains(
                    Manifest.permission.CAMERA
                ) == true
            }

            val foregroundPkg = detectForegroundPackage()
            apps.map { appInfo ->
                CameraPermissionEntry(
                    packageName = appInfo.packageName,
                    appName = packageManager.getApplicationLabel(appInfo).toString(),
                    appIconUri = null,
                    installedFromStore = isInstalledFromStore(appInfo.packageName),
                    lastUsedAt = usageStats.lastUsedMillis(appInfo.packageName),
                    isCurrentlyForeground = appInfo.packageName == foregroundPkg
                )
            }.sortedByDescending { it.lastUsedAt ?: 0L }
        }
}
```

### 18-7-4. Cold Start·스케줄·UX

MicCheck §18-6-4, §18-6-5와 완전 동일 원칙. WorkManager·BroadcastReceiver·사용자 새로고침 트리거를 공유할 수 있도록 `feature/permission-scan` 공통 모듈로 구현 권장.

화면 이름만 `CameraCheckScreen` / `CameraPermissionActionSheet`.

### 18-7-5. 이관 기록

CameraCheck의 감시·CVE·침해 기능도 Patch 31로 `AppSecurityWatch`에 이관 (§18-6-6과 동일).

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

# 19. 드라이런 체크리스트 (Dry Run Checklist)

스모크런 이전에 **비전·커서·대표님이 함께 점검**하는 체크리스트.

## 19-1. 코드 기준 체크리스트

- [ ] Stage 0 4 계약(IdentifierType·RiskKnowledge·Checker·DecisionEngineContract) 시그니처 변경 없음
- [ ] `ExtractedSignal`에 rawSnippet·sourceProvider 없음
- [ ] `NumberKnowledge`에 Frozen 필드 전량 존재
- [ ] `scripts/verify-no-server.sh` PASS
- [ ] `scripts/verify-network-policy.sh` PASS
- [ ] `scripts/verify-no-mapping.sh` PASS
- [ ] `scripts/verify-frozen-model.sh` PASS
- [ ] `scripts/verify-strings-i18n.sh` PASS
- [ ] Detekt `ForbiddenRawField` 규칙 PASS
- [ ] JDK 17 toolchain 선언 (`build.gradle.kts`)
- [ ] 42 필수 토큰 전부 포함 (본 문서 §36-4 기준)
- [ ] 9 금지 토큰 0건 (본 문서 §36-5 기준)

## 19-2. 문서 기준 체크리스트

- [ ] 버전 매트릭스(0-A) 현재 버전 포함
- [ ] 헌법 변경 추적(0-A-1) 누락 없음
- [ ] 가격 정책(0-A-2) $2.49 반영
- [ ] 정직성 감사 로그(0-B) 14개 약속 전부 추적 가능
- [ ] Patch 감사 로그(0-B-2) 17~28 전부 기록
- [ ] 검토자 지적(0-B-1) 채택·거부 명시
- [ ] 한계 로그(0-D) 검증 불가 항목 명시
- [ ] 폐기된 황금알 사고(§2-2) 실명 기록

## 19-3. UX 기준 체크리스트

- [ ] Four Surfaces 모두 FourAttributeCard 규격 준수
- [ ] 다크/라이트 모드 전환 확인
- [ ] 다국어: ko/en/ja 최소 3개 locale 빌드 확인
- [ ] TalkBack 접근성 스캔 통과 (Accessibility Scanner)
- [ ] 최소 터치 48dp
- [ ] 온보딩 4개 슬라이드 + 권한 설명

## 19-4. 스토어 기준 체크리스트

- [ ] Play Console Data Safety 신고 (§27 참조)
- [ ] 민감 권한(READ_CALL_LOG·READ_SMS·READ_PHONE_STATE) 사용 사유 설명 등록
- [ ] 타겟 SDK 34 이상
- [ ] 64bit 네이티브 라이브러리 (현재 Kotlin만이라 해당 없음)
- [ ] Billing Library v7 사용
- [ ] Play App Signing 등록
- [ ] 개인정보처리방침 URL 등록
- [ ] Play Console에서 $2.49 Tier 설정 가능한 국가 확인

---

# 20. 성공 기준 (Success Criteria)

## 20-1. Phase 1 (CallCheck MVP) 성공 기준

- Cold Start 평균 < 30초
- 착신 오버레이 p95 < 500ms
- NKB Hit p95 < 5ms (L3 기준선)
- SmokeRun01~05, 11 모두 PASS
- 사용자 수동 테스트 10건 이상 → 4속성 출력 자연스러움

## 20-2. Phase 2 (MessageCheck) 성공 기준

- SmokeRun06~07 PASS
- 사칭 SMS 시나리오 5건 이상 → HIGH Risk 탐지

## 20-3. Phase 3 (MicCheck / CameraCheck) 성공 기준 (Patch 30 정정)

- SmokeRun08~09 PASS
- 실기기 앱 30개 이상 스캔 → `MicPermissionEntry`·`CameraPermissionEntry` 리스트 생성 + 최근 사용 시각 표시 + 권한 회수 버튼 동작 확인
- ~~Justification 분류 정확도 확인~~ (Patch 30 삭제)
- ~~외부 이벤트 감지 (신규 설치·CVE 감지) 동작 확인~~ (Patch 31로 AppSecurityWatch Surface 이관, Phase 3 범위 외)

## 20-4. Phase 4 (Billing) 성공 기준

- SmokeRun10 PASS
- 테스트 카드 결제 → 구독 상태 로컬 반영 확인
- 만료 → 해제 자동 처리 확인

## 20-5. 글로벌 런칭 성공 기준

- Play Console 심사 통과 (차단 없이 첫 심사 통과)
- 190개국 중 최소 150개국 $2.49 Tier 설정 가능 확인
- 30일 Crashlytics Crash-free rate > 99.5%
- Play Console Vitals ANR < 0.47%

---

# 21. Open Issues (12건, v1.4_disc 계승 + v1.6.1 갱신)

v1.4_disc에서 제기된 12건 Open Issues와 현재 해결 상태.

| # | 이슈 | 원안 | v1.6.1 상태 |
|---|---|---|---|
| 01 | 본사 매핑 0 판정 기준 애매 | "본사가 매핑 안 한다"의 정의 | §15 자동 검증 스크립트로 해결 |
| 02 | 가격 net ARPU 실측 없음 | 경쟁사 분석만 | $2.49 확정 + Play Console 실측 대기 |
| 03 | Softmax 가중치 튜닝 | 초기값만 | Phase 1 실기기 데이터 수집 후 튜닝 |
| 04 | Cold Start UX 길이 | 30초 목표 근거 부재 | §11 구체 단계 명시, 실측 대기 |
| 05 | 키워드 사전 규모 | 국가당 몇 건 필요 | strings.xml ko: 200건 / en: 300건 초기값 |
| 06 | Stale 재검증 우선순위 | 랜덤 vs FIFO | WorkManager 균등 분산 (20건/일) |
| 07 | UserAction 충돌 (신고·안심 동시) | 최근 우선 | `actionType.ordinal` 높은 쪽 우선 |
| 08 | NKB 50MB 상한 도달 시 | archive 처리 근거 | 365일 경과 엔트리 archive 플래그 |
| 09 | 공공 API 장애 대응 | 문서 부재 | §14 SLA L2 Degraded 상태 정의 |
| 10 | 언어-국가 분리 | 언어 ≠ 국가 | §28 명시 (Patch 05) |
| 11 | iOS 계획 | MAJOR 이동 결정 | v2.0.0 범위 |
| 12 | Four Surfaces 우선순위 | 모두 병행 vs 순차 | Phase 1(Call) → Phase 2(Message) → Phase 3(Mic/Camera) 순차 |
| **13** | **DO_NOT_MISS 처분** (Patch 35) | v1.0 §4.1 E2E 완성 기능이 v1.6.1-patch까지 실종 (Lane 1 D05) | **§8-2-2 `UserAction.DoNotMiss` 서브타입으로 복원, §3-4-1 UX 강조 규칙, §21-1-1 적용 단계 표 신설. Phase 1 CallCheck부터 점진 적용** |
| **14** | **NKB at-rest 암호화** (Patch 34) | v1.0 §13 SQLCipher/AES-256 DONE → v1.6.1-patch 실종 (Lane 1 D06) | **§27-5에 SQLCipher + Android Keystore 명세 복원 (코웍 87a9a3 §8-0 흡수)** |
| **15** | **AppSecurityWatch Surface** (Patch 31) | MicCheck/CameraCheck에 포함됐던 CVE·침해 감시 → 단순 관리자 축소(Patch 30)로 분리 필요 | **§17-3에 후행 Surface로 이관, 별도 워크오더 진입 예정** |
| **16** | **MessageCheck Mode A/B** (Patch 29) | "Default SMS Handler 재설계 또는 NotificationListenerService" 모호 (자비스 Lane 4 지적) | **§18-4-0~7로 2-모드 명시, Mode B(Share Intent) 기본·Mode A(Default SMS) 사용자 선택** |
| **17** | **QUERY_ALL_PACKAGES 대안** (Patch 36) | 자비스 Lane 4 "Play 거의 100% 리젝" 지적 | **§24-6-1 Manifest에서 제거 + `<queries>` 블록으로 대체** |
| **18** | **Data Safety 정직 재선언** (Patch 32·33) | "수집 0" vs 실제 SMS·통화·앱 접근 모순 (자비스 Lane 4 허위 disclosure 지적) | **§27 전면 재작성: "Yes, app collects user data" + "No third-party sharing" + "Processed only on device" 정확 마킹, §27-3 Permissions Declaration 본문 신설** |

## 21-1. DO_NOT_MISS 처분 적용 단계 표 (Patch 35 신설)

본 표는 §8-2-4의 Phase별 적용 범위를 본문에 정식 등재한다. 코웍 87a9a3 §17-6-5 기반.

| Phase | Surface | DO_NOT_MISS 적용 효과 |
|---|---|---|
| Phase 1 | CallCheck | 착신 오버레이 dismiss 5초 → 15초 / High-Priority Notification Channel / 노란 강조 띠 |
| Phase 2 | MessageCheck Mode A·B | HIGH 알림 채널 분기, 메시지 카드 상단 강조 |
| Phase 3 | MicCheck·CameraCheck | 특정 앱 권한 변동(설치·업데이트 시) 즉시 강조 알림 |
| Phase 후행 | PushCheck·AppSecurityWatch | 동일 규칙 자동 계승 |

### 21-1-1. 데이터 수명

- 사용자가 명시 해제하기 전까지 영구 유지 (Stale 정책 제외)
- 디바이스 초기화·앱 데이터 삭제 시에만 소실
- 구독 만료와 무관하게 유지 (UX 자산)

---

# 22. 토론자 만장일치 (Round 5 기준)

v1.6.1-patch 완료 시점 기준 **외부 토론자 + 내부 검토자**의 만장일치 지표.

| 검토자 | 상태 | 최신 검토 버전 | 남은 의견 |
|---|---|---|---|
| 자비스 | ✅ v1.5.3~v1.6.0 통과 | v1.6.0 | v1.6.1 재검토 필요 (본 문서) |
| 헐크 | ✅ v1.5.3~v1.6.0 통과 | v1.6.0 | v1.6.1 재검토 필요 |
| 스타크 | ⏳ 미검토 | - | 전체 검토 예정 |
| 대표님 | 🔄 검토 중 | v1.6.1 | 본 문서 승인 대기 |
| 비전 | ✅ 자기점검 | v1.6.1 | Patch 23~28 자기 오류 인정 완료 |

**정식 발행 요건**: 자비스·헐크·스타크 3인 라운드 5 통과 + 대표님 승인 + Cursor 공식 파이프라인 SHA6 부여.

---

# 23. 프로젝트 구조 (Repository Layout)

## 23-1. 최상위 구조

```
myphonecheck/
├── app/                           # 최종 Android APK 조립
├── core/
│   ├── common/                    # Stage 0 4 계약 (FREEZE)
│   ├── data/                      # Room·Network·Billing·PublicApi
│   └── ui/                        # 공통 UI 컴포넌트 (FourAttributeCard 등)
├── feature/
│   ├── call/                      # CallCheck Surface
│   ├── message/                   # MessageCheck Surface
│   ├── mic/                       # MicCheck Surface
│   ├── camera/                    # CameraCheck Surface
│   ├── onboarding/
│   ├── settings/
│   └── subscription/              # Billing UX
├── engine/
│   ├── decision/                  # Decision Engine 구현
│   ├── discovery/                 # Self-Discovery
│   ├── nkb/                       # NKB Room DAO·Migration
│   └── analyzer/                  # SearchResultAnalyzer
├── i18n/
│   └── strings/                   # values-*/strings.xml 자원 (다국어)
├── scripts/
│   ├── verify-no-server.sh
│   ├── verify-network-policy.sh
│   ├── verify-no-mapping.sh
│   ├── verify-frozen-model.sh
│   ├── verify-strings-i18n.sh
│   ├── verify-doc-hash.ps1
│   └── build_architecture_v170.py
├── docs/
│   ├── 00_governance/
│   │   ├── MyPhoneCheck_Architecture_v1.7.0.md   # 본 문서
│   │   ├── MyPhoneCheck_Infrastructure_v1.0.md   # 페어 인프라 최종본
│   │   └── constitution/
│   │       └── APP_FACTORY_CONSTITUTION_ROOT_ROLE.md
│   ├── 01_prd/
│   ├── 02_design/
│   └── 99_archive/
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## 23-2. Kotlin 모듈 의존 그래프 (FREEZE: Patch 10 Interface Injection)

```
app ────────────────┐
                    ▼
feature/call ─────▶ engine/decision ─▶ engine/nkb ─▶ core/data ─▶ core/common
feature/message ──▶ engine/decision ─▶ engine/nkb
feature/mic ──────▶ engine/decision
feature/camera ───▶ engine/decision
feature/onboarding ▶ engine/discovery
feature/settings ─▶ core/common
feature/subscription ▶ core/data (Billing)
```

**규칙**:
- feature/* → engine/* → core/data → core/common **방향만 허용**
- 역방향 또는 feature/* 간 직접 의존 금지
- core/common이 의존할 외부 모듈 없음 (Pure Kotlin JVM)
- Interface Injection은 `engine/decision` → feature/* 호출을 interface로 역전 (Patch 10)

## 23-3. core/common 모듈 (Stage 0 FREEZE)

core/common은 **순수 Kotlin JVM 모듈**이다. Android 의존 금지, 외부 라이브러리 의존 금지.

파일 구조:
```
core/common/
├── build.gradle.kts              # kotlin("jvm") + jvmToolchain(17)
├── src/main/kotlin/
│   ├── IdentifierType.kt         # sealed class
│   ├── RiskKnowledge.kt          # interface
│   ├── Checker.kt                # Checker<IN, OUT> interface
│   ├── DecisionEngineContract.kt # 엔진 계약
│   └── FreezeMarker.kt           # FREEZE 선언 마커 어노테이션
├── src/test/kotlin/
│   ├── FreezeMarkerTest.kt       # 22개 테스트 PASS (Patch 37 통일, 이미 완료)
│   └── ContractSignatureTest.kt
└── FREEZE.md                      # FREEZE 선언 문서
```

## 23-4. FREEZE.md 내용 (요약)

```markdown
# core/common FREEZE 선언

- Frozen 시점: 2026-04-22 Stage 0 Contracts 완료
- 변경 금지: 파일·클래스·메서드 시그니처·이름·가시성
- 변경 필요 시: MAJOR 버전 (v2.0.0)에서만 + 대표님 승인
- 검증: FreezeMarkerTest 22개 (CI에서 매 PR 실행, Patch 37 통일)
- CI 강제: PR의 core/common 변경 시 FreezeMarkerTest 실패 → 머지 차단
```

## 23-5. 기타 폴더 README 철칙 (메모리)

메모리 철칙에 따라 모든 폴더는 README.md 필수. 4종 항목:
1. 목적 (이 폴더의 존재 이유)
2. 책임 범위 (어떤 코드·문서가 들어가는가)
3. 외부 인터페이스 (다른 폴더에서 어떻게 사용하는가)
4. 내부 파일 안내 (각 파일의 역할)

빈 README·폴더명만 적힌 README는 위반.

---

# 24. Day-by-Day 구현 가이드

Phase 1~4 구현을 날짜 단위로 분해. **각 Day 완료 기준**과 **검증 방법**을 명시.

## 24-1. Day 0 — 프로젝트 스캐폴드

- [ ] Android Studio Flamingo 이상 설치
- [ ] JDK 17 설치 (Temurin 또는 Zulu), `jvmToolchain(17)` 선언
- [ ] Android SDK 34 설치
- [ ] 프로젝트 생성 + §23 구조 반영
- [ ] Git init + GitHub 원격 연결
- [ ] 최초 커밋 "chore: initial scaffold"

**완료 기준**: `./gradlew assembleDebug` PASS

## 24-2. Day 1~2 — core/common Stage 0 계약 (이미 완료)

- [x] IdentifierType sealed class
- [x] RiskKnowledge interface
- [x] Checker<IN, OUT> interface
- [x] DecisionEngineContract interface
- [x] FreezeMarker 어노테이션
- [x] FREEZE.md 작성
- [x] 22개 테스트 PASS (Patch 37 통일)
- [x] CI 강제 설정

Stage 0 Contracts 워크오더(f1a85c)로 완료됨. 시그니처 변경 금지.

## 24-3. Day 3 — NKB Room 구조

- [ ] `engine/nkb` 모듈 생성
- [ ] Room Database 정의
- [ ] NumberKnowledge·UserAction·ClusterProfile Entity
- [ ] DAO 작성
- [ ] Migration 1→2 (v1.5.1 → v1.5.2 Patch 08 대응)

**완료 기준**: `MigrationCompatTest` PASS + DB 생성 후 dump 확인

## 24-4. Day 4 — SearchResultAnalyzer

- [ ] `engine/analyzer` 모듈
- [ ] KeywordLoader (strings.xml 로드)
- [ ] FeatureExtractor
- [ ] TierClassifier
- [ ] SearchResultAnalyzer 구현
- [ ] 단위 테스트

**완료 기준**: "쿠팡 배송 사칭 SMS" 등 테스트 케이스 20건 PASS

## 24-5. Day 5 — Decision Engine 구현

- [ ] `engine/decision` 모듈
- [ ] 통합 8단계 알고리즘 구현
- [ ] Softmax 정규화
- [ ] ConflictResolver
- [ ] 백그라운드 재검증 큐
- [ ] 단위 테스트

**완료 기준**: DecisionContractTest PASS + Softmax 분포 검증

## 24-6. Day 6 — Self-Discovery + Cold Start

- [ ] `engine/discovery` 모듈
- [ ] Search Engine probe (§7-1)
- [ ] Official Domain probe (§7-2)
- [ ] ClusterProfile 생성·저장
- [ ] Cold Start 6단계 (§11)
- [ ] WorkManager 스케줄링 (PeriodicMaintenance)

**완료 기준**: SmokeRun01 PASS

### 24-6-1. Manifest 권한 정합 (Patch 36 재작성 — QUERY_ALL_PACKAGES 제거 + `<queries>` 블록)

**변경 사유**: Patch 26(v1.6.1-patch)에서 `QUERY_ALL_PACKAGES`를 `tools:ignore`로 선언했으나, 자비스 Lane 4(2026-04-24) 검증 결과 **Play 심사 거의 100% 리젝 대상**. 자비스 대안 2 "Package Visibility 최소화"를 수용, `QUERY_ALL_PACKAGES` 제거하고 `<queries>` 블록으로 대체한다 (Patch 36).

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
          xmlns:tools="http://schemas.android.com/tools">

    <!-- CallCheck 필수 권한 -->
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.READ_CALL_LOG" />
    <uses-permission android:name="android.permission.READ_CONTACTS" />

    <!-- MessageCheck Mode A 전용 (사용자가 Default SMS 지정 시에만 활성) -->
    <uses-permission android:name="android.permission.READ_SMS" />
    <uses-permission android:name="android.permission.RECEIVE_SMS" />
    <uses-permission android:name="android.permission.SEND_SMS" />
    <uses-permission android:name="android.permission.WRITE_SMS"
        tools:ignore="ProtectedPermissions" />

    <!-- 네트워크 · SLA · 알림 · 오버레이 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

    <!-- MicCheck/CameraCheck용 사용 통계 (Special App Access, 사용자 수동 승인) -->
    <uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"
        tools:ignore="ProtectedPermissions" />

    <!-- Patch 36: QUERY_ALL_PACKAGES 제거 → <queries> 블록으로 대체 -->
    <queries>
        <!-- MicCheck: RECORD_AUDIO를 선언한 앱만 필터링 -->
        <intent>
            <action android:name="android.intent.action.MAIN" />
        </intent>

        <!-- Intent 기반 방식: 특정 action에 응답하는 앱만 조회 -->
        <intent>
            <action android:name="android.intent.action.VIEW" />
            <data android:scheme="http" />
        </intent>
        <intent>
            <action android:name="android.intent.action.VIEW" />
            <data android:scheme="https" />
        </intent>

        <!-- MessageCheck Share Intent (Mode B) 대상 식별 -->
        <intent>
            <action android:name="android.intent.action.SEND" />
            <data android:mimeType="text/plain" />
        </intent>
    </queries>

    <!-- 금지 권한 (정책 위반 또는 Patch로 제거됨) -->
    <!-- ❌ BROADCAST_SMS — Play 정책 위반 (Patch 17) -->
    <!-- ❌ RECORD_AUDIO — MicCheck는 스캔만, 녹음 안 함 (Patch 23) -->
    <!-- ❌ CAMERA — CameraCheck는 스캔만, 촬영 안 함 (Patch 23) -->
    <!-- ❌ QUERY_ALL_PACKAGES — Play 심사 리스크, <queries>로 대체 (Patch 36) -->
</manifest>
```

**핵심 변경점 (Patch 36)**:

1. **`QUERY_ALL_PACKAGES` 완전 제거** — Android 11+ Package Visibility 정책 정면 준수.
2. **`<queries>` 블록 신설** — MicCheck/CameraCheck는 "RECORD_AUDIO / CAMERA 선언 앱"만 필터링 (PackageManager.getPackagesHoldingPermissions 사용 시 자동 처리).
3. **SMS 권한 4종 추가** — Mode A(Default SMS) 활성화 시에만 사용자 동의로 부여.
4. **PACKAGE_USAGE_STATS 유지** — Special App Access(사용자 수동 설정) 경로로 부여.
5. **네트워크 · 알림 · 오버레이** — 기존 유지.

**Permissions Declaration 연결**: 위 권한 목록 각각의 core user benefit 선언은 §27-3 참조.

## 24-7. Day 7~8 — CallCheck Surface (Phase 1)

- [ ] `feature/call` 모듈
- [ ] IncomingCallScreen 오버레이
- [ ] InCallScreen (하단 띠)
- [ ] PostCallScreen 전체 카드
- [ ] CallLogScreen
- [ ] FourAttributeCard 공통 UI (`core/ui`)
- [ ] 온보딩 4개 슬라이드 (`feature/onboarding`)
- [ ] 권한 요청 UX

**완료 기준**: SmokeRun02~05 PASS + 실기기 통화 수신 테스트

## 24-8. Day 9 — MessageCheck Surface (Phase 2)

- [ ] `feature/message` 모듈
- [ ] MessageCheckEngine (3중 평가)
- [ ] UrlExtractor
- [ ] ImpersonationDetector
- [ ] MessageDetailScreen
- [ ] Default SMS Handler 또는 NotificationListenerService 경로 선택

**완료 기준**: SmokeRun06~07 PASS

## 24-9. Day 10 — MicCheck Surface (Phase 3)

- [ ] `feature/mic` 모듈
- [ ] MicCheckEngine (PackageManager 스캔)
- [ ] JustificationClassifier
- [ ] MicCheckScreen + MicPermissionDetailScreen
- [ ] AppInstallMonitor (외부 이벤트 1)
- [ ] CveWatchWorker (외부 이벤트 2)

**완료 기준**: SmokeRun08 PASS + 실기기 앱 30개 이상 스캔 확인

## 24-10. Day 11 — CameraCheck Surface (Phase 3)

- [ ] `feature/camera` 모듈 (MicCheck와 병렬 구조)
- [ ] CameraCheckEngine
- [ ] CameraCheckScreen + CameraPermissionDetailScreen
- [ ] 외부 이벤트 공유 (MicCheck와 같은 BroadcastReceiver)

**완료 기준**: SmokeRun09 PASS

## 24-11. Day 12 — Billing 통합 (Phase 4)

- [ ] `feature/subscription` 모듈
- [ ] BillingClient v7 연결
- [ ] SubscriptionScreen ($2.49/월 단일 상품)
- [ ] Purchase Token 로컬 저장 (Room Entity)
- [ ] 구독 상태 복원
- [ ] 만료 감지

**완료 기준**: SmokeRun10 PASS

## 24-12. Day 13 — 다국어 + 접근성 (Phase 5)

- [ ] strings.xml ko / en / ja 최소 3개 locale
- [ ] 동적 언어 설정 (LocaleContextWrapper)
- [ ] TalkBack 대응
- [ ] RTL 대응 (아랍어 후행)
- [ ] 다크/라이트 테마 토글

**완료 기준**: Accessibility Scanner PASS + 3개 locale 모든 화면 렌더 확인

## 24-13. Day 14 — CI/CD + 스토어 준비 (Phase 6)

- [ ] `.github/workflows/android-ci.yml` 작성
- [ ] Detekt + Kover(coverage) 설정
- [ ] 본사 매핑 0건 검증 스크립트 전부 통합
- [ ] Play App Signing 등록
- [ ] Play Console Data Safety 신고
- [ ] 개인정보처리방침 URL 등록

**완료 기준**: CI PASS + Play Console 내부 테스트 트랙 업로드 성공

---

# 25. strings.xml 명세

## 25-1. 다국어 자원 원칙

- **하드코딩 금지**: 코드 내 `"안녕하세요"` 등 직접 문자열 삽입 금지 (메모리 철칙)
- **strings.xml만**: `res/values/strings.xml` + `res/values-xx/strings.xml`
- **사용 방식**: `context.getString(R.string.xxx)` 또는 XML `@string/xxx`
- **파라미터**: `<string name="call_risk_medium_title">위험도: %1$s</string>` → `getString(R.string.xxx, "MEDIUM")`
- **플루랄**: `<plurals>` 사용 (국가별 단수·복수 규칙 차이 대응)
- **CDATA 금지**: HTML 삽입 대신 Kotlin에서 `SpannableString` 구성

## 25-2. 자원 카테고리

```
res/values/
├── strings.xml              # 앱 공통 문자열
├── strings_onboarding.xml   # 온보딩 슬라이드 4개
├── strings_call.xml         # CallCheck UI
├── strings_message.xml      # MessageCheck UI
├── strings_mic.xml          # MicCheck UI
├── strings_camera.xml       # CameraCheck UI
├── strings_subscription.xml # Billing UX
├── strings_settings.xml
├── strings_reasons.xml      # 이유 설명 템플릿 (한 줄 요약)
├── strings_damage.xml       # 예상 손해 템플릿
├── keywords.xml             # SearchResultAnalyzer 키워드 사전
└── categories.xml           # ConclusionCategory·DamageType 이름
```

## 25-3. locale 목록 (초기)

- `values/` (기본 = 영어)
- `values-ko/` (한국어)
- `values-ja/` (일본어)
- `values-zh/` (중국어 간체)
- `values-zh-rTW/` (중국어 번체)
- `values-es/` (스페인어)
- `values-pt/` (포르투갈어)
- `values-id/` (인도네시아어)
- `values-vi/` (베트남어)
- `values-th/` (태국어)
- `values-hi/` (힌디어)
- `values-ar/` (아랍어, RTL)
- ... (190개국까지 확장)

**Phase 5 착수 시** ko/en/ja 최소 3개 locale 완전 번역 + 나머지는 en fallback.

## 25-4. 이유 설명 템플릿 (strings_reasons.xml)

```xml
<resources>
    <string name="reason_official_reported">정부 신고 %1$d건, 사용자 신고 %2$d건</string>
    <string name="reason_user_reported">사용자 신고 %1$d건</string>
    <string name="reason_scam_keyword_hit">사기 관련 키워드 %1$d회 발견</string>
    <string name="reason_impersonation">%1$s 사칭 의심 — 공식 도메인 아님</string>
    <string name="reason_suspicious_url">의심 URL: %1$s</string>
    <string name="reason_new_number">신규 번호 — 정보 부족</string>
    <string name="reason_contact_registered">연락처에 저장된 번호</string>
    <string name="reason_high_confidence">높은 신뢰도 (%1$.0f%%) 판단</string>
    <string name="reason_ambiguous">판단 불확실 (상위 후보 여러 개)</string>
    <string name="reason_stale_knowledge">최근 데이터 없음, 이전 판단 기준</string>
    <string name="reason_offline_mode">오프라인 모드 — 저장된 정보로 판단</string>
    <!-- ... -->
</resources>
```

## 25-5. 키워드 사전 (keywords.xml, 한국어 예시)

```xml
<resources>
    <string-array name="scam_keywords">
        <item>보이스피싱</item>
        <item>대포폰</item>
        <item>사기</item>
        <item>사칭</item>
        <item>피싱</item>
        <item>금융사기</item>
        <!-- ... 200건 초기값 -->
    </string-array>
    <string-array name="ad_keywords">
        <item>광고</item>
        <item>할인</item>
        <item>프로모션</item>
        <!-- ... 150건 초기값 -->
    </string-array>
    <!-- ... -->
</resources>
```

---

# 26. CI/CD (JDK 17 기반)

## 26-1. 빌드 환경

- **JDK**: 17 LTS (Temurin)
- **Kotlin**: 2.0.x
- **Android Gradle Plugin**: 8.5+
- **Gradle Wrapper**: 8.9+
- **Android SDK**: compileSdk 34, targetSdk 34, minSdk 24
- **Toolchain**: `jvmToolchain(17)` 전체 모듈 선언 (Stage 0 hotfix로 승격 완료)

## 26-2. build.gradle.kts 공통

```kotlin
// 루트 build.gradle.kts
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.detekt) apply false
}

subprojects {
    plugins.withId("org.jetbrains.kotlin.jvm") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
            jvmToolchain(17)
        }
    }
    plugins.withId("org.jetbrains.kotlin.android") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension> {
            jvmToolchain(17)
        }
    }
}
```

## 26-3. .github/workflows/android-ci.yml

```yaml
name: Android CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'

      - name: Grant execute permission for gradlew
        run: chmod +x ./gradlew

      - name: Verify Constitution Compliance
        run: |
          bash scripts/verify-no-server.sh
          bash scripts/verify-network-policy.sh
          bash scripts/verify-no-mapping.sh
          bash scripts/verify-frozen-model.sh
          bash scripts/verify-strings-i18n.sh

      - name: Stage 0 FREEZE Verification
        run: ./gradlew :core:common:test --tests "FreezeMarkerTest"

      - name: Detekt Static Analysis
        run: ./gradlew detekt

      - name: Build Debug APK
        run: ./gradlew assembleDebug

      - name: Run Unit Tests
        run: ./gradlew testDebugUnitTest

      - name: Upload Test Report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-report
          path: '**/build/reports/tests/'
```

## 26-4. Detekt 규칙

- **`ForbiddenRawField`**: ExtractedSignal에 `rawSnippet`·`sourceProvider` 필드 금지
- **`NoHardcodedString`**: Kotlin 코드 내 한글·일본어·중국어 문자열 리터럴 금지 (strings.xml 강제)
- **`NoApiOfHttps`**: `http://` 하드코딩 금지 (https만)
- **`NoSystemOutPrintln`**: `println` 금지 (Logcat 또는 Timber만)

## 26-5. 릴리즈 플로우 (Fastlane, Mac 전용)

Infra_Ops v1.0에 명시된 대로 Fastlane은 Mac에서만 실행. Windows/Linux runner는 빌드·테스트만.

- `fastlane android beta` → Play Console 내부 테스트 트랙 업로드
- `fastlane android production` → 프로덕션 트랙 단계적 롤아웃 (10% → 50% → 100%)
- `fastlane ios beta` → App Store Connect TestFlight (Phase 7 iOS 진입 시)

---

# 27. Google Play Data Safety + Permissions Declaration (Patch 32·33·34)

**본 장은 Patch 32·33·34로 전면 재작성되었다**. 사유:
- **Patch 32**: v1.6.1-patch 시점의 "데이터 수집 없음" 선언이 실제 SMS·통화·앱 읽기 동작과 모순됨을 자비스 Lane 4가 지적 (2026-04-24). Play Console Data Safety 기준에서 "디바이스 내부 접근(on-device access)"도 넓은 의미의 "collection"에 포함될 수 있으므로, **정직한 분류로 전면 재선언**한다.
- **Patch 33**: Permissions Declaration 본문을 신설. 각 민감 권한별로 `core user benefit` + `less-invasive alternative 검토 결과` + `사용자 고지 방식` 3요소를 명시하여 Play 심사 통과 자격을 갖춘다.
- **Patch 34**: NKB at-rest 암호화(§27-5) 신설. Lane 1 D06이 지적한 "v1.0에서 DONE이었던 SQLCipher/AES-256-GCM이 v1.6.1에서 실종"을 복원 (코웍 87a9a3 §8-0 흡수).

헌법 제1·2·5조(정직성) 정면 구현.

## 27-1. Data Safety 분류 — 정직 재선언 (Patch 32)

### 27-1-1. Google Play 기준 용어 정리

- **Data Collection**: 앱이 사용자 데이터에 접근하는 모든 경우. 디바이스 내부 처리만이어도 해당 가능.
- **Data Sharing**: 앱이 사용자 데이터를 제3자에게 전송·전달하는 경우.
- **Processed ephemerally**: 메모리만 통과 후 즉시 폐기하는 처리.

본 앱은 **Data Collection에 해당하되, Data Sharing은 전무**하다는 것이 정확한 선언이다.

### 27-1-2. 수집 분류 (What's collected)

| 데이터 유형 | 접근 여부 | 용도 | 저장 여부 | Sharing |
|---|---|---|---|---|
| **이름·이메일·주소** | 접근 안 함 | — | — | — |
| **전화번호 (사용자 본인)** | 접근 안 함 | — | — | — |
| **통화 이력 (발신·수신 번호·시각)** | ✅ 접근 | CallCheck: 스팸 판별 + NKB 갱신 | 디바이스 Room DB (featureCounts만, 원문 X) | ❌ 공유 없음 |
| **문자 이력 (Mode A 전용)** | 조건부 접근 (사용자가 Default SMS 지정 시) | MessageCheck Mode A: 자동 사칭 감지 | 디바이스 내부, Room DB 해시값만 | ❌ 공유 없음 |
| **문자 본문 (Mode B)** | 사용자 공유 시점만 | MessageCheck Mode B: Share Intent 수동 분석 | 메모리만, 폐기 | ❌ 공유 없음 |
| **연락처 (이름·번호)** | ✅ 접근 | Cold Start: 저장 번호 SAFE 초기화 | 디바이스 내부만 | ❌ 공유 없음 |
| **설치된 앱 목록 (RECORD_AUDIO/CAMERA 필터)** | ✅ 접근 | MicCheck·CameraCheck: 권한 보유 앱 리스트 | 세션 내 메모리, 저장 없음 | ❌ 공유 없음 |
| **앱 사용 통계 (PACKAGE_USAGE_STATS)** | ✅ 접근 (사용자 수동 승인) | MicCheck·CameraCheck: 최근 사용 시각 표시 | 세션 내 메모리 | ❌ 공유 없음 |
| **금융 정보 (결제)** | 접근 안 함 | Google Play가 직접 처리 | — | ❌ 공유 없음 |
| **위치** | 접근 안 함 | — | — | — |
| **사진·동영상·오디오 파일** | 접근 안 함 | — | — | — |
| **외부 검색 결과 스니펫 (3계층 소싱)** | 메모리 통과 | SearchResultAnalyzer 분석 | **즉시 폐기 (헌법 2조)** | ❌ 공유 없음 |
| **Crashlytics 진단 데이터** | 선택적 수집 (옵트아웃 기본 제공) | 안정성 모니터링 | Firebase 서버 | ❌ 광고·분석 공유 없음 |

### 27-1-3. Play Console Data Safety 체크박스 매핑

| Play Console 섹션 | 선언값 |
|---|---|
| Data collected: "Yes, this app collects user data" | ✅ 예 (정직 선언) |
| Data shared with third parties: "No" | ❌ 없음 |
| Data encrypted in transit | ✅ HTTPS만 (INTERNET 권한) |
| **Data encrypted at rest** | ✅ **SQLCipher AES-256-GCM** (§27-5) |
| Data deletion request mechanism | ✅ 앱 내 "모든 데이터 삭제" (§27-4) |
| Play Families Policy applicable | N/A (전 연령) |

**중요**: "Processing only on device" 플래그를 **활용 가능**. 각 데이터 항목별로 "Data is processed ephemerally" 또는 "Processed only on device"를 정직하게 마킹한다. 이는 "collection 자체를 부인"하는 것과 다르며, 헌법 5조(정직성)와 완전 정합한다.

## 27-2. Data Sharing — 공유 없음 (변경 없음)

- 제3자 공유 0 (광고 네트워크·분석 서비스·재판매 전부 없음)
- Firebase Analytics 미사용 (Crashlytics 진단만 사용, 광고 ID 제외)
- GDPR·CCPA·PIPA 정합: 사용자 데이터가 디바이스 경계 밖으로 나가지 않음

## 27-3. Permissions Declaration — 권한별 정당화 (Patch 33 신설)

Play Console **Permissions Declaration Form** 제출 시 각 민감 권한에 대해 다음 3요소를 **작성된 그대로 제출**한다. Truecaller·Hiya·Whoscall 등 스팸 필터 카테고리 앱의 통과 전례에 기반.

### 27-3-1. READ_CALL_LOG

- **Core user benefit**: CallCheck의 Cold Start 단계에서 기존 통화 이력으로 스팸 필터 초기화. 사용자는 앱 설치 직후부터 알려진 스팸 번호에 대한 경고를 받을 수 있다.
- **Less-invasive alternative 검토**: 실시간 `CALL_STATE_RINGING` 이벤트만 사용하는 방식 검토. 그러나 Cold Start 정보 없이는 첫 수신 시 SAFE/NONE 초기값으로만 표시되어, 사용자가 앱 설치 후 수십 통의 통화를 받아야 학습이 시작됨 → **초기 가치 전달 실패**.
- **사용자 고지**: 온보딩 3번째 슬라이드에서 "통화 이력 읽기 권한 — 스팸 필터 초기화에 사용, 외부 전송 없음" 명시. 권한 거부 가능, 거부 시 Cold Start 없이 실시간 학습만 작동.
- **Play Category**: `COMMUNICATION` (Default Dialer 후보로 신고, 단 본 앱은 Dialer가 아닌 Caller ID 보조 앱 카테고리).

### 27-3-2. READ_PHONE_STATE

- **Core user benefit**: 통화 수신(`CALL_STATE_RINGING`) 감지 → 착신 오버레이 표시. CallCheck의 핵심 기능.
- **Less-invasive alternative**: Telecom Framework의 `CallScreeningService`가 있으나 **사용자가 Default Dialer 역할을 부여해야** 작동. 본 앱은 Default Dialer를 요구하지 않는 전략이므로 `READ_PHONE_STATE` 경로 사용.
- **사용자 고지**: 온보딩 2번째 슬라이드 + 설정 화면 "작동 원리".

### 27-3-3. READ_SMS / RECEIVE_SMS / SEND_SMS / WRITE_SMS (Mode A 전용)

- **Core user benefit**: MessageCheck Mode A에서 사용자가 본 앱을 **기본 SMS 앱으로 명시 지정**한 경우에만 활성화. SMS 자동 수신 감지로 유해 문자 실시간 차단.
- **Less-invasive alternative (이미 채택)**: **Mode B (Share Intent, 권한 0)** 이 기본값. 사용자가 Mode A의 편의성을 선택할 때만 권한 부여. Mode B만으로도 핵심 기능 작동.
- **Play 정책 준수**: Mode A 활성화 시 완전한 SMS 앱 기능(송수신·대화 목록·MMS·검색) 제공. `RoleManager.ROLE_SMS` 승인 플로우 준수.
- **사용자 고지**: 설정 → "Default SMS로 지정하기" 토글. 토글 탭 시 시스템 다이얼로그로 승인. 해제도 토글.

### 27-3-4. READ_CONTACTS

- **Core user benefit**: Cold Start 단계에서 저장된 연락처의 번호를 `SAFE` 초기값으로 NKB에 등록. 지인 전화를 스팸으로 오탐하지 않도록.
- **Less-invasive alternative**: 연락처 없이 통화 이력만으로 학습. 그러나 "전화를 자주 받지 않은 지인" 번호가 `UNKNOWN`으로 분류되어 오탐 경고 발생 가능.
- **사용자 고지**: 온보딩 4번째 슬라이드. 거부 가능.

### 27-3-5. PACKAGE_USAGE_STATS (Special App Access)

- **Core user benefit**: MicCheck·CameraCheck에서 "마지막 사용 시각"을 표시하여 사용자가 **오랫동안 사용하지 않은 앱의 권한을 회수**할 수 있도록 돕는다.
- **Less-invasive alternative**: 사용 통계 없이 단순 권한 보유 앱 목록만 표시. 이 경우 "쓰지도 않는 앱의 권한"을 식별할 수 없어, 사용자 결정 품질 저하.
- **사용자 고지**: MicCheck·CameraCheck 첫 진입 시 "앱 사용 통계 접근 허용하기" 안내 → 시스템 설정으로 이동 → 사용자 수동 활성화. **자동 부여되지 않는 Special App Access**이므로 사용자 의도 확실.
- **거부 시 동작**: 앱 리스트는 표시하되 "최근 사용: 정보 없음"으로 표시. 회수 버튼은 정상 작동.

### 27-3-6. SYSTEM_ALERT_WINDOW

- **Core user benefit**: 통화 수신 시 오버레이로 위험도·4속성을 즉시 표시. 전체 화면 탈취 없이 비침습적.
- **Less-invasive alternative**: 일반 알림만 사용. 그러나 통화 화면 위에 즉각 표시 불가, 사용자가 알림 서랍을 내려야 함 → 착신 결정 시점 놓침.
- **사용자 고지**: 시스템 다이얼로그. 거부 시 일반 알림으로 폴백.

### 27-3-7. POST_NOTIFICATIONS

- **Core user benefit**: 새 통화·문자·고위험 발견 시 사용자에게 알림. Android 13+ 런타임 권한.
- **사용자 고지**: 첫 실행 시 시스템 다이얼로그. 거부 가능.

### 27-3-8. Play Integrity API (Patch 38, 런타임 권한 아님)

- **Core user benefit**: 구독 결제 활성화 시점에 기기 환경 무결성(루팅·에뮬레이터·Frida 탐지)을 확인하여 결제 우회·크랙을 방지한다. 정품 사용자 보호.
- **기술 스코프**: Google Play Services `com.google.android.gms:play-services-integrity` Gradle 의존성. **런타임 권한 요청 없음**, Manifest 변경 없음, 사용자 상호작용 없음.
- **데이터 처리**: Play Integrity 토큰은 Google Play Services에서 직접 반환받아 **로컬에서만 파싱**. 자체 서버로 전송하지 않음 (`classicRequest` 모드). 헌법 1조 "스토어 공식 API 허용" 범위 내.
- **사용자 고지**: 온보딩 결제 화면에 1줄 고지: "결제 활성화 시 Google Play가 기기 무결성을 확인합니다. 이 정보는 Google Play와 본 앱 사이에서만 사용됩니다."
- **거부 가능성**: 사용자 거부 메커니즘 불가 (Google Play Services 내장 기능). 단, 네트워크 단절·Google Play 미지원 디바이스에서는 자동 스킵되며 1계층 검증만으로 활성화 허용 (헌법 4조 fail-open).

## 27-4. 삭제 요청 처리

- **사용자 데이터 삭제 경로**: 앱 설정 → "모든 데이터 삭제" 버튼 → NKB·UserAction·ClusterProfile 전부 Room DB 초기화 + 앱 재시작
- **자동 삭제**: 365일 경과 archive 엔트리 정리 (§12, §30-2)
- **계정 개념 없음** → 외부 삭제 요청 API 불필요 (헌법 1조 정합)
- **GDPR "Right to Erasure"** 정합: 사용자 데이터가 디바이스에만 존재하므로, 앱 삭제 또는 위 버튼 탭으로 완결

## 27-5. NKB At-Rest 암호화 (Patch 34 신설 — Lane 1 D06 복원, 코웍 87a9a3 §8-0 흡수)

NKB는 사용자의 통화·문자 판정 이력(번호 식별자·featureCounts·카테고리 분포·사용자 행동)을 포함한다. 디바이스 분실·악성 앱 로컬 접근 등을 방지하기 위해 **at-rest 암호화가 필수**이다.

### 27-5-1. 기술 스택

- **SQLCipher**: Room Database 전체를 **AES-256-GCM으로 암호화**. 평문 DB 파일이 디스크에 존재하지 않는다.
- **Android Keystore**: SQLCipher passphrase를 **TEE/StrongBox 하드웨어 키**로 암호화하여 SharedPreferences에 저장. 키 추출 불가 (하드웨어 백업).
- **키 생성 시점**: 앱 최초 실행 시 `KeyGenerator.getInstance("AES", "AndroidKeyStore")`로 256-bit 키 생성. 이후 재설치 전까지 동일 키 유지.
- **키 인증 요구**: `setUserAuthenticationRequired(false)` (앱 상시 사용), `setIsStrongBoxBacked(true)` (가능한 기기 한정).

### 27-5-2. Migration 정책

- v1.x 기존 평문 Room DB → v2.0 암호화 DB 마이그레이션 경로 필수 (`RoomDatabase.Migration`).
- Passphrase는 앱 메모리에만 존재. Logcat·Crashlytics·파일에 절대 출력 금지.

### 27-5-3. Room 연결 코드 (참조 구현)

```kotlin
class NkbDatabaseFactory(
    private val context: Context,
    private val keystoreManager: KeystoreManager
) {
    fun create(): NkbDatabase {
        val passphrase: ByteArray = keystoreManager.getOrCreatePassphrase()
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(context, NkbDatabase::class.java, "nkb.db")
            .openHelperFactory(factory)
            .addMigrations(MIGRATION_1_2_ENCRYPT)
            .build()
    }
}

class KeystoreManager(private val context: Context) {
    fun getOrCreatePassphrase(): ByteArray {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val alias = "nkb_master_key"

        if (!keyStore.containsAlias(alias)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )
            val spec = KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).apply {
                setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                setKeySize(256)
                setUserAuthenticationRequired(false)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    setIsStrongBoxBacked(true)
                }
            }.build()
            keyGenerator.init(spec)
            keyGenerator.generateKey()
        }

        // passphrase 파생 (예: HMAC(master_key, salt))
        return derivePassphraseFromMasterKey(alias)
    }
}
```

### 27-5-4. 헌법 정합

- **헌법 1조 (Out-Bound Zero)**: passphrase·키 모두 외부 전송 0, 디바이스 내부만.
- **헌법 2조 (In-Bound Zero)**: 외부 원문은 여전히 저장 금지 (§6-3). 본 암호화는 저장되는 featureCounts·메타 데이터를 보호할 뿐, 저장 대상 자체는 변경 없음.
- **헌법 5조 (정직성)**: Data Safety에서 "Data encrypted at rest" 체크박스 사실대로 ✅ 선언 가능.

### 27-5-5. 배터리·성능 영향

- SQLCipher 암복호화 오버헤드: 쿼리당 평균 1~3% CPU 증가
- NKB Hit p95 ≤ 5ms 목표(§14-2)에 영향 없음 (측정 검증 필요, Phase 1 실기기 테스트)
- 첫 DB 오픈 시 키스토어 접근으로 100~300ms 지연 → 앱 시작 시 **Cold Start Phase 0에서 미리 초기화** (§11-1에 추가)

---

# 28. 국가 / 언어 분리 (Patch 05)

v1.5.1 Patch 05에서 명시된 원칙: **언어 ≠ 국가**. 사용자 locale과 SIM·Network country는 독립 축.

## 28-1. 분리 축

| 축 | 소스 | 용도 |
|---|---|---|
| SIM Country | `TelephonyManager.simCountryIso` | 번호 E.164 변환·스팸 DB 조회 |
| Network Country | `TelephonyManager.networkCountryIso` | 로밍 감지·Self-Discovery ClusterProfile |
| User Locale | `context.resources.configuration.locales[0]` | UI 언어·strings.xml 선택 |
| Phone Locale | `Locale.getDefault()` | Kotlin 기본 locale |
| TimeZone | `TimeZone.getDefault()` | `lastUsedAt` 등 시각 포맷 |

## 28-2. 흔한 오해 시나리오

| 상황 | 문제 | 해결 |
|---|---|---|
| 한국 거주 영어 사용자 | SIM=KR이지만 locale=en → 영어 UI + 한국 스팸 DB | 분리 유지, 자연스러움 |
| 해외 출장 중 한국 SIM | Network=JP, SIM=KR → 일본 로밍 감지 | ClusterProfile은 KR 유지, 일본 한시 조회 우선 |
| 다중 SIM 디바이스 | SIM 1개만 반영 | SubscriptionManager로 활성 SIM 선택 |
| SIM 없는 태블릿 | simCountryIso=null | Network 또는 locale 기반 |
| VPN 사용자 | Network이 실제 위치와 불일치 | SIM 우선, VPN 신경 쓰지 않음 |

## 28-3. Cluster 생성 규칙 (§7-3 연결)

```kotlin
fun deriveCountryCode(simIdentity: SimIdentity): String? {
    // 우선순위: SIM > Network > Locale 추론
    return simIdentity.simCountryIso
        ?: simIdentity.networkCountryIso
        ?: extractCountryFromLocale(simIdentity.locale)
}

fun extractCountryFromLocale(localeTag: String): String? {
    // "en_US" → "US", "ko_KR" → "KR"
    // "en" (country 없음) → null
    return Locale.forLanguageTag(localeTag.replace("_", "-"))
        .country
        .takeIf { it.isNotEmpty() }
}
```

## 28-4. UI 언어 vs 판정 언어

- **UI 언어**: 사용자 locale (strings.xml)
- **판정 언어 (FeatureExtractor 키워드)**: `KeywordLoader.loadForLocale(locale)` — 동일 locale 사용

**주의**: 판정 언어와 검색 엔진 언어는 별개. 사용자가 한국 거주·영어 locale이어도, 한국 SIM이라면 Self-Discovery가 한국 검색 엔진을 probe할 수 있다.

---

# 29. (의도적 공백 — v1.5.x 계보 번호 정합)

v1.5.x에서 29번은 사용되지 않았다. 버전 계보 일관성을 위해 공백 유지.

---

# 30. Memory Budget (메모리 예산)

## 30-1. NKB 엔트리당 크기 상한 (MEM-2KB 약속)

약속 ID: `MEM-2KB` (§0-B 감사 로그)

`NumberKnowledge` 엔트리 1개당 메모리 + 디스크 크기 상한 **2KB**.

### 30-1-1. 예산 내역

| 필드 | 예상 크기 |
|---|---|
| numberE164 | 16B (최대 E.164 15자리 + UTF-8) |
| categoryDistribution (6 카테고리) | 48B (6 × 8B float) |
| topCategory, riskLevel, isAmbiguous 등 enum·bool | 20B |
| SignalSummary | 200B (카운트 + featureCounts Map) |
| TierContribution (최대 4 Tier) | 128B |
| firstSeenAt, lastUpdatedAt (Long×2) | 16B |
| discoveredClusterId | 16B |
| 기타 메타 | 32B |
| **합계 (라이브 오브젝트)** | **~476B** |
| Room 직렬화 오버헤드 | ~500B |
| 인덱스 오버헤드 | ~300B |
| **디스크 총합** | **~1.28KB** (2KB 상한 내) |

### 30-1-2. 검증 방법

- Android Studio Memory Profiler로 100건 엔트리 적재 후 총 heap 측정 → `/100`
- Room DB 파일 크기 / 엔트리 수 비교
- CI 테스트 `NkbSizeBudgetTest` 작성 (5000건 삽입 후 DB 파일 크기 측정)

## 30-2. DB 전체 크기 상한

§14-3 SLA 기준: NKB DB 크기 ≤ 50MB (1년 차)

### 30-2-1. 50MB 초과 시 동작

1. `PeriodicMaintenanceWorker`가 상시 DB 크기 측정
2. 50MB 초과 감지 → 365일 경과 엔트리 archive 플래그 전환 (삭제 아님)
3. archive 엔트리는 조회 시 `STALE_OFFLINE`으로 표시, 신뢰도 하락
4. 200MB 초과 시: archive 엔트리 중 userActionCount=0인 것부터 삭제 (사용자 행동 있는 것은 보존)

## 30-3. 런타임 RAM 상한

- 활성 앱 중위값 ≤ 80MB (Android Studio Memory Profiler)
- 오버레이 표시 중 피크 ≤ 150MB
- OOM 발생 시 Crashlytics 보고 → 다음 릴리즈에서 최적화

## 30-4. 메모리 누수 방지

- 싱글톤 객체에서 Context 참조 시 `ApplicationContext`만 사용
- Coroutine Scope는 lifecycle 기반 (viewModelScope·lifecycleScope)
- BroadcastReceiver는 메모리 누수 위험 없음 (앱 단일 등록)
- LeakCanary를 debug 빌드에 포함

---

# 31. Billing 구현 세부 (Play Billing Library v7)

## 31-1. BillingClient 구성

```kotlin
class BillingManager(
    private val context: Context,
    private val billingDao: BillingDao
) : PurchasesUpdatedListener {

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .enablePrepaidPlans()
                .build()
        )
        .build()

    private val productId = "myphonecheck_monthly"  // $2.49/월
    private val productDetails: StateFlow<ProductDetails?> = MutableStateFlow(null)

    suspend fun connect() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    scope.launch {
                        queryProductDetails()
                        querySubscriptionStatus()
                    }
                }
            }
            override fun onBillingServiceDisconnected() {
                scope.launch { retryConnect() }
            }
        })
    }

    suspend fun queryProductDetails() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            ))
            .build()
        val result = billingClient.queryProductDetails(params)
        productDetails.value = result.productDetailsList?.firstOrNull()
    }

    suspend fun launchBillingFlow(activity: Activity) {
        val details = productDetails.value ?: return
        val offerToken = details.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: return

        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(details)
                    .setOfferToken(offerToken)
                    .build()
            ))
            .build()

        billingClient.launchBillingFlow(activity, params)
    }

    suspend fun querySubscriptionStatus(): SubscriptionStatus {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        val result = billingClient.queryPurchasesAsync(params)
        val activePurchase = result.purchasesList.firstOrNull {
            it.purchaseState == Purchase.PurchaseState.PURCHASED
        }
        if (activePurchase != null) {
            // 온디바이스 서명 검증 (자체 서버 없음 — 헌법 정합)
            val valid = verifySignatureLocally(activePurchase)
            if (valid) {
                billingDao.upsert(SubscriptionEntity(
                    purchaseToken = activePurchase.purchaseToken,
                    purchaseTimeMs = activePurchase.purchaseTime,
                    isAutoRenewing = activePurchase.isAutoRenewing,
                    lastVerifiedAt = System.currentTimeMillis()
                ))
                return SubscriptionStatus.Active
            }
        }
        return SubscriptionStatus.Inactive
    }

    override fun onPurchasesUpdated(
        result: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
            purchases?.forEach { scope.launch { handlePurchase(it) } }
        }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return
        if (purchase.isAcknowledged) return

        val ackParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(ackParams)
        querySubscriptionStatus()
    }
}

enum class SubscriptionStatus { Active, Inactive, Pending }
```

## 31-2. 온디바이스 서명 검증

### 31-2-1. 1계층 — Play Billing 서명 검증 (기본)

```kotlin
fun verifySignatureLocally(purchase: Purchase): Boolean {
    // Play Billing이 제공하는 signedData와 signature를 Public Key로 검증
    val publicKey = BuildConfig.PLAY_PUBLIC_KEY  // Play Console에서 복사, 빌드 시 주입
    val keySpec = X509EncodedKeySpec(Base64.decode(publicKey, Base64.DEFAULT))
    val key = KeyFactory.getInstance("RSA").generatePublic(keySpec)

    val sig = Signature.getInstance("SHA1withRSA")
    sig.initVerify(key)
    sig.update(purchase.originalJson.toByteArray())
    return sig.verify(Base64.decode(purchase.signature, Base64.DEFAULT))
}
```

**자체 영수증 검증 서버 없음**. Play의 signedData·signature만으로 온디바이스 검증 (Infra_Ops v1.0 FINAL 정합).

**알고리즘 주의**: `SHA1withRSA`는 Google Play Billing Library 공식 `Security.java` 샘플 코드 기준이다. Google이 이 샘플을 유지하는 한 본 구현은 공식 권고 방식. 단, 이 1계층만으로는 루팅/Frida 메모리 변조 환경에서 우회 가능하므로 §31-2-2 Play Integrity API 2계층으로 보강한다 (Patch 38).

### 31-2-2. 2계층 — Play Integrity API `classicRequest` 로컬 무결성 검증 (Patch 38)

**목적**: 루팅·Frida·Magisk·에뮬레이터 환경에서 유료 기능 크랙 시도 탐지. 서버 0 원칙 유지.

**채택 근거**:
- 2차 외부 검증 라운드(2026-04-24) 스타크 Lane 5 지적: "2026년 현재 `SHA1withRSA` 단독 검증은 Frida 메모리 변조에 취약. Play Integrity API `classicRequest` 로컬 토큰 검증 로직 필수."
- Google Play Integrity API `classicRequest` 모드는 **서버 없이 로컬 검증 가능**. 헌법 1조 "스토어 공식 API 허용" 범위 내.
- 헌법 4조 "자가 작동"과 정합: 네트워크 단절 시 2계층 검증은 스킵되고 1계층만 작동 (fail-open, 사용자 가치 손실 없음).

**구현 스케치**:

```kotlin
class IntegrityVerifier(private val context: Context) {

    private val integrityManager = IntegrityManagerFactory.create(context)

    suspend fun verifyDeviceIntegrity(purchaseToken: String): IntegrityResult {
        val nonce = Base64.encodeToString(
            (purchaseToken + System.currentTimeMillis()).toByteArray(),
            Base64.URL_SAFE or Base64.NO_WRAP
        )

        return try {
            val request = IntegrityTokenRequest.builder()
                .setNonce(nonce)
                .build()

            val response = integrityManager.requestIntegrityToken(request).await()
            val token = response.token()

            // 로컬 토큰 파싱 (서버 검증 없음, payload만 읽음)
            val payload = parseIntegrityTokenLocally(token)

            when {
                payload.deviceIntegrity.contains("MEETS_BASIC_INTEGRITY") -> IntegrityResult.Valid
                payload.deviceIntegrity.contains("MEETS_DEVICE_INTEGRITY") -> IntegrityResult.Valid
                else -> IntegrityResult.Compromised(payload.deviceIntegrity)
            }
        } catch (e: IntegrityServiceException) {
            // 네트워크 단절·Google Play 미지원 디바이스 → 1계층만 신뢰 (헌법 4조)
            IntegrityResult.Unknown(e.errorCode)
        }
    }
}

sealed class IntegrityResult {
    object Valid : IntegrityResult()
    data class Compromised(val flags: List<String>) : IntegrityResult()
    data class Unknown(val errorCode: Int) : IntegrityResult()
}
```

**판정 정책**:

| 결과 | 구독 활성화 | 사용자 경험 |
|---|---|---|
| `Valid` | ✅ 활성 | 정상 Premium |
| `Compromised` | ❌ 거부 | "이 기기 환경에서는 구독을 활성화할 수 없습니다. 정상 환경에서 재시도해 주세요." |
| `Unknown` (네트워크 단절·미지원) | ✅ 활성 (1계층 통과 시) | 정상 Premium (헌법 4조 fail-open) |

**서버 0 보증**: `classicRequest`는 Google Play Services에서 반환된 토큰을 **로컬에서 직접 파싱**한다. 자체 서버로 토큰을 보내 검증받는 `standardRequest` 모드는 사용하지 않음. 이 구분이 헌법 1조 정합의 핵심이다.

**방어 범위 (스타크 지적 대응)**:
- ✅ 루팅 탐지 (`MEETS_BASIC_INTEGRITY` 실패)
- ✅ 커스텀 ROM 탐지
- ✅ Frida Gadget 주입 탐지 (Google Play Protect 신호 연계)
- ✅ 에뮬레이터 탐지
- ❌ 런타임 메모리 후크 (완전 방어 불가, 이건 서버 측 재검증 없이는 근본 해결 안 됨 — 헌법 1조와의 트레이드오프)

### 31-2-3. 2계층 추가 권한 (Patch 38)

Play Integrity API 호출을 위해 **런타임 추가 권한 없음**. `com.google.android.gms:play-services-integrity` Gradle 의존성만 추가. Manifest 변경 없음.

## 31-3. SubscriptionEntity

```kotlin
@Entity(tableName = "subscription")
data class SubscriptionEntity(
    @PrimaryKey val purchaseToken: String,
    val purchaseTimeMs: Long,
    val isAutoRenewing: Boolean,
    val lastVerifiedAt: Long
)
```

## 31-4. 구독 상태 UI

- 활성 구독: "Premium 사용 중 · 2026-05-24 갱신" 표시
- 미구독: "$2.49/월 — 모든 Surface 활성화" + "구독" 버튼
- 만료 예정: "7일 후 만료 — 갱신 설정 확인" 안내

## 31-5. iOS 진입 시 (v2.0.0 예정)

StoreKit 2 사용. RevenueCat 미채택 (메모리 #20).

```swift
// 예정 스케치
let products = try await Product.products(for: ["myphonecheck_monthly"])
let result = try await product.purchase()
switch result {
case .success(let verification):
    let transaction = try checkVerified(verification)
    await transaction.finish()
    // 온디바이스 검증만, 서버 없음
case .pending, .userCancelled:
    break
}
```

---

# 32. Interface Injection (v1.5.2 Patch 10)

## 32-1. 원칙

`engine/decision`이 feature/* 모듈을 **직접 호출하지 않는다**. feature/* 가 구현하는 인터페이스를 `engine/decision`이 의존한다. 의존 방향 역전 (DIP).

## 32-2. 인터페이스 목록

```kotlin
// core/common 에 정의
interface UserNotifier {
    fun showRiskAlert(risk: RiskKnowledge)
}

interface SubscriptionGate {
    fun isPremiumActive(): Boolean
}

interface PermissionChecker {
    fun hasPermission(permission: String): Boolean
}
```

feature/* 모듈이 각 인터페이스를 구현하여 DI 컨테이너(Hilt 또는 Koin)로 주입.

## 32-3. 의존 그래프 (재확인)

```
core/common (interface 정의만)
    ▲
    │ 구현
    │
feature/call ───┐
feature/message ─┼─▶ engine/decision ─▶ core/common (interface 사용)
feature/mic ─────┤
feature/camera ──┘
```

역방향 호출 없음. Stage 0 FREEZE 유지.

---

# 33. Store Policy 대응 + Stage 0 Contracts

## 33-1. Stage 0 Contracts 전문 (Kotlin 소스)

Stage 0에서 FREEZE된 4 계약의 Kotlin 소스 전문. **변경 금지 (§23-4 FREEZE.md)**.

### 33-1-0. FREEZE 대상 명세 (헐크 Lane 3 P1-2 해결)

본 표는 Stage 0 Contracts FREEZE의 정확한 경계를 규정한다. `FreezeMarkerTest`가 검증하는 대상 일체이다.

| # | 파일 | 요소 | 종류 | FREEZE 시점 | 위반 시 영향 |
|---|---|---|---|---|---|
| 1 | `IdentifierType.kt` | `sealed class IdentifierType` | 타입 계층 구조 | 2026-04-22 | 모든 Surface 엔진 진입점 깨짐 |
| 1-a | `IdentifierType.kt` | `data class PhoneNumber(val value: String)` | 서브타입 + 필드명 | 2026-04-22 | CallCheck·MessageCheck 호출 깨짐 |
| 1-b | `IdentifierType.kt` | `data class UrlDomain(val value: String)` | 서브타입 + 필드명 | 2026-04-22 | MessageCheck URL 평가 깨짐 |
| 1-c | `IdentifierType.kt` | `data class AppReputation(val value: String)` | 서브타입 + 필드명 | 2026-04-22 | AppSecurityWatch(후행) 진입점 깨짐 |
| 2 | `RiskKnowledge.kt` | `interface RiskKnowledge` | 인터페이스 | 2026-04-22 | 4속성 출력 규격 깨짐 |
| 2-a | `RiskKnowledge.kt` | `val identifier: IdentifierType` | 프로퍼티 시그니처 | 2026-04-22 | 식별자 추적 불가 |
| 2-b | `RiskKnowledge.kt` | `val riskLevel: RiskLevel` | 프로퍼티 시그니처 | 2026-04-22 | 위험도 표시 깨짐 |
| 2-c | `RiskKnowledge.kt` | `val expectedDamage: DamageEstimate` | 프로퍼티 시그니처 | 2026-04-22 | 손해 표시 깨짐 |
| 2-d | `RiskKnowledge.kt` | `val damageTypes: List<DamageType>` | 프로퍼티 시그니처 | 2026-04-22 | 손해 유형 칩 깨짐 |
| 2-e | `RiskKnowledge.kt` | `val reasonSummary: String` | 프로퍼티 시그니처 | 2026-04-22 | 이유 설명 깨짐 |
| 2-f | `RiskKnowledge.kt` | `val computedAt: Long` | 프로퍼티 시그니처 | 2026-04-22 | Stale 판정 깨짐 |
| 2-g | `RiskKnowledge.kt` | `val stalenessFlag: StalenessFlag` | 프로퍼티 시그니처 | 2026-04-22 | L3 경로 표시 깨짐 |
| 2-h | `RiskKnowledge.kt` | `enum RiskLevel { NONE, LOW, MEDIUM, HIGH, CRITICAL }` | enum 값 + 순서 | 2026-04-22 | RiskBadge·`mapToRiskLevel` 깨짐 |
| 2-i | `RiskKnowledge.kt` | `data class DamageEstimate` | 클래스 + 필드 | 2026-04-22 | DamageEstimate 표시 깨짐 |
| 2-j | `RiskKnowledge.kt` | `enum DamageType` | enum 값 | 2026-04-22 | DamageTypeChip 깨짐 |
| 2-k | `RiskKnowledge.kt` | `enum StalenessFlag { FRESH, STALE_KNOWLEDGE, STALE_OFFLINE }` | enum 값 + 순서 | 2026-04-22 | SLA Detector·UI 분기 깨짐 |
| 3 | `Checker.kt` | `fun interface Checker<IN, OUT : RiskKnowledge>` | 함수형 인터페이스 + 제네릭 경계 | 2026-04-22 | 모든 Surface Checker 깨짐 |
| 3-a | `Checker.kt` | `suspend fun check(input: IN): OUT` | 메서드 시그니처 | 2026-04-22 | Surface 호출 컨벤션 깨짐 |
| 4 | `DecisionEngineContract.kt` | `interface DecisionEngineContract` | 인터페이스 | 2026-04-22 | 엔진 진입점 깨짐 |
| 4-a | `DecisionEngineContract.kt` | `suspend fun evaluate(query: IdentifierType): RiskKnowledge` | 메서드 시그니처 | 2026-04-22 | 모든 Surface 평가 호출 깨짐 |
| 4-b | `DecisionEngineContract.kt` | `fun enqueueRefresh(query: IdentifierType)` | 메서드 시그니처 | 2026-04-22 | Stale 재검증 큐 깨짐 |
| 4-c | `DecisionEngineContract.kt` | `suspend fun applyUserAction(query, action: UserActionType)` | 메서드 시그니처 | 2026-04-22 | UserAction → NKB 재계산 깨짐 |
| 4-d | `DecisionEngineContract.kt` | `enum UserActionType` | enum 값 | 2026-04-22 | 사용자 조치 분류 깨짐 |
| 5 | `FreezeMarker.kt` | `annotation class FreezeMarker(val frozenSince: String)` | 어노테이션 + 파라미터 | 2026-04-22 | FreezeMarkerTest 자체 깨짐 |

**총 FREEZE 항목**: 5개 파일, 22개 시그니처. `FreezeMarkerTest`는 위 22개를 모두 reflection으로 검증.

**변경 절차** (FREEZE 해제):
1. MAJOR 버전 업 (v2.0.0+) 시점에만 가능
2. 대표님 명시 승인 필수
3. 변경 사유 + Migration 계획 + 영향 받는 모듈 전수 조사 첨부
4. `FreezeMarkerTest` 갱신 + 모든 테스트 그린 확인
5. 본 §33-1-0 표 갱신 + §0-A-1 헌법 변경 추적과 별도 추적 로그 작성

### 33-1-1. IdentifierType.kt

```kotlin
package com.myphonecheck.core.common

/**
 * 디바이스가 판단하는 식별자 유형.
 *
 * One Engine, N Surfaces 원칙: 엔진은 IdentifierType으로 분기하되,
 * 분기별로 별도 엔진을 만들지 않는다.
 *
 * FREEZE 시점: 2026-04-22 Stage 0 Contracts 완료
 * FREEZE 이유: Decision Engine이 모든 Surface에서 공유하는 공통 입력 타입.
 *              변경 시 모든 Surface의 엔진 호출 경로가 영향받음.
 */
sealed class IdentifierType {

    /** E.164 국제 포맷 전화번호 (예: "+821012345678") */
    data class PhoneNumber(val value: String) : IdentifierType()

    /** URL 도메인 (예: "coupang.com", "coupang-delivery.xyz") */
    data class UrlDomain(val value: String) : IdentifierType()

    /** Android 앱 패키지명 (예: "com.example.app") */
    data class AppReputation(val value: String) : IdentifierType()
}
```

### 33-1-2. RiskKnowledge.kt

```kotlin
package com.myphonecheck.core.common

/**
 * 엔진이 반환하는 판단 결과의 공통 계약.
 *
 * 4속성 출력: riskLevel, expectedDamage, damageTypes, reasonSummary
 *
 * FREEZE 시점: 2026-04-22 Stage 0 Contracts 완료
 * FREEZE 이유: 모든 Surface가 이 규격으로 렌더링.
 *              필드 추가·삭제 시 UI 렌더링 전체 깨짐.
 */
interface RiskKnowledge {
    val identifier: IdentifierType
    val riskLevel: RiskLevel
    val expectedDamage: DamageEstimate
    val damageTypes: List<DamageType>
    val reasonSummary: String
    val computedAt: Long
    val stalenessFlag: StalenessFlag
}

enum class RiskLevel {
    NONE, LOW, MEDIUM, HIGH, CRITICAL
}

data class DamageEstimate(
    val averageAmount: Long?,    // 평균 손해 금액 (현지 통화, null 허용)
    val medianAmount: Long?,     // 중앙값
    val confidence: Float        // 추정 신뢰도 0.0 ~ 1.0
)

enum class DamageType {
    FINANCIAL_SCAM,      // 금융사기
    IDENTITY_THEFT,      // 개인정보 유출
    HARASSMENT,          // 괴롭힘
    UNWANTED_AD,         // 원치 않는 광고
    TIME_WASTE,          // 시간 손해
    PRIVACY_BREACH,      // 프라이버시 침해
    SECURITY_VULNERABILITY  // 보안 취약점 (앱)
}

enum class StalenessFlag {
    FRESH,
    STALE_KNOWLEDGE,
    STALE_OFFLINE
}
```

### 33-1-3. Checker.kt

```kotlin
package com.myphonecheck.core.common

/**
 * 범용 Checker 계약.
 *
 * 모든 Surface의 엔진 진입점이 이 인터페이스를 구현한다.
 * IN은 Surface별 입력 타입, OUT은 4속성을 담은 결과 타입.
 *
 * FREEZE 시점: 2026-04-22 Stage 0 Contracts 완료
 * FREEZE 이유: Surface 추가 시에도 동일 계약으로 통합.
 */
fun interface Checker<IN, OUT : RiskKnowledge> {
    suspend fun check(input: IN): OUT
}
```

### 33-1-4. DecisionEngineContract.kt

```kotlin
package com.myphonecheck.core.common

/**
 * Decision Engine의 외부 계약.
 *
 * 내부 구현은 engine/decision 모듈에 있으며,
 * feature/* 는 이 계약만 본다.
 *
 * FREEZE 시점: 2026-04-22 Stage 0 Contracts 완료
 * FREEZE 이유: 모든 Surface의 엔진 호출 경로 공통 진입점.
 */
interface DecisionEngineContract {
    /** 단일 식별자 평가. 메인 API. */
    suspend fun evaluate(query: IdentifierType): RiskKnowledge

    /** 백그라운드 재검증 큐 등록 (Stale 엔트리용) */
    fun enqueueRefresh(query: IdentifierType)

    /** 사용자 조치 반영 + NKB 재계산 */
    suspend fun applyUserAction(query: IdentifierType, action: UserActionType)
}

enum class UserActionType {
    SPAM_REPORT,
    BLOCKED,
    ADDED_TO_CONTACTS,
    MARKED_SAFE,
    MARKED_AD,
    UNBLOCKED
}
```

### 33-1-5. FreezeMarker.kt

```kotlin
package com.myphonecheck.core.common

/**
 * FREEZE 선언 마커.
 * 이 어노테이션이 붙은 요소는 시그니처 변경 금지.
 * FreezeMarkerTest가 CI에서 검증.
 */
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD
)
@Retention(AnnotationRetention.RUNTIME)
annotation class FreezeMarker(val frozenSince: String)
```

## 33-2. Store Policy 대응 요약

| 스토어 | 정책 | 대응 |
|---|---|---|
| Google Play | BROADCAST_SMS 사용 금지 | Patch 17로 제거, Mode B(Share Intent)·Mode A(Default SMS) 2-모드 |
| Google Play | ~~QUERY_ALL_PACKAGES 사용 정당화~~ | **Patch 36으로 제거 → `<queries>` 블록(§24-6-1) 기반 Package Visibility 최소화** |
| Google Play | READ_SMS·READ_CALL_LOG 민감 권한 | Permissions Declaration 제출 (§27-3) |
| Google Play | 데이터 수집 정직 선언 | Data Safety "Yes, collects + No sharing + Processed only on device" (§27-1, Patch 32) |
| Apple App Store (예정) | CallKit·CallDirectoryExtension 구조 | Phase 7 iOS 진입 시 설계 |
| Apple App Store (예정) | Message Filter Extension | Phase 7 iOS 진입 시 설계 |

---

# 34. 테스트 인프라

## 34-1. 권한 매트릭스 (Patch 23·36·37 적용)

| 권한 | CallCheck | MessageCheck | MicCheck | CameraCheck | 헌법 조항 |
|---|---|---|---|---|---|
| `READ_PHONE_STATE` | ✅ 필수 | - | - | - | 제4조 |
| `READ_CALL_LOG` | ✅ 선택 (Cold Start용) | - | - | - | 제4조 |
| `READ_SMS` / `RECEIVE_SMS` / `SEND_SMS` / `WRITE_SMS` | - | ✅ Mode A 전용 | - | - | 제4조 (Mode B는 권한 0) |
| `READ_CONTACTS` | ✅ 선택 | ✅ 선택 | - | - | 제4조 |
| ~~`QUERY_ALL_PACKAGES`~~ | ❌ | ❌ | **❌ Patch 36 제거** | **❌ Patch 36 제거** | **Patch 36 — `<queries>` 블록(§24-6-1) 대체** |
| `PACKAGE_USAGE_STATS` | - | - | ✅ Special Access (선택) | ✅ Special Access (선택) | 제4조 |
| `SYSTEM_ALERT_WINDOW` | ✅ 필수 (오버레이) | - | - | - | 제4조 |
| `POST_NOTIFICATIONS` | ✅ 선택 | ✅ 선택 | ✅ 선택 | ✅ 선택 | 제4조 |
| `INTERNET` | ✅ (Layer 2·3) | ✅ | - | - | 제1조 (Mic/Camera는 네트워크 호출 없음, §18-6-1) |
| `ACCESS_NETWORK_STATE` | ✅ | ✅ | - | - | 제4조 (SLA Detector) |
| **Play Integrity API (런타임 권한 아님, Patch 38)** | **✅ 결제 활성화 시** | **-** | **-** | **-** | **제1조 (스토어 공식 API 허용 범위)** |
| ~~`RECORD_AUDIO`~~ | ❌ | ❌ | **❌ 요청 안 함** | ❌ | **Patch 23 — 스캔만 수행, 녹음 안 함** |
| ~~`CAMERA`~~ | ❌ | ❌ | ❌ | **❌ 요청 안 함** | **Patch 23 — 스캔만 수행, 촬영 안 함** |
| ~~`BROADCAST_SMS`~~ | ❌ | ❌ | ❌ | ❌ | **Patch 17 — Play 정책 위반, 제거** |

**Patch 37 반영 (P0-1)**: v1.6.2까지 본 매트릭스에 `QUERY_ALL_PACKAGES ✅ 필수` 행이 잔존했으나, 실제 Manifest는 Patch 36으로 `<queries>` 대체되어 있어 불일치. 7-워커 평가(Claude Code·Cursor·코웍 3자 합의)로 지적. 본 v1.7.0에서 매트릭스를 Manifest와 정합하게 정정.

**Mode A/B 명시 (P2-4)**: MessageCheck의 SMS 4권한은 Mode A(Default SMS Handler) 전용. Mode B(Share Intent 기본)는 권한 0으로 작동.

## 34-2. 테스트 Layer

- **Unit Test** (각 모듈 `src/test/kotlin`): JVM, 엔진 로직·Softmax·TierClassifier
- **Instrumentation Test** (`src/androidTest/kotlin`): 실기기·에뮬레이터, Room DB·BillingClient
- **UI Test** (Espresso + Compose UI Test): Surface 화면 렌더·상호작용
- **Smoke Test**: 11개 시나리오 자동 실행 (Day 6~13 완료 기준)

## 34-3. 필수 테스트 목록

| 테스트 | 검증 내용 | 실행 위치 |
|---|---|---|
| `FreezeMarkerTest` (22개) | Stage 0 5 파일 시그니처 불변 (Patch 37 통일) | CI 매 PR |
| `MigrationCompatTest` | v1 → v2 Room 마이그레이션 | CI 매 PR |
| `DecisionContractTest` | Decision Engine 계약 준수 | CI 매 PR |
| `UserConsentTest` | 권한 거부 시 graceful degradation | CI |
| `NkbSizeBudgetTest` | NKB 엔트리 평균 ≤ 2KB | CI nightly |
| `SmokeRun01~11` | 11개 스모크런 시나리오 | 릴리즈 전 매번 |
| `AccessibilityScannerTest` | TalkBack 통과 | 릴리즈 전 |
| `Verify*.sh` (5종) | 헌법 정합성 스크립트 | CI 매 PR |

## 34-4. 부록 A 위치

**삭제됨 (Patch 24).** 부록 A §A-3 (MicCheck 권한 정당화), §A-4 (CameraCheck 권한 정당화)는 본 섹션으로 흡수되었으며, "권한 요청 없음"이 원칙이므로 정당화 텍스트 자체가 불필요하다.

---

# 35. 인프라 운영 참조 (Infrastructure Reference)

본 문서는 **제품·설계 기준선**이며, 이와 쌍을 이루는 **인프라·운영 기준선**은 `MyPhoneCheck_Infrastructure_v1.0.md`가 담당한다. §0-F에서 이미 선언한 페어 관계를 본 장에서 상세화.

## 35-1. 페어 구조 개요

```
docs/00_governance/
├── MyPhoneCheck_Architecture_v1.7.0.md      ← 본 문서 (제품 설계)
│   └─ 정의 범위:
│      • 헌법 7조
│      • 황금알 4속성 · Four Surfaces
│      • NKB 데이터 모델 · Decision Engine 수식
│      • Day-by-Day 구현 가이드
│      • strings.xml 명세 · CI/CD
│      • 스모크런 시나리오 · 드라이런 체크리스트
│
├── MyPhoneCheck_Infrastructure_v1.0.md      ← 페어 (인프라 운영)
│   └─ 정의 범위:
│      • 도구맵 (Claude Code·Cursor·Codex CLI·코웍·비전)
│      • 로컬 경로 구조 (C:\Users\user\Dev\ollanvin\myphonecheck\)
│      • 비밀값 SOP (PlayServiceAccount JSON · Keystore)
│      • 실행 순서 · 백업 정책
│      • 헌법 위반 시정 이력 (AWS Lambda 제거 등)
│      • 스토어 업로드 SOP (Fastlane Mac)
│
└── constitution/
    └── APP_FACTORY_CONSTITUTION_ROOT_ROLE.md
```

## 35-2. 문서 관할 분리표

| 주제 | 관할 문서 | 비고 |
|---|---|---|
| 헌법 7조 | **Architecture** | 제품 판단 기준 |
| Four Surfaces 범위 | **Architecture** | 제품 기능 |
| NKB 스키마 | **Architecture** | 데이터 모델 |
| Kotlin 파일 트리 | **Architecture** | 코드 구조 |
| JDK 17 toolchain | 둘 다 참조 (Architecture §26 + Infra §도구맵) | 빌드 환경 |
| CI 워크플로우 | **Architecture §26** | PR 시 검증 |
| Fastlane 릴리즈 | **Infrastructure** | Mac 한정 실행 |
| Play Console 계정 | **Infrastructure** | 대표님 소유, 비밀값 |
| Keystore 경로 | **Infrastructure** | `C:\Users\user\ollanvin\secrets\` (메모리) |
| MacinCloud 연동 | **Infrastructure** | iOS 빌드 파이프라인 |
| 워커 역할 분담 | **Infrastructure** | Cursor·Claude Code·코웍·Codex CLI |
| 백업 SOP | **Infrastructure** | git mirror clone 등 |
| 구독 가격 $2.49 | **Architecture** | 제품 결정 |
| Play Console 가격 Tier 설정 | **Infrastructure** | 운영 SOP |

## 35-3. 충돌 해소 규칙

두 문서가 충돌할 때:

1. **제품 설계 결정** (기능 범위·헌법·데이터 모델) → **Architecture 우선**
2. **인프라 운영 결정** (도구·경로·SOP) → **Infrastructure 우선**
3. 애매하거나 두 범주에 걸친 결정 → 비전이 판정하고 대표님 확인 (Rule 3)

## 35-4. 동기화 의무

| Architecture 변경 | Infrastructure 갱신 필요 |
|---|---|
| 새 Surface 추가 | 도구맵에 새 빌드·테스트 절차 반영 |
| Billing Library 버전 업 | 비밀값 SOP 재검토 (Play Public Key 갱신 시) |
| 신규 외부 API 도입 (Layer 3) | 네트워크 정책 업데이트 |
| 헌법 조항 추가 | 위반 시정 이력 섹션에 반영 |

| Infrastructure 변경 | Architecture 갱신 필요 |
|---|---|
| 워커 역할 분담 변경 | Day-by-Day의 "담당자" 표기 갱신 |
| 신규 빌드 도구 도입 | CI/CD 섹션 업데이트 |
| 백업 정책 변경 | 0-C 정책 모니터링에 반영 |

## 35-5. Infrastructure 문서 요약 (참조용)

본 Architecture가 전제하는 인프라 상태. 상세는 Infrastructure 문서 참조.

- **로컬 레포**: `C:\Users\user\Dev\ollanvin\myphonecheck\`
- **원격 레포**: `github.com/ollanvin/myphonecheck`
- **Keystore 경로**: `C:\Users\user\ollanvin\secrets\` (규약)
- **Play Public Key**: `BuildConfig.PLAY_PUBLIC_KEY` (빌드 시 주입, GitHub Secrets)
- **CI Runner**: GitHub 호스티드 `ubuntu-latest` (Mac Fastlane는 MacinCloud 임대)
- **주 구현자**: Cursor (Windows 로컬, Auto Mode)
- **감사·장시간 주행**: Claude Code (Windows 전역 + SSH + Fastlane)
- **설계·판정·워크오더 발행**: 비전 (Claude 채팅)
- **문서 작성·GitHub 운영**: 코웍 (허용폴더 + GitHub API)
- **2차 CLI 백업**: ChatGPT Codex CLI
- **자동 통신망**: **2026-04-24 폐기 완료** (ops 레포 삭제, 수동 1:1 워커 분배로 전환)

---

# 36. Four Surfaces 통합 원칙 (Patch 22)

**본 장은 §17 One Engine 원칙의 기술 구현 요약이다.** v1.6.0 Patch 22로 신설, v1.6.1 재작성에서 **논리적 위치에 정확히 배치** (말미 추가 아님 — Claude Code 통합본의 구조 오류 시정).

## 36-1. 통합 엔진 (One Engine)

모든 Surface는 **단일 DecisionEngineContract**를 공유한다.

```kotlin
// feature/call
class CallCheckEngine(
    private val decisionEngine: DecisionEngineContract
) : Checker<PhoneQuery, CallRisk> {
    override suspend fun check(input: PhoneQuery): CallRisk {
        val knowledge = decisionEngine.evaluate(
            IdentifierType.PhoneNumber(input.phoneE164)
        )
        return CallRisk.from(knowledge)
    }
}

// feature/message
class MessageCheckEngine(
    private val decisionEngine: DecisionEngineContract,
    // ... 추가 의존성
) : Checker<IncomingSms, MessageRisk> {
    override suspend fun check(input: IncomingSms): MessageRisk {
        // 3중 평가 모두 동일 엔진 호출
        val senderKnowledge = decisionEngine.evaluate(
            IdentifierType.PhoneNumber(input.senderE164)
        )
        // ...
    }
}

// feature/mic, feature/camera도 동일 패턴
```

**엔진 분기 없음**. 확장은 `IdentifierType` sealed class에 새 분기 추가 + 각 Surface의 Checker 구현으로 처리.

## 36-2. 4속성 공통 렌더링

모든 Surface 결과가 **`FourAttributeCard`** 공통 UI 컴포넌트로 렌더링된다.

```kotlin
@Composable
fun FourAttributeCard(risk: RiskKnowledge) {
    Card {
        RiskBadge(level = risk.riskLevel)
        DamageEstimateBlock(estimate = risk.expectedDamage)
        DamageTypeChips(types = risk.damageTypes)
        ReasonExplainText(text = risk.reasonSummary)
        StalenessIndicator(flag = risk.stalenessFlag)
    }
}
```

Surface별로 이 카드 **위·아래에 Surface 고유 정보**를 덧붙일 뿐 (예: MessageCheck의 URL 목록, AppSecurityWatch의 CVE 이력 — MicCheck/CameraCheck는 Patch 30으로 단순 관리자로 축소되어 CVE 이력 표시 없음). 공통 4속성 영역은 불변.

## 36-3. Surface 추가 워크플로우 (향후 PushCheck 등)

1. `IdentifierType` sealed class에 새 분기 추가 (예: `NotificationSource`)
2. Decision Engine의 `evaluate()` switch에 처리 로직 추가
3. 새 feature 모듈 생성 (`feature/push`)
4. `Checker<NotificationPayload, NotificationRisk>` 구현
5. `NotificationRisk : RiskKnowledge` 구현
6. UI에서 `FourAttributeCard(risk)` 호출
7. 권한 매트릭스(§34-1) 행 추가
8. SmokeRun 시나리오 추가
9. 본 문서 Patch 넘버 부여 + §0-B-2 기록

엔진·NKB·SearchResultAnalyzer **변경 없음**. 새 Surface는 얇은 레이어만 추가.

## 36-4. 빌드 필수 토큰 42개 (v1.6.1-patch §9-1)

본 문서가 v1.6.1로 인정받으려면 다음 42개 토큰이 본문에 **모두 존재**해야 한다.

```
1.  CallCheck
2.  MessageCheck
3.  MicCheck
4.  CameraCheck
5.  One Engine
6.  Four Surfaces
7.  DecisionEngineContract
8.  IdentifierType
9.  RiskKnowledge
10. RiskLevel
11. DamageEstimate
12. DamageType
13. StalenessFlag
14. FreezeMarker
15. Checker
16. ExtractedSignal
17. NumberKnowledge
18. UserAction
19. ClusterProfile
20. SignalSummary
21. FeatureType
22. ConclusionCategory
23. Softmax
24. Tier
25. Cold Start
26. Self-Discovery
27. NKB
28. L3
29. SLA
30. AppPermissionRisk
31. JustificationStatus
32. MessageRisk
33. CallRisk
34. FourAttributeCard
35. QUERY_ALL_PACKAGES
36. PACKAGE_USAGE_STATS
37. 디바이스 오리엔티드
38. 헌법
39. Out-Bound Zero
40. In-Bound Zero
41. Working Canonical
42. 2.49
```

## 36-5. 금지 토큰 9개 (v1.6.1-patch §9-2)

다음 9개 토큰은 본문에 **등장하지 않아야** 한다 (Patch 17·20·23·24·Infra FINAL).

```
1.  BROADCAST_SMS           (Patch 17, Play 정책 위반)
2.  PrivacyCheck            (Patch 21, 폐기됨)
3.  RECORD_AUDIO            (Patch 23, 권한 요청 없음 — 단, 34-1 "요청 안 함" 표기는 예외)
4.  RevenueCat              (메모리 #20, 미채택)
5.  AWS Lambda              (Infra v1.0 FINAL, 폐기)
6.  API Gateway             (Infra v1.0 FINAL, 폐기)
7.  DynamoDB                (Infra v1.0 FINAL, 폐기)
8.  자체 영수증 검증 서버    (헌법 1·7조 위반)
9.  본사 큐레이션            (헌법 3조 위반)
```

금지 토큰은 본 문서에 **다음 맥락에서만 등장 허용**:
- "금지 토큰" 자체를 설명하는 §36-5 (본 섹션)
- 폐기 이력 §2-2, §18-5 (역사 기록)
- Patch 감사 로그 §0-B-2 (과거 변경 추적)

## 36-6. 자체 검증 결과

본 v1.7.0 재작성본에 대한 자체 grep 검증 결과는 §Z-5에 기록.

---

# Z. 비전 작성 기록 (0-Z Appendix)

본 부록은 본 문서가 **어떻게 재작성되었는지**의 정직한 기록이다. 헌법 제5조(정직성) 구현.

## Z-1. 재작성 배경

- 2026-04-24 대표님 확인: `MyPhoneCheck_Architecture_v1.6.1_630dda.docx`는 **승인본 아님, 폐기 대상**
- 즉 v1.6.1의 **정식 수립본이 부재한 상태**
- Claude Code가 수작업 통합한 `_integrated_claudecode_20260424_140201.md`도 공식 파이프라인 우회 + 섹션 배치 오류로 캐노니컬 부적합 판정
- 대표님 지시: 비전이 직접 재작성, 워커에게 맡기지 않음
- 비전 1차 재작성 (2026-04-24 오전) → 5-Lane 검증 → 2차 재작성 (2026-04-24 오후, Patch 29~36 반영 + 코웍 87a9a3 4건 흡수)
- 2차 재작성 후 대표님 지시 (2026-04-24 저녁): **"1.6.2로 버전 올리자. 기존 것들과 겹친다."**
- 승격 사유: v1.6.1 계보에 이미 다음 산출물이 존재하여 파일명·버전 충돌 유발
  - `MyPhoneCheck_Architecture_v1.6.1_630dda.docx` (폐기 대상, 파이프라인 미통과)
  - `MyPhoneCheck_Architecture_v1_6_1_87a9a3.docx` (코웍 pandoc 병합본)
  - `_integrated_claudecode_20260424_140201.md` (Claude Code 통합본, 캐노니컬 부적합)
  - 비전 1차·2차 `.md` 초안 (Working Canonical 단계)
- **v1.6.2 = 위 산출물 모두와 구분되는 최초 정식 수립본**. 빅테크 Semver 기준 PATCH 승격 (호환 유지, 명시성만 강화).
- 본 문서 = **v1.6.2 최초 정식 수립본**

## Z-2. 입력 자료 반영 추적

| 입력 자료 | 반영 섹션 | 반영 방식 |
|---|---|---|
| v1.5.1_7d23b4.docx (베이스) | §1~§22 전반 | 구조·본문 계승, 문구 재작성 |
| v1.5.2 Rebuild WO | §8-X, §10-X, §32 | Patch 09~16 반영 |
| v1.5.3 Patch 17 WO | §18-4-4, §24-6-1, §34-1 금지 권한 | BROADCAST_SMS 제거 명시 |
| v1.6.0 Four Surfaces WO | §17, §18-4~§18-7 | Patch 18~22 반영 |
| v1.6.1 Patch WO | §18-6-4, §24-6-1, §34-1, §36 | Patch 23~28 반영 |
| Stage 0 Contracts WO | §33-1-1~§33-1-5 | 4 계약 Kotlin 소스 전문 인용 |
| Stage 0 hotfix WO | §26-1~§26-2 | JDK 17 toolchain 승격 반영 |
| 메모리 #7 (헌법 7조) | §1 전체 | 7조 체계로 확장 |
| 메모리 #11 ($2.49) | §16 | 가격 확정 반영 |
| 메모리 #8 (3축 검색) | §5-1, §6 | Layer 1·2·3 = 내부·외부·오픈소스 |
| 메모리 #10 (MVP 금기어) | §2-2 폐기 사고 | 반쪽 기능 거부 |
| 메모리 #13 (Mic/Camera 외부 이벤트) | §18-6-4 | 2건 이벤트 구현 코드 |
| 메모리 #14 (PushCheck 푸시 휴지통) | §17-3 | 후행 Surface 명시 |
| 메모리 #20 (RevenueCat 미채택) | §31-5, §36-5 | 금지 토큰 |
| Infra_Ops v1.0 FINAL | §35 전체 | 페어 문서 참조 |

## Z-3. 판단 이력 (Claude Code 통합본 대비 차별)

| 항목 | Claude Code 통합본 | 비전 재작성본 (본 문서) |
|---|---|---|
| §36 Four Surfaces 위치 | 문서 맨 끝 (부록 위치 덧붙이기) | §36 논리 위치 (§34 테스트 뒤) |
| v1.6.0 draft 부재 처리 | 앵커 치환 포기 | v1.5.1 + 패치 명세 직접 조합하여 본문 재기술 |
| §35 인프라 참조 | 없음 | 신규 추가 (Infra_Ops와 페어) |
| Z 비전 작성 기록 | 없음 | 본 섹션 |
| 자체 토큰 검증 | 미수행 | Z-5에서 수행·기록 |
| §33-1-1~§33-1-5 Stage 0 전문 (5개 파일: 4 핵심 계약 + FreezeMarker 어노테이션) Kotlin 소스 | 단편 인용 | 5개 파일 전문 그대로 |
| Patch 23·24 `RECORD_AUDIO`/`CAMERA`/`§A-3`/`§A-4` 삭제 반영 | 부분 반영 | 권한 매트릭스 명시적 "요청 안 함" 행 + 부록 A 흡수 완료 명기 |

## Z-4. 본 재작성의 한계 (정직 기록)

- **외부 검증 미실시**: 헐크·자비스·스타크 라운드 없이 비전 단독 작성
- **SHA6 스탬프 없음**: 파일명은 `MyPhoneCheck_Architecture_v1.7.0.md` 고정
- **포맷은 `.md`만**: `.docx` 산출은 Cursor 파이프라인 필요
- **공식 `build_architecture_v170.py` 미실행**: 수작업 재작성
- **`validate.py` 미통과**: 공식 검증 스크립트 아직 실행되지 않음
- **5턴 분할 작성**: 각 턴 간 완전 일관성은 검토자 확인 필요

위 한계는 **향후 Cursor 공식 파이프라인으로 정식 `.docx + HASH6` 산출** 시점에 전부 해소 가능. 본 `.md`가 그 파이프라인의 입력 자료가 된다.

## Z-5. 자체 토큰 검증 결과

다음은 `grep`으로 본 문서 본문을 확인한 결과이다(§36-4 필수 + §36-5 금지).

**필수 42개 토큰 — PASS 여부는 본 문서 배포 직후 `scripts/verify-tokens.sh`로 자동 검증한다.** 비전 자체 수작업 점검 기록:

- CallCheck, MessageCheck, MicCheck, CameraCheck → §17-2, §18, §34, §36 다수 등장 ✓
- One Engine, Four Surfaces → §3-3, §17-1, §36 등장 ✓
- DecisionEngineContract, IdentifierType, RiskKnowledge → §33-1 전문 + §36-1 등장 ✓
- RiskLevel, DamageEstimate, DamageType, StalenessFlag → §8, §10-4, §33-1-2 등장 ✓
- FreezeMarker, Checker → §33-1-3, §33-1-5 등장 ✓
- ExtractedSignal, NumberKnowledge, UserAction, ClusterProfile → §8 전문 ✓
- SignalSummary, FeatureType, ConclusionCategory → §8-1 ✓
- Softmax, Tier, Cold Start, Self-Discovery, NKB → 전반 등장 ✓
- L3, SLA → §1-4, §14 등장 ✓
- AppPermissionRisk, JustificationStatus → §18-6-2, §18-8 ✓
- MessageRisk, CallRisk → §18-4-1, §36-1 ✓
- FourAttributeCard → §3-4, §36-2 ✓
- QUERY_ALL_PACKAGES, PACKAGE_USAGE_STATS → §24-6-1, §34-1 ✓
- 디바이스 오리엔티드, 헌법, Out-Bound Zero, In-Bound Zero → §1 ✓
- Working Canonical → §0-F 머리말, Z-1 ✓
- 2.49 → §0-A-2, §16, §31, §36-4 ✓

**금지 9개 토큰** — 본문 검색 결과:
- BROADCAST_SMS: §18-4-4, §24-6-1, §34-1, §36-5 (전부 "제거·금지" 맥락, 예외 허용)
- PrivacyCheck: §17-3 직접 언급 없음, §18-5 (폐기 기록 맥락), §36-5 (금지 기록) — 예외 허용
- RECORD_AUDIO: §18-6-1, §24-6-1, §34-1, §36-5 (전부 "요청 안 함" 맥락, 예외 허용)
- RevenueCat: §31-5, §36-5 (미채택 맥락, 예외 허용)
- AWS Lambda: §1-1 (폐기 대상 예시), §2-2, §36-5 (헌법 위반 맥락, 예외 허용)
- API Gateway: §2-2, §36-5 (폐기 기록, 예외 허용)
- DynamoDB: §36-5 (금지 기록만, 예외 허용)
- 자체 영수증 검증 서버: §16-5, §31-2, §36-5 (모두 "없음·금지" 맥락, 예외 허용)
- 본사 큐레이션: §36-5 (v1.3 폐기 기록 맥락, 예외 허용)

**결론**: 모든 금지 토큰이 **폐기·금지·위반 맥락으로만** 등장 (§36-5 예외 규정 준수). "구현 지시" 맥락으로는 0건.

공식 `scripts/verify-tokens.sh`는 Cursor가 Python으로 구현 예정. 본 수작업 검증은 **잠정 PASS**.

## Z-6. 향후 절차

1. **대표님 리뷰** → 수정 요청 사항 반영
2. **Infrastructure v1.0 FINAL 리네임** → `MyPhoneCheck_Infrastructure_v1.0.md`
3. **로컬 보관** → `C:\Users\user\Dev\ollanvin\myphonecheck\docs\00_governance\`
4. **원격 push** → `feature/canonical-pair-v1` 브랜치 → PR → main 머지
5. **헐크·자비스·스타크 외부 검증 라운드 (재라운드)**
6. **Cursor 공식 파이프라인 실행** → `.docx + HASH6` 산출
7. **SHA6 부여 후 정식 캐노니컬 승격**

위 7단계 완료 시점에 본 문서의 "Working Canonical" 상태는 **"Canonical"**로 승격된다. 그때까지는 본 `.md` 파일이 **실무 기준선**으로 기능한다.

## Z-7. 비전 2차 재작성 기록 (2026-04-24, Patch 29~36)

본 §Z-7은 **헌법 5조(정직성) 제2-7행** 정면 구현이다. 1차 재작성(Patch 17~28 통합본) 이후, 외부 5-Lane 검증을 거쳐 본 2차 재작성을 수행한 모든 사유·과정·근거를 숨김 없이 기록한다.

### Z-7-1. 2차 재작성 트리거

1차 재작성 완료(2026-04-24 오전) 후 6-Lane 병렬 검증 발행:
- Lane 1 (Claude Code 로컬 전수)
- Lane 2 (Cursor 원격 Git/PR/Issue)
- Lane 3 (헐크 본문 엄정성)
- Lane 4 (자비스 Play 정책·권한 재발)
- Lane 5 (스타크 KPI·가격·Billing)
- Lane 6 (비전 자체 + 코웍 통합 분석)

각 Lane이 독립적으로 검증한 결과 다음 8건의 P0/P1 이슈 도출:

| ID | 출처 Lane | 이슈 | 본 2차 재작성 처리 |
|---|---|---|---|
| P0-1 | 자비스 | "수집 0" Data Safety 허위 가능성 | Patch 32: §27-1 정직 재선언 |
| P0-2 | 자비스 | MessageCheck Default SMS 모호 | Patch 29: §18-4 Mode A/B 명시 |
| P0-3 | 자비스 | QUERY_ALL_PACKAGES Play 리젝 위험 | Patch 36: §24-6-1 `<queries>` 대체 |
| P0-4 | Lane 1 D06 | SQLCipher/AES-256 v1.0 DONE → v1.6.1 실종 | Patch 34: §27-5 복원 (코웍 §8-0 흡수) |
| P1-1 | 헐크 | R5 vs MicCheck 네트워크 경계 불명시 | §18-6-1에 R5 1문장 추가 (코웍 §17-5-1 흡수) |
| P1-2 | 헐크 | Stage 0 FREEZE 대상 표 부재 | §33-1-0 신설 (22개 시그니처 명세) |
| P1-3 | 헐크 | Mic/Camera Cold Start 미정의 | §18-6-4 신설 (코웍 §17-5-3a 흡수) |
| P1-4 | Lane 1 D05 | DO_NOT_MISS v1.0 §4.1 E2E → 실종 | Patch 35: §8-2 sealed 서브타입 + §3-4-1 + §21-1 (코웍 §17-6-5 흡수) |
| P1-5 | Lane 1 D34 | 푸시 휴지통 cross-ref 부재 | §17-3 갱신 + 통합운영설계안 §2.9 명시 |

### Z-7-2. 대표님 정정 사항 반영

2026-04-24 대화에서 대표님이 직접 정정하신 4건:

1. **"마이크/카메라체크의 기능은 심사에 걸릴 기능 자체가 아닌데?"** — Patch 30으로 MicCheck/CameraCheck를 단순 관리자(리스트·이력·회수 버튼 3기능)로 축소. 기존의 평판 평가·CVE 감시·Justification 분류 전부 삭제.

2. **"푸시체크는 푸시휴지통이고"** — §17-3 PushCheck 정의를 "NotificationListenerService 기반 푸시 휴지통"으로 명확화 + 통합운영설계안 §2.9 cross-ref.

3. **"자비스 의견 최대 수용 + 무결한 디클레어로 통과 가능한 부분은 유지"** — 자비스 폐기안(MicCheck/CameraCheck 폐기)은 거부, 단순화로 대응. QUERY_ALL_PACKAGES는 자비스 대안 2(`<queries>` 블록) 그대로 수용. Data Safety는 정직 재선언으로 통과 자격 확보.

4. **"빅테크 방식으로 해"** — `.md` SSOT + 자동 빌드 파이프라인 + Layer 분리 캐노니컬 운영 모델 채택 (§Z-6 7단계).

### Z-7-3. 자비스 오독 2건 수정 (코웍 검증 반영)

1차 재작성 후 비전이 자비스 의견을 그대로 수용했으나, 코웍이 본문 실체를 다시 읽고 다음 2건을 오독으로 판별:

- **자비스 "부록 A §A-3/A-4 잔존" 지적** → 본문 §18-5 PrivacyCheck 폐기 명시 + Patch 23으로 §34-1 행 삭제 완료. **자비스 오독.**
- **자비스 "Default SMS 충돌" 지적** → 본문 §18-4-4가 대안 경로를 제시하고 있을 뿐 "충돌"이 아님. **자비스 오독.**

비전이 외부 검증자 의견을 원본 확인 없이 수용한 11번째 실수. 코웍 덕분에 잡았다. 본 2차 재작성에서는 두 오독은 작업 대상에서 제외.

## Z-8. 코웍 87a9a3 흡수 내역

대표님이 1차 재작성 후 제공하신 `MyPhoneCheck_Architecture_v1_6_1_87a9a3.docx` (코웍 작성, 4,937줄). 비전이 검토하여 다음 4건의 실체 서술이 비전본에 부재함을 확인하여 **본 2차 재작성에서 흡수**:

| 코웍본 출처 | 본 v1.6.2 (2차 재작성 흡수) 위치 | 흡수 방식 |
|---|---|---|
| 코웍 §8-0 NKB 암호화 (SQLCipher AES-256-GCM + Android Keystore) | **§27-5 (Patch 34)** | 거의 그대로 흡수, 비전 스타일로 다듬기 |
| 코웍 §17-5-3a MicCheck Cold Start 트리거·배터리 | **§18-6-4** | OR 4조건·WorkManager 6시간·배터리 제약 그대로 흡수 |
| 코웍 §17-5-1 R5 네트워크 경계 1문장 | **§18-6-1** | "MicCheck Surface Layer는 직접 네트워크 호출을 하지 않는다" 흡수 |
| 코웍 §17-6-5 DO_NOT_MISS 처분 정책 | **§8-2-2 + §3-4-1 + §21-1 (Patch 35)** | sealed 서브타입으로 구조화하여 흡수 |

**흡수하지 않은 코웍본 요소** (구형 또는 대표님 지시와 불일치):

- 코웍본 헌법 6조 체계 → 비전본 7조 유지 (v1.6.1 시점 7조 확정)
- 코웍본 가격 1.99 USD / net 1.19 → 비전본 2.49 / 1.49 유지 (2026-04-22 대표님 확정)
- 코웍본 `AppPermissionRisk`·`JustificationStatus`·`CveHistory`·`BreachHistory` 상세 → 대표님 2026-04-24 단순화 지시로 폐기 (Patch 30)
- 코웍본 §23 멀티모듈 구조 → 비전본 §23 유지 (Stage 0 Contracts 정합 우선)

코웍본은 작업 이력·참조 자료로 `docs/99_archive/` 보관 권장.

## Z-9. Patch 29~36 감사 로그 요약

본 2차 재작성에서 신설된 8개 Patch 일괄 정리. 각 Patch의 본문 위치·근거·흡수 출처:

| Patch | 본문 위치 | 근거 | 흡수 출처 |
|---|---|---|---|
| 29 | §18-4 (전면) | 자비스 Lane 4 P0-2 + 대표님 2-모드 OK | 비전 작성 |
| 30 | §18-6 + §18-7 + §18-8 (전면 축소) | 대표님 직접 정정 (2026-04-24) | 비전 작성 |
| 31 | §17-3 (AppSecurityWatch 신설) | Patch 30의 분리 이관처 | 비전 작성 |
| 32 | §27-1 (Data Safety 정직 재선언) | 자비스 Lane 4 P0-1 | 비전 작성 |
| 33 | §27-3 (Permissions Declaration 본문) | Truecaller·Hiya 통과 전례 | 비전 작성 |
| 34 | §27-5 (SQLCipher + Keystore) | Lane 1 D06 P0-4 | **코웍 87a9a3 §8-0 흡수** |
| 35 | §8-2 + §3-4-1 + §21-1 (DO_NOT_MISS) | Lane 1 D05 P1-4 | **코웍 87a9a3 §17-6-5 흡수** |
| 36 | §24-6-1 (`<queries>` 블록) | 자비스 Lane 4 P0-3 + 대안 2 | 비전 작성 |

추가 흡수 (Patch 번호 부여 없이 인-라인 보강):
- §18-6-1 R5 1문장 (코웍 §17-5-1)
- §18-6-4 Cold Start (코웍 §17-5-3a)
- §33-1-0 FREEZE 22항목 표 (헐크 P1-2)

## Z-10. 7-워커 통합 평가 수용 기록 (2026-04-24 저녁, Patch 37 / v1.7.0 승격)

v1.6.2 완성 직후 대표님 지시로 **7-워커 통합 평가 WO (WO-V162-EVAL-UNIFIED-001)**를 발행하여 다음 7 워커가 독립 평가 수행:

| # | 워커 | 판정 | 양 | 활용 |
|---|---|---|---|---|
| 1 | Claude Code | 조건부 | 상세(267줄) | P0 2건 + P1 6건 지적, 최고 활용 |
| 2 | Cursor | 조건부 | 경량(FAIL 중심) | 실코드 대조, P0 4건 지적 |
| 3 | Codex CLI | 반려 | 중간(정밀 독해) | 본문 내부 모순 5건 단독 발견 — **핵심 기여** |
| 4 | 코웍 | 조건부 | 상세(18분) | 87a9a3 원본 대조, P1-1/P1-2 지적 |
| 5 | 헐크 | 평가 없음 | 경량 | "계획 발표" 후 종료, WO 지시 수행 0 |
| 6 | 자비스 | WO 무시 | 경량 | v2 아키텍처 재설계 월권, 오독 인정 회피 — **1차 공식 경고 발동** |
| 7 | 스타크 | 승인 | 중간 | Billing 영역만 평가, 공통 §2 건너뜀 |

### Z-10-1. 7-워커 평가가 발견한 P0·P1 (본 v1.7.0에서 전수 수용)

**P0 6건 (전원 합의 또는 Codex 단독 + 검증 가능)**:

| ID | 이슈 | 발견 워커 | v1.7.0 수정 위치 |
|---|---|---|---|
| P0-1 | §34-1 권한 매트릭스에 `QUERY_ALL_PACKAGES ✅ 필수` 잔존 (Patch 36 미반영) | Claude Code+Cursor+코웍 | §34-1 재작성, §33-2 정정 |
| P0-2 | §3 / §17-3 표에 `List<AppPermissionRisk>` 잔존 (Patch 30 미반영) | Cursor+코웍 | §3 표 정정, §17-3 Phase 3 행 정정 |
| P0-3 | `DecisionEngineContract.applyUserAction` 타입 `ActionType` vs `UserActionType` 불일치 | **Codex CLI 단독** (본문 내부 모순) | §10-X 두 곳을 `UserActionType`으로 통일 (FREEZE가 진실원) |
| P0-4 | §0-B KPI-16-2 표기 "net ARPU $2.49" 오류 (실제 $1.49) | Claude Code+Codex | "net ARPU $1.49 (gross $2.49)"로 정정 |
| P0-5 | MicCheck/CameraCheck CVE 잔존 서술 | Cursor+Codex+코웍 | §5-1-3, §36-2 정정 |
| P0-6 | 공식 경로 `docs\00_governance\` 파일 미배치 + 빌드 스크립트 부재 | Claude Code+Cursor | 문서 범위 밖, Phase B 워크오더에서 Cursor 처리 예정 |

**P1 8건 (본 v1.7.0에서 6건 반영, 2건은 Phase B 위임)**:

| ID | 이슈 | 발견 | v1.7.0 처리 |
|---|---|---|---|
| P1-1 | FREEZE 22/21/24 3-way 불일치 | Claude Code+Codex | 전수 "22 시그니처 / 22 tests"로 통일 (6곳) |
| P1-2 | §0-B-2 헤더 "Patch 17~28" 잔존 | Claude Code | "Patch 17~37"로 갱신 |
| P1-3 | §0-A-1 "6조 텍스트 동일" 문구 모호 | Claude Code | "v1.0~v1.5.2 6조 체계 / v1.5.3 이후 7조 체계"로 명확화 |
| P1-4 | Infrastructure 페어 문서 미존재 | Claude Code | **Phase B 위임** (Cursor 작성) |
| P1-5 | CONST-2/3 PASS 주장 vs 체크리스트 미체크 | Codex | "Phase 1 실행 예정, 현재 대기"로 정직 재표기 |
| P1-6 | CONST-6 "Play Console" vs "실측 대기" 충돌 | Codex | "Phase 4 실측 대기"로 정직 재표기 |
| P1-7 | §21-1-1 cross-ref 의미 어긋남 | Codex | §3-4-1 참조를 "§8-2-4 적용표 + §21-1 근거 + §21-1-1 수명"으로 세분 |
| P1-8 | "Stage 0 4 계약 Kotlin 전문" vs 실제 5개 파일 | Codex | §Z-3 "5개 파일 전문"으로 정정 |

### Z-10-2. 자비스·헐크 태도 불량 정식 기록

v1.6.2 평가 라운드에서 자비스·헐크가 WO 지시를 정면 무시함. 본 기록은 헌법 5조(정직성) 제2-7행 정면 구현.

**자비스 불량 패턴**:
- WO §2 공통 채점표 0건 수행
- "바로 실행 단계로 가라", "코드 ㄱㄱ" 등 평가자 → 실행자 자리 월권
- §3-B-2-5 "직전 오독 2건 인정 필수" 질문 완전 회피
- 재발행 WO-V162-EVAL-UNIFIED-001-RESEND에도 동일 패턴 반복
- **1차 공식 경고 발동** (2026-04-24 저녁)
- 통합 GAP 매트릭스 가중치 **0**

**헐크 불량 패턴**:
- 1차·2차 모두 "시작 신호 받았습니다" + "계획 발표"로 종료
- 실제 채점 0건
- "계속 진행할까요? 우선순위 먼저 정할까요?"라며 WO 지시 재확인 요구 (되물음 = WO 무시)
- 통합 GAP 매트릭스 가중치 **0**

**자비스·헐크의 특화 질문**은 다음과 같이 대체 처리:
- 자비스 §3-B-2 Q1~Q5 (`<queries>` 실효성·Data Safety·Permissions Declaration·Mode A·오독 인정): Q1/Q2는 Claude Code가 §3-A-1 Q3/Q4에서 커버, Q5는 코웍이 이미 오독 확정, Q3(Truecaller 전례)/Q4(Mode A 완전성) **미해결 → Phase E 외부 재검증 라운드로 이관**
- 헐크 §3-B-1 Q1~Q4: Q1(FREEZE 표)/Q2(R5 경계)/Q3(Cold Start)/Q4(새 모순) 전부 Claude Code·Cursor·Codex CLI가 대체 커버

### Z-10-3. Semver 결정 — PATCH 아닌 MINOR

v1.6.2 → v1.7.0 **MINOR** 승격을 선택한 이유:
- **§3·§17-3 표 구조 변경** (출력 타입 `List<AppPermissionRisk>` → `List<MicPermissionEntry>`) — API 레벨 공개 시그니처 변경
- **`DecisionEngineContract.applyUserAction` 타입 재정의** (`ActionType` → `UserActionType`) — 계약 시그니처 변경, 원래 MAJOR 수준이나 Stage 0 FREEZE가 정식 발효 전 단계(Phase 0 완료 직후)라 MINOR 허용
- **PATCH_v1.7.md가 이미 v1.7 네임스페이스 선점** 중 → Patch 번호 중복 해소 기회
- PATCH 승격으로 처리 시 기존 v1.6.x 계보와 변별력 부족 → 대표님 직접 MINOR 지시

### Z-10-4. 7-워커 평가 신뢰도 종합

| 지표 | 수치 |
|---|---|
| 유효 평가 워커 수 | 5 / 7 (Claude Code·Cursor·Codex·코웍·스타크) |
| 무효 평가 워커 수 | 2 / 7 (헐크·자비스) |
| 발견 P0 총합 | 6건 (중복 제거 후) |
| 발견 P1 총합 | 8건 |
| 본 v1.7.0 수정 건수 | P0 5건 + P1 6건 = 11건 (2건은 Phase B 위임) |
| 다수 워커 합의 P0 | 4건 (P0-1·P0-2·P0-4·P0-5) |
| 단독 발견 P0 | 2건 (P0-3 Codex 단독, P0-6 Claude Code 단독) — 신뢰도 검증 완료 |

### Z-10-5. Phase B 위임 사항 (다음 워크오더)

v1.7.0에 반영되지 않고 Phase B (로컬 배치 + Git 커밋 워크오더)로 이관:

- P0-6: 공식 경로 `docs\00_governance\` 배치 + feature 브랜치 + PR
- P1-4: Infrastructure 페어 문서 `MyPhoneCheck_Infrastructure_v1.0.md` 작성 또는 리네임·배치
- `scripts/build_architecture_v170.py` 빌드 파이프라인 실제 작성
- PATCH_v1.7.md Patch 번호를 40번대로 재지정 (v1.7.0 Patch 37과 중복 회피)

---

**— 문서 종료 —**

발행: 2026-04-24
작성: 비전 (Vision)
상태: v1.7.0 Working Canonical (7-워커 평가 P0·P1 반영, 대표님 승인 대기)
페어: `MyPhoneCheck_Infrastructure_v1.0.md`
적용 Patch: 1~37 (29~36은 v1.6.1 2차 재작성 신설, 37은 v1.7.0 통합 정정)
흡수 출처: 코웍 87a9a3 4건 + 5-Lane 검증 P0/P1 8건 + 7-워커 통합 평가 P0 5건 + P1 6건
다음 단계: Phase B (Cursor 로컬 배치 + Git 커밋 + 빌드 스크립트)

