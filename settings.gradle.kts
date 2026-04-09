pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
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
include(":core:model")
include(":core:util")

// Feature modules
include(":feature:call-intercept")
include(":feature:device-evidence")
include(":feature:search-enrichment")
include(":feature:decision-engine")
include(":feature:decision-ui")
include(":feature:settings")
include(":feature:billing")
include(":feature:country-config")
include(":feature:push-intercept")
include(":feature:message-intercept")
include(":feature:privacy-check")

// Data modules
include(":data:contacts")
include(":data:calllog")
include(":data:sms")
include(":data:search")
include(":data:local-cache")
