package com.titan2keyboard.ime

import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import com.titan2keyboard.domain.model.KeyEventResult
import com.titan2keyboard.domain.model.ModifiersState
import com.titan2keyboard.domain.repository.SettingsRepository
import com.titan2keyboard.ui.ime.ModifierIndicatorView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main Input Method Service for Titan2 Keyboard
 * Handles physical keyboard input events
 */
@AndroidEntryPoint
class Titan2InputMethodService : InputMethodService(), ModifierStateListener {

    @Inject
    lateinit var keyEventHandler: KeyEventHandler

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var modifierIndicatorView: ModifierIndicatorView? = null

    // Track whether we're in any input field to block capacitive touch
    private var isInputActive = false
    private var lastKeyEventTime = 0L

    companion object {
        private const val TAG = "Titan2IME"
        private const val CAPACITIVE_BLOCK_TIME_MS = 1000L // Block capacitive for 1s after keystroke
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "IME Service created")

        // Set up modifier state listener
        keyEventHandler.setModifierStateListener(this)

        // Observe settings changes
        serviceScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                Log.d(TAG, "Settings updated: $settings")
                Log.d(TAG, "  stickyShift=${settings.stickyShift}, stickyAlt=${settings.stickyAlt}")
                keyEventHandler.updateSettings(settings)
            }
        }
    }

    override fun onCreateInputView(): View {
        // Return an empty view for input view since we use candidates view for the indicator
        return View(this)
    }

    override fun onCreateCandidatesView(): View {
        // Create modifier indicator view in candidates area to avoid focus issues
        if (modifierIndicatorView == null) {
            modifierIndicatorView = ModifierIndicatorView(this)
        }
        return modifierIndicatorView!!.getView()
    }

    override fun onEvaluateInputViewShown(): Boolean {
        // Don't show input view for hardware keyboard
        return false
    }

    override fun onShowInputRequested(flags: Int, configChange: Boolean): Boolean {
        // Always return true to ensure we're active for hardware keyboard
        // This ensures key events come to us even when there's no soft keyboard shown
        return true
    }

    override fun onModifierStateChanged(modifiersState: ModifiersState) {
        Log.d(TAG, "Modifier state changed: shift=${modifiersState.shift}, alt=${modifiersState.alt}")

        // Update the modifier indicator view
        modifierIndicatorView?.updateModifiers(modifiersState)

        // Show/hide the candidates view based on modifier state
        val shouldShow = modifiersState.isShiftActive() || modifiersState.isAltActive()
        Log.d(TAG, "shouldShow=$shouldShow")

        // Use candidates view to avoid focus loss issues
        setCandidatesViewShown(shouldShow)
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Log.d(TAG, "Input started - inputType: ${attribute?.inputType}, " +
                "packageName: ${attribute?.packageName}, " +
                "fieldId: ${attribute?.fieldId}, " +
                "restarting: $restarting")

        // Log input type details for debugging
        attribute?.let { info ->
            val typeClass = info.inputType and android.text.InputType.TYPE_MASK_CLASS
            val typeVariation = info.inputType and android.text.InputType.TYPE_MASK_VARIATION
            val hasNoSuggestions = (info.inputType and android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS) != 0
            val hasAutoCorrectionDisabled = (info.inputType and android.text.InputType.TYPE_TEXT_FLAG_AUTO_CORRECT) == 0
            Log.d(TAG, "Input type - class: $typeClass, variation: $typeVariation, " +
                    "noSuggestions: $hasNoSuggestions, autoCorrectionDisabled: $hasAutoCorrectionDisabled")
        }

        // Block capacitive touch for ANY input field
        isInputActive = attribute != null
        Log.d(TAG, "isInputActive: $isInputActive")

        // Update the key event handler with current editor info
        keyEventHandler.updateEditorInfo(attribute)

        // Check if we should activate auto-cap shift at start of input
        keyEventHandler.onInputStarted(currentInputConnection)
    }

    override fun onFinishInput() {
        super.onFinishInput()
        Log.d(TAG, "Input finished")
        isInputActive = false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        event ?: return super.onKeyDown(keyCode, event)

        // Track key event time for capacitive touch blocking
        lastKeyEventTime = System.currentTimeMillis()

        val result = keyEventHandler.handleKeyDown(event, currentInputConnection)
        return when (result) {
            KeyEventResult.Handled -> {
                Log.d(TAG, "Key handled: $keyCode")
                true
            }
            KeyEventResult.NotHandled -> {
                super.onKeyDown(keyCode, event)
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        event ?: return super.onKeyUp(keyCode, event)

        val result = keyEventHandler.handleKeyUp(event, currentInputConnection)
        return when (result) {
            KeyEventResult.Handled -> true
            KeyEventResult.NotHandled -> super.onKeyUp(keyCode, event)
        }
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        // Block capacitive touch events (trackpad/scroll gestures) when any input is active
        if (isInputActive) {
            val timeSinceLastKey = System.currentTimeMillis() - lastKeyEventTime

            // Block if we recently typed (within 1 second)
            if (lastKeyEventTime > 0 && timeSinceLastKey < CAPACITIVE_BLOCK_TIME_MS) {
                Log.d(TAG, "Blocking capacitive touch event (${timeSinceLastKey}ms since last key)")
                return true // Consume the event
            }

            // Also block all capacitive touch while any input field is active
            Log.d(TAG, "Blocking capacitive touch event (input active)")
            return true // Consume the event
        }

        // No input active, allow capacitive touch
        return super.onGenericMotionEvent(event)
    }

    override fun onDestroy() {
        Log.d(TAG, "IME Service destroyed")
        serviceScope.cancel()
        super.onDestroy()
    }
}
