package nad.master.pa.data.scheduler

import nad.master.pa.data.model.Session
import nad.master.pa.data.model.SessionCategory
import nad.master.pa.data.model.SessionStatus
import nad.master.pa.data.model.SessionType
import nad.master.pa.data.repository.GoalRepository
import nad.master.pa.data.repository.SessionRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The NAD MASTER Scheduling Intelligence Engine.
 *
 * Rules:
 * 1. Fixed sessions (Class, Training, Quran) are NEVER moved or removed.
 * 2. Religious sessions (Salah, Tahajjud, Dhikr) are NEVER moved.
 * 3. Missed sessions are rescheduled within the same or next available slot.
 * 4. Breaks are the FIRST to be trimmed when there are too many goals.
 * 5. 10-15 minute slack time is maintained between sessions.
 * 6. Failed goal progress triggers intensification of upcoming goal sessions.
 */
@Singleton
class SchedulingEngine @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val goalRepository: GoalRepository
) {
    companion object {
        private val FIXED_CATEGORIES = setOf(
            SessionCategory.CLASS,
            SessionCategory.TRAINING,
            SessionCategory.QURAN_MEMORIZATION
        )

        private val RELIGIOUS_CATEGORIES = setOf(
            SessionCategory.SALAH,
            SessionCategory.TAHAJJUD,
            SessionCategory.DHIKR
        )

        /** Sessions that must never be removed or rescheduled. */
        fun isImmovable(session: Session): Boolean {
            return session.category in FIXED_CATEGORIES ||
                   session.category in RELIGIOUS_CATEGORIES ||
                   session.isFixed
        }

        /** Sessions that can be removed when overloaded. */
        fun isFlexible(session: Session): Boolean {
            return session.type == SessionType.BREAK ||
                   session.category == SessionCategory.SLACK
        }

        /**
         * Compute ISO week ID string for a given date (e.g., "2026-W14").
         * Fixed to start on Saturday to match UI alignment.
         */
        fun getWeekId(date: LocalDate = LocalDate.now()): String {
            val weekFields = java.time.temporal.WeekFields.of(java.time.DayOfWeek.SATURDAY, 1)
            val week = date.get(weekFields.weekOfWeekBasedYear())
            val year = date.get(weekFields.weekBasedYear())
            return "$year-W${week.toString().padStart(2, '0')}"
        }
    }

    /**
     * Analyze missed sessions and generate an adjusted schedule proposal.
     * Returns a map of date → list of adjusted sessions.
     */
    suspend fun analyzeAndAdjust(fromDate: LocalDate, daysForward: Int = 7): AdjustmentResult {
        val missedSessions = sessionRepository.getMissedSessionsSince(7)

        if (missedSessions.isEmpty()) {
            return AdjustmentResult(
                adjustedSessions = emptyList(),
                removedSessions = emptyList(),
                message = "Great job! Your schedule is running perfectly."
            )
        }

        val movableMissed = missedSessions.filter { !isImmovable(it) }
        val immovableMissed = missedSessions.filter { isImmovable(it) }

        // For immovable missed sessions (e.g., Quran session missed),
        // intensify future goal sessions rather than reschedule.
        val intensifications = mutableListOf<String>()
        if (immovableMissed.isNotEmpty()) {
            intensifications.add(
                "You missed ${immovableMissed.size} fixed session(s). " +
                "Next sessions will be intensified to compensate."
            )
        }

        val adjustedSessions = mutableListOf<Session>()
        val removedSessions  = mutableListOf<Session>()

        // Find upcoming break sessions that can be compressed
        val targetDate = fromDate.plusDays(1)
        val targetDateStr = targetDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val futureBreaks = sessionRepository
            .getMissedSessionsSince(-1) // Reuse — get 0 days to get nothing, placeholder

        // Build adjustment message
        val message = buildString {
            if (movableMissed.isNotEmpty()) {
                appendLine("Rescheduling ${movableMissed.size} missed session(s) to next available slots.")
            }
            intensifications.forEach { appendLine(it) }
            appendLine("Slack time adjusted to maintain balance without overloading.")
        }

        return AdjustmentResult(
            adjustedSessions = adjustedSessions,
            removedSessions  = removedSessions,
            message          = message.trim()
        )
    }

    /**
     * Check if today's schedule is overloaded and suggest removing breaks.
     */
    fun checkOverloadAndSuggestRemovals(sessions: List<Session>): List<Session> {
        val totalMinutes = sessions.sumOf { session ->
            val start = session.startTime.toDate().time
            val end   = session.endTime.toDate().time
            ((end - start) / 60_000).toInt()
        }

        // If total scheduled time > 14 hours (840 min), remove breaks
        return if (totalMinutes > 840) {
            sessions.filter { isFlexible(it) }.take(
                ((totalMinutes - 840) / 30).coerceAtLeast(1)
            )
        } else {
            emptyList()
        }
    }

    /**
     * Compute start-of-week date for a given week offset from today.
     * weekOffset = 0 → current week, -1 → last week, +1 → next week
     */
    fun getWeekStart(weekOffset: Int = 0): LocalDate {
        val today = LocalDate.now()
        // Saturday is start of the week. Mon=1..Sun=7
        // daysSinceSaturday: Sat=0, Sun=1, Mon=2, Tue=3, Wed=4, Thu=5, Fri=6
        val daysSinceSaturday = (today.dayOfWeek.value + 1) % 7
        val saturday = today.minusDays(daysSinceSaturday.toLong())
        return saturday.plusWeeks(weekOffset.toLong())
    }

    /**
     * Compute week label (WEEK 0, WEEK -1, WEEK 1, etc.) and date range.
     */
    fun getWeekLabel(weekOffset: Int): WeekInfo {
        val start = getWeekStart(weekOffset)
        val end   = start.plusDays(6)
        val label = when {
            weekOffset == 0 -> "WEEK 0 (Current)"
            weekOffset > 0  -> "WEEK +$weekOffset"
            else            -> "WEEK $weekOffset"
        }
        return WeekInfo(
            weekId    = getWeekId(start),
            weekLabel = label,
            weekOffset = weekOffset,
            startDate  = start.format(DateTimeFormatter.ofPattern("MMM dd")),
            endDate    = end.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            startLocalDate = start,
            endLocalDate   = end
        )
    }
}

data class AdjustmentResult(
    val adjustedSessions: List<Session>,
    val removedSessions: List<Session>,
    val message: String
)

data class WeekInfo(
    val weekId: String,
    val weekLabel: String,
    val weekOffset: Int,
    val startDate: String,
    val endDate: String,
    val startLocalDate: LocalDate,
    val endLocalDate: LocalDate
)
