package com.titan2keyboard.domain.model

/**
 * State of a modifier key (Shift or Alt)
 */
enum class ModifierState {
    /** Modifier is not active */
    NONE,

    /** Modifier is active for one character (single press) */
    ONE_SHOT,

    /** Modifier is locked on (long press) until pressed again */
    LOCKED
}

/**
 * Tracks the state of all modifiers
 */
data class ModifiersState(
    val shift: ModifierState = ModifierState.NONE,
    val alt: ModifierState = ModifierState.NONE,
    val symPickerVisible: Boolean = false,
    val symCategory: SymbolCategory = SymbolCategory.PUNCTUATION
) {
    /**
     * Check if shift is active (either one-shot or locked)
     */
    fun isShiftActive(): Boolean = shift != ModifierState.NONE

    /**
     * Check if alt is active (either one-shot or locked)
     */
    fun isAltActive(): Boolean = alt != ModifierState.NONE

    /**
     * Check if any modifier is in one-shot mode
     */
    fun hasOneShotModifier(): Boolean = shift == ModifierState.ONE_SHOT || alt == ModifierState.ONE_SHOT

    /**
     * Check if symbol picker is showing
     */
    fun isSymPickerActive(): Boolean = symPickerVisible
}
