# Proof of Concept: TitanQwerty with Titan 2 Support

## What We Accomplished

✅ **Identified the root cause** of titanqwerty not working on Titan 2
✅ **Found Titan 2 Sym key code**: 63 (different from Titan original: 250, Titan Pocket: 127)
✅ **Created modified titanqwerty** with Titan 2 support
✅ **Added Russian Cyrillic layout** as proof of concept
✅ **Documented** complete build and testing process

## Modified Repository

**Location**: `/home/user/titanqwerty-fork/`

**Changes**:
1. Created `titan2_cyr_1.kcm` with Sym key = 63
2. Added string label for display name
3. Registered layout in XML configuration

## Next Steps for You

### Step 1: Transfer Modified Repository

Copy the modified repository to your build machine:

```bash
# Option A: Compress and transfer
cd /home/user
tar -czf titanqwerty-titan2.tar.gz titanqwerty-fork/
# Then copy titanqwerty-titan2.tar.gz to your build machine

# Option B: Push to Git (if you have repo access)
cd /home/user/titanqwerty-fork
git remote add myfork https://github.com/YOUR_USERNAME/titanqwerty.git
git push myfork main
```

### Step 2: Build the APK

On your machine with Android development tools:

```bash
cd titanqwerty-fork
./gradlew assembleDebug
```

APK will be at: `finqwerty/build/outputs/apk/debug/finqwerty-debug.apk`

### Step 3: Install on Titan 2

```bash
# Uninstall original titanqwerty first
adb uninstall de.fjdrjr.titanqwerty

# Install modified version
adb install finqwerty/build/outputs/apk/debug/finqwerty-debug.apk

# Restart device
adb reboot
```

### Step 4: Test the Layout

1. Go to **Settings → Physical keyboard → TitanKey**
2. Look for **"TitanQwerty Unihertz Titan 2, Cyrillic Phonetic"**
3. Select it
4. Open text app and type
5. **Expected**: Cyrillic characters should appear!

## If It Works 🎉

You've proven the concept! Next steps:

### Add More Layouts

Use the same pattern to add all languages:

```bash
# For each layout you want (German, Greek, Ukrainian, etc.):

# 1. Copy file
cp finqwerty/src/main/res/raw/titan_xxx_1.kcm finqwerty/src/main/res/raw/titan2_xxx_1.kcm

# 2. Change Sym key
sed -i 's/map key 250 SYM/map key 63 SYM/' finqwerty/src/main/res/raw/titan2_xxx_1.kcm

# 3. Add string in strings.xml
<string name="titan2_xxx_1" translatable="false">TitanQwerty Unihertz Titan 2, [Language Name]</string>

# 4. Add layout entry in finqwerty_layouts.xml
<keyboard-layout android:name="titan2_xxx_1" android:label="@string/titan2_xxx_1" android:keyboardLayout="@raw/titan2_xxx_1"/>

# 5. Rebuild
./gradlew assembleDebug
```

### Contribute Back to Community

Option 1: **Pull Request to titanqwerty**
- Fork fjdrjr/titanqwerty on GitHub
- Apply your changes
- Submit PR with Titan 2 support
- Help all Titan 2 users!

Option 2: **Publish Your Own Fork**
- Maintain as "TitanQwerty for Titan 2"
- Publish to Play Store or F-Droid
- Share with Unihertz community

Option 3: **Share with Your Titan2Keyboard**
- Package as companion app
- Bundle with your IME
- One-stop solution for Titan 2 users

## If It Doesn't Work ❌

### Troubleshooting Steps

1. **Verify Sym key code again**:
   ```bash
   adb shell getevent -l
   # Press Sym key - is it really 63?
   ```

2. **Check logcat for errors**:
   ```bash
   adb logcat | grep -iE "keyboard|layout|kcm"
   ```

3. **Try simpler layout first**:
   - Create titan2_us_1.kcm (US QWERTY)
   - Easier to debug than Cyrillic

4. **Verify file syntax**:
   - Check titan2_cyr_1.kcm is properly formatted
   - Ensure line 3 is exactly: `map key 63 SYM`

## Integration with Titan2Keyboard IME

Once titanqwerty Titan 2 layouts work, your IME can enhance them:

### Detect Active System Layout

```kotlin
// In your Titan2InputMethodService
private fun detectSystemKeyboardLayout(): String {
    val imm = getSystemService(InputManager::class.java)
    // Detect if user has selected Cyrillic, Greek, etc.
    // Adjust your IME features accordingly
}
```

### Layout-Aware Features

```kotlin
when (activeLayoutLanguage) {
    "ru" -> {
        // Load Cyrillic symbol picker
        // Enable Russian autocorrect
        // Show Cyrillic-specific shortcuts
    }
    "el" -> {
        // Greek symbols and features
    }
    // etc.
}
```

### Best of Both Worlds

```
TitanQwerty (system layouts)     Titan2Keyboard (IME features)
├─ Russian character mapping  +  ├─ Russian autocorrect
├─ Greek character mapping    +  ├─ Greek symbols
├─ Ukrainian character mapping+  ├─ Smart suggestions
└─ Multi-language support     +  └─ Custom shortcuts
                              =
        Complete Titan 2 multilingual solution!
```

## Files for Reference

- **BUILD_TITANQWERTY_TITAN2.md** - Complete build instructions
- **TITANQWERTY_TITAN2_MOD.md** - Summary of changes
- **FINDING_TITAN2_SYM_KEY.md** - How Sym key was discovered
- **TESTING_TITANQWERTY.md** - Original coexistence testing plan
- **TROUBLESHOOTING_TITANQWERTY.md** - Debugging guide

## Modified Repository Contents

```
/home/user/titanqwerty-fork/
├── finqwerty/src/main/res/
│   ├── raw/
│   │   └── titan2_cyr_1.kcm              ← NEW
│   ├── values/
│   │   └── strings.xml                    ← MODIFIED
│   └── xml/
│       └── finqwerty_layouts.xml          ← MODIFIED
└── TITAN2_SUPPORT.md                      ← NEW
```

## Success Metrics

This proof of concept is successful if:

1. ✅ Modified titanqwerty APK builds without errors
2. ✅ APK installs on Titan 2
3. ✅ "Titan 2 Cyrillic" layout appears in Settings
4. ✅ Selecting layout produces Cyrillic characters
5. ✅ Sym key functions correctly

## Timeline

**Now**: Build and test Russian Cyrillic proof of concept
**If successful**: Add remaining layouts (German, Greek, Ukrainian, etc.)
**Next**: Integrate with Titan2Keyboard IME for layout-aware features
**Future**: Contribute to titanqwerty project or maintain fork

---

## Ready to Test!

The modified repository is prepared and ready for you to build. Follow the instructions in `BUILD_TITANQWERTY_TITAN2.md` and let me know the results!

**Good luck! 🚀**
