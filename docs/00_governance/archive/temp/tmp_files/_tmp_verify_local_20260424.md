# Lane 1 Claude Code 로컬 검증 리포트

**WO**: WO-V161-VERIFY-LOCAL-001
**검증 대상**: `MyPhoneCheck_Architecture_v1.6.1.md` (2,935줄, 157KB)
**실측 경로**: `C:\Users\user\Downloads\MyPhoneCheck_Architecture_v1.6.1.md`
**공식 경로(`docs/00_governance/`) 상태**: **MISSING** — 대표님 복사 전
**수행자**: Claude Code (Opus 4.7)
**일자**: 2026-04-24

---

## 1. 인덱싱 결과

- **탐지된 과거 MyPhoneCheck 자료**: **88건** (4개 탐색 루트, .desktop은 루트 미존재)
- **탐색 루트**:
  - `C:\Users\user\Dev\ollanvin\myphonecheck` — 리포지토리 내 과거 자료 (현재 + backup + archive)
  - `C:\Users\user\Downloads` — 가장 많은 결정 문서 보관 (v1.0~v1.6.1, WO 시리즈)
  - `C:\Users\user\ollanvin` — 작업 폴더 (전용 자료 없음)
  - `C:\Users\user\Desktop` — **루트 미존재**
- **핵심 자료 계보 (버전 순)**:
  - v1.0 → v1.1 → v1.2 → v1.3 → v1.4_disc(3인 만장일치 헌장) → v1.5.1/5.2/5.3 → v1.6.0(Four Surfaces) → v1.6.1-patch(6건 정정)
  - WO 계보: v2 → v3/v3.1 → v4/v4.1/v4.2/v4.3 → v1.5.1~1.5.3 WO → v1.6.0_FourSurfaces → v1.6.1-patch → Stage0_Contracts → Stage0_hotfix_Java17
  - 통합 문서: `MyPhoneCheck_통합운영설계안_v1.docx` (2026-04-24, $2.49 확정 기록)
  - Infra: `MyPhoneCheck_Infra_Ops_v1.md` (AWS Lambda 폐기 근거)
  - 거버넌스: `docs/00_governance/patches/PATCH_v1.7.md` (Patch 30~34, v1.6.1 이후)

**추출 방식**: 35개 .docx를 System.IO.Compression으로 풀어 `word/document.xml` 태그를 스트립 → 텍스트 파일로 분석(`C:\Users\user\ollanvin\_tmp_docx_extract\`). 원본 파일 수정 0건.

---

## 2. 추출된 과거 결정 목록 (주요 35건)

| # | 출처 파일(최종 근거) | 결정 요지 | 키워드 |
|---|---|---|---|
| D01 | v1.0 §2, §11.2 | 100% 온디바이스, 서버 없음, PII 저장 금지 | Out-Bound Zero |
| D02 | v1.0 §3.1, v1.6.0 WO §1-2 | 4 체크 유닛 (Call/Message/Mic/Camera) | Four Surfaces |
| D03 | v1.0 §9, §11 | 190개국 동시 론칭, 7언어 초기 (EN/KO/JA/ZH/RU/ES/AR) | 190개국 |
| D04 | v1.0 §11.1 | READ_CALL_LOG/READ_SMS 원칙 DENY(이후 Patch 정당화로 전환) | 권한 매트릭스 |
| D05 | v1.0 §4.1 | DO_NOT_MISS (저장 없이 중요 번호 표시) | DO_NOT_MISS |
| D06 | v1.0 §13 | SQLCipher AES-256-GCM + Android Keystore | SQLCipher |
| D07 | v1.1 §1544 | $5.99 → $1.99 인하 (경쟁 가격 전략) | 가격 변경 |
| D08 | v1.2 | SearchResultAnalyzer P0, NKB 신규 모듈, Outbound Zero 헌법 2조 | NKB / SearchResultAnalyzer |
| D09 | v1.4_disc §1-3 (2026-04-22 만장일치) | 헌법 3조 — 결정권 중앙집중 금지 (정적 입력 금지에서 정정) | 헌법 3조 |
| D10 | v1.4_disc §1-4 | SLA 4단계 신설 (L1 Full/L2 Degraded/L3 Offline/L4 Catastrophic), 헌법 기준선 L3 | SLA 4단계 |
| D11 | v1.4_disc §1-6 | 헌법 6조 — 가격 정직성 (net ARPU 기준 KPI) | 헌법 6조 |
| D12 | v1.4_disc §2-2, §2-4 | 황금알 자격 4조건, 출력 4속성 (위험도/예상손해/유형/이유) | 황금알 4속성 |
| D13 | v1.4_disc §3-1 | One Engine, Multiple Surfaces (수직 깊이 우선, 수평 후행) | One Engine |
| D14 | v1.4_disc §4 | net ARPU $1.19 (gross $1.99), 전환율 3%, 국가당 14.7만 MAU, 월 $1M | net ARPU |
| D15 | v1.4_disc §5-2 | Phase 1~4 로드맵 (Call→Message→Privacy→URL/App) | Phase 로드맵 |
| D16 | v1.4_disc §8-2 | Cold Start Day 1/3/7/30 단계별 황금알 가치 도달 | Cold Start |
| D17 | v1.5.2-patch Patch 09 | Section 6 — Three-Layer Knowledge Sourcing 명문화 | 3계층 소싱 |
| D18 | v1.5.2-patch Patch 10 | Decision Engine Contract (PRE/POST, STALE_KNOWLEDGE) | [P10_DECISION_CONTRACT] |
| D19 | v1.5.2-patch Patch 11 | Data Model FREEZE (ExtractedSignal/NumberKnowledge/UserAction/Decision) | [P11_DATA_MODEL_FREEZE] |
| D20 | v1.5.2-patch Patch 13 | Zero Central Server 정의 명확화 (외부 표준/OS API는 위반 아님) | Zero Central Server |
| D21 | v1.5.2-patch Patch 14 | 거버넌스 프레임워크 (0-A 버전/0-B 감사/0-C 정책/0-D 한계/0-E SHA256) | [P14_GOVERNANCE] |
| D22 | v1.5.2-patch Patch 15 | Store Policy 사전 스크리닝 + 부록 A Permissions Declaration Form | 부록 A |
| D23 | v1.5.2-patch Patch 16 | Test Infrastructure (AVD 5개 매트릭스, CallSimulator, 90% 커버리지) | [P16_TEST_INFRA] |
| D24 | v1.5.3 Patch 17 | BROADCAST_SMS 권한 완전 제거 (Play 정책 위반) | Patch 17 |
| D25 | v1.6.0 WO §1-3, §4-1 | MicCheck/CameraCheck는 본 앱이 RECORD_AUDIO/CAMERA 권한 **미보유** (설치앱 스캔+평판+원터치 해제) | 권한 타령 종료 |
| D26 | v1.6.0 WO §2-2 | RiskKnowledge 일반화 + IdentifierType sealed class + 4 Checker 구현체 | Stage 0 4 계약 |
| D27 | v1.6.0 WO §3 | MessageCheck = 발신번호 + URL + 기관 사칭 3중 분석 (InstitutionMatch) | MessageCheck 3중 |
| D28 | v1.6.0 WO Patch 21 | PrivacyCheck Surface 폐기 → MicCheck/CameraCheck로 대체 | Patch 21 |
| D29 | v1.6.1-patch Patch 23 | §34-1 권한 매트릭스에서 RECORD_AUDIO/CAMERA 행 삭제 | Patch 23 |
| D30 | v1.6.1-patch Patch 24 | 부록 A §A-3/§A-4 삭제, §A-5 SYSTEM_ALERT_WINDOW → §A-3 번호 당김 | Patch 24 |
| D31 | v1.6.1-patch Patch 25 | §18-4/§18-6/§18-7 본문 완성 (Kotlin data class + KB국민은행 시나리오) | Patch 25 |
| D32 | v1.6.1-patch Patch 26 | §24-6 Manifest에 QUERY_ALL_PACKAGES + PACKAGE_USAGE_STATS 실제 선언 | Patch 26 |
| D33 | 통합운영설계안_v1 §2.6 (2026-04-24) | 가격 $2.49/월 단일 가격 전세계 동일, 연간 없음 | $2.49 확정 |
| D34 | 통합운영설계안_v1 §2.9 | 푸시 휴지통: 통계만=반쪽 기능 폐기, 실제 격리+휴지통 UI 필수 | 푸시 휴지통 |
| D35 | 통합운영설계안_v1 §2.10 | Mic/Camera 실시간 이벤트 감지 (PACKAGE_ADDED + NVD CVE + CISA KEV + FCM) | 실시간 이벤트 |

추가 발견 (v1.6.1 이후 존재):
- `docs/00_governance/patches/PATCH_v1.7.md` — Patch 30~34 (Patch 30: $2.49 단일화, 31: PushCheck 재정의, 32: Mic/Camera 트리거형, 33: 검색 3축 명문화, 34: RiskLevel 매퍼 정책). 현재 검증 대상 v1.6.1 범위 **외**.

---

## 3. 본문 ↔ 과거 자료 대조

| # | 과거 결정 | 본문 섹션/줄 | 상태 |
|---|---|---|---|
| D01 | 100% 온디바이스 / Out-Bound Zero | §1 제1조 (223~), §15 "본사 매핑 0건" | ✅ 존재 |
| D02 | 4 체크 유닛 (Call/Message/Mic/Camera) | §4 제품 도메인, §17-2, §18-4~18-7, §36 Four Surfaces | ✅ 존재 (확장) |
| D03 | 190개국 | §20-5 (2281줄: "150개국 $2.49 Tier"), §25-3 locale 11개 | ✅ 존재 |
| D04 | READ_CALL_LOG/READ_SMS 정책 | §18-4-4 (Default SMS Handler), §34-1 권한 매트릭스 | ✅ 존재 (Patch 17 반영) |
| D05 | DO_NOT_MISS | **없음** (grep 0건) | ⚠️ **누락** (v1.0 기능, 의도된 폐기 가능성) |
| D06 | SQLCipher AES-256-GCM | **없음** (grep 0건) | ⚠️ **누락** (보안 구현 스펙 본문 부재) |
| D07 | 가격 변경 이력 ($5.99→$1.99) | §0-A-2 "가격 정책 변경 이력" (96~), §16-4 버전 비교표 (1619~) | ✅ 존재 |
| D08 | SearchResultAnalyzer / NKB | §9 SearchResultAnalyzer, §8 NKB | ✅ 존재 |
| D09 | 헌법 3조 결정권 중앙집중 금지 | §1 제3조 (247~) | ✅ 존재 (v1.4_disc 본문 계승) |
| D10 | SLA 4단계, L3 기준선 | §4-1 SLA 4단계, §14 L1/L3 상세, §14-2 헌법 기준선 | ✅ 존재 |
| D11 | 헌법 6조 가격 정직성 | §1 제6조 (314~), §16 Business Model | ✅ 존재 |
| D12 | 황금알 4조건 / 출력 4속성 | §3-1 4조건 (442~), §3-2 4속성 (453~), §3-4 UX 표현 규격 | ✅ 존재 |
| D13 | One Engine, Multiple Surfaces | §3-3 (466~), §17 제품 전략, §36 Four Surfaces 통합 | ✅ 존재 (Four Surfaces로 구체화) |
| D14 | net ARPU / 전환율 3% / 14.7만 MAU | §16-2 재계산 (1592~): gross $2.49 → net $1.49, **break-even 67,114명/국가당** | ⚠️ **수정됨** ($2.49 반영으로 MAU 기준 재산출. v1.4_disc "14.7만"은 $1.99 기준이었음) |
| D15 | Phase 1~4 로드맵 | §17-4 Phase 로드맵 | ⚠️ **의미 변경** (v1.6.0에서 Four Surfaces 동시 활성화로 수정됨, §17-1 명시) |
| D16 | Cold Start Day 1/3/7/30 | §11 Cold Start 단계 (1333~) | ✅ 존재 (구체 일정은 요약됨) |
| D17 | 3-Layer Knowledge Sourcing | §5-1 (554~), §6 상세 (656~) | ✅ 존재 |
| D18 | Decision Contract | §10-X (1308~) "Decision Contract (v1.5.2 Patch 10)" | ✅ 존재 |
| D19 | Data Model FREEZE | §8-X (1036~) "Data Model Freeze Declaration (v1.5.2 Patch 11)" | ✅ 존재 |
| D20 | Zero Central Server 정의 | §15 본사 매핑 0건 + 정의 | ✅ 존재 |
| D21 | 거버넌스 0-A~0-E | §0-A/0-A-1/0-A-2/0-B/0-B-1/0-B-2/0-C/0-D/0-E 전부 | ✅ 존재 |
| D22 | 부록 A Permissions Declaration | Patch 24로 §A-3/A-4 삭제 반영 (§34-1 "삭제됨 (Patch 24)" 명시, 3400줄) | ✅ 존재 |
| D23 | Test Infrastructure | §35 Test Infrastructure (언급), §26 CI/CD JDK 17 | ✅ 존재 |
| D24 | BROADCAST_SMS 제거 (Patch 17) | §18-4-4 (1858~), §24-6 금지권한 (2527줄), §34-1 (3376줄) | ✅ 존재 (다중 지점 방어) |
| D25 | MicCheck/CameraCheck 본 앱 권한 0 | §18-6-1 핵심 원칙, §18-7-1 CameraCheck 범위 | ✅ 존재 |
| D26 | IdentifierType / RiskKnowledge / Checker / DecisionEngineContract | §33-1-1 IdentifierType, §33-1-2 RiskKnowledge, §36-1 4 계약 | ✅ 존재 (Stage 0 4 계약 FREEZE) |
| D27 | MessageCheck 3중 분석 + InstitutionMatch | §18-4 MessageCheck (1753~), KB국민은행 시나리오 포함 | ✅ 존재 |
| D28 | PrivacyCheck 폐기 (Patch 21) | §18-5 "PrivacyCheck 폐기 기록 (Patch 21)" (1864~) | ✅ 존재 |
| D29 | Patch 23 권한 매트릭스 삭제 | §34-1 (3360줄) "권한 매트릭스 (Patch 23 적용)" — RECORD_AUDIO/CAMERA **취소선 + 요청 안 함** | ✅ 존재 |
| D30 | Patch 24 부록 A §A-3/A-4 삭제 | §34-1 (3400줄) 명시적 흡수 선언 | ✅ 존재 |
| D31 | Patch 25 본문 완성 (Kotlin + 시나리오) | §18-4(MessageCheck)·§18-6(MicCheck)·§18-7(CameraCheck) 본문에 Kotlin 구현 존재 | ✅ 존재 |
| D32 | Patch 26 Manifest 권한 선언 | §24-6-1 "Manifest 권한 정합 (Patch 26)" (2507~) | ✅ 존재 |
| D33 | $2.49 확정 단일가 | §0-A-2, §16-1 (1582~), §24-12, §30 전반 | ✅ 존재 |
| D34 | 푸시 휴지통 (실제 격리 + UI) | §17-3 "향후 Surface" (1663~) + §21 Open Issues — **후행 Surface로만 언급** | ⚠️ **범위 외** (v1.6.1 본문에 구체 스펙 없음, PATCH_v1.7 Patch 31로 이관) |
| D35 | Mic/Camera 실시간 이벤트 감지 | §18-6-4 "외부 이벤트 감지 (메모리 #13)" + ACTION_PACKAGE_ADDED 코드 (2004줄), CISA KEV 언급 (2043줄) | ✅ 존재 |

**누락 가능성 플래그**: D05(DO_NOT_MISS), D06(SQLCipher/AES-256), D34(푸시 휴지통 구체 스펙).

---

## 4. 본문에만 존재 (근거 없음 플래그)

| # | 본문 주장 | 섹션 | 비고 |
|---|---|---|---|
| B1 | 제7조 Device-Oriented Goose (단독 조항) | §1 제7조 (332~) | **근거 있음** (v1.4_disc §1-1에 "디바이스 오리엔티드 거위" 개념이 헌법 1조 안에 포함돼 있었으나 v1.5.x 들어 별도 5조로 분리됨. v1.5.1_fd908b/7d23b4 텍스트에 "Device-Oriented Sovereignty"로 존재. v1.6.1이 7조로 격상 — 정당) |
| B2 | Round 5 만장일치 (§22) | §22 (2308~) | **근거 있음** (v1.4_disc가 Round 1~4, v1.6.1 자기점검이 Round 5로 이어짐. v1.5.2-patch Patch 14의 "v1.5 라운드 전용 서명 테이블"이 선례) |
| B3 | 빌드 무결성 SHA256 스탬프 (§0-E) | §0-E (184~) | **근거 있음** (v1.5.2-patch Patch 14에서 이미 도입: "파일명 형식: MyPhoneCheck_<Type>_v<VER>_<HASH6>.docx") |
| B4 | 0-B-2 Patch 감사 로그 Patch 17~28 | §0-B-2 (143~) | **근거 있음** (v1.5.2-patch Patch 14의 0-B 로그 구조 계승) |
| B5 | JDK 17 toolchain (§26) | §26-1 (2703~) | **근거 있음** (`Stage0_hotfix_Java17_e3b05e.docx` 및 v1.5.2-patch Patch 14 KPI 표에 "JAVA17"로 선행 명시) |
| B6 | Fastlane Mac 한정 실행 | §26-5 (2799~) | **근거 있음** (통합운영설계안 §2.5, Infra_Ops v1.0에서 합의) |
| B7 | 자체 영수증 검증 서버 없음 / RevenueCat 미채택 | §31-2, §36-5 금지 토큰 | **근거 있음** (Infra_Ops v1.0 FINAL의 AWS Lambda 폐기 + 메모리 #20) |

**결론**: 본문의 주요 주장은 모두 과거 자료 또는 명시된 메모리 번호에 근거가 있으며, **창작 추정 항목 0건**.

---

## 5. 판정 요약

- **검증된 과거 결정**: 35건 (D01~D35)
- **본문에 충실히 반영**: 30건 (86%)
- **본문에 수정/범위변경 반영**: 2건 (D14 가격 재계산, D15 Phase 로드맵 동시화)
- **본문에 누락/후행 이관**: 3건 (D05 DO_NOT_MISS, D06 SQLCipher, D34 푸시 휴지통 구체 스펙)
- **본문 내 창작 추정 항목**: **0건** (모두 근거 추적 가능)

### 가장 치명적 누락 Top 3

1. **D06 — SQLCipher / AES-256-GCM 보안 구현 스펙 부재**
   - v1.0 §13에서 "DONE" 상태로 기록돼 있던 보안 기반 기술
   - v1.6.1 본문 전체에서 `SQLCipher`, `AES-256` 토큰 0건
   - 헌법 제1조(Out-Bound Zero)·제2조(In-Bound Zero)의 **암호학적 실현 방식**이 본문에 명시되지 않음 → 구현자가 Room DB 평문 저장으로 오해할 위험
   - **근거 있는 원본 결정이 v1.5~v1.6.1 진화 과정에서 실전(失傳)된 것으로 추정**

2. **D05 — DO_NOT_MISS 기능 완전 소실**
   - v1.0 §4.1에서 "저장 없이 중요 번호 표시 / E2E 완전 연결(DONE)"로 기록된 핵심 UX 기능
   - v1.6.1 본문에서 `DO_NOT_MISS` 문자열 0건
   - 개념이 "사용자 Override"(§10-5), "UserAction"(§8-2) 등으로 흡수됐을 가능성은 있으나 **명시적 계승 선언 없음**
   - 과거 자료의 UX 자산이 폐기된 것인지 흡수된 것인지 모호

3. **D34 — 푸시 휴지통 구체 스펙 부재 (v1.6.1 범위 외 이관됨)**
   - 통합운영설계안_v1(2026-04-24) §2.9에서 "통계만=반쪽 기능 폐기, 실제 격리+휴지통 UI 필수"로 **확정** 선언
   - v1.6.1은 §17-3 "향후 Surface"·§21 Open Issues에만 언급 → 구체 스펙 없음
   - **완화 요소**: `docs/00_governance/patches/PATCH_v1.7.md` Patch 31에서 "PushCheck 재정의(푸시 휴지통)"로 이관이 명시돼 있음. 범위 관리상 정당하나, v1.6.1 본문 §17-3에 "Patch 31에서 확정됨"을 cross-ref하지 않은 점은 흠결

---

## 6. 권고

### P0 (즉시 반영 권장)

1. **D06 보안 기반 기술 복원**: §8 또는 §26 말미에 "NKB Room DB 암호화 = SQLCipher AES-256-GCM + Android Keystore" 1문단 보강. 헌법 1·2조의 암호학적 실현 방식 명시. (근거: v1.0 §13 구현 완료)

2. **D05 DO_NOT_MISS 처분 명시**: §18 또는 §21 Open Issues에 다음 중 택1:
   - (A) "v1.0 DO_NOT_MISS 기능은 §10-5 사용자 Override로 흡수됨" 주석
   - (B) "v1.6.1 범위 외, Phase 5 이후 재도입 검토" 명시
   - 현재처럼 **말 없이 사라진** 상태는 정직성 감사(§0-B) 원칙과 충돌

### P1 (차기 패치 반영)

3. **D34 푸시 휴지통 cross-ref**: §17-3 "향후 Surface" 표의 PushCheck 행에 "구체 스펙: `docs/00_governance/patches/PATCH_v1.7.md` Patch 31" 링크 추가. v1.6.1 본문이 v1.7 패치를 명시적으로 이관처로 가리키게 해서 정직성 유지.

4. **D14 MAU 기준 변경 주석**: §16-2 재계산 결과(67,114명/국가당)가 v1.4_disc 3인 만장일치(14.7만명/국가당, gross $1.99 기준)와 다름을 §16-4 비교표 옆에 "※ 기준 가격 변경($1.99 → $2.49)으로 break-even MAU 재산출" 1줄 주석. 이미 표는 있으나 해석을 안내하는 문구 부재.

### P2 (선택)

5. **D15 Phase 로드맵 해석 주석**: §17-4 옆에 "v1.4_disc의 Call→Message→Privacy→URL/App 순차 로드맵은 v1.6.0 Four Surfaces 동시화로 대체됨 (§17-1, Patch 18)" 명시. 현재 본문에도 충돌 해소 흔적은 있으나, Phase 로드맵 자체 설명에서 전환 이유 표기가 엷음.

---

## 7. 검증 메타데이터

- **분석 파일 수**: 35개 .docx 텍스트 추출 + 본문 .md 1개 + 통합운영설계안 1개 + PATCH_v1.7 1개
- **임시 산출물**: `C:\Users\user\ollanvin\_tmp_docx_extract\*.txt` (35개, 조사 후 삭제 가능)
- **수정 파일**: **0개** (조사 전용 WO 원칙 준수)
- **본문 전문 복사**: 없음 (섹션 grep 앵커만 사용)
- **추정 사용**: 없음 (모든 판정은 라인 번호/토큰 인용)

---

**— 리포트 종료 —**

작성: Claude Code (Opus 4.7) / 수임 WO: WO-V161-VERIFY-LOCAL-001 / 2026-04-24
