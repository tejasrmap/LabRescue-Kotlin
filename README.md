# LabRescue – Android Application (Assignment 2)
> *"When your practical fails, learn how to fix it."*

**Author:** Teja Ganugula  
**Module:** IBM4203M Mobile Applications  
**Status:** Successfully Built & Compiled (`app-debug.apk`)

---

## 📱 Executive Overview
**LabRescue** is a native Android application designed to bridge the gap between theoretical knowledge and practical debugging in university laboratory sessions. Instead of generic multiple-choice quizzes or static documentation, LabRescue immerses students in **218 interactive diagnostic troubleshooting scenarios** across **12 computing disciplines** (including Database Systems `CSC204`, Computer Networks `NET301`, Software Engineering `SWE201`, Operating Systems `OS302`, Cybersecurity, Cloud & DevOps, Distributed Systems, Web APIs, Data Science, Compilers, Embedded Systems, and Testing & QA).

---

## 🛠️ Architecture & Technical Stack
The app is engineered strictly following modern Android development best practices and the IBM4203M Assignment 2 assessment rubric:

| Layer | Technology | Description |
| :--- | :--- | :--- |
| **Language** | Kotlin 2.2 + Coroutines | Data classes, pure functions, `StateFlow`, and `@Preview` composables |
| **UI & Controls** | Jetpack Compose | Validated `OutlinedTextField`, `DropdownMenu`, `RadioButton`, `FilterChip`, `LazyColumn`, & `LazyRow` |
| **Design System** | Material 3 | Custom Obsidian dark theme, adaptive layouts, and custom branded adaptive launcher icon |
| **Navigation** | Navigation Compose | `NavController` & `NavHost` multi-screen architecture with typed route arguments |
| **Architecture** | MVVM + ViewModel | `DashboardViewModel` & `DiagnosticViewModel` backed by `LabRescueRepository` |
| **Security & Authentication** | Salted PBKDF2-HMAC-SHA256 | Cryptographic password hashing, secure random 16-byte salts, timing-attack resistant verification (`MessageDigest.isEqual`), session persistence, and zero plaintext credentials |
| **Data Persistence** | Jetpack Room (`androidx.room`) | `@Entity` (`UserAccount`, `UserProfile`, `Attempt`, `Mistake` with `ForeignKey.CASCADE`), `@Dao` interfaces, `AppDatabase` (v4), and full Create/Read/Update/Delete (CRUD) operations |

---

## 🧭 Application Screen Inventory & User Flow
The app implements a full multi-screen diagnostic architecture with enterprise-grade authentication:

1. **Student Sign In (`LoginScreen`)**:
   - Secure academic email and password authentication
   - Real-time input validation, show/hide password toggle, and "Remember Me" session support
   - One-tap **"Quick Demo Student Sign In"** button for immediate marker/assessor review (`teja.ganugula@university.ac.uk` / `Password123!`)
   - Direct pathway to registration (`SignUpScreen`)
2. **Student Registration (`SignUpScreen`)**:
   - Full student onboarding with Full Name, Academic Email, Degree Programme dropdown, Academic Year picker, and Learning Goal presets
   - Password strength enforcement (min 6 characters, alphanumeric requirement) and confirm password matching
   - Automatic account creation with PBKDF2 cryptographic hashing and Room database persistence
3. **Introduction Tour (`OnboardingScreen`)**:
   - 3-step interactive onboarding walkthrough introducing terminal diagnostics, evidence cards, and cognitive feedback
4. **Home Dashboard (`DashboardScreen`)**:
   - Personalized greeting and student profile overview
   - Key metric cards: *Problems Rescued*, *Diagnostic Accuracy (%)*, and *Daily Practice Streak*
   - Dynamic *Recommended Rescue* banner spotlighting targeted practice areas
   - Direct launchpad for coursework disciplines and recent rescue history
5. **Disciplines Library (`SubjectListScreen`)**:
   - Coursework domain catalog covering Databases, Networks, Software Engineering, and Operating Systems
6. **Scenario Library (`ScenarioListScreen`)**:
   - Problem catalog with difficulty tiers (*Beginner*, *Intermediate*, *Advanced*), time estimates, and symptoms
7. **Interactive Diagnostic Mode (`DiagnosticScenarioScreen`)**:
   - High-fidelity **Terminal / IDE Console Window** with realistic log outputs
   - Multi-step branching decision trees (*What should you check first?*)
   - **Forensic Investigation Evidence Cards** that reveal real system state changes upon selection
   - **"Why?" Deliberate Cognitive Feedback Cards** explaining why a check was successful or what misstep occurred
   - Real-time diagnostic scoring engine with time tracking and hint toggles
8. **Rescue Report (`RescueReportScreen`)**:
   - Qualitative diagnostic competence report (*Configuration: Strong*, *Logic: Needs Practice*, etc.)
   - Detailed Root Cause Analysis and recommended fix actions
   - Direct retry and "Next Recommended Scenario" action pathways
9. **Mistake Profile Analytics (`MistakeProfileScreen`)**:
   - Category distribution tracking: **Syntax**, **Configuration**, **Logic**, and **Conceptual**
   - Remediation tips grounded in cognitive science (Retrieval Practice & Spaced Learning)
10. **Rescue History (`HistoryScreen`)**:
    - Chronological historical log of all completed labs with instant report review
11. **Learner Profile & Settings (`ProfileSettingsScreen`)**:
    - Profile personalization (Name, Degree Programme, Academic Year, Diagnostic Goal)
    - Active account email overview and secure **Sign Out** flow
    - Database reset options for fresh lab testing

---

## 🔒 Security Audit & Vulnerability Fixes
The codebase underwent a full security and stability audit addressing the following issues:

1. **Authentication & Identity Isolation (Fixed)**:
   - *Issue*: Previously, any app opening bypassed authentication and loaded a static hardcoded learner profile.
   - *Fix*: Implemented native multi-user authentication with `UserAccount` entity, secure session manager, Login, and Sign Up screens.
2. **Cryptographic Password Storage (OWASP Mobile M2 - Insecure Data Storage - Fixed)**:
   - *Issue*: Storing plaintext credentials creates severe security vulnerabilities.
   - *Fix*: Created `SecurityUtils` implementing **PBKDF2-HMAC-SHA256** (10,000 iterations, 256-bit key) with 16-byte cryptographically secure random salts (`SecureRandom`). Password verification uses constant-time byte comparisons (`MessageDigest.isEqual`) to eliminate timing attack vectors.
3. **Application Backup & ADB Data Theft Vulnerability (Fixed)**:
   - *Issue*: `android:allowBackup="true"` enabled unauthorized extraction of local SQLite databases and shared preferences via `adb backup`.
   - *Fix*: Configured `android:allowBackup="false"` in `AndroidManifest.xml` and established strict exclusion rules in `backup_rules.xml` and `data_extraction_rules.xml` for Room databases (`labrescue.db*`) and preferences.
4. **Scoring Engine Race Condition & Duplicate Penalty Exploit (Fixed)**:
   - *Issue*: In `DiagnosticViewModel.kt`, selecting an option multiple times or repeatedly clicking options on the same node re-triggered score reductions (-25 points repeatedly) and spawned duplicate mistake records.
   - *Fix*: Added node-level evaluation tracking (`evaluatedNodeIds`) ensuring each diagnostic step is evaluated and penalized exactly once.
5. **Startup Null Safety & NoSuchElementException Crash (Fixed)**:
   - *Issue*: `DashboardViewModel.kt` called `.first()` on `SampleData.scenarios`, causing immediate application crashes if the case library was empty or initializing.
   - *Fix*: Switched to `.firstOrNull()` with safe fallback handling.
6. **Input Sanitization & Injection Defense (Fixed)**:
   - *Issue*: Free-form input fields lacked validation against injection and whitespace spoofing.
   - *Fix*: Added RFC-compliant email regex validation, alphanumeric password strength enforcement, and strict character/length constraints on student names and learning goals.

---

## 🧪 Included Lab Scenarios
The app comes pre-seeded with real-world scenarios:
- **Database Systems**:
  - `SQL JOIN Duplicate Records` (Flagship scenario: Cartesian product & 1-to-many cardinality diagnosis)
  - `Unindexed Transaction Lock Timeout` (Full table scan lock escalation diagnosis)
- **Computer Networks**:
  - `Socket Connection Refused on Port 8080` (Localhost loopback vs 0.0.0.0 binding diagnosis)
  - `Subnet Mask IP Isolation` (/25 vs /24 CIDR boundary mismatch diagnosis)
- **Software Engineering**:
  - `Async Search Query Race Condition` (Out-of-order coroutine completion & debounce/flatMapLatest resolution)
  - `Binary Tree Traversal Stack Overflow` (Recursive base case deficiency diagnosis)
- **Operating Systems**:
  - `Mutex Lock Circular Wait Deadlock` (Dining philosophers Coffman condition & lock hierarchy)
  - `Virtual Memory Page Fault Thrashing` (Working set overflow & degree of multiprogramming)

---

## 🚀 How to Run & Build
### In Android Studio:
1. Open Android Studio.
2. Select **File > Open** and choose the `IBM4203M ASSIGNMENT 2` folder.
3. Allow Gradle to sync.
4. Select a connected Android device or emulator (API 26+) and press **Run (Shift + F10)**.

### Via Command Line:
```powershell
# Build Debug APK
.\gradlew.bat assembleDebug

# Output APK Location:
# app/build/outputs/apk/debug/app-debug.apk
```
