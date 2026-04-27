# Archive

본 디렉토리는 **거버넌스 역사 보존**용. 직접 참조용 아님.

## 보관 정책

- 정식 문서에서 제거된 내용 영구 보존
- 직접 수정 금지 (역사 기록 무결성)
- 새 정보 추가는 정식 문서(architecture/, infrastructure/)에만
- **삭제 금지** (의사결정 근거 영구 보존)

## 디렉토리 구조

| 디렉토리 | 용도 |
|---|---|
| `workorders/` | 완료된 단발 워크오더 산출물 |
| `patches/` | 구 패치 묶음 (Architecture Appendix B에 통합 완료) |
| `legacy_docx/` | 폐기된 docx 빌드 산출물 |
| `legacy_docs/` | 구버전 통합 문서 (현행 v1.7.1/v1.8.0으로 대체) |
| `temp/` | 임시 작업 폴더 |

## 정리 시리즈

본 archive는 거버넌스 루트 정리 시리즈로 구축:

- **PR-A** (PR #5, 머지 완료): __pycache__ 삭제 + .gitignore 강화
- **PR-B** (본 PR): archive/ 신설 + 명확한 이관 5건
- PR-C (예약): 백업 디렉토리 외부 이동 후 레포 삭제
- PR-D (예약): docs/ 하위 모호 항목 정리
- PR-E (예약): project-governance.md 분할 + docs-index.md 이동

## 작성

- 작성자: 비전 (WO-V180-CLEANUP-009-B)
- 작성일: 2026-04-27
