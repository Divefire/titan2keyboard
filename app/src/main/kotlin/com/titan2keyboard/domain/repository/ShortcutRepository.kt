package com.titan2keyboard.domain.repository

import com.titan2keyboard.domain.model.TextShortcut

/**
 * Repository for managing text shortcuts
 */
interface ShortcutRepository {
    /**
     * Get all available shortcuts
     */
    fun getShortcuts(): List<TextShortcut>

    /**
     * Find a replacement for the given text
     * @param text The text to look up
     * @return The replacement text, or null if no shortcut matches
     */
    fun findReplacement(text: String): String?
}
