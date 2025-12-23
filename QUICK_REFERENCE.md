# Effet - Developer Quick Reference

## Project Overview
**Effet** is a production-ready Kotlin Android application for downloading publicly accessible media streams. 
- **Package:** com.effet.downloader
- **Min SDK:** 24+ | **Target SDK:** 34
- **Theme:** Dark Minimal (#0F0F0F background, #00E5C4 accent)
- **Status:** Ready to build & distribute

## Quick Commands

### Windows PowerShell
```powershell
# Clean & build debug APK
.\gradlew.bat clean assembleDebug

# Build release APK
.\gradlew.bat clean assembleRelease

# Run on connected device
.\gradlew.bat installDebug

# View gradle tasks
.\gradlew.bat tasks
```

### Linux/macOS
```bash
# Clean & build debug APK
./gradlew clean assembleDebug

# Build release APK
./gradlew clean assembleRelease

# Run on connected device
./gradlew installDebug
```

## File Locations

### Kotlin Code
```
app/src/main/java/com/effet/downloader/
├── TermsActivity.kt       [Terms screen, age confirmation]
├── MainActivity.kt        [Navigation, Home/Downloads tabs]
├── DownloadActivity.kt    [Format/Quality selection]
├── DownloadService.kt     [Foreground service, notifications]
├── Downloader.kt          [Download engine, yt-dlp wrapper]
└── Prefs.kt               [SharedPreferences helper]
```

### Layouts (XML)
```
app/src/main/res/layout/
├── activity_terms.xml       [Terms + checkbox + buttons]
├── activity_main.xml        [Fragment container + bottom nav]
├── fragment_home.xml        [URL input + download button]
├── fragment_downloads.xml   [Active downloads list]
├── item_download.xml        [Download progress card]
└── activity_download.xml    [Format/quality dialog]
```

### Resources
```
app/src/main/res/
├── values/colors.xml        [8 theme colors]
├── values/strings.xml       [45+ text strings]
├── values/themes.xml        [Theme.Effet.Dark + styles]
├── drawable/                [Icons, button shapes]
└── menu/bottom_nav.xml      [Bottom navigation items]
```

## Key Classes

### TermsActivity
```kotlin
class TermsActivity : AppCompatActivity() {
    // First-launch flow
    // Terms acceptance via SharedPreferences
    // Age confirmation checkbox
    // "I Agree" / "Exit" buttons
}
```

### MainActivity
```kotlin
class MainActivity : AppCompatActivity() {
    // Bottom navigation (Home / Downloads)
    // Fragment switching
    // loadHomeFragment() - URL input
    // loadDownloadsFragment() - Download list
}
```

### DownloadActivity
```kotlin
class DownloadActivity : AppCompatActivity() {
    // Format spinner (MP4, MKV, WEBM, MP3, M4A, OPUS)
    // Quality spinner (144p - 4K, Best)
    // Download initiation
    // Service startup
}
```

### DownloadService
```kotlin
class DownloadService : Service() {
    // Foreground service
    // Notification channel (API 26+)
    // Persistent notification
    // Background execution
}
```

### Downloader
```kotlin
class Downloader(context: Context) {
    fun isValidUrl(url: String): Boolean
    fun downloadMedia(url, format, quality, callbacks)
    fun extractBinaries()
    fun getDownloadDirectory(): File
    fun sanitizeFileName(name: String): String
    fun ensureUniqueFileName(file: File): File
}
```

### Prefs
```kotlin
class Prefs(context: Context) {
    fun setTermsAccepted(accepted: Boolean)
    fun isTermsAccepted(): Boolean
    fun addDownloadItem(downloadId, fileName, format, quality)
    fun updateDownloadProgress(downloadId, progress)
    fun updateDownloadStatus(downloadId, status)
    fun getDownloads(): List<Map<String, String>>
    fun clearDownload(downloadId)
}
```

## UI Components

### Colors
| Use | Hex | RGB |
|-----|-----|-----|
| Background | #0F0F0F | (15, 15, 15) |
| Surface | #1A1A1A | (26, 26, 26) |
| Primary | #00E5C4 | (0, 229, 196) Teal |
| Secondary | #4FC3F7 | (79, 195, 247) Light Blue |
| Text | #FFFFFF | (255, 255, 255) |
| Hint | #B0B0B0 | (176, 176, 176) |
| Error | #FF5252 | (255, 82, 82) |

### Buttons
```xml
<!-- Primary: Teal filled -->
<shape><solid android:color="#00E5C4" /></shape>

<!-- Secondary: Teal stroke -->
<shape><stroke android:color="#00E5C4" /></shape>
```

## Permissions
```xml
INTERNET                    [Download streams]
READ_MEDIA_VIDEO           [SDK 33+]
READ_MEDIA_AUDIO           [SDK 33+]
READ_EXTERNAL_STORAGE      [SDK < 33]
WRITE_EXTERNAL_STORAGE     [SDK < 33]
FOREGROUND_SERVICE         [Background downloads]
FOREGROUND_SERVICE_DATA_SYNC [Download classification]
```

## Download Directory
```
/sdcard/Download/Effet/
├── media_1704067200000.mp4
├── media_1704067205000.mp3
├── media_1704067210000(1).mkv
└── ...
```

## Terms & Age Confirmation
```
✅ First launch only
✅ Checkbox: "I confirm I am legally allowed to access such content"
✅ Buttons: "I Agree & Continue" / "Exit App"
✅ No data stored (only acceptance flag)
✅ Stored in SharedPreferences
```

## Building for Distribution

### Debug APK
```bash
./gradlew.bat assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Release APK (Unsigned)
```bash
./gradlew.bat assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

### Release APK (Signed)
```bash
# 1. Create keystore (one-time)
keytool -genkey -v -keystore effet.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias effet

# 2. Sign APK
jarsigner -verbose -sigalg SHA256withRSA \
  -digestalg SHA-256 \
  -keystore effet.jks \
  app-release.apk effet

# 3. Verify
jarsigner -verify app-release.apk
```

### Rename for Release
```bash
copy app-release.apk Effet-v1.0-project.apk
```

## Installation

### Via ADB (Debug)
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Via Android Studio
- Build → Build Bundle(s) / APK(s) → Build APK(s)
- Right-click APK → Analyze APK → Install

### Via File Manager
- Transfer APK to device
- Tap to install
- Allow unknown sources if needed

## Testing Checklist

- [ ] First launch: Terms screen appears
- [ ] Checkbox: Can't click agree until checked
- [ ] Home tab: URL input works
- [ ] Download button: Validates URL
- [ ] Download screen: Formats populate
- [ ] Download screen: Qualities populate
- [ ] Start download: Service starts
- [ ] Downloads tab: Progress visible
- [ ] Notification: Shows during download
- [ ] Completed: File saved to Downloads/Effet/
- [ ] Back navigation: Works smoothly

## Common Issues & Fixes

| Issue | Solution |
|-------|----------|
| Build fails: SDK not found | Set `sdk.dir` in local.properties |
| Cannot resolve R | Run `./gradlew.bat clean` then rebuild |
| Gradle sync fails | Delete .gradle folder, sync again |
| APK too large | Enable minifyEnabled in build.gradle |
| Permission denied | Request at runtime (API 23+) |
| Service crashes | Check FOREGROUND_SERVICE permission |

## Documentation
- **BUILD_GUIDE.md** - Detailed build instructions
- **PROJECT_STRUCTURE.md** - Complete file listing
- **README.md** - Project overview
- **This file** - Quick reference

## Version Info
```
Version: 1.0
Build: debug/release
Package: com.effet.downloader
App Name: Effet
Min SDK: 24 (Android 7.0)
Target SDK: 34 (Android 14)
Language: Kotlin
```

---

**Status:** ✅ Ready for Development & Distribution
**No External APIs Required** | **Zero Tracking**
