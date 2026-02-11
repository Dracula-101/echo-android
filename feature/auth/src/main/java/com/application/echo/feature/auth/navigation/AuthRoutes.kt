package com.application.echo.feature.auth.navigation

import kotlinx.serialization.Serializable

/**
 * Top-level graph route for the authentication flow.
 *
 * Used by the app module to embed the entire auth feature as a nested navigation graph.
 */
@Serializable
data object AuthGraph

/**
 * Login screen route.
 */
@Serializable
data object LoginRoute

/**
 * Sign-up / registration screen route.
 */
@Serializable
data object SignUpRoute
