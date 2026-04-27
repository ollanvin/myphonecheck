> **HISTORICAL WARNING (2026-04-27 추가)**
>
> 본 파일의 Patch 30~34는 **Architecture v1.7.1의 PATCH-30~34와 번호 충돌**한다.
>
> | 번호 | 본 파일 (구) | Architecture v1.7.1 (현행) |
> |---|---|---|
> | PATCH-30 | 가격 `$2.49` 단일화 | MicCheck/CameraCheck 단순 관리자 축소 |
> | PATCH-31 | PushCheck 재정의 | AppSecurityWatch Surface 신설 |
> | PATCH-32 | Mic/Camera 트리거형 | Data Safety 정직 재선언 |
> | PATCH-33 | 검색 3축 명문화 | Permissions Declaration 신설 |
> | PATCH-34 | RiskLevel 매퍼 | NKB 암호화 SQLCipher |
>
> **본 파일의 모든 변경 내용은 Architecture v1.7.1에 다른 번호로 흡수 완료**.
> 직접 참조 금지. 역사 기록용으로만 보존.
>
> 정확한 패치 이력은: `docs/00_governance/architecture/v1.8.0/appendix/B_patch_history.md`
>
> ---
# Architecture Patch v1.7

**기준**: v1.6.1 (Patch 23~28 통합)
**패치 ID**: v1.7
**적용일**: 2026-04-22
**발행자**: 비전
**반영자**: Claude Code (WO-GOV-005)
**승인**: 대표님 (founder@idolab.ai)

## Patch 30 — 가격 $2.49 단일화
Base §11 가격 조항 변경, PRD §13 가격 참조로 전환.

## Patch 31 — PushCheck 재정의 (푸시 휴지통)
SPEC v2.1 §3.3 P6 폐기, 푸시 휴지통 모델로 대체.

## Patch 32 — Mic/Camera 외부 이벤트 (트리거형)
Base §6.3-6.4에 외부 보안 사고 이력 표시 추가. 폴링 배치 금지.

## Patch 33 — 검색 3축 명문화
global-single-core-system.md에 L1/L2/L3 모델 추가.

## Patch 34 — RiskLevel 매퍼 정책
project-governance.md에 이중 타입 정책 추가.

## 이전 버전 대비 삭제 항목
- Base §11: "약 1.5달러 수준" 조항
- PRD §13: "USD 1/month" 조항
- SPEC v2.1 §3.3: P6 "통계 + 차단 CTA" 전체 섹션
