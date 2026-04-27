# KPI 매핑 (정직성 감사)

**원본 출처**: v1.7.1 §0-B (0줄)
**v1.8.0 Layer**: Business
**의존**: `70_business/01_business_model.md`
**변경 이력**: 본 파일은 v1.7.1 §0-B 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cowork/70_business/02_kpi_mapping.md`

---

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


