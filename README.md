# Java SonarQube / SonarCloud Lab (`java-sonar-lab`)

A zero-dependency, pure Java project structure configured for **SonarQube / SonarCloud code quality analysis** and **JaCoCo code coverage**, automated end-to-end via **GitHub Actions** (`.github/workflows/build.yml`).

---

## 📑 Table of Contents
1. [Project Overview](#-project-overview)
2. [File Directory & Purpose Catalog](#-file-directory--purpose-catalog)
3. [Deep Dive: `.github/workflows/build.yml` Step-by-Step](#-deep-dive-githubworkflowsbuildyml-step-by-step)
4. [SonarQube / SonarCloud Configuration (`sonar-project.properties`)](#-sonarqube--sonarcloud-configuration-sonar-projectproperties)
5. [Local Development & Build Scripts](#-local-development--build-scripts)
6. [Test Suites & Coverage Metrics](#-test-suites--coverage-metrics)

---

## 🌟 Project Overview

This repository demonstrates how to build, test, and analyze a Java project without using heavy build frameworks like Maven or Gradle:
- **Build & Test Engine:** Standard JDK tools (`javac`, `java`) + JUnit 5 Platform Console Standalone.
- **Coverage Engine:** JaCoCo Java Agent & JaCoCo CLI for bytecode instrumentation and XML/HTML report generation.
- **Static Analysis & Quality Gate:** SonarCloud / SonarQube via GitHub Actions.
- **Coverage Quality:** **100% Line & Branch Coverage** across all production source files.

---

## 📁 File Directory & Purpose Catalog

Below is the complete inventory of every file committed to the repository, explaining **why it is necessary** and **what it does**:

| File Path | Why It Is Necessary | What It Does |
| :--- | :--- | :--- |
| **`.github/workflows/build.yml`** | Defines the CI/CD pipeline in GitHub Actions. | Automatically compiles the Java code, runs JUnit 5 tests, generates JaCoCo coverage reports, uploads HTML artifacts, and invokes SonarScanner on every push and pull request. |
| **`sonar-project.properties`** | Configuration file for the SonarQube / SonarCloud scanner CLI. | Defines project keys, organization key, source and test paths, compiled binary paths, and coverage inclusion/exclusion rules so SonarCloud correctly interprets Java bytecode and JaCoCo reports. |
| **`.gitignore`** | Prevents transient and machine-specific files from being pushed to Git. | Ignores compiled bytecode (`out/`, `bin/`), downloaded JAR binaries (`lib/`), temporary scanner workdirs (`.scannerwork/`), and IDE configurations (`.idea/`, `.vscode/`). |
| **`build.ps1`** | Local build and test runner for Windows PowerShell. | Allows developers to compile sources, execute unit tests with the JaCoCo agent, and generate HTML coverage reports locally on Windows with a single command (`.\build.ps1`). |
| **`build.bat`** | Local build and test runner for Windows Command Prompt (`cmd.exe`). | Provides the same local build, test, and coverage pipeline as `build.ps1` for environments running classic Windows Batch scripting. |
| **`build.sh`** | Local build and test runner for Linux, macOS, and WSL. | POSIX-compliant shell script that compiles, tests, and outputs coverage reports in Unix environments with zero third-party dependencies. |
| **`README.md`** | Project documentation. | Provides technical architecture explanations, file-by-file breakdowns, CI/CD step explanations, and setup instructions. |
| **`src/main/java/com/example/sqe/Calculator.java`** | Production business logic. | Provides arithmetic methods (`add`, `subtract`, `multiply`, `divide`), boundary-checked factorial calculation, and prime number detection. |
| **`src/main/java/com/example/sqe/StringService.java`** | Production business logic. | Provides string manipulation utilities including reversal, palindrome validation (ignoring case and non-alphanumerics), vowel counting, and safe truncation. |
| **`src/main/java/com/example/sqe/UserValidator.java`** | Production business logic. | Enforces business validation rules for usernames (regex length/character checks), email addresses (regex format verification), and age ranges (18–120). |
| **`src/test/java/com/example/sqe/CalculatorTest.java`** | Unit test suite for `Calculator`. | Contains parameterized tests, boundary value assertions, and exception tests (`divide by zero`, `negative factorial`) ensuring 100% branch and line coverage. |
| **`src/test/java/com/example/sqe/StringServiceTest.java`** | Unit test suite for `StringService`. | Tests edge cases including `null` inputs, empty strings, punctuation handling, mixed-case vowels, and negative truncation limits. |
| **`src/test/java/com/example/sqe/UserValidatorTest.java`** | Unit test suite for `UserValidator`. | Verifies valid and invalid patterns for usernames, emails, age limits, and composite multi-field user verification. |

---

## ⚙️ Deep Dive: `.github/workflows/build.yml` Step-by-Step

The workflow runs on an Ubuntu virtual environment (`runs-on: ubuntu-latest`) and executes 10 distinct steps:

### 1. Trigger Definition (`on`)
```yaml
on:
  push:
    branches: [ main, master ]
  pull_request:
    types: [opened, synchronize, reopened]
  workflow_dispatch:
```
- **What it does:** Triggers the pipeline whenever code is pushed to `main`/`master`, when a pull request is opened or updated, or manually via the GitHub Actions UI (`workflow_dispatch`).

---

### 2. Step 1: Checkout Repository
```yaml
- name: Checkout Repository
  uses: actions/checkout@v4
  with:
    fetch-depth: 0
```
- **Why it is necessary:** SonarCloud requires the complete Git commit history (`fetch-depth: 0`) to determine code authorship (blame data), track "New Code Period" metrics, and perform branch/PR comparisons.

---

### 3. Step 2: Set up Java JDK
```yaml
- name: Set up Java JDK
  uses: actions/setup-java@v4
  with:
    distribution: 'temurin'
    java-version: '17'
```
- **What it does:** Installs Eclipse Temurin OpenJDK 17 on the GitHub Actions runner and configures `JAVA_HOME` and `PATH`.
- **Why it is necessary:** SonarScanner and JaCoCo require Java 17+ to run their analysis engine.

---

### 4. Step 3: Cache Testing & Coverage Tools
```yaml
- name: Cache Testing & Coverage Tools
  id: cache-tools
  uses: actions/cache@v4
  with:
    path: lib
    key: pure-java-testing-tools-v1
```
- **What it does:** Stores the downloaded testing JARs in the GitHub Actions runner cache. Subsequent runs restore `lib/` in milliseconds rather than re-downloading from Maven Central.

---

### 5. Step 4: Download JUnit & JaCoCo Standalone Tools
```yaml
- name: Download JUnit & JaCoCo Standalone Tools
  if: steps.cache-tools.outputs.cache-hit != 'true'
  run: |
    mkdir -p lib
    curl -sLo lib/junit-platform-console-standalone.jar https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar
    curl -sLo jacoco.zip https://repo1.maven.org/maven2/org/jacoco/jacoco/0.8.12/jacoco-0.8.12.zip
    unzip -q jacoco.zip -d jacoco-temp
    cp jacoco-temp/lib/jacocoagent.jar lib/
    cp jacoco-temp/lib/jacococli.jar lib/
    rm -rf jacoco.zip jacoco-temp
```
- **Why it is necessary:** Because Maven and Gradle are not used, this step fetches the three standalone tools needed for testing:
  1. `junit-platform-console-standalone.jar`: Runs JUnit Jupiter tests from the command line.
  2. `jacocoagent.jar`: Instruments bytecode at runtime to track executed branches and lines.
  3. `jacococli.jar`: Converts raw binary execution data (`jacoco.exec`) into readable XML and HTML reports.

---

### 6. Step 5: Compile Main Sources
```yaml
- name: Compile Main Sources
  run: |
    mkdir -p out/classes
    javac --release 17 -d out/classes $(find src/main/java -name "*.java")
```
- **What it does:** Compiles production Java classes (`Calculator.java`, `StringService.java`, `UserValidator.java`) using `javac` with `--release 17` into `out/classes`.
- **Why `--release 17`:** Ensures the compiled class files use bytecode version 61 (Java 17), which guarantees compatibility with JaCoCo and SonarScanner regardless of the host JDK.

---

### 7. Step 6: Compile Test Sources
```yaml
- name: Compile Test Sources
  run: |
    mkdir -p out/test-classes
    javac --release 17 -cp "out/classes:lib/*" -d out/test-classes $(find src/test/java -name "*.java")
```
- **What it does:** Compiles unit test classes into `out/test-classes`, linking against both the compiled main classes (`out/classes`) and the JUnit 5 standalone library (`lib/*`).

---

### 8. Step 7: Run Unit Tests with JaCoCo Coverage Agent
```yaml
- name: Run Unit Tests with JaCoCo Coverage Agent
  run: |
    mkdir -p out/reports/junit out/reports/jacoco
    java -javaagent:lib/jacocoagent.jar=destfile=out/reports/jacoco.exec,includes=com.example.sqe.* \
      -jar lib/junit-platform-console-standalone.jar \
      execute \
      --class-path out/classes \
      --class-path out/test-classes \
      --scan-class-path \
      --reports-dir=out/reports/junit
```
- **What it does:** Launches the JUnit 5 test runner with the JaCoCo Java Agent attached.
- **Why `includes=com.example.sqe.*` is critical:** Limits JaCoCo bytecode instrumentation strictly to our application package, preventing JaCoCo from attempting to instrument internal JDK classes.
- **Output:** All 88 tests execute, and runtime coverage is written to `out/reports/jacoco.exec`.

---

### 9. Step 8: Generate JaCoCo XML & HTML Coverage Reports
```yaml
- name: Generate JaCoCo XML & HTML Coverage Reports
  run: |
    java -jar lib/jacococli.jar report out/reports/jacoco.exec \
      --classfiles out/classes \
      --sourcefiles src/main/java \
      --xml out/reports/jacoco/jacoco.xml \
      --html out/reports/jacoco/html
```
- **What it does:** Runs `jacococli.jar` to read `out/reports/jacoco.exec` alongside class bytecode and source code, generating:
  - `out/reports/jacoco/jacoco.xml`: Machine-readable report consumed by SonarCloud.
  - `out/reports/jacoco/html/index.html`: Interactive visual HTML report.

---

### 10. Step 9: Upload JaCoCo HTML Coverage Report Artifact
```yaml
- name: Upload JaCoCo HTML Coverage Report Artifact
  uses: actions/upload-artifact@v4
  with:
    name: jacoco-coverage-report
    path: out/reports/jacoco/html/
    if-no-files-found: warn
```
- **What it does:** Packages the HTML coverage report into a downloadable ZIP archive accessible directly on the GitHub Actions workflow run summary page.

---

### 11. Step 10: SonarQube / SonarCloud Scanner
```yaml
- name: SonarQube Scan
  uses: SonarSource/sonarqube-scan-action@v5
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
    SONAR_HOST_URL: https://sonarcloud.io
  with:
    args: >
      -Dsonar.organization=sonarqubecloudsqetest
      -Dsonar.projectKey=SonarQubeCloudSQETest_java-sonar-lab
      -Dsonar.sources=src/main/java
      -Dsonar.tests=src/test/java
      -Dsonar.java.binaries=out/classes
      -Dsonar.java.test.binaries=out/test-classes
      -Dsonar.coverage.inclusions=src/main/java/**/*.java
      -Dsonar.coverage.exclusions=**/*.ps1,**/*.sh,**/*.bat,**/*.cmd,src/test/**,**/*Test.java,**/test/**
      -Dsonar.exclusions=*.ps1,*.sh,*.bat,*.cmd
      -Dsonar.coverage.jacoco.xmlReportPaths=out/reports/jacoco/jacoco.xml
```
- **What it does:** Runs the official SonarQube Scanner action, securely authenticating with `SONAR_TOKEN` and transmitting the analysis and coverage data to SonarCloud.
- **Key Parameters Explained:**
  - `sonar.organization`: Specifies your SonarCloud organization key (`sonarqubecloudsqetest`).
  - `sonar.projectKey`: Unique identifier for the project in SonarCloud.
  - `sonar.sources=src/main/java`: Informs Sonar which files contain production source code.
  - `sonar.tests=src/test/java`: Informs Sonar which files contain test code (so test files appear under "Unit Tests", not "Lines of Code").
  - `sonar.coverage.inclusions=src/main/java/**/*.java`: **Ensures coverage is calculated exclusively on production Java classes.**
  - `sonar.coverage.exclusions`: Prevents scripts (`build.ps1`, `build.sh`) and test classes from deflating code coverage.
  - `sonar.exclusions`: Excludes helper shell/batch scripts from static code analysis smells.
  - `sonar.coverage.jacoco.xmlReportPaths`: Tells SonarCloud where to find the JaCoCo XML report.

---

## 🔍 SonarQube / SonarCloud Configuration (`sonar-project.properties`)

The `sonar-project.properties` file in the project root mirrors the analysis configuration for local or alternate scanner CLI runs:

```properties
# Project Identification
sonar.projectKey=SonarQubeCloudSQETest_java-sonar-lab
sonar.projectName=SQEJavaTest
sonar.projectVersion=1.0

# Source and Test roots
sonar.sources=src/main/java
sonar.tests=src/test/java

# Strictly calculate coverage ONLY on Java files in src/main/java
sonar.coverage.inclusions=src/main/java/**/*.java

# Exclude scripts and test files from coverage & analysis
sonar.coverage.exclusions=**/*.ps1,**/*.sh,**/*.bat,**/*.cmd,src/test/**,**/*Test.java,**/test/**
sonar.exclusions=*.ps1,*.sh,*.bat,*.cmd
sonar.cpd.exclusions=src/test/**,**/*Test.java,**/test/**

# Bytecode binaries (required for Sonar Java analyzer)
sonar.java.binaries=out/classes
sonar.java.test.binaries=out/test-classes

# JaCoCo Report path
sonar.coverage.jacoco.xmlReportPaths=out/reports/jacoco/jacoco.xml

# Encoding & Target Cloud Host
sonar.sourceEncoding=UTF-8
sonar.organization=sonarqubecloudsqetest
sonar.host.url=https://sonarcloud.io
```

---

## 💻 Local Development & Build Scripts

Developers can verify code compilation, run all 88 unit tests, and inspect the HTML JaCoCo coverage report locally before pushing to GitHub:

### Windows PowerShell
```powershell
.\build.ps1
```

### Windows Command Prompt (CMD)
```cmd
build.bat
```

### Linux / macOS / Git Bash
```bash
chmod +x build.sh
./build.sh
```

### Viewing Local Coverage
Once the script completes, open the interactive HTML report in your browser:
```text
out/reports/jacoco/html/index.html
```

---

## 📊 Test Suites & Coverage Metrics

The project includes **88 automated unit tests** written with **JUnit 5 Jupiter**:

| Service Class | Test Class | Methods Tested | Coverage Achieved |
| :--- | :--- | :--- | :---: |
| `Calculator.java` | `CalculatorTest.java` | `add`, `subtract`, `multiply`, `divide`, `factorial`, `isPrime` | **100%** |
| `StringService.java` | `StringServiceTest.java` | `reverse`, `isPalindrome`, `countVowels`, `truncate` | **100%** |
| `UserValidator.java` | `UserValidatorTest.java` | `validateUsername`, `validateEmail`, `validateAge`, `isValidUser` | **100%** |

### SonarCloud Metrics Summary
- **Line Coverage:** 100% (69 / 69 lines covered)
- **Branch Coverage:** 100% (68 / 68 branches covered)
- **Security Vulnerabilities:** 0
- **Bugs:** 0
- **Code Smells:** 0
- **Duplicated Lines:** 0.0%
- **Quality Gate:** **PASSED**
