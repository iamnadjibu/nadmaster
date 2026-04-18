package nad.master.pa.ui.schedule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nad.master.pa.data.model.Session
import nad.master.pa.data.model.SessionCategory
import nad.master.pa.data.model.SessionStatus
import nad.master.pa.data.model.getSessionColor
import nad.master.pa.data.scheduler.WeekInfo
import nad.master.pa.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = hiltViewModel()) {
    val state      by viewModel.uiState.collectAsStateWithLifecycle()
    val weekOffset by viewModel.weekOffset.collectAsStateWithLifecycle()
    
    var sessionToEdit by remember { mutableStateOf<Session?>(null) }

    Scaffold(
        containerColor = DarkBrown,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = "Weekly Schedule",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = LightCream)
                    )
                },
                actions = {
                    if (state.canSeedRoutine) {
                        IconButton(onClick = viewModel::seedRoutine) {
                            Icon(Icons.Filled.Download, "Seed Routine", tint = InfoBlue)
                        }
                    }
                    IconButton(onClick = viewModel::runScheduleAdjustment) {
                        Icon(Icons.Filled.AutoFixHigh, "Adjust Schedule", tint = WarmCream)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBrown)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).background(DarkBrown)
        ) {
            // ── Week Selector Bar ────────────────────────────────────────────
            WeekSelectorBar(
                weekRange     = viewModel.weekRange,
                selectedOffset = weekOffset,
                onSelectWeek  = viewModel::selectWeek,
                getWeekInfo   = viewModel::getWeekInfo
            )

            // Adjustment banner
            AnimatedVisibility(
                visible = state.adjustmentMessage != null,
                enter   = fadeIn(), exit = fadeOut()
            ) {
                state.adjustmentMessage?.let { msg ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = CardDefaults.cardColors(containerColor = WarningAmber.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text     = msg,
                                style    = MaterialTheme.typography.bodySmall,
                                color    = WarningAmber,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = viewModel::dismissAdjustmentMessage, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Filled.Close, null, tint = WarningAmber, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Week info subtitle
            state.selectedWeekInfo?.let { info ->
                Text(
                    text  = "${info.weekLabel} • ${info.startDate} – ${info.endDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmCream.copy(alpha = 0.55f),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }

            // ── Error Banner ─────────────────────────────────────────────────
            AnimatedVisibility(
                visible = state.error != null,
                enter   = fadeIn(), exit = fadeOut()
            ) {
                state.error?.let { errorMsg ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = CardDefaults.cardColors(containerColor = CriticalRed.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text     = errorMsg,
                                style    = MaterialTheme.typography.bodySmall,
                                color    = CriticalRed,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = viewModel::dismissError, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Filled.Close, null, tint = CriticalRed, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // ── Session Schedule ─────────────────────────────────────────────
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = WarmCream)
                }
            } else if (state.sessions.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("📅", style = MaterialTheme.typography.displaySmall)
                        Text(
                            text  = "No sessions scheduled for this week",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WarmCream.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                state.selectedWeekInfo?.startLocalDate?.let { startDate ->
                    WeeklyTimetable(
                        sessions = state.sessions, 
                        startDate = startDate,
                        onSessionClick = { sessionToEdit = it },
                        onConfirm = viewModel::confirmSession,
                        onDecline = viewModel::declineSession
                    )
                }
            }
        }
    }

    // Session Edit Bottom Sheet
    if (sessionToEdit != null) {
        ModalBottomSheet(
            onDismissRequest = { sessionToEdit = null },
            containerColor = MediumBrown,
            tonalElevation = 0.dp
        ) {
            SessionEditSheet(
                session = sessionToEdit!!,
                onDelete = {
                    viewModel.deleteSession(it.id)
                    sessionToEdit = null
                },
                onDismiss = { sessionToEdit = null }
            )
        }
    }
}

@Composable
private fun WeekSelectorBar(
    weekRange: List<Int>,
    selectedOffset: Int,
    onSelectWeek: (Int) -> Unit,
    getWeekInfo: (Int) -> WeekInfo
) {
    val listState = rememberLazyListState()
    val centerIndex = weekRange.indexOf(0)

    LaunchedEffect(Unit) {
        if (centerIndex >= 0) listState.scrollToItem(
            (centerIndex - 2).coerceAtLeast(0)
        )
    }

    LazyRow(
        state            = listState,
        modifier         = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        contentPadding   = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(weekRange) { offset ->
            val info     = getWeekInfo(offset)
            val selected = offset == selectedOffset
            FilterChip(
                selected = selected,
                onClick  = { onSelectWeek(offset) },
                label    = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text  = when {
                                offset == 0 -> "WEEK 0"
                                offset > 0  -> "WEEK $offset"
                                else        -> "WEEK $offset"
                            },
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                        )
                        Text(
                            text  = "${info.startDate}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = WarmCream,
                    selectedLabelColor     = DarkBrown,
                    containerColor         = MediumBrown,
                    labelColor             = WarmCream.copy(alpha = 0.7f)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    selected = selected,
                    enabled  = true,
                    borderColor = WarmCream.copy(alpha = 0.2f),
                    selectedBorderColor = WarmCream
                )
            )
        }
    }
}

@Composable
fun WeeklyTimetable(
    sessions: List<Session>, 
    startDate: LocalDate,
    onSessionClick: (Session) -> Unit,
    onConfirm: (String) -> Unit,
    onDecline: (String) -> Unit
) {
    val scrollStateX = rememberScrollState()
    val scrollStateY = rememberScrollState()

    val hourHeight = 84.dp
    val dayWidth = 124.dp
    
    // Show full 24 hours now
    val startHour = 0
    val endHour = 24
    val totalHours = 24

    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }
    
    // Auto-scroll to 4 AM on first load
    LaunchedEffect(Unit) {
        scrollStateY.scrollTo((4 * hourHeight.value * 3).toInt()) // scale factor for density approx
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalDateTime.now()
            kotlinx.coroutines.delay(60_000)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(DarkBrown)
    ) {
        // Fixed Top Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 50.dp).horizontalScroll(scrollStateX)
        ) {
            for (i in 0 until 7) {
                val day = startDate.plusDays(i.toLong())
                val isToday = day == LocalDate.now()
                Box(modifier = Modifier.width(dayWidth).padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = day.format(DateTimeFormatter.ofPattern("EEE dd")),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            color = if (isToday) WarningAmber else WarmCream
                        )
                    )
                }
            }
        }
        
        HorizontalDivider(color = WarmCream.copy(alpha = 0.1f))

        Row(modifier = Modifier.fillMaxSize().verticalScroll(scrollStateY)) {
            // Time Column
            Column(modifier = Modifier.width(50.dp)) {
                for (hour in startHour until endHour) {
                    Box(modifier = Modifier.height(hourHeight).fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                        Text(
                            text = String.format("%02d:00", hour),
                            style = MaterialTheme.typography.labelSmall,
                            color = WarmCream.copy(alpha = if (hour in 4..22) 0.6f else 0.3f),
                            modifier = Modifier.padding(end = 8.dp, top = 4.dp)
                        )
                    }
                }
            }

            VerticalDivider(color = WarmCream.copy(alpha = 0.1f))

            Box(
                modifier = Modifier.fillMaxSize().horizontalScroll(scrollStateX).height(hourHeight * totalHours).width((dayWidth.value * 7).dp)
            ) {
                // Grid Lines
                for (hour in 0..totalHours) {
                    HorizontalDivider(
                        modifier = Modifier.offset(y = (hourHeight.value * hour).dp),
                        color = WarmCream.copy(alpha = if (hour % 4 == 0) 0.1f else 0.05f)
                    )
                }
                for (day in 0..7) {
                    val isToday = startDate.plusDays(day.toLong()) == LocalDate.now()
                    val xPos = (dayWidth.value * day).dp
                    if (isToday) {
                        Box(modifier = Modifier.offset(x = xPos).width(dayWidth).fillMaxHeight().background(WarmCream.copy(alpha = 0.03f)))
                    }
                    VerticalDivider(modifier = Modifier.offset(x = xPos), color = WarmCream.copy(alpha = 0.05f))
                }
                
                // Now Indicator
                if (currentTime.toLocalDate() >= startDate && currentTime.toLocalDate() <= startDate.plusDays(6)) {
                    val daysDiff = java.time.temporal.ChronoUnit.DAYS.between(startDate, currentTime.toLocalDate()).toInt()
                    val hourOffset = currentTime.hour + currentTime.minute / 60f
                    val yOffset = (hourOffset * hourHeight.value).dp
                    
                    Box(modifier = Modifier.offset(y = yOffset - 4.dp, x = (-2).dp).size(8.dp).clip(RoundedCornerShape(50)).background(CriticalRed))
                    HorizontalDivider(modifier = Modifier.offset(y = yOffset).fillMaxWidth(), thickness = 1.dp, color = CriticalRed.copy(alpha = 0.6f))
                }

                // Sessions
                sessions.forEach { session ->
                    val sessionDate = try { LocalDate.parse(session.date) } catch(e: Exception) { null }
                    if (sessionDate != null) {
                        val daysDiff = java.time.temporal.ChronoUnit.DAYS.between(startDate, sessionDate).toInt()
                        if (daysDiff in 0..6) {
                            val startDateTime = LocalDateTime.ofInstant(session.startTime.toDate().toInstant(), ZoneId.systemDefault())
                            val endDateTime = LocalDateTime.ofInstant(session.endTime.toDate().toInstant(), ZoneId.systemDefault())
                            
                            val startOffsetHours = startDateTime.hour + startDateTime.minute / 60f
                            val durationHours = java.time.Duration.between(startDateTime, endDateTime).toMinutes() / 60f
                            
                            val yPos = (hourHeight.value * startOffsetHours).dp
                            val hPos = (durationHours * hourHeight.value).dp.coerceAtLeast(26.dp)
                            
                            TimetableSessionBlock(
                                session = session,
                                currentTime = currentTime,
                                onClick = { onSessionClick(session) },
                                onConfirm = { onConfirm(session.id) },
                                onDecline = { onDecline(session.id) },
                                modifier = Modifier
                                    .absoluteOffset(x = (dayWidth.value * daysDiff).dp + 4.dp, y = yPos + 2.dp)
                                    .size(width = dayWidth - 8.dp, height = hPos - 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimetableSessionBlock(
    session: Session, 
    currentTime: LocalDateTime, 
    onClick: () -> Unit,
    onConfirm: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier
) {
    val sessionColor = Color(session.getSessionColor())
    val endDateTime = LocalDateTime.ofInstant(session.endTime.toDate().toInstant(), ZoneId.systemDefault())
    val isPast = endDateTime.isBefore(currentTime)
    val isActive = !isPast && LocalDateTime.ofInstant(session.startTime.toDate().toInstant(), ZoneId.systemDefault()).isBefore(currentTime)

    Card(
        onClick = onClick,
        modifier = modifier.then(
            if (session.needsConfirmation) Modifier.border(2.dp, WarningAmber, RoundedCornerShape(8.dp))
            else if (isActive) Modifier.border(2.dp, WarmCream, RoundedCornerShape(8.dp))
            else Modifier
        ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = sessionColor.copy(alpha = if (isPast) 0.35f else 0.9f)
        )
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(
                    session.title, 
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = DarkBrown),
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (session.needsConfirmation) {
                    Icon(Icons.Filled.NotificationsActive, null, tint = DarkBrown, modifier = Modifier.size(10.dp))
                }
            }
            Text(
                formatTimestamp(session.startTime) + " - " + formatTimestamp(session.endTime),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = DarkBrown.copy(alpha = 0.7f))
            )

            // Confirmation Actions
            if (session.needsConfirmation) {
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecline, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Filled.Close, null, tint = CriticalRed, modifier = Modifier.size(14.dp))
                    }
                    IconButton(onClick = onConfirm, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Filled.Check, null, tint = IslamicGreen, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionEditSheet(
    session: Session,
    onDelete: (Session) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp).navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Edit Session",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = LightCream)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkBrown.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(session.title, style = MaterialTheme.typography.titleMedium, color = WarmCream)
                Text(session.description.ifBlank { "No description provided." }, style = MaterialTheme.typography.bodySmall, color = WarmCream.copy(0.6f))
                Text("Time: ${session.date} | ${formatTimestamp(session.startTime)} - ${formatTimestamp(session.endTime)}", 
                    style = MaterialTheme.typography.bodySmall, color = InfoBlue)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { onDelete(session) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = CriticalRed, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Delete, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Delete")
            }
            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = WarmCream, contentColor = DarkBrown),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Close")
            }
        }
    }
}

private fun formatTimestamp(ts: com.google.firebase.Timestamp): String {
    return try {
        val sdf   = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(ts.toDate())
    } catch (e: Exception) { "--:--" }
}

