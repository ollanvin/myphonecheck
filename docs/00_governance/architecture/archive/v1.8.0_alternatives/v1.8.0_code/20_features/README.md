# 20_features/ — README

## 목적
Feature Layer — Four Surfaces 기능 본문 (CallCheck / MessageCheck / MicCheck / CameraCheck) + 스모크런 시나리오.

## 책임 범위
원본 §18 전문의 기능별 분할. §18-1~3 (CallCheck), §18-4 (MessageCheck), §18-6 (MicCheck), §18-7 (CameraCheck). §18 전체는 `25_smoke_scenarios.md`에 원본 보존.

## 외부 인터페이스
`06_product_design/04_system_arch.md` (상위 아키텍처), `07_engine/05_decision_formula.md` (Decision Engine 호출), `10_policy/*` (기능별 권한), `50_test_infra/02_smoke_scenarios.md` (테스트 관점 재배치).

## 내부 파일 안내
- `21_call.md` — CallCheck 관련 (§18 intro + §18-1,2,3 스모크런).
- `22_message.md` — MessageCheck 본문 (§18-4 전문, Mode A/B).
- `23_mic.md` — MicCheck 본문 (§18-6 전문).
- `24_camera.md` — CameraCheck 본문 (§18-7 전문).
- `25_smoke_scenarios.md` — §18 전문 (스모크런 11개 시나리오 + 기능 본문 통합).
