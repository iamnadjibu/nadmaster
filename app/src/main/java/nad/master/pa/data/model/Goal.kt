package nad.master.pa.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

/**
 * Represents a personal goal for NAD.
 * Stored under users/{uid}/goals/{goalId}
 */
data class Goal(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: GoalCategory = GoalCategory.PERSONAL,
    val startDate: String = "",   // "yyyy-MM-dd"
    val endDate: String = "",     // "yyyy-MM-dd"
    val progress: Float = 0f,     // 0.0 to 1.0
    val isCompleted: Boolean = false,
    val completedDate: String? = null,
    val priority: GoalPriority = GoalPriority.MEDIUM,
    val milestones: List<Milestone> = emptyList(),
    val sessionIds: List<String> = emptyList(), // Linked sessions
    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var createdAt: Timestamp = Timestamp.now(),
    @get:PropertyName("updated_at")
    @set:PropertyName("updated_at")
    var updatedAt: Timestamp = Timestamp.now()
)

data class Milestone(
    val id: String = "",
    val title: String = "",
    val isCompleted: Boolean = false,
    val dueDate: String = ""
)

enum class GoalCategory {
    ACADEMIC,
    FITNESS,
    SPIRITUAL,
    PERSONAL,
    PROFESSIONAL,
    FINANCIAL,
    HEALTH,
    SOCIAL,
    CREATIVE
}

enum class GoalPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Represents a weekly performance summary for the goals.
 */
data class WeeklyGoalSummary(
    val weekId: String = "",
    val totalGoals: Int = 0,
    val completedGoals: Int = 0,
    val inProgressGoals: Int = 0,
    val missedGoals: Int = 0,
    val overallProgress: Float = 0f
)
