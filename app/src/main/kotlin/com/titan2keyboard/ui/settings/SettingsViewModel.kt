package com.titan2keyboard.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.titan2keyboard.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for settings screen
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settingsState: StateFlow<SettingsUiState> = settingsRepository
        .settingsFlow
        .map<com.titan2keyboard.domain.model.KeyboardSettings, SettingsUiState> { settings ->
            SettingsUiState.Success(settings)
        }
        .catch { exception ->
            emit(SettingsUiState.Error(exception.message ?: "Unknown error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState.Loading
        )

    fun updateAutoCapitalize(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSetting("autoCapitalize", enabled)
        }
    }

    fun updateKeyRepeat(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSetting("keyRepeatEnabled", enabled)
        }
    }

    fun updateLongPressCapitalize(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSetting("longPressCapitalize", enabled)
        }
    }

    fun updateDoubleSpacePeriod(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSetting("doubleSpacePeriod", enabled)
        }
    }

    fun updateTextShortcuts(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSetting("textShortcutsEnabled", enabled)
        }
    }

    fun updateStickyShift(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSetting("stickyShift", enabled)
        }
    }

    fun updateStickyAlt(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSetting("stickyAlt", enabled)
        }
    }

    fun updateAltBackspaceDeleteLine(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSetting("altBackspaceDeleteLine", enabled)
        }
    }

    fun updatePreferredCurrency(currency: String?) {
        viewModelScope.launch {
            settingsRepository.updateSetting("preferredCurrency", currency ?: "")
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            settingsRepository.resetToDefaults()
        }
    }
}
