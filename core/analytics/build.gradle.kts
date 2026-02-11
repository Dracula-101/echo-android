plugins {
    alias(libs.plugins.echo.android.library)
    alias(libs.plugins.echo.android.library.compose)
    alias(libs.plugins.echo.android.hilt)
}

android {
    namespace = "com.application.echo.core.analytics"
}

dependencies {
    implementation(libs.androidx.compose.runtime)

    productionImplementation(platform(libs.firebase.bom))
    productionImplementation(libs.firebase.analytics)
}