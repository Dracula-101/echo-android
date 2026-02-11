import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    alias(libs.plugins.echo.android.application)
    alias(libs.plugins.echo.android.application.compose)
    alias(libs.plugins.echo.android.application.flavors)
    alias(libs.plugins.echo.android.application.firebase)
    alias(libs.plugins.echo.android.hilt)
    alias(libs.plugins.echo.android.room)
    alias(libs.plugins.kotlin.serialization)
}

val secretPropertiesFile = rootProject.file("secrets.properties")
val secretProperties = if (secretPropertiesFile.exists()) {
    loadProperties(secretPropertiesFile.path)
} else {
    throw IllegalStateException("Secret properties file not found: ${secretPropertiesFile.path}")
}

val keyPropertiesFile = rootProject.file("key.properties")
val keyProperties = loadProperties(keyPropertiesFile.path)

android {
    namespace = "com.application.echo"

    signingConfigs {
        getByName("debug") {
            storeFile = file("../keystores/debug-keystore.jks")
            storePassword = "DebugEcho"
            keyAlias = "debug-echo"
            keyPassword = "debug-echo"
        }

        create("release") {
            storeFile = file(keyProperties.getProperty("KEYSTORE_FILE"))
            storePassword = keyProperties.getProperty("KEYSTORE_PASSWORD")
            keyAlias = keyProperties.getProperty("KEY_ALIAS")
            keyPassword = keyProperties.getProperty("KEY_PASSWORD")
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }

        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {

    implementation(projects.core.common)
    implementation(projects.core.network)
    implementation(projects.core.analytics)
    implementation(projects.core.navigation)

    implementation(projects.ui.design)
    implementation(projects.ui.components)

    implementation(projects.feature.auth)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.dataStore)
    implementation(libs.androidx.core.security.crypto)
    implementation(libs.androidx.work.ktx)
    implementation(libs.hilt.ext.work)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)

    ksp(libs.hilt.ext.compiler)
}