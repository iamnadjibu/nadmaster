package nad.master.pa.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import nad.master.pa.data.model.DailyPerformance
import nad.master.pa.data.model.UserProfile
import nad.master.pa.data.model.WeeklyReport
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformanceRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val uid get() = auth.currentUser?.uid ?: ""
    private val userDoc get() = firestore.collection("users").document(uid)

    /** Get performance for the past N days (for charts). */
    fun getRecentPerformance(daysBack: Int = 7): Flow<List<DailyPerformance>> = callbackFlow {
        val cutoff = LocalDate.now().minusDays(daysBack.toLong())
            .format(DateTimeFormatter.ISO_LOCAL_DATE)
        val listener = userDoc.collection("performance")
            .whereGreaterThanOrEqualTo("date", cutoff)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val list = snapshot?.toObjects(DailyPerformance::class.java)
                    ?.sortedBy { it.date }
                    ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    /** Save or update today's performance snapshot. */
    suspend fun saveDailyPerformance(performance: DailyPerformance) {
        userDoc.collection("performance")
            .document(performance.date)
            .set(performance, SetOptions.merge())
            .await()
    }

    /** Compute today's performance from session data and save it. */
    suspend fun computeAndSaveDailyPerformance(
        scheduled: Int, completed: Int, missed: Int, unfinished: Int,
        prayers: Int, tahajjud: Boolean, quranVerses: Int
    ) {
        val date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val score = computeDisciplineScore(scheduled, completed, missed, unfinished, prayers, tahajjud)
        val perf = DailyPerformance(
            date = date,
            sessionsScheduled = scheduled,
            sessionsCompleted = completed,
            sessionsMissed = missed,
            prayersCompleted = prayers,
            tahajjudDone = tahajjud,
            quranVersesMemorized = quranVerses,
            disciplineScore = score
        )
        saveDailyPerformance(perf)
    }

    private fun computeDisciplineScore(
        scheduled: Int, completed: Int, missed: Int, unfinished: Int,
        prayers: Int, tahajjud: Boolean
    ): Float {
        if (scheduled == 0) return 0f
        // Unfinished counts as 0.5 of a completion
        val effectiveCompleted = completed + (unfinished * 0.5f)
        val sessionScore = (effectiveCompleted / scheduled.coerceAtLeast(1)) * 60f  // 60% weight
        val prayerScore  = (prayers.toFloat() / 5f) * 30f                             // 30% weight
        val tahajjudBonus = if (tahajjud) 10f else 0f                                 // 10% bonus
        return (sessionScore + prayerScore + tahajjudBonus).coerceIn(0f, 100f)
    }

    /** User profile real-time stream. */
    fun getUserProfile(): Flow<UserProfile> = callbackFlow {
        val listener = userDoc.collection("profile").document("main")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val profile = snapshot?.toObject(UserProfile::class.java) ?: UserProfile()
                trySend(profile)
            }
        awaitClose { listener.remove() }
    }

    /** Save/update user profile. */
    suspend fun saveUserProfile(profile: UserProfile) {
        userDoc.collection("profile").document("main")
            .set(profile, SetOptions.merge())
            .await()
    }

    /** Get weekly report. */
    fun getWeeklyReport(weekId: String): Flow<WeeklyReport?> = callbackFlow {
        val listener = userDoc.collection("weeklyReports").document(weekId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val report = snapshot?.toObject(WeeklyReport::class.java)
                trySend(report)
            }
        awaitClose { listener.remove() }
    }
}
