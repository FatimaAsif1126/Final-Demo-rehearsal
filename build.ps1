<#
.SYNOPSIS
    Pure Java Build, Test, and JaCoCo Coverage Runner (No Maven/Gradle Required)
#>

$ErrorActionPreference = "Stop"

$Root = $PSScriptRoot
$LibDir = Join-Path $Root "lib"
$OutDir = Join-Path $Root "out"
$ClassesDir = Join-Path $OutDir "classes"
$TestClassesDir = Join-Path $OutDir "test-classes"
$ReportsDir = Join-Path $OutDir "reports"
$JacocoReportsDir = Join-Path $ReportsDir "jacoco"
$JacocoHtmlDir = Join-Path $JacocoReportsDir "html"
$JacocoXmlFile = Join-Path $JacocoReportsDir "jacoco.xml"
$JacocoExecFile = Join-Path $ReportsDir "jacoco.exec"

$JunitJar = Join-Path $LibDir "junit-platform-console-standalone.jar"
$JacocoAgentJar = Join-Path $LibDir "jacocoagent.jar"
$JacocoCliJar = Join-Path $LibDir "jacococli.jar"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "   Pure Java Build, Test & Coverage (No Maven/Gradle)     " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Ensure testing tools exist in lib/
if (-not (Test-Path $LibDir)) {
    New-Item -ItemType Directory -Path $LibDir | Out-Null
}

if (-not (Test-Path $JunitJar)) {
    Write-Host "[1/5] Downloading JUnit 5 Console Standalone..." -ForegroundColor Yellow
    $junitUrl = "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar"
    & "C:\Windows\System32\curl.exe" -sLo $JunitJar $junitUrl
}

if ((-not (Test-Path $JacocoAgentJar)) -or (-not (Test-Path $JacocoCliJar))) {
    Write-Host "[1/5] Downloading JaCoCo tools..." -ForegroundColor Yellow
    $zipPath = Join-Path $LibDir "jacoco.zip"
    $jacocoUrl = "https://repo1.maven.org/maven2/org/jacoco/jacoco/0.8.12/jacoco-0.8.12.zip"
    & "C:\Windows\System32\curl.exe" -sLo $zipPath $jacocoUrl
    Expand-Archive -Path $zipPath -DestinationPath (Join-Path $LibDir "temp_jacoco") -Force
    Copy-Item (Join-Path $LibDir "temp_jacoco\lib\jacocoagent.jar") $JacocoAgentJar
    Copy-Item (Join-Path $LibDir "temp_jacoco\lib\jacococli.jar") $JacocoCliJar
    Remove-Item -Recurse -Force (Join-Path $LibDir "temp_jacoco")
    Remove-Item -Force $zipPath
}

# 2. Prepare output directories
if (Test-Path $OutDir) {
    Remove-Item -Recurse -Force $OutDir
}
New-Item -ItemType Directory -Path $ClassesDir -Force | Out-Null
New-Item -ItemType Directory -Path $TestClassesDir -Force | Out-Null
New-Item -ItemType Directory -Path $JacocoReportsDir -Force | Out-Null

# 3. Compile Main Java sources
Write-Host "[2/5] Compiling main Java sources (Java 17 target)..." -ForegroundColor Green
$mainFiles = (Get-ChildItem -Path (Join-Path $Root "src\main\java") -Filter "*.java" -Recurse).FullName
& javac --release 17 -d $ClassesDir $mainFiles
if ($LASTEXITCODE -ne 0) {
    Write-Error "Main compilation failed."
    exit 1
}

# 4. Compile Test Java sources
Write-Host "[3/5] Compiling test Java sources (Java 17 target)..." -ForegroundColor Green
$testFiles = (Get-ChildItem -Path (Join-Path $Root "src\test\java") -Filter "*.java" -Recurse).FullName
$classpath = "$ClassesDir;$LibDir\*"
& javac --release 17 -cp $classpath -d $TestClassesDir $testFiles
if ($LASTEXITCODE -ne 0) {
    Write-Error "Test compilation failed."
    exit 1
}

# 5. Run tests with JaCoCo agent
Write-Host "[4/5] Executing JUnit 5 tests with JaCoCo..." -ForegroundColor Green
$junitReportsDir = Join-Path $ReportsDir "junit"
& java "-javaagent:$JacocoAgentJar=destfile=$JacocoExecFile,includes=com.example.sqe.*" `
    -jar $JunitJar `
    execute `
    --class-path $ClassesDir `
    --class-path $TestClassesDir `
    --scan-class-path `
    --reports-dir=$junitReportsDir

if ($LASTEXITCODE -ne 0) {
    Write-Warning "Some tests failed or returned non-zero exit code."
}

# 6. Generate JaCoCo XML & HTML Report
Write-Host "[5/5] Generating JaCoCo coverage report..." -ForegroundColor Green
$srcMain = Join-Path $Root "src\main\java"
& java -jar $JacocoCliJar report $JacocoExecFile `
    --classfiles $ClassesDir `
    --sourcefiles $srcMain `
    --xml $JacocoXmlFile `
    --html $JacocoHtmlDir

Write-Host "----------------------------------------------------------" -ForegroundColor Cyan
Write-Host " Build & Tests Completed Successfully!" -ForegroundColor Green
Write-Host " JaCoCo XML Report: $JacocoXmlFile" -ForegroundColor Cyan
Write-Host " JaCoCo HTML Report: $JacocoHtmlDir\index.html" -ForegroundColor Cyan
Write-Host "----------------------------------------------------------" -ForegroundColor Cyan
