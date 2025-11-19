# androidtv_frontend — Build & Run (TV UI + Playback)

This module is a fully configured Android TV application. It includes TV-specific manifest entries, Leanback dependencies, and a placeholder release signing configuration that reads from environment variables.

## Prerequisites
- JDK 17
- Android SDK platform 34 installed
- Gradle wrapper included (no need to install Gradle separately)

## Build (Debug APK)
From the `androidtv_frontend` directory run:
```
./gradlew assembleDebug
```
The debug APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

## Install & Run
- Install on an Android TV device or emulator.
- The launcher opens MainActivity with two demo buttons (existing WebView screens).
- Press D‑Pad RIGHT on “Open Home Page” to open the new Leanback Home; or start `TvHomeActivity` directly via adb:
```
adb shell am start -n com.example.androidtv_frontend.debug/com.example.androidtv_frontend.ui.TvHomeActivity
```

## TV Home (Browse)
- HomeBrowseFragment shows rows:
  - Featured
  - Movies
  - Live TV (placeholder)
- Use D‑Pad to navigate rows and cards.
- Press OK to play.

## Playback
- PlaybackActivity uses Jetpack Media3 (ExoPlayer) with MediaSession.
- Supported sample URLs:
  - HLS, DASH and MP4 demo streams from public sources.
- Remote keys:
  - OK/Play/Pause: toggle play/pause
  - Left/Right: seek −/+10s
- Errors:
  - Initializes within try/catch and shows Toast on network or format failures.

## Architecture
- data/MockContentRepository: mock catalog (DI-friendly constructor)
- domain/Models: MediaItem/MediaCategory
- presentation/CardPresenter: Leanback card visuals (Ocean Professional accents)
- ui/HomeBrowseFragment: BrowseSupportFragment rows
- ui/PlaybackActivity: Media3 player + MediaSession
- ui/TvHomeActivity: hosts HomeBrowseFragment

## Theming
- Ocean Professional colors:
  - Primary: #2563EB
  - Secondary: #F59E0B
- Focused card applies subtle focus scaling and tinted overlay.

## Extending
- Replace MockContentRepository with real implementation (e.g., Retrofit) and inject via constructor.
- Add more categories by returning additional MediaCategory entries.
- To support channel logos and fanart, add proper HTTPS image URLs to MediaItem.

## Release Build
Release builds require signing. Provide the following environment variables before running assembleRelease:
- ANDROID_KEYSTORE_PATH
- ANDROID_KEYSTORE_PASSWORD
- ANDROID_KEY_ALIAS
- ANDROID_KEY_PASSWORD

Example:
```
export ANDROID_KEYSTORE_PATH=/abs/path/to/release.jks
export ANDROID_KEYSTORE_PASSWORD=your-keystore-pass
export ANDROID_KEY_ALIAS=release
export ANDROID_KEY_PASSWORD=your-key-pass

./gradlew assembleRelease
```

If these variables are not set, Gradle will configure the project but assembleRelease will produce an unsigned APK or fail at signing. See `.env.example` for variable names.

## Notes
- The app targets Android TV: it declares `android.software.leanback` and a LEANBACK_LAUNCHER activity.
- ExoPlayer (Jetpack Media3) and Leanback dependencies are included for playback and TV UI compatibility.
- The app avoids runtime network/service dependencies and can build standalone.
