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

        // Handle long-press capitalization for letter keys
        if (currentSettings.longPressCapitalize && isLetterKey(event.keyCode) && event.repeatCount > 0) {
            // User is holding down a letter key - output uppercase
            val char = getCharForKeyCode(event.keyCode)
            if (char != null) {
                inputConnection.commitText(char.uppercase(), 1)
                return KeyEventResult.Handled
            }
        }

        // Handle key repeat setting (but always allow backspace to repeat)
        if (!currentSettings.keyRepeatEnabled && event.repeatCount > 0 && event.keyCode != KeyEvent.KEYCODE_DEL) {
            // Block repeated keys if key repeat is disabled (except backspace and long-press capitalize)
            return KeyEventResult.Handled
        }

        // Handle auto-capitalization for letter keys (first press only)
        if (isLetterKey(event.keyCode) && event.repeatCount == 0) {
            // Only apply auto-capitalize on first key press, not repeats
            if (shouldAutoCapitalize(inputConnection)) {
                // Get the letter character for this key code
                val char = getCharForKeyCode(event.keyCode)
                if (char != null) {
                    // Commit the capitalized character directly
                    inputConnection.commitText(char.uppercase(), 1)
                    return KeyEventResult.Handled
                }
            }
        }

        // Let system handle all other events normally
        return KeyEventResult.NotHandled
    }

    /**
     * Get the character for a letter key code
     */
    private fun getCharForKeyCode(keyCode: Int): String? {
        return when (keyCode) {
            KeyEvent.KEYCODE_A -> "a"
            KeyEvent.KEYCODE_B -> "b"
            KeyEvent.KEYCODE_C -> "c"
            KeyEvent.KEYCODE_D -> "d"
            KeyEvent.KEYCODE_E -> "e"
            KeyEvent.KEYCODE_F -> "f"
            KeyEvent.KEYCODE_G -> "g"
            KeyEvent.KEYCODE_H -> "h"
            KeyEvent.KEYCODE_I -> "i"
            KeyEvent.KEYCODE_J -> "j"
            KeyEvent.KEYCODE_K -> "k"
            KeyEvent.KEYCODE_L -> "l"
            KeyEvent.KEYCODE_M -> "m"
            KeyEvent.KEYCODE_N -> "n"
            KeyEvent.KEYCODE_O -> "o"
            KeyEvent.KEYCODE_P -> "p"
            KeyEvent.KEYCODE_Q -> "q"
            KeyEvent.KEYCODE_R -> "r"
            KeyEvent.KEYCODE_S -> "s"
            KeyEvent.KEYCODE_T -> "t"
            KeyEvent.KEYCODE_U -> "u"
            KeyEvent.KEYCODE_V -> "v"
            KeyEvent.KEYCODE_W -> "w"
            KeyEvent.KEYCODE_X -> "x"
            KeyEvent.KEYCODE_Y -> "y"
            KeyEvent.KEYCODE_Z -> "z"
            else -> null
        }
    }

    /**
     * Handle key up event
     */
    fun handleKeyUp(event: KeyEvent, inputConnection: InputConnection?): KeyEventResult {
        inputConnection ?: return KeyEventResult.NotHandled

        // Handle key repeat setting for key up (but always allow backspace to repeat)
        if (!currentSettings.keyRepeatEnabled && event.repeatCount > 0 && event.keyCode != KeyEvent.KEYCODE_DEL) {
            return KeyEventResult.Handled
        }

        return KeyEventResult.NotHandled
    }

    /**
     * Check if the key code represents a letter
     */
    private fun isLetterKey(keyCode: Int): Boolean {
        return keyCode in KeyEvent.KEYCODE_A..KeyEvent.KEYCODE_Z
    }

    /**
     * Check if auto-capitalization should be applied
     */
    private fun shouldAutoCapitalize(inputConnection: InputConnection): Boolean {
        if (!currentSettings.autoCapitalize) return false

        // Check text before cursor to determine if we should capitalize
        val textBeforeCursor = inputConnection.getTextBeforeCursor(100, 0)

        if (textBeforeCursor.isNullOrEmpty()) {
            // Start of text, capitalize
            return true
        }

        // Get the last character
        val lastChar = textBeforeCursor.last()

        // Capitalize after sentence-ending punctuation
        if (lastChar in listOf('.', '!', '?')) {
            return true
        }

        // Capitalize after newline
        if (lastChar == '\n') {
            return true
        }

        // Check for sentence-ending punctuation followed by space
        if (textBeforeCursor.length >= 2) {
            val secondToLast = textBeforeCursor[textBeforeCursor.length - 2]
            if (lastChar in listOf(' ', '\t') && secondToLast in listOf('.', '!', '?')) {
                return true
            }
        }

        return false
    }
}
