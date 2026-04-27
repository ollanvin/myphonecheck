# Project Governance

## Scope

MyPhoneCheck is a project governed by the shared constitution repository and by this project-level applied governance.

This repository stores:

- applied project rules
- project architecture records
- implementation and validation documents
- project history

It does not store the cross-project constitution itself.

## Constitution Source

- Constitution repository local path: `C:\Users\user\Dev\ollanvin\web`
- Constitution repository remote: `https://github.com/ollanvin/web`
- Constitution document baseline: `CONSTITUTION.md`

MyPhoneCheck must align to the constitution repository, but project-specific implementation documents remain inside this repository only.

## Fixed Project Principles

- MyPhoneCheck is a 190-country shared single-core system.
- Country-specific feature branching is prohibited in the global core.
- The fixed layers are `Global Core`, `Country Policy Layer`, and `Presentation Layer`.
- `CallCheck Core Engine` is the only judgment core.
- `MessageCheck` is an extension path into the same core, not an independent engine.
- `PushCheck` remains disabled.
- The app does not make the final user judgment. It shows evidence.
- The user remains the final decision-maker.
- Contact saving and relationship management remain separate.
- Unsaved numbers must still support local relationship profiles.
- Only measured on-device data may be shown.
- Search-result status must use direct fixed wording only.

## Documentation Standard

Project documents must live under `docs/` and be classified by purpose.

Standard folders:

- `docs/00_governance/`
- `docs/01_architecture/`
- `docs/02_product/`
- `docs/03_engineering/`
- `docs/04_operations/`
- `docs/05_quality/`
- `docs/06_history/`

## In-repo architecture charter

- **Integrated product + device-sovereignty spec (normative):** `docs/00_governance/MyPhoneCheck_Architecture_v4.1_final.md` (v4.1 Final: 3-AI consensus, six-article constitution, merged SLA, v1.3 technical body).
- **Two-part merge snapshot:** `docs/00_governance/MyPhoneCheck_Architecture_v1.4_full.md` (reference only when tracing v1.3 vs v1.4_disc side-by-side).
- **v1.5.1 Word build (vision + patches 01–08):** `docs/00_governance/MyPhoneCheck_Architecture_v1.5.1_7d23b4.docx` — rebuild via `python docs/00_governance/build_architecture_v151.py` (SHA256 suffix may change between runs). Draft markdown: `MyPhoneCheck_Architecture_v1.5.1_draft.md`. Pipeline: `md_normalizer.py` (fence-aware CommonMark) then patches; keep archived `MyPhoneCheck_Architecture_v1.5.1_fd908b.docx` for history.
- **v1.5.2 Word build (v1.5.1 draft + §6–§32 재번호 + patches 09–16):** `docs/00_governance/MyPhoneCheck_Architecture_v1.5.2_{SHA256_6}.docx` — rebuild via `python docs/00_governance/build_architecture_v152.py` (콘솔에 `SHA256_6` 출력). Draft markdown: `MyPhoneCheck_Architecture_v1.5.2_draft.md`. Pipeline: `normalize_commonmark()` (결함 A 방지) → 재번호 → Patch 09 삽입 및 10–16 적용 → pandoc; GFM 스모크 22토큰 + 번호형 H1 중복 0건 검증. 결함본 `MyPhoneCheck_Architecture_v1.5.2_0e40dd.docx`는 삭제하지 말고 아카이브 유지.
- **v1.5.3 Word build (v1.5.2 draft + Patch 17):** `docs/00_governance/MyPhoneCheck_Architecture_v1.5.3_{SHA256_6}.docx` — rebuild via `python docs/00_governance/build_architecture_v153.py`. Draft: `MyPhoneCheck_Architecture_v1.5.3_draft.md`. Patch-only bundle: `MyPhoneCheck_Patches_v1.5.3-patch_{SHA256_6}.docx`. 내용: AndroidManifest `BROADCAST_SMS` SmsReceiver 블록 제거(주석 치환), §34-1 표 행 갱신, §0-A/§0-B 거버넌스 행 추가, 표지 v1.5.3 + Store Policy Hardening. GFM 스모크: v1.5.2 토큰 + v1.5.3 신규 4개, 금지 토큰 2종 부재. 아카이브: v1.5.2 `9f1d43` 등 삭제 금지.
- **v1.6.0 Word build (v1.5.3 draft + Four Surfaces / MINOR):** `docs/00_governance/MyPhoneCheck_Architecture_v1.6.0_{SHA256_6}.docx` — rebuild via `python docs/00_governance/build_architecture_v160.py`. Draft: `MyPhoneCheck_Architecture_v1.6.0_draft.md`. Patch bundle (18–22): `MyPhoneCheck_Patches_v1.6.0-patch_{SHA256_6}.docx`. 내용: §3–6 Four Surfaces 동시 정책, §18 MessageCheck·MicCheck·CameraCheck, §25 Day 22–35, §33-1-1 sealed `IdentifierType` + four checkers, §34-1-1 `QUERY_ALL_PACKAGES` / `PACKAGE_USAGE_STATS`, §35-9 + **§36** Four Surfaces 통합, §0-B PATCH-18…22. GFM 스모크: v1.5.3 토큰 + v1.6.0 신규 문자열, 금지 구문 5종 부재, 번호형 H1 36개(§36 포함). 재실행 시 SHA256 접미사는 변할 수 있음.
- **v1.6.1 Word build (v1.6.0 draft + Patch 23~28 / PATCH):** `docs/00_governance/MyPhoneCheck_Architecture_v1.6.1_{SHA256_6}.docx` — rebuild via `python docs/00_governance/build_architecture_v161.py`. Draft: `MyPhoneCheck_Architecture_v1.6.1_draft.md`. Patch bundle (23–28): `MyPhoneCheck_Patches_v1.6.1-patch_{SHA256_6}.docx`. 내용: §34-1 표에서 본 앱 미보유 RECORD_AUDIO/CAMERA 행 삭제, 부록 A §A-3/§A-4 삭제 및 SYSTEM_ALERT_WINDOW §A-3 당김, §18-4/18-6/18-7 Kotlin·시나리오 완성, §24-6 Manifest `QUERY_ALL_PACKAGES`·`PACKAGE_USAGE_STATS` 선언, §0-A/§0-B PATCH-23…28, 표지 v1.6.1. GFM 스모크: 확장 필수 토큰 + 금지 9종, 번호형 H1 36개. 아카이브: v1.6.0 `b7bb60`, v1.6.0-patch `03b734` 삭제 금지.
- **Stage 0 — Common contracts (Coding WO f1a85c + hotfix e3b05e Java 17):** 순수 JVM 모듈 `:core:common` (`app.myphonecheck.core.common.*`) — `IdentifierType`, `RiskKnowledge` 계약군, `Checker`, `DecisionEngineContract`, `FREEZE.md`, `FreezeMarkerTest`, 워크플로 `.github/workflows/contract-freeze-check.yml`. 리포 전역 **JDK 17** (`jvmToolchain(17)`, `gradle.properties`의 `org.gradle.java.installations.auto-download`, 루트·`build-logic` `settings.gradle.kts`의 **Foojay toolchain resolver** `0.9.0`). 검증: `./gradlew :core:common:test`. 런타임 classpath에 `android*` 의존성 없음.
- **RiskLevel 이중 타입 정책 (2026-04-22)**: `core:common.risk.RiskLevel`(5단계: SAFE/SAFE_UNKNOWN/UNKNOWN/CAUTION/DANGER)을 **도메인 분류 정본**으로 한다. `core:model.RiskLevel`(4단계: HIGH/MEDIUM/LOW/UNKNOWN)은 **UI 표시 모델**로 유지. 변환은 단방향 매퍼(`:feature:decision-engine` 내 `RiskLevelMapper.kt`)로 처리. 매핑: SAFE→LOW, SAFE_UNKNOWN→LOW, UNKNOWN→UNKNOWN, CAUTION→MEDIUM, DANGER→HIGH. `core:common`은 FREEZE 상태 유지하므로 수정 불가. 역방향 매핑은 의미 손실로 인해 정의하지 않는다.

## v1.5.1 빌드 이력

| 빌드 | SHA256 앞6 | 상태 | 비고 |
|---|---|---|---|
| 초기 | fd908b | 결함 (§7/§13/§24/§25 H1 헤더 pandoc 미인식) | 아카이브 보존 |
| 재빌드 | 7d23b4 | 정상 | `md_normalizer.py` 전처리 + GFM 검증 (H1 ≥ 33, 토큰 13개) |

## v1.5.2 빌드 이력

| 빌드 | SHA256 앞6 | 상태 | 비고 |
|---|---|---|---|
| 결함 (워크오더 기준) | 0e40dd | 재빌드 대상 | 아카이브 보존 (삭제 금지) |
| 재빌드 | 예: 9f1d43 | 정상 | §6 Three-Layer 신설, §6–§32 → §7–§33 재번호, §34–§35 신설, 22토큰 검증; 재실행 시 해시는 변할 수 있음 |

## v1.5.3 빌드 이력

| 산출물 | SHA256 앞6 (예시) | 비고 |
|---|---|---|
| 통합본 | 예: 758158 | Patch 17, 헐크 라운드 3 채택; 재실행 시 해시 변동 |
| Patches v1.5.3-patch | 예: 6bb29e | Patch 17 독립 묶음 |

## v1.6.0 빌드 이력

| 산출물 | SHA256 앞6 (예시) | 비고 |
|---|---|---|
| 통합본 | b7bb60 | Four Surfaces 워크오더 212359 반영; 재실행 시 해시 변동 |
| Patches v1.6.0-patch | 03b734 | Patch 18–22 독립 묶음 |

## v1.6.1 빌드 이력

| 산출물 | SHA256 앞6 (예시) | 비고 |
|---|---|---|
| 통합본 | 630dda | 워크오더 v1.6.1-patch Cursor 6827a2 반영; 재실행 시 해시 변동 |
| Patches v1.6.1-patch | 61225a | Patch 23–28 독립 묶음 (본문 보강) |

### v1.5.3 참고

- 공식 `validate.py`가 로컬에 없으면 빌드 스크립트는 GFM 스모크만 수행한다.
- `build_architecture_v153.py`는 UTF-8로 저장한다 (UTF-16 저장 시 `SyntaxError: null bytes`).

### 결함 원인 및 방지

- 원인: 원본 md 4곳에서 ATX 헤더 직전 빈 줄 누락 (CommonMark 위반) → pandoc가 H1로 파싱 실패.
- 해결: 빌드 전 `normalize_commonmark()`로 fence 외부 헤더만 정규화; `fixed_lines == [627, 1318, 2556, 2848]` assert로 원본 변경 탐지.
- 적용 범위: v1.5.1 재빌드 및 동일 베이스를 쓰는 이후 빌드 스크립트.

### v1.5.2 추가 방지

- 재번호 없이 Patch 09만 넣으면 §6 H1이 중복된다. 반드시 기존 §6–§32를 §7–§33으로 민 뒤 새 §6을 삽입한다.
- `build_architecture_v152.py`는 UTF-8로 유지한다 (UTF-16 저장 시 Python `SyntaxError: null bytes`).

Rules:

- Do not leave implementation notes scattered in the repository root or feature folders.
- Do not mix constitution documents with project implementation documents.
- Keep work instructions, architecture, validation, and history as separate records.
- Keep temporary local notes and duplicate floating artifacts out of the project tree.
