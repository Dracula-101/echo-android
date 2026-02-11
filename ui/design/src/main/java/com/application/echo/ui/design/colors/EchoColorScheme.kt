package com.application.echo.ui.design.colors


/**
 * Represents a complete color scheme for an application theme.
 *
 * This data class encapsulates all the primary color categories used throughout
 * the application, providing a centralized way to manage and access theme colors.
 * Each color category contains multiple color variants and shades for different
 * UI states and contexts.
 *
 * @property primary Primary brand colors used for key UI elements like buttons,
 *                   floating action buttons, and active states
 * @property secondary Secondary colors used for less prominent UI elements,
 *                     providing visual hierarchy and accent colors
 * @property background Background colors for different surfaces and containers,
 *                      including main background and elevated surfaces
 * @property surface Surface colors for cards, sheets, and other elevated components
 *                   that sit above the background
 * @property error Error state colors used for validation messages, alerts,
 *                 and destructive actions
 * @property outline Outline colors used for borders, dividers, and component
 *                   boundaries to define visual separation
 * @property scrim Scrim colors used for overlays, modals, and dim effects
 *                 that appear over other content
 * @property inverse Inverse colors used when content needs to stand out against
 *                   the primary color scheme, typically for high contrast scenarios
 */
data class EchoColorScheme(
    val primary: PrimaryColors,
    val secondary: SecondaryColors,
    val background: BackgroundColors,
    val surface: SurfaceColors,
    val error: ErrorColors,
    val outline: OutlineColors,
    val scrim: ScrimColors,
    val inverse: InverseColors,
    val shades: ColorShadesData,
)

fun EchoColorScheme.toMaterialColorScheme(): androidx.compose.material3.ColorScheme {
    return androidx.compose.material3.ColorScheme(
        primary = this.primary.color,
        onPrimary = this.primary.onColor,
        primaryContainer = this.primary.container,
        onPrimaryContainer = this.primary.onContainer,
        inversePrimary = this.inverse.primary,

        secondary = this.secondary.color,
        onSecondary = this.secondary.onColor,
        secondaryContainer = this.secondary.container,
        onSecondaryContainer = this.secondary.onContainer,

        // For tertiary, you might need to add these to your EchoColorScheme
        // or use fallback colors. Here I'm using primary colors as fallback
        tertiary = this.primary.color, // Fallback - add tertiary to EchoColorScheme
        onTertiary = this.primary.onColor,
        tertiaryContainer = this.primary.container,
        onTertiaryContainer = this.primary.onContainer,

        background = this.background.color,
        onBackground = this.background.onColor,

        surface = this.surface.color,
        onSurface = this.surface.onColor,
        surfaceVariant = this.surface.variant,
        onSurfaceVariant = this.surface.onColor, // You might want to add onSurfaceVariant to EchoColorScheme
        surfaceTint = this.primary.color, // Typically uses primary color

        inverseSurface = this.inverse.surface,
        inverseOnSurface = this.inverse.onSurface,

        error = this.error.color,
        onError = this.error.onColor,
        errorContainer = this.error.container,
        onErrorContainer = this.error.onContainer,

        outline = this.outline.color,
        outlineVariant = this.outline.variant,

        scrim = this.scrim.color,

        // Surface container variants
        surfaceBright = this.surface.color, // Fallback - you might want to add these to EchoColorScheme
        surfaceDim = this.surface.color,
        surfaceContainer = this.surface.container,
        surfaceContainerHigh = this.surface.high,
        surfaceContainerHighest = this.surface.highest,
        surfaceContainerLow = this.surface.low,
        surfaceContainerLowest = this.surface.lowest,

        // Fixed colors - these might need to be added to your EchoColorScheme
        primaryFixed = this.primary.color, // Fallback
        primaryFixedDim = this.primary.dim,
        onPrimaryFixed = this.primary.onColor,
        onPrimaryFixedVariant = this.primary.onColor, // Fallback

        secondaryFixed = this.secondary.color, // Fallback
        secondaryFixedDim = this.secondary.color, // You might want to add dim to SecondaryColors
        onSecondaryFixed = this.secondary.onColor,
        onSecondaryFixedVariant = this.secondary.onColor, // Fallback

        tertiaryFixed = this.primary.color, // Fallback
        tertiaryFixedDim = this.primary.dim,
        onTertiaryFixed = this.primary.onColor,
        onTertiaryFixedVariant = this.primary.onColor // Fallback
    )
}