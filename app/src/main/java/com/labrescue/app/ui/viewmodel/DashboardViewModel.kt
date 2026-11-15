package com.labrescue.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.labrescue.app.data.model.Attempt
import com.labrescue.app.data.model.Mistake
import com.labrescue.app.data.model.MistakeCategory
import com.labrescue.app.data.model.Scenario
import com.labrescue.app.data.model.Subject
import com.labrescue.app.data.model.UserAccount
import com.labrescue.app.data.model.UserProfile
import com.labrescue.app.data.repository.LabRescueRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(private val repository: LabRescueRepository) : ViewModel() {

    val isUserLoggedIn: StateFlow<Boolean> = repository.isUserLoggedIn
    val currentAccount: StateFlow<UserAccount?> = repository.currentAccount
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val hasCompletedOnboarding: StateFlow<Boolean> = repository.hasCompletedOnboarding

    val subjects: StateFlow<List<Subject>> = repository.subjects
    val recentAttempts: StateFlow<List<Attempt>> = repository.attempts
    val mistakes: StateFlow<List<Mistake>> = repository.mistakes
    val mistakeStats: StateFlow<Map<MistakeCategory, Int>> = repository.mistakeStats

    // Accuracy calculation: 0% when no attempts yet
    val diagnosticAccuracy: StateFlow<Int> = repository.attempts.map { attempts ->
        if (attempts.isEmpty()) 0
        else {
            val totalScore = attempts.sumOf { it.score }
            (totalScore / attempts.size).coerceIn(0, 100)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Recommended scenario logic: safe against empty scenarios list (prevents NoSuchElementException)
    val recommendedScenario: StateFlow<Scenario?> = repository.attempts.map { attempts ->
        val scenarios = com.labrescue.app.data.db.SampleData.scenarios
        val unattempted = scenarios.firstOrNull { sc ->
            attempts.none { it.scenarioId == sc.id }
        }
        unattempted ?: scenarios.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.labrescue.app.data.db.SampleData.scenarios.firstOrNull())

    suspend fun login(email: String, password: String): Result<UserAccount> {
        return repository.login(email, password)
    }

    suspend fun signUp(
        name: String,
        email: String,
        password: String,
        programme: String,
        year: String,
        targetGoal: String
    ): Result<UserAccount> {
        return repository.signUp(name, email, password, programme, year, targetGoal)
    }

    suspend fun loginAsDemoStudent(): Result<UserAccount> {
        return repository.loginAsDemoStudent()
    }

    fun logout() {
        repository.logout()
    }

    fun updateProfile(name: String, programme: String, year: String, targetGoal: String) {
        repository.updateProfile(name, programme, year, targetGoal)
    }

    fun completeOnboarding() {
        repository.completeOnboarding()
    }

    fun replayTour() {
        repository.resetOnboarding()
    }

    fun deleteAttempt(attempt: Attempt) {
        repository.deleteAttempt(attempt)
    }

    fun getScenario(scenarioId: String): Scenario? {
        return repository.getScenario(scenarioId)
    }

    fun getSubjectCodeForScenario(scenarioId: String): String {
        val subjectId = repository.getScenario(scenarioId)?.subjectId ?: return "LAB"
        return repository.subjects.value.find { it.id == subjectId }?.code ?: "LAB"
    }

    fun resetData() {
        repository.resetProgress()
    }

    fun injectDemoData() {
        repositoryScopeInjectDemoData()
    }

    private fun repositoryScopeInjectDemoData() {
        repository.injectDemoData()
    }
}

class DashboardViewModelFactory(private val repository: LabRescueRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
