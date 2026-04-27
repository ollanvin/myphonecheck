plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
}

android {
    namespace = "app.myphonecheck.mobile"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "app.myphonecheck.mobile"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        // MissingTranslation: 5개 locale(ar/es/ja/ru/zh) 번역 적체 — 별도 트랙 관리
        // error → warning 강등. 번역 추가 시 이 설정 제거 예정.
        warning += "MissingTranslation"
    }
}

dependencies {
    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // AndroidX Lifecycle
    implementation(libs.bundles.androidx.lifecycle)

    // AndroidX Activity & Compose
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.androidx.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Hilt
    implementation(libs.bundles.hilt)
    kapt(libs.hilt.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // SQLCipher — Application.onCreate()에서 loadLibs 호출 위해 직접 의존성 필요
    implementation(libs.sqlcipher.android)

    // Gson
    implementation(libs.gson)

    // DataStore
    implementation(libs.datastore.preferences)

    // DocumentFile (SAF)
    implementation(libs.documentfile)

    // Phone number utilities
    implementation(libs.libphonenumber)

    // Play Services
    implementation(libs.play.services.base)

    // Play Billing
    implementation(libs.play.billing)

    // Feature modules
    implementation(project(":feature:call-intercept"))
    implementation(project(":feature:device-evidence"))
    implementation(project(":feature:search-enrichment"))
    implementation(project(":feature:decision-engine"))
    implementation(project(":feature:decision-ui"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:billing"))
    implementation(project(":feature:country-config"))
    // feature:push-intercept removed per v1.1 Architecture (PUSH REMOVED)
    implementation(project(":feature:message-intercept"))
    implementation(project(":feature:privacy-check"))
    implementation(project(":feature:push-trash"))
    implementation(project(":feature:card-check"))

    // Data modules
    implementation(project(":data:contacts"))
    implementation(project(":data:calllog"))
    implementation(project(":data:sms"))
    implementation(project(":data:search"))
    implementation(project(":data:local-cache"))

    // Core modules
    implementation(project(":core:model"))
    implementation(project(":core:util"))
    implementation(project(":core:security"))

    // WorkManager
    implementation(libs.work.runtime.ktx)
    implementation(libs.hilt.work)
    kapt(libs.hilt.work.compiler)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

hilt {
    enableAggregatingTask = true
}

kapt {
    correctErrorTypes = true
}
