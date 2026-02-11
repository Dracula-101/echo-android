package com.application.echo.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe route definitions for the main app navigation graph.
 *
 * Auth routes live inside the `feature:auth` module.
 * These routes are shown **after** the user is authenticated.
 */

// ========================== MAIN ROUTES ==========================

/**
 * The top-level main graph destination (after authentication).
 */
@Serializable
data object MainGraph

/**
 * Home / feed screen.
 */
@Serializable
data object HomeRoute

/**
 * Search / discover screen.
 */
@Serializable
data object SearchRoute

/**
 * Messages / conversations list.
 */
@Serializable
data object MessagesRoute

/**
 * Notifications screen.
 */
@Serializable
data object NotificationsRoute

/**
 * Current user's profile screen.
 */
@Serializable
data object ProfileRoute

/**
 * Another user's profile screen.
 */
@Serializable
data class UserProfileRoute(val userId: String)

/**
 * Settings screen.
 */
@Serializable
data object SettingsRoute
