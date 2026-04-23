# Summary — executing-OS (Cursor)
- 작성: Cursor (WO-DISCOVERY-002)
- 작성 시각: 2026-04-22 16:00 UTC
- 스캔 대상: C:\Users\user\Dev\ollanvin\executing-OS
- 총 파일 수: 77 (.md), 20 (.py), 141 (.ts) (로컬 스캔; 원격 443 .md 주장은 GitHub 전체 클론과 차이 있을 수 있음)
- 비전 정독 권장 TOP 5: §9 참조

## 1. 헌법성 진술

- **Constitution v4 / App Factory** (`docs/ARCHITECTURE-CONSTITUTION.md`, L13–L21): Table lists principles: global Play release; no hardcoding; Zero ops (cost/people/storage/centralization/hardcoding/manual); on-device runtime; store-only billing.
- **Exception policy** (`docs/ARCHITECTURE-CONSTITUTION.md`, L71–L93): Rule lifecycle `observe → warn → deny`; structured YAML exceptions with mandatory expiry; verbal agreements forbidden—document under `decisions/`.
- **Executing-OS architecture tenets** (`docs/ARCHITECTURE.md`, L12–L17): Policy-as-Code; stateless execution; Windows-native (SQLite WAL + worker pool, no Redis/RabbitMQ); failure intelligence (TRANSIENT vs STRUCTURAL); audit trail (JSON + Markdown + JSONL).
- **ai_rules_neo locked file** (`docs/ai/rule/ai_rules_neo.md`, L7–L9, L87–L95): Core AI rules are read-only in normal sessions; edits only in dedicated rules/constitution sessions after constitution repo updates; agents should refuse arbitrary edits.
- **ai_rules_neo — Constitution precedence** (`docs/ai/rule/ai_rules_neo.md`, L13–L18): All rules defer to Constitution v4; on conflict, constitution wins; follow `app-factory-constitution` repo.
- **Emulator-first execution** (`docs/ai/rule/ai_rules_neo.md`, L23–L33): Default order Emulator → ADB → capture; physical devices/manual ops are exceptional and must be justified + logged in SSW.
- **docs/ai folder shape** (`docs/ai/rule/ai_rules_neo.md`, L37–L46): Only `ssw`, `rule`, `checkpoint` under `docs/ai`; no fourth top-level folder; under `ssw` only `cursor/` for Cursor blog reports; `create_new_archive_files_freely: off` (no archive sprawl).
- **SSW reporting + push** (`docs/ai/rule/ai_rules_neo.md`, L56–L70): After work, write blog-style report under `docs/ai/ssw/cursor/` with filename pattern `YYYY-MM-DD_session-<nn>_cursor-<slug>_report.md`; commit and push to `origin/main`; chat uses `result` fenced block instead of duplicating long text.
- **Canonical repo** (`README.md`, L17–L18): After M1, GitHub `ollanvin/executing-OS` is canonical for Neo and CI clones (aligned with Cursor local-agent tree).

## 2. 도구·역할·워커 관련 진술

- **Factory CLI vs pipeline** (`docs/local-executor-os.md`, L31–L33, L37–L39): `executor.py` provides `enqueue-batch`, `worker`, `init-project`; `local_pipeline.py` sequential/queue orchestration; SQLite job queue + worker pool per ADR-002.
- **Neo launcher** (`README.md`, L45–L61): `run_neo.bat` menu drives WebStub US, Fooapp KR, G20 batch, MyPhoneCheck KR scenarios and env-only CMD.
- **Hex Executor Console** (`README.md`, L79+): React/TS UI + backend API subprojects for Cursor-style chat.
- **AI session read order** (`docs/ai/rule/ai_rules_neo.md`, L74–L82): ai_rules_neo → checkpoint → latest SSW → operator `[SESSION CORE]`; session core can override session scope when not violating irreversible constraints.

## 3. 마일스톤·진행상태·결정

- **M1 factory tag** (`README.md`, L13–L15): Snapshot tag `executor-os-factory-m1`.
- **Neo equivalence / migration** (reports under `docs/reports/`): Plans and runs for WebStub KR G20, MyPhoneCheck KR smoke, executor OS migration M1/M2 summaries.
- **ADR-001 superseded** (`docs/adr/ADR-001-MULTI-RUN-PARALLELIZATION.md`, L3–L4, L17–L19): Sequential execution was status quo; parallel worker pool specified in ADR-002.
- **Metrics strategy adopted** (`docs/strategy/STRATEGY-METRICS-AND-QA.md`, L15–L23): JSONL `run_events.jsonl`, `finalize_production` hook, daily KPI markdown append, weekly review of top failure clusters.

## 4. 미해결·의사결정 대기·이슈

- **Local vs remote file count**: This summary uses the local tree on disk; upstream may list ~443 `.md` files—verify with full clone if parity matters.
- **iOS on Windows** (`docs/local-executor-os.md`, L50–L54): Native Xcode/IPA skipped on Windows; reports describe handoff expectations for Mac/CI.

## 5. 사업 정보

- 없음 (본 저장소는 내부 SaaS 가격/수익 등을 다루지 않음). KPI 관점은 팩토리 QA/metrics 전략 문서에 있음.

## 6. 기술 정보

- **Pipeline**: Python `agent/local_executor.py` seven-stage flow; validators/gatekeepers; Paparazzi screenshots; SQLite queue in `runs/executor_queue.db`.
- **Policy merge**: `executor_schema.py` merges payload + project config + country profiles (`profiles/*.json`).
- **Subsystems**: `hex-executor-console` (frontend), `hex-executor-console-backend` (API, Ollama/planner notes), fixtures under `fixtures/`.
- **Evidence artifacts**: Per-run `reports/*.json`, Markdown mirrors, device/environment snapshots.

## 7. 파일별 1줄 요약

- `data/README.md` — Data files — **FULL**
- `docs/adr/ADR-001-MULTI-RUN-PARALLELIZATION.md` — ADR-001: Multi-country pipeline parallelization — **FULL**
- `docs/adr/ADR-002-WORKER-POOL-QUEUE.md` — ADR-002: Worker pool and local job queue — **FULL**
- `docs/adr/ADR-003-LEGACY-NEO-AGENT-REMOVED.md` — ADR-003 — Legacy NeO “local agent” shell removed from executing-OS — **FULL**
- `docs/ai/checkpoint/neo_session_checkpoint.md` — neo_session_checkpoint — executing-OS AI 세션 체크포인트 — **HEADER**
- `docs/ai/rule/ai_rules_neo.md` — ai_rules_neo — executing-OS 핵심 AI 규칙 — **HEADER**
- `docs/ai/ssw/cursor/2026-04-21_session-01_cursor-ai-rules-neo-checkpoint_report.md` — Cursor Result Report - 2026-04-21 Session 01 / ai_rules_neo checkpoint — **HEADER**
- `docs/ai/ssw/cursor/2026-04-21_session-01_cursor-ai-rules-neo-sync_report.md` — 오늘 작업 리포트 - 2026-04-21 / ai-rules-neo-sync — **HEADER**
- `docs/ai/ssw/cursor/2026-04-21_session-01_cursor-ai-rules-neo_report.md` — 오늘 작업 리포트 - 2026-04-21 / ai-rules-neo — **HEADER**
- `docs/ai/ssw/cursor/2026-04-21_session-01_cursor-session-core-and-reporting-meta_report.md` — 1. 오늘 내가 받은 미션 — **HEADER**
- `docs/ai/ssw/cursor/2026-04-21_session-02_cursor-ready-to-context-v1_report.md` — 오늘 작업 리포트 - 2026-04-21 / session-02 / ready-to-context-v1 — **HEADER**
- `docs/ARCHITECTURE-CONSTITUTION.md` — App Factory Constitution Architecture — **FULL**
- `docs/ARCHITECTURE.md` — Executing-OS Architecture — **FULL**
- `docs/forensics/FORENSICS-EXECUTOR-OS-SOURCE-2026-04-19.md` — Executor OS source snapshot — local-agent (reference 2026-04-19) — **HEADER**
- `docs/local-executor-os.md` — Local Executor OS v1 (Windows) — **HEADER**
- `docs/projects/fooapp.md` — Fooapp — Executor notes — **HEADER**
- `docs/projects/MYPHONECHECK-KR-SMOKE-SPEC.md` — MyPhoneCheck — KR smoke & dry-run spec (Cursor / Neo shared) — **FULL**
- `docs/projects/myphonecheck.md` — MyPhoneCheck — Executor notes — **HEADER**
- `docs/reports/EXECUTOR-OS-NEO-MIGRATION-M1-SUMMARY.md` — Executor OS — Neo migration M1 summary (2026-04-19) — **HEADER**
- `docs/reports/M2-MYPHONECHECK-KR-SMOKE-RUN-CURSOR.md` — M2 — MyPhoneCheck KR smoke run (Cursor baseline) — **HEADER**
- `docs/reports/NEO-EQUIVALENCE-PLAN-M2-MYPHONECHECK-KR.md` — Neo equivalence plan — M2 MyPhoneCheck KR — **HEADER**
- `docs/reports/NEO-EQUIVALENCE-PLAN-WEBSTUB-KR-G20.md` — Neo equivalence plan — WebStub / KR Android / G20 WebStub batch — **HEADER**
- `docs/reports/NEO-EQUIVALENCE-RUN-RESULTS-M1.md` — Neo equivalence run results — M1 (WebStub US / Fooapp KR / G20 batch) — **HEADER**
- `docs/reports/RUN-2026-04-19-WEBSTUB-KR-G20-MYPC-ONBOARDING.md` — Run report — WebStub US baseline, KR demos, G20 WebStub batch, MyPhoneCheck onboarding — **HEADER**
- `docs/reports/SYNC-2026-04-19-EXECUTING-OS-FROM-LOCAL-AGENT.md` — Sync report — executing-OS aligned from local-agent (2026-04-19) — **HEADER**
- `docs/strategy/STRATEGY-COUNTRY-PROFILES.md` — Country profile coverage (G20-first) — **FULL**
- `docs/strategy/STRATEGY-ISO-DATA-MANAGEMENT.md` — ISO 3166-1 alpha-2 table operations — **FULL**
- `docs/strategy/STRATEGY-METRICS-AND-QA.md` — Strategy: Metrics and QA loop — **FULL**
- `docs/strategy/STRATEGY-PROJECT-SCAFFOLDING.md` — Strategy: Project scaffolding — **FULL**
- `docs/windows/NEO-SHORTCUT-SETUP.md` — Neo 바로가기(.lnk) 복구 — Executor OS (Windows) — **HEADER**
- `fixtures/fooapp/README.md` — Fixtures: fooapp — **FULL**
- `fixtures/myphonecheck/README.md` — Fixtures: myphonecheck — **FULL**
- `hex-executor-console-backend/docs/OLLAMA_ROLE_SPLIT_NOTES.md` — Ollama 역할 분리 (초도작업 메모) — **HEADER**
- `hex-executor-console-backend/docs/workflow/INTERNAL-CELL-APP-LAUNCH-SUCCESS.md` — Stage 1 사례: app_launch_foreground (앱 기동 + foreground) — **HEADER**
- `hex-executor-console-backend/docs/workflow/INTERNAL-CELL-APP-READY-SCREENSHOT-SUCCESS.md` — Stage 1 composite: app_ready_screenshot (앱 준비 → 캡처) — **HEADER**
- `hex-executor-console-backend/docs/workflow/INTERNAL-CELL-EMULATOR-ENSURE-SUCCESS.md` — Stage 1 사례: emulator_ensure_boot (에뮬/ADB 확보) — **HEADER**
- `hex-executor-console-backend/docs/workflow/INTERNAL-CELL-MYPHONECHECK-CAPTURE-PACKAGE-SUCCESS.md` — Stage 1 golden path: myphonecheck_capture_package — **HEADER**
- `hex-executor-console-backend/docs/workflow/INTERNAL-CELL-SCREENSHOT-SUCCESS.md` — Stage 1 사례: 런타임 스크린샷 recoverable workflow — **HEADER**
- `hex-executor-console-backend/docs/workflow/NEO-PLANNER-EXECUTOR-ROADMAP.md` — Neo Planner / Executor 로드맵 — **HEADER**
- `hex-executor-console-backend/docs/workflow/PLANNER-EXAMPLE-MYPHONECHECK-CAPTURE.md` — 참조 플랜: MyPhoneCheck capture package — **HEADER**
- `hex-executor-console-backend/docs/workflow/PLANNER-MYPHONECHECK-TRACE-EXAMPLE.md` — MyPhoneCheck capture: 플랜 JSON + trace 예시 — **HEADER**
- `hex-executor-console-backend/docs/workflow/PLANNER-OVERVIEW.md` — LLM Planner API (1차) — **HEADER**
- `hex-executor-console-backend/docs/workflow/STAGE1-INTERNAL-AUTOMATION-CELL.md` — Stage 1: Internal Automation Cell (현재 스프린트 범위) — **HEADER**
- `hex-executor-console-backend/docs/workflow/WORK-ORDER-TEMPLATE.md` — Work order template (Neo / Stage 1) — **HEADER**
- `hex-executor-console-backend/output/control-plane-delivery/report-myphonecheck-ux-1776680293924.md` — MyPhoneCheck UX capture report (code worker draft) — **HEADER**
- `hex-executor-console-backend/output/control-plane-delivery/report-myphonecheck-ux-1776680446847.md` — MyPhoneCheck UX capture report (code worker draft) — **HEADER**
- `hex-executor-console-backend/output/control-plane-delivery/report-myphonecheck-ux-1776684373903.md` — MyPhoneCheck UX capture report (code worker draft) — **HEADER**
- `hex-executor-console-backend/output/control-plane-delivery/report-myphonecheck-ux-1776687158016.md` — MyPhoneCheck UX capture report (Iteration 3) — **HEADER**
- `hex-executor-console-backend/output/control-plane-delivery/report-myphonecheck-ux-1776687513601.md` — MyPhoneCheck UX capture report (Iteration 3) — **HEADER**
- `hex-executor-console-backend/README.md` — Neo Local Operator API — **FULL**
- `hex-executor-console/README.md` — Neo Local Operator Console — **FULL**
- `README.md` — executing-OS (Executor OS) — **FULL**
- `runs/daily_global_report.md` — KPI refresh — 2026-04-19T13:51:07Z — **HEADER**
- `runs/Fooapp/b77d6de3-48e6-46b7-bddd-b7f0e05ea31e/reports/gate_report.md` — Gate report — **HEADER**
- `runs/Fooapp/b77d6de3-48e6-46b7-bddd-b7f0e05ea31e/reports/task_result_report.md` — Local Executor OS — task result — **HEADER**
- `runs/Fooapp/b77d6de3-48e6-46b7-bddd-b7f0e05ea31e/reports/validation_report.md` — Validation report — **HEADER**
- `runs/MyPhoneCheck/0ffeb37c-bdbb-4150-9558-1814cecda137/reports/gate_report.md` — Gate report — **HEADER**
- `runs/MyPhoneCheck/0ffeb37c-bdbb-4150-9558-1814cecda137/reports/task_result_report.md` — Local Executor OS — task result — **HEADER**
- `runs/MyPhoneCheck/0ffeb37c-bdbb-4150-9558-1814cecda137/reports/validation_report.md` — Validation report — **HEADER**
- `runs/WebStub/22015eeb-217e-469d-8b9b-09f036d21af6/reports/gate_report.md` — Gate report — **HEADER**
- `runs/WebStub/22015eeb-217e-469d-8b9b-09f036d21af6/reports/task_result_report.md` — Local Executor OS — task result — **HEADER**
- `runs/WebStub/22015eeb-217e-469d-8b9b-09f036d21af6/reports/validation_report.md` — Validation report — **HEADER**
- `runs/WebStub/2efdc63f-6799-4f59-97c5-642d303986dc/reports/gate_report.md` — Gate report — **HEADER**
- `runs/WebStub/2efdc63f-6799-4f59-97c5-642d303986dc/reports/task_result_report.md` — Local Executor OS — task result — **HEADER**
- `runs/WebStub/2efdc63f-6799-4f59-97c5-642d303986dc/reports/validation_report.md` — Validation report — **HEADER**
- `runs/WebStub/463f3094-c646-4f96-b231-f8629a44f287/reports/gate_report.md` — Gate report — **HEADER**
- `runs/WebStub/463f3094-c646-4f96-b231-f8629a44f287/reports/task_result_report.md` — Local Executor OS — task result — **HEADER**
- `runs/WebStub/463f3094-c646-4f96-b231-f8629a44f287/reports/validation_report.md` — Validation report — **HEADER**
- `runs/WebStub/5d73077f-a864-4b67-8547-7cb36518d8b8/reports/gate_report.md` — Gate report — **HEADER**
- `runs/WebStub/5d73077f-a864-4b67-8547-7cb36518d8b8/reports/task_result_report.md` — Local Executor OS — task result — **HEADER**
- `runs/WebStub/5d73077f-a864-4b67-8547-7cb36518d8b8/reports/validation_report.md` — Validation report — **HEADER**
- `runs/WebStub/b664187c-ae4a-4318-a1ef-32558a1d80a4/reports/gate_report.md` — Gate report — **HEADER**
- `runs/WebStub/b664187c-ae4a-4318-a1ef-32558a1d80a4/reports/task_result_report.md` — Local Executor OS — task result — **HEADER**
- `runs/WebStub/b664187c-ae4a-4318-a1ef-32558a1d80a4/reports/validation_report.md` — Validation report — **HEADER**
- `runs/WebStub/f5f42b54-7f44-48fd-95ac-e2582d673216/reports/gate_report.md` — Gate report — **HEADER**
- `runs/WebStub/f5f42b54-7f44-48fd-95ac-e2582d673216/reports/task_result_report.md` — Local Executor OS — task result — **HEADER**
- `runs/WebStub/f5f42b54-7f44-48fd-95ac-e2582d673216/reports/validation_report.md` — Validation report — **HEADER**

## 8. 발견 + 예상 밖

- **App Factory constitution mirrored**: `ARCHITECTURE-CONSTITUTION.md` is a reverse-engineered snapshot from `app-factory-constitution`, not only executor internals.
- **Strict AI documentation taxonomy**: `docs/ai` limited to three subtrees + `ssw/cursor` reporting discipline is unusually formal.
- **Windows-first + emulator-first**: Explicit deprioritization of physical devices in AI rules.

## 9. 비전 정독 권장 TOP 5

1. `docs/ARCHITECTURE-CONSTITUTION.md`—upstream constitution mapping, rule lifecycle, exceptions (governance baseline).
2. `docs/ARCHITECTURE.md`—full module map and directory layout for executor OS.
3. `docs/ai/rule/ai_rules_neo.md`—locked AI rules, session order, SSW reporting, emulator-first policy.
4. `docs/local-executor-os.md`—operational module map, queue/worker usage, iOS-on-Windows strategy.
5. `README.md`—entrypoints, Neo launcher, canonical repo declaration, quick start.

