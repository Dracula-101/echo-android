plugins {
    alias(libs.plugins.echo.android.library)
    alias(libs.plugins.echo.android.library.compose)
}

android {
    namespace = "com.application.echo.ui.components"
}

dependencies {
    implementation(projects.ui.design)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Compose Essentials
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.adaptive)

    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.androidx.compose.material3.windowSizeClass)
}