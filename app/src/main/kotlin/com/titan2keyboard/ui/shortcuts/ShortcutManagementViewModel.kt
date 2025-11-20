package com.titan2keyboard.ui.shortcuts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.titan2keyboard.data.AccentRepository
import com.titan2keyboard.domain.model.TextShortcut
import com.titan2keyboard.domain.repository.SettingsRepository
import com.titan2keyboard.domain.repository.ShortcutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
     * Current filter mode for displaying shortcuts
     */
    private val _filterMode = MutableStateFlow(ShortcutFilter.CURRENT_LANGUAGE_ONLY)
    val filterMode: StateFlow<ShortcutFilter> = _filterMode

    /**
     * UI state for the shortcuts screen
     */
    val uiState: StateFlow<ShortcutManagementUiState> = combine(
        shortcutRepository.shortcutsFlow,
        settingsRepository.settingsFlow,
        _filterMode
    ) { shortcuts, settings, filter ->
        val filteredShortcuts = when (filter) {
            ShortcutFilter.CURRENT_LANGUAGE_ONLY -> {
                shortcuts.filter { it.language == settings.selectedLanguage }
            }
            ShortcutFilter.CURRENT_AND_ENGLISH -> {
                if (settings.selectedLanguage == "en") {
                    shortcuts.filter { it.language == "en" }
                } else {
                    shortcuts.filter { it.language == settings.selectedLanguage || it.language == "en" }
                }
            }
            ShortcutFilter.ALL_LANGUAGES -> {
                shortcuts
            }
        }

        ShortcutManagementUiState.Success(
            shortcuts = filteredShortcuts,
            languageMap = accentRepository.getSupportedLanguages().toMap(),
            currentLanguage = settings.selectedLanguage,
            filterMode = filter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ShortcutManagementUiState.Loading
    )

    /**
     * Set the filter mode for displaying shortcuts
     */
    fun setFilterMode(filter: ShortcutFilter) {
        _filterMode.value = filter
    }

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
        val currentLanguage: String,
        val filterMode: ShortcutFilter
    ) : ShortcutManagementUiState
}

/**
 * Filter modes for displaying shortcuts
 */
enum class ShortcutFilter {
    /**
     * Show only shortcuts for the currently selected language
     */
    CURRENT_LANGUAGE_ONLY,

    /**
     * Show shortcuts for the current language and English
     * (If current is English, show only English)
     */
    CURRENT_AND_ENGLISH,

    /**
     * Show shortcuts for all languages
     */
    ALL_LANGUAGES
}
