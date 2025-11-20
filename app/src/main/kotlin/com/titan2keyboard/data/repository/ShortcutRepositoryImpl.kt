package com.titan2keyboard.data.repository

import android.util.Log
import com.titan2keyboard.data.DefaultShortcuts
import com.titan2keyboard.data.datastore.ShortcutsDataStore
import com.titan2keyboard.domain.model.TextShortcut
import com.titan2keyboard.domain.repository.SettingsRepository
import com.titan2keyboard.domain.repository.ShortcutRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ShortcutRepository with persistent storage
 * Maintains an in-memory cache for fast synchronous lookups during typing
 * Filters shortcuts based on the currently selected language
 */
@Singleton
class ShortcutRepositoryImpl @Inject constructor(
    private val shortcutsDataStore: ShortcutsDataStore,
    private val settingsRepository: SettingsRepository
) : ShortcutRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // In-memory cache for fast synchronous access during typing
    private val cachedShortcuts = MutableStateFlow<List<TextShortcut>>(emptyList())
    private val cachedMap = MutableStateFlow<Map<String, TextShortcut>>(emptyMap())

    companion object {
        private const val TAG = "ShortcutRepository"
    }

    init {
        // Initialize cache from DataStore, filtered by selected language
        scope.launch {
            combine(
                shortcutsDataStore.shortcutsFlow,
                settingsRepository.settingsFlow
            ) { shortcuts, settings ->
                shortcuts to settings.selectedLanguage
            }.collect { (shortcuts, selectedLanguage) ->
                // Filter shortcuts by selected language
                val filteredShortcuts = shortcuts.filter { it.language == selectedLanguage }

                Log.d(TAG, "Cache updated: ${filteredShortcuts.size} shortcuts loaded for language '$selectedLanguage' (total: ${shortcuts.size})")
                filteredShortcuts.forEach { shortcut ->
                    Log.d(TAG, "  Shortcut: '${shortcut.trigger}' -> '${shortcut.replacement}' (language=${shortcut.language}, caseSensitive=${shortcut.caseSensitive}, isDefault=${shortcut.isDefault})")
                }
                cachedShortcuts.value = filteredShortcuts
                // Build fast lookup map for the current language
                cachedMap.value = filteredShortcuts.associateBy {
                    if (it.caseSensitive) it.trigger else it.trigger.lowercase()
                }
                Log.d(TAG, "Cache map keys for language '$selectedLanguage': ${cachedMap.value.keys}")
            }
        }
    }

    override val shortcutsFlow: Flow<List<TextShortcut>> = shortcutsDataStore.shortcutsFlow

    override suspend fun getShortcuts(): List<TextShortcut> {
        return shortcutsDataStore.shortcutsFlow.first()
    }

    override fun findReplacement(text: String): String? {
        val lookupKey = text.lowercase()
        Log.d(TAG, "findReplacement: text='$text', lookupKey='$lookupKey', cachedMap.size=${cachedMap.value.size}")
        val shortcut = cachedMap.value[lookupKey]
        if (shortcut == null) {
            Log.d(TAG, "findReplacement: No match found for '$lookupKey'")
            return null
        }

        Log.d(TAG, "findReplacement: Found match: '${shortcut.trigger}' -> '${shortcut.replacement}'")

        // Preserve the case of the original text
        return when {
            // If original is all uppercase, make replacement uppercase
            text.all { it.isUpperCase() || !it.isLetter() } -> shortcut.replacement.uppercase()
            // If original starts with uppercase, capitalize replacement
            text.firstOrNull()?.isUpperCase() == true -> shortcut.replacement.replaceFirstChar { it.uppercase() }
            // Otherwise use replacement as-is
            else -> shortcut.replacement
        }
    }

    override suspend fun addShortcut(shortcut: TextShortcut) {
        shortcutsDataStore.addShortcut(shortcut)
    }

    override suspend fun updateShortcut(shortcut: TextShortcut) {
        shortcutsDataStore.updateShortcut(shortcut)
    }

    override suspend fun deleteShortcut(id: String) {
        shortcutsDataStore.deleteShortcut(id)
    }

    override suspend fun initializeDefaults() {
        val existing = getShortcuts()
        if (existing.isNotEmpty()) {
            return // Already initialized
        }

        // Load all default shortcuts for all supported languages
        val defaults = DefaultShortcuts.getAllDefaults()
        Log.d(TAG, "Initializing ${defaults.size} default shortcuts across all languages")

        shortcutsDataStore.saveShortcuts(defaults)
    }
}
