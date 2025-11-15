package com.titan2keyboard.domain.model

/**
 * Sealed class representing the result of handling a key event
 */
sealed class KeyEventResult {
    /**
     * The key event was handled by the keyboard
     */
    data object Handled : KeyEventResult()

    /**
     * The key event was not handled and should be passed to the system
     */
    data object NotHandled : KeyEventResult()
}
