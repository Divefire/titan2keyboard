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
                keyRepeatDelay = preferences[PreferencesKeys.KEY_REPEAT_DELAY] ?: 400L,
                keyRepeatRate = preferences[PreferencesKeys.KEY_REPEAT_RATE] ?: 50L
            )
        }

    override suspend fun updateSetting(key: String, value: Any) {
        dataStore.edit { preferences ->
            when (key) {
                "autoCapitalize" -> preferences[PreferencesKeys.AUTO_CAPITALIZE] = value as Boolean
                "keyRepeatEnabled" -> preferences[PreferencesKeys.KEY_REPEAT_ENABLED] = value as Boolean
                "keyRepeatDelay" -> preferences[PreferencesKeys.KEY_REPEAT_DELAY] = value as Long
                "keyRepeatRate" -> preferences[PreferencesKeys.KEY_REPEAT_RATE] = value as Long
            }
        }
    }

    override suspend fun resetToDefaults() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
