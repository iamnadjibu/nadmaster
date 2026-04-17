package nad.master.pa.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nad.master.pa.data.model.DailyPerformance
import nad.master.pa.data.model.Goal
import nad.master.pa.data.model.Session
import nad.master.pa.data.model.SessionCategory
import nad.master.pa.data.model.SessionStatus
import nad.master.pa.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAccount: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        containerColor = DarkBrown,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text  = "NAD MASTER",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color      = LightCream
                            )
                        )
                        Text(
                            text  = "السلام عليكم، NAD",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmCream.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAccount) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Account",
                            tint = WarmCream
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBrown,
                    titleContentColor = LightCream
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = WarmCream)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DarkBrown),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Performance Chart Section ────────────────────────────────────
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 4 }
                ) {
                    PerformanceDashboardCard(
                        recentPerformance = state.recentPerformance,
                        weeklyRate        = state.weeklyCompletionRate,
                        disciplineScore   = state.disciplineScore,
                        quote             = state.motivationalQuote?.text ?: "",
                        quoteAuthor       = state.motivationalQuote?.author ?: ""
                    )
                }
            }

            // ── Current & Next Session ───────────────────────────────────────
            item {
                AnimatedVisibility(visible = visible, enter = fadeIn(tween(800))) {
                    CurrentNextSessionCard(
                        currentSession = state.currentSession,
                        nextSession    = state.nextSession,
                        onMarkCompleted = viewModel::markSessionCompleted,
                        onMarkMissed    = viewModel::markSessionMissed
                    )
                }
            }

            // ── Quran Statistics ─────────────────────────────────────────────
            item {
                AnimatedVisibility(visible = visible, enter = fadeIn(tween(1000))) {
                    QuranStatsCard(progress = state.quranProgress)
                }
            }

            // ── Daily Hadith ─────────────────────────────────────────────────
            item {
                AnimatedVisibility(visible = visible, enter = fadeIn(tween(1100))) {
                    DailyHadithCard(hadith = state.dailyHadith)
                }
            }

            // ── Today's Goals ────────────────────────────────────────────────
            item {
                SectionHeader(title = "Today's Goals", icon = "🎯")
            }
            if (state.todayGoals.isEmpty()) {
                item { EmptyStateCard(message = "No active goals for today") }
            } else {
                items(state.todayGoals) { goal ->
                    GoalProgressCard(
                        goal = goal,
                        onProgressChanged = { viewModel.updateGoalProgress(goal.id, it) }
                    )
                }
            }

            // ── Weekly Goals ─────────────────────────────────────────────────
            item { SectionHeader(title = "Weekly Goals", icon = "📅") }
            if (state.weeklyGoals.isEmpty()) {
                item { EmptyStateCard(message = "No weekly goals set") }
            } else {
                items(state.weeklyGoals.take(5)) { goal ->
                    GoalProgressCard(
                        goal = goal,
                        onProgressChanged = { viewModel.updateGoalProgress(goal.id, it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PerformanceDashboardCard(
    recentPerformance: List<DailyPerformance>,
    weeklyRate: Float,
    disciplineScore: Float,
    quote: String,
    quoteAuthor: String
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = MediumBrown)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text  = "Performance Dashboard",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color      = LightCream
                )
            )

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCircle(label = "Weekly Rate", value = "${(weeklyRate * 100).toInt()}%", color = IslamicGreen)
                StatCircle(label = "Discipline", value = "${disciplineScore.toInt()}", color = WarmCream)
                StatCircle(
                    label = "Sessions",
                    value = "${recentPerformance.sumOf { it.sessionsCompleted }}✓",
                    color = InfoBlue
                )
            }

            // Simple bar chart
            if (recentPerformance.isNotEmpty()) {
                WeeklyBarChart(performances = recentPerformance)
            }

            // Motivational quote
            if (quote.isNotBlank()) {
                HorizontalDivider(color = Divider)
                Text(
                    text  = "\"$quote\"",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontStyle = FontStyle.Italic,
                        color     = WarmCream
                    )
                )
                Text(
                    text  = "— $quoteAuthor",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmCream.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun StatCircle(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text  = value,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color      = color
                )
            )
        }
        Text(
            text  = label,
            style = MaterialTheme.typography.bodySmall,
            color = WarmCream.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun WeeklyBarChart(performances: List<DailyPerformance>) {
    val maxVal = performances.maxOfOrNull { it.sessionsScheduled }?.toFloat()?.coerceAtLeast(1f) ?: 1f
    val dayLabels = listOf("Su","Mo","Tu","We","Th","Fr","Sa")

    Canvas(modifier = Modifier.fillMaxWidth().height(80.dp)) {
        val barWidth = size.width / (performances.size * 2f)
        performances.forEachIndexed { index, perf ->
            val x      = index * (size.width / performances.size) + barWidth / 2
            val height = (perf.sessionsScheduled / maxVal) * size.height * 0.85f
            val completedH = (perf.sessionsCompleted / maxVal) * size.height * 0.85f

            // Background bar (scheduled)
            drawRoundRect(
                color    = WarmCream.copy(alpha = 0.15f),
                topLeft  = Offset(x, size.height - height),
                size     = Size(barWidth, height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
            )
            // Foreground bar (completed)
            if (completedH > 0) {
                drawRoundRect(
                    color    = if (perf.sessionsMissed > 0) CriticalRed else IslamicGreen,
                    topLeft  = Offset(x, size.height - completedH),
                    size     = Size(barWidth, completedH),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
                )
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        performances.take(7).forEach { perf ->
            val date = try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val d   = sdf.parse(perf.date)
                val cal = Calendar.getInstance().apply { time = d ?: Date() }
                dayLabels[cal.get(Calendar.DAY_OF_WEEK) - 1]
            } catch (e: Exception) { "?" }
            Text(
                text  = date,
                style = MaterialTheme.typography.labelSmall,
                color = WarmCream.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun CurrentNextSessionCard(
    currentSession: Session?,
    nextSession: Session?,
    onMarkCompleted: (String) -> Unit,
    onMarkMissed: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = MediumBrown)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text  = "Sessions",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color      = LightCream
                )
            )

            if (currentSession != null) {
                SessionStatusChip(
                    label  = "NOW",
                    color  = IslamicGreen,
                    session = currentSession,
                    onComplete = { onMarkCompleted(currentSession.id) },
                    onMissed   = { onMarkMissed(currentSession.id) }
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.AccessTime, null, tint = WarmCream.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                    Text(
                        text  = "No active session at the moment",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmCream.copy(alpha = 0.5f)
                    )
                }
            }

            if (nextSession != null) {
                HorizontalDivider(color = Divider)
                SessionStatusChip(label = "NEXT", color = WarningAmber, session = nextSession)
            }
        }
    }
}

@Composable
private fun SessionStatusChip(
    label: String,
    color: Color,
    session: Session,
    onComplete: (() -> Unit)? = null,
    onMissed: (() -> Unit)? = null
) {
    val sessionColor = Color(session.getSessionColor())
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Label chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(color.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = color, fontWeight = FontWeight.Bold))
            }
            // Color stripe
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(sessionColor))
            // Session info
            Column {
                Text(
                    text  = session.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = LightCream),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    text  = session.category.name.replace("_", " "),
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmCream.copy(alpha = 0.6f)
                )
            }
        }
        // Quick actions for current session
        if (onComplete != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onComplete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.CheckCircle, "Done", tint = IslamicGreen, modifier = Modifier.size(22.dp))
                }
                IconButton(onClick = { onMissed?.invoke() }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Circle, "Missed", tint = CriticalRed, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}

@Composable
private fun QuranStatsCard(progress: nad.master.pa.data.model.QuranProgress) {
    val animatedPercent by animateFloatAsState(
        targetValue = progress.overallPercent,
        animationSpec = tween(1200),
        label = "quranProgress"
    )

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = MediumBrown)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("📖", style = MaterialTheme.typography.titleMedium)
                Text(
                    text  = "Quran Progress",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = LightCream)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                // Arc progress
                Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(80.dp)) {
                        val stroke = 8f
                        drawArc(
                            color      = MediumBrown,
                            startAngle = -210f, sweepAngle = 240f,
                            useCenter  = false,
                            style      = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                        drawArc(
                            color      = IslamicGreen,
                            startAngle = -210f, sweepAngle = 240f * animatedPercent,
                            useCenter  = false,
                            style      = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                    }
                    Text(
                        text  = "${(animatedPercent * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = IslamicGreen)
                    )
                }

                // Stats
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatRow("Current Surah", progress.currentSurahName, IslamicGreen)
                    StatRow("Current Juzz", "Juzz ${progress.currentJuzz}", WarmCream)
                    StatRow("Daily Target", "${progress.dailyVerseTarget} verses", WarningAmber)
                    StatRow("Memorized", "${progress.versesMemorized}/${progress.totalVerses}", InfoBlue)
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, valueColor: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = "$label:", style = MaterialTheme.typography.bodySmall, color = WarmCream.copy(alpha = 0.6f))
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = valueColor))
    }
}

@Composable
private fun DailyHadithCard(hadith: nad.master.pa.data.model.Hadith) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = MediumBrown.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("✨", style = MaterialTheme.typography.titleMedium)
                Text(
                    text  = "Daily Hadith",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = LightCream)
                )
            }
            Text(
                text  = "\"${hadith.textEnglish}\"",
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic, color = WarmCream)
            )
            Text(
                text  = "— ${hadith.narrator}, ${hadith.source}",
                style = MaterialTheme.typography.bodySmall,
                color = WarmCream.copy(alpha = 0.55f)
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, icon: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(icon, style = MaterialTheme.typography.titleSmall)
        Text(
            text  = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LightCream)
        )
    }
}

@Composable
private fun GoalProgressCard(goal: Goal, onProgressChanged: (Float) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = MediumBrown)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text  = goal.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = LightCream),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    text  = "${(goal.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall.copy(color = if (goal.progress >= 0.8f) IslamicGreen else WarmCream)
                )
            }
            LinearProgressIndicator(
                progress = { goal.progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color    = if (goal.progress >= 1f) IslamicGreen else WarningAmber,
                trackColor = WarmCream.copy(alpha = 0.15f)
            )
            Text(
                text  = goal.category.name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodySmall,
                color = WarmCream.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text  = message,
            style = MaterialTheme.typography.bodyMedium,
            color = WarmCream.copy(alpha = 0.4f)
        )
    }
}
