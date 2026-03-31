plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
}

android {
    namespace = "app.callcheck.mobile.data.search"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    // ═══════════════════════════════════════════════════════
    // 3층 테스트 역할 분리: VM은 Frozen/정적/빌드만 담당
    // LiveNetworkTest 카테고리는 VM에서 자동 제외
    // 대표님 로컬 PC에서만 실행 (scripts/local-test-rig/)
    // ═══════════════════════════════════════════════════════
    testOptions {
        unitTests.all {
            it.useJUnit {
                excludeCategories("app.callcheck.mobile.data.search.LiveNetworkTest")
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.okhttp)

    implementation(project(":core:model"))
    implementation(project(":core:util"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
