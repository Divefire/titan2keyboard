# Building Modified TitanQwerty with Titan 2 Support

## Summary of Changes

We've successfully modified titanqwerty to support Titan 2 (Unihertz) with the Russian Cyrillic layout as a proof of concept.

### What Was Changed

1. **Created**: `titan2_cyr_1.kcm` - Russian Cyrillic layout for Titan 2
   - Based on `titan_cyr_1.kcm` (Titan original)
   - **Key modification**: Changed Sym key from 250 to 63 (Titan 2 hardware code)

2. **Updated**: `strings.xml` - Added display label
   - Added: `titan2_cyr_1` → "TitanQwerty Unihertz Titan 2, Cyrillic Phonetic"

3. **Updated**: `finqwerty_layouts.xml` - Registered the layout
   - Added layout entry for Android to recognize

## Modified Repository Location

The modified titanqwerty repository is located at:
```
/home/user/titanqwerty-fork/
```

## How to Build the APK

### Option A: Build on Your Local Machine (Recommended)

1. **Copy the modified repository to your machine**:
   ```bash
   # From this environment, compress the repo
   cd /home/user
   tar -czf titanqwerty-titan2-mod.tar.gz titanqwerty-fork/

   # Then transfer to your machine and extract
   ```

2. **Prerequisites on your machine**:
   - Java JDK 17 or later
   - Android SDK (or Android Studio)
   - Internet connection (for Gradle dependencies)

3. **Build the APK**:
   ```bash
   cd titanqwerty-fork

   # Clean build
   ./gradlew clean

   # Build debug APK
   ./gradlew assembleDebug

   # Or build release APK (unsigned)
   ./gradlew assembleRelease
   ```

4. **Find the APK**:
   ```bash
   # Debug APK:
   finqwerty/build/outputs/apk/debug/finqwerty-debug.apk

   # Release APK:
   finqwerty/build/outputs/apk/release/finqwerty-release-unsigned.apk
   ```

### Option B: Build Using Android Studio

1. **Open Android Studio**
2. **File → Open** → Select `/path/to/titanqwerty-fork`
3. **Wait for Gradle sync** to complete
4. **Build → Build Bundle(s) / APK(s) → Build APK(s)**
5. APK will be in `finqwerty/build/outputs/apk/debug/`

### Option C: Use GitHub Actions (If You Fork to GitHub)

1. Fork the modified repository to your GitHub account
2. Enable GitHub Actions in your fork
3. Actions will automatically build APKs on push
4. Download artifacts from the Actions tab

## Installation and Testing

### Step 1: Uninstall Existing TitanQwerty

```bash
# Via ADB
adb uninstall de.fjdrjr.titanqwerty

# Or on device:
# Settings → Apps → TitanQwerty → Uninstall
```

### Step 2: Install Modified Version

```bash
# Via ADB
adb install finqwerty/build/outputs/apk/debug/finqwerty-debug.apk

# Or copy APK to device and install manually
```

### Step 3: Restart Device

```bash
adb reboot

# Or restart manually
```

This ensures Android recognizes the new keyboard layouts.

### Step 4: Verify Layout Appears

1. **Settings → System → Languages & input → Physical keyboard**
2. Tap on **TitanKey** (your Titan 2 keyboard)
3. **Look for**: "TitanQwerty Unihertz Titan 2, Cyrillic Phonetic"
4. **Select it**

### Step 5: Test Typing

1. Open any app with text input (Messages, Notes, etc.)
2. Type on physical keyboard
3. **Expected behavior**:
   - Q key → 'й' (Cyrillic 'ya')
   - W key → 'в' (Cyrillic 've')
   - E key → 'е' (Cyrillic 'ye')
   - Etc. (full Russian keyboard mapping)

## Expected Results

### Success Criteria

✅ **Layout shows up** in Settings → Physical keyboard → TitanKey
✅ **Can select** "TitanQwerty Unihertz Titan 2, Cyrillic Phonetic"
✅ **Typing produces** Cyrillic characters correctly
✅ **Sym key works** (should trigger symbol/alt characters)

### If Layout Doesn't Appear

**Possible causes**:
1. **Didn't restart** - Restart required for Android to recognize new layouts
2. **Installation failed** - Check `adb logcat` for errors
3. **Wrong Sym key code** - Verify 63 is correct (use `getevent` to confirm)
4. **Permissions issue** - Ensure app has necessary permissions

## Next Steps After Successful Test

### If It Works 🎉

This proves the concept! You can then:

1. **Add more Titan 2 layouts**:
   - Copy other titan_*.kcm files → titan2_*.kcm
   - Change Sym key to 63 in each
   - Add strings and layout entries
   - Rebuild

2. **Add all languages**:
   - German (QWERTY and QWERTZ)
   - Greek
   - Ukrainian
   - Czech
   - Vietnamese
   - Korean
   - US QWERTY
   - Etc.

3. **Contribute back**:
   - Create pull request to titanqwerty project
   - Add Titan 2 support for the community
   - Or maintain your own fork

4. **Package for distribution**:
   - Sign the APK properly
   - Publish to Play Store or F-Droid
   - Share with Titan 2 community

### If It Doesn't Work ❌

**Troubleshooting steps**:

1. **Verify Sym key code**:
   ```bash
   adb shell getevent -l
   # Press Sym key and check the scan code
   ```

2. **Check logcat for errors**:
   ```bash
   adb logcat | grep -E "keyboard|layout|Input"
   ```

3. **Verify file format**:
   - Open titan2_cyr_1.kcm in text editor
   - Check line 3: `map key 63 SYM`
   - Verify syntax is correct

4. **Test with simpler layout**:
   - Try creating titan2_us_1.kcm (basic US QWERTY)
   - Easier to debug than Cyrillic

## Files Modified Summary

```
titanqwerty-fork/
├── finqwerty/src/main/res/
│   ├── raw/
│   │   └── titan2_cyr_1.kcm          ← NEW (Sym key 63)
│   ├── values/
│   │   └── strings.xml                ← MODIFIED (added titan2_cyr_1 string)
│   └── xml/
│       └── finqwerty_layouts.xml      ← MODIFIED (added titan2_cyr_1 entry)
```

## Diff of Changes

### titan2_cyr_1.kcm (line 3)
```diff
- map key 250 SYM
+ map key 63 SYM
```

### strings.xml (after line 44)
```xml
+ <string name="titan2_cyr_1" translatable="false">TitanQwerty Unihertz Titan 2, Cyrillic Phonetic</string>
```

### finqwerty_layouts.xml (after line 46)
```xml
+ <keyboard-layout android:name="titan2_cyr_1" android:label="@string/titan2_cyr_1" android:keyboardLayout="@raw/titan2_cyr_1"/>
```

## Build Troubleshooting

### Gradle Build Fails

```bash
# Clear Gradle cache
./gradlew clean
rm -rf ~/.gradle/caches/

# Try build again
./gradlew assembleDebug --stacktrace
```

### Java Version Issues

```bash
# Check Java version (needs 17+)
java -version

# Set JAVA_HOME if needed
export JAVA_HOME=/path/to/jdk-17
```

### Android SDK Not Found

```bash
# Set ANDROID_HOME
export ANDROID_HOME=/path/to/Android/Sdk

# Or use Android Studio which sets this automatically
```

## Alternative: Provide Build as Service

If you can't build locally, you could:

1. **Create GitHub fork** of the modified repo
2. **Share the fork URL** with me
3. I can provide CI/CD setup for automated builds
4. Or community members can build and share

## Questions?

If you encounter issues:
1. Check `adb logcat` output
2. Verify all file changes are correct
3. Ensure proper restart after installation
4. Try with US QWERTY layout first (simpler to debug)

---

**Ready to build and test!** Let me know how it goes!
