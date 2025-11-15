package com.titan2keyboard.ui.ime

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.titan2keyboard.R
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
        setBackgroundColor(Color.parseColor("#1A237E")) // Dark blue background
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    private val shiftIndicator = createIndicatorImageView(context, R.drawable.ic_shift)
    private val altIndicator = createIndicatorImageView(context, R.drawable.ic_alt)

    init {
        rootLayout.addView(shiftIndicator)

        // Add spacing between indicators
        val spacerWidth = (24 * context.resources.displayMetrics.density).toInt()
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
            updateIndicatorImageStyle(shiftIndicator, newState.shift)
        } else {
            shiftIndicator.visibility = View.GONE
        }

        // Update alt indicator
        if (newState.isAltActive()) {
            altIndicator.visibility = View.VISIBLE
            updateIndicatorImageStyle(altIndicator, newState.alt)
        } else {
            altIndicator.visibility = View.GONE
        }
    }

    private fun createIndicatorImageView(context: Context, iconRes: Int): ImageView {
        val padding = (12 * context.resources.displayMetrics.density).toInt()
        val iconSize = (32 * context.resources.displayMetrics.density).toInt()

        return ImageView(context).apply {
            setImageResource(iconRes)
            setPadding(padding, padding, padding, padding)
            layoutParams = LinearLayout.LayoutParams(iconSize + padding * 2, iconSize + padding * 2)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
    }

    private fun updateIndicatorImageStyle(imageView: ImageView, state: ModifierState) {
        when (state) {
            ModifierState.ONE_SHOT -> {
                // Purple background with white icon for one-shot
                imageView.setBackgroundColor(Color.parseColor("#9C27B0"))
                imageView.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            }
            ModifierState.LOCKED -> {
                // Deep purple background with white icon for locked
                imageView.setBackgroundColor(Color.parseColor("#673AB7"))
                imageView.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            }
            ModifierState.NONE -> {
                // This shouldn't happen as we set visibility to GONE
                imageView.setBackgroundColor(Color.TRANSPARENT)
                imageView.clearColorFilter()
            }
        }
    }
}
