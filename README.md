# Running Metronome (Cadence)

An Android app that plays a metronome beat alongside your music or podcasts to help you hold a consistent running cadence.

## Features

- **First-run onboarding** — choose your running level (New / Casual / Regular / Competitive) to get six level-appropriate BPM presets tailored to your pace
- **Level presets** — one-tap BPM chips tuned to your level (e.g. Competitive: Recovery 170 → Sprint 200); change level any time via the settings icon
- **Adjustable BPM** — 130–210 range, large display, slider, and active preset tracking
- **Volume control** — independent metronome volume (0–100%), separate from system volume
- **Two audio modes**
  - **Media** (recommended for running): always plays, uses media volume — works even on silent/vibrate
  - **Notification**: respects hardware mute switch, uses notification volume
- **Background playback** — foreground service with a persistent notification; play/pause control without opening the app
- **Settings persistence** — BPM, volume, audio mode, and running level are saved across restarts via DataStore
- **Notification permission** — requested in-app during onboarding with a clear explanation

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material Design 3 (dark-only theme)
- **Architecture**: MVVM with Hilt dependency injection
- **Audio**: SoundPool for low-latency playback
- **Persistence**: Jetpack DataStore (Preferences)
- **Async**: Kotlin Coroutines + StateFlow
- **Build**: Gradle Kotlin DSL with Version Catalogs
- **Min SDK**: Android 8.0 (API 26) · **Target SDK**: API 36

## Development

See [DEVELOPMENT.md](DEVELOPMENT.md) for setup, architecture details, and how to run the app.

## Status

**Current Status**: ✅ Working MVP — tested on emulator (Pixel 9 Pro, API 36) and real device (OnePlus CPH2609, Android 16)

### What's implemented
- Core metronome with live BPM changes (takes effect on next beat)
- First-run onboarding (Welcome → Level Select → Permission) with animated transitions
- Running level system — 4 levels × 6 presets each, persisted via DataStore
- Settings persistence for BPM, volume, audio mode, and running level
- Runtime notification permission request (Android 13+) wired into onboarding
- Redesigned main screen — dark theme, large BPM ring with beat-pulse animation, 2×3 preset grid
- Settings icon on main screen re-opens Level Select to change presets
- 3-step coachmark tour on first launch after onboarding
- Background playback with foreground service and notification play/pause
- Custom app icon (indigo gradient, coral pendulum)
- ~90 unit tests passing

### Not yet implemented
- Release build configuration (signing, ProGuard)
- Google Play Store listing
- Sound selector UI (six sounds in `res/raw/`, Classic only exposed in the UI)
- Lock screen / home screen widget controls

## License

[Add your license here]
