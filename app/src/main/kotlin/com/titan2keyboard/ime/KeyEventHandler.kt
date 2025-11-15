package com.titan2keyboard.ime

import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import com.titan2keyboard.domain.model.KeyEventResult
import com.titan2keyboard.domain.model.KeyboardSettings
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles physical keyboard key events
 */
@Singleton
class KeyEventHandler @Inject constructor() {

    private var currentSettings: KeyboardSettings = KeyboardSettings()

    /**
     * Update the current settings
     */
    fun updateSettings(settings: KeyboardSettings) {
        currentSettings = settings
    }

    /**
     * Handle a key event from the physical keyboard
     * @param event The key event to handle
     * @param inputConnection The current input connection
     * @return KeyEventResult indicating whether the event was handled
     */
    fun handleKeyDown(event: KeyEvent, inputConnection: InputConnection?): KeyEventResult {
        inputConnection ?: return KeyEventResult.NotHandled

        // For now, let the system handle all events
        // This is where we'll add custom key handling logic
        return when (event.keyCode) {
            // System will handle standard text input
            else -> KeyEventResult.NotHandled
        }
    }

    /**
     * Handle key up event
     */
    fun handleKeyUp(event: KeyEvent, inputConnection: InputConnection?): KeyEventResult {
        inputConnection ?: return KeyEventResult.NotHandled

        // For now, let the system handle all events
        return KeyEventResult.NotHandled
    }

    /**
     * Check if auto-capitalization should be applied
     */
    private fun shouldAutoCapitalize(inputConnection: InputConnection): Boolean {
        if (!currentSettings.autoCapitalize) return false

        val textBeforeCursor = inputConnection.getTextBeforeCursor(1, 0)
        return textBeforeCursor.isNullOrEmpty() || textBeforeCursor.last() in listOf('.', '!', '?', '\n')
    }
}
