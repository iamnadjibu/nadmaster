package nad.master.pa.data.scheduler

import android.content.Context
import android.util.Log
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

    private const val TAG = "RoutineSeeder"

    suspend fun seedRoutineIfFirstTime(context: Context, repository: SessionRepository) = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences("NadMasterPrefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("has_seeded_routine", false)) {
            Log.d(TAG, "Already seeded — skipping.")
            return@withContext
        }

        // Determine this week's Saturday start
        val today = LocalDate.now()
        val daysSinceSat = (today.dayOfWeek.value + 1) % 7
        val weekStartSeed = today.minusDays(daysSinceSat.toLong())

        Log.d(TAG, "Seeding for 4 weeks starting from weekStart=$weekStartSeed")

        val sessionsToSeed = mutableListOf<Session>()

        fun addSession(date: LocalDate, startT: String, endT: String, title: String, type: SessionType, category: SessionCategory, desc: String = "") {
            val startParsed = LocalTime.parse(startT)
            val endParsed = LocalTime.parse(endT)
            val startLDT = LocalDateTime.of(date, startParsed)
            val endLDT = LocalDateTime.of(date, endParsed)
            val sessionWeekId = SchedulingEngine.getWeekId(date)

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
                    weekId = sessionWeekId,
                    status = SessionStatus.UPCOMING,
                    isFixed = type == SessionType.FIXED || type == SessionType.RELIGIOUS,
                    colorCode = "#2C3E50"
                )
            )
        }

        // Loop through 4 weeks
        for (weekOffset in 0 until 4) {
            val weekStart = weekStartSeed.plusWeeks(weekOffset.toLong())
            
            for (dayOffset in 0..6) {
                val d = weekStart.plusDays(dayOffset.toLong())
                val globalDayIndex = weekOffset * 7 + dayOffset
                
                // Determine Quran Type (2 days New, 1 day Repeat)
                val quranType = if (globalDayIndex % 3 == 2) "Revision (Repeat)" else "Memorization (New)"
                val quranDesc = if (globalDayIndex % 3 == 2) "Repeat/Revise the 2 pages memorized in previous 2 days." else "Memorize 1 new page."

                // Saturday & Sunday — Weekend Routine
                if (dayOffset == 0 || dayOffset == 1) {
                    addSession(d, "04:30", "05:00", "Fajr", SessionType.RELIGIOUS, SessionCategory.SALAH, "Wake up, Wudu, Fajr")
                    addSession(d, "05:00", "06:30", "Hygiene / Chores", SessionType.FLEXIBLE, SessionCategory.PERSONAL_GOALS, "Cleaning & prep")
                    addSession(d, "06:30", "07:00", "Prep", SessionType.FLEXIBLE, SessionCategory.PERSONAL_GOALS, "Breakfast")
                    addSession(d, "07:00", "09:00", "Commute", SessionType.FIXED, SessionCategory.OTHER, "Home to Work/Kigali")
                    addSession(d, "09:00", "11:30", "FYP II", SessionType.FIXED, SessionCategory.STUDY, "Deep work on Final Year Project")
                    addSession(d, "11:30", "13:00", "Dhuhr & Lunch", SessionType.RELIGIOUS, SessionCategory.SALAH, "Prayer (~12:00) and mid-day meal")
                    addSession(d, "13:00", "17:00", "JOB: KSP Rwanda", SessionType.FIXED, SessionCategory.TRAINING, "Weekend Shift. Asr break ~16:00")
                    addSession(d, "17:00", "18:00", "Break", SessionType.BREAK, SessionCategory.BREAK, "Relax and unwind")
                    addSession(d, "18:00", "20:00", "Quran: $quranType", SessionType.RELIGIOUS, SessionCategory.QURAN_MEMORIZATION, quranDesc)
                    addSession(d, "20:00", "21:30", "Commute", SessionType.FIXED, SessionCategory.OTHER, "Transport Home")
                    addSession(d, "21:30", "21:45", "Wind Down", SessionType.BREAK, SessionCategory.BREAK, "Prepare for sleep")
                } 
                // Monday (2) & Tuesday (3) — KSP Job & Deep Study
                else if (dayOffset == 2 || dayOffset == 3) {
                    addSession(d, "04:30", "06:30", "Fajr & FYP II", SessionType.FIXED, SessionCategory.STUDY, "Fajr (~5:00). Deep work on FYP")
                    addSession(d, "06:30", "09:00", "Commute", SessionType.FIXED, SessionCategory.OTHER, "Home to Kigali")
                    addSession(d, "09:00", "10:30", "Self-Study", SessionType.FLEXIBLE, SessionCategory.STUDY, "University Free Slot")
                    addSession(d, "10:30", "12:30", "JOB: KSP Rwanda", SessionType.FIXED, SessionCategory.TRAINING, "Work shift")
                    addSession(d, "12:30", "14:00", "Dhuhr & Lunch", SessionType.RELIGIOUS, SessionCategory.SALAH, "Prayer and mid-day meal")
                    addSession(d, "14:00", "17:00", "Study & Asr", SessionType.FIXED, SessionCategory.STUDY, "Afternoon FYP Block. Deep study")
                    addSession(d, "17:00", "18:00", "Break", SessionType.BREAK, SessionCategory.BREAK, "Relax and unwind")
                    addSession(d, "18:00", "20:00", "Quran: $quranType", SessionType.RELIGIOUS, SessionCategory.QURAN_MEMORIZATION, quranDesc)
                    addSession(d, "20:00", "21:30", "Commute", SessionType.FIXED, SessionCategory.OTHER, "Transport Home")
                    addSession(d, "21:30", "21:45", "Wind Down", SessionType.BREAK, SessionCategory.BREAK, "Prepare for sleep")
                }
                // Wednesday (4) — KSP Job & Occ. Safety
                else if (dayOffset == 4) {
                    addSession(d, "04:30", "06:30", "Fajr & FYP II", SessionType.FIXED, SessionCategory.STUDY, "Fajr (~5:00). Deep work on FYP")
                    addSession(d, "06:30", "09:00", "Commute", SessionType.FIXED, SessionCategory.OTHER, "Home to Kigali")
                    addSession(d, "09:00", "10:30", "Self-Study", SessionType.FLEXIBLE, SessionCategory.STUDY, "University Free Slot")
                    addSession(d, "10:30", "12:30", "JOB: KSP Rwanda", SessionType.FIXED, SessionCategory.TRAINING, "Work shift")
                    addSession(d, "12:30", "14:00", "Dhuhr & Lunch", SessionType.RELIGIOUS, SessionCategory.SALAH, "Prayer and mid-day meal")
                    addSession(d, "14:00", "17:00", "Class & Asr", SessionType.FIXED, SessionCategory.CLASS, "Occ. Safety (CAMP KIGALI_0R03). Asr break ~16:00")
                    addSession(d, "17:00", "18:00", "Break", SessionType.BREAK, SessionCategory.BREAK, "Relax and unwind")
                    addSession(d, "18:00", "20:00", "Quran: $quranType", SessionType.RELIGIOUS, SessionCategory.QURAN_MEMORIZATION, quranDesc)
                    addSession(d, "20:00", "21:30", "Commute", SessionType.FIXED, SessionCategory.OTHER, "Transport Home")
                    addSession(d, "21:30", "21:45", "Wind Down", SessionType.BREAK, SessionCategory.BREAK, "Prepare for sleep")
                }
                // Thursday (5) — Mandatory Classes
                else if (dayOffset == 5) {
                    addSession(d, "04:30", "06:30", "Fajr & FYP II", SessionType.FIXED, SessionCategory.STUDY, "Fajr (~5:00). Deep work on FYP")
                    addSession(d, "06:30", "09:00", "Commute", SessionType.FIXED, SessionCategory.OTHER, "Home to Campus")
                    addSession(d, "09:00", "12:00", "Class (Morning)", SessionType.FIXED, SessionCategory.CLASS, "Non-destructive testing (MUHABURA_1R09)")
                    addSession(d, "12:00", "14:00", "Dhuhr & Lunch", SessionType.RELIGIOUS, SessionCategory.SALAH, "Prayer and mid-day meal")
                    addSession(d, "14:00", "17:00", "Class & Asr", SessionType.FIXED, SessionCategory.CLASS, "Adv. machining (MUHABURA_2R10). Asr break ~16:00")
                    addSession(d, "17:00", "18:00", "Break", SessionType.BREAK, SessionCategory.BREAK, "Relax and unwind")
                    addSession(d, "18:00", "20:00", "Quran: $quranType", SessionType.RELIGIOUS, SessionCategory.QURAN_MEMORIZATION, quranDesc)
                    addSession(d, "20:00", "21:30", "Commute", SessionType.FIXED, SessionCategory.OTHER, "Transport Home")
                    addSession(d, "21:30", "21:45", "Wind Down", SessionType.BREAK, SessionCategory.BREAK, "Prepare for sleep")
                }
                // Friday (6) — Composite Materials & Jumu'ah
                else if (dayOffset == 6) {
                    addSession(d, "04:30", "06:30", "Fajr & FYP II", SessionType.FIXED, SessionCategory.STUDY, "Fajr (~5:00). Deep work on FYP")
                    addSession(d, "06:30", "09:00", "Commute", SessionType.FIXED, SessionCategory.OTHER, "Home to Campus")
                    addSession(d, "09:00", "12:00", "Class (Morning)", SessionType.FIXED, SessionCategory.CLASS, "Composite eng. materials (IKAZE_1R01)")
                    addSession(d, "12:00", "14:00", "Jumu'ah & Lunch", SessionType.RELIGIOUS, SessionCategory.SALAH, "Friday Prayer Congregation and Lunch")
                    addSession(d, "14:00", "17:00", "Study & Asr", SessionType.FLEXIBLE, SessionCategory.STUDY, "Afternoon FYP Block. Complete assignments")
                    addSession(d, "17:00", "18:00", "Break", SessionType.BREAK, SessionCategory.BREAK, "Relax and unwind")
                    addSession(d, "18:00", "20:00", "Quran: $quranType", SessionType.RELIGIOUS, SessionCategory.QURAN_MEMORIZATION, quranDesc)
                    addSession(d, "20:00", "21:30", "Commute", SessionType.FIXED, SessionCategory.OTHER, "Transport Home")
                    addSession(d, "21:30", "21:45", "Wind Down", SessionType.BREAK, SessionCategory.BREAK, "Prepare for sleep")
                }
            }
        }

        Log.d(TAG, "Prepared ${sessionsToSeed.size} sessions to seed")

        // Write sessions to Firestore one by one
        var count = 0
        sessionsToSeed.forEach { session ->
            repository.addSession(session)
            count++
            if (count % 10 == 0) Log.d(TAG, "Seeded $count/${sessionsToSeed.size} sessions…")
        }

        Log.d(TAG, "All $count sessions seeded successfully for 4 weeks!")
        prefs.edit().putBoolean("has_seeded_routine", true).apply()
    }
}
