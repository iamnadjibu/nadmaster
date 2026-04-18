package nad.master.pa.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import nad.master.pa.data.model.QuranDailyPortion
import nad.master.pa.data.model.QuranProgress
import nad.master.pa.data.model.SurahStatus
import nad.master.pa.data.model.SurahTracking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuranRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val uid get() = auth.currentUser?.uid ?: ""
    private val userDoc get() = firestore.collection("users").document(uid)

    /** Real-time overall Quran progress. */
    fun getQuranProgress(): Flow<QuranProgress> = callbackFlow {
        val listener = userDoc.collection("quranProgress").document("main")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val progress = snapshot?.toObject(QuranProgress::class.java) ?: QuranProgress()
                trySend(progress)
            }
        awaitClose { listener.remove() }
    }

    /** Real-time stream of all surah tracking entries. */
    fun getSurahTrackingList(): Flow<List<SurahTracking>> = callbackFlow {
        val listener = userDoc.collection("surahTracking")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val list = snapshot?.toObjects(SurahTracking::class.java) ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    /** Update overall Quran progress. */
    suspend fun updateQuranProgress(progress: QuranProgress) {
        userDoc.collection("quranProgress").document("main")
            .set(progress, SetOptions.merge())
            .await()
    }

    /** Update a single surah's tracking status. */
    suspend fun updateSurahTracking(tracking: SurahTracking) {
        userDoc.collection("surahTracking")
            .document(tracking.surahNumber.toString())
            .set(tracking, SetOptions.merge())
            .await()
    }

    /** Mark a surah as completed. */
    suspend fun markSurahCompleted(surahNumber: Int, date: String) {
        userDoc.collection("surahTracking")
            .document(surahNumber.toString())
            .set(
                SurahTracking(
                    surahNumber = surahNumber,
                    status = SurahStatus.COMPLETED,
                    versesMemorized = -1, // Will be set from surah data
                    completedDate = date
                ),
                SetOptions.merge()
            )
            .await()
    }

    /** Set the current surah being memorized. */
    suspend fun setCurrentSurah(surahNumber: Int, date: String) {
        userDoc.collection("surahTracking")
            .document(surahNumber.toString())
            .set(
                SurahTracking(
                    surahNumber = surahNumber,
                    status = SurahStatus.IN_PROGRESS,
                    startedDate = date
                ),
                SetOptions.merge()
            )
            .await()
    }

    /** Save a daily progress entry. */
    suspend fun saveDailyPortion(portion: QuranDailyPortion) {
        userDoc.collection("quranDailyPortions")
            .document()
            .set(portion)
            .await()
    }

    /** Get all daily portions for the current journey. */
    fun getDailyPortions(): Flow<List<QuranDailyPortion>> = callbackFlow {
        val listener = userDoc.collection("quranDailyPortions")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val list = snapshot?.toObjects(QuranDailyPortion::class.java) ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }
}
