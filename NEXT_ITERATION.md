# Next Iteration

## Current State (May 2026)

The app is a solid MVP with onboarding, settings persistence, and a polished dark UI. It has been tested on emulator and a real device (OnePlus CPH2609, Android 16).

### Done
- [x] BPM slider (130–210) with live updates while playing
- [x] Running levels (New / Casual / Regular / Competitive) with six level-appropriate presets each
- [x] Settings persistence — BPM, volume, audio mode, and running level saved via DataStore
- [x] First-run onboarding (Welcome → Level Select → Permission) with animated transitions
- [x] Runtime POST_NOTIFICATIONS permission requested during onboarding (Android 13+)
- [x] 3-step coachmark tour on first launch after onboarding
- [x] Change level at any time via settings icon → returns to Level Select
- [x] Volume control (0–100%)
- [x] Audio mode toggle (Media / Notification)
- [x] Background foreground service with notification play/pause
- [x] Custom app icon (indigo gradient, coral pendulum)
- [x] Dark-only brand theme (Accent coral, BgBase/BgCard surface hierarchy)
- [x] Edge-to-edge layout (status bar + nav bar insets respected)
- [x] Notification back-stack fix (no duplicate MainActivity on back press)
- [x] ~90 unit tests passing

### Not done
- [ ] Release build (signing config, ProGuard rules, Play Store listing)
- [ ] Sound selector UI (six sounds in `res/raw/`, only Classic exposed)

---

## Suggested Next Steps

### 1. Release build (required for Play Store)
- Set up a keystore and signing config in `build.gradle.kts`
- Add basic ProGuard rules for Hilt, Compose, and Kotlin serialisation
- Test a release APK on a real device before upload
- Create Play Store listing (screenshots, description, privacy policy)

### 2. Sound selector
Six sounds are already in `res/raw/` and `MetronomeSoundEnum` defines them all. A small chip row below the audio mode section would expose them. The service `setSound()` method already works — it's purely a UI gap.

---

## Longer-term Ideas

### UX / Accessibility
- **Lock screen controls** — adjust BPM without unlocking (home screen widget or lock screen media controls)
- **Haptic pulse** — vibrate on beat as a silent alternative to audio (useful in noisy environments or for hearing-impaired users)

### Cadence detection
- **Detect mode** — use the accelerometer to measure the user's current step rate and display it as BPM; a "Lock" button snaps that cadence in as the active BPM

### Interval training
- **Interval mode** — auto-switch between a work BPM and a rest BPM on a configurable timer (e.g. 3 min at Race, 2 min at Easy, × 6 repeats)
- **Garmin / Health Connect integration** — receive current workout phase from a watch and switch BPM automatically

### Distribution
- Google Play Store listing
- Garmin Connect IQ companion app
