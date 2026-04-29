# README — MyPhoneCheck Architecture v2.5.0

**원본 출처**: v2.4.0 (§1 Out-Bound Zero 검색 4축 명문화 MINOR)
**v2.5.0 작업 성격**: 검색 4축 → 2축 단순화 MINOR 승격
**의존**: `00_core/01_primary.md`
**변경 이력**: v1.7.1 → v1.8.0 → v1.9.0 → v2.0.0 → v2.1.0 → v2.2.0 → v2.3.0 → v2.4.0 → v2.5.0
**파일 경로**: `docs/00_governance/architecture/v2.5.0/README.md`

---

## 목적

Architecture v2.4.0을 기반으로 v2.5.0 MINOR 승격(§1 검색 4축 → 2축 단순화 + SimAiSearchRegistry 신설 + SearchInput sealed class 신설) 결과를 담는 Working Canonical 디렉토리.

## 작성자

워커: Claude Code (WO-V250-CONST-2AXIS).

## 비교

`v1.7.1/`, `v1.8.0/`, `v1.9.0/`, `v2.0.0/`, `v2.1.0/`, `v2.2.0/`, `v2.3.0/`, `v2.4.0/` 모두 FROZEN 보존. 본 `v2.5.0/`은 검색 4축 → 2축 단순화 + SIM 기준 AI 후보 자율 결정 + N Inputs 정합을 담는 신규 디렉토리.

## 내부 구조

`INDEX.md`와 하위 디렉터리 README 참조. 78 파일 (v2.4.0 동일 구조 + 본문 정정).

## 핵심 변경 사항 (v2.4.0 → v2.5.0)

| 영역 | 변경 |
|---|---|
| §1 Out-Bound Zero | 검색 2축 매핑 (NKB 0.40 + AI 검색 0.60). SIM 기준 AI 검색 후보군 (SimAiSearchRegistry, 최소 2개 보장 + 사용자 자율). SearchInput sealed class 명시. AI Mode가 (구) 공공 + 경쟁사 reverse 자체 통합. |
| `07_engine/05_decision_formula.md` | §10-formula-4axis → §10-formula-2axis 정정 (가중치 NKB 0.40 + AI 검색 0.60). N Inputs 정합 추가. |
| `20_features/30_core_global_engine.md` | §30-4 검색 4대 축 → 검색 2축 정의. (구) 4축 폐기 명시. SearchInput sealed class 추가. |
| `20_features/21~27` (6 Surface) | §direct-search-* 본문 정정 — 4축 메뉴 트리 → SIM 기준 AI 후보 메뉴 (최소 2개) + 사용자 자율 결정. |
| 폐기 (영구 금지) | 4축 분리 가중치 합산 / 경쟁사 Reverse Lookup 별도 통합 / AI Mode 우선순위 고정 |
