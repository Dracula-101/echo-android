import com.android.build.api.dsl.ApplicationExtension
import com.application.echo.FilePrecheck
import com.application.echo.configureAndroid
import com.application.echo.configureApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("echo.android.lint")
            }

            extensions.configure<ApplicationExtension> {
                configureAndroid(this)
                configureApplicationExtension(this)
            }

            // Register file precheck task
            tasks.register("validateImportantFiles") {
                group = "verification"
                description =
                    "Validates important files like key.properties, secrets.properties, and release-keystore.jks"

                doLast {
                    FilePrecheck.performPrecheck(project)
                }
            }

            // Run file precheck based on build type
            afterEvaluate {
                val isReleaseBuild = gradle.startParameter.taskNames.any { taskName ->
                    taskName.contains("release", ignoreCase = true) ||
                            taskName.contains("bundle", ignoreCase = true) ||
                            (taskName.contains(
                                "assemble",
                                ignoreCase = true
                            ) && taskName.contains("release", ignoreCase = true))
                }

                if (isReleaseBuild) {
                    logger.info("Release build detected, running file precheck...")
                    FilePrecheck.performPrecheck(project)
                } else {
                    logger.info("Debug build detected, running file precheck with warnings only...")
                    FilePrecheck.performPrecheckWithWarnings(project)
                }
            }
        }
    }
}