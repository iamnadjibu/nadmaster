package nad.master.pa.ui.quran

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import nad.master.pa.data.local.QuranData
import nad.master.pa.data.model.QuranProgress
import nad.master.pa.data.model.SurahData
import nad.master.pa.data.model.SurahStatus
import nad.master.pa.data.model.SurahTracking
import nad.master.pa.data.repository.QuranRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class QuranUiState(
    val isLoading: Boolean = true,
    val progress: QuranProgress = QuranProgress(),
    val surahTrackingMap: Map<Int, SurahTracking> = emptyMap(),
    val expandedJuzz: Int? = null,
    val error: String? = null
) {
    val overallPercent: Float get() = progress.overallPercent
    val daysAhead: Int get() {
        // Calculate where NAD should be based on startDate and dailyVerseTarget
        if (progress.startDate.isBlank()) return 0
        val start = LocalDate.parse(progress.startDate)
        val daysPassed = LocalDate.now().toEpochDay() - start.toEpochDay()
        val expectedVerses = daysPassed * progress.dailyVerseTarget
        return ((expectedVerses - progress.versesMemorized) / progress.dailyVerseTarget.coerceAtLeast(1)).toInt()
    }
}

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val quranRepository: QuranRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuranUiState())
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    init {
        loadQuranData()
    }

    private fun loadQuranData() {
        viewModelScope.launch {
            combine(
                quranRepository.getQuranProgress(),
                quranRepository.getSurahTrackingList()
            ) { progress, trackingList ->
                val trackingMap = trackingList.associateBy { it.surahNumber }
                QuranUiState(
                    isLoading       = false,
                    progress        = progress,
                    surahTrackingMap = trackingMap,
                    error           = null
                )
            }
            .catch { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
            .collect { state -> _uiState.value = state }
        }
    }

    fun toggleJuzzExpansion(juzz: Int) {
        _uiState.value = _uiState.value.copy(
            expandedJuzz = if (_uiState.value.expandedJuzz == juzz) null else juzz
        )
    }

    fun getStatusForSurah(surahNumber: Int): SurahStatus {
        return _uiState.value.surahTrackingMap[surahNumber]?.status ?: SurahStatus.NOT_STARTED
    }

    fun markSurahCompleted(surahData: SurahData) {
        viewModelScope.launch {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            quranRepository.markSurahCompleted(surahData.number, today)
            // Update overall progress
            val completedCount = _uiState.value.surahTrackingMap
                .values.count { it.status == SurahStatus.COMPLETED } + 1
            val totalVersesCovered = QuranData.ALL_SURAHS
                .filter { s ->
                    _uiState.value.surahTrackingMap[s.number]?.status == SurahStatus.COMPLETED ||
                    s.number == surahData.number
                }
                .sumOf { it.verseCount }
            quranRepository.updateQuranProgress(
                _uiState.value.progress.copy(
                    surahsCompleted   = completedCount,
                    versesMemorized   = totalVersesCovered,
                    lastSessionDate   = today
                )
            )
        }
    }

    fun setCurrentSurah(surahData: SurahData) {
        viewModelScope.launch {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            quranRepository.setCurrentSurah(surahData.number, today)
            quranRepository.updateQuranProgress(
                _uiState.value.progress.copy(
                    currentSurahNumber = surahData.number,
                    currentSurahName   = surahData.nameEnglish,
                    currentJuzz        = surahData.juzz,
                    lastSessionDate    = today
                )
            )
        }
    }
}
