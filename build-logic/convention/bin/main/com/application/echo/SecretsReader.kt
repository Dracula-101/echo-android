package com.application.echo

import org.gradle.api.Project
import java.io.File
import java.util.Properties

/**
 * Utility for reading secrets from secrets.properties file
 */
object SecretsReader {

    /**
     * Reads secrets.properties file and returns Properties object
     */
    fun readSecrets(project: Project): Properties {
        val secretsFile = File(project.rootProject.rootDir, "secrets.properties")
        val properties = Properties()

        if (secretsFile.exists()) {
            properties.load(secretsFile.inputStream())
        } else {
            project.logger.warn("secrets.properties file not found at ${secretsFile.absolutePath}")
        }

        return properties
    }

    /**
     * Gets backend URL for development flavor
     */
    fun getDevBackendUrl(project: Project): String {
        val secrets = readSecrets(project)
        return secrets.getProperty("DEV_BACKEND_URL", "http://localhost:8080")
    }

    /**
     * Gets backend URL for production flavor
     */
    fun getProdBackendUrl(project: Project): String {
        val secrets = readSecrets(project)
        return secrets.getProperty("PROD_BACKEND_URL")
    }

    /**
     * Gets a secret property with optional default value
     */
    fun getSecret(project: Project, key: String, defaultValue: String = ""): String {
        val secrets = readSecrets(project)
        return secrets.getProperty(key, defaultValue)
    }
}
