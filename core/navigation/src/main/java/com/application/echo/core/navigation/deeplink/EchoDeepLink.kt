package com.application.echo.core.navigation.deeplink

import androidx.navigation.NavDeepLink
import androidx.navigation.NavDeepLinkDslBuilder
import androidx.navigation.navDeepLink

/**
 * DSL builder for declaring deep links in a clean, readable way.
 *
 * ```kotlin
 * echoComposable<UserProfileRoute>(
 *     deepLinks = echoDeepLinks {
 *         uriPattern("echo://profile/{userId}")
 *         uriPattern("https://echo.app/profile/{userId}")
 *         action("com.application.echo.OPEN_PROFILE")
 *     },
 * ) { backStackEntry -> ... }
 * ```
 */
class EchoDeepLinkBuilder {
    private val links = mutableListOf<NavDeepLink>()

    /**
     * Add a deep link matching a URI pattern.
     *
     * Supports path parameters (`{param}`) and query parameters (`?key={value}`).
     */
    fun uriPattern(pattern: String) {
        links += navDeepLink { uriPattern = pattern }
    }

    /**
     * Add a deep link matching an explicit [Intent action][android.content.Intent.getAction].
     */
    fun action(action: String) {
        links += navDeepLink { this.action = action }
    }

    /**
     * Add a deep link matching a MIME type.
     */
    fun mimeType(mimeType: String) {
        links += navDeepLink { this.mimeType = mimeType }
    }

    /**
     * Add a fully custom [NavDeepLink] built via the standard builder.
     */
    fun custom(builder: NavDeepLinkDslBuilder.() -> Unit) {
        links += navDeepLink(builder)
    }

    internal fun build(): List<NavDeepLink> = links.toList()
}

/**
 * Creates a list of [NavDeepLink]s using the [EchoDeepLinkBuilder] DSL.
 *
 * @see EchoDeepLinkBuilder
 */
fun echoDeepLinks(builder: EchoDeepLinkBuilder.() -> Unit): List<NavDeepLink> =
    EchoDeepLinkBuilder().apply(builder).build()
