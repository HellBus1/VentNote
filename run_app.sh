#!/bin/bash
# Script to build and run the VentNote app on the connected device/emulator

echo "Building and installing debug APK..."
./gradlew installDebug

if [ $? -eq 0 ]; then
    echo "====================================="
    echo "Build successful! Launching the app..."
    adb shell am start -n com.digiventure.ventnote/.MainActivity
    echo "App launched successfully."
else
    echo "====================================="
    echo "Build failed. Please check the errors above."
    exit 1
fi
