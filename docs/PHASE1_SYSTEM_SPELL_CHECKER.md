# Phase 1: System-Wide Spell Checker Implementation
## titan2keyboard - Android SpellCheckerService with SymSpellKt

**Duration:** 1.5 weeks (8 working days)
**Goal:** Ship system-wide spell checking service that any app can use

---

## Overview

Phase 1 delivers a production-ready **system-wide spell checking service** for English using SymSpellKt. This is NOT tied to your keyboard - it's a separate Android service that:

- ✅ Works with ANY keyboard (Gboard, SwiftKey, your IME, etc.)
- ✅ Works in ANY app (Gmail, Messages, Chrome, etc.)
- ✅ Appears in Settings → Language & Input → Spell Checker
- ✅ Android handles ALL UI (underlines, suggestion popups)
- ✅ You control the dictionary and algorithm
- ✅ Completely offline and privacy-first

---

## Architecture Overview

```
┌──────────────────────────────────────────────────────────┐
│            Android System & Applications                  │
│  (Text fields, IMEs - show underlines & suggestions)     │
└────────────────────────┬─────────────────────────────────┘
                         │
                         │ TextService API
                         ▼
┌──────────────────────────────────────────────────────────┐
│           Titan2SpellCheckerService                       │
│        (extends SpellCheckerService)                      │
│                                                           │
│  - Registered with BIND_TEXT_SERVICE permission          │
│  - Creates Titan2SpellSession for each client            │
│  - Manages service lifecycle                             │
└────────────────────────┬─────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│              Titan2SpellSession                           │
│           (extends SpellCheckerService.Session)          │
│                                                           │
│  - onGetSuggestions(TextInfo) → SuggestionsInfo          │
│  - onGetSentenceSuggestions(TextInfo[]) → SentenceSuggestions │
│  - Handles individual spell check requests               │
└────────────────────────┬─────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│               SpellCheckRepository                        │
│              (Business Logic Layer)                       │
│  - checkWord(word: String): SpellCheckResult             │
│  - checkSentence(text: String): List<SpellCheckResult>  │
└────────────────────────┬─────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│                  SymSpellEngine                           │
│              (Core Spell Check Engine)                    │
│  - symSpell: SymSpell instance                            │
│  - lookup(word: String): List<SuggestionItem>            │
└────────────────────────┬─────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│                  DictionaryManager                        │
│              (Dictionary Loading & Caching)               │
│  - loadDictionary(locale: Locale)                         │
│  - dictionaryState: StateFlow<DictionaryLoadState>       │
└──────────────────────────────────────────────────────────┘
```

**Key Points:**
- Android system manages ALL UI (underlines, popups)
- Your service only provides: "Is this word correct? If not, what are suggestions?"
- No custom UI needed - Android handles everything
- Service runs independently of your IME

---

## Task Breakdown

### Week 1: Core Infrastructure

#### **Task 1.1: Project Setup & Dependencies** (Day 1, 2 hours)

**Objective:** Add SymSpellKt dependency and configure build system

**Steps:**
1. Update `gradle/libs.versions.toml`
2. Add dependency to `app/build.gradle.kts`
3. Sync and verify build

**Implementation:**

```toml
# gradle/libs.versions.toml
[versions]
symspell = "3.4.0"
timber = "5.0.1"

[libraries]
symspell-kt = { group = "com.darkrockstudios", name = "symspellkt", version.ref = "symspell" }
timber = { group = "com.jakewharton.timber", name = "timber", version.ref = "timber" }
```

```kotlin
// app/build.gradle.kts
dependencies {
    implementation(libs.symspell.kt)
    implementation(libs.timber)

    // Ensure these are present
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.datastore.preferences)
}
```

**Acceptance Criteria:**
- ✅ Project builds successfully
- ✅ SymSpellKt classes are importable
- ✅ No version conflicts

**Deliverable:** Commit "feat: add SymSpellKt dependency for spell checking"

---

#### **Task 1.2: Dictionary Preparation** (Day 1, 4 hours)

**Objective:** Obtain, prepare, and bundle English dictionary files

**Steps:**
1. Download SymSpell English dictionary
2. Create `app/src/main/assets/dictionaries/` directory
3. Add dictionary files with proper format
4. Document dictionary format and sources

**Dictionary Format:**
```
# frequency_dictionary_en_82_765.txt
the 23135851162
of 13151942776
and 12997637966
to 12136980858
a 9081174698
```

**Directory Structure:**
```
app/src/main/assets/
└── dictionaries/
    ├── en-US.txt          # Main word frequency dictionary
    └── README.md          # Dictionary sources and format
```

**Implementation:**

```markdown
# app/src/main/assets/dictionaries/README.md

## Dictionary Sources

### en-US.txt
- Source: SymSpell frequency_dictionary_en_82_765.txt
- URL: https://github.com/wolfgarbe/SymSpell/tree/master/SymSpell.FrequencyDictionary
- Format: `word frequency` (space-separated)
- Entries: 82,765 words
- License: MIT

## Format Specification

Each line contains:
```
<word><space><frequency>
```

Example:
```
hello 12345678
world 23456789
```
```

**Acceptance Criteria:**
- ✅ Dictionary file is in `assets/dictionaries/`
- ✅ File size is reasonable (<5MB)
- ✅ Format is correct (word + frequency)
- ✅ Documentation exists

**Deliverable:** Commit "feat: add English dictionary for spell checking"

---

#### **Task 1.3: Domain Models** (Day 1, 2 hours)

**Objective:** Define domain models for spell checking

**Implementation:**

```kotlin
// app/src/main/kotlin/com/titan2keyboard/domain/model/SpellCheck.kt

package com.titan2keyboard.domain.model

/**
 * Represents a word that has been spell-checked
 */
data class SpellCheckResult(
    val originalWord: String,
    val isCorrect: Boolean,
    val suggestions: List<Suggestion>,
    val position: IntRange? = null // Position in text if applicable
)

/**
 * A spelling correction suggestion
 */
data class Suggestion(
    val word: String,
    val distance: Int,        // Edit distance from original
    val frequency: Long       // Word frequency (higher = more common)
) {
    companion object {
        /**
         * Sort suggestions by relevance (distance, then frequency)
         */
        fun List<Suggestion>.sortByRelevance(): List<Suggestion> =
            sortedWith(compareBy({ it.distance }, { -it.frequency }))
    }
}

/**
 * State of dictionary loading
 */
sealed class DictionaryLoadState {
    data object Unloaded : DictionaryLoadState()
    data object Loading : DictionaryLoadState()
    data class Loaded(val wordCount: Int, val locale: String) : DictionaryLoadState()
    data class Error(val message: String, val cause: Throwable? = null) : DictionaryLoadState()
}

/**
 * Result of sentence-level spell checking
 */
data class SentenceSpellCheckResult(
    val originalText: String,
    val wordResults: List<WordInSentence>
)

/**
 * A word within a sentence with its spell check result
 */
data class WordInSentence(
    val word: String,
    val start: Int,
    val end: Int,
    val isCorrect: Boolean,
    val suggestions: List<Suggestion>
)
```

**Acceptance Criteria:**
- ✅ Models are immutable (data classes)
- ✅ KDoc documentation present
- ✅ Models follow clean architecture (domain layer)

**Deliverable:** Commit "feat: add spell check domain models"

---

#### **Task 1.4: SymSpell Engine Wrapper** (Day 2, 4 hours)

**Objective:** Create wrapper around SymSpell library with proper initialization

**Implementation:**

```kotlin
// app/src/main/kotlin/com/titan2keyboard/data/spellcheck/SymSpellEngine.kt

package com.titan2keyboard.data.spellcheck

import android.content.Context
import com.darkrockstudios.symspellkt.api.SymSpell
import com.darkrockstudios.symspellkt.common.Verbosity
import com.darkrockstudios.symspellkt.common.SuggestionItem
import com.titan2keyboard.domain.model.Suggestion
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core spell checking engine using SymSpell algorithm
 */
@Singleton
class SymSpellEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var symSpell: SymSpell? = null
    private var isInitialized = false

    companion object {
        private const val TAG = "SymSpellEngine"
        private const val MAX_EDIT_DISTANCE = 2
        private const val PREFIX_LENGTH = 7
        private const val DEFAULT_DICTIONARY = "dictionaries/en-US.txt"
    }

    /**
     * Initialize the spell check engine with dictionary
     * Should be called once on background thread during app startup
     *
     * @param dictionaryPath Path to dictionary file in assets
     * @return Number of words loaded, or -1 on error
     */
    suspend fun initialize(
        dictionaryPath: String = DEFAULT_DICTIONARY
    ): Int = withContext(Dispatchers.IO) {
        try {
            Timber.d("Initializing SymSpell with dictionary: $dictionaryPath")

            val spell = SymSpell(
                maxDictionaryEditDistance = MAX_EDIT_DISTANCE,
                prefixLength = PREFIX_LENGTH
            )

            // Load dictionary from assets
            var wordCount = 0
            context.assets.open(dictionaryPath).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.forEachLine { line ->
                        val parts = line.trim().split(" ", limit = 2)
                        if (parts.size == 2) {
                            val word = parts[0]
                            val frequency = parts[1].toLongOrNull() ?: 0L
                            spell.createDictionaryEntry(word, frequency.toDouble())
                            wordCount++
                        }
                    }
                }
            }

            symSpell = spell
            isInitialized = true

            Timber.i("SymSpell initialized successfully with $wordCount words")
            wordCount
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize SymSpell")
            isInitialized = false
            -1
        }
    }

    /**
     * Check if a word is spelled correctly
     *
     * @param word The word to check
     * @return true if word is in dictionary (correctly spelled)
     */
    fun isCorrect(word: String): Boolean {
        if (!isInitialized || word.isBlank()) return true

        val suggestions = symSpell?.lookup(
            word,
            Verbosity.Top,
            maxEditDistance = 0 // Exact match only
        ) ?: return false

        return suggestions.isNotEmpty() &&
               suggestions[0].term.equals(word, ignoreCase = true)
    }

    /**
     * Get spelling suggestions for a word
     *
     * @param word The word to get suggestions for
     * @param maxEditDistance Maximum edit distance (1-3)
     * @param maxSuggestions Maximum number of suggestions to return
     * @return List of suggestions, sorted by relevance
     */
    fun getSuggestions(
        word: String,
        maxEditDistance: Int = MAX_EDIT_DISTANCE,
        maxSuggestions: Int = 5
    ): List<Suggestion> {
        if (!isInitialized || word.isBlank()) return emptyList()

        val results = symSpell?.lookup(
            word,
            Verbosity.Closest,
            maxEditDistance
        ) ?: return emptyList()

        return results
            .take(maxSuggestions)
            .map { it.toSuggestion() }
    }

    /**
     * Perform comprehensive spell check with suggestions
     *
     * @param word The word to check
     * @return Suggestions list (empty if word is correct)
     */
    fun checkWord(word: String): List<Suggestion> {
        if (!isInitialized || word.isBlank()) return emptyList()

        // Quick check if word is correct
        if (isCorrect(word)) return emptyList()

        // Get suggestions for misspelled word
        return getSuggestions(word)
    }

    /**
     * Convert SymSpell SuggestionItem to our domain Suggestion
     */
    private fun SuggestionItem.toSuggestion(): Suggestion {
        return Suggestion(
            word = term,
            distance = distance,
            frequency = count.toLong()
        )
    }

    /**
     * Check if engine is ready to use
     */
    fun isReady(): Boolean = isInitialized && symSpell != null
}
```

**Acceptance Criteria:**
- ✅ Engine loads dictionary successfully
- ✅ `isCorrect()` returns true for valid words
- ✅ `getSuggestions()` returns relevant suggestions
- ✅ Proper error handling and logging
- ✅ Thread-safe initialization

**Deliverable:** Commit "feat: implement SymSpell engine wrapper"

---

#### **Task 1.5: Dictionary Manager** (Day 2, 3 hours)

**Objective:** Manage dictionary loading with lifecycle awareness

**Implementation:**

```kotlin
// app/src/main/kotlin/com/titan2keyboard/data/spellcheck/DictionaryManager.kt

package com.titan2keyboard.data.spellcheck

import com.titan2keyboard.domain.model.DictionaryLoadState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages dictionary loading lifecycle and state
 */
@Singleton
class DictionaryManager @Inject constructor(
    private val symSpellEngine: SymSpellEngine
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _loadState = MutableStateFlow<DictionaryLoadState>(
        DictionaryLoadState.Unloaded
    )
    val loadState: StateFlow<DictionaryLoadState> = _loadState.asStateFlow()

    /**
     * Load dictionary for specified locale
     * This is idempotent - calling multiple times won't reload
     */
    fun loadDictionary(locale: String = "en-US") {
        if (_loadState.value is DictionaryLoadState.Loading ||
            _loadState.value is DictionaryLoadState.Loaded) {
            Timber.d("Dictionary already loading or loaded, skipping")
            return
        }

        _loadState.value = DictionaryLoadState.Loading

        scope.launch {
            try {
                val dictionaryPath = "dictionaries/$locale.txt"
                val wordCount = symSpellEngine.initialize(dictionaryPath)

                if (wordCount > 0) {
                    _loadState.value = DictionaryLoadState.Loaded(
                        wordCount = wordCount,
                        locale = locale
                    )
                    Timber.i("Dictionary loaded: $wordCount words for $locale")
                } else {
                    _loadState.value = DictionaryLoadState.Error(
                        message = "Failed to load dictionary: no words loaded"
                    )
                    Timber.e("Dictionary load failed: wordCount = $wordCount")
                }
            } catch (e: Exception) {
                _loadState.value = DictionaryLoadState.Error(
                    message = "Failed to load dictionary: ${e.message}",
                    cause = e
                )
                Timber.e(e, "Dictionary load exception")
            }
        }
    }

    /**
     * Check if dictionary is ready for spell checking
     */
    fun isReady(): Boolean = _loadState.value is DictionaryLoadState.Loaded
}
```

**Acceptance Criteria:**
- ✅ Dictionary loads asynchronously
- ✅ State flow emits loading states
- ✅ Idempotent (won't reload if already loaded)
- ✅ Proper error handling

**Deliverable:** Commit "feat: add dictionary manager with state management"

---

#### **Task 1.6: Spell Check Repository** (Day 3, 4 hours)

**Objective:** Create repository layer for spell checking business logic

**Implementation:**

```kotlin
// app/src/main/kotlin/com/titan2keyboard/domain/repository/SpellCheckRepository.kt

package com.titan2keyboard.domain.repository

import com.titan2keyboard.domain.model.SpellCheckResult
import com.titan2keyboard.domain.model.SentenceSpellCheckResult
import com.titan2keyboard.domain.model.Suggestion

/**
 * Repository interface for spell checking operations
 */
interface SpellCheckRepository {
    /**
     * Check spelling of a single word
     */
    suspend fun checkWord(word: String): SpellCheckResult

    /**
     * Check spelling of an entire sentence
     */
    suspend fun checkSentence(text: String): SentenceSpellCheckResult

    /**
     * Get spelling suggestions for a word
     */
    suspend fun getSuggestions(word: String, limit: Int = 5): List<Suggestion>
}
```

```kotlin
// app/src/main/kotlin/com/titan2keyboard/data/repository/SpellCheckRepositoryImpl.kt

package com.titan2keyboard.data.repository

import com.titan2keyboard.data.spellcheck.DictionaryManager
import com.titan2keyboard.data.spellcheck.SymSpellEngine
import com.titan2keyboard.domain.model.SpellCheckResult
import com.titan2keyboard.domain.model.SentenceSpellCheckResult
import com.titan2keyboard.domain.model.Suggestion
import com.titan2keyboard.domain.model.WordInSentence
import com.titan2keyboard.domain.repository.SpellCheckRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for spell checking
 */
@Singleton
class SpellCheckRepositoryImpl @Inject constructor(
    private val symSpellEngine: SymSpellEngine,
    private val dictionaryManager: DictionaryManager
) : SpellCheckRepository {

    companion object {
        private const val TAG = "SpellCheckRepository"
        private const val MAX_EDIT_DISTANCE = 2
        private const val MAX_SUGGESTIONS = 5

        // Words to skip checking
        private val SKIP_PATTERNS = listOf(
            Regex("^\\d+$"),              // Pure numbers
            Regex("^[A-Z0-9_]+$"),        // ALL_CAPS (constants)
            Regex("^\\w+://.*"),          // URLs
            Regex("^[\\w.+-]+@[\\w.-]+"), // Emails
            Regex("^#\\w+"),              // Hashtags
            Regex("^@\\w+")               // Mentions
        )
    }

    override suspend fun checkWord(word: String): SpellCheckResult = withContext(Dispatchers.Default) {
        // Skip if dictionary not ready
        if (!dictionaryManager.isReady()) {
            return@withContext SpellCheckResult(
                originalWord = word,
                isCorrect = true,
                suggestions = emptyList()
            )
        }

        // Skip certain patterns
        if (shouldSkipWord(word)) {
            return@withContext SpellCheckResult(
                originalWord = word,
                isCorrect = true,
                suggestions = emptyList()
            )
        }

        val isCorrect = symSpellEngine.isCorrect(word)

        val suggestions = if (!isCorrect) {
            symSpellEngine.getSuggestions(
                word = word,
                maxEditDistance = MAX_EDIT_DISTANCE,
                maxSuggestions = MAX_SUGGESTIONS
            )
        } else {
            emptyList()
        }

        SpellCheckResult(
            originalWord = word,
            isCorrect = isCorrect,
            suggestions = suggestions
        )
    }

    override suspend fun checkSentence(text: String): SentenceSpellCheckResult = withContext(Dispatchers.Default) {
        val wordResults = mutableListOf<WordInSentence>()

        // Extract words using regex
        val wordRegex = Regex("\\b[a-zA-Z]+\\b")
        wordRegex.findAll(text).forEach { match ->
            val word = match.value
            val result = checkWord(word)

            wordResults.add(
                WordInSentence(
                    word = word,
                    start = match.range.first,
                    end = match.range.last + 1,
                    isCorrect = result.isCorrect,
                    suggestions = result.suggestions
                )
            )
        }

        SentenceSpellCheckResult(
            originalText = text,
            wordResults = wordResults
        )
    }

    override suspend fun getSuggestions(
        word: String,
        limit: Int
    ): List<Suggestion> = withContext(Dispatchers.Default) {
        if (!dictionaryManager.isReady()) return@withContext emptyList()

        symSpellEngine.getSuggestions(
            word = word,
            maxSuggestions = limit
        )
    }

    /**
     * Determine if word should skip spell checking
     */
    private fun shouldSkipWord(word: String): Boolean {
        if (word.length <= 1) return true
        return SKIP_PATTERNS.any { it.matches(word) }
    }
}
```

**Acceptance Criteria:**
- ✅ Repository checks words correctly
- ✅ Skips numbers, URLs, emails, etc.
- ✅ Returns relevant suggestions
- ✅ Handles sentences (batch checking)
- ✅ Proper separation of concerns

**Deliverable:** Commit "feat: implement spell check repository layer"

---

#### **Task 1.7: Dependency Injection Setup** (Day 3, 2 hours)

**Objective:** Configure Hilt modules for spell checking dependencies

**Implementation:**

```kotlin
// app/src/main/kotlin/com/titan2keyboard/di/SpellCheckModule.kt

package com.titan2keyboard.di

import com.titan2keyboard.data.repository.SpellCheckRepositoryImpl
import com.titan2keyboard.domain.repository.SpellCheckRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SpellCheckModule {

    @Binds
    @Singleton
    abstract fun bindSpellCheckRepository(
        impl: SpellCheckRepositoryImpl
    ): SpellCheckRepository
}
```

**Acceptance Criteria:**
- ✅ All dependencies are injectable
- ✅ Singleton scopes are correct
- ✅ App builds successfully

**Deliverable:** Commit "feat: add Hilt DI configuration for spell checking"

---

### Week 2: SpellCheckerService Implementation

#### **Task 2.1: Spell Checker Session** (Day 4, 4 hours)

**Objective:** Implement SpellCheckerService.Session for handling spell check requests

**Implementation:**

```kotlin
// app/src/main/kotlin/com/titan2keyboard/service/Titan2SpellSession.kt

package com.titan2keyboard.service

import android.service.textservice.SpellCheckerService
import android.service.textservice.SentenceSuggestionsInfo
import android.service.textservice.SuggestionsInfo
import android.view.textservice.SentenceWordItem
import android.view.textservice.SuggestionsInfo as TextSuggestionsInfo
import android.view.textservice.TextInfo
import com.titan2keyboard.domain.repository.SpellCheckRepository
import kotlinx.coroutines.runBlocking
import timber.log.Timber

/**
 * Spell checking session for handling individual client requests
 */
class Titan2SpellSession(
    private val repository: SpellCheckRepository
) : SpellCheckerService.Session() {

    companion object {
        private const val TAG = "Titan2SpellSession"
    }

    /**
     * Called when system requests spell check for a single word
     */
    override fun onGetSuggestions(
        textInfo: TextInfo,
        suggestionsLimit: Int
    ): SuggestionsInfo {
        val word = textInfo.text.toString()
        Timber.d("onGetSuggestions: word='$word', limit=$suggestionsLimit")

        // Run spell check synchronously (Android expects synchronous response)
        val result = runBlocking {
            repository.checkWord(word)
        }

        return if (result.isCorrect) {
            // Word is correct - no suggestions needed
            SuggestionsInfo(
                SuggestionsInfo.RESULT_ATTR_IN_THE_DICTIONARY,
                emptyArray()
            )
        } else {
            // Word is misspelled - return suggestions
            val suggestions = result.suggestions
                .take(suggestionsLimit)
                .map { it.word }
                .toTypedArray()

            SuggestionsInfo(
                SuggestionsInfo.RESULT_ATTR_LOOKS_LIKE_TYPO,
                suggestions
            ).apply {
                // Set cookie to maintain context (optional)
                setCookieAndSequence(textInfo.cookie, textInfo.sequence)
            }
        }
    }

    /**
     * Called when system requests spell check for a sentence or paragraph
     * This is more efficient than checking word-by-word
     */
    override fun onGetSentenceSuggestionsMultiple(
        textInfos: Array<out TextInfo>,
        suggestionsLimit: Int
    ): Array<SentenceSuggestionsInfo> {
        Timber.d("onGetSentenceSuggestionsMultiple: ${textInfos.size} sentences")

        return textInfos.map { textInfo ->
            val text = textInfo.text.toString()

            // Check entire sentence
            val sentenceResult = runBlocking {
                repository.checkSentence(text)
            }

            // Build SentenceWordItem for each word
            val wordItems = sentenceResult.wordResults
                .filter { !it.isCorrect } // Only include misspelled words
                .map { wordResult ->
                    val suggestions = wordResult.suggestions
                        .take(suggestionsLimit)
                        .map { it.word }
                        .toTypedArray()

                    SentenceWordItem(
                        wordResult.start,
                        wordResult.end - wordResult.start,
                        SuggestionsInfo(
                            SuggestionsInfo.RESULT_ATTR_LOOKS_LIKE_TYPO,
                            suggestions
                        )
                    )
                }
                .toTypedArray()

            // Return sentence suggestions
            SentenceSuggestionsInfo(wordItems).apply {
                setCookieAndSequence(textInfo.cookie, textInfo.sequence)
            }
        }.toTypedArray()
    }

    override fun onCancel() {
        Timber.d("Session cancelled")
        // Clean up if needed
    }

    override fun onClose() {
        Timber.d("Session closed")
        // Clean up resources
    }
}
```

**Acceptance Criteria:**
- ✅ Handles single word requests correctly
- ✅ Handles sentence-level requests efficiently
- ✅ Returns proper SuggestionsInfo flags
- ✅ Manages session lifecycle

**Deliverable:** Commit "feat: implement spell checker session"

---

#### **Task 2.2: SpellCheckerService Implementation** (Day 4-5, 6 hours)

**Objective:** Implement main SpellCheckerService that Android binds to

**Implementation:**

```kotlin
// app/src/main/kotlin/com/titan2keyboard/service/Titan2SpellCheckerService.kt

package com.titan2keyboard.service

import android.service.textservice.SpellCheckerService
import com.titan2keyboard.data.spellcheck.DictionaryManager
import com.titan2keyboard.domain.repository.SpellCheckRepository
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * System-wide spell checking service
 *
 * This service is registered with the Android system and can be used by any app
 * or keyboard. It runs independently of the Titan2 IME.
 */
@AndroidEntryPoint
class Titan2SpellCheckerService : SpellCheckerService() {

    @Inject
    lateinit var repository: SpellCheckRepository

    @Inject
    lateinit var dictionaryManager: DictionaryManager

    companion object {
        private const val TAG = "Titan2SpellChecker"
    }

    override fun onCreate() {
        super.onCreate()
        Timber.i("SpellCheckerService created")

        // Load dictionary asynchronously
        dictionaryManager.loadDictionary("en-US")
    }

    /**
     * Called when a client (app/keyboard) requests a spell checking session
     */
    override fun createSession(): Session {
        Timber.d("Creating new spell check session")
        return Titan2SpellSession(repository)
    }

    override fun onDestroy() {
        Timber.i("SpellCheckerService destroyed")
        super.onDestroy()
    }
}
```

**Acceptance Criteria:**
- ✅ Service initializes correctly
- ✅ Dictionary loads on service creation
- ✅ Creates sessions for clients
- ✅ Hilt injection works

**Deliverable:** Commit "feat: implement system-wide spell checker service"

---

#### **Task 2.3: Manifest Configuration** (Day 5, 2 hours)

**Objective:** Configure AndroidManifest.xml for spell checker service

**Implementation:**

```xml
<!-- app/src/main/AndroidManifest.xml -->

<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Permission required for SpellCheckerService -->
    <uses-permission android:name="android.permission.BIND_TEXT_SERVICE" />

    <application
        android:name=".Titan2KeyboardApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.Titan2Keyboard">

        <!-- Spell Checker Service -->
        <service
            android:name=".service.Titan2SpellCheckerService"
            android:label="@string/spell_checker_name"
            android:permission="android.permission.BIND_TEXT_SERVICE"
            android:exported="true">

            <intent-filter>
                <action android:name="android.service.textservice.SpellCheckerService" />
            </intent-filter>

            <meta-data
                android:name="android.view.textservice.scs"
                android:resource="@xml/spellchecker" />
        </service>

        <!-- Your IME Service (if exists) -->
        <!-- ... -->

    </application>
</manifest>
```

**Spell Checker XML Configuration:**

```xml
<!-- app/src/main/res/xml/spellchecker.xml -->

<?xml version="1.0" encoding="utf-8"?>
<spell-checker
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:label="@string/spell_checker_name"
    android:settingsActivity="com.titan2keyboard.ui.settings.SettingsActivity">

    <!-- Supported locales -->
    <subtype
        android:label="@string/subtype_en_US"
        android:subtypeLocale="en_US" />
</spell-checker>
```

**String Resources:**

```xml
<!-- app/src/main/res/values/strings.xml -->

<resources>
    <string name="app_name">Titan2 Keyboard</string>
    <string name="spell_checker_name">Titan2 Spell Checker</string>
    <string name="subtype_en_US">English (United States)</string>
    <string name="spell_checker_description">
        Fast, privacy-first spell checker powered by SymSpell.
        Works offline with any keyboard.
    </string>
</resources>
```

**Acceptance Criteria:**
- ✅ Manifest declares service correctly
- ✅ Required permissions present
- ✅ spellchecker.xml is valid
- ✅ Service exports correctly

**Deliverable:** Commit "feat: configure manifest for spell checker service"

---

#### **Task 2.4: Application Class Setup** (Day 5, 1 hour)

**Objective:** Initialize Timber and Hilt in Application class

**Implementation:**

```kotlin
// app/src/main/kotlin/com/titan2keyboard/Titan2KeyboardApp.kt

package com.titan2keyboard

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class for Titan2 Keyboard
 */
@HiltAndroidApp
class Titan2KeyboardApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.i("Titan2KeyboardApp initialized")
    }
}
```

**Acceptance Criteria:**
- ✅ @HiltAndroidApp annotation present
- ✅ Timber configured for debug builds
- ✅ App builds and runs

**Deliverable:** Commit "feat: initialize application with Hilt and Timber"

---

#### **Task 2.5: Testing** (Day 6-7, 8 hours)

**Objective:** Write unit tests and integration tests

**Implementation:**

```kotlin
// app/src/test/kotlin/com/titan2keyboard/data/SymSpellEngineTest.kt

package com.titan2keyboard.data

import android.content.Context
import com.titan2keyboard.data.spellcheck.SymSpellEngine
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

class SymSpellEngineTest {

    private lateinit var context: Context
    private lateinit var engine: SymSpellEngine

    private val testDictionary = """
        hello 1000000
        world 900000
        test 500000
        spell 300000
        check 250000
    """.trimIndent()

    @BeforeEach
    fun setup() {
        context = mockk(relaxed = true)

        every { context.assets.open(any()) } returns
            ByteArrayInputStream(testDictionary.toByteArray())

        engine = SymSpellEngine(context)
    }

    @AfterEach
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `initialize loads dictionary successfully`() = runTest {
        val wordCount = engine.initialize()

        assertTrue(wordCount > 0)
        assertEquals(5, wordCount)
        assertTrue(engine.isReady())
    }

    @Test
    fun `isCorrect returns true for valid words`() = runTest {
        engine.initialize()

        assertTrue(engine.isCorrect("hello"))
        assertTrue(engine.isCorrect("world"))
        assertTrue(engine.isCorrect("test"))
    }

    @Test
    fun `isCorrect returns false for misspelled words`() = runTest {
        engine.initialize()

        assertFalse(engine.isCorrect("helo"))
        assertFalse(engine.isCorrect("wrld"))
        assertFalse(engine.isCorrect("tset"))
    }

    @Test
    fun `getSuggestions returns corrections for misspelled words`() = runTest {
        engine.initialize()

        val suggestions = engine.getSuggestions("helo")

        assertTrue(suggestions.isNotEmpty())
        assertEquals("hello", suggestions.first().word)
    }

    @Test
    fun `checkWord returns empty list for correct words`() = runTest {
        engine.initialize()

        val suggestions = engine.checkWord("hello")

        assertTrue(suggestions.isEmpty())
    }
}
```

```kotlin
// app/src/test/kotlin/com/titan2keyboard/data/SpellCheckRepositoryTest.kt

package com.titan2keyboard.data

import com.titan2keyboard.data.repository.SpellCheckRepositoryImpl
import com.titan2keyboard.data.spellcheck.DictionaryManager
import com.titan2keyboard.data.spellcheck.SymSpellEngine
import com.titan2keyboard.domain.model.Suggestion
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SpellCheckRepositoryTest {

    private lateinit var symSpellEngine: SymSpellEngine
    private lateinit var dictionaryManager: DictionaryManager
    private lateinit var repository: SpellCheckRepositoryImpl

    @BeforeEach
    fun setup() {
        symSpellEngine = mockk()
        dictionaryManager = mockk()

        every { dictionaryManager.isReady() } returns true

        repository = SpellCheckRepositoryImpl(symSpellEngine, dictionaryManager)
    }

    @AfterEach
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `checkWord returns correct result for valid word`() = runTest {
        every { symSpellEngine.isCorrect("hello") } returns true

        val result = repository.checkWord("hello")

        assertTrue(result.isCorrect)
        assertTrue(result.suggestions.isEmpty())
        assertEquals("hello", result.originalWord)
    }

    @Test
    fun `checkWord returns suggestions for misspelled word`() = runTest {
        val testSuggestions = listOf(
            Suggestion("hello", 1, 1000000)
        )

        every { symSpellEngine.isCorrect("helo") } returns false
        every { symSpellEngine.getSuggestions("helo", any(), any()) } returns testSuggestions

        val result = repository.checkWord("helo")

        assertFalse(result.isCorrect)
        assertEquals(1, result.suggestions.size)
        assertEquals("hello", result.suggestions.first().word)
    }

    @Test
    fun `checkWord skips URLs`() = runTest {
        val result = repository.checkWord("https://example.com")

        assertTrue(result.isCorrect)
        assertTrue(result.suggestions.isEmpty())
    }

    @Test
    fun `checkWord skips email addresses`() = runTest {
        val result = repository.checkWord("test@example.com")

        assertTrue(result.isCorrect)
        assertTrue(result.suggestions.isEmpty())
    }

    @Test
    fun `checkSentence checks multiple words`() = runTest {
        every { symSpellEngine.isCorrect("This") } returns true
        every { symSpellEngine.isCorrect("is") } returns true
        every { symSpellEngine.isCorrect("a") } returns true
        every { symSpellEngine.isCorrect("tset") } returns false
        every { symSpellEngine.getSuggestions("tset", any(), any()) } returns listOf(
            Suggestion("test", 1, 500000)
        )

        val result = repository.checkSentence("This is a tset")

        assertEquals(4, result.wordResults.size)
        assertEquals(1, result.wordResults.count { !it.isCorrect })

        val misspelledWord = result.wordResults.first { !it.isCorrect }
        assertEquals("tset", misspelledWord.word)
        assertEquals("test", misspelledWord.suggestions.first().word)
    }
}
```

**Run Tests:**

```bash
./gradlew test
./gradlew testDebugUnitTest
```

**Acceptance Criteria:**
- ✅ All unit tests pass
- ✅ Code coverage >70%
- ✅ Edge cases handled

**Deliverable:** Commit "test: add unit tests for spell checking"

---

#### **Task 2.6: Manual Testing & Documentation** (Day 8, 4 hours)

**Objective:** Test on device and create documentation

**Manual Testing Steps:**

1. **Install app on device**
   ```bash
   ./gradlew installDebug
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

2. **Enable spell checker**
   - Go to Settings → System → Languages & input → Spell checker
   - Select "Titan2 Spell Checker"

3. **Test in various apps**
   - Open Messages app
   - Type "helo wrld" (should underline as misspelled)
   - Tap underlined word to see suggestions
   - Test in Chrome, Gmail, Notes app

4. **Verify with different keyboards**
   - Test with Gboard
   - Test with Samsung Keyboard
   - Test with your Titan2 IME (when available)

**Documentation:**

```markdown
# Titan2 Spell Checker

## Overview

Titan2 Spell Checker is a fast, privacy-first spell checking service for Android. It uses the SymSpell algorithm to provide accurate suggestions with minimal resource usage.

## Features

- ✅ **System-wide**: Works with any keyboard and any app
- ✅ **Privacy-first**: 100% offline, no data sent to cloud
- ✅ **Fast**: 0.033ms per word lookup
- ✅ **Lightweight**: <50MB memory footprint
- ✅ **English support**: en-US dictionary included

## Installation

1. Download and install the app
2. Go to Settings → System → Languages & input → Spell checker
3. Select "Titan2 Spell Checker"
4. Start typing in any app!

## How It Works

The spell checker runs as a system service that Android apps and keyboards can query. When you type:

1. Your keyboard sends words to the system
2. Android forwards them to Titan2 Spell Checker
3. We check against our offline dictionary using SymSpell
4. Suggestions are sent back through Android
5. Your keyboard/app shows underlines and suggestions

## Supported Languages

- English (United States) - en-US

More languages coming in future updates!

## Technical Details

- **Algorithm**: SymSpell (Symmetric Delete)
- **Dictionary**: 82,765 English words
- **Edit Distance**: Up to 2 character changes
- **Performance**: <1ms per word

## Privacy

- ✅ All processing happens on-device
- ✅ No internet connection required
- ✅ No data collection or telemetry
- ✅ No cloud services used

## License

Apache 2.0

## Credits

- SymSpell algorithm by Wolf Garbe
- SymSpellKt Kotlin implementation
```

**Update README.md:**

```markdown
# titan2keyboard

Modern Input Method Editor (IME) for Unihertz Titan 2

## Features

### Spell Checking ✅

System-wide spell checker powered by SymSpell:
- Works with any keyboard (Gboard, SwiftKey, etc.)
- Works in any app (Gmail, Messages, Chrome, etc.)
- Fast: 0.033ms per word
- Private: 100% offline
- English (en-US) supported

Enable in: Settings → System → Languages & input → Spell checker → Titan2 Spell Checker

## Installation

```bash
./gradlew installDebug
```

Then enable the spell checker in Android settings.

## Architecture

- **Clean Architecture**: Domain → Data → Service layers
- **Hilt**: Dependency injection
- **Kotlin Coroutines**: Async operations
- **SymSpellKt**: Fast spell checking algorithm
- **DataStore**: Settings persistence

## Development

Built for Android 15 with Kotlin. See [CLAUDE.md](CLAUDE.md) for development guidelines.
```

**Acceptance Criteria:**
- ✅ Service shows in system settings
- ✅ Spell checking works in multiple apps
- ✅ Underlines appear correctly
- ✅ Suggestions are accurate
- ✅ Documentation complete

**Deliverable:** Commit "docs: add spell checker documentation and testing guide"

---

## Acceptance Criteria for Phase 1

### Functional Requirements
- ✅ Spell checker appears in Android Settings → Spell Checker
- ✅ Works with any keyboard (Gboard, SwiftKey, etc.)
- ✅ Works in any app (Gmail, Messages, Chrome, Notes)
- ✅ Underlines misspelled words (Android handles UI)
- ✅ Provides relevant suggestions when tapped
- ✅ Works offline (no network calls)
- ✅ English (en-US) supported

### Non-Functional Requirements
- ✅ Latency <1ms per word (SymSpell is 0.033ms)
- ✅ Memory footprint <50MB
- ✅ Dictionary loads in <2 seconds
- ✅ No impact on typing experience
- ✅ Battery efficient (minimal background processing)

### Quality Requirements
- ✅ Code follows Kotlin best practices
- ✅ Clean architecture maintained
- ✅ Unit tests pass with >70% coverage
- ✅ No memory leaks detected
- ✅ Proper logging with Timber

---

## Testing Checklist

### Manual Testing
- [ ] Install on Titan 2 device
- [ ] Enable in Settings → Spell Checker
- [ ] Test with Gboard keyboard
- [ ] Test with Samsung keyboard
- [ ] Test in Messages app
- [ ] Test in Gmail
- [ ] Test in Chrome browser
- [ ] Test in Notes app
- [ ] Verify underlines appear for misspellings
- [ ] Verify tapping shows suggestions
- [ ] Verify suggestions are accurate
- [ ] Test with correct words (no underline)
- [ ] Test with numbers (should skip)
- [ ] Test with URLs (should skip)
- [ ] Test with email addresses (should skip)

### Performance Testing
- [ ] Profile memory usage (<50MB)
- [ ] Measure dictionary load time (<2s)
- [ ] Verify no lag during typing
- [ ] Test battery impact (minimal)

### Edge Cases
- [ ] Empty input
- [ ] Very long words (>20 characters)
- [ ] All caps words
- [ ] Mixed case words
- [ ] Contractions (don't, won't)
- [ ] Possessives (John's)

---

## File Structure After Phase 1

```
titan2keyboard/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── assets/
│   │   │   │   └── dictionaries/
│   │   │   │       ├── en-US.txt
│   │   │   │       └── README.md
│   │   │   ├── kotlin/com/titan2keyboard/
│   │   │   │   ├── Titan2KeyboardApp.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   └── SpellCheckRepositoryImpl.kt
│   │   │   │   │   └── spellcheck/
│   │   │   │   │       ├── SymSpellEngine.kt
│   │   │   │   │       └── DictionaryManager.kt
│   │   │   │   ├── di/
│   │   │   │   │   └── SpellCheckModule.kt
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/
│   │   │   │   │   │   └── SpellCheck.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       └── SpellCheckRepository.kt
│   │   │   │   └── service/
│   │   │   │       ├── Titan2SpellCheckerService.kt
│   │   │   │       └── Titan2SpellSession.kt
│   │   │   ├── res/
│   │   │   │   ├── values/
│   │   │   │   │   └── strings.xml
│   │   │   │   └── xml/
│   │   │   │       └── spellchecker.xml
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   │       └── kotlin/com/titan2keyboard/
│   │           ├── data/
│   │           │   ├── SymSpellEngineTest.kt
│   │           │   └── SpellCheckRepositoryTest.kt
│   └── build.gradle.kts
├── docs/
│   ├── PHASE1_SPELL_CHECKING_PLAN.md (old UI-based plan)
│   └── PHASE1_SYSTEM_SPELL_CHECKER.md (this file)
└── gradle/
    └── libs.versions.toml
```

---

## Risks & Mitigations

| Risk | Impact | Probability | Mitigation |
|------|--------|-------------|------------|
| Dictionary too large | Medium | Low | Use compressed format, ~82K words is reasonable |
| Slow initialization | Medium | Low | Load async on service creation, cached after first load |
| False positives | Medium | Medium | Tune edit distance, skip patterns (URLs, emails) |
| Service not appearing in settings | High | Low | Verify manifest configuration, test on multiple devices |
| Memory leaks in service | High | Low | Proper lifecycle management, Hilt handles cleanup |

---

## Success Metrics

At the end of Phase 1, we should achieve:

1. **Functionality**: System-wide spell checking works in any app
2. **Performance**: <1ms latency, no lag
3. **Quality**: >70% test coverage, no crashes
4. **Usability**: Shows in system settings, easy to enable
5. **Privacy**: 100% offline, no data transmitted

---

## Next Steps After Phase 1

Once Phase 1 is complete and validated:

1. **Phase 2**: Multi-language support (add more dictionaries)
2. **Phase 3**: Custom dictionary (user-added words, learn from typing)
3. **Phase 4**: Grammar checking with Harper (if desired)
4. **Phase 5**: Advanced features (context-aware suggestions)

---

## Key Differences from Original Plan

### Removed (No longer needed)
- ❌ All Compose UI components (SuggestionBar, etc.)
- ❌ ViewModel layer (no UI state to manage)
- ❌ IME service integration
- ❌ Settings screen for spell check
- ❌ DataStore for preferences (service has no settings yet)

### Kept (Essential)
- ✅ SymSpellEngine (core algorithm)
- ✅ DictionaryManager (loading dictionaries)
- ✅ Repository layer (business logic)
- ✅ Domain models
- ✅ Hilt DI
- ✅ Testing strategy

### Added (System service specific)
- ✅ SpellCheckerService implementation
- ✅ Session management
- ✅ SuggestionsInfo/TextInfo handling
- ✅ Manifest configuration for text service
- ✅ XML spell checker configuration

---

## Resources

- [Android SpellCheckerService](https://developer.android.com/reference/android/service/textservice/SpellCheckerService)
- [Creating a Spell Checker](https://android-developers.googleblog.com/2012/08/creating-your-own-spelling-checker.html)
- [SymSpellKt GitHub](https://github.com/Wavesonics/SymSpellKt)
- [SymSpell Algorithm](https://github.com/wolfgarbe/SymSpell)

---

**Last Updated**: 2025-11-21
**Status**: Ready to Implement
**Estimated Duration**: 1.5 weeks (8 working days)
**Complexity**: Low-Medium (simpler than UI-based approach)
