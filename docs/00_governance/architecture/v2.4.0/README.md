# README — MyPhoneCheck Architecture v2.4.0

**원본 출처**: v2.3.0 (헌법 §10-6 자체 머지 의무 신설 MINOR)
**v2.4.0 작업 성격**: §1 Out-Bound Zero 4축 명문화 MINOR 승격
**의존**: `00_core/01_primary.md`
**변경 이력**: v1.7.1 → v1.8.0 → v1.9.0 → v2.0.0 → v2.1.0 → v2.2.0 → v2.3.0 → v2.4.0
**파일 경로**: `docs/00_governance/architecture/v2.4.0/README.md`

---

## 목적

Architecture v2.3.0을 기반으로 v2.4.0 MINOR 승격(§1 Out-Bound Zero 검색 4대 축 명문화 + AI 검색 모드 + 경쟁사 Reverse Lookup 허용 명시) 결과를 담는 Working Canonical 디렉토리.

## 작성자

워커: Claude Code (WO-V230-CONST-V240-SEARCH).

## 비교

`v1.7.1/`, `v1.8.0/`, `v1.9.0/`, `v2.0.0/`, `v2.1.0/`, `v2.2.0/`, `v2.3.0/` 모두 FROZEN 보존. 본 `v2.4.0/`은 헌법 §1 검색 영역 명문화 + 결정 엔진 4축 가중치 spec + 6 Surface 직접 검색 버튼 spec를 담는 신규 디렉토리.

## 내부 구조

`INDEX.md`와 하위 디렉터리 README 참조. 78 파일 (v2.3.0 동일 구조 + 본문 보강).

## 핵심 변경 사항 (v2.3.0 → v2.4.0)

| 영역 | 변경 |
|---|---|
| §1 Out-Bound Zero | 검색 4대 축 명문화 (공공 / 외부 AI / 일반 검색 / 경쟁사 Reverse Lookup), Custom Tab 사용자 직접 진입 원칙, Perplexity Sonar 등 AI answer engine API 직접 통합 영구 금지 |
| `07_engine/05_decision_formula.md` | §10-formula-4axis 신설 (4축 가중치 표 + 합산 공식 + Tier 매핑 + Unknown Tier 특별 처리 + 6 Surface 적용) |
| `20_features/21~27` (6 Surface) | 각 Surface에 §direct-search 신설 (직접 검색 버튼 + 4축 메뉴 spec + Surface별 배치 위치) |
| 헌법 본문 표기 정합 | L15 "8개 조항" → "10개 조항" 정정 (v2.0.0 §8 / v2.2.0 §9·§10 신설 반영) |
