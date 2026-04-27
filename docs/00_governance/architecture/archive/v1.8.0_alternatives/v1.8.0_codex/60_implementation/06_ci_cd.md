# 26. CI/CD (JDK 17 기반)

**원본 출처**: v1.7.1 §26 (108줄)
**v1.8.0 Layer**: Implementation
**의존**: `60_implementation/01_day_by_day.md` + `50_test_infra/01_test_infra.md`
**변경 이력**: 본 파일은 v1.7.1 §26 (108줄) 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_codex/60_implementation/06_ci_cd.md`

---


## 26-1. 빌드 환경

- **JDK**: 17 LTS (Temurin)
- **Kotlin**: 2.0.x
- **Android Gradle Plugin**: 8.5+
- **Gradle Wrapper**: 8.9+
- **Android SDK**: compileSdk 34, targetSdk 34, minSdk 24
- **Toolchain**: `jvmToolchain(17)` 전체 모듈 선언 (Stage 0 hotfix로 승격 완료)

## 26-2. build.gradle.kts 공통

```kotlin
// 루트 build.gradle.kts
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.detekt) apply false
}

subprojects {
    plugins.withId("org.jetbrains.kotlin.jvm") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
            jvmToolchain(17)
        }
    }
    plugins.withId("org.jetbrains.kotlin.android") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension> {
            jvmToolchain(17)
        }
    }
}
```

## 26-3. .github/workflows/android-ci.yml

```yaml
name: Android CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'

      - name: Grant execute permission for gradlew
        run: chmod +x ./gradlew

      - name: Verify Constitution Compliance
        run: |
          bash scripts/verify-no-server.sh
          bash scripts/verify-network-policy.sh
          bash scripts/verify-no-mapping.sh
          bash scripts/verify-frozen-model.sh
          bash scripts/verify-strings-i18n.sh

      - name: Stage 0 FREEZE Verification
        run: ./gradlew :core:common:test --tests "FreezeMarkerTest"

      - name: Detekt Static Analysis
        run: ./gradlew detekt

      - name: Build Debug APK
        run: ./gradlew assembleDebug

      - name: Run Unit Tests
        run: ./gradlew testDebugUnitTest

      - name: Upload Test Report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-report
          path: '**/build/reports/tests/'
```

## 26-4. Detekt 규칙

- **`ForbiddenRawField`**: ExtractedSignal에 `rawSnippet`·`sourceProvider` 필드 금지
- **`NoHardcodedString`**: Kotlin 코드 내 한글·일본어·중국어 문자열 리터럴 금지 (strings.xml 강제)
- **`NoApiOfHttps`**: `http://` 하드코딩 금지 (https만)
- **`NoSystemOutPrintln`**: `println` 금지 (Logcat 또는 Timber만)

## 26-5. 릴리즈 플로우 (Fastlane, Mac 전용)

Infra_Ops v1.0에 명시된 대로 Fastlane은 Mac에서만 실행. Windows/Linux runner는 빌드·테스트만.

- `fastlane android beta` → Play Console 내부 테스트 트랙 업로드
- `fastlane android production` → 프로덕션 트랙 단계적 롤아웃 (10% → 50% → 100%)
- `fastlane ios beta` → App Store Connect TestFlight (Phase 7 iOS 진입 시)

---

