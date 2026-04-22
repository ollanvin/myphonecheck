# 워크오더: WO-CLEANUP-002 크로스체크 감사 (WO-AUDIT-001)

**대상 도구**: ChatGPT Codex CLI (Windows 로컬, PowerShell)
**작업 위치**: `C:\Users\user\Dev\ollanvin\myphonecheck`
**발행자**: 비전
**발행일**: 2026-04-22
**역할**: Claude Code가 수행한 WO-CLEANUP-002 결과를 **독립적으로 검증**

---

## 0. 전제 조건

- [ ] Claude Code가 WO-CLEANUP-002 완료 보고 후에 실행
- [ ] 리포 HEAD에 f7b0411 또는 그 이후 커밋 존재

---

## 1. 작업 목적

Claude Code의 청소 작업을 Codex CLI가 **독립 감사**. Claude Code 자기 보고를 신뢰하지 않고 처음부터 모든 항목 재검증.

검증 대상 8개:
1. 원격 브랜치 2개 삭제 완료 여부
2. logs/filter/ tracked 파일 제거 확인
3. .gitignore에 logs/ 규칙 존재
4. 커밋 f7b0411 본문 · 메시지 일치
5. 로컬 HEAD == origin/main
6. core:common 무결성 (변경 없어야 함)
7. 백업 폴더 존재 확인
8. 작업 트리 clean

---

## 2. 작업 절차

```powershell
cd C:\Users\user\Dev\ollanvin\myphonecheck
git fetch origin --prune
```

### 검증 1: 원격 브랜치 목록

```powershell
git branch -r
```
**PASS 조건**: `origin/HEAD -> origin/main`과 `origin/main` 두 줄만 출력. 다른 브랜치 0건.

### 검증 2: logs/filter/ tracked 파일

```powershell
git ls-tree -r HEAD | Select-String "logs/filter"
```
**PASS 조건**: 0건 매칭.

### 검증 3: .gitignore 규칙

```powershell
Select-String -Path .gitignore -Pattern "^logs/" -SimpleMatch
```
**PASS 조건**: 1건 이상 매칭.

### 검증 4: 커밋 내용

```powershell
git show f7b0411 --stat
git log -1 --format="%H%n%s%n%b" f7b0411
```
**PASS 조건**:
- 파일 33개 변경
- +4 insertions, -444 deletions
- 커밋 메시지에 "stop tracking logs/ and ignore going forward" 포함

### 검증 5: 로컬-원격 동기화

```powershell
git log -1 --format=%H
git log -1 --format=%H origin/main
```
**PASS 조건**: 두 SHA 동일.

### 검증 6: core:common 무결성

```powershell
git diff 96471c6..f7b0411 -- core/common/
```
**PASS 조건**: 출력 0줄 (core:common 변경 없어야 함).

### 검증 7: 백업 폴더

```powershell
Test-Path C:\Users\user\Dev\ollanvin\myphonecheck-backup-pre-filter-20260422
```
**PASS 조건**: True (1주일 보존 권고 이행).

### 검증 8: 작업 트리 clean

```powershell
git status --porcelain
```
**PASS 조건**: 출력 0줄.

---

## 3. 산출 보고

파일: `docs/07_relay/done/REPORT-WO-AUDIT-001__codex__done.md`

포맷:

```markdown
# Report: WO-AUDIT-001 — WO-CLEANUP-002 Cross-Check

**감사자**: Codex CLI
**완료일**: 2026-04-22

## 검증 매트릭스
| # | 검증 항목 | 결과 | 증거 |
|---|---|---|---|
| 1 | 원격 브랜치 | PASS/FAIL | (출력 일부) |
| 2 | logs/filter tracked | PASS/FAIL | ... |
| ... | ... | ... | ... |

## 종합 판정
- ALL PASS / {N}/8 FAIL
- 실패 항목 상세
- 권고 사항

## Claude Code 보고와의 차이
(Claude Code가 보고한 내용과 Codex가 직접 검증한 결과 비교)
```

---

## 4. 제약 사항

- **임의 수정 절대 금지** — 감사만. git add/commit/push 금지 (보고서만 별도로 커밋)
- Claude Code 보고 내용을 근거로 건너뛰지 말 것. 전부 재실행.
- 검증 명령 실패 시 그 자체가 감사 결과 (원인 분석 후 보고)

### 보고서 커밋 방법

보고서 파일만 git 추가:
```powershell
git add docs/07_relay/done/REPORT-WO-AUDIT-001__codex__done.md
git commit -m "audit(relay): Codex cross-check of WO-CLEANUP-002

See REPORT-WO-AUDIT-001 for verification matrix.

Refs: WO-AUDIT-001"
git push origin main
```

push 차단 시 대표님 옵션 A 요청.

---

## 5. 실패 시 대응

어느 검증이든 FAIL이면 즉시 중단하지 말고 **8개 항목 전부 실행 후 종합 보고**. 일부 실패가 있어도 감사관은 전수 기록이 임무.

FAIL이 1건 이상이면 `docs/07_relay/needs_approval/` 폴더에도 경고 파일 생성:

파일명: `ALERT-WO-AUDIT-001-FAILURES.md`
내용: 실패 항목 요약 + "대표님·비전 확인 필요"

---

## 끝
