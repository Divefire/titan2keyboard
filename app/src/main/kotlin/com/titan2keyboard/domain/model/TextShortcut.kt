package com.titan2keyboard.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a text shortcut/auto-correction rule
 * @param id Unique identifier for the shortcut
 * @param trigger The text pattern to match (e.g., "Im")
 * @param replacement The text to replace it with (e.g., "I'm")
 * @param caseSensitive Whether the trigger should be case-sensitive
 * @param isDefault Whether this is a default shortcut (cannot be deleted, only disabled)
 * @param language The language code this shortcut applies to (e.g., "en", "fr", "de")
 */
@Serializable
data class TextShortcut(
    val id: String,
    val trigger: String,
    val replacement: String,
    val caseSensitive: Boolean = false,
    val isDefault: Boolean = false,
    val language: String = "en"
)
