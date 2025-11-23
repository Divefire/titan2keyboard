# Finding Titan 2 Sym Key Hardware Code

To add Titan 2 support to titanqwerty, we need to know the hardware scan code for the Sym key on Titan 2.

## Method 1: Check System Keyboard Files (Recommended)

On your Titan 2, if you have extracted keyboard files following `EXTRACT_KEYBOARD_FILES.md`:

1. Look for the file: `keyboard_layout_agui_titan-key.kcm` or similar
2. Open it in a text editor
3. Look for a line like: `map key XXX SYM`
4. The number (XXX) is what we need!

Example:
```
map key 250 SYM    ← This is the Sym key code!
```

## Method 2: Use Event Viewer App

1. Install "Event Viewer for GMD GestureControl" or similar key event app from Play Store
2. Open the app
3. Press the **Sym** key on your Titan 2 physical keyboard
4. Note the "scanCode" or "keyCode" displayed
5. That's the number we need!

## Method 3: ADB with getevent

If you have ADB access:

```bash
# On your computer
adb shell getevent -l

# Then on Titan 2: Press the Sym key

# Look for output like:
# EV_KEY       KEY_SYM              DOWN
# The number associated with KEY_SYM is what we need
```

## Method 4: Check Existing Titan 2 System Files

If you haven't extracted files yet:

```bash
# Find and read the keyboard character map
adb shell find /system /vendor -name "*.kcm" 2>/dev/null | grep -i titan

# Then read the file:
adb shell cat /system/usr/keychars/[filename].kcm | grep -i sym
```

## What To Look For

You're looking for a number between 1-300 typically. Common values:
- **127**: Titan Pocket
- **250**: Titan (original)
- **???**: Titan 2 (what we need to find!)

## Report Back

Once you find the number, let me know and we'll:
1. Create titan2_*.kcm files with the correct mapping
2. Add them to titanqwerty
3. Build and test

## Alternative: Test Without Sym Key First

If finding the Sym key code is difficult, we can test with a basic layout first:

1. Create a simple QWERTY layout without Sym mapping
2. Test if it shows up in settings
3. If it works, then figure out Sym key later

This validates the approach before worrying about all the details.
