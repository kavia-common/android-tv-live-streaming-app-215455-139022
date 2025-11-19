# androidtv_frontend — Build Instructions

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

## Build (Release APK)
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

