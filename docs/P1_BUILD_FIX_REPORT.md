# P1 빌드 환경 정리 리포트

일시: 2026-03-24
작성: 비전 (AI Agent)

---

## 수정 요약

### 1. libs.versions.toml — OkHttp 4.12.0 추가
- `data:search` 모듈의 `GenericWebSearchProvider`가 OkHttp를 사용하는데 version catalog에 누락되어 있었음
- `versions` 섹션에 `okhttp = "4.12.0"` 추가
- `libraries` 섹션에 `okhttp` 라이브러리 정의 추가
- `data/search/build.gradle.kts`에 `implementation(libs.okhttp)` 추가

### 2. Kotlin 2.0.0 + Compose Compiler 호환성 해결
- **문제**: Kotlin 2.0.0과 Compose Compiler 1.5.11은 호환되지 않음 (1.5.x는 Kotlin 1.9.x 전용)
- **해결**: Kotlin 2.0의 공식 방식인 Compose Compiler Gradle Plugin으로 전환
  - `libs.versions.toml`에서 `androidx-compose-compiler` version 삭제
  - `kotlin-compose` 플러그인 추가 (`org.jetbrains.kotlin.plugin.compose`)
  - 4개 Compose 모듈에 `alias(libs.plugins.kotlin.compose)` 적용
  - 4개 모듈에서 `composeOptions { kotlinCompilerExtensionVersion }` 블록 삭제
- **대상 모듈**: `app`, `feature:decision-ui`, `feature:settings`, `feature:billing`

### 3. AndroidManifest.xml 정리
- `CallCheckContentProvider` 선언 삭제 — 구현 클래스가 존재하지 않음
- `CallActionReceiver`에서 `exported="true"` → `exported="false"` — 내부 전용 리시버이므로 외부 노출 불필요
- `CallActionReceiver`의 `intent-filter` 삭제 — exported=false이므로 intent-filter가 모순
- `CallScreeningService`에서 `android.telecom.InCallService` 액션 삭제 — CallScreeningService에는 해당 액션이 부적절

### 4. DI 바인딩 갭 해결
- **문제**: `CallInterceptRepositoryImpl`이 `DeviceEvidenceProvider`와 `SearchEvidenceProvider`를 주입받는데 구현체가 없었음
- **해결**:
  - `DeviceEvidenceProviderImpl` 생성 — `DeviceEvidenceRepository.gatherEvidence()` 위임
  - `SearchEvidenceProviderImpl` 생성 — `SearchEnrichmentRepository.enrichWithSearch()` 위임
  - `CallInterceptModule`에 3개 `@Binds` 추가 (Repository, DeviceProvider, SearchProvider)

### 5. call-intercept 모듈 의존성 추가
- `feature:device-evidence` — DeviceEvidenceRepository 참조 필요
- `feature:decision-engine` — DecisionEngine 인터페이스 참조 필요
- `data:search` — SearchEnrichmentRepository 참조 필요

### 6. SettingsScreen.kt 미사용 import 제거
- `import com.google.android.gms.common.util.DeviceProperties` 삭제 — 실제 사용하지 않음

### 7. 구모델 참조 전체 스캔 — 98개 파일 클린
- Sentiment, TrendCategory, UserAction, CallType — 0건
- RiskLevel.SAFE, RiskLevel.CRITICAL — 0건
- 구 DeviceEvidence 필드 (isMarkedAsSpam, callFrequency 등) — 0건
- 구 SearchEvidence 필드 (threatIndicators, overallSentiment 등) — 0건
- 구 ConclusionCategory 값 (SAFE_KNOWN, FRAUD_WARNING 등) — 0건

---

## DI 의존성 체인 (완전성 검증)

```
CallCheckScreeningService
  └── CallInterceptRepository (@Binds → CallInterceptRepositoryImpl)
        ├── DeviceEvidenceProvider (@Binds → DeviceEvidenceProviderImpl)
        │     └── DeviceEvidenceRepository (@Provides → DeviceEvidenceRepositoryImpl)
        │           ├── ContactsDataSource
        │           ├── CallLogDataSource
        │           └── SmsMetadataDataSource
        ├── SearchEvidenceProvider (@Binds → SearchEvidenceProviderImpl)
        │     └── SearchEnrichmentRepository (@Provides → SearchEnrichmentRepositoryImpl)
        │           ├── SearchProviderRegistry
        │           └── SearchResultAnalyzer
        └── DecisionEngine (@Binds → DecisionEngineImpl)
              ├── RiskBadgeMapper (@Inject constructor)
              ├── ActionMapper (@Inject constructor)
              └── SummaryGenerator (@Inject constructor)
```

모든 노드에 Hilt 바인딩 존재 확인 완료.

---

## 대표님 로컬 빌드 가이드

이 환경에 Android SDK가 없어서 `assembleDebug`를 실행할 수 없었습니다.
대표님 로컬에서 빌드하려면:

```bash
cd android/

# 1. Gradle wrapper 생성 (최초 1회)
gradle wrapper --gradle-version 8.6

# 2. local.properties 설정
echo "sdk.dir=/path/to/Android/Sdk" > local.properties

# 3. 빌드
./gradlew assembleDebug
```

---

## 변경 파일 목록

| 파일 | 변경 내용 |
|------|----------|
| `gradle/libs.versions.toml` | OkHttp 추가, compose-compiler 삭제, compose plugin 추가 |
| `app/build.gradle.kts` | compose plugin 적용, composeOptions 삭제 |
| `feature/decision-ui/build.gradle.kts` | compose plugin 적용, composeOptions 삭제 |
| `feature/settings/build.gradle.kts` | compose plugin 적용, composeOptions 삭제 |
| `feature/billing/build.gradle.kts` | compose plugin 적용, composeOptions 삭제 |
| `data/search/build.gradle.kts` | okhttp 의존성 추가 |
| `feature/call-intercept/build.gradle.kts` | 3개 모듈 의존성 추가 |
| `feature/call-intercept/.../DeviceEvidenceProviderImpl.kt` | **신규** |
| `feature/call-intercept/.../SearchEvidenceProviderImpl.kt` | **신규** |
| `feature/call-intercept/.../di/CallInterceptModule.kt` | 3개 @Binds 추가 |
| `app/src/main/AndroidManifest.xml` | ContentProvider 삭제, Receiver 보안, InCallService 삭제 |
| `feature/settings/.../SettingsScreen.kt` | 미사용 import 삭제 |
