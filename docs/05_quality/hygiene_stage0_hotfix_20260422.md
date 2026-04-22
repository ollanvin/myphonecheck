# Stage 0-hotfix Repository Hygiene 보고서

## 메타
- 실행자: 비전 (Claude Code, Opus 4.7, Auto Mode)
- 발행자: 비전 채팅 (Claude Opus 4.7)
- 승인자: 대표님
- 실행 시각 (KST, ISO 8601): 2026-04-22T19:00:09+09:00 시작 / 2026-04-22T20:45:00+09:00 에스컬레이션
- 작업 디렉터리: C:\Users\user\Dev\ollanvin\myphonecheck
- 커밋 SHA (before): cca259cc92e88bb6ca4a8656726320d5f9d82d50 (로컬 HEAD, origin 대비 +1)
- 커밋 SHA (after):  a2eec04a1606bd93de6b9af3624851f85287fade (로컬 HEAD, origin 대비 +2)
- 원격 푸시 SHA: **미반영** — push 거부됨 (본 보고서 §5 참조)

## 1. 결론

**부분 완료 / 푸시 차단 (ESCALATION)** — 로컬 저장소 위생 복구는 완료되었으나 원격 푸시는 GitHub pre-receive hook에 의해 거부되었다. 거부 원인은 본 보완 작업 범위 밖의 이전 커밋 `cca259c` 에 포함된 4개의 JVM heap dump(.hprof, 각 ~700MB)가 GitHub 100MB 파일 한도를 초과하기 때문이다. 워크오더 Section 12가 rebase 및 force push를 금지하므로 비전은 이 지점에서 작업을 중단하고 대표님 판단을 대기한다.

- 로컬 working tree: clean (stash 격리분 제외)
- 로컬 commit a2eec04: 작성 완료, 검증 통과 (127 files, 6153 insertions, 67 deletions)
- 원격 push: 실패 (상세 §5)
- Stash: 1건 (Category C 잔재 격리)

## 2. 변경 요약

| 범주 | 처리 | 파일 수 |
|------|------|--------|
| A — Stage 0-hotfix 필수 (Java 17, Foojay, core/common, CI freeze gate, governance.md, workorder, audit, evidence logs) | commit 포함 | 126 |
| B — .gitignore 보강 (`.claude/`) | commit 포함 | 1 (.gitignore) |
| C — push-intercept 제거 리팩터링 · 타 workorder · 아키텍처 산출물 등 | stash 격리 | 58 (22 수정 + 36 untracked) |

상세 분류: `logs/hygiene/classification.md` (A/B/C 각 파일별 근거 포함)

## 3. 커밋 해시

```
a2eec04a1606bd93de6b9af3624851f85287fade
```

커밋 메시지 (전문):
```
Stage 0-hotfix: Java 17 migration + core:common freeze + repository hygiene

- Java 17 toolchain unified across all modules (Gradle 8.6 / AGP 8.4.0 / Kotlin 2.0.0)
- Foojay resolver convention v0.9.0 added to settings.gradle.kts and build-logic
- core/common module committed (previously untracked) - Stage 0 contract freeze baseline
- .github/workflows/contract-freeze-check.yml tracked + UTF-8 encoding normalized
- Audit report: docs/05_quality/audit_stage0_hotfix_java17_20260422.md
- Audit evidence: logs/ (55 files)
- .gitignore updated: .claude/

Audit verdict: PARTIAL (Java 17 PASS, repository hygiene now complete)
Vision final verdict pending post-hygiene re-verification.
```

커밋 내용 검증 (모두 PASS):
- core/common/src/** 14개 파일 전부 포함
- core/common/build.gradle.kts, core/common/FREEZE.md 포함
- .github/workflows/contract-freeze-check.yml 포함 (UTF-8 정규화 후)
- Java 17 범주 A 파일 전부 포함
- Category C 누출 0건 (logs/hygiene/categoryC_leak_check.txt)
- build 산출물(`build/`, `.gradle/`, `.class`, `.jar`) 누출 0건 (logs/hygiene/build_artifact_leak.txt)

## 4. 상세 로그 인덱스 (logs/hygiene/)

| 파일                                   | 용도                                                        |
| -------------------------------------- | ----------------------------------------------------------- |
| git_head_before.txt                    | 작업 시작 시점 HEAD SHA (cca259c)                             |
| git_log_before.txt                     | 작업 시작 시점 recent 10 커밋                                  |
| git_status_before.txt                  | 작업 시작 시점 git status (전체)                               |
| git_status_porcelain_before.txt        | 작업 시작 시점 porcelain status                                |
| git_fetch.txt                          | origin fetch 결과                                            |
| remote_sync_before.txt                 | origin 대비 ahead/behind 상태                                 |
| branch_status.txt                      | 로컬 브랜치 tracking 상태                                      |
| untracked_files.txt                    | 작업 시작 시점 untracked 115건                                 |
| modified_files.txt                     | 작업 시작 시점 modified 50건                                   |
| staged_files.txt                       | 작업 시작 시점 staged 0건                                      |
| classification.md                      | A/B/C 분류표 (전체 173 파일 + 근거)                             |
| gitignore_core_check.txt               | .gitignore가 core/common 배제 안 함 확인                        |
| core_common_inventory.txt              | core/common 하위 13 파일 인벤토리                               |
| core_common_add.txt                    | core/common git add 로그                                      |
| core_common_staged.txt                 | core/common 14 staged 파일 확정                                |
| freeze_yml_bom_check.txt               | contract-freeze-check.yml 인코딩 검사 (UTF-16LE → UTF-8 변환)   |
| freeze_yml_syntax.txt                  | YAML 구조 검사 (탭 없음 + 섹션 구조 확인, PyYAML 미설치)           |
| freeze_yml_add.txt                     | contract-freeze-check.yml git add 로그                        |
| freeze_yml_staged.txt                  | CI 워크플로 staged 확정                                         |
| categoryA_add.txt                      | Category A 파일 일괄 add 로그                                   |
| gitignore_logs_check.txt               | .gitignore의 logs/ 패턴 검사 (없음 → 커밋 대상)                   |
| logs_add.txt, logs_hygiene_add.txt     | logs/, logs/hygiene/ 단계별 add 로그                            |
| gitignore_add.txt                      | .gitignore 변경 (.claude/ 추가) add 로그                         |
| unstaged_modified_after_stash.txt      | 첫 stash 시도 후 남은 unstaged 진단                              |
| stash_contents.txt                     | 최종 stash 내용 stat (Category C + index mirror)                  |
| stash_list.txt                         | stash 목록 (1건)                                              |
| categoryC_leak_check.txt               | Category C 누출 검사 (0건)                                    |
| build_artifact_leak.txt                | build/.gradle/.class/.jar 누출 검사 (0건)                      |
| staged_final.txt                       | 최종 staged diff stat (127 파일)                               |
| push_result.txt                        | **git push 거부 원문 (§5 핵심 증거)**                          |
| hprof_history.txt                      | .hprof 파일이 포함된 커밋 추적 (cca259c)                         |
| git_status_final.txt                   | 커밋 후 git status (ahead 2, untracked 감사증거만)                |

## 5. 푸시 차단 — 에스컬레이션 (Section 13 트리거)

### 5.1 현상

`git push origin HEAD` 실행 결과:

```
remote: error: Trace: 4443d589e0aaebdc8cc19ebbf4bdcfda00a4d1e24f6280b470a3820e0a161607
remote: error: See https://gh.io/lfs for more information.
remote: error: File java_pid397536.hprof is 692.87 MB; this exceeds GitHub's file size limit of 100.00 MB
remote: error: File java_pid399920.hprof is 688.71 MB; this exceeds GitHub's file size limit of 100.00 MB
remote: error: File java_pid415016.hprof is 692.36 MB; this exceeds GitHub's file size limit of 100.00 MB
remote: error: File java_pid384076.hprof is 696.53 MB; this exceeds GitHub's file size limit of 100.00 MB
remote: error: GH001: Large files detected. You may want to try Git Large File Storage - https://git-lfs.github.com.
To https://github.com/ollanvin/myphonecheck.git
 ! [remote rejected] HEAD -> codex-global-single-core-snapshot (pre-receive hook declined)
error: failed to push some refs to 'https://github.com/ollanvin/myphonecheck.git'
```

원본 전문: `logs/hygiene/push_result.txt`.

### 5.2 원인 진단

`git log --all --diff-filter=A --name-only -- '*.hprof'` 결과:

```
cca259c build: gradlew check 수정 + docs/ARCHITECTURE.md 역설계도
java_pid384076.hprof   (730,367,154 bytes ≈ 696.53 MB)
java_pid397536.hprof   (726,523,478 bytes ≈ 692.87 MB)
java_pid399920.hprof   (722,164,160 bytes ≈ 688.71 MB)
java_pid415016.hprof   (725,996,040 bytes ≈ 692.36 MB)
```

- 이 4개 JVM heap dump 파일은 `cca259c` (2026-04-21 17:14:46 KST, author: `비전 (AI Agent) <founder@idolab.ai>`) 커밋에서 리포 루트에 추가되었다.
- `cca259c`는 본 보완 워크오더 이전의 커밋으로, 원격 origin에는 아직 미반영 상태 (origin은 5ef0840가 tip).
- 내 신규 커밋 `a2eec04`는 `cca259c` 위에 쌓였기 때문에 push 시 `cca259c`까지 함께 GitHub로 전송되며, 이 단계에서 100MB 한도 검사에 걸린다.
- 내 신규 커밋 `a2eec04`에는 .hprof가 전혀 포함되어 있지 않다 (검증: 동 커밋 diff 에 .hprof 문자열 0건).

### 5.3 로컬 디스크에 .hprof 파일 현존

```
C:\Users\user\Dev\ollanvin\myphonecheck\java_pid384076.hprof  730,367,154 bytes
C:\Users\user\Dev\ollanvin\myphonecheck\java_pid397536.hprof  726,523,478 bytes
C:\Users\user\Dev\ollanvin\myphonecheck\java_pid399920.hprof  722,164,160 bytes
C:\Users\user\Dev\ollanvin\myphonecheck\java_pid415016.hprof  725,996,040 bytes
```

이 파일들은 Gradle/Kotlin 빌드 중 OOM으로 JVM이 heap dump를 남긴 결과물이다. (파일명의 `pidNNNNNN`는 dump 당시 프로세스 ID.) 개발 산출물로서 리포지토리에 보관할 가치는 없다.

### 5.4 워크오더 금지 조항 적용

`_workorder_stage0_hotfix_audit_supplement_repo_hygiene.txt` Section 12:

> - 강제 push (--force) 금지
> - rebase / squash 금지 (단일 커밋으로 구성)

`cca259c`에서 .hprof 파일을 제거하는 모든 현실적 방안(`git rebase`, `git filter-branch`, `git filter-repo`, `git reset` 후 재커밋 등)은 커밋 SHA 재작성을 수반하며 그 결과를 원격에 반영하려면 force push가 필요하다. 두 수단 모두 워크오더 금지 대상이다.

따라서 비전은 **이 지점에서 자발적 수정을 중단**하고 대표님의 판단·승인을 대기한다.

### 5.5 대표님께 드리는 후속 조치 옵션 (비전은 실행 금지)

각 옵션은 커밋 히스토리 재작성 및/또는 force push가 필요하므로 별도 지시가 필수다.

**옵션 A — .hprof 삭제 후 history rewrite + force push (가장 정공법):**
1. `cca259c`를 유지하되 .hprof 4개를 제거한 동등 커밋으로 재작성 (`git filter-repo --path 'java_pid*.hprof' --invert-paths`).
2. HEAD도 동일 규칙으로 재작성된 후, force push.
3. 비전이 수행했던 `a2eec04`는 rebase 과정에서 내용 그대로 승계된다.
4. 원격 브랜치가 `codex-global-single-core-snapshot` 에만 영향, 다른 브랜치엔 영향 없음.
5. 로컬 working tree 의 .hprof 4개는 별도 수동 삭제 + `.gitignore`에 `*.hprof` 추가 권장.

**옵션 B — Git LFS 마이그레이션:**
1. 대용량 파일 전용 저장. 리포 히스토리에 LFS 포인터로 전환.
2. 설정 비용, LFS quota 비용, 팀 워크플로 변경 수반. 현 시점 권장도 낮음 (heap dump는 영구 보관 가치 없음).

**옵션 C — cca259c 이전 상태로 리셋 + a2eec04 재적용:**
1. `git reset --hard origin/codex-global-single-core-snapshot` → `a2eec04`와 `cca259c` 모두 로컬에서 소거.
2. `a2eec04`만 cherry-pick.
3. push.
4. `cca259c`에 있던 "gradlew check 수정 + docs/ARCHITECTURE.md 역설계도" 변경은 소실. 대표님이 해당 작업의 재수행 필요성을 판단해야 함.

**옵션 D — 현 상태 유지 + 푸시 보류:**
1. 로컬 `a2eec04`는 그대로 보존.
2. `.hprof` 처리 정책이 정해질 때까지 push 시도 중단.
3. 대신 audit 산출물을 `docs/05_quality/` 및 `logs/`에서 로컬로 검토 가능.

비전의 의견: **옵션 A가 정석**. `filter-repo`가 대형 파일만 깨끗이 걷어내므로 cca259c의 다른 변경(gradlew 수정, ARCHITECTURE.md)은 그대로 보존된다. 단, force push가 필요하므로 대표님 명시 승인 필요.

## 6. 잔존 이슈

### 6.1 Category C stash 재분류 (Stage 1 착수 전 필요)

```
stash@{0}: On codex-global-single-core-snapshot:
  stage0-hotfix-hygiene: category C residue (push-removal refactor + other governance docs/scripts)
```

- 안에는 v1.1 아키텍처 PUSH 엔진 제거 리팩터 + 타 workorder + 아키텍처 산출물 58건이 격리되어 있다.
- Stage 1 착수 전 대표님이 직접 재분류하여 커밋/폐기 결정을 내려야 한다.
- `git stash show stash@{0}` 또는 `git stash pop` (주의: pop은 파괴적이므로 `git stash apply`가 안전)로 내용 확인 가능.

### 6.2 로컬 디스크 .hprof 4개 처리

현재 리포 루트에 상주 중인 4개 heap dump (~2.9GB)는:
- 향후 `.gitignore`에 `*.hprof` 패턴 추가 후 수동 삭제 권장.
- Stage 0-hotfix Audit §4-1에서 분석된 sqlite-jdbc DLL 추출 실패 시점에 함께 쌓인 부산물일 가능성이 높으나, pid 범위(384076–415016)로 단순 추정 불가.

### 6.3 커밋 후 생성된 감사 진단 로그 8건

`logs/hygiene/` 하위에 커밋 후 진단 로그 8건이 untracked로 남아 있다:
```
logs/hygiene/build_artifact_leak.txt
logs/hygiene/categoryC_leak_check.txt
logs/hygiene/git_status_final.txt
logs/hygiene/hprof_history.txt
logs/hygiene/push_result.txt
logs/hygiene/staged_final.txt
logs/hygiene/stash_contents.txt
logs/hygiene/stash_list.txt
```

이들은 본 에스컬레이션 보고서의 증거 파일이다. 워크오더가 "단일 커밋" 원칙을 명시하므로 비전은 이들을 추가 커밋하지 않고 그대로 두었다. 대표님이 §5의 옵션 A/B/C/D 중 하나를 택해 다음 커밋 사이클이 열리는 시점에 함께 반영하거나, 별도 "post-hygiene audit evidence" 커밋으로 처리하면 된다.

### 6.4 sqlite-jdbc java.io.tmpdir 정공법 (Stage 2 이관)

선행 Stage 0-hotfix Audit §5 권고 사항. 본 보고서 범위 밖. Stage 2로 이관.

## 7. 원칙 준수 선언

- **추정 결론 없음**: 푸시 거부 원인은 `remote: error: File java_pid*.hprof ... exceeds GitHub's file size limit`의 실제 로그 출력(logs/hygiene/push_result.txt)을 근거로 한다.
- **땜빵/우회 없음**: .hprof 문제를 회피하기 위한 `--no-verify`, `--force`, 부분 커밋, 파일 잘라내기 등의 우회 조치를 일체 시도하지 않았다.
- **.gitignore 보강은 정석**: `.claude/` 단일 패턴만 추가했다. `*.hprof` 패턴 추가는 .hprof 제거 정책과 동반 의사결정이 필요하므로 보류했다.
- **범주 C stash는 격리이지 폐기 아님**: `git stash drop`은 §5.5 옵션 결정 후 별도 판단.
- **rebase / force push 금지 준수**: 워크오더 Section 12 명시 조항. 실행 0건. 현재 로컬 HEAD(a2eec04)는 그대로 보존.
- **호칭 규칙 준수**: 실행자 "비전", 수신자 "대표님", 존칭·반말 사용 없음.
