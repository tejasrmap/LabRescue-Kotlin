package com.labrescue.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.labrescue.app.data.model.Attempt
import com.labrescue.app.data.model.DiagnosticOption
import com.labrescue.app.data.model.Mistake
import com.labrescue.app.data.model.Scenario
import com.labrescue.app.data.model.ScenarioNode
import com.labrescue.app.data.repository.LabRescueRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class DiagnosticViewModel(
    private val scenarioId: String,
    private val repository: LabRescueRepository
) : ViewModel() {

    val scenario: Scenario? = repository.getScenario(scenarioId)

    private val _currentNode = MutableStateFlow<ScenarioNode?>(null)
    val currentNode: StateFlow<ScenarioNode?> = _currentNode.asStateFlow()

    private val _selectedOption = MutableStateFlow<DiagnosticOption?>(null)
    val selectedOption: StateFlow<DiagnosticOption?> = _selectedOption.asStateFlow()

    private val _evidenceRevealed = MutableStateFlow(false)
    val evidenceRevealed: StateFlow<Boolean> = _evidenceRevealed.asStateFlow()

    private val _score = MutableStateFlow(100)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private val _isCompleted = MutableStateFlow(false)
    val isCompleted: StateFlow<Boolean> = _isCompleted.asStateFlow()

    private val _showHint = MutableStateFlow(false)
    val showHint: StateFlow<Boolean> = _showHint.asStateFlow()

    private val _lastGeneratedAttemptId = MutableStateFlow<String?>(null)
    val lastGeneratedAttemptId: StateFlow<String?> = _lastGeneratedAttemptId.asStateFlow()

    private val collectedMistakes = mutableListOf<Mistake>()
    private val decisionPath = mutableListOf<String>()
    private val evaluatedNodeIds = mutableSetOf<String>()
    private var timerJob: Job? = null

    init {
        scenario?.let {
            _currentNode.value = repository.getNode(it.startNodeId)
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _elapsedSeconds.value += 1
            }
        }
    }

    fun selectOption(option: DiagnosticOption) {
        val node = _currentNode.value ?: return
        if (_selectedOption.value?.id == option.id) {
            return
        }
        _selectedOption.value = option
        _evidenceRevealed.value = true

        // Only evaluate penalty and record decision once per step to prevent duplicate deduction exploit
        if (!evaluatedNodeIds.contains(node.id)) {
            evaluatedNodeIds.add(node.id)
            decisionPath.add(option.id)

            if (!option.isCorrectPath) {
                // Deduct 25 points per incorrect diagnostic check
                _score.value = (_score.value - 25).coerceAtLeast(10)
                option.penaltyCategory?.let { category ->
                    collectedMistakes.add(
                        Mistake(
                            id = UUID.randomUUID().toString(),
                            attemptId = "",
                            scenarioId = scenarioId,
                            category = category,
                            description = option.whyExplanation
                        )
                    )
                }
            }
        }
    }

    fun proceedToNextStep() {
        val currentOpt = _selectedOption.value ?: return

        if (currentOpt.nextNodeId != null) {
            // Advance to next branch in decision tree
            _currentNode.value = repository.getNode(currentOpt.nextNodeId)
            _selectedOption.value = null
            _evidenceRevealed.value = false
            _showHint.value = false
        } else {
            // Final terminal node reached - complete the diagnostic
            completeDiagnostic()
        }
    }

    private fun completeDiagnostic() {
        timerJob?.cancel()
        val attemptId = "att_" + UUID.randomUUID().toString().take(8)
        _lastGeneratedAttemptId.value = attemptId

        val mistakesWithAttemptId = collectedMistakes.map { it.copy(attemptId = attemptId) }
        val currentUserId = repository.currentAccount.value?.id ?: repository.userProfile.value.id
        val attempt = Attempt(
            id = attemptId,
            userId = currentUserId,
            scenarioId = scenarioId,
            scenarioTitle = scenario?.title ?: "Scenario",
            subjectId = scenario?.subjectId ?: "general",
            score = _score.value,
            durationSeconds = _elapsedSeconds.value,
            completedAt = System.currentTimeMillis(),
            isSuccess = _score.value >= 50,
            mistakesCount = mistakesWithAttemptId.size,
            decisionsPathJson = decisionPath.joinToString(",", "[", "]") { "\"$it\"" }
        )

        repository.recordAttempt(attempt, mistakesWithAttemptId)
        _isCompleted.value = true
    }

    fun toggleHint() {
        _showHint.value = !_showHint.value
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

class DiagnosticViewModelFactory(
    private val scenarioId: String,
    private val repository: LabRescueRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiagnosticViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DiagnosticViewModel(scenarioId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
