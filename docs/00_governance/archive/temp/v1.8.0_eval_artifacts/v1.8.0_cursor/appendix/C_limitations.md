## 0-C. 정책 모니터링 로그 (Policy Monitoring Log)

**원본 출처**: v1.7.1 §0-C~0-F (190–248)
**v1.8.0 Layer**: Appendix
**의존**: `00_core/01_primary.md`
**변경 이력**: 본 파일은 v1.7.1 §0-C~0-F 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cursor/appendix/C_limitations.md`

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

## 0-D. 검증 불가 한계 로그 (Limitations Log)

현재 시점에 검증할 수 없는 항목을 명시. 향후 실측으로 보완한다.

| 한계 | 내용 | 보완 시점 |
|---|---|---|
| 실기기 성능 | L3 NKB p95 ≤ 5ms 실측 미완 | Stage 1 이후 실기기 테스트 |
| 국가별 스토어 가격 | $2.49 Tier가 없는 국가 존재 가능 | Play Console 가격 설정 시 |
| 공공 API 응답 안정성 | KISA·경찰청 API 가용성 SLA 미확인 | 통합 개발 단계 |
| MacinCloud 연동 | iOS 빌드·서명·배포 파이프라인 실측 미완 | Stage 2 iOS 진입 시 |
| 외부 검증 라운드 | 헐크·자비스·스타크 본 재작성본 리뷰 미실시 | 대표님 승인 직전 |
| PACKAGE_USAGE_STATS 존폐 (2차 외부 검증, 자비스 지적) | 자비스 2차 라운드 R-05: "최근 사용 시각은 편의 기능, Play 심사 리스크 > 사용자 결정 품질"로 제거 권고. 본문 §27-3-5는 "반쪽 기능 금지" 원칙으로 Special Access 유지 중. 헌법 해석 충돌로 대표님 판단 보류. | v1.7.2 또는 Play 심사 실제 피드백 시점 |
| Billing 실패 로컬 로그 (2차 외부 검증, 스타크 지적) | 스타크 2차 라운드: `onPurchasesUpdated` OK 분기만 존재, 실패 시 CS 대응 데이터 없음. 로컬 로그 추가는 헌법 1·2조(In/Out-Bound Zero) 해석 충돌 + 서버 0 원칙상 CS 대응 불가 구조가 필연적 대가. 대표님 판단 보류. 당면 대안: "사용자가 Play Console로 직접 환불 요청" 공식 채널 선언. | v1.7.2 |
| Frida 런타임 메모리 후크 완전 방어 | Patch 38 Play Integrity API는 루팅·에뮬레이터·Frida Gadget 주입까지 탐지. 단 런타임 메모리 후크는 서버 측 재검증 없이 근본 해결 불가 (헌법 1조 "자체 서버 0"과의 트레이드오프). 알려진 한계. | 서버 도입 없는 한 보완 불가 |

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
