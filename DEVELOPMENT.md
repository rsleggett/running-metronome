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

### Phase 1: Project Setup
- [ ] Create Android Studio project with Compose template
- [ ] Configure Gradle with version catalogs
- [ ] Set up Hilt dependency injection
- [ ] Create basic project structure
- [ ] Add audio files to res/raw/

### Phase 2: Data Layer
- [ ] Create Settings data model (BPM, volume, sound type)
- [ ] Implement DataStore for preferences
- [ ] Create SettingsRepository

### Phase 3: Audio Engine
- [ ] Implement MetronomePlayer class using MediaPlayer/AudioTrack
- [ ] Support for loading different sound files
- [ ] Independent volume control implementation
- [ ] BPM-based timing logic with coroutines

### Phase 4: Background Service
- [ ] Create Foreground Service for background playback
- [ ] Implement notification controls
- [ ] Handle audio focus (duck music when metronome plays)

### Phase 5: UI Development
- [ ] Design Material 3 theme
- [ ] Build main screen UI with Compose:
  - BPM slider/picker
  - Volume control
  - Sound selector
  - Play/Pause button
- [ ] Implement ViewModel with StateFlow
- [ ] Connect UI to audio engine

### Phase 6: Testing & Polish
- [ ] Unit tests for ViewModel and use cases
- [ ] UI tests with Compose Testing
- [ ] Test background playback scenarios
- [ ] Test with actual music/podcast apps
- [ ] Icon and launcher setup
- [ ] Performance optimization

## Key Technical Considerations

### Background Audio Playback
- Use Foreground Service with ongoing notification (required for Android 8+)
- Request `FOREGROUND_SERVICE` permission
- Handle audio focus to mix with other apps (not duck completely)

### Volume Control
- Use separate audio stream (USAGE_ASSISTANCE_SONIFICATION or custom)
- Don't use STREAM_MUSIC (conflicts with media apps)
- Implement app-level volume control

### BPM Timing
- Use coroutines with delay for precise timing
- Consider using ScheduledExecutorService for more precise timing
- Account for audio latency

### Battery Optimization
- Efficient service lifecycle management
- Wake locks only when necessary
- Optimize timer precision vs battery trade-off

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

# Install on connected device
./gradlew installDebug

# Generate APK
./gradlew assembleRelease
```
