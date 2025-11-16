# TODO - Titan2 Keyboard

## Current Issues

### 1. Symbol Picker Implementation (High Priority)

**Status:** Disabled due to technical limitations

**Problem:**
The symbol picker feature was originally implemented using Jetpack Compose in the InputMethodService's candidates view. However, this approach crashes on Android 15 with the following error:

```
java.lang.IllegalStateException: ViewTreeLifecycleOwner not found from android.widget.LinearLayout
```

**Root Cause:**
- InputMethodService creates its own window hierarchy for the keyboard UI
- The parent LinearLayout created by InputMethodService doesn't have a ViewTreeLifecycleOwner
- Compose requires ViewTreeLifecycleOwner to be set in the view hierarchy
- Even when we manually set it on our ComposeView, Compose still searches the parent hierarchy and fails

**What We Tried:**
1. ✗ Implementing LifecycleOwner and SavedStateRegistryOwner in InputMethodService
2. ✗ Setting ViewTreeLifecycleOwner directly on the ComposeView
3. ✗ Using ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
4. ✗ Managing lifecycle events manually (ON_CREATE, ON_START, ON_RESUME, etc.)

All attempts failed because the IME window hierarchy is created outside of our control.

**Current Workaround:**
The symbol picker is disabled - `onCreateCandidatesView()` returns `null`. The Sym key press is still detected but does nothing.

**Future Implementation Options:**

#### Option 1: Dialog-based Symbol Picker (Recommended)
- Show a Compose Dialog when Sym key is pressed
- Dialogs have their own window and lifecycle, separate from IME
- Can use full Compose with Material3 theming
- Pros: Clean implementation, full Compose support
- Cons: May feel less integrated, takes over screen

#### Option 2: PopupWindow with Compose
- Use PopupWindow anchored to cursor or screen position
- Wrap Compose content in PopupWindow
- Pros: More lightweight than Dialog
- Cons: Still need to manage lifecycle properly, positioning complexity

#### Option 3: Custom View-based UI
- Build symbol picker using traditional Android Views
- No Compose, no lifecycle issues
- Pros: Guaranteed to work in IME context
- Cons: More code, less maintainable, no Material3 benefits

#### Option 4: Input View instead of Candidates View
- Use `onCreateInputView()` instead of `onCreateCandidatesView()`
- Show/hide based on Sym key state
- Pros: Input view has better lifecycle support
- Cons: Conflicts with hardware keyboard paradigm, may interfere with other IME functionality

**Recommended Next Steps:**
1. Implement Dialog-based approach (Option 1)
2. Create a Dialog that appears when Sym is pressed
3. Show symbol categories with navigation
4. Dismiss dialog after symbol selection or back button
5. Test thoroughly on device

**Related Files:**
- `app/src/main/kotlin/com/titan2keyboard/ime/Titan2InputMethodService.kt` - Line 132 (disabled candidates view)
- `app/src/main/kotlin/com/titan2keyboard/ui/symbolpicker/SymbolPickerViewModel.kt` - Ready to use
- `app/src/main/kotlin/com/titan2keyboard/ui/symbolpicker/SymbolPicker.kt` - Compose UI ready
- `app/src/main/kotlin/com/titan2keyboard/data/SymbolRepository.kt` - Symbol data ready

---

## Future Enhancements

### 2. Configuration Cache Warning
**Status:** Low priority, build works but shows warning

**Issue:**
Build shows configuration cache warning for the `incrementBuildNumber` task:
```
cannot serialize Gradle script object references
```

**Impact:** None - builds complete successfully, just a warning

**Fix:** Refactor the auto-increment task to avoid using script object references. Consider using a custom task class instead of inline task registration.

---

## Completed Features

### ✓ Release APK Signing
- Keystore configuration working
- Signing config properly references keystore.properties
- Release builds sign correctly

### ✓ Custom APK Naming
- Debug builds: `titan2keyboard-debug.apk`
- Release builds: `titan2keyboard-release.apk`

### ✓ Auto-incrementing Build Numbers
- Build number stored in `version.properties`
- Increments automatically on each build
- Shown in About screen

### ✓ About Screen
- Shows app name, version, build number
- Shows build type (debug/release)
- Shows package name
- Shows license information

### ✓ Auto-capitalization
- Works after sentence-ending punctuation + space (. ! ?)
- Works at start of text and after newline
- Works after double-space period insertion
- Removed restrictive input field cap flag checking

### ✓ Modifier Key Features
- Sticky Shift: Single tap = one-shot, long press = locked
- Sticky Alt: Single tap = one-shot, long press = locked
- Double-tap lock: Quick double-tap also locks modifiers
- Visual feedback: Status bar notification shows active modifiers with lock icon
- Documented in settings UI

### ✓ Text Shortcuts
- Auto-correct common contractions (Im → I'm)
- Custom shortcuts management UI
- Backspace undo for recent replacements
- Case-sensitive option per shortcut

### ✓ Alt+Backspace
- Optional: Delete entire line before cursor
- Configurable in settings
- Respects modifier lock state

### ✓ Capacitive Touch Blocking
- Blocks accidental trackpad/scroll gestures while typing
- 1-second grace period after key press
- Blocks all capacitive input while in any text field

---

## Known Limitations

1. **Symbol Picker:** Currently disabled (see issue #1 above)
2. **Sym Key:** Detected but non-functional until symbol picker is reimplemented
3. **Configuration Cache:** Warning on build (cosmetic, no functional impact)

---

**Last Updated:** 2025-11-16
**Build Version:** 0.1.0 (Build #7)
