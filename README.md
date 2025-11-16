# Running Metronome

A simple Android app that plays a metronome beat in the background while you listen to music or podcasts during your runs.

## Overview

This is a single-screen Android app designed to help runners maintain a consistent cadence. The metronome plays alongside other audio apps without interrupting your music or podcasts, giving you rhythmic guidance for your running pace.

## Features

### MVP (Version 1.0)
- **Adjustable BPM**: Set your desired beats per minute (typical running cadence: 160-180 BPM)
- **Independent Volume Control**: Adjust metronome volume separately from system/media volume
- **Sound Options**: Choose between 3 different metronome sounds:
  - Classic (traditional metronome beep)
  - Wood Block (percussive, organic sound)
  - Click (subtle, minimal)
- **Persistent Settings**: App remembers your BPM, volume, and sound preference between sessions
- **Background Playback**: Continues playing when screen is locked or when using other apps

### Future Enhancements
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

**Current Status**: 📋 Specification Complete - Ready for Development

**Next Steps**: Set up Android Studio project and begin Phase 1 implementation

## License

[Add your license here]
