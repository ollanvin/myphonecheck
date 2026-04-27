# Workorders Archive

본 디렉토리는 **완료된 단발 워크오더** 또는 **outdated 워크오더**의 보존 위치.

## 구조

| 경로 | 용도 |
|---|---|
| `_workorder_stage0_hotfix_java17_e3b05e.txt` | Stage 0 Java 17 마이그레이션 핫픽스 (완료) |
| `discovery/` | DISCOVERY 시리즈 워크오더 (outdated) |

## 보관 정책

- 단발 완료 워크오더: 처리 후 즉시 archive 이관
- outdated 워크오더: 작성일로부터 시점이 지나 가치 소실 시 archive 이관
- 직접 참조 금지 (현행 작업은 `docs/07_relay/queue/`에서 발행)
- 삭제 금지 (의사결정 근거 영구 보존)

## DISCOVERY 시리즈 (2026-04-22)

작성: 2026-04-22
이관: 2026-04-27 (WO-V180-RELAY-CLEANUP-010)

| WO | 담당 | 목적 | 처리 결과 |
|---|---|---|---|
| DISCOVERY-003 | Claude Code | ollanvin 조직 5개 sub-repo 요약 | 미처리, outdated |
| DISCOVERY-004 | Codex CLI | myphonecheck 58 .md + web repo 정독 | done에 결과 보존 |
| DISCOVERY-005 | 코웍 | 대화록 2개 카탈로그화 | 미처리, 비전 conversation_search로 대체 가능 |

이관 사유: Pre-Stage 1 합성 보고서가 2026-04-22에 비준 완료된 후
v1.8.0 마이그레이션 + Stage 1-001 완료까지 진행되어 DISCOVERY 가치 소실.

## 관련 결과물 (별도 위치 보존)

- DISCOVERY-004 처리 결과: `docs/07_relay/done/WO-DISCOVERY-004__codex__done.md`
- DISCOVERY-004 보고서: `docs/07_relay/done/REPORT-WO-DISCOVERY-004__codex__done.md`
- 합성 보고서: `docs/06_history/archive/2026-04-imports/synthesis_2026-04-22_pre-stage1.md`

---

작성: 비전 (WO-V180-RELAY-CLEANUP-010)
작성일: 2026-04-27
