#!/usr/bin/env bash
set -e

echo "=========================================================="
echo "   Pure Java Build, Test & Coverage (No Maven/Gradle)     "
echo "=========================================================="

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LIB_DIR="$ROOT/lib"
OUT_DIR="$ROOT/out"
CLASSES_DIR="$OUT_DIR/classes"
TEST_CLASSES_DIR="$OUT_DIR/test-classes"
REPORTS_DIR="$OUT_DIR/reports"
JACOCO_DIR="$REPORTS_DIR/jacoco"
JACOCO_XML="$JACOCO_DIR/jacoco.xml"
JACOCO_HTML="$JACOCO_DIR/html"
JACOCO_EXEC="$REPORTS_DIR/jacoco.exec"

JUNIT_JAR="$LIB_DIR/junit-platform-console-standalone.jar"
JACOCO_AGENT="$LIB_DIR/jacocoagent.jar"
JACOCO_CLI="$LIB_DIR/jacococli.jar"

mkdir -p "$LIB_DIR"

if [ ! -f "$JUNIT_JAR" ]; then
    echo "[1/5] Downloading JUnit 5 Console Standalone..."
    curl -sLo "$JUNIT_JAR" https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar
fi

if [ ! -f "$JACOCO_AGENT" ] || [ ! -f "$JACOCO_CLI" ]; then
    echo "[1/5] Downloading JaCoCo tools..."
    curl -sLo "$LIB_DIR/jacoco.zip" https://repo1.maven.org/maven2/org/jacoco/jacoco/0.8.12/jacoco-0.8.12.zip
    unzip -q "$LIB_DIR/jacoco.zip" -d "$LIB_DIR/temp_jacoco"
    cp "$LIB_DIR/temp_jacoco/lib/jacocoagent.jar" "$JACOCO_AGENT"
    cp "$LIB_DIR/temp_jacoco/lib/jacococli.jar" "$JACOCO_CLI"
    rm -rf "$LIB_DIR/temp_jacoco" "$LIB_DIR/jacoco.zip"
fi

rm -rf "$OUT_DIR"
mkdir -p "$CLASSES_DIR" "$TEST_CLASSES_DIR" "$JACOCO_DIR" "$REPORTS_DIR/junit"

echo "[2/5] Compiling main Java sources (Java 17 target)..."
find "$ROOT/src/main/java" -name "*.java" > "$OUT_DIR/sources_main.txt"
javac --release 17 -d "$CLASSES_DIR" @"$OUT_DIR/sources_main.txt"

echo "[3/5] Compiling test Java sources (Java 17 target)..."
find "$ROOT/src/test/java" -name "*.java" > "$OUT_DIR/sources_test.txt"
javac --release 17 -cp "$CLASSES_DIR:$LIB_DIR/*" -d "$TEST_CLASSES_DIR" @"$OUT_DIR/sources_test.txt"

echo "[4/5] Executing JUnit 5 tests with JaCoCo..."
java -javaagent:"$JACOCO_AGENT=destfile=$JACOCO_EXEC,includes=com.example.sqe.*" -jar "$JUNIT_JAR" \
    execute \
    --class-path "$CLASSES_DIR" \
    --class-path "$TEST_CLASSES_DIR" \
    --scan-class-path \
    --reports-dir="$REPORTS_DIR/junit"

echo "[5/5] Generating JaCoCo coverage report..."
java -jar "$JACOCO_CLI" report "$JACOCO_EXEC" \
    --classfiles "$CLASSES_DIR" \
    --sourcefiles "$ROOT/src/main/java" \
    --xml "$JACOCO_XML" \
    --html "$JACOCO_HTML"

echo "----------------------------------------------------------"
echo " Build & Tests Completed Successfully!"
echo " JaCoCo XML Report: $JACOCO_XML"
echo " JaCoCo HTML Report: $JACOCO_HTML/index.html"
echo "----------------------------------------------------------"
