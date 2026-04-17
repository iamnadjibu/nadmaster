package nad.master.pa.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Overall Quran memorization progress.
 * Stored under users/{uid}/quranProgress/main
 */
data class QuranProgress(
    val currentSurahNumber: Int = 1,
    val currentSurahName: String = "Al-Fatihah",
    val currentJuzz: Int = 1,
    val versesMemorized: Int = 0,
    val totalVerses: Int = 6236,      // Total verses in the Quran
    val surahsCompleted: Int = 0,
    val juzzCompleted: Int = 0,
    val dailyVerseTarget: Int = 5,   // Verses to memorize per day
    val startDate: String = "",       // When memorization journey began
    val lastSessionDate: String = "",
    val updatedAt: Timestamp = Timestamp.now()
) {
    val overallPercent: Float
        get() = if (totalVerses > 0) versesMemorized.toFloat() / totalVerses else 0f
}

/**
 * Status tracking for individual Surahs.
 * Stored under users/{uid}/surahTracking/{surahId}
 */
data class SurahTracking(
    @DocumentId
    val id: String = "",
    val surahNumber: Int = 0,
    val status: SurahStatus = SurahStatus.NOT_STARTED,
    val versesMemorized: Int = 0,
    val completedDate: String? = null,
    val startedDate: String? = null,
    val notes: String = ""
)

enum class SurahStatus {
    COMPLETED,
    IN_PROGRESS,
    NOT_STARTED
}

/**
 * Static data model for a Surah (built into the app).
 */
data class SurahData(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val nameTrans: String,        // Transliteration
    val verseCount: Int,
    val juzz: Int,               // Primary juzz (some span two)
    val juzzEnd: Int = juzz,     // End juzz if different
    val revelationType: RevelationType = RevelationType.MECCAN
)

enum class RevelationType {
    MECCAN, MEDINAN
}

/**
 * Aggregate model for a Juzz with all its Surahs.
 */
data class JuzzData(
    val number: Int,
    val surahs: List<SurahData> = emptyList()
) {
    val totalVerses: Int get() = surahs.sumOf { it.verseCount }
}

/**
 * A Dhikr / supplication item.
 */
data class DhikrItem(
    val id: String,
    val category: DhikrCategory,
    val titleArabic: String,
    val titleEnglish: String,
    val textArabic: String,
    val transliteration: String,
    val translation: String,
    val count: Int = 1,              // Recommended repetition count
    val source: String = "",         // Hadith reference
    val benefits: String = ""
)

enum class DhikrCategory {
    MORNING,
    EVENING,
    BEFORE_SLEEP,
    TAHAJJUD,
    BEFORE_SALAH,
    AFTER_SALAH,
    QURANIC_DUAS,
    SUNNAH_DUAS,
    ISTIGHFAR,
    ALL_TIMES,
    RUQIYAH
}

/**
 * Daily Hadith.
 */
data class Hadith(
    @DocumentId
    val id: String = "",
    val textArabic: String = "",
    val textEnglish: String = "",
    val narrator: String = "",
    val source: String = "",   // e.g., "Sahih al-Bukhari, Book 1, Hadith 1"
    val category: String = "",
    val date: String = ""      // "yyyy-MM-dd" — daily rotation
)
