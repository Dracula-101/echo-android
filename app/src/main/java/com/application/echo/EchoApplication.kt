package com.application.echo

import com.application.echo.features.notification.channel.NotificationChannels
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class EchoApplication : android.app.Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        NotificationChannels.createAll(this)
        val app = FirebaseApp.initializeApp(this)
        if (app != null) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("FCM Token: %s", task.result)
                } else {
                    Timber.e(task.exception, "Failed to get FCM token")
                }
            }
        } else {
            Timber.w("Firebase not initialized — skipping FCM token fetch")
        }
    }
}
