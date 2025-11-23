# Troubleshooting TitanQwerty Installation

## Issue: TitanQwerty Layouts Not Appearing

If you've installed titanqwerty but don't see additional layouts in Settings → Physical keyboard:

### Step 1: Verify Installation

**On your Titan 2:**
1. Go to **Settings → Apps**
2. Search for "TitanQwerty" or "FinQwerty"
3. Confirm it's installed

**Expected app names:**
- "TitanQwerty Keyboard Layouts"
- "FinQwerty" (if you installed the base version)

### Step 2: Check Supported Devices

TitanQwerty from the b1ink301 repo may only support:
- Unihertz Titan (original)
- Unihertz Titan Pocket

**It might NOT support Titan 2** because Titan 2 uses different hardware identifiers!

#### Check Device Compatibility

The app looks for specific keyboard hardware IDs. Your Titan 2 keyboard hardware might be named:
- `agui-titan-key` (Titan original/Pocket)
- `aw9523-key` (Titan Pocket)
- Something different for Titan 2

**To find your keyboard hardware ID:**
1. Install a terminal app (Termux from F-Droid or Play Store)
2. Run: `cat /proc/bus/input/devices`
3. Look for keyboard-related entries
4. Note the "Name=" field

### Step 3: Verify Keyboard Device in Settings

1. Go to **Settings → System → Languages & input → Physical keyboard**
2. What device name do you see?
   - If you see "Titan-key" or similar, note the exact name
   - This is your keyboard's hardware identifier

### Step 4: Check TitanQwerty Supported Devices

The titanqwerty app from b1ink301 declares supported devices in its configuration. If Titan 2 isn't listed, the layouts won't show up.

**Possible solutions:**

#### Option A: Try Different TitanQwerty Fork
There are multiple forks:
- fjdrjr/titanqwerty (main fork)
- bluedskim/titanqwerty (original)
- b1ink301/titanqwerty (your reference)

Try installing from fjdrjr's fork (most actively maintained):
- **Play Store**: "TitanQwerty Keyboard Layouts" by fjdrjr
- **GitHub**: https://github.com/fjdrjr/titanqwerty/releases

#### Option B: Contact Developer
If Titan 2 isn't supported, file an issue on GitHub asking for Titan 2 support:
- Provide your keyboard hardware ID
- Request addition of Titan 2 device support

#### Option C: Create Custom Layout App
If titanqwerty doesn't support Titan 2, you can create your own companion app.

### Step 5: Restart After Installation

Sometimes Android needs a restart to recognize new keyboard layouts:
1. Restart your Titan 2
2. Go back to **Settings → Physical keyboard**
3. Check if new layouts appear

### Step 6: Check Android Version Compatibility

TitanQwerty may have Android version requirements:
- Check your Android version: **Settings → About phone**
- Check titanqwerty requirements on its Play Store or GitHub page
- Titan 2 runs Android 15 - ensure titanqwerty supports this

## Understanding the Architecture

### What You Should See When Everything Works:

#### 1. Physical Keyboard Settings
**Path**: Settings → System → Languages & input → Physical keyboard

You'll see:
```
Physical keyboard
  └─ Titan-key (or your device name)
       ├─ English (US) - QWERTY        [System default]
       ├─ Russian - Cyrillic           [From titanqwerty]
       ├─ German - QWERTZ              [From titanqwerty]
       ├─ Czech                         [From titanqwerty]
       └─ Vietnamese                    [From titanqwerty]
```

#### 2. Virtual Keyboard / IME Settings
**Path**: Settings → System → Languages & input → On-screen keyboard

You'll see:
```
On-screen keyboard
  ├─ Gboard                             [Google's IME]
  ├─ Titan2Keyboard                     [Your IME] ✓ Selected
  └─ [Other installed IMEs]
```

### What's Happening Under the Hood:

```
User presses Q key on physical keyboard
         ↓
Android reads hardware scan code
         ↓
System applies selected .kcm layout (from titanqwerty)
  - If QWERTY selected: Q → 'q'
  - If Cyrillic selected: Q → 'й'
         ↓
Character sent to active IME (Titan2Keyboard)
         ↓
Your IME processes it (autocorrect, suggestions, etc.)
         ↓
Text appears in app
```

## Verification Commands (if you have terminal access)

If you have Termux or ADB access:

```bash
# Check installed packages
pm list packages | grep -E "qwerty|keyboard"

# Check keyboard input devices
cat /proc/bus/input/devices | grep -A 10 -i keyboard

# Check available keyboard layouts
dumpsys input | grep -A 20 "KeyboardLayout"

# List all input devices
ls -la /system/usr/keylayout/
ls -la /system/usr/keychars/
```

## Alternative: Check Which TitanQwerty You Installed

There are different versions:

### From Play Store (fjdrjr version)
- Package: `de.fjdrjr.titanqwerty`
- Most actively maintained
- Supports: Titan, Titan Pocket, KEYone, Priv, Pro1, Gemini

### From GitHub (b1ink301 version)
- May be older fork
- May not include Titan 2 support

**Recommendation**: Uninstall current version and install from Play Store (fjdrjr's version) which is more likely to be updated.

## Expected Issues with Titan 2

### Titan 2 is Newer
The Unihertz Titan 2 was released after the original Titan and Titan Pocket. Many keyboard layout apps were built for the original devices and may not include Titan 2 support.

### Hardware Differences
Titan 2 might use:
- Different keyboard hardware identifier
- Different vendor/product IDs
- Different key layout files

### Solution Path Forward

If titanqwerty doesn't work with Titan 2:

1. **Short term**: Extract the .kcm files from titanqwerty and create Titan 2 versions
2. **Medium term**: Fork titanqwerty and add Titan 2 support
3. **Long term**: Create your own "Titan2KeyboardLayouts" companion app

## Next Steps

Based on your findings, please check:

1. **What keyboard device name shows** in Settings → Physical keyboard?
2. **Which titanqwerty version** did you install (package name)?
3. **Are there ANY additional layouts** showing up, or only the default?
4. **Your Android version** on Titan 2?

With this information, we can determine:
- If titanqwerty needs to be updated for Titan 2
- If you need to create custom layouts
- If there's a configuration issue

## Quick Test: Does Your IME Work?

Separately from titanqwerty, verify your Titan2Keyboard IME works:

1. Go to **Settings → Languages & input → On-screen keyboard**
2. Ensure "Titan2Keyboard" is listed and enabled
3. Open any app with text input (Messages, Notes, etc.)
4. Tap in a text field
5. Your IME should be active (you might see your autocorrect, symbol picker, etc.)
6. Type on physical keyboard
7. Does your accent cycling work? Do your features work?

If YES: Your IME is working fine, titanqwerty just isn't providing layouts yet.
If NO: We need to troubleshoot your IME separately.

---

## Summary

The key distinction:
- **TitanQwerty** = Layout provider (goes in Physical keyboard settings)
- **Titan2Keyboard** = IME (goes in On-screen keyboard settings)
- They work at different layers and don't overlap

If titanqwerty layouts aren't showing, it's likely because:
1. Titan 2 isn't in its supported devices list
2. Wrong fork/version installed
3. Needs restart
4. Compatibility issue

Let's figure out which one and fix it!
