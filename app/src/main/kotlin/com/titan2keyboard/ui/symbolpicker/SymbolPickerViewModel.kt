package com.titan2keyboard.ui.symbolpicker

import com.titan2keyboard.data.SymbolCategoryItem
import com.titan2keyboard.data.SymbolRepository
import com.titan2keyboard.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for the symbol picker
 * Manages visibility state and category cycling
 * Note: This is a Singleton, not a ViewModel, because InputMethodService doesn't support ViewModelStore
 */
@Singleton
class SymbolPickerViewModel @Inject constructor(
    private val symbolRepository: SymbolRepository,
    private val settingsRepository: SettingsRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _isVisible = MutableStateFlow(false)
    val isVisible: StateFlow<Boolean> = _isVisible.asStateFlow()

    private val _currentCategory = MutableStateFlow<SymbolCategoryItem?>(null)
    val currentCategory: StateFlow<SymbolCategoryItem?> = _currentCategory.asStateFlow()

    private var preferredCurrency: String? = null
    private var allCategories: List<SymbolCategoryItem> = emptyList()

    init {
        // Watch for settings changes to update categories when currency preference changes
        scope.launch {
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
