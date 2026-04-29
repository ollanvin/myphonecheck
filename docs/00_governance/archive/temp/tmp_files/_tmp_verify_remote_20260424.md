# Lane 2 Cursor 원격 검증 리포트

WO: **WO-V161-VERIFY-REMOTE-001**  
수행: 2026-04-24 (로컬 Git + GitHub REST API). `gh` CLI는 PATH에 없어 **REST API**로 PR/Issue 조회.

**본문 대상 경로 정정:** WO 전제 `docs/00_governance/MyPhoneCheck_Architecture_v1.6.1.md`는 **현재 워킹 트리에 없음**. 교차 대조는 사용자 지정 **`C:\Users\user\Downloads\MyPhoneCheck_Architecture_v1.6.1.md`** 를 사용함.

---

## 1. Git 히스토리 요약

| 항목 | 값 |
|------|-----|
| `git fetch --all --prune` | 완료 (`origin/main` 갱신 확인) |
| 로컬·원격 브랜치 (표시 줄) | 8줄 (`_tmp_branch_list_v161.txt`) — 브랜치명 6개 + `remotes/origin/HEAD` |
| 태그 | **4** (`SNAPSHOT_CODEX_TAKEOVER_20260414`, `v0.1-overlay-decision-ui`, `v0.1.0-pre-refactor`, `v1-global-number-engine`) |
| `2026-01-01` 이후 `docs/` 터치 커밋 | **28** (`_tmp_docs_commit_log.txt`) |
| 동일 기간 전체 커밋 (oneline) | **85** (`_tmp_all_commits.txt`) |
| Architecture 관련 커밋×파일 매니페스트 | **17행** (`_tmp_arch_snapshot_manifest.txt`) — `docs/01_architecture`, `docs/ARCHITECTURE.md`, `docs/00_governance` 경로 한정 |
| Phase 2 blob 덤프 (`_tmp_snapshot_*`) | **미생성** (WO 루프는 저장소 부하 방지를 위해 매니페스트만 수행) |
| GitHub **PR** (state=all, per_page=100) | **1**건 |
| GitHub **Issue** (비-PR) | **0**건 (API 첫 페이지에 PR만 존재) |

---

## 2. 과거 공식 결정 목록 (Git·PR·Issue 기준)

| # | 출처 (SHA·PR#·Issue#) | 결정 요지 | 날짜 (커밋/머지) |
|---|------------------------|-----------|------------------|
| 1 | `ea0c4da` | `docs/06_history/archive/2026-04-imports/` 에 Word/Markdown 아키텍처·패치·워크오더 **아카이브 반입** | 2026-04-23 |
| 2 | `289126a` | `docs(governance): apply architecture patch v1.7` — governance·base architecture 갱신 | 2026-04-23 |
| 3 | `7d1c71b` / `f1934ff` | `docs/ARCHITECTURE.md` 역설계·conformance·base arch 연동 | 2026-04-21 |
| 4 | `1426473` / `0ecd4ab` | 글로벌 싱글코어 문서 트리 도입·정리 | 2026-04-15 |
| 5 | `d470a73` | Stage 0 hotfix: **Java 17**, `core:common` FREEZE, 저장소 위생 | 2026-04-22 |
| 6 | **PR #1** (`9b172e8`) | **PRD·연동 문서 가격 USD 2.49/월** (memory #11, WO-GOV-005 언급). Patch 17~28 **미언급** | 머지 2026-04-24Z |
| 7 | `docs/07_relay/done/WO-GOV-005__claudecode__done.md` | 2026-04-22 **대표님 확정·승인** (가격·Push·Mic/Camera 등) | 문서 내부 날짜 |

---

## 3. 본문 ↔ Git 이력 대조

| # | 과거 결정 / 본문 주장 | 본문 섹션 | 상태 |
|---|------------------------|-----------|------|
| 1 | Patch **17** | §0-B-2 PATCH-17, §0-B-1 | **부분** — 아카이브 DOCX·픽액스 커밋만 간접; Markdown 동형 표 없음 |
| 2 | Patch **18~22** | §0-B-2 | **부분** — governance에 v1.6 계열 메타만; 18~22 단행 표 Git 동형 없음 |
| 3 | Patch **23~28** | §0-B-2 | **부분** — `project-governance.md`에 PATCH-23…28 요약; Downloads 본문 전표와 동일 파일 없음 |
| 4 | §0-B-1 검토자 표 | §0-B-1 | **근거 분산** — PR/Issue 미포함; `WO-GOV-005` done과 일부 정합 |
| 5 | 헌법 **7조** | §0-A-1 | **확인 제한** — 전문 diff 미실시 |
| 6 | 가격 **USD 2.49/월** | §0-A-2 | **일치** — PR #1, 커밋, WO-GOV-005 |
| 7 | **630dda** 폐기 vs 통합본 | 주석 vs governance | **충돌** — 본문 폐기 / 레포는 통합본 SHA 예시 |
| 8 | Infrastructure v1.0 페어 | §0-F | **미일치** — 파일 레포에 없음 |
| 9 | `scripts/build_architecture_v161.py` | §0-E | **미일치** — 레포에 없음; governance는 `docs/00_governance/build_architecture_v161.py` 언급 |

---

## 4. 본문에만 존재 (Git 근거 없음)

| # | 본문 주장 | 섹션 | 비고 |
|---|-----------|------|------|
| 1 | §0-B-2 Patch 17~28 **전량 표** | §0-B-2 | Git 추적 MD에 동일 표 없음 |
| 2 | 630dda **폐기** (대표님 지시) | HTML 주석 | PR/코멘트 미검색; governance와 상충 |
| 3 | Infrastructure v1.0 파일 | §0-F | 미존재 |
| 4 | `scripts/verify-doc-hash.ps1` 등 | §0-E | 미존재 또는 경로 불일치 |
| 5 | 캐노니컬 승격 조건 3건 | 머리말 | Git 동문 없음 |

---

## 5. 판정 요약 (Lane 1과 동일 포맷)

**Lane 1 입력 없음** — Lane 2 단독 관측.

- 가격·WO-GOV-005: Git·PR과 **대체로 정합**.
- Patch 17~28 **세부 감사표·서사**: GitHub PR/Issue **근거 부족**; 레포는 governance 요약 + 아카이브 DOCX.
- 630dda·스크립트 경로·Infrastructure: 레포와 본문 **불일치/부재** 확인.

---

## 6. 권고

1. v1.6.1.md를 레포 `docs/00_governance/` 등에 **커밋**해 이력 대조 가능하게 할 것.  
2. `project-governance.md`의 630dda 설명과 본문 폐기 기록을 **대표님 확인 하에 통일**.  
3. §0-E 스크립트 경로를 실제 레포 배치와 **통일**.  
4. Infrastructure 페어 문서 **실파일 부재 시** 외부 보관 여부 명시.  
5. 필요 시 `_tmp_arch_snapshot_manifest.txt` 기준으로 **blob 스냅샷 WO** 추가.

---

## 부록: 조사 산출물

- `_tmp_docs_commit_log.txt`, `_tmp_all_commits.txt`, `_tmp_branch_list_v161.txt`, `_tmp_tag_list_v161.txt`, `_tmp_arch_snapshot_manifest.txt`, `_tmp_pr_issue_history.json`
