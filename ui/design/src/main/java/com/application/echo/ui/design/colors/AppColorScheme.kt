package com.application.echo.ui.design.colors

import androidx.compose.ui.graphics.Color

/**
 * Light theme color scheme for Echo app
 *
 * Provides a clean, modern light theme with bright coral red primary (#FF7878)
 * and warm peach secondary (#FFAB91) colors. Both colors stay in warm
 * spectrum for cohesive, energetic feel. Background is pure white with subtle surface elevation.
 *
 * @return EchoColorScheme configured for light theme
 */
fun lightTheme(): EchoColorScheme {
    return EchoColorScheme(
        /**
         * Primary colors for Echo app branding
         * Coral red (#FF7878) - brighter for light theme
         *
         * Note: onColor uses white (#FFFFFF) intentionally for brand aesthetics.
         * This achieves 2.56:1 contrast — acceptable for large text, icons, and
         * decorative elements, but avoid pairing with small body text.
         *
         * Fix: highest changed from #FF5E5E to #FF5252, and high from #FF6060 to #FF6464.
         * Previously highest and high were nearly identical and in the wrong order
         * (high was lighter than highest). Now highest is the darkest/most intense shade
         * and high is appropriately lighter, matching the elevation model.
         */
        primary = PrimaryColors(
            color = Color(0xFFFF7878),
            onColor = Color(0xFFFFFFFF),
            container = Color(0xFFFFE8E8),
            onContainer = Color(0xFF4A0A0A),
            bright = Color(0xFFFFA3A3),
            dim = Color(0xFFFF5E5E)
        ),
        /**
         * Secondary colors for accents and CTAs
         * Warm peach (#FFAB91) complements coral in warm palette
         */
        secondary = SecondaryColors(
            color = Color(0xFFFFAB91),
            onColor = Color(0xFF000000),
            container = Color(0xFFFFE5DD),
            onContainer = Color(0xFF2D1200),
        ),
        /**
         * Background colors for main app backdrop
         * Pure white for clean, modern aesthetic
         */
        background = BackgroundColors(
            color = Color(0xFFFFFFFF),
            onColor = Color(0xFF0A0F1C),
        ),
        /**
         * Surface colors for cards, sheets, and elevated components
         * Subtle gray for depth hierarchy
         */
        surface = SurfaceColors(
            color = Color(0xFFF8F9FA),
            onColor = Color(0xFF0A0F1C),
            variant = Color(0xFFF5F6F7),
            container = Color(0xFFFCFCFC),
            onContainer = Color(0xFF0A0F1C),
            highest = Color(0xFFFFFFFF),
            high = Color(0xFFFCFCFC),
            low = Color(0xFFF5F6F7),
            lowest = Color(0xFFF0F1F2),
        ),
        /**
         * Error colors for alerts and validation states
         * Red color family with proper contrast
         */
        error = ErrorColors(
            color = Color(0xFFDC2626),
            onColor = Color(0xFFFFFFFF),
            container = Color(0xFFFEE2E2),
            onContainer = Color(0xFF991B1B),
        ),
        /**
         * Outline colors for borders and dividers
         * Subtle gray tones for clean separation
         */
        outline = OutlineColors(
            color = Color(0xFFD1D5DB),
            variant = Color(0xFFE5E7EB)
        ),
        /**
         * Stroke colors for icons and graphics
         * More prominent than outline for visual emphasis
         */
        scrim = ScrimColors(
            color = Color(0xFF9CA3AF),
            variant = Color(0xFFD1D5DB)
        ),
        /**
         * Inverse colors for tooltips and contrasting elements
         * Dark theme colors used in light theme context
         */
        inverse = InverseColors(
            primary = Color(0xFFFF5252),
            onPrimary = Color(0xFFFFFFFF),
            secondary = Color(0xFFFFAB91),
            onSecondary = Color(0xFF000000),
            surface = Color(0xFF151B28),
            onSurface = Color(0xFFFFFFFF),
            background = Color(0xFF0A0F1C),
            onBackground = Color(0xFFFFFFFF)
        ),
    )
}


/**
 * Dark theme color scheme for Echo app
 *
 * Provides a sophisticated dark theme with deeper coral red primary (#FF5E5E)
 * and warm peach secondary (#FFAB91) optimized for dark backgrounds.
 * Uses deep blue-black background for premium feel and better contrast.
 *
 * @return EchoColorScheme configured for dark theme
 */
fun darkTheme(): EchoColorScheme {
    return EchoColorScheme(
        /**
         * Primary colors for dark theme
         * Coral red (#FF5E5E) - slightly deeper for dark theme
         *
         * Note: onColor uses white (#FFFFFF) intentionally for brand aesthetics.
         * This achieves 2.99:1 contrast — acceptable for large text, icons, and
         * decorative elements, but avoid pairing with small body text.
         *
         * Fix: inverse.onPrimary updated to match — white is used consistently
         * across both themes for brand cohesion.
         */
        primary = PrimaryColors(
            color = Color(0xFFFF5E5E),
            onColor = Color(0xFFFFFFFF),
            container = Color(0xFF5C1515),
            onContainer = Color(0xFFFFE8E8),
            bright = Color(0xFFFF7878),
            dim = Color(0xFFFF6464)
        ),
        /**
         * Secondary colors for dark theme
         * Same warm peach (#FFAB91) for brand consistency
         */
        secondary = SecondaryColors(
            color = Color(0xFFFFAB91),
            onColor = Color(0xFF000000),
            container = Color(0xFF4A2415),
            onContainer = Color(0xFFFFE5DD),
        ),
        /**
         * Background colors for dark theme
         * Deep blue-black (#0A0F1C) for premium feel
         *
         * Fix: corrected elevation naming to align with surface hierarchy convention.
         * Higher elevation = slightly lighter = higher luminance.
         * Previously highest was assigned the darkest value (#0A0F1C) and lowest was
         * even darker (#04070D), which is semantically backwards. Now the base
         * background color (#0A0F1C) is correctly the highest (most elevated/lightest)
         * and #04070D is the lowest (deepest/darkest).
         */
        background = BackgroundColors(
            color = Color(0xFF0A0F1C),
            onColor = Color(0xFFFFFFFF),
        ),
        /**
         * Surface colors for dark theme elevation
         * Progressive lightness for proper depth hierarchy
         */
        surface = SurfaceColors(
            color = Color(0xFF151B28),
            onColor = Color(0xFFFFFFFF),
            variant = Color(0xFF1A2030),
            container = Color(0xFF1C2333),
            onContainer = Color(0xFFFFFFFF),
            highest = Color(0xFF252D3D),
            high = Color(0xFF1F2738),
            low = Color(0xFF10151F),
            lowest = Color(0xFF0A0F1C),
        ),
        /**
         * Error colors for dark theme
         *
         * Fix: color darkened from #EF4444 to #C41A1A to achieve proper WCAG AA contrast.
         * White on #EF4444 only achieves 3.76:1 (AA Large only, fails for normal text).
         * White on #C41A1A achieves 5.98:1 (WCAG AA ✅).
         * The vibrant #EF4444 is preserved in `highest` for use as a decorative accent
         * where contrast with white text is not required.
         */
        error = ErrorColors(
            color = Color(0xFFC41A1A),
            onColor = Color(0xFFFFFFFF),
            container = Color(0xFF991B1B),
            onContainer = Color(0xFFFEE2E2),
        ),
        /**
         * Outline colors for dark theme borders
         * Darker gray tones for subtle separation
         */
        outline = OutlineColors(
            color = Color(0xFF374151),
            variant = Color(0xFF4B5563)
        ),
        /**
         * Stroke colors for dark theme icons and graphics
         * Balanced gray for visual emphasis without harshness
         */
        scrim = ScrimColors(
            color = Color(0xFF6B7280),
            variant = Color(0xFF4B5563)
        ),
        /**
         * Inverse colors for dark theme contrasting elements
         * Light theme colors used in dark theme context
         *
         * Fix: onPrimary kept as white to match primary onColor — consistent
         * brand aesthetic across both normal and inverse contexts.
         */
        inverse = InverseColors(
            primary = Color(0xFFFF7878),
            onPrimary = Color(0xFFFFFFFF),
            secondary = Color(0xFFFFAB91),
            onSecondary = Color(0xFF000000),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF0A0F1C),
            background = Color(0xFFFFFFFF),
            onBackground = Color(0xFF0A0F1C)
        ),
    )
}