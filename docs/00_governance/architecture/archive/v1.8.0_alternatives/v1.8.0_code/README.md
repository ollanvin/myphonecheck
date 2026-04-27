# v1.8.0_code/ — README

## 이 폴더의 정체성

본 폴더는 **WO-V180-MIGRATE-002의 Claude Code 워커 결과물**이다.
MyPhoneCheck Architecture v1.7.1 (단일 파일, 4,556줄, 216 KB) 원본을
v1.8.0 Layered 구조로 **기계적으로 재배치**한 것이다.

- **워커**: Claude Code (Anthropic — CLI)
- **작성 환경**: Windows 11 Pro (OS Version 10.0.26200), PowerShell 7 + Git Bash
- **Python**: 3.14.4 (자동화 스크립트 실행)
- **작성 일자**: 2026-04-24 (KST, 심야)
- **브랜치 예약**: `feature/v180-claudecode`
- **Auto Mode**: 활성 (WO §0-1, §6-3 요구)

## 원본 보존 약속

- 원본 텍스트 **한 글자도 변경하지 않았다**
- 요약·압축·의역 **수행하지 않았다**
- 각 파일 상단에 WO §4-4 표준 헤더 블록만 **추가**하였다
- 마크다운 헤더 레벨, 코드 블록, 표, 수식 전부 원본 그대로이다
- cross-ref (예: "§18-4-2 참조")는 원본 그대로 유지하였다 (v1.8.1 별도 WO 범위)

## 파일 형식 준수 (WO §4-7)

- **인코딩**: UTF-8 without BOM (모든 파일 검증 완료)
- **줄바꿈**: LF (Unix)
- **확장자**: .md 전용
- **마크다운 방언**: GFM
- **EOF newline**: POSIX 표준 준수

## 네이밍 규칙 준수 (WO §4-8)

- ASCII 소문자 + 숫자 + `_` + `.` 만 사용
- `INDEX.md`, `README.md` 관습 예외 유지
- 디렉토리 prefix: 2자리 숫자 + `_` + 목적 단어 (10 단위 gap)
- Appendix 네이밍: 대문자 A~E + `_` + 목적

## README 의무 준수 (WO §4-9)

각 하위 디렉토리에 README.md 1개 생성. 4요소 포함:
1. 목적
2. 책임 범위
3. 외부 인터페이스
4. 내부 파일 안내

## Claude Code 워커 특화 산출물

WO §6-3에 따라 본 폴더 루트에 다음 추가 산출물을 생성하였다:

- `_audit_report.md` — 무결성 감사 리포트 (원본 vs 결과물 텍스트 diff, grep 결과, 이상 징후)

## 타 워커와의 관계

본 폴더는 코웍·Cursor 결과물과 **병렬 비교 대상**이다.

- `v1.8.0_cowork/` (코웍 워커)
- `v1.8.0_cursor/` (Cursor 워커)
- **`v1.8.0_code/`** (본 워커)

대표님이 3 결과물을 비교한 뒤 `v1.8.0/`로 선택본을 copy한다 (본 WO 범위 밖).
본 워커는 WO §9에 따라 **타 워커 폴더 열람 금지**를 준수하였다.

## 통계 (실측)

- 총 .md 파일: 68 (이관 본문 55 + README 13)
- 총 크기: 304,906 B (약 297.76 KB)
- placeholder: 4 (비전 작성분 미수신분 — 00_core/01·02·03 + 10_policy/03_special_access)
- 빈 Layer: 1 (90_declarations, v1.8.1 예정)

상세 계측 및 검증 결과는 `_audit_report.md` 참조.

## 다음 단계

1. 대표님 3 워커 결과물 비교·선택
2. 비전이 선택본을 `v1.8.0/`로 copy
3. 비전 작성분 (00_core/01·02·03, 10_policy/03_special_access) 수신·삽입
4. v1.8.1 별도 WO에서 cross-ref 현대화

---

**작업 완료**: 2026-04-24 (KST)
**다음 작업 입력 대기**: 대표님 비교·선택 결과
