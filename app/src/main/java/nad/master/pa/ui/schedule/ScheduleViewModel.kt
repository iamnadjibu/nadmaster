package nad.master.pa.ui.schedule

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import nad.master.pa.data.model.Session
import nad.master.pa.data.repository.SessionRepository
import nad.master.pa.data.scheduler.RoutineSeeder
import nad.master.pa.data.scheduler.SchedulingEngine
import nad.master.pa.data.scheduler.WeekInfo
import javax.inject.Inject

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

    companion object {
        private const val TAG = "ScheduleVM"
    }

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    private val _weekOffset = MutableStateFlow(0)
    val weekOffset: StateFlow<Int> = _weekOffset.asStateFlow()

    val weekRange = (-4..4).toList()

    init {
        checkSeedStatus()
        loadWeek(0)
    }

    private fun checkSeedStatus() {
        val prefs = context.getSharedPreferences("NadMasterPrefs", Context.MODE_PRIVATE)
        val hasSeeded = prefs.getBoolean("has_seeded_routine", false)
        Log.d(TAG, "checkSeedStatus: has_seeded_routine=$hasSeeded")
        _uiState.value = _uiState.value.copy(canSeedRoutine = !hasSeeded)
    }

    fun seedRoutine() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                Log.d(TAG, "seedRoutine: Starting seed…")
                RoutineSeeder.seedRoutineIfFirstTime(context, sessionRepository)
                Log.d(TAG, "seedRoutine: Seed completed successfully")
                checkSeedStatus()
            } catch (e: Exception) {
                Log.e(TAG, "seedRoutine: FAILED", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Seed failed: ${e.message}"
                )
            } finally {
                loadWeek(_weekOffset.value)
            }
        }
    }

    fun selectWeek(offset: Int) {
        _weekOffset.value = offset
        loadWeek(offset)
    }

    private fun loadWeek(offset: Int) {
        // preserve canSeedRoutine across reloads
        val currentSeedStatus = _uiState.value.canSeedRoutine
        _uiState.value = _uiState.value.copy(isLoading = true)
        val weekInfo = schedulingEngine.getWeekLabel(offset)
        Log.d(TAG, "loadWeek: offset=$offset weekId=${weekInfo.weekId}")

        viewModelScope.launch {
            sessionRepository.getSessionsForWeek(weekInfo.weekId)
                .catch { e ->
                    Log.e(TAG, "loadWeek: Firestore error", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Load failed: ${e.message}"
                    )
                }
                .collect { sessions ->
                    Log.d(TAG, "loadWeek: Received ${sessions.size} sessions for ${weekInfo.weekId}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        sessions = sessions,
                        selectedWeekInfo = weekInfo,
                        canSeedRoutine = currentSeedStatus,
                        error = null
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

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun getWeekInfo(offset: Int): WeekInfo = schedulingEngine.getWeekLabel(offset)

    fun confirmSession(sessionId: String) {
        viewModelScope.launch {
            try {
                // Clear the needsConfirmation flag
                val session = _uiState.value.sessions.find { it.id == sessionId } ?: return@launch
                sessionRepository.updateSession(session.copy(needsConfirmation = false))
            } catch (e: Exception) {
                Log.e(TAG, "confirmSession: FAILED", e)
            }
        }
    }

    fun declineSession(sessionId: String) {
        viewModelScope.launch {
            try {
                sessionRepository.deleteSession(sessionId)
            } catch (e: Exception) {
                Log.e(TAG, "declineSession: FAILED", e)
            }
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            try {
                sessionRepository.deleteSession(sessionId)
            } catch (e: Exception) {
                Log.e(TAG, "deleteSession: FAILED", e)
            }
        }
    }
}

