package com.application.echo.core.navigation.transition

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
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

// ──────────────────────────────────────────────────────────────────────────────
// Typealiases
// ──────────────────────────────────────────────────────────────────────────────

/** Convenience typealias for navigation enter-transition lambdas. */
typealias NavEnterTransition =
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition

/** Convenience typealias for navigation exit-transition lambdas. */
typealias NavExitTransition =
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition

// ──────────────────────────────────────────────────────────────────────────────
// Easing
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Easing curves used across Echo navigation transitions.
 *
 * Based on Material 3 motion guidelines:
 * - **emphasized**: For prominent, high-attention transitions (forward navigation).
 * - **emphasizedDecelerate**: For elements arriving on-screen.
 * - **emphasizedAccelerate**: For elements leaving the screen.
 * - **standard**: General-purpose symmetric easing.
 * - **standardDecelerate / standardAccelerate**: Asymmetric standard motion.
 */
object EchoEasing {
    /** Material 3 emphasized — natural, expressive deceleration. */
    val emphasized: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

    /** Material 3 emphasized decelerate — incoming elements settle in. */
    val emphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)

    /** Material 3 emphasized accelerate — outgoing elements pick up speed. */
    val emphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)

    /** Material 3 standard — balanced, symmetric motion. */
    val standard: Easing = FastOutSlowInEasing

    /** Material 3 standard decelerate — incoming, less dramatic than emphasized. */
    val standardDecelerate: Easing = LinearOutSlowInEasing

    /** Material 3 standard accelerate — outgoing, less dramatic than emphasized. */
    val standardAccelerate: Easing = FastOutLinearInEasing
}

// ──────────────────────────────────────────────────────────────────────────────
// Duration
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Canonical duration tokens for navigation transitions.
 *
 * Aligned with Material 3 motion duration guidelines.
 * All values are in milliseconds.
 */
object EchoDuration {
    /** Quick micro-interactions (ripples, icon swaps). */
    const val SHORT_1 = 100

    /** Subtle fades, small state changes. */
    const val SHORT_2 = 150

    /** Standard fades, opacity changes. */
    const val SHORT_3 = 200

    /** Fast slides, snappy push/pop. */
    const val SHORT_4 = 250

    /** Default navigation slide + fade. */
    const val MEDIUM_1 = 300

    /** Standard screen transitions. */
    const val MEDIUM_2 = 350

    /** Slightly longer for complex motion. */
    const val MEDIUM_3 = 400

    /** Full-screen modals, large surfaces. */
    const val LONG_1 = 450

    /** Complex shared-axis transitions. */
    const val LONG_2 = 500

    /** Very large surface changes, onboarding. */
    const val EXTRA_LONG = 600
}

// ──────────────────────────────────────────────────────────────────────────────
// Extensions
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Whether this scope's transition represents forward navigation (same parent graph).
 */
val AnimatedContentTransitionScope<NavBackStackEntry>.isForwardNavigation: Boolean
    get() = initialState.destination.parent?.id == targetState.destination.parent?.id

// ──────────────────────────────────────────────────────────────────────────────
// Transition library
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Complete library of reusable, polished navigation transitions.
 *
 * Every animation family provides **enter**, **exit**, **pop-enter**, and **pop-exit**
 * variants so you can mix-and-match freely. The object also exposes factory functions
 * (`slideHorizontal()`, `fade()`, etc.) for full configurability — custom durations,
 * easings, offsets — while the properties give zero-config sensible defaults.
 *
 * ```kotlin
 * // Zero-config — use a property directly
 * EchoNavHost(
 *     enterTransition = EchoTransitions.slideInFromEnd,
 *     exitTransition = EchoTransitions.slideOutToStart,
 * )
 *
 * // Custom config — use a factory
 * echoComposable<Route>(
 *     enterTransition = EchoTransitions.slideHorizontal(
 *         durationMillis = 400,
 *         initialOffsetX = { it / 2 },
 *     ),
 * )
 * ```
 */
object EchoTransitions {

    // ┌─────────────────────────────────────────────────────────────────────┐
    // │  FADE                                                               │
    // └─────────────────────────────────────────────────────────────────────┘

    val fadeIn: NavEnterTransition = {
        fadeIn(
            animationSpec = tween(
                durationMillis = EchoDuration.SHORT_4,
                easing = EchoEasing.emphasizedDecelerate,
            ),
        )
    }

    val fadeOut: NavExitTransition = {
        fadeOut(
            animationSpec = tween(
                durationMillis = EchoDuration.SHORT_4,
                easing = EchoEasing.emphasizedAccelerate,
            ),
        )
    }

    /** Pop enter — identical to [fadeIn] for symmetric cross-fades. */
    val fadePopEnter: NavEnterTransition = fadeIn

    /** Pop exit — identical to [fadeOut] for symmetric cross-fades. */
    val fadePopExit: NavExitTransition = fadeOut

    /**
     * Factory: custom fade-in transition.
     *
     * @param durationMillis Animation duration. Default [EchoDuration.SHORT_4].
     * @param delayMillis Start delay. Default 0.
     * @param easing Interpolation curve. Default [EchoEasing.emphasizedDecelerate].
     * @param initialAlpha Starting opacity. Default 0f.
     */
    fun fade(
        durationMillis: Int = EchoDuration.SHORT_4,
        delayMillis: Int = 0,
        easing: Easing = EchoEasing.emphasizedDecelerate,
        initialAlpha: Float = 0f,
    ): NavEnterTransition = {
        fadeIn(
            animationSpec = tween(durationMillis, delayMillis, easing),
            initialAlpha = initialAlpha,
        )
    }

    /**
     * Factory: custom fade-out transition.
     *
     * @param durationMillis Animation duration. Default [EchoDuration.SHORT_4].
     * @param delayMillis Start delay. Default 0.
     * @param easing Interpolation curve. Default [EchoEasing.emphasizedAccelerate].
     * @param targetAlpha Ending opacity. Default 0f.
     */
    fun fadeExit(
        durationMillis: Int = EchoDuration.SHORT_4,
        delayMillis: Int = 0,
        easing: Easing = EchoEasing.emphasizedAccelerate,
        targetAlpha: Float = 0f,
    ): NavExitTransition = {
        fadeOut(
            animationSpec = tween(durationMillis, delayMillis, easing),
            targetAlpha = targetAlpha,
        )
    }

    // ┌─────────────────────────────────────────────────────────────────────┐
    // │  SLIDE — Horizontal (raw, full-width)                               │
    // └─────────────────────────────────────────────────────────────────────┘

    val slideInLeft: NavEnterTransition = {
        slideInHorizontally(
            animationSpec = tween(EchoDuration.MEDIUM_1, easing = EchoEasing.emphasizedDecelerate),
            initialOffsetX = { -it },
        )
    }

    val slideInRight: NavEnterTransition = {
        slideInHorizontally(
            animationSpec = tween(EchoDuration.MEDIUM_1, easing = EchoEasing.emphasizedDecelerate),
            initialOffsetX = { it },
        )
    }

    val slideOutLeft: NavExitTransition = {
        slideOutHorizontally(
            animationSpec = tween(EchoDuration.MEDIUM_1, easing = EchoEasing.emphasizedAccelerate),
            targetOffsetX = { -it },
        )
    }

    val slideOutRight: NavExitTransition = {
        slideOutHorizontally(
            animationSpec = tween(EchoDuration.MEDIUM_1, easing = EchoEasing.emphasizedAccelerate),
            targetOffsetX = { it },
        )
    }

    /**
     * Factory: custom horizontal slide-in.
     *
     * @param durationMillis Animation duration.
     * @param easing Interpolation curve.
     * @param initialOffsetX Lambda returning the initial X pixel offset.
     */
    fun slideHorizontal(
        durationMillis: Int = EchoDuration.MEDIUM_1,
        easing: Easing = EchoEasing.emphasizedDecelerate,
        initialOffsetX: (fullWidth: Int) -> Int = { it },
    ): NavEnterTransition = {
        slideInHorizontally(
            animationSpec = tween(durationMillis, easing = easing),
            initialOffsetX = initialOffsetX,
        )
    }

    /**
     * Factory: custom horizontal slide-out.
     *
     * @param durationMillis Animation duration.
     * @param easing Interpolation curve.
     * @param targetOffsetX Lambda returning the target X pixel offset.
     */
    fun slideHorizontalExit(
        durationMillis: Int = EchoDuration.MEDIUM_1,
        easing: Easing = EchoEasing.emphasizedAccelerate,
        targetOffsetX: (fullWidth: Int) -> Int = { -it },
    ): NavExitTransition = {
        slideOutHorizontally(
            animationSpec = tween(durationMillis, easing = easing),
            targetOffsetX = targetOffsetX,
        )
    }

    // ┌─────────────────────────────────────────────────────────────────────┐
    // │  SLIDE — Vertical (raw, full-height)                                │
    // └─────────────────────────────────────────────────────────────────────┘

    /** Slide in from bottom. */
    val slideInUp: NavEnterTransition = {
        slideInVertically(
            animationSpec = tween(EchoDuration.LONG_1, easing = EchoEasing.emphasizedDecelerate),
            initialOffsetY = { it },
        )
    }

    /** Slide out toward bottom. */
    val slideOutDown: NavExitTransition = {
        slideOutVertically(
            animationSpec = tween(EchoDuration.LONG_1, easing = EchoEasing.emphasizedAccelerate),
            targetOffsetY = { it },
        )
    }

    /** Slide in from top. */
    val slideInDown: NavEnterTransition = {
        slideInVertically(
            animationSpec = tween(EchoDuration.LONG_1, easing = EchoEasing.emphasizedDecelerate),
            initialOffsetY = { -it },
        )
    }

    /** Slide out toward top. */
    val slideOutUp: NavExitTransition = {
        slideOutVertically(
            animationSpec = tween(EchoDuration.LONG_1, easing = EchoEasing.emphasizedAccelerate),
            targetOffsetY = { -it },
        )
    }

    /**
     * Factory: custom vertical slide-in.
     */
    fun slideVertical(
        durationMillis: Int = EchoDuration.LONG_1,
        easing: Easing = EchoEasing.emphasizedDecelerate,
        initialOffsetY: (fullHeight: Int) -> Int = { it },
    ): NavEnterTransition = {
        slideInVertically(
            animationSpec = tween(durationMillis, easing = easing),
            initialOffsetY = initialOffsetY,
        )
    }

    /**
     * Factory: custom vertical slide-out.
     */
    fun slideVerticalExit(
        durationMillis: Int = EchoDuration.LONG_1,
        easing: Easing = EchoEasing.emphasizedAccelerate,
        targetOffsetY: (fullHeight: Int) -> Int = { it },
    ): NavExitTransition = {
        slideOutVertically(
            animationSpec = tween(durationMillis, easing = easing),
            targetOffsetY = targetOffsetY,
        )
    }

    // ┌─────────────────────────────────────────────────────────────────────┐
    // │  SLIDE + FADE (default forward / back navigation)                   │
    // └─────────────────────────────────────────────────────────────────────┘

    /** Forward enter: slide in from trailing edge + staggered fade. */
    val slideInFromEnd: NavEnterTransition = {
        slideInHorizontally(
            animationSpec = tween(EchoDuration.MEDIUM_2, easing = EchoEasing.emphasizedDecelerate),
            initialOffsetX = { it },
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = EchoDuration.SHORT_3,
                delayMillis = SHORT_STAGGER,
                easing = EchoEasing.emphasizedDecelerate,
            ),
            initialAlpha = STAGGER_INITIAL_ALPHA,
        )
    }

    /** Forward exit: parallax slide toward leading edge + fade. */
    val slideOutToStart: NavExitTransition = {
        slideOutHorizontally(
            animationSpec = tween(EchoDuration.MEDIUM_2, easing = EchoEasing.emphasizedAccelerate),
            targetOffsetX = { -it / PARALLAX_DIVISOR },
        ) + fadeOut(
            animationSpec = tween(EchoDuration.MEDIUM_1, easing = EchoEasing.emphasizedAccelerate),
        )
    }

    /** Pop enter: slide in from leading edge (reverse of forward exit). */
    val slideInFromStart: NavEnterTransition = {
        slideInHorizontally(
            animationSpec = tween(EchoDuration.MEDIUM_2, easing = EchoEasing.emphasizedDecelerate),
            initialOffsetX = { -it / PARALLAX_DIVISOR },
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = EchoDuration.SHORT_3,
                delayMillis = SHORT_STAGGER,
                easing = EchoEasing.emphasizedDecelerate,
            ),
            initialAlpha = STAGGER_INITIAL_ALPHA,
        )
    }

    /** Pop exit: slide out toward trailing edge (reverse of forward enter). */
    val slideOutToEnd: NavExitTransition = {
        slideOutHorizontally(
            animationSpec = tween(EchoDuration.MEDIUM_2, easing = EchoEasing.emphasizedAccelerate),
            targetOffsetX = { it },
        ) + fadeOut(
            animationSpec = tween(EchoDuration.MEDIUM_1, easing = EchoEasing.emphasizedAccelerate),
        )
    }

    // ┌─────────────────────────────────────────────────────────────────────┐
    // │  SLIDE + FADE — Vertical (for modals / sheets)                      │
    // └─────────────────────────────────────────────────────────────────────┘

    /** Slide up from bottom half + fade in. */
    val slideFadeInUp: NavEnterTransition = {
        slideInVertically(
            animationSpec = tween(EchoDuration.MEDIUM_2, easing = EchoEasing.emphasizedDecelerate),
            initialOffsetY = { it / 2 },
        ) + fadeIn(
            animationSpec = tween(EchoDuration.SHORT_3, easing = EchoEasing.emphasizedDecelerate),
            initialAlpha = 0f,
        )
    }

    /** Slide down + fade out (reverse of [slideFadeInUp]). */
    val slideFadeOutDown: NavExitTransition = {
        slideOutVertically(
            animationSpec = tween(EchoDuration.MEDIUM_1, easing = EchoEasing.emphasizedAccelerate),
            targetOffsetY = { it / 2 },
        ) + fadeOut(
            animationSpec = tween(EchoDuration.SHORT_3, easing = EchoEasing.emphasizedAccelerate),
        )
    }

    /** Slide down from top half + fade in. */
    val slideFadeInDown: NavEnterTransition = {
        slideInVertically(
            animationSpec = tween(EchoDuration.MEDIUM_2, easing = EchoEasing.emphasizedDecelerate),
            initialOffsetY = { -it / 2 },
        ) + fadeIn(
            animationSpec = tween(EchoDuration.SHORT_3, easing = EchoEasing.emphasizedDecelerate),
            initialAlpha = 0f,
        )
    }

    /** Slide up + fade out (reverse of [slideFadeInDown]). */
    val slideFadeOutUp: NavExitTransition = {
        slideOutVertically(
            animationSpec = tween(EchoDuration.MEDIUM_1, easing = EchoEasing.emphasizedAccelerate),
            targetOffsetY = { -it / 2 },
        ) + fadeOut(
            animationSpec = tween(EchoDuration.SHORT_3, easing = EchoEasing.emphasizedAccelerate),
        )
    }

    // ┌─────────────────────────────────────────────────────────────────────┐
    // │  MODAL (bottom sheet-style)                                         │
    // └─────────────────────────────────────────────────────────────────────┘

    /** Modal enter: slide up from bottom edge with fade. */
    val modalEnter: NavEnterTransition = {
        slideInVertically(
            animationSpec = tween(EchoDuration.MEDIUM_2, easing = EchoEasing.emphasized),
            initialOffsetY = { it / 2 },
        ) + fadeIn(
            animationSpec = tween(EchoDuration.SHORT_3, easing = EchoEasing.standardDecelerate),
            initialAlpha = 0f,
        )
    }

    /** Modal exit: slide down + fade. */
    val modalExit: NavExitTransition = {
        slideOutVertically(
            animationSpec = tween(EchoDuration.MEDIUM_1, easing = EchoEasing.emphasizedAccelerate),
            targetOffsetY = { it / 2 },
        ) + fadeOut(
            animationSpec = tween(EchoDuration.SHORT_3, easing = EchoEasing.standardAccelerate),
        )
    }

    /** The screen behind the modal fades/shrinks slightly while modal is open. */
    val modalPopEnter: NavEnterTransition = {
        fadeIn(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.standardDecelerate),
            initialAlpha = 0.8f,
        ) + scaleIn(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.standardDecelerate),
            initialScale = 0.96f,
        )
    }

    /** The screen behind the modal dims slightly as the modal slides in. */
    val modalPopExit: NavExitTransition = {
        fadeOut(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.standardAccelerate),
            targetAlpha = 0.8f,
        ) + scaleOut(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.standardAccelerate),
            targetScale = 0.96f,
        )
    }

    // ┌─────────────────────────────────────────────────────────────────────┐
    // │  SCALE + FADE                                                       │
    // └─────────────────────────────────────────────────────────────────────┘

    /** Scale up from 90% + fade in. */
    val scaleIn: NavEnterTransition = {
        scaleIn(
            animationSpec = tween(EchoDuration.MEDIUM_1, easing = EchoEasing.emphasizedDecelerate),
            initialScale = 0.9f,
        ) + fadeIn(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.emphasizedDecelerate),
        )
    }

    /** Scale up to 110% + fade out. */
    val scaleOut: NavExitTransition = {
        scaleOut(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.emphasizedAccelerate),
            targetScale = 1.1f,
        ) + fadeOut(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.emphasizedAccelerate),
        )
    }

    /** Pop enter: scale down from 110% + fade in (reverse of [scaleOut]). */
    val scalePopEnter: NavEnterTransition = {
        scaleIn(
            animationSpec = tween(EchoDuration.MEDIUM_1, easing = EchoEasing.emphasizedDecelerate),
            initialScale = 1.1f,
        ) + fadeIn(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.emphasizedDecelerate),
        )
    }

    /** Pop exit: scale down to 90% + fade out (reverse of [scaleIn]). */
    val scalePopExit: NavExitTransition = {
        scaleOut(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.emphasizedAccelerate),
            targetScale = 0.9f,
        ) + fadeOut(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.emphasizedAccelerate),
        )
    }

    /**
     * Factory: custom scale-in transition.
     *
     * @param durationMillis Animation duration.
     * @param easing Interpolation curve.
     * @param initialScale Starting scale factor.
     * @param withFade Whether to combine with a fade-in.
     */
    fun scale(
        durationMillis: Int = EchoDuration.MEDIUM_1,
        easing: Easing = EchoEasing.emphasizedDecelerate,
        initialScale: Float = 0.9f,
        withFade: Boolean = true,
    ): NavEnterTransition = {
        val scaleAnim = scaleIn(
            animationSpec = tween(durationMillis, easing = easing),
            initialScale = initialScale,
        )
        if (withFade) {
            scaleAnim + fadeIn(animationSpec = tween(durationMillis, easing = easing))
        } else {
            scaleAnim
        }
    }

    /**
     * Factory: custom scale-out transition.
     */
    fun scaleExit(
        durationMillis: Int = EchoDuration.SHORT_4,
        easing: Easing = EchoEasing.emphasizedAccelerate,
        targetScale: Float = 1.1f,
        withFade: Boolean = true,
    ): NavExitTransition = {
        val scaleAnim = scaleOut(
            animationSpec = tween(durationMillis, easing = easing),
            targetScale = targetScale,
        )
        if (withFade) {
            scaleAnim + fadeOut(animationSpec = tween(durationMillis, easing = easing))
        } else {
            scaleAnim
        }
    }

    // ┌─────────────────────────────────────────────────────────────────────┐
    // │  SHARED AXIS Z (Material Motion)                                    │
    // └─────────────────────────────────────────────────────────────────────┘

    /** Shared Z-axis enter: subtle scale-up from 85% + fade in. */
    val sharedAxisZEnter: NavEnterTransition = {
        scaleIn(
            animationSpec = tween(EchoDuration.MEDIUM_2, easing = EchoEasing.emphasized),
            initialScale = 0.85f,
        ) + fadeIn(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.emphasizedDecelerate),
        )
    }

    /** Shared Z-axis exit: scale down to 85% + fade out. */
    val sharedAxisZExit: NavExitTransition = {
        scaleOut(
            animationSpec = tween(EchoDuration.MEDIUM_2, easing = EchoEasing.emphasized),
            targetScale = 0.85f,
        ) + fadeOut(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.emphasizedAccelerate),
        )
    }

    /** Shared Z-axis pop enter (same as enter — symmetric motion). */
    val sharedAxisZPopEnter: NavEnterTransition = sharedAxisZEnter

    /** Shared Z-axis pop exit (same as exit — symmetric motion). */
    val sharedAxisZPopExit: NavExitTransition = sharedAxisZExit

    // ┌─────────────────────────────────────────────────────────────────────┐
    // │  SHARED AXIS X (Material Motion — horizontal)                       │
    // └─────────────────────────────────────────────────────────────────────┘

    /** Shared X-axis enter: slide in from 30% width + fade. */
    val sharedAxisXEnter: NavEnterTransition = {
        slideInHorizontally(
            animationSpec = tween(EchoDuration.MEDIUM_2, easing = EchoEasing.emphasized),
            initialOffsetX = { (it * 0.3f).toInt() },
        ) + fadeIn(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.emphasizedDecelerate),
        )
    }

    /** Shared X-axis exit: slide out to -30% width + fade. */
    val sharedAxisXExit: NavExitTransition = {
        slideOutHorizontally(
            animationSpec = tween(EchoDuration.MEDIUM_2, easing = EchoEasing.emphasized),
            targetOffsetX = { -(it * 0.3f).toInt() },
        ) + fadeOut(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.emphasizedAccelerate),
        )
    }

    /** Shared X-axis pop enter: slide in from -30% width + fade. */
    val sharedAxisXPopEnter: NavEnterTransition = {
        slideInHorizontally(
            animationSpec = tween(EchoDuration.MEDIUM_2, easing = EchoEasing.emphasized),
            initialOffsetX = { -(it * 0.3f).toInt() },
        ) + fadeIn(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.emphasizedDecelerate),
        )
    }

    /** Shared X-axis pop exit: slide out to 30% width + fade. */
    val sharedAxisXPopExit: NavExitTransition = {
        slideOutHorizontally(
            animationSpec = tween(EchoDuration.MEDIUM_2, easing = EchoEasing.emphasized),
            targetOffsetX = { (it * 0.3f).toInt() },
        ) + fadeOut(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.emphasizedAccelerate),
        )
    }

    // ┌─────────────────────────────────────────────────────────────────────┐
    // │  SHARED AXIS Y (Material Motion — vertical)                         │
    // └─────────────────────────────────────────────────────────────────────┘

    /** Shared Y-axis enter: slide up from 30% height + fade. */
    val sharedAxisYEnter: NavEnterTransition = {
        slideInVertically(
            animationSpec = tween(EchoDuration.MEDIUM_2, easing = EchoEasing.emphasized),
            initialOffsetY = { (it * 0.3f).toInt() },
        ) + fadeIn(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.emphasizedDecelerate),
        )
    }

    /** Shared Y-axis exit: slide up to -30% height + fade. */
    val sharedAxisYExit: NavExitTransition = {
        slideOutVertically(
            animationSpec = tween(EchoDuration.MEDIUM_2, easing = EchoEasing.emphasized),
            targetOffsetY = { -(it * 0.3f).toInt() },
        ) + fadeOut(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.emphasizedAccelerate),
        )
    }

    /** Shared Y-axis pop enter: slide down from -30% height + fade. */
    val sharedAxisYPopEnter: NavEnterTransition = {
        slideInVertically(
            animationSpec = tween(EchoDuration.MEDIUM_2, easing = EchoEasing.emphasized),
            initialOffsetY = { -(it * 0.3f).toInt() },
        ) + fadeIn(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.emphasizedDecelerate),
        )
    }

    /** Shared Y-axis pop exit: slide down to 30% height + fade. */
    val sharedAxisYPopExit: NavExitTransition = {
        slideOutVertically(
            animationSpec = tween(EchoDuration.MEDIUM_2, easing = EchoEasing.emphasized),
            targetOffsetY = { (it * 0.3f).toInt() },
        ) + fadeOut(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.emphasizedAccelerate),
        )
    }

    // ┌─────────────────────────────────────────────────────────────────────┐
    // │  CONTAINER TRANSFORM-LIKE (scale + slide + fade)                    │
    // └─────────────────────────────────────────────────────────────────────┘

    /** Expand from center: scale from small + slide up slightly + fade in. */
    val expandIn: NavEnterTransition = {
        scaleIn(
            animationSpec = tween(EchoDuration.MEDIUM_3, easing = EchoEasing.emphasized),
            initialScale = 0.7f,
        ) + slideInVertically(
            animationSpec = tween(EchoDuration.MEDIUM_3, easing = EchoEasing.emphasized),
            initialOffsetY = { it / 6 },
        ) + fadeIn(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.standardDecelerate),
        )
    }

    /** Shrink to center: scale down + slide down slightly + fade out. */
    val shrinkOut: NavExitTransition = {
        scaleOut(
            animationSpec = tween(EchoDuration.MEDIUM_1, easing = EchoEasing.emphasizedAccelerate),
            targetScale = 0.7f,
        ) + slideOutVertically(
            animationSpec = tween(EchoDuration.MEDIUM_1, easing = EchoEasing.emphasizedAccelerate),
            targetOffsetY = { it / 6 },
        ) + fadeOut(
            animationSpec = tween(EchoDuration.SHORT_3, easing = EchoEasing.standardAccelerate),
        )
    }

    /** Pop enter when returning from an expanded screen. */
    val expandPopEnter: NavEnterTransition = {
        scaleIn(
            animationSpec = tween(EchoDuration.MEDIUM_1, easing = EchoEasing.emphasizedDecelerate),
            initialScale = 0.96f,
        ) + fadeIn(
            animationSpec = tween(EchoDuration.SHORT_4, easing = EchoEasing.standardDecelerate),
            initialAlpha = 0.6f,
        )
    }

    /** Pop exit: the expanded screen shrinks back. */
    val shrinkPopExit: NavExitTransition = shrinkOut

    // ┌─────────────────────────────────────────────────────────────────────┐
    // │  NONE                                                               │
    // └─────────────────────────────────────────────────────────────────────┘

    val none: NavEnterTransition = { EnterTransition.None }
    val noneExit: NavExitTransition = { ExitTransition.None }

    // ──────────────────── Internal constants ────────────────────

    private const val PARALLAX_DIVISOR = 3
    private const val SHORT_STAGGER = 80
    private const val STAGGER_INITIAL_ALPHA = 0.3f
}
