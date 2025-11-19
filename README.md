# android-tv-live-streaming-app-215455-139022

This workspace contains the Android TV frontend application under `androidtv_frontend/`.

Quick build:
- Debug APK: `cd androidtv_frontend && ./gradlew assembleDebug`
- Release APK (requires env vars): `cd androidtv_frontend && ./gradlew assembleRelease`

Run TV Home after install:
- Use D‑Pad RIGHT on “Open Home Page” in the launcher screen to open TV Home, or:
- `adb shell am start -n com.example.androidtv_frontend.debug/com.example.androidtv_frontend.ui.TvHomeActivity`

See `androidtv_frontend/README.md` for usage details, playback key mapping, and how to extend the repository.