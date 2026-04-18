package nad.master.pa.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import nad.master.pa.data.model.Session
import nad.master.pa.data.model.SessionStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val uid get() = auth.currentUser?.uid ?: ""
    private val sessionsRef get() = firestore.collection("users").document(uid).collection("sessions")

    /** Real-time stream of all sessions for a given date (yyyy-MM-dd). */
    fun getSessionsForDate(date: String): Flow<List<Session>> = callbackFlow {
        val listener = sessionsRef
            .whereEqualTo("date", date)
            .orderBy("startTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val sessions = snapshot?.toObjects(Session::class.java) ?: emptyList()
                trySend(sessions)
            }
        awaitClose { listener.remove() }
    }

    /** Real-time stream of sessions for a given weekId (e.g. "2026-W14"). */
    fun getSessionsForWeek(weekId: String): Flow<List<Session>> = callbackFlow {
        val listener = sessionsRef
            .whereEqualTo("weekId", weekId)
            .orderBy("startTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val sessions = snapshot?.toObjects(Session::class.java) ?: emptyList()
                trySend(sessions)
            }
        awaitClose { listener.remove() }
    }

    /** Today's sessions. */
    fun getTodaySessions(): Flow<List<Session>> {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        return getSessionsForDate(today)
    }

    /** Update the status of a session. */
    suspend fun updateSessionStatus(sessionId: String, status: SessionStatus) {
        sessionsRef.document(sessionId)
            .update("status", status.name)
            .await()
    }

    /** Add a new session. */
    suspend fun addSession(session: Session): String {
        val ref = sessionsRef.document()
        ref.set(session.copy(id = ref.id)).await()
        return ref.id
    }

    /** Delete a session. */
    suspend fun deleteSession(sessionId: String) {
        sessionsRef.document(sessionId).delete().await()
    }

    /** Update an existing session. */
    suspend fun updateSession(session: Session) {
        sessionsRef.document(session.id).set(session).await()
    }

    /** Get sessions that were missed in the past N days. */
    suspend fun getMissedSessionsSince(daysBack: Int): List<Session> {
        val cutoff = LocalDate.now().minusDays(daysBack.toLong())
            .format(DateTimeFormatter.ISO_LOCAL_DATE)
        return sessionsRef
            .whereEqualTo("status", SessionStatus.MISSED.name)
            .whereGreaterThanOrEqualTo("date", cutoff)
            .get()
            .await()
            .toObjects(Session::class.java)
    }

    /** Get sessions that were completed in the past N days. */
    suspend fun getCompletedSessionsSince(daysBack: Int): List<Session> {
        val cutoff = LocalDate.now().minusDays(daysBack.toLong())
            .format(DateTimeFormatter.ISO_LOCAL_DATE)
        return sessionsRef
            .whereEqualTo("status", SessionStatus.COMPLETED.name)
            .whereGreaterThanOrEqualTo("date", cutoff)
            .get()
            .await()
            .toObjects(Session::class.java)
    }
}
