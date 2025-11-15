package com.titan2keyboard.ui.settings

import com.titan2keyboard.domain.model.KeyboardSettings

/**
 * UI state for settings screen
 */
sealed class SettingsUiState {
    /**
     * Loading state
     */
    data object Loading : SettingsUiState()

    /**
     * Success state with settings data
     */
    data class Success(val settings: KeyboardSettings) : SettingsUiState()

    /**
     * Error state
     */
    data class Error(val message: String) : SettingsUiState()
}
