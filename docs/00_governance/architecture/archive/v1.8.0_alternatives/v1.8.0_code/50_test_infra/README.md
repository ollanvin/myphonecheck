# 50_test_infra/ — README

## 목적
Test Infra Layer — 테스트 계층·권한 매트릭스·스모크런 시나리오 (테스트 관점).

## 책임 범위
원본 §34 전문 + §34-1 권한 매트릭스 + §18 스모크런(SmokeRun01~11) + §19 드라이런 + §20 성공 기준.

## 외부 인터페이스
`80_verification/*` (검증 기준), `20_features/*` (기능 본문), `60_implementation/06_ci_cd.md` (CI 통합).

## 내부 파일 안내
- `01_test_infra.md` — §34 테스트 인프라 전문 (Unit/Integration/E2E 3 Layer).
- `02_smoke_scenarios.md` — SmokeRun01~11 (§18 H1 + §18-1,2,3,9,10) + §19 드라이런 + §20 성공 기준.
- `03_permission_matrix.md` — §34-1 권한 매트릭스 (Patch 23·36·37).
