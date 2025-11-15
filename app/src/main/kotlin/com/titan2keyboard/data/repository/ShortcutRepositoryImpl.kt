package com.titan2keyboard.data.repository

import com.titan2keyboard.domain.model.TextShortcut
import com.titan2keyboard.domain.repository.ShortcutRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ShortcutRepository with default shortcuts
 */
@Singleton
class ShortcutRepositoryImpl @Inject constructor() : ShortcutRepository {

    private val shortcuts = listOf(
        // Common contractions
        TextShortcut("Im", "I'm", caseSensitive = false),
        TextShortcut("Ive", "I've", caseSensitive = false),
        TextShortcut("Ill", "I'll", caseSensitive = false),
        TextShortcut("Id", "I'd", caseSensitive = false),
        TextShortcut("dont", "don't", caseSensitive = false),
        TextShortcut("doesnt", "doesn't", caseSensitive = false),
        TextShortcut("didnt", "didn't", caseSensitive = false),
        TextShortcut("cant", "can't", caseSensitive = false),
        TextShortcut("couldnt", "couldn't", caseSensitive = false),
        TextShortcut("wouldnt", "wouldn't", caseSensitive = false),
        TextShortcut("shouldnt", "shouldn't", caseSensitive = false),
        TextShortcut("isnt", "isn't", caseSensitive = false),
        TextShortcut("arent", "aren't", caseSensitive = false),
        TextShortcut("wasnt", "wasn't", caseSensitive = false),
        TextShortcut("werent", "weren't", caseSensitive = false),
        TextShortcut("hasnt", "hasn't", caseSensitive = false),
        TextShortcut("havent", "haven't", caseSensitive = false),
        TextShortcut("hadnt", "hadn't", caseSensitive = false),
        TextShortcut("wont", "won't", caseSensitive = false),
        TextShortcut("thats", "that's", caseSensitive = false),
        TextShortcut("theres", "there's", caseSensitive = false),
        TextShortcut("heres", "here's", caseSensitive = false),
        TextShortcut("whats", "what's", caseSensitive = false),
        TextShortcut("wheres", "where's", caseSensitive = false),
        TextShortcut("whos", "who's", caseSensitive = false),
        TextShortcut("hows", "how's", caseSensitive = false),
        TextShortcut("whens", "when's", caseSensitive = false),
        TextShortcut("whys", "why's", caseSensitive = false),
        TextShortcut("youre", "you're", caseSensitive = false),
        TextShortcut("youve", "you've", caseSensitive = false),
        TextShortcut("youll", "you'll", caseSensitive = false),
        TextShortcut("youd", "you'd", caseSensitive = false),
        TextShortcut("theyre", "they're", caseSensitive = false),
        TextShortcut("theyve", "they've", caseSensitive = false),
        TextShortcut("theyll", "they'll", caseSensitive = false),
        TextShortcut("theyd", "they'd", caseSensitive = false),
        TextShortcut("weve", "we've", caseSensitive = false),
        TextShortcut("wed", "we'd", caseSensitive = false),
        TextShortcut("shes", "she's", caseSensitive = false),
        TextShortcut("hes", "he's", caseSensitive = false),
        TextShortcut("lets", "let's", caseSensitive = false),

        // Common typos
        TextShortcut("teh", "the", caseSensitive = false),
        TextShortcut("recieve", "receive", caseSensitive = false),
        TextShortcut("occured", "occurred", caseSensitive = false),
        TextShortcut("seperate", "separate", caseSensitive = false),
        TextShortcut("definately", "definitely", caseSensitive = false),
        TextShortcut("alot", "a lot", caseSensitive = false),
    )

    // Build a map for fast lookup (case-insensitive)
    private val shortcutMap: Map<String, TextShortcut> = shortcuts.associateBy {
        if (it.caseSensitive) it.trigger else it.trigger.lowercase()
    }

    override fun getShortcuts(): List<TextShortcut> = shortcuts

    override fun findReplacement(text: String): String? {
        val lookupKey = text.lowercase()
        val shortcut = shortcutMap[lookupKey] ?: return null

        // Preserve the case of the original text
        return when {
            // If original is all uppercase, make replacement uppercase
            text.all { it.isUpperCase() || !it.isLetter() } -> shortcut.replacement.uppercase()
            // If original starts with uppercase, capitalize replacement
            text.firstOrNull()?.isUpperCase() == true -> shortcut.replacement.replaceFirstChar { it.uppercase() }
            // Otherwise use replacement as-is
            else -> shortcut.replacement
        }
    }
}
