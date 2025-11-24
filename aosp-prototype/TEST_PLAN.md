# AOSP Prototype Test Plan

## Overview

This prototype tests AOSP IME functionality on the Unihertz Titan 2 to validate assumptions before merging with the main titan2keyboard project.

## Setup Instructions

### 1. Build and Install

```bash
# From project root
./gradlew :aosp-prototype:assembleDebug
adb install -r aosp-prototype/build/outputs/apk/debug/aosp-prototype-debug.apk
```

### 2. Enable the IME

1. Go to **Settings → System → Languages & input → On-screen keyboard**
2. Tap **Manage on-screen keyboards**
3. Enable **AOSP Prototype IME**
4. Open any text field (Messages, Notes, Chrome, etc.)
5. Long-press the spacebar or use keyboard switcher to select **AOSP Prototype IME**

### 3. Monitor Logs

```bash
# Watch real-time logs
adb logcat -s AospPrototype:D

# Or filter for specific events
adb logcat -s AospPrototype:D | grep "KEY"
adb logcat -s AospPrototype:D | grep "MOTION"
adb logcat -s AospPrototype:D | grep "TOUCH"
```

## Test Cases

### ✅ Test 1: Basic Key Events

**Objective:** Verify physical key press detection and character mapping

**Steps:**
1. Open a text field with prototype IME active
2. Press various letter keys (a-z)
3. Observe the debug log at bottom of screen and in logcat

**Expected Results:**
- [ ] Each key press logs `KEY DOWN` event
- [ ] Each key release logs `KEY UP` event
- [ ] `KeyCharacterMap` shows correct base character
- [ ] Unicode character code is logged
- [ ] Key labels match physical keys

**Actual Results:**
```
[Record your observations here]
```

---

### ✅ Test 2: Modifier Keys

**Objective:** Test Shift, Alt, Sym key combinations

**Steps:**
1. Press **Shift + A** (should produce 'A')
2. Press **Alt + E** (test for accent character)
3. Press **Sym + 1** (test for symbol)
4. Try various modifier combinations

**Expected Results:**
- [ ] Shift modifier is detected and logged
- [ ] Alt modifier is detected and logged
- [ ] Sym modifier is detected and logged
- [ ] `KeyCharacterMap` shows different characters for each modifier
- [ ] Meta state is logged correctly

**Actual Results:**
```
[Record your observations here]
```

---

### ✅ Test 3: Long Press Detection

**Objective:** Test long-press key detection for accent popups

**Steps:**
1. Press and hold the **E** key for 1+ seconds
2. Press and hold the **A** key for 1+ seconds
3. Try other vowels (I, O, U)

**Expected Results:**
- [ ] `LONG PRESS DETECTED` message appears
- [ ] Duration is logged correctly (>500ms)
- [ ] Can distinguish between short and long presses

**Actual Results:**
```
[Record your observations here]
```

---

### ✅ Test 4: Accent Character Data

**Objective:** Check if AOSP provides accent character data

**Steps:**
1. Look at logs for "Popup chars available" messages
2. Long-press vowels (E, A, I, O, U)
3. Check if `KeyCharacterMap.getKeyData()` returns accent options

**Expected Results:**
- [ ] Accent characters are available for vowels
- [ ] Log shows which accents are available (é, è, ê, ë, etc.)
- [ ] Data matches expected language (English defaults)

**Actual Results:**
```
[Record your observations here]
```

---

### ✅ Test 5: Capacitive Keyboard Touch Events

**Objective:** Test if IME receives touch events from capacitive keyboard surface

**Steps:**
1. **IMPORTANT:** Disable Unihertz gestures: Settings → Gestures → Keyboard gestures (turn OFF scroll assist and edit assist)
2. With text field active, swipe finger across keyboard surface (left, right, up, down)
3. Try single-finger swipe
4. Try two-finger swipe
5. Try tapping on keyboard surface

**Expected Results:**
- [ ] `GENERIC MOTION` or `TOUCH` events are logged
- [ ] Source is identified (TOUCHSCREEN, TOUCHPAD, or other)
- [ ] X/Y coordinates are captured
- [ ] Pointer count shows multiple fingers correctly
- [ ] Swipe direction can be determined from coordinates

**Actual Results:**
```
[Record your observations here]

CRITICAL: Does the IME receive these events, or does the system intercept them?
```

---

### ✅ Test 6: Multi-Finger Gestures

**Objective:** Test multi-finger gesture detection

**Steps:**
1. Place 2 fingers on keyboard surface and swipe
2. Place 3 fingers on keyboard surface and swipe
3. Try pinch gesture (if possible)

**Expected Results:**
- [ ] Pointer count > 1 is detected
- [ ] Each finger's position is logged separately
- [ ] Pressure values are available (if supported)
- [ ] Can distinguish between 2-finger and 3-finger gestures

**Actual Results:**
```
[Record your observations here]
```

---

### ✅ Test 7: Unihertz System Gesture Conflict

**Objective:** Determine if Unihertz gestures block IME events

**Test 7a: System Gestures Enabled**
1. Enable Settings → Gestures → Keyboard gestures → Scroll assist
2. Enable Settings → Gestures → Keyboard gestures → Edit assist
3. Try swiping on keyboard surface
4. Check if IME receives events

**Expected Results:**
- [ ] System handles scrolling (scroll assist active)
- [ ] IME may not receive touch events (blocked by system)
- [ ] Double-tap requirement for cursor movement (edit assist)

**Actual Results:**
```
[Record your observations here]
```

**Test 7b: System Gestures Disabled**
1. Disable all Unihertz keyboard gestures
2. Try swiping on keyboard surface
3. Check if IME now receives events

**Expected Results:**
- [ ] IME receives more/all touch events
- [ ] Greater control over gesture handling

**Actual Results:**
```
[Record your observations here]
```

---

### ✅ Test 8: Motion Event Sources

**Objective:** Identify what input sources report

**Steps:**
1. Check logs for source types
2. Physical key press → Should show `SOURCE_KEYBOARD`
3. Keyboard surface touch → Should show `SOURCE_TOUCHSCREEN` or `SOURCE_TOUCHPAD`
4. External mouse (if available) → Should show `SOURCE_MOUSE`

**Expected Results:**
- [ ] Different input sources are identified correctly
- [ ] Capacitive keyboard reports as TOUCHSCREEN or TOUCHPAD
- [ ] Physical keys report as KEYBOARD

**Actual Results:**
```
[Record your observations here]
```

---

### ✅ Test 9: Text Input Connection

**Objective:** Verify IME can commit text to apps

**Steps:**
1. With IME active, type some characters
2. Check if characters appear in the text field
3. Try in different apps (Messages, Chrome, Notes)

**Expected Results:**
- [ ] Characters commit to text field successfully
- [ ] Works across different apps
- [ ] No lag or dropped characters

**Actual Results:**
```
[Record your observations here]
```

---

### ✅ Test 10: Input Context Types

**Objective:** Test different input field types

**Steps:**
1. Try normal text field
2. Try password field
3. Try email field
4. Try number field
5. Try URL field

**Expected Results:**
- [ ] `onStartInput` logs show different input types
- [ ] Editor info shows correct type (TEXT, NUMBER, etc.)
- [ ] Hint text and labels are captured

**Actual Results:**
```
[Record your observations here]
```

---

## Key Questions to Answer

After testing, document answers to these critical questions:

### 1. KeyCharacterMap Functionality
- ✅ Does KeyCharacterMap provide comprehensive character mappings?
- ✅ Are accent characters available via `getKeyData()`?
- ✅ Do modifier combinations work correctly?

**Answer:**
```
[Your findings]
```

### 2. Gesture/Touch Event Availability
- ✅ Can the IME intercept capacitive keyboard touch events?
- ✅ What is the event source type?
- ✅ Do Unihertz system gestures block IME access?
- ✅ Must users disable system gestures?

**Answer:**
```
[Your findings]
```

### 3. Multi-Finger Detection
- ✅ Can the IME detect 2-finger gestures?
- ✅ Can the IME detect 3-finger gestures?
- ✅ Are pressure/size values available?

**Answer:**
```
[Your findings]
```

### 4. Long-Press Behavior
- ✅ Is long-press reliably detected?
- ✅ What is the threshold timing?
- ✅ Can we customize the popup that appears?

**Answer:**
```
[Your findings]
```

### 5. AOSP Popup System
- ✅ Does AOSP have a built-in accent popup?
- ✅ How is it triggered?
- ✅ Can we customize its appearance/behavior?

**Answer:**
```
[Your findings]
```

---

## Performance Notes

Document any performance observations:

- Latency between key press and log appearance: ___ms
- Touch event sampling rate: ___Hz
- Any lag or dropped events: Yes/No
- Battery impact: High/Medium/Low

---

## Next Steps

Based on test results, decide:

1. **Use AOSP Components:**
   - [ ] KeyCharacterMap for character mapping
   - [ ] Accent character data structure
   - [ ] Popup UI system
   - [ ] Input logic framework

2. **Build Custom:**
   - [ ] Gesture recognition system
   - [ ] Accent popup interaction (press-to-cycle)
   - [ ] Multi-finger gesture handling
   - [ ] Language-aware accent filtering

3. **Integration Plan:**
   - Document which AOSP files to extract
   - Plan conversion from Java → Kotlin
   - Design integration with existing titan2keyboard architecture

---

## Appendix: Useful ADB Commands

```bash
# Install prototype
adb install -r aosp-prototype/build/outputs/apk/debug/aosp-prototype-debug.apk

# Watch logs
adb logcat -s AospPrototype:D

# Clear logs
adb logcat -c

# Save logs to file
adb logcat -s AospPrototype:D > test-results.log

# Check input devices
adb shell dumpsys input

# List enabled IMEs
adb shell ime list -s

# Enable IME via command line
adb shell ime enable com.titan2.prototype/.AospPrototypeIME

# Set as default IME
adb shell ime set com.titan2.prototype/.AospPrototypeIME

# Uninstall
adb uninstall com.titan2.prototype
```

---

## Test Session Log Template

```
Date: _______________
Device: Unihertz Titan 2
Android Version: _______________
Prototype Version: 0.1.0

System Gestures: Enabled / Disabled

Test Results:
- Test 1: Pass / Fail
- Test 2: Pass / Fail
- Test 3: Pass / Fail
- Test 4: Pass / Fail
- Test 5: Pass / Fail
- Test 6: Pass / Fail
- Test 7: Pass / Fail
- Test 8: Pass / Fail
- Test 9: Pass / Fail
- Test 10: Pass / Fail

Key Findings:
[Write summary here]

Blockers:
[List any issues that would prevent implementation]

Recommendations:
[Suggest approach for full implementation]
```
