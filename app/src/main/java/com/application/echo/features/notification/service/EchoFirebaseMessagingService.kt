package com.application.echo.features.notification.service

import com.application.echo.features.notification.builder.NotificationDispatcher
import com.application.echo.features.notification.model.NotificationPayload
import com.application.echo.features.notification.token.FcmTokenManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Entry point for all FCM messages.
 *
 * Delegates to:
 * - [FcmTokenManager] for token lifecycle
 * - [NotificationDispatcher] for building and posting notifications
 *
 * Uses `@AndroidEntryPoint` so Hilt can inject dependencies.
 */
@AndroidEntryPoint
class EchoFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var tokenManager: FcmTokenManager
    @Inject lateinit var dispatcher: NotificationDispatcher

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        tokenManager.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Timber.d("FCM message from: %s, data keys: %s", message.from, message.data.keys)

        val payload = NotificationPayload.from(message)
        if (payload == null) {
            Timber.w("Received FCM message with no usable payload")
            return
        }

        dispatcher.dispatch(payload)
    }
}
