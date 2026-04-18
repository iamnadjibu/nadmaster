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
            modelName = "gemini-2.5-flash",
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
            modelName = "gemini-2.5-flash",
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
            You are a strict Personal Assistant AI for the NAD MASTER app.
            Today's Date: $todayStr (yyyy-MM-dd)
            
            USER ACTIVE HOURS: 04:00 to 22:00. 
            EXISTING SCHEDULE:
            $existingBrief
            
            NEW REQUEST: "$requestText"
            
            Analyze the request and return an optimal schedule.
            
            STRICT RULES:
            1. OUTPUT: RETURN ONLY A JSON ARRAY. No preamble, no conversational text.
            2. NO OVERLAPS: Check EXISTING SCHEDULE and never overlap.
            3. CATEGORIES: Choose from [PERSONAL_GOALS, STUDY, QURAN_MEMORIZATION, TRAINING, CLASS, BREAK, SALAH].
            4. DURATION: If not specified, use reasonable blocks (1-2 hours).
            5. MULTI-WEEK PLANNING: If the user asks for a 'month', '4 weeks', or 'routine', generate sessions for up to 30 days ahead from ${todayStr}.
            6. QURAN ROUTINE: Follow the "2+1" rule: 2 days of "Memorization (New Page)" followed by 1 day of "Revision (Repeat 2 Pages)".
            
            FORMAT EXAMPLE:
            [
              {
                "title": "Study Math",
                "description": "Calculus Chapter 5",
                "type": "FLEXIBLE",
                "category": "STUDY",
                "date": "$todayStr",
                "startHour": 17, "startMinute": 0,
                "endHour": 19, "endMinute": 0
              }
            ]
        """.trimIndent()

        try {
            Log.d(TAG, "scheduleGoal: Sending prompt to Gemini…")
            val response = generativeModel.generateContent(prompt)
            val rawText = response.text ?: ""
            Log.d(TAG, "scheduleGoal: Gemini Raw Response = $rawText")
            
            val jsonText = extractJsonArray(rawText)
            Log.d(TAG, "scheduleGoal: Extracted JSON = $jsonText")
            
            if (jsonText.isBlank()) {
                Log.w(TAG, "scheduleGoal: No JSON array found in response")
                return@withContext emptyList()
            }
            
            val listType = object : TypeToken<List<AiSessionDto>>() {}.type
            val dtos: List<AiSessionDto> = gson.fromJson(jsonText, listType)
            Log.d(TAG, "scheduleGoal: Successfully parsed ${dtos.size} sessions")

            dtos.map { dto ->
                val date = LocalDate.parse(dto.date, DateTimeFormatter.ISO_LOCAL_DATE)
                val start = LocalDateTime.of(date, java.time.LocalTime.of(dto.startHour % 24, dto.startMinute % 60))
                val end = LocalDateTime.of(date, java.time.LocalTime.of(dto.endHour % 24, dto.endMinute % 60))
                
                val isOddHour = dto.startHour < 4 || dto.endHour > 22 || (dto.endHour == 22 && dto.endMinute > 0)
                
                Session(
                    id = UUID.randomUUID().toString(),
                    title = dto.title,
                    description = dto.description,
                    type = try { SessionType.valueOf(dto.type.uppercase()) } catch(e: Exception) { SessionType.FLEXIBLE },
                    category = try { SessionCategory.valueOf(dto.category.uppercase()) } catch(e: Exception) { SessionCategory.PERSONAL_GOALS },
                    startTime = Timestamp(java.util.Date.from(start.atZone(ZoneId.systemDefault()).toInstant())),
                    endTime = Timestamp(java.util.Date.from(end.atZone(ZoneId.systemDefault()).toInstant())),
                    date = dto.date,
                    weekId = nad.master.pa.data.scheduler.SchedulingEngine.getWeekId(date),
                    status = SessionStatus.UPCOMING,
                    isFixed = dto.type.uppercase() == "FIXED",
                    needsConfirmation = isOddHour,
                    colorCode = "#D5CEA3"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "scheduleGoal: AI Parsing or API error", e)
            throw e
        }
    }

    /**
     * The unified entry point for AI control. Parses natural language into specific intents.
     */
    suspend fun processIntelligentCommand(
        request: String,
        contextJson: String
    ): List<AiAction> = withContext(Dispatchers.IO) {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        
        val prompt = """
            You are the "Master AI" for the NAD MASTER app. 
            You have full control over the user's data. 
            
            TODAY: $today
            APP CONTEXT: $contextJson
            USER REQUEST: "$request"
            
            Analyze the request and decide what MUST change in the app state.
            
            POSSIBLE ACTIONS:
            - ADD_SESSIONS: Data is a list of sessions (use scheduling logic).
            - DELETE_SESSIONS: Data is a list of sessionIds.
            - CLEAR_DAY: Data is a specific date string.
            - UPDATE_PROFILE: Data is a map of profile fields (name, targetJuzz).
            - CREATE_GOAL: Data is a goal object.
            - LOG_QURAN: Data is a float "pages".
            - ANALYZE: Data is a string message (conversational response).
            
            STRICT RULES:
            1. RETURN ONLY A JSON ARRAY OF ACTIONS.
            2. MULTIPLE ACTIONS: You can return multiple actions (e.g., Delete sessions AND Add new ones).
            3. CONSISTENCY: Ensure all dates are yyyy-MM-dd.
            
            FORMAT EXAMPLE:
            [
              {"type": "DELETE_SESSIONS", "data": ["id1", "id2"]},
              {"type": "ADD_SESSIONS", "data": [{"title": "Quick Study", "date": "$today", ...}]}
            ]
        """.trimIndent()

        try {
            val response = generativeModel.generateContent(prompt)
            val rawText = response.text ?: ""
            val jsonText = extractJsonArray(rawText)
            
            if (jsonText.isBlank()) return@withContext emptyList()
            
            val listType = object : TypeToken<List<AiAction>>() {}.type
            gson.fromJson(jsonText, listType)
        } catch (e: Exception) {
            Log.e(TAG, "processIntelligentCommand FAILED", e)
            emptyList()
        }
    }

    /**
     * Extracts the first JSON array found in the text.
     */
    private fun extractJsonArray(text: String): String {
        val start = text.indexOf("[")
        val end = text.lastIndexOf("]")
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1)
        }
        return ""
    }

    /**
     * Analyzes recent behavior to provide discipline insights.
     */
    suspend fun analyzeDiscipline(completed: List<Session>, missed: List<Session>, unfinished: List<Session>): String = withContext(Dispatchers.IO) {
        val completedSummary = completed.groupBy { it.category }.map { "${it.key}: ${it.value.size}" }.joinToString(", ")
        val missedSummary = missed.groupBy { it.category }.map { "${it.key}: ${it.value.size}" }.joinToString(", ")
        val unfinishedSummary = unfinished.groupBy { it.category }.map { "${it.key}: ${it.value.size}" }.joinToString(", ")

        val prompt = """
            You are a strict but encouraging Personal Assistant for the NAD MASTER app.
            Your role is to monitor discipline and trending behaviors.
            
            Recent Behavior Data:
            - Completed Sessions: ${if (completedSummary.isBlank()) "None" else completedSummary}
            - Missed Sessions: ${if (missedSummary.isBlank()) "None" else missedSummary}
            - Partially Completed (Unfinished): ${if (unfinishedSummary.isBlank()) "None" else unfinishedSummary}
            
            Analyze the user's progress. "Unfinished" is better than "Missed" but needs improvement.
            Keep your response concise (3-4 sentences total).
            Identify any trending negative behavior and suggest a discipline correction. 
            If behavior is good, acknowledge the dedication.
        """.trimIndent()

        val response = disciplineModel.generateContent(prompt)
        response.text?.trim() ?: "Keep pushing forward!"
    }

    /**
     * Segments a top-level goal into a hierarchical list of milestones (weekly/daily).
     */
    suspend fun segmentGoal(
        goalTitle: String,
        description: String,
        startDate: String,
        endDate: String
    ): List<nad.master.pa.data.model.Milestone> = withContext(Dispatchers.IO) {
        val prompt = """
            You are a project management expert. 
            Goal: "$goalTitle"
            Description: "$description"
            Timeline: $startDate to $endDate
            
            Segment this goal into a structured roadmap.
            1. Generate WEEKLY milestones for each week in the timeline.
            2. Generate DAILY milestones for the first week to get started immediately.
            
            OUTPUT: RETURN ONLY A JSON ARRAY of milestones.
            Format:
            [
              {
                "title": "Setup environment",
                "targetDate": "yyyy-MM-dd",
                "type": "DAILY"
              },
              {
                "title": "Complete Chapter 1",
                "targetDate": "yyyy-MM-dd",
                "type": "WEEKLY"
              }
            ]
            Choose types from [WEEKLY, DAILY].
        """.trimIndent()

        try {
            val response = generativeModel.generateContent(prompt)
            val rawText = response.text ?: ""
            val jsonText = extractJsonArray(rawText)
            
            if (jsonText.isBlank()) return@withContext emptyList()
            
            val listType = object : TypeToken<List<AiMilestoneDto>>() {}.type
            val dtos: List<AiMilestoneDto> = gson.fromJson(jsonText, listType)
            
            dtos.map { dto ->
                nad.master.pa.data.model.Milestone(
                    id = UUID.randomUUID().toString(),
                    title = dto.title,
                    targetDate = dto.targetDate,
                    type = try { nad.master.pa.data.model.MilestoneType.valueOf(dto.type.uppercase()) } catch(e: Exception) { nad.master.pa.data.model.MilestoneType.DAILY }
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "segmentGoal: FAILED", e)
            emptyList()
        }
    }

    private fun formatTimestamp(ts: com.google.firebase.Timestamp): String {
        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return sdf.format(ts.toDate())
    }
}

data class AiAction(
    val type: String,
    val data: Any?
)

private data class AiMilestoneDto(
    val title: String,
    val targetDate: String,
    val type: String
)

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
