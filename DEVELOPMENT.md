# Development Guide

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material Design 3 (dark-only theme)
- **Architecture**: MVVM (Model-View-ViewModel)
- **DI**: Hilt
- **Audio**: SoundPool (low-latency, mixes with other apps)
- **Persistence**: Jetpack DataStore (Preferences)
- **Async**: Kotlin Coroutines + StateFlow
- **Build**: Gradle Kotlin DSL with Version Catalogs
- **Min SDK**: Android 8.0 (API 26) | **Target SDK**: API 36

## Project Structure

```
RunningMetronome/app/src/main/java/com/electricbiro/runningmetronome/
├── MainActivity.kt             # Single activity — Compose host, service binding, AppRoot routing
├── audio/
│   └── MetronomeAudioPlayer.kt # SoundPool engine, BPM timing coroutine
├── data/
│   ├── model/
│   │   ├── SettingsModel.kt    # MetronomeSoundEnum, AudioUsageType
│   │   └── RunningLevel.kt     # RunningLevel enum (4 levels), Preset data class
│   └── repository/
│       └── SettingsRepository.kt  # DataStore persistence (BPM, volume, audio mode, level, onboarding flag)
├── di/
│   └── AppModule.kt            # Hilt singleton providers (DataStore, SettingsRepository)
├── service/
│   └── MetronomeService.kt     # Foreground service, notification, StateFlow state
└── ui/
    ├── onboarding/
    │   └── OnboardingScreen.kt # Welcome, LevelSelect, Permission screens + shared components
    ├── theme/
    │   ├── Color.kt            # Design tokens (Accent, BgBase, BgCard, TextPrimary, etc.)
    │   ├── Theme.kt            # Dark-only MaterialTheme
    │   └── Type.kt             # Typography scale
    └── viewmodel/
        ├── MetronomeViewModel.kt   # UI state (StateFlow), delegates to service + repository
        └── OnboardingViewModel.kt  # Onboarding step state machine, level selection, tour steps
```

## Build & Run

```bash
# Build
./gradlew build

# Run unit tests
./gradlew test

# Install debug build on connected device/emulator
./gradlew installDebug

# Build and launch (one-liner)
./gradlew installDebug && adb shell am start -n com.electricbiro.runningmetronome/.MainActivity

# Or use the helper script
cd RunningMetronome && ./run-app.sh
```

### Emulator setup

```bash
# List available emulators
~/Library/Android/sdk/emulator/emulator -list-avds

# Start (Pixel_9_Pro_API_36)
~/Library/Android/sdk/emulator/emulator -avd Pixel_9_Pro &

# Wait for boot
adb shell getprop sys.boot_completed   # 1 = ready
```

> **Audio on emulator**: If sound doesn't play, do a cold boot — in Android Studio Device Manager select the AVD → Cold Boot Now. Warm boots sometimes skip audio initialisation.

### Troubleshooting

| Problem | Fix |
|---------|-----|
| No devices found | `adb kill-server && adb start-server` |
| Install fails (signature mismatch) | `./gradlew uninstallAll && ./gradlew installDebug` |
| App crashes on launch | `adb logcat \| grep -E "AndroidRuntime\|FATAL"` |
| Multiple devices connected | `adb -s <serial> shell am start ...` or `export ANDROID_SERIAL=<serial>` |
| Onboarding shows again after reinstall | DataStore is cleared on uninstall — expected behaviour |

## Architecture Notes

### Routing: AppRoot

`AppRoot` in `MainActivity.kt` is the single routing composable. It reads `OnboardingUiState` and routes to:
- Loading splash (while DataStore is read on first frame)
- `OnboardingScreen` (first run — step is WELCOME, LEVEL_SELECT, or PERMISSION)
- `MainScreen` with coachmark tour overlay (step is APP, tourStep 0–2)
- `MainScreen` without tour (tourStep = -1, isComplete = true)

When the user taps the settings icon on the main screen, `OnboardingViewModel.resetOnboarding()` is called, setting `isComplete = false` and routing back to Level Select so they can change their running level.

### Service is the source of truth

`MetronomeService` holds authoritative playback state (`isPlaying`, `bpm`, `volume`, `sound`, `audioUsageType`) as `StateFlow` properties. When `MainActivity` binds to the service, `MetronomeViewModel.bindService()` reads all state FROM the service — it does not push ViewModel defaults to the service. This matters when the app is opened from the notification while the service is already playing: the ViewModel immediately reflects the correct `isPlaying = true` state.

### Settings persistence

`SettingsRepository` reads/writes BPM, volume, audio mode, running level, and onboarding-complete flag via DataStore Preferences. On cold start, `MetronomeViewModel.init` reads the persisted settings and stashes them as `pendingSettings`; when `bindService()` is called, `applyPersistedSettings()` pushes them to the service and updates UI state. This handles the race between DataStore reads and service binding.

### Running levels and presets

`RunningLevel` is a Kotlin enum with four entries (NEW, CASUAL, REGULAR, COMPETITIVE). Each holds a `List<Preset>` of six BPM checkpoints suited to that runner profile. The selected level is persisted in DataStore. `MetronomeViewModel.refreshPresets()` should be called after onboarding completes to load the newly-selected level's presets.

### BPM timing

The playback coroutine in `MetronomeAudioPlayer` re-reads `currentBpm` on every beat (`delay((60000.0 / currentBpm).toLong())`). Changes take effect on the next beat (up to one full interval lag, max ~460ms at 130 BPM — acceptable for a running cadence app).

### Audio modes

Two `AudioAttributes` configurations are user-selectable:
- **Media** (`USAGE_MEDIA`): plays through media volume, works even on vibrate/silent — recommended for running
- **Notification** (`USAGE_ASSISTANCE_SONIFICATION`): respects hardware mute switch, uses notification volume

Switching audio mode requires recreating the `SoundPool`, which `MetronomeAudioPlayer.setAudioUsageType()` handles by pausing playback, releasing the old pool, creating a new one, then resuming if applicable.

### Notification

The foreground notification shows the current BPM and sound name with a play/pause action button (MediaStyle). The small icon is `ic_notification`. Action icons are `ic_play` / `ic_pause`. The notification permission is requested during onboarding via `ActivityResultContracts.RequestPermission` (Android 13+ only).

### Theme

The app uses a dark-only colour scheme (`darkColorScheme`). There is no light theme. Design tokens are defined in `ui/theme/Color.kt`:
- `Accent` / `AccentSoft` / `AccentRim` — coral orange brand colour
- `BgBase` / `BgCard` / `BgElev` — surface hierarchy
- `TextPrimary` / `TextMute` / `TextDim` — text hierarchy

## Testing

Unit tests live in `app/src/test/`:

| File | Tests | Coverage |
|------|-------|----------|
| `MetronomeAudioPlayerTest` | ~32 | SoundPool engine, Robolectric |
| `MetronomeViewModelTest` | ~30 | State management, bind-time sync, presets, active preset ID |
| `OnboardingViewModelTest` | ~22 | Step navigation, level selection, tour, resetOnboarding |
| `RunningLevelTest` | ~12 | fromId, preset count/order/range/IDs |

```bash
# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "com.electricbiro.runningmetronome.ui.viewmodel.OnboardingViewModelTest"
```

## Key Decisions & Known Issues

| Area | Decision / Status |
|------|-----------------|
| Settings persistence | ✅ Implemented via DataStore — BPM, volume, audio mode, running level all persist |
| Notification permission | ✅ Requested at runtime during the Permission onboarding screen (Android 13+) |
| Sound selection | Six sounds in `res/raw/` but only `CLASSIC` is used — sound selector UI is not exposed |
| Instrumented tests | Files exist in `src/androidTest/` but are not maintained — unit tests cover the critical paths |
| KAPT | Hilt uses KAPT (with a Kotlin 2.0 fallback warning). Migrate to KSP when Hilt KSP support is stable. |
| Edge-to-edge | `enableEdgeToEdge()` is set in `onCreate`. All screens apply `statusBarsPadding()` and `navigationBarsPadding()` on their outer container. |
