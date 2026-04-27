# 20_features

**목적**: Four Surfaces 기능 본문. CallCheck, MessageCheck, MicCheck, CameraCheck 각각의 상세 구현 명세.

**책임 범위**: 각 Surface의 입력·출력·구현·권한·스모크런 시나리오.

**외부 인터페이스**: `07_engine/`의 엔진 계약 호출. `10_policy/`의 권한 정책 준수. `50_test_infra/`에서 테스트 시나리오 참조.

**내부 파일 안내**:
- `21_call.md` — CallCheck 전화 위험도 판정 (§18 중 CallCheck)
- `22_message.md` — MessageCheck 문자 위험도 판정, Mode A/B (§18-4)
- `23_mic.md` — MicCheck RECORD_AUDIO 보유 앱 감시 (§18-6)
- `24_camera.md` — CameraCheck CAMERA 보유 앱 감시 (§18-7)
- `25_smoke_scenarios.md` — 스모크런 시나리오 + Four Surfaces 본문 전체 (§18)
