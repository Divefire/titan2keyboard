# Titan 2 Support Added to TitanQwerty

## Overview

This fork adds support for **Unihertz Titan 2** keyboard layouts to TitanQwerty.

## Key Discovery

Titan 2 uses a different Sym key hardware scan code:
- **Titan (original)**: Sym key = 250
- **Titan Pocket**: Sym key = 127
- **Titan 2**: Sym key = **63** ✓

This difference is why the original titanqwerty didn't work with Titan 2 devices.

## Changes Made

### Proof of Concept: Russian Cyrillic Layout

**New Files:**
- `finqwerty/src/main/res/raw/titan2_cyr_1.kcm` - Russian Cyrillic layout for Titan 2

**Modified Files:**
- `finqwerty/src/main/res/values/strings.xml` - Added display label
- `finqwerty/src/main/res/xml/finqwerty_layouts.xml` - Registered layout

**Key Modification in .kcm file:**
```diff
- map key 250 SYM
+ map key 63 SYM
```

## Future Additions

To add more Titan 2 layouts:

1. Copy existing layout: `titan_*.kcm` → `titan2_*.kcm`
2. Change line 3: `map key 63 SYM`
3. Add string in `strings.xml`:
   ```xml
   <string name="titan2_xxx_1" translatable="false">TitanQwerty Unihertz Titan 2, [Language]</string>
   ```
4. Add entry in `finqwerty_layouts.xml`:
   ```xml
   <keyboard-layout android:name="titan2_xxx_1" android:label="@string/titan2_xxx_1" android:keyboardLayout="@raw/titan2_xxx_1"/>
   ```

## Planned Layouts

- [ ] titan2_cyr_1 - Russian Cyrillic Phonetic ✓ (Done)
- [ ] titan2_ger_1 - German QWERTY
- [ ] titan2_ger_2 - German QWERTZ
- [ ] titan2_us_1 - US QWERTY
- [ ] titan2_cz_1 - Czech QWERTY
- [ ] titan2_cz_2 - Czech QWERTZ
- [ ] titan2_vie_1 - Vietnamese
- [ ] titan2_kor_1 - Korean
- [ ] titan2_gre_1 - Greek
- [ ] titan2_ukr_1 - Ukrainian

## Testing

See `BUILD_TITANQWERTY_TITAN2.md` for build and installation instructions.

## Credits

- Original TitanQwerty: fjdrjr/titanqwerty
- Titan 2 support: Added via Claude Code AI assistance
- Sym key discovery: Hardware testing on Unihertz Titan 2

## License

Apache 2.0 (same as original titanqwerty project)
