package com.titan2keyboard.data.repository

import android.util.Log
import com.titan2keyboard.data.datastore.ShortcutsDataStore
import com.titan2keyboard.domain.model.TextShortcut
import com.titan2keyboard.domain.repository.ShortcutRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ShortcutRepository with persistent storage
 * Maintains an in-memory cache for fast synchronous lookups during typing
 */
@Singleton
class ShortcutRepositoryImpl @Inject constructor(
    private val shortcutsDataStore: ShortcutsDataStore
) : ShortcutRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // In-memory cache for fast synchronous access during typing
    private val cachedShortcuts = MutableStateFlow<List<TextShortcut>>(emptyList())
    private val cachedMap = MutableStateFlow<Map<String, TextShortcut>>(emptyMap())

    companion object {
        private const val TAG = "ShortcutRepository"
    }

    init {
        // Initialize cache from DataStore
        scope.launch {
            shortcutsDataStore.shortcutsFlow.collect { shortcuts ->
                Log.d(TAG, "Cache updated: ${shortcuts.size} shortcuts loaded")
                shortcuts.forEach { shortcut ->
                    Log.d(TAG, "  Shortcut: '${shortcut.trigger}' -> '${shortcut.replacement}' (caseSensitive=${shortcut.caseSensitive}, isDefault=${shortcut.isDefault})")
                }
                cachedShortcuts.value = shortcuts
                // Build fast lookup map
                cachedMap.value = shortcuts.associateBy {
                    if (it.caseSensitive) it.trigger else it.trigger.lowercase()
                }
                Log.d(TAG, "Cache map keys: ${cachedMap.value.keys}")
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

        // Create default shortcuts with IDs
        val defaults = listOf(
            // Common contractions
            TextShortcut(UUID.randomUUID().toString(), "Im", "I'm", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "Ive", "I've", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "Ill", "I'll", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "Id", "I'd", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "dont", "don't", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "doesnt", "doesn't", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "didnt", "didn't", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "cant", "can't", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "couldnt", "couldn't", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "wouldnt", "wouldn't", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "shouldnt", "shouldn't", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "isnt", "isn't", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "arent", "aren't", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "wasnt", "wasn't", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "werent", "weren't", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "hasnt", "hasn't", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "havent", "haven't", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "hadnt", "hadn't", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "wont", "won't", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "thats", "that's", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "theres", "there's", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "heres", "here's", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "whats", "what's", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "wheres", "where's", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "whos", "who's", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "hows", "how's", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "whens", "when's", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "whys", "why's", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "youre", "you're", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "youve", "you've", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "youll", "you'll", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "youd", "you'd", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "theyre", "they're", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "theyve", "they've", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "theyll", "they'll", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "theyd", "they'd", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "weve", "we've", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "wed", "we'd", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "shes", "she's", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "hes", "he's", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "lets", "let's", caseSensitive = false, isDefault = true),

            // Common typos
            TextShortcut(UUID.randomUUID().toString(), "teh", "the", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "recieve", "receive", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "occured", "occurred", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "seperate", "separate", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "definately", "definitely", caseSensitive = false, isDefault = true),
            TextShortcut(UUID.randomUUID().toString(), "alot", "a lot", caseSensitive = false, isDefault = true),
        )

        shortcutsDataStore.saveShortcuts(defaults)
    }
}
