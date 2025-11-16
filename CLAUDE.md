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
  MetronomeAudioPlayer.kt - Core audio engine using AudioTrack

service/       # Background services (planned)
  - Foreground service for background playback

di/            # Hilt dependency injection modules
  AppModule.kt - Singleton providers
```

### Key Architecture Points

1. **Hilt DI**: All activities and application class use `@AndroidEntryPoint` and `@HiltAndroidApp` annotations
2. **Version Catalog**: Dependencies managed via `gradle/libs.versions.toml` with alias references
3. **Jetpack Compose**: Full Compose UI (no XML layouts)
4. **Material 3**: Using Material Design 3 components and theming

## Audio Implementation Requirements

The metronome must play **alongside other audio** (music/podcasts) without ducking or interrupting:

- Use `AudioTrack` for precise timing control
- Use audio stream type that doesn't conflict with `STREAM_MUSIC`
- Implement app-level volume control (not system volume)
- Use Kotlin coroutines with delay for BPM-based timing
- Account for audio latency in timing calculations

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

Three metronome sounds in `app/src/main/res/raw/`:
- `metronomeclick.mp3` (MVP - currently implemented)
- `metronomedrum.mp3` (planned)
- `metronomeclassic.mp3` (planned)

Access via resource ID: `R.raw.metronomeclick`

## Current Implementation Status

**Phase**: Early development - basic structure in place

Implemented:
- Hilt setup with `@HiltAndroidApp` on `MetronomeApplication`
- Basic Compose UI scaffolding in `MainActivity`
- `SettingsModel` with BPM (40-200), volume (0-100), and sound enum
- Empty `MetronomeAudioPlayer` class skeleton

Not yet implemented:
- DataStore repository for settings persistence
- Audio playback logic in `MetronomeAudioPlayer`
- Background service for persistent playback
- Complete UI with proper BPM/volume controls
- ViewModels and proper MVVM structure

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
Consider using `ScheduledExecutorService` instead of coroutine delays for more precise metronome timing if accuracy issues arise. Trade-off between battery optimization and timing precision.

### Battery Optimization
- Efficient service lifecycle management required
- Wake locks only when necessary
- Balance timer precision vs battery consumption
