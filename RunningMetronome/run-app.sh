#!/bin/bash

# Helper script to build, install, and launch the Running Metronome app on an emulator/device

set -e  # Exit on error

echo "🔍 Checking for connected devices..."
DEVICES=$(adb devices | grep -v "List of devices" | grep device | wc -l)

if [ "$DEVICES" -eq 0 ]; then
    echo "❌ No devices or emulators connected!"
    echo "Please start an emulator or connect a device first."
    echo ""
    echo "To list available emulators:"
    echo "  emulator -list-avds"
    echo ""
    echo "To start an emulator:"
    echo "  emulator -avd <emulator-name> &"
    exit 1
fi

echo "✅ Found $DEVICES connected device(s)"
echo ""

echo "🔨 Building and installing app..."
./gradlew installDebug

echo ""
echo "🚀 Launching app..."
adb shell am start -n com.electricbiro.runningmetronome/.MainActivity

echo ""
echo "✅ App launched successfully!"
echo ""
echo "To view logs:"
echo "  adb logcat | grep -i metronome"
