# README — MyPhoneCheck Architecture v2.7.0

**원본 출처**: v2.6.0 (3액션 단일 책임 MINOR)
**v2.7.0 작업 성격**: UTF-8 강제 + UTF-16 영구 금지 PATCH
**의존**: `00_core/01_primary.md`
**변경 이력**: v1.7.1 → v1.8.0 → v1.9.0 → v2.0.0 → v2.1.0 → v2.2.0 → v2.3.0 → v2.4.0 → v2.5.0 → v2.6.0 → v2.7.0
**파일 경로**: `docs/00_governance/architecture/v2.7.0/README.md`

---

## 목적

Architecture v2.6.0을 기반으로 v2.7.0 PATCH 승격(§9-7 코드 인코딩 정공법 신설 + CI 인코딩 검증) 결과를 담는 Working Canonical 디렉토리.

## 작성자

워커: Claude Code (WO-V270-CONST-UTF8-ENFORCE).

## 비교

`v1.7.1/` ~ `v2.6.0/` 모두 FROZEN 보존. 본 `v2.7.0/`은 UTF-8 (BOM 없음) 강제 + UTF-16 영구 금지 + CI 인코딩 검증 정공법을 담는 신규 디렉토리.

## 내부 구조

`INDEX.md`와 하위 디렉터리 README 참조. 78 파일 (v2.6.0 동일 구조 + §9-7 본문 정정).

## 핵심 변경 사항 (v2.6.0 → v2.7.0)

| 영역 | 변경 |
|---|---|
| §9-7 신설 | 모든 코드·설정·문서 파일 UTF-8 (BOM 없음) 강제. UTF-16 LE/BE, UTF-8 BOM, ANSI/EUC-KR/Windows-1252 영구 금지. |
| CI 인코딩 검증 | `.github/workflows/encoding-check.yml` 신설. PR/main push에서 비-UTF-8 및 BOM 검출 시 fail. |
| 워크오더 헤더 의무 | 모든 워크오더 §2 첫 줄에 UTF-8 의무 명시. |
| `.editorconfig` | root charset = utf-8 명시. IntelliJ / Android Studio / VSCode / Cursor 공통 적용. |
