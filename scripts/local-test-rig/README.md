# MyPhoneCheck 테스트 3층 구조

## 역할 분리 원칙

| 층 | 환경 | 역할 | 테스트 대상 |
|---|---|---|---|
| 1층 | VM | 빌드 + 정적 검증 + Frozen | CountrySearchRouterTest, SearchFallbackTest, SignalConflictResolutionTest, FrozenSnapshotValidationTest, EndToEndSearchIntegrationTest |
| 2층 | 로컬 PC | Live Validation + 에뮬레이터 | RealWorldValidationSetTest, NationalProviderEndToEndTest, 에뮬레이터 UI |
| 3층 | 테스트 리그 | 무인 반복 검증 | 전체 (장기 목표) |

## 원칙

- VM skip은 실패가 아님
- Live 성공 없이는 실전 검증 완료 선언 금지
- Frozen 없이는 회귀 검증 완료 선언 금지
- 둘 다 있어야 완료

## 스크립트

| 스크립트 | 용도 |
|---|---|
| `run_live_validation.ps1` | 10개 실전 번호 Live 검증 + fixture 저장 |
| `run_provider_capture.ps1` | 개별 번호 캡처 → Frozen fixture 편입 |
| `run_frozen_tests.ps1` | VM 역할 테스트 로컬 실행 |
| `run_emulator_callcheck.ps1` | 에뮬레이터 통합 테스트 |

## 사용법

```powershell
# 1) Live 검증만 (fixture 갱신 안 함)
.\run_live_validation.ps1

# 2) Live 검증 + Frozen fixture 자동 갱신
.\run_live_validation.ps1 -SaveFixtures

# 3) 특정 번호만 Live 검증
.\run_live_validation.ps1 -PhoneNumbers "15881234","1345"

# 4) 새 번호 캡처 → fixture 편입
.\run_provider_capture.ps1 -Phone "0312345678" -Category "kr-custom" -Label "새 번호"

# 5) Frozen 테스트 실행
.\run_frozen_tests.ps1

# 6) 에뮬레이터 통합 테스트
.\run_emulator_callcheck.ps1
```
