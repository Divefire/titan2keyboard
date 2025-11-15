# CLAUDE.md - AI Assistant Guide for titan2keyboard

## Project Overview

**titan2keyboard** is a modern Input Method Editor (IME) keyboard specifically designed for the Unihertz Titan 2 device. This is an Android keyboard application that provides an enhanced typing experience optimized for the physical QWERTY keyboard found on the Titan 2.

## Project Status

This is a newly initialized project. The codebase is in early development with minimal structure established.

## Technology Stack

### Expected Technologies
- **Platform**: Android
- **Language**: Kotlin (preferred) or Java
- **Build System**: Gradle with Android Gradle Plugin
- **Target Device**: Unihertz Titan 2 (physical QWERTY keyboard)
- **Minimum SDK**: TBD (Titan 2 runs Android 11+, so minSdk should be 30+)
- **Target SDK**: Latest stable Android version

### Key Android Components
- `InputMethodService` - Core IME service
- `KeyboardView` - Custom keyboard view for physical key handling
- Android Input Method Framework
- Accessibility Services for enhanced functionality

## Expected Project Structure

```
titan2keyboard/
├── app/                          # Main application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/titan2keyboard/
│   │   │   │   ├── ime/         # IME service implementation
│   │   │   │   ├── keyboard/    # Keyboard layout and handling
│   │   │   │   ├── settings/    # Settings and preferences
│   │   │   │   ├── utils/       # Utility classes
│   │   │   │   └── models/      # Data models
│   │   │   ├── res/
│   │   │   │   ├── layout/      # UI layouts
│   │   │   │   ├── values/      # Strings, colors, dimensions
│   │   │   │   ├── xml/         # Keyboard layouts, method definitions
│   │   │   │   └── drawable/    # Icons and graphics
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                # Unit tests
│   │   └── androidTest/         # Instrumentation tests
│   ├── build.gradle.kts         # App-level build configuration
│   └── proguard-rules.pro       # ProGuard configuration
├── buildSrc/                     # Build logic and dependencies
├── gradle/                       # Gradle wrapper
├── build.gradle.kts             # Project-level build configuration
├── settings.gradle.kts          # Gradle settings
├── gradle.properties            # Gradle properties
├── LICENSE                      # Apache 2.0 License
├── README.md                    # User-facing documentation
└── CLAUDE.md                    # This file
```

## Development Workflows

### Setting Up the Project

When initializing the Android project structure:

1. **Create Android Application Structure**
   - Use standard Android project layout
   - Set up Gradle build system with Kotlin DSL
   - Configure proper package structure: `com.titan2keyboard.*`

2. **Configure Build Files**
   - Set appropriate `minSdkVersion` (30+)
   - Configure `targetSdkVersion` to latest stable
   - Add necessary dependencies (AndroidX, Material Design, etc.)

3. **Manifest Configuration**
   - Declare `InputMethodService`
   - Add required permissions (if any)
   - Configure intent filters for IME service

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

1. **Unit Tests**
   - Test key mapping logic
   - Test text processing algorithms
   - Mock Android framework dependencies

2. **Integration Tests**
   - Test IME service lifecycle
   - Test input connection handling
   - Test settings persistence

3. **Manual Testing**
   - Test on actual Unihertz Titan 2 device
   - Test various input scenarios
   - Test integration with different apps

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
// Example structure for IME Service
class Titan2KeyboardService : InputMethodService() {
    // Companion object for constants
    companion object {
        private const val TAG = "Titan2KeyboardService"
    }

    // Properties (lateinit, lazy initialization)
    private lateinit var keyboardView: View

    // Lifecycle methods
    override fun onCreateInputView(): View { }
    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) { }

    // Key handling
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean { }
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean { }

    // Helper methods (private)
    private fun processKeyEvent(event: KeyEvent) { }
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
   - Follow Android architecture best practices
   - Use AndroidX libraries
   - Implement proper lifecycle management
   - Handle configuration changes

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
   - Minimize external dependencies
   - Prefer AndroidX over support libraries
   - Use stable, well-maintained libraries
   - Document why each dependency is needed

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

- **Titan 2 Hardware**: Physical QWERTY keyboard with specific layout
- **Screen Size**: Compact display (4.2" 768x1280)
- **Android Version**: Runs Android 11, ensure compatibility

## Resources

### Documentation

- [Android Input Method Framework](https://developer.android.com/develop/ui/views/touch-and-input/creating-input-method)
- [InputMethodService Reference](https://developer.android.com/reference/android/inputmethodservice/InputMethodService)
- [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)
- [Unihertz Titan 2 Specifications](https://www.unihertz.com/products/titan-2)

### Tools

- Android Studio (recommended IDE)
- ADB for debugging
- Logcat for runtime logging
- Android Emulator (limited testing - physical device recommended)

## License

This project is licensed under the Apache License 2.0. See LICENSE file for details.

---

**Last Updated**: 2025-11-15
**Project Stage**: Initial Setup
