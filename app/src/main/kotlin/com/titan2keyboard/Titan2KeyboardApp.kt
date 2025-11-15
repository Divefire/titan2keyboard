package com.titan2keyboard

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Titan2 Keyboard
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection
 */
@HiltAndroidApp
class Titan2KeyboardApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Application initialization
    }
}
