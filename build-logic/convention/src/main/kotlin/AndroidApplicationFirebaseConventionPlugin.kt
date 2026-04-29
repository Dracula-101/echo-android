import com.android.build.api.dsl.ApplicationExtension
import com.android.tools.r8.internal.pl
import com.application.echo.BuildType
import com.application.echo.DEBUG
import com.application.echo.DEVELOPMENT
import com.application.echo.Flavor
import com.application.echo.PRODUCTION
import com.application.echo.RELEASE
import com.application.echo.isAnalyticsEnabled
import com.application.echo.isCrashlyticsEnabled
import com.application.echo.isFirebaseEnabled
import com.application.echo.isPerformanceMonitoringEnabled
import com.application.echo.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationFirebaseConventionPlugin : Plugin<Project> {

    companion object {

        private const val FIREBASE_BOM = "firebase.bom"
        private const val GOOGLE_SERVICES_PLUGIN = "com.google.gms.google-services"

        private const val CRASHLYTICS_PLUGIN = "com.google.firebase.crashlytics"
        private const val PERFORMANCE_PLUGIN = "com.google.firebase.firebase-perf"
        private const val FIREBASE_AUTH_DEP = "firebase-auth"
        private val FIREBASE_TASK_PATTERNS = listOf("firebase", "crashlytics", "performance")
    }

    override fun apply(target: Project) {
        with(target) {
            configureAndroidExtension()
            applyGoogleServicePlugins()

            val isProductionRelease = isProductionReleaseBuild()
            if (isProductionRelease) {
                applyFirebasePlugins()
            }
            configureFirebaseDependencies(isProductionRelease)
        }
    }

    private fun Project.isProductionReleaseBuild(): Boolean {
        val requestedTasks = gradle.startParameter.taskNames
        val productionReleaseVariant = "${PRODUCTION}${RELEASE}"
        val assembleTask =
            "assemble${PRODUCTION.replaceFirstChar { it.uppercase() }}${RELEASE.replaceFirstChar { it.uppercase() }}"
        val bundleTask =
            "bundle${PRODUCTION.replaceFirstChar { it.uppercase() }}${RELEASE.replaceFirstChar { it.uppercase() }}"

        return requestedTasks.any { task ->
            task.contains(productionReleaseVariant, ignoreCase = true) ||
                    task.contains(assembleTask, ignoreCase = true) ||
                    task.contains(bundleTask, ignoreCase = true)
        }
    }

    private fun Project.applyGoogleServicePlugins() {
        with(pluginManager) {
            apply(GOOGLE_SERVICES_PLUGIN)
        }
    }

    private fun Project.applyFirebasePlugins() {
        with(pluginManager) {
            apply(CRASHLYTICS_PLUGIN)
            apply(PERFORMANCE_PLUGIN)
        }
    }

    private fun Project.configureAndroidExtension() {
        extensions.configure<ApplicationExtension> {
            configureBuildTypes()
        }
        configureProductFlavors()
    }

    private fun ApplicationExtension.configureBuildTypes() {
        buildTypes {
            getByName(DEBUG) {
                configureBuildType(BuildType.DEBUG)
            }

            getByName(RELEASE) {
                configureBuildType(BuildType.RELEASE)
            }
        }
    }

    private fun com.android.build.api.dsl.BuildType.configureBuildType(buildType: BuildType) {
        // Configure manifest placeholders
        manifestPlaceholders.apply {
            put("enableCrashlytics", buildType.isCrashlyticsEnabled())
            put("enableAnalytics", buildType.isAnalyticsEnabled())
            put("enablePerformance", buildType.isPerformanceMonitoringEnabled())
        }

        // Configure build config fields
        buildConfigField(
            "boolean",
            "ENABLE_CRASHLYTICS",
            buildType.isCrashlyticsEnabled().toString()
        )
        buildConfigField("boolean", "ENABLE_ANALYTICS", buildType.isAnalyticsEnabled().toString())
        buildConfigField(
            "boolean",
            "ENABLE_PERFORMANCE",
            buildType.isPerformanceMonitoringEnabled().toString()
        )
    }

    private fun Project.configureProductFlavors() {
        afterEvaluate {
            extensions.configure<ApplicationExtension> {
                productFlavors.configureEach {
                    when (name) {
                        DEVELOPMENT -> configureFlavorSettings(Flavor.DEVELOPMENT)
                        PRODUCTION -> configureFlavorSettings(Flavor.PRODUCTION)
                    }
                }
            }
        }
    }

    private fun com.android.build.api.dsl.ProductFlavor.configureFlavorSettings(flavor: Flavor) {
        manifestPlaceholders["enableFirebase"] = flavor.isFirebaseEnabled()
        buildConfigField("boolean", "ENABLE_FIREBASE", flavor.isFirebaseEnabled().toString())
    }

    private fun Project.configureFirebaseDependencies(isProductionRelease: Boolean) {
        // BOM must be available for all variants so versionless Firebase
        // libs (e.g. firebase-messaging) can resolve in every build.
        dependencies {
            add("implementation", platform(libs.findLibrary(FIREBASE_BOM).get()))
            add("implementation", libs.findLibrary(FIREBASE_AUTH_DEP).get())
        }

        afterEvaluate {
            if (isProductionRelease) {
                addProductionFirebaseDependencies()
            } else {
                disableFirebaseTasks()
            }
        }
    }

    private fun Project.addProductionFirebaseDependencies() {
        dependencies {
            add("productionReleaseImplementation", libs.findLibrary("firebase.analytics").get())
            add("productionReleaseImplementation", libs.findLibrary("firebase.crashlytics").get())
            add("productionReleaseImplementation", libs.findLibrary("firebase.performance").get())
        }
    }

    private fun Project.disableFirebaseTasks() {
        tasks.configureEach {
            if (FIREBASE_TASK_PATTERNS.any { pattern -> name.lowercase().contains(pattern) }) {
                enabled = false
            }
        }
    }
}