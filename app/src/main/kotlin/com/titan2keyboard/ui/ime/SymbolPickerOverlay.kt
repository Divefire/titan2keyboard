package com.titan2keyboard.ui.ime

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.titan2keyboard.domain.model.Symbol
import com.titan2keyboard.domain.model.SymbolCategory
import com.titan2keyboard.domain.model.SymbolData

/**
 * BB OS 7-style centered symbol picker overlay.
 *
 * A compact, floating overlay that appears centered on screen with
 * a semi-transparent backdrop. Mimics the classic BlackBerry symbol
 * picker design.
 */
@Composable
fun SymbolPickerOverlay(
    visible: Boolean,
    currentCategory: SymbolCategory,
    onSymbolSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 0.9f),
        exit = fadeOut() + scaleOut(targetScale = 0.9f)
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            SymbolPickerCard(
                category = currentCategory,
                onSymbolSelected = onSymbolSelected,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* Consume clicks to prevent dismissal */ }
            )
        }
    }
}

@Composable
private fun SymbolPickerCard(
    category: SymbolCategory,
    onSymbolSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val symbols = remember(category) { SymbolData.getSymbolsForCategory(category) }

    Card(
        modifier = modifier
            .widthIn(max = 320.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Category header
            CategoryHeader(
                category = category,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Symbol grid - 6 columns for compact BB style
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(symbols) { symbol ->
                    SymbolButton(
                        symbol = symbol,
                        onClick = { onSymbolSelected(symbol.character) }
                    )
                }
            }

            // Hint text
            Text(
                text = "SYM to cycle categories",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun CategoryHeader(
    category: SymbolCategory,
    modifier: Modifier = Modifier
) {
    Text(
        text = category.displayName,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = modifier
    )
}

@Composable
private fun SymbolButton(
    symbol: Symbol,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .size(44.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF2A2A2A)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = symbol.character,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}

/**
 * Get the next category in sequence for cycling
 */
fun getNextCategory(current: SymbolCategory): SymbolCategory {
    val categories = SymbolData.categories
    val currentIndex = categories.indexOf(current)
    val nextIndex = (currentIndex + 1) % categories.size
    return categories[nextIndex]
}
