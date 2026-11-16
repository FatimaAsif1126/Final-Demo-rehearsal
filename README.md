# Java SonarQube / SonarCloud Lab (`java-sonar-lab`)

A zero-dependency, pure Java project structure configured for **SonarQube / SonarCloud code quality analysis** and **JaCoCo code coverage** automated via **GitHub Actions**.

---

## 📑 Table of Contents
1. [Project Overview](#-project-overview)
2. [What to Paste in `build.yml`](#-what-to-paste-in-buildyml)
3. [SonarCloud Code Issues & Solutions Guide (`SecurityAuditService.java`)](#-sonarcloud-code-issues--solutions-guide-securityauditservicejava)
4. [SonarCloud Workflow Security Rules Fixed in `test.yml`](#-sonarcloud-workflow-security-rules-fixed-in-testyml)

---

## 🌟 Project Overview

- **Build & Test Engine:** Standard JDK tools (`javac`, `java`) + JUnit 5 Platform Console Standalone.
- **Coverage Engine:** JaCoCo Java Agent & JaCoCo CLI for bytecode instrumentation and XML/HTML report generation.
- **CI/CD Architecture:** Modular GitHub Actions pipeline where [.github/workflows/build.yml](file:///d:/WorkSheets/XFAST/SQEJavaTest/.github/workflows/build.yml) delegates execution to the comprehensive test and scan workflow [.github/workflows/test.yml](file:///d:/WorkSheets/XFAST/SQEJavaTest/.github/workflows/test.yml).

---

## 🔄 What to Paste in `build.yml` (Windows Runner Compatible)

Keep `runs-on: windows-latest` in [`.github/workflows/build.yml`](file:///d:/WorkSheets/XFAST/SQEJavaTest/.github/workflows/build.yml).

### Step 1: Replace Placeholder Comments

In [`.github/workflows/build.yml`](file:///d:/WorkSheets/XFAST/SQEJavaTest/.github/workflows/build.yml), locate lines 16–18:
```yaml
      # This step should build, test and generate coverage reports
      # - name: Build and test
      #   run: <build + test + generate coverage reports>
```

Replace those 3 lines with this exact Windows-compatible block:
```yaml
      - name: Set up Java JDK
        uses: actions/setup-java@3a4f6e1af504cf6a31855fa899c6aa5355ba6c12 # v4.7.0
        with:
          distribution: 'temurin'
          java-version: '17'

      - name: Build, Test and Generate JaCoCo Coverage
        shell: bash
        run: |
          mkdir -p lib out/classes out/test-classes out/reports/junit out/reports/jacoco
          curl --proto '=https' --proto-redir '=https' --tlsv1.2 -sSfLo lib/junit-platform-console-standalone.jar https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar
          curl --proto '=https' --proto-redir '=https' --tlsv1.2 -sSfLo jacoco.zip https://repo1.maven.org/maven2/org/jacoco/jacoco/0.8.12/jacoco-0.8.12.zip
          unzip -q -o jacoco.zip -d jacoco-temp
          cp jacoco-temp/lib/jacocoagent.jar lib/
          cp jacoco-temp/lib/jacococli.jar lib/
          rm -rf jacoco.zip jacoco-temp
          find src/main/java -name "*.java" > sources_main.txt
          javac --release 17 -d out/classes @sources_main.txt
          find src/test/java -name "*.java" > sources_test.txt
          javac --release 17 -cp "out/classes;lib/*" -d out/test-classes @sources_test.txt
          java -javaagent:lib/jacocoagent.jar=destfile=out/reports/jacoco.exec,includes=com.example.sqe.* -jar lib/junit-platform-console-standalone.jar execute --class-path out/classes --class-path out/test-classes --scan-class-path --reports-dir=out/reports/junit
          java -jar lib/jacococli.jar report out/reports/jacoco.exec --classfiles out/classes --sourcefiles src/main/java --xml out/reports/jacoco/jacoco.xml
```

> [!NOTE]
> **Why this works seamlessly on `windows-latest`:**
> - `shell: bash`: Executes inside Git Bash (pre-installed on all Windows GitHub runners).
> - `-cp "out/classes;lib/*"`: Windows `javac.exe` requires a semicolon (`;`) classpath separator instead of a colon (`:`). The enclosing quotes prevent bash from interpreting the semicolon as a command separator.
> - `@sources_main.txt` & `@sources_test.txt`: Avoids Windows CLI argument length and path escaping limitations.

---

### Step 2: Add JaCoCo Report Path to SonarQube Scan

At the bottom of [`.github/workflows/build.yml`](file:///d:/WorkSheets/XFAST/SQEJavaTest/.github/workflows/build.yml), append `-Dsonar.coverage.jacoco.xmlReportPaths=out/reports/jacoco/jacoco.xml` to `args: >`:
```yaml
      - name: SonarQube Scan
        uses: SonarSource/sonarqube-scan-action@4d1dc0c91ee400dcf8a6dddf2f705ac36542e304 # v8.4.0
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        with:
          args: >
            -Dsonar.organization=sonarqubecloudsqetest
            -Dsonar.projectKey=SonarQubeCloudSQETest_java-sonar-lab
            -Dsonar.coverage.jacoco.xmlReportPaths=out/reports/jacoco/jacoco.xml
```

---

## 🛡️ SonarCloud Code Issues & Solutions Guide (`SecurityAuditService.java`)

The class [`src/main/java/com/example/sqe/SecurityAuditService.java`](file:///d:/WorkSheets/XFAST/SQEJavaTest/src/main/java/com/example/sqe/SecurityAuditService.java) deliberately implements 4 specific code issues to demonstrate SonarCloud quality analysis rules:

---

### Issue 1: "Make sure that hashing data is safe here."
- **Sonar Rule:** `java:S4790`
- **Category:** Security (Severity: High)
- **Tags:** `cryptography`, `cwe-327`, `cwe-328`
- **Method:** `hashData(String input)`

#### Problem Code:
```java
MessageDigest md = MessageDigest.getInstance("MD5"); // ⚠️ FLAGGED BY SONAR
```

#### Why Sonar Flags It:
MD5 is an obsolete, cryptographically broken algorithm vulnerable to hash collision attacks. Attackers can generate two different inputs that produce the identical MD5 digest, rendering it unsafe for digital signatures, certificates, and password storage.

#### Solution:
Replace `"MD5"` with a secure, collision-resistant cryptographic algorithm:
```java
// ✅ SOLUTION: Use SHA-256 or SHA-512
MessageDigest md = MessageDigest.getInstance("SHA-256");
```
*(For password storage, use adaptive key-stretching algorithms such as Argon2, bcrypt, or PBKDF2).*

---

### Issue 2: "Raise this exception or remove this useless statement."
- **Sonar Rule:** `java:S3984`
- **Category:** Reliability (Severity: Medium)
- **Tags:** `error-handling`
- **Method:** `validateToken(String token)`

#### Problem Code:
```java
if (token == null || token.trim().isEmpty()) {
    new IllegalArgumentException("User token cannot be null or empty."); // ⚠️ FLAGGED BY SONAR
    return false;
}
```

#### Why Sonar Flags It:
The exception object is instantiated with `new`, but the `throw` keyword was omitted. As a result, the exception is created in memory and immediately discarded. Execution continues normally, and the caller is never notified of the error via an exception.

#### Solution:
Add the `throw` keyword to raise the exception, or remove the statement if no exception was intended:
```java
// ✅ SOLUTION: Add 'throw'
if (token == null || token.trim().isEmpty()) {
    throw new IllegalArgumentException("User token cannot be null or empty.");
}
```

---

### Issue 3: "Remove this if statement or edit its code blocks so that they're not all the same."
- **Sonar Rule:** `java:S3923`
- **Category:** Reliability (Severity: Medium)
- **Tags:** `convention`, `suspicious`
- **Method:** `evaluateAccessLevel(String role)`

#### Problem Code:
```java
if ("ADMIN".equalsIgnoreCase(role)) {
    return "ACCESS_LEVEL_STANDARD"; // ⚠️ FLAGGED BY SONAR: Identical to else branch
} else {
    return "ACCESS_LEVEL_STANDARD"; // ⚠️ FLAGGED BY SONAR: Identical to if branch
}
```

#### Why Sonar Flags It:
All branches of the conditional structure execute the exact same statement (`return "ACCESS_LEVEL_STANDARD"`). Regardless of whether the role is "ADMIN" or not, the output is unchanged. This indicates dead code or a copy-paste logic bug where the "ADMIN" branch was left unfinished.

#### Solution:
Either provide distinct logic for each branch:
```java
// ✅ SOLUTION A: Differentiate branch logic
if ("ADMIN".equalsIgnoreCase(role)) {
    return "ACCESS_LEVEL_ADMIN";
} else {
    return "ACCESS_LEVEL_STANDARD";
}
```
Or remove the conditional statement entirely if all roles truly receive the same access level:
```java
// ✅ SOLUTION B: Remove redundant conditional
return "ACCESS_LEVEL_STANDARD";
```

---

### Issue 4: "Refactor this method to not always return the same value."
- **Sonar Rule:** `java:S3516`
- **Category:** Maintainability (Severity: Blocker)
- **Tags:** `confusing`, `design`
- **Method:** `checkServerHealth(int statusCode)`

#### Problem Code:
```java
if (statusCode >= 200 && statusCode < 300) {
    return true; // ⚠️ FLAGGED BY SONAR: Returns true
} else if (statusCode == 404) {
    return true; // ⚠️ FLAGGED BY SONAR: Returns true
} else {
    return true; // ⚠️ FLAGGED BY SONAR: Returns true
}
```

#### Why Sonar Flags It:
The method appears to evaluate whether a server is healthy based on HTTP status codes, but every possible execution branch returns `true`. It can never return `false`. Callers relying on this method for health-checking are misled into believing validation is taking place.

#### Solution:
Refactor the method to return `true` for healthy status codes and `false` for error codes:
```java
// ✅ SOLUTION: Return meaningful boolean values
if (statusCode >= 200 && statusCode < 300) {
    return true;  // Healthy 2xx status
} else {
    return false; // Error status
}
```

---
