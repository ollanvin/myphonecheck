# HTML 주석 블록

**원본 출처**: v1.7.1 §0 머리말 (1–48 / 총4557줄)
**v1.8.0 Layer**: Core
**의존**: `00_core/01_primary.md`
**변경 이력**: 본 파일은 v1.7.1 §0 머리말 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cursor/00_core/01_primary.md`

---

<!--
================================================================================
MyPhoneCheck Architecture v1.7.0 — 캐노니컬 재작성본 (비전)
================================================================================
작성: 2026-04-24
작성자: 비전 (Claude 채팅, 설계·판정)
입력 자료:
  - MyPhoneCheck_Architecture_v1.5.1_7d23b4.docx (베이스라인, 4101줄)
  - MyPhoneCheck_Rebuild_WorkOrder_v1.5.2_435ef4.docx (Patch 09~16)
  - MyPhoneCheck_WorkOrder_v1.5.3-patch_Cursor_4cad42.docx (Patch 17)
  - MyPhoneCheck_WorkOrder_v1.6.0_FourSurfaces_212359.docx (Patch 18~22)
  - MyPhoneCheck_WorkOrder_v1.6.1-patch_Cursor_6827a2.docx (Patch 23~28)
  - MyPhoneCheck_CodingWorkOrder_Stage0_Contracts_f1a85c.docx (4 계약 FREEZE)
  - MyPhoneCheck_Stage0_hotfix_Java17_e3b05e.docx (Java 17 toolchain)
  - 메모리 (헌법 7조, 가격 $2.49/월, 3축 검색, Six Surfaces 승격, 반쪽 기능 금지 등)
  - MyPhoneCheck_Infrastructure_v1.0.md (인프라 운영 최종본, 쌍으로 참조)

페어 문서:
  - MyPhoneCheck_Architecture_v1.7.0.md (본 문서, 제품·설계 기준선)
  - MyPhoneCheck_Infrastructure_v1.0.md (인프라·운영 기준선)
  두 문서는 서로 참조하며, 충돌 시 본 문서(제품 설계) 우선.

한계 (정직 기록):
  - 본 문서는 비전 단독 재작성본이다.
  - Cursor 공식 build_architecture_v170.py 파이프라인 미실행.
  - 헐크·자비스 외부 검증 라운드 미실시.
  - SHA6 변조 방지 스탬프 미부여.
  정식 캐노니컬로 승격하려면 위 3건 완수 필요.
  현재 상태: "Working Canonical" (대표님 승인 기반 실무 기준선).

폐기 기록:
  - MyPhoneCheck_Architecture_v1.6.1_630dda.docx 는 승인본이 아니며
    2026-04-24 대표님 지시로 폐기 대상.
  - v1.6.1 계보(1차·2차 초안 + 코웍 87a9a3 + 630dda 파이프라인 미통과본)와
    구분하기 위해 2026-04-24 저녁 대표님 지시로 본 문서를 v1.6.2로 PATCH 승격.
  - v1.6.2에 7-워커 통합 평가 결과(P0 6건 + P1 8건) 반영하여
    2026-04-24 저녁 대표님 지시로 v1.7.0 MINOR 에스컬레이션 완료.
    §3·§17-3 표 구조 변경 + DecisionEngineContract 타입 재정의 등
    Semver MINOR 수준 변경 포함.
  - v1.7.0에 2차 외부 검증 라운드(자비스·헐크·스타크) 유효 지적 반영하여
    2026-04-24 심야 대표님 지시로 v1.7.1 PATCH 발행.
    Patch 38 Play Integrity API classicRequest 로컬 무결성 검증 추가.
    스타크 허위 지적(ActionType 잔존) 및 헌법 충돌 권고(PPP 티어 가격) 미반영.
    자비스 R-05 (PACKAGE_USAGE_STATS 제거) 및 스타크 R-01 (Billing 실패 로컬 로그)는
    헌법 해석 충돌로 §0-D 한계 로그 보류 기록.
  - 본 문서가 v1.7.1 최초 정식 수립본이다.
================================================================================
-->
