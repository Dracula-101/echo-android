package com.application.echo.ui.components.chip

/**
 * Visual treatment for [EchoChip].
 *
 * The same content can render in any of these styles depending on context:
 * filter chips toggle between [Soft] and [Active], status tags use [Soft], a
 * "currently selected" filter pill uses [Solid], and [Outline] / [Dashed] are
 * reserved for emphasis or "add new" affordances.
 */
enum class ChipStyle {
    /** Tinted container with the variant color as text. Default resting state. */
    Soft,

    /** Stronger tinted fill, used for the "selected" state of filter chips. */
    Active,

    /** Solid variant color background with on-variant text. Use sparingly. */
    Solid,

    /** Transparent with a 1.5dp variant-colored border. */
    Outline,

    /** Transparent with a dashed border — typically the "Add filter" affordance. */
    Dashed,
}
