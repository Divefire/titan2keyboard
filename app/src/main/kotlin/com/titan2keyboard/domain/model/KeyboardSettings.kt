package com.titan2keyboard.domain.model

/**
 * Domain model representing keyboard settings
 */
data class KeyboardSettings(
    val autoCapitalize: Boolean = true,
    val keyRepeatEnabled: Boolean = true,
    val longPressCapitalize: Boolean = false,
    val doubleSpacePeriod: Boolean = true,
    val textShortcutsEnabled: Boolean = true,
    val stickyShift: Boolean = false,
    val stickyAlt: Boolean = false,
    val altBackspaceDeleteLine: Boolean = true, // Alt+Backspace deletes entire line
    val keyRepeatDelay: Long = 400L, // milliseconds
    val keyRepeatRate: Long = 50L    // milliseconds
)
