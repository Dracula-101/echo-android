package com.application.echo.ui.components.snackbar

import androidx.compose.runtime.Immutable
import java.util.UUID

enum class EchoSnackbarType { INFO, SUCCESS, WARNING, ERROR }

sealed class EchoSnackbarDuration(val millis: kotlin.Long) {
    data object Short      : EchoSnackbarDuration(2_500L)
    data object Long       : EchoSnackbarDuration(4_000L)
    data object Indefinite : EchoSnackbarDuration(kotlin.Long.MAX_VALUE)
    class Custom(millis: kotlin.Long) : EchoSnackbarDuration(millis)
}

/**
 * @param message     Bold title line.
 * @param detail      Optional subtitle — shown on the same line as [code], wraps below on overflow.
 * @param code        Optional short pill badge, e.g. "200 OK" or "ERR_404".
 * @param actionLabel Label for the inline action button.
 */
@Immutable
data class EchoSnackbarData(
    val message: String,
    val detail: String? = null,
    val code: String? = null,
    val type: EchoSnackbarType = EchoSnackbarType.INFO,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
    val duration: EchoSnackbarDuration = EchoSnackbarDuration.Short,
    internal val id: String = UUID.randomUUID().toString(),
)
