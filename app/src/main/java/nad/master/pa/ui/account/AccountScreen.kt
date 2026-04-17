package nad.master.pa.ui.account

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nad.master.pa.data.model.Goal
import nad.master.pa.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: AccountViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = DarkBrown,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = "Personal Account",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = LightCream)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = WarmCream)
                    }
                },
                actions = {
                    // No sign-out button — this app uses silent anonymous auth.
                    // Signing out would orphan the Firestore data.
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
            contentPadding  = PaddingValues(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Profile Header ───────────────────────────────────────────────
            item {
                ProfileHeaderCard(
                    name         = state.profile.name,
                    email        = state.profile.email,
                    streak       = state.profile.currentStreak,
                    totalDone    = state.profile.totalCompleted,
                    personalMsg  = state.profile.personalMessage
                )
            }

            // ── Weekly Report ────────────────────────────────────────────────
            item {
                state.weeklyReport?.let { report ->
                    WeeklyReportCard(
                        grade          = report.grade,
                        score          = report.overallScore,
                        completed      = report.completedSessions,
                        missed         = report.missedSessions,
                        weekLabel      = report.weekLabel,
                        motivMsg       = report.motivationalMessage
                    )
                } ?: run {
                    NoReportCard()
                }
            }

            // ── Daily Hadith ─────────────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape    = RoundedCornerShape(20.dp),
                    colors   = CardDefaults.cardColors(containerColor = MediumBrown)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("✨", style = MaterialTheme.typography.titleMedium)
                            Text("Daily Hadith", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LightCream))
                        }
                        Text(
                            text  = "\"${state.dailyHadith.textEnglish}\"",
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic, color = WarmCream)
                        )
                        Text(
                            text  = "— ${state.dailyHadith.narrator}, ${state.dailyHadith.source}",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmCream.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // ── Weekly Goals ─────────────────────────────────────────────────
            item {
                Text(
                    text  = "Weekly Goals Highlight",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = WarmCream),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            if (state.weeklyGoals.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No active goals", style = MaterialTheme.typography.bodyMedium, color = WarmCream.copy(alpha = 0.4f))
                    }
                }
            } else {
                items(state.weeklyGoals) { goal ->
                    WeeklyGoalHighlightRow(goal = goal)
                }
            }
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    name: String, email: String, streak: Int,
    totalDone: Int, personalMsg: String
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(24.dp),
        colors   = CardDefaults.cardColors(containerColor = MediumBrown)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(WarmCream.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = if (name.isNotBlank()) name.first().uppercase() else "N",
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold, color = WarmCream)
                )
            }

            Text(
                text  = name.ifBlank { "Nad" },
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = LightCream)
            )
            Text(
                text  = email,
                style = MaterialTheme.typography.bodySmall,
                color = WarmCream.copy(alpha = 0.55f)
            )

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AccountStat("$streak", "Day Streak", WarningAmber)
                AccountStat("$totalDone", "Sessions Done", IslamicGreen)
            }

            if (personalMsg.isNotBlank()) {
                HorizontalDivider(color = Divider)
                Text(
                    text      = "\"$personalMsg\"",
                    style     = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic, color = WarmCream),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AccountStat(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = color))
        Text(label, style = MaterialTheme.typography.bodySmall, color = WarmCream.copy(alpha = 0.55f))
    }
}

@Composable
private fun WeeklyReportCard(
    grade: String, score: Float, completed: Int,
    missed: Int, weekLabel: String, motivMsg: String
) {
    val gradeColor = when (grade.first()) {
        'A' -> IslamicGreen
        'B' -> WarningAmber
        'C' -> WarmCream
        else -> CriticalRed
    }
    val animScore by animateFloatAsState(targetValue = score / 100f, animationSpec = tween(1000), label = "score")

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = MediumBrown)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Weekly Report", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LightCream))
                    Text(weekLabel, style = MaterialTheme.typography.bodySmall, color = WarmCream.copy(alpha = 0.55f))
                }
                // Grade circle
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(gradeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(grade, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = gradeColor))
                }
            }

            LinearProgressIndicator(
                progress  = { animScore },
                modifier  = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color     = gradeColor,
                trackColor = WarmCream.copy(alpha = 0.1f)
            )
            Text(
                text  = "Overall score: ${score.toInt()}/100",
                style = MaterialTheme.typography.bodySmall,
                color = WarmCream.copy(alpha = 0.5f)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ReportStatChip("✅ $completed completed", IslamicGreen, Modifier.weight(1f))
                ReportStatChip("❌ $missed missed", CriticalRed, Modifier.weight(1f))
            }

            if (motivMsg.isNotBlank()) {
                Text(
                    text  = motivMsg,
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = WarmCream.copy(alpha = 0.65f)
                )
            }
        }
    }
}

@Composable
private fun ReportStatChip(text: String, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(10.dp)).background(color.copy(alpha = 0.1f)).padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = MaterialTheme.typography.bodySmall.copy(color = color, fontWeight = FontWeight.SemiBold))
    }
}

@Composable
private fun NoReportCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = MediumBrown)
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(
                text  = "No weekly report yet.\nComplete some sessions this week to generate your report.",
                style = MaterialTheme.typography.bodyMedium,
                color = WarmCream.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WeeklyGoalHighlightRow(goal: Goal) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = MediumBrown)
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(goal.title, style = MaterialTheme.typography.bodyMedium.copy(color = LightCream, fontWeight = FontWeight.Medium))
                Text("${(goal.progress * 100).toInt()}% complete", style = MaterialTheme.typography.bodySmall, color = WarmCream.copy(alpha = 0.5f))
            }
            LinearProgressIndicator(
                progress  = { goal.progress },
                modifier  = Modifier.width(70.dp).height(5.dp).clip(RoundedCornerShape(3.dp)),
                color     = if (goal.progress >= 0.8f) IslamicGreen else WarningAmber,
                trackColor = WarmCream.copy(alpha = 0.1f)
            )
        }
    }
}

