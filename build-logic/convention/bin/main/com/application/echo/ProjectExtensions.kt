package com.application.echo

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.libs get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal val Project.minSdkValue: Int
    get() = libs.findVersion("minSdk").get().toString().toInt()

internal val Project.targetSdkValue: Int
    get() = libs.findVersion("targetSdk").get().toString().toInt()

internal val Project.compileSdkValue: Int
    get() = libs.findVersion("compileSdk").get().toString().toInt()

internal val Project.jvmTarget: String
    get() = libs.findVersion("jvmTarget").get().toString()

internal const val NAMESPACE = "com.application.echo"
internal const val APP_NAME = "Echo"

internal const val DEBUG = "debug"
internal const val RELEASE = "release"
internal const val DEVELOPMENT = "development"
internal const val PRODUCTION = "production"
internal const val ENVIRONMENT = "environment"

internal const val DEBUG_KEYSTORE_PATH = "../keystores/debug-keystore.jks"
internal const val DEBUG_KEYSTORE_PASSWORD = "DebugEcho"
internal const val DEBUG_KEY_ALIAS = "debug-echo"
internal const val DEBUG_KEY_PASSWORD = "debug-echo"
