package com.titan2keyboard.domain.model

/**
 * Domain model representing keyboard settings
 */
data class KeyboardSettings(
    val vibrationEnabled: Boolean = false,
    val soundEnabled: Boolean = false,
    val autoCapitalize: Boolean = true,
    val keyRepeatEnabled: Boolean = true,
    val keyRepeatDelay: Long = 400L, // milliseconds
    val keyRepeatRate: Long = 50L    // milliseconds
)
