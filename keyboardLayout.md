# Keyboard Layout Support for Titan2Keyboard

## Overview

This document explains how to add support for alternative physical keyboard layouts (like AZERTY, QWERTZ, etc.) on the Unihertz Titan 2. There are multiple approaches available, each with different trade-offs.

## Understanding Android's Physical Keyboard Architecture

Android handles physical keyboards through two layers:

### Layer 1: System-Level Keyboard Mapping

**Key Layout Files (.kl)**
- Map hardware scan codes to Android key codes
- Located in `/system/usr/keylayout/`
- Example: `Generic.kl`, `Vendor_XXXX_Product_XXXX.kl`

**Key Character Map Files (.kcm)**
- Map Android key codes + modifiers to characters
- Located in `/system/usr/keychars/`
- For Titan 2: `keyboard_layout_agui_titan-key.kcm`
- Defines what character appears when you press a key

### Layer 2: IME (Input Method Editor)

- Receives the characters produced by Layer 1
- Can intercept and modify behavior
- Your Titan2Keyboard app operates at this layer

## Unihertz Engineer's Recommendation: RRO APK

### What is an RRO (Runtime Resource Overlay)?

A **Runtime Resource Overlay** is a special type of Android APK that can overlay (replace) resources in another package at runtime without modifying system files.

**Key Benefits:**
- ✅ No root required
- ✅ Installable as a normal APK
- ✅ Can be distributed via Play Store
- ✅ Non-invasive (doesn't modify system files)
- ✅ Updatable independently
- ✅ Works system-wide (all apps benefit)

**Target Package:** `com.android.inputdevices`
**Target File:** `keyboard_layout_agui_titan-key.kcm`
**Source in AOSP:** `frameworks/base/packages/InputDevices`

### How RRO Works

```
┌────────────────────────────────────────┐
│ System: com.android.inputdevices      │
│  Resources:                            │
│   └─ keyboard_layout_agui_titan-key.kcm │ ← Original QWERTY
└────────────────────────────────────────┘
              ↓ overlaid by
┌────────────────────────────────────────┐
│ Your RRO APK: com.yourname.titan2azerty│
│  Overlay Resources:                    │
│   └─ keyboard_layout_agui_titan-key.kcm │ ← Your AZERTY
└────────────────────────────────────────┘
              ↓ runtime result
         AZERTY layout active!
```

### Creating an RRO APK

**Project Structure:**
```
Titan2KeyboardLayoutAZERTY/
├── AndroidManifest.xml
├── res/
│   └── raw/
│       └── keyboard_layout_agui_titan-key.kcm
└── build.gradle.kts
```

**AndroidManifest.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.yourname.titan2azerty">

    <overlay
        android:targetPackage="com.android.inputdevices"
        android:priority="1"
        android:isStatic="true" />

    <application
        android:label="Titan2 AZERTY Layout"
        android:hasCode="false" />
</manifest>
```

**build.gradle.kts:**
```kotlin
plugins {
    id("com.android.application")
}

android {
    compileSdk = 35
    namespace = "com.yourname.titan2azerty"

    defaultConfig {
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}
```

## Key Character Map (.kcm) File Format

### Basic Syntax

KCM files are plain text files that define how key combinations produce characters.

**Type Declaration:**
```
type OVERLAY
```

**Key Remapping (optional):**
```
# Swap Y and Z keys (for QWERTZ)
map key 21 Z
map key 44 Y
```

**Key Definition Syntax:**
```
key [KEY_NAME] {
    label: '[display_label]'
    base: '[normal_character]'
    shift: '[shifted_character]'
    capslock: '[caps_lock_character]'
    shift+capslock: '[shift+caps_character]'
    ralt: '[right_alt_character]'
    ctrl: none
    alt: none
    meta: none
}
```

### Example: AZERTY Layout Snippet

```
# Copyright 2025 Your Name
# Licensed under the Apache License, Version 2.0

type OVERLAY

### ROW 1 - Number keys (AZERTY style)
key 1 {
    label: '1'
    base: '&'
    shift: '1'
    ralt: '|'
}

key 2 {
    label: '2'
    base: '\u00e9'  # é
    shift: '2'
    ralt: '@'
}

key 3 {
    label: '3'
    base: '"'
    shift: '3'
    ralt: '#'
}

key 4 {
    label: '4'
    base: '\u0027'  # '
    shift: '4'
    ralt: '{'
}

# ... etc

### ROW 2 - AZERTY top row
key A {
    label: 'A'
    base: 'a'
    shift, capslock: 'A'
    ctrl, alt, meta: none
}

key Z {
    label: 'Z'
    base: 'z'
    shift, capslock: 'Z'
    ctrl, alt, meta: none
}

key E {
    label: 'E'
    base: 'e'
    shift, capslock: 'E'
    ralt: '\u20ac'  # € symbol
    ctrl, alt, meta: none
}

# ... continue for all keys
```

### Common Unicode Escapes

- `\u00e9` = é (e with acute accent)
- `\u00e8` = è (e with grave accent)
- `\u00e0` = à (a with grave accent)
- `\u00f9` = ù (u with grave accent)
- `\u00e7` = ç (c with cedilla)
- `\u20ac` = € (Euro symbol)
- `\u00a3` = £ (Pound symbol)
- `\u00df` = ß (German sharp S)
- `\u00fc` = ü (u with umlaut)
- `\u00f6` = ö (o with umlaut)
- `\u00e4` = ä (a with umlaut)

## Alternative Approaches

### Approach 1: RRO APK (Recommended by Unihertz)

**Pros:**
- ✅ System-wide effect (works in all apps)
- ✅ Most integrated solution
- ✅ No root required
- ✅ Can be distributed as APK

**Cons:**
- ❌ More complex to implement
- ❌ Requires exact filename match
- ❌ Must target correct package
- ❌ Separate APK for each layout

**Best for:** Official layout support, maximum compatibility

---

### Approach 2: Standalone Layout App (like titanqwerty)

**Reference Project:** https://github.com/bluedskim/titanqwerty

**How it Works:**
- Creates an app using Android's `KeyboardLayout` API
- Users select layout in Settings → Physical Keyboard
- Multiple layouts in one app

**Implementation:**
```kotlin
// Define keyboard layout in XML
<keyboard-layouts xmlns:android="http://schemas.android.com/apk/res/android">
    <keyboard-layout
        android:name="azerty"
        android:label="AZERTY"
        android:keyboardLayout="@raw/keyboard_layout_azerty" />
</keyboard-layouts>
```

**Pros:**
- ✅ Easier to implement
- ✅ Well-documented approach
- ✅ Multiple layouts in one app
- ✅ Standard Android mechanism

**Cons:**
- ❌ User must manually select layout
- ❌ Less integrated than RRO

**Best for:** Providing layout options to users, multi-layout support

---

### Approach 3: Software Remapping in IME

**How it Works:**
- Intercept key events in your IME
- Remap characters in software based on user preference
- Store layout preference in app settings

**Implementation Sketch:**
```kotlin
// In KeyEventHandler.kt
private var keyboardLayout: KeyboardLayout = KeyboardLayout.QWERTY

override fun handleKeyDown(event: KeyEvent, inputConnection: InputConnection?): KeyEventResult {
    // Get the character from the layout
    val char = keyboardLayout.getCharForKey(event.keyCode, modifiers)

    // Commit the remapped character
    inputConnection?.commitText(char, 1)
    return KeyEventResult.Handled
}
```

**Pros:**
- ✅ No separate APK needed
- ✅ Fully under your control
- ✅ Can switch layouts on the fly
- ✅ Easy to add new layouts

**Cons:**
- ❌ Only works when your IME is active
- ❌ Might have slight latency
- ❌ Doesn't affect system shortcuts

**Best for:** Quick prototyping, IME-specific layouts

---

## AOSP Reference Files

### InputDevices Package

**Location in AOSP:**
```
frameworks/base/packages/InputDevices/
├── AndroidManifest.xml
├── res/
│   ├── raw/
│   │   ├── keyboard_layout_german.kcm        (QWERTZ)
│   │   ├── keyboard_layout_french.kcm        (AZERTY)
│   │   ├── keyboard_layout_spanish.kcm
│   │   ├── keyboard_layout_english_us.kcm    (QWERTY)
│   │   └── ... (many others)
│   └── xml/
│       └── keyboard_layouts.xml
└── src/...
```

**GitHub Mirror:**
https://github.com/aosp-mirror/platform_frameworks_base/tree/master/packages/InputDevices

### Existing Layout Examples

**German (QWERTZ):**
https://github.com/aosp-mirror/platform_frameworks_base/blob/master/packages/InputDevices/res/raw/keyboard_layout_german.kcm

**French (AZERTY):**
https://github.com/aosp-mirror/platform_frameworks_base/blob/master/packages/InputDevices/res/raw/keyboard_layout_french.kcm

**Spanish:**
https://github.com/aosp-mirror/platform_frameworks_base/blob/master/packages/InputDevices/res/raw/keyboard_layout_spanish.kcm

These files serve as excellent templates for creating custom layouts.

---

## Community Projects

### titanqwerty
- **GitHub:** https://github.com/bluedskim/titanqwerty
- **Description:** Keyboard layouts for Android phones with physical keyboards
- **Approach:** Standalone layout selection app
- **Devices:** Unihertz Titan, Titan Pocket, F(x)tec Pro1
- **Installation:** APK via Google Play or manual installation

### titanpocketkeyboard
- **GitHub:** https://github.com/oin/titanpocketkeyboard
- **Description:** Custom IME for Unihertz Titan Pocket
- **Languages:** French QWERTY, English, Spanish, Portuguese, German
- **Approach:** Full IME replacement

---

## Recommended Implementation Path

### Phase 1: Research and Preparation
1. ✅ **Done:** Research RRO and KCM file formats
2. Extract the original `keyboard_layout_agui_titan-key.kcm` from Titan 2 (if possible)
3. Study AOSP reference layouts (German, French)

### Phase 2: Prototype
**Option A: Quick Prototype (Software Remapping)**
- Add layout selection to Titan2Keyboard settings
- Implement character remapping in `KeyEventHandler`
- Test with AZERTY layout
- Pros: Fast, integrated, no separate APK

**Option B: Proper Solution (RRO APK)**
- Create separate RRO APK project
- Implement AZERTY .kcm file
- Test overlay functionality
- Pros: System-wide, proper solution

### Phase 3: Expansion
- Add more layouts (QWERTZ, Dvorak, Colemak, etc.)
- Create layout picker UI
- Document installation process
- Publish to Play Store

---

## Testing Checklist

When implementing a new keyboard layout:

- [ ] All letter keys produce correct characters
- [ ] Number keys produce correct characters
- [ ] Shift modifier works correctly
- [ ] Alt/AltGr modifier works correctly
- [ ] Special characters accessible (é, ñ, ü, etc.)
- [ ] Punctuation keys in correct positions
- [ ] Caps Lock behavior correct
- [ ] Ctrl/Meta keys don't interfere (set to `none`)
- [ ] Works across different apps
- [ ] System shortcuts still functional
- [ ] No crashes or errors in logcat

---

## Useful Resources

### Official Documentation
- [Android Key Character Map Files](https://source.android.com/docs/core/interaction/input/key-character-map-files)
- [Runtime Resource Overlays (RRO)](https://source.android.com/docs/core/runtime/rros)
- [InputDevices AOSP Package](https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/packages/InputDevices/)

### XDA Forum Threads
- [External keyboard remapping without root [4.1+]](https://xdaforums.com/t/tutorial-external-keyboard-remapping-without-root-4-1.4492481/)
- [Simple way to change physical keyboard keymappings](https://xdaforums.com/t/guide-simple-way-to-change-physical-keyboard-keymappings.2179306/)

### GitHub Projects
- [bluedskim/titanqwerty](https://github.com/bluedskim/titanqwerty) - Layout selection app
- [oin/titanpocketkeyboard](https://github.com/oin/titanpocketkeyboard) - Custom IME
- [Android RRO Examples](https://github.com/MartinStyk/Android-RRO)

---

## Next Steps

**Immediate Actions:**
1. Decide on implementation approach (RRO vs Software Remapping)
2. Extract or create base `keyboard_layout_agui_titan-key.kcm` file
3. Create AZERTY layout variant
4. Test on Titan 2 device

**Long-term Goals:**
- Support multiple layouts (AZERTY, QWERTZ, Dvorak, Colemak)
- Create layout switcher in IME settings
- Document installation and usage
- Publish layout APKs to Play Store
- Collaborate with community on additional layouts

---

## License

Layout files are typically licensed under Apache 2.0 (same as AOSP). Custom implementations should maintain compatible licensing.

---

**Last Updated:** 2025-01-16
**Author:** Claude (AI Assistant) based on research and Unihertz engineer recommendations
**Status:** Research and Planning Phase
