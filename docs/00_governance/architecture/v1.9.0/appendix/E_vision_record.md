# Z. 비전 작성 기록 (0-Z Appendix)

**원본 출처**: v1.7.1 §Z (4252–4555)
**v1.9.0 Layer**: Appendix
**의존**: `00_core/01_primary.md`
**변경 이력**: v1.7.1 §Z 원본을 v1.8.0 이관 후, v1.9.0 MAJOR 승격 사유 §Z-11 추가 (Six Surfaces, PushCheck 정식, CardCheck 신설, Surface 정의 정정).
**파일 경로**: `docs/00_governance/architecture/v1.9.0/appendix/E_vision_record.md`

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

## Z-11. v1.9.0 MAJOR 승격 사유 (2026-04-27, Patch 39·40)

본 §Z-11은 v1.8.0 → v1.9.0 MAJOR 승격을 정직하게 기록한다. 헌법 5조(정직성) 정면 구현.

### Z-11-1. 승격 트리거

대표님 비준 (2026-04-27):

1. **PushCheck 정식 승격 결정**: Stage 1-001 cursor 구현 완료 (`feature/push-trash` 모듈, NLS + Room DB v12 + Compose UI). 더 이상 "Phase 후행"이 아닌 정식 Surface.

2. **CardCheck 신규 신설 결정**: 카드스펜드 별도 앱 폐기 + MyPhoneCheck 통합. SMS/Push 재활용 모델로 새 권한·외부 통신 0 보장.

3. **Surface 정의 정정 결정**: "Engine 사용 단위"라는 v1.8.0 정의가 부정확함을 인정 (MicCheck/CameraCheck는 Engine 안 쓰면서도 정식 Surface). "Value Extraction Layer"로 정정.

4. **Producer/Consumer 모델 명시 결정**: CardCheck = 순수 소비자 사례를 통해 Surface 간 의존 패턴을 처음 명시.

### Z-11-2. SemVer MAJOR 결정 사유

v1.8.0 → v1.9.0 **MAJOR** 승격이 정합:

- **Surface 정의 자체 변경**: "Engine 사용 단위" → "Value Extraction Layer"는 § 17-1 핵심 정의 변경. SemVer MAJOR 수준.
- **Surface 개수 변경**: 4 → 6. 외부에서 명세 참조하는 워커·외부 검증자가 이전 정의로 동작하면 호환성 깨짐.
- **Three Pillars 두 번째 기둥 갱신**: 00_core/02_secondary.md "Four Surfaces — 네 표면" → "Six Surfaces — 여섯 표면". 헌법 가까운 영역 변경.
- **PATCH 또는 MINOR로 처리 시 변별력 부족**: v1.7.1·v1.8.0 모두 4 Surface 명세. v1.9.0이 6 Surface임은 명시적 MAJOR 신호.

### Z-11-3. v1.7.1 / v1.8.0 보존 정책

v1.7.1은 v1.6.x 계열 PATCH (FROZEN), v1.8.0은 4워커 마이그레이션 결과 (FROZEN). v1.9.0 신설은 **v1.8.0 디렉토리 통째 복사 후 변경**:

- v1.7.1, v1.8.0 디렉토리 무손상 보존 (역사 기록)
- v1.9.0 신설 디렉토리 안에서만 갱신
- 향후 v2.0.0 (iOS Edition)까지 v1.9.x 계열 유지

### Z-11-4. 본 v1.9.0 Working Canonical 한계 (정직 기록)

- **외부 검증 미실시**: 헐크·자비스·스타크 라운드 없이 본 MAJOR 승격 진행 (페어 워커 Codex CLI + 크로스 체크 Cursor·Cowork만)
- **메인 구현 페어 채점 대기**: Claude Code (본 작업) + Codex CLI 동시 실행 결과를 비전이 8축 채점하여 채택본 결정 예정
- **CardCheck 구현 미진행**: 본 v1.9.0은 스펙 신설만. 실제 구현은 Stage 1-002 별도 PR
- **카드스펜드 별도 앱 폐기 미진행**: figma 자료 보존 + 코드 폐기는 별도 작업
- **Architecture v1.9.0 ↔ Infrastructure 페어 갱신 미실행**: Infra는 별도 PR로 v1.2 승격 예정 (현행 v1.1 그대로)

위 한계는 본 PR 머지 후 후행 워크오더로 단계적 해소.

### Z-11-5. 본 v1.9.0이 흡수한 입력

| 입력 | 반영 위치 |
|---|---|
| 메모리 #14 (PushCheck 푸시 휴지통) | §17-3 + §26 신설 |
| Stage 1-001 cursor 구현 보고서 | §26-5 (5개 범위 모두 완료 표시) |
| 통합운영설계안 v1 §2.9 | §26-9 cross-ref |
| `docs/05_quality/stage1_push_trash_manual_test.md` | §26-9 cross-ref |
| 카드스펜드 폐기 결정 (메모리 #2) | §27 신설 + §27-12 cross-ref |
| 4워커 분담 정책 갱신 (2026-04-27) | 본 WO 헤더 + Layer 3 표시 |

### Z-11-6. 본 v1.9.0 Patch 번호 부여

본 MAJOR 승격에서 새로 도입된 패치는:

- **PATCH-39**: PushCheck 정식 Surface 승격 (§17-2 + §17-3 + §26 신설)
- **PATCH-40**: CardCheck 신규 Surface 신설 (§17-2 + §17-3 + §27 신설)

기존 PATCH-17~38은 v1.7.x 계열에서 누적된 패치. PATCH-39·40은 v1.9.0 신설 시점의 신규 patch이며 §0-B-2 (B_patch_history.md) 표 마지막에 추가.

### Z-11-7. 다음 단계

1. **본 PR 머지** (페어 채점 + 채택본 결정 + 크로스 체크 후)
2. **Stage 1-002 워크오더 발행**: CardCheck 실제 구현 (`feature/card-check` 모듈)
3. **카드스펜드 별도 앱 폐기·마이그레이션**: figma 자료 보존 + 코드 폐기 별도 작업
4. **Infrastructure v1.2 승격 검토**: Architecture v1.9.0 페어 정합 (§35-6 v1.2 갱신 주석 추가)
5. **외부 검증 라운드** (헐크·자비스·스타크): v1.9.0 정합성 + Six Surfaces 통합 확인

---

**— 문서 종료 —**

발행: 2026-04-24
작성: 비전 (Vision)
상태: v1.7.0 Working Canonical (7-워커 평가 P0·P1 반영, 대표님 승인 대기)
페어: `MyPhoneCheck_Infrastructure_v1.0.md`
적용 Patch: 1~37 (29~36은 v1.6.1 2차 재작성 신설, 37은 v1.7.0 통합 정정)
흡수 출처: 코웍 87a9a3 4건 + 5-Lane 검증 P0/P1 8건 + 7-워커 통합 평가 P0 5건 + P1 6건
다음 단계: Phase B (Cursor 로컬 배치 + Git 커밋 + 빌드 스크립트)
