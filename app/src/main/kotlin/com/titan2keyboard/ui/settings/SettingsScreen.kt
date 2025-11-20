package com.titan2keyboard.ui.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.titan2keyboard.R
import com.titan2keyboard.util.LocaleUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToShortcuts: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsState by viewModel.settingsState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.settings_title)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Enable IME Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.enable_ime_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = stringResource(R.string.enable_ime_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(stringResource(R.string.enable_ime_button))
                    }
                }
            }

            when (val state = settingsState) {
                is SettingsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is SettingsUiState.Success -> {
                    SettingsContent(
                        settings = state.settings,
                        onAutoCapitalizeChanged = viewModel::updateAutoCapitalize,
                        onKeyRepeatChanged = viewModel::updateKeyRepeat,
                        onLongPressCapitalizeChanged = viewModel::updateLongPressCapitalize,
                        onDoubleSpacePeriodChanged = viewModel::updateDoubleSpacePeriod,
                        onTextShortcutsChanged = viewModel::updateTextShortcuts,
                        onStickyShiftChanged = viewModel::updateStickyShift,
                        onStickyAltChanged = viewModel::updateStickyAlt,
                        onAltBackspaceBehaviorChanged = viewModel::updateAltBackspaceBehavior,
                        onPreferredCurrencyChanged = viewModel::updatePreferredCurrency,
                        onSelectedLanguageChanged = viewModel::updateSelectedLanguage,
                        onLongPressAccentsChanged = viewModel::updateLongPressAccents,
                        onManageShortcuts = onNavigateToShortcuts,
                        onNavigateToAbout = onNavigateToAbout,
                        onResetToDefaults = viewModel::resetToDefaults
                    )
                }
                is SettingsUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsContent(
    settings: com.titan2keyboard.domain.model.KeyboardSettings,
    onAutoCapitalizeChanged: (Boolean) -> Unit,
    onKeyRepeatChanged: (Boolean) -> Unit,
    onLongPressCapitalizeChanged: (Boolean) -> Unit,
    onDoubleSpacePeriodChanged: (Boolean) -> Unit,
    onTextShortcutsChanged: (Boolean) -> Unit,
    onStickyShiftChanged: (Boolean) -> Unit,
    onStickyAltChanged: (Boolean) -> Unit,
    onAltBackspaceBehaviorChanged: (com.titan2keyboard.domain.model.AltBackspaceBehavior) -> Unit,
    onPreferredCurrencyChanged: (String?) -> Unit,
    onSelectedLanguageChanged: (String) -> Unit,
    onLongPressAccentsChanged: (Boolean) -> Unit,
    onManageShortcuts: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onResetToDefaults: () -> Unit
) {
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAltBackspaceDialog by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_general),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        SettingItem(
            title = stringResource(R.string.setting_auto_capitalize),
            description = stringResource(R.string.setting_auto_capitalize_desc),
            checked = settings.autoCapitalize,
            onCheckedChange = onAutoCapitalizeChanged
        )

        SettingItem(
            title = stringResource(R.string.setting_long_press_capitalize),
            description = stringResource(R.string.setting_long_press_capitalize_desc),
            checked = settings.longPressCapitalize,
            onCheckedChange = onLongPressCapitalizeChanged
        )

        SettingItem(
            title = stringResource(R.string.setting_double_space_period),
            description = stringResource(R.string.setting_double_space_period_desc),
            checked = settings.doubleSpacePeriod,
            onCheckedChange = onDoubleSpacePeriodChanged
        )

        SettingItem(
            title = stringResource(R.string.setting_text_shortcuts),
            description = stringResource(R.string.setting_text_shortcuts_desc),
            checked = settings.textShortcutsEnabled,
            onCheckedChange = onTextShortcutsChanged
        )

        // Currency Symbol Preference
        CurrencyPreferenceItem(
            currentCurrency = settings.preferredCurrency,
            onClick = { showCurrencyDialog = true }
        )

        // Language Selection
        LanguagePreferenceItem(
            currentLanguage = settings.selectedLanguage,
            onClick = { showLanguageDialog = true }
        )

        // Long-press accent behavior
        SettingItem(
            title = "Long-press for accents",
            description = "Long-press letter keys to cycle through accent variants instead of uppercase (e.g., e → é → è → ê)",
            checked = settings.longPressAccents,
            onCheckedChange = onLongPressAccentsChanged
        )

        // Manage Shortcuts Button
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = onManageShortcuts,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(stringResource(R.string.manage_shortcuts))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.settings_advanced),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        SettingItem(
            title = stringResource(R.string.setting_key_repeat),
            description = stringResource(R.string.setting_key_repeat_desc),
            checked = settings.keyRepeatEnabled,
            onCheckedChange = onKeyRepeatChanged
        )

        SettingItem(
            title = stringResource(R.string.setting_sticky_shift),
            description = stringResource(R.string.setting_sticky_shift_desc),
            checked = settings.stickyShift,
            onCheckedChange = onStickyShiftChanged
        )

        SettingItem(
            title = stringResource(R.string.setting_sticky_alt),
            description = stringResource(R.string.setting_sticky_alt_desc),
            checked = settings.stickyAlt,
            onCheckedChange = onStickyAltChanged
        )

        // Alt+Backspace behavior selection
        AltBackspaceBehaviorItem(
            currentBehavior = settings.altBackspaceBehavior,
            onClick = { showAltBackspaceDialog = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // About Button
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = onNavigateToAbout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(stringResource(R.string.about_title))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onResetToDefaults,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Reset to Defaults")
        }
    }

    // Currency selection dialog
    if (showCurrencyDialog) {
        CurrencySelectionDialog(
            currentCurrency = settings.preferredCurrency,
            onCurrencySelected = { currency ->
                onPreferredCurrencyChanged(currency)
                showCurrencyDialog = false
            },
            onDismiss = { showCurrencyDialog = false }
        )
    }

    // Language selection dialog
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = settings.selectedLanguage,
            onLanguageSelected = { language ->
                onSelectedLanguageChanged(language)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    // Alt+Backspace behavior selection dialog
    if (showAltBackspaceDialog) {
        AltBackspaceBehaviorDialog(
            currentBehavior = settings.altBackspaceBehavior,
            onBehaviorSelected = { behavior ->
                onAltBackspaceBehaviorChanged(behavior)
                showAltBackspaceDialog = false
            },
            onDismiss = { showAltBackspaceDialog = false }
        )
    }
}

@Composable
private fun SettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
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
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun CurrencyPreferenceItem(
    currentCurrency: String?,
    onClick: () -> Unit
) {
    val displayText = currentCurrency?.let { "$it" }
        ?: "Auto (${LocaleUtils.getDefaultCurrencySymbol()})"

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Preferred Currency Symbol",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Currency shown in Sym key picker (Common category)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun LanguagePreferenceItem(
    currentLanguage: String,
    onClick: () -> Unit
) {
    // Get display name for current language using AccentRepository
    val displayName = com.titan2keyboard.data.AccentRepository().getSupportedLanguages()
        .find { it.first == currentLanguage }?.second
        ?: "English (no accents)"

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Language for accents",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Determines which accent variants appear on long-press",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = displayName.substringBefore(" ("),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun LanguageSelectionDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Language for Accents") },
        text = {
            LazyColumn {
                items(com.titan2keyboard.data.AccentRepository().getSupportedLanguages()) { (code, displayName) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(code) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = displayName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        if (currentLanguage == code) {
                            Text("✓", style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun CurrencySelectionDialog(
    currentCurrency: String?,
    onCurrencySelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Currency Symbol") },
        text = {
            LazyColumn {
                // Auto option (null = use locale default)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCurrencySelected(null) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Auto (based on locale)",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Currently: ${LocaleUtils.getDefaultCurrencySymbol()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (currentCurrency == null) {
                            Text("✓", style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                }

                // Manual currency options
                items(LocaleUtils.getAllCurrencySymbols()) { (symbol, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCurrencySelected(symbol) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (currentCurrency == symbol) {
                            Text("✓", style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun AltBackspaceBehaviorItem(
    currentBehavior: com.titan2keyboard.domain.model.AltBackspaceBehavior,
    onClick: () -> Unit
) {
    val displayName = when (currentBehavior) {
        com.titan2keyboard.domain.model.AltBackspaceBehavior.REGULAR_BACKSPACE -> "Regular Backspace"
        com.titan2keyboard.domain.model.AltBackspaceBehavior.DELETE_LINE -> "Delete Line"
        com.titan2keyboard.domain.model.AltBackspaceBehavior.DELETE_FORWARD -> "Delete Forward"
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Alt+Backspace Behavior",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "What happens when you press Alt+Backspace",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = displayName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun AltBackspaceBehaviorDialog(
    currentBehavior: com.titan2keyboard.domain.model.AltBackspaceBehavior,
    onBehaviorSelected: (com.titan2keyboard.domain.model.AltBackspaceBehavior) -> Unit,
    onDismiss: () -> Unit
) {
    val behaviors = listOf(
        com.titan2keyboard.domain.model.AltBackspaceBehavior.REGULAR_BACKSPACE to Pair(
            "Regular Backspace",
            "Alt is ignored, deletes character before cursor"
        ),
        com.titan2keyboard.domain.model.AltBackspaceBehavior.DELETE_LINE to Pair(
            "Delete Line",
            "Deletes entire line before cursor"
        ),
        com.titan2keyboard.domain.model.AltBackspaceBehavior.DELETE_FORWARD to Pair(
            "Delete Forward",
            "Deletes character after cursor"
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Alt+Backspace Behavior")
        },
        text = {
            Column {
                behaviors.forEach { (behavior, labels) ->
                    val (title, description) = labels
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBehaviorSelected(behavior) }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (currentBehavior == behavior) {
                            Text("✓", style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
