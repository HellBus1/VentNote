#!/usr/bin/env bash
# ==============================================================================
# VentNote Database Seeder Helper
# ==============================================================================
# Seeds the connected device or emulator with 7 realistic notes, 12 categories,
# and 16 associations via the instrumentation runner.
# ==============================================================================

set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "$DIR"

if [ -d "/Users/syubbanfakhriya/.sdkman/candidates/java/current" ]; then
    export JAVA_HOME="/Users/syubbanfakhriya/.sdkman/candidates/java/current"
elif [ -d "/Applications/Android Studio.app/Contents/jbr/Contents/Home" ]; then
    export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
fi
export PATH="$JAVA_HOME/bin:$PATH"

echo "🌱 Seeding VentNote database with 7 realistic notes on connected device..."

./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.digiventure.ventnote.DatabaseSeederTest

echo "✅ Seeding complete! 7 notes, 12 categories, and 16 associations inserted."
