#!/usr/bin/env bash
# ==============================================================================
# VentNote Database Seeder Helper
# ==============================================================================
# Seeds the connected device or emulator with 7 realistic notes, 12 categories,
# and 16 associations via the instrumentation runner.
# ==============================================================================

set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
"$DIR/seed_db.sh"
