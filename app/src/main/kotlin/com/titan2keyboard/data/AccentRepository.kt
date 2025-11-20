package com.titan2keyboard.data

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accent characters organized by language
 * Provides accent variants for letters when long-pressing
 */
@Singleton
class AccentRepository @Inject constructor() {

    /**
     * Map of language code to accent variants for each letter
     * Key: language code (ISO 639-1)
     * Value: Map of base character to list of accent variants
     */
    private val accentMap = mapOf(
        // French
        "fr" to mapOf(
            'a' to listOf("à", "â", "æ"),
            'e' to listOf("é", "è", "ê", "ë"),
            'i' to listOf("î", "ï"),
            'o' to listOf("ô", "œ"),
            'u' to listOf("ù", "û", "ü"),
            'c' to listOf("ç"),
            'y' to listOf("ÿ"),
            'A' to listOf("À", "Â", "Æ"),
            'E' to listOf("É", "È", "Ê", "Ë"),
            'I' to listOf("Î", "Ï"),
            'O' to listOf("Ô", "Œ"),
            'U' to listOf("Ù", "Û", "Ü"),
            'C' to listOf("Ç"),
            'Y' to listOf("Ÿ")
        ),

        // German
        "de" to mapOf(
            'a' to listOf("ä"),
            'o' to listOf("ö"),
            'u' to listOf("ü"),
            's' to listOf("ß"),
            'A' to listOf("Ä"),
            'O' to listOf("Ö"),
            'U' to listOf("Ü")
        ),

        // Spanish
        "es" to mapOf(
            'a' to listOf("á"),
            'e' to listOf("é"),
            'i' to listOf("í"),
            'o' to listOf("ó"),
            'u' to listOf("ú", "ü"),
            'n' to listOf("ñ"),
            'A' to listOf("Á"),
            'E' to listOf("É"),
            'I' to listOf("Í"),
            'O' to listOf("Ó"),
            'U' to listOf("Ú", "Ü"),
            'N' to listOf("Ñ")
        ),

        // Portuguese
        "pt" to mapOf(
            'a' to listOf("á", "à", "â", "ã"),
            'e' to listOf("é", "ê"),
            'i' to listOf("í"),
            'o' to listOf("ó", "ô", "õ"),
            'u' to listOf("ú"),
            'c' to listOf("ç"),
            'A' to listOf("Á", "À", "Â", "Ã"),
            'E' to listOf("É", "Ê"),
            'I' to listOf("Í"),
            'O' to listOf("Ó", "Ô", "Õ"),
            'U' to listOf("Ú"),
            'C' to listOf("Ç")
        ),

        // Italian
        "it" to mapOf(
            'a' to listOf("à"),
            'e' to listOf("è", "é"),
            'i' to listOf("ì", "í"),
            'o' to listOf("ò", "ó"),
            'u' to listOf("ù", "ú"),
            'A' to listOf("À"),
            'E' to listOf("È", "É"),
            'I' to listOf("Ì", "Í"),
            'O' to listOf("Ò", "Ó"),
            'U' to listOf("Ù", "Ú")
        ),

        // Dutch
        "nl" to mapOf(
            'a' to listOf("á", "à", "ä"),
            'e' to listOf("é", "è", "ë"),
            'i' to listOf("í", "ï"),
            'o' to listOf("ó", "ö"),
            'u' to listOf("ú", "ü"),
            'A' to listOf("Á", "À", "Ä"),
            'E' to listOf("É", "È", "Ë"),
            'I' to listOf("Í", "Ï"),
            'O' to listOf("Ó", "Ö"),
            'U' to listOf("Ú", "Ü")
        ),

        // Swedish
        "sv" to mapOf(
            'a' to listOf("å", "ä"),
            'o' to listOf("ö"),
            'A' to listOf("Å", "Ä"),
            'O' to listOf("Ö")
        ),

        // Norwegian/Danish
        "no" to mapOf(
            'a' to listOf("å", "æ"),
            'o' to listOf("ø"),
            'A' to listOf("Å", "Æ"),
            'O' to listOf("Ø")
        ),

        // Danish (same as Norwegian for now)
        "da" to mapOf(
            'a' to listOf("å", "æ"),
            'o' to listOf("ø"),
            'A' to listOf("Å", "Æ"),
            'O' to listOf("Ø")
        ),

        // Czech
        "cs" to mapOf(
            'a' to listOf("á"),
            'c' to listOf("č"),
            'd' to listOf("ď"),
            'e' to listOf("é", "ě"),
            'i' to listOf("í"),
            'n' to listOf("ň"),
            'o' to listOf("ó"),
            'r' to listOf("ř"),
            's' to listOf("š"),
            't' to listOf("ť"),
            'u' to listOf("ú", "ů"),
            'y' to listOf("ý"),
            'z' to listOf("ž"),
            'A' to listOf("Á"),
            'C' to listOf("Č"),
            'D' to listOf("Ď"),
            'E' to listOf("É", "Ě"),
            'I' to listOf("Í"),
            'N' to listOf("Ň"),
            'O' to listOf("Ó"),
            'R' to listOf("Ř"),
            'S' to listOf("Š"),
            'T' to listOf("Ť"),
            'U' to listOf("Ú", "Ů"),
            'Y' to listOf("Ý"),
            'Z' to listOf("Ž")
        ),

        // Polish
        "pl" to mapOf(
            'a' to listOf("ą"),
            'c' to listOf("ć"),
            'e' to listOf("ę"),
            'l' to listOf("ł"),
            'n' to listOf("ń"),
            'o' to listOf("ó"),
            's' to listOf("ś"),
            'z' to listOf("ź", "ż"),
            'A' to listOf("Ą"),
            'C' to listOf("Ć"),
            'E' to listOf("Ę"),
            'L' to listOf("Ł"),
            'N' to listOf("Ń"),
            'O' to listOf("Ó"),
            'S' to listOf("Ś"),
            'Z' to listOf("Ź", "Ż")
        )
    )

    /**
     * Get accent variants for a character in the specified language
     * Returns list including the base character first, then accent variants
     *
     * @param language ISO 639-1 language code (e.g., "en", "fr", "de")
     * @param baseChar The base character to get accents for
     * @return List of characters starting with base, then accents (empty if no accents)
     */
    fun getAccentCycle(language: String, baseChar: Char): List<String> {
        val accents = accentMap[language]?.get(baseChar) ?: emptyList()

        // If no accents exist, return empty list (will fallback to uppercase)
        if (accents.isEmpty()) {
            return emptyList()
        }

        // Return base char + accent variants for cycling
        return listOf(baseChar.toString()) + accents
    }

    /**
     * Check if a character has accent variants in the specified language
     */
    fun hasAccents(language: String, baseChar: Char): Boolean {
        return accentMap[language]?.containsKey(baseChar) == true
    }

    /**
     * Get all supported languages
     * @return List of (code, displayName) pairs
     */
    fun getSupportedLanguages(): List<Pair<String, String>> {
        return listOf(
            "en-GB" to "English (UK)",
            "en-US" to "English (US)",
            "en-AU" to "English (Australia)",
            "en-CA" to "English (Canada)",
            "en" to "English (Generic)",
            "fr" to "French (Français)",
            "de" to "German (Deutsch)",
            "es" to "Spanish (Español)",
            "pt" to "Portuguese (Português)",
            "it" to "Italian (Italiano)",
            "nl" to "Dutch (Nederlands)",
            "sv" to "Swedish (Svenska)",
            "no" to "Norwegian (Norsk)",
            "da" to "Danish (Dansk)",
            "cs" to "Czech (Čeština)",
            "pl" to "Polish (Polski)"
        )
    }
}
