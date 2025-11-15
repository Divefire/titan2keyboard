package com.titan2keyboard.domain.repository

import com.titan2keyboard.domain.model.TextShortcut
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing text shortcuts
 */
interface ShortcutRepository {
    /**
     * Flow of all available shortcuts
     */
    val shortcutsFlow: Flow<List<TextShortcut>>

    /**
     * Get all available shortcuts (snapshot)
     */
    suspend fun getShortcuts(): List<TextShortcut>

    /**
     * Find a replacement for the given text
     * @param text The text to look up
     * @return The replacement text, or null if no shortcut matches
     */
    suspend fun findReplacement(text: String): String?

    /**
     * Add a new shortcut
     */
    suspend fun addShortcut(shortcut: TextShortcut)

    /**
     * Update an existing shortcut
     */
    suspend fun updateShortcut(shortcut: TextShortcut)

    /**
     * Delete a shortcut by ID
     */
    suspend fun deleteShortcut(id: String)

    /**
     * Initialize with default shortcuts if none exist
     */
    suspend fun initializeDefaults()
}
