package com.titan2keyboard.ime

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.titan2keyboard.R
import com.titan2keyboard.domain.model.KeyEventResult
import com.titan2keyboard.domain.model.ModifierState
import com.titan2keyboard.domain.model.ModifiersState
import com.titan2keyboard.domain.model.SymbolCategory
import com.titan2keyboard.domain.repository.SettingsRepository
import com.titan2keyboard.ui.ime.SymbolPickerOverlay
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

    // Track whether we're in any input field to block capacitive touch
    private var isInputActive = false
    private var lastKeyEventTime = 0L

    // Notification for status bar indicator
    private lateinit var notificationManager: NotificationManager

    // Symbol picker window management
    private var windowManager: WindowManager? = null
    private var symbolPickerView: ComposeView? = null
    private var isSymbolPickerShowing = false

    // Compose state for symbol picker
    private var symbolPickerVisible by mutableStateOf(false)
    private var symbolPickerCategory by mutableStateOf(SymbolCategory.PUNCTUATION)

    // Lifecycle owner for Compose in service
    private val lifecycleOwner = ServiceLifecycleOwner()

    companion object {
        private const val TAG = "Titan2IME"
        private const val CAPACITIVE_BLOCK_TIME_MS = 1000L // Block capacitive for 1s after keystroke
        private const val NOTIFICATION_CHANNEL_ID = "modifier_keys_status_v2"
        private const val NOTIFICATION_ID = 1001
    }

    /**
     * Custom lifecycle owner for running Compose in a Service
     */
    private class ServiceLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {
        private val lifecycleRegistry = LifecycleRegistry(this)
        private val savedStateRegistryController = SavedStateRegistryController.create(this)

        override val lifecycle: Lifecycle get() = lifecycleRegistry
        override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

        fun handleLifecycleEvent(event: Lifecycle.Event) {
            lifecycleRegistry.handleLifecycleEvent(event)
        }

        fun performRestore() {
            savedStateRegistryController.performRestore(null)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "IME Service created")

        // Initialize lifecycle for Compose
        lifecycleOwner.performRestore()
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        // Set up notification manager and channel for status bar indicators
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()

        // Initialize window manager for symbol picker overlay
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

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

        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
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
        // Reserve candidates view for other functions (autocomplete, suggestions, etc.)
        return null
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
        Log.d(TAG, "Modifier state changed: shift=${modifiersState.shift}, alt=${modifiersState.alt}, symPicker=${modifiersState.symPickerVisible}")

        // Update status bar notification
        Log.d(TAG, "Calling updateStatusBarNotification")
        try {
            updateStatusBarNotification(modifiersState)
            Log.d(TAG, "updateStatusBarNotification completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error in updateStatusBarNotification", e)
        }

        // Handle symbol picker visibility
        if (modifiersState.symPickerVisible != symbolPickerVisible) {
            symbolPickerVisible = modifiersState.symPickerVisible
            if (symbolPickerVisible) {
                showSymbolPicker()
            } else {
                hideSymbolPicker()
            }
        }

        // Update symbol picker category
        if (modifiersState.symCategory != symbolPickerCategory) {
            symbolPickerCategory = modifiersState.symCategory
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
     * Show the symbol picker overlay window
     */
    private fun showSymbolPicker() {
        if (isSymbolPickerShowing) return

        Log.d(TAG, "Showing symbol picker")

        try {
            // Create the ComposeView for the symbol picker
            val composeView = ComposeView(this).apply {
                setViewTreeLifecycleOwner(lifecycleOwner)
                setViewTreeSavedStateRegistryOwner(lifecycleOwner)

                setContent {
                    SymbolPickerOverlay(
                        visible = symbolPickerVisible,
                        currentCategory = symbolPickerCategory,
                        onSymbolSelected = { symbol ->
                            keyEventHandler.insertSymbol(symbol, currentInputConnection)
                        },
                        onDismiss = {
                            keyEventHandler.dismissSymbolPicker()
                        }
                    )
                }
            }

            // Window parameters for overlay
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.CENTER
                token = window?.window?.decorView?.windowToken
            }

            windowManager?.addView(composeView, params)
            symbolPickerView = composeView
            isSymbolPickerShowing = true

            Log.d(TAG, "Symbol picker shown successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing symbol picker", e)
        }
    }

    /**
     * Hide the symbol picker overlay window
     */
    private fun hideSymbolPicker() {
        if (!isSymbolPickerShowing) return

        Log.d(TAG, "Hiding symbol picker")

        try {
            symbolPickerView?.let { view ->
                windowManager?.removeView(view)
            }
            symbolPickerView = null
            isSymbolPickerShowing = false

            Log.d(TAG, "Symbol picker hidden successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error hiding symbol picker", e)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "IME Service destroyed")

        // Hide symbol picker if showing
        hideSymbolPicker()

        // Update lifecycle
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)

        // Cancel any active notifications
        notificationManager.cancel(NOTIFICATION_ID)
        serviceScope.cancel()
        super.onDestroy()
    }
}
