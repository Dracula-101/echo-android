package com.application.echo.core.common.manager.toast

import android.content.Context
import android.widget.Toast

class ToastManagerImpl(private val context: Context) : ToastManager {

    override fun show(message: CharSequence, duration: Int) {
        Toast.makeText(context, message, duration).show()
    }

    override fun show(messageResId: Int, duration: Int) {
        Toast.makeText(context, context.getString(messageResId), duration).show()
    }
}