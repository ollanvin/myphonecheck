import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.GradleException
import org.gradle.kotlin.dsl.register
import java.io.File

/**
 * HardcodedStringDetectorPlugin
 *
 * Compose UI 소스에서 하드코딩된 CJK 문자열을 빌드 시점에 감지.
 *
 * ═══════════════════════════════════════════════
 * 감지 대상:
 *   - Text("...한글...") — Compose Text composable에 직접 한국어/CJK 리터럴
 *   - text|label|title|description|placeholder|contentDescription = "...한글..."
 *     → Compose import가 있는 파일에서만 적용 (데이터 클래스 false positive 방지)
 *   - getString("...한글...") 등 하드코딩 패턴
 *
 * 허용 예외 (false positive 제거):
 *   - 주석 (// 또는 /* */)
 *   - Log.x(TAG, "...") — 로그 태그/메시지
 *   - const val / companion object 내 상수 정의
 *   - @Suppress 어노테이션
 *   - @Preview 어노테이션이 있는 라인
 *   - Compose import가 없는 파일의 named parameter 패턴
 *
 * 등록: root build.gradle.kts subprojects 블록에서 Android 모듈에 자동 적용.
 * check lifecycle에 자동 연결 — `./gradlew check` 시 실행.
 * ═══════════════════════════════════════════════
 */
class HardcodedStringDetectorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val detectTask = tasks.register<org.gradle.api.DefaultTask>("detectHardcodedStrings") {
                group = "verification"
                description = "Detect hardcoded CJK strings in Compose UI source files"

                doLast {
                    val violations = mutableListOf<String>()

                    // Pattern 1: Always check — Text() composable with CJK
                    val textComposablePattern = Regex(
                        """Text\s*\(\s*(?:text\s*=\s*)?"[^"]*[\u3000-\u9FFF\uAC00-\uD7AF\uF900-\uFAFF][^"]*""""
                    )

                    // Pattern 2: Compose-only — named parameter with CJK
                    // Only applied to files that import androidx.compose.*
                    val namedParamPattern = Regex(
                        """(?:text|label|title|description|placeholder|contentDescription)\s*=\s*"[^"]*[\u3000-\u9FFF\uAC00-\uD7AF\uF900-\uFAFF][^"]*""""
                    )

                    // Pattern 3: Always check — setContent/getString with hardcoded CJK
                    val androidUiPatterns = listOf(
                        Regex("""\.setContentTitle\s*\([^)]*"[^"]*[\u3000-\u9FFF\uAC00-\uD7AF\uF900-\uFAFF][^"]*""""),
                        Regex("""\.setContentText\s*\([^)]*"[^"]*[\u3000-\u9FFF\uAC00-\uD7AF\uF900-\uFAFF][^"]*""""),
                        Regex("""\.addAction\s*\([^,]*,\s*"[^"]*[\u3000-\u9FFF\uAC00-\uD7AF\uF900-\uFAFF][^"]*""""),
                    )

                    // Lines to skip
                    val skipPatterns = listOf(
                        Regex("""^\s*//"""),
                        Regex("""^\s*\*"""),
                        Regex("""^\s*/\*"""),
                        Regex("""Log\.[divwev]\s*\("""),
                        Regex("""const\s+val"""),
                        Regex("""@Suppress"""),
                        Regex("""@Preview"""),
                        Regex("""TAG\s*="""),
                    )

                    // Compose import indicator
                    val composeImportPattern = Regex("""import\s+androidx\.compose\.""")

                    val srcDirs = listOf(
                        file("src/main/kotlin"),
                        file("src/main/java"),
                    )

                    srcDirs.filter { it.exists() }.forEach { srcDir ->
                        srcDir.walkTopDown()
                            .filter { it.extension == "kt" }
                            .forEach { ktFile ->
                                val lines = ktFile.readLines()
                                val isComposeFile = lines.any { composeImportPattern.containsMatchIn(it) }

                                lines.forEachIndexed { index, line ->
                                    val lineNum = index + 1
                                    val trimmed = line.trim()

                                    // Skip non-UI lines
                                    if (skipPatterns.any { it.containsMatchIn(trimmed) }) return@forEachIndexed

                                    // Pattern 1: Text() — always check
                                    if (textComposablePattern.containsMatchIn(line)) {
                                        val relativePath = ktFile.relativeTo(projectDir).path
                                        violations.add("$relativePath:$lineNum → $trimmed")
                                        return@forEachIndexed
                                    }

                                    // Pattern 2: named params — Compose files only
                                    if (isComposeFile && namedParamPattern.containsMatchIn(line)) {
                                        val relativePath = ktFile.relativeTo(projectDir).path
                                        violations.add("$relativePath:$lineNum → $trimmed")
                                        return@forEachIndexed
                                    }

                                    // Pattern 3: Android notification/view APIs — always check
                                    for (pattern in androidUiPatterns) {
                                        if (pattern.containsMatchIn(line)) {
                                            val relativePath = ktFile.relativeTo(projectDir).path
                                            violations.add("$relativePath:$lineNum → $trimmed")
                                            return@forEachIndexed
                                        }
                                    }
                                }
                            }
                    }

                    if (violations.isNotEmpty()) {
                        val report = buildString {
                            appendLine()
                            appendLine("═══════════════════════════════════════════════════")
                            appendLine("  HARDCODED CJK STRING DETECTED IN UI CODE")
                            appendLine("═══════════════════════════════════════════════════")
                            appendLine()
                            appendLine("  Compose: use stringResource(R.string.xxx)")
                            appendLine("  Android: use context.getString(R.string.xxx)")
                            appendLine()
                            violations.forEach { appendLine("  ✗ $it") }
                            appendLine()
                            appendLine("  Total: ${violations.size} violation(s)")
                            appendLine("═══════════════════════════════════════════════════")
                        }
                        throw GradleException(report)
                    } else {
                        logger.lifecycle("✓ No hardcoded CJK strings found in UI code")
                    }
                }
            }

            // Wire into `check` lifecycle
            tasks.matching { it.name == "check" }.configureEach {
                dependsOn(detectTask)
            }
        }
    }
}
