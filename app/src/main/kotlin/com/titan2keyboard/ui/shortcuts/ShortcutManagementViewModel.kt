package com.titan2keyboard.ui.shortcuts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.titan2keyboard.data.AccentRepository
import com.titan2keyboard.domain.model.TextShortcut
import com.titan2keyboard.domain.repository.SettingsRepository
import com.titan2keyboard.domain.repository.ShortcutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for managing text shortcuts
 */
@HiltViewModel
class ShortcutManagementViewModel @Inject constructor(
    private val shortcutRepository: ShortcutRepository,
    private val settingsRepository: SettingsRepository,
    private val accentRepository: AccentRepository
) : ViewModel() {

    /**
     * UI state for the shortcuts screen
     */
    val uiState: StateFlow<ShortcutManagementUiState> = combine(
        shortcutRepository.shortcutsFlow,
        settingsRepository.settingsFlow
    ) { shortcuts, settings ->
        ShortcutManagementUiState.Success(
            shortcuts = shortcuts,
            languageMap = accentRepository.getSupportedLanguages().toMap(),
            currentLanguage = settings.selectedLanguage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ShortcutManagementUiState.Loading
    )

    /**
     * Add a new shortcut (uses the currently selected language)
     */
    fun addShortcut(trigger: String, replacement: String, caseSensitive: Boolean = false) {
        viewModelScope.launch {
            val currentLanguage = (uiState.value as? ShortcutManagementUiState.Success)?.currentLanguage ?: "en"
            val shortcut = TextShortcut(
                id = UUID.randomUUID().toString(),
                trigger = trigger,
                replacement = replacement,
                caseSensitive = caseSensitive,
                isDefault = false,
                language = currentLanguage
            )
            shortcutRepository.addShortcut(shortcut)
        }
    }

    /**
     * Update an existing shortcut
     */
    fun updateShortcut(shortcut: TextShortcut) {
        viewModelScope.launch {
            shortcutRepository.updateShortcut(shortcut)
        }
    }

    /**
     * Delete a shortcut
     * Default shortcuts cannot be deleted
     */
    fun deleteShortcut(id: String) {
        viewModelScope.launch {
            shortcutRepository.deleteShortcut(id)
        }
    }
}

/**
 * UI state for the shortcuts management screen
 */
sealed interface ShortcutManagementUiState {
    data object Loading : ShortcutManagementUiState
    data class Success(
        val shortcuts: List<TextShortcut>,
        val languageMap: Map<String, String>,
        val currentLanguage: String
    ) : ShortcutManagementUiState
}
