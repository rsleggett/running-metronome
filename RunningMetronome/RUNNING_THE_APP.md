# Running the App

## Quick Start

```bash
cd RunningMetronome
./run-app.sh
```

This builds, installs, and launches in one step.

## Manual Commands

```bash
# Build and install
./gradlew installDebug

# Launch
adb shell am start -n com.electricbiro.runningmetronome/.MainActivity

# Build, install, and launch
./gradlew installDebug && adb shell am start -n com.electricbiro.runningmetronome/.MainActivity
```

## Emulator Setup

```bash
# List available emulators
~/Library/Android/sdk/emulator/emulator -list-avds

# Start the emulator (currently Pixel_9_Pro)
~/Library/Android/sdk/emulator/emulator -avd Pixel_9_Pro &

# Wait for boot, then confirm
adb shell getprop sys.boot_completed   # should print 1
```

> **Audio on emulator**: If sound doesn't play, do a cold boot. In Android Studio Device Manager: Pixel_9_Pro → Cold Boot Now. Hot/warm boots sometimes skip audio initialisation.

## Physical Device

1. Enable **Developer Options** → **USB Debugging**
2. Connect via USB and accept the authorization prompt
3. `adb devices` — device should show as `device` (not `unauthorized`)
4. Run `./gradlew installDebug` as normal

If you have multiple devices connected, target a specific one:
```bash
export ANDROID_SERIAL=<serial-from-adb-devices>
./gradlew installDebug
```

## Logs

```bash
# Metronome-specific logs
adb logcat | grep -i metronome

# Crash logs
adb logcat | grep -E "AndroidRuntime|FATAL"

# Clear buffer
adb logcat -c
```

## Tests

```bash
# All unit tests
./gradlew test

# Specific class
./gradlew test --tests "com.electricbiro.runningmetronome.ui.viewmodel.MetronomeViewModelTest"

# Instrumented tests (requires connected device)
./gradlew connectedAndroidTest
```

## Troubleshooting

**No devices found**
```bash
adb kill-server && adb start-server
```

**Install fails with signature mismatch**
```bash
./gradlew uninstallAll && ./gradlew installDebug
```

**App crashes on launch** — check logs:
```bash
adb logcat | grep -E "AndroidRuntime|FATAL"
```
