package com.titan2keyboard.ui.ime

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.titan2keyboard.domain.model.ModifierState
import com.titan2keyboard.domain.model.ModifiersState

/**
 * View that shows the current modifier state (Shift/Alt)
 * Uses regular Android Views instead of Compose for compatibility with InputMethodService
 */
class ModifierIndicatorView(context: Context) {

    private val rootLayout = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER
        val padding = (16 * context.resources.displayMetrics.density).toInt()
        val verticalPadding = (8 * context.resources.displayMetrics.density).toInt()
        setPadding(padding, verticalPadding, padding, verticalPadding)
        setBackgroundColor(Color.parseColor("#E3F2FD")) // Light blue background
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    private val shiftIndicator = createIndicatorTextView(context, "SHIFT")
    private val altIndicator = createIndicatorTextView(context, "ALT")

    init {
        rootLayout.addView(shiftIndicator)

        // Add spacing between indicators
        val spacerWidth = (16 * context.resources.displayMetrics.density).toInt()
        val spacer = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(spacerWidth, 1)
        }
        rootLayout.addView(spacer)

        rootLayout.addView(altIndicator)

        // Initially hide both indicators
        shiftIndicator.visibility = View.GONE
        altIndicator.visibility = View.GONE
    }

    fun getView(): View = rootLayout

    fun updateModifiers(newState: ModifiersState) {
        // Update shift indicator
        if (newState.isShiftActive()) {
            shiftIndicator.visibility = View.VISIBLE
            updateIndicatorStyle(shiftIndicator, newState.shift)
        } else {
            shiftIndicator.visibility = View.GONE
        }

        // Update alt indicator
        if (newState.isAltActive()) {
            altIndicator.visibility = View.VISIBLE
            updateIndicatorStyle(altIndicator, newState.alt)
        } else {
            altIndicator.visibility = View.GONE
        }
    }

    private fun createIndicatorTextView(context: Context, label: String): TextView {
        val horizontalPadding = (12 * context.resources.displayMetrics.density).toInt()
        val verticalPadding = (6 * context.resources.displayMetrics.density).toInt()

        return TextView(context).apply {
            text = label
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTypeface(null, Typeface.BOLD)
            setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun updateIndicatorStyle(textView: TextView, state: ModifierState) {
        when (state) {
            ModifierState.ONE_SHOT -> {
                // Purple background for one-shot
                textView.setBackgroundColor(Color.parseColor("#9C27B0"))
                textView.setTextColor(Color.WHITE)
                textView.text = textView.text.toString().replace(" 🔒", "")
            }
            ModifierState.LOCKED -> {
                // Deep purple background for locked
                textView.setBackgroundColor(Color.parseColor("#673AB7"))
                textView.setTextColor(Color.WHITE)
                if (!textView.text.toString().endsWith(" 🔒")) {
                    textView.text = "${textView.text} 🔒"
                }
            }
            ModifierState.NONE -> {
                // This shouldn't happen as we set visibility to GONE
                textView.setBackgroundColor(Color.TRANSPARENT)
                textView.setTextColor(Color.BLACK)
            }
        }
    }
}
