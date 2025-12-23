# README for Effet - Video/Audio Downloader

## Project Information
- **App Name:** Effet
- **Package:** com.effet.downloader
- **Version:** 1.0
- **Min SDK:** 24 (Android 7.0+)
- **Target SDK:** 34 (Android 14)
- **Language:** Kotlin

## Purpose
Effet is a local media downloader for educational, trial, and project purposes only. It allows users to download publicly accessible video and audio streams from user-provided links.

## Key Features
- ✅ Video formats: MP4, MKV, WEBM
- ✅ Audio formats: MP3, M4A, OPUS
- ✅ Multiple quality options (144p - 4K)
- ✅ Audio bitrates: 64, 128, 192, 320 kbps
- ✅ Background download service with notifications
- ✅ Dark minimal UI theme
- ✅ Terms & Conditions on first launch
- ✅ Age confirmation (no data collection)
- ✅ Scoped storage compatible
- ✅ Zero analytics, no tracking, no ads

## Privacy Policy
- **No data collection** - Local processing only
- **No accounts** - Completely anonymous
- **No tracking** - No analytics or telemetry
- **No ads** - Ad-free experience
- **Local storage** - All files saved to Downloads/Effet/

## Project Structure
```
Effet/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/effet/downloader/
│   │       │   ├── TermsActivity.kt      - First-launch terms screen
│   │       │   ├── MainActivity.kt       - Home & Downloads navigation
│   │       │   ├── DownloadActivity.kt   - Download format/quality selection
│   │       │   ├── DownloadService.kt    - Foreground download service
│   │       │   ├── Downloader.kt         - Media download engine
│   │       │   └── Prefs.kt              - SharedPreferences wrapper
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   ├── values/
│   │       │   ├── drawable/
│   │       │   └── menu/
│   │       ├── assets/
│   │       └── AndroidManifest.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
└── gradle.properties
```

## Building the APK
### Prerequisites
- Android Studio Arctic Fox or newer
- Java 11+ JDK
- Android SDK (API 34)
- Gradle 8.1+

### Build Steps
1. Clone or extract the project
2. Open in Android Studio
3. Sync Gradle files
4. Build → Build Bundle(s) / APK(s) → Build APK(s)
5. Output: `app/build/outputs/apk/debug/app-debug.apk`

### For Release Build
```bash
./gradlew clean build -Pbuild.gradle
```
Output: `app/build/outputs/apk/release/Effet-v1.0-project.apk`

## Permissions Required
- `INTERNET` - Download media
- `READ_MEDIA_VIDEO` / `READ_MEDIA_AUDIO` - Access media library
- `READ_EXTERNAL_STORAGE` - Device storage access (SDK < 33)
- `WRITE_EXTERNAL_STORAGE` - Save files (SDK < 33)
- `FOREGROUND_SERVICE` - Background downloads

## Theme Colors
- **Background:** #0F0F0F (Dark)
- **Surface:** #1A1A1A (Card backgrounds)
- **Primary Accent:** #00E5C4 (Teal)
- **Secondary Accent:** #4FC3F7 (Light blue)
- **Text Primary:** #FFFFFF
- **Text Secondary:** #B0B0B0
- **Error:** #FF5252

## Implementation Notes

### Binary Dependencies
- **yt-dlp:** Download engine (extract from assets on first run)
- **ffmpeg:** Video/audio merging and conversion

To include binaries:
1. Compile yt-dlp and ffmpeg for Android ARM64
2. Place in `app/src/main/assets/`
3. Binaries extracted to `/data/data/com.effet.downloader/files/` at runtime

### First Launch Flow
1. App opens → TermsActivity
2. User reads terms → Checks confirmation
3. Clicks "I Agree & Continue" → MainActivity
4. Terms acceptance stored in SharedPreferences

### Download Flow
1. Home tab → Paste URL → Click Download
2. DownloadActivity → Select format & quality
3. Click "Start Download" → Service starts
4. Foreground notification with progress
5. File saved to: `Downloads/Effet/`

## API Levels
- **Min:** 24 (Android 7.0)
- **Target:** 34 (Android 14)
- **Tested:** 24-34

## Disclaimer
This app is provided AS-IS for educational and project purposes only. Users are solely responsible for:
- Ensuring legal right to download content
- Complying with local laws and regulations
- Respecting copyright and intellectual property rights
- Age-appropriate content selection

Developers assume no liability for misuse.

## License
Educational/Trial Use Only - Not for Commercial Distribution

---
**Built with ❤️ using Kotlin & Android**
