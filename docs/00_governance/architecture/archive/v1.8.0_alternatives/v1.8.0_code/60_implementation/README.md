# 60_implementation/ — README

## 목적
Implementation Layer — 구현 가이드·Stage 0 FREEZE·Interface Injection·Memory Budget·Repo Layout·CI/CD.

## 책임 범위
원본 §23, §24, §26, §30, §32, §33-1 Stage 0 전문. 어떻게 빌드·배포·동결하는가.

## 외부 인터페이스
`07_engine/*` (구현 대상), `10_policy/06_store_policy.md` (Store 정책 구현), `50_test_infra/*` (CI 테스트 통합).

## 내부 파일 안내
- `01_day_by_day.md` — §24. Day 0~14 구현 가이드.
- `02_stage0_freeze.md` — §33-1 Stage 0 Contracts 전문 + §23-4 FREEZE.md 블록.
- `03_interface_injection.md` — §32. Interface Injection (Patch 10).
- `04_memory_budget.md` — §30. Memory Budget.
- `05_repo_layout.md` — §23. Repository Layout 전문.
- `06_ci_cd.md` — §26. CI/CD (JDK 17).
