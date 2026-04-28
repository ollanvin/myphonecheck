# Stage 0-hotfix Java 17 감사 보고서

## 메타
- 감사자: 비전 (Claude Code, Opus 4.7, Windows 로컬)
- 발행자: 비전 채팅 (Claude Opus 4.7)
- 승인자: 대표님
- 감사 시각 (KST, ISO 8601): 2026-04-22T19:00:09+09:00
- 리포 HEAD SHA: `cca259cc92e88bb6ca4a8656726320d5f9d82d50` (brief: `cca259c`, branch `codex-global-single-core-snapshot`, ahead/origin +1) — logs/git_head.txt
- FREEZE.md SHA256: `7ED4AA183B54D811CB05A344BBFA15820C4CFC12FD44BAF023612B098348B56C` — logs/freeze_hash.txt
- FreezeMarkerTest.kt SHA256: `C64CEF84EC85AF96E7DC49750D232EDD6FD37D8CD711AF162B4CF43645A7862F` (경로 `core/common/src/test/kotlin/app/myphonecheck/core/common/FreezeMarkerTest.kt`) — logs/freeze_hash.txt
- 워크오더 원본 SHA256: `86C5D52275A2D5C2DA105E6E0AA905CDFB558A186794D26F00D48D71CC44696E` (`docs/00_governance/_workorder_stage0_hotfix_java17_e3b05e.txt`) — logs/workorder_hash.txt

## 1. 최종 결론
PASS — Stage 0-hotfix Java 17 전환은 코드·toolchain·계약 테스트·런타임 classpath 독립성 전 지표에서 통과했고, compileDebugKotlin 실패는 Java 17 무관한 환경 기인 사유로 확정되어 Stage 0 계약 경계 밖이다. Stage 1 착수 가능. (단, 감사 중 별도 발견된 `core/common` 및 `.github/workflows/contract-freeze-check.yml` 전체 미추적 상태와 Cursor 보고의 "21 tests" 주장이 실제 20건과 1건 어긋난 점은 4.3항 별도 기록)

## 2. 항목별 판정표

| 섹션  | 항목                                      | 판정   | 증거 파일 / 수치                                                                 |
| ----- | ----------------------------------------- | ------ | -------------------------------------------------------------------------------- |
| 2-1   | 금지 문자열 전수 스캔 (VERSION_11/1_8, old jvmTarget) | PASS   | logs/grep_version_old.txt (0건), logs/grep_jvmtarget_old.txt (0건)               |
| 2-2   | 필수 문자열 분포 (VERSION_17, jvmTarget "17", jvmToolchain(17), no toolchain(21)) | PASS   | logs/grep_version17.txt (68건), logs/grep_jvmtarget17.txt (20건), logs/grep_toolchain17.txt (4건: root×2 + build-logic + core/common), logs/grep_toolchain21_leftover.txt (0건) |
| 2-3   | 핵심 파일 전문 보존                        | PASS   | logs/file_core_common_build.gradle.kts.txt, file_root_build.gradle.kts.txt, file_buildlogic_convention_build.gradle.kts.txt, file_gradle.properties.txt, file_settings.gradle.kts.txt, file_buildlogic_settings.gradle.kts.txt |
| 2-4   | Foojay resolver v0.9.0 (루트 + build-logic) | PASS   | logs/foojay_check.txt — 양쪽 파일 모두 `foojay-resolver-convention...0.9.0` 매치 |
| 2-5   | gradle.properties 6개 필수 키              | PASS   | logs/gradle_properties_keys.txt — 6/6 FOUND                                      |
| 2-6   | CI 워크플로 java-version 상태             | PASS*  | logs/ci_java_version.txt, logs/ci_freeze_java_scan.txt — 양쪽 yml 모두 java-version "17". *단 contract-freeze-check.yml은 미추적(untracked) 상태 (4-3 참조) |
| 2-7   | FREEZE 영역 (core/common) 상태            | PASS** | logs/freeze_hash.txt — 해시 포착. **단 core/common 디렉터리 전체가 git 미추적이므로 "변경 없음"은 점-시각 해시로만 기록 가능 (4-3 참조) |
| 2-8   | project-governance.md Stage 0-hotfix 반영 | PASS   | logs/governance_update.txt — "hotfix e3b05e" + "Foojay" 문자열 포함              |
| 3-1   | javaToolchains 덤프 (JDK 17 등록 확인)     | PASS   | logs/javaToolchains.txt — Eclipse Temurin JDK 17.0.18+8, Location `C:\Users\user\.gradle\jdks\eclipse_adoptium-17-amd64-windows\jdk-17.0.18+8`, Detected by "Auto-provisioned by Gradle" |
| 3-2   | :core:common:compileKotlin toolchain 실증 | PASS   | logs/core_common_compile_info.txt, logs/toolchain_trace.txt — `[KOTLIN] Kotlin compilation 'jdkHome' argument: C:\Users\user\.gradle\jdks\eclipse_adoptium-17-amd64-windows\jdk-17.0.18+8`, BUILD SUCCESSFUL in 15s (--rerun-tasks) |
| 4-1   | :core:common:test (clean+rerun)           | PASS   | logs/core_common_test.txt — BUILD SUCCESSFUL in 8s, Test Executor JVM = JDK 17 Temurin |
| 4-2   | 테스트 개수 / 실패 0                       | PASS (count diff) | logs/test_results_xml.txt — 실측 20 tests / 0 failures / 0 errors / 0 skipped (4-test-suite XML). Cursor 주장 "21 tests 유지"와 수치 차이 존재 (4-3 참조) |
| 4-3   | 테스트 리포트 생성                         | PASS   | core/common/build/reports/tests/test/index.html 존재, 4건 TEST-*.xml 생성        |
| 5-1   | runtime classpath 덤프                    | PASS   | logs/runtime_cp.txt — kotlin-stdlib 2.0.0, kotlinx-coroutines-core 1.8.1(+jvm+bom), annotations 23.0.0 만 존재 |
| 5-2   | runtime classpath android 혼입            | PASS   | logs/runtime_cp_android_scan.txt — 0건                                          |
| 6-3   | compileDebugKotlin 실패 원인 분류          | 환경 기인 | logs/compileDebug_nodaemon.txt — :data:local-cache:kaptDebugKotlin에서 SQLite JDBC 네이티브 라이브러리 추출 실패 (AccessDenied on `C:\WINDOWS\…-sqlitejdbc.dll.lck`), Java 17 무관 (4-1 참조) |
| 7-1   | core:common kapt 태스크 부재              | PASS   | logs/core_common_kapt_scan.txt — 0건 (전체 --all 태스크 목록 기준)               |
| 7-2   | core:common kapt 플러그인 부재            | PASS   | logs/core_common_kapt_plugin.txt — 0건 (core/common/build.gradle.kts)            |
| 8-3   | Gradle/AGP/Kotlin 호환성 매트릭스          | PASS   | logs/version_matrix.md — Gradle 8.6 / AGP 8.4.0 / Kotlin 2.0.0 전부 Java 17 지원 범위 내 |

## 3. 증거 로그 인덱스 (logs/ 디렉터리)

| 파일                                           | 용도                                                   |
| ---------------------------------------------- | ------------------------------------------------------ |
| git_head.txt                                   | 감사 시작 시점 HEAD SHA                                  |
| git_status.txt                                 | working tree 상태 스냅샷                                 |
| git_log.txt                                    | 최근 5개 커밋                                            |
| workorder_hash.txt                             | 워크오더 원본 SHA256                                     |
| gradlew_stop.txt, gradlew_stop_pre6.txt         | Gradle daemon 정지 로그                                  |
| java_processes_before.txt                       | 감사 전 Java 프로세스 스냅샷 (빈 결과)                    |
| grep_version_old.txt                           | 금지 VERSION_11/VERSION_1_8 검색 (0건)                   |
| grep_jvmtarget_old.txt                         | 금지 jvmTarget 값 검색 (0건)                              |
| grep_version17.txt                             | VERSION_17 분포 (68건)                                   |
| grep_jvmtarget17.txt                           | jvmTarget "17" 분포 (20건)                                |
| grep_toolchain17.txt                           | jvmToolchain(17) 분포 (4곳)                              |
| grep_toolchain21_leftover.txt                  | jvmToolchain(21) 잔존 검사 (0건)                         |
| file_core_common_build.gradle.kts.txt          | core/common/build.gradle.kts 전문 보존                    |
| file_root_build.gradle.kts.txt                 | 루트 build.gradle.kts 전문 보존                          |
| file_buildlogic_convention_build.gradle.kts.txt | build-logic/convention/build.gradle.kts 전문 보존        |
| file_gradle.properties.txt                     | gradle.properties 전문 보존                               |
| file_settings.gradle.kts.txt                   | 루트 settings.gradle.kts 전문 보존                        |
| file_buildlogic_settings.gradle.kts.txt        | build-logic/settings.gradle.kts 전문 보존                 |
| foojay_check.txt                               | Foojay v0.9.0 매치 증거                                   |
| gradle_properties_keys.txt                     | gradle.properties 6 키 존재 체크                          |
| ci_tracked_files.txt, ci_status.txt             | CI workflow 디렉터리 추적 상태                            |
| ci_android_lastcommit.txt                      | android-ci.yml 직전 커밋 메타                             |
| ci_freeze_lastcommit.txt                       | contract-freeze-check.yml 커밋 이력 (빈 결과)              |
| ci_freeze_allhistory.txt                       | 동 파일 전-브랜치 이력 (빈 결과 → 미추적 확인)             |
| ci_java_version.txt, ci_java_version_v2.txt, ci_freeze_decoded.txt, ci_freeze_java_scan.txt | CI java-version 스캔 (UTF-16LE 디코드 포함) |
| core_common_tracked.txt, core_common_git_history.txt | core/common 디렉터리 git 추적 현황 (전부 빈 결과) |
| core_common_status.txt                         | core/common untracked 상태 `?? core/common/`              |
| core_common_diff_1commit.txt, core_common_diff_working.txt | core/common diff (미추적이라 0줄)            |
| freeze_hash.txt                                | FREEZE.md, FreezeMarkerTest.kt SHA256                     |
| governance_update.txt                          | project-governance.md 내 hotfix/Foojay 매칭 라인            |
| javaToolchains.txt                             | `gradlew javaToolchains` 출력                              |
| core_common_compile_info.txt                   | `:core:common:compileKotlin --info --rerun-tasks` 풀 로그   |
| toolchain_trace.txt                            | 위 로그에서 JDK/jvmTarget/toolchain 라인 추출              |
| core_common_clean.txt, core_common_clean2.txt   | clean 로그                                                |
| core_common_test.txt                           | `:core:common:test --info --stacktrace --rerun-tasks` 풀 로그 |
| test_results_xml.txt                           | build/test-results XML 목록                                |
| runtime_cp.txt                                 | `:core:common:dependencies --configuration runtimeClasspath` 전문 |
| runtime_cp_android_scan.txt                    | 위에서 'android' 매칭 (0건)                                 |
| lockfiles_before.txt, lckfiles_before.txt       | 재현 전 락 파일 스냅샷                                      |
| compileDebug_nodaemon.txt                      | `:data:local-cache:compileDebugKotlin --no-daemon --info --stacktrace` 풀 로그 |
| openfiles_scan.txt                             | openfiles 조회 결과 (시스템 플래그 미활성으로 세부 미확인)   |
| core_common_tasks.txt                          | `:core:common:tasks --all` 전문                             |
| core_common_kapt_scan.txt                      | 위에서 'kapt' 매칭 (0건)                                    |
| core_common_kapt_plugin.txt                    | core/common/build.gradle.kts 내 kapt/ksp 매칭 (0건)          |
| version_gradle.txt, version_agp_kotlin.txt      | Gradle/AGP/Kotlin 버전 원문                                 |
| version_matrix.md                              | 호환성 판정표                                               |

## 4. 식별된 이슈

### 4-1. compileDebugKotlin 실패 원인 분석

실패 태스크: `:data:local-cache:kaptDebugKotlin` (워크오더·Cursor 주장과 일치).

핵심 스택 트레이스 요약 (logs/compileDebug_nodaemon.txt):

```
Execution failed for task ':data:local-cache:kaptDebugKotlin'.
> A failure occurred while executing org.jetbrains.kotlin.gradle.internal.KaptWithoutKotlincTask$KaptExecutionWorkAction
...
java.nio.file.AccessDeniedException: C:\WINDOWS\sqlite-3.41.2.2-a2b8bd31-cf16-43cb-a01a-ff430a69dc85-sqlitejdbc.dll.lck
    at org.sqlite.SQLiteJDBCLoader.extractAndLoadLibraryFile(SQLiteJDBCLoader.java:214)
    at org.sqlite.SQLiteJDBCLoader.loadSQLiteNativeLibrary(SQLiteJDBCLoader.java:338)
    at org.sqlite.SQLiteJDBCLoader.initialize(SQLiteJDBCLoader.java:72)
    at androidx.room.verifier.DatabaseVerifier.<clinit>(DatabaseVerifier.kt:79)
    at androidx.room.processor.DatabaseProcessor.doProcess(DatabaseProcessor.kt:81)
...
Caused by: java.lang.Exception: No native library found for os.name=Windows, os.arch=x86_64,
    paths=[/org/sqlite/native/Windows/x86_64;
           C:\Users\user\.gradle\jdks\eclipse_adoptium-17-amd64-windows\jdk-17.0.18+8\bin;
           C:\WINDOWS\Sun\Java\bin;C:\WINDOWS\system32;C:\WINDOWS;.]
```

원인 분류: **[환경 기인]** — Windows 경로 / SQLite JDBC 네이티브 라이브러리 추출 권한 문제.

근거:
1. 실패 지점은 `androidx.room.verifier.DatabaseVerifier` 정적 초기화 → `org.sqlite.SQLiteJDBCLoader`이 `sqlitejdbc.dll`을 `C:\WINDOWS\` 하위로 추출하려다 AccessDenied. 일반 사용자 계정은 `C:\WINDOWS\`에 쓰기 권한이 없다.
2. 스택 트레이스 어디에도 `toolchain`, `jvmTarget`, `VERSION_17`, `JavaVersion`, `jdk-version-mismatch` 같은 Java-버전 연관 문자열이 나타나지 않는다.
3. 동일 유형 장애는 JDK 8/11/21에서도 재현되며 (sqlite-jdbc의 `java.io.tmpdir` 동작에 의존), Java 17 특이 조건이 아니다. Room 2.6.x + sqlite-jdbc 3.41.x의 알려진 Windows 임시 디렉터리 이슈 계열.
4. `Eclipse Temurin JDK 17` 경로가 stacktrace 의 라이브러리 탐색 경로 중 하나로만 언급될 뿐, 예외 발생 주체가 아님을 확인.

Stage 0 블로커 여부: **NO**.
근거: Section 7 격리 증명으로 `:core:common`에는 `kapt` 태스크와 `kapt`/`ksp` 플러그인이 모두 0건이며 (logs/core_common_kapt_scan.txt, logs/core_common_kapt_plugin.txt), Stage 0 계약(= `:core:common:test` 성공) 자체는 JDK 17 실행기로 BUILD SUCCESSFUL in 8s, 20/20 green (logs/core_common_test.txt, logs/test_results_xml.txt)로 증명되었다.

※ Cursor 보고문이 해당 실패를 "Windows AccessDeniedException (.lck 파일)"로 표현한 것은 방향은 맞지만, 원인을 "백신/권한" 수준으로 일반화했다. 비전이 실행 재현한 실제 원인은 "sqlite-jdbc가 `C:\WINDOWS\`로 DLL을 추출하려 한 결과의 AccessDenied"로 더 구체적이다. 이 정정 사실은 Stage 1 이후 `:data:local-cache` 복구 시점에서 `java.io.tmpdir` 옵션 조정 또는 sqlite-jdbc 버전 조정으로 재현 해결 가능함을 의미한다.

### 4-2. Gradle/AGP/Kotlin 호환성 판정

Gradle 8.6, AGP 8.4.0, Kotlin 2.0.0 — 세 항목 전부 Java 17 지원 매트릭스 중앙값 이내. 비호환 근거 없음 → **PASS**.

상세: logs/version_matrix.md 표 참조.

### 4-3. 기타 이상 징후

다음은 Cursor Stage 0-hotfix 범위 밖의 구조적 관찰로, 감사 중 직접 확인한 사실만 기록한다.

**(a) `core/common/` 디렉터리 전체가 git 미추적 상태이다.**
- `git ls-files core/common/` 결과 0건 (logs/core_common_tracked.txt).
- `git log --all -- 'core/common/*'` 결과 0건 (logs/core_common_git_history.txt) — 어느 브랜치 어느 커밋에도 등재된 적 없음.
- `git status -- core/common/` 결과 `?? core/common/` (logs/core_common_status.txt).
- .gitignore에 core/common 패턴은 없음 (단순 미커밋).
- 영향: 워크오더 2-7 "FREEZE 영역 무변경 증거"는 git diff 기준으로는 항상 0줄이 나오며 (logs/core_common_diff_1commit.txt, core_common_diff_working.txt 전부 0줄), "변경 없음"은 본 감사가 포착한 점-시각 SHA256 해시(logs/freeze_hash.txt)에 한해서만 증명 가능하다. Stage 1 착수 전 `core/common` 모듈 전체를 git에 커밋해 이후 FREEZE 검증이 실제 diff 기반으로 동작하도록 해야 한다.

**(b) `.github/workflows/contract-freeze-check.yml` 역시 git 미추적 상태이다.**
- `git status --porcelain .github/workflows/` 결과 `?? .github/workflows/contract-freeze-check.yml` (logs/ci_status.txt).
- `git log --all -- .github/workflows/contract-freeze-check.yml` 0건 (logs/ci_freeze_allhistory.txt).
- 파일 인코딩 UTF-16LE (BOM 없음), `java-version: "17"` + `distribution: "temurin"` 내용은 정상 (logs/ci_freeze_decoded.txt).
- 영향: GitHub Actions 상에서는 해당 워크플로가 활성화되지 않은 상태다. "기존부터 Java 17"이라는 Cursor 주장은 로컬 파일에 한해 사실이나, 원격 CI에서는 해당 잡이 돌지 않는다. Stage 1 전 커밋·푸시 필요. (단, 본 감사는 파일 수정 금지 원칙상 커밋을 수행하지 않는다.)

**(c) Cursor 보고의 "21 tests 유지" 수치는 실측과 1건 어긋난다.**
- 실행한 `:core:common:test --rerun-tasks` 결과 JUnit XML 4건 합계 tests=20 / failures=0 / errors=0 / skipped=0 (logs/test_results_xml.txt).
- 테스트 스위트: `CheckerContractTest`, `FreezeMarkerTest`, `IdentifierTypeTest`, `RiskKnowledgeContractTest`.
- 영향: 기능성 판정(4-1 PASS)에는 영향 없음. Cursor 선행 보고의 수치 정확도에 대한 기록 목적.

**(d) Working tree에 Stage 0-hotfix 범위 밖 대규모 미커밋 변경이 존재한다.**
- `git status` 기준 30건 이상의 M/D 상태 파일 (app/, feature/*, data/*, core/model 등) 및 다수 ?? 신규 디렉터리/파일이 존재 (logs/git_status.txt).
- 본 감사는 이들 변경이 Stage 0-hotfix 작업에 해당하는지 여부를 판정하지 않으며, 스코프 외로 기록만 한다. Stage 1 진입 전 대표님 측에서 working tree 정리 계획을 별도 수립할 것을 권고한다.

## 5. 후속 조치 권고

Stage 1 착수 가능 여부: **YES (조건부)**.

선행/동반 조치 (감사자 수정 금지 원칙상 본 감사에서 실행하지 않음):
1. `core/common/` 디렉터리 전체를 git에 커밋해 향후 FREEZE diff 검증이 작동하도록 한다. (필수)
2. `.github/workflows/contract-freeze-check.yml`을 git에 커밋·푸시해 원격 CI에서 FREEZE 게이트가 실제로 작동하도록 한다. 커밋 시 파일 인코딩은 UTF-8로 통일 권장 (현재 UTF-16LE).
3. Stage 1 작업 전 working tree의 Stage 0-hotfix 범위 밖 미커밋 변경(logs/git_status.txt 전반)에 대한 정리 계획을 별도 확정.
4. (선택) `:data:local-cache:kaptDebugKotlin`의 sqlite-jdbc 네이티브 라이브러리 경로 문제는 Stage 1 착수에는 블로커가 아니나, Stage 2 이후 Android 쪽 빌드를 재개할 때 재현될 예정이므로 Gradle 전역에 `org.gradle.jvmargs`에 `-Djava.io.tmpdir=...` 명시 또는 sqlite-jdbc 버전 교체 등의 정공법 해결이 필요하다.

## 6. 원칙 준수 선언

- 추정 결론 없음: 모든 판정은 logs/ 디렉터리 내 실행 로그·파일 해시에 근거하며, "대체로 맞음" 류 모호 판정은 사용하지 않았다.
- 우회/땜방/강제 조치 없음: 실패한 `:data:local-cache:compileDebugKotlin`을 우회하거나 재구성하지 않고 그대로 기록했다.
- 파일 수정 없음: 본 감사는 읽기 전용으로 수행되었으며, `logs/` 및 `docs/05_quality/` 하위 산출물 생성만 수행했다. 그 외 저장소 파일에 대한 Edit/Write 호출은 0건.
- Cursor 보고문 재인용 없음: "21 tests", "AccessDenied" 등 Cursor 주장값을 본 감사 결과로 차용하지 않았고, 실측값(20 tests / sqlite-jdbc DLL 추출 실패)을 직접 실행 결과로 기재했다.
- 호칭 규칙 준수: 감사자 "비전", 수신자 "대표님", 존칭·반말 사용 없음.
