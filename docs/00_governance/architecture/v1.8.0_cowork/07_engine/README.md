# 07_engine

**목적**: Decision Engine 내부 설계. 3계층 소싱, NKB, 수식, Cold Start, SLA.

**책임 범위**: "어떻게 판단하는가"에 대한 엔진 내부 구현 명세.

**외부 인터페이스**: `20_features/`의 각 Surface가 엔진 계약을 호출. `60_implementation/02_stage0_freeze.md`에서 FREEZE 대상 정의.

**내부 파일 안내**:
- `01_three_layer.md` — 3계층 소싱 상세 (§6)
- `02_self_discovery.md` — Self-Discovery 환경 자가 발견 (§7)
- `03_nkb.md` — NKB 데이터 설계 (§8)
- `04_analyzer.md` — SearchResultAnalyzer (§9)
- `05_decision_formula.md` — Decision Engine 수식 (§10)
- `06_cold_start.md` — Cold Start 부트스트랩 (§11)
- `07_self_evolution.md` — 자가 진화 (§12)
- `08_sla.md` — 비기능 SLA 4단계 (§14)
