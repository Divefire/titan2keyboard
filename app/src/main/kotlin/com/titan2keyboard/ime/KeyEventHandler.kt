package com.titan2keyboard.ime

import android.text.InputType
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import com.titan2keyboard.domain.model.KeyEventResult
import com.titan2keyboard.domain.model.KeyboardSettings
import com.titan2keyboard.domain.model.ModifierState
import com.titan2keyboard.domain.model.ModifiersState
import com.titan2keyboard.domain.repository.ShortcutRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Listener for modifier state changes
 */
interface ModifierStateListener {
    fun onModifierStateChanged(modifiersState: ModifiersState)
}

/**
 * Handles physical keyboard key events
 */
@Singleton
class KeyEventHandler @Inject constructor(
    private val shortcutRepository: ShortcutRepository
) {

    private var currentSettings: KeyboardSettings = KeyboardSettings()
    private var currentEditorInfo: EditorInfo? = null
    private var lastSpaceTime: Long = 0L
    private var currentWord: StringBuilder = StringBuilder()

    // Track last replacement for undo on backspace
    private data class ReplacementInfo(
        val original: String,
        val replacement: String,
        val hadTrailingSpace: Boolean
    )
    private var lastReplacement: ReplacementInfo? = null
    private var skipNextShortcut: Boolean = false

    // Track last auto-capitalized character for undo on backspace
    private var lastAutoCapitalizedChar: String? = null

    // Modifier state tracking
    private var modifiersState = ModifiersState()
    private var shiftKeyDownTime: Long = 0L
    private var altKeyDownTime: Long = 0L
    private var modifierStateListener: ModifierStateListener? = null

    companion object {
        private const val DOUBLE_SPACE_THRESHOLD_MS = 500L
        private const val LONG_PRESS_THRESHOLD_MS = 500L
    }

    /**
     * Set the listener for modifier state changes
     */
    fun setModifierStateListener(listener: ModifierStateListener?) {
        modifierStateListener = listener
    }

    /**
     * Get the current modifier state
     */
    fun getModifiersState(): ModifiersState = modifiersState

    /**
     * Update the current settings
     */
    fun updateSettings(settings: KeyboardSettings) {
        currentSettings = settings
    }

    /**
     * Update the current editor info
     */
    fun updateEditorInfo(editorInfo: EditorInfo?) {
        currentEditorInfo = editorInfo
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

            // Handle long-press capitalization for letter keys
            if (currentSettings.longPressCapitalize && isLetterKey(event.keyCode)) {
                val char = getCharForKeyCode(event.keyCode)
                if (char != null) {
                    if (currentSettings.keyRepeatEnabled) {
                        // Key repeat is enabled: output uppercase on all repeats (appends)
                        inputConnection.commitText(char.uppercase(), 1)
                        return KeyEventResult.Handled
                    } else {
                        // Key repeat is disabled: replace lowercase with uppercase on first repeat
                        if (event.repeatCount == 1) {
                            // Delete the lowercase letter we just typed
                            inputConnection.deleteSurroundingText(1, 0)
                            // Replace it with uppercase
                            inputConnection.commitText(char.uppercase(), 1)
                        }
                        // Block this and all subsequent repeats
                        return KeyEventResult.Handled
                    }
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

        // Handle modifier keys (Shift/Alt)
        when (event.keyCode) {
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> {
                if (currentSettings.stickyShift) {
                    shiftKeyDownTime = event.eventTime
                    return KeyEventResult.Handled
                }
            }
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {
                if (currentSettings.stickyAlt) {
                    altKeyDownTime = event.eventTime
                    return KeyEventResult.Handled
                }
            }
        }

        // Handle backspace - check if we should undo auto-capitalization
        if (event.keyCode == KeyEvent.KEYCODE_DEL && lastAutoCapitalizedChar != null) {
            val capitalizedChar = lastAutoCapitalizedChar!!
            // Check if the text before cursor matches our capitalized character
            val textBefore = inputConnection.getTextBeforeCursor(1, 0)

            if (textBefore?.toString() == capitalizedChar.uppercase()) {
                // Undo the auto-capitalization - replace with lowercase
                inputConnection.deleteSurroundingText(1, 0)
                inputConnection.commitText(capitalizedChar.lowercase(), 1)
                lastAutoCapitalizedChar = null
                return KeyEventResult.Handled
            }
            // Text doesn't match, clear the tracking
            lastAutoCapitalizedChar = null
        }

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
                // Skip next shortcut check to prevent re-triggering on the next space
                skipNextShortcut = true
                return KeyEventResult.Handled
            }
            // Text doesn't match, clear the replacement tracking
            lastReplacement = null
        }

        // Clear tracking on any non-backspace key
        if (event.keyCode != KeyEvent.KEYCODE_DEL) {
            lastReplacement = null
            lastAutoCapitalizedChar = null
        }

        // Check for text shortcuts on word boundary keys (space, enter, punctuation)
        if (currentSettings.textShortcutsEnabled && isWordBoundary(event.keyCode)) {
            // If we just undid a replacement, skip this shortcut check
            if (skipNextShortcut) {
                skipNextShortcut = false
                // Let the boundary character be inserted normally
                return KeyEventResult.NotHandled
            }

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

        // Clear skip flag when typing a letter or other non-boundary key
        if (!isWordBoundary(event.keyCode)) {
            skipNextShortcut = false
        }

        // Handle letter keys with modifiers or auto-capitalization
        if (isLetterKey(event.keyCode)) {
            val char = getCharForKeyCode(event.keyCode)
            if (char != null) {
                // Check if we should apply shift modifier
                val shouldCapitalize = modifiersState.isShiftActive() || shouldAutoCapitalize(inputConnection)

                if (shouldCapitalize) {
                    // Commit the capitalized character
                    inputConnection.commitText(char.uppercase(), 1)

                    // Track auto-capitalization for undo (only if not from sticky modifier)
                    if (!modifiersState.isShiftActive()) {
                        lastAutoCapitalizedChar = char
                    }

                    // Clear one-shot modifiers after use
                    clearOneShotModifiers()
                    return KeyEventResult.Handled
                }

                // If modifier is active but it's for a letter key, let it apply via clear below
                if (modifiersState.isShiftActive() || modifiersState.isAltActive()) {
                    // Clear one-shot modifiers after any character input
                    clearOneShotModifiers()
                }
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

        // Handle modifier key release
        when (event.keyCode) {
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> {
                if (currentSettings.stickyShift && shiftKeyDownTime > 0) {
                    val pressDuration = event.eventTime - shiftKeyDownTime
                    val isLongPress = pressDuration >= LONG_PRESS_THRESHOLD_MS
                    toggleShiftModifier(isLongPress)
                    shiftKeyDownTime = 0L
                    return KeyEventResult.Handled
                }
            }
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {
                if (currentSettings.stickyAlt && altKeyDownTime > 0) {
                    val pressDuration = event.eventTime - altKeyDownTime
                    val isLongPress = pressDuration >= LONG_PRESS_THRESHOLD_MS
                    toggleAltModifier(isLongPress)
                    altKeyDownTime = 0L
                    return KeyEventResult.Handled
                }
            }
        }

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

        // Check editor info for input types that shouldn't auto-capitalize
        currentEditorInfo?.let { info ->
            val inputType = info.inputType
            val typeClass = inputType and InputType.TYPE_MASK_CLASS
            val typeVariation = inputType and InputType.TYPE_MASK_VARIATION

            // Don't auto-capitalize for specific input types
            when (typeClass) {
                InputType.TYPE_CLASS_TEXT -> {
                    // Check for variations that shouldn't capitalize
                    when (typeVariation) {
                        InputType.TYPE_TEXT_VARIATION_URI,
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
                        InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS,
                        InputType.TYPE_TEXT_VARIATION_PASSWORD,
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
                        InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD -> return false
                    }

                    // Check for NO_SUGGESTIONS flag (used by our shortcut dialog)
                    if (inputType and InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS != 0) {
                        return false
                    }
                }
                // Don't auto-capitalize for number, phone, datetime inputs
                InputType.TYPE_CLASS_NUMBER,
                InputType.TYPE_CLASS_PHONE,
                InputType.TYPE_CLASS_DATETIME -> return false
            }
        }

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

    /**
     * Update modifier state and notify listener
     */
    private fun updateModifierState(newState: ModifiersState) {
        modifiersState = newState
        modifierStateListener?.onModifierStateChanged(newState)
    }

    /**
     * Toggle shift modifier state
     * @param isLongPress true if this is a long press (locked), false for one-shot
     */
    private fun toggleShiftModifier(isLongPress: Boolean) {
        if (!currentSettings.stickyShift) return

        val newShiftState = when {
            // If already locked and pressed again, turn off
            modifiersState.shift == ModifierState.LOCKED -> ModifierState.NONE
            // Long press = locked
            isLongPress -> ModifierState.LOCKED
            // Short press = one-shot
            else -> ModifierState.ONE_SHOT
        }

        // Clear alt if activating shift (mutual exclusivity)
        val newAltState = if (newShiftState != ModifierState.NONE) ModifierState.NONE else modifiersState.alt

        updateModifierState(ModifiersState(shift = newShiftState, alt = newAltState))
    }

    /**
     * Toggle alt modifier state
     * @param isLongPress true if this is a long press (locked), false for one-shot
     */
    private fun toggleAltModifier(isLongPress: Boolean) {
        if (!currentSettings.stickyAlt) return

        val newAltState = when {
            // If already locked and pressed again, turn off
            modifiersState.alt == ModifierState.LOCKED -> ModifierState.NONE
            // Long press = locked
            isLongPress -> ModifierState.LOCKED
            // Short press = one-shot
            else -> ModifierState.ONE_SHOT
        }

        // Clear shift if activating alt (mutual exclusivity)
        val newShiftState = if (newAltState != ModifierState.NONE) ModifierState.NONE else modifiersState.shift

        updateModifierState(ModifiersState(shift = newShiftState, alt = newAltState))
    }

    /**
     * Clear one-shot modifiers after use
     */
    private fun clearOneShotModifiers() {
        if (!modifiersState.hasOneShotModifier()) return

        val newShift = if (modifiersState.shift == ModifierState.ONE_SHOT) ModifierState.NONE else modifiersState.shift
        val newAlt = if (modifiersState.alt == ModifierState.ONE_SHOT) ModifierState.NONE else modifiersState.alt

        if (newShift != modifiersState.shift || newAlt != modifiersState.alt) {
            updateModifierState(ModifiersState(shift = newShift, alt = newAlt))
        }
    }
}
