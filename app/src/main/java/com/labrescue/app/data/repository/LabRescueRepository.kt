package com.labrescue.app.data.repository

import android.content.Context
import androidx.room.withTransaction
import com.labrescue.app.data.db.AppDatabase
import com.labrescue.app.data.db.DatabaseSeedData
import com.labrescue.app.data.db.SampleData
import com.labrescue.app.data.model.Attempt
import com.labrescue.app.data.model.Mistake
import com.labrescue.app.data.model.MistakeCategory
import com.labrescue.app.data.model.Scenario
import com.labrescue.app.data.model.ScenarioNode
import com.labrescue.app.data.model.Subject
import com.labrescue.app.data.model.UserAccount
import com.labrescue.app.data.model.UserProfile
import com.labrescue.app.data.security.SecurityUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class LabRescueRepository(context: Context) {

    companion object {
        private const val PREFS_NAME = "labrescue_prefs"
        private const val KEY_ONBOARDING_COMPLETED = "has_completed_onboarding"
        private const val KEY_LOGGED_IN_USER_ID = "logged_in_user_id"
        private const val KEY_INITIAL_ROOM_SEED_V6 = "initial_room_seed_v6"
    }

    private val database = AppDatabase.getInstance(context)
    private val userProfileDao = database.userProfileDao()
    private val userAccountDao = database.userAccountDao()
    private val attemptDao = database.attemptDao()
    private val mistakeDao = database.mistakeDao()

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    private val _currentAccount = MutableStateFlow<UserAccount?>(null)
    val currentAccount: StateFlow<UserAccount?> = _currentAccount.asStateFlow()

    private val _hasCompletedOnboarding = MutableStateFlow(
        prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    )
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    private val _userProfile = MutableStateFlow(DatabaseSeedData.defaultUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _subjects = MutableStateFlow(SampleData.subjects)
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _attempts = MutableStateFlow<List<Attempt>>(emptyList())
    val attempts: StateFlow<List<Attempt>> = _attempts.asStateFlow()

    private val _mistakes = MutableStateFlow<List<Mistake>>(emptyList())
    val mistakes: StateFlow<List<Mistake>> = _mistakes.asStateFlow()

    private val _mistakeStats = MutableStateFlow(
        MistakeCategory.entries.associateWith { 0 }
    )
    val mistakeStats: StateFlow<Map<MistakeCategory, Int>> = _mistakeStats.asStateFlow()

    // Active session observation coroutines
    private var userAttemptsJob: Job? = null
    private var userMistakesJob: Job? = null
    private var userProfileJob: Job? = null

    init {
        repositoryScope.launch {
            val hasSeeded = prefs.getBoolean(KEY_INITIAL_ROOM_SEED_V6, false)
            val demoAccount = userAccountDao.getAccountByEmail(DatabaseSeedData.DEMO_EMAIL)

            if (!hasSeeded || demoAccount == null) {
                seedDemoDatasetTransaction(preserveProfileIdentity = false)
                prefs.edit().putBoolean(KEY_INITIAL_ROOM_SEED_V6, true).apply()
            }

            // Restore logged in user session if present
            val savedUserId = prefs.getString(KEY_LOGGED_IN_USER_ID, null)
            if (!savedUserId.isNullOrEmpty()) {
                val account = userAccountDao.getAccountById(savedUserId)
                if (account != null) {
                    switchUserSession(account)
                } else {
                    prefs.edit().remove(KEY_LOGGED_IN_USER_ID).apply()
                    _isUserLoggedIn.value = false
                    _currentAccount.value = null
                }
            } else {
                _isUserLoggedIn.value = false
                _currentAccount.value = null
            }
        }
    }

    private fun computeMistakeStats(mistakesList: List<Mistake>): Map<MistakeCategory, Int> {
        val counts = MistakeCategory.entries.associateWith { 0 }.toMutableMap()
        for (mistake in mistakesList) {
            counts[mistake.category] = (counts[mistake.category] ?: 0) + 1
        }
        return counts
    }

    private suspend fun seedDemoDatasetTransaction(preserveProfileIdentity: Boolean) {
        val now = System.currentTimeMillis()
        val demoAccount = DatabaseSeedData.createDemoUserAccount()
        val demoAttempts = DatabaseSeedData.demoAttempts(now).map { it.copy(userId = demoAccount.id) }
        val demoMistakes = DatabaseSeedData.demoMistakes(now)

        database.withTransaction {
            if (userAccountDao.getAccountByEmail(DatabaseSeedData.DEMO_EMAIL) == null) {
                userAccountDao.insertAccount(demoAccount)
            }

            val current = userProfileDao.getUserProfile(demoAccount.id)
            val defaultSeed = DatabaseSeedData.defaultUserProfile()
            val profileToSave = if (preserveProfileIdentity && current != null) {
                current.copy(
                    totalRescues = demoAttempts.size,
                    successfulRescues = demoAttempts.count { it.isSuccess },
                    streakDays = defaultSeed.streakDays
                )
            } else {
                defaultSeed
            }

            userProfileDao.insertUserProfile(profileToSave)
            attemptDao.deleteAllAttemptsForUser(demoAccount.id)
            attemptDao.insertAttempts(demoAttempts)
            mistakeDao.insertMistakes(demoMistakes)
        }
    }

    /**
     * Completely switches the active reactive session to the given user account.
     * Cancels any prior user Flow collectors to ensure 100% data isolation and zero cross-penetration.
     */
    private suspend fun switchUserSession(account: UserAccount) {
        prefs.edit().putString(KEY_LOGGED_IN_USER_ID, account.id).apply()
        _currentAccount.value = account
        _isUserLoggedIn.value = true

        // Cancel previous session observers
        userAttemptsJob?.cancel()
        userMistakesJob?.cancel()
        userProfileJob?.cancel()

        // 1. Fetch or create UserProfile for this account
        var profile = userProfileDao.getUserProfile(account.id)
        if (profile == null) {
            profile = UserProfile(
                id = account.id,
                email = account.email,
                name = account.name,
                programme = account.programme,
                year = account.year,
                targetGoal = account.targetGoal,
                totalRescues = 0,
                successfulRescues = 0,
                streakDays = 0
            )
            userProfileDao.insertUserProfile(profile)
        }
        _userProfile.value = profile

        // 2. Load attempts for this user immediately and observe
        val userAttempts = attemptDao.getAttemptsForUser(account.id)
        _attempts.value = userAttempts

        userAttemptsJob = repositoryScope.launch {
            attemptDao.observeAttemptsForUser(account.id).collect { attemptsList ->
                _attempts.value = attemptsList
            }
        }

        // 3. Load mistakes for this user immediately and observe
        val userMistakes = mistakeDao.getMistakesForUser(account.id)
        _mistakes.value = userMistakes
        _mistakeStats.value = computeMistakeStats(userMistakes)

        userMistakesJob = repositoryScope.launch {
            mistakeDao.observeMistakesForUser(account.id).collect { mistakesList ->
                _mistakes.value = mistakesList
                _mistakeStats.value = computeMistakeStats(mistakesList)
            }
        }

        // 4. Observe live profile updates for this user
        userProfileJob = repositoryScope.launch {
            userProfileDao.observeUserProfile(account.id).collect { liveProfile ->
                if (liveProfile != null) {
                    _userProfile.value = liveProfile
                }
            }
        }
    }

    suspend fun login(email: String, password: String): Result<UserAccount> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        if (!SecurityUtils.isValidEmail(trimmedEmail)) {
            return@withContext Result.failure(IllegalArgumentException("Please enter a valid email address"))
        }
        if (password.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Password cannot be empty"))
        }

        val account = userAccountDao.getAccountByEmail(trimmedEmail)
            ?: return@withContext Result.failure(IllegalArgumentException("No account found with this email"))

        val isPasswordCorrect = SecurityUtils.verifyPassword(password, account.salt, account.passwordHash)
        if (!isPasswordCorrect) {
            return@withContext Result.failure(IllegalArgumentException("Incorrect password. Please try again."))
        }

        switchUserSession(account)
        Result.success(account)
    }

    suspend fun signUp(
        name: String,
        email: String,
        password: String,
        programme: String,
        year: String,
        targetGoal: String,
        enrolledSubjects: String = "db,net,prog,os"
    ): Result<UserAccount> = withContext(Dispatchers.IO) {
        val nameErr = SecurityUtils.validateFullName(name)
        if (nameErr != null) return@withContext Result.failure(IllegalArgumentException(nameErr))

        val trimmedEmail = email.trim().lowercase()
        if (!SecurityUtils.isValidEmail(trimmedEmail)) {
            return@withContext Result.failure(IllegalArgumentException("Please enter a valid email address"))
        }

        val passwordErr = SecurityUtils.validatePasswordStrength(password)
        if (passwordErr != null) return@withContext Result.failure(IllegalArgumentException(passwordErr))

        if (programme.isBlank()) return@withContext Result.failure(IllegalArgumentException("Please select a degree programme"))
        if (year.isBlank()) return@withContext Result.failure(IllegalArgumentException("Please select an academic year"))

        val existing = userAccountDao.getAccountByEmail(trimmedEmail)
        if (existing != null) {
            return@withContext Result.failure(IllegalArgumentException("An account with this email already exists"))
        }

        val salt = SecurityUtils.generateSalt()
        val passwordHash = SecurityUtils.hashPassword(password, salt)
        val accountId = "usr_" + UUID.randomUUID().toString().take(10)

        val newAccount = UserAccount(
            id = accountId,
            email = trimmedEmail,
            passwordHash = passwordHash,
            salt = salt,
            name = name.trim(),
            programme = programme.trim(),
            year = year.trim(),
            targetGoal = targetGoal.trim().ifEmpty { "Master Diagnostic Troubleshooting" },
            enrolledSubjects = enrolledSubjects.ifBlank { "db,net,prog,os" },
            createdAt = System.currentTimeMillis()
        )

        val newProfile = UserProfile(
            id = accountId,
            email = trimmedEmail,
            name = name.trim(),
            programme = programme.trim(),
            year = year.trim(),
            targetGoal = targetGoal.trim().ifEmpty { "Master Diagnostic Troubleshooting" },
            enrolledSubjects = enrolledSubjects.ifBlank { "db,net,prog,os" },
            totalRescues = 0,
            successfulRescues = 0,
            streakDays = 0
        )

        database.withTransaction {
            userAccountDao.insertAccount(newAccount)
            userProfileDao.insertUserProfile(newProfile)
        }

        switchUserSession(newAccount)
        Result.success(newAccount)
    }

    suspend fun loginAsDemoStudent(): Result<UserAccount> = withContext(Dispatchers.IO) {
        var demoAccount = userAccountDao.getAccountByEmail(DatabaseSeedData.DEMO_EMAIL)
        if (demoAccount == null) {
            demoAccount = DatabaseSeedData.createDemoUserAccount()
            userAccountDao.insertAccount(demoAccount)
        }
        switchUserSession(demoAccount)
        Result.success(demoAccount)
    }

    fun logout() {
        prefs.edit().remove(KEY_LOGGED_IN_USER_ID).apply()
        _isUserLoggedIn.value = false
        _currentAccount.value = null
        _attempts.value = emptyList()
        _mistakes.value = emptyList()
        _mistakeStats.value = MistakeCategory.entries.associateWith { 0 }
        _userProfile.value = DatabaseSeedData.defaultUserProfile().copy(
            totalRescues = 0,
            successfulRescues = 0,
            streakDays = 0
        )

        userAttemptsJob?.cancel()
        userMistakesJob?.cancel()
        userProfileJob?.cancel()
    }

    fun getScenario(id: String): Scenario? {
        return SampleData.scenarios.find { it.id == id }
    }

    fun getScenariosForSubject(subjectId: String): List<Scenario> {
        return SampleData.scenarios.filter { it.subjectId == subjectId }
    }

    fun getNode(nodeId: String): ScenarioNode? {
        return SampleData.scenarioNodes.find { it.id == nodeId }
    }

    fun getAttempt(attemptId: String): Attempt? {
        return _attempts.value.find { it.id == attemptId }
    }

    // CREATE operation: Insert Attempt & Mistakes for the active user + update UserProfile stats
    fun recordAttempt(attempt: Attempt, newMistakes: List<Mistake>) {
        val currentUserId = attempt.userId
        val updatedProfile = _userProfile.value.let { current ->
            current.copy(
                totalRescues = current.totalRescues + 1,
                successfulRescues = if (attempt.isSuccess) current.successfulRescues + 1 else current.successfulRescues,
                streakDays = if (current.streakDays == 0) 1 else current.streakDays
            )
        }
        _attempts.value = listOf(attempt) + _attempts.value
        _mistakes.value = newMistakes + _mistakes.value
        _mistakeStats.value = computeMistakeStats(_mistakes.value)
        _userProfile.value = updatedProfile

        repositoryScope.launch {
            database.withTransaction {
                attemptDao.insertAttempt(attempt)
                if (newMistakes.isNotEmpty()) {
                    mistakeDao.insertMistakes(newMistakes)
                }
                val userAttempts = attemptDao.getAttemptsForUser(currentUserId)
                val persistedProfile = userProfileDao.getUserProfile(currentUserId) ?: _userProfile.value
                val updated = persistedProfile.copy(
                    totalRescues = userAttempts.size,
                    successfulRescues = userAttempts.count { it.isSuccess },
                    streakDays = if (persistedProfile.streakDays == 0) 1 else persistedProfile.streakDays
                )
                userProfileDao.updateUserProfile(updated)
                if (_userProfile.value.id == currentUserId) {
                    _userProfile.value = updated
                }
            }
        }
    }

    // UPDATE operation: Update learner credentials & target goal in Room
    fun updateProfile(name: String, programme: String, year: String, targetGoal: String, enrolledSubjects: String? = null) {
        val updated = _userProfile.value.copy(
            name = name.trim().ifEmpty { _userProfile.value.name },
            programme = programme.trim().ifEmpty { _userProfile.value.programme },
            year = year.trim().ifEmpty { _userProfile.value.year },
            targetGoal = targetGoal.trim().ifEmpty { _userProfile.value.targetGoal },
            enrolledSubjects = enrolledSubjects ?: _userProfile.value.enrolledSubjects
        )
        _userProfile.value = updated

        repositoryScope.launch {
            val existing = userProfileDao.getUserProfile(updated.id)
            if (existing != null) {
                userProfileDao.updateUserProfile(
                    existing.copy(
                        name = updated.name,
                        programme = updated.programme,
                        year = updated.year,
                        targetGoal = updated.targetGoal,
                        enrolledSubjects = updated.enrolledSubjects
                    )
                )
            } else {
                userProfileDao.insertUserProfile(updated)
            }

            // Sync with UserAccount if one matches
            val account = userAccountDao.getAccountById(updated.id)
            if (account != null) {
                val updatedAccount = account.copy(
                    name = updated.name,
                    programme = updated.programme,
                    year = updated.year,
                    targetGoal = updated.targetGoal,
                    enrolledSubjects = updated.enrolledSubjects
                )
                userAccountDao.updateAccount(updatedAccount)
                _currentAccount.value = updatedAccount
            }
        }
    }

    // DELETE operation (single entity): Delete an individual Attempt
    fun deleteAttempt(attempt: Attempt) {
        val currentUserId = attempt.userId
        repositoryScope.launch {
            database.withTransaction {
                attemptDao.deleteAttempt(attempt)
                val remainingAttempts = attemptDao.getAttemptsForUser(currentUserId)
                val existingProfile = userProfileDao.getUserProfile(currentUserId)
                if (existingProfile != null) {
                    val updated = existingProfile.copy(
                        totalRescues = remainingAttempts.size,
                        successfulRescues = remainingAttempts.count { it.isSuccess }
                    )
                    userProfileDao.updateUserProfile(updated)
                    if (_userProfile.value.id == currentUserId) {
                        _userProfile.value = updated
                    }
                }
            }
        }
    }

    fun completeOnboarding() {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
        _hasCompletedOnboarding.value = true
    }

    fun resetOnboarding() {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, false).apply()
        _hasCompletedOnboarding.value = false
    }

    fun injectDemoData() {
        val currentUserId = _currentAccount.value?.id ?: _userProfile.value.id
        repositoryScope.launch {
            val now = System.currentTimeMillis()
            val demoAttempts = DatabaseSeedData.demoAttempts(now).map { it.copy(userId = currentUserId) }
            val demoMistakes = DatabaseSeedData.demoMistakes(now)
            database.withTransaction {
                attemptDao.deleteAllAttemptsForUser(currentUserId)
                mistakeDao.deleteAllMistakesForUser(currentUserId)
                attemptDao.insertAttempts(demoAttempts)
                mistakeDao.insertMistakes(demoMistakes)
                val current = userProfileDao.getUserProfile(currentUserId) ?: _userProfile.value
                val restoredProfile = current.copy(
                    totalRescues = demoAttempts.size,
                    successfulRescues = demoAttempts.count { it.isSuccess },
                    streakDays = 3
                )
                userProfileDao.updateUserProfile(restoredProfile)
                _userProfile.value = restoredProfile
            }
            _attempts.value = demoAttempts
            _mistakes.value = demoMistakes
            _mistakeStats.value = computeMistakeStats(demoMistakes)
        }
    }

    // DELETE operation (bulk): Clear recorded Attempts & Mistakes for the active user and reset counters to 0%
    fun resetProgress() {
        val currentUserId = _currentAccount.value?.id ?: _userProfile.value.id
        repositoryScope.launch {
            database.withTransaction {
                mistakeDao.deleteAllMistakesForUser(currentUserId)
                attemptDao.deleteAllAttemptsForUser(currentUserId)
                val current = userProfileDao.getUserProfile(currentUserId) ?: _userProfile.value
                val resetProfile = current.copy(
                    totalRescues = 0,
                    successfulRescues = 0,
                    streakDays = 0
                )
                userProfileDao.updateUserProfile(resetProfile)
                _userProfile.value = resetProfile
            }
            _attempts.value = emptyList()
            _mistakes.value = emptyList()
            _mistakeStats.value = MistakeCategory.entries.associateWith { 0 }
        }
    }
}
