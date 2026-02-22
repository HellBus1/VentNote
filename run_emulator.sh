#!/bin/bash
# Script to run the Android Emulator
# By default, it uses Pixel_8_Pro as the AVD. You can pass a different AVD name as an argument.

AVD_NAME=${1:-Pixel_8_Pro}

echo "Starting emulator with AVD: $AVD_NAME..."
~/Library/Android/sdk/emulator/emulator -avd "$AVD_NAME"
