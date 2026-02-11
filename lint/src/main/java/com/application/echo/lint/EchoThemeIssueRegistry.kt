package com.application.echo.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API

class EchoThemeIssueRegistry : IssueRegistry() {
    override val issues = listOf(
        EchoThemeDetector.ISSUE,
        HardcodedStringDetector.ISSUE,
        SecurityDetector.ISSUE_HARDCODED_SECRETS,
        MemoryLeakDetector.ISSUE,
    )

    override val api: Int = CURRENT_API

    override val minApi: Int = 8

    override val vendor: Vendor = Vendor(
        vendorName = "echo",
        feedbackUrl = "https://github.com/Dracula-101/echo/issues",
    )
}