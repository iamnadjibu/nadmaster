package nad.master.pa.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import nad.master.pa.data.model.Goal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val uid get() = auth.currentUser?.uid ?: ""
    private val goalsRef get() = firestore.collection("users").document(uid).collection("goals")

    /** Real-time stream of all active (not completed) goals. */
    fun getActiveGoals(): Flow<List<Goal>> = callbackFlow {
        val listener = goalsRef
            .whereEqualTo("isCompleted", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val goals = snapshot?.toObjects(Goal::class.java) ?: emptyList()
                trySend(goals)
            }
        awaitClose { listener.remove() }
    }

    /** Real-time stream of all completed goals. */
    fun getCompletedGoals(): Flow<List<Goal>> = callbackFlow {
        val listener = goalsRef
            .whereEqualTo("isCompleted", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val goals = snapshot?.toObjects(Goal::class.java) ?: emptyList()
                    .sortedByDescending { (it as Goal).completedDate }
                trySend(goals)
            }
        awaitClose { listener.remove() }
    }

    /** Add a new goal. */
    suspend fun addGoal(goal: Goal): String {
        val ref = goalsRef.document()
        ref.set(goal.copy(id = ref.id)).await()
        return ref.id
    }

    /** Update goal progress. */
    suspend fun updateGoalProgress(goalId: String, progress: Float) {
        goalsRef.document(goalId)
            .update("progress", progress)
            .await()
    }

    /** Mark goal as completed. */
    suspend fun markGoalCompleted(goalId: String, completedDate: String) {
        goalsRef.document(goalId)
            .update(mapOf(
                "isCompleted" to true,
                "completedDate" to completedDate,
                "progress" to 1.0f
            ))
            .await()
    }

    /** Update a goal in Firestore. */
    suspend fun updateGoal(goal: Goal) {
        goalsRef.document(goal.id).set(goal).await()
    }

    /** Delete a goal. */
    suspend fun deleteGoal(goalId: String) {
        goalsRef.document(goalId).delete().await()
    }

    /** Get goals that end this week (for weekly summary). */
    fun getGoalsForCurrentWeek(weekStartDate: String, weekEndDate: String): Flow<List<Goal>> = callbackFlow {
        val listener = goalsRef
            .whereGreaterThanOrEqualTo("startDate", weekStartDate)
            .whereLessThanOrEqualTo("endDate", weekEndDate)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val goals = snapshot?.toObjects(Goal::class.java) ?: emptyList()
                trySend(goals)
            }
        awaitClose { listener.remove() }
    }
}
