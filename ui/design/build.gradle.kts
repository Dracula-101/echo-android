plugins {
    alias(libs.plugins.echo.android.library)
    alias(libs.plugins.echo.android.library.compose)
}

android {
    namespace = "com.application.echo.ui.design"
}


dependencies {
    lintPublish(projects.lint)
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Core Android libraries
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)

    // Compose Essentials
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

}