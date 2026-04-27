## 0-B. 정직성 감사 로그 (Honesty Audit Log)

**원본 출처**: v1.7.1 §0-B (21줄)
**v1.8.0 Layer**: Appendix
**의존**: `appendix/B_patch_history.md` + `appendix/C_limitations.md`
**변경 이력**: 본 파일은 v1.7.1 §0-B (21줄) 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_codex/appendix/A_audit_log.md`

---


본 로그는 0-A의 약속이 본문·코드에서 실제 이행되었는지 추적한다.

| 약속 ID | 약속 내용 | 구현 증거 위치 | 검증 방법 | 검토자 |
|---|---|---|---|---|
| CONST-1 | Universal Truth Defense | §1-1 + §17 + §36 | 4 Surface 통합 테스트 PASS | 대표님 |
| CONST-2 | In-Bound Zero | §1-2 + §6-2/6-3 | `scripts/verify-network-policy.sh` (Phase 1 실행 예정, 현재 대기) | 비전, 헐크, 자비스 |
| CONST-3 | Out-Bound Zero | §1-3 + §6-2/6-3 | `scripts/verify-no-server.sh` (Phase 1 실행 예정, 현재 대기) | 비전, 헐크 |
| CONST-4 | 자가 작동 | §1-4 + §19 (권한 정책) | UserConsentTest (Phase 1 실행 예정) | 스타크 |
| CONST-5 | 정직성 | §1-5 + 0-B/0-C/0-D | 본 로그 자체 | 비전 |
| CONST-6 | 가격 정직성 | §1-6 + BillingManager.kt + §31 | Play Console net revenue (Phase 4 실측 대기) | 스타크 |
| CONST-7 | 디바이스 오리엔티드 거위 | §1-7 + §5 + §17 | 본사 매핑 0건 검증 체크리스트 (§15) | 비전 |
| SLA-14-2 | L3 NKB Hit p95 ≤ 5ms | §14-2 | SmokeRun11 PASS | 자비스 |
| KPI-16-2 | net ARPU $1.49 (gross $2.49) | §16-2 + BillingManager.kt | Play Console net revenue | 스타크 |
| MODEL-FREEZE | Data Model Frozen | §8-X | MigrationCompatTest PASS | 헐크 |
| CONTRACT-DEC | Decision Contract | §10-X | DecisionContractTest PASS | 헐크 |
| MEM-2KB | NKB 엔트리 ≤ 2KB | §30-X | Memory Profiler 측정 | 헐크 |
| STAGE0-FREEZE | Stage 0 5 파일 FREEZE (4 핵심 계약 + FreezeMarker 어노테이션) | §33-1-0, §33-1-1~§33-1-5 | FreezeMarkerTest PASS (22 tests, 22 시그니처) | 비전 |
| JAVA17 | JDK 17 toolchain | §26 | CI 빌드 PASS | Cursor |

