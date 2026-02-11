import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidRetrofitConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies {
                add("implementation", platform(libs.findLibrary("square.retrofit.bom").get()))
                add("implementation", libs.findLibrary("square.retrofit").get())
                add("implementation", libs.findLibrary("square.retrofit.gson").get())
                add("implementation", libs.findLibrary("square.okhttp").get())
                add("implementation", libs.findLibrary("square.okhttp.logging").get())
            }
        }
    }
}