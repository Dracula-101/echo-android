package com.application.echo.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UImportStatement
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.USimpleNameReferenceExpression

class EchoThemeDetector : Detector(), Detector.UastScanner {

    companion object {
        val ISSUE = Issue.create(
            id = "UseEchoThemeInsteadOfMaterialTheme",
            briefDescription = "Use echoTheme instead of MaterialTheme",
            explanation = """
                Use echoTheme from the core.design module instead of MaterialTheme for consistent theming.
                
                echoTheme provides:
                • Consistent color schemes across the app
                • Custom typography
                • Theme-aware components
                
                Replace MaterialTheme usage with echoTheme.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 8,
            severity = Severity.WARNING,
            implementation = Implementation(
                EchoThemeDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            ),
            androidSpecific = true
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>> {
        return listOf(
            UCallExpression::class.java,
            UQualifiedReferenceExpression::class.java,
            USimpleNameReferenceExpression::class.java
        )
    }

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return EchoThemeHandler(context)
    }

    private class EchoThemeHandler(private val context: JavaContext) : UElementHandler() {

        override fun visitCallExpression(node: UCallExpression) {
            val methodName = node.methodName
            if (methodName == "MaterialTheme") {
                reportMaterialThemeUsage(node, "Replace MaterialTheme composable with EchoTheme")
            }
        }

        override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
            val receiverText = node.receiver.asSourceString()
            if (receiverText == "MaterialTheme" || receiverText.endsWith(".MaterialTheme")) {
                val selector = node.selector.asSourceString()
                reportMaterialThemeUsage(
                    node,
                    "Use EchoTheme.$selector instead of MaterialTheme.$selector"
                )
            }
        }

        override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression) {
            if (node.identifier == "MaterialTheme") {
                // Check if it's being used as a reference (not in an import)
                val parent = node.uastParent
                if (parent !is UImportStatement) {
                    reportMaterialThemeUsage(node, "Use EchoTheme instead of MaterialTheme")
                }
            }
        }

        private fun reportMaterialThemeUsage(node: UElement, message: String) {
            val fix = LintFix.create()
                .name("Replace with EchoTheme")
                .replace()
                .pattern("MaterialTheme")
                .with("EchoTheme")
                .robot(true)
                .independent(true)
                .build()

            context.report(
                issue = ISSUE,
                scope = node,
                location = context.getLocation(node),
                message = message,
                quickfixData = fix
            )
        }
    }
}

