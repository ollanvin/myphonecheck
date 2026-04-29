# _legacy_pre_v230 — Pre-v2.3.0 Documentation Archive

본 디렉토리는 v2.3.0 SSOT 확정 이전(2026-04-27 이전) 작성된 문서 잔재 보관소입니다.

## 정책

- **신규 작업 금지**. 본 디렉토리 안 어떤 파일도 수정/추가하지 않습니다.
- **참조 전용**. 과거 의사결정 이력 또는 v1 시절 설계 추적 시 참조.
- **현재 SSOT**: `docs/00_governance/architecture/v2.3.0/` + `docs/00_governance/infrastructure/v1.3/`.
- **활성 운영 문서**: `docs/05_quality/`.

## 격리 배경

통신 자동화 홀딩(2026-04-27) 시점까지 다음 폴더가 docs/ 루트에 v2.3.0 SSOT와 공존하면서 비전/워커가 매 워크오더마다 어느 쪽이 정본인지 추론해야 하는 상황 발생. WO-V230-QUARANTINE-DEBRIS (PR #46)에서 일괄 격리.

## 격리된 항목

- `01_architecture/` — v1 시절 architecture 문서 (v2.3.0이 정본)
- `02_product/` — assets/, specs/ (v2.3.0 06_product_design/, appendix/ 가 정본)
- `03_engineering/` — DO_NOT_MISS_IMPLEMENTATION 등 (v2.3.0 60_implementation/ 가 정본)
- `04_operations/` — codex-work-instruction, relay_protocol_v1 (통신 자동화 시절, v2.x 무관)
- `06_history/` — discovery, archive, branchpoint (역사 기록)
- `07_relay/` — 죽은 자동화 5폴더 큐 시스템 (메모리 합의로 폐기)
- `ARCHITECTURE.md` — 역설계도 1회성 (v2.3.0 흡수)

## 추적성

각 파일의 git history는 100% 보존됩니다. `git log --follow docs/_legacy_pre_v230/<path>` 로 origin 추적 가능.
