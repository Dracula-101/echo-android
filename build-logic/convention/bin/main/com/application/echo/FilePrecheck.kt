package com.application.echo

import org.gradle.api.GradleException
import org.gradle.api.Project
import java.io.File

/**
 * File precheck utility for validating important build files
 */
object FilePrecheck {

    private const val KEY_PROPERTIES_PATH = "key.properties"
    private const val SECRETS_PROPERTIES_PATH = "secrets.properties"
    private const val RELEASE_KEYSTORE_PATH = "keystores/release-keystore.jks"

    /**
     * Performs precheck validation for all important files - fails build on errors
     */
    fun performPrecheck(project: Project) {
        project.logger.info("🔍 Starting precheck for important files...")

        val rootProject = project.rootProject
        val failures = mutableListOf<String>()

        // Check all files
        checkAllFiles(rootProject, project, failures)

        // Report results - fail build if any failures
        if (failures.isNotEmpty()) {
            val errorMessage = buildString {
                appendLine("⚠️  Precheck failed for important files:")
                failures.forEach { appendLine("  $it") }
                appendLine()
                appendLine("📋 Required files:")
                appendLine("  • key.properties (root directory)")
                appendLine("  • secrets.properties (root directory)")
                appendLine("  • keystores/release-keystore.jks")
            }

            project.logger.error(errorMessage)
            throw GradleException("Precheck failed for important files. See logs for details.")
        }
    }

    /**
     * Performs precheck validation for all important files - only shows warnings
     */
    fun performPrecheckWithWarnings(project: Project) {
        project.logger.info("🔍 Starting precheck for important files (warnings only)...")

        val rootProject = project.rootProject
        val failures = mutableListOf<String>()

        // Check all files
        checkAllFiles(rootProject, project, failures)

        // Report results - only show warnings, don't fail build
        if (failures.isNotEmpty()) {
            project.logger.warn("⚠️  File precheck warnings:")
            failures.forEach { project.logger.warn("  $it") }
            project.logger.warn("📋 These files will be required for release builds")
        }
    }

    /**
     * Common method to check all files
     */
    private fun checkAllFiles(
        rootProject: Project,
        project: Project,
        failures: MutableList<String>
    ) {
        // Check key.properties
        val keyPropertiesResult = checkFileExists(rootProject, KEY_PROPERTIES_PATH)
        if (!keyPropertiesResult.isValid) {
            failures.add("❌ key.properties: ${keyPropertiesResult.message}")
        } else {
            project.logger.info("✅ key.properties: ${keyPropertiesResult.message}")
        }

        // Check secrets.properties
        val secretsPropertiesResult = checkFileExists(rootProject, SECRETS_PROPERTIES_PATH)
        if (!secretsPropertiesResult.isValid) {
            failures.add("❌ secrets.properties: ${secretsPropertiesResult.message}")
        } else {
            project.logger.info("✅ secrets.properties: ${secretsPropertiesResult.message}")
        }

        // Check release keystore
        val releaseKeystoreResult = checkFileExists(rootProject, RELEASE_KEYSTORE_PATH)
        if (!releaseKeystoreResult.isValid) {
            failures.add("❌ release-keystore.jks: ${releaseKeystoreResult.message}")
        } else {
            project.logger.info("✅ release-keystore.jks: ${releaseKeystoreResult.message}")
        }
    }

    /**
     * Simple file existence check
     */
    private fun checkFileExists(project: Project, filePath: String): ValidationResult {
        val file = File(project.rootDir, filePath)

        return when {
            !file.exists() -> ValidationResult(
                isValid = false,
                message = "File not found at ${file.absolutePath}"
            )

            !file.isFile -> ValidationResult(
                isValid = false,
                message = "Path exists but is not a file"
            )

            !file.canRead() -> ValidationResult(
                isValid = false,
                message = "File exists but is not readable"
            )

            file.length() == 0L -> ValidationResult(
                isValid = false,
                message = "File exists but is empty"
            )

            else -> ValidationResult(
                isValid = true,
                message = "File found (${file.length()} bytes)"
            )
        }
    }

    /**
     * Data class for validation results
     */
    private data class ValidationResult(
        val isValid: Boolean,
        val message: String
    )
}

/**
 * Extension function to easily run precheck from any project
 */
fun Project.runFilePrecheck() {
    FilePrecheck.performPrecheck(this)
}
