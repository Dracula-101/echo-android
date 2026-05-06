package com.application.echo.ui.components.toast

import com.application.echo.ui.components.util.IconResource

/**
 * Leading visual rendered to the left of an [EchoToast]'s text.
 */
sealed interface ToastLeading {
    /** No leading visual — text only. */
    data object None : ToastLeading

    /** Indeterminate spinner — pair with `"Sending…"` style messages. */
    data object Loading : ToastLeading

    /** Mint-tinted check — confirmation. */
    data object Success : ToastLeading

    /** Error-tinted icon — failure / problem. */
    data object Error : ToastLeading

    /** Caller-supplied icon, tinted with [accentVariant] of the toast. */
    data class Custom(val icon: IconResource) : ToastLeading
}
