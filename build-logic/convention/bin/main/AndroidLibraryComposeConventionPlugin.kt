import com.android.build.api.dsl.LibraryExtension
import com.application.echo.configureCompose
import com.application.echo.configureLibraryFlavors
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            extensions.configure<LibraryExtension> {
                configureLibraryFlavors(this)
                configureCompose(this)
            }
        }
    }
}