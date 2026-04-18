package nad.master.pa.ui.quran

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nad.master.pa.data.local.DhikrData
import nad.master.pa.data.local.QuranData
import nad.master.pa.data.model.DhikrCategory
import nad.master.pa.data.model.QuranDailyPortion
import nad.master.pa.data.model.SurahData
import nad.master.pa.data.model.SurahStatus
import nad.master.pa.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(viewModel: QuranViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = DarkBrown,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text  = "القرآن الكريم",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = LightCream)
                        )
                        Text(
                            text  = "Quran Memorization Tracker",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmCream.copy(alpha = 0.7f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBrown)
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
            modifier = Modifier.fillMaxSize().padding(padding).background(DarkBrown),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Overall Progress Card ────────────────────────────────────────
            item {
                OverallProgressCard(
                    progress = state.progress,
                    animPercent = state.overallPercent
                )
            }

            // ── Daily Portion Recording ──────────────────────────────────────
            item {
                DailyPortionCard(onLog = viewModel::logDailyPortion)
            }

            // ── Your Commitment Score (Graph) ────────────────────────────────
            item {
                val sessions by viewModel.dailyPortions.collectAsStateWithLifecycle()
                CommitmentScoreGraph(
                    dailyPortions = sessions,
                    targetMonthlyPages = 20f
                )
            }

            item {
                BehindScheduleCard(
                    daysOffset    = state.daysAhead,
                    versesMemorized = state.progress.versesMemorized,
                    dailyTarget   = state.progress.dailyVerseTarget,
                    startDate     = state.progress.startDate
                )
            }

            // ── Quran Juzz Tracker ───────────────────────────────────────────
            item {
                Text(
                    text  = "Juzz Tracker — 30 Parts",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = WarmCream),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            items((1..30).toList()) { juzzNum ->
                JuzzExpandableBar(
                    juzzNumber       = juzzNum,
                    isExpanded       = state.expandedJuzz == juzzNum,
                    onToggle         = { viewModel.toggleJuzzExpansion(juzzNum) },
                    surahs           = QuranData.getSurahsInJuzz(juzzNum),
                    currentSurah     = state.progress.currentSurahNumber,
                    surahTrackingMap = state.surahTrackingMap,
                    onMarkComplete   = viewModel::markSurahCompleted,
                    onSetCurrent     = viewModel::setCurrentSurah
                )
            }

            // ── Daily Duas ───────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "Daily Duas",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = WarmCream),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            val quranicDuas = DhikrData.getByCategory(DhikrCategory.QURANIC_DUAS)
            items(quranicDuas) { dua ->
                DuaCard(
                    titleEnglish  = dua.titleEnglish,
                    textArabic    = dua.textArabic,
                    translation   = dua.translation,
                    source        = dua.source
                )
            }
        }
    }
}

@Composable
private fun DailyPortionCard(onLog: (Float, String) -> Unit) {
    var valueStr by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("PAGES") }
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MediumBrown)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "Record Daily Portion",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LightCream)
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = valueStr,
                    onValueChange = { valueStr = it },
                    placeholder = { Text("Amount", color = WarmCream.copy(0.4f)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LightCream,
                        unfocusedTextColor = WarmCream,
                        focusedBorderColor = IslamicGreen
                    ),
                    singleLine = true
                )
                
                Box {
                    Button(
                        onClick = { expanded = true },
                        colors = ButtonDefaults.buttonColors(containerColor = WarmCream.copy(0.1f))
                    ) {
                        Text(unit, color = WarmCream)
                        Icon(Icons.Default.ArrowDropDown, null, tint = WarmCream)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("Pages") }, onClick = { unit = "PAGES"; expanded = false })
                        DropdownMenuItem(text = { Text("Lines") }, onClick = { unit = "LINES"; expanded = false })
                    }
                }
            }
            
            Button(
                onClick = {
                    val v = valueStr.toFloatOrNull() ?: 0f
                    if (v > 0) onLog(v, unit)
                    valueStr = ""
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Log Progress", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun CommitmentScoreGraph(dailyPortions: List<nad.master.pa.data.model.QuranDailyPortion>, targetMonthlyPages: Float) {
    val currentDayOfMonth = LocalDate.now().dayOfMonth
    val totalDays = LocalDate.now().lengthOfMonth()
    
    // Calculate cumulative progress
    val dailyTotals = mutableMapOf<Int, Float>()
    var currentSum = 0f
    
    // Sort portions by day
    val portionsByDay = dailyPortions
        .filter { 
            val date = try { LocalDate.parse(it.date) } catch(e: Exception) { null }
            date?.month == LocalDate.now().month && date?.year == LocalDate.now().year
        }
        .groupBy { LocalDate.parse(it.date).dayOfMonth }
    
    for (day in 1..totalDays) {
        val daySum = portionsByDay[day]?.sumOf { it.pagesCount.toDouble() }?.toFloat() ?: 0f
        currentSum += daySum
        dailyTotals[day] = currentSum
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBrown.copy(0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Commitment Status (Pages vs Target)",
                style = MaterialTheme.typography.labelMedium,
                color = WarmCream.copy(0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val spacing = width / (totalDays - 1).coerceAtLeast(1).toFloat()
                    val maxHeight = targetMonthlyPages.coerceAtLeast(currentSum + 5f)
                    
                    // Grid lines (horizontal)
                    for (i in 0..4) {
                        val y = height - (i.toFloat() * height / 4f)
                        drawLine(WarmCream.copy(0.05f), start = androidx.compose.ui.geometry.Offset(0f, y), end = androidx.compose.ui.geometry.Offset(width, y))
                    }

                    // Target Line (Linear)
                    val targetPoints = (0 until totalDays).map { day ->
                        val x = day.toFloat() * spacing
                        val y = height - (day.toFloat() + 1f) * (targetMonthlyPages / totalDays.toFloat()) * (height / maxHeight)
                        androidx.compose.ui.geometry.Offset(x, y)
                    }
                    for (i in 0 until targetPoints.size - 1) {
                        drawLine(WarmCream.copy(0.2f), targetPoints[i], targetPoints[i+1], strokeWidth = 2f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                    }

                    // Actual Progress Line
                    val actualPoints = (1..currentDayOfMonth).map { day ->
                        val pages = dailyTotals[day] ?: 0f
                        val x = (day.toFloat() - 1f) * spacing
                        val y = height - (pages * (height / maxHeight))
                        androidx.compose.ui.geometry.Offset(x, y)
                    }
                    
                    if (actualPoints.size > 1) {
                        for (i in 0 until actualPoints.size - 1) {
                            drawLine(IslamicGreen, actualPoints[i], actualPoints[i+1], strokeWidth = 4f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        }
                    }
                }
            }
            
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Day 1", style = MaterialTheme.typography.labelSmall, color = WarmCream.copy(0.4f))
                Text("Today: ${String.format("%.1f", currentSum)} pgs", style = MaterialTheme.typography.labelSmall, color = IslamicGreen)
                Text("Goal: $targetMonthlyPages pgs", style = MaterialTheme.typography.labelSmall, color = WarmCream.copy(0.4f))
            }
        }
    }
}

@Composable
private fun OverallProgressCard(progress: nad.master.pa.data.model.QuranProgress, animPercent: Float) {
    val animated by animateFloatAsState(targetValue = animPercent, animationSpec = tween(1400), label = "qp")

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = MediumBrown)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text  = "Overall Quran Completion",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = LightCream)
            )
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                // Large arc progress
                Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.size(100.dp)) {
                        drawArc(WarmCream.copy(alpha = 0.1f), -220f, 260f, false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(12f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
                        drawArc(IslamicGreen, -220f, 260f * animated, false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(12f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text  = "${(animated * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = IslamicGreen)
                        )
                        Text("Done", style = MaterialTheme.typography.labelSmall, color = WarmCream.copy(alpha = 0.6f))
                    }
                }
                // Stats
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuranStatRow("Memorized",  "${progress.versesMemorized}", "verses", IslamicGreen)
                    QuranStatRow("Remaining",  "${progress.totalVerses - progress.versesMemorized}", "verses", WarmCream)
                    QuranStatRow("Surahs Done","${progress.surahsCompleted}", "/ 114", WarmCream)
                    QuranStatRow("Juzz Done",  "${progress.juzzCompleted}", "/ 30", WarningAmber)
                }
            }
        }
    }
}

@Composable
private fun QuranStatRow(label: String, value: String, unit: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = "$label:", style = MaterialTheme.typography.bodySmall, color = WarmCream.copy(alpha = 0.55f))
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = color))
        Text(text = unit, style = MaterialTheme.typography.bodySmall, color = WarmCream.copy(alpha = 0.4f))
    }
}

@Composable
private fun JuzzExpandableBar(
    juzzNumber: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    surahs: List<SurahData>,
    currentSurah: Int,
    surahTrackingMap: Map<Int, nad.master.pa.data.model.SurahTracking>,
    onMarkComplete: (SurahData) -> Unit,
    onSetCurrent: (SurahData) -> Unit
) {
    val hasCurrentSurah = surahs.any { it.number == currentSurah }
    val completedCount  = surahs.count { surahTrackingMap[it.number]?.status == SurahStatus.COMPLETED }
    val isAllCompleted  = completedCount == surahs.size && surahs.isNotEmpty()

    val headerColor = when {
        isAllCompleted    -> IslamicGreen
        hasCurrentSurah   -> WarningAmber
        else              -> WarmCream.copy(alpha = 0.4f)
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = MediumBrown)
    ) {
        Column {
            // Juzz header bar
            Row(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle).padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Status indicator
                    Box(
                        modifier = Modifier.size(12.dp).clip(CircleShape).background(headerColor)
                    )
                    // Juzz info
                    Column {
                        Text(
                            text  = "Juzz $juzzNumber",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = LightCream)
                        )
                        Text(
                            text  = "${surahs.size} Surahs • $completedCount done",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmCream.copy(alpha = 0.55f)
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isAllCompleted) {
                        Icon(Icons.Filled.CheckCircle, null, tint = IslamicGreen, modifier = Modifier.size(18.dp))
                    }
                    // Completion bar
                    LinearProgressIndicator(
                        progress = { if (surahs.isNotEmpty()) completedCount.toFloat() / surahs.size else 0f },
                        modifier = Modifier.width(60.dp).height(4.dp).clip(CircleShape),
                        color    = headerColor,
                        trackColor = WarmCream.copy(alpha = 0.1f)
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = WarmCream.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Expandable surah list
            AnimatedVisibility(visible = isExpanded, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    HorizontalDivider(color = Divider, modifier = Modifier.padding(horizontal = 16.dp))
                    surahs.forEach { surah ->
                        val status = surahTrackingMap[surah.number]?.status ?: SurahStatus.NOT_STARTED
                        val isCurrent = surah.number == currentSurah
                        SurahRow(
                            surah     = surah,
                            status    = status,
                            isCurrent = isCurrent,
                            onMarkComplete = { onMarkComplete(surah) },
                            onSetCurrent   = { onSetCurrent(surah) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SurahRow(
    surah: SurahData,
    status: SurahStatus,
    isCurrent: Boolean,
    onMarkComplete: () -> Unit,
    onSetCurrent: () -> Unit
) {
    val statusColor = when {
        status == SurahStatus.COMPLETED -> IslamicGreen
        isCurrent                       -> WarningAmber
        else                            -> WarmCream.copy(alpha = 0.35f)
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            // Status icon
            when {
                status == SurahStatus.COMPLETED ->
                    Icon(Icons.Filled.CheckCircle, null, tint = IslamicGreen, modifier = Modifier.size(16.dp))
                isCurrent ->
                    Icon(Icons.Filled.PlayCircle, null, tint = WarningAmber, modifier = Modifier.size(16.dp))
                else ->
                    Icon(Icons.Filled.RadioButtonUnchecked, null, tint = WarmCream.copy(alpha = 0.3f), modifier = Modifier.size(16.dp))
            }

            Column {
                Text(
                    text  = "${surah.number}. ${surah.nameEnglish}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium, color = if (isCurrent) WarningAmber else LightCream)
                )
                Text(
                    text  = "${surah.nameArabic} • ${surah.verseCount} verses",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmCream.copy(alpha = 0.5f)
                )
            }
        }

        // Quick actions
        if (status != SurahStatus.COMPLETED) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (!isCurrent) {
                    TextButton(
                        onClick = onSetCurrent,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Start", style = MaterialTheme.typography.labelSmall, color = WarningAmber)
                    }
                }
                TextButton(
                    onClick = onMarkComplete,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Done ✓", style = MaterialTheme.typography.labelSmall, color = IslamicGreen)
                }
            }
        }
    }
}

@Composable
private fun DuaCard(titleEnglish: String, textArabic: String, translation: String, source: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = MediumBrown)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(titleEnglish, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = LightCream))
            Text(textArabic, style = MaterialTheme.typography.bodyMedium.copy(color = WarmCream), textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
            Text(translation, style = MaterialTheme.typography.bodySmall, color = WarmCream.copy(alpha = 0.65f))
            Text("Source: $source", style = MaterialTheme.typography.labelSmall, color = IslamicGreen.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun BehindScheduleCard(daysOffset: Int, versesMemorized: Int, dailyTarget: Int, startDate: String) {
    val message = when {
        startDate.isBlank() -> "Set your memorization start date to track your progress."
        daysOffset > 0      -> "📉 You are $daysOffset day(s) BEHIND schedule. Need to memorize ${daysOffset * dailyTarget} extra verses to catch up."
        daysOffset < 0      -> "📈 MashaAllah! You are ${-daysOffset} day(s) AHEAD of schedule. Keep it up!"
        else                -> "✅ You are exactly on schedule. Alhamdulillah!"
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(
            containerColor = when {
                daysOffset > 5  -> CriticalRed.copy(alpha = 0.12f)
                daysOffset < 0  -> IslamicGreen.copy(alpha = 0.12f)
                else            -> MediumBrown
            }
        )
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text  = "Your Commitment Score",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LightCream)
            )
            Text(
                text  = message,
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    daysOffset > 0  -> CriticalRed
                    daysOffset < 0  -> IslamicGreen
                    else            -> WarmCream
                }
            )
            if (startDate.isNotBlank()) {
                Text(
                    text  = "Journey started: $startDate • Daily target: $dailyTarget verses",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmCream.copy(alpha = 0.5f)
                )
            }
        }
    }
}
