# Gradle / AGP / Kotlin Compatibility Matrix (Java 17)

| Tool   | Detected Version | Source                                   | Java 17 Support Range   | Verdict |
|--------|------------------|------------------------------------------|-------------------------|---------|
| Gradle | 8.6              | gradle/wrapper/gradle-wrapper.properties | 7.3+ runs, 8.x stable   | PASS    |
| AGP    | 8.4.0            | gradle/libs.versions.toml `agp`          | 7.4+, 8.x requires JDK17 | PASS    |
| Kotlin | 2.0.0            | gradle/libs.versions.toml `kotlin`       | 1.8+ stable, 2.0 native | PASS    |

Notes:
- Gradle 8.x requires JVM >= 8 and targets up to JDK 21; JDK 17 target fully supported.
- AGP 8.x officially mandates JDK 17 as the minimum runtime.
- Kotlin 2.0 supports `jvmTarget = "17"` and `jvmToolchain(17)` as first-class.
- No cell is out of range; no amber/red findings.
