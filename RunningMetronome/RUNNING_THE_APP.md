# Running the App

This guide explains how to run the Running Metronome app on an emulator or physical device.

## Quick Start

### Option 1: Use the Helper Script (Recommended)

```bash
./run-app.sh
```

This script will:
1. Check for connected devices/emulators
2. Build and install the debug APK
3. Launch the app automatically

### Option 2: Manual Commands

**Build and Install:**
```bash
./gradlew installDebug
```

**Launch the App:**
```bash
adb shell am start -n com.electricbiro.runningmetronome/.MainActivity
```

**Combined (Build, Install, and Launch):**
```bash
./gradlew installDebug && adb shell am start -n com.electricbiro.runningmetronome/.MainActivity
```

## Setting Up Your Environment

### 1. Check for Connected Devices

```bash
adb devices
```

You should see output like:
```
List of devices attached
emulator-5554    device
```

If no devices are listed, continue to the next section.

### 2. Start an Emulator

**List Available Emulators:**
```bash
emulator -list-avds
```

**Start an Emulator:**
```bash
emulator -avd <emulator-name> &
```

For example:
```bash
emulator -avd Pixel_Fold_API_35 &
```

**Wait for the emulator to fully boot** (this can take 30-60 seconds), then verify it's connected:
```bash
adb devices
```

### 3. Install on a Physical Device

1. Enable **Developer Options** on your Android device
2. Enable **USB Debugging** in Developer Options
3. Connect your device via USB
4. Verify connection:
   ```bash
   adb devices
   ```
5. If you see "unauthorized", check your device screen and authorize the computer

## Available Gradle Tasks

### Build Tasks

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Build all variants
./gradlew build

# Clean build
./gradlew clean build
```

### Install Tasks

```bash
# Install debug build
./gradlew installDebug

# Install release build (requires signing config)
./gradlew installRelease

# Uninstall debug build
./gradlew uninstallDebug

# Uninstall all versions
./gradlew uninstallAll
```

### Test Tasks

```bash
# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device)
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests "com.electricbiro.runningmetronome.audio.MetronomeAudioPlayerTest"
```

## Viewing Logs

### View All App Logs

```bash
adb logcat | grep -i runningmetronome
```

### View Metronome-Specific Logs

```bash
adb logcat | grep -i metronome
```

### View Crash Logs

```bash
adb logcat | grep -E "AndroidRuntime|FATAL"
```

### Clear Logcat Buffer

```bash
adb logcat -c
```

## Troubleshooting

### "No devices found"

**Problem:** `adb devices` shows no devices

**Solutions:**
1. Make sure an emulator is running: `emulator -list-avds` then `emulator -avd <name> &`
2. For physical devices, check USB connection and enable USB debugging
3. Restart adb server: `adb kill-server && adb start-server`

### "INSTALL_FAILED_UPDATE_INCOMPATIBLE"

**Problem:** Installation fails due to signature mismatch

**Solution:**
```bash
./gradlew uninstallAll
./gradlew installDebug
```

### "Activity not started, unable to resolve Intent"

**Problem:** Wrong package name or activity name

**Solution:** Verify the correct command:
```bash
adb shell am start -n com.electricbiro.runningmetronome/.MainActivity
```

### App Crashes on Launch

**Solution:** Check logs:
```bash
adb logcat | grep -E "AndroidRuntime|FATAL"
```

### Multiple Devices Connected

**Problem:** You have multiple devices/emulators and want to target a specific one

**Solution:** Use `-s` flag with device serial:
```bash
# List devices with serials
adb devices

# Install on specific device
adb -s emulator-5554 install app/build/outputs/apk/debug/app-debug.apk

# Launch on specific device
adb -s emulator-5554 shell am start -n com.electricbiro.runningmetronome/.MainActivity
```

Or use environment variable:
```bash
export ANDROID_SERIAL=emulator-5554
./gradlew installDebug
```

## Android Studio Alternative

If you prefer using Android Studio:

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Click the **Run** button (green play icon) or press `Shift + F10`
4. Select your target device/emulator
5. Click **OK**

Android Studio will automatically:
- Build the app
- Install it on the selected device
- Launch the app
- Attach the debugger

## Common Development Workflows

### Quick Test Cycle

```bash
# Build, install, and launch in one command
./gradlew installDebug && adb shell am start -n com.electricbiro.runningmetronome/.MainActivity

# Or use the helper script
./run-app.sh
```

### Testing on Multiple Devices

```bash
# Install on all connected devices
./gradlew installDebug

# Launch on each device manually
adb -s device1 shell am start -n com.electricbiro.runningmetronome/.MainActivity
adb -s device2 shell am start -n com.electricbiro.runningmetronome/.MainActivity
```

### Clean Reinstall

```bash
# Uninstall, clean, build, and reinstall
./gradlew uninstallAll clean installDebug
```

### Monitor App While Testing

```bash
# Open logs in one terminal
adb logcat | grep -i metronome

# In another terminal, run the app
./run-app.sh
```

## Performance Notes

- **First build** may take 1-2 minutes
- **Incremental builds** typically take 5-15 seconds
- **Clean builds** take 20-40 seconds
- **Install time** is usually < 5 seconds
- **App launch** is instant on emulator, may vary on physical devices

## Additional Resources

- [Android Debug Bridge (adb) Documentation](https://developer.android.com/tools/adb)
- [Gradle Plugin User Guide](https://developer.android.com/build)
- [Run apps on the Android Emulator](https://developer.android.com/studio/run/emulator)
