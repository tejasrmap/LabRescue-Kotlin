package com.labrescue.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labrescue.app.data.model.Mistake
import com.labrescue.app.data.model.MistakeCategory
import com.labrescue.app.ui.components.AdaptiveContainer
import com.labrescue.app.ui.theme.CardBorderColor
import com.labrescue.app.ui.theme.CyanNeon
import com.labrescue.app.ui.theme.ErrorRose
import com.labrescue.app.ui.theme.IndigoNeon
import com.labrescue.app.ui.theme.LabRescueTheme
import com.labrescue.app.ui.theme.ObsidianBg
import com.labrescue.app.ui.theme.ObsidianCard
import com.labrescue.app.ui.theme.ObsidianCardElevated
import com.labrescue.app.ui.theme.SuccessEmerald
import com.labrescue.app.ui.theme.TextHighEmphasis
import com.labrescue.app.ui.theme.TextLowEmphasis
import com.labrescue.app.ui.theme.TextMediumEmphasis
import com.labrescue.app.ui.theme.TextWhite
import com.labrescue.app.ui.theme.WarningAmber
import com.labrescue.app.ui.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun MistakeProfileScreen(viewModel: DashboardViewModel) {
    val mistakeStats by viewModel.mistakeStats.collectAsState()
    val mistakes by viewModel.mistakes.collectAsState()
    val accuracy by viewModel.diagnosticAccuracy.collectAsState()
    val totalMistakes = remember(mistakeStats) { mistakeStats.values.sum() }

    val topBlindspot = remember(mistakeStats, totalMistakes) {
        if (totalMistakes == 0) "None"
        else {
            mistakeStats.maxByOrNull { it.value }?.key?.let { cat ->
                when (cat) {
                    MistakeCategory.CONFIGURATION -> "Config"
                    MistakeCategory.LOGIC -> "Logic"
                    MistakeCategory.CONCEPTUAL -> "Concept"
                    MistakeCategory.SYNTAX -> "Syntax"
                }
            } ?: "None"
        }
    }

    AdaptiveContainer(maxWidth = 1080.dp) { isWideScreen ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBg)
                .padding(horizontal = if (isWideScreen) 32.dp else 14.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 84.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Left-Aligned Screen Header
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Diagnostic Analytics",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            fontSize = if (isWideScreen) 24.sp else 20.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Error distribution across practical lab sessions and targeted retrieval guidance.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextMediumEmphasis,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }

            // Compact 3-Metric Summary Strip
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCardElevated)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnalyticsStatPill(
                            label = "TOTAL MISSTEPS",
                            value = "$totalMistakes",
                            color = if (totalMistakes == 0) SuccessEmerald else WarningAmber,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(26.dp)
                                .background(CardBorderColor.copy(alpha = 0.5f))
                        )
                        AnalyticsStatPill(
                            label = "TOP BLINDSPOT",
                            value = topBlindspot,
                            color = if (topBlindspot == "None") SuccessEmerald else CyanNeon,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(26.dp)
                                .background(CardBorderColor.copy(alpha = 0.5f))
                        )
                        AnalyticsStatPill(
                            label = "AVG ACCURACY",
                            value = "$accuracy%",
                            color = if (accuracy >= 75) SuccessEmerald else WarningAmber,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (isWideScreen) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Box(modifier = Modifier.weight(1.2f)) {
                            MistakeBreakdownCard(mistakeStats, totalMistakes)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            RetrievalAdviceCard()
                        }
                    }
                }
            } else {
                item {
                    MistakeBreakdownCard(mistakeStats, totalMistakes)
                }
                item {
                    RetrievalAdviceCard()
                }
            }

            // Recent Logged Missteps from Room Database
            item {
                RecentLoggedMisstepsCard(
                    mistakes = mistakes.take(8),
                    resolveScenarioTitle = { scenarioId ->
                        viewModel.getScenario(scenarioId)?.title ?: "Lab Diagnostic Session"
                    }
                )
            }
        }
    }
}

@Composable
private fun AnalyticsStatPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            maxLines = 1,
            softWrap = false,
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextLowEmphasis,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.6.sp
            )
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            maxLines = 1,
            softWrap = false,
            style = MaterialTheme.typography.titleMedium.copy(
                color = color,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp
            )
        )
    }
}

@Composable
fun MistakeBreakdownCard(mistakeStats: Map<MistakeCategory, Int>, totalMistakes: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ERROR CATEGORY OCCURRENCE",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = IndigoNeon,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(14.dp))

            MistakeCategoryProgressRow(
                categoryName = "Configuration & Service Setup",
                count = mistakeStats[MistakeCategory.CONFIGURATION] ?: 0,
                total = totalMistakes,
                color = WarningAmber
            )
            Spacer(modifier = Modifier.height(12.dp))
            MistakeCategoryProgressRow(
                categoryName = "Logical & Relational Reasoning",
                count = mistakeStats[MistakeCategory.LOGIC] ?: 0,
                total = totalMistakes,
                color = CyanNeon
            )
            Spacer(modifier = Modifier.height(12.dp))
            MistakeCategoryProgressRow(
                categoryName = "Conceptual Architectural Models",
                count = mistakeStats[MistakeCategory.CONCEPTUAL] ?: 0,
                total = totalMistakes,
                color = IndigoNeon
            )
            Spacer(modifier = Modifier.height(12.dp))
            MistakeCategoryProgressRow(
                categoryName = "Syntax, Semantics & Parameters",
                count = mistakeStats[MistakeCategory.SYNTAX] ?: 0,
                total = totalMistakes,
                color = SuccessEmerald
            )
        }
    }
}

@Composable
fun RetrievalAdviceCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyanNeon.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCardElevated),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(CyanNeon.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = CyanNeon,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Retrieval Learning Advice",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.5.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Avoid jumping directly to code edits or server restarts before inspecting runtime evidence. Formulate a hypothesis, check forensic traces (EXPLAIN plans, socket bindings, thread dumps), and isolate root causes step by step.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextMediumEmphasis,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            )
        }
    }
}

@Composable
private fun RecentLoggedMisstepsCard(
    mistakes: List<Mistake>,
    resolveScenarioTitle: (String) -> String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "RECENT LOGGED MISSTEPS",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CyanNeon,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (mistakes.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "No diagnostic missteps logged. Complete lab scenarios to track specific blindspots here.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMediumEmphasis,
                            lineHeight = 18.sp
                        )
                    )
                }
            } else {
                mistakes.forEachIndexed { index, mistake ->
                    if (index > 0) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = CardBorderColor.copy(alpha = 0.45f)
                        )
                    }
                    val catColor = when (mistake.category) {
                        MistakeCategory.CONFIGURATION -> WarningAmber
                        MistakeCategory.LOGIC -> CyanNeon
                        MistakeCategory.CONCEPTUAL -> IndigoNeon
                        MistakeCategory.SYNTAX -> SuccessEmerald
                    }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = resolveScenarioTitle(mistake.scenarioId),
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = TextWhite,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(catColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = mistake.category.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = catColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.5.sp
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = mistake.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMediumEmphasis,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MistakeCategoryProgressRow(
    categoryName: String,
    count: Int,
    total: Int,
    color: Color
) {
    val pct = if (total > 0) count.toFloat() / total.toFloat() else 0f
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categoryName,
                modifier = Modifier.weight(1f, fill = false),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextHighEmphasis,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(color.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "$count missteps",
                    maxLines = 1,
                    softWrap = false,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        LinearProgressIndicator(
            progress = { pct },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Color(0xFF1E293B)
        )
    }
}

private enum class HistoryFilter(val label: String) {
    ALL("All"),
    PASSED("Passed"),
    NEEDS_RETRY("Needs Retry")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistoryScreen(
    viewModel: DashboardViewModel,
    onNavigateToReport: (String) -> Unit
) {
    val attempts by viewModel.recentAttempts.collectAsState()
    var selectedFilter by rememberSaveable { mutableStateOf(HistoryFilter.ALL) }
    val sdf = remember { SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val passedCount = remember(attempts) { attempts.count { it.score >= 50 } }
    val retryCount = remember(attempts) { attempts.count { it.score < 50 } }

    val filteredAttempts = remember(attempts, selectedFilter) {
        when (selectedFilter) {
            HistoryFilter.ALL -> attempts
            HistoryFilter.PASSED -> attempts.filter { it.score >= 50 }
            HistoryFilter.NEEDS_RETRY -> attempts.filter { it.score < 50 }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        AdaptiveContainer(maxWidth = 1080.dp) { isWideScreen ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ObsidianBg)
                    .padding(horizontal = if (isWideScreen) 32.dp else 14.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 84.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Clean Left-Aligned Header
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Rescue History",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite,
                                    fontSize = if (isWideScreen) 24.sp else 20.sp
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(IndigoNeon.copy(alpha = 0.16f))
                                    .padding(horizontal = 9.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${attempts.size} sessions",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CyanNeon,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tap any completed investigation to inspect its forensic report, or remove individual logs.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextMediumEmphasis,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }

                // Filter Chip Row
                if (attempts.isNotEmpty()) {
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(HistoryFilter.entries) { filter ->
                                val count = when (filter) {
                                    HistoryFilter.ALL -> attempts.size
                                    HistoryFilter.PASSED -> passedCount
                                    HistoryFilter.NEEDS_RETRY -> retryCount
                                }
                                val isSelected = selectedFilter == filter
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedFilter = filter },
                                    label = {
                                        Text(
                                            text = "${filter.label} ($count)",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 11.5.sp
                                            )
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyanNeon.copy(alpha = 0.18f),
                                        selectedLabelColor = CyanNeon,
                                        containerColor = ObsidianCard,
                                        labelColor = TextMediumEmphasis
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = CardBorderColor,
                                        selectedBorderColor = CyanNeon.copy(alpha = 0.6f)
                                    )
                                )
                            }
                        }
                    }
                }

                if (filteredAttempts.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = TextLowEmphasis,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (attempts.isEmpty()) "No rescue sessions recorded yet."
                                    else "No sessions match '${selectedFilter.label}'.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextMediumEmphasis,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                } else {
                    items(filteredAttempts, key = { it.id }) { att ->
                        val dateStr = sdf.format(Date(att.completedAt))
                        val subjectCode = viewModel.getSubjectCodeForScenario(att.scenarioId)
                        val statusColor = when {
                            att.score >= 80 -> SuccessEmerald
                            att.score >= 50 -> WarningAmber
                            else -> ErrorRose
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToReport(att.id) }
                                .border(
                                    width = 1.dp,
                                    brush = Brush.horizontalGradient(
                                        listOf(
                                            statusColor.copy(alpha = 0.45f),
                                            CardBorderColor.copy(alpha = 0.4f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1527)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(15.dp)
                            ) {
                                // Top Row: Status Accent + Subject Code + Title + Chevron
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(3.5.dp)
                                                .height(20.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(statusColor)
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(5.dp))
                                                .background(IndigoNeon.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = subjectCode,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = CyanNeon,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = att.scenarioTitle,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextWhite,
                                                fontSize = 15.sp
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(CyanNeon.copy(alpha = 0.14f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "View Report",
                                            tint = CyanNeon,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Telemetry Metric Chips
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(statusColor.copy(alpha = 0.15f))
                                            .border(1.dp, statusColor.copy(alpha = 0.38f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "${att.score}% Accuracy",
                                            maxLines = 1,
                                            softWrap = false,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = statusColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF1E293B))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "${att.durationSeconds}s",
                                            maxLines = 1,
                                            softWrap = false,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = TextHighEmphasis,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }

                                    val misstepLabel = when (att.mistakesCount) {
                                        0 -> "0 missteps"
                                        1 -> "1 misstep"
                                        else -> "${att.mistakesCount} missteps"
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (att.mistakesCount == 0) SuccessEmerald.copy(alpha = 0.14f)
                                                else ErrorRose.copy(alpha = 0.14f)
                                            )
                                            .border(
                                                1.dp,
                                                if (att.mistakesCount == 0) SuccessEmerald.copy(alpha = 0.35f)
                                                else ErrorRose.copy(alpha = 0.35f),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = misstepLabel,
                                            maxLines = 1,
                                            softWrap = false,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (att.mistakesCount == 0) SuccessEmerald else ErrorRose,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Bottom Row: Timestamp + Compact Delete Action
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dateStr,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextLowEmphasis,
                                            fontSize = 11.sp
                                        )
                                    )

                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(ErrorRose.copy(alpha = 0.10f))
                                            .clickable {
                                                viewModel.deleteAttempt(att)
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Removed '${att.scenarioTitle}' from history.")
                                                }
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete Attempt",
                                            tint = ErrorRose.copy(alpha = 0.9f),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Remove",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = ErrorRose.copy(alpha = 0.9f),
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060A12)
@Composable
private fun MistakeBreakdownCardPreview() {
    LabRescueTheme(darkTheme = true) {
        Box(modifier = Modifier.background(ObsidianBg).padding(16.dp)) {
            MistakeBreakdownCard(
                mistakeStats = mapOf(
                    MistakeCategory.CONFIGURATION to 3,
                    MistakeCategory.LOGIC to 4,
                    MistakeCategory.CONCEPTUAL to 3,
                    MistakeCategory.SYNTAX to 2
                ),
                totalMistakes = 12
            )
        }
    }
}
