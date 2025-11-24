package com.titan2.prototype

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import android.widget.LinearLayout
import android.graphics.Color
import android.view.Gravity

/**
 * Minimal settings activity for the prototype IME.
 * Shows instructions for enabling the IME and testing.
 */
class SettingsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(Color.WHITE)
        }

        val title = TextView(this).apply {
            text = "AOSP Prototype IME"
            textSize = 24f
            setTextColor(Color.BLACK)
            setPadding(0, 0, 0, 32)
        }

        val instructions = TextView(this).apply {
            text = """
                This is a minimal IME for testing AOSP features on the Titan 2.

                HOW TO ENABLE:
                1. Go to Settings → System → Languages & input → On-screen keyboard
                2. Tap "Manage on-screen keyboards"
                3. Enable "AOSP Prototype IME"
                4. In a text field, switch to this keyboard

                WHAT IT TESTS:
                • Physical key events and character mapping
                • Long-press detection for accents
                • Capacitive keyboard gesture/touch events
                • Motion events (trackpad behavior)
                • KeyCharacterMap functionality

                USING IT:
                • Open any text field (Messages, Notes, etc.)
                • Type on the physical keyboard
                • Watch the debug log at the bottom of the screen
                • Try long-pressing keys
                • Try swiping on the capacitive keyboard surface

                The IME will log all events it receives to help understand
                what's available for the full implementation.

                CHECK LOGCAT:
                adb logcat -s AospPrototype:D
            """.trimIndent()
            textSize = 14f
            setTextColor(Color.DKGRAY)
            setPadding(0, 0, 0, 16)
        }

        val note = TextView(this).apply {
            text = "Note: Disable Unihertz keyboard gestures in Settings → Gestures for best results"
            textSize = 12f
            setTextColor(Color.rgb(200, 100, 0))
            setPadding(16, 16, 16, 16)
            setBackgroundColor(Color.rgb(255, 243, 205))
        }

        layout.addView(title)
        layout.addView(instructions)
        layout.addView(note)

        setContentView(layout)
    }
}
