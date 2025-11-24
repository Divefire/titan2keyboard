package com.titan2.prototype

import android.graphics.Color
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.KeyCharacterMap
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Minimal AOSP Prototype IME for testing:
 * - Key event handling and character mapping
 * - Long-press detection
 * - Gesture/motion event capture
 * - Capacitive keyboard trackpad testing
 *
 * This prototype logs everything to help understand what events are available
 * and what AOSP provides out of the box.
 */
class AospPrototypeIME : InputMethodService() {

    private val testLog = mutableListOf<String>()
    private lateinit var logTextView: TextView
    private var longPressStartTime = 0L
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)

    companion object {
        private const val TAG = "AospPrototype"
        private const val LONG_PRESS_THRESHOLD = 500L // ms
        private const val MAX_LOG_LINES = 100
    }

    override fun onCreate() {
        super.onCreate()
        log("═══ IME Created ═══")
        log("Device: ${android.os.Build.MODEL}")
        log("Android: ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})")
    }

    override fun onCreateInputView(): View {
        log("Creating input view")

        // Create a simple debug view showing the log
        logTextView = TextView(this).apply {
            text = "AOSP Prototype IME\nWaiting for events...\n\n"
            setTextColor(Color.BLACK)
            setBackgroundColor(Color.WHITE)
            setPadding(16, 16, 16, 16)
            textSize = 10f
            typeface = android.graphics.Typeface.MONOSPACE
        }

        return ScrollView(this).apply {
            setBackgroundColor(Color.LTGRAY)
            addView(logTextView)
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        log("─── Input Started ───")
        log("Editor: ${attribute?.let { getEditorInfo(it) } ?: "null"}")
        log("Restarting: $restarting")
    }

    override fun onFinishInput() {
        log("─── Input Finished ───")
        super.onFinishInput()
    }

    // ═══ KEY EVENT TESTING ═══

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event == null) return super.onKeyDown(keyCode, event)

        longPressStartTime = event.eventTime

        // Log comprehensive key information
        val keyInfo = buildString {
            append("↓ KEY DOWN: ")
            append("code=$keyCode")
            append(" char='${event.displayLabel}'")
            append(" unicode=${event.unicodeChar}")

            // Modifiers
            val mods = mutableListOf<String>()
            if (event.isShiftPressed) mods.add("SHIFT")
            if (event.isAltPressed) mods.add("ALT")
            if (event.isCtrlPressed) mods.add("CTRL")
            if (event.isSymPressed) mods.add("SYM")
            if (event.isFunctionPressed) mods.add("FN")
            if (mods.isNotEmpty()) append(" [${mods.joinToString("+")}]")

            append(" meta=0x${event.metaState.toString(16)}")
        }
        log(keyInfo)

        // Test KeyCharacterMap
        testKeyCharacterMap(keyCode, event)

        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (event == null) return super.onKeyUp(keyCode, event)

        val duration = event.eventTime - longPressStartTime
        val wasLongPress = duration >= LONG_PRESS_THRESHOLD

        log("↑ KEY UP: code=$keyCode duration=${duration}ms${if (wasLongPress) " [LONG]" else ""}")

        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        log("⏱ LONG PRESS DETECTED: code=$keyCode")

        // TODO: Test showing AOSP accent popup here
        // This is where we'd trigger the accent popup system

        return super.onKeyLongPress(keyCode, event)
    }

    private fun testKeyCharacterMap(keyCode: Int, event: KeyEvent) {
        try {
            val charMap = KeyCharacterMap.load(event.deviceId)

            // Test different modifier combinations
            val baseChar = charMap.get(keyCode, 0)
            val shiftChar = charMap.get(keyCode, KeyEvent.META_SHIFT_ON)
            val altChar = charMap.get(keyCode, KeyEvent.META_ALT_ON)
            val symChar = charMap.get(keyCode, KeyEvent.META_SYM_ON)

            log("  CharMap: base='${baseChar.toCharOrNull()}' shift='${shiftChar.toCharOrNull()}' alt='${altChar.toCharOrNull()}' sym='${symChar.toCharOrNull()}'")

            // Check for popup characters (accents)
            val keyData = android.view.KeyCharacterMap.KeyData()
            if (charMap.getKeyData(keyCode, keyData)) {
                val hasPopup = keyData.meta.any { it.toInt() != 0 && it != keyData.displayLabel }
                if (hasPopup) {
                    log("  Popup chars available: ${keyData.meta.filter { it.toInt() != 0 }.joinToString("")}")
                }
            }
        } catch (e: Exception) {
            log("  CharMap error: ${e.message}")
        }
    }

    private fun Int.toCharOrNull(): Char? {
        return if (this > 0 && this < 65536) this.toChar() else null
    }

    // ═══ MOTION EVENT TESTING (TRACKPAD/GESTURE) ═══

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        val sourceStr = getSourceString(event.source)
        val actionStr = getActionString(event.actionMasked)

        log("◉ GENERIC MOTION: source=$sourceStr action=$actionStr pointers=${event.pointerCount} x=${event.x.toInt()} y=${event.y.toInt()}")

        // Log additional details for trackpad events
        if (event.source and android.view.InputDevice.SOURCE_TOUCHPAD != 0 ||
            event.source and android.view.InputDevice.SOURCE_TOUCHSCREEN != 0) {
            logTrackpadDetails(event)
        }

        return super.onGenericMotionEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val actionStr = getActionString(event.actionMasked)
        log("👆 TOUCH: action=$actionStr pointers=${event.pointerCount} x=${event.x.toInt()} y=${event.y.toInt()}")

        if (event.pointerCount > 1) {
            log("  Multi-finger: ${event.pointerCount} fingers")
            for (i in 0 until event.pointerCount) {
                log("    Finger $i: x=${event.getX(i).toInt()} y=${event.getY(i).toInt()}")
            }
        }

        return super.onTouchEvent(event)
    }

    private fun logTrackpadDetails(event: MotionEvent) {
        // Log scroll axes if available
        val vScroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL)
        val hScroll = event.getAxisValue(MotionEvent.AXIS_HSCROLL)
        if (vScroll != 0f || hScroll != 0f) {
            log("  Scroll: v=$vScroll h=$hScroll")
        }

        // Log button state
        if (event.buttonState != 0) {
            log("  Buttons: 0x${event.buttonState.toString(16)}")
        }

        // Log pressure/size for multi-touch
        if (event.pointerCount > 1) {
            val pressures = (0 until event.pointerCount).map { event.getPressure(it) }
            log("  Pressures: ${pressures.joinToString(", ") { "%.2f".format(it) }}")
        }
    }

    // ═══ TEXT COMMIT TESTING ═══

    override fun onDisplayCompletions(completions: Array<out android.view.inputmethod.CompletionInfo>?) {
        log("📝 Completions requested: ${completions?.size ?: 0}")
    }

    // ═══ UTILITY FUNCTIONS ═══

    private fun log(message: String) {
        val timestamp = dateFormat.format(Date())
        val logMessage = "[$timestamp] $message"

        Log.d(TAG, message)
        testLog.add(logMessage)

        // Keep log size manageable
        if (testLog.size > MAX_LOG_LINES) {
            testLog.removeAt(0)
        }

        // Update UI if available
        if (::logTextView.isInitialized) {
            logTextView.post {
                logTextView.text = testLog.joinToString("\n")
                // Scroll to bottom
                (logTextView.parent as? ScrollView)?.fullScroll(View.FOCUS_DOWN)
            }
        }
    }

    private fun getEditorInfo(info: EditorInfo): String {
        val type = when (info.inputType and android.text.InputType.TYPE_MASK_CLASS) {
            android.text.InputType.TYPE_CLASS_TEXT -> "TEXT"
            android.text.InputType.TYPE_CLASS_NUMBER -> "NUMBER"
            android.text.InputType.TYPE_CLASS_PHONE -> "PHONE"
            android.text.InputType.TYPE_CLASS_DATETIME -> "DATETIME"
            else -> "OTHER(${info.inputType})"
        }
        return "$type hint='${info.hintText}' label='${info.label}'"
    }

    private fun getSourceString(source: Int): String {
        val sources = mutableListOf<String>()
        if (source and android.view.InputDevice.SOURCE_KEYBOARD != 0) sources.add("KEYBOARD")
        if (source and android.view.InputDevice.SOURCE_TOUCHSCREEN != 0) sources.add("TOUCHSCREEN")
        if (source and android.view.InputDevice.SOURCE_TOUCHPAD != 0) sources.add("TOUCHPAD")
        if (source and android.view.InputDevice.SOURCE_MOUSE != 0) sources.add("MOUSE")
        if (source and android.view.InputDevice.SOURCE_STYLUS != 0) sources.add("STYLUS")
        return if (sources.isEmpty()) "UNKNOWN(0x${source.toString(16)})" else sources.joinToString("|")
    }

    private fun getActionString(action: Int): String {
        return when (action) {
            MotionEvent.ACTION_DOWN -> "DOWN"
            MotionEvent.ACTION_UP -> "UP"
            MotionEvent.ACTION_MOVE -> "MOVE"
            MotionEvent.ACTION_CANCEL -> "CANCEL"
            MotionEvent.ACTION_OUTSIDE -> "OUTSIDE"
            MotionEvent.ACTION_POINTER_DOWN -> "POINTER_DOWN"
            MotionEvent.ACTION_POINTER_UP -> "POINTER_UP"
            MotionEvent.ACTION_HOVER_MOVE -> "HOVER"
            MotionEvent.ACTION_SCROLL -> "SCROLL"
            else -> "UNKNOWN($action)"
        }
    }

    override fun onDestroy() {
        log("═══ IME Destroyed ═══")
        super.onDestroy()
    }
}
