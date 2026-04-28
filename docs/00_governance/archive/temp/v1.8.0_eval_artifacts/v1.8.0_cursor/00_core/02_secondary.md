# MyPhoneCheck Architecture v1.7.1 — One Engine, Four Surfaces

**원본 출처**: v1.7.1 §0 서문 (51–74)
**v1.8.0 Layer**: Core
**의존**: `00_core/01_primary.md`
**변경 이력**: 본 파일은 v1.7.1 §0 서문 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cursor/00_core/02_secondary.md`

---

# MyPhoneCheck Architecture v1.7.1 — One Engine, Four Surfaces

**캐노니컬 재작성본 — Device-Oriented Sovereignty Edition (Code-Ready)**

- **발행일**: 2026-04-24
- **작성**: 비전 (Vision)
- **검토 예정**: 헐크, 자비스, 스타크 3차 외부 검증 라운드 (190개국 동시 론칭 + 수익성 극대화 검증 집중)
- **승인 대기**: 대표님
- **버전 확정**: v1.7.1 (v1.7.0에 2차 외부 검증 라운드 유효 지적 반영, Patch 38 Play Integrity API classicRequest 추가, 2026-04-24 심야)

**본 문서의 세 기둥**:

1. **One Engine** — 단일 Decision Engine이 3계층(온디바이스 NKB + 외부 일반 검색 + 공공 공신력 DB) 신호를 받아 모든 판단을 수행한다.
2. **Four Surfaces** — CallCheck / MessageCheck / MicCheck / CameraCheck 네 표면이 동일 엔진을 공유한다.
3. **Device-Oriented Sovereignty** — 우리(올랑방)가 운영하는 중앙 서버·중앙 DB·중앙 매핑이 0. 디바이스가 스스로 판단한다.

**본 문서는 네 단계로 구성된다**:

- **1~22장**: 헌장 + 토론 합의 (v1.3 100% 계승, v1.4~v1.6 정정·신설)
- **23~26장**: 코딩 작업 전환 명세 (프로젝트 구조, Day-by-Day, strings.xml, CI/CD)
- **27~34장**: 외부 검증 정정 (권한 매트릭스, 국가/언어 분리, Memory Budget, Billing, Interface Injection, Store Policy, 테스트 인프라)
- **35~36장**: 인프라 운영 참조 + Four Surfaces 통합

---
