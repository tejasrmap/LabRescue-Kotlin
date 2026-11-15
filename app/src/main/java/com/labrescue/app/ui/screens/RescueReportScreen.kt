package com.labrescue.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labrescue.app.data.db.SampleData
import com.labrescue.app.data.model.Attempt
import com.labrescue.app.data.model.Mistake
import com.labrescue.app.data.model.MistakeCategory
import com.labrescue.app.data.model.Scenario
import com.labrescue.app.data.repository.LabRescueRepository
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
import com.labrescue.app.ui.theme.SuccessEmeraldBg
import com.labrescue.app.ui.theme.TextHighEmphasis
import com.labrescue.app.ui.theme.TextLowEmphasis
import com.labrescue.app.ui.theme.TextMediumEmphasis
import com.labrescue.app.ui.theme.TextWhite
import com.labrescue.app.ui.theme.WarningAmber
import com.labrescue.app.ui.theme.WarningAmberBg

@Composable
fun RescueReportScreen(
    attemptId: String,
    repository: LabRescueRepository,
    onNavigateToHome: () -> Unit,
    onNavigateToScenario: (String) -> Unit,
    onNavigateBack: () -> Unit = onNavigateToHome
) {
    val attempts by repository.attempts.collectAsState()
    val mistakes by repository.mistakes.collectAsState()
    val attempt = remember(attempts, attemptId) { attempts.find { it.id == attemptId } }
    val attemptMistakes = remember(mistakes, attemptId) { mistakes.filter { it.attemptId == attemptId } }
    val scenario = attempt?.let { repository.getScenario(it.scenarioId) }

    if (attempt == null || scenario == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBg),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Rescue Report not found", color = TextWhite)
                Spacer(modifier = Modifier.height(14.dp))
                Button(onClick = onNavigateToHome) {
                    Text("Return to Home")
                }
            }
        }
        return
    }

    val isPassed = attempt.score >= 50
    val durationMin = attempt.durationSeconds / 60
    val durationSec = attempt.durationSeconds % 60
    val timeFormatted = remember(attempt.durationSeconds) {
        "${durationMin.toString().padStart(2, '0')}:${durationSec.toString().padStart(2, '0')}"
    }

    AdaptiveContainer(maxWidth = 1050.dp) { isWideScreen ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBg)
                .padding(horizontal = if (isWideScreen) 32.dp else 14.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 26.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Navigation Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = CyanNeon,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Diagnostic Report",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isPassed) SuccessEmerald.copy(alpha = 0.14f)
                                else WarningAmber.copy(alpha = 0.14f)
                            )
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isPassed) "PASSED" else "NEEDS REVIEW",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isPassed) SuccessEmerald else WarningAmber,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Status Hero Card (Compact & Crisp)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.2.dp,
                            if (isPassed) Brush.horizontalGradient(listOf(SuccessEmerald, CyanNeon))
                            else Brush.horizontalGradient(listOf(WarningAmber, ErrorRose)),
                            RoundedCornerShape(18.dp)
                        ),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCardElevated),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isPassed) SuccessEmeraldBg.copy(alpha = 0.35f)
                                        else WarningAmberBg.copy(alpha = 0.35f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (isPassed) SuccessEmerald else WarningAmber,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isPassed) "PRACTICAL LAB RESCUED" else "COMPLETED WITH MISSTEPS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isPassed) SuccessEmerald else WarningAmber,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = scenario.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite,
                                        fontSize = if (isWideScreen) 20.sp else 17.sp,
                                        lineHeight = 22.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Metric Pillars Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF090F1D))
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MetricPillar(
                                label = "ACCURACY",
                                value = "${attempt.score}%",
                                valueColor = CyanNeon,
                                modifier = Modifier.weight(1f)
                            )

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(28.dp)
                                    .background(CardBorderColor.copy(alpha = 0.5f))
                            )

                            MetricPillar(
                                label = "DURATION",
                                value = timeFormatted,
                                valueColor = TextWhite,
                                modifier = Modifier.weight(1f)
                            )

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(28.dp)
                                    .background(CardBorderColor.copy(alpha = 0.5f))
                            )

                            MetricPillar(
                                label = "MISSTEPS",
                                value = "${attempt.mistakesCount}",
                                valueColor = if (attempt.mistakesCount == 0) SuccessEmerald else ErrorRose,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Adaptive Section: Evaluation + Root Cause
            if (isWideScreen) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            QualitativeEvaluationCard(attempt = attempt, attemptMistakes = attemptMistakes)
                        }
                        Box(modifier = Modifier.weight(1.2f)) {
                            RootCauseCard(scenario = scenario)
                        }
                    }
                }
            } else {
                item {
                    QualitativeEvaluationCard(attempt = attempt, attemptMistakes = attemptMistakes)
                }
                item {
                    RootCauseCard(scenario = scenario)
                }
            }

            // Logged Session Missteps (Data-Driven from Room Mistake Entity)
            if (attemptMistakes.isNotEmpty()) {
                item {
                    SessionMisstepsCard(mistakes = attemptMistakes)
                }
            }

            // Action Buttons (Primary Next + Retry / Home Row)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val nextScenario = SampleData.scenarios.find { it.id != scenario.id && it.subjectId == scenario.subjectId }
                        ?: SampleData.scenarios.find { it.id != scenario.id }

                    nextScenario?.let { next ->
                        Button(
                            onClick = { onNavigateToScenario(next.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Practice Next: ${next.title}",
                                    color = ObsidianBg,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = ObsidianBg,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onNavigateToScenario(scenario.id) },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, CardBorderColor),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = ObsidianCardElevated),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = TextWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Retry Lab",
                                color = TextWhite,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.5.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateToHome,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, CyanNeon.copy(alpha = 0.4f)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = ObsidianCard),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Dashboard",
                                color = CyanNeon,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricPillar(
    label: String,
    value: String,
    valueColor: Color,
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
                letterSpacing = 0.8.sp,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            maxLines = 1,
            softWrap = false,
            style = MaterialTheme.typography.titleLarge.copy(
                color = valueColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            )
        )
    }
}

@Composable
fun DiagnosticProfileRow(label: String, status: String, statusColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextMediumEmphasis,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(statusColor.copy(alpha = 0.14f))
                .padding(horizontal = 9.dp, vertical = 3.dp)
        ) {
            Text(
                text = status,
                maxLines = 1,
                softWrap = false,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                )
            )
        }
    }
}

@Composable
fun QualitativeEvaluationCard(
    attempt: Attempt,
    attemptMistakes: List<Mistake> = emptyList()
) {
    fun statusForCategory(category: MistakeCategory, defaultLabel: String, defaultColor: Color): Pair<String, Color> {
        val count = attemptMistakes.count { it.category == category }
        return when {
            count >= 2 -> "Review ($count)" to ErrorRose
            count == 1 -> "1 Misstep" to WarningAmber
            attemptMistakes.isEmpty() && attempt.mistakesCount > 0 && category == MistakeCategory.LOGIC ->
                "Needs Practice" to WarningAmber
            else -> defaultLabel to defaultColor
        }
    }

    val (syntaxStatus, syntaxColor) = statusForCategory(MistakeCategory.SYNTAX, "Strong", SuccessEmerald)
    val (configStatus, configColor) = statusForCategory(MistakeCategory.CONFIGURATION, "Consistent", SuccessEmerald)
    val (logicStatus, logicColor) = statusForCategory(MistakeCategory.LOGIC, "Solid", SuccessEmerald)
    val (conceptStatus, conceptColor) = statusForCategory(MistakeCategory.CONCEPTUAL, "Verified", CyanNeon)

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
                text = "COMPETENCY EVALUATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = IndigoNeon,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            DiagnosticProfileRow("Syntax & Parameter Validation", syntaxStatus, syntaxColor)
            DiagnosticProfileRow("Configuration & Service Setup", configStatus, configColor)
            DiagnosticProfileRow("Logical & Relational Reasoning", logicStatus, logicColor)
            DiagnosticProfileRow("Core Conceptual Framework", conceptStatus, conceptColor)
        }
    }
}

@Composable
fun SessionMisstepsCard(mistakes: List<Mistake>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ErrorRose.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = ErrorRose,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logged Missteps in This Session (${mistakes.size})",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            mistakes.forEachIndexed { index, mistake ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = CardBorderColor.copy(alpha = 0.4f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(WarningAmber.copy(alpha = 0.14f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = mistake.category.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = WarningAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = mistake.description,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextHighEmphasis,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun RootCauseCard(scenario: Scenario) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, IndigoNeon.copy(alpha = 0.45f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(IndigoNeon.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = CyanNeon,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Root Cause Analysis",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.5.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = scenario.rootCauseSummary,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextHighEmphasis,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "RECOMMENDED REMEDIAL FIX",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CyanNeon,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.9.sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF040711))
                    .border(1.dp, CardBorderColor, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = scenario.fixRecommendation,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF38BDF8),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060A12)
@Composable
private fun QualitativeEvaluationCardPreview() {
    LabRescueTheme(darkTheme = true) {
        Box(modifier = Modifier.background(ObsidianBg).padding(16.dp)) {
            QualitativeEvaluationCard(
                attempt = Attempt(
                    id = "att_demo",
                    scenarioId = "sc_db_join",
                    scenarioTitle = "SQL JOIN Duplicate Rows",
                    subjectId = "sub_db",
                    score = 75,
                    durationSeconds = 140,
                    completedAt = 1793435000000L,
                    isSuccess = true,
                    mistakesCount = 1,
                    decisionsPathJson = "[\"opt_1\",\"opt_2\"]"
                ),
                attemptMistakes = listOf(
                    Mistake(
                        id = "m_1",
                        attemptId = "att_demo",
                        scenarioId = "sc_db_join",
                        category = MistakeCategory.LOGIC,
                        description = "Applied SELECT DISTINCT before inspecting JOIN cardinality."
                    )
                )
            )
        }
    }
}
