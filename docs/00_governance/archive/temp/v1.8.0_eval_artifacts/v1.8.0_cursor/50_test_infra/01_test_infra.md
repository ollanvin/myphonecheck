# 34. 테스트 인프라

**원본 출처**: v1.7.1 §34 (34-2~4) (3962–3963 + 3987–4010)
**v1.8.0 Layer**: Test
**의존**: `00_core/01_primary.md`
**변경 이력**: 본 파일은 v1.7.1 §34 (34-2~4) 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cursor/50_test_infra/01_test_infra.md`

---

# 34. 테스트 인프라

## 34-2. 테스트 Layer

- **Unit Test** (각 모듈 `src/test/kotlin`): JVM, 엔진 로직·Softmax·TierClassifier
- **Instrumentation Test** (`src/androidTest/kotlin`): 실기기·에뮬레이터, Room DB·BillingClient
- **UI Test** (Espresso + Compose UI Test): Surface 화면 렌더·상호작용
- **Smoke Test**: 11개 시나리오 자동 실행 (Day 6~13 완료 기준)

## 34-3. 필수 테스트 목록

| 테스트 | 검증 내용 | 실행 위치 |
|---|---|---|
| `FreezeMarkerTest` (22개) | Stage 0 5 파일 시그니처 불변 (Patch 37 통일) | CI 매 PR |
| `MigrationCompatTest` | v1 → v2 Room 마이그레이션 | CI 매 PR |
| `DecisionContractTest` | Decision Engine 계약 준수 | CI 매 PR |
| `UserConsentTest` | 권한 거부 시 graceful degradation | CI |
| `NkbSizeBudgetTest` | NKB 엔트리 평균 ≤ 2KB | CI nightly |
| `SmokeRun01~11` | 11개 스모크런 시나리오 | 릴리즈 전 매번 |
| `AccessibilityScannerTest` | TalkBack 통과 | 릴리즈 전 |
| `Verify*.sh` (5종) | 헌법 정합성 스크립트 | CI 매 PR |

## 34-4. 부록 A 위치

**삭제됨 (Patch 24).** 부록 A §A-3 (MicCheck 권한 정당화), §A-4 (CameraCheck 권한 정당화)는 본 섹션으로 흡수되었으며, "권한 요청 없음"이 원칙이므로 정당화 텍스트 자체가 불필요하다.
