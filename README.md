# Titan 2 Keyboard

A modern Input Method Editor (IME) keyboard application specifically designed for the Unihertz Titan 2 physical QWERTY keyboard.

## Overview

Titan 2 Keyboard is built from the ground up using the latest Android technologies and best practices, targeting Android 15 (API 35) with no legacy code or backwards compatibility concerns. It enhances the typing experience on the Unihertz Titan 2's physical keyboard through intelligent key handling, customizable settings, and modern Android integration.

## Features

- **Physical Keyboard Optimization** - Designed specifically for Titan 2's 48-key QWERTY layout
- **Modern Architecture** - Clean Architecture with MVVM, Jetpack Compose, and Hilt DI
- **Reactive Settings** - Real-time settings updates using Kotlin Flow and DataStore
- **Material Design 3** - Modern UI with dynamic theming support
- **Customizable Behavior**
  - Vibration feedback
  - Sound feedback
  - Auto-capitalization
  - Key repeat configuration

## Technology Stack

### Core
- **Android 15** (API 35) - Target platform
- **Kotlin** - 100% Kotlin codebase
- **Jetpack Compose** - Modern declarative UI
- **Material Design 3** - Latest design system

### Architecture
- **Clean Architecture** - Clear separation: UI → Domain → Data
- **MVVM/MVI** - Unidirectional data flow
- **Hilt** - Compile-time dependency injection
- **Kotlin Coroutines** - Structured concurrency
- **Flow & StateFlow** - Reactive streams

### Key Libraries
- AndroidX Core, Lifecycle, Activity
- Jetpack Compose (UI, Material3)
- Hilt (DI)
- DataStore (Preferences)
- JUnit 5, MockK, Turbine (Testing)

### Build
- Gradle 8.x with Kotlin DSL
- Version Catalogs
- KSP (Kotlin Symbol Processing)
- R8 Full Mode

## Requirements

- **Device**: Unihertz Titan 2 (or compatible physical keyboard device)
- **Android**: 14+ (API 34+) - App targets Android 15
- **Development**:
  - Android Studio Hedgehog (2023.1.1) or later
  - JDK 17
  - Android SDK 35

## Installation

### For Users

1. Download the APK from releases
2. Install on your Unihertz Titan 2
3. Enable the keyboard:
   - Settings → System → Languages & input → Virtual keyboard
   - Enable "Titan2 Keyboard"
4. Select as active keyboard

### For Developers

```bash
# Clone the repository
git clone https://github.com/Divefire/titan2keyboard.git
cd titan2keyboard

# Build the project
./gradlew build

# Install debug build on connected device
./gradlew installDebug
```

## Project Structure

```
titan2keyboard/
├── app/
│   └── src/main/kotlin/com/titan2keyboard/
│       ├── Titan2KeyboardApp.kt          # Application class
│       ├── di/                            # Hilt modules
│       ├── domain/                        # Business logic
│       │   ├── model/                     # Domain models
│       │   └── repository/                # Repository interfaces
│       ├── data/                          # Data layer
│       │   ├── repository/                # Repository implementations
│       │   └── datastore/                 # DataStore preferences
│       ├── ime/                           # IME service
│       │   ├── Titan2InputMethodService.kt
│       │   └── KeyEventHandler.kt
│       └── ui/                            # Compose UI
│           ├── theme/                     # Material3 theme
│           └── settings/                  # Settings screen
├── gradle/libs.versions.toml              # Version catalog
└── CLAUDE.md                              # AI assistant guide
```

## Development

See [CLAUDE.md](CLAUDE.md) for comprehensive development guidelines, architecture patterns, and best practices.

### Building

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test

# Run lint
./gradlew lint
```

### Testing

The project uses modern testing practices:

- **Unit Tests**: JUnit 5 + MockK + Turbine
- **UI Tests**: Compose Testing
- **Coroutines Tests**: kotlinx-coroutines-test

```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests "SettingsRepositoryImplTest"
```

## Architecture

This project follows **Clean Architecture** principles:

- **UI Layer** (Compose) - User interface and ViewModels
- **Domain Layer** - Business logic, models, and repository interfaces
- **Data Layer** - Repository implementations and data sources

Key patterns:
- Dependency Injection via Hilt
- Reactive state management with StateFlow
- Repository pattern for data access
- Use cases for complex business logic (when needed)

## Contributing

Contributions are welcome! Please ensure:

1. Follow the coding conventions in [CLAUDE.md](CLAUDE.md)
2. Write tests for new features
3. Update documentation as needed
4. Use conventional commits format

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Built for the [Unihertz Titan 2](https://www.unihertz.com/products/titan-2) community
- Powered by modern Android development tools and libraries

---

**Status**: Active Development
**Target**: Android 15 (API 35)
**Architecture**: Clean Architecture with MVVM, Compose, Hilt
