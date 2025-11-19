package com.titan2keyboard.domain.model

/**
 * Categories of symbols in the BB OS 7-style picker
 */
enum class SymbolCategory(val displayName: String) {
    PUNCTUATION("Punctuation"),
    CURRENCY("Currency"),
    MATH("Math"),
    ARROWS("Arrows"),
    EMOJI("Emoji"),
    MISC("Misc")
}

/**
 * Symbol data for display in the picker
 */
data class Symbol(
    val character: String,
    val description: String = ""
)

/**
 * Symbols organized by category - BB OS 7 style layout
 */
object SymbolData {

    val categories = SymbolCategory.entries.toList()

    private val punctuationSymbols = listOf(
        Symbol("!", "Exclamation"),
        Symbol("?", "Question"),
        Symbol(".", "Period"),
        Symbol(",", "Comma"),
        Symbol(":", "Colon"),
        Symbol(";", "Semicolon"),
        Symbol("'", "Apostrophe"),
        Symbol("\"", "Quote"),
        Symbol("`", "Backtick"),
        Symbol("~", "Tilde"),
        Symbol("-", "Hyphen"),
        Symbol("_", "Underscore"),
        Symbol("(", "Open paren"),
        Symbol(")", "Close paren"),
        Symbol("[", "Open bracket"),
        Symbol("]", "Close bracket"),
        Symbol("{", "Open brace"),
        Symbol("}", "Close brace"),
        Symbol("<", "Less than"),
        Symbol(">", "Greater than"),
        Symbol("/", "Slash"),
        Symbol("\\", "Backslash"),
        Symbol("|", "Pipe"),
        Symbol("@", "At"),
        Symbol("#", "Hash"),
        Symbol("&", "Ampersand"),
        Symbol("*", "Asterisk"),
        Symbol("^", "Caret"),
        Symbol("%", "Percent"),
        Symbol("...", "Ellipsis")
    )

    private val currencySymbols = listOf(
        Symbol("$", "Dollar"),
        Symbol("€", "Euro"),
        Symbol("£", "Pound"),
        Symbol("¥", "Yen"),
        Symbol("₹", "Rupee"),
        Symbol("₽", "Ruble"),
        Symbol("₿", "Bitcoin"),
        Symbol("¢", "Cent"),
        Symbol("₩", "Won"),
        Symbol("₪", "Shekel"),
        Symbol("₫", "Dong"),
        Symbol("₱", "Peso"),
        Symbol("฿", "Baht"),
        Symbol("₴", "Hryvnia"),
        Symbol("₦", "Naira"),
        Symbol("₲", "Guarani"),
        Symbol("₵", "Cedi"),
        Symbol("₡", "Colon"),
        Symbol("₸", "Tenge"),
        Symbol("₺", "Lira")
    )

    private val mathSymbols = listOf(
        Symbol("+", "Plus"),
        Symbol("-", "Minus"),
        Symbol("×", "Multiply"),
        Symbol("÷", "Divide"),
        Symbol("=", "Equals"),
        Symbol("≠", "Not equal"),
        Symbol("≈", "Approx"),
        Symbol("<", "Less than"),
        Symbol(">", "Greater than"),
        Symbol("≤", "Less or equal"),
        Symbol("≥", "Greater or equal"),
        Symbol("±", "Plus minus"),
        Symbol("∞", "Infinity"),
        Symbol("√", "Square root"),
        Symbol("∑", "Sum"),
        Symbol("∏", "Product"),
        Symbol("∫", "Integral"),
        Symbol("π", "Pi"),
        Symbol("°", "Degree"),
        Symbol("′", "Prime"),
        Symbol("″", "Double prime"),
        Symbol("‰", "Per mille"),
        Symbol("∂", "Partial"),
        Symbol("∆", "Delta"),
        Symbol("∇", "Nabla"),
        Symbol("µ", "Micro"),
        Symbol("∈", "Element of"),
        Symbol("∉", "Not element"),
        Symbol("⊂", "Subset"),
        Symbol("∪", "Union")
    )

    private val arrowSymbols = listOf(
        Symbol("←", "Left"),
        Symbol("→", "Right"),
        Symbol("↑", "Up"),
        Symbol("↓", "Down"),
        Symbol("↔", "Left right"),
        Symbol("↕", "Up down"),
        Symbol("↖", "Upper left"),
        Symbol("↗", "Upper right"),
        Symbol("↘", "Lower right"),
        Symbol("↙", "Lower left"),
        Symbol("⇐", "Double left"),
        Symbol("⇒", "Double right"),
        Symbol("⇑", "Double up"),
        Symbol("⇓", "Double down"),
        Symbol("⇔", "Double horiz"),
        Symbol("⇕", "Double vert"),
        Symbol("↵", "Return"),
        Symbol("↩", "Return left"),
        Symbol("↪", "Return right"),
        Symbol("⟲", "Anticlockwise"),
        Symbol("⟳", "Clockwise"),
        Symbol("↻", "Clockwise arrow"),
        Symbol("↺", "Counter arrow"),
        Symbol("⤴", "Right up"),
        Symbol("⤵", "Right down"),
        Symbol("➔", "Heavy right"),
        Symbol("➜", "Heavy right 2"),
        Symbol("➤", "Triangle right"),
        Symbol("►", "Play"),
        Symbol("◄", "Play left")
    )

    private val emojiSymbols = listOf(
        Symbol("😀", "Smile"),
        Symbol("😃", "Grin"),
        Symbol("😄", "Happy"),
        Symbol("😊", "Blush"),
        Symbol("🙂", "Slight smile"),
        Symbol("😉", "Wink"),
        Symbol("😍", "Heart eyes"),
        Symbol("😘", "Kiss"),
        Symbol("😎", "Cool"),
        Symbol("🤔", "Think"),
        Symbol("😢", "Cry"),
        Symbol("😭", "Sob"),
        Symbol("😤", "Huff"),
        Symbol("😠", "Angry"),
        Symbol("🤯", "Mind blown"),
        Symbol("👍", "Thumbs up"),
        Symbol("👎", "Thumbs down"),
        Symbol("👋", "Wave"),
        Symbol("🙏", "Pray"),
        Symbol("💪", "Strong"),
        Symbol("❤️", "Heart"),
        Symbol("💔", "Broken heart"),
        Symbol("⭐", "Star"),
        Symbol("🔥", "Fire"),
        Symbol("✨", "Sparkle"),
        Symbol("💯", "100"),
        Symbol("✅", "Check"),
        Symbol("❌", "Cross"),
        Symbol("⚠️", "Warning"),
        Symbol("🎉", "Party")
    )

    private val miscSymbols = listOf(
        Symbol("©", "Copyright"),
        Symbol("®", "Registered"),
        Symbol("™", "Trademark"),
        Symbol("§", "Section"),
        Symbol("¶", "Paragraph"),
        Symbol("†", "Dagger"),
        Symbol("‡", "Double dag"),
        Symbol("•", "Bullet"),
        Symbol("·", "Middle dot"),
        Symbol("…", "Ellipsis"),
        Symbol("—", "Em dash"),
        Symbol("–", "En dash"),
        Symbol("‹", "Single left"),
        Symbol("›", "Single right"),
        Symbol("«", "Double left"),
        Symbol("»", "Double right"),
        Symbol("№", "Numero"),
        Symbol("℃", "Celsius"),
        Symbol("℉", "Fahrenheit"),
        Symbol("♠", "Spade"),
        Symbol("♥", "Heart"),
        Symbol("♦", "Diamond"),
        Symbol("♣", "Club"),
        Symbol("♪", "Note"),
        Symbol("♫", "Notes"),
        Symbol("☀", "Sun"),
        Symbol("☁", "Cloud"),
        Symbol("☂", "Umbrella"),
        Symbol("★", "Black star"),
        Symbol("☆", "White star")
    )

    /**
     * Get symbols for a given category
     */
    fun getSymbolsForCategory(category: SymbolCategory): List<Symbol> {
        return when (category) {
            SymbolCategory.PUNCTUATION -> punctuationSymbols
            SymbolCategory.CURRENCY -> currencySymbols
            SymbolCategory.MATH -> mathSymbols
            SymbolCategory.ARROWS -> arrowSymbols
            SymbolCategory.EMOJI -> emojiSymbols
            SymbolCategory.MISC -> miscSymbols
        }
    }

    /**
     * Get the default category to show
     */
    fun getDefaultCategory(): SymbolCategory = SymbolCategory.PUNCTUATION
}
