package com.titan2keyboard.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.titan2keyboard.data.datastore.PreferencesKeys
import com.titan2keyboard.domain.model.KeyboardSettings
import com.titan2keyboard.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of SettingsRepository using DataStore
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override val settingsFlow: Flow<KeyboardSettings> = dataStore.data
        .map { preferences ->
            KeyboardSettings(
                autoCapitalize = preferences[PreferencesKeys.AUTO_CAPITALIZE] ?: true,
                keyRepeatEnabled = preferences[PreferencesKeys.KEY_REPEAT_ENABLED] ?: true,
                longPressCapitalize = preferences[PreferencesKeys.LONG_PRESS_CAPITALIZE] ?: false,
                doubleSpacePeriod = preferences[PreferencesKeys.DOUBLE_SPACE_PERIOD] ?: true,
                textShortcutsEnabled = preferences[PreferencesKeys.TEXT_SHORTCUTS_ENABLED] ?: true,
                stickyShift = preferences[PreferencesKeys.STICKY_SHIFT] ?: false,
                stickyAlt = preferences[PreferencesKeys.STICKY_ALT] ?: false,
                altBackspaceDeleteLine = preferences[PreferencesKeys.ALT_BACKSPACE_DELETE_LINE] ?: true,
                keyRepeatDelay = preferences[PreferencesKeys.KEY_REPEAT_DELAY] ?: 400L,
                keyRepeatRate = preferences[PreferencesKeys.KEY_REPEAT_RATE] ?: 50L,
                preferredCurrency = preferences[PreferencesKeys.PREFERRED_CURRENCY]
            )
        }

    override suspend fun updateSetting(key: String, value: Any) {
        dataStore.edit { preferences ->
            when (key) {
                "autoCapitalize" -> preferences[PreferencesKeys.AUTO_CAPITALIZE] = value as Boolean
                "keyRepeatEnabled" -> preferences[PreferencesKeys.KEY_REPEAT_ENABLED] = value as Boolean
                "longPressCapitalize" -> preferences[PreferencesKeys.LONG_PRESS_CAPITALIZE] = value as Boolean
                "doubleSpacePeriod" -> preferences[PreferencesKeys.DOUBLE_SPACE_PERIOD] = value as Boolean
                "textShortcutsEnabled" -> preferences[PreferencesKeys.TEXT_SHORTCUTS_ENABLED] = value as Boolean
                "stickyShift" -> preferences[PreferencesKeys.STICKY_SHIFT] = value as Boolean
                "stickyAlt" -> preferences[PreferencesKeys.STICKY_ALT] = value as Boolean
                "altBackspaceDeleteLine" -> preferences[PreferencesKeys.ALT_BACKSPACE_DELETE_LINE] = value as Boolean
                "keyRepeatDelay" -> preferences[PreferencesKeys.KEY_REPEAT_DELAY] = value as Long
                "keyRepeatRate" -> preferences[PreferencesKeys.KEY_REPEAT_RATE] = value as Long
                "preferredCurrency" -> {
                    if (value == null) {
                        preferences.remove(PreferencesKeys.PREFERRED_CURRENCY)
                    } else {
                        preferences[PreferencesKeys.PREFERRED_CURRENCY] = value as String
                    }
                }
            }
        }
    }

    override suspend fun resetToDefaults() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
