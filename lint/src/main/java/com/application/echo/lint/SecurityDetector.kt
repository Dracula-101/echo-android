package com.application.echo.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.ULiteralExpression

class SecurityDetector : Detector(), Detector.UastScanner {

    companion object {
        val ISSUE_HARDCODED_SECRETS = Issue.create(
            id = "HardcodedSecrets",
            briefDescription = "Hardcoded secrets or API keys detected",
            explanation = "API keys, passwords, and secrets should not be hardcoded. Use BuildConfig or secure storage.",
            category = Category.SECURITY,
            priority = 10,
            severity = Severity.ERROR,
            implementation = Implementation(SecurityDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        private val SECRET_PATTERNS = arrayOf(
            Regex("(?i)(api[_-]?key|secret|password|token|auth)[\"'`]?\\s*[:=]\\s*[\"'`][a-zA-Z0-9+/=]{12,}"),
            Regex("(?i)sk_[a-zA-Z0-9]{20,}"), // Stripe secret keys
            Regex("(?i)pk_[a-zA-Z0-9]{20,}"), // Stripe public keys
            Regex("(?i)[\"'`][a-zA-Z0-9]{32,}[\"'`]") // Long strings that might be secrets
        )
    }

    override fun getApplicableUastTypes() = listOf(ULiteralExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitLiteralExpression(node: ULiteralExpression) {
            val value = node.value as? String ?: return

            if (value.length > 8 && SECRET_PATTERNS.any { it.containsMatchIn(value) }) {
                context.report(
                    ISSUE_HARDCODED_SECRETS,
                    context.getLocation(node),
                    "Potential hardcoded secret detected. Move to BuildConfig or secure storage."
                )
            }
        }
    }
}
