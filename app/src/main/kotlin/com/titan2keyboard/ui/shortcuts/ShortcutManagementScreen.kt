package com.titan2keyboard.ui.shortcuts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.titan2keyboard.R
import com.titan2keyboard.domain.model.TextShortcut

/**
 * Screen for managing text shortcuts
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortcutManagementScreen(
    onNavigateBack: () -> Unit,
    viewModel: ShortcutManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingShortcut by remember { mutableStateOf<TextShortcut?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.shortcuts_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.navigate_back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_shortcut))
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is ShortcutManagementUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ShortcutManagementUiState.Success -> {
                ShortcutsList(
                    shortcuts = state.shortcuts,
                    onEditShortcut = { editingShortcut = it },
                    onDeleteShortcut = { viewModel.deleteShortcut(it.id) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }

    // Add/Edit Dialog
    if (showAddDialog) {
        ShortcutEditDialog(
            shortcut = null,
            onDismiss = { showAddDialog = false },
            onSave = { trigger, replacement, caseSensitive ->
                viewModel.addShortcut(trigger, replacement, caseSensitive)
                showAddDialog = false
            }
        )
    }

    if (editingShortcut != null) {
        ShortcutEditDialog(
            shortcut = editingShortcut,
            onDismiss = { editingShortcut = null },
            onSave = { trigger, replacement, caseSensitive ->
                editingShortcut?.let { shortcut ->
                    viewModel.updateShortcut(
                        shortcut.copy(
                            trigger = trigger,
                            replacement = replacement,
                            caseSensitive = caseSensitive
                        )
                    )
                }
                editingShortcut = null
            }
        )
    }
}

/**
 * List of shortcuts
 */
@Composable
private fun ShortcutsList(
    shortcuts: List<TextShortcut>,
    onEditShortcut: (TextShortcut) -> Unit,
    onDeleteShortcut: (TextShortcut) -> Unit,
    modifier: Modifier = Modifier
) {
    if (shortcuts.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_shortcuts),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(shortcuts, key = { it.id }) { shortcut ->
                ShortcutItem(
                    shortcut = shortcut,
                    onEdit = { onEditShortcut(shortcut) },
                    onDelete = { onDeleteShortcut(shortcut) }
                )
            }
        }
    }
}

/**
 * Individual shortcut item
 */
@Composable
private fun ShortcutItem(
    shortcut: TextShortcut,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = shortcut.trigger,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "→",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = shortcut.replacement,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                if (shortcut.caseSensitive) {
                    Text(
                        text = stringResource(R.string.case_sensitive),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (shortcut.isDefault) {
                    Text(
                        text = stringResource(R.string.default_shortcut),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_shortcut)
                    )
                }
                if (!shortcut.isDefault) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_shortcut),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dialog for adding or editing a shortcut
 */
@Composable
private fun ShortcutEditDialog(
    shortcut: TextShortcut?,
    onDismiss: () -> Unit,
    onSave: (trigger: String, replacement: String, caseSensitive: Boolean) -> Unit
) {
    var trigger by remember { mutableStateOf(shortcut?.trigger ?: "") }
    var replacement by remember { mutableStateOf(shortcut?.replacement ?: "") }
    var caseSensitive by remember { mutableStateOf(shortcut?.caseSensitive ?: false) }

    val isEdit = shortcut != null
    val canSave = trigger.isNotBlank() && replacement.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (isEdit) stringResource(R.string.edit_shortcut)
                else stringResource(R.string.add_shortcut)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = trigger,
                    onValueChange = { trigger = it },
                    label = { Text(stringResource(R.string.trigger_text)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = replacement,
                    onValueChange = { replacement = it },
                    label = { Text(stringResource(R.string.replacement_text)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = caseSensitive,
                        onCheckedChange = { caseSensitive = it }
                    )
                    Text(
                        text = stringResource(R.string.case_sensitive),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (canSave) {
                        onSave(trigger, replacement, caseSensitive)
                    }
                },
                enabled = canSave
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
