import java.io.File
import org.jetbrains.kotlin.gradle.internal.KaptWithoutKotlincTask

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
}

// Room's annotation processor loads sqlite-jdbc, which extracts native DLLs. Gradle KAPT
// workers do not inherit org.gradle.jvmargs, so point tmp/native dirs at the build tree.
tasks.withType<KaptWithoutKotlincTask>().configureEach {
    val tmpRoot = layout.buildDirectory.dir("kapt-tmp").get().asFile
    val sqliteDir = File(tmpRoot, "sqlite-native")
    tmpRoot.mkdirs()
    sqliteDir.mkdirs()
    val rootPath = tmpRoot.absolutePath.replace('\\', '/')
    val sqlitePath = sqliteDir.absolutePath.replace('\\', '/')
    kaptProcessJvmArgs.addAll(
        "-Djava.io.tmpdir=$rootPath",
        "-Dorg.sqlite.tmpdir=$sqlitePath",
    )
}

android {
    namespace = "app.myphonecheck.mobile.data.localcache"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

kapt {
    arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // SQLCipher for encrypted Room DB
    implementation(libs.sqlcipher.android)
    implementation(libs.sqlite.ktx)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(project(":core:model"))
    implementation(project(":core:util"))
    implementation(project(":core:security"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
