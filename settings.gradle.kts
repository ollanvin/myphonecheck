pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "MyPhoneCheck"

include(":app")

// Core modules
include(":core:common")
include(":core:model")
include(":core:util")
include(":core:security")
include(":core:global-engine")

// Feature modules
include(":feature:call-intercept")
include(":feature:device-evidence")
include(":feature:search-enrichment")
include(":feature:decision-engine")
include(":feature:decision-ui")
include(":feature:settings")
include(":feature:billing")
include(":feature:country-config")
// feature:push-intercept removed per v1.1 Architecture (PUSH REMOVED)
include(":feature:message-intercept")
include(":feature:privacy-check")
include(":feature:push-trash")
include(":feature:card-check")
include(":feature:call-check")
include(":feature:message-check")

// Data modules
include(":data:contacts")
include(":data:calllog")
include(":data:sms")
include(":data:search")
include(":data:local-cache")
