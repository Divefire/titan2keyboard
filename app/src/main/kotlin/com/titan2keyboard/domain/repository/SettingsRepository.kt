package com.titan2keyboard.domain.repository

import com.titan2keyboard.domain.model.KeyboardSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for keyboard settings
 */
interface SettingsRepository {
    /**
     * Flow of keyboard settings that emits whenever settings change
     */
    val settingsFlow: Flow<KeyboardSettings>

    /**
     * Update a specific setting
     * @param key The setting key
     * @param value The new value
     */
    suspend fun updateSetting(key: String, value: Any?)

    /**
     * Reset all settings to defaults
     */
    suspend fun resetToDefaults()
}
