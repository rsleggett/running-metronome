# Running Metronome

A simple Android app that plays a metronome beat in the background while you listen to music or podcasts during your runs.

## Overview

A single-screen Android app designed to help runners maintain a consistent cadence. The metronome plays alongside other audio apps without interrupting your music or podcasts, giving you rhythmic guidance for your running pace.

## Features

- **Adjustable BPM**: 40–200 BPM range with a large numeric display and slider
- **Quick Presets**: One-tap BPM presets at 160, 170, 175, 180, 185
- **Volume Control**: Independent metronome volume (0–100%), separate from system volume
- **One Sound Option**: Classic metronome click (additional sounds present in assets but UI currently exposes Classic only)
- **Two Audio Modes**:
  - **Media** (recommended for running): Always plays, uses media volume — works even on vibrate/silent
  - **Notification**: Respects device mute switch, uses notification volume
- **Background Playback**: Continues playing when screen is locked or using other apps via a foreground service with a persistent notification (play/pause control)

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with Hilt dependency injection
- **Audio**: SoundPool for low-latency playback
- **Async**: Kotlin Coroutines + StateFlow
- **Build**: Gradle Kotlin DSL with Version Catalogs
- **Min SDK**: Android 8.0 (API 26)
- **Target SDK**: API 36

## Development

See [DEVELOPMENT.md](DEVELOPMENT.md) for setup instructions and architecture details.

## Status

**Current Status**: ✅ Working MVP

### What's implemented
- Core metronome with live BPM changes (takes effect on the next beat)
- Background playback with foreground service and notification play/pause control
- Volume control and audio mode switching
- MVVM architecture with Hilt DI
- Custom app icon (indigo background, white metronome, coral pendulum)
- ~50 unit tests passing
- Settings persistence (resets to defaults on restart — DataStore not yet wired up)
- Runtime notification permission request (Android 13+)
- Named running-focused presets ("Easy Run", "Tempo Run", etc.)

### Not yet implemented
- Release build configuration (signing, ProGuard)
- Google Play Store listing

## License

[Add your license here]
