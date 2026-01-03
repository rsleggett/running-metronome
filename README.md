# Running Metronome

A simple Android app that plays a metronome beat in the background while you listen to music or podcasts during your runs.

## Overview

This is a single-screen Android app designed to help runners maintain a consistent cadence. The metronome plays alongside other audio apps without interrupting your music or podcasts, giving you rhythmic guidance for your running pace.

## Features

### Current Version (1.0 - MVP Complete)
- **Adjustable BPM**: Set your desired beats per minute (40-200 BPM range)
  - BPM slider with large numeric display
  - Quick presets: 160, 170, 175, 180, 185 BPM
- **Independent Volume Control**: Adjust metronome volume (0-100%) separately from system volume
- **Six Sound Options**: Choose from 6 different metronome sounds:
  - Classic (traditional metronome click)
  - Snare (drum machine snare)
  - Knock (wood block sound)
  - TR-707, TR-808, TR-909 (classic drum machine sounds)
- **Two Playback Modes**:
  - **Simple Mode**: Standard metronome with optional accent patterns
    - Accent every 2nd, 3rd, or 4th beat
  - **Pattern Mode**: 8-step drum sequencer
    - Create custom rhythmic patterns
    - Mix two sounds with rest steps
- **Audio Modes**: Choose how sound plays
  - Media mode: Always plays (recommended for running)
  - Notification mode: Respects device mute switch
- **Background Playback**: Continues playing when screen is locked or using other apps
  - Foreground service with notification controls
  - Play/Pause/Stop buttons in notification
  - Works alongside music and podcast apps

### Future Enhancements
- Settings persistence (DataStore implementation)
- Runtime notification permissions for Android 13+
- Custom app icon and branding
- Garmin integration to adapt BPM to workout data
- Workout presets (warm-up, tempo run, intervals)
- Cadence analytics and tracking

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with Hilt DI
- **Build**: Gradle Kotlin DSL with Version Catalogs
- **Min SDK**: Android 8.0 (API 26)

## Development

See [DEVELOPMENT.md](DEVELOPMENT.md) for detailed setup instructions, architecture details, and implementation guide.

## Status

**Current Status**: ✅ MVP Complete with Advanced Features + Comprehensive Test Suite

**Achievements**:
- Core metronome functionality implemented and tested
- Advanced features: Pattern sequencer, accent patterns, quick presets
- 126 automated tests (82 unit tests + 44 instrumented tests)
- Background playback with notification controls
- All builds passing

**Next Steps**: See iteration plan below for priorities

## License

[Add your license here]
