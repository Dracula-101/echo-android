package com.application.echo.ui.components.snackbar

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf

private const val MAX_SNACKBARS = 4

@Stable
internal data class EchoSnackbarEntry(
    val data: EchoSnackbarData,
    val isExiting: Boolean = false,
)

/**
 * Observable state for [EchoSnackbarHost]. Obtain via [rememberEchoSnackbarState].
 *
 * - [show] inserts at index 0 — new card becomes front, old cards spring to back.
 * - Max 4 cards on screen; additional calls are silently ignored.
 */
@Stable
class EchoSnackbarState {

    internal val entries = mutableStateListOf<EchoSnackbarEntry>()

    fun show(data: EchoSnackbarData) {
        if (entries.count { !it.isExiting } >= MAX_SNACKBARS) return
        entries.add(0, EchoSnackbarEntry(data)) // prepend → new card is depth 0 (front)
    }

    fun show(
        message: String,
        type: EchoSnackbarType = EchoSnackbarType.INFO,
        detail: String? = null,
        code: String? = null,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        duration: EchoSnackbarDuration = EchoSnackbarDuration.Short,
    ) = show(EchoSnackbarData(message, detail, code, type, actionLabel, onAction, duration))

    fun dismissFront() = entries.firstOrNull { !it.isExiting }?.let { startExit(it.data.id) }

    fun dismiss(id: String) = startExit(id)

    fun clearAll() = entries.toList().forEach { startExit(it.data.id) }

    internal fun startExit(id: String) {
        val idx = entries.indexOfFirst { it.data.id == id }
        if (idx >= 0) entries[idx] = entries[idx].copy(isExiting = true)
    }

    internal fun removeEntry(id: String) {
        entries.removeIf { it.data.id == id }
    }
}
