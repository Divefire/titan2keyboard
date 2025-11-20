package com.titan2keyboard.data

import com.titan2keyboard.domain.model.TextShortcut
import java.util.UUID

/**
 * Provides default text shortcuts for different languages
 */
object DefaultShortcuts {

    /**
     * Get default shortcuts for a specific language
     * @param language ISO 639-1 language code (or regional variant like en-GB)
     * @return List of default shortcuts for the language
     */
    fun getDefaultsForLanguage(language: String): List<TextShortcut> {
        return when (language) {
            "en" -> englishDefaults()
            "en-GB" -> englishGBDefaults()
            "en-US" -> englishUSDefaults()
            "en-AU" -> englishAUDefaults()
            "en-CA" -> englishCADefaults()
            "fr" -> frenchDefaults()
            "fr-CA" -> frenchCADefaults()
            "de" -> germanDefaults()
            "es" -> spanishDefaults()
            "es-MX" -> spanishMXDefaults()
            "pt" -> portugueseDefaults()
            "pt-BR" -> portugueseBRDefaults()
            "it" -> italianDefaults()
            "nl" -> dutchDefaults()
            "sv" -> swedishDefaults()
            "no" -> norwegianDefaults()
            "da" -> danishDefaults()
            "cs" -> czechDefaults()
            "pl" -> polishDefaults()
            "el" -> greekDefaults()
            "tr" -> turkishDefaults()
            "ru" -> russianDefaults()
            "hu" -> hungarianDefaults()
            "ro" -> romanianDefaults()
            "fi" -> finnishDefaults()
            "bg" -> bulgarianDefaults()
            else -> emptyList()
        }
    }

    /**
     * Get all default shortcuts for all languages
     */
    fun getAllDefaults(): List<TextShortcut> {
        return listOf("en", "en-GB", "en-US", "en-AU", "en-CA", "fr", "fr-CA", "de", "es", "es-MX", "pt", "pt-BR", "it", "nl", "sv", "no", "da", "cs", "pl", "el", "tr", "ru", "hu", "ro", "fi", "bg")
            .flatMap { getDefaultsForLanguage(it) }
    }

    private fun englishDefaults(): List<TextShortcut> {
        return listOf(
            // Common contractions
            TextShortcut(UUID.randomUUID().toString(), "Im", "I'm", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "Ive", "I've", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "Ill", "I'll", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "Id", "I'd", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "dont", "don't", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "doesnt", "doesn't", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "didnt", "didn't", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "cant", "can't", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "couldnt", "couldn't", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "wouldnt", "wouldn't", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "shouldnt", "shouldn't", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "isnt", "isn't", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "arent", "aren't", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "wasnt", "wasn't", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "werent", "weren't", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "hasnt", "hasn't", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "havent", "haven't", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "hadnt", "hadn't", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "wont", "won't", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "thats", "that's", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "theres", "there's", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "heres", "here's", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "whats", "what's", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "wheres", "where's", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "whos", "who's", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "hows", "how's", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "whens", "when's", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "whys", "why's", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "youre", "you're", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "youve", "you've", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "youll", "you'll", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "youd", "you'd", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "theyre", "they're", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "theyve", "they've", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "theyll", "they'll", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "theyd", "they'd", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "weve", "we've", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "wed", "we'd", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "shes", "she's", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "hes", "he's", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "lets", "let's", caseSensitive = false, isDefault = true, language = "en"),

            // Common typos
            TextShortcut(UUID.randomUUID().toString(), "teh", "the", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "recieve", "receive", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "occured", "occurred", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "seperate", "separate", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "definately", "definitely", caseSensitive = false, isDefault = true, language = "en"),
            TextShortcut(UUID.randomUUID().toString(), "alot", "a lot", caseSensitive = false, isDefault = true, language = "en"),
        )
    }

    private fun englishGBDefaults(): List<TextShortcut> {
        return buildList {
            // Common contractions (same as generic English)
            add(TextShortcut(UUID.randomUUID().toString(), "Im", "I'm", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "Ive", "I've", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "Ill", "I'll", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "Id", "I'd", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "dont", "don't", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "doesnt", "doesn't", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "didnt", "didn't", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "cant", "can't", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "couldnt", "couldn't", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "wouldnt", "wouldn't", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "shouldnt", "shouldn't", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "isnt", "isn't", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "arent", "aren't", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "wasnt", "wasn't", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "werent", "weren't", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "hasnt", "hasn't", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "havent", "haven't", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "hadnt", "hadn't", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "wont", "won't", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "thats", "that's", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "theres", "there's", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "heres", "here's", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "whats", "what's", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "wheres", "where's", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "whos", "who's", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "hows", "how's", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "whens", "when's", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "whys", "why's", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "youre", "you're", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "youve", "you've", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "youll", "you'll", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "youd", "you'd", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "theyre", "they're", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "theyve", "they've", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "theyll", "they'll", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "theyd", "they'd", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "weve", "we've", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "wed", "we'd", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "shes", "she's", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "hes", "he's", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "lets", "let's", caseSensitive = false, isDefault = true, language = "en-GB"))

            // Common typos
            add(TextShortcut(UUID.randomUUID().toString(), "teh", "the", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "recieve", "receive", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "occured", "occurred", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "seperate", "separate", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "definately", "definitely", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "alot", "a lot", caseSensitive = false, isDefault = true, language = "en-GB"))

            // UK-specific: American spelling corrections
            add(TextShortcut(UUID.randomUUID().toString(), "color", "colour", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "favor", "favour", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "honor", "honour", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "organize", "organise", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "realize", "realise", caseSensitive = false, isDefault = true, language = "en-GB"))
            add(TextShortcut(UUID.randomUUID().toString(), "analyze", "analyse", caseSensitive = false, isDefault = true, language = "en-GB"))
        }
    }

    private fun englishUSDefaults(): List<TextShortcut> {
        return buildList {
            // Common contractions (same as generic English)
            add(TextShortcut(UUID.randomUUID().toString(), "Im", "I'm", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "Ive", "I've", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "Ill", "I'll", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "Id", "I'd", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "dont", "don't", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "doesnt", "doesn't", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "didnt", "didn't", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "cant", "can't", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "couldnt", "couldn't", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "wouldnt", "wouldn't", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "shouldnt", "shouldn't", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "isnt", "isn't", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "arent", "aren't", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "wasnt", "wasn't", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "werent", "weren't", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "hasnt", "hasn't", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "havent", "haven't", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "hadnt", "hadn't", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "wont", "won't", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "thats", "that's", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "theres", "there's", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "heres", "here's", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "whats", "what's", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "wheres", "where's", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "whos", "who's", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "hows", "how's", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "whens", "when's", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "whys", "why's", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "youre", "you're", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "youve", "you've", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "youll", "you'll", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "youd", "you'd", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "theyre", "they're", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "theyve", "they've", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "theyll", "they'll", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "theyd", "they'd", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "weve", "we've", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "wed", "we'd", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "shes", "she's", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "hes", "he's", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "lets", "let's", caseSensitive = false, isDefault = true, language = "en-US"))

            // Common typos
            add(TextShortcut(UUID.randomUUID().toString(), "teh", "the", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "recieve", "receive", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "occured", "occurred", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "seperate", "separate", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "definately", "definitely", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "alot", "a lot", caseSensitive = false, isDefault = true, language = "en-US"))

            // US-specific: British spelling corrections
            add(TextShortcut(UUID.randomUUID().toString(), "colour", "color", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "favour", "favor", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "honour", "honor", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "organise", "organize", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "realise", "realize", caseSensitive = false, isDefault = true, language = "en-US"))
            add(TextShortcut(UUID.randomUUID().toString(), "analyse", "analyze", caseSensitive = false, isDefault = true, language = "en-US"))
        }
    }

    private fun englishAUDefaults(): List<TextShortcut> {
        return buildList {
            // Common contractions (same as generic English)
            add(TextShortcut(UUID.randomUUID().toString(), "Im", "I'm", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "Ive", "I've", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "Ill", "I'll", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "Id", "I'd", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "dont", "don't", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "doesnt", "doesn't", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "didnt", "didn't", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "cant", "can't", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "couldnt", "couldn't", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "wouldnt", "wouldn't", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "shouldnt", "shouldn't", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "isnt", "isn't", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "arent", "aren't", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "wasnt", "wasn't", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "werent", "weren't", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "hasnt", "hasn't", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "havent", "haven't", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "hadnt", "hadn't", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "wont", "won't", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "thats", "that's", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "theres", "there's", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "heres", "here's", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "whats", "what's", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "wheres", "where's", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "whos", "who's", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "hows", "how's", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "whens", "when's", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "whys", "why's", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "youre", "you're", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "youve", "you've", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "youll", "you'll", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "youd", "you'd", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "theyre", "they're", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "theyve", "they've", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "theyll", "they'll", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "theyd", "they'd", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "weve", "we've", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "wed", "we'd", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "shes", "she's", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "hes", "he's", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "lets", "let's", caseSensitive = false, isDefault = true, language = "en-AU"))

            // Common typos
            add(TextShortcut(UUID.randomUUID().toString(), "teh", "the", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "recieve", "receive", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "occured", "occurred", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "seperate", "separate", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "definately", "definitely", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "alot", "a lot", caseSensitive = false, isDefault = true, language = "en-AU"))

            // AU follows UK spelling, so correct American spellings
            add(TextShortcut(UUID.randomUUID().toString(), "color", "colour", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "favor", "favour", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "honor", "honour", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "organize", "organise", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "realize", "realise", caseSensitive = false, isDefault = true, language = "en-AU"))
            add(TextShortcut(UUID.randomUUID().toString(), "analyze", "analyse", caseSensitive = false, isDefault = true, language = "en-AU"))
        }
    }

    private fun englishCADefaults(): List<TextShortcut> {
        return buildList {
            // Common contractions (same as generic English)
            add(TextShortcut(UUID.randomUUID().toString(), "Im", "I'm", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "Ive", "I've", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "Ill", "I'll", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "Id", "I'd", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "dont", "don't", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "doesnt", "doesn't", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "didnt", "didn't", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "cant", "can't", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "couldnt", "couldn't", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "wouldnt", "wouldn't", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "shouldnt", "shouldn't", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "isnt", "isn't", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "arent", "aren't", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "wasnt", "wasn't", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "werent", "weren't", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "hasnt", "hasn't", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "havent", "haven't", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "hadnt", "hadn't", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "wont", "won't", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "thats", "that's", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "theres", "there's", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "heres", "here's", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "whats", "what's", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "wheres", "where's", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "whos", "who's", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "hows", "how's", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "whens", "when's", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "whys", "why's", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "youre", "you're", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "youve", "you've", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "youll", "you'll", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "youd", "you'd", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "theyre", "they're", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "theyve", "they've", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "theyll", "they'll", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "theyd", "they'd", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "weve", "we've", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "wed", "we'd", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "shes", "she's", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "hes", "he's", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "lets", "let's", caseSensitive = false, isDefault = true, language = "en-CA"))

            // Common typos
            add(TextShortcut(UUID.randomUUID().toString(), "teh", "the", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "recieve", "receive", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "occured", "occurred", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "seperate", "separate", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "definately", "definitely", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "alot", "a lot", caseSensitive = false, isDefault = true, language = "en-CA"))

            // Canadian spelling follows UK for -our/-ise words, so correct American spellings
            add(TextShortcut(UUID.randomUUID().toString(), "color", "colour", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "favor", "favour", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "honor", "honour", caseSensitive = false, isDefault = true, language = "en-CA"))
            // Note: Canadian uses -ize (like US) not -ise (like UK) for these:
            add(TextShortcut(UUID.randomUUID().toString(), "organise", "organize", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "realise", "realize", caseSensitive = false, isDefault = true, language = "en-CA"))
            add(TextShortcut(UUID.randomUUID().toString(), "analyse", "analyze", caseSensitive = false, isDefault = true, language = "en-CA"))
        }
    }

    private fun frenchDefaults(): List<TextShortcut> {
        return listOf(
            // Common contractions and shortcuts
            TextShortcut(UUID.randomUUID().toString(), "qq", "quelque", caseSensitive = false, isDefault = true, language = "fr"),
            TextShortcut(UUID.randomUUID().toString(), "qqch", "quelque chose", caseSensitive = false, isDefault = true, language = "fr"),
            TextShortcut(UUID.randomUUID().toString(), "qqn", "quelqu'un", caseSensitive = false, isDefault = true, language = "fr"),
            TextShortcut(UUID.randomUUID().toString(), "tjrs", "toujours", caseSensitive = false, isDefault = true, language = "fr"),
            TextShortcut(UUID.randomUUID().toString(), "bcp", "beaucoup", caseSensitive = false, isDefault = true, language = "fr"),
            TextShortcut(UUID.randomUUID().toString(), "svp", "s'il vous plaît", caseSensitive = false, isDefault = true, language = "fr"),
            TextShortcut(UUID.randomUUID().toString(), "stp", "s'il te plaît", caseSensitive = false, isDefault = true, language = "fr"),
            TextShortcut(UUID.randomUUID().toString(), "pq", "pourquoi", caseSensitive = false, isDefault = true, language = "fr"),
            TextShortcut(UUID.randomUUID().toString(), "pcq", "parce que", caseSensitive = false, isDefault = true, language = "fr"),
            TextShortcut(UUID.randomUUID().toString(), "qd", "quand", caseSensitive = false, isDefault = true, language = "fr"),
            TextShortcut(UUID.randomUUID().toString(), "rdv", "rendez-vous", caseSensitive = false, isDefault = true, language = "fr"),
            TextShortcut(UUID.randomUUID().toString(), "jai", "j'ai", caseSensitive = false, isDefault = true, language = "fr"),
            TextShortcut(UUID.randomUUID().toString(), "cest", "c'est", caseSensitive = false, isDefault = true, language = "fr"),
            TextShortcut(UUID.randomUUID().toString(), "sil", "s'il", caseSensitive = false, isDefault = true, language = "fr"),
            TextShortcut(UUID.randomUUID().toString(), "qqun", "quelqu'un", caseSensitive = false, isDefault = true, language = "fr"),
        )
    }

    private fun frenchCADefaults(): List<TextShortcut> {
        return listOf(
            // Common Quebec French abbreviations and shortcuts
            TextShortcut(UUID.randomUUID().toString(), "qq", "quelque", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "qqch", "quelque chose", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "qqn", "quelqu'un", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "tjrs", "toujours", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "bcp", "beaucoup", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "svp", "s'il vous plaît", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "stp", "s'il te plaît", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "pq", "pourquoi", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "pcq", "parce que", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "qd", "quand", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "rdv", "rendez-vous", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "jai", "j'ai", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "cest", "c'est", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "sil", "s'il", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "qqun", "quelqu'un", caseSensitive = false, isDefault = true, language = "fr-CA"),
            // Quebec-specific
            TextShortcut(UUID.randomUUID().toString(), "tsé", "tu sais", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "tse", "tu sais", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "pis", "puis", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "dsl", "désolé", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "msg", "message", caseSensitive = false, isDefault = true, language = "fr-CA"),
            TextShortcut(UUID.randomUUID().toString(), "jsais", "je sais", caseSensitive = false, isDefault = true, language = "fr-CA"),
        )
    }

    private fun germanDefaults(): List<TextShortcut> {
        return listOf(
            // Common German typos and shortcuts
            TextShortcut(UUID.randomUUID().toString(), "zb", "z.B.", caseSensitive = false, isDefault = true, language = "de"),
            TextShortcut(UUID.randomUUID().toString(), "bzw", "bzw.", caseSensitive = false, isDefault = true, language = "de"),
            TextShortcut(UUID.randomUUID().toString(), "usw", "usw.", caseSensitive = false, isDefault = true, language = "de"),
            TextShortcut(UUID.randomUUID().toString(), "ua", "u.a.", caseSensitive = false, isDefault = true, language = "de"),
            TextShortcut(UUID.randomUUID().toString(), "zzt", "z.Zt.", caseSensitive = false, isDefault = true, language = "de"),
            TextShortcut(UUID.randomUUID().toString(), "mfg", "Mit freundlichen Grüßen", caseSensitive = false, isDefault = true, language = "de"),
            TextShortcut(UUID.randomUUID().toString(), "lg", "Liebe Grüße", caseSensitive = false, isDefault = true, language = "de"),
            TextShortcut(UUID.randomUUID().toString(), "vg", "Viele Grüße", caseSensitive = false, isDefault = true, language = "de"),
        )
    }

    private fun spanishDefaults(): List<TextShortcut> {
        return listOf(
            // Common Spanish typos and shortcuts
            TextShortcut(UUID.randomUUID().toString(), "xq", "porque", caseSensitive = false, isDefault = true, language = "es"),
            TextShortcut(UUID.randomUUID().toString(), "pq", "porque", caseSensitive = false, isDefault = true, language = "es"),
            TextShortcut(UUID.randomUUID().toString(), "xk", "porque", caseSensitive = false, isDefault = true, language = "es"),
            TextShortcut(UUID.randomUUID().toString(), "tb", "también", caseSensitive = false, isDefault = true, language = "es"),
            TextShortcut(UUID.randomUUID().toString(), "tmb", "también", caseSensitive = false, isDefault = true, language = "es"),
            TextShortcut(UUID.randomUUID().toString(), "q", "que", caseSensitive = false, isDefault = true, language = "es"),
            TextShortcut(UUID.randomUUID().toString(), "bn", "bien", caseSensitive = false, isDefault = true, language = "es"),
            TextShortcut(UUID.randomUUID().toString(), "porfavor", "por favor", caseSensitive = false, isDefault = true, language = "es"),
            TextShortcut(UUID.randomUUID().toString(), "pf", "por favor", caseSensitive = false, isDefault = true, language = "es"),
        )
    }

    private fun spanishMXDefaults(): List<TextShortcut> {
        return listOf(
            // Common Mexican Spanish abbreviations
            TextShortcut(UUID.randomUUID().toString(), "xq", "porque", caseSensitive = false, isDefault = true, language = "es-MX"),
            TextShortcut(UUID.randomUUID().toString(), "pq", "porque", caseSensitive = false, isDefault = true, language = "es-MX"),
            TextShortcut(UUID.randomUUID().toString(), "xk", "porque", caseSensitive = false, isDefault = true, language = "es-MX"),
            TextShortcut(UUID.randomUUID().toString(), "tb", "también", caseSensitive = false, isDefault = true, language = "es-MX"),
            TextShortcut(UUID.randomUUID().toString(), "tmb", "también", caseSensitive = false, isDefault = true, language = "es-MX"),
            TextShortcut(UUID.randomUUID().toString(), "q", "que", caseSensitive = false, isDefault = true, language = "es-MX"),
            TextShortcut(UUID.randomUUID().toString(), "bn", "bien", caseSensitive = false, isDefault = true, language = "es-MX"),
            TextShortcut(UUID.randomUUID().toString(), "porfavor", "por favor", caseSensitive = false, isDefault = true, language = "es-MX"),
            TextShortcut(UUID.randomUUID().toString(), "pf", "por favor", caseSensitive = false, isDefault = true, language = "es-MX"),
            // Mexican-specific
            TextShortcut(UUID.randomUUID().toString(), "x", "por", caseSensitive = false, isDefault = true, language = "es-MX"),
            TextShortcut(UUID.randomUUID().toString(), "pa", "para", caseSensitive = false, isDefault = true, language = "es-MX"),
            TextShortcut(UUID.randomUUID().toString(), "qndo", "cuando", caseSensitive = false, isDefault = true, language = "es-MX"),
            TextShortcut(UUID.randomUUID().toString(), "k", "que", caseSensitive = false, isDefault = true, language = "es-MX"),
            TextShortcut(UUID.randomUUID().toString(), "aki", "aquí", caseSensitive = false, isDefault = true, language = "es-MX"),
            TextShortcut(UUID.randomUUID().toString(), "ahi", "ahí", caseSensitive = false, isDefault = true, language = "es-MX"),
            TextShortcut(UUID.randomUUID().toString(), "asi", "así", caseSensitive = false, isDefault = true, language = "es-MX"),
        )
    }

    private fun portugueseDefaults(): List<TextShortcut> {
        return listOf(
            // Common Portuguese typos and shortcuts
            TextShortcut(UUID.randomUUID().toString(), "vc", "você", caseSensitive = false, isDefault = true, language = "pt"),
            TextShortcut(UUID.randomUUID().toString(), "vcs", "vocês", caseSensitive = false, isDefault = true, language = "pt"),
            TextShortcut(UUID.randomUUID().toString(), "pq", "porque", caseSensitive = false, isDefault = true, language = "pt"),
            TextShortcut(UUID.randomUUID().toString(), "tb", "também", caseSensitive = false, isDefault = true, language = "pt"),
            TextShortcut(UUID.randomUUID().toString(), "tbm", "também", caseSensitive = false, isDefault = true, language = "pt"),
            TextShortcut(UUID.randomUUID().toString(), "blz", "beleza", caseSensitive = false, isDefault = true, language = "pt"),
            TextShortcut(UUID.randomUUID().toString(), "pfv", "por favor", caseSensitive = false, isDefault = true, language = "pt"),
            TextShortcut(UUID.randomUUID().toString(), "obg", "obrigado", caseSensitive = false, isDefault = true, language = "pt"),
        )
    }

    private fun portugueseBRDefaults(): List<TextShortcut> {
        return listOf(
            // Common Brazilian Portuguese abbreviations and slang
            TextShortcut(UUID.randomUUID().toString(), "vc", "você", caseSensitive = false, isDefault = true, language = "pt-BR"),
            TextShortcut(UUID.randomUUID().toString(), "vcs", "vocês", caseSensitive = false, isDefault = true, language = "pt-BR"),
            TextShortcut(UUID.randomUUID().toString(), "pq", "porque", caseSensitive = false, isDefault = true, language = "pt-BR"),
            TextShortcut(UUID.randomUUID().toString(), "tb", "também", caseSensitive = false, isDefault = true, language = "pt-BR"),
            TextShortcut(UUID.randomUUID().toString(), "tbm", "também", caseSensitive = false, isDefault = true, language = "pt-BR"),
            TextShortcut(UUID.randomUUID().toString(), "blz", "beleza", caseSensitive = false, isDefault = true, language = "pt-BR"),
            TextShortcut(UUID.randomUUID().toString(), "pfv", "por favor", caseSensitive = false, isDefault = true, language = "pt-BR"),
            TextShortcut(UUID.randomUUID().toString(), "obg", "obrigado", caseSensitive = false, isDefault = true, language = "pt-BR"),
            TextShortcut(UUID.randomUUID().toString(), "td", "tudo", caseSensitive = false, isDefault = true, language = "pt-BR"),
            TextShortcut(UUID.randomUUID().toString(), "fds", "fim de semana", caseSensitive = false, isDefault = true, language = "pt-BR"),
            TextShortcut(UUID.randomUUID().toString(), "msg", "mensagem", caseSensitive = false, isDefault = true, language = "pt-BR"),
            TextShortcut(UUID.randomUUID().toString(), "hj", "hoje", caseSensitive = false, isDefault = true, language = "pt-BR"),
            TextShortcut(UUID.randomUUID().toString(), "dps", "depois", caseSensitive = false, isDefault = true, language = "pt-BR"),
            TextShortcut(UUID.randomUUID().toString(), "tmj", "tamo junto", caseSensitive = false, isDefault = true, language = "pt-BR"),
            TextShortcut(UUID.randomUUID().toString(), "flw", "falou", caseSensitive = false, isDefault = true, language = "pt-BR"),
            TextShortcut(UUID.randomUUID().toString(), "vlw", "valeu", caseSensitive = false, isDefault = true, language = "pt-BR"),
        )
    }

    private fun italianDefaults(): List<TextShortcut> {
        return listOf(
            // Common Italian typos and shortcuts
            TextShortcut(UUID.randomUUID().toString(), "cmq", "comunque", caseSensitive = false, isDefault = true, language = "it"),
            TextShortcut(UUID.randomUUID().toString(), "xche", "perché", caseSensitive = false, isDefault = true, language = "it"),
            TextShortcut(UUID.randomUUID().toString(), "xchè", "perché", caseSensitive = false, isDefault = true, language = "it"),
            TextShortcut(UUID.randomUUID().toString(), "nn", "non", caseSensitive = false, isDefault = true, language = "it"),
            TextShortcut(UUID.randomUUID().toString(), "qnd", "quando", caseSensitive = false, isDefault = true, language = "it"),
            TextShortcut(UUID.randomUUID().toString(), "qlc", "qualche", caseSensitive = false, isDefault = true, language = "it"),
            TextShortcut(UUID.randomUUID().toString(), "qlcs", "qualcosa", caseSensitive = false, isDefault = true, language = "it"),
        )
    }

    private fun dutchDefaults(): List<TextShortcut> {
        return listOf(
            // Common Dutch typos and shortcuts
            TextShortcut(UUID.randomUUID().toString(), "svp", "alstublieft", caseSensitive = false, isDefault = true, language = "nl"),
            TextShortcut(UUID.randomUUID().toString(), "aub", "alstublieft", caseSensitive = false, isDefault = true, language = "nl"),
            TextShortcut(UUID.randomUUID().toString(), "ajb", "alsjeblieft", caseSensitive = false, isDefault = true, language = "nl"),
            TextShortcut(UUID.randomUUID().toString(), "bv", "bijvoorbeeld", caseSensitive = false, isDefault = true, language = "nl"),
            TextShortcut(UUID.randomUUID().toString(), "mvg", "Met vriendelijke groet", caseSensitive = false, isDefault = true, language = "nl"),
            TextShortcut(UUID.randomUUID().toString(), "gr", "groet", caseSensitive = false, isDefault = true, language = "nl"),
        )
    }

    private fun swedishDefaults(): List<TextShortcut> {
        return listOf(
            // Common Swedish typos and shortcuts
            TextShortcut(UUID.randomUUID().toString(), "tex", "till exempel", caseSensitive = false, isDefault = true, language = "sv"),
            TextShortcut(UUID.randomUUID().toString(), "osv", "och så vidare", caseSensitive = false, isDefault = true, language = "sv"),
            TextShortcut(UUID.randomUUID().toString(), "mvh", "Med vänliga hälsningar", caseSensitive = false, isDefault = true, language = "sv"),
        )
    }

    private fun norwegianDefaults(): List<TextShortcut> {
        return listOf(
            // Common Norwegian typos and shortcuts
            TextShortcut(UUID.randomUUID().toString(), "feks", "for eksempel", caseSensitive = false, isDefault = true, language = "no"),
            TextShortcut(UUID.randomUUID().toString(), "osv", "og så videre", caseSensitive = false, isDefault = true, language = "no"),
            TextShortcut(UUID.randomUUID().toString(), "mvh", "Med vennlig hilsen", caseSensitive = false, isDefault = true, language = "no"),
        )
    }

    private fun danishDefaults(): List<TextShortcut> {
        return listOf(
            // Common Danish typos and shortcuts
            TextShortcut(UUID.randomUUID().toString(), "fx", "for eksempel", caseSensitive = false, isDefault = true, language = "da"),
            TextShortcut(UUID.randomUUID().toString(), "osv", "og så videre", caseSensitive = false, isDefault = true, language = "da"),
            TextShortcut(UUID.randomUUID().toString(), "mvh", "Med venlig hilsen", caseSensitive = false, isDefault = true, language = "da"),
        )
    }

    private fun czechDefaults(): List<TextShortcut> {
        return listOf(
            // Common Czech typos and shortcuts
            TextShortcut(UUID.randomUUID().toString(), "napр", "například", caseSensitive = false, isDefault = true, language = "cs"),
            TextShortcut(UUID.randomUUID().toString(), "apod", "a podobně", caseSensitive = false, isDefault = true, language = "cs"),
            TextShortcut(UUID.randomUUID().toString(), "atd", "a tak dále", caseSensitive = false, isDefault = true, language = "cs"),
        )
    }

    private fun polishDefaults(): List<TextShortcut> {
        return listOf(
            // Common Polish typos and shortcuts
            TextShortcut(UUID.randomUUID().toString(), "np", "na przykład", caseSensitive = false, isDefault = true, language = "pl"),
            TextShortcut(UUID.randomUUID().toString(), "itd", "i tak dalej", caseSensitive = false, isDefault = true, language = "pl"),
            TextShortcut(UUID.randomUUID().toString(), "itp", "i tym podobne", caseSensitive = false, isDefault = true, language = "pl"),
            TextShortcut(UUID.randomUUID().toString(), "tzw", "tak zwany", caseSensitive = false, isDefault = true, language = "pl"),
        )
    }

    private fun greekDefaults(): List<TextShortcut> {
        return listOf(
            // Common Greek abbreviations
            TextShortcut(UUID.randomUUID().toString(), "dx", "δηλαδή", caseSensitive = false, isDefault = true, language = "el"),
            TextShortcut(UUID.randomUUID().toString(), "dhladh", "δηλαδή", caseSensitive = false, isDefault = true, language = "el"),
            TextShortcut(UUID.randomUUID().toString(), "klp", "και λοιπά", caseSensitive = false, isDefault = true, language = "el"),
            TextShortcut(UUID.randomUUID().toString(), "kati", "κάτι", caseSensitive = false, isDefault = true, language = "el"),
            TextShortcut(UUID.randomUUID().toString(), "pou", "που", caseSensitive = false, isDefault = true, language = "el"),
            TextShortcut(UUID.randomUUID().toString(), "gia", "για", caseSensitive = false, isDefault = true, language = "el"),
            TextShortcut(UUID.randomUUID().toString(), "den", "δεν", caseSensitive = false, isDefault = true, language = "el"),
        )
    }

    private fun turkishDefaults(): List<TextShortcut> {
        return listOf(
            // Common Turkish abbreviations
            TextShortcut(UUID.randomUUID().toString(), "mrb", "merhaba", caseSensitive = false, isDefault = true, language = "tr"),
            TextShortcut(UUID.randomUUID().toString(), "tmm", "tamam", caseSensitive = false, isDefault = true, language = "tr"),
            TextShortcut(UUID.randomUUID().toString(), "slm", "selam", caseSensitive = false, isDefault = true, language = "tr"),
            TextShortcut(UUID.randomUUID().toString(), "naber", "ne haber", caseSensitive = false, isDefault = true, language = "tr"),
            TextShortcut(UUID.randomUUID().toString(), "nbr", "ne haber", caseSensitive = false, isDefault = true, language = "tr"),
            TextShortcut(UUID.randomUUID().toString(), "tsk", "teşekkür", caseSensitive = false, isDefault = true, language = "tr"),
            TextShortcut(UUID.randomUUID().toString(), "tsklr", "teşekkürler", caseSensitive = false, isDefault = true, language = "tr"),
            TextShortcut(UUID.randomUUID().toString(), "sgol", "sağ ol", caseSensitive = false, isDefault = true, language = "tr"),
        )
    }

    private fun russianDefaults(): List<TextShortcut> {
        return listOf(
            // Common Russian abbreviations
            TextShortcut(UUID.randomUUID().toString(), "spasibo", "спасибо", caseSensitive = false, isDefault = true, language = "ru"),
            TextShortcut(UUID.randomUUID().toString(), "pozhaluista", "пожалуйста", caseSensitive = false, isDefault = true, language = "ru"),
            TextShortcut(UUID.randomUUID().toString(), "privet", "привет", caseSensitive = false, isDefault = true, language = "ru"),
            TextShortcut(UUID.randomUUID().toString(), "chto", "что", caseSensitive = false, isDefault = true, language = "ru"),
            TextShortcut(UUID.randomUUID().toString(), "kogda", "когда", caseSensitive = false, isDefault = true, language = "ru"),
            TextShortcut(UUID.randomUUID().toString(), "kak", "как", caseSensitive = false, isDefault = true, language = "ru"),
            TextShortcut(UUID.randomUUID().toString(), "gde", "где", caseSensitive = false, isDefault = true, language = "ru"),
        )
    }

    private fun hungarianDefaults(): List<TextShortcut> {
        return listOf(
            // Common Hungarian abbreviations
            TextShortcut(UUID.randomUUID().toString(), "pl", "például", caseSensitive = false, isDefault = true, language = "hu"),
            TextShortcut(UUID.randomUUID().toString(), "stb", "és a többi", caseSensitive = false, isDefault = true, language = "hu"),
            TextShortcut(UUID.randomUUID().toString(), "kb", "körülbelül", caseSensitive = false, isDefault = true, language = "hu"),
            TextShortcut(UUID.randomUUID().toString(), "kb.", "körülbelül", caseSensitive = false, isDefault = true, language = "hu"),
            TextShortcut(UUID.randomUUID().toString(), "tf", "tisztelettel", caseSensitive = false, isDefault = true, language = "hu"),
            TextShortcut(UUID.randomUUID().toString(), "udv", "üdvözlettel", caseSensitive = false, isDefault = true, language = "hu"),
        )
    }

    private fun romanianDefaults(): List<TextShortcut> {
        return listOf(
            // Common Romanian abbreviations
            TextShortcut(UUID.randomUUID().toString(), "vr", "vă rog", caseSensitive = false, isDefault = true, language = "ro"),
            TextShortcut(UUID.randomUUID().toString(), "ms", "mulțumesc", caseSensitive = false, isDefault = true, language = "ro"),
            TextShortcut(UUID.randomUUID().toString(), "mss", "mulțumesc", caseSensitive = false, isDefault = true, language = "ro"),
            TextShortcut(UUID.randomUUID().toString(), "dc", "de ce", caseSensitive = false, isDefault = true, language = "ro"),
            TextShortcut(UUID.randomUUID().toString(), "pt", "pentru", caseSensitive = false, isDefault = true, language = "ro"),
            TextShortcut(UUID.randomUUID().toString(), "ptr", "pentru", caseSensitive = false, isDefault = true, language = "ro"),
            TextShortcut(UUID.randomUUID().toString(), "samd", "și altele multe de", caseSensitive = false, isDefault = true, language = "ro"),
        )
    }

    private fun finnishDefaults(): List<TextShortcut> {
        return listOf(
            // Common Finnish abbreviations
            TextShortcut(UUID.randomUUID().toString(), "esim", "esimerkiksi", caseSensitive = false, isDefault = true, language = "fi"),
            TextShortcut(UUID.randomUUID().toString(), "jne", "ja niin edelleen", caseSensitive = false, isDefault = true, language = "fi"),
            TextShortcut(UUID.randomUUID().toString(), "yms", "ynnä muuta sellaista", caseSensitive = false, isDefault = true, language = "fi"),
            TextShortcut(UUID.randomUUID().toString(), "ym", "ynnä muuta", caseSensitive = false, isDefault = true, language = "fi"),
            TextShortcut(UUID.randomUUID().toString(), "ts", "toisin sanoen", caseSensitive = false, isDefault = true, language = "fi"),
            TextShortcut(UUID.randomUUID().toString(), "mm", "muun muassa", caseSensitive = false, isDefault = true, language = "fi"),
        )
    }

    private fun bulgarianDefaults(): List<TextShortcut> {
        return listOf(
            // Common Bulgarian abbreviations (using Latin transliterations)
            TextShortcut(UUID.randomUUID().toString(), "mnogo", "много", caseSensitive = false, isDefault = true, language = "bg"),
            TextShortcut(UUID.randomUUID().toString(), "blagodarya", "благодаря", caseSensitive = false, isDefault = true, language = "bg"),
            TextShortcut(UUID.randomUUID().toString(), "molya", "моля", caseSensitive = false, isDefault = true, language = "bg"),
            TextShortcut(UUID.randomUUID().toString(), "zdravei", "здравей", caseSensitive = false, isDefault = true, language = "bg"),
            TextShortcut(UUID.randomUUID().toString(), "kak", "как", caseSensitive = false, isDefault = true, language = "bg"),
            TextShortcut(UUID.randomUUID().toString(), "kakvo", "какво", caseSensitive = false, isDefault = true, language = "bg"),
        )
    }
}
