package nad.master.pa.ui.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nad.master.pa.data.model.Goal
import nad.master.pa.data.model.GoalCategory
import nad.master.pa.data.model.GoalPriority
import nad.master.pa.data.repository.GoalRepository
import nad.master.pa.data.repository.SessionRepository
import nad.master.pa.data.ai.AiAssistantEngine
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val activeGoals: List<Goal> = emptyList(),
    val completedGoals: List<Goal> = emptyList(),
    val showAddGoalSheet: Boolean = false,
    val error: String? = null,
    val showAiSheet: Boolean = false,
    val isAiLoading: Boolean = false,
    val disciplineInsight: String? = null
)

data class NewGoalForm(
    val title: String = "",
    val description: String = "",
    val category: GoalCategory = GoalCategory.PERSONAL,
    val priority: GoalPriority = GoalPriority.MEDIUM,
    val startDate: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val endDate: String = LocalDate.now().plusWeeks(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val sessionRepository: SessionRepository,
    private val aiAssistantEngine: AiAssistantEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _goalForm = MutableStateFlow(NewGoalForm())
    val goalForm: StateFlow<NewGoalForm> = _goalForm.asStateFlow()

    private val _aiRequest = MutableStateFlow("")
    val aiRequest: StateFlow<String> = _aiRequest.asStateFlow()

    init {
        loadGoals()
        loadDisciplineInsight()
    }

    private fun loadDisciplineInsight() {
        viewModelScope.launch {
            try {
                val missed = sessionRepository.getMissedSessionsSince(7)
                val completed = sessionRepository.getCompletedSessionsSince(7)
                val insight = aiAssistantEngine.analyzeDiscipline(completed, missed)
                _uiState.value = _uiState.value.copy(disciplineInsight = insight)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(disciplineInsight = "DEBUG ERROR (ViewModel): ${e.localizedMessage}")
            }
        }
    }

    private fun loadGoals() {
        viewModelScope.launch {
            combine(
                goalRepository.getActiveGoals(),
                goalRepository.getCompletedGoals()
            ) { active, completed ->
                DashboardUiState(
                    isLoading      = false,
                    activeGoals    = active.sortedByDescending { it.createdAt.seconds },
                    completedGoals = completed.sortedByDescending { it.completedDate },
                    error          = null
                )
            }
            .catch { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
            .collect { state -> _uiState.value = state }
        }
    }

    fun showAddGoalSheet()  { _uiState.value = _uiState.value.copy(showAddGoalSheet = true) }
    fun hideAddGoalSheet()  { _uiState.value = _uiState.value.copy(showAddGoalSheet = false); _goalForm.value = NewGoalForm() }

    fun showAiSheet() { _uiState.value = _uiState.value.copy(showAiSheet = true) }
    fun hideAiSheet() { _uiState.value = _uiState.value.copy(showAiSheet = false, isAiLoading = false); _aiRequest.value = "" }
    fun updateAiRequest(req: String) { _aiRequest.value = req }

    fun submitAiRequest() {
        val req = _aiRequest.value
        if (req.isBlank()) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAiLoading = true)
            try {
                // Fetch upcoming sessions for a 2-week range to provide broad context
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val futureLimit = LocalDate.now().plusWeeks(2).format(DateTimeFormatter.ISO_LOCAL_DATE)
                val existingSessions = sessionRepository.getSessionsInRange(today, futureLimit)
                
                Log.d("DashboardVM", "submitAiRequest: Sending to Gemini with range context")
                val generatedSessions = aiAssistantEngine.scheduleGoal(req, existingSessions)
                
                if (generatedSessions.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isAiLoading = false, 
                        error = "Assistant couldn't find a way to schedule that. Try being more specific about the time or goal."
                    )
                    return@launch
                }

                generatedSessions.forEach { session ->
                    sessionRepository.addSession(session)
                }
                hideAiSheet()
            } catch (e: Exception) {
                Log.e("DashboardVM", "submitAiRequest: FAILED", e)
                val userError = when {
                    e.message?.contains("API_KEY_INVALID", true) == true -> "AI API Key is invalid. Check local.properties."
                    e.message?.contains("quota", true) == true -> "AI quota exceeded. Please try again later."
                    else -> "AI Assistant Error: ${e.localizedMessage ?: "Unknown failure"}"
                }
                _uiState.value = _uiState.value.copy(isAiLoading = false, error = userError)
            }
        }
    }

    fun updateFormTitle(value: String)       { _goalForm.value = _goalForm.value.copy(title = value) }
    fun updateFormDescription(value: String) { _goalForm.value = _goalForm.value.copy(description = value) }
    fun updateFormCategory(value: GoalCategory) { _goalForm.value = _goalForm.value.copy(category = value) }
    fun updateFormPriority(value: GoalPriority) { _goalForm.value = _goalForm.value.copy(priority = value) }
    fun updateFormStartDate(value: String)   { _goalForm.value = _goalForm.value.copy(startDate = value) }
    fun updateFormEndDate(value: String)     { _goalForm.value = _goalForm.value.copy(endDate = value) }

    fun submitGoal() {
        val form = _goalForm.value
        if (form.title.isBlank()) return
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                Log.d("DashboardVM", "submitGoal: Adding goal '${form.title}' until ${form.endDate}")
                goalRepository.addGoal(
                    Goal(
                        title       = form.title,
                        description = form.description,
                        category    = form.category,
                        priority    = form.priority,
                        startDate   = form.startDate,
                        endDate     = form.endDate
                    )
                )
                
                // Fetch sessions from start to end of goal for context
                val existingRange = sessionRepository.getSessionsInRange(form.startDate, form.endDate)
                
                // Automatically ask AI to plan this goal until the deadline
                val aiPrompt = "I have a new goal: ${form.title}. ${form.description}. " +
                               "It starts on ${form.startDate} and must be completed by ${form.endDate}. " +
                               "Please plan reasonable sessions across this entire period to ensure I finish by the deadline."
                
                val generatedSessions = aiAssistantEngine.scheduleGoal(aiPrompt, existingRange)
                generatedSessions.forEach { session ->
                    sessionRepository.addSession(session)
                }
                
                Log.d("DashboardVM", "submitGoal: Goal added and multi-week AI sessions planned")
                hideAddGoalSheet()
            } catch (e: Exception) {
                Log.e("DashboardVM", "submitGoal: FAILED", e)
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun updateGoalProgress(goalId: String, progress: Float) {
        viewModelScope.launch { goalRepository.updateGoalProgress(goalId, progress) }
    }

    fun completeGoal(goalId: String) {
        viewModelScope.launch {
            goalRepository.markGoalCompleted(
                goalId,
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            )
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch { goalRepository.deleteGoal(goalId) }
    }
}
