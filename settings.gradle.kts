pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "Echo"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")
include(":lint")
include(":api")

// ----------------- Core Features -----------------
include(":core:network")
include(":core:common")
include(":core:analytics")
include(":core:navigation")
include(":core:websocket")

// ----------------- UI -----------------
include(":ui:components")
include(":ui:design")

// ----------------- Features -----------------
include(":feature:auth")