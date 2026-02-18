package com.application.echo.core.navigation.transition

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavBackStackEntry

// ──────────────────────────────────────────────────────────────────────────────
// Typealiases — Two-tier provider system
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Enter transition provider for **composable-level** destinations.
 * Returns `null` to defer to the parent NavHost's default transition.
 */
typealias EnterTransitionProvider =
    (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)?

/**
 * Exit transition provider for **composable-level** destinations.
 * Returns `null` to defer to the parent NavHost.
 */
typealias ExitTransitionProvider =
    (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)?

/**
 * Non-null enter transition provider for **NavHost root-level** defaults.
 */
typealias RootEnterTransitionProvider =
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition

/**
 * Non-null exit transition provider for **NavHost root-level** defaults.
 */
typealias RootExitTransitionProvider =
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition

// ──────────────────────────────────────────────────────────────────────────────
// Material 3 Easing — actual M3 spec values
// ──────────────────────────────────────────────────────────────────────────────

object EchoEasing {
    /** M3 emphasized decelerate — for elements entering the screen. */
    val emphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)

    /** M3 emphasized accelerate — for elements leaving the screen. */
    val emphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)

    /** M3 standard — symmetric, for utility transitions. */
    val standard: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

    /** M3 standard decelerate — for entering utility elements. */
    val standardDecelerate: Easing = CubicBezierEasing(0.0f, 0.0f, 0.0f, 1.0f)

    /** M3 standard accelerate — for exiting utility elements. */
    val standardAccelerate: Easing = CubicBezierEasing(0.3f, 0.0f, 1.0f, 1.0f)
}

// ──────────────────────────────────────────────────────────────────────────────
// Duration — coordinated timing constants
// ──────────────────────────────────────────────────────────────────────────────

/** Slide duration for push-style navigation (forward/back). */
private const val SLIDE_DURATION = 350

/** Fade duration — matches slide for smooth coordination. */
private const val FADE_DURATION = 300

/** Delay before fade starts on enter (lets slide establish direction first). */
private const val ENTER_FADE_DELAY = 50

/** Exit fade runs shorter — content fades quickly while sliding away. */
private const val EXIT_FADE_DURATION = 150

/** Delay on exit fade — content starts sliding first, then fades. */
private const val EXIT_FADE_DELAY = 50

/** Duration for vertical slide transitions (modals, sheets). */
private const val VERTICAL_DURATION = 400

/** Duration for scale transitions. */
private const val SCALE_DURATION = 300

/** Duration for expand/shrink (container transform). */
private const val EXPAND_DURATION = 350

/** Stay transition duration — matches the longest counterpart. */
private const val STAY_DURATION = SLIDE_DURATION

// ──────────────────────────────────────────────────────────────────────────────
// Navigation scope extensions
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Whether initial and target destinations share the same parent graph.
 *
 * When `false`, composable-level transitions should return `null`
 * so the root host handles the cross-graph animation.
 */
val AnimatedContentTransitionScope<NavBackStackEntry>.isSameGraphNavigation: Boolean
    get() = initialState.destination.parent?.id == targetState.destination.parent?.id

// ──────────────────────────────────────────────────────────────────────────────
// Composable-level transition providers (nullable)
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Nullable transition providers for **composable-level** destinations.
 *
 * Returns `null` when navigation crosses graph boundaries, deferring
 * to the root host's default.
 */
object EchoTransitionProviders {

    object Enter {

        val slideFromEnd: EnterTransitionProvider = {
            if (!isSameGraphNavigation) null
            else slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(SLIDE_DURATION, easing = EchoEasing.emphasizedDecelerate),
            ) + fadeIn(
                animationSpec = tween(FADE_DURATION, delayMillis = ENTER_FADE_DELAY, easing = EchoEasing.standardDecelerate),
            )
        }

        val slideFromStart: EnterTransitionProvider = {
            if (!isSameGraphNavigation) null
            else slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(SLIDE_DURATION, easing = EchoEasing.emphasizedDecelerate),
            ) + fadeIn(
                animationSpec = tween(FADE_DURATION, delayMillis = ENTER_FADE_DELAY, easing = EchoEasing.standardDecelerate),
            )
        }

        val fadeIn: EnterTransitionProvider = {
            if (!isSameGraphNavigation) null
            else fadeIn(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardDecelerate))
        }

        val slideFromBottom: EnterTransitionProvider = {
            if (!isSameGraphNavigation) null
            else slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(VERTICAL_DURATION, easing = EchoEasing.emphasizedDecelerate),
            )
        }

        val stay: EnterTransitionProvider = {
            fadeIn(animationSpec = tween(durationMillis = STAY_DURATION), initialAlpha = STAY_ALPHA)
        }

        val scaleUp: EnterTransitionProvider = {
            if (!isSameGraphNavigation) null
            else scaleIn(
                animationSpec = tween(SCALE_DURATION, easing = EchoEasing.emphasizedDecelerate),
                initialScale = 0.92f,
            ) + fadeIn(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardDecelerate))
        }

        val expand: EnterTransitionProvider = {
            if (!isSameGraphNavigation) null
            else scaleIn(
                animationSpec = tween(EXPAND_DURATION, easing = EchoEasing.emphasizedDecelerate),
                initialScale = 0.8f,
            ) + slideInVertically(
                animationSpec = tween(EXPAND_DURATION, easing = EchoEasing.emphasizedDecelerate),
                initialOffsetY = { it / 8 },
            ) + fadeIn(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardDecelerate))
        }
    }

    object Exit {

        val slideToStart: ExitTransitionProvider = {
            if (!isSameGraphNavigation) null
            else slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(SLIDE_DURATION, easing = EchoEasing.emphasizedAccelerate),
            ) + fadeOut(
                animationSpec = tween(EXIT_FADE_DURATION, delayMillis = EXIT_FADE_DELAY, easing = EchoEasing.standardAccelerate),
            )
        }

        val slideToEnd: ExitTransitionProvider = {
            if (!isSameGraphNavigation) null
            else slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(SLIDE_DURATION, easing = EchoEasing.emphasizedAccelerate),
            ) + fadeOut(
                animationSpec = tween(EXIT_FADE_DURATION, delayMillis = EXIT_FADE_DELAY, easing = EchoEasing.standardAccelerate),
            )
        }

        val fadeOut: ExitTransitionProvider = {
            if (!isSameGraphNavigation) null
            else fadeOut(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardAccelerate))
        }

        val slideToBottom: ExitTransitionProvider = {
            if (!isSameGraphNavigation) null
            else slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(VERTICAL_DURATION, easing = EchoEasing.emphasizedAccelerate),
            )
        }

        val stay: ExitTransitionProvider = {
            fadeOut(animationSpec = tween(durationMillis = STAY_DURATION), targetAlpha = STAY_ALPHA)
        }

        val scaleDown: ExitTransitionProvider = {
            if (!isSameGraphNavigation) null
            else scaleOut(
                animationSpec = tween(SCALE_DURATION, easing = EchoEasing.emphasizedAccelerate),
                targetScale = 0.92f,
            ) + fadeOut(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardAccelerate))
        }

        val shrink: ExitTransitionProvider = {
            if (!isSameGraphNavigation) null
            else scaleOut(
                animationSpec = tween(EXPAND_DURATION, easing = EchoEasing.emphasizedAccelerate),
                targetScale = 0.8f,
            ) + slideOutVertically(
                animationSpec = tween(EXPAND_DURATION, easing = EchoEasing.emphasizedAccelerate),
                targetOffsetY = { it / 8 },
            ) + fadeOut(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardAccelerate))
        }

        val modalDim: ExitTransitionProvider = {
            fadeOut(
                animationSpec = tween(VERTICAL_DURATION, easing = EchoEasing.standard),
                targetAlpha = 0.85f,
            ) + scaleOut(
                animationSpec = tween(VERTICAL_DURATION, easing = EchoEasing.standard),
                targetScale = 0.97f,
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Root-level transition providers (non-null)
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Non-null transition providers for **NavHost root-level** defaults.
 *
 * Always returns a concrete transition — the final fallback when
 * composable-level providers return `null`.
 */
object EchoTransitions {

    object Enter {

        val slideFromEnd: RootEnterTransitionProvider = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(SLIDE_DURATION, easing = EchoEasing.emphasizedDecelerate),
            ) + fadeIn(
                animationSpec = tween(FADE_DURATION, delayMillis = ENTER_FADE_DELAY, easing = EchoEasing.standardDecelerate),
            )
        }

        val slideFromStart: RootEnterTransitionProvider = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(SLIDE_DURATION, easing = EchoEasing.emphasizedDecelerate),
            ) + fadeIn(
                animationSpec = tween(FADE_DURATION, delayMillis = ENTER_FADE_DELAY, easing = EchoEasing.standardDecelerate),
            )
        }

        val fadeIn: RootEnterTransitionProvider = {
            fadeIn(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardDecelerate))
        }

        val slideFromBottom: RootEnterTransitionProvider = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(VERTICAL_DURATION, easing = EchoEasing.emphasizedDecelerate),
            )
        }

        val slideFromTop: RootEnterTransitionProvider = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(VERTICAL_DURATION, easing = EchoEasing.emphasizedDecelerate),
            )
        }

        val stay: RootEnterTransitionProvider = {
            fadeIn(animationSpec = tween(durationMillis = STAY_DURATION), initialAlpha = STAY_ALPHA)
        }

        val scaleUp: RootEnterTransitionProvider = {
            scaleIn(
                animationSpec = tween(SCALE_DURATION, easing = EchoEasing.emphasizedDecelerate),
                initialScale = 0.92f,
            ) + fadeIn(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardDecelerate))
        }

        val popScale: RootEnterTransitionProvider = {
            scaleIn(
                animationSpec = tween(SCALE_DURATION, easing = EchoEasing.emphasizedDecelerate),
                initialScale = 0.94f,
            ) + fadeIn(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardDecelerate))
        }

        val sharedAxisX: RootEnterTransitionProvider = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(SLIDE_DURATION, easing = EchoEasing.emphasizedDecelerate),
                initialOffset = { (it * 0.3f).toInt() },
            ) + fadeIn(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardDecelerate))
        }

        val sharedAxisXPop: RootEnterTransitionProvider = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(SLIDE_DURATION, easing = EchoEasing.emphasizedDecelerate),
                initialOffset = { (it * 0.3f).toInt() },
            ) + fadeIn(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardDecelerate))
        }

        val sharedAxisY: RootEnterTransitionProvider = {
            slideInVertically(
                animationSpec = tween(SLIDE_DURATION, easing = EchoEasing.emphasizedDecelerate),
                initialOffsetY = { (it * 0.3f).toInt() },
            ) + fadeIn(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardDecelerate))
        }

        val sharedAxisYPop: RootEnterTransitionProvider = {
            slideInVertically(
                animationSpec = tween(SLIDE_DURATION, easing = EchoEasing.emphasizedDecelerate),
                initialOffsetY = { -(it * 0.3f).toInt() },
            ) + fadeIn(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardDecelerate))
        }

        val modalRestore: RootEnterTransitionProvider = {
            scaleIn(
                animationSpec = tween(VERTICAL_DURATION, easing = EchoEasing.emphasizedDecelerate),
                initialScale = 0.97f,
            ) + fadeIn(
                animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardDecelerate),
                initialAlpha = 0.85f,
            )
        }

        val expand: RootEnterTransitionProvider = {
            scaleIn(
                animationSpec = tween(EXPAND_DURATION, easing = EchoEasing.emphasizedDecelerate),
                initialScale = 0.8f,
            ) + slideInVertically(
                animationSpec = tween(EXPAND_DURATION, easing = EchoEasing.emphasizedDecelerate),
                initialOffsetY = { it / 8 },
            ) + fadeIn(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardDecelerate))
        }

        val expandRestore: RootEnterTransitionProvider = {
            scaleIn(
                animationSpec = tween(SCALE_DURATION, easing = EchoEasing.emphasizedDecelerate),
                initialScale = 0.97f,
            ) + fadeIn(
                animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardDecelerate),
                initialAlpha = 0.7f,
            )
        }

        val none: RootEnterTransitionProvider = { EnterTransition.None }
    }

    object Exit {

        val slideToStart: RootExitTransitionProvider = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(SLIDE_DURATION, easing = EchoEasing.emphasizedAccelerate),
            ) + fadeOut(
                animationSpec = tween(EXIT_FADE_DURATION, delayMillis = EXIT_FADE_DELAY, easing = EchoEasing.standardAccelerate),
            )
        }

        val slideToEnd: RootExitTransitionProvider = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(SLIDE_DURATION, easing = EchoEasing.emphasizedAccelerate),
            ) + fadeOut(
                animationSpec = tween(EXIT_FADE_DURATION, delayMillis = EXIT_FADE_DELAY, easing = EchoEasing.standardAccelerate),
            )
        }

        val fadeOut: RootExitTransitionProvider = {
            fadeOut(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardAccelerate))
        }

        val slideToBottom: RootExitTransitionProvider = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(VERTICAL_DURATION, easing = EchoEasing.emphasizedAccelerate),
            )
        }

        val slideToTop: RootExitTransitionProvider = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(VERTICAL_DURATION, easing = EchoEasing.emphasizedAccelerate),
            )
        }

        val stay: RootExitTransitionProvider = {
            fadeOut(animationSpec = tween(durationMillis = STAY_DURATION), targetAlpha = STAY_ALPHA)
        }

        val scaleDown: RootExitTransitionProvider = {
            scaleOut(
                animationSpec = tween(SCALE_DURATION, easing = EchoEasing.emphasizedAccelerate),
                targetScale = 0.92f,
            ) + fadeOut(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardAccelerate))
        }

        val popScale: RootExitTransitionProvider = {
            scaleOut(
                animationSpec = tween(SCALE_DURATION, easing = EchoEasing.emphasizedAccelerate),
                targetScale = 0.94f,
            ) + fadeOut(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardAccelerate))
        }

        val scaleUp: RootExitTransitionProvider = {
            scaleOut(
                animationSpec = tween(SCALE_DURATION, easing = EchoEasing.emphasizedAccelerate),
                targetScale = 1.06f,
            ) + fadeOut(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardAccelerate))
        }

        val sharedAxisX: RootExitTransitionProvider = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(SLIDE_DURATION, easing = EchoEasing.emphasizedAccelerate),
                targetOffset = { (it * 0.3f).toInt() },
            ) + fadeOut(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardAccelerate))
        }

        val sharedAxisXPop: RootExitTransitionProvider = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(SLIDE_DURATION, easing = EchoEasing.emphasizedAccelerate),
                targetOffset = { (it * 0.3f).toInt() },
            ) + fadeOut(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardAccelerate))
        }

        val sharedAxisY: RootExitTransitionProvider = {
            slideOutVertically(
                animationSpec = tween(SLIDE_DURATION, easing = EchoEasing.emphasizedAccelerate),
                targetOffsetY = { -(it * 0.3f).toInt() },
            ) + fadeOut(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardAccelerate))
        }

        val sharedAxisYPop: RootExitTransitionProvider = {
            slideOutVertically(
                animationSpec = tween(SLIDE_DURATION, easing = EchoEasing.emphasizedAccelerate),
                targetOffsetY = { (it * 0.3f).toInt() },
            ) + fadeOut(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardAccelerate))
        }

        val modalDim: RootExitTransitionProvider = {
            fadeOut(
                animationSpec = tween(VERTICAL_DURATION, easing = EchoEasing.standard),
                targetAlpha = 0.85f,
            ) + scaleOut(
                animationSpec = tween(VERTICAL_DURATION, easing = EchoEasing.standard),
                targetScale = 0.97f,
            )
        }

        val shrink: RootExitTransitionProvider = {
            scaleOut(
                animationSpec = tween(EXPAND_DURATION, easing = EchoEasing.emphasizedAccelerate),
                targetScale = 0.8f,
            ) + slideOutVertically(
                animationSpec = tween(EXPAND_DURATION, easing = EchoEasing.emphasizedAccelerate),
                targetOffsetY = { it / 8 },
            ) + fadeOut(animationSpec = tween(FADE_DURATION, easing = EchoEasing.standardAccelerate))
        }

        val none: RootExitTransitionProvider = { ExitTransition.None }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Internal constants
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Alpha for "stay" transitions — 0.99f so Compose doesn't optimize it away.
 */
private const val STAY_ALPHA = 0.99f
