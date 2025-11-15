package com.titan2keyboard.ui.ime

import android.content.Context
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.titan2keyboard.domain.model.ModifierState
import com.titan2keyboard.domain.model.ModifiersState
import com.titan2keyboard.ui.theme.Titan2KeyboardTheme

/**
 * View that shows the current modifier state (Shift/Alt)
 */
class ModifierIndicatorView(context: Context) {

    private var modifiersState by mutableStateOf(ModifiersState())

    private val composeView = ComposeView(context).apply {
        setContent {
            Titan2KeyboardTheme {
                ModifierIndicator(modifiersState)
            }
        }
    }

    fun getView(): View = composeView

    fun updateModifiers(newState: ModifiersState) {
        modifiersState = newState
    }
}

@Composable
private fun ModifierIndicator(modifiersState: ModifiersState) {
    // Only show if at least one modifier is active
    if (!modifiersState.isShiftActive() && !modifiersState.isAltActive()) {
        return
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Shift indicator
            if (modifiersState.isShiftActive()) {
                ModifierChip(
                    label = "SHIFT",
                    state = modifiersState.shift
                )
            }

            // Alt indicator
            if (modifiersState.isAltActive()) {
                ModifierChip(
                    label = "ALT",
                    state = modifiersState.alt
                )
            }
        }
    }
}

@Composable
private fun ModifierChip(
    label: String,
    state: ModifierState
) {
    val backgroundColor = when (state) {
        ModifierState.ONE_SHOT -> MaterialTheme.colorScheme.secondary
        ModifierState.LOCKED -> MaterialTheme.colorScheme.tertiary
        else -> Color.Transparent
    }

    val textColor = when (state) {
        ModifierState.ONE_SHOT -> MaterialTheme.colorScheme.onSecondary
        ModifierState.LOCKED -> MaterialTheme.colorScheme.onTertiary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = textColor
            )
            if (state == ModifierState.LOCKED) {
                Text(
                    text = " 🔒",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
