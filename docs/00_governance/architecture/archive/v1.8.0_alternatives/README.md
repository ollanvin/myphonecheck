# v1.8.0 Alternatives

본 디렉토리는 v1.8.0 마이그레이션 시 4워커 병렬 작업 결과 중
**최종 채택되지 않은 3개 워커 결과물**을 보존한다.

## 이관 일자

2026-04-27 (KST)

## 채택 결과

- **메인 채택**: `v1.8.0_cursor/` → `docs/00_governance/architecture/v1.8.0/`
  - 비전 채점 결과 1위 (98.76 / 100)
- **Cherry-pick 1**: `v1.8.0_cowork/INDEX.md` → `v1.8.0/INDEX.md`
  - 시각적 트리 우수
- **Cherry-pick 2**: `v1.8.0_code/_audit_report.md` → `v1.8.0/_audit_report.md`
  - 감사 가치, WO §6-3 특화 산출물

## 보존된 alternatives (탈락)

| 폴더 | 채점 점수 | 탈락 사유 |
|---|---|---|
| `v1.8.0_cowork/` | 97.93 | INDEX만 cherry-pick, 본문은 cursor 채택 |
| `v1.8.0_codex/` | 96.43 | §18·§27 일부 본문 변경 이관 (96.3% 무결성) |
| `v1.8.0_code/` | 92.73 | 크기 +31.7 KB 초과, _audit_report만 cherry-pick |

## 보관 정책

- 직접 참조 금지 (메인 채택본은 `v1.8.0/`)
- 부분 재활용 가능 (대표님 결정 시)
- 향후 4워커 병렬 작업 비교 학습 자료
- **삭제 금지** (의사결정 근거 영구 보존)

## 채점 리포트

`v180_4worker_evaluation.md` (비전 작성, 별도 보관)
