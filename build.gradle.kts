plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt) apply false
    id("myphonecheck.hardcoded.detector") apply false
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    // 전체 프로젝트 lint 정책 — MissingTranslation 경고 강등
    pluginManager.withPlugin("com.android.application") {
        apply(plugin = "myphonecheck.hardcoded.detector")
        extensions.configure<com.android.build.gradle.BaseExtension> {
            lintOptions {
                warning("MissingTranslation")
            }
            testOptions {
                unitTests.isReturnDefaultValues = true
            }
        }
    }
    pluginManager.withPlugin("com.android.library") {
        apply(plugin = "myphonecheck.hardcoded.detector")
        extensions.configure<com.android.build.gradle.BaseExtension> {
            lintOptions {
                warning("MissingTranslation")
            }
            testOptions {
                unitTests.isReturnDefaultValues = true
            }
        }
    }
}
