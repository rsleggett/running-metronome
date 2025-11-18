# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Running Metronome is an Android app that plays a metronome beat in the background while users listen to music or podcasts during runs. The app is designed to help runners maintain consistent cadence (160-180 BPM typical).

**Package**: `com.electricbiro.runningmetronome`
**Min SDK**: API 26 (Android 8.0)
**Target SDK**: API 36

## Build Commands

```bash
# Build the app
./gradlew build

# Run unit tests
./gradlew test

# Run instrumented tests on connected device/emulator
./gradlew connectedAndroidTest

# Install debug build on connected device
./gradlew installDebug

# Generate release APK
./gradlew assembleRelease

# Clean build
./gradlew clean
```

## Architecture

This app uses **MVVM (Model-View-ViewModel)** with **Hilt** for dependency injection and **Jetpack Compose** for UI.

### Layer Structure

```
data/          # Data models and repositories
  model/       # Data classes (SettingsModel, enums)
  repository/  # Data persistence via DataStore (planned)

domain/        # Business logic layer (planned)
  usecase/     # Use cases for metronome operations

ui/            # Presentation layer
  screen/      # Compose screens
  component/   # Reusable UI components
  theme/       # Material Design 3 theme

audio/         # Audio playback logic
  MetronomeAudioPlayer.kt - Core audio engine using SoundPool

service/       # Background services
  MetronomeService.kt - Foreground service for background playback

di/            # Hilt dependency injection modules
  AppModule.kt - Singleton providers
```

### Key Architecture Points

1. **Hilt DI**: All activities and application class use `@AndroidEntryPoint` and `@HiltAndroidApp` annotations
2. **Version Catalog**: Dependencies managed via `gradle/libs.versions.toml` with alias references
3. **Jetpack Compose**: Full Compose UI (no XML layouts)
4. **Material 3**: Using Material Design 3 components and theming

## Audio Implementation

The metronome plays **alongside other audio** (music/podcasts) without ducking or interrupting:

- Uses `SoundPool` for low-latency playback
- Two audio modes selectable by user:
  - **Media mode**: Uses `USAGE_MEDIA` - always plays, uses media volume
  - **Notification mode**: Uses `USAGE_ASSISTANCE_SONIFICATION` - respects mute switch, uses notification volume
- App-level volume control (0-100%)
- Kotlin coroutines with delay for BPM-based timing (60000ms / BPM)
- SoundPool handles audio latency automatically

## Background Playback Requirements

For Android 8.0+:

- Must use **Foreground Service** with ongoing notification
- Requires `FOREGROUND_SERVICE` permission in manifest
- Handle audio focus to mix with other apps (don't duck music completely)
- Implement notification controls for play/pause

## Settings Persistence

User preferences (BPM, volume, sound type) should persist using:

- **DataStore** (androidx.datastore.preferences) - not SharedPreferences
- Store as `Flow<Settings>` in repository
- ViewModel collects and exposes as `StateFlow`

## Sound Files

Six metronome sounds in `app/src/main/res/raw/`:
- `metronomeclick.mp3` - Classic metronome click
- `metronomesnare.mp3` - Snare drum sound
- `metronomeknock.mp3` - Knock/wood block sound
- `metronomedrumtr707.mp3` - TR-707 drum sound
- `metronomedrumtr808.mp3` - TR-808 drum sound
- `metronomedrumtr909.mp3` - TR-909 drum sound

Access via resource ID: `R.raw.metronomeclick`, etc.
User can switch between sounds via FilterChip selector in UI.

## Current Implementation Status

**Phase**: Core functionality complete - MVP ready for testing

### ✅ Implemented:
- **Hilt DI**: Complete setup with `@HiltAndroidApp` and `@AndroidEntryPoint`
- **Audio Playback**: Full `MetronomeAudioPlayer` implementation with SoundPool
  - Support for all 6 sound types
  - Switchable audio modes (Media/Notification)
  - Volume control (0-100%)
  - BPM range: 40-200
  - Debug logging for troubleshooting
- **Background Service**: `MetronomeService` foreground service
  - Persistent notification with Play/Pause/Stop controls
  - Continues playing when app is minimized
  - Works with screen off
  - MediaStyle notification
- **UI**: Complete Material 3 Compose interface
  - BPM slider with large display (40-200 range)
  - Volume slider with percentage display
  - Sound selector (6 sounds via FilterChips)
  - Audio mode selector (Media/Notification)
  - Large Play/Pause FAB
  - Scrollable layout
- **MVVM**: Full architecture implementation
  - `MetronomeViewModel` with StateFlow
  - Service binding and lifecycle management
  - Proper state management
- **Permissions**: All required permissions in manifest
  - `FOREGROUND_SERVICE`
  - `FOREGROUND_SERVICE_MEDIA_PLAYBACK`
  - `POST_NOTIFICATIONS`

### 🔄 Not Yet Implemented:
- DataStore repository for settings persistence (currently defaults on restart)
- Runtime notification permission request for Android 13+
- Unit tests
- UI tests
- Release build configuration

## Development Notes

### Gradle Configuration
- Using Kotlin DSL with version catalog
- kapt enabled for Hilt annotation processing
- Compose compiler plugin via `kotlin-compose` plugin
- Java 11 compatibility (sourceCompatibility/targetCompatibility)

### Testing
- Unit tests: JUnit 4 in `app/src/test/`
- Instrumented tests: AndroidJUnit + Espresso in `app/src/androidTest/`
- Compose UI testing available via `androidx.compose.ui.test.junit4`

### BPM Timing Precision
Currently using Kotlin coroutines with `delay()` for timing (60000ms / BPM).
If more precise timing needed, consider `ScheduledExecutorService`. Current implementation provides adequate precision for running cadence.

### Battery Optimization
- Service automatically starts/stops with playback
- No wake locks used
- Coroutine-based timing is battery efficient
- SoundPool provides low-latency playback without continuous CPU usage

### Known Issues & Solutions
- **Mute Switch**: On real devices, notification mode respects hardware mute switch. Use Media mode for running to ensure sound plays even when phone is on vibrate.
- **Sound Loading**: SoundPool loads sounds asynchronously. OnLoadCompleteListener confirms loading before playback.
- **Service Lifecycle**: Service binds on MainActivity onCreate and unbinds on destroy. Service persists when app is minimized.
