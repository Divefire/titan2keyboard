package com.titan2keyboard.domain.model

/**
 * Represents a text shortcut/auto-correction rule
 * @param trigger The text pattern to match (e.g., "Im")
 * @param replacement The text to replace it with (e.g., "I'm")
 * @param caseSensitive Whether the trigger should be case-sensitive
 */
data class TextShortcut(
    val trigger: String,
    val replacement: String,
    val caseSensitive: Boolean = false
)
