package nad.master.pa.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import nad.master.pa.data.local.DisciplineQuotes
import nad.master.pa.data.local.HadithData
import nad.master.pa.data.model.DailyPerformance
import nad.master.pa.data.model.Goal
import nad.master.pa.data.model.Hadith
import nad.master.pa.data.model.QuranProgress
import nad.master.pa.data.model.Session
import nad.master.pa.data.model.SessionStatus
import nad.master.pa.data.repository.GoalRepository
import nad.master.pa.data.repository.PerformanceRepository
import nad.master.pa.data.repository.QuranRepository
import nad.master.pa.data.repository.SessionRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val todaySessions: List<Session> = emptyList(),
    val currentSession: Session? = null,
    val nextSession: Session? = null,
    val recentPerformance: List<DailyPerformance> = emptyList(),
    val weeklyCompletionRate: Float = 0f,
    val disciplineScore: Float = 0f,
    val motivationalQuote: DisciplineQuotes.DisciplineQuote? = null,
    val quranProgress: QuranProgress = QuranProgress(),
    val dailyHadith: Hadith = HadithData.getDailyHadith(),
    val todayGoals: List<Goal> = emptyList(),
    val weeklyGoals: List<Goal> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val goalRepository: GoalRepository,
    private val quranRepository: QuranRepository,
    private val performanceRepository: PerformanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        viewModelScope.launch {
            // Load sessions, goals, performance and quran progress in parallel via Flow combine
            combine(
                sessionRepository.getTodaySessions(),
                goalRepository.getActiveGoals(),
                quranRepository.getQuranProgress(),
                performanceRepository.getRecentPerformance(7)
            ) { sessions, goals, quranProgress, recentPerf ->

                val now = LocalDateTime.now()
                val currentSession = sessions.firstOrNull { session ->
                    session.status == SessionStatus.IN_PROGRESS
                }
                val nextSession = sessions.firstOrNull { session ->
                    session.status == SessionStatus.UPCOMING
                }

                val latestScore = recentPerf.lastOrNull()?.disciplineScore ?: 0f
                val weeklyRate  = if (recentPerf.isNotEmpty()) {
                    recentPerf.map { it.completionRate }.average().toFloat()
                } else 0f

                HomeUiState(
                    isLoading          = false,
                    todaySessions      = sessions,
                    currentSession     = currentSession,
                    nextSession        = nextSession,
                    recentPerformance  = recentPerf,
                    weeklyCompletionRate = weeklyRate,
                    disciplineScore    = latestScore,
                    motivationalQuote  = DisciplineQuotes.getQuoteForScore(latestScore),
                    quranProgress      = quranProgress,
                    dailyHadith        = HadithData.getDailyHadith(),
                    todayGoals         = goals.filter { !it.isCompleted }.take(5),
                    weeklyGoals        = goals.filter { !it.isCompleted },
                    error              = null
                )
            }
            .catch { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
            .collect { state -> _uiState.value = state }
        }
    }

    fun markSessionCompleted(sessionId: String) {
        viewModelScope.launch {
            sessionRepository.updateSessionStatus(sessionId, SessionStatus.COMPLETED)
        }
    }

    fun markSessionMissed(sessionId: String) {
        viewModelScope.launch {
            sessionRepository.updateSessionStatus(sessionId, SessionStatus.MISSED)
        }
    }

    fun updateGoalProgress(goalId: String, progress: Float) {
        viewModelScope.launch {
            goalRepository.updateGoalProgress(goalId, progress)
        }
    }
}
