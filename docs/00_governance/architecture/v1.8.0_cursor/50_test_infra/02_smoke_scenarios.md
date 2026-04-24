## 18-1. 스모크런 11개 시나리오

**원본 출처**: v1.7.1 §18-1 + §19 + §20 (1851–1865 + 2464–2511 + 2516–2550)
**v1.8.0 Layer**: Test
**의존**: `00_core/01_primary.md`
**변경 이력**: 본 파일은 v1.7.1 §18-1 + §19 + §20 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cursor/50_test_infra/02_smoke_scenarios.md`

---

## 18-1. 스모크런 11개 시나리오

| # | 이름 | 목적 | Surface | SLA 레벨 |
|---|---|---|---|---|
| SmokeRun01 | 기본 Cold Start | 설치 직후 Day 0 부트 | 공통 | L1 |
| SmokeRun02 | 착신 오버레이 | 전화 수신 시 4속성 표시 | CallCheck | L1 |
| SmokeRun03 | Softmax 분포 | 신호 100개에서 topConfidence 산출 | 엔진 | L1 |
| SmokeRun04 | 사용자 Override | "안심 표시" 후 NKB 재계산 | 엔진 | L1 |
| SmokeRun05 | 연락처 상호작용 | 연락처 등록 번호는 SAFE 초기값 | Cold Start | L1 |
| SmokeRun06 | MessageCheck 3중 평가 | 발신번호 + URL + 기관명 평가 결합 | MessageCheck | L1 |
| SmokeRun07 | MessageCheck 시나리오 | "쿠팡 배송 알림" 사칭 SMS 검출 | MessageCheck | L1 |
| SmokeRun08 | MicCheck 기본 | RECORD_AUDIO 보유 앱 스캔 + 평가 | MicCheck | L1 |
| SmokeRun09 | CameraCheck 기본 | CAMERA 보유 앱 스캔 + 평가 | CameraCheck | L1 |
| SmokeRun10 | Billing 주기 | 구독 구매 → 상태 업데이트 → 만료 감지 | Billing | L1 |
| SmokeRun11 | L3 Offline 기준선 | 비행기 모드 + NKB 200건에서 p95 ≤ 5ms | 공통 | **L3** |
# 19. 드라이런 체크리스트 (Dry Run Checklist)

스모크런 이전에 **비전·커서·대표님이 함께 점검**하는 체크리스트.

## 19-1. 코드 기준 체크리스트

- [ ] Stage 0 4 계약(IdentifierType·RiskKnowledge·Checker·DecisionEngineContract) 시그니처 변경 없음
- [ ] `ExtractedSignal`에 rawSnippet·sourceProvider 없음
- [ ] `NumberKnowledge`에 Frozen 필드 전량 존재
- [ ] `scripts/verify-no-server.sh` PASS
- [ ] `scripts/verify-network-policy.sh` PASS
- [ ] `scripts/verify-no-mapping.sh` PASS
- [ ] `scripts/verify-frozen-model.sh` PASS
- [ ] `scripts/verify-strings-i18n.sh` PASS
- [ ] Detekt `ForbiddenRawField` 규칙 PASS
- [ ] JDK 17 toolchain 선언 (`build.gradle.kts`)
- [ ] 42 필수 토큰 전부 포함 (본 문서 §36-4 기준)
- [ ] 9 금지 토큰 0건 (본 문서 §36-5 기준)

## 19-2. 문서 기준 체크리스트

- [ ] 버전 매트릭스(0-A) 현재 버전 포함
- [ ] 헌법 변경 추적(0-A-1) 누락 없음
- [ ] 가격 정책(0-A-2) $2.49 반영
- [ ] 정직성 감사 로그(0-B) 14개 약속 전부 추적 가능
- [ ] Patch 감사 로그(0-B-2) 17~28 전부 기록
- [ ] 검토자 지적(0-B-1) 채택·거부 명시
- [ ] 한계 로그(0-D) 검증 불가 항목 명시
- [ ] 폐기된 황금알 사고(§2-2) 실명 기록

## 19-3. UX 기준 체크리스트

- [ ] Four Surfaces 모두 FourAttributeCard 규격 준수
- [ ] 다크/라이트 모드 전환 확인
- [ ] 다국어: ko/en/ja 최소 3개 locale 빌드 확인
- [ ] TalkBack 접근성 스캔 통과 (Accessibility Scanner)
- [ ] 최소 터치 48dp
- [ ] 온보딩 4개 슬라이드 + 권한 설명

## 19-4. 스토어 기준 체크리스트

- [ ] Play Console Data Safety 신고 (§27 참조)
- [ ] 민감 권한(READ_CALL_LOG·READ_SMS·READ_PHONE_STATE) 사용 사유 설명 등록
- [ ] 타겟 SDK 34 이상
- [ ] 64bit 네이티브 라이브러리 (현재 Kotlin만이라 해당 없음)
- [ ] Billing Library v7 사용
- [ ] Play App Signing 등록
- [ ] 개인정보처리방침 URL 등록
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
