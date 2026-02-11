package com.application.echo.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UField
import org.jetbrains.uast.ULiteralExpression
import org.jetbrains.uast.UNamedExpression
import org.jetbrains.uast.UVariable
import org.jetbrains.uast.getParentOfType

class HardcodedStringDetector : Detector(), Detector.UastScanner {

    companion object {
        val ISSUE = Issue.create(
            id = "HardcodedStrings",
            briefDescription = "Use string resources instead of hardcoded strings",
            explanation = """
                Hardcoded strings should be replaced with string resources for proper localization.
                Use stringResource(R.string.resource_name) in Compose or getString(R.string.resource_name) in traditional Android code.
            """.trimIndent(),
            category = Category.I18N,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(
                HardcodedStringDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            ),
            androidSpecific = true
        )

        // Comprehensive UI component patterns for matching
        private val UI_COMPONENT_PATTERNS = arrayOf(
            "Text", "Button", "Chip", "Tab", "Dialog", "Snackbar", "Toast",
            "AppBar", "SearchBar", "TextField", "Card", "Menu", "Item",
            "Label", "Title", "Header", "Footer", "Content", "Message",
            "Alert", "Banner", "Badge", "Navigation", "Toolbar", "Switch",
            "Radio", "Checkbox", "Slider", "Progress", "Loading", "Error",
            "Empty", "Placeholder", "Hint", "Description", "Caption"
        )

        // Parameter names that typically contain user-facing text
        private val TEXT_PARAMETER_NAMES = setOf(
            "text", "title", "subtitle", "label", "placeholder", "hint",
            "message", "content", "description", "caption", "summary",
            "header", "footer", "leadingContent", "trailingContent",
            "supportingText", "helperText", "errorText", "confirmText",
            "dismissText", "positiveText", "negativeText", "neutralText",
            "headlineContent", "overlineContent", "bodyContent",
            "primaryText", "secondaryText", "tertiaryText"
        )

        // Logging and debugging related method names and receivers
        private val LOG_METHOD_NAMES = setOf(
            "d", "e", "i", "v", "w", "wtf", "debug", "error", "info",
            "verbose", "warn", "warning", "trace", "log", "println", "print"
        )

        private val LOG_RECEIVER_PATTERNS = arrayOf(
            "Log", "Timber", "Logger", "System", "Console", "Debug"
        )

        // Technical string patterns to ignore
        private val TECHNICAL_PREFIXES = arrayOf(
            "http://", "https://", "ftp://", "file://", "content://", "data:",
            "android:", "com.", "org.", "net.", "io.", "androidx.", "kotlin.",
            "java.", "javax.", "dalvik.", "art.", "META-INF", "WEB-INF"
        )

        // Regex patterns for various string types to ignore
        private val IGNORE_PATTERNS = arrayOf(
            Regex("^[A-Z][A-Z0-9_]*$"),                    // CONSTANT_CASE
            Regex("^[a-z][a-z0-9_]*$"),                    // snake_case (likely keys)
            Regex("^\\d+$"),                               // Pure numbers
            Regex("^[^a-zA-Z]*$"),                         // No alphabetic characters
            Regex("^[A-Za-z0-9._-]+\\.[a-zA-Z]{2,}$"),     // Domain-like strings
            Regex("^/[a-zA-Z0-9/_.-]*$"),                  // File paths
            Regex("^[a-zA-Z0-9+/=]+$"),                    // Base64-like
            Regex("^[0-9a-fA-F-]{8,}$"),                   // Hex/UUID-like
            Regex("^\\$\\{.*\\}$"),                        // Template strings
            Regex("^#[0-9a-fA-F]{3,8}$"),                  // Color codes
            Regex("^[+-]?\\d*\\.?\\d+[a-zA-Z%]*$")         // Numbers with units
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(ULiteralExpression::class.java, UCallExpression::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitLiteralExpression(node: ULiteralExpression) {
                checkStringLiteral(context, node)
            }

            override fun visitCallExpression(node: UCallExpression) {
                checkFunctionCall(context, node)
            }
        }
    }

    private fun checkStringLiteral(context: JavaContext, node: ULiteralExpression) {
        val value = node.value as? String ?: return

        if (shouldIgnoreString(value, context, node)) return

        val parentCall = node.getParentOfType<UCallExpression>()
        val isInUIComponent = parentCall?.let { isUIComponentCall(it) } ?: false
        val isUserFacing = isUserFacingText(value)

        if (isInUIComponent || isUserFacing) {
            reportHardcodedString(context, node, value, parentCall)
        }
    }

    private fun checkFunctionCall(context: JavaContext, call: UCallExpression) {
        if (!isUIComponentCall(call)) return

        // Check all arguments, especially named parameters
        call.valueArguments.forEachIndexed { index, argument ->
            when (argument) {
                is ULiteralExpression -> {
                    val value = argument.value as? String ?: return@forEachIndexed
                    if (!shouldIgnoreString(value, context, argument)) {
                        val paramName = getParameterName(call, index)
                        if (paramName == null || paramName in TEXT_PARAMETER_NAMES || index == 0) {
                            reportHardcodedString(context, argument, value, call)
                        }
                    }
                }

                is UNamedExpression -> {
                    val expr = argument.expression
                    if (expr is ULiteralExpression) {
                        val value = expr.value as? String ?: return@forEachIndexed
                        if (!shouldIgnoreString(value, context, expr) &&
                            argument.name in TEXT_PARAMETER_NAMES
                        ) {
                            reportHardcodedString(context, expr, value, call)
                        }
                    }
                }
            }
        }
    }

    private fun shouldIgnoreString(
        value: String,
        context: JavaContext,
        node: ULiteralExpression
    ): Boolean {
        return value.length <= 1 ||
                value.isBlank() ||
                isInTestCode(context) ||
                isInLogStatement(node) ||
                isInAnnotation(node) ||
                isInConstantDeclaration(node) ||
                isInResourceFile(context) ||
                isTechnicalString(value) ||
                matchesIgnorePattern(value)
    }

    private fun isInTestCode(context: JavaContext): Boolean {
        val path = context.file.path
        return path.contains("/test/") ||
                path.contains("/androidTest/") ||
                path.contains("/debug/") ||
                path.endsWith("Test.kt") ||
                path.endsWith("Test.java")
    }

    private fun isInLogStatement(node: ULiteralExpression): Boolean {
        val call = node.getParentOfType<UCallExpression>() ?: return false
        val methodName = call.methodName ?: return false

        if (methodName in LOG_METHOD_NAMES) return true

        val receiverType = call.receiverType?.canonicalText
        if (receiverType != null) {
            val receiverName = receiverType.substringAfterLast('.')
            return LOG_RECEIVER_PATTERNS.any { receiverName.contains(it, ignoreCase = true) }
        }

        return false
    }

    private fun isInAnnotation(node: ULiteralExpression): Boolean =
        node.getParentOfType<UAnnotation>() != null

    private fun isInConstantDeclaration(node: ULiteralExpression): Boolean {
        val field = node.getParentOfType<UField>()
        val variable = node.getParentOfType<UVariable>()

        return (field?.isFinal == true && field.isStatic) ||
                (variable?.isFinal == true && variable.name?.matches(Regex("^[A-Z][A-Z0-9_]*$")) == true)
    }

    private fun isInResourceFile(context: JavaContext): Boolean =
        context.file.path.contains("/res/") || context.file.name.startsWith("R.")

    private fun isTechnicalString(value: String): Boolean {
        return TECHNICAL_PREFIXES.any { value.startsWith(it, ignoreCase = true) } ||
                value.contains("://") ||
                (value.contains('/') && value.contains('.') && !value.contains(' '))
    }

    private fun matchesIgnorePattern(value: String): Boolean =
        IGNORE_PATTERNS.any { it.matches(value) }

    private fun isUIComponentCall(call: UCallExpression): Boolean {
        val methodName = call.methodName ?: return false
        return UI_COMPONENT_PATTERNS.any { pattern ->
            methodName.contains(pattern, ignoreCase = true)
        }
    }

    private fun isUserFacingText(value: String): Boolean {
        if (value.length < 2) return false

        // Multi-word strings are likely user-facing
        val words = value.trim().split(Regex("\\s+"))
        if (words.size > 1) return true

        // Title case strings (first letter uppercase, contains lowercase)
        if (value[0].isUpperCase() && value.drop(1).any { it.isLowerCase() }) {
            return true
        }

        // Contains whitespace or punctuation suggesting natural language
        if (value.any { it.isWhitespace() || it in ".,!?;:\"'()[]{}" }) {
            return true
        }

        // Sentence case with proper punctuation
        if (value.endsWith('.') || value.endsWith('!') || value.endsWith('?')) {
            return true
        }

        return false
    }

    private fun getParameterName(call: UCallExpression, index: Int): String? {
        return try {
            call.resolve()?.parameters?.getOrNull(index)?.name
        } catch (e: Exception) {
            null
        }
    }

    private fun reportHardcodedString(
        context: JavaContext,
        node: ULiteralExpression,
        value: String,
        call: UCallExpression?
    ) {
        val componentInfo = call?.methodName?.let { " in $it component" } ?: ""
        val truncatedValue = if (value.length > 50) "${value.take(47)}..." else value
        val message =
            "Hardcoded string \"$truncatedValue\"$componentInfo should use string resources for localization"

        context.report(
            issue = ISSUE,
            location = context.getLocation(node),
            message = message
        )
    }
}
