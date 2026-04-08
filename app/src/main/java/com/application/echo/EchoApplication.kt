package com.application.echo

import com.application.echo.features.notification.channel.NotificationChannels
import com.application.echo.features.websocket.EchoWebSocketManager
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class EchoApplication : android.app.Application() {

    @Inject
    lateinit var webSocketManager: EchoWebSocketManager

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        NotificationChannels.createAll(this)
    }
}
