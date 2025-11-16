package com.titan2keyboard.ui.symbolpicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.titan2keyboard.data.SymbolRepository
import com.titan2keyboard.domain.model.SymbolCategory
import com.titan2keyboard.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the symbol picker
 * Manages visibility state and category cycling
 */
@HiltViewModel
class SymbolPickerViewModel @Inject constructor(
    private val symbolRepository: SymbolRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _isVisible = MutableStateFlow(false)
    val isVisible: StateFlow<Boolean> = _isVisible.asStateFlow()

    private val _currentCategory = MutableStateFlow<SymbolCategory?>(null)
    val currentCategory: StateFlow<SymbolCategory?> = _currentCategory.asStateFlow()

    private var preferredCurrency: String? = null
    private var allCategories: List<SymbolCategory> = emptyList()

    init {
        // Watch for settings changes to update categories when currency preference changes
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                preferredCurrency = settings.preferredCurrency
                allCategories = symbolRepository.getCategories(preferredCurrency)

                // If picker is visible, update current category to reflect new currency
                if (_isVisible.value && _currentCategory.value != null) {
                    // Update to same category index with new currency
                    val currentIndex = allCategories.indexOfFirst { it.id == _currentCategory.value?.id }
                    if (currentIndex >= 0) {
                        _currentCategory.value = allCategories[currentIndex]
                    }
                }
            }
        }
    }

    /**
     * Show the symbol picker with the first category
     */
    fun show() {
        if (allCategories.isEmpty()) {
            allCategories = symbolRepository.getCategories(preferredCurrency)
        }
        _isVisible.value = true
        _currentCategory.value = allCategories.firstOrNull()
    }

    /**
     * Hide the symbol picker
     */
    fun hide() {
        _isVisible.value = false
    }

    /**
     * Cycle to the next category
     * If already visible, moves to next category
     * If not visible, shows picker with first category
     */
    fun cycleToNextCategory() {
        if (!_isVisible.value) {
            show()
            return
        }

        val current = _currentCategory.value ?: return
        _currentCategory.value = symbolRepository.getNextCategory(current.id, preferredCurrency)
    }

    /**
     * Get current category index (0-based)
     */
    fun getCurrentCategoryIndex(): Int {
        val current = _currentCategory.value ?: return 0
        return allCategories.indexOfFirst { it.id == current.id }.coerceAtLeast(0)
    }

    /**
     * Get total number of categories
     */
    fun getTotalCategories(): Int {
        return symbolRepository.getCategoryCount()
    }
}
