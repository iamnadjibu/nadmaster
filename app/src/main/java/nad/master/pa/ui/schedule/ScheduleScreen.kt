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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = hiltViewModel()) {
    val state      by viewModel.uiState.collectAsStateWithLifecycle()
    val weekOffset by viewModel.weekOffset.collectAsStateWithLifecycle()

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
                // Group sessions by day
                val sessionsByDay = state.sessions.groupBy { it.date }.toSortedMap()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sessionsByDay.forEach { (date, daySessions) ->
                        item {
                            DayHeader(date = date)
                        }
                        items(daySessions.sortedBy { it.startTime.seconds }) { session ->
                            ScheduleSessionRow(session = session)
                        }
                    }
                }
            }
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
                                offset > 0  -> "WEEK +$offset"
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
private fun DayHeader(date: String) {
    val dayName = try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val d   = sdf.parse(date)
        val out = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        out.format(d ?: Date())
    } catch (e: Exception) { date }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Divider)
        Text(
            text  = dayName,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = WarmCream.copy(alpha = 0.5f))
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Divider)
    }
}

@Composable
private fun ScheduleSessionRow(session: Session) {
    val sessionColor = Color(session.getSessionColor())
    val statusAlpha  = if (session.status == SessionStatus.COMPLETED) 0.5f else 1f

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = MediumBrown)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Color stripe
            Box(
                modifier = Modifier
                    .width(4.dp).height(52.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(sessionColor.copy(alpha = statusAlpha))
            )

            // Time column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(56.dp)
            ) {
                Text(
                    text  = formatTimestamp(session.startTime),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = WarmCream.copy(alpha = statusAlpha))
                )
                Text(
                    text  = "↓",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmCream.copy(alpha = 0.3f)
                )
                Text(
                    text  = formatTimestamp(session.endTime),
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmCream.copy(alpha = statusAlpha * 0.6f)
                )
            }

            // Session info
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text  = session.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color      = if (session.status == SessionStatus.COMPLETED) WarmCream.copy(alpha = 0.5f) else LightCream
                    ),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    text  = session.category.name.replace("_", " "),
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmCream.copy(alpha = 0.5f)
                )
            }

            // Status chip
            StatusChip(status = session.status, color = sessionColor)
        }
    }
}

@Composable
private fun StatusChip(status: SessionStatus, color: Color) {
    val (label, chipColor) = when (status) {
        SessionStatus.COMPLETED    -> "Done" to IslamicGreen
        SessionStatus.MISSED       -> "Missed" to CriticalRed
        SessionStatus.IN_PROGRESS  -> "Now" to WarningAmber
        SessionStatus.ADJUSTED     -> "Moved" to InfoBlue
        SessionStatus.CANCELLED    -> "Cancelled" to WarmCream.copy(alpha = 0.4f)
        else                       -> "Upcoming" to WarmCream.copy(alpha = 0.3f)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(chipColor.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(color = chipColor, fontWeight = FontWeight.Bold))
    }
}

private fun formatTimestamp(ts: com.google.firebase.Timestamp): String {
    return try {
        val sdf   = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(ts.toDate())
    } catch (e: Exception) { "--:--" }
}
