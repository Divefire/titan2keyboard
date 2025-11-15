package com.titan2keyboard.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.titan2keyboard.domain.model.TextShortcut
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.shortcutsDataStore: DataStore<Preferences> by preferencesDataStore(name = "shortcuts")

/**
 * DataStore wrapper for managing text shortcuts
 * Uses JSON serialization to store list of shortcuts as a string
 */
class ShortcutsDataStore(context: Context) {

    private val dataStore = context.shortcutsDataStore
    private val json = Json {
        prettyPrint = false
        ignoreUnknownKeys = true
    }

    companion object {
        private val SHORTCUTS_KEY = stringPreferencesKey("shortcuts_list")
    }

    /**
     * Flow of all shortcuts
     */
    val shortcutsFlow: Flow<List<TextShortcut>> = dataStore.data.map { preferences ->
        val jsonString = preferences[SHORTCUTS_KEY] ?: return@map emptyList()
        try {
            json.decodeFromString<List<TextShortcut>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Save shortcuts list
     */
    suspend fun saveShortcuts(shortcuts: List<TextShortcut>) {
        dataStore.edit { preferences ->
            preferences[SHORTCUTS_KEY] = json.encodeToString(shortcuts)
        }
    }

    /**
     * Add a shortcut
     */
    suspend fun addShortcut(shortcut: TextShortcut) {
        dataStore.edit { preferences ->
            val jsonString = preferences[SHORTCUTS_KEY]
            val currentList = if (jsonString != null) {
                try {
                    json.decodeFromString<List<TextShortcut>>(jsonString)
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }

            val updatedList = currentList + shortcut
            preferences[SHORTCUTS_KEY] = json.encodeToString(updatedList)
        }
    }

    /**
     * Update a shortcut
     */
    suspend fun updateShortcut(shortcut: TextShortcut) {
        dataStore.edit { preferences ->
            val jsonString = preferences[SHORTCUTS_KEY] ?: return@edit
            val currentList = try {
                json.decodeFromString<List<TextShortcut>>(jsonString)
            } catch (e: Exception) {
                return@edit
            }

            val updatedList = currentList.map {
                if (it.id == shortcut.id) shortcut else it
            }
            preferences[SHORTCUTS_KEY] = json.encodeToString(updatedList)
        }
    }

    /**
     * Delete a shortcut
     */
    suspend fun deleteShortcut(id: String) {
        dataStore.edit { preferences ->
            val jsonString = preferences[SHORTCUTS_KEY] ?: return@edit
            val currentList = try {
                json.decodeFromString<List<TextShortcut>>(jsonString)
            } catch (e: Exception) {
                return@edit
            }

            val updatedList = currentList.filter { it.id != id }
            preferences[SHORTCUTS_KEY] = json.encodeToString(updatedList)
        }
    }

    /**
     * Clear all shortcuts
     */
    suspend fun clearShortcuts() {
        dataStore.edit { preferences ->
            preferences.remove(SHORTCUTS_KEY)
        }
    }
}
