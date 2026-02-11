package com.application.echo.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.UField

class MemoryLeakDetector : Detector(), Detector.UastScanner {

    companion object {
        val ISSUE = Issue.create(
            id = "PotentialMemoryLeak",
            briefDescription = "Potential memory leak detected",
            explanation = "Static references to Context, Activity, or View objects can cause memory leaks. Use Application context or WeakReference.",
            category = Category.CORRECTNESS,
            priority = 9,
            severity = Severity.ERROR,
            implementation = Implementation(MemoryLeakDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        private val LEAK_PRONE_TYPES = setOf(
            "android.content.Context", "android.app.Activity", "android.view.View",
            "androidx.fragment.app.Fragment", "android.app.Fragment"
        )
    }

    override fun getApplicableUastTypes() = listOf(UField::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitField(node: UField) {
            if (node.isStatic && node.type.canonicalText in LEAK_PRONE_TYPES) {
                context.report(
                    ISSUE,
                    context.getLocation(node),
                    "Static field '${node.name}' holds reference to ${node.type.canonicalText} which can cause memory leaks"
                )
            }
        }
    }
}
