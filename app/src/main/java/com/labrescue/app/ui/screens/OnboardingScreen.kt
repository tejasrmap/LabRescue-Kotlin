package com.labrescue.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labrescue.app.ui.components.AdaptiveContainer
import com.labrescue.app.ui.theme.CardBorderColor
import com.labrescue.app.ui.theme.CyanNeon
import com.labrescue.app.ui.theme.IndigoNeon
import com.labrescue.app.ui.theme.LabRescueTheme
import com.labrescue.app.ui.theme.ObsidianBg
import com.labrescue.app.ui.theme.ObsidianCard
import com.labrescue.app.ui.theme.ObsidianCardElevated
import com.labrescue.app.ui.theme.SuccessEmerald
import com.labrescue.app.ui.theme.TextLowEmphasis
import com.labrescue.app.ui.theme.TextMediumEmphasis
import com.labrescue.app.ui.theme.TextWhite
import com.labrescue.app.ui.theme.WarningAmber

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 3
    val scrollState = rememberScrollState()

    LaunchedEffect(currentStep) {
        scrollState.scrollTo(0)
    }

    AdaptiveContainer(maxWidth = 880.dp) { isWideScreen ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBg)
                .padding(horizontal = if (isWideScreen) 32.dp else 16.dp)
        ) {
            // Pinned Top Brand Bar & Skip Option
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.linearGradient(listOf(CyanNeon, IndigoNeon))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = ObsidianBg,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "LabRescue",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.4.sp
                        )
                    )
                }

                if (currentStep < totalSteps - 1) {
                    TextButton(onClick = onFinishOnboarding) {
                        Text(
                            text = "Skip Tour",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = TextMediumEmphasis,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable Middle Slide Body
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally(animationSpec = tween(300, easing = FastOutSlowInEasing)) { it } + fadeIn(tween(240)))
                                .togetherWith(slideOutHorizontally(animationSpec = tween(300, easing = FastOutSlowInEasing)) { -it } + fadeOut(tween(200)))
                        } else {
                            (slideInHorizontally(animationSpec = tween(300, easing = FastOutSlowInEasing)) { -it } + fadeIn(tween(240)))
                                .togetherWith(slideOutHorizontally(animationSpec = tween(300, easing = FastOutSlowInEasing)) { it } + fadeOut(tween(200)))
                        }
                    },
                    label = "OnboardingStep"
                ) { step ->
                    when (step) {
                        0 -> OnboardingStepWelcome()
                        1 -> OnboardingStepDisciplines()
                        else -> OnboardingStepScience()
                    }
                }
            }

            // Pinned Bottom Progress Dots & Navigation Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(totalSteps) { index ->
                        val isSelected = currentStep == index
                        val pillWidth by animateDpAsState(
                            targetValue = if (isSelected) 26.dp else 8.dp,
                            animationSpec = tween(250, easing = FastOutSlowInEasing),
                            label = "pillWidth"
                        )
                        val pillColor by animateColorAsState(
                            targetValue = if (isSelected) CyanNeon else CardBorderColor,
                            animationSpec = tween(250),
                            label = "pillColor"
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(6.dp)
                                .width(pillWidth)
                                .clip(CircleShape)
                                .background(pillColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 0) {
                        OutlinedButton(
                            onClick = { currentStep -= 1 },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, CardBorderColor),
                            modifier = Modifier
                                .weight(0.34f)
                                .height(48.dp)
                        ) {
                            Text("Back", color = TextWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }

                    Button(
                        onClick = {
                            if (currentStep < totalSteps - 1) {
                                currentStep += 1
                            } else {
                                onFinishOnboarding()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(if (currentStep > 0) 0.66f else 1f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = if (currentStep == totalSteps - 1) "Enter LabRescue Hub" else "Continue",
                            color = ObsidianBg,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.5.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (currentStep == totalSteps - 1) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = ObsidianBg,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingStepWelcome() {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(IndigoNeon.copy(alpha = 0.2f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "STEP 1 OF 3  •  PURPOSE & MISSION",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CyanNeon,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.5.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "When your practical fails, learn how to fix it.",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = TextWhite,
                fontSize = 24.sp,
                lineHeight = 31.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Instead of passive quizzes, LabRescue immerses you in real university lab failures — teaching you to form hypotheses, inspect forensic traces, and isolate root causes.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextMediumEmphasis,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorderColor.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "THE DIAGNOSTIC LEARNING LOOP",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyanNeon,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                DiagnosticLoopStep(number = "01", label = "Observe Runtime Symptom", desc = "Unexpected SQL rows, refused sockets, or deadlocks")
                DiagnosticLoopStep(number = "02", label = "Inspect Forensic Evidence", desc = "Examine query plans, port bindings & thread dumps")
                DiagnosticLoopStep(number = "03", label = "Isolate Root Cause & Fix", desc = "Resolve the underlying fault rather than masking symptoms")
            }
        }
    }
}

@Composable
fun DiagnosticLoopStep(number: String, label: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number, style = MaterialTheme.typography.labelSmall.copy(color = CyanNeon, fontWeight = FontWeight.Bold, fontSize = 10.sp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodyMedium.copy(color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.5.sp))
            Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(color = TextLowEmphasis, fontSize = 12.sp))
        }
    }
}

@Composable
fun OnboardingStepDisciplines() {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(IndigoNeon.copy(alpha = 0.2f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "STEP 2 OF 3  •  12 COURSEWORK DOMAINS",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CyanNeon,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.5.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "218+ University Lab Troubleshooting Scenarios",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = TextWhite,
                fontSize = 24.sp,
                lineHeight = 31.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Practice guided decision trees built around recurring pitfalls across core computer science modules:",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextMediumEmphasis,
                fontSize = 13.5.sp,
                lineHeight = 20.sp
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        DomainFeatureCard("Database Systems", "CSC204", "SQL JOIN duplicate rows, deadlocks & lock timeouts", Icons.Default.Dataset)
        Spacer(modifier = Modifier.height(8.dp))
        DomainFeatureCard("Computer Networks", "NET301", "Port 8080 refused, loopback bindings & CIDR masks", Icons.Default.NetworkCheck)
        Spacer(modifier = Modifier.height(8.dp))
        DomainFeatureCard("Programming & Debugging", "CSE101", "Async race conditions, null safety & recursion limits", Icons.Default.Code)
        Spacer(modifier = Modifier.height(8.dp))
        DomainFeatureCard("Operating Systems", "OS302", "Circular wait deadlocks, synchronization & thrashing", Icons.Default.Memory)
    }
}

@Composable
fun DomainFeatureCard(title: String, code: String, desc: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(IndigoNeon.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(21.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = title, style = MaterialTheme.typography.titleSmall.copy(color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.5.sp))
                    Text(text = code, style = MaterialTheme.typography.labelSmall.copy(color = CyanNeon, fontWeight = FontWeight.Bold))
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(color = TextMediumEmphasis, fontSize = 12.sp, lineHeight = 16.sp))
            }
        }
    }
}

@Composable
fun OnboardingStepScience() {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(IndigoNeon.copy(alpha = 0.2f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "STEP 3 OF 3  •  RETRIEVAL ANALYTICS",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CyanNeon,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.5.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Track Mistakes, Build Debugging Intuition",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = TextWhite,
                fontSize = 24.sp,
                lineHeight = 31.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Grounded in cognitive retrieval research, LabRescue categorizes every diagnostic misstep to surface blindspots:",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextMediumEmphasis,
                fontSize = 13.5.sp,
                lineHeight = 20.sp
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorderColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCard)
        ) {
            Column(modifier = Modifier.padding(15.dp)) {
                MistakeTagRow("Configuration", "Premature server restarts & firewall toggles", WarningAmber)
                MistakeTagRow("Logical Reasoning", "Symptom suppression instead of key cardinality", CyanNeon)
                MistakeTagRow("Conceptual Models", "Misunderstanding JOIN semantics or ACID locks", IndigoNeon)
                MistakeTagRow("Syntax & Parameters", "Malformed base conditions & loop bounds", SuccessEmerald)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyanNeon.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianCardElevated)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(26.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "All progress, scores, and mistake analytics are saved locally offline using Jetpack Room persistence.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextWhite, lineHeight = 18.sp)
                )
            }
        }
    }
}

@Composable
fun MistakeTagRow(category: String, desc: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = category, style = MaterialTheme.typography.bodyMedium.copy(color = color, fontWeight = FontWeight.Bold, fontSize = 13.5.sp))
            Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(color = TextLowEmphasis, fontSize = 12.sp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060A12)
@Composable
private fun OnboardingScreenPreview() {
    LabRescueTheme(darkTheme = true) {
        OnboardingScreen(onFinishOnboarding = {})
    }
}
