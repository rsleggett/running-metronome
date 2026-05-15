# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**RunTick** is an Android app that plays a metronome beat in the background while users listen to music or podcasts during runs. It helps runners maintain a consistent cadence (130–210 BPM range; typical running cadence 160–195 BPM).

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

## Running the App

**Quick Start (Recommended):**
```bash
cd RunningMetronome
./run-app.sh
```

**Manual Commands:**
```bash
./gradlew installDebug
adb shell am start -n com.electricbiro.runningmetronome/.MainActivity

# Or combined:
./gradlew installDebug && adb shell am start -n com.electricbiro.runningmetronome/.MainActivity
```

**Check Connected Devices:**
```bash
adb devices
```

**Start an Emulator:**
```bash
emulator -list-avds
emulator -avd Pixel_9_Pro &
```

**View Logs:**
```bash
adb logcat | grep -i metronome
adb logcat | grep -E "AndroidRuntime|FATAL"
```

## Architecture

MVVM with Hilt DI and Jetpack Compose UI. Single activity (`MainActivity`).

### Actual Project Structure

```
RunningMetronome/app/src/main/java/com/electricbiro/runningmetronome/
├── MainActivity.kt             # Activity + Compose host + service binding + AppRoot routing
├── audio/
│   └── MetronomeAudioPlayer.kt # SoundPool engine, BPM timing coroutine
├── data/
│   ├── model/
│   │   ├── SettingsModel.kt    # MetronomeSoundEnum, AudioUsageType
│   │   └── RunningLevel.kt     # RunningLevel enum (NEW/CASUAL/REGULAR/COMPETITIVE), Preset class
│   └── repository/
│       └── SettingsRepository.kt  # DataStore: bpm, volume, audioUsageType, runningLevel, onboardingComplete
├── di/
│   └── AppModule.kt            # Hilt providers: DataStore, SettingsRepository
├── service/
│   └── MetronomeService.kt     # Foreground service, notification, StateFlow state
└── ui/
    ├── onboarding/
    │   └── OnboardingScreen.kt # WelcomeScreen, LevelSelectScreen, PermissionScreen + shared components
    ├── theme/
    │   ├── Color.kt            # Design tokens (Accent, BgBase/BgCard/BgElev, TextPrimary/Mute/Dim, etc.)
    │   ├── Theme.kt            # Dark-only darkColorScheme — no light theme
    │   └── Type.kt             # Typography scale (displayLarge 96sp BPM down to labelSmall 11sp)
    └── viewmodel/
        ├── MetronomeViewModel.kt   # MetronomeUiState, presets, activePresetId, delegates to service
        └── OnboardingViewModel.kt  # OnboardingUiState, step state machine, tour steps, resetOnboarding
```

### Key Architecture Points

1. **Hilt DI**: `@HiltAndroidApp` on Application, `@AndroidEntryPoint` on MainActivity, `@HiltViewModel` on both ViewModels
2. **Version Catalog**: Dependencies in `gradle/libs.versions.toml`
3. **Jetpack Compose**: Full Compose UI — no XML layouts
4. **Dark-only theme**: No light theme. `RunningMetronomeTheme` always uses `darkColorScheme`
5. **Edge-to-edge**: `enableEdgeToEdge()` in `onCreate`. All screens apply `statusBarsPadding()` and `navigationBarsPadding()`

### Routing

`AppRoot` composable routes based on `OnboardingUiState`:
- `isLoading = true` → blank BgBase splash (DataStore loading)
- `isComplete = false, step != APP` → `OnboardingScreen`
- `step == APP, tourStep in 0..2` → `MainScreen` + `CoachmarkTour` overlay
- `isComplete = true` → `MainScreen` (no tour)

The settings cog on the main screen calls `OnboardingViewModel.resetOnboarding()` → routes user back to Level Select to change their running level.

### Service is Source of Truth

`MetronomeService` holds authoritative playback state as `StateFlow`. `MetronomeViewModel.bindService()` reads all state FROM the service. Persisted settings are applied via `applyPersistedSettings()` on bind — not pushed from the ViewModel directly.

### Settings Persistence

`SettingsRepository` uses DataStore Preferences to persist: `bpm`, `volume`, `audioUsageType`, `runningLevel`, `onboardingComplete`. The ViewModel stashes `pendingSettings` in `init` so they're available whether DataStore or service binding finishes first.

## Audio Implementation

- `SoundPool` for low-latency playback alongside other audio (no ducking)
- BPM range: 130–210; live changes take effect on the next beat
- Two user-selectable audio modes:
  - **Media** (`USAGE_MEDIA`): always plays, media volume — recommended for running
  - **Notification** (`USAGE_ASSISTANCE_SONIFICATION`): respects mute switch, notification volume
- App-level volume 0–100%
- Switching audio mode recreates the SoundPool

## Sound Files

Six metronome sounds in `app/src/main/res/raw/`:
- `metronomeclick.mp3` — Classic (the only one currently exposed in the UI)
- `metronomesnare.mp3`, `metronomeknock.mp3`, `metronomedrumtr707.mp3`, `metronomedrumtr808.mp3`, `metronomedrumtr909.mp3`

`MetronomeSoundEnum` defines all six. A sound-selector UI is not yet implemented.

## Current Implementation Status

**Phase**: Working MVP (May 2026) — tested on Pixel 9 Pro emulator and OnePlus CPH2609 (Android 16)

### ✅ Implemented

- **Onboarding**: 3-screen first-run flow (Welcome → Level Select → Permission) with animated slide+fade transitions, `InfiniteTransition` pulsing rings on welcome, and `animateDpAsState` progress dots
- **Running levels**: `RunningLevel` enum — NEW / CASUAL / REGULAR / COMPETITIVE, each with 6 `Preset(id, label, bpm)` entries tailored to that runner profile
- **Settings persistence**: DataStore persists BPM, volume, audio mode, running level, and onboarding flag across app restarts
- **Runtime notification permission**: Requested in `PermissionScreen` via `ActivityResultContracts.RequestPermission` (Android 13+ guard)
- **Coachmark tour**: 3-step overlay on first main-screen open after onboarding (presets → tempo slider → play button), positioned using `LocalDensity` pixel-to-dp conversion
- **Change level**: Settings cog on main screen calls `resetOnboarding()` → navigates back to Level Select
- **Main screen**: Dark theme, 160dp BPM ring with beat-pulse animation (`animateFloatAsState` + `LaunchedEffect` coroutine), 2×3 preset chip grid, TEMPO slider, circular play button, VOLUME slider, AUDIO MODE cards
- **Background service**: Foreground service with MediaStyle notification, play/pause action, `ic_notification` icon; tapping notification brings existing activity to foreground (no duplicate back-stack)
- **MVVM**: `MetronomeViewModel` + `OnboardingViewModel` both `@HiltViewModel`
- **Custom app icon**: Indigo gradient background, white metronome body, coral pendulum
- **Testing**: ~90 unit tests passing
  - `MetronomeAudioPlayerTest`: ~32 tests (Robolectric)
  - `MetronomeViewModelTest`: ~30 tests (state sync, presets, activePresetId)
  - `OnboardingViewModelTest`: ~22 tests (navigation, tour, resetOnboarding)
  - `RunningLevelTest`: ~12 tests (fromId, preset invariants)

### ✅ Verified Working (May 2026)

- Full onboarding flow end-to-end on emulator and real device
- Presets update correctly after changing running level
- Settings (BPM, volume, level) persist across app restarts
- Beat-pulse ring animation while playing
- Notification play/pause syncs with app UI
- Edge-to-edge layout on OnePlus virtual nav buttons and status bar

### 🔄 Not Yet Implemented

- Release build configuration (signing, ProGuard)
- Sound selector UI (sounds exist in res/raw — just no UI)
- Google Play Store listing

## Development Notes

### Gradle Configuration

- Kotlin DSL with version catalog (`gradle/libs.versions.toml`)
- KAPT for Hilt annotation processing (Kotlin 2.0 fallback warning is expected; migrate to KSP when Hilt support is stable)
- `kotlin-compose` plugin for Compose compiler
- Java 11 `sourceCompatibility`/`targetCompatibility`

### Testing

- Unit tests: JUnit 4 + Mockito Kotlin in `app/src/test/`
- `UnconfinedTestDispatcher` + `Dispatchers.setMain` for coroutine tests
- `runTest` required for tests that `verify` suspend functions on mocks
- Instrumented tests exist in `src/androidTest/` but are not maintained

### BPM Timing Precision

Kotlin coroutines `delay()` with `60000ms / bpm`. Adequate for running cadence. For sub-millisecond precision a `ScheduledExecutorService` would be needed, but that's not required here.

### Battery Optimization

- Service starts/stops with playback (no always-on process)
- No wake locks
- SoundPool + coroutine delay is efficient; minimal CPU between beats

### Known Issues & Solutions

- **Emulator audio**: Cold boot required if sound doesn't play on warm boot
- **Mute switch**: Use Media mode for running — Notification mode respects the mute switch and will be silent on vibrate
- **Sound loading**: SoundPool loads asynchronously; `OnLoadCompleteListener` confirms readiness before playback starts
- **Service lifecycle**: Binds in `onCreate`, unbinds in `onDestroy`; persists when app is backgrounded
