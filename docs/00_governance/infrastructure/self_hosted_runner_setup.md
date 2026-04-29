# Self-hosted Runner 등록 가이드 (CI 가속)

**WO**: WO-CI-OPT-001
**작성일**: 2026-04-29
**워커**: Claude Code

---

본 가이드는 대표님 본인 PC 또는 MacinCloud 인스턴스를 GitHub Actions Self-hosted Runner로 등록하는 절차.

## 효과

- GitHub-hosted runner 대비 CI 시간 1/3
- 본 PC = 16+ CPU 코어 / 32+ GB RAM 가정 시 Gradle 빌드 ~3분
- 단점: 본 PC 켜져 있어야 함 (또는 MacinCloud 24/7 임대)

## 등록 절차

1. GitHub 레포 → Settings → Actions → Runners → New self-hosted runner
2. OS / Architecture 선택 (Windows x64 또는 macOS arm64)
3. Download + Configure 명령 본 PC PowerShell 또는 Mac Terminal 실행
4. Runner 등록 후 workflow `runs-on: self-hosted` 또는 `runs-on: [self-hosted, windows]` 적용

## 보안 의무

- Self-hosted runner는 main 브랜치만 trust (PR 외부 기여자 차단)
- workflow 권한 최소화 (`permissions: read-all` 기본 + 명시 elevate)
- 비밀 의존성 검증 (third-party action allowlist)
- runner 등록 후 즉시 `Setup as a service` 옵션으로 백그라운드 실행 (Windows: PowerShell 관리자 권한, macOS: launchd)

## 메모리 #18 정합

- App Factory 자동화 = GitHub 네이티브 (Self-hosted Runner 포함)
- 헌법 §9-2 외부 영업·계약 0 정합 (GitHub Actions는 표준 인프라, 외부 회사 계약 아님)

## workflow 정정 예시

본 가이드 적용 후 workflow `runs-on:` 정정:

```yaml
jobs:
  build:
    runs-on: [self-hosted, windows, x64]   # 또는 [self-hosted, macos, arm64]
    steps:
      - uses: actions/checkout@v4
      # JDK / Android SDK는 본 PC에 사전 설치 (setup-java, setup-android step 생략 가능)
      - uses: gradle/actions/setup-gradle@v4
      - run: ./gradlew app:assembleDebug --build-cache --parallel
```

본 정정은 별 WO (`WO-CI-OPT-002`)로 분리. 본 WO-CI-OPT-001은 GitHub-hosted runner 위에서 Gradle 캐싱 + Job 분할만 처리.

## 비용 비교

| 항목 | GitHub-hosted | Self-hosted (본 PC) | MacinCloud |
|---|---|---|---|
| 월 비용 | 무료 (public repo) / $0.008/min (private) | 0 (전기료만) | $30~80/월 |
| 빌드 시간 | 11분 (현재) | ~3분 | ~3분 |
| 가용성 | 24/7 | 본 PC 켜져 있을 때만 | 24/7 |
| 보안 | GitHub 보장 | 본 PC 책임 | MacinCloud 보장 |

본 레포는 private 가정 → GitHub-hosted 비용 발생. Self-hosted 전환 시 ~$30/월 절감 가능 (대표님 PC 24/7 켜져 있는 경우).

## 권고

- **단기**: GitHub-hosted + Gradle 캐싱 (WO-CI-OPT-001 본 PR) — 11분 → 3~5분
- **중기**: Self-hosted Runner 등록 (대표님 결정 영역)
- **장기**: 대표님 PC + MacinCloud 백업 (24/7 보장)
