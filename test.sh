#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
OUT="$ROOT/build/tests"
mkdir -p "$OUT"
javac -source 8 -target 8 -d "$OUT" \
  "$ROOT/src/com/jeremykenedy/starfielddrift/StarfieldOptions.java" \
  "$ROOT/src/com/jeremykenedy/starfielddrift/SettingsValues.java" \
  "$ROOT/tests/StarfieldOptionsTest.java"
java -ea -cp "$OUT" com.jeremykenedy.starfielddrift.StarfieldOptionsTest
python3 -m unittest -v tests.test_installer
