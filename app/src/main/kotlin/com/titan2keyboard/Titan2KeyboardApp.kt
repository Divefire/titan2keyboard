package com.titan2keyboard

import android.app.Application
import com.titan2keyboard.domain.repository.ShortcutRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Application class for Titan2 Keyboard
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection
 */
@HiltAndroidApp
class Titan2KeyboardApp : Application() {

    @Inject
    lateinit var shortcutRepository: ShortcutRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        // Initialize default shortcuts if none exist
        applicationScope.launch {
            shortcutRepository.initializeDefaults()
        }
    }
}
