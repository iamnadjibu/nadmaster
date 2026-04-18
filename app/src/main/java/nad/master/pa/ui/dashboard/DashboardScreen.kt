package nad.master.pa.ui.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AutoAwesome
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
import nad.master.pa.data.model.Goal
import nad.master.pa.data.model.GoalCategory
import nad.master.pa.data.model.GoalPriority
import nad.master.pa.ui.theme.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val state    by viewModel.uiState.collectAsStateWithLifecycle()
    val goalForm by viewModel.goalForm.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = DarkBrown,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = "Dashboard & Goals",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = LightCream)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBrown)
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FloatingActionButton(
                    onClick          = viewModel::showAiSheet,
                    containerColor   = InfoBlue,
                    contentColor     = Color.White,
                    shape            = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.AutoAwesome, "AI Assistant")
                }
                FloatingActionButton(
                    onClick          = viewModel::showAddGoalSheet,
                    containerColor   = WarmCream,
                    contentColor     = DarkBrown,
                    shape            = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Add, "Add Goal")
                }
            }
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
            contentPadding  = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                GoalSummaryRow(
                    active    = state.activeGoals.size,
                    completed = state.completedGoals.size,
                    avgProgress = state.activeGoals.map { it.progress }.average().toFloat().takeIf { !it.isNaN() } ?: 0f
                )
            }

            state.disciplineInsight?.let { insight ->
                item { DisciplineInsightCard(insight) }
            }

            // Active goals header
            item {
                SectionHeader(title = "Active Goals (${state.activeGoals.size})")
            }

            if (state.activeGoals.isEmpty()) {
                item {
                    EmptyGoals(text = "No active goals yet.\nTap + to add your first goal.")
                }
            } else {
                items(state.activeGoals, key = { it.id }) { goal ->
                    ActiveGoalCard(
                        goal       = goal,
                        onProgressChange = { viewModel.updateGoalProgress(goal.id, it) },
                        onComplete = { viewModel.completeGoal(goal.id) },
                        onDelete   = { viewModel.deleteGoal(goal.id) }
                    )
                }
            }

            // Completed goals header
            if (state.completedGoals.isNotEmpty()) {
                item { SectionHeader(title = "Completed Goals (${state.completedGoals.size})") }
                items(state.completedGoals.take(10), key = { "c${it.id}" }) { goal ->
                    CompletedGoalCard(goal = goal)
                }
            }
        }

        // Add Goal Bottom Sheet
        if (state.showAddGoalSheet) {
            ModalBottomSheet(
                onDismissRequest = viewModel::hideAddGoalSheet,
                containerColor   = MediumBrown,
                tonalElevation   = 0.dp
            ) {
                AddGoalForm(
                    form       = goalForm,
                    onTitleChange   = viewModel::updateFormTitle,
                    onDescChange    = viewModel::updateFormDescription,
                    onCategoryChange = viewModel::updateFormCategory,
                    onPriorityChange = viewModel::updateFormPriority,
                    onStartDateChange = viewModel::updateFormStartDate,
                    onEndDateChange   = viewModel::updateFormEndDate,
                    onSubmit   = viewModel::submitGoal,
                    onCancel   = viewModel::hideAddGoalSheet
                )
            }
        }

        // AI Assistant Sheet
        if (state.showAiSheet) {
            val aiReq by viewModel.aiRequest.collectAsStateWithLifecycle()
            ModalBottomSheet(
                onDismissRequest = viewModel::hideAiSheet,
                containerColor   = MediumBrown,
                tonalElevation   = 0.dp
            ) {
                AiAssistantForm(
                    request = aiReq,
                    onRequestChange = viewModel::updateAiRequest,
                    isLoading = state.isAiLoading,
                    onSubmit = viewModel::submitAiRequest,
                    onCancel = viewModel::hideAiSheet
                )
            }
        }
    }
}

@Composable
private fun GoalSummaryRow(active: Int, completed: Int, avgProgress: Float) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryTile("Active", "$active", WarningAmber, Modifier.weight(1f))
        SummaryTile("Done", "$completed", IslamicGreen, Modifier.weight(1f))
        SummaryTile("Avg Progress", "${(avgProgress * 100).toInt()}%", InfoBlue, Modifier.weight(1f))
    }
}

@Composable
private fun SummaryTile(label: String, value: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = MediumBrown)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = color))
            Text(label, style = MaterialTheme.typography.bodySmall, color = WarmCream.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun ActiveGoalCard(
    goal: Goal,
    onProgressChange: (Float) -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val animatedProgress by animateFloatAsState(targetValue = goal.progress, animationSpec = tween(600), label = "prog")
    val priorityColor = when (goal.priority) {
        GoalPriority.CRITICAL -> CriticalRed
        GoalPriority.HIGH     -> WarningAmber
        GoalPriority.MEDIUM   -> WarmCream
        GoalPriority.LOW      -> InfoBlue
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = MediumBrown)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = goal.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LightCream),
                        maxLines = 2, overflow = TextOverflow.Ellipsis
                    )
                    if (goal.description.isNotBlank()) {
                        Text(
                            text  = goal.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmCream.copy(alpha = 0.6f),
                            maxLines = 2, overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                // Priority badge
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(priorityColor.copy(alpha = 0.2f)).padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text  = goal.priority.name,
                        style = MaterialTheme.typography.labelSmall.copy(color = priorityColor, fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Progress bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color    = if (animatedProgress >= 0.8f) IslamicGreen else WarningAmber,
                trackColor = WarmCream.copy(alpha = 0.12f)
            )

            // Details row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text  = "${(goal.progress * 100).toInt()}% complete",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (goal.progress >= 0.8f) IslamicGreen else WarmCream
                    )
                    Text(
                        text  = "${goal.startDate} → ${goal.endDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmCream.copy(alpha = 0.5f)
                    )
                }
                Row {
                    IconButton(onClick = onComplete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Filled.CheckCircle, "Complete", tint = IslamicGreen, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Filled.Delete, "Delete", tint = CriticalRed, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CompletedGoalCard(goal: Goal) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = MediumBrown.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.CheckCircle, null, tint = IslamicGreen, modifier = Modifier.size(20.dp))
                Text(
                    text  = goal.title,
                    style = MaterialTheme.typography.bodyMedium.copy(color = WarmCream),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            goal.completedDate?.let { d ->
                Text(
                    text  = d,
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmCream.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddGoalForm(
    form: NewGoalForm,
    onTitleChange: (String) -> Unit,
    onDescChange: (String) -> Unit,
    onCategoryChange: (GoalCategory) -> Unit,
    onPriorityChange: (GoalPriority) -> Unit,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp).navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text  = "New Goal",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = LightCream)
        )

        OutlinedTextField(
            value = form.title, onValueChange = onTitleChange,
            label = { Text("Goal Title *", color = WarmCream) },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            colors = defaultTextFieldColors(),
            shape  = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = form.description, onValueChange = onDescChange,
            label = { Text("Description", color = WarmCream) },
            modifier = Modifier.fillMaxWidth().height(90.dp),
            colors = defaultTextFieldColors(),
            shape  = RoundedCornerShape(12.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = form.startDate, onValueChange = onStartDateChange,
                label = { Text("Start Date", color = WarmCream) },
                modifier = Modifier.weight(1f), singleLine = true,
                colors = defaultTextFieldColors(),
                shape  = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = form.endDate, onValueChange = onEndDateChange,
                label = { Text("End Date", color = WarmCream) },
                modifier = Modifier.weight(1f), singleLine = true,
                colors = defaultTextFieldColors(),
                shape  = RoundedCornerShape(12.dp)
            )
        }

        // Category chips — using Compose Foundation FlowRow (no Accompanist needed)
        Text("Category", style = MaterialTheme.typography.bodySmall, color = WarmCream.copy(alpha = 0.7f))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement   = Arrangement.spacedBy(8.dp)
        ) {
            GoalCategory.entries.forEach { cat ->
                FilterChip(
                    selected = form.category == cat,
                    onClick  = { onCategoryChange(cat) },
                    label    = { Text(cat.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall) },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor    = WarmCream,
                        selectedLabelColor        = DarkBrown,
                        containerColor            = MediumBrown,
                        labelColor                = WarmCream
                    )
                )
            }
        }

        // Priority chips
        Text("Priority", style = MaterialTheme.typography.bodySmall, color = WarmCream.copy(alpha = 0.7f))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GoalPriority.entries.forEach { pri ->
                val priColor = when (pri) {
                    GoalPriority.CRITICAL -> CriticalRed
                    GoalPriority.HIGH     -> WarningAmber
                    GoalPriority.MEDIUM   -> WarmCream
                    GoalPriority.LOW      -> InfoBlue
                }
                FilterChip(
                    selected = form.priority == pri,
                    onClick  = { onPriorityChange(pri) },
                    label    = { Text(pri.name, style = MaterialTheme.typography.labelSmall) },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = priColor,
                        selectedLabelColor     = DarkBrown,
                        containerColor         = MediumBrown,
                        labelColor             = WarmCream
                    )
                )
            }
        }

        // Buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onCancel, modifier = Modifier.weight(1f),
                border  = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(WarmCream.copy(alpha = 0.4f))
                )
            ) {
                Text("Cancel", color = WarmCream)
            }
            Button(
                onClick = onSubmit, modifier = Modifier.weight(1f),
                colors  = ButtonDefaults.buttonColors(containerColor = WarmCream, contentColor = DarkBrown),
                shape   = RoundedCornerShape(12.dp),
                enabled = form.title.isNotBlank()
            ) {
                Text("Save Goal", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text  = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = WarmCream),
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
    )
}

@Composable
private fun EmptyGoals(text: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = WarmCream.copy(alpha = 0.4f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
private fun DisciplineInsightCard(insight: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = InfoBlue.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.AutoAwesome, contentDescription = "AI Insight", tint = InfoBlue)
            Text(
                text = insight,
                style = MaterialTheme.typography.bodyMedium,
                color = LightCream
            )
        }
    }
}

@Composable
private fun AiAssistantForm(
    request: String,
    onRequestChange: (String) -> Unit,
    isLoading: Boolean,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp).navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.AutoAwesome, null, tint = InfoBlue)
            Text(
                text  = "AI Assistant",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = LightCream)
            )
        }
        Text(
            text = "Tell me your goal and timeline, and I will perfectly adapt your schedule. Example: 'I need to study Math today from 5 PM to 7 PM'",
            style = MaterialTheme.typography.bodySmall, color = WarmCream.copy(alpha = 0.7f)
        )

        OutlinedTextField(
            value = request, onValueChange = onRequestChange,
            modifier = Modifier.fillMaxWidth().height(120.dp),
            colors = defaultTextFieldColors(),
            shape  = RoundedCornerShape(12.dp),
            placeholder = { Text("What do you want to achieve?", color = WarmCream.copy(alpha = 0.5f)) },
            enabled = !isLoading
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onCancel, modifier = Modifier.weight(1f),
                border  = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(WarmCream.copy(alpha = 0.4f))
                ),
                enabled = !isLoading
            ) {
                Text("Cancel", color = WarmCream)
            }
            Button(
                onClick = onSubmit, modifier = Modifier.weight(1f),
                colors  = ButtonDefaults.buttonColors(containerColor = InfoBlue, contentColor = Color.White),
                shape   = RoundedCornerShape(12.dp),
                enabled = request.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Schedule it", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun defaultTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = WarmCream,
    unfocusedBorderColor = WarmCream.copy(alpha = 0.35f),
    focusedTextColor     = LightCream,
    unfocusedTextColor   = LightCream,
    cursorColor          = WarmCream
)
