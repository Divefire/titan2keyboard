# AOSP Prototype IME

A minimal Input Method Editor for testing AOSP keyboard functionality on the Unihertz Titan 2.

## Purpose

This prototype validates key assumptions before integrating AOSP components into the main titan2keyboard project:

- **Key Event Handling**: Test KeyCharacterMap and physical keyboard event capture
- **Accent System**: Verify AOSP accent character data availability
- **Gesture Detection**: Test capacitive keyboard touch/motion event capture
- **System Integration**: Check for conflicts with Unihertz system gestures

## Quick Start

### Build

```bash
./gradlew :aosp-prototype:assembleDebug
```

### Install

```bash
adb install -r aosp-prototype/build/outputs/apk/debug/aosp-prototype-debug.apk
```

### Enable

1. Settings → System → Languages & input → On-screen keyboard
2. Manage on-screen keyboards → Enable "AOSP Prototype IME"
3. In a text field, switch to the prototype keyboard

### Use

- Type on the physical keyboard
- Watch the debug log at the bottom of the screen
- Check detailed logs: `adb logcat -s AospPrototype:D`
- Try long-pressing keys
- Try swiping on the capacitive keyboard surface

## What It Tests

✅ Physical key event capture
✅ KeyCharacterMap character mapping
✅ Modifier keys (Shift, Alt, Sym)
✅ Long-press detection
✅ Accent character availability
✅ Capacitive keyboard touch events
✅ Multi-finger gesture detection
✅ Motion event sources
✅ Unihertz gesture conflicts

## Test Plan

See [TEST_PLAN.md](TEST_PLAN.md) for comprehensive testing instructions.

## Project Structure

```
aosp-prototype/
├── src/main/
│   ├── kotlin/com/titan2/prototype/
│   │   ├── AospPrototypeIME.kt      # Main IME service with logging
│   │   └── SettingsActivity.kt      # Instructions screen
│   ├── res/
│   │   ├── xml/method.xml           # IME configuration
│   │   ├── values/strings.xml       # String resources
│   │   └── drawable/ic_launcher.xml # App icon
│   └── AndroidManifest.xml
├── build.gradle.kts
├── TEST_PLAN.md                      # Detailed test cases
└── README.md                         # This file
```

## Key Files

- **AospPrototypeIME.kt**: Logs all key events, motion events, and touch events with detailed information
- **TEST_PLAN.md**: 10 comprehensive test cases to validate all assumptions

## Important Notes

⚠️ **Disable Unihertz gestures** for accurate touch event testing:
- Settings → Gestures → Keyboard gestures
- Turn OFF "Scroll assist" and "Edit assist"

## Next Steps

After testing:

1. Document findings in TEST_PLAN.md
2. Identify which AOSP components to use
3. Plan integration with main titan2keyboard project
4. Convert relevant Java AOSP code to Kotlin
5. Merge validated features into production IME

## License

Apache 2.0 (same as main project)
