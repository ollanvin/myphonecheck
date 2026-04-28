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

    // 헌법 §9-1 빅테크 정공법: values-xx 수동 번역 파일 0, 영문 default + OS 위임.
    // MissingTranslation 강등은 §9-1 정합 후 불필요 (번역 누락 발생 0) → 제거.

    // 헌법 §9-6 검증·테스트 정공법: Gradle Managed Devices 4종 매트릭스
    // (단일 디바이스 검증 금지, API 28/31/33/34 + 폰·태블릿 form factor)
    @Suppress("UnstableApiUsage")
    testOptions {
        managedDevices {
            localDevices {
                create("pixel4Api28") {
                    device = "Pixel 4"
                    apiLevel = 28
                    systemImageSource = "google"
                }
                create("pixel5Api31") {
                    device = "Pixel 5"
                    apiLevel = 31
                    systemImageSource = "google"
                }
                create("tabletApi33") {
                    device = "Pixel Tablet"
                    apiLevel = 33
                    systemImageSource = "google"
                }
                create("pixel7Api34") {
                    device = "Pixel 7"
                    apiLevel = 34
                    systemImageSource = "google"
                }
            }
            groups {
                create("ciMatrix") {
                    targetDevices.add(localDevices.getByName("pixel4Api28"))
                    targetDevices.add(localDevices.getByName("pixel5Api31"))
                    targetDevices.add(localDevices.getByName("tabletApi33"))
                    targetDevices.add(localDevices.getByName("pixel7Api34"))
                }
                create("prGate") {
                    targetDevices.add(localDevices.getByName("pixel7Api34"))
                }
            }
        }
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
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:privacy-check"))
    implementation(project(":feature:push-trash"))
    implementation(project(":feature:card-check"))
    implementation(project(":feature:call-check"))
    implementation(project(":feature:message-check"))
    implementation(project(":feature:initial-scan"))
    implementation(project(":feature:call-screening"))
    implementation(project(":feature:sms-block"))
    implementation(project(":feature:tag-system"))

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
    implementation(project(":core:global-engine"))

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
