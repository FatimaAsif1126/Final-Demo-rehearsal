@echo off
setlocal enabledelayedexpansion

echo ==========================================================
echo    Pure Java Build, Test ^& Coverage (No Maven/Gradle)
echo ==========================================================

set ROOT=%~dp0
set LIB_DIR=%ROOT%lib
set OUT_DIR=%ROOT%out
set CLASSES_DIR=%OUT_DIR%\classes
set TEST_CLASSES_DIR=%OUT_DIR%\test-classes
set REPORTS_DIR=%OUT_DIR%\reports
set JACOCO_DIR=%REPORTS_DIR%\jacoco
set JACOCO_XML=%JACOCO_DIR%\jacoco.xml
set JACOCO_HTML=%JACOCO_DIR%\html
set JACOCO_EXEC=%REPORTS_DIR%\jacoco.exec

set JUNIT_JAR=%LIB_DIR%\junit-platform-console-standalone.jar
set JACOCO_AGENT=%LIB_DIR%\jacocoagent.jar
set JACOCO_CLI=%LIB_DIR%\jacococli.jar

if not exist "%LIB_DIR%" mkdir "%LIB_DIR%"

if not exist "%JUNIT_JAR%" (
    echo [1/5] Downloading JUnit 5 Console Standalone...
    curl -sLo "%JUNIT_JAR%" https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar
)

if not exist "%JACOCO_AGENT%" (
    echo [1/5] Downloading JaCoCo tools...
    curl -sLo "%LIB_DIR%\jacoco.zip" https://repo1.maven.org/maven2/org/jacoco/jacoco/0.8.12/jacoco-0.8.12.zip
    powershell -Command "Expand-Archive -Path '%LIB_DIR%\jacoco.zip' -DestinationPath '%LIB_DIR%\temp_jacoco' -Force; Copy-Item '%LIB_DIR%\temp_jacoco\lib\jacocoagent.jar' '%LIB_DIR%\jacocoagent.jar'; Copy-Item '%LIB_DIR%\temp_jacoco\lib\jacococli.jar' '%LIB_DIR%\jacococli.jar'; Remove-Item -Recurse -Force '%LIB_DIR%\temp_jacoco'; Remove-Item '%LIB_DIR%\jacoco.zip'"
)

if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
mkdir "%CLASSES_DIR%"
mkdir "%TEST_CLASSES_DIR%"
mkdir "%JACOCO_DIR%"
mkdir "%REPORTS_DIR%\junit"

echo [2/5] Compiling main Java sources (Java 17 target)...
dir /s /b "%ROOT%src\main\java\*.java" > "%OUT_DIR%\sources_main.txt"
javac --release 17 -d "%CLASSES_DIR%" @"%OUT_DIR%\sources_main.txt"
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Main Java compilation failed.
    exit /b %ERRORLEVEL%
)

echo [3/5] Compiling test Java sources (Java 17 target)...
dir /s /b "%ROOT%src\test\java\*.java" > "%OUT_DIR%\sources_test.txt"
javac --release 17 -cp "%CLASSES_DIR%;%LIB_DIR%\*" -d "%TEST_CLASSES_DIR%" @"%OUT_DIR%\sources_test.txt"
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Test Java compilation failed.
    exit /b %ERRORLEVEL%
)

echo [4/5] Executing JUnit 5 tests with JaCoCo...
java -javaagent:"%JACOCO_AGENT%=destfile=%JACOCO_EXEC%,includes=com.example.sqe.*" -jar "%JUNIT_JAR%" execute --class-path "%CLASSES_DIR%" --class-path "%TEST_CLASSES_DIR%" --scan-class-path --reports-dir="%REPORTS_DIR%\junit"

echo [5/5] Generating JaCoCo coverage report...
java -jar "%JACOCO_CLI%" report "%JACOCO_EXEC%" --classfiles "%CLASSES_DIR%" --sourcefiles "%ROOT%src\main\java" --xml "%JACOCO_XML%" --html "%JACOCO_HTML%"

echo ----------------------------------------------------------
echo  Build ^& Tests Completed Successfully!
echo  JaCoCo XML Report: %JACOCO_XML%
echo  JaCoCo HTML Report: %JACOCO_HTML%\index.html
echo ----------------------------------------------------------
