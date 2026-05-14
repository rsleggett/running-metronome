# Next Iteration

## Current State (May 2026)

The app is a working MVP. Core metronome functionality, background playback, notification controls, and a custom icon are all done. The main gap is that nothing persists between sessions.

### Done
- [x] BPM slider (130–210) with live updates while playing
- [x] Named running presets (Easy 160, Tempo 170, Race 175, Speed 180, Fast 185)
- [x] Volume control (0–100%)
- [x] Audio mode toggle (Media / Notification)
- [x] Background foreground service with notification play/pause
- [x] Custom app icon (indigo + coral metronome)
- [x] Brand colour scheme (indigo/coral, light + dark mode)
- [x] ~50 unit tests passing

### Not done
- [ ] **Settings persistence** — biggest gap, everything resets on restart
- [ ] Runtime `POST_NOTIFICATIONS` permission request (Android 13+)
- [ ] Release build (signing config, ProGuard rules, Play Store listing)

---

## Suggested Next Steps

### 1. Settings persistence (highest value)
Use DataStore to save BPM, volume, sound, and audio mode between sessions. Users shouldn't have to reconfigure every run.

```
data/repository/SettingsRepository.kt   ← new
di/AppModule.kt                         ← wire up repository
ui/viewmodel/MetronomeViewModel.kt      ← load on init, save on change
```

### 2. Runtime notification permission
On Android 13+, POST_NOTIFICATIONS must be requested at runtime. Without it the notification (and therefore background controls) silently doesn't appear. A single `rememberPermissionState` call in `MainActivity` before starting the service is all that's needed.

### 3. Release build
- Set up a keystore and signing config in `build.gradle.kts`
- Add basic ProGuard rules for Hilt and Compose
- Test a release APK on a real device

---

## Longer-term Ideas
- Tap tempo (tap to set BPM)
- Visual pulse indicator (pulsing circle on beat)
- Interval mode (auto-change BPM after N minutes)
- Garmin / Health Connect integration
