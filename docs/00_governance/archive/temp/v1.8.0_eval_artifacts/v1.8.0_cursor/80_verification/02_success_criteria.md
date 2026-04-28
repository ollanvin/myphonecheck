# 20. 성공 기준 (Success Criteria)

**원본 출처**: v1.7.1 §20 (2516–2550)
**v1.8.0 Layer**: Verification
**의존**: `00_core/01_primary.md`
**변경 이력**: 본 파일은 v1.7.1 §20 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cursor/80_verification/02_success_criteria.md`

---

# 20. 성공 기준 (Success Criteria)

## 20-1. Phase 1 (CallCheck MVP) 성공 기준

- Cold Start 평균 < 30초
- 착신 오버레이 p95 < 500ms
- NKB Hit p95 < 5ms (L3 기준선)
- SmokeRun01~05, 11 모두 PASS
- 사용자 수동 테스트 10건 이상 → 4속성 출력 자연스러움

## 20-2. Phase 2 (MessageCheck) 성공 기준

- SmokeRun06~07 PASS
- 사칭 SMS 시나리오 5건 이상 → HIGH Risk 탐지

## 20-3. Phase 3 (MicCheck / CameraCheck) 성공 기준 (Patch 30 정정)

- SmokeRun08~09 PASS
- 실기기 앱 30개 이상 스캔 → `MicPermissionEntry`·`CameraPermissionEntry` 리스트 생성 + 최근 사용 시각 표시 + 권한 회수 버튼 동작 확인
- ~~Justification 분류 정확도 확인~~ (Patch 30 삭제)
- ~~외부 이벤트 감지 (신규 설치·CVE 감지) 동작 확인~~ (Patch 31로 AppSecurityWatch Surface 이관, Phase 3 범위 외)

## 20-4. Phase 4 (Billing) 성공 기준

- SmokeRun10 PASS
- 테스트 카드 결제 → 구독 상태 로컬 반영 확인
- 만료 → 해제 자동 처리 확인

## 20-5. 글로벌 런칭 성공 기준

- Play Console 심사 통과 (차단 없이 첫 심사 통과)
- 190개국 중 최소 150개국 $2.49 Tier 설정 가능 확인
- 30일 Crashlytics Crash-free rate > 99.5%
- Play Console Vitals ANR < 0.47%
