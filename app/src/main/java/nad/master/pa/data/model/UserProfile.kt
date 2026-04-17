package nad.master.pa.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * NAD's personal profile.
 * Stored under users/{uid}/profile/main
 */
data class UserProfile(
    @DocumentId
    val id: String = "",
    val uid: String = "",
    val name: String = "Nad",
    val email: String = "",
    val avatarUrl: String = "",
    val timezone: String = "Africa/Nairobi",
    val joinedDate: String = "",
    val weeklyGoalTarget: Int = 5,
    val dailySessionTarget: Int = 8,
    val currentStreak: Int = 0,      // Consecutive days on-schedule
    val longestStreak: Int = 0,
    val totalSessions: Int = 0,
    val totalCompleted: Int = 0,
    val fcmToken: String = "",
    val personalMessage: String = "Discipline is the bridge between goals and accomplishment.",
    val updatedAt: Timestamp = Timestamp.now()
) {
    val completionRate: Float
        get() = if (totalSessions > 0) totalCompleted.toFloat() / totalSessions else 0f
}

/**
 * Weekly personal report card.
 */
data class WeeklyReport(
    val weekId: String = "",
    val weekLabel: String = "WEEK 0",
    val startDate: String = "",
    val endDate: String = "",
    val highlights: List<String> = emptyList(),
    val totalSessions: Int = 0,
    val completedSessions: Int = 0,
    val missedSessions: Int = 0,
    val sessionsAdjusted: Int = 0,
    val goalsAchieved: Int = 0,
    val quranVersesCovered: Int = 0,
    val overallScore: Float = 0f,      // 0-100
    val motivationalMessage: String = "",
    val generatedAt: Timestamp = Timestamp.now()
) {
    val grade: String
        get() = when {
            overallScore >= 90 -> "A+"
            overallScore >= 80 -> "A"
            overallScore >= 70 -> "B"
            overallScore >= 60 -> "C"
            overallScore >= 50 -> "D"
            else -> "F"
        }
}
