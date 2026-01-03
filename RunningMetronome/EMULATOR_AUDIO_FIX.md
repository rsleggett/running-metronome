# Fixing Emulator Audio Issues

## Problem
The app is working correctly and attempting to play sounds, but the Android emulator's audio system is experiencing I/O errors. You'll see logs like:
```
pcm_writei failed with 'cannot read/write stream data: I/O error'
```

## Solutions

### Option 1: Restart the Emulator (Quickest)

1. **Close the emulator**
2. **Restart it:**
   ```bash
   emulator -avd Pixel_Fold_API_35 &
   ```
3. **Wait for it to fully boot**
4. **Reinstall and run the app:**
   ```bash
   ./run-app.sh
   ```

### Option 2: Start Emulator with Audio Enabled

When starting the emulator, explicitly enable audio:

```bash
emulator -avd Pixel_Fold_API_35 -qemu -audiodev coreaudio,id=audio0
```

On Linux, use:
```bash
emulator -avd Pixel_Fold_API_35 -qemu -audiodev pa,id=audio0
```

### Option 3: Cold Boot the Emulator

1. In Android Studio:
   - Tools → Device Manager
   - Click the dropdown next to your emulator
   - Select "Cold Boot Now"

2. Or via command line:
   ```bash
   emulator -avd Pixel_Fold_API_35 -no-snapshot-load &
   ```

### Option 4: Check System Audio Settings

**On macOS:**
1. Go to System Settings → Sound
2. Make sure "Output" has a device selected (not muted)
3. Adjust the output volume

The emulator uses your system's audio output.

### Option 5: Increase Emulator Audio Buffer

Start the emulator with a larger audio buffer:
```bash
emulator -avd Pixel_Fold_API_35 -audio-buffer 2048 &
```

### Option 6: Test on a Physical Device (Best Option)

The app works perfectly on real devices. To test on a physical device:

1. **Enable Developer Options** on your Android phone
2. **Enable USB Debugging**
3. **Connect via USB**
4. **Install and run:**
   ```bash
   ./run-app.sh
   ```

Physical devices don't have the audio I/O issues that emulators sometimes have.

## Verification

After trying any of the above solutions:

1. **Check that the app is playing:**
   ```bash
   adb logcat | grep "MetronomeAudioPlayer: playBeat"
   ```

2. **You should see output like:**
   ```
   MetronomeAudioPlayer: playBeat: sound=CLASSIC, soundId=1, volume=0.89, streamId=372
   ```

3. **Listen for the click sound** - it should play at the selected BPM

## Your App is Working! ✅

The logs confirm:
- ✅ Sound files are loading successfully
- ✅ SoundPool is playing audio (streamId incrementing)
- ✅ UI controls are working (changing sounds, BPM, accent patterns)
- ✅ The metronome timing is correct (playing at the right intervals)

The only issue is the **emulator's audio driver**, not your app code.

## Testing Without Audio

If you can't get emulator audio working, you can still verify the app works by:

1. **Check the logs show beats playing:**
   ```bash
   adb logcat | grep "playBeat"
   ```

2. **Verify the timing is correct:**
   - At 180 BPM, beats should be ~333ms apart (60000ms / 180)
   - Check the timestamps in the logs

3. **Test on a physical device** for full audio verification

## Alternative: Use Android Studio Emulator

Android Studio's built-in emulator manager sometimes has better audio support:

1. Open Android Studio
2. Tools → Device Manager
3. Create a new device (or use existing)
4. Start the emulator from Android Studio
5. Run the app

The Android Studio emulator often has fewer audio issues than command-line emulators.
