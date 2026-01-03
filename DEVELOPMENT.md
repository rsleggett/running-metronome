# Development Guide

This guide contains all technical details and instructions for developing the Running Metronome app.

## Tech Stack

### Core Technologies
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Build System**: Gradle (Kotlin DSL) with Version Catalogs
- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14+ (API 34+)

### Architecture & Libraries
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Persistent Storage**: DataStore (for user preferences)
- **Audio Playback**: Android Media APIs (MediaPlayer/AudioTrack)
- **Async Operations**: Kotlin Coroutines + Flow
- **UI Design**: Material Design 3

### Why This Stack?
- **Jetpack Compose**: Perfect for a single-screen app - less boilerplate than XML
- **MVVM**: Lightweight architecture suitable for simple apps
- **Hilt**: Simplified dependency injection, easy testing
- **DataStore**: Modern, type-safe preference storage (replaces SharedPreferences)
- **Single Module**: Simple project structure appropriate for this scope

## Project Structure

```
running-metronome/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/runningmetronome/
│   │   │   │   ├── data/
│   │   │   │   │   ├── repository/     # Settings repository
│   │   │   │   │   └── model/          # Data models
│   │   │   │   ├── domain/
│   │   │   │   │   └── usecase/        # Business logic
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screen/         # Main metronome screen
│   │   │   │   │   ├── component/      # Reusable UI components
│   │   │   │   │   └── theme/          # Material Design theme
│   │   │   │   ├── service/            # Foreground service for background playback
│   │   │   │   ├── audio/              # Audio playback logic
│   │   │   │   └── di/                 # Hilt modules
│   │   │   ├── res/
│   │   │   │   ├── raw/                # Audio files (metronome sounds)
│   │   │   │   └── values/             # Strings, colors, etc.
│   │   │   └── AndroidManifest.xml
│   │   └── test/                       # Unit tests
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml              # Version catalog
├── build.gradle.kts
└── README.md
```

## Development Setup

### Prerequisites
1. **Android Studio**: Latest stable version (2025+)
2. **System Requirements**:
   - 16GB RAM recommended
   - 8GB minimum disk space
   - Java JDK 17+
3. **Android Device or Emulator**: Android 8.0+ (API 26+)

### Getting Started

#### 1. Install Android Studio
- Download from [developer.android.com](https://developer.android.com/studio)
- Follow installation wizard
- Install Android SDK and required build tools

#### 2. Create New Project
```bash
# In Android Studio:
File → New → New Project
→ Select "Empty Activity" (Compose)
→ Name: Running Metronome
→ Package: com.example.runningmetronome
→ Language: Kotlin
→ Minimum SDK: API 26 (Android 8.0)
→ Build configuration: Kotlin DSL
```

#### 3. Configure Dependencies
Add to `gradle/libs.versions.toml`:
```toml
[versions]
kotlin = "1.9.20"
compose = "1.5.4"
hilt = "2.48"
datastore = "1.0.0"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version = "1.12.0" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version = "2.6.2" }
compose-ui = { group = "androidx.compose.ui", name = "ui", version.ref = "compose" }
compose-material3 = { group = "androidx.compose.material3", name = "material3", version = "1.1.2" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }

[plugins]
android-application = { id = "com.android.application", version = "8.2.0" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
```

## Implementation Phases

### Phase 1: Project Setup ✅ COMPLETE
- [x] Create Android Studio project with Compose template
- [x] Configure Gradle with version catalogs
- [x] Set up Hilt dependency injection
- [x] Create basic project structure
- [x] Add audio files to res/raw/ (6 sounds)

### Phase 2: Data Layer ✅ PARTIAL
- [x] Create Settings data model (BPM, volume, sound type, audio usage type)
- [ ] Implement DataStore for preferences (not yet implemented - using defaults)
- [ ] Create SettingsRepository (not yet implemented)

### Phase 3: Audio Engine ✅ COMPLETE
- [x] Implement MetronomeAudioPlayer class using SoundPool
- [x] Support for loading different sound files (6 sounds)
- [x] Independent volume control implementation (0-100%)
- [x] BPM-based timing logic with coroutines (40-200 BPM)
- [x] Switchable audio usage types (Media/Notification)
- [x] Debug logging for troubleshooting

### Phase 4: Background Service ✅ COMPLETE
- [x] Create Foreground Service for background playback
- [x] Implement notification controls (Play/Pause/Stop)
- [x] Audio mixing with other apps (doesn't duck music)
- [x] MediaStyle notification with controls
- [x] Service binding and lifecycle management

### Phase 5: UI Development ✅ COMPLETE
- [x] Design Material 3 theme
- [x] Build main screen UI with Compose:
  - [x] BPM slider with large numeric display
  - [x] Volume control slider with percentage
  - [x] Sound selector (6 sounds via FilterChips)
  - [x] Audio mode selector (Media/Notification)
  - [x] Large Play/Pause FAB
  - [x] Scrollable layout for all screen sizes
- [x] Implement ViewModel with StateFlow
- [x] Connect UI to audio engine via service

### Phase 6: Testing & Polish ✅ TESTS COMPLETE, POLISH IN PROGRESS
- [x] Unit tests for ViewModel and data models (~50 tests passing)
  - [x] MetronomeAudioPlayerTest: 32 tests for audio playback (Robolectric)
  - [x] MetronomeViewModelTest: 18 tests for state management
- [x] Instrumented tests (44 tests ready for device testing)
  - [x] MetronomeScreenTest: 27 UI tests for Compose interactions
  - [x] MetronomeServiceTest: 17 tests for service with Hilt DI
- [x] Test background playback scenarios (tested on real device)
- [x] Test with actual music/podcast apps (verified mixing works)
- [ ] Icon and launcher setup (using default icons)
- [ ] Performance optimization
- [ ] Runtime notification permissions for Android 13+
- [ ] Settings persistence with DataStore

### Phase 7: Advanced Features ✅ REMOVED (Simplified to MVP)
- [x] BPM quick presets (160, 170, 175, 180, 185)
- ~~Playback modes (Simple/Pattern)~~ - Removed for simplicity
- ~~Accent patterns for Simple mode~~ - Removed for simplicity
- ~~8-step drum pattern sequencer~~ - Removed for simplicity
- ~~Pattern editor UI with step controls~~ - Removed for simplicity
- ~~Dual sound selection for patterns~~ - Removed for simplicity

## Key Technical Considerations

### Background Audio Playback
- Use Foreground Service with ongoing notification (required for Android 8+)
- Request `FOREGROUND_SERVICE` permission
- Handle audio focus to mix with other apps (not duck completely)

### Volume Control ✅ IMPLEMENTED
- Two audio modes selectable by user:
  - **Media mode** (default): Uses `USAGE_MEDIA` - always plays, uses media volume, works on vibrate/silent
  - **Notification mode**: Uses `USAGE_ASSISTANCE_SONIFICATION` - respects mute switch, uses notification volume
- App-level volume control (0-100%) independent of system volume
- Volume changes apply immediately during playback

### BPM Timing ✅ IMPLEMENTED
- Coroutines with delay for timing: `60000ms / BPM`
- Runs in `Dispatchers.Default` for background execution
- Adequate precision for running cadence (tested 40-200 BPM)
- SoundPool handles audio latency automatically
- Future: Consider ScheduledExecutorService if sub-millisecond precision needed

### Battery Optimization ✅ IMPLEMENTED
- Efficient service lifecycle: starts/stops with playback
- No wake locks used
- Coroutine-based timing is battery efficient
- Service persists only during active playback

## Design Guidelines

### Material Design 3 Implementation
- Use dynamic theming (adapts to user wallpaper on Android 12+)
- Support light/dark mode
- Large, touch-friendly controls (minimum 48dp touch targets)
- Clear visual feedback for all interactions

### Accessibility
- Proper content descriptions
- TalkBack support
- High contrast support
- Haptic feedback option

## Resources

### Learning Resources
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Android Audio Focus](https://developer.android.com/guide/topics/media-apps/audio-focus)
- [Foreground Services](https://developer.android.com/develop/background-work/services/foreground-services)

### Similar Apps for Reference
- Metronome apps on Google Play
- Audio mixing behavior examples

## Development Commands

```bash
# Build the app
./gradlew build

# Run tests
./gradlew test

# Install on connected device (specify device if multiple)
./gradlew installDebug
ANDROID_SERIAL=<device-id> ./gradlew installDebug

# Generate APK
./gradlew assembleRelease

# Clean build
./gradlew clean

# Check connected devices
adb devices
```

## Testing Notes

### Real Device Testing
The app has been tested on real devices (OnePlus CPH2609, Android 15) and emulators (Pixel Fold API 35).

**Important**: On real devices with hardware mute switches:
- **Media mode** (recommended for running): Plays even when phone is on vibrate/silent
- **Notification mode**: Respects hardware mute switch - will be silent on vibrate

### Audio Mixing
Verified to work alongside:
- Spotify
- YouTube Music
- Podcast apps
- Other media players

The metronome mixes with music without ducking or interrupting playback.

### Background Playback
Tested scenarios:
- App minimized while playing
- Screen off while playing
- Switching between apps
- Using notification controls
- Phone calls (needs further testing)

## Recent Session Progress

### Session 2026-01-03: Simplification & Running Setup
**Duration**: ~3.5 hours | **Status**: ✅ Complete

**Major Changes**:
- **Removed Pattern Mode** based on user testing feedback (too complex)
  - Deleted 8-step drum sequencer, pattern editor, dual sound selection
  - Removed `PlaybackMode`, `DrumPattern`, `PatternStep` data classes
  - Deleted ~50 pattern-related tests
  - Simplified codebase: 10 files modified, 3 deleted

- **Verified App Working**
  - Built and tested on emulator (Pixel_Fold_API_35)
  - Fixed emulator audio issues (cold boot solution)
  - Confirmed all features working: audio, UI controls, background service

- **Development Tools Created**
  - `run-app.sh` - One-command build and launch script
  - `RUNNING_THE_APP.md` - Comprehensive running guide (40+ commands)
  - `EMULATOR_AUDIO_FIX.md` - Audio troubleshooting guide
  - Updated CLAUDE.md with running instructions

**What Remains** (Simplified MVP):
- Simple metronome with consistent beat
- 6 sound options, volume control, BPM control (40-200)
- Background playback with notification controls
- ~50 unit tests passing, all builds successful

**Next Priority**: Settings persistence with DataStore (see NEXT_ITERATION.md)

---

### Session 2025-11-18: Test Suite & Build Fixes
- **Fixed** `build.gradle.kts` compileSdk syntax error
- **Added** comprehensive test suite:
  - 82 unit tests covering data models, audio player, and ViewModel
  - 44 instrumented tests for UI and service integration
  - Test dependencies: Mockito-Kotlin, Turbine, Robolectric, Hilt Testing
- **Verified** all tests pass and build is clean
- **Updated** documentation to reflect current implementation status

### Session 2025-11-16: Advanced Features (Later Simplified)
- **Added** BPM quick presets (160, 170, 175, 180, 185)
- **Added** Sound selector for 6 different metronome sounds
- **Added** Audio mode selector (Media/Notification)
- **Added** Background service with notification controls
- **Fixed** Sound not playing on real devices (mute switch issue)
- **Fixed** UI layout cutting off play button (scrollable layout)
- **Updated** `.gitignore` files and added androidx.media dependency
- **Note**: Playback modes, accent patterns, and drum sequencer were later removed for simplicity
