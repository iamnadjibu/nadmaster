package nad.master.pa.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import nad.master.pa.data.model.Goal
import nad.master.pa.data.model.GoalCategory
import nad.master.pa.data.model.GoalPriority
import nad.master.pa.data.repository.GoalRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val activeGoals: List<Goal> = emptyList(),
    val completedGoals: List<Goal> = emptyList(),
    val showAddGoalSheet: Boolean = false,
    val error: String? = null
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
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _goalForm = MutableStateFlow(NewGoalForm())
    val goalForm: StateFlow<NewGoalForm> = _goalForm.asStateFlow()

    init {
        loadGoals()
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
            hideAddGoalSheet()
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
