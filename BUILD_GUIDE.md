# Effet - Complete Build & Deployment Guide

## Quick Start (Windows)

### Prerequisites
- Android Studio 2022.1+ (or Arctic Fox)
- Java JDK 11+
- Android SDK API 34
- Gradle 8.1+

### Build Steps

1. **Clone/Extract Project**
   ```bash
   cd /path/to/Effet
   ```

2. **Configure SDK Path**
   - Option A: Auto-detect
     ```bash
     # Android Studio will auto-detect
     ```
   - Option B: Manual setup
     ```bash
     # Create local.properties
     echo sdk.dir=C:\Users\YourUsername\AppData\Local\Android\Sdk > local.properties
     ```

3. **Sync & Build**
   - In Android Studio: File → Sync Now
   - Build → Build Bundle(s) / APK(s) → Build APK(s)

4. **Output**
   - Debug APK: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build (Command Line)

```bash
# For Windows PowerShell
.\gradlew.bat clean build -Pbuild=release

# For Linux/macOS
./gradlew clean build -Pbuild=release
```

**Output:** `app/build/outputs/apk/release/app-release.apk`

## Project Components

### Activities
1. **TermsActivity**
   - First-launch terms screen
   - Age confirmation checkbox
   - No data collection
   - SharedPreferences to track acceptance

2. **MainActivity**
   - Bottom navigation (Home/Downloads)
   - Fragment switching
   - Home: URL input + Download button
   - Downloads: Active downloads list

3. **DownloadActivity**
   - Format selection (MP4, MKV, WEBM, MP3, M4A, OPUS)
   - Quality options (144p - 4K, bitrates 64-320 kbps)
   - Download execution & progress tracking

### Services
- **DownloadService**: Foreground service for background downloads with persistent notification

### Utilities
- **Prefs.kt**: SharedPreferences wrapper for persistent data
- **Downloader.kt**: Media download engine using yt-dlp + ffmpeg

## Binary Integration

### Adding yt-dlp
1. Download yt-dlp source
2. Cross-compile for Android ARM64:
   ```bash
   # Requires Android NDK
   ndk-build APP_ABI=arm64-v8a
   ```
3. Copy binary to: `app/src/main/assets/yt-dlp`
4. Mark as executable at runtime (Downloader.kt handles this)

### Adding ffmpeg
1. Download FFmpeg source
2. Compile using NDK for Android ARM64
3. Copy to: `app/src/main/assets/ffmpeg`

## UI Theme

### Dark Minimal Theme
```xml
Background:        #0F0F0F
Surface/Cards:     #1A1A1A
Primary Accent:    #00E5C4 (Teal)
Secondary Accent:  #4FC3F7 (Light Blue)
Text Primary:      #FFFFFF
Text Secondary:    #B0B0B0
Error:             #FF5252
Success:           #4CAF50
```

## Permissions

### Required
- `INTERNET` - Fetch media streams
- `READ_MEDIA_VIDEO` / `READ_MEDIA_AUDIO` - Access device media
- `READ_EXTERNAL_STORAGE` - Device storage (SDK < 33)
- `WRITE_EXTERNAL_STORAGE` - Save files (SDK < 33)
- `FOREGROUND_SERVICE` - Background downloads

### Scoped Storage (SDK 30+)
- Automatically requests permission at runtime
- Saves to: `Downloads/Effet/`
- Scoped storage compatible

## Signing APK for Release

### Create Keystore (One-time)
```bash
keytool -genkey -v -keystore effet-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias effet
```

### Sign APK
```bash
jarsigner -verbose -sigalg SHA256withRSA \
  -digestalg SHA-256 \
  -keystore effet-key.jks \
  app/build/outputs/apk/release/app-release.apk \
  effet
```

### Verify
```bash
jarsigner -verify -verbose app-release-signed.apk
```

## Testing

### Emulator Setup
1. Android Studio → Virtual Device Manager
2. Create device (API 24+ recommended)
3. Install APK:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### Device Testing
1. Enable Developer Mode (Settings → About → Build Number)
2. Enable USB Debugging
3. Connect via USB
4. Run: `./gradlew installDebug`

## Gradle Tasks

### Common Commands
```bash
# Clean build
./gradlew.bat clean

# Build debug APK
./gradlew.bat assembleDebug

# Build release APK
./gradlew.bat assembleRelease

# Build both debug & release
./gradlew.bat build

# Install on connected device
./gradlew.bat installDebug

# Run app on device
./gradlew.bat run

# Check dependencies
./gradlew.bat dependencies

# ProGuard/R8 minification preview
./gradlew.bat assembleRelease --scan
```

## Troubleshooting

### Build Fails: "Cannot find SDK"
```
Solution: Set sdk.dir in local.properties
sdk.dir=C:\Users\YourName\AppData\Local\Android\Sdk
```

### Compilation Error: "Unresolved reference: R"
```
Solution:
1. Clean project: ./gradlew.bat clean
2. Rebuild: ./gradlew.bat build
3. In Android Studio: Build → Clean Project → Rebuild
```

### Missing Dependencies
```
Solution:
./gradlew.bat --refresh-dependencies build
```

### APK Size Too Large
```
Enable ProGuard/R8 in build.gradle (release):
minifyEnabled true
shrinkResources true
```

### Permission Denials at Runtime
```
Android 6.0+ requires runtime permission requests.
All handled in Activities - user grants at first use.
```

## Distribution

### GitHub Releases
1. Build signed release APK (see Signing section)
2. Go to Releases → Create New Release
3. Upload: `Effet-v1.0-project.apk`
4. Add release notes

### Installation from APK
```bash
adb install Effet-v1.0-project.apk
```

## Version Management

### Update Version
Edit `app/build.gradle`:
```gradle
versionCode 2
versionName '1.1'
```

### App Naming
APK naming convention: `Effet-v<VERSION>-<BUILD_TYPE>.apk`
- Debug: `app-debug.apk`
- Release: `app-release.apk`

## Privacy Compliance

✅ **No Data Collection**
- No analytics
- No telemetry
- No tracking
- No ads
- No accounts

✅ **Local Processing Only**
- All operations on-device
- No cloud sync
- No external APIs (except download URLs)

✅ **Terms & Age Confirmation**
- First-launch mandatory
- Checkbox for legal compliance
- No age data stored

## App Metadata

| Field | Value |
|-------|-------|
| Package | com.effet.downloader |
| App Name | Effet |
| Min SDK | 24 |
| Target SDK | 34 |
| Version | 1.0 |
| Language | Kotlin |
| Theme | Dark Minimal |

---

**Build Status:** ✅ Production-Ready
**Last Updated:** 2025-01-01
