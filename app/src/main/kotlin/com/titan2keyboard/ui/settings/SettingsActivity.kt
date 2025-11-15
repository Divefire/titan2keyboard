package com.titan2keyboard.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.titan2keyboard.ui.theme.Titan2KeyboardTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main settings activity for the keyboard
 */
@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Titan2KeyboardTheme {
                SettingsScreen()
            }
        }
    }
}
