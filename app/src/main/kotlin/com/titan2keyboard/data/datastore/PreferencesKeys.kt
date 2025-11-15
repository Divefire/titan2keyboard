package com.titan2keyboard.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey

/**
 * DataStore preference keys for keyboard settings
 */
object PreferencesKeys {
    val AUTO_CAPITALIZE = booleanPreferencesKey("auto_capitalize")
    val KEY_REPEAT_ENABLED = booleanPreferencesKey("key_repeat_enabled")
    val LONG_PRESS_CAPITALIZE = booleanPreferencesKey("long_press_capitalize")
    val DOUBLE_SPACE_PERIOD = booleanPreferencesKey("double_space_period")
    val TEXT_SHORTCUTS_ENABLED = booleanPreferencesKey("text_shortcuts_enabled")
    val STICKY_SHIFT = booleanPreferencesKey("sticky_shift")
    val STICKY_ALT = booleanPreferencesKey("sticky_alt")
    val ALT_BACKSPACE_DELETE_LINE = booleanPreferencesKey("alt_backspace_delete_line")
    val KEY_REPEAT_DELAY = longPreferencesKey("key_repeat_delay")
    val KEY_REPEAT_RATE = longPreferencesKey("key_repeat_rate")
}
