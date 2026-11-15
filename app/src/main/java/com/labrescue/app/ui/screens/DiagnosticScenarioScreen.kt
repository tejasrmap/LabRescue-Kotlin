package com.labrescue.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.delay
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labrescue.app.data.model.DiagnosticOption
import com.labrescue.app.ui.components.AdaptiveContainer
import com.labrescue.app.ui.components.EvidenceCard
import com.labrescue.app.ui.components.TerminalConsoleCard
import com.labrescue.app.ui.components.WhyFeedbackCard
import com.labrescue.app.ui.theme.CardBorderColor
import com.labrescue.app.ui.theme.CyanNeon
import com.labrescue.app.ui.theme.ErrorRose
import com.labrescue.app.ui.theme.ErrorRoseBg
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
import com.labrescue.app.ui.viewmodel.DiagnosticViewModel

@Composable
fun DiagnosticScenarioScreen(
    viewModel: DiagnosticViewModel,
    onNavigateBack: () -> Unit,
    onRescueCompleted: (String) -> Unit
) {
    val scenario = viewModel.scenario
    val currentNode by viewModel.currentNode.collectAsState()
    val selectedOption by viewModel.selectedOption.collectAsState()
    val evidenceRevealed by viewModel.evidenceRevealed.collectAsState()
    val isCompleted by viewModel.isCompleted.collectAsState()
    val showHint by viewModel.showHint.collectAsState()
    val attemptId by viewModel.lastGeneratedAttemptId.collectAsState()

    LaunchedEffect(isCompleted, attemptId) {
        if (isCompleted && attemptId != null) {
            onRescueCompleted(attemptId!!)
        }
    }

    if (scenario == null || currentNode == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBg),
            contentAlignment = Alignment.Center
        ) {
            Text("Scenario or Step not found", color = TextWhite)
        }
        return
    }

    val progress = currentNode!!.stepIndex.toFloat() / currentNode!!.totalSteps.toFloat()
    val listState = rememberLazyListState()

    LaunchedEffect(currentNode?.id) {
        listState.scrollToItem(0)
    }

    LaunchedEffect(evidenceRevealed) {
        if (evidenceRevealed) {
            delay(150)
            listState.animateScrollToItem(index = 3, scrollOffset = 500)
        }
    }

    AdaptiveContainer(maxWidth = 1120.dp) { isWideScreen ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBg)
                .padding(horizontal = if (isWideScreen) 32.dp else 14.dp),
            contentPadding = PaddingValues(top = 6.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(if (isWideScreen) 16.dp else 14.dp)
        ) {
            // Header Bar & Telemetry Status (Compact & Left-Aligned)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
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
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyanNeon.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "DIAGNOSTIC MODE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CyanNeon,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DiagnosticTimerBadge(viewModel = viewModel)
                            DiagnosticScoreBadge(viewModel = viewModel)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = scenario.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isWideScreen) 19.sp else 16.5.sp,
                            lineHeight = 22.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Progress Bar
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Step ${currentNode!!.stepIndex} of ${currentNode!!.totalSteps}",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMediumEmphasis)
                        )
                        Text(
                            text = "${(progress * 100).toInt()}% RESOLVED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanNeon,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = CyanNeon,
                        trackColor = ObsidianCardElevated
                    )
                }
            }

            // Dynamic Body Layout: 2 Columns on wide screens, 1 column on phones
            if (isWideScreen) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Left Column: Lab Context & Terminal Output
                        Column(
                            modifier = Modifier.weight(1.1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, CardBorderColor, RoundedCornerShape(18.dp)),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = "LABORATORY CONTEXT",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = IndigoNeon,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = scenario.labContext,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = TextHighEmphasis,
                                            lineHeight = 22.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Symptom: ${scenario.symptom}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextMediumEmphasis)
                                    )
                                }
                            }

                            TerminalConsoleCard(output = scenario.consoleOutput)
                        }

                        // Right Column: Interactive Diagnostic Action Tree
                        Column(
                            modifier = Modifier.weight(1.2f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            DiagnosticInteractionCard(
                                questionPrompt = currentNode!!.questionPrompt,
                                options = currentNode!!.options,
                                selectedOption = selectedOption,
                                evidenceRevealed = evidenceRevealed,
                                onSelectOption = { viewModel.selectOption(it) },
                                onProceed = { viewModel.proceedToNextStep() },
                                isFinalStep = currentNode!!.stepIndex >= currentNode!!.totalSteps || selectedOption?.nextNodeId == null,
                                showHint = showHint,
                                hintText = currentNode!!.investigationHint,
                                onToggleHint = { viewModel.toggleHint() }
                            )
                        }
                    }
                }
            } else {
                // Mobile Vertical Layout
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorderColor, RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "LAB CONTEXT & PROBLEM OBSERVATION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = IndigoNeon,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    fontSize = 10.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = scenario.labContext,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextHighEmphasis,
                                    fontSize = 13.5.sp,
                                    lineHeight = 19.sp
                                )
                            )
                        }
                    }
                }

                item {
                    TerminalConsoleCard(output = scenario.consoleOutput)
                }

                item {
                    DiagnosticInteractionCard(
                        questionPrompt = currentNode!!.questionPrompt,
                        options = currentNode!!.options,
                        selectedOption = selectedOption,
                        evidenceRevealed = evidenceRevealed,
                        onSelectOption = { viewModel.selectOption(it) },
                        onProceed = { viewModel.proceedToNextStep() },
                        isFinalStep = currentNode!!.stepIndex >= currentNode!!.totalSteps || selectedOption?.nextNodeId == null,
                        showHint = showHint,
                        hintText = currentNode!!.investigationHint,
                        onToggleHint = { viewModel.toggleHint() }
                    )
                }
            }
        }
    }
}

@Composable
fun DiagnosticInteractionCard(
    questionPrompt: String,
    options: List<DiagnosticOption>,
    selectedOption: DiagnosticOption?,
    evidenceRevealed: Boolean,
    onSelectOption: (DiagnosticOption) -> Unit,
    onProceed: () -> Unit,
    isFinalStep: Boolean,
    showHint: Boolean,
    hintText: String,
    onToggleHint: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Question Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorderColor, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "INVESTIGATION DECISION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyanNeon,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = questionPrompt,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        fontSize = 14.5.sp,
                        lineHeight = 20.sp
                    )
                )
            }
        }

        // Diagnostic Options
        options.forEach { option ->
            val isSelected = selectedOption?.id == option.id
            val isAnswered = selectedOption != null

            DiagnosticOptionItem(
                option = option,
                isSelected = isSelected,
                isAnswered = isAnswered,
                onClick = {
                    if (!isAnswered) {
                        onSelectOption(option)
                    }
                }
            )
        }

        // Revealed Evidence and Feedback
        if (evidenceRevealed && selectedOption != null) {
            EvidenceCard(evidenceText = selectedOption.evidenceDetails)

            WhyFeedbackCard(
                isCorrect = selectedOption.isCorrectPath,
                explanation = selectedOption.whyExplanation,
                category = selectedOption.penaltyCategory
            )

            // Proceed Action Button
            Button(
                onClick = onProceed,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedOption.isCorrectPath) CyanNeon else WarningAmber
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (isFinalStep) "Finalize Rescue Report" else "Proceed to Next Branch",
                    color = ObsidianBg,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = ObsidianBg
                )
            }
        }

        // Hint Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = onToggleHint,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = WarningAmber,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (showHint) "Hide Diagnostic Hint" else "Need Diagnostic Hint?",
                    color = TextMediumEmphasis,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        if (showHint) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, WarningAmber.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard)
            ) {
                Text(
                    text = "Hint: $hintText",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = WarningAmber,
                        lineHeight = 18.sp
                    ),
                    modifier = Modifier.padding(14.dp)
                )
            }
        }
    }
}

@Composable
fun DiagnosticTimerBadge(viewModel: DiagnosticViewModel) {
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val timeFormatted = remember(elapsedSeconds) {
        "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ObsidianCardElevated)
            .border(1.dp, CardBorderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                tint = TextMediumEmphasis,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = timeFormatted,
                maxLines = 1,
                softWrap = false,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = TextHighEmphasis,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                )
            )
        }
    }
}

@Composable
fun DiagnosticScoreBadge(viewModel: DiagnosticViewModel) {
    val score by viewModel.score.collectAsState()
    val isGood = score >= 80
    val targetColor = if (isGood) SuccessEmerald else WarningAmber
    val animatedBg by animateColorAsState(
        targetValue = targetColor.copy(alpha = 0.2f),
        animationSpec = tween(300),
        label = "scoreBg"
    )
    val animatedBorder by animateColorAsState(
        targetValue = targetColor.copy(alpha = 0.5f),
        animationSpec = tween(300),
        label = "scoreBorder"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(animatedBg)
            .border(1.dp, animatedBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp, vertical = 5.dp)
    ) {
        Text(
            text = "$score PTS",
            maxLines = 1,
            softWrap = false,
            style = MaterialTheme.typography.labelMedium.copy(
                color = targetColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.5.sp
            )
        )
    }
}

@Composable
fun DiagnosticOptionItem(
    option: DiagnosticOption,
    isSelected: Boolean,
    isAnswered: Boolean,
    onClick: () -> Unit
) {
    val targetBorder = when {
        isSelected && option.isCorrectPath -> SuccessEmerald
        isSelected && !option.isCorrectPath -> ErrorRose
        else -> CardBorderColor
    }

    val targetBackground = when {
        isSelected && option.isCorrectPath -> SuccessEmeraldBg.copy(alpha = 0.22f)
        isSelected && !option.isCorrectPath -> ErrorRoseBg.copy(alpha = 0.22f)
        else -> ObsidianCard
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = targetBorder,
        animationSpec = tween(250),
        label = "optionBorder"
    )
    val animatedBackgroundColor by animateColorAsState(
        targetValue = targetBackground,
        animationSpec = tween(250),
        label = "optionBg"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isAnswered && !isSelected) 0.62f else 1f)
            .clickable(enabled = !isAnswered, onClick = onClick)
            .border(1.2.dp, animatedBorderColor, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = animatedBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (option.isCorrectPath) SuccessEmerald else ErrorRose),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (option.isCorrectPath) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = ObsidianBg,
                        modifier = Modifier.size(14.dp)
                    )
                }
            } else {
                RadioButton(
                    selected = false,
                    onClick = if (!isAnswered) onClick else null,
                    modifier = Modifier.size(24.dp),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = CyanNeon,
                        unselectedColor = TextLowEmphasis,
                        disabledUnselectedColor = TextLowEmphasis.copy(alpha = 0.5f)
                    ),
                    enabled = !isAnswered
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = option.label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = TextWhite,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp
                )
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060A12)
@Composable
private fun DiagnosticOptionItemPreview() {
    LabRescueTheme(darkTheme = true) {
        Column(
            modifier = Modifier.background(ObsidianBg).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DiagnosticOptionItem(
                option = DiagnosticOption(
                    id = "opt_1",
                    label = "Run EXPLAIN on the JOIN query and inspect key cardinality",
                    evidenceDetails = " cardinality 1:N ",
                    isCorrectPath = true,
                    whyExplanation = "Inspecting query execution plans confirms 1-to-many expansion."
                ),
                isSelected = false,
                isAnswered = false,
                onClick = {}
            )
            DiagnosticOptionItem(
                option = DiagnosticOption(
                    id = "opt_2",
                    label = "Verified root cause option selected",
                    evidenceDetails = " cardinality 1:N ",
                    isCorrectPath = true,
                    whyExplanation = "Correct check."
                ),
                isSelected = true,
                isAnswered = true,
                onClick = {}
            )
        }
    }
}
