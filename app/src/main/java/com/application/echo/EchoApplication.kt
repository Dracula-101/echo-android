package com.application.echo

import android.os.Handler
import android.os.Looper
import com.application.echo.features.notification.channel.NotificationChannels
import com.application.echo.features.websocket.EchoWebSocketManager
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class EchoApplication : android.app.Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        NotificationChannels.createAll(this)
    }
}
