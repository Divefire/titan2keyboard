# Investigating TitanQwerty Installation on Titan 2

## Current State (Based on User Testing)

### Device Information
- **Device**: Unihertz Titan 2
- **Android Version**: Android 15
- **Physical Keyboard Device**: "TitanKey"

### Installed Apps
1. **Titan2Keyboard** (our IME) - Enabled ✓
2. **TitanQwerty** - Installed from GitHub APK (b1ink301)
3. **Pastiera Physical Keyboard Input** - Physical keyboard IME
4. **Gboard** - Google IME
5. **Kika-Keyboard** - IME
6. **Microsoft SwiftKey** - IME

### Physical Keyboard Settings Observation

**Path**: Settings → System → Languages & input → Physical keyboard → TitanKey

Each IME shows:
```
TitanKey
├─ Gboard layout
│   ├─ English UK
│   └─ User Selected: Titan      ← Only ONE layout showing
├─ Kika-Keyboard layout
│   ├─ English (UK)
│   └─ User Selected: Titan      ← Only ONE layout showing
├─ Microsoft SwiftKey Keyboard layout
│   ├─ Microsoft SwiftKey Keyboard
│   └─ User Selected: Titan      ← Only ONE layout showing
└─ Pastiera Physical Keyboard Input
    ├─ English
    └─ User Selected: Titan      ← Only ONE layout showing
```

When clicking on any, shows:
- Layout name: "Titan" (QWERTY visual)
- Section: "Physical Keyboard layouts"
- Only "Titan" listed (no Russian, Greek, Czech, Vietnamese, etc.)

## Analysis

### What This Means

1. **TitanQwerty DID register a layout** ("Titan" is showing up)
2. **But only ONE layout** instead of multiple language options
3. **This suggests**: Partial compatibility with Titan 2

### Possible Causes

#### Cause A: Limited Titan 2 Support
The GitHub APK (b1ink301 fork) may only have basic Titan 2 support:
- Recognized TitanKey hardware ✓
- Registered a default "Titan" layout ✓
- But didn't include language variants ✗

#### Cause B: Wrong APK/Fork
The b1ink301 fork might be older or have limited layouts. Different forks may have different features:
- **b1ink301/titanqwerty**: Your installed version (limited?)
- **fjdrjr/titanqwerty**: Main maintained fork (possibly more layouts?)
- **bluedskim/titanqwerty**: Original FinQwerty fork

#### Cause C: Layouts Need Manual Activation
Some keyboard layout apps require:
- Opening the app itself to enable layouts
- Granting permissions
- Configuring which layouts to expose

#### Cause D: Android 15 Compatibility Issue
Titan 2 runs Android 15 (very new). TitanQwerty might:
- Have partial Android 15 support
- Need updates for Android 15 compatibility
- Have registration issues on newer Android versions

## Investigation Steps

### Step 1: Open TitanQwerty App Directly

**Action**: Find and open the TitanQwerty app from your app drawer

**Look for**:
- Settings to enable specific layouts
- List of available languages (Russian, Greek, Czech, etc.)
- Checkboxes or toggles to activate layouts
- Device selection (Titan vs Titan Pocket vs Titan 2)
- "Enable layouts" or similar option

**Expected**: App should show available layouts with options to enable/disable each

### Step 2: Check TitanQwerty Permissions

**Path**: Settings → Apps → TitanQwerty → Permissions

**Verify**:
- App has necessary permissions
- No permission errors
- Try granting any requested permissions

### Step 3: Check Which APK You Installed

**Find the APK source**:
- Which GitHub release did you download?
- What was the filename?
- What's the version number?

**Check package details**:
**Path**: Settings → Apps → TitanQwerty → Advanced → App details

**Note**:
- Package name (should be `de.fjdrjr.titanqwerty` or similar)
- Version number
- Install source

### Step 4: Compare with Expected Behavior

**Expected from TitanQwerty**: When properly installed, you should see:

```
Settings → Physical keyboard → TitanKey → [Select layout]

Physical Keyboard layouts:
├─ English (US)
├─ German (QWERTZ)          ← From TitanQwerty
├─ Russian (Cyrillic)       ← From TitanQwerty
├─ Czech                    ← From TitanQwerty
├─ Vietnamese               ← From TitanQwerty
└─ Titan (QWERTY)           ← What you're seeing (possibly default)
```

**What you're seeing**: Only "Titan" layout

### Step 5: Check Pastiera Physical Keyboard Input

**Interesting discovery**: You have another physical keyboard app installed!

**Investigate Pastiera**:
1. What is this app? (Check in app drawer or Settings → Apps)
2. Does it offer multiple layouts?
3. Could this be conflicting with TitanQwerty?
4. Is it worth trying instead of TitanQwerty?

**Action**: Open Pastiera app (if it has a UI) and see if it offers layout options

## Diagnostic Commands

If you have terminal access (Termux):

```bash
# Check TitanQwerty package info
pm list packages -f | grep -i qwerty

# Check what's installed
pm list packages | grep -E "titan|finqwerty|pastiera"

# Dump input configuration
dumpsys input | grep -A 30 "KeyboardLayout"

# Check keyboard layout files installed by app
ls -la /data/data/de.fjdrjr.titanqwerty/ 2>/dev/null
ls -la /data/data/*/files/*.kcm 2>/dev/null
```

## Possible Solutions

### Solution A: Reinstall from Different Source

If Play Store link was broken, try:
1. **F-Droid** (if TitanQwerty is available there)
2. **GitHub releases from fjdrjr** (not b1ink301):
   - https://github.com/fjdrjr/titanqwerty/releases
   - Download latest APK
   - Uninstall current version first
   - Install new version
   - Restart device

### Solution B: Try Pastiera Instead

Since you already have Pastiera installed:
1. Investigate what Pastiera offers
2. Check if it has multiple language layouts
3. Might be a better solution than TitanQwerty

### Solution C: Contact Developer

File a GitHub issue:
- **Where**: https://github.com/b1ink301/titanqwerty/issues (if using b1ink301 fork)
- **Or**: https://github.com/fjdrjr/titanqwerty/issues (main fork)
- **Title**: "Titan 2 support: Only showing one layout"
- **Include**:
  - Device: Unihertz Titan 2
  - Android: 15
  - Keyboard hardware: TitanKey
  - Issue: Only "Titan" layout showing, no language variants
  - Request: Add full Titan 2 support with all layouts

### Solution D: Create Our Own Layout App

If TitanQwerty doesn't work well with Titan 2:

**Create "Titan2KeyboardLayouts" companion app**:
1. Extract .kcm files from TitanQwerty source
2. Create proper Titan 2 device support
3. Register all layouts properly
4. Ensure Android 15 compatibility

This gives you full control and guaranteed Titan 2 support.

## Next Steps

Please investigate:

1. **Open TitanQwerty app** - Does it have settings? Can you enable layouts?
2. **Check TitanQwerty version** - Settings → Apps → TitanQwerty → App details
3. **Investigate Pastiera** - What is this app? Does it offer multiple layouts?
4. **Try different APK** - Uninstall b1ink301 version, try fjdrjr version

Report back with:
- Screenshots of TitanQwerty app (if it has a UI)
- Package name and version
- What Pastiera app is/does
- Any settings or options you find

Then we can decide:
- Fix TitanQwerty for Titan 2
- Use Pastiera instead
- Create our own layout companion app

## Key Question

**The "Titan" layout that's showing** - when you select it and type:
- Does it output standard QWERTY characters?
- Any special features?
- Different from not having TitanQwerty installed?

This tells us if TitanQwerty is actually doing anything or if "Titan" is just a default system layout.
