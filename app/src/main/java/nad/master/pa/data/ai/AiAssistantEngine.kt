package nad.master.pa.data.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import nad.master.pa.BuildConfig
import nad.master.pa.data.model.Session
import nad.master.pa.data.model.SessionCategory
import nad.master.pa.data.model.SessionStatus
import nad.master.pa.data.model.SessionType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiAssistantEngine @Inject constructor() {
    
    companion object {
        private const val TAG = "AiAssistant"
    }

    private val gson = Gson()

    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            // The API key is securely compiled into the app via local.properties -> BuildConfig
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                temperature = 0.2f
            }
        )
    }

    private val disciplineModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = generationConfig {
                temperature = 0.5f
            }
        )
    }

    /**
     * Parses a natural language request into a list of structured Session objects.
     */
    suspend fun scheduleGoal(
        requestText: String, 
        existingSessions: List<Session> = emptyList(),
        contextDate: LocalDate = LocalDate.now()
    ): List<Session> = withContext(Dispatchers.IO) {
        val todayStr = contextDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        
        val existingBrief = if (existingSessions.isEmpty()) "None" else 
            existingSessions.joinToString("\n") { 
                "- ${it.title} on ${it.date} from ${formatTimestamp(it.startTime)} to ${formatTimestamp(it.endTime)}" 
            }

        val prompt = """
            You are a Personal Assistant AI for the NAD MASTER app.
            Today's Date: $todayStr (Format yyyy-MM-dd)
            
            USER ACTIVE HOURS: 04:00 to 22:00. Never schedule anything outside this window.
            
            EXISTING SCHEDULE:
            $existingBrief
            
            THE USER'S NEW REQUEST: "$requestText"
            
            Analyze the request and generate an optimal schedule as a JSON array of Session objects.
            RULES:
            1. DO NOT OVERLOAD: Find free gaps in the existing schedule. Never overlap with existing fixed sessions.
            2. ACTIVE WINDOW: Only schedule between 04:00 (4 AM) and 22:00 (10 PM).
            3. REASONABLE DURATION: If time is implied but not stated, choose a reasonable time (e.g., study 1-2h).
            4. OUTPUT: Return only a JSON array matching this structure:
            [
              {
                "title": "String",
                "description": "String",
                "type": "FLEXIBLE", // FIXED, FLEXIBLE, BREAK, RELIGIOUS
                "category": "PERSONAL_GOALS", // CLASS, TRAINING, QURAN_MEMORIZATION, STUDY, PERSONAL_GOALS, SALAH, TAHAJJUD, DHIKR, BREAK, SLACK, OTHER
                "date": "yyyy-MM-dd",
                "startHour": 17,
                "startMinute": 0,
                "endHour": 19,
                "endMinute": 0
              }
            ]
        """.trimIndent()

        try {
            Log.d(TAG, "scheduleGoal: API key present = ${BuildConfig.GEMINI_API_KEY.isNotBlank()}")
            Log.d(TAG, "scheduleGoal: Sending prompt to Gemini…")
            val response = generativeModel.generateContent(prompt)
            val jsonText = response.text?.trim()?.removePrefix("```json")?.removeSuffix("```")?.trim()
            Log.d(TAG, "scheduleGoal: Gemini raw response = $jsonText")
            if (jsonText.isNullOrBlank()) return@withContext emptyList()
            
            val listType = object : TypeToken<List<AiSessionDto>>() {}.type
            val dtos: List<AiSessionDto> = gson.fromJson(jsonText, listType)
            Log.d(TAG, "scheduleGoal: Parsed ${dtos.size} DTOs")

            dtos.map { dto ->
                val date = LocalDate.parse(dto.date, DateTimeFormatter.ISO_LOCAL_DATE)
                val start = LocalDateTime.of(date, java.time.LocalTime.of(dto.startHour, dto.startMinute))
                val end = LocalDateTime.of(date, java.time.LocalTime.of(dto.endHour, dto.endMinute))
                
                Session(
                    id = UUID.randomUUID().toString(),
                    title = dto.title,
                    description = dto.description,
                    type = SessionType.valueOf(dto.type),
                    category = SessionCategory.valueOf(dto.category),
                    startTime = Timestamp(java.util.Date.from(start.atZone(ZoneId.systemDefault()).toInstant())),
                    endTime = Timestamp(java.util.Date.from(end.atZone(ZoneId.systemDefault()).toInstant())),
                    date = dto.date,
                    weekId = nad.master.pa.data.scheduler.SchedulingEngine.getWeekId(date),
                    status = SessionStatus.UPCOMING,
                    isFixed = dto.type == "FIXED",
                    colorCode = "#D5CEA3"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "scheduleGoal: FAILED", e)
            throw e
        }
    }

    /**
     * Analyzes recent behavior to provide discipline insights.
     */
    suspend fun analyzeDiscipline(completed: List<Session>, missed: List<Session>): String = withContext(Dispatchers.IO) {
        val completedSummary = completed.groupBy { it.category }.map { "${it.key}: ${it.value.size}" }.joinToString(", ")
        val missedSummary = missed.groupBy { it.category }.map { "${it.key}: ${it.value.size}" }.joinToString(", ")

        val prompt = """
            You are a strict but encouraging Personal Assistant for the NAD MASTER app.
            Your role is to monitor discipline and trending behaviors.
            
            Recent Behavior Data:
            - Completed Sessions: ${if (completedSummary.isBlank()) "None" else completedSummary}
            - Missed Sessions: ${if (missedSummary.isBlank()) "None" else missedSummary}
            
            Keep your response concise (3-4 sentences total).
            Identify any trending negative behavior (if missed sessions exist) and suggest a discipline correction. 
            If behavior is good, acknowledge the dedication.
        """.trimIndent()

        try {
            val response = disciplineModel.generateContent(prompt)
            response.text?.trim() ?: "Keep pushing forward!"
        } catch (e: Exception) {
            e.printStackTrace()
            "Unable to generate discipline insights right now."
        }
    }

    private fun formatTimestamp(ts: com.google.firebase.Timestamp): String {
        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return sdf.format(ts.toDate())
    }
}

// Intermediate DTO to simplify complex object parsing for AI
private data class AiSessionDto(
    val title: String,
    val description: String,
    val type: String,
    val category: String,
    val date: String,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int
)
