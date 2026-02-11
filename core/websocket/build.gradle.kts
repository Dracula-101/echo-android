import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.echo.android.library)
    alias(libs.plugins.echo.android.hilt)
}

android {
    namespace = "com.application.echo.core.websocket"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.valueOf("VERSION_${libs.versions.jvmTarget.get()}")
        targetCompatibility = JavaVersion.valueOf("VERSION_${libs.versions.jvmTarget.get()}")
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvmTarget.get()))
    }
}

dependencies {

    // AndroidX
    implementation(libs.androidx.core.ktx)

    // Network
    implementation(libs.square.okhttp)
    implementation(libs.square.okhttp.logging)

    // Serialization (Gson via Retrofit converter — only Gson is used)
    implementation(platform(libs.square.retrofit.bom))
    implementation(libs.square.retrofit.gson)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Logging
    implementation(libs.timber)
}
