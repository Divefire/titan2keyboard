package com.titan2keyboard.ui.symbolpicker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.titan2keyboard.data.SymbolCategoryItem

/**
 * Symbol picker bottom sheet
 * Shows a grid of symbols organized by category
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolPicker(
    viewModel: SymbolPickerViewModel,
    onSymbolSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isVisible by viewModel.isVisible.collectAsStateWithLifecycle()
    val currentCategory by viewModel.currentCategory.collectAsStateWithLifecycle()

    if (isVisible && currentCategory != null) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.hide()
                onDismiss()
            },
            modifier = modifier
        ) {
            SymbolPickerContent(
                category = currentCategory!!,
                categoryIndex = viewModel.getCurrentCategoryIndex(),
                totalCategories = viewModel.getTotalCategories(),
                onSymbolClick = { symbol ->
                    onSymbolSelected(symbol)
                    viewModel.hide()
                }
            )
        }
    }
}

/**
 * Content of the symbol picker
 */
@Composable
private fun SymbolPickerContent(
    category: SymbolCategoryItem,
    categoryIndex: Int,
    totalCategories: Int,
    onSymbolClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        // Header with category name and counter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${categoryIndex + 1}/$totalCategories",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Hint text
        Text(
            text = "Tap a symbol to insert it, or press Sym again for next category",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Symbol grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.heightIn(max = 300.dp)
        ) {
            items(category.symbols) { symbol ->
                SymbolButton(
                    symbol = symbol,
                    onClick = { onSymbolClick(symbol) }
                )
            }
        }

        // Bottom spacing for gesture bar
        Spacer(modifier = Modifier.height(8.dp))
    }
}

/**
 * Individual symbol button
 */
@Composable
private fun SymbolButton(
    symbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f)
            .size(56.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = symbol,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
    }
}
