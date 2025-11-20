package com.titan2keyboard.domain.model

/**
 * Defines the behavior of Alt+Backspace key combination
 */
enum class AltBackspaceBehavior {
    /**
     * Alt+Backspace acts as regular backspace (deletes character before cursor)
     * Alt modifier is ignored
     */
    REGULAR_BACKSPACE,

    /**
     * Alt+Backspace deletes entire line before cursor
     * Everything from last newline (or start of text) to cursor is deleted
     */
    DELETE_LINE,

    /**
     * Alt+Backspace deletes character after cursor (forward delete)
     * Acts like the Delete key
     */
    DELETE_FORWARD;

    companion object {
        /**
         * Get enum from string value, with fallback to default
         */
        fun fromString(value: String?): AltBackspaceBehavior {
            return when (value?.uppercase()) {
                "REGULAR_BACKSPACE" -> REGULAR_BACKSPACE
                "DELETE_LINE" -> DELETE_LINE
                "DELETE_FORWARD" -> DELETE_FORWARD
                else -> DELETE_LINE // Default behavior
            }
        }

        /**
         * Migrate from old boolean setting
         * @param deleteLine true if old setting was "delete line", false if "regular backspace"
         */
        fun fromBoolean(deleteLine: Boolean): AltBackspaceBehavior {
            return if (deleteLine) DELETE_LINE else REGULAR_BACKSPACE
        }
    }
}
