package nad.master.pa.ui.dhikr

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nad.master.pa.data.local.DhikrData
import nad.master.pa.data.model.DhikrCategory
import nad.master.pa.data.model.DhikrItem
import nad.master.pa.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DhikrScreen() {
    val expandedCategories = remember { mutableStateMapOf<DhikrCategory, Boolean>() }
    // Counter states: dhikrId → count tapped so far
    val counters = remember { mutableStateMapOf<String, Int>() }

    Scaffold(
        containerColor = DarkBrown,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text  = "الأذكار والأدعية",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = LightCream)
                        )
                        Text(
                            text  = "Dhikr & Adhkar",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmCream.copy(alpha = 0.7f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBrown)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(DarkBrown),
            contentPadding = PaddingValues(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text  = "اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ وَشُكْرِكَ وَحُسْنِ عِبَادَتِكَ",
                    style = MaterialTheme.typography.bodyMedium.copy(color = IslamicGreen),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            DhikrData.CATEGORIES_ORDER.forEach { category ->
                val dhikrList = DhikrData.getByCategory(category)
                if (dhikrList.isEmpty()) return@forEach

                item {
                    DhikrCategoryHeader(
                        category   = category,
                        itemCount  = dhikrList.size,
                        isExpanded = expandedCategories[category] == true,
                        onToggle   = {
                            expandedCategories[category] = expandedCategories[category] != true
                        }
                    )
                }

                if (expandedCategories[category] == true) {
                    items(dhikrList, key = { it.id }) { dhikrItem ->
                        DhikrCard(
                            item     = dhikrItem,
                            tapCount = counters[dhikrItem.id] ?: 0,
                            onTap    = {
                                val current = counters[dhikrItem.id] ?: 0
                                counters[dhikrItem.id] = if (current >= dhikrItem.count) 0 else current + 1
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DhikrCategoryHeader(
    category: DhikrCategory,
    itemCount: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val icon = when (category) {
        DhikrCategory.MORNING       -> "🌅"
        DhikrCategory.EVENING       -> "🌆"
        DhikrCategory.BEFORE_SLEEP  -> "🌙"
        DhikrCategory.TAHAJJUD      -> "⭐"
        DhikrCategory.BEFORE_SALAH  -> "🕌"
        DhikrCategory.AFTER_SALAH   -> "🤲"
        DhikrCategory.QURANIC_DUAS  -> "📖"
        DhikrCategory.SUNNAH_DUAS   -> "☀️"
        DhikrCategory.ISTIGHFAR     -> "💧"
        DhikrCategory.ALL_TIMES     -> "♾️"
        DhikrCategory.RUQIYAH       -> "🛡️"
    }
    val rotation by animateFloatAsState(targetValue = if (isExpanded) 90f else 0f, label = "rot")

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable(onClick = onToggle),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = MediumBrown)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(icon, style = MaterialTheme.typography.titleMedium)
                Column {
                    Text(
                        text  = DhikrData.categoryDisplayName(category),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = LightCream)
                    )
                    Text(
                        text  = "$itemCount adhkar",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmCream.copy(alpha = 0.5f)
                    )
                }
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint     = WarmCream.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp).rotate(rotation)
            )
        }
    }
}

@Composable
private fun DhikrCard(item: DhikrItem, tapCount: Int, onTap: () -> Unit) {
    val isComplete = tapCount >= item.count
    val progress   = if (item.count > 1) tapCount.toFloat() / item.count else if (tapCount > 0) 1f else 0f

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(
            containerColor = if (isComplete) IslamicGreen.copy(alpha = 0.1f) else DarkBrown
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Title
            Text(
                text  = item.titleEnglish,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = if (isComplete) IslamicGreen else LightCream)
            )

            // Arabic text
            Text(
                text      = item.textArabic,
                style     = MaterialTheme.typography.titleSmall.copy(color = WarmCream, fontWeight = FontWeight.Normal),
                textAlign = TextAlign.End,
                modifier  = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )

            // Transliteration
            Text(
                text  = item.transliteration,
                style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                color = WarmCream.copy(alpha = 0.6f)
            )

            // Translation
            Text(
                text  = item.translation,
                style = MaterialTheme.typography.bodySmall,
                color = WarmCream.copy(alpha = 0.55f)
            )

            // Source
            if (item.source.isNotBlank()) {
                Text(
                    text  = "📚 ${item.source}",
                    style = MaterialTheme.typography.labelSmall,
                    color = IslamicGreen.copy(alpha = 0.6f)
                )
            }

            // Benefits
            if (item.benefits.isNotBlank()) {
                Text(
                    text  = "✨ ${item.benefits}",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarningAmber.copy(alpha = 0.7f)
                )
            }

            HorizontalDivider(color = Divider)

            // Counter row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (item.count > 1) {
                    LinearProgressIndicator(
                        progress     = { progress },
                        modifier     = Modifier.weight(1f).height(5.dp).padding(end = 12.dp),
                        color        = if (isComplete) IslamicGreen else WarmCream,
                        trackColor   = WarmCream.copy(alpha = 0.1f)
                    )
                    Text(
                        text  = "$tapCount / ${item.count}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isComplete) IslamicGreen else WarmCream.copy(alpha = 0.5f)
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }

                Button(
                    onClick = onTap,
                    enabled = !isComplete,
                    colors  = ButtonDefaults.buttonColors(
                        containerColor = if (isComplete) IslamicGreen.copy(alpha = 0.2f) else WarmCream,
                        contentColor   = DarkBrown
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text  = if (isComplete) "✓ Done" else if (item.count == 1) "Recite" else "Tab",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}
