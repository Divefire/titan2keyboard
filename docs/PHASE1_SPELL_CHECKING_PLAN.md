# Phase 1: Spell Checking Implementation Plan
## titan2keyboard - English Spell Checking with SymSpellKt

**Duration:** 2 weeks (10 working days)
**Goal:** Ship working spell checker for English with visual feedback and suggestion UI

---

## Overview

Phase 1 delivers a production-ready spell checking feature for English using SymSpellKt. Users will see:
- Red underlines for misspelled words
- Tap-to-see suggestions
- Suggestion bar showing alternatives
- Settings to enable/disable spell checking

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    Titan2InputMethodService                  │
│                     (IME Entry Point)                        │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                   SpellCheckViewModel                        │
│              (UI State Management)                           │
│  - currentWordState: StateFlow<Word>                         │
│  - suggestionsState: StateFlow<List<Suggestion>>            │
│  - spellCheckEnabled: StateFlow<Boolean>                     │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                 SpellCheckRepository                         │
│              (Business Logic Layer)                          │
│  - checkWord(word: String): SpellCheckResult                │
│  - getSuggestions(word: String): List<Suggestion>           │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                  SymSpellEngine                              │
│              (Core Spell Check Engine)                       │
│  - symSpell: SymSpell instance                               │
│  - lookup(word: String): List<SuggestionItem>               │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                  DictionaryManager                           │
│              (Dictionary Loading & Caching)                  │
│  - loadDictionary(locale: Locale)                            │
│  - dictionaryState: StateFlow<DictionaryLoadState>          │
└─────────────────────────────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                 SettingsRepository                           │
│              (User Preferences via DataStore)                │
│  - spellCheckEnabled: Flow<Boolean>                          │
│  - underlineColor: Flow<Color>                               │
└─────────────────────────────────────────────────────────────┘
```

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

[libraries]
symspell-kt = { group = "com.darkrockstudios", name = "symspellkt", version.ref = "symspell" }
```

```kotlin
// app/build.gradle.kts
dependencies {
    implementation(libs.symspell.kt)

    // Add if not already present
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.datastore.preferences)
}
```

**Acceptance Criteria:**
- ✅ Project builds successfully
- ✅ SymSpellKt classes are importable
- ✅ No version conflicts

**Deliverable:** Commit with message "feat: add SymSpellKt dependency for spell checking"

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
    ├── en-US-bigram.txt   # Optional: bigram frequency (future)
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
 * Configuration for spell checking
 */
data class SpellCheckConfig(
    val enabled: Boolean = true,
    val maxEditDistance: Int = 2,
    val maxSuggestions: Int = 5,
    val underlineColor: Int = 0xFFFF0000.toInt(), // Red
    val checkAsYouType: Boolean = true
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
 *
 * This class wraps the SymSpell library and provides a simplified API
 * for spell checking operations.
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
// app/src/main/kotlin/com/titan2keyboard/data/repository/SpellCheckRepositoryImpl.kt

package com.titan2keyboard.data.repository

import com.titan2keyboard.data.spellcheck.DictionaryManager
import com.titan2keyboard.data.spellcheck.SymSpellEngine
import com.titan2keyboard.domain.model.SpellCheckConfig
import com.titan2keyboard.domain.model.SpellCheckResult
import com.titan2keyboard.domain.model.Suggestion
import com.titan2keyboard.domain.repository.SpellCheckRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val dictionaryManager: DictionaryManager,
    private val settingsRepository: SettingsRepository
) : SpellCheckRepository {

    private val _config = MutableStateFlow(SpellCheckConfig())
    override val config: Flow<SpellCheckConfig> = _config.asStateFlow()

    companion object {
        private const val TAG = "SpellCheckRepository"

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
        // Skip if spell check disabled or dictionary not ready
        if (!_config.value.enabled || !dictionaryManager.isReady()) {
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
                maxEditDistance = _config.value.maxEditDistance,
                maxSuggestions = _config.value.maxSuggestions
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

    override suspend fun updateConfig(config: SpellCheckConfig) {
        _config.value = config
        // Optionally persist to DataStore
        // settingsRepository.saveSpellCheckConfig(config)
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

```kotlin
// app/src/main/kotlin/com/titan2keyboard/domain/repository/SpellCheckRepository.kt

package com.titan2keyboard.domain.repository

import com.titan2keyboard.domain.model.SpellCheckConfig
import com.titan2keyboard.domain.model.SpellCheckResult
import com.titan2keyboard.domain.model.Suggestion
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for spell checking operations
 */
interface SpellCheckRepository {
    /**
     * Current spell check configuration
     */
    val config: Flow<SpellCheckConfig>

    /**
     * Check spelling of a single word
     */
    suspend fun checkWord(word: String): SpellCheckResult

    /**
     * Get spelling suggestions for a word
     */
    suspend fun getSuggestions(word: String, limit: Int = 5): List<Suggestion>

    /**
     * Update spell check configuration
     */
    suspend fun updateConfig(config: SpellCheckConfig)
}
```

**Acceptance Criteria:**
- ✅ Repository checks words correctly
- ✅ Skips numbers, URLs, emails, etc.
- ✅ Returns relevant suggestions
- ✅ Respects configuration
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

```kotlin
// app/src/main/kotlin/com/titan2keyboard/di/DataModule.kt

package com.titan2keyboard.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "titan2_settings")

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.dataStore
}
```

**Acceptance Criteria:**
- ✅ All dependencies are injectable
- ✅ Singleton scopes are correct
- ✅ App builds successfully

**Deliverable:** Commit "feat: add Hilt DI configuration for spell checking"

---

### Week 2: IME Integration & UI

#### **Task 2.1: Settings Repository Integration** (Day 4, 3 hours)

**Objective:** Add spell check settings to DataStore

**Implementation:**

```kotlin
// app/src/main/kotlin/com/titan2keyboard/data/repository/SettingsRepositoryImpl.kt

package com.titan2keyboard.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.titan2keyboard.domain.model.SpellCheckConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val SPELL_CHECK_ENABLED = booleanPreferencesKey("spell_check_enabled")
        val MAX_EDIT_DISTANCE = intPreferencesKey("max_edit_distance")
        val MAX_SUGGESTIONS = intPreferencesKey("max_suggestions")
        val CHECK_AS_YOU_TYPE = booleanPreferencesKey("check_as_you_type")
        val UNDERLINE_COLOR = intPreferencesKey("underline_color")
    }

    val spellCheckConfig: Flow<SpellCheckConfig> = dataStore.data.map { prefs ->
        SpellCheckConfig(
            enabled = prefs[SPELL_CHECK_ENABLED] ?: true,
            maxEditDistance = prefs[MAX_EDIT_DISTANCE] ?: 2,
            maxSuggestions = prefs[MAX_SUGGESTIONS] ?: 5,
            checkAsYouType = prefs[CHECK_AS_YOU_TYPE] ?: true,
            underlineColor = prefs[UNDERLINE_COLOR] ?: 0xFFFF0000.toInt()
        )
    }

    suspend fun updateSpellCheckEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[SPELL_CHECK_ENABLED] = enabled
        }
    }

    suspend fun updateMaxSuggestions(count: Int) {
        dataStore.edit { prefs ->
            prefs[MAX_SUGGESTIONS] = count.coerceIn(1, 10)
        }
    }
}
```

**Acceptance Criteria:**
- ✅ Settings persist across app restarts
- ✅ Flow emits updates reactively
- ✅ Default values are sensible

**Deliverable:** Commit "feat: add spell check settings to DataStore"

---

#### **Task 2.2: Text Processing Utilities** (Day 4, 2 hours)

**Objective:** Create utilities for extracting words from text input

**Implementation:**

```kotlin
// app/src/main/kotlin/com/titan2keyboard/util/TextUtils.kt

package com.titan2keyboard.util

/**
 * Utilities for text processing in IME context
 */
object TextUtils {

    /**
     * Extract current word being typed from cursor position
     *
     * @param text The full text
     * @param cursorPosition Current cursor position
     * @return Triple of (word, startIndex, endIndex)
     */
    fun getCurrentWord(text: CharSequence, cursorPosition: Int): CurrentWord? {
        if (text.isEmpty() || cursorPosition < 0 || cursorPosition > text.length) {
            return null
        }

        // Find word boundaries
        var start = cursorPosition
        var end = cursorPosition

        // Move start backwards to beginning of word
        while (start > 0 && text[start - 1].isLetterOrDigit()) {
            start--
        }

        // Move end forwards to end of word
        while (end < text.length && text[end].isLetterOrDigit()) {
            end++
        }

        if (start == end) return null

        val word = text.substring(start, end)
        return CurrentWord(word, start, end)
    }

    /**
     * Split text into words for batch spell checking
     */
    fun extractWords(text: CharSequence): List<WordPosition> {
        val words = mutableListOf<WordPosition>()
        val wordRegex = Regex("\\b[a-zA-Z]+\\b")

        wordRegex.findAll(text).forEach { match ->
            words.add(
                WordPosition(
                    word = match.value,
                    start = match.range.first,
                    end = match.range.last + 1
                )
            )
        }

        return words
    }
}

/**
 * Represents a word at a specific position in text
 */
data class CurrentWord(
    val word: String,
    val start: Int,
    val end: Int
)

data class WordPosition(
    val word: String,
    val start: Int,
    val end: Int
)
```

**Acceptance Criteria:**
- ✅ Correctly extracts word at cursor
- ✅ Handles edge cases (empty text, boundaries)
- ✅ Unit tests pass

**Deliverable:** Commit "feat: add text processing utilities for word extraction"

---

#### **Task 2.3: IME Service Integration** (Day 5-6, 8 hours)

**Objective:** Integrate spell checking into Titan2InputMethodService

**Implementation:**

```kotlin
// app/src/main/kotlin/com/titan2keyboard/ime/Titan2InputMethodService.kt

package com.titan2keyboard.ime

import android.inputmethodservice.InputMethodService
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.SuggestionSpan
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.lifecycle.lifecycleScope
import com.titan2keyboard.data.repository.SpellCheckRepositoryImpl
import com.titan2keyboard.data.spellcheck.DictionaryManager
import com.titan2keyboard.domain.model.SpellCheckResult
import com.titan2keyboard.util.TextUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class Titan2InputMethodService : InputMethodService() {

    @Inject
    lateinit var spellCheckRepository: SpellCheckRepositoryImpl

    @Inject
    lateinit var dictionaryManager: DictionaryManager

    private var spellCheckJob: Job? = null
    private var isSpellCheckEnabled = true

    companion object {
        private const val TAG = "Titan2IME"
        private const val SPELL_CHECK_DEBOUNCE_MS = 300L
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("IME Service created")

        // Initialize dictionary on background thread
        dictionaryManager.loadDictionary("en-US")

        // Observe spell check settings
        lifecycleScope.launch {
            spellCheckRepository.config.collectLatest { config ->
                isSpellCheckEnabled = config.enabled
                Timber.d("Spell check enabled: $isSpellCheckEnabled")
            }
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Timber.d("Starting input, restarting: $restarting")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        event ?: return super.onKeyDown(keyCode, event)

        // Handle key press
        val handled = handleKeyEvent(keyCode, event)

        if (handled) {
            // Trigger spell check after key press
            scheduleSpellCheck()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun handleKeyEvent(keyCode: Int, event: KeyEvent): Boolean {
        val ic = currentInputConnection ?: return false

        when (keyCode) {
            KeyEvent.KEYCODE_DEL -> {
                ic.deleteSurroundingText(1, 0)
                return true
            }
            KeyEvent.KEYCODE_ENTER -> {
                ic.commitText("\n", 1)
                return true
            }
            KeyEvent.KEYCODE_SPACE -> {
                ic.commitText(" ", 1)
                // Check the word that was just completed
                checkCompletedWord(ic)
                return true
            }
            else -> {
                // Letter keys
                val char = event.unicodeChar.toChar()
                if (char.isLetterOrDigit() || char.isWhitespace()) {
                    ic.commitText(char.toString(), 1)
                    return true
                }
            }
        }

        return false
    }

    /**
     * Schedule spell check with debouncing
     */
    private fun scheduleSpellCheck() {
        if (!isSpellCheckEnabled) return

        spellCheckJob?.cancel()
        spellCheckJob = lifecycleScope.launch {
            delay(SPELL_CHECK_DEBOUNCE_MS)
            performSpellCheck()
        }
    }

    /**
     * Perform spell check on current word
     */
    private suspend fun performSpellCheck() {
        val ic = currentInputConnection ?: return

        // Get text before cursor
        val textBeforeCursor = ic.getTextBeforeCursor(50, 0) ?: return

        // Extract current word
        val currentWord = TextUtils.getCurrentWord(
            textBeforeCursor,
            textBeforeCursor.length
        ) ?: return

        // Check spelling
        val result = spellCheckRepository.checkWord(currentWord.word)

        if (!result.isCorrect && result.suggestions.isNotEmpty()) {
            // Show suggestions (will implement UI in next task)
            Timber.d("Misspelled: ${currentWord.word}, suggestions: ${result.suggestions}")
            showSpellingSuggestions(result, currentWord.start)
        }
    }

    /**
     * Check word that was just completed (after space or punctuation)
     */
    private fun checkCompletedWord(ic: InputConnection) {
        if (!isSpellCheckEnabled) return

        lifecycleScope.launch {
            // Get the word before the space
            val textBeforeCursor = ic.getTextBeforeCursor(50, 0) ?: return@launch

            if (textBeforeCursor.length < 2) return@launch

            // Find the last word (before the space we just typed)
            val lastWord = TextUtils.getCurrentWord(
                textBeforeCursor.dropLast(1), // Remove the space
                textBeforeCursor.length - 1
            ) ?: return@launch

            val result = spellCheckRepository.checkWord(lastWord.word)

            if (!result.isCorrect && result.suggestions.isNotEmpty()) {
                // Apply underline to misspelled word
                applySpellingUnderline(ic, lastWord, result)
            }
        }
    }

    /**
     * Apply red underline to misspelled word using SuggestionSpan
     */
    private fun applySpellingUnderline(
        ic: InputConnection,
        word: com.titan2keyboard.util.CurrentWord,
        result: SpellCheckResult
    ) {
        // Create suggestion span with alternatives
        val suggestions = result.suggestions.take(5).map { it.word }.toTypedArray()

        val suggestionSpan = SuggestionSpan(
            this,
            suggestions,
            SuggestionSpan.FLAG_MISSPELLED or SuggestionSpan.FLAG_EASY_CORRECT
        )

        // Get current text
        val currentText = ic.getTextBeforeCursor(100, 0) ?: return
        val start = currentText.length - word.word.length

        if (start < 0) return

        // Create spannable with underline
        val spannable = SpannableStringBuilder(word.word)
        spannable.setSpan(
            suggestionSpan,
            0,
            word.word.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Replace word with spannable version
        ic.deleteSurroundingText(word.word.length, 0)
        ic.commitText(spannable, 1)

        Timber.d("Applied underline to: ${word.word}")
    }

    /**
     * Show spelling suggestions in suggestion strip or popup
     * TODO: Implement UI in Task 2.4
     */
    private fun showSpellingSuggestions(result: SpellCheckResult, position: Int) {
        // Placeholder for suggestion UI
        Timber.d("Would show suggestions: ${result.suggestions}")
    }

    override fun onDestroy() {
        spellCheckJob?.cancel()
        super.onDestroy()
        Timber.d("IME Service destroyed")
    }
}
```

**Acceptance Criteria:**
- ✅ Spell check runs as user types
- ✅ Debouncing prevents excessive checks
- ✅ Misspelled words are detected
- ✅ SuggestionSpan underlines work
- ✅ No UI lag or stuttering

**Deliverable:** Commit "feat: integrate spell checking into IME service"

---

#### **Task 2.4: Suggestion UI (Compose)** (Day 7-8, 8 hours)

**Objective:** Create Compose UI for displaying spelling suggestions

**Implementation:**

```kotlin
// app/src/main/kotlin/com/titan2keyboard/ui/ime/SuggestionBar.kt

package com.titan2keyboard.ui.ime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.titan2keyboard.domain.model.Suggestion

/**
 * Suggestion bar shown above keyboard for spell check suggestions
 */
@Composable
fun SuggestionBar(
    suggestions: List<Suggestion>,
    onSuggestionClick: (Suggestion) -> Unit,
    modifier: Modifier = Modifier
) {
    if (suggestions.isEmpty()) return

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(suggestions) { suggestion ->
                SuggestionChip(
                    suggestion = suggestion,
                    onClick = { onSuggestionClick(suggestion) }
                )
            }
        }
    }
}

@Composable
private fun SuggestionChip(
    suggestion: Suggestion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = suggestion.word,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (suggestion.distance == 0) FontWeight.Bold else FontWeight.Normal
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Show edit distance badge for debugging
            if (suggestion.distance > 0) {
                Text(
                    text = "±${suggestion.distance}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Popup for showing suggestions when tapping on underlined word
 */
@Composable
fun SuggestionPopup(
    originalWord: String,
    suggestions: List<Suggestion>,
    onSuggestionClick: (Suggestion) -> Unit,
    onDismiss: () -> Unit,
    onAddToDictionary: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Original word
            Text(
                text = "\"$originalWord\"",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )

            Divider()

            // Suggestions
            suggestions.take(5).forEach { suggestion ->
                Text(
                    text = suggestion.word,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSuggestionClick(suggestion) }
                        .padding(12.dp)
                )
            }

            Divider()

            // Add to dictionary option
            Text(
                text = "Add to dictionary",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onAddToDictionary)
                    .padding(12.dp)
            )
        }
    }
}
```

```kotlin
// app/src/main/kotlin/com/titan2keyboard/ui/ime/SpellCheckViewModel.kt

package com.titan2keyboard.ui.ime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.titan2keyboard.domain.model.Suggestion
import com.titan2keyboard.domain.repository.SpellCheckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpellCheckViewModel @Inject constructor(
    private val spellCheckRepository: SpellCheckRepository
) : ViewModel() {

    private val _suggestions = MutableStateFlow<List<Suggestion>>(emptyList())
    val suggestions: StateFlow<List<Suggestion>> = _suggestions.asStateFlow()

    fun checkWord(word: String) {
        viewModelScope.launch {
            val result = spellCheckRepository.checkWord(word)
            _suggestions.value = if (!result.isCorrect) {
                result.suggestions
            } else {
                emptyList()
            }
        }
    }

    fun clearSuggestions() {
        _suggestions.value = emptyList()
    }
}
```

**Update IME Service:**

```kotlin
// In Titan2InputMethodService.kt - add suggestion bar

override fun onCreateInputView(): View {
    return ComposeView(this).apply {
        setContent {
            MaterialTheme {
                val viewModel: SpellCheckViewModel = hiltViewModel()
                val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()

                Column {
                    SuggestionBar(
                        suggestions = suggestions,
                        onSuggestionClick = { suggestion ->
                            applySuggestion(suggestion)
                            viewModel.clearSuggestions()
                        }
                    )

                    // Minimal keyboard UI or empty for physical keyboard
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

private fun applySuggestion(suggestion: Suggestion) {
    val ic = currentInputConnection ?: return

    // Get current word and replace it
    val textBefore = ic.getTextBeforeCursor(50, 0) ?: return
    val currentWord = TextUtils.getCurrentWord(textBefore, textBefore.length) ?: return

    // Delete current word and insert suggestion
    ic.deleteSurroundingText(currentWord.word.length, 0)
    ic.commitText(suggestion.word, 1)

    Timber.d("Applied suggestion: ${suggestion.word}")
}
```

**Acceptance Criteria:**
- ✅ Suggestion bar appears above keyboard
- ✅ Suggestions are clickable and apply correctly
- ✅ UI is responsive and doesn't lag
- ✅ Material Design 3 styling
- ✅ Works on Titan 2's square display

**Deliverable:** Commit "feat: add suggestion bar UI with Compose"

---

#### **Task 2.5: Settings Screen** (Day 9, 4 hours)

**Objective:** Add spell checking settings to app settings screen

**Implementation:**

```kotlin
// app/src/main/kotlin/com/titan2keyboard/ui/settings/SpellCheckSettingsSection.kt

package com.titan2keyboard.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SpellCheckSettingsSection(
    enabled: Boolean,
    checkAsYouType: Boolean,
    maxSuggestions: Int,
    onEnabledChange: (Boolean) -> Unit,
    onCheckAsYouTypeChange: (Boolean) -> Unit,
    onMaxSuggestionsChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Spell Checking",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Enable/disable toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Enable spell checking",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Check spelling as you type",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onEnabledChange
            )
        }

        // Check as you type
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Check as you type",
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = checkAsYouType,
                onCheckedChange = onCheckAsYouTypeChange,
                enabled = enabled
            )
        }

        // Max suggestions slider
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Maximum suggestions: $maxSuggestions",
                style = MaterialTheme.typography.bodyLarge
            )
            Slider(
                value = maxSuggestions.toFloat(),
                onValueChange = { onMaxSuggestionsChange(it.toInt()) },
                valueRange = 1f..10f,
                steps = 8,
                enabled = enabled,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
```

```kotlin
// Add to SettingsViewModel

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val spellCheckRepository: SpellCheckRepository
) : ViewModel() {

    val spellCheckConfig = spellCheckRepository.config
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SpellCheckConfig())

    fun updateSpellCheckEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSpellCheckEnabled(enabled)
        }
    }

    fun updateMaxSuggestions(count: Int) {
        viewModelScope.launch {
            settingsRepository.updateMaxSuggestions(count)
        }
    }
}
```

**Acceptance Criteria:**
- ✅ Settings persist across app restarts
- ✅ UI updates reflect immediately in IME
- ✅ Material Design 3 styling
- ✅ Accessible and easy to use

**Deliverable:** Commit "feat: add spell check settings UI"

---

#### **Task 2.6: Testing** (Day 10, 4 hours)

**Objective:** Write unit and integration tests

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

        // Mock assets
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
    fun `getSuggestions returns empty for correct words`() = runTest {
        engine.initialize()

        val suggestions = engine.checkWord("hello")

        assertTrue(suggestions.isEmpty())
    }

    @Test
    fun `getSuggestions respects maxSuggestions parameter`() = runTest {
        engine.initialize()

        val suggestions = engine.getSuggestions("helo", maxSuggestions = 2)

        assertTrue(suggestions.size <= 2)
    }
}
```

```kotlin
// app/src/test/kotlin/com/titan2keyboard/util/TextUtilsTest.kt

package com.titan2keyboard.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TextUtilsTest {

    @Test
    fun `getCurrentWord extracts word at cursor position`() {
        val text = "hello world"
        val cursorPos = 7 // After "hello w"

        val result = TextUtils.getCurrentWord(text, cursorPos)

        assertNotNull(result)
        assertEquals("world", result?.word)
        assertEquals(6, result?.start)
        assertEquals(11, result?.end)
    }

    @Test
    fun `getCurrentWord handles cursor at word start`() {
        val text = "hello world"
        val cursorPos = 6 // Start of "world"

        val result = TextUtils.getCurrentWord(text, cursorPos)

        assertNotNull(result)
        assertEquals("world", result?.word)
    }

    @Test
    fun `getCurrentWord handles cursor at word end`() {
        val text = "hello world"
        val cursorPos = 5 // End of "hello"

        val result = TextUtils.getCurrentWord(text, cursorPos)

        assertNotNull(result)
        assertEquals("hello", result?.word)
    }

    @Test
    fun `getCurrentWord returns null for empty text`() {
        val result = TextUtils.getCurrentWord("", 0)

        assertNull(result)
    }

    @Test
    fun `extractWords returns all words with positions`() {
        val text = "The quick brown fox"

        val words = TextUtils.extractWords(text)

        assertEquals(4, words.size)
        assertEquals("The", words[0].word)
        assertEquals("quick", words[1].word)
        assertEquals("brown", words[2].word)
        assertEquals("fox", words[3].word)
    }
}
```

**Run tests:**
```bash
./gradlew test
```

**Acceptance Criteria:**
- ✅ All unit tests pass
- ✅ Code coverage >70%
- ✅ Edge cases handled

**Deliverable:** Commit "test: add unit tests for spell checking"

---

#### **Task 2.7: Documentation & Polish** (Day 10, 2 hours)

**Objective:** Add documentation and finalize Phase 1

**Implementation:**

1. Update README.md with spell checking feature
2. Add KDoc comments to public APIs
3. Create user guide for spell checking
4. Test on physical Titan 2 device
5. Performance profiling

```markdown
# README.md additions

## Features

### ✅ Spell Checking
- Real-time spell checking as you type
- Smart suggestions based on frequency and edit distance
- Works completely offline (privacy-first)
- Supports English (en-US)
- Customizable settings
  - Enable/disable spell checking
  - Adjust maximum suggestions
  - Check-as-you-type toggle

### How to Use Spell Checking
1. Enable titan2keyboard as your input method
2. Start typing in any app
3. Misspelled words will be underlined in red
4. Tap the suggestion bar to see alternatives
5. Tap a suggestion to replace the word

### Settings
Navigate to Settings > Spell Checking to configure:
- Enable or disable spell checking
- Adjust number of suggestions shown
- Toggle real-time checking
```

**Deliverable:** Commit "docs: add spell checking documentation"

---

## Acceptance Criteria for Phase 1

### Functional Requirements
- ✅ English spell checking works in real-time
- ✅ Misspelled words show red underlines
- ✅ Suggestion bar displays alternatives
- ✅ Tapping suggestions replaces words correctly
- ✅ Settings allow enable/disable
- ✅ Works offline (no network calls)

### Non-Functional Requirements
- ✅ Latency <100ms for spell check
- ✅ No perceptible typing lag
- ✅ Memory footprint <50MB
- ✅ Dictionary loads in <2 seconds
- ✅ Battery drain <2% per hour of use

### Quality Requirements
- ✅ Code follows Kotlin best practices
- ✅ Clean architecture maintained
- ✅ Unit tests pass with >70% coverage
- ✅ No memory leaks detected
- ✅ Works on Titan 2's physical keyboard

---

## Testing Checklist

### Manual Testing
- [ ] Install on Titan 2 device
- [ ] Enable as IME
- [ ] Test typing in various apps (Messages, Notes, Browser)
- [ ] Verify underlines appear for misspellings
- [ ] Verify suggestions are relevant
- [ ] Test applying suggestions
- [ ] Test settings changes
- [ ] Test with spell check disabled
- [ ] Test performance under heavy typing
- [ ] Test with different text input types (email, URL, password fields)

### Performance Testing
- [ ] Profile CPU usage during typing
- [ ] Measure memory consumption
- [ ] Test dictionary load time
- [ ] Verify no frame drops or stuttering
- [ ] Test battery impact over 1-hour session

### Edge Cases
- [ ] Empty input
- [ ] Very long words
- [ ] All caps words
- [ ] Numbers and special characters
- [ ] URLs and emails (should skip)
- [ ] Mixed language input

---

## Dependencies Added

```toml
# gradle/libs.versions.toml
[versions]
symspell = "3.4.0"
timber = "5.0.1"

[libraries]
symspell-kt = { group = "com.darkrockstudios", name = "symspellkt", version.ref = "symspell" }
timber = { group = "com.jakewharton.timber", name = "timber", version.ref = "timber" }
```

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
│   │   │   │   ├── data/
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── SettingsRepository.kt
│   │   │   │   │   │   └── SpellCheckRepositoryImpl.kt
│   │   │   │   │   └── spellcheck/
│   │   │   │   │       ├── SymSpellEngine.kt
│   │   │   │   │       └── DictionaryManager.kt
│   │   │   │   ├── di/
│   │   │   │   │   ├── AppModule.kt
│   │   │   │   │   ├── DataModule.kt
│   │   │   │   │   └── SpellCheckModule.kt
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/
│   │   │   │   │   │   └── SpellCheck.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       └── SpellCheckRepository.kt
│   │   │   │   ├── ime/
│   │   │   │   │   └── Titan2InputMethodService.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── ime/
│   │   │   │   │   │   ├── SuggestionBar.kt
│   │   │   │   │   │   └── SpellCheckViewModel.kt
│   │   │   │   │   └── settings/
│   │   │   │   │       ├── SettingsScreen.kt
│   │   │   │   │       ├── SpellCheckSettingsSection.kt
│   │   │   │   │       └── SettingsViewModel.kt
│   │   │   │   └── util/
│   │   │   │       └── TextUtils.kt
│   │   └── test/
│   │       └── kotlin/com/titan2keyboard/
│   │           ├── data/
│   │           │   └── SymSpellEngineTest.kt
│   │           └── util/
│   │               └── TextUtilsTest.kt
│   └── build.gradle.kts
├── docs/
│   └── PHASE1_SPELL_CHECKING_PLAN.md
└── gradle/
    └── libs.versions.toml
```

---

## Risks & Mitigations

| Risk | Impact | Probability | Mitigation |
|------|--------|-------------|------------|
| Dictionary too large | High | Low | Use compressed format, lazy loading |
| Slow initialization | Medium | Medium | Load async on app start, show progress |
| False positives | Medium | Medium | Tune edit distance, skip patterns (URLs, etc.) |
| UI lag on typing | High | Low | Debounce checks, run on background thread |
| Memory leaks | High | Low | Proper lifecycle management, profiling |

---

## Success Metrics

At the end of Phase 1, we should achieve:

1. **Functionality**: Users can type with real-time spell checking
2. **Performance**: <100ms latency, no lag
3. **Quality**: >70% test coverage, no crashes
4. **UX**: Clean UI with Material Design 3
5. **Privacy**: 100% offline, no data transmitted

---

## Next Steps After Phase 1

Once Phase 1 is complete and validated:

1. **Phase 2**: Multi-language support (add more dictionaries)
2. **Phase 3**: Grammar checking with Harper
3. **Phase 4**: Custom dictionary (add user words)
4. **Phase 5**: Advanced features (auto-correct, smart predictions)

---

## Questions & Decisions

### Resolved
- ✅ Library choice: SymSpellKt (confirmed)
- ✅ Architecture: Clean architecture with MVVM (confirmed)
- ✅ UI framework: Jetpack Compose (confirmed)
- ✅ Storage: DataStore for settings (confirmed)

### To Decide
- ⏳ Should we auto-correct common typos without user confirmation?
- ⏳ Should we add haptic feedback for misspellings?
- ⏳ Should we collect anonymous spell check metrics?

---

## Resources

- [SymSpellKt GitHub](https://github.com/Wavesonics/SymSpellKt)
- [Android IME Framework](https://developer.android.com/develop/ui/views/touch-and-input/creating-input-method)
- [Jetpack Compose for IME](https://developer.android.com/jetpack/compose)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)

---

**Last Updated**: 2025-11-21
**Status**: Ready to Implement
**Estimated Duration**: 2 weeks (10 working days)
**Complexity**: Medium
