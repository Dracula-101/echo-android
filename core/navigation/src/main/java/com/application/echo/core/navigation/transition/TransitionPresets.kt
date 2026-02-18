package com.application.echo.core.navigation.transition

/**
 * Bundled set of enter / exit / popEnter / popExit transitions
 * that can be applied as a single unit to `echoComposable` or `EchoNavHost`.
 *
 * ## Two-tier design
 *
 * Each preset exposes both **root-level** (non-null) and **composable-level**
 * (nullable, graph-aware) providers via its fields. The preset uses root-level
 * providers for [EchoNavHost] defaults and composable-level for [echoComposable]
 * overrides.
 *
 * ## Usage
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
 * **Per-screen override:**
 * ```kotlin
 * echoComposable<SettingsRoute>(
 *     transition = EchoTransitionPreset.SlideHorizontal,
 * ) { SettingsScreen() }
 * ```
 *
 * **Custom preset via `copy`:**
 * ```kotlin
 * val custom = EchoTransitionPreset.Modal.copy(
 *     popEnterTransition = EchoTransitions.Enter.stay,
 * )
 * ```
 */
data class EchoTransitionPreset(
    val enterTransition: RootEnterTransitionProvider,
    val exitTransition: RootExitTransitionProvider,
    val popEnterTransition: RootEnterTransitionProvider,
    val popExitTransition: RootExitTransitionProvider,
) {
    companion object {

        // ─────────────── Standard navigations ───────────────

        /**
         * Standard forward/back horizontal slide with parallax + staggered fade.
         * RTL-safe via `slideIntoContainer`/`slideOutOfContainer`.
         *
         * Best for peer-level screen pushes (detail, settings, profile, etc.).
         * This is the default for [EchoNavHost].
         */
        val SlideHorizontal = EchoTransitionPreset(
            enterTransition = EchoTransitions.Enter.slideFromEnd,
            exitTransition = EchoTransitions.Exit.slideToStart,
            popEnterTransition = EchoTransitions.Enter.slideFromStart,
            popExitTransition = EchoTransitions.Exit.slideToEnd,
        )

        /**
         * Cross-fade — symmetric enter/exit.
         *
         * Ideal for tab switches, bottom-nav transitions, or
         * root-level swaps where direction doesn't matter.
         */
        val Fade = EchoTransitionPreset(
            enterTransition = EchoTransitions.Enter.fadeIn,
            exitTransition = EchoTransitions.Exit.fadeOut,
            popEnterTransition = EchoTransitions.Enter.fadeIn,
            popExitTransition = EchoTransitions.Exit.fadeOut,
        )

        // ─────────────── Modals / sheets ───────────────

        /**
         * Bottom-sheet style: modal slides up, fades on dismiss.
         *
         * The background screen gently shrinks/dims while the modal is visible,
         * giving it a layered, physical feel.
         */
        val Modal = EchoTransitionPreset(
            enterTransition = EchoTransitions.Enter.slideFromBottom,
            exitTransition = EchoTransitions.Exit.modalDim,
            popEnterTransition = EchoTransitions.Enter.modalRestore,
            popExitTransition = EchoTransitions.Exit.slideToBottom,
        )

        /**
         * Full-screen modal: slides up from bottom, slides back down on dismiss.
         *
         * More dramatic than [Modal] — covers the entire screen.
         * Good for compose flows, image viewers, full-screen editors.
         */
        val Vertical = EchoTransitionPreset(
            enterTransition = EchoTransitions.Enter.slideFromBottom,
            exitTransition = EchoTransitions.Exit.slideToBottom,
            popEnterTransition = EchoTransitions.Enter.slideFromTop,
            popExitTransition = EchoTransitions.Exit.slideToBottom,
        )

        // ─────────────── Material Motion ───────────────

        /**
         * Material Motion shared Z-axis: scale + fade.
         *
         * For hierarchical transitions — parent/child relationships
         * where the child conceptually sits "above" the parent.
         */
        val SharedAxisZ = EchoTransitionPreset(
            enterTransition = EchoTransitions.Enter.scaleUp,
            exitTransition = EchoTransitions.Exit.scaleDown,
            popEnterTransition = EchoTransitions.Enter.scaleUp,
            popExitTransition = EchoTransitions.Exit.scaleDown,
        )

        /**
         * Material Motion shared X-axis: horizontal slide (30%) + fade.
         *
         * For lateral navigation — onboarding steps, wizard flows,
         * or any sequential content on the same level.
         */
        val SharedAxisX = EchoTransitionPreset(
            enterTransition = EchoTransitions.Enter.sharedAxisX,
            exitTransition = EchoTransitions.Exit.sharedAxisX,
            popEnterTransition = EchoTransitions.Enter.sharedAxisXPop,
            popExitTransition = EchoTransitions.Exit.sharedAxisXPop,
        )

        /**
         * Material Motion shared Y-axis: vertical slide (30%) + fade.
         *
         * For vertical sequential content — feed item expansion,
         * list-to-detail in vertical lists, stepper UIs.
         */
        val SharedAxisY = EchoTransitionPreset(
            enterTransition = EchoTransitions.Enter.sharedAxisY,
            exitTransition = EchoTransitions.Exit.sharedAxisY,
            popEnterTransition = EchoTransitions.Enter.sharedAxisYPop,
            popExitTransition = EchoTransitions.Exit.sharedAxisYPop,
        )

        // ─────────────── Scale ───────────────

        /**
         * Scale + fade: zoom-in enter, zoom-out exit with reversed pop directions.
         *
         * Punchy and expressive — good for FAB actions, media previews,
         * or any screen that feels like it "pops" into view.
         */
        val Scale = EchoTransitionPreset(
            enterTransition = EchoTransitions.Enter.popScale,
            exitTransition = EchoTransitions.Exit.scaleUp,
            popEnterTransition = EchoTransitions.Enter.popScale,
            popExitTransition = EchoTransitions.Exit.popScale,
        )

        /**
         * Container transform-like: scale from small + slide up + fade.
         *
         * Dramatic expansion for card-to-detail, thumbnail-to-full,
         * or any transition that feels like an element is "opening up."
         */
        val Expand = EchoTransitionPreset(
            enterTransition = EchoTransitions.Enter.expand,
            exitTransition = EchoTransitions.Exit.shrink,
            popEnterTransition = EchoTransitions.Enter.expandRestore,
            popExitTransition = EchoTransitions.Exit.shrink,
        )

        // ─────────────── Push ───────────────

        /**
         * Push-style: new screen slides in while old screen stays in place.
         *
         * Feels like a card being pushed on top of a stack —
         * the background doesn't move, only the foreground animates.
         */
        val Push = EchoTransitionPreset(
            enterTransition = EchoTransitions.Enter.slideFromEnd,
            exitTransition = EchoTransitions.Exit.stay,
            popEnterTransition = EchoTransitions.Enter.stay,
            popExitTransition = EchoTransitions.Exit.slideToEnd,
        )

        // ─────────────── Utility ───────────────

        /**
         * No transitions at all — instant swap.
         *
         * Useful for testing, or screens that handle their
         * own internal animations and don't want nav transitions.
         */
        val None = EchoTransitionPreset(
            enterTransition = EchoTransitions.Enter.none,
            exitTransition = EchoTransitions.Exit.none,
            popEnterTransition = EchoTransitions.Enter.none,
            popExitTransition = EchoTransitions.Exit.none,
        )
    }
}
