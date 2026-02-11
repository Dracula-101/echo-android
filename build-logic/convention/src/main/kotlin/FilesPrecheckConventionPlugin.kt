import com.application.echo.FilePrecheck
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle plugin for running file precheck validation
 */
class FilesPrecheckConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Register a task for running precheck
            tasks.register("validateImportantFiles") {
                group = "verification"
                description =
                    "Validates important files like key.properties, secrets.properties, and release-keystore.jks"

                doLast {
                    FilePrecheck.performPrecheck(project)
                }
            }

            // Optionally run precheck automatically during build
            afterEvaluate {
                // Only run for release builds or when explicitly requested
                if (project.gradle.startParameter.taskNames.any {
                        it.contains("release", ignoreCase = true) ||
                                it.contains("bundle", ignoreCase = true) ||
                                it.contains("assemble", ignoreCase = true) && it.contains(
                            "release",
                            ignoreCase = true
                        )
                    }) {
                    logger.info("Release build detected, running file precheck...")
                    FilePrecheck.performPrecheck(project)
                }
            }
        }
    }
}
