## 0-B-2. Patch 감사 로그 (Patch 17~46)

**원본 출처**: v1.7.1 §0-B-2 (159–188)
**v2.0.0 Layer**: Appendix
**의존**: `00_core/01_primary.md`
**변경 이력**: v1.7.1 §0-B-2 원본 → v1.8.0 이관 → v1.9.0 PATCH-39·40 (Six Surfaces) → v2.0.0 PATCH-41~45 (헌법 §8조 + §28 Initial Scan + §29 SIM-Oriented + §30 코어 엔진 + §17 정확화).
**파일 경로**: `docs/00_governance/architecture/v2.0.0/appendix/B_patch_history.md`

---

## 0-B-2. Patch 감사 로그 (Patch 17~46)

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
| **PATCH-39** | **PushCheck 정식 Surface 승격 (Stage 1 = 규칙 기반 휴지통)** | **§17-2 + §17-3 + §26 신설** | **Stage 1-001 cursor 구현 완료 (`feature/push-trash`)** | **Phase 후행 → 정식 Surface. NotificationListenerService + Room DB v12 + 휴지통 Compose UI. v1.9.0 시점 PushCheck = 규칙 기반 휴지통(앱/채널 차단). Decision Engine 통합은 Stage 2+ 후속 작업으로 위임 (§36-1 목표 스케치 + §36-3-A 참조).** |
| **PATCH-40** | **CardCheck 신규 Surface 신설** | **§17-2 + §17-3 + §27 신설** | **카드스펜드 별도 앱 폐기 결정에 따라 MyPhoneCheck 통합** | **순수 소비자(Pure Consumer) 모델. SMS/Push 재활용 거래 추출 (새 권한·외부 통신 0). 거래 추출 Surface로 분류 (Decision Engine 미사용). 사용자 대면 약속 문구 명시 (§27-11).** |
| **PATCH-41** | **헌법 제8조 SIM-Oriented Single Core 신설** | **05_constitution.md 제8조 신설** | **신규 헌법 조항 (8개 조항 체계)** | **국가·통화·전화번호 양식 단일 진실원 = SIM. UI 언어 3단 fallback. 제3조 v2.0.0 강화 주석 (코어 엔진 = 본 조 비대상). v1.x까지 명문화 누락분 정정.** |
| **PATCH-42** | **§28 Initial Scan 신설** | **20_features/28_initial_scan.md 신규** | **신규 H1** | **6 Surface 베이스데이터 일괄 구축. SimContext 우선 → 병렬 스캔 → 영구 저장. 사용자 동의·점진 갱신·재스캔. 헌법 1·2·3·8조 정합.** |
| **PATCH-43** | **§29 SIM-Oriented Single Core 신설** | **20_features/29_sim_oriented_core.md 신규** | **신규 H1** | **SimContext 데이터 클래스. UI 언어 3단 fallback (SIM → 시스템 → English). SIM 변경 추적 3-옵션 (적용/유지/초기화). SIM 부재 fallback + 명시.** |
| **PATCH-44** | **§30 :core:global-engine 신설** | **20_features/30_core_global_engine.md 신규** | **신규 H1** | **모든 Surface 단일 코어 사용 (One Core Engine). parsing/search/decision/sim-context 모듈 구조. 검색 3대 축 통합. v1.9.0 §17 위협 평가 한정 해석 정정. Stage 2-001~005 마이그레이션 표.** |
| **PATCH-45** | **§17 One Core Engine 정확화** | **06_product_design/05_product_strategy.md** | **본문 갱신** | **Decision Engine = 코어 내부 모듈 (decision/InputAggregator). 모든 Surface가 단일 코어 사용. §17-3-A 신규 정식 항목 표 (Initial Scan / SIM-Oriented / Core Engine). v1.9.0 위협 평가 한정 해석 정정.** |
| **PATCH-46** | **v2.0.0 정정 (Cursor CHECK-CODE 감사)** | **05_constitution.md §1-1, 06_product_design/05_product_strategy.md §17, 20_features/27_card.md §27-10, 20_features/30_core_global_engine.md §30-6, 95_integration/01_six_surfaces_integration.md §36-3-B** | **본문 갱신** | **Cursor CHECK-CODE Major-1·2 + Minor-1·2·3 정정. §17 표 'Stage 1-002 예정' → '구현 완료 (PR #14)'. §95 §36-3-B v1.9.0 'DE 미공유' 논지 폐기 + One Core 통합 재서술. §1-1 제목 7조 → 8조. §30-6 헌법 표 6조 가격 정직성 행 추가. §27-10 기준 헌법 v1.9.0 → v2.0.0 + 8조 SIM-Oriented 행 추가. v1.9.0 cp -r 후 잔존한 표현을 v2.0.0 본질(One Core, 헌법 §8조, PR #14 흡수)에 정합화.** |

**MAJOR 승격 기록 (2026-04-27 v2.0.0)**: Patch 41~45 반영 시점에 **v1.9.0 → v2.0.0 MAJOR 에스컬레이션**. 사유: 헌법 8조 신설(헌법 본문 변경 = SemVer MAJOR) + One Core Engine 본질 정확화 + SIM-Oriented Single Core 명문화 + Initial Scan + `:core:global-engine` 모듈 신설. v1.7.1·v1.8.0·v1.9.0 모두 FROZEN 보존, v2.0.0 신설. 코드 마이그레이션은 Stage 2-001~005 후속 PR.

| **PATCH-47** | **§30-3-A 4-Layer 데이터 모델 신설** | **20_features/30_core_global_engine.md** | **신규 H2** | **OS / MyPhoneCheck / 외부 캐시 / 외부 검색 4계층 분리. Layer 우선순위 흐름 + FeedType (SecurityIntelligence / GovernmentPublic / CompetitorApp / TelcoBlocklist) + CountryScope 정의.** |
| **PATCH-48** | **§30-4 검색 4축 정정 (경쟁 앱 추가)** | **20_features/30_core_global_engine.md** | **본문 갱신 + §30-4-4 신규** | **기존 3축(internal/external/public)을 4축으로 본질 확장. §30-4-4 Competitor Feeds (더콜·후후·뭐야이번호·Whoscall) 신규 — 사용자 신고 기반 풍부한 데이터셋, 옵트인 다운로드, 라이선스 정합 필수.** |
| **PATCH-49** | **§31 Real-time Action Engine 신규** | **20_features/31_realtime_action.md** | **신규 H1** | **수신 이벤트 발생 시 50ms 즉시 조치. CallScreeningService(setDisallowCall+setRejectCall+setSkipNotification) + SmsReceiver abortBroadcast + Push cancelNotification. Action 5종 (BLOCK/SILENT/TAG_DISPLAY/LABEL_DISPLAY/PASS). InputAggregator 즉시 응답 모드. 사용자 대면 약속.** |
| **PATCH-50** | **§32 Tag System 신규** | **20_features/32_tag_system.md** | **신규 H1** | **휘발성 메모 모델. TagEntity + TagPriority (REMIND_ME/PENDING/SUSPICIOUS/ARCHIVE). 부여 흐름 3유형 (수신 후 / 부재중 후 / 일괄). Real-time Action 통합. 일일 리마인드 옵션. :feature:tag-system + Room v15 entity.** |
| **PATCH-51** | **§17 Product Strategy v2.1.0 신규 항목 표** | **06_product_design/05_product_strategy.md** | **§17-3-B 신규** | **4-Layer / 검색 4축 / Real-time Action / Tag System 4 정식 항목 명문화. v2.1.0 = MINOR (헌법 8조 변경 없음) 명시.** |
| **PATCH-52** | **§95 §36-1-A2 v2.1.0 4-Layer + Real-time + Tag 다이어그램** | **95_integration/01_six_surfaces_integration.md** | **신규 H3** | **§36-1-A v2.0.0 코어 다이어그램 직후 v2.1.0 확장 다이어그램 추가. 4-Layer 박스 + RealTimeActionEngine + Tag System Layer 2 안 위치 + Competitor Feeds Layer 3-C 위치 표시.** |
| **PATCH-53** | **§27 CardCheck cross-ref §31·§32 추가** | **20_features/27_card.md §27-13** | **본문 갱신** | **카드 SMS 발신자에 Real-time Action(차단·태그 즉시 적용) + Tag System(수신 후 태그 부여) 적용 가능 명시.** |

**MINOR 승격 기록 (2026-04-28 v2.1.0)**: Patch 47~53 반영 시점에 **v2.0.0 → v2.1.0 MINOR 에스컬레이션**. 사유: 4-Layer 데이터 모델 + 검색 4축(Competitor 추가) + Real-time Action Engine + Tag System 4가지 핵심 통찰 명문화. 헌법 8조 변경 없음 → SemVer MINOR. v1.7.1·v1.8.0·v1.9.0·v2.0.0 모두 FROZEN 보존, v2.1.0 신설. 코드 구현은 후속 PR.

**MAJOR 승격 기록 (2026-04-27)**: Patch 39·40 반영 시점에 **v1.8.0 → v1.9.0 MAJOR 에스컬레이션**. 사유: Six Surfaces 명문화 (4 → 6) + Surface 정의 정정 (Engine 사용 단위 → Value Extraction Layer) + Producer/Consumer 모델 명시는 Semver MAJOR 수준 (§17-1 핵심 정의 변경). v1.7.1·v1.8.0은 FROZEN 보존, v1.9.0은 신설 디렉토리. 헌법 7조·데이터 모델 핵심 시그니처는 무변경.

**MINOR 승격 기록 (2026-04-24 저녁)**: Patch 37 반영 시점에 대표님 지시로 **v1.6.2 → v1.7.0 MINOR 에스컬레이션**. 사유: §3/§17-3 표 구조 변경 + DecisionEngineContract 타입명 재정의는 Semver MINOR 수준. PATCH 번호는 연속 유지, 버전만 MINOR 승격. 헌법·데이터 모델 핵심 시그니처는 무변경.

**버전 승격 기록 (2026-04-24 저녁, v1.6.1→v1.6.2)**: Patch 29~36을 모두 담은 2차 재작성 완료 시점에, 대표님 지시로 **v1.6.1 → v1.6.2 PATCH 승격**. 승격은 Patch 번호를 추가하지 않으며, 헌법·데이터 모델·Stage 0 FREEZE 시그니처 무변경. 승격 사유 및 기존 v1.6.1 산출물과의 구분은 §Z-1, §0-A 버전 매트릭스 참조.
