# Fastlane metadata

Play Store listing metadata and screenshots for **Base64 ⇄ QmFzZTY0**
(`be.robinj.base64`), laid out for [fastlane supply](https://docs.fastlane.tools/actions/supply/).

```
metadata/android/<locale>/
  title.txt              # ≤ 30 chars
  short_description.txt  # ≤ 80 chars
  full_description.txt   # ≤ 4000 chars
  images/phoneScreenshots/*.png
```

Locales: `en-GB`, `nl-NL` (matching the app's bundled translations).

## Screenshots

The screenshots are generated **without an emulator**. A Robolectric test
(`ScreenshotTest`) launches the real activities in native graphics mode, types
the demo phrase `NOOT NOOT`, and rasterises the genuine view hierarchy to PNGs.

Regenerate them with either:

```bash
./gradlew generateScreenshots -Pscreenshots     # writes straight into this tree
# or
bundle exec fastlane screenshots
```

## Uploading (optional)

`fastlane upload_metadata` pushes the listing text + screenshots to Google Play.
It needs a Play service-account key: set `json_key_file` in `Appfile` (or the
`SUPPLY_JSON_KEY` env var) first. No credentials are committed to this repo.
