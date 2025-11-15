package com.titan2keyboard.ime

import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import com.titan2keyboard.domain.model.KeyEventResult
import com.titan2keyboard.domain.model.KeyboardSettings
import com.titan2keyboard.domain.repository.ShortcutRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles physical keyboard key events
 */
@Singleton
class KeyEventHandler @Inject constructor(
    private val shortcutRepository: ShortcutRepository
) {

    private var currentSettings: KeyboardSettings = KeyboardSettings()
    private var lastSpaceTime: Long = 0L
    private var currentWord: StringBuilder = StringBuilder()

    // Track last replacement for undo on backspace
    private data class ReplacementInfo(
        val original: String,
        val replacement: String,
        val hadTrailingSpace: Boolean
    )
    private var lastReplacement: ReplacementInfo? = null

    companion object {
        private const val DOUBLE_SPACE_THRESHOLD_MS = 500L
    }

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

        // Handle key repeats (repeatCount > 0)
        if (event.repeatCount > 0) {
            // Always allow backspace to repeat
            if (event.keyCode == KeyEvent.KEYCODE_DEL) {
                return KeyEventResult.NotHandled
            }

            // Handle long-press capitalization for letter keys (if enabled)
            if (currentSettings.longPressCapitalize && isLetterKey(event.keyCode)) {
                val char = getCharForKeyCode(event.keyCode)
                if (char != null) {
                    inputConnection.commitText(char.uppercase(), 1)
                    return KeyEventResult.Handled
                }
            }

            // Block all other key repeats if key repeat is disabled
            if (!currentSettings.keyRepeatEnabled) {
                return KeyEventResult.Handled
            }

            // If key repeat is enabled, let system handle the repeat
            return KeyEventResult.NotHandled
        }

        // Handle first key press (repeatCount == 0)

        // Handle backspace - check if we should undo last replacement
        if (event.keyCode == KeyEvent.KEYCODE_DEL && lastReplacement != null) {
            val replacement = lastReplacement!!
            // Check if the text before cursor matches our replacement
            val textBefore = inputConnection.getTextBeforeCursor(replacement.replacement.length + 1, 0)
            val expectedText = if (replacement.hadTrailingSpace) {
                replacement.replacement + " "
            } else {
                replacement.replacement
            }

            if (textBefore?.toString() == expectedText) {
                // Undo the replacement - restore original word
                val deleteCount = if (replacement.hadTrailingSpace) {
                    replacement.replacement.length + 1
                } else {
                    replacement.replacement.length
                }
                inputConnection.deleteSurroundingText(deleteCount, 0)
                inputConnection.commitText(replacement.original, 1)
                lastReplacement = null
                return KeyEventResult.Handled
            }
            // Text doesn't match, clear the replacement tracking
            lastReplacement = null
        }

        // Clear replacement tracking on any non-backspace key
        if (event.keyCode != KeyEvent.KEYCODE_DEL) {
            lastReplacement = null
        }

        // Check for text shortcuts on word boundary keys (space, enter, punctuation)
        if (currentSettings.textShortcutsEnabled && isWordBoundary(event.keyCode)) {
            val replacementResult = checkAndReplaceShortcut(inputConnection, event.keyCode)
            if (replacementResult != null) {
                // Shortcut was replaced, handle double-space for space key
                if (event.keyCode == KeyEvent.KEYCODE_SPACE && currentSettings.doubleSpacePeriod) {
                    val currentTime = System.currentTimeMillis()
                    if (lastSpaceTime > 0 && (currentTime - lastSpaceTime) <= DOUBLE_SPACE_THRESHOLD_MS) {
                        // Double space after replacement - replace the trailing space with period+space
                        inputConnection.deleteSurroundingText(1, 0)
                        inputConnection.commitText(". ", 1)
                        // Update lastReplacement to reflect the space is gone
                        lastReplacement = lastReplacement?.copy(hadTrailingSpace = false)
                        lastSpaceTime = 0L
                        return KeyEventResult.Handled
                    } else {
                        lastSpaceTime = currentTime
                    }
                }
                // Shortcut was replaced and space/punctuation was added
                return KeyEventResult.Handled
            }
        }

        // Handle double-space period (for non-shortcut cases)
        if (event.keyCode == KeyEvent.KEYCODE_SPACE && currentSettings.doubleSpacePeriod) {
            val currentTime = System.currentTimeMillis()
            if (lastSpaceTime > 0 && (currentTime - lastSpaceTime) <= DOUBLE_SPACE_THRESHOLD_MS) {
                // Double space detected - replace last space with period and space
                inputConnection.deleteSurroundingText(1, 0)
                inputConnection.commitText(". ", 1)
                lastSpaceTime = 0L  // Reset to prevent triple-space issues
                return KeyEventResult.Handled
            } else {
                // Single space - record the time and let system handle it
                lastSpaceTime = currentTime
                return KeyEventResult.NotHandled
            }
        }

        // Reset space timer if any other key is pressed
        if (event.keyCode != KeyEvent.KEYCODE_SPACE) {
            lastSpaceTime = 0L
        }

        // Handle auto-capitalization for letter keys
        if (isLetterKey(event.keyCode) && shouldAutoCapitalize(inputConnection)) {
            val char = getCharForKeyCode(event.keyCode)
            if (char != null) {
                // Commit the capitalized character directly
                inputConnection.commitText(char.uppercase(), 1)
                return KeyEventResult.Handled
            }
        }

        // Let system handle all other first presses normally
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

        // Block key up for repeats if key repeat is disabled
        // (but always allow backspace and long-press capitalize)
        if (event.repeatCount > 0 && !currentSettings.keyRepeatEnabled &&
            event.keyCode != KeyEvent.KEYCODE_DEL &&
            !(currentSettings.longPressCapitalize && isLetterKey(event.keyCode))) {
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

    /**
     * Check if a key code represents a word boundary
     */
    private fun isWordBoundary(keyCode: Int): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_SPACE,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_TAB,
            KeyEvent.KEYCODE_PERIOD,
            KeyEvent.KEYCODE_COMMA -> true
            else -> false
        }
    }

    /**
     * Check if the last word matches a shortcut and replace it
     * @param inputConnection The input connection
     * @param boundaryKeyCode The key code of the word boundary character (space, period, etc.)
     * @return The replacement text if a shortcut was found, null otherwise
     */
    private fun checkAndReplaceShortcut(inputConnection: InputConnection, boundaryKeyCode: Int): String? {
        // Get text before cursor to extract the last word
        val textBeforeCursor = inputConnection.getTextBeforeCursor(100, 0) ?: return null
        if (textBeforeCursor.isEmpty()) return null

        // Extract the last word (everything after the last word boundary)
        val lastWord = extractLastWord(textBeforeCursor)
        if (lastWord.isEmpty()) return null

        // Check if this word has a shortcut replacement
        val replacement = shortcutRepository.findReplacement(lastWord) ?: return null

        // Delete the original word
        inputConnection.deleteSurroundingText(lastWord.length, 0)

        // Get the trailing character for this boundary key
        val trailingChar = when (boundaryKeyCode) {
            KeyEvent.KEYCODE_SPACE -> " "
            KeyEvent.KEYCODE_ENTER -> "\n"
            KeyEvent.KEYCODE_TAB -> "\t"
            KeyEvent.KEYCODE_PERIOD -> "."
            KeyEvent.KEYCODE_COMMA -> ","
            else -> " " // Default to space
        }

        // Commit the replacement with the trailing character
        inputConnection.commitText(replacement + trailingChar, 1)

        // Track this replacement for potential undo
        lastReplacement = ReplacementInfo(
            original = lastWord,
            replacement = replacement,
            hadTrailingSpace = trailingChar == " "
        )

        return replacement
    }

    /**
     * Extract the last word from text (everything after the last word boundary)
     */
    private fun extractLastWord(text: CharSequence): String {
        val str = text.toString()
        var startIndex = str.length - 1

        // Find the start of the last word by going backwards until we hit a word boundary
        while (startIndex >= 0) {
            val char = str[startIndex]
            if (char.isWhitespace() || char in ".!?,;:\"'()[]{}") {
                break
            }
            startIndex--
        }

        // Extract the word (startIndex is now at the boundary, so add 1)
        return str.substring(startIndex + 1)
    }
}
