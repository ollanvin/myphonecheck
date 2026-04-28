# v1.8.0 Evaluation Artifacts (Archived)

본 디렉토리는 2026-04-22 v1.8.0 마이그레이션 시 4워커 결과물 zip + cursor 채택본 폴더의 archive.

## Files

- `v1.8.0_code.zip` — Claude Code worker 결과물
- `v1.8.0_codex.zip` — Codex CLI worker 결과물
- `v1.8.0_cowork.zip` — Cowork worker 결과물
- `v1.8.0_cursor.zip` — Cursor worker 결과물 (1위 채택)
- `v1.8.0_cursor/` — Cursor 채택본 풀린 폴더
- `architecture_root_README.md` — architecture/ 루트 README (v1.8.0 평가 시점 문서, 정식 버전 디렉토리 정리 후 archive)

## 채점 결과 (메모리 #24)

- Cursor: 98.76점 (1위, 채택)
- Cowork: 97.93점 (2위)
- Codex: 96.43점 (3위)
- Claude Code: 92.73점 (4위)

본 결과는 v1.8.0 마이그레이션 단일 작업 사례. 이후 워커 분담 정책 변경 (메모리 #6).

## Why archived

- 2026-04-28 (Stage 2-010 통합 정리, PR #31) 시 architecture/ 루트에서 archive/temp/로 이관.
- 빅테크 정공법: 정식 버전 디렉토리(v1.7.1~v2.1.0)와 평가 보조물 분리.
- 채점 사례 참조 가치로 보존 (삭제 0).

## Successor

- 정식 산출물: `docs/00_governance/architecture/v1.8.0/` (Cursor 채택본 정식 디렉토리).
- 이후 v1.9.0 / v2.0.0 / v2.1.0으로 점진 발전.
