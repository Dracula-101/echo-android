import com.android.build.api.dsl.LibraryExtension
import com.application.echo.configureAndroid
import com.application.echo.configureLibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("echo.android.lint")
            }

            extensions.configure<LibraryExtension> {
                configureAndroid(this)
                configureLibraryExtension(this)
            }
        }
    }
}