package com.titan2keyboard.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.titan2keyboard.ui.shortcuts.ShortcutManagementScreen
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
                SettingsNavigation()
            }
        }
    }
}

@Composable
private fun SettingsNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "settings"
    ) {
        composable("settings") {
            SettingsScreen(
                onNavigateToShortcuts = {
                    navController.navigate("shortcuts")
                },
                onNavigateToAbout = {
                    navController.navigate("about")
                }
            )
        }
        composable("shortcuts") {
            ShortcutManagementScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("about") {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
