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
    val altBackspaceBehavior: AltBackspaceBehavior = AltBackspaceBehavior.DELETE_LINE, // Alt+Backspace behavior
    val keyRepeatDelay: Long = 400L, // milliseconds
    val keyRepeatRate: Long = 50L,   // milliseconds
    val preferredCurrency: String? = null, // Preferred currency symbol (null = use locale default)
    val selectedLanguage: String = "en", // Primary language for accents (en, fr, de, es, pt, it, etc.)
    val longPressAccents: Boolean = false // Long-press shows accent variants instead of uppercase
)
