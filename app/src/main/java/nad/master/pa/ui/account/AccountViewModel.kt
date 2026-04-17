package nad.master.pa.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import nad.master.pa.data.local.HadithData
import nad.master.pa.data.model.Goal
import nad.master.pa.data.model.Hadith
import nad.master.pa.data.model.UserProfile
import nad.master.pa.data.model.WeeklyReport
import nad.master.pa.data.repository.GoalRepository
import nad.master.pa.data.repository.PerformanceRepository
import nad.master.pa.data.scheduler.SchedulingEngine
import javax.inject.Inject

data class AccountUiState(
    val isLoading: Boolean = true,
    val profile: UserProfile = UserProfile(),
    val weeklyReport: WeeklyReport? = null,
    val weeklyGoals: List<Goal> = emptyList(),
    val dailyHadith: Hadith = HadithData.getDailyHadith(),
    val error: String? = null
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val performanceRepository: PerformanceRepository,
    private val goalRepository: GoalRepository,
    private val schedulingEngine: SchedulingEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    init {
        loadAccountData()
    }

    private fun loadAccountData() {
        val currentWeek = schedulingEngine.getWeekLabel(0)

        viewModelScope.launch {
            combine(
                performanceRepository.getUserProfile(),
                performanceRepository.getWeeklyReport(currentWeek.weekId),
                goalRepository.getActiveGoals()
            ) { profile, report, goals ->
                AccountUiState(
                    isLoading    = false,
                    profile      = profile,
                    weeklyReport = report,
                    weeklyGoals  = goals.take(5),
                    dailyHadith  = HadithData.getDailyHadith(),
                    error        = null
                )
            }
            .catch { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
            .collect { state -> _uiState.value = state }
        }
    }
}
