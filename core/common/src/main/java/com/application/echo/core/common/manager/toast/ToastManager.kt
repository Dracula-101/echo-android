package com.application.echo.core.common.manager.toast

import android.widget.Toast
import androidx.annotation.StringRes

/**
 * Interface for managing toast messages in the application.
 */
interface ToastManager {

    /**
     * Displays a toast message with the specified text and duration.
     *
     * @param message The text to display in the toast.
     * @param duration The duration for which the toast should be visible. Default is [Toast.LENGTH_SHORT].
     */
    fun show(message: CharSequence, duration: Int = Toast.LENGTH_SHORT)

    /**
     * Displays a toast message with the specified string resource ID and duration.
     *
     * @param resId The resource ID of the string to display in the toast.
     * @param duration The duration for which the toast should be visible. Default is [Toast.LENGTH_SHORT].
     */
    fun show(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT)
}