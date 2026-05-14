# Next Iteration

## Current State (May 2026)

The app is feature-complete for everyday use. All core functionality works, settings persist between sessions, and the UI is polished. The remaining gap before a Play Store release is the signing/build configuration.

### Done
- [x] BPM slider (130–210) with live updates while playing
- [x] Named running presets (Easy 160, Tempo 170, Race 175, Speed 180, Fast 185, Sprint 195) in 3×3 grid
- [x] Volume control (0–100%)
- [x] Audio mode toggle (Media / Notification)
- [x] Background foreground service with notification play/pause
- [x] Custom app icon (indigo + coral metronome)
- [x] Brand colour scheme (indigo/coral, light + dark mode)
- [x] Settings persistence (BPM, volume, audio mode via DataStore)
- [x] Runtime POST_NOTIFICATIONS permission request (Android 13+)
- [x] Notification back-stack fix (no duplicate MainActivity on back press)
- [x] ~54 unit tests passing

### Not done
- [ ] **Release build** — signing config, ProGuard rules, Play Store listing

---

## Suggested Next Steps

### 1. Release build
- Set up a keystore and signing config in `build.gradle.kts`
- Add basic ProGuard rules for Hilt and Compose
- Test a release APK on a real device
- Create Play Store listing (screenshots, description, privacy policy)

---

## Longer-term Ideas
- Tap tempo (tap to set BPM)
- Visual pulse indicator (pulsing circle on beat)
- Interval mode (auto-change BPM after N minutes)
- Garmin / Health Connect integration
