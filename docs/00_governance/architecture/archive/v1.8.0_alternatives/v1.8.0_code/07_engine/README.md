# 07_engine/ — README

## 목적
Engine Layer — 3계층 소싱·Self-Discovery·NKB·Analyzer·Decision Engine·Cold Start·자가 진화·SLA.

## 책임 범위
원본 §6, §7, §8, §9, §10, §11, §12, §14 전문. 온디바이스 엔진의 내부 알고리즘·데이터 구조·수식.

## 외부 인터페이스
`06_product_design/*` (엔진 출력이 UX 규격 충족), `10_policy/*` (엔진 동작은 권한 정책 준수), `50_test_infra/*` (엔진 검증 테스트), `60_implementation/*` (엔진 구현 순서).

## 내부 파일 안내
- `01_three_layer.md` — §6. 3계층 소싱 (Layer A/B/C).
- `02_self_discovery.md` — §7. 환경 자가 발견.
- `03_nkb.md` — §8. NumberKnowledge DB 설계.
- `04_analyzer.md` — §9. SearchResultAnalyzer (Tier A → Tier C).
- `05_decision_formula.md` — §10. Decision Engine 수식 (Softmax + Override).
- `06_cold_start.md` — §11. On-Device Bootstrap.
- `07_self_evolution.md` — §12. Self-Evolution.
- `08_sla.md` — §14. 비기능 / 디바이스 SLA 4단계.
