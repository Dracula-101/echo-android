plugins {
    alias(libs.plugins.echo.android.library)
    alias(libs.plugins.echo.android.hilt)
    alias(libs.plugins.echo.android.retrofit)
}

android {
    namespace = "com.application.echo.core.api"
}

dependencies {

    // Project
    implementation(projects.core.common)
    implementation(projects.core.network)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Logging
    implementation(libs.timber)
}
