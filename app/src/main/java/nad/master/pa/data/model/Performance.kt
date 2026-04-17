package nad.master.pa.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Daily performance snapshot for NAD.
 * Stored under users/{uid}/performance/{date}
 */
data class DailyPerformance(
    @DocumentId
    val date: String = "",     // "yyyy-MM-dd"
    val sessionsScheduled: Int = 0,
    val sessionsCompleted: Int = 0,
    val sessionsMissed: Int = 0,
    val sessionsAdjusted: Int = 0,
    val goalsWorkedOn: Int = 0,
    val quranVersesMemorized: Int = 0,
    val prayersCompleted: Int = 0,   // Out of 5
    val tahajjudDone: Boolean = false,
    val disciplineScore: Float = 0f, // 0 to 100
    val notes: String = "",
    val createdAt: Timestamp = Timestamp.now()
) {
    val completionRate: Float
        get() = if (sessionsScheduled > 0) sessionsCompleted.toFloat() / sessionsScheduled else 0f

    val performanceLabel: String
        get() = when {
            disciplineScore >= 90 -> "Excellent"
            disciplineScore >= 75 -> "Good"
            disciplineScore >= 60 -> "Average"
            disciplineScore >= 40 -> "Below Average"
            else -> "Needs Improvement"
        }
}

/**
 * Weekly performance aggregation for charts.
 */
data class WeeklyPerformance(
    val weekId: String = "",          // "yyyy-Www"
    val weekLabel: String = "",       // "WEEK 0", "WEEK -1", etc.
    val startDate: String = "",
    val endDate: String = "",
    val totalSessions: Int = 0,
    val completedSessions: Int = 0,
    val missedSessions: Int = 0,
    val averageDisciplineScore: Float = 0f,
    val dailyPerformances: List<DailyPerformance> = emptyList()
) {
    val completionRate: Float
        get() = if (totalSessions > 0) completedSessions.toFloat() / totalSessions else 0f
}
