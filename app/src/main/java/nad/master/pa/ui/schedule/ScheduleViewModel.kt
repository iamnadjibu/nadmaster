package nad.master.pa.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import nad.master.pa.data.model.Session
import nad.master.pa.data.repository.SessionRepository
import nad.master.pa.data.scheduler.SchedulingEngine
import nad.master.pa.data.scheduler.WeekInfo
import nad.master.pa.data.scheduler.RoutineSeeder
import javax.inject.Inject
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

data class ScheduleUiState(
    val isLoading: Boolean = true,
    val sessions: List<Session> = emptyList(),
    val selectedWeekInfo: WeekInfo? = null,
    val adjustmentMessage: String? = null,
    val error: String? = null,
    val canSeedRoutine: Boolean = false
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val schedulingEngine: SchedulingEngine,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    // Week offset from current week: 0 = current, -1 = last, +1 = next
    private val _weekOffset = MutableStateFlow(0)
    val weekOffset: StateFlow<Int> = _weekOffset.asStateFlow()

    // Available week range: [-4 .. +4]
    val weekRange = (-4..4).toList()

    init {
        checkSeedStatus()
        loadWeek(0)
    }

    private fun checkSeedStatus() {
        val prefs = context.getSharedPreferences("NadMasterPrefs", Context.MODE_PRIVATE)
        _uiState.value = _uiState.value.copy(canSeedRoutine = !prefs.getBoolean("has_seeded_routine", false))
    }

    fun seedRoutine() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            RoutineSeeder.seedRoutineIfFirstTime(context, sessionRepository)
            checkSeedStatus()
            loadWeek(_weekOffset.value)
        }
    }

    fun selectWeek(offset: Int) {
        _weekOffset.value = offset
        loadWeek(offset)
    }

    private fun loadWeek(offset: Int) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val weekInfo = schedulingEngine.getWeekLabel(offset)

        viewModelScope.launch {
            sessionRepository.getSessionsForWeek(weekInfo.weekId)
                .catch { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
                .collect { sessions ->
                    _uiState.value = ScheduleUiState(
                        isLoading        = false,
                        sessions         = sessions,
                        selectedWeekInfo = weekInfo,
                        error            = null
                    )
                }
        }
    }

    fun runScheduleAdjustment() {
        viewModelScope.launch {
            val result = schedulingEngine.analyzeAndAdjust(
                schedulingEngine.getWeekStart(0)
            )
            _uiState.value = _uiState.value.copy(adjustmentMessage = result.message)
        }
    }

    fun dismissAdjustmentMessage() {
        _uiState.value = _uiState.value.copy(adjustmentMessage = null)
    }

    fun getWeekInfo(offset: Int): WeekInfo = schedulingEngine.getWeekLabel(offset)
}
