package com.application.echo.features.notification.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Checks notification permission state.
 *
 * On Android 12 and below, notifications are allowed by default.
 * On Android 13+, the app must request [Manifest.permission.POST_NOTIFICATIONS].
 */
@Singleton
class NotificationPermissionHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    val isGranted: Boolean
        get() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        }

    val requiresRuntimePermission: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    companion object {
        const val PERMISSION = Manifest.permission.POST_NOTIFICATIONS
    }
}
