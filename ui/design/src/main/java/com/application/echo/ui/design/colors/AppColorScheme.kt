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
         */
        primary = PrimaryColors(
            color = Color(0xFFFF7878),
            onColor = Color(0xFFFFFFFF),
            container = Color(0xFFFFE8E8),
            onContainer = Color(0xFF4A0A0A),
            bright = Color(0xFFFFA0A0),
            dim = Color(0xFFFF6060)
        ),
        /**
         * Secondary colors for accents and CTAs
         * Warm peach (#FFAB91) complements coral in warm palette
         */
        secondary = SecondaryColors(
            color = Color(0xFFFFAB91),
            onColor = Color(0xFF000000),
            container = Color(0xFFFFE5DD),
            onContainer = Color(0xFF2D1200)
        ),
        /**
         * Background colors for main app backdrop
         * Pure white for clean, modern aesthetic
         */
        background = BackgroundColors(
            color = Color(0xFFFFFFFF),
            onColor = Color(0xFF0A0F1C),
            bright = Color(0xFFFFFFFF),
            dim = Color(0xFFFAFAFA)
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
            lowest = Color(0xFFF0F1F2)
        ),
        /**
         * Error colors for alerts and validation states
         * Red color family with proper contrast
         */
        error = ErrorColors(
            color = Color(0xFFDC2626),
            onColor = Color(0xFFFFFFFF),
            container = Color(0xFFFEE2E2),
            onContainer = Color(0xFF991B1B)
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
            primary = Color(0xFFFF5E5E),
            onPrimary = Color(0xFFFFFFFF),
            secondary = Color(0xFFFFAB91),
            onSecondary = Color(0xFF000000),
            surface = Color(0xFF151B28),
            onSurface = Color(0xFFFFFFFF),
            background = Color(0xFF0A0F1C),
            onBackground = Color(0xFFFFFFFF)
        ),
        shades = lightThemeShades()
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
         */
        primary = PrimaryColors(
            color = Color(0xFFFF5E5E),
            onColor = Color(0xFFFFFFFF),
            container = Color(0xFF5C1515),
            onContainer = Color(0xFFFFE8E8),
            bright = Color(0xFFFF8585),
            dim = Color(0xFFE64848)
        ),
        /**
         * Secondary colors for dark theme
         * Same warm peach (#FFAB91) for brand consistency
         */
        secondary = SecondaryColors(
            color = Color(0xFFFFAB91),
            onColor = Color(0xFF000000),
            container = Color(0xFF4A2415),
            onContainer = Color(0xFFFFE5DD)
        ),
        /**
         * Background colors for dark theme
         * Deep blue-black (#0A0F1C) for premium feel
         */
        background = BackgroundColors(
            color = Color(0xFF0A0F1C),
            onColor = Color(0xFFFFFFFF),
            bright = Color(0xFF1A1F2E),
            dim = Color(0xFF050A14)
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
            lowest = Color(0xFF0A0F1C)
        ),
        /**
         * Error colors for dark theme
         * Brighter red for visibility on dark backgrounds
         */
        error = ErrorColors(
            color = Color(0xFFEF4444),
            onColor = Color(0xFFFFFFFF),
            container = Color(0xFF991B1B),
            onContainer = Color(0xFFFEE2E2)
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
        shades = darkThemeShades()
    )
}

/**
 * Light theme color shades data for Echo app
 *
 * Provides complete shade ranges (50-900) for primary, secondary, and background colors.
 * Each shade offers different intensities for flexible UI design and proper hierarchy.
 *
 * @return ColorShadesData configured for light theme
 */
fun lightThemeShades(): ColorShadesData {
    return ColorShadesData(
        /**
         * Primary color shades - Brighter coral red family (#FF7878)
         * From lightest tint (50) to darkest shade (900)
         */
        primary = Shades(
            shade50 = Color(0xFFFFF0F0),
            shade100 = Color(0xFFFFE8E8),
            shade200 = Color(0xFFFFD0D0),
            shade300 = Color(0xFFFFB8B8),
            shade400 = Color(0xFFFFA0A0),
            shade500 = Color(0xFFFF7878), // Brighter primary
            shade600 = Color(0xFFFF6060),
            shade700 = Color(0xFFE64848),
            shade800 = Color(0xFFCC3030),
            shade900 = Color(0xFFB31818)
        ),

        /**
         * Secondary color shades - Warm peach/salmon family (#FFAB91)
         * Softer warm complement to coral red
         */
        secondary = Shades(
            shade50 = Color(0xFFFFF5F2),
            shade100 = Color(0xFFFFE5DD),
            shade200 = Color(0xFFFFD4C4),
            shade300 = Color(0xFFFFC2AB),
            shade400 = Color(0xFFFFB8A0),
            shade500 = Color(0xFFFFAB91), // Original secondary
            shade600 = Color(0xFFFF9770),
            shade700 = Color(0xFFFF8359),
            shade800 = Color(0xFFE66F42),
            shade900 = Color(0xFFCC5B2B)
        ),

        /**
         * Background color shades - White to light gray family
         * Subtle variations for layering and depth
         */
        background = Shades(
            shade50 = Color(0xFFFFFFFF),
            shade100 = Color(0xFFFCFCFC),
            shade200 = Color(0xFFFAFAFA),
            shade300 = Color(0xFFF8F9FA), // Surface color
            shade400 = Color(0xFFF5F6F7),
            shade500 = Color(0xFFF0F1F2),
            shade600 = Color(0xFFE8E9EB),
            shade700 = Color(0xFFE0E1E3),
            shade800 = Color(0xFFD5D6D8),
            shade900 = Color(0xFFC0C1C3)
        ),
        /**
         * On background color shades - Dark gray family
         * Provides contrast for text and icons on light backgrounds
         */
        onBackground = Shades(
            shade50 = Color(0xFF404853),
            shade100 = Color(0xFF353D48),
            shade200 = Color(0xFF2A323D),
            shade300 = Color(0xFF1F2732),
            shade400 = Color(0xFF151C27),
            shade500 = Color(0xFF0A0F1C), // Original dark text
            shade600 = Color(0xFF080D17),
            shade700 = Color(0xFF060A12),
            shade800 = Color(0xFF04070D),
            shade900 = Color(0xFF020408)
        )
    )
}

/**
 * Dark theme color shades data for Echo app
 *
 * Provides complete shade ranges optimized for dark backgrounds.
 * Colors are adjusted for better visibility and contrast in dark mode.
 *
 * @return ColorShadesData configured for dark theme
 */
fun darkThemeShades(): ColorShadesData {
    return ColorShadesData(
        /**
         * Primary color shades for dark theme - Deeper coral red family
         * Slightly different from light theme for optimal dark visibility
         */
        primary = Shades(
            shade50 = Color(0xFFFFF0F0),
            shade100 = Color(0xFFFFE8E8),
            shade200 = Color(0xFFFFD0D0),
            shade300 = Color(0xFFFFB8B8),
            shade400 = Color(0xFFFF8585),
            shade500 = Color(0xFFFF5E5E), // Deeper for dark theme
            shade600 = Color(0xFFE64848),
            shade700 = Color(0xFFCC3030),
            shade800 = Color(0xFFB31818),
            shade900 = Color(0xFF990000)
        ),

        /**
         * Secondary color shades for dark theme - Warm peach/salmon family
         * Same as light theme for brand consistency
         */
        secondary = Shades(
            shade50 = Color(0xFFFFF5F2),
            shade100 = Color(0xFFFFE5DD),
            shade200 = Color(0xFFFFD4C4),
            shade300 = Color(0xFFFFC2AB),
            shade400 = Color(0xFFFFB8A0),
            shade500 = Color(0xFFFFAB91), // Same in dark theme
            shade600 = Color(0xFFFF9770),
            shade700 = Color(0xFFFF8359),
            shade800 = Color(0xFFE66F42),
            shade900 = Color(0xFFCC5B2B)
        ),

        /**
         * Background color shades for dark theme - Deep blue-black to gray
         * Progressive lightness for proper elevation hierarchy
         */
        background = Shades(
            shade50 = Color(0xFF404853),
            shade100 = Color(0xFF353D48),
            shade200 = Color(0xFF2A323D),
            shade300 = Color(0xFF1F2732),
            shade400 = Color(0xFF151C27),
            shade500 = Color(0xFF0A0F1C), // Original dark background
            shade600 = Color(0xFF080D17),
            shade700 = Color(0xFF060A12),
            shade800 = Color(0xFF04070D),
            shade900 = Color(0xFF020408)
        ),
        /**
         * On background color shades for dark theme - Lighter grays
         * Provides contrast for text and icons on dark backgrounds
         */
        onBackground = Shades(
            shade50 = Color(0xFFFFFFFF),
            shade100 = Color(0xFFFCFCFC),
            shade200 = Color(0xFFFAFAFA),
            shade300 = Color(0xFFF8F9FA),
            shade400 = Color(0xFFF5F6F7),
            shade500 = Color(0xFFF0F1F2),
            shade600 = Color(0xFFE8E9EB),
            shade700 = Color(0xFFE0E1E3),
            shade800 = Color(0xFFD5D6D8),
            shade900 = Color(0xFFC0C1C3)
        )
    )
}