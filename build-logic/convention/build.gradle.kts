plugins {
    `kotlin-dsl`
}

group = "app.myphonecheck.mobile.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "myphonecheck.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "myphonecheck.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "myphonecheck.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("hardcodedStringDetector") {
            id = "myphonecheck.hardcoded.detector"
            implementationClass = "HardcodedStringDetectorPlugin"
        }
    }
}
