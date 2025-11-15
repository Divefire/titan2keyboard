package com.titan2keyboard.domain.model

/**
 * Domain model representing keyboard settings
 */
data class KeyboardSettings(
    val autoCapitalize: Boolean = true,
    val keyRepeatEnabled: Boolean = true,
    val longPressCapitalize: Boolean = false,
    val keyRepeatDelay: Long = 400L, // milliseconds
    val keyRepeatRate: Long = 50L    // milliseconds
)
