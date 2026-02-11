package com.application.echo;
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EchoApplication : android.app.Application() {

    override fun onCreate() {
        super.onCreate()
    }

}