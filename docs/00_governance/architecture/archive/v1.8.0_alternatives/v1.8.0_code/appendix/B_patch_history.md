# 0-C·0-E·0-F. 정책 모니터링 + 빌드 무결성 + 인프라 운영 참조

**원본 출처**: v1.7.1 §0-C 정책 모니터링 로그 + §0-E 빌드 무결성 SHA256 + §0-F 인프라 운영 참조
**v1.8.0 Layer**: Appendix
**의존**: `appendix/A_audit_log.md` + `95_integration/02_infrastructure_reference.md`
**변경 이력**: 본 파일은 v1.7.1 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_code/appendix/B_patch_history.md`

---

## 0-C. 정책 모니터링 로그 (Policy Monitoring Log)

외부 요인(OS/스토어/법률)이 본 설계에 영향을 미치는 항목을 추적.

| 항목 | 모니터링 대상 | 주기 | 담당 |
|---|---|---|---|
| Android OS 버전 | Play 최소 SDK 요구 상승 | 분기 | Cursor |
| Google Play 정책 | CallScreeningService, Default SMS, QUERY_ALL_PACKAGES 정책 변경 | 분기 | Cursor |
| App Store 정책 | CallKit·CallDirectory·영수증 검증 요구 | 분기 | Cursor |
| GDPR·CCPA·PIPA | 데이터 수집·전송·동의 요구 변경 | 반기 | 비전 |
| 공공 API | KISA·경찰청·금감원 스팸 DB 공개 여부 | 분기 | 비전 |
## 0-E. 빌드 무결성 (SHA256 스탬프)

v1.6.1까지의 산출물은 공식 빌드 시 자동 SHA256 해시가 파일명에 부여된다.

파일명 형식: `MyPhoneCheck_<Type>_v<MAJOR>.<MINOR>.<PATCH><suffix>_<HASH6>.docx`

예시:
- `MyPhoneCheck_Architecture_v1.6.1_{HASH6}.docx` ← Cursor 파이프라인 산출 시
- `MyPhoneCheck_Patches_v1.6.1-patch_{HASH6}.docx` ← Patch 23~28 독립 묶음

빌드 스크립트: `scripts/build_architecture_v170.py` (Cursor 담당)
검증 스크립트: `scripts/verify-doc-hash.ps1`

**본 문서 상태**: SHA6 미부여. 비전 재작성본이므로 파일명 `MyPhoneCheck_Architecture_v1.7.0.md` 고정. 정식 발행 시 Cursor 파이프라인 재실행으로 `.docx + HASH6` 산출.
## 0-F. 인프라 운영 참조

본 문서는 **제품·설계 기준선**이다. 이와 쌍을 이루는 **인프라·운영 기준선**은 별도 문서로 관리한다.

- 파일: `MyPhoneCheck_Infrastructure_v1.0.md`
- 위치: `docs/00_governance/`
- 상태: v1.0 FINAL (2026-04-24)

본 문서가 제품 기능·데이터 모델·알고리즘·UX를 정의하고, Infrastructure 문서가 도구맵·보관 경로·비밀값 SOP·실행 순서를 정의한다. 두 문서는 서로 참조하며 충돌 시 다음 규칙을 따른다:

- 제품 설계 결정(기능 범위·헌법) → 본 문서 우선
- 인프라 결정(도구 선택·경로·SOP) → Infrastructure 문서 우선
- 충돌 시 비전이 대표님에게 확인 후 정정 (Rule 3)

자세한 내용은 §35를 참조.

---
