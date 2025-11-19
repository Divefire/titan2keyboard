package com.titan2keyboard.domain.model

/**
 * Represents a category of symbols in the symbol picker
 */
data class SymbolCategory(
    /** Unique identifier for this category */
    val id: String,

    /** Display name shown in UI */
    val displayName: String,

    /** List of symbol characters in this category */
    val symbols: List<String>,

    /** Order in which categories are displayed (0-based) */
    val order: Int
)
