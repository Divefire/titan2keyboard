package com.titan2keyboard.ime

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.app.NotificationCompat
import com.titan2keyboard.R
import com.titan2keyboard.domain.model.KeyEventResult
import com.titan2keyboard.domain.model.ModifierState
import com.titan2keyboard.domain.model.ModifiersState
import com.titan2keyboard.domain.repository.SettingsRepository
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

    @Inject
    lateinit var symbolPickerViewModel: com.titan2keyboard.ui.symbolpicker.SymbolPickerViewModel

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Track whether we're in any input field to block capacitive touch
    private var isInputActive = false
    private var lastKeyEventTime = 0L

    // Notification for status bar indicator
    private lateinit var notificationManager: NotificationManager

    companion object {
        private const val TAG = "Titan2IME"
        private const val CAPACITIVE_BLOCK_TIME_MS = 1000L // Block capacitive for 1s after keystroke
        private const val NOTIFICATION_CHANNEL_ID = "modifier_keys_status_v2"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "IME Service created")

        // Set up notification manager and channel for status bar indicators
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()

        // Set up modifier state listener
        keyEventHandler.setModifierStateListener(this)

        // Set up Sym key callback to show/cycle symbol picker
        keyEventHandler.setSymKeyPressedCallback {
            handleSymKeyPressed()
        }

        // Observe settings changes
        serviceScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                Log.d(TAG, "Settings updated: $settings")
                Log.d(TAG, "  stickyShift=${settings.stickyShift}, stickyAlt=${settings.stickyAlt}")
                keyEventHandler.updateSettings(settings)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Modifier Keys Status",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Shows Shift/Alt key status in status bar"
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
                // Silent notification - no sound
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreateInputView(): View {
        // Return an empty view for input view since we use candidates view for the indicator
        return View(this)
    }

    override fun onCreateCandidatesView(): View? {
        // Create a Compose view for the symbol picker
        return androidx.compose.ui.platform.ComposeView(this).apply {
            setContent {
                com.titan2keyboard.ui.theme.Titan2KeyboardTheme {
                    com.titan2keyboard.ui.symbolpicker.SymbolPicker(
                        viewModel = symbolPickerViewModel,
                        onSymbolSelected = { symbol ->
                            handleSymbolSelected(symbol)
                        },
                        onDismiss = {
                            // Symbol picker dismissed
                        }
                    )
                }
            }
        }
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

        // Update status bar notification
        Log.d(TAG, "Calling updateStatusBarNotification")
        try {
            updateStatusBarNotification(modifiersState)
            Log.d(TAG, "updateStatusBarNotification completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error in updateStatusBarNotification", e)
        }
    }

    private fun updateStatusBarNotification(modifiersState: ModifiersState) {
        val shouldShow = modifiersState.isShiftActive() || modifiersState.isAltActive()
        Log.d(TAG, "updateStatusBarNotification: shouldShow=$shouldShow")

        if (shouldShow) {
            // Choose icon based on which modifiers are active
            val iconRes = when {
                modifiersState.isShiftActive() && modifiersState.isAltActive() -> R.drawable.ic_shift_alt
                modifiersState.isShiftActive() -> R.drawable.ic_shift
                modifiersState.isAltActive() -> R.drawable.ic_alt
                else -> R.drawable.ic_shift // Fallback
            }
            Log.d(TAG, "Using icon resource: $iconRes")

            // Build notification text based on active modifiers
            val notificationText = buildString {
                if (modifiersState.isShiftActive()) {
                    append("SHIFT")
                    if (modifiersState.shift == ModifierState.LOCKED) {
                        append(" 🔒")
                    }
                }
                if (modifiersState.isAltActive()) {
                    if (isNotEmpty()) append(" + ")
                    append("ALT")
                    if (modifiersState.alt == ModifierState.LOCKED) {
                        append(" 🔒")
                    }
                }
            }
            Log.d(TAG, "Notification text: $notificationText")

            try {
                val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(iconRes)
                    .setContentTitle("Modifier Keys Active")
                    .setContentText(notificationText)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOngoing(true)
                    .setShowWhen(false)
                    .build()

                notificationManager.notify(NOTIFICATION_ID, notification)
                Log.d(TAG, "Notification posted successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error posting notification", e)
            }
        } else {
            // Cancel notification when no modifiers are active
            Log.d(TAG, "Canceling notification")
            notificationManager.cancel(NOTIFICATION_ID)
        }
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

    /**
     * Handle Sym key press - show picker or cycle to next category
     */
    private fun handleSymKeyPressed() {
        Log.d(TAG, "Sym key pressed")
        symbolPickerViewModel.cycleToNextCategory()
    }

    /**
     * Handle symbol selection from picker
     */
    private fun handleSymbolSelected(symbol: String) {
        Log.d(TAG, "Symbol selected: $symbol")
        currentInputConnection?.commitText(symbol, 1)
    }

    override fun onDestroy() {
        Log.d(TAG, "IME Service destroyed")
        // Cancel any active notifications
        notificationManager.cancel(NOTIFICATION_ID)
        serviceScope.cancel()
        super.onDestroy()
    }
}
