# 19. 드라이런 체크리스트 (Dry Run Checklist)

**원본 출처**: v1.7.1 §19 (52줄)
**v1.8.0 Layer**: Verification
**의존**: `05_constitution.md`
**변경 이력**: 본 파일은 v1.7.1 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_code/80_verification/01_dry_run_checklist.md`

---

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
- [ ] Play Console에서 $2.49 Tier 설정 가능한 국가 확인

---
