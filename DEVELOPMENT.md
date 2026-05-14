# Development Guide

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **DI**: Hilt
- **Audio**: SoundPool (low-latency, mixes with other apps)
- **Async**: Kotlin Coroutines + StateFlow
- **Build**: Gradle Kotlin DSL with Version Catalogs
- **Min SDK**: Android 8.0 (API 26) | **Target SDK**: API 36

## Project Structure

```
RunningMetronome/app/src/main/java/com/electricbiro/runningmetronome/
├── MainActivity.kt          # Single activity — Compose host + service binding
├── audio/
│   └── MetronomeAudioPlayer.kt   # SoundPool engine, BPM timing coroutine
├── data/model/
│   └── SettingsModel.kt     # MetronomeSoundEnum, AudioUsageType
├── di/
│   └── AppModule.kt         # Hilt singleton providers
├── service/
│   └── MetronomeService.kt  # Foreground service, notification, StateFlow state
└── ui/
    ├── theme/               # Material 3 theme
    └── viewmodel/
        └── MetronomeViewModel.kt  # UI state (StateFlow), delegates to service
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

See [RUNNING_THE_APP.md](RunningMetronome/RUNNING_THE_APP.md) for emulator setup and troubleshooting.

## Architecture Notes

### Service is the source of truth

`MetronomeService` holds the authoritative state (`isPlaying`, `bpm`, `volume`, `sound`, `audioUsageType`) as `StateFlow` properties. When `MainActivity` binds to the service, `MetronomeViewModel.bindService()` reads all state FROM the service — it does not push ViewModel defaults to the service. This is important: if the app is opened from the notification while the service is already playing, the ViewModel immediately reflects the correct `isPlaying = true` state.

### BPM timing

The playback coroutine in `MetronomeAudioPlayer` re-reads `currentBpm` on every beat (`delay((60000.0 / currentBpm).toLong())`). Changes take effect on the next beat (up to one full interval lag, max ~1.5s at 40 BPM — acceptable for a running cadence app).

### Audio modes

Two `AudioAttributes` configurations are user-selectable:
- **Media** (`USAGE_MEDIA`): plays through media volume, works even on vibrate/silent — recommended for running
- **Notification** (`USAGE_ASSISTANCE_SONIFICATION`): respects hardware mute switch, uses notification volume

Switching audio mode requires recreating the `SoundPool`, which `MetronomeAudioPlayer.setAudioUsageType()` handles by pausing playback, releasing the old pool, creating a new one, then resuming if applicable.

### Notification

The foreground notification shows the current BPM and sound name, with a single play/pause action button. It uses `MediaStyle`. The small icon is `ic_notification` (music note vector). Action button icons are `ic_play` / `ic_pause`. Request codes for `PendingIntent` are distinct (1 = play/pause) to avoid `FLAG_UPDATE_CURRENT` collisions.

## Testing

Unit tests live in `app/src/test/`:
- `MetronomeViewModelTest` — ~22 tests covering state management, bind-time sync from service, and togglePlayPause behaviour
- `MetronomeAudioPlayerTest` — ~32 tests using Robolectric

```bash
./gradlew test
./gradlew test --tests "com.electricbiro.runningmetronome.ui.viewmodel.MetronomeViewModelTest"
```

## Key Decisions & Known Issues

| Area | Decision / Issue |
|------|-----------------|
| Settings persistence | Not yet implemented — app resets to defaults (BPM 175, volume 75%, CLASSIC, MEDIA) on each launch. DataStore is the planned solution. |
| Notification permission | `POST_NOTIFICATIONS` is declared in the manifest but not requested at runtime. On Android 13+ users must grant it manually. |
| Sound selection | Six sounds are in `res/raw/` and `MetronomeSoundEnum` only has `CLASSIC`. The UI sound selector is present in code but hidden from the current screen layout. |
| Instrumented tests | Test files exist in `src/androidTest/` but have not been run recently and may be out of date. |
| KAPT | Hilt uses KAPT (with a Kotlin 2.0 fallback warning). Migrate to KSP when Hilt support is stable. |
