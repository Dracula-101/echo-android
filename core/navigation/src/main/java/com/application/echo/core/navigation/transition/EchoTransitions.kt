package com.application.echo.core.navigation.transition

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavBackStackEntry

/**
 * Shared animation timing constants used across all Echo navigation transitions.
 */
internal object AnimSpec {
    const val DURATION_SHORT = 250
    const val DURATION_MEDIUM = 350
    const val DURATION_LONG = 450

    /** Material-style decelerate easing for entering screens. */
    val ENTER_EASING = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)

    /** Material-style accelerate easing for exiting screens. */
    val EXIT_EASING = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
}

/** Convenience typealias for navigation enter-transition lambdas. */
typealias NavEnterTransition = AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition

/** Convenience typealias for navigation exit-transition lambdas. */
typealias NavExitTransition = AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition

/**
 * Whether this scope's transition represents forward navigation (same parent graph).
 */
val AnimatedContentTransitionScope<NavBackStackEntry>.isForwardNavigation: Boolean
    get() = initialState.destination.parent?.id == targetState.destination.parent?.id

/**
 * Library of reusable navigation transitions.
 *
 * Each property is a lambda conforming to the Compose Navigation transition signature,
 * ready to be passed directly to `NavHost`, `composable()`, or `echoComposable()`.
 *
 * ```kotlin
 * EchoNavHost(
 *     enterTransition = EchoTransitions.slideInFromEnd,
 *     exitTransition = EchoTransitions.slideOutToStart,
 * )
 * ```
 */
object EchoTransitions {

    // ──────────────── Fade ────────────────

    val fadeIn: NavEnterTransition = {
        fadeIn(
            animationSpec = tween(
                durationMillis = AnimSpec.DURATION_SHORT,
                easing = AnimSpec.ENTER_EASING,
            ),
        )
    }

    val fadeOut: NavExitTransition = {
        fadeOut(
            animationSpec = tween(
                durationMillis = AnimSpec.DURATION_SHORT,
                easing = AnimSpec.EXIT_EASING,
            ),
        )
    }

    // ──────────────── Slide (raw) ────────────────

    val slideInLeft: NavEnterTransition = {
        slideInHorizontally(
            animationSpec = tween(AnimSpec.DURATION_MEDIUM, easing = AnimSpec.ENTER_EASING),
            initialOffsetX = { -it },
        )
    }

    val slideInRight: NavEnterTransition = {
        slideInHorizontally(
            animationSpec = tween(AnimSpec.DURATION_MEDIUM, easing = AnimSpec.ENTER_EASING),
            initialOffsetX = { it },
        )
    }

    val slideOutLeft: NavExitTransition = {
        slideOutHorizontally(
            animationSpec = tween(AnimSpec.DURATION_MEDIUM, easing = AnimSpec.EXIT_EASING),
            targetOffsetX = { -it },
        )
    }

    val slideOutRight: NavExitTransition = {
        slideOutHorizontally(
            animationSpec = tween(AnimSpec.DURATION_MEDIUM, easing = AnimSpec.EXIT_EASING),
            targetOffsetX = { it },
        )
    }

    val slideInUp: NavEnterTransition = {
        slideInVertically(
            animationSpec = tween(AnimSpec.DURATION_LONG, easing = AnimSpec.ENTER_EASING),
            initialOffsetY = { it },
        )
    }

    val slideOutDown: NavExitTransition = {
        slideOutVertically(
            animationSpec = tween(AnimSpec.DURATION_LONG, easing = AnimSpec.EXIT_EASING),
            targetOffsetY = { it },
        )
    }

    val slideInDown: NavEnterTransition = {
        slideInVertically(
            animationSpec = tween(AnimSpec.DURATION_LONG, easing = AnimSpec.ENTER_EASING),
            initialOffsetY = { -it },
        )
    }

    val slideOutUp: NavExitTransition = {
        slideOutVertically(
            animationSpec = tween(AnimSpec.DURATION_LONG, easing = AnimSpec.EXIT_EASING),
            targetOffsetY = { -it },
        )
    }

    // ──────────────── Scale + Fade ────────────────

    val scaleIn: NavEnterTransition = {
        scaleIn(
            animationSpec = tween(AnimSpec.DURATION_MEDIUM, easing = AnimSpec.ENTER_EASING),
            initialScale = 0.9f,
        ) + fadeIn(
            animationSpec = tween(AnimSpec.DURATION_SHORT, easing = AnimSpec.ENTER_EASING),
        )
    }

    val scaleOut: NavExitTransition = {
        scaleOut(
            animationSpec = tween(AnimSpec.DURATION_SHORT, easing = AnimSpec.EXIT_EASING),
            targetScale = 1.1f,
        ) + fadeOut(
            animationSpec = tween(AnimSpec.DURATION_SHORT, easing = AnimSpec.EXIT_EASING),
        )
    }

    // ──────────────── Slide + Fade (recommended for forward/back) ────────────────

    /** Forward enter: slide in from the trailing edge with a staggered fade. */
    val slideInFromEnd: NavEnterTransition = {
        slideInHorizontally(
            animationSpec = tween(AnimSpec.DURATION_MEDIUM, easing = AnimSpec.ENTER_EASING),
            initialOffsetX = { it },
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = AnimSpec.DURATION_SHORT,
                delayMillis = 100,
                easing = AnimSpec.ENTER_EASING,
            ),
            initialAlpha = 0.3f,
        )
    }

    /** Forward exit: slide out toward the leading edge with a parallax-style partial offset. */
    val slideOutToStart: NavExitTransition = {
        slideOutHorizontally(
            animationSpec = tween(AnimSpec.DURATION_MEDIUM, easing = AnimSpec.EXIT_EASING),
            targetOffsetX = { -it / 3 },
        ) + fadeOut(
            animationSpec = tween(AnimSpec.DURATION_MEDIUM, easing = AnimSpec.EXIT_EASING),
        )
    }

    /** Pop enter: slide in from the leading edge (reverse of forward exit). */
    val slideInFromStart: NavEnterTransition = {
        slideInHorizontally(
            animationSpec = tween(AnimSpec.DURATION_MEDIUM, easing = AnimSpec.ENTER_EASING),
            initialOffsetX = { -it },
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = AnimSpec.DURATION_SHORT,
                delayMillis = 100,
                easing = AnimSpec.ENTER_EASING,
            ),
            initialAlpha = 0.3f,
        )
    }

    /** Pop exit: slide out toward the trailing edge (reverse of forward enter). */
    val slideOutToEnd: NavExitTransition = {
        slideOutHorizontally(
            animationSpec = tween(AnimSpec.DURATION_MEDIUM, easing = AnimSpec.EXIT_EASING),
            targetOffsetX = { it / 3 },
        ) + fadeOut(
            animationSpec = tween(AnimSpec.DURATION_MEDIUM, easing = AnimSpec.EXIT_EASING),
        )
    }

    // ──────────────── Modal (bottom sheet-style) ────────────────

    /** Modal enter: slide up from the bottom with a fade. */
    val modalEnter: NavEnterTransition = {
        slideInVertically(
            animationSpec = tween(AnimSpec.DURATION_MEDIUM, easing = AnimSpec.ENTER_EASING),
            initialOffsetY = { it / 2 },
        ) + fadeIn(
            animationSpec = tween(AnimSpec.DURATION_SHORT, easing = AnimSpec.ENTER_EASING),
            initialAlpha = 0.0f,
        )
    }

    /** Modal exit: slide down toward the bottom with a fade. */
    val modalExit: NavExitTransition = {
        slideOutVertically(
            animationSpec = tween(AnimSpec.DURATION_MEDIUM, easing = AnimSpec.EXIT_EASING),
            targetOffsetY = { it / 2 },
        ) + fadeOut(
            animationSpec = tween(AnimSpec.DURATION_SHORT, easing = AnimSpec.EXIT_EASING),
        )
    }

    // ──────────────── Shared Axis (Material Motion) ────────────────

    /** Shared Z-axis enter: scale up + fade in. */
    val sharedAxisZEnter: NavEnterTransition = {
        scaleIn(
            animationSpec = tween(AnimSpec.DURATION_MEDIUM, easing = AnimSpec.ENTER_EASING),
            initialScale = 0.85f,
        ) + fadeIn(
            animationSpec = tween(AnimSpec.DURATION_SHORT, easing = AnimSpec.ENTER_EASING),
        )
    }

    /** Shared Z-axis exit: scale down + fade out. */
    val sharedAxisZExit: NavExitTransition = {
        scaleOut(
            animationSpec = tween(AnimSpec.DURATION_MEDIUM, easing = AnimSpec.EXIT_EASING),
            targetScale = 0.85f,
        ) + fadeOut(
            animationSpec = tween(AnimSpec.DURATION_SHORT, easing = AnimSpec.EXIT_EASING),
        )
    }

    // ──────────────── None ────────────────

    val none: NavEnterTransition = { EnterTransition.None }
    val noneExit: NavExitTransition = { ExitTransition.None }
}
