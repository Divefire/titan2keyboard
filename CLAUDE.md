# CLAUDE.md - AI Assistant Guide for titan2keyboard

## Project Overview

**titan2keyboard** is a modern Input Method Editor (IME) keyboard specifically designed for the Unihertz Titan 2 device. This is an Android keyboard application that provides an enhanced typing experience optimized for the physical QWERTY keyboard found on the Titan 2.

## Project Status

This is a newly initialized project. The codebase is in early development with minimal structure established.

## Design Philosophy

**Clean Sheet, Modern Implementation**

This project is built from the ground up using the latest Android technologies and best practices:

- **No Legacy Code**: 100% modern implementation, no backwards compatibility concerns
- **Android 15 First**: Targets latest Android platform (API 35), minimum Android 14 (API 34)
- **Kotlin-Only**: Pure Kotlin codebase, no Java
- **Modern Toolchain**: Latest Gradle, AGP, KSP (no KAPT), version catalogs
- **Jetpack Compose**: Declarative UI with Material Design 3
- **Clean Architecture**: Proper separation of concerns (UI → Domain → Data)
- **Reactive Patterns**: Coroutines, Flow, StateFlow throughout
- **Dependency Injection**: Hilt for compile-time DI
- **Type Safety**: Leverage Kotlin's type system fully
- **Performance**: Built for Android 15's performance features and R8 optimization
- **Developer Experience**: Fast builds, clear architecture, maintainable code

This approach allows us to use the best tools and patterns without being constrained by legacy requirements.

## Technology Stack

### Core Technologies
- **Platform**: Android 15 (API 35)
- **Language**: Kotlin (100% Kotlin, no Java)
- **Build System**: Gradle 8.x with Android Gradle Plugin 8.x (Kotlin DSL)
- **Target Device**: Unihertz Titan 2 (physical QWERTY keyboard)
- **Minimum SDK**: 34 (Android 14) - Clean sheet, no backwards compatibility needed
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 35

### Modern Android Architecture & Libraries

**UI Framework**
- **Jetpack Compose** - Modern declarative UI (Material Design 3)
- Compose UI for settings and configuration screens
- Material Design 3 components and theming

**Architecture Components**
- **MVVM/MVI Architecture** - Modern unidirectional data flow
- **ViewModel** - UI state management with lifecycle awareness
- **StateFlow/SharedFlow** - Reactive state management (prefer over LiveData)
- **Lifecycle** - Lifecycle-aware components

**Dependency Injection**
- **Hilt** - Modern dependency injection framework
- Compile-time DI for better performance

**Asynchronous Programming**
- **Kotlin Coroutines** - All async operations
- **Flow** - Reactive streams for data
- **Dispatchers** - Proper thread management (Main, IO, Default)

**Data Persistence**
- **DataStore** - Modern preferences storage (replaces SharedPreferences)
- **Room** (if needed) - Type-safe database access with Kotlin coroutines support

**Build & Dependencies**
- **Version Catalog** (libs.versions.toml) - Centralized dependency management
- **Kotlin Symbol Processing (KSP)** - Replace kapt for faster builds

### Key Android Components
- `InputMethodService` - Core IME service
- Android Input Method Framework (latest APIs)
- Hardware keyboard event handling
- Modern accessibility APIs

## Expected Project Structure

```
titan2keyboard/
├── app/                          # Main application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/titan2keyboard/
│   │   │   │   ├── Titan2KeyboardApp.kt  # Application class with Hilt
│   │   │   │   ├── di/          # Dependency injection modules
│   │   │   │   │   ├── AppModule.kt
│   │   │   │   │   └── DataModule.kt
│   │   │   │   ├── ime/         # IME service implementation
│   │   │   │   │   ├── Titan2InputMethodService.kt
│   │   │   │   │   └── KeyEventHandler.kt
│   │   │   │   ├── domain/      # Business logic layer
│   │   │   │   │   ├── model/   # Domain models
│   │   │   │   │   ├── repository/  # Repository interfaces
│   │   │   │   │   └── usecase/ # Use cases
│   │   │   │   ├── data/        # Data layer
│   │   │   │   │   ├── repository/  # Repository implementations
│   │   │   │   │   ├── datastore/   # DataStore preferences
│   │   │   │   │   └── model/   # Data models/DTOs
│   │   │   │   ├── ui/          # Presentation layer (Compose)
│   │   │   │   │   ├── theme/   # Material3 theme
│   │   │   │   │   ├── settings/    # Settings screens
│   │   │   │   │   │   ├── SettingsScreen.kt
│   │   │   │   │   │   └── SettingsViewModel.kt
│   │   │   │   │   └── components/  # Reusable Compose components
│   │   │   │   └── util/        # Utility classes and extensions
│   │   │   ├── res/
│   │   │   │   ├── values/      # Strings, colors, dimensions (Compose-based)
│   │   │   │   ├── xml/         # IME method definitions, input method
│   │   │   │   └── drawable/    # Vector drawables, icons
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                # Unit tests (JUnit 5, Mockk)
│   │   └── androidTest/         # Instrumentation tests (Compose UI tests)
│   ├── build.gradle.kts         # App-level build configuration
│   └── proguard-rules.pro       # R8 optimization rules
├── gradle/                       # Gradle wrapper
│   └── libs.versions.toml       # Version catalog for dependencies
├── build.gradle.kts             # Project-level build configuration
├── settings.gradle.kts          # Gradle settings
├── gradle.properties            # Gradle properties (Kotlin, AndroidX flags)
├── LICENSE                      # Apache 2.0 License
├── README.md                    # User-facing documentation
└── CLAUDE.md                    # This file
```

## Development Workflows

### Setting Up the Project

When initializing the Android project structure:

1. **Create Android Application Structure**
   - Use standard Android project layout with Kotlin source sets
   - Set up Gradle build system with Kotlin DSL (all `.gradle.kts` files)
   - Configure proper package structure: `com.titan2keyboard.*`
   - Enable Jetpack Compose in build configuration

2. **Configure Build Files**
   - **Minimum SDK**: 34 (Android 14)
   - **Target SDK**: 35 (Android 15)
   - **Compile SDK**: 35
   - **Kotlin**: Latest stable (1.9.x or 2.0.x)
   - **JVM Target**: 17 (required for Android 15)
   - Enable KSP (Kotlin Symbol Processing) for Hilt
   - Configure version catalog in `gradle/libs.versions.toml`
   - Enable Compose compiler and runtime

3. **Version Catalog Setup** (`gradle/libs.versions.toml`)
   ```toml
   [versions]
   kotlin = "1.9.22"
   agp = "8.3.0"
   compose-bom = "2024.02.00"
   hilt = "2.50"

   [libraries]
   androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version = "1.12.0" }
   compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
   hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }

   [plugins]
   android-application = { id = "com.android.application", version.ref = "agp" }
   kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
   hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
   ksp = { id = "com.google.devtools.ksp", version = "1.9.22-1.0.17" }
   ```

4. **Manifest Configuration**
   - Declare `InputMethodService` with modern intent filters
   - Enable `android:enableOnBackInvokedCallback="true"` for predictive back
   - Add required permissions (minimal set)
   - Configure app theme for Material Design 3
   - Set `android:extractNativeLibs="false"` for better install performance

5. **Gradle Properties**
   ```properties
   # Enable AndroidX
   android.useAndroidX=true
   # Enable Jetpack Compose
   android.enableJetifier=false
   # Kotlin code generation
   kapt.use.worker.api=true
   # Enable R8 full mode
   android.enableR8.fullMode=true
   # Enable non-transitive R classes
   android.nonTransitiveRClass=true
   # Enable configuration cache
   org.gradle.configuration-cache=true
   ```

### IME Development Guidelines

#### Core IME Implementation

1. **Service Implementation**
   - Extend `InputMethodService`
   - Override key lifecycle methods: `onCreateInputView()`, `onStartInput()`, `onFinishInput()`
   - Handle physical keyboard events properly

2. **Physical Keyboard Handling**
   - The Titan 2 has a physical QWERTY keyboard
   - Focus on key event interception and processing
   - Implement modifier key handling (Shift, Alt, Sym, etc.)
   - Support key combinations and shortcuts

3. **Text Input Processing**
   - Handle text composition and commitment
   - Implement autocorrect/suggestion logic (if applicable)
   - Support special character input
   - Handle different input types (text, email, URL, etc.)

#### Key Features to Implement

- **Hardware Key Mapping**: Map physical keys to appropriate characters
- **Multi-language Support**: Handle different keyboard layouts
- **Smart Punctuation**: Context-aware punctuation insertion
- **Clipboard Integration**: Quick access to clipboard history
- **Customizable Shortcuts**: User-defined key combinations
- **Settings UI**: User preferences for keyboard behavior

### Testing Strategy

**Modern Testing Approach**

1. **Unit Tests** (JUnit 5 + Mockk + Turbine for Flow testing)
   - Test key mapping logic and event handling
   - Test ViewModels with StateFlow/Flow
   - Test repository implementations
   - Test use cases and domain logic
   - Use `MockK` for mocking
   - Use `Turbine` for testing Flows
   - Use `kotlinx-coroutines-test` for testing coroutines

   ```kotlin
   @Test
   fun `key event handler maps physical key correctly`() = runTest {
       val handler = KeyEventHandler()
       val result = handler.handleKeyEvent(createKeyEvent(KeyEvent.KEYCODE_A))
       assertThat(result).isEqualTo(KeyEventResult.Handled)
   }

   @Test
   fun `settings flow emits updated values`() = runTest {
       settingsRepository.settingsFlow.test {
           val initial = awaitItem()
           settingsRepository.updateSetting("vibration", true)
           val updated = awaitItem()
           assertThat(updated.vibrationEnabled).isTrue()
       }
   }
   ```

2. **Compose UI Tests**
   - Use Compose testing APIs
   - Test settings screen interactions
   - Test state changes and UI updates
   - Semantic-based testing (accessibility-focused)

   ```kotlin
   @Test
   fun settingsScreen_togglesVibration() {
       composeTestRule.setContent {
           SettingsScreen()
       }
       composeTestRule
           .onNodeWithText("Vibration")
           .performClick()
       composeTestRule
           .onNodeWithText("Vibration")
           .assertIsOn()
   }
   ```

3. **Instrumentation Tests**
   - Test IME service lifecycle on device
   - Test actual input connection handling
   - Test DataStore persistence
   - Use Hilt testing components

4. **Manual Testing**
   - Test on actual Unihertz Titan 2 device (primary)
   - Test various input scenarios across apps
   - Test key combinations and modifiers
   - Test performance and latency
   - Test with different input types (text, email, password, etc.)

### Code Conventions

#### General Conventions

- **Language**: Prefer Kotlin over Java for new code
- **Code Style**: Follow Android Kotlin style guide
- **Naming**: Use descriptive, clear names
  - Classes: PascalCase (`KeyboardService`)
  - Functions/Variables: camelCase (`handleKeyPress`)
  - Constants: UPPER_SNAKE_CASE (`MAX_SUGGESTIONS`)
  - Resources: snake_case (`keyboard_view`, `key_preview`)

#### Android-Specific Conventions

- **Lifecycle Awareness**: Always handle Android lifecycle properly
- **Memory Management**: Avoid memory leaks, use weak references where appropriate
- **Threading**: Use appropriate threading for background tasks
  - Coroutines for asynchronous operations
  - Main thread for UI updates only
- **Resources**: Externalize all strings, dimensions, colors
- **Accessibility**: Ensure IME is accessible

#### Code Organization

```kotlin
// Example: Modern IME Service with Hilt and Coroutines
@AndroidEntryPoint
class Titan2InputMethodService : InputMethodService() {

    @Inject
    lateinit var keyEventHandler: KeyEventHandler

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var currentInputConnection: InputConnection? = null

    companion object {
        private const val TAG = "Titan2IME"
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize with coroutines
        serviceScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                // React to settings changes
                applySettings(settings)
            }
        }
    }

    override fun onCreateInputView(): View {
        // IME services typically don't have a view for physical keyboards
        // but can show minimal UI if needed using Compose
        return ComposeView(this).apply {
            setContent {
                MaterialTheme {
                    // Optional: Minimal keyboard UI
                }
            }
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        currentInputConnection = currentInputConnection
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        event ?: return super.onKeyDown(keyCode, event)

        return when (keyEventHandler.handleKeyEvent(event, currentInputConnection)) {
            KeyEventResult.Handled -> true
            KeyEventResult.NotHandled -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun applySettings(settings: KeyboardSettings) {
        // Apply user settings
    }
}

// Example: ViewModel with StateFlow
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settingsState: StateFlow<SettingsUiState> = settingsRepository
        .settingsFlow
        .map { settings ->
            SettingsUiState.Success(settings)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState.Loading
        )

    fun updateSetting(key: String, value: Any) {
        viewModelScope.launch {
            settingsRepository.updateSetting(key, value)
        }
    }
}

// Example: Compose UI Screen
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsState by viewModel.settingsState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Titan2 Keyboard Settings") }
            )
        }
    ) { paddingValues ->
        when (val state = settingsState) {
            is SettingsUiState.Loading -> LoadingIndicator()
            is SettingsUiState.Success -> {
                SettingsContent(
                    settings = state.settings,
                    onSettingChanged = viewModel::updateSetting,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

// Example: Repository with DataStore
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override val settingsFlow: Flow<KeyboardSettings> = dataStore.data
        .map { preferences ->
            KeyboardSettings(
                autoCapitalize = preferences[AUTO_CAPITALIZE] ?: true,
                vibrationEnabled = preferences[VIBRATION_ENABLED] ?: false
            )
        }

    override suspend fun updateSetting(key: String, value: Any) {
        dataStore.edit { preferences ->
            when (key) {
                "autoCapitalize" -> preferences[AUTO_CAPITALIZE] = value as Boolean
                "vibration" -> preferences[VIBRATION_ENABLED] = value as Boolean
            }
        }
    }
}
```

### Git Workflow

1. **Branch Naming**
   - Feature branches: `feature/description`
   - Bug fixes: `fix/description`
   - Refactoring: `refactor/description`
   - Claude AI branches: `claude/claude-md-*` (auto-managed)

2. **Commit Messages**
   - Use conventional commits format
   - Clear, concise descriptions
   - Reference issues when applicable

   ```
   feat: add support for hardware keyboard shortcuts
   fix: resolve key repeat issue on long press
   refactor: reorganize IME service structure
   docs: update README with installation instructions
   ```

3. **Pull Requests**
   - Provide clear description of changes
   - Include testing performed
   - Reference related issues

### Build and Release

1. **Debug Builds**
   - `./gradlew assembleDebug` - Build debug APK
   - Enable debug logging

2. **Release Builds**
   - `./gradlew assembleRelease` - Build release APK
   - Configure signing keys
   - Enable ProGuard/R8 optimization
   - Test thoroughly before release

3. **Installation**
   - Debug: `adb install -r app/build/outputs/apk/debug/app-debug.apk`
   - After installation, enable IME in Android Settings > System > Languages & input > Virtual keyboard

## AI Assistant Guidelines

### When Working on This Project

1. **Understand IME Context**
   - This is an Input Method Editor for Android
   - Physical keyboard focus, not on-screen keyboard
   - Device-specific optimization for Unihertz Titan 2

2. **Android Development Patterns**
   - **Architecture**: Follow clean architecture with separation of concerns (UI → Domain → Data)
   - **State Management**: Use StateFlow for UI state, prefer immutable data classes
   - **Dependency Injection**: Use Hilt for all dependency injection
   - **Coroutines**: Use structured concurrency, proper scopes (viewModelScope, lifecycleScope)
   - **Compose**: Declarative UI with Material3, proper state hoisting
   - **Lifecycle**: Use lifecycle-aware components, collect flows with `collectAsStateWithLifecycle()`
   - **No Deprecated APIs**: Only use modern APIs, no backwards compatibility code
   - **R8 Optimization**: Write code that optimizes well with R8 (avoid reflection where possible)
   - **Type Safety**: Leverage Kotlin's type system, use sealed classes for states/events

3. **Testing Before Committing**
   - Ensure code compiles without errors
   - Run lint checks
   - Verify manifest configuration
   - Check for common Android pitfalls

4. **Documentation**
   - Document complex IME logic
   - Add KDoc comments for public APIs
   - Update README when adding user-facing features
   - Keep this CLAUDE.md updated with architectural decisions

5. **Dependencies**
   - Use version catalog (`libs.versions.toml`) for all dependencies
   - Use Bill of Materials (BOM) for Compose dependencies
   - Only use modern AndroidX libraries (no legacy support libraries)
   - Prefer KSP over KAPT for annotation processing
   - Use stable, well-maintained libraries
   - Document why each dependency is needed

   **Essential Dependencies:**
   - Jetpack Compose (UI)
   - Hilt (Dependency Injection)
   - Coroutines + Flow (Async)
   - DataStore (Preferences)
   - Lifecycle + ViewModel (Architecture)
   - Material3 (Design)
   - JUnit5 + Mockk + Turbine (Testing)

### Common Tasks

#### Adding a New Feature

1. Plan the implementation
2. Create necessary classes/files
3. Update manifest if needed
4. Add resources (strings, layouts, etc.)
5. Implement the feature
6. Add tests
7. Update documentation
8. Commit with clear message

#### Fixing a Bug

1. Reproduce the bug
2. Identify root cause
3. Implement fix
4. Add test to prevent regression
5. Commit with "fix:" prefix

#### Refactoring

1. Ensure existing tests pass
2. Make incremental changes
3. Keep tests passing throughout
4. Update documentation if architecture changes
5. Commit with "refactor:" prefix

## Important Notes

### Security Considerations

- **User Privacy**: IME has access to all user input
  - Never log sensitive user input
  - Never transmit user data without explicit consent
  - Clearly document any data collection
  - Follow Android's privacy guidelines

- **Permissions**: Request minimal necessary permissions
- **Data Storage**: Encrypt any stored user data

### Performance

- **Key Latency**: Minimize delay between key press and character appearance
- **Memory**: Keep memory footprint small
- **Battery**: Avoid background processing that drains battery

### Device-Specific Considerations

**Unihertz Titan 2 Specifications:**

- **Android Version**: Android 15 (native, fully supported)
- **Display**:
  - Primary: 4.5" square display, 1440 × 1440 pixels, 60Hz refresh rate
  - Secondary: 2" rear display, 410 × 502 pixels
- **Processor**: MediaTek Dimensity 7300 (5G) Octa-Core 2.0-2.6 GHz
- **Memory**: 12GB LPDDR5 RAM
- **Storage**: 512GB UFS 3.1
- **Battery**: 5050mAh with 33W fast charging
- **Dimensions**: 137.8 × 88.7 × 10.8mm, 235g

**Physical QWERTY Keyboard Features:**
- Full QWERTY layout with A-Z keys plus function keys
- Customizable key assignments (long-press and short-press shortcuts for each letter key)
- Scroll Assistant: swipe on keyboard surface to browse
- Cursor Assistant: move cursor via keyboard gestures
- Backlit keyboard with adjustable brightness
- Trackpad-like functionality: swipe fingers across key tops to scroll or move cursor
- Dedicated modifier keys (Shift, Alt, Sym)
- Multi-language support

**Development Focus:**
- Physical key event handling, not virtual keyboard rendering
- Suggestions bar rendering, emoji board rendering, alternative characters not featured on the physical board rendering will all need to be implimented
- Optimize for square display aspect ratio (1:1)
- Support customizable key mapping and gestures
- Integrate with hardware keyboard features (scroll/cursor assistants)

### Modern Android Best Practices

**Kotlin Best Practices**
- Use Kotlin idioms: extension functions, scope functions, null safety
- Prefer `when` expressions over `if-else` chains
- Use data classes for models with immutability
- Leverage coroutines for all async work
- Use Flow for reactive data streams

**Jetpack Compose Best Practices**
- Single source of truth for state
- State hoisting to appropriate levels
- Composition over inheritance
- Use `remember` and `derivedStateOf` appropriately
- Avoid side effects in composables, use `LaunchedEffect`, `DisposableEffect`
- Material3 theming with dynamic colors support

**Performance Optimizations**
- Use R8 full mode for code shrinking and optimization
- Baseline profiles for improved startup performance
- Lazy loading where appropriate
- Avoid unnecessary recompositions in Compose
- Profile with Android Studio Profiler

**Security & Privacy**
- Never log user input (PII)
- Use encrypted DataStore for sensitive preferences
- Follow Android 14/15 privacy requirements
- Request minimal permissions
- Be transparent about data usage

**Build Configuration**
- Use non-transitive R classes for faster builds
- Enable configuration cache
- Use KSP instead of KAPT
- Leverage Gradle build cache
- Version catalogs for dependency management

## Resources

### Documentation

**Android Official**
- [Android 15 Documentation](https://developer.android.com/about/versions/15)
- [Android Input Method Framework](https://developer.android.com/develop/ui/views/touch-and-input/creating-input-method)
- [InputMethodService Reference](https://developer.android.com/reference/android/inputmethodservice/InputMethodService)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Kotlin Coroutines Guide](https://developer.android.com/kotlin/coroutines)
- [Hilt Documentation](https://developer.android.com/training/dependency-injection/hilt-android)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)

**Kotlin**
- [Kotlin Language Reference](https://kotlinlang.org/docs/reference/)
- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Flow API](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/)

**Device**
- [Unihertz Titan 2 Specifications](https://www.unihertz.com/products/titan-2)

### Tools

**Required**
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 35 (Android 15)
- Gradle 8.x
- JDK 17

**Development**
- ADB for device debugging
- Logcat for runtime logging
- Android Studio Profiler (CPU, Memory, Network)
- Layout Inspector for Compose debugging
- Physical Unihertz Titan 2 device (required for proper testing)

## License

This project is licensed under the Apache License 2.0. See LICENSE file for details.

---

**Last Updated**: 2025-11-15
**Project Stage**: Initial Setup
**Target Platform**: Android 15 (API 35)
**Architecture**: Clean Architecture with MVVM, Jetpack Compose, Hilt, Coroutines/Flow
