# SQEJavaTest — Pure Java SonarQube & JaCoCo Coverage Template

A lightweight, production-ready Java project structure configured for **SonarQube / SonarCloud code quality analysis** and **JaCoCo code coverage reports**, running automatically via **GitHub Actions** with **Pure Java (No Maven or Gradle required)**.

---

## 🌟 Key Features

- **Pure Java**: Uses standard Java compiler (`javac`) and Java runtime (`java`). Zero dependencies on Maven or Gradle.
- **100% Code Coverage**: Unit tests built with **JUnit 5** covering all lines, branches, and edge cases.
- **JaCoCo Coverage**: Produces both `jacoco.xml` (for SonarQube automated ingestion) and interactive `index.html` (for human review).
- **GitHub Actions CI/CD**: Automatically compiles, runs tests, generates JaCoCo reports, uploads HTML coverage artifacts, and triggers SonarQube scans on `push` and `pull_request`.
- **SonarQube & SonarCloud Ready**: Configured with `sonar-project.properties` and the official `SonarSource/sonarqube-scan-action`.
- **Cross-Platform Local Scripts**: Works out of the box on Windows (`build.ps1`, `build.bat`) and Linux / macOS (`build.sh`).

---

## 📁 Project Structure

```
SQEJavaTest/
├── .github/
│   └── workflows/
│       └── sonar.yml               # GitHub Actions workflow for build, test, and Sonar scan
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/example/sqe/
│   │           ├── Calculator.java       # Arithmetic, factorial, and prime checks
│   │           ├── StringService.java    # Reverse, palindrome, vowels, truncate
│   │           └── UserValidator.java    # Username, email, and age validation
│   └── test/
│       └── java/
│           └── com/example/sqe/
│               ├── CalculatorTest.java    # Unit tests with parameterized inputs
│               ├── StringServiceTest.java # String manipulation edge cases
│               └── UserValidatorTest.java # Validation rule test coverage
├── sonar-project.properties        # SonarQube / SonarCloud configuration file
├── build.ps1                       # PowerShell runner for Windows
├── build.bat                       # Command prompt runner for Windows
├── build.sh                        # Bash runner for Linux / macOS / WSL
├── .gitignore                      # Git ignore patterns for build outputs and IDEs
└── README.md                       # Project documentation
```

---

## 🚀 Running Tests & Generating Coverage Locally

You can test and generate JaCoCo coverage reports locally with a single command. The script downloads standalone test runner tools once and executes everything:

### Windows (PowerShell)
```powershell
.\build.ps1
```

### Windows (CMD)
```cmd
build.bat
```

### Linux / macOS / Git Bash
```bash
chmod +x build.sh
./build.sh
```

### Coverage Report Location
Once the script completes, inspect the generated reports at:
- **XML Report (for SonarQube)**: `out/reports/jacoco/jacoco.xml`
- **Interactive HTML Report (for browser)**: `out/reports/jacoco/html/index.html`

---

## 🛠️ Setting Up GitHub Actions & SonarQube

### 1. Push Project to GitHub

Initialize your repository and push to GitHub:
```bash
git init
git add .
git commit -m "feat: initial Pure Java project with JUnit 5, JaCoCo, and SonarQube GitHub Actions"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-repo-name>.git
git push -u origin main
```

---

### 2. Configure GitHub Secrets

Go to your repository on GitHub:
**Settings** ➔ **Secrets and variables** ➔ **Actions** ➔ **New repository secret**

Add the following secrets:

| Secret Name | Value Description | Example / Notes |
| :--- | :--- | :--- |
| `SONAR_TOKEN` | Your SonarQube or SonarCloud user authentication token | Generated from Sonar: *My Account ➔ Security ➔ Generate Token* |
| `SONAR_HOST_URL` | The URL of your SonarQube server | e.g., `https://sonarqube.yourdomain.com` *(If using SonarCloud, set to `https://sonarcloud.io`)* |

---

### 3. SonarCloud 


## 📊 Viewing Results in GitHub Actions & SonarQube

1. **GitHub Actions Tab**:
   - Every push triggers the `SonarQube Quality Analysis & Test Coverage` workflow.
   - The workflow compiles your code with `javac`, executes tests with JUnit 5, generates JaCoCo reports, uploads the HTML report artifact, and runs the SonarQube scanner.
   - You can download the **`jacoco-coverage-report`** ZIP artifact directly from the workflow summary page.

2. **SonarQube Dashboard**:
   - Open your project dashboard in SonarQube / SonarCloud.
   - Check the **Quality Gate** (Passed).
   - Check **Coverage** (100% line & branch coverage).
   - Check **Reliability, Security, and Maintainability** (Clean A-ratings).

---

## 🧪 Included Test Suites

| Class | Covered Scenarios |
| :--- | :--- |
| `Calculator` | Addition, subtraction, multiplication, division, divide-by-zero exceptions, factorials (0, 1, 5, negative numbers), and prime checking for positives, negatives, primes, and composites. |
| `UserValidator` | Username length and character validation, null handling, email format regex matching, age constraints (18–120), and compound user validation. |
| `StringService` | Reverse strings, case-insensitive and punctuation-ignoring palindrome detection, vowel counting, truncation boundary cases, negative length exceptions, and null safety. |
