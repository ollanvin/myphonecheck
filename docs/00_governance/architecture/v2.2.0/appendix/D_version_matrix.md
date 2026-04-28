## 0-A. 버전 매트릭스 (Version Matrix)

**원본 출처**: v1.7.1 §0-A (78–121)
**v1.9.0 Layer**: Appendix
**의존**: `00_core/01_primary.md`
**변경 이력**: v1.7.1 §0-A 원본을 v1.8.0 이관 후, v1.9.0 MAJOR 승격 기록 추가 (Six Surfaces, PushCheck 정식, CardCheck 신설).
**파일 경로**: `docs/00_governance/architecture/v1.9.0/appendix/D_version_matrix.md`

---

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
| **v1.7.1** | **2026-04-24 심야** | **PATCH (FROZEN)** | **2차 외부 검증 라운드(자비스·헐크·스타크) 유효 지적 반영. Patch 38 신설 (Play Integrity API classicRequest 로컬 무결성 검증 추가). 스타크 지적 기반 크랙 방지 방어선 보강. v1.9.0 신설 후 FROZEN 처리.** |
| **v1.8.0** | **2026-04-27 오전** | **마이그레이션 (FROZEN)** | **4워커 병렬 작업 비교 결과 cursor 채택본 + cherry-pick (cowork INDEX, code _audit_report). v1.7.1 본문 텍스트 변경 0. 디렉토리 구조만 재정렬. v1.9.0 신설 후 FROZEN 처리.** |
| **v1.9.0** | **2026-04-27 오후** | **MAJOR (FROZEN)** | **Six Surfaces 명문화 (4 → 6). PATCH-39 PushCheck 정식 승격 (Stage 1-001 cursor 구현 완료 반영). PATCH-40 CardCheck 신규 신설 (카드스펜드 통합, Producer/Consumer 모델). Surface 정의 정정 (Engine 사용 단위 → Value Extraction Layer). v2.0.0 신설 후 FROZEN 처리.** |
| **v2.0.0** | **2026-04-27 저녁** | **본 통합본 (MAJOR)** | **One Core Engine + SIM-Oriented + Initial Scan. PATCH-41 헌법 §8조 SIM-Oriented Single Core 신설 (헌법 7조 → 8조). PATCH-42 §28 Initial Scan. PATCH-43 §29 SIM-Oriented Single Core. PATCH-44 §30 :core:global-engine. PATCH-45 §17 One Core Engine 정확화 (v1.9.0 위협 평가 한정 해석 정정). v1.7.1·v1.8.0·v1.9.0 모두 FROZEN 보존. 코드 마이그레이션은 Stage 2-001~005 후속.** |
| v3.0.0 | 미정 | MAJOR | iOS Edition (예정) |

## 0-A-1. 헌법 변경 추적

| 버전 | 헌법 변경 여부 | 변경 사항 |
|---|---|---|
| v1.0 ~ v1.5.2 | 변경 없음 | **6조 체계** (1~6조 텍스트 동일) |
| v1.5.3 ~ v1.9.0 | 신설 1건 | **7조 체계** — 7조(디바이스 오리엔티드 거위) 명문화, 기존 5조 내용을 조문으로 격상 |
| **v2.0.0** | **신설 1건 + 강화 1건** | **8조 체계** — **8조(SIM-Oriented Single Core) 신설** (PATCH-41), 3조 본문 v2.0.0 강화 주석 (코어 엔진 = 본 조 비대상) |
| v3.0.0 (예정) | 변경 가능 | iOS Edition 도입 시 일부 조항 보강 가능 |

**대표님 명시 승인 없이 헌법 변경 금지.** v2.0.0 헌법 변경은 대표님 비준 (2026-04-27).

## 0-A-2. 가격 정책 변경 이력

| 버전 | 가격 | 비고 |
|---|---|---|
| PRD 초안 | USD 1/월 | 비현실적 (수수료·세금 미반영) |
| v1.4_disc | net ARPU $1.19/월 | 계산 근거 문서화 |
| v1.5.x | ~1.5 USD 조항 | 폐기 |
| **v1.6.1** | **USD 2.49/월 단일** | **전세계 동일, 연간 가격 없음, 2026-04-22 확정** |

근거: Whoscall 신규 $2.89 / Hiya $3.99 / Truecaller $4.49 대비 최저가 포지셔닝이면서 Base Architecture ~1.5 USD보다 지속가능한 마진 확보. 경쟁 가격 조사(2026-04-22) 근거.

---

## 0-A-3. v2.1.0 신규 행 (Architecture 버전 매트릭스)

| 버전 | 발행일 | 종류 | 핵심 |
|---|---|---|---|
| v1.6.1 | 2026-04-22 | 재작성 | PRD 초안 + Patch 1~28 흡수 |
| v1.6.2 | 2026-04-24 | PATCH | Patch 29~36 통합 |
| v1.7.0 | 2026-04-24 저녁 | MINOR | §3/§17-3 표 구조 변경, DecisionEngineContract 타입명 |
| v1.7.1 | 2026-04-25 | PATCH | Patch 38 (안정화 정합) |
| v1.8.0 | 2026-04-26 | MAJOR | 헌법 7조 신설 (Device-Oriented Goose) |
| v1.9.0 | 2026-04-27 | MAJOR | Six Surfaces 정식 (4 → 6), Producer/Consumer |
| v2.0.0 | 2026-04-27 | MAJOR | 헌법 8조 신설 (SIM-Oriented), One Core Engine, Initial Scan, :core:global-engine |
| **v2.1.0** | **2026-04-28** | **MINOR** | **4-Layer 데이터 모델 + 검색 4축(Competitor 추가) + Real-time Action Engine + Tag System** |
| **v2.2.0** | **2026-04-28** | **MAJOR** | **헌법 §9 빅테크 정공법(7절: 언어·운영·출시·코드·위반·검증·비고) + §10 비전 자율 결정(5절) 신설 (8조 → 10조). §9-6 검증·테스트 정책 (Gradle Managed Devices + SIM 11개국 × Locale 11개 × 디바이스 4종 매트릭스). 비전 누적 위반 6건 영구 차단.** |
