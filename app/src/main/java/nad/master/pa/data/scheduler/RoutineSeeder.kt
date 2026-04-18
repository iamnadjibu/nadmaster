package nad.master.pa.data.scheduler

import android.content.Context
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nad.master.pa.data.model.Session
import nad.master.pa.data.model.SessionCategory
import nad.master.pa.data.model.SessionStatus
import nad.master.pa.data.model.SessionType
import nad.master.pa.data.repository.SessionRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID

object RoutineSeeder {

    suspend fun seedRoutineIfFirstTime(context: Context, repository: SessionRepository) = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences("NadMasterPrefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("has_seeded_routine", false)) return@withContext

        // Determine this week's Saturday (or use the one from SchedulingEngine)
        val today = LocalDate.now()
        val daysSinceSat = (today.dayOfWeek.value + 1) % 7
        val weekStart = today.minusDays(daysSinceSat.toLong())
        val weekId = SchedulingEngine.getWeekId(weekStart)

        val sessionsToSeed = mutableListOf<Session>()

        // Helper to add session
        fun addSession(date: LocalDate, startT: String, endT: String, title: String, type: SessionType, category: SessionCategory, desc: String = "") {
            val startParsed = LocalTime.parse(startT)
            val endParsed = LocalTime.parse(endT)
            val startLDT = LocalDateTime.of(date, startParsed)
            val endLDT = LocalDateTime.of(date, endParsed)

            sessionsToSeed.add(
                Session(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = desc,
                    type = type,
                    category = category,
                    startTime = Timestamp(java.util.Date.from(startLDT.atZone(ZoneId.systemDefault()).toInstant())),
                    endTime = Timestamp(java.util.Date.from(endLDT.atZone(ZoneId.systemDefault()).toInstant())),
                    date = date.toString(),
                    weekId = weekId,
                    status = SessionStatus.UPCOMING,
                    isFixed = true,
                    colorCode = "#2C3E50" // Handled dynamically in UI
                )
            )
        }

        // --- Saturday (Offset 0) & Sunday (Offset 1) ---
        for (i in 0..1) {
            val d = weekStart.plusDays(i.toLong())
            addSession(d, "04:30", "05:00", "Fajr", SessionType.RELIGIOUS, SessionCategory.SALAH, "Wake up, Wudu, Fajr")
            addSession(d, "05:00", "06:30", "Hygiene / Chores", SessionType.FLEXIBLE, SessionCategory.PERSONAL_GOALS, "Cleaning & prep")
            addSession(d, "06:30", "07:00", "Prep", SessionType.FLEXIBLE, SessionCategory.PERSONAL_GOALS, "Breakfast")
            addSession(d, "07:00", "09:00", "Commute", SessionType.FIXED, SessionCategory.OTHER, "Home to Work")
            addSession(d, "09:00", "11:30", "FYP II", SessionType.FIXED, SessionCategory.STUDY, "Deep work on Final Year Project")
            addSession(d, "11:30", "13:00", "Dhuhr & Lunch", SessionType.RELIGIOUS, SessionCategory.SALAH)
            addSession(d, "13:00", "17:00", "JOB: KSP Rwanda", SessionType.FIXED, SessionCategory.TRAINING, "Weekend Shift. Asr break ~16:00")
            addSession(d, "17:00", "18:00", "Break", SessionType.BREAK, SessionCategory.BREAK, "Relax and unwind")
            addSession(d, "18:00", "20:00", "Quran & Prayers", SessionType.RELIGIOUS, SessionCategory.QURAN_MEMORIZATION, "Maghrib, Quran, Isha")
            addSession(d, "20:00", "21:30", "Commute", SessionType.FIXED, SessionCategory.OTHER, "Transport Home")
            addSession(d, "21:30", "21:45", "Wind Down", SessionType.BREAK, SessionCategory.BREAK, "Prepare for sleep")
        }

        // --- Monday (2) & Tuesday (3) ---
        for (i in 2..3) {
            val d = weekStart.plusDays(i.toLong())
            addSession(d, "04:30", "06:30", "Fajr & FYP II", SessionType.FIXED, SessionCategory.STUDY, "Fajr, FYP work")
            addSession(d, "06:30", "09:00", "Commute", SessionType.FIXED, SessionCategory.OTHER, "Home to Kigali")
            addSession(d, "09:00", "10:30", "Self-Study", SessionType.FLEXIBLE, SessionCategory.STUDY, "University Free Slot")
            addSession(d, "10:30", "12:30", "JOB: KSP Rwanda", SessionType.FIXED, SessionCategory.TRAINING, "Work shift")
            addSession(d, "12:30", "14:00", "Dhuhr & Lunch", SessionType.RELIGIOUS, SessionCategory.SALAH)
            addSession(d, "14:00", "17:00", "Study & Asr", SessionType.FIXED, SessionCategory.STUDY, "FYP Block")
            addSession(d, "17:00", "18:00", "Break", SessionType.BREAK, SessionCategory.BREAK)
            addSession(d, "18:00", "20:00", "Quran & Prayers", SessionType.RELIGIOUS, SessionCategory.QURAN_MEMORIZATION)
            addSession(d, "20:00", "21:30", "Commute", SessionType.FIXED, SessionCategory.OTHER)
            addSession(d, "21:30", "21:45", "Wind Down", SessionType.BREAK, SessionCategory.BREAK)
        }

        // --- Wednesday (4) ---
        val wed = weekStart.plusDays(4)
        addSession(wed, "04:30", "06:30", "Fajr & FYP II", SessionType.FIXED, SessionCategory.STUDY)
        addSession(wed, "06:30", "09:00", "Commute", SessionType.FIXED, SessionCategory.OTHER)
        addSession(wed, "09:00", "10:30", "Self-Study", SessionType.FLEXIBLE, SessionCategory.STUDY)
        addSession(wed, "10:30", "12:30", "JOB: KSP Rwanda", SessionType.FIXED, SessionCategory.TRAINING)
        addSession(wed, "12:30", "14:00", "Dhuhr & Lunch", SessionType.RELIGIOUS, SessionCategory.SALAH)
        addSession(wed, "14:00", "17:00", "Class & Asr", SessionType.FIXED, SessionCategory.CLASS, "Occ. Safety (CAMP KIGALI_0R03)")
        addSession(wed, "17:00", "18:00", "Break", SessionType.BREAK, SessionCategory.BREAK)
        addSession(wed, "18:00", "20:00", "Quran & Prayers", SessionType.RELIGIOUS, SessionCategory.QURAN_MEMORIZATION)
        addSession(wed, "20:00", "21:30", "Commute", SessionType.FIXED, SessionCategory.OTHER)
        addSession(wed, "21:30", "21:45", "Wind Down", SessionType.BREAK, SessionCategory.BREAK)

        // --- Thursday (5) ---
        val thu = weekStart.plusDays(5)
        addSession(thu, "04:30", "06:30", "Fajr & FYP II", SessionType.FIXED, SessionCategory.STUDY)
        addSession(thu, "06:30", "09:00", "Commute", SessionType.FIXED, SessionCategory.OTHER)
        addSession(thu, "09:00", "12:00", "Class (Morning)", SessionType.FIXED, SessionCategory.CLASS, "Non-destructive testing (MUHABURA_1R09)")
        addSession(thu, "12:00", "14:00", "Dhuhr & Lunch", SessionType.RELIGIOUS, SessionCategory.SALAH)
        addSession(thu, "14:00", "17:00", "Class & Asr", SessionType.FIXED, SessionCategory.CLASS, "Adv. machining (MUHABURA_2R10)")
        addSession(thu, "17:00", "18:00", "Break", SessionType.BREAK, SessionCategory.BREAK)
        addSession(thu, "18:00", "20:00", "Quran & Prayers", SessionType.RELIGIOUS, SessionCategory.QURAN_MEMORIZATION)
        addSession(thu, "20:00", "21:30", "Commute", SessionType.FIXED, SessionCategory.OTHER)
        addSession(thu, "21:30", "21:45", "Wind Down", SessionType.BREAK, SessionCategory.BREAK)

        // --- Friday (6) ---
        val fri = weekStart.plusDays(6)
        addSession(fri, "04:30", "06:30", "Fajr & FYP II", SessionType.FIXED, SessionCategory.STUDY)
        addSession(fri, "06:30", "09:00", "Commute", SessionType.FIXED, SessionCategory.OTHER)
        addSession(fri, "09:00", "12:00", "Class (Morning)", SessionType.FIXED, SessionCategory.CLASS, "Composite eng. materials (IKAZE_1R01)")
        addSession(fri, "12:00", "14:00", "Jumu'ah & Lunch", SessionType.RELIGIOUS, SessionCategory.SALAH, "Friday Prayer Congregation")
        addSession(fri, "14:00", "17:00", "Study & Asr", SessionType.FLEXIBLE, SessionCategory.STUDY, "FYP Block")
        addSession(fri, "17:00", "18:00", "Break", SessionType.BREAK, SessionCategory.BREAK)
        addSession(fri, "18:00", "20:00", "Quran & Prayers", SessionType.RELIGIOUS, SessionCategory.QURAN_MEMORIZATION)
        addSession(fri, "20:00", "21:30", "Commute", SessionType.FIXED, SessionCategory.OTHER)
        addSession(fri, "21:30", "21:45", "Wind Down", SessionType.BREAK, SessionCategory.BREAK)

        try {
            sessionsToSeed.forEach { session ->
                repository.addSession(session)
            }
            prefs.edit().putBoolean("has_seeded_routine", true).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
