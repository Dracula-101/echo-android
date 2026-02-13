@file:Suppress("DEPRECATION")

package com.application.echo.ui.design.navigation

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

private object AnimationSpec {
    const val DURATION_SHORT = 250
    const val DURATION_MEDIUM = 350
    const val DURATION_LONG = 450

    val ENTER_EASING = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    val EXIT_EASING = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
}

@Deprecated(
    message = "Moved to core:navigation. Use EchoTransitions.isForwardNavigation instead.",
    level = DeprecationLevel.WARNING,
)
val AnimatedContentTransitionScope<NavBackStackEntry>.isForwardNavigation: Boolean
    get() = initialState.destination.parent?.id == targetState.destination.parent?.id

/**
 * @deprecated Transitions have moved to `core:navigation`.
 * Use [com.application.echo.core.navigation.transition.EchoTransitions] instead.
 */
@Deprecated(
    message = "Moved to core:navigation. Use EchoTransitions instead.",
    replaceWith = ReplaceWith(
        expression = "EchoTransitions",
        imports = ["com.application.echo.core.navigation.transition.EchoTransitions"],
    ),
    level = DeprecationLevel.WARNING,
)
object Transitions {

    val fadeIn: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        fadeIn(
            animationSpec = tween(
                durationMillis = AnimationSpec.DURATION_SHORT,
                easing = AnimationSpec.ENTER_EASING
            )
        )
    }

    val fadeOut: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        fadeOut(
            animationSpec = tween(
                durationMillis = AnimationSpec.DURATION_SHORT,
                easing = AnimationSpec.EXIT_EASING
            )
        )
    }

    val slideInLeft: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        slideInHorizontally(
            animationSpec = tween(
                durationMillis = AnimationSpec.DURATION_MEDIUM,
                easing = AnimationSpec.ENTER_EASING
            ),
            initialOffsetX = { -it }
        )
    }

    val slideInRight: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        slideInHorizontally(
            animationSpec = tween(
                durationMillis = AnimationSpec.DURATION_MEDIUM,
                easing = AnimationSpec.ENTER_EASING
            ),
            initialOffsetX = { it }
        )
    }

    val slideOutLeft: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        slideOutHorizontally(
            animationSpec = tween(
                durationMillis = AnimationSpec.DURATION_MEDIUM,
                easing = AnimationSpec.EXIT_EASING
            ),
            targetOffsetX = { -it }
        )
    }

    val slideOutRight: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        slideOutHorizontally(
            animationSpec = tween(
                durationMillis = AnimationSpec.DURATION_MEDIUM,
                easing = AnimationSpec.EXIT_EASING
            ),
            targetOffsetX = { it }
        )
    }

    val slideInUp: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        slideInVertically(
            animationSpec = tween(
                durationMillis = AnimationSpec.DURATION_LONG,
                easing = AnimationSpec.ENTER_EASING
            ),
            initialOffsetY = { it }
        )
    }

    val slideOutDown: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        slideOutVertically(
            animationSpec = tween(
                durationMillis = AnimationSpec.DURATION_LONG,
                easing = AnimationSpec.EXIT_EASING
            ),
            targetOffsetY = { it }
        )
    }

    val scaleIn: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        scaleIn(
            animationSpec = tween(
                durationMillis = AnimationSpec.DURATION_MEDIUM,
                easing = AnimationSpec.ENTER_EASING
            ),
            initialScale = 0.9f
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = AnimationSpec.DURATION_SHORT,
                easing = AnimationSpec.ENTER_EASING
            )
        )
    }

    val scaleOut: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        scaleOut(
            animationSpec = tween(
                durationMillis = AnimationSpec.DURATION_SHORT,
                easing = AnimationSpec.EXIT_EASING
            ),
            targetScale = 1.1f
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = AnimationSpec.DURATION_SHORT,
                easing = AnimationSpec.EXIT_EASING
            )
        )
    }

    val slideInFromEnd: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            slideInHorizontally(
                animationSpec = tween(
                    durationMillis = AnimationSpec.DURATION_MEDIUM,
                    easing = AnimationSpec.ENTER_EASING
                ),
                initialOffsetX = { it }
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = AnimationSpec.DURATION_SHORT,
                    delayMillis = 100,
                    easing = AnimationSpec.ENTER_EASING
                ),
                initialAlpha = 0.3f
            )
        }

    val slideOutToStart: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        {
            slideOutHorizontally(
                animationSpec = tween(
                    durationMillis = AnimationSpec.DURATION_MEDIUM,
                    easing = AnimationSpec.EXIT_EASING
                ),
                targetOffsetX = { -it / 3 }
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = AnimationSpec.DURATION_MEDIUM,
                    easing = AnimationSpec.EXIT_EASING
                )
            )
        }

    val slideInFromStart: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            slideInHorizontally(
                animationSpec = tween(
                    durationMillis = AnimationSpec.DURATION_MEDIUM,
                    easing = AnimationSpec.ENTER_EASING
                ),
                initialOffsetX = { -it }
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = AnimationSpec.DURATION_SHORT,
                    delayMillis = 100,
                    easing = AnimationSpec.ENTER_EASING
                ),
                initialAlpha = 0.3f
            )
        }

    val slideOutToEnd: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        slideOutHorizontally(
            animationSpec = tween(
                durationMillis = AnimationSpec.DURATION_MEDIUM,
                easing = AnimationSpec.EXIT_EASING
            ),
            targetOffsetX = { it / 3 }
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = AnimationSpec.DURATION_MEDIUM,
                easing = AnimationSpec.EXIT_EASING
            )
        )
    }

    val none: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        EnterTransition.None
    }

    val noneExit: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        ExitTransition.None
    }
}
