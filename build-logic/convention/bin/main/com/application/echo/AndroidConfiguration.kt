package com.application.echo

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        compileSdk = compileSdkValue

        defaultConfig {
            minSdk = minSdkValue
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables.useSupportLibrary = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.toVersion(jvmTarget)
            targetCompatibility = JavaVersion.toVersion(jvmTarget)
        }

        packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget.set(JvmTarget.fromTarget(jvmTarget))
    }
}

internal fun Project.configureApplicationExtension(applicationExtension: ApplicationExtension) {
    applicationExtension.apply {

        defaultConfig {
            applicationId = NAMESPACE
            targetSdk = targetSdkValue
            versionCode = 1
            versionName = "0.0.1"
        }

        buildFeatures.buildConfig = true

        buildTypes {
            getByName(DEBUG) {
                val buildType = BuildType.DEBUG
                buildType.applicationIdSuffix()?.let { applicationIdSuffix = it }
                buildType.versionNameSuffix()?.let { versionNameSuffix = it }
                isMinifyEnabled = buildType.isMinifyEnabled()

                buildConfigField(
                    "boolean",
                    "ENABLE_CRASHLYTICS",
                    buildType.isCrashlyticsEnabled().toString()
                )
                buildConfigField(
                    "boolean",
                    "ENABLE_PERFORMANCE",
                    buildType.isPerformanceMonitoringEnabled().toString()
                )
                buildConfigField(
                    "boolean",
                    "ENABLE_FIREBASE",
                    buildType.isFirebaseEnabled().toString()
                )

                manifestPlaceholders["enableCrashlytics"] = buildType.isCrashlyticsEnabled()
                manifestPlaceholders["enableAnalytics"] = buildType.isAnalyticsEnabled()
                manifestPlaceholders["enablePerformance"] =
                    buildType.isPerformanceMonitoringEnabled()
            }

            getByName(RELEASE) {
                val buildType = BuildType.RELEASE
                buildType.applicationIdSuffix()?.let { applicationIdSuffix = it }
                buildType.versionNameSuffix()?.let { versionNameSuffix = it }
                isMinifyEnabled = buildType.isMinifyEnabled()
                isShrinkResources = buildType.isShrinkResourcesEnabled()

                buildConfigField(
                    "boolean",
                    "ENABLE_CRASHLYTICS",
                    buildType.isCrashlyticsEnabled().toString()
                )
                buildConfigField(
                    "boolean",
                    "ENABLE_PERFORMANCE",
                    buildType.isPerformanceMonitoringEnabled().toString()
                )
                buildConfigField(
                    "boolean",
                    "ENABLE_FIREBASE",
                    buildType.isFirebaseEnabled().toString()
                )

                manifestPlaceholders["enableCrashlytics"] = buildType.isCrashlyticsEnabled()
                manifestPlaceholders["enableAnalytics"] = buildType.isAnalyticsEnabled()
                manifestPlaceholders["enablePerformance"] =
                    buildType.isPerformanceMonitoringEnabled()
            }
        }
    }
}

internal fun Project.configureLibraryExtension(libraryExtension: LibraryExtension) {
    libraryExtension.apply {
        compileSdk = this@configureLibraryExtension.compileSdkValue

        defaultConfig {
            minSdk = this@configureLibraryExtension.minSdkValue
            consumerProguardFiles("consumer-rules.pro")
        }

        buildFeatures.buildConfig = true

        buildTypes {
            getByName(DEBUG) {
                isMinifyEnabled = false
            }
            getByName(RELEASE) {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
    }
}

internal fun Project.configureLibraryFlavors(libraryExtension: LibraryExtension) {
    libraryExtension.apply {
        flavorDimensions += ENVIRONMENT

        productFlavors {
            create(DEVELOPMENT) {
                dimension = ENVIRONMENT
            }

            create(PRODUCTION) {
                dimension = ENVIRONMENT
            }
        }
    }
}

internal fun Project.configureProductFlavors(applicationExtension: ApplicationExtension) {
    applicationExtension.apply {
        flavorDimensions += ENVIRONMENT

        productFlavors {
            create(DEVELOPMENT) {
                val flavor = Flavor.DEVELOPMENT
                dimension = ENVIRONMENT
                flavor.applicationIdSuffix()?.let { applicationIdSuffix = it }
                flavor.versionNameSuffix()?.let { versionNameSuffix = it }

                buildConfigField(
                    "String",
                    "BACKEND_URL",
                    "\"${SecretsReader.getDevBackendUrl(project)}\""
                )
                buildConfigField("String", "WEB_BASE_URL", "\"${flavor.webBaseUrl()}\"")
                buildConfigField("boolean", "ENABLE_LOGGING", flavor.isLoggingEnabled().toString())
                buildConfigField(
                    "boolean",
                    "ENABLE_STRICT_MODE",
                    flavor.isStrictModeEnabled().toString()
                )
                buildConfigField("String", "FLAVOR", "\"$DEVELOPMENT\"")

                manifestPlaceholders["appName"] = flavor.appName()
                manifestPlaceholders["appIcon"] = flavor.appIcon()
                manifestPlaceholders["enableFirebase"] = flavor.isFirebaseEnabled()
            }

            create(PRODUCTION) {
                val flavor = Flavor.PRODUCTION
                dimension = ENVIRONMENT

                buildConfigField(
                    "String",
                    "BACKEND_URL",
                    "\"${SecretsReader.getProdBackendUrl(project)}\""
                )
                buildConfigField("String", "WEB_BASE_URL", "\"${flavor.webBaseUrl()}\"")
                buildConfigField("boolean", "ENABLE_LOGGING", flavor.isLoggingEnabled().toString())
                buildConfigField(
                    "boolean",
                    "ENABLE_STRICT_MODE",
                    flavor.isStrictModeEnabled().toString()
                )
                buildConfigField("String", "FLAVOR", "\"$PRODUCTION\"")

                manifestPlaceholders["appName"] = flavor.appName()
                manifestPlaceholders["appIcon"] = flavor.appIcon()
                manifestPlaceholders["enableFirebase"] = flavor.isFirebaseEnabled()
            }
        }
    }
}

internal fun Project.configureCompose(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        buildFeatures.compose = true
    }

    dependencies {
        val bom = libs.findLibrary("androidx.compose.bom").get()
        add("implementation", platform(bom))
        add("androidTestImplementation", platform(bom))

        add("implementation", libs.findLibrary("androidx.compose.ui").get())
        add("implementation", libs.findLibrary("androidx.compose.ui.graphics").get())
        add("implementation", libs.findLibrary("androidx.compose.ui.tooling.preview").get())
        add("implementation", libs.findLibrary("androidx.compose.material3").get())
        add("implementation", libs.findLibrary("androidx.compose.material.icons.core").get())
        add("implementation", libs.findLibrary("androidx.compose.material.icons.extended").get())
        add("implementation", libs.findLibrary("androidx.activity.compose").get())
        add("implementation", libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
        add("implementation", libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
        add("implementation", libs.findLibrary("androidx.navigation.compose").get())

        add("debugImplementation", libs.findLibrary("androidx.compose.ui.tooling").get())
        add("debugImplementation", libs.findLibrary("androidx.ui.test.manifest").get())
    }
}
