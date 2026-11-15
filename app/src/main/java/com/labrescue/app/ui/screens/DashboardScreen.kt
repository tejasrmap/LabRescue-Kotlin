package com.labrescue.app.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.ForkLeft
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labrescue.app.data.db.SampleData
import com.labrescue.app.data.model.Subject
import com.labrescue.app.ui.components.AdaptiveContainer
import com.labrescue.app.ui.components.UnifiedTelemetryStrip
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

fun iconForSubject(subjectId: String): ImageVector = when (subjectId) {
    "db" -> Icons.Default.Dataset
    "net" -> Icons.Default.NetworkCheck
    "prog" -> Icons.Default.Code
    "os" -> Icons.Default.Memory
    "git" -> Icons.Default.ForkLeft
    "web" -> Icons.Default.Language
    "android" -> Icons.Default.PhoneAndroid
    "algo" -> Icons.Default.AccountTree
    "linux" -> Icons.Default.Terminal
    "security" -> Icons.Default.Security
    "data" -> Icons.Default.Analytics
    "testing" -> Icons.Default.BugReport
    else -> Icons.Default.School
}

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToSubject: (String) -> Unit = {},
    onNavigateToAllSubjects: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToScenario: (String) -> Unit,
    onNavigateToReport: (String) -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val attempts by viewModel.recentAttempts.collectAsState()
    val accuracy by viewModel.diagnosticAccuracy.collectAsState()
    val recommended by viewModel.recommendedScenario.collectAsState()

    AdaptiveContainer(maxWidth = 1050.dp) { isWideScreen ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBg)
                .padding(horizontal = if (isWideScreen) 32.dp else 14.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sleek Compact Horizontal Greeting Header
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            Brush.horizontalGradient(listOf(IndigoNeon.copy(alpha = 0.4f), CardBorderColor.copy(alpha = 0.55f))),
                            RoundedCornerShape(18.dp)
                        ),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCardElevated),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(Brush.linearGradient(listOf(CyanNeon, IndigoNeon))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Profile Avatar",
                                tint = ObsidianBg,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(SuccessEmerald)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LAB WORKFLOW ACTIVE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SuccessEmerald,
                                        letterSpacing = 1.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Welcome back, ${userProfile.name}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = if (isWideScreen) 20.sp else 17.sp
                                )
                            )
                            Text(
                                text = "${userProfile.programme} • ${userProfile.year}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextMediumEmphasis,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            // Telemetry Strip
            item {
                UnifiedTelemetryStrip(
                    rescuedCount = userProfile.totalRescues,
                    accuracyPct = accuracy,
                    streakDays = userProfile.streakDays
                )
            }

            // Recommended Rescue Feature Card
            item {
                recommended?.let { rec ->
                    val recSubject = subjects.find { it.id == rec.subjectId }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                Brush.horizontalGradient(listOf(CyanNeon.copy(alpha = 0.75f), IndigoNeon.copy(alpha = 0.5f))),
                                RoundedCornerShape(20.dp)
                            ),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f, fill = false),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(CyanNeon.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = null,
                                            tint = CyanNeon,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(7.dp))
                                    Text(
                                        text = "RECOMMENDED RESCUE" + (recSubject?.let { " • ${it.code}" } ?: ""),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CyanNeon,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.8.sp,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF1E293B))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Timer,
                                            contentDescription = null,
                                            tint = TextLowEmphasis,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${rec.estimatedMinutes}m",
                                            maxLines = 1,
                                            softWrap = false,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = TextHighEmphasis,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = rec.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    lineHeight = 22.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = rec.symptom,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextMediumEmphasis,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(IndigoNeon.copy(alpha = 0.18f))
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = rec.difficulty.name,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CyanNeon,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.8.sp,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }

                                Button(
                                    onClick = { onNavigateToScenario(rec.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 9.dp)
                                ) {
                                    Text("Launch Rescue", color = ObsidianBg, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = ObsidianBg,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick-Launch Coursework Disciplines LazyRow
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeaderRow(
                        title = "Core Lab Disciplines",
                        actionLabel = "All ${subjects.size} Modules",
                        onActionClick = onNavigateToAllSubjects
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(subjects.take(6), key = { it.id }) { subject ->
                            CompactSubjectLaunchCard(
                                subject = subject,
                                onClick = { onNavigateToSubject(subject.id) }
                            )
                        }
                    }
                }
            }

            // Recent Diagnostic History Section
            if (attempts.isNotEmpty()) {
                item {
                    SectionHeaderRow(
                        title = "Recent Diagnostic History",
                        actionLabel = "View All (${attempts.size})",
                        onActionClick = onNavigateToHistory
                    )
                }

                items(attempts.take(3), key = { it.id }) { att ->
                    val subject = subjects.find { it.id == att.subjectId }
                    val subjectCode = subject?.code ?: "LAB"
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
                                        CardBorderColor.copy(alpha = 0.45f)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(IndigoNeon.copy(alpha = 0.18f))
                                            .border(1.dp, IndigoNeon.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = subjectCode,
                                            maxLines = 1,
                                            softWrap = false,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = CyanNeon,
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
                                            text = "⏱ ${att.durationSeconds}s",
                                            maxLines = 1,
                                            softWrap = false,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = TextHighEmphasis,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }
                                }

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

                            Text(
                                text = att.scenarioTitle,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    lineHeight = 20.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(statusColor.copy(alpha = 0.16f))
                                        .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.5.dp)
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
                                        .padding(horizontal = 8.dp, vertical = 3.5.dp)
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
                        }
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorderColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Ready for Your First Practical Lab",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Select a core coursework discipline or launch the recommended rescue above to begin troubleshooting.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextMediumEmphasis,
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    lineHeight = 17.sp
                                )
                            )
                        }
                    }
                }
            }

            // Bottom clearance so content never collides with floating navigation dock
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
private fun SectionHeaderRow(
    title: String,
    actionLabel: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onActionClick)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = CyanNeon,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            )
            Spacer(modifier = Modifier.width(3.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = CyanNeon,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

@Composable
private fun CompactSubjectLaunchCard(
    subject: Subject,
    onClick: () -> Unit
) {
    val icon = iconForSubject(subject.id)
    Card(
        modifier = Modifier
            .width(156.dp)
            .clickable(onClick = onClick)
            .border(1.dp, CardBorderColor.copy(alpha = 0.65f), RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(13.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(IndigoNeon.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = subject.name,
                        tint = CyanNeon,
                        modifier = Modifier.size(19.dp)
                    )
                }

                Text(
                    text = subject.code,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyanNeon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = subject.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${subject.totalScenarios} labs available",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextLowEmphasis,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun SubjectCardItem(
    subject: Subject,
    onClick: () -> Unit
) {
    val icon = iconForSubject(subject.id)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, CardBorderColor.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(IndigoNeon.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = subject.name,
                    tint = CyanNeon,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = subject.code,
                            maxLines = 1,
                            softWrap = false,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanNeon,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    if (subject.totalScenarios > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(IndigoNeon.copy(alpha = 0.18f))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${subject.totalScenarios} labs",
                                maxLines = 1,
                                softWrap = false,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextHighEmphasis,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = subject.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        fontSize = 15.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subject.description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMediumEmphasis,
                        lineHeight = 16.5.sp,
                        fontSize = 12.2.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TextLowEmphasis,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060A12)
@Composable
private fun SubjectCardItemPreview() {
    LabRescueTheme(darkTheme = true) {
        Box(modifier = Modifier.background(ObsidianBg).padding(16.dp)) {
            SubjectCardItem(
                subject = SampleData.subjects.first(),
                onClick = {}
            )
        }
    }
}
