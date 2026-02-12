package com.application.echo.core.navigation.transition

/**
 * Bundled set of enter / exit / popEnter / popExit transitions
 * that can be applied as a single unit to `echoComposable`.
 *
 * ```kotlin
 * echoComposable<SettingsRoute>(
 *     transition = EchoTransitionPreset.SlideHorizontal,
 * ) { SettingsScreen() }
 * ```
 */
data class EchoTransitionPreset(
    val enterTransition: NavEnterTransition,
    val exitTransition: NavExitTransition,
    val popEnterTransition: NavEnterTransition,
    val popExitTransition: NavExitTransition,
) {
    companion object {

        /**
         * Standard forward/back horizontal slide with parallax fade.
         * Best for peer-level screens (detail push, settings, etc.).
         */
        val SlideHorizontal = EchoTransitionPreset(
            enterTransition = EchoTransitions.slideInFromEnd,
            exitTransition = EchoTransitions.slideOutToStart,
            popEnterTransition = EchoTransitions.slideInFromStart,
            popExitTransition = EchoTransitions.slideOutToEnd,
        )

        /**
         * Cross-fade — ideal for tab/bottom-nav switches.
         */
        val Fade = EchoTransitionPreset(
            enterTransition = EchoTransitions.fadeIn,
            exitTransition = EchoTransitions.fadeOut,
            popEnterTransition = EchoTransitions.fadeIn,
            popExitTransition = EchoTransitions.fadeOut,
        )

        /**
         * Bottom-sheet style: slides up on enter, down on exit.
         */
        val Modal = EchoTransitionPreset(
            enterTransition = EchoTransitions.modalEnter,
            exitTransition = EchoTransitions.fadeOut,
            popEnterTransition = EchoTransitions.fadeIn,
            popExitTransition = EchoTransitions.modalExit,
        )

        /**
         * Material Motion shared Z-axis: scale + fade for hierarchical transitions.
         */
        val SharedAxisZ = EchoTransitionPreset(
            enterTransition = EchoTransitions.sharedAxisZEnter,
            exitTransition = EchoTransitions.sharedAxisZExit,
            popEnterTransition = EchoTransitions.sharedAxisZEnter,
            popExitTransition = EchoTransitions.sharedAxisZExit,
        )

        /**
         * Scale + fade: punchy zoom-in enter, zoom-out exit.
         */
        val Scale = EchoTransitionPreset(
            enterTransition = EchoTransitions.scaleIn,
            exitTransition = EchoTransitions.scaleOut,
            popEnterTransition = EchoTransitions.scaleIn,
            popExitTransition = EchoTransitions.scaleOut,
        )

        /**
         * Slide up (enter) / slide down (exit). Good for full-screen modals.
         */
        val Vertical = EchoTransitionPreset(
            enterTransition = EchoTransitions.slideInUp,
            exitTransition = EchoTransitions.slideOutDown,
            popEnterTransition = EchoTransitions.slideInDown,
            popExitTransition = EchoTransitions.slideOutDown,
        )

        /**
         * No transitions at all — instant swap.
         */
        val None = EchoTransitionPreset(
            enterTransition = EchoTransitions.none,
            exitTransition = EchoTransitions.noneExit,
            popEnterTransition = EchoTransitions.none,
            popExitTransition = EchoTransitions.noneExit,
        )
    }
}
