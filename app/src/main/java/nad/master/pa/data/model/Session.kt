package nad.master.pa.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

/**
 * Represents a scheduled session in NAD's daily routine.
 * Sessions are stored under users/{uid}/sessions/{sessionId}
 */
data class Session(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val type: SessionType = SessionType.FLEXIBLE,
    val category: SessionCategory = SessionCategory.PERSONAL_GOALS,
    val startTime: Timestamp = Timestamp.now(),
    val endTime: Timestamp = Timestamp.now(),
    val date: String = "",          // "yyyy-MM-dd"
    val weekId: String = "",        // "yyyy-Www" (ISO week)
    val status: SessionStatus = SessionStatus.UPCOMING,
    val isFixed: Boolean = false,
    val needsConfirmation: Boolean = false,
    val isUserAdded: Boolean = false,
    val colorCode: String = "#D5CEA3",
    val notes: String = "",
    val notifyMinutesBefore: Int = 15,
    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var createdAt: Timestamp = Timestamp.now()
)

enum class SessionType {
    FIXED,      // Cannot be rescheduled (class, training, Quran)
    FLEXIBLE,   // Can be adjusted (study, work sessions)
    BREAK,      // Relaxation/slack time
    RELIGIOUS   // Salah, Tahajjud, Adhkar
}

enum class SessionCategory {
    CLASS,
    TRAINING,
    QURAN_MEMORIZATION,
    STUDY,
    PERSONAL_GOALS,
    SALAH,
    TAHAJJUD,
    DHIKR,
    BREAK,
    SLACK,
    OTHER
}

enum class SessionStatus {
    UPCOMING,
    IN_PROGRESS,
    COMPLETED,
    MISSED,
    UNFINISHED,
    ADJUSTED,   // Was rescheduled by the engine
    CANCELLED
}

/**
 * Extension to determine the display color of a session.
 */
fun Session.getSessionColor(): Long {
    return when {
        category == SessionCategory.SALAH ||
        category == SessionCategory.TAHAJJUD ||
        category == SessionCategory.DHIKR ||
        category == SessionCategory.QURAN_MEMORIZATION -> 0xFF4CAF50L  // Islamic Green
        type == SessionType.FIXED && category == SessionCategory.CLASS -> 0xFFE05263L     // Critical Red
        type == SessionType.FIXED && category == SessionCategory.TRAINING -> 0xFFE05263L  // Critical Red
        type == SessionType.BREAK || type == SessionType.FLEXIBLE.also {
            category == SessionCategory.SLACK
        } -> 0xFF64B5F6L   // Info Blue
        category == SessionCategory.PERSONAL_GOALS ||
        category == SessionCategory.STUDY -> 0xFFFFC107L  // Warning Amber
        else -> 0xFFD5CEA3L  // Default Cream
    }
}
