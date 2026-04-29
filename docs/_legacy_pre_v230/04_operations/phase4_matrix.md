# Phase 4 매트릭스 검증 운영 SOP

> **버전**: v1.0 (Working Canonical)
> **작성일**: 2026-04-28 저녁
> **작성**: Claude Code (WO-V230-PHASE4-MATRIX-IMPL)
> **헌법 정합**: §9-6 검증·테스트 정책 (v2.2.0 정정본 신설, v2.3.0 보존)
> **인프라 정합**: Infrastructure v1.3 §0.2 Cross-references (§9-6 검증 매트릭스 명시)

---

## 1. 골격

본 SOP는 **헌법 §9-6 빅테크 정공법 검증 매트릭스**의 운영 절차를 정의한다.

| 축 | 범위 | 비고 |
|---|---|---|
| 디바이스 | 4종 (`pixel4Api28`, `pixel5Api31`, `tabletApi33`, `pixel7Api34`) | API 28/31/33/34 + 폰·태블릿 form factor |
| SIM | 11개국 (CORE 4 + EXTENDED 4 + SPECIALIZED 3) | 라틴·CJK·RTL·동남아 커버 |
| 시나리오 | 17종 (S01~S17) | v2.3.0 §18 SmokeRun 11 + Initial Scan + SIM Locale + Real-time + Tag + FeedRegistry + 4-Layer |

전수 풀 = 4 × 11 × 17 = **748 케이스**. 전수 안 함. **Latin Hypercube 샘플링**으로 핵심 40~60 케이스 선정.

## 2. SIM 매트릭스 (11개국)

| Tier | 국가 | iso | Locale | MCC+MNC | 적용 시점 |
|---|---|---|---|---|---|
| CORE | KR | kr | ko-KR | 45005 | PR 게이트 |
| CORE | US | us | en-US | 310260 | PR 게이트 |
| CORE | JP | jp | ja-JP | 44010 | PR 게이트 |
| CORE | DE | de | de-DE | 26201 | PR 게이트 |
| EXTENDED | GB | gb | en-GB | 23410 | 야간 |
| EXTENDED | IN | in | hi-IN | 40410 | 야간 |
| EXTENDED | BR | br | pt-BR | 72402 | 야간 |
| EXTENDED | CN | cn | zh-CN | 46000 | 야간 |
| SPECIALIZED | TW | tw | zh-TW | 46692 | 야간 |
| SPECIALIZED | SA (RTL) | sa | ar-SA | 42001 | 야간 |
| SPECIALIZED | TH | th | th-TH | 52001 | 야간 |

코드 정합: `app/src/androidTest/java/app/myphonecheck/mobile/matrix/SimMatrixContext.kt`.
스크립트 정합: `scripts/matrix/sim/apply_sim.sh`.

## 3. 시나리오 (17종)

| ID | 표면 | 설명 |
|---|---|---|
| S01 | CallCheck | inbound — known number |
| S02 | CallCheck | inbound — unknown number |
| S03 | CallCheck | outbound — local-relationship profile |
| S04 | MessageCheck | bank notification (legitimate) |
| S05 | MessageCheck | delivery phishing (suspicious) |
| S06 | MessageCheck | Coupang impersonation |
| S07 | MicCheck | voice quality probe |
| S08 | CameraCheck | permission probe |
| S09 | CardCheck | month spend aggregation |
| S10 | PushCheck | notification quarantine |
| S11 | Integration | Six Surfaces — single core |
| S12 | InitialScan | base data construction (§28) |
| S13 | SIMCore | Locale auto-follow (§29) |
| S14 | RealTime | 50ms PASS fallback (§31) |
| S15 | TagSystem | SUSPICIOUS to SILENT (§32) |
| S16 | FeedRegistry | KISA active source match (§30-4) |
| S17 | DataModel | 4-Layer priority 2-1-3-4 (§30-3-A) |

코드 정합: `app/src/androidTest/java/app/myphonecheck/mobile/matrix/ScenarioId.kt`.

## 4. CI Workflow 2종

### 4.1 PR 게이트 (`.github/workflows/matrix-test-pr.yml`)

- 트리거: PR `app/**`, `core/**`, `data/**`, `feature/**`, `scripts/matrix/**` 변경
- 매트릭스: CORE 4 SIM × 17 시나리오 = 68 케이스, **pixel7Api34 단일 디바이스**
- 목표 시간: ~10분 (`max-parallel` GitHub Actions 기본)
- 명령:
  ```bash
  ./gradlew :app:pixel7Api34DebugAndroidTest \
    -Pandroid.testInstrumentationRunnerArguments.class=app.myphonecheck.mobile.matrix.ScenarioMatrixTest#testS01 \
    -Pandroid.testInstrumentationRunnerArguments.sim=KR
  ```

### 4.2 야간 풀 (`.github/workflows/matrix-test-nightly.yml`)

- 트리거: `cron: '0 18 * * *'` (매일 18:00 UTC = 한국 03:00 KST) + `workflow_dispatch`
- 매트릭스: 4 device × 11 SIM × 17 시나리오 풀에서 **LHS 샘플 60 케이스** 선정
- 목표 시간: ~60분 (`max-parallel: 8`)
- 샘플 산출: `python3 scripts/matrix/sample/sample_lhs.py --size 60 --seed 42`
- `workflow_dispatch` 입력: `sample_size`, `seed` 사용자 지정 가능

## 5. Latin Hypercube 샘플링

`scripts/matrix/sample/sample_lhs.py` — NumPy 의존 0, 표준 라이브러리만.

원리: 각 축(device/sim/scenario)의 모든 값이 균등 비율로 등장하도록 stratified shuffle. 748 풀 중 60개 선정 = 약 8% 커버.

PR 게이트는 LHS 사용 안 함 (CORE 4 SIM × 17 = 전수 68 케이스로 제한적 풀 자체가 가벼움).

## 6. 로컬 실행

```bash
# 1) Gradle Managed Devices 생성 (1회, 자동 emulator 생성)
./gradlew :app:pixel7Api34Setup

# 2) SIM 적용
./scripts/matrix/sim/apply_sim.sh KR

# 3) 시나리오 단일 실행
./gradlew :app:pixel7Api34DebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=app.myphonecheck.mobile.matrix.ScenarioMatrixTest#testS05 \
  -Pandroid.testInstrumentationRunnerArguments.sim=KR

# 4) 그룹 전체 실행 (CI 동등)
./gradlew :app:ciMatrixGroupDebugAndroidTest
./gradlew :app:prGateGroupDebugAndroidTest
```

## 7. 후속 PR (시나리오 본문)

본 PR은 **매트릭스 골격**만 작성한다. 17 시나리오 본문(actual UI flow + assertion)은 후속 PR에서 작성:

- S01~S11: v2.3.0 §18 SmokeRun 본문 매핑 (Compose UI test + Espresso)
- S12: Initial Scan 베이스데이터 검증 (Room v14 entity count)
- S13: SIM-Oriented Locale 자동 추종 검증 (`Currency.getInstance(locale)` + libphonenumber 양식)
- S14: Real-time Action 50ms 응답 검증 (`withTimeout(50L)` PASS fallback)
- S15: Tag System priority 4단 검증 (RoomTagRepository + ActionDecision)
- S16: FeedRegistry KISA 활성 출처 매칭 검증
- S17: 4-Layer Data Model 우선순위 검증 (Layer 2·1·3·4)

각 후속 PR은 **본 PR의 매트릭스 골격을 재사용**한다 (코드 구조 안정 후 본문만 보강).

## 8. 헌법 §9-6 정합 체크리스트

- [x] Gradle Managed Devices 활용 (단일 디바이스 검증 금지)
- [x] 디바이스 매트릭스 API 28/31/33/34 + 폰·태블릿
- [x] SIM 매트릭스 11개국 (KR JP US GB DE IN BR CN TW SA TH)
- [x] Locale 매트릭스 11개 (ko ja en-US en-GB de hi pt-BR zh-CN zh-TW ar th)
- [x] 각 SIM·Locale 조합에서 시나리오 17개 매트릭스 가능
- [x] adb emu gsm 시뮬레이션 활용 (실 SIM 0)
- [x] CI 자동 매트릭스 (수동 단일 검증 금지) — PR 게이트 + 야간 풀
- [x] Latin Hypercube 샘플링 — 핵심 40~60 케이스
