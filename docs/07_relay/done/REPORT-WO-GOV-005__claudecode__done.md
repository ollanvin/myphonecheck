# Report: WO-GOV-005 Architecture Patch v1.7

**완료일**: 2026-04-23
**실행자**: Claude Code (Auto Mode, Opus 4.7)
**커밋**: `289126a`
**Push**: 성공 (`7441390..289126a`, build status check bypass 경고)

## 변경 파일 (6개)

| # | 파일 | 변경 내용 |
|---|---|---|
| 1 | `docs/01_architecture/myphonecheck_base_architecture_v1.md` | §11 가격 조항 1줄 → 4줄 교체 (Patch 30); §6.3 Mic Check 외부 이벤트 불릿 추가 (Patch 32); §6.4 Camera Check 동일 불릿 추가 (Patch 32) |
| 2 | `docs/02_product/specs/PRD_CALLCHECK_V1.md` | §13 Pricing 6줄 → 1줄 압축 (Patch 30) |
| 3 | `docs/03_engineering/integration/SPEC_2026-04-14_core_rebuild_v2.md` | §3.3 P6 PushCheck 전체(통계 CTA 모델 76줄) → 푸시 휴지통 모델(31줄) 교체 (Patch 31) |
| 4 | `docs/01_architecture/global-single-core-system.md` | "## Search Evidence — 3축 모델" 섹션 신규 추가 (Patch 33) |
| 5 | `docs/00_governance/project-governance.md` | "In-repo architecture charter" 섹션 Stage 0 직후에 RiskLevel 이중 타입 정책 불릿 추가 (Patch 34) |
| 6 | `docs/00_governance/patches/PATCH_v1.7.md` | 신규 파일 (28줄, 패치 메타) |

## 삽입 위치 검증

- Base §11 가격: 정확히 "약 1.5달러 수준" 1줄만 교체. 기존 31일 과금 / 디바이스당 1회 등 다른 조항 보존
- PRD §13: 기존 6개 불릿 전체를 새 1줄로 압축. 외부 참조(`§11`)로 단일 출처 유지
- SPEC §3.3 P6: §3.3.1~§3.3.4 전체 교체. 인접 §3.2, §3.4 무손상
- Global Core: "## Country Policy Layer" 직전에 새 "## Search Evidence" 섹션 삽입
- Governance: Stage 0 불릿 직후에 RiskLevel 매퍼 불릿 1줄 삽입. 다른 빌드 이력 표 보존

## diff --cached --stat 검증
```
6 files changed, 77 insertions(+), 74 deletions(-)
```
타 파일 섞임 0건. WO-GOV-005 Step 9 기대값 일치.

## 다음 단계
- 본 워크오더 파일 `WO-GOV-005__claudecode__queue.md` → `done/` 이동 후 별도 commit/push (본 보고서와 함께)
- 비전 → Claude Code WO-CLEANUP-003 진행 지시 (이미 같은 세션에서 순서 3으로 예약됨)
