# Testing TitanQwerty Coexistence with Titan2Keyboard

## Objective
Verify that titanqwerty (keyboard layout provider) and Titan2Keyboard (IME) work together seamlessly.

## Installation Steps

### 1. Install TitanQwerty

**Option A: Google Play Store**
- Open Play Store
- Search for "TitanQwerty Keyboard Layouts"
- Install the app
- Link: https://play.google.com/store/apps/details?id=de.fjdrjr.titanqwerty

**Option B: GitHub Release (APK)**
- Visit: https://github.com/fjdrjr/titanqwerty/releases
- Download latest APK
- Install via ADB: `adb install titanqwerty.apk`

### 2. Verify Both Apps Are Installed

```bash
# Check if both apps are installed
adb shell pm list packages | grep -E "titanqwerty|titan2keyboard"

# Expected output:
# package:de.fjdrjr.titanqwerty         ← Layout provider
# package:com.titan2keyboard             ← Your IME
```

### 3. Configure Keyboard Layout

**On Device:**
1. Open **Settings** → **System** → **Languages & input**
2. Tap **Physical keyboard**
3. Select your keyboard device (e.g., "agui-titan-key" or "Titan-key")
4. You should now see layouts from titanqwerty:
   - German (QWERTZ)
   - Russian (Cyrillic)
   - Czech
   - Vietnamese
   - US (QWERTY)
   - etc.
5. Select a non-Latin layout (e.g., "Russian (Cyrillic)")

### 4. Verify IME Configuration

1. Go to **Settings** → **System** → **Languages & input** → **On-screen keyboard**
2. Ensure **Titan2Keyboard** is enabled and selected
3. This confirms your IME is active

### 5. Test Coexistence

Open any text field (Notes, Messages, etc.) and test:

#### Test 1: Russian Layout with Your IME
- **System Layout**: Russian (Cyrillic) [from titanqwerty]
- **IME**: Titan2Keyboard
- **Expected**:
  - Q key types 'й' (Russian character from layout)
  - Your IME features still work (autocorrect, symbols, etc.)
  - No conflicts

#### Test 2: Switch Back to QWERTY
- Change system layout back to QWERTY
- **Expected**:
  - Q key types 'q' (Latin)
  - Your accent cycling still works
  - All your IME features intact

#### Test 3: Symbol Picker
- With Russian layout active
- Open your symbol picker (Sym key)
- **Expected**:
  - Symbol picker shows
  - Both systems coexist peacefully

## Verification Checklist

- [ ] TitanQwerty installed successfully
- [ ] Titan2Keyboard enabled as IME
- [ ] Physical keyboard layouts visible in Settings
- [ ] Can select Russian/Cyrillic layout
- [ ] Typing produces correct Cyrillic characters
- [ ] Can switch between layouts
- [ ] IME features work with non-Latin layouts
- [ ] No crashes or conflicts
- [ ] Both apps running simultaneously without issues

## ADB Commands for Testing

```bash
# Check which IME is currently active
adb shell settings get secure default_input_method

# Expected: com.titan2keyboard/.ime.Titan2InputMethodService

# Check available keyboard layouts
adb shell dumpsys input | grep -A 20 "KeyboardLayout"

# Check for any errors
adb logcat | grep -E "titan2keyboard|titanqwerty"
```

## Expected Behaviors

### When titanqwerty Layout is Active:
1. **System handles key mapping** (physical key → character)
2. **Your IME receives** already-mapped characters
3. **Your IME provides** additional features:
   - Autocorrect (if implemented for that language)
   - Symbol picker
   - Clipboard
   - Shortcuts
   - Text suggestions

### When Using QWERTY (No titanqwerty layout):
1. **System uses** default QWERTY mapping
2. **Your IME handles** accent cycling for Latin languages
3. **Everything works** as it does currently

## Potential Issues to Watch For

### Issue: Layouts Not Showing
**Symptom**: Can't see titanqwerty layouts in Settings
**Cause**: App not properly registering layouts
**Solution**:
- Restart device
- Check app permissions
- Reinstall titanqwerty

### Issue: Characters Typed Twice
**Symptom**: Each key press produces two characters
**Cause**: Both system and IME handling the same key
**Solution**:
- Your IME should return `KeyEventResult.NotHandled` for non-Latin layouts
- Let system handle the character mapping

### Issue: IME Features Not Working
**Symptom**: Symbol picker or other features broken with non-Latin layout
**Solution**:
- Detect active layout in your IME
- Adjust features accordingly
- Some features may need layout-specific logic

## Next Steps After Testing

If coexistence works well:

1. **Document the integration**
   - Add to README: "Works with TitanQwerty for non-Latin layouts"
   - Link to titanqwerty in your app

2. **Enhance IME for layout detection**
   - Detect when Cyrillic layout is active
   - Provide Cyrillic-specific symbols
   - Disable Latin accent cycling for non-Latin layouts

3. **Consider creating your own layout provider**
   - Fork titanqwerty
   - Add Titan 2 specific layouts
   - Provide additional languages
   - Maintain as companion app

## Testing Log

Document your findings here:

**Date**: _____________

**TitanQwerty Version**: _____________

**Titan2Keyboard Version**: _____________

### Layout Tests
- [ ] Russian (Cyrillic): Works / Issues: _____________
- [ ] German (QWERTZ): Works / Issues: _____________
- [ ] Czech: Works / Issues: _____________
- [ ] Vietnamese: Works / Issues: _____________
- [ ] QWERTY: Works / Issues: _____________

### IME Feature Tests
- [ ] Symbol picker: Works / Issues: _____________
- [ ] Autocorrect: Works / Issues: _____________
- [ ] Shortcuts: Works / Issues: _____________
- [ ] Accent cycling: Works / Issues: _____________
- [ ] Modifier keys: Works / Issues: _____________

### Performance
- [ ] No lag when typing
- [ ] No crashes
- [ ] Battery usage normal
- [ ] Memory usage acceptable

### Notes:
_____________________________________________
_____________________________________________
_____________________________________________

---

**Conclusion**: [Works perfectly / Has issues / Needs improvements]

**Recommendation**: [Use as-is / Enhance IME / Create own layout app / Other]
