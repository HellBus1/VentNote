#!/bin/bash
# Script to build the VentNote debug APK

echo "Building debug APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "====================================="
    echo "Build successful!"
    echo "APK location: app/build/outputs/apk/debug/app-debug.apk"
else
    echo "====================================="
    echo "Build failed. Please check the errors above."
    exit 1
fi
