package com.labrescue.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labrescue.app.data.model.MistakeCategory
import com.labrescue.app.ui.navigation.Screen
import com.labrescue.app.ui.theme.CardBorderColor
import com.labrescue.app.ui.theme.CyanNeon
import com.labrescue.app.ui.theme.ErrorRose
import com.labrescue.app.ui.theme.ErrorRoseBg
import com.labrescue.app.ui.theme.IndigoNeon
import com.labrescue.app.ui.theme.LabRescueTheme
import com.labrescue.app.ui.theme.ObsidianBg
import com.labrescue.app.ui.theme.ObsidianCard
import com.labrescue.app.ui.theme.ObsidianCardElevated
import com.labrescue.app.ui.theme.ObsidianSurface
import com.labrescue.app.ui.theme.SuccessEmerald
import com.labrescue.app.ui.theme.SuccessEmeraldBg
import com.labrescue.app.ui.theme.TerminalCyan
import com.labrescue.app.ui.theme.TerminalDeepBg
import com.labrescue.app.ui.theme.TerminalHeaderBg
import com.labrescue.app.ui.theme.TextHighEmphasis
import com.labrescue.app.ui.theme.TextLowEmphasis
import com.labrescue.app.ui.theme.TextMediumEmphasis
import com.labrescue.app.ui.theme.TextWhite
import com.labrescue.app.ui.theme.WarningAmber

/**
 * Adaptive container that provides relaxed responsive breathing room.
 */
@Composable
fun AdaptiveContainer(
    maxWidth: Dp = 1000.dp,
    modifier: Modifier = Modifier,
    content: @Composable (isWideScreen: Boolean) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        val isWide = maxWidth >= 600.dp && this.maxWidth >= 600.dp
        Box(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .fillMaxWidth()
        ) {
            content(isWide)
        }
    }
}

/**
 * Relaxed, unconstrained Telemetry Strip that replaces cramped 3-card rows.
 * Features 3 beautifully spaced columns with subtle vertical dividers so words NEVER wrap awkwardly!
 */
@Composable
fun UnifiedTelemetryStrip(
    rescuedCount: Int,
    accuracyPct: Int,
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val isCompact = maxWidth < 360.dp
        val headlineSize = if (isCompact) 21.sp else 26.sp
        val labelSize = if (isCompact) 10.sp else 11.sp
        val subLabelSize = if (isCompact) 11.sp else 12.sp
        val vertPad = if (isCompact) 14.dp else 18.dp
        val horizPad = if (isCompact) 8.dp else 12.dp

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorderColor.copy(alpha = 0.7f), RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(vertical = vertPad, horizontal = horizPad),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Metric 1: Rescued
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "RESCUED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMediumEmphasis,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = labelSize
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$rescuedCount",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = CyanNeon,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = headlineSize
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "labs solved",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextLowEmphasis,
                            fontSize = subLabelSize
                        )
                    )
                }

                // Divider 1
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight(0.7f)
                        .background(CardBorderColor.copy(alpha = 0.8f))
                )

                // Metric 2: Accuracy
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ACCURACY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMediumEmphasis,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = labelSize
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$accuracyPct%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = SuccessEmerald,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = headlineSize
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (rescuedCount == 0) "no attempts" else "first pass",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextLowEmphasis,
                            fontSize = subLabelSize
                        )
                    )
                }

                // Divider 2
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight(0.7f)
                        .background(CardBorderColor.copy(alpha = 0.8f))
                )

                // Metric 3: Streak
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "STREAK",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMediumEmphasis,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = labelSize
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${streakDays}d",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = WarningAmber,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = headlineSize
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "daily practice",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextLowEmphasis,
                            fontSize = subLabelSize
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun TerminalConsoleCard(
    output: String,
    title: String = "DIAGNOSTIC LOG",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderColor.copy(alpha = 0.8f), RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = TerminalDeepBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TerminalHeaderBg)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF27C93F)))
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = CyanNeon,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMediumEmphasis,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp,
                            fontSize = 10.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "CONSOLE",
                        maxLines = 1,
                        softWrap = false,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            color = CyanNeon,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Console output with horizontal scrolling for long stack traces
            val scrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text = output,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.5.sp,
                        lineHeight = 17.sp,
                        color = TerminalCyan
                    )
                )
            }
        }
    }
}

@Composable
fun EvidenceCard(
    evidenceText: String,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = evidenceText.isNotEmpty(),
        enter = fadeIn() + slideInVertically()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(listOf(IndigoNeon, CyanNeon)),
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCardElevated),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(IndigoNeon.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Evidence",
                            tint = CyanNeon,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "FORENSIC EVIDENCE CAPTURE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanNeon,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.1.sp
                            )
                        )
                        Text(
                            text = "State After Diagnostic Check",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = TextMediumEmphasis
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = evidenceText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextHighEmphasis,
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp
                    )
                )
            }
        }
    }
}

@Composable
fun WhyFeedbackCard(
    isCorrect: Boolean,
    explanation: String,
    category: MistakeCategory? = null,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isCorrect) SuccessEmerald else ErrorRose
    val titleColor = if (isCorrect) SuccessEmerald else ErrorRose
    val icon = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Error
    val headerTitle = if (isCorrect) "Reasoning Verified" else "Diagnostic Blindspot: ${category?.name ?: "MISSTEP"}"
    val cardBg = if (isCorrect) SuccessEmeraldBg.copy(alpha = 0.15f) else ErrorRoseBg.copy(alpha = 0.15f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(cardBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = titleColor,
                        modifier = Modifier.size(19.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isCorrect) "COGNITIVE FEEDBACK" else "ERROR LOGGED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = titleColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = headerTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextHighEmphasis,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = explanation,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextHighEmphasis.copy(alpha = 0.92f),
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

/**
 * Curved Floating Island Main Navigation Bar.
 * High-end floating pill dock with rounded corners, subtle glassmorphism,
 * gradient border shine, and glowing active tab indicators.
 */
@Composable
fun LabRescueBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val screenWidth = maxWidth
        val isCompact = screenWidth < 380.dp
        val navHorizPadding = if (isCompact) 10.dp else 14.dp
        val navVertPadding = 6.dp
        val itemHorizPad = if (isCompact) 8.dp else 14.dp
        val itemVertPad = 7.dp
        val iconSize = if (isCompact) 19.dp else 21.dp
        val fontSize = if (isCompact) 10.5.sp else 11.sp

        Card(
            modifier = Modifier
                .padding(horizontal = navHorizPadding, vertical = navVertPadding)
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            CyanNeon.copy(alpha = 0.6f),
                            IndigoNeon.copy(alpha = 0.4f),
                            CardBorderColor.copy(alpha = 0.7f)
                        )
                    ),
                    shape = RoundedCornerShape(26.dp)
                ),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A101D)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val items = listOf(
                    Triple(Screen.Dashboard.route, "Home", Icons.Default.Home),
                    Triple(Screen.SubjectList.route, "Labs", Icons.Default.School),
                    Triple(Screen.MistakeProfile.route, "Metrics", Icons.Default.Analytics),
                    Triple(Screen.History.route, "History", Icons.Default.History),
                    Triple(Screen.ProfileSettings.route, "Profile", Icons.Default.Person)
                )

                items.forEach { (route, label, icon) ->
                    val selected = currentRoute == route

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (selected) CyanNeon.copy(alpha = 0.14f) else Color.Transparent)
                            .then(
                                if (selected) Modifier.border(1.dp, CyanNeon.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                                else Modifier
                            )
                            .clickable { if (!selected) onNavigate(route) }
                            .padding(horizontal = if (isCompact) 10.dp else 14.dp, vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (selected) CyanNeon else TextLowEmphasis,
                                modifier = Modifier.size(iconSize)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = label,
                                maxLines = 1,
                                softWrap = false,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                                    fontSize = fontSize,
                                    color = if (selected) CyanNeon else TextLowEmphasis
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060A12)
@Composable
private fun UnifiedTelemetryStripPreview() {
    LabRescueTheme(darkTheme = true) {
        Box(modifier = Modifier.background(ObsidianBg).padding(16.dp)) {
            UnifiedTelemetryStrip(rescuedCount = 9, accuracyPct = 78, streakDays = 3)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060A12)
@Composable
private fun TerminalConsoleCardPreview() {
    LabRescueTheme(darkTheme = true) {
        Box(modifier = Modifier.background(ObsidianBg).padding(16.dp)) {
            TerminalConsoleCard(
                output = "ERROR 1205 (HY000): Lock wait timeout exceeded; try restarting transaction"
            )
        }
    }
}
