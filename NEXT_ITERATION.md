# Next Iteration Plan

## Current State Summary

**Status**: MVP Complete with Advanced Features + Comprehensive Test Suite ✅

**What Works**:
- Core metronome playback (40-200 BPM)
- 6 different sounds (Classic, Snare, Knock, TR-707, TR-808, TR-909)
- Two playback modes:
  - Simple mode with accent patterns (None, Every 2nd/3rd/4th)
  - Pattern mode with 8-step drum sequencer
- Background playback with notification controls
- Audio mixing with music/podcast apps
- Volume control (0-100%)
- Audio mode switching (Media/Notification)
- 126 automated tests (all passing)

**What's Missing**:
- Settings persistence (resets to defaults on app restart)
- Runtime notification permissions (Android 13+)
- Custom app icon
- Release build configuration
- ProGuard/R8 rules

## Iteration Options

Choose one of the following iteration priorities based on your goals:

### Option A: Polish for Production Release 🚀
**Goal**: Get the app ready for Google Play Store

**Tasks**:
1. **Settings Persistence** (High Priority)
   - Implement DataStore for saving user preferences
   - Create SettingsRepository with Flow
   - Persist: BPM, volume, sound, audio mode, playback mode, accent pattern, drum pattern
   - Load settings on app start

2. **Runtime Permissions** (High Priority - Android 13+)
   - Request POST_NOTIFICATIONS permission at runtime
   - Show rationale dialog explaining why notification permission is needed
   - Handle permission denial gracefully

3. **App Icon & Branding** (Medium Priority)
   - Design app icon (or use icon generator)
   - Create adaptive icon for Android 8+
   - Set up launcher icon in multiple densities
   - Add app icon to notification

4. **Release Build Configuration** (Medium Priority)
   - Set up signing configuration
   - Configure ProGuard/R8 rules
   - Test release build
   - Prepare for Google Play release

5. **Documentation & Polish**
   - Add in-app help/tutorial
   - Create Play Store assets (screenshots, description)
   - Write privacy policy (if needed)

**Estimated Effort**: 2-3 sessions
**Outcome**: Production-ready app for Google Play Store

---

### Option B: Simplified UX & Core Usability 🎯 **[SELECTED]**
**Goal**: Remove complexity, focus on essential running features with great UX

**Philosophy**: Less is more. Remove Pattern mode, keep Simple mode only, add practical running features.

**Tasks**:

**Phase 1: Simplification & Foundation** (Session 1 - 2-3 hours)
1. **Remove Pattern Mode Complexity**
   - Remove Pattern mode toggle from UI
   - Remove 8-step pattern editor
   - Remove PatternStep and DrumPattern from data model
   - Keep only Simple mode with accent patterns
   - Remove pattern-related tests
   - Update documentation

2. **Settings Persistence** (Critical)
   - Implement DataStore for user preferences
   - Persist: BPM, volume, sound, audio mode, accent pattern
   - Load saved settings on app start
   - Smooth migration (first launch uses defaults)

3. **Runtime Notification Permission** (Android 13+)
   - Request POST_NOTIFICATIONS at appropriate time
   - Simple permission dialog with clear explanation
   - Handle denial gracefully (app still works, just no notification controls)

**Phase 2: Preset System** (Session 2 - 2-3 hours)
4. **Named BPM Presets**
   - Replace generic BPM chips (160, 170, 175, etc.)
   - Add running-specific presets:
     - "Easy Run" - 160 BPM
     - "Tempo Run" - 170 BPM
     - "Race Pace" - 180 BPM
     - "Speed Work" - 185 BPM
   - Allow user to customize preset values
   - Save custom presets with DataStore
   - Simple preset management (add, edit, delete)

**Phase 3: Workout Integration** (Session 3 - 2-4 hours)
5. **Basic Workout Features**
   - Interval timer with BPM changes
     - Warm-up: Start at lower BPM (e.g., 160)
     - Work interval: Higher BPM (e.g., 180)
     - Recovery: Lower BPM (e.g., 165)
     - Cool-down: Lowest BPM (e.g., 155)
   - Visual countdown timer
   - Audio cue when interval changes
   - Simple workout builder (optional)
   - Save favorite workouts

6. **UI Polish**
   - Cleaner, simpler layout (one screen, no mode switching)
   - Haptic feedback on button presses
   - Visual metronome indicator (pulsing circle)
   - Better visual hierarchy
   - Smooth animations

**Phase 4: Nice-to-Have Enhancements** (Session 4+ - Optional)
7. **Quality of Life**
   - Quick start from last session
   - Favorite sound/settings
   - Visual feedback improvements
   - Better sound selection UI
   - Tap tempo (tap to set BPM)

**Estimated Effort**: 3-4 sessions (8-12 hours total)
**Outcome**: Clean, focused running metronome app with practical workout features

**What Gets Removed**:
- ❌ Pattern mode toggle
- ❌ 8-step sequencer UI
- ❌ Pattern editor
- ❌ DrumPattern/PatternStep data classes
- ❌ Pattern-related tests (44 fewer tests)
- ❌ Dual sound selection
- ❌ PlaybackMode enum (only Simple mode remains)

**What Stays**:
- ✅ Simple metronome mode
- ✅ Accent patterns (None, Every 2nd/3rd/4th)
- ✅ 6 sound options
- ✅ Volume control
- ✅ Audio mode (Media/Notification)
- ✅ Background playback
- ✅ BPM range (40-200)

**What Gets Better**:
- ✅ Settings save between sessions
- ✅ Running-focused presets
- ✅ Workout interval support
- ✅ Cleaner, simpler UI
- ✅ Faster to use

---

### Option C: Advanced Features 🔥
**Goal**: Add unique capabilities that differentiate the app

**Tasks**:
1. **Settings Persistence** (Critical - do this first)
   - Same as Option A

2. **Workout Integration**
   - Tempo changes during workout
   - Interval timer with BPM changes
   - Warm-up/cool-down presets
   - Progressive BPM increase/decrease

3. **Analytics & Tracking**
   - Track usage statistics
   - Run history
   - Cadence consistency metrics
   - Weekly/monthly reports

4. **External Integration**
   - Export workout data
   - Garmin Connect integration (future)
   - Strava integration (future)
   - Health Connect integration

5. **Advanced Audio Features**
   - Stereo panning
   - Polyrhythm support
   - Swing/groove timing
   - Custom sound upload

**Estimated Effort**: 4-5 sessions
**Outcome**: Feature-rich app with competitive advantages

---

### Option D: Testing & Quality Assurance 🧪
**Goal**: Ensure rock-solid reliability and performance

**Tasks**:
1. **Settings Persistence** (Critical - do this first)
   - Same as Option A

2. **Expand Test Coverage**
   - Run instrumented tests on real devices
   - Add edge case tests
   - Performance benchmarking
   - Memory leak detection

3. **Error Handling**
   - Comprehensive error recovery
   - Crash reporting setup (Firebase Crashlytics)
   - Logging infrastructure
   - User-friendly error messages

4. **Performance Optimization**
   - Audio latency reduction
   - Battery usage optimization
   - Memory footprint reduction
   - Startup time optimization

5. **Compatibility Testing**
   - Test on various Android versions (8.0 - 15)
   - Test on different devices (Samsung, Pixel, OnePlus, etc.)
   - Test with different audio scenarios
   - Test notification behavior across devices

**Estimated Effort**: 2-3 sessions
**Outcome**: Highly reliable, well-tested app

---

## Recommended Path: Option B - Simplified & Focused 🎯

Based on real-world testing feedback, **Option B is the selected path**.

**Session 1: Simplify & Persist** (2-3 hours)
- Remove Pattern mode complexity
- Implement DataStore for settings persistence
- Add runtime notification permissions
- Clean up codebase and tests

**Session 2: Smart Presets** (2-3 hours)
- Replace generic BPM numbers with running-focused presets
- Add preset customization
- Improve UI layout and flow

**Session 3: Workout Features** (2-4 hours)
- Add interval timer with BPM changes
- Visual countdown and audio cues
- Save favorite workouts

**Session 4+: Polish** (Optional)
- Tap tempo feature
- Visual metronome indicator
- Additional UX improvements

**Why This Path**:
- ✅ Real user feedback: Pattern mode too complex
- ✅ Focus on core running use case
- ✅ Simpler = easier to use = better retention
- ✅ Practical features runners actually need
- ✅ Cleaner codebase, easier to maintain

---

## Technical Debt to Address

1. **KAPT Warning**: Consider migrating to KSP (Kotlin Symbol Processing) when Hilt supports it
2. **Version Catalog**: Update dependencies to latest stable versions
3. **Compose Compatibility**: Already using latest Compose, keep monitoring
4. **API 36 Target**: Verify all Android 14+ features work correctly

---

## Decision Questions

Before starting the next iteration, answer these:

1. **Primary Goal**: Release to store, improve UX, add features, or ensure quality?
2. **Timeline**: How quickly do you want to ship?
3. **Target Audience**: Casual runners, serious athletes, or broad appeal?
4. **Monetization**: Free, paid, freemium, or ads?
5. **Differentiation**: What makes this app unique vs. competitors?

---

## Quick Start: Session 1 Implementation Plan

**Goal**: Simplify the app and add settings persistence

**Preparation** (5-10 minutes):
1. Commit current work to git
2. Create a new branch: `git checkout -b simplify-and-persist`
3. Review files to be modified

**Step 1: Remove Pattern Mode** (30-45 minutes)
- Remove Pattern mode UI elements from MetronomeScreen
- Remove PlaybackMode enum (keep only Simple mode)
- Remove DrumPattern and PatternStep data classes
- Update MetronomeAudioPlayer to remove pattern logic
- Update MetronomeService to remove pattern methods
- Update MetronomeViewModel to remove pattern state
- Clean up imports

**Step 2: Remove Pattern Tests** (15 minutes)
- Delete pattern-related test files
- Remove pattern tests from existing test files
- Verify remaining tests still pass: `./gradlew test`

**Step 3: Implement DataStore** (45-60 minutes)
- Create SettingsRepository with DataStore
- Add Preferences data class
- Implement save/load for: BPM, volume, sound, audio mode, accent pattern
- Inject repository into ViewModel
- Load settings on app start
- Save settings on changes

**Step 4: Runtime Permissions** (30-45 minutes)
- Add permission check in MainActivity
- Create permission request dialog
- Handle permission result
- Test on Android 13+ device/emulator

**Step 5: Test & Verify** (15-30 minutes)
- Run all tests: `./gradlew test`
- Build app: `./gradlew build`
- Test on real device:
  - Change settings
  - Close app
  - Reopen app
  - Verify settings persisted
- Test notification permission flow

**Step 6: Update Documentation** (15 minutes)
- Update CLAUDE.md
- Update DEVELOPMENT.md
- Update README.md
- Commit changes

**Total Time**: 2.5-3.5 hours
**Result**: Simplified, focused app with settings that persist
