package com.titan2keyboard.ime

import android.text.InputType
import android.util.Log
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
    private val shortcutRepository: ShortcutRepository,
    private val accentRepository: com.titan2keyboard.data.AccentRepository
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

    // Modifier state tracking
    private var modifiersState = ModifiersState()
    private var shiftKeyDownTime: Long = 0L
    private var altKeyDownTime: Long = 0L
    private var lastShiftTapTime: Long = 0L
    private var lastAltTapTime: Long = 0L
    private var modifierStateListener: ModifierStateListener? = null

    // Sym key tracking
    private var symKeyDownTime: Long = 0L
    private var lastSymTapTime: Long = 0L
    private var onSymKeyPressed: (() -> Unit)? = null
    private var onSymPickerDismiss: (() -> Unit)? = null
    private var isSymPickerVisible: Boolean = false

    // Accent cycling tracking
    private var accentKeyDownTime: Long = 0L
    private var accentBaseChar: Char? = null
    private var accentCycleList: List<String> = emptyList()
    private var accentCycleIndex: Int = 0
    private var accentCycleHandler: android.os.Handler? = null
    private var accentCycleRunnable: Runnable? = null

    companion object {
        private const val TAG = "KeyEventHandler"
        private const val DOUBLE_SPACE_THRESHOLD_MS = 500L
        private const val LONG_PRESS_THRESHOLD_MS = 500L
        private const val DOUBLE_TAP_THRESHOLD_MS = 300L // Max time between taps for double-tap
        private const val ACCENT_CYCLE_INTERVAL_MS = 450L // Time between accent cycles while holding key
        private const val ACCENT_START_DELAY_MS = 500L // Delay before starting accent cycling
    }

    /**
     * Set the listener for modifier state changes
     */
    fun setModifierStateListener(listener: ModifierStateListener?) {
        modifierStateListener = listener
    }

    /**
     * Set the callback for Sym key presses
     */
    fun setSymKeyPressedCallback(callback: () -> Unit) {
        onSymKeyPressed = callback
    }

    /**
     * Set the callback for dismissing the symbol picker
     */
    fun setSymPickerDismissCallback(callback: () -> Unit) {
        onSymPickerDismiss = callback
    }

    /**
     * Update the symbol picker visibility state
     */
    fun setSymPickerVisible(visible: Boolean) {
        isSymPickerVisible = visible
    }

    /**
     * Insert a symbol from the symbol picker
     */
    fun insertSymbol(symbol: String, inputConnection: InputConnection?) {
        inputConnection?.commitText(symbol, 1)
        // Clear one-shot modifiers after inserting
        clearOneShotModifiers()
    }

    /**
     * Dismiss the symbol picker overlay
     */
    fun dismissSymbolPicker() {
        // Update modifier state to hide symbol picker
        updateModifierState(modifiersState.copy(symPickerVisible = false))
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
     * Called when input is started - check if we should activate auto-cap shift
     */
    fun onInputStarted(inputConnection: InputConnection?) {
        inputConnection ?: return
        // Reset modifier state when starting new input
        updateModifierState(ModifiersState())
        // Don't auto-activate shift on input start - wait for first key press
        // This prevents the shift indicator from showing on home screen or non-text fields
    }

    /**
     * Handle a key event from the physical keyboard
     * @param event The key event to handle
     * @param inputConnection The current input connection
     * @return KeyEventResult indicating whether the event was handled
     */
    fun handleKeyDown(event: KeyEvent, inputConnection: InputConnection?): KeyEventResult {
        Log.d(TAG, "handleKeyDown: keyCode=${event.keyCode}, modifiers=shift:${modifiersState.shift}/alt:${modifiersState.alt}")
        inputConnection ?: return KeyEventResult.NotHandled

        // Dismiss symbol picker on any key except Sym
        if (isSymPickerVisible && event.keyCode != KeyEvent.KEYCODE_SYM) {
            onSymPickerDismiss?.invoke()

            // Consume back key to prevent Android from also handling it
            if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                return KeyEventResult.Handled
            }
        }

        // Handle key repeats (repeatCount > 0)
        if (event.repeatCount > 0) {
            // Always allow backspace to repeat
            if (event.keyCode == KeyEvent.KEYCODE_DEL) {
                return KeyEventResult.NotHandled
            }

            // Handle accent cycling for letter keys if enabled
            if (currentSettings.longPressAccents && isLetterKey(event.keyCode) && accentKeyDownTime > 0) {
                val pressDuration = event.eventTime - accentKeyDownTime

                // Start cycling after the initial delay
                if (pressDuration >= ACCENT_START_DELAY_MS && accentCycleList.isNotEmpty()) {
                    // Calculate which accent to show based on press duration
                    val cyclesSinceStart = ((pressDuration - ACCENT_START_DELAY_MS) / ACCENT_CYCLE_INTERVAL_MS).toInt()
                    val newIndex = (cyclesSinceStart + 1) % accentCycleList.size  // +1 because we start at index 0

                    if (newIndex != accentCycleIndex) {
                        // Cycle to next accent
                        accentCycleIndex = newIndex

                        // Delete previous character and insert new one
                        inputConnection.deleteSurroundingText(1, 0)
                        inputConnection.commitText(accentCycleList[accentCycleIndex], 1)

                        Log.d(TAG, "Accent cycling: ${accentCycleList[accentCycleIndex]} (index $accentCycleIndex/${accentCycleList.size})")
                    }
                }
                return KeyEventResult.Handled
            }

            // Handle long-press capitalization for letter keys (legacy behavior)
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

        // Check if we should activate auto-cap shift before processing this key
        checkAndActivateAutoCapShift(inputConnection)

        // Handle modifier keys (Shift/Alt/Sym)
        when (event.keyCode) {
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> {
                shiftKeyDownTime = event.eventTime
                return KeyEventResult.Handled
            }
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {
                if (currentSettings.stickyAlt) {
                    altKeyDownTime = event.eventTime
                    return KeyEventResult.Handled
                }
            }
            KeyEvent.KEYCODE_SYM -> {
                symKeyDownTime = event.eventTime
                return KeyEventResult.Handled
            }
        }

        // Handle Alt+Backspace behavior
        if (event.keyCode == KeyEvent.KEYCODE_DEL && modifiersState.isAltActive()) {
            if (currentSettings.altBackspaceDeleteLine) {
                // Delete entire line before cursor (everything from last newline or start of text to cursor)
                val textBeforeCursor = inputConnection.getTextBeforeCursor(1000, 0)
                if (textBeforeCursor != null && textBeforeCursor.isNotEmpty()) {
                    val lastNewlineIndex = textBeforeCursor.lastIndexOf('\n')
                    val deleteCount = if (lastNewlineIndex >= 0) {
                        // Delete everything after the last newline
                        textBeforeCursor.length - lastNewlineIndex - 1
                    } else {
                        // No newline found, delete everything
                        textBeforeCursor.length
                    }

                    if (deleteCount > 0) {
                        inputConnection.deleteSurroundingText(deleteCount, 0)
                    }
                }

                // Clear one-shot modifiers after use
                clearOneShotModifiers()
                return KeyEventResult.Handled
            } else {
                // Setting is OFF: treat Alt+Backspace as regular backspace (ignore Alt modifier)
                // Clear one-shot modifiers and let the regular backspace handler process it
                clearOneShotModifiers()
                // Fall through to let system handle regular backspace (don't send Alt modifier)
                return KeyEventResult.NotHandled
            }
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

        // Handle Alt modifier - send key event with Alt meta state
        if (modifiersState.isAltActive()) {
            Log.d(TAG, "Sending key with Alt modifier: keyCode=${event.keyCode}")

            // Create key events with Alt modifier
            val downTime = event.downTime
            val eventTime = event.eventTime

            val altDownEvent = KeyEvent(
                downTime,
                eventTime,
                KeyEvent.ACTION_DOWN,
                event.keyCode,
                0, // repeat
                KeyEvent.META_ALT_ON or KeyEvent.META_ALT_LEFT_ON,
                event.deviceId,
                event.scanCode
            )

            val altUpEvent = KeyEvent(
                downTime,
                eventTime,
                KeyEvent.ACTION_UP,
                event.keyCode,
                0, // repeat
                KeyEvent.META_ALT_ON or KeyEvent.META_ALT_LEFT_ON,
                event.deviceId,
                event.scanCode
            )

            // Send the key events with Alt modifier
            inputConnection.sendKeyEvent(altDownEvent)
            inputConnection.sendKeyEvent(altUpEvent)

            // Clear one-shot modifiers after use
            clearOneShotModifiers()
            return KeyEventResult.Handled
        }

        // Handle letter keys with shift modifier
        if (isLetterKey(event.keyCode)) {
            val char = getCharForKeyCode(event.keyCode)
            if (char != null) {
                // Check if we should apply shift modifier
                if (modifiersState.isShiftActive()) {
                    // Commit the capitalized character
                    inputConnection.commitText(char.uppercase(), 1)

                    // Clear one-shot modifiers after use
                    clearOneShotModifiers()
                    return KeyEventResult.Handled
                }

                // Start tracking for accent cycling if enabled and no modifiers active
                if (currentSettings.longPressAccents && !modifiersState.isShiftActive() && !modifiersState.isAltActive()) {
                    val baseChar = if (modifiersState.shift == ModifierState.ONE_SHOT) char[0].uppercaseChar() else char[0]
                    val accentCycle = accentRepository.getAccentCycle(currentSettings.selectedLanguage, baseChar)

                    if (accentCycle.isNotEmpty()) {
                        // This letter has accents, track it for cycling
                        accentKeyDownTime = event.eventTime
                        accentBaseChar = baseChar
                        accentCycleList = accentCycle
                        accentCycleIndex = 0  // Start with base character
                    }
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
                if (shiftKeyDownTime > 0) {
                    val pressDuration = event.eventTime - shiftKeyDownTime
                    val isLongPress = pressDuration >= LONG_PRESS_THRESHOLD_MS

                    // Check for double-tap (only on short presses)
                    val isDoubleTap = if (!isLongPress) {
                        val timeSinceLastTap = event.eventTime - lastShiftTapTime
                        timeSinceLastTap < DOUBLE_TAP_THRESHOLD_MS
                    } else {
                        false
                    }

                    // Double-tap acts like long-press (locks modifier)
                    val shouldLock = isLongPress || isDoubleTap

                    if (shouldLock) {
                        Log.d(TAG, "Shift: ${if (isDoubleTap) "double-tap" else "long-press"} detected, locking")
                    }

                    toggleShiftModifier(shouldLock)
                    shiftKeyDownTime = 0L

                    // Update last tap time for double-tap detection (only for short presses)
                    if (!isLongPress) {
                        lastShiftTapTime = event.eventTime
                    }

                    return KeyEventResult.Handled
                }
            }
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {
                if (currentSettings.stickyAlt && altKeyDownTime > 0) {
                    val pressDuration = event.eventTime - altKeyDownTime
                    val isLongPress = pressDuration >= LONG_PRESS_THRESHOLD_MS

                    // Check for double-tap (only on short presses)
                    val isDoubleTap = if (!isLongPress) {
                        val timeSinceLastTap = event.eventTime - lastAltTapTime
                        timeSinceLastTap < DOUBLE_TAP_THRESHOLD_MS
                    } else {
                        false
                    }

                    // Double-tap acts like long-press (locks modifier)
                    val shouldLock = isLongPress || isDoubleTap

                    if (shouldLock) {
                        Log.d(TAG, "Alt: ${if (isDoubleTap) "double-tap" else "long-press"} detected, locking")
                    }

                    toggleAltModifier(shouldLock)
                    altKeyDownTime = 0L

                    // Update last tap time for double-tap detection (only for short presses)
                    if (!isLongPress) {
                        lastAltTapTime = event.eventTime
                    }

                    return KeyEventResult.Handled
                }
            }
            KeyEvent.KEYCODE_SYM -> {
                if (symKeyDownTime > 0) {
                    val pressDuration = event.eventTime - symKeyDownTime
                    val isLongPress = pressDuration >= LONG_PRESS_THRESHOLD_MS

                    // Check for double-tap (only on short presses)
                    val timeSinceLastTap = event.eventTime - lastSymTapTime
                    val isDoubleTap = !isLongPress && timeSinceLastTap < DOUBLE_TAP_THRESHOLD_MS

                    if (isDoubleTap || isLongPress) {
                        // Double-tap or long-press: insert preferred currency symbol
                        val currency = currentSettings.preferredCurrency
                            ?: com.titan2keyboard.util.LocaleUtils.getDefaultCurrencySymbol()
                        inputConnection.commitText(currency, 1)

                        // Dismiss the symbol picker if it's visible (from first tap)
                        if (isSymPickerVisible) {
                            onSymPickerDismiss?.invoke()
                        }

                        // Reset tap time to prevent triple-tap issues
                        lastSymTapTime = 0L
                    } else {
                        // Short press: show picker or cycle category
                        onSymKeyPressed?.invoke()
                        // Update last tap time for double-tap detection
                        lastSymTapTime = event.eventTime
                    }
                    symKeyDownTime = 0L
                    return KeyEventResult.Handled
                }
            }
        }

        // Reset accent tracking when letter key is released
        if (isLetterKey(event.keyCode) && accentKeyDownTime > 0) {
            accentKeyDownTime = 0L
            accentBaseChar = null
            accentCycleList = emptyList()
            accentCycleIndex = 0
            Log.d(TAG, "Accent tracking reset for keyCode=${event.keyCode}")
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
     * Check if auto-capitalization conditions are met, and if so, activate shift in ONE_SHOT mode
     */
    private fun checkAndActivateAutoCapShift(inputConnection: InputConnection) {
        if (!currentSettings.autoCapitalize) return

        // Don't override existing modifier states
        if (modifiersState.isShiftActive() || modifiersState.isAltActive()) return

        // Must have valid editor info
        val info = currentEditorInfo ?: return

        val inputType = info.inputType
        val typeClass = inputType and InputType.TYPE_MASK_CLASS
        val typeVariation = inputType and InputType.TYPE_MASK_VARIATION

        // Only auto-capitalize for text input types
        if (typeClass != InputType.TYPE_CLASS_TEXT) {
            return
        }

        // Don't auto-capitalize for specific text variations
        when (typeVariation) {
            InputType.TYPE_TEXT_VARIATION_URI,
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
            InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS,
            InputType.TYPE_TEXT_VARIATION_PASSWORD,
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
            InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD -> return
        }

        // Check for NO_SUGGESTIONS flag (used by our shortcut dialog)
        if (inputType and InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS != 0) {
            return
        }

        // Check text before cursor to determine if we should capitalize
        val textBeforeCursor = inputConnection.getTextBeforeCursor(100, 0)

        var shouldActivate = false

        if (textBeforeCursor.isNullOrEmpty()) {
            // Start of text, activate shift
            shouldActivate = true
        } else {
            // Get the last character
            val lastChar = textBeforeCursor.last()

            // Activate after newline
            if (lastChar == '\n') {
                shouldActivate = true
            }

            // Check for sentence-ending punctuation followed by space
            // This prevents auto-cap in URLs (www.github.com) and other period uses
            if (textBeforeCursor.length >= 2) {
                val secondToLast = textBeforeCursor[textBeforeCursor.length - 2]
                if (lastChar in listOf(' ', '\t') && secondToLast in listOf('.', '!', '?')) {
                    shouldActivate = true
                }
            }
        }

        if (shouldActivate) {
            // Activate shift in ONE_SHOT mode for auto-cap
            updateModifierState(ModifiersState(shift = ModifierState.ONE_SHOT, alt = ModifierState.NONE))
        }
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
        Log.d(TAG, "checkAndReplaceShortcut: lastWord='$lastWord'")
        if (lastWord.isEmpty()) return null

        // Check if this word has a shortcut replacement
        val replacement = shortcutRepository.findReplacement(lastWord)
        Log.d(TAG, "checkAndReplaceShortcut: lastWord='$lastWord' -> replacement='$replacement'")
        if (replacement == null) return null

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
        Log.d(TAG, "updateModifierState called: old=${modifiersState}, new=${newState}, caller=${Thread.currentThread().stackTrace[3].methodName}")
        modifiersState = newState
        modifierStateListener?.onModifierStateChanged(newState)
    }

    /**
     * Toggle shift modifier state
     * @param isLongPress true if this is a long press (locked), false for one-shot
     */
    private fun toggleShiftModifier(isLongPress: Boolean) {
        val newShiftState = when {
            // If already locked, turn off
            modifiersState.shift == ModifierState.LOCKED -> ModifierState.NONE
            // If already one-shot (from auto-cap or manual)
            modifiersState.shift == ModifierState.ONE_SHOT -> {
                if (isLongPress) {
                    // Long press upgrades to locked
                    ModifierState.LOCKED
                } else {
                    // Short press toggles off
                    ModifierState.NONE
                }
            }
            // Currently none
            else -> {
                if (isLongPress) {
                    // Long press = locked
                    ModifierState.LOCKED
                } else {
                    // Short press = one-shot (only if sticky shift enabled or will be handled by system)
                    if (currentSettings.stickyShift) ModifierState.ONE_SHOT else modifiersState.shift
                }
            }
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
            // If already locked, turn off
            modifiersState.alt == ModifierState.LOCKED -> ModifierState.NONE
            // If already one-shot
            modifiersState.alt == ModifierState.ONE_SHOT -> {
                if (isLongPress) {
                    // Long press upgrades to locked
                    ModifierState.LOCKED
                } else {
                    // Short press toggles off
                    ModifierState.NONE
                }
            }
            // Currently none
            else -> {
                if (isLongPress) {
                    // Long press = locked
                    ModifierState.LOCKED
                } else {
                    // Short press = one-shot
                    ModifierState.ONE_SHOT
                }
            }
        }

        // Clear shift if activating alt (mutual exclusivity)
        val newShiftState = if (newAltState != ModifierState.NONE) ModifierState.NONE else modifiersState.shift

        updateModifierState(ModifiersState(shift = newShiftState, alt = newAltState))
    }

    /**
     * Clear one-shot modifiers after use
     */
    private fun clearOneShotModifiers() {
        Log.d(TAG, "clearOneShotModifiers called: current modifiers=shift:${modifiersState.shift}/alt:${modifiersState.alt}")
        if (!modifiersState.hasOneShotModifier()) return

        val newShift = if (modifiersState.shift == ModifierState.ONE_SHOT) ModifierState.NONE else modifiersState.shift
        val newAlt = if (modifiersState.alt == ModifierState.ONE_SHOT) ModifierState.NONE else modifiersState.alt

        if (newShift != modifiersState.shift || newAlt != modifiersState.alt) {
            updateModifierState(ModifiersState(shift = newShift, alt = newAlt))
        }
    }
}
