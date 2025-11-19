# Extracting Keyboard Configuration Files from Unihertz Titan 2

## Overview

This guide explains how to extract the original keyboard configuration files from your Unihertz Titan 2 device. These files serve as the foundation for creating custom keyboard layouts like AZERTY, QWERTZ, etc.

## Prerequisites

### Required Tools
- **ADB (Android Debug Bridge)** installed on your computer
- **USB cable** to connect Titan 2 to computer
- **USB debugging enabled** on Titan 2

### Enable USB Debugging on Titan 2

1. Go to **Settings** → **About phone**
2. Tap **Build number** 7 times to enable Developer options
3. Go back to **Settings** → **System** → **Developer options**
4. Enable **USB debugging**
5. Connect device via USB and authorize the computer when prompted

### Verify ADB Connection

```bash
# Check if device is connected
adb devices

# Expected output:
# List of devices attached
# ABC123456789    device
```

---

## Step 1: Locate Keyboard Files on Device

### List Keyboard Layout Files (.kl)

```bash
adb shell ls -la /system/usr/keylayout/
```

**Expected files:**
- `Generic.kl` - Generic keyboard layout
- `Vendor_XXXX_Product_XXXX.kl` - Device-specific layouts
- Possibly: `keyboard_layout_agui_titan-key.kl` or similar

### List Key Character Map Files (.kcm)

```bash
adb shell ls -la /system/usr/keychars/
```

**Expected files:**
- `Generic.kcm` - Generic character map
- `Virtual.kcm` - Virtual keyboard character map
- Possibly: `keyboard_layout_agui_titan-key.kcm` (target file!)

### List Input Device Configuration Files (.idc)

```bash
adb shell ls -la /system/usr/idc/
```

**Expected files:**
- Device-specific input configuration files

### Alternative Locations (Android 10+)

Some files may be in vendor or product partitions:

```bash
# Check vendor partition
adb shell ls -la /vendor/usr/keylayout/
adb shell ls -la /vendor/usr/keychars/

# Check product partition
adb shell ls -la /product/usr/keylayout/
adb shell ls -la /product/usr/keychars/

# Check overlay partition
adb shell ls -la /odm/usr/keylayout/
adb shell ls -la /odm/usr/keychars/
```

---

## Step 2: Find Titan 2 Keyboard Device Name

### Method 1: List All Input Devices

```bash
adb shell getevent -p
```

Look for entries related to the physical keyboard. You might see:
- Device name containing "agui", "titan", "keyboard", or similar
- Multiple event devices (event0, event1, etc.)

**Example output:**
```
add device 3: /dev/input/event3
  name:     "agui-titan-key"
  ...
```

### Method 2: Check Input Device Information

```bash
adb shell cat /proc/bus/input/devices
```

Look for keyboard-related entries with:
- `Name=` field (might contain "agui", "titan-key", etc.)
- `Handlers=` field showing event device

**Example output:**
```
I: Bus=0019 Vendor=0001 Product=0001 Version=0100
N: Name="agui-titan-key"
P: Phys=gpio-keys/input0
S: Sysfs=/devices/platform/gpio-keys/input/input3
H: Handlers=kbd event3
```

### Method 3: Find Device by Vendor/Product ID

```bash
# Find all keyboard-related files
adb shell find /system /vendor /product -name "*key*" -o -name "*keyboard*" 2>/dev/null | grep -E "\.(kl|kcm)$"
```

---

## Step 3: Extract the Files

### Create Local Directory

```bash
# On your computer
mkdir -p titan2_keyboard_files
cd titan2_keyboard_files
```

### Pull All Keylayout Files

```bash
# Generic files (baseline)
adb pull /system/usr/keylayout/Generic.kl ./

# Try common Titan 2 names
adb pull /system/usr/keylayout/agui-titan-key.kl ./ 2>/dev/null
adb pull /system/usr/keylayout/keyboard_layout_agui_titan-key.kl ./ 2>/dev/null
adb pull /system/usr/keylayout/titan_keyboard.kl ./ 2>/dev/null

# Pull all .kl files (brute force approach)
adb shell "ls /system/usr/keylayout/*.kl" | tr -d '\r' | while read file; do
    adb pull "$file" ./
done
```

### Pull All Keychar Files

```bash
# Generic files (baseline)
adb pull /system/usr/keychars/Generic.kcm ./
adb pull /system/usr/keychars/Virtual.kcm ./

# Try common Titan 2 names
adb pull /system/usr/keychars/agui-titan-key.kcm ./ 2>/dev/null
adb pull /system/usr/keychars/keyboard_layout_agui_titan-key.kcm ./ 2>/dev/null
adb pull /system/usr/keychars/titan_keyboard.kcm ./ 2>/dev/null

# Pull all .kcm files (brute force approach)
adb shell "ls /system/usr/keychars/*.kcm" | tr -d '\r' | while read file; do
    adb pull "$file" ./
done
```

### Pull Input Device Configuration Files

```bash
# Pull all .idc files
adb shell "ls /system/usr/idc/*.idc" 2>/dev/null | tr -d '\r' | while read file; do
    adb pull "$file" ./
done
```

### Check Vendor Partitions

```bash
# Vendor keylayout
adb shell "ls /vendor/usr/keylayout/*.kl" 2>/dev/null | tr -d '\r' | while read file; do
    adb pull "$file" ./vendor_
done

# Vendor keychars
adb shell "ls /vendor/usr/keychars/*.kcm" 2>/dev/null | tr -d '\r' | while read file; do
    adb pull "$file" ./vendor_
done
```

---

## Step 4: Identify the Correct File

Once you've pulled all the files:

### Check File Sizes

```bash
ls -lh *.kl *.kcm *.idc
```

**Indicators:**
- **Generic.kcm** is usually small (basic ASCII only)
- **Device-specific .kcm** will be larger (includes accents, symbols)
- Empty or very small files are likely not the ones you want

### Read File Contents

```bash
# View first 50 lines of each kcm file
for file in *.kcm; do
    echo "=== $file ==="
    head -50 "$file"
    echo ""
done
```

**Look for:**
- Copyright notices mentioning "Unihertz" or "agui"
- `type OVERLAY` or `type FULL`
- Key mappings for physical keyboard (A-Z keys)
- Special characters and symbols

### Check for Titan-Specific Keys

The correct `.kcm` file should have mappings for all Titan 2 keys:
- Letter keys A-Z
- Number keys 0-9 (with Alt modifier)
- Sym key mappings
- Special symbols (@ # $ % etc.)

---

## Step 5: Alternative Method - Extract from Running System

If you can't find the files in filesystem, extract from running device:

### Get Active Input Device

```bash
# While typing on Titan 2 keyboard, run:
adb shell getevent -l

# Press keys and note which event device responds
# Example: /dev/input/event3
```

### Find Associated Configuration

```bash
# Check device properties
adb shell dumpsys input

# Look for KeyCharacterMap and KeyboardLayoutOverlay sections
```

### Use InputManagerService Dump

```bash
# Get detailed input configuration
adb shell dumpsys input > input_dump.txt

# Search for keyboard layout info
grep -A 50 "KeyCharacterMap" input_dump.txt
grep -A 50 "KeyboardLayout" input_dump.txt
```

---

## Step 6: Verify Extracted Files

### Check File Format

Open the `.kcm` file in a text editor and verify:

```bash
# Should start with type declaration
type OVERLAY
# or
type FULL

# Should have key definitions like:
key A {
    label: 'A'
    base: 'a'
    shift, capslock: 'A'
}
```

### Test Key Mappings

Compare the file contents with what you actually type on Titan 2:
- Press **Alt + Q** on device → Check what `key Q` says for `ralt:` or `alt:`
- Press **Sym + A** on device → Check symbol mappings

---

## Common File Naming Patterns

Based on Unihertz engineer's reference to `keyboard_layout_agui_titan-key.kcm`, look for files matching:

```
keyboard_layout_agui_titan-key.kcm
agui-titan-key.kcm
agui_titan_key.kcm
titan-key.kcm
titan_keyboard.kcm
unihertz_titan.kcm
```

The device name "agui" likely refers to:
- Internal Android GUI component
- Manufacturer device ID
- Hardware keyboard identifier

---

## Troubleshooting

### Permission Denied

If you get "Permission denied" errors:

```bash
# Check if you need root
adb shell su -c "ls /system/usr/keychars/"

# Or try shell with root
adb root
adb remount
adb pull /system/usr/keychars/desired_file.kcm ./
```

**Note:** Unihertz Titan 2 with Android 15 may not require root for reading `/system` files, but some partitions might be restricted.

### Files Not Found

If files don't exist in expected locations:

```bash
# Search entire filesystem (slow)
adb shell find / -name "*.kcm" 2>/dev/null
adb shell find / -name "*.kl" 2>/dev/null

# Search for "agui" or "titan"
adb shell find / -name "*agui*" 2>/dev/null
adb shell find / -name "*titan*" 2>/dev/null
```

### Device Not Responding

```bash
# Kill and restart ADB server
adb kill-server
adb start-server
adb devices
```

---

## Expected Results

After extraction, you should have:

### Core Files
- `Generic.kl` - Generic key layout (scan codes → key codes)
- `Generic.kcm` - Generic character map (basic ASCII)
- `keyboard_layout_agui_titan-key.kcm` - **TARGET FILE** (Titan 2 QWERTY layout)

### Optional Files
- Device-specific `.idc` files (input device configuration)
- Vendor-specific variants
- Additional keyboard layouts

---

## Next Steps

Once you have `keyboard_layout_agui_titan-key.kcm`:

1. **Study the file structure**
   - Understand how QWERTY is mapped
   - Note modifier combinations (Shift, Alt, Sym)
   - Identify symbol positions

2. **Create AZERTY variant**
   - Copy the file
   - Remap keys (Q→A, W→Z, etc.)
   - Adjust symbols for French layout

3. **Test the layout**
   - Create RRO APK or standalone layout app
   - Install on device
   - Verify all keys work correctly

4. **Share with community**
   - Add to `titan2keyboard` repository
   - Create `keyboard_files/` directory
   - Document file origins and modifications

---

## Example Script: Extract All Keyboard Files

Save this as `extract_titan2_keyboard.sh`:

```bash
#!/bin/bash

# Extract Unihertz Titan 2 keyboard configuration files
# Usage: ./extract_titan2_keyboard.sh

set -e

OUTDIR="titan2_keyboard_files"
mkdir -p "$OUTDIR"

echo "Extracting Titan 2 keyboard files..."

# Function to pull files with error handling
pull_files() {
    local pattern=$1
    local prefix=$2

    adb shell "ls $pattern" 2>/dev/null | tr -d '\r' | while read file; do
        if [ -n "$file" ]; then
            echo "Pulling: $file"
            adb pull "$file" "$OUTDIR/${prefix}$(basename $file)" 2>/dev/null || true
        fi
    done
}

# Pull from all common locations
echo "Checking /system/usr/keylayout/..."
pull_files "/system/usr/keylayout/*.kl" "system_"

echo "Checking /system/usr/keychars/..."
pull_files "/system/usr/keychars/*.kcm" "system_"

echo "Checking /system/usr/idc/..."
pull_files "/system/usr/idc/*.idc" "system_"

echo "Checking /vendor/usr/keylayout/..."
pull_files "/vendor/usr/keylayout/*.kl" "vendor_"

echo "Checking /vendor/usr/keychars/..."
pull_files "/vendor/usr/keychars/*.kcm" "vendor_"

echo ""
echo "Extraction complete. Files saved to $OUTDIR/"
echo ""
echo "Files found:"
ls -lh "$OUTDIR/"

echo ""
echo "Searching for Titan-specific files:"
find "$OUTDIR" -name "*agui*" -o -name "*titan*"
```

Make it executable and run:

```bash
chmod +x extract_titan2_keyboard.sh
./extract_titan2_keyboard.sh
```

---

## Resources

- [Android Key Character Map Files (Official Docs)](https://source.android.com/docs/core/interaction/input/key-character-map-files)
- [ADB Shell Commands](https://adbshell.com/)
- [XDA: External Keyboard Remapping](https://xdaforums.com/t/tutorial-external-keyboard-remapping-3-0.1568760/)

---

**Last Updated:** 2025-01-16
**Status:** Extraction Guide
**Next Step:** Execute extraction and analyze retrieved files
