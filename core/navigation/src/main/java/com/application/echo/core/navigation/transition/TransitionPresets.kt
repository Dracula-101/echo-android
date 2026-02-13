package com.application.echo.core.navigation.transition

/**
 * Bundled set of enter / exit / popEnter / popExit transitions
 * that can be applied as a single unit to `echoComposable` or `EchoNavHost`.
 *
 * ## Usage
 *
 * **As a preset:**
 * ```kotlin
 * echoComposable<SettingsRoute>(
 *     transition = EchoTransitionPreset.SlideHorizontal,
 * ) { SettingsScreen() }
 * ```
 *
 * **As a host default:**
 * ```kotlin
 * EchoNavHost(
 *     navigator = navigator,
 *     startDestination = HomeRoute,
 *     transition = EchoTransitionPreset.Fade,
 * ) { ... }
 * ```
 *
 * **Custom preset via `copy`:**
 * ```kotlin
 * val customModal = EchoTransitionPreset.Modal.copy(
 *     popEnterTransition = EchoTransitions.fadeIn,
 * )
 * ```
 *
 * **Build from scratch via factory:**
 * ```kotlin
 * val slow = EchoTransitionPreset(
 *     enterTransition = EchoTransitions.fade(durationMillis = 600),
 *     exitTransition = EchoTransitions.fadeExit(durationMillis = 600),
 *     popEnterTransition = EchoTransitions.fade(durationMillis = 600),
 *     popExitTransition = EchoTransitions.fadeExit(durationMillis = 600),
 * )
 * ```
 */
data class EchoTransitionPreset(
    val enterTransition: NavEnterTransition,
    val exitTransition: NavExitTransition,
    val popEnterTransition: NavEnterTransition,
    val popExitTransition: NavExitTransition,
) {
    companion object {

        // ─────────────── Standard navigations ───────────────

        /**
         * Standard forward/back horizontal slide with parallax + staggered fade.
         *
         * Best for peer-level screen pushes (detail, settings, profile, etc.).
         * This is the default for [EchoNavHost].
         */
        val SlideHorizontal = EchoTransitionPreset(
            enterTransition = EchoTransitions.slideInFromEnd,
            exitTransition = EchoTransitions.slideOutToStart,
            popEnterTransition = EchoTransitions.slideInFromStart,
            popExitTransition = EchoTransitions.slideOutToEnd,
        )

        /**
         * Cross-fade — symmetric enter/exit.
         *
         * Ideal for tab switches, bottom-nav transitions, or
         * root-level swaps where direction doesn't matter.
         */
        val Fade = EchoTransitionPreset(
            enterTransition = EchoTransitions.fadeIn,
            exitTransition = EchoTransitions.fadeOut,
            popEnterTransition = EchoTransitions.fadePopEnter,
            popExitTransition = EchoTransitions.fadePopExit,
        )

        // ─────────────── Modals / sheets ───────────────

        /**
         * Bottom-sheet style: modal slides up, fades on dismiss.
         *
         * The background screen gently shrinks/dims while the modal is visible,
         * giving it a layered, physical feel.
         */
        val Modal = EchoTransitionPreset(
            enterTransition = EchoTransitions.modalEnter,
            exitTransition = EchoTransitions.modalPopExit,
            popEnterTransition = EchoTransitions.modalPopEnter,
            popExitTransition = EchoTransitions.modalExit,
        )

        /**
         * Full-screen modal: slides up from bottom, slides back down on dismiss.
         *
         * More dramatic than [Modal] — covers the entire screen.
         * Good for compose flows, image viewers, full-screen editors.
         */
        val Vertical = EchoTransitionPreset(
            enterTransition = EchoTransitions.slideFadeInUp,
            exitTransition = EchoTransitions.slideFadeOutDown,
            popEnterTransition = EchoTransitions.slideFadeInDown,
            popExitTransition = EchoTransitions.slideFadeOutDown,
        )

        // ─────────────── Material Motion ───────────────

        /**
         * Material Motion shared Z-axis: scale + fade.
         *
         * For hierarchical transitions — parent/child relationships
         * where the child conceptually sits "above" the parent.
         */
        val SharedAxisZ = EchoTransitionPreset(
            enterTransition = EchoTransitions.sharedAxisZEnter,
            exitTransition = EchoTransitions.sharedAxisZExit,
            popEnterTransition = EchoTransitions.sharedAxisZPopEnter,
            popExitTransition = EchoTransitions.sharedAxisZPopExit,
        )

        /**
         * Material Motion shared X-axis: horizontal slide (30%) + fade.
         *
         * For lateral navigation — onboarding steps, wizard flows,
         * or any sequential content on the same level.
         */
        val SharedAxisX = EchoTransitionPreset(
            enterTransition = EchoTransitions.sharedAxisXEnter,
            exitTransition = EchoTransitions.sharedAxisXExit,
            popEnterTransition = EchoTransitions.sharedAxisXPopEnter,
            popExitTransition = EchoTransitions.sharedAxisXPopExit,
        )

        /**
         * Material Motion shared Y-axis: vertical slide (30%) + fade.
         *
         * For vertical sequential content — feed item expansion,
         * list-to-detail in vertical lists, stepper UIs.
         */
        val SharedAxisY = EchoTransitionPreset(
            enterTransition = EchoTransitions.sharedAxisYEnter,
            exitTransition = EchoTransitions.sharedAxisYExit,
            popEnterTransition = EchoTransitions.sharedAxisYPopEnter,
            popExitTransition = EchoTransitions.sharedAxisYPopExit,
        )

        // ─────────────── Scale ───────────────

        /**
         * Scale + fade: zoom-in enter, zoom-out exit with reversed pop directions.
         *
         * Punchy and expressive — good for FAB actions, media previews,
         * or any screen that feels like it "pops" into view.
         */
        val Scale = EchoTransitionPreset(
            enterTransition = EchoTransitions.scaleIn,
            exitTransition = EchoTransitions.scaleOut,
            popEnterTransition = EchoTransitions.scalePopEnter,
            popExitTransition = EchoTransitions.scalePopExit,
        )

        /**
         * Container transform-like: scale from small + slide up + fade.
         *
         * Dramatic expansion for card-to-detail, thumbnail-to-full,
         * or any transition that feels like an element is "opening up."
         */
        val Expand = EchoTransitionPreset(
            enterTransition = EchoTransitions.expandIn,
            exitTransition = EchoTransitions.shrinkOut,
            popEnterTransition = EchoTransitions.expandPopEnter,
            popExitTransition = EchoTransitions.shrinkPopExit,
        )

        // ─────────────── Utility ───────────────

        /**
         * No transitions at all — instant swap.
         *
         * Useful for testing, or screens that handle their
         * own internal animations and don't want nav transitions.
         */
        val None = EchoTransitionPreset(
            enterTransition = EchoTransitions.none,
            exitTransition = EchoTransitions.noneExit,
            popEnterTransition = EchoTransitions.none,
            popExitTransition = EchoTransitions.noneExit,
        )
    }
}
