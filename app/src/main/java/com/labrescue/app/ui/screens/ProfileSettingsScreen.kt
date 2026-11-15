package com.labrescue.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labrescue.app.data.model.UserProfile
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
import com.labrescue.app.ui.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch

private val DEGREE_PROGRAMMES = listOf(
    "BSc (Hons) Computer Science",
    "BSc (Hons) Software Engineering",
    "BSc (Hons) Cyber Security & Networks",
    "BSc (Hons) Data Science & AI",
    "BSc (Hons) Information Technology"
)

private val ACADEMIC_YEARS = listOf(
    "Year 1",
    "Year 2",
    "Year 3",
    "Year 4"
)

fun validateStudentName(input: String): String? {
    val trimmed = input.trim()
    return when {
        trimmed.isEmpty() -> "Full name is required."
        trimmed.length < 2 -> "Full name must be at least 2 characters."
        trimmed.length > 50 -> "Full name must be 50 characters or fewer."
        trimmed.none { it.isLetter() } -> "Full name must include alphabetic characters."
        else -> null
    }
}

fun validateLearningObjective(input: String): String? {
    val trimmed = input.trim()
    return when {
        trimmed.isEmpty() -> "Diagnostic learning objective cannot be empty."
        trimmed.length < 10 -> "Please enter a meaningful objective (at least 10 characters)."
        trimmed.length > 140 -> "Learning objective must be 140 characters or fewer."
        else -> null
    }
}

@Composable
fun ProfileSettingsScreen(
    viewModel: DashboardViewModel,
    onReplayTour: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val userProfile by viewModel.userProfile.collectAsState()

    ProfileSettingsContent(
        userProfile = userProfile,
        onSaveProfile = { name, programme, year, targetGoal ->
            viewModel.updateProfile(name, programme, year, targetGoal)
        },
        onReplayTour = onReplayTour,
        onRestoreDemoData = { viewModel.injectDemoData() },
        onResetData = { viewModel.resetData() },
        onLogout = onLogout
    )
}

@Composable
fun ProfileSettingsContent(
    userProfile: UserProfile,
    onSaveProfile: (name: String, programme: String, year: String, targetGoal: String) -> Unit,
    onReplayTour: () -> Unit = {},
    onRestoreDemoData: () -> Unit = {},
    onResetData: () -> Unit = {},
    onLogout: () -> Unit = {},
    initiallyEditing: Boolean = false
) {
    var isEditingProfile by remember { mutableStateOf(initiallyEditing) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    var name by remember(userProfile.name) { mutableStateOf(userProfile.name) }
    var programme by remember(userProfile.programme) { mutableStateOf(userProfile.programme) }
    var year by remember(userProfile.year) { mutableStateOf(userProfile.year) }
    var targetGoal by remember(userProfile.targetGoal) { mutableStateOf(userProfile.targetGoal) }

    var programmeMenuExpanded by remember { mutableStateOf(false) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }
    var nameTouched by remember { mutableStateOf(false) }
    var goalTouched by remember { mutableStateOf(false) }

    val nameError = if (hasAttemptedSubmit || nameTouched) validateStudentName(name) else null
    val goalError = if (hasAttemptedSubmit || goalTouched) validateLearningObjective(targetGoal) else null

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            containerColor = ObsidianCardElevated,
            titleContentColor = TextWhite,
            textContentColor = TextMediumEmphasis,
            title = {
                Text(
                    text = "Reset Diagnostic History?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "This will permanently delete all recorded rescue attempts and mistake analytics from your local Room database. Your profile identity will be kept.",
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 19.sp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirmDialog = false
                        onResetData()
                        scope.launch {
                            snackbarHostState.showSnackbar("Diagnostic history and mistake records cleared.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRose),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Reset Data", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel", color = TextMediumEmphasis)
                }
            }
        )
    }

    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            containerColor = ObsidianCardElevated,
            titleContentColor = TextWhite,
            textContentColor = TextMediumEmphasis,
            title = {
                Text(
                    text = "Sign Out of LabRescue?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "You will be returned to the student login screen. Your laboratory attempts and diagnostic scores remain safely preserved in local storage.",
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 19.sp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Sign Out", color = ObsidianBg, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Cancel", color = TextMediumEmphasis)
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(ObsidianBg)) {
        AdaptiveContainer(maxWidth = 920.dp) { isWideScreen ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = if (isWideScreen) 32.dp else 14.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 84.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Banner
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Learner Profile & Settings",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                fontSize = if (isWideScreen) 24.sp else 20.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Manage your student credentials, learning objective, and Room storage.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextMediumEmphasis,
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Compact Profile Identity Card + Collapsible Credential Editor
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorderColor.copy(alpha = 0.65f), RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCardElevated),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            // Top Identity Row (Horizontal, Compact & Structured)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(CyanNeon, IndigoNeon))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = ObsidianBg,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = userProfile.name,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                color = TextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.5.sp
                                            ),
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(5.dp))
                                                .background(SuccessEmerald.copy(alpha = 0.16f))
                                                .padding(horizontal = 7.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "ACTIVE",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = SuccessEmerald,
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${userProfile.programme} • ${userProfile.year}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextMediumEmphasis,
                                            fontSize = 12.5.sp
                                        )
                                    )
                                    if (userProfile.email.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = userProfile.email,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = CyanNeon.copy(alpha = 0.9f),
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Current Learning Objective Summary Box
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF070C17))
                                    .border(1.dp, CardBorderColor.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = null,
                                    tint = CyanNeon,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "LEARNING OBJECTIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextLowEmphasis,
                                            fontSize = 9.5.sp,
                                            letterSpacing = 0.8.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Text(
                                        text = userProfile.targetGoal,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextHighEmphasis,
                                            fontSize = 12.5.sp,
                                            lineHeight = 17.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Toggle Edit Credentials Button
                            OutlinedButton(
                                onClick = {
                                    if (isEditingProfile) {
                                        // Reset unsaved edits on cancel
                                        name = userProfile.name
                                        programme = userProfile.programme
                                        year = userProfile.year
                                        targetGoal = userProfile.targetGoal
                                        hasAttemptedSubmit = false
                                        nameTouched = false
                                        goalTouched = false
                                    }
                                    isEditingProfile = !isEditingProfile
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (isEditingProfile) TextMediumEmphasis else CyanNeon
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isEditingProfile) CardBorderColor else CyanNeon.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                            ) {
                                Icon(
                                    imageVector = if (isEditingProfile) Icons.Default.Close else Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isEditingProfile) "Close Credential Editor" else "Edit Student Credentials",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }

                            // Collapsible Form Drawer (Reveals TextField, DropdownMenu, RadioButton & Validation)
                            AnimatedVisibility(
                                visible = isEditingProfile,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(modifier = Modifier.padding(top = 16.dp)) {
                                    HorizontalDivider(color = CardBorderColor.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(14.dp))

                                    Text(
                                        text = "EDIT STUDENT CREDENTIALS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CyanNeon,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    val tfColors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFF070B14),
                                        unfocusedContainerColor = Color(0xFF070B14),
                                        errorContainerColor = Color(0xFF160910),
                                        focusedBorderColor = CyanNeon,
                                        unfocusedBorderColor = CardBorderColor.copy(alpha = 0.55f),
                                        errorBorderColor = ErrorRose,
                                        focusedTextColor = TextWhite,
                                        unfocusedTextColor = TextWhite,
                                        errorTextColor = TextWhite,
                                        cursorColor = CyanNeon,
                                        errorCursorColor = ErrorRose
                                    )

                                    // Field 1: Full Name (TextField + Validation)
                                    Text(
                                        text = "Full Name *",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (nameError != null) ErrorRose else TextMediumEmphasis,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = name,
                                        onValueChange = {
                                            name = it
                                            nameTouched = true
                                        },
                                        isError = nameError != null,
                                        placeholder = {
                                            Text("Enter your full name", color = TextLowEmphasis, fontSize = 13.sp)
                                        },
                                        trailingIcon = {
                                            if (nameError != null) {
                                                Icon(
                                                    imageVector = Icons.Default.ErrorOutline,
                                                    contentDescription = "Validation error",
                                                    tint = ErrorRose
                                                )
                                            }
                                        },
                                        supportingText = nameError?.let { err ->
                                            {
                                                Text(
                                                    text = err,
                                                    color = ErrorRose,
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                                        colors = tfColors,
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Field 2: Degree Programme (DropdownMenu)
                                    Text(
                                        text = "Degree Programme",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMediumEmphasis, fontSize = 11.5.sp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFF070B14))
                                                .border(
                                                    width = 1.dp,
                                                    color = if (programmeMenuExpanded) CyanNeon else CardBorderColor.copy(alpha = 0.55f),
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .clickable { programmeMenuExpanded = !programmeMenuExpanded }
                                                .padding(horizontal = 14.dp, vertical = 13.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = programme,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = TextWhite,
                                                    fontSize = 13.5.sp
                                                ),
                                                modifier = Modifier.weight(1f)
                                            )
                                            Icon(
                                                imageVector = if (programmeMenuExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                                contentDescription = "Select Degree Programme",
                                                tint = CyanNeon
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = programmeMenuExpanded,
                                            onDismissRequest = { programmeMenuExpanded = false },
                                            modifier = Modifier
                                                .background(ObsidianCard)
                                                .border(1.dp, CardBorderColor, RoundedCornerShape(8.dp))
                                        ) {
                                            DEGREE_PROGRAMMES.forEach { option ->
                                                val isSelected = option == programme
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            text = option,
                                                            color = if (isSelected) CyanNeon else TextWhite,
                                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                            fontSize = 13.sp
                                                        )
                                                    },
                                                    trailingIcon = {
                                                        if (isSelected) {
                                                            Icon(
                                                                imageVector = Icons.Default.Check,
                                                                contentDescription = null,
                                                                tint = CyanNeon,
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                    },
                                                    onClick = {
                                                        programme = option
                                                        programmeMenuExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Field 3: Academic Year (Framed Compact RadioButton Tiles — no wide empty gap!)
                                    Text(
                                        text = "Academic Year",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMediumEmphasis, fontSize = 11.5.sp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    val yearOptions = if (year in ACADEMIC_YEARS) ACADEMIC_YEARS else ACADEMIC_YEARS + year
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        yearOptions.chunked(2).forEach { rowYears ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                rowYears.forEach { yearOption ->
                                                    val selected = year == yearOption
                                                    Row(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clip(RoundedCornerShape(10.dp))
                                                            .background(
                                                                if (selected) CyanNeon.copy(alpha = 0.12f)
                                                                else Color(0xFF070B14)
                                                            )
                                                            .border(
                                                                width = 1.dp,
                                                                color = if (selected) CyanNeon.copy(alpha = 0.7f)
                                                                else CardBorderColor.copy(alpha = 0.5f),
                                                                shape = RoundedCornerShape(10.dp)
                                                            )
                                                            .selectable(
                                                                selected = selected,
                                                                onClick = { year = yearOption },
                                                                role = Role.RadioButton
                                                            )
                                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        RadioButton(
                                                            selected = selected,
                                                            onClick = null,
                                                            modifier = Modifier.size(18.dp),
                                                            colors = RadioButtonDefaults.colors(
                                                                selectedColor = CyanNeon,
                                                                unselectedColor = TextLowEmphasis
                                                            )
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            text = yearOption,
                                                            maxLines = 1,
                                                            softWrap = false,
                                                            style = MaterialTheme.typography.bodySmall.copy(
                                                                color = if (selected) TextWhite else TextMediumEmphasis,
                                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                                                fontSize = 13.sp
                                                            )
                                                        )
                                                    }
                                                }
                                                if (rowYears.size == 1) {
                                                    Spacer(modifier = Modifier.weight(1f))
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Field 4: Objective (TextField + Validation)
                                    Text(
                                        text = "Diagnostic Learning Objective *",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (goalError != null) ErrorRose else TextMediumEmphasis,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = targetGoal,
                                        onValueChange = {
                                            targetGoal = it
                                            goalTouched = true
                                        },
                                        isError = goalError != null,
                                        placeholder = {
                                            Text(
                                                "e.g. Master SQL JOIN optimization & thread deadlock analysis",
                                                color = TextLowEmphasis,
                                                fontSize = 13.sp
                                            )
                                        },
                                        supportingText = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = goalError ?: "Minimum 10 characters",
                                                    color = if (goalError != null) ErrorRose else TextLowEmphasis,
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                                Text(
                                                    text = "${targetGoal.trim().length}/140",
                                                    color = if (targetGoal.trim().length > 140) ErrorRose else TextLowEmphasis,
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                                        colors = tfColors,
                                        minLines = 2,
                                        maxLines = 3
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Save Button (Validates, persists to Room, and closes drawer)
                                    Button(
                                        onClick = {
                                            hasAttemptedSubmit = true
                                            val currentNameErr = validateStudentName(name)
                                            val currentGoalErr = validateLearningObjective(targetGoal)
                                            if (currentNameErr == null && currentGoalErr == null) {
                                                onSaveProfile(name.trim(), programme, year, targetGoal.trim())
                                                isEditingProfile = false
                                                hasAttemptedSubmit = false
                                                nameTouched = false
                                                goalTouched = false
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Profile saved to Room database!")
                                                }
                                            } else {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        currentNameErr ?: currentGoalErr ?: "Please resolve form errors."
                                                    )
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(42.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = ObsidianBg,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "Save Profile Changes",
                                            color = ObsidianBg,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Grouped Preferences & Room Database Controls Card (Replaces loose open buttons)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorderColor.copy(alpha = 0.6f), RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(CyanNeon.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Storage,
                                        contentDescription = null,
                                        tint = CyanNeon,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "App & Database Controls",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.5.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            CompactActionRow(
                                icon = Icons.Default.Info,
                                title = "Introduction Walkthrough",
                                subtitle = "Replay the 3-step diagnostic tour",
                                actionLabel = "Replay",
                                accentColor = CyanNeon,
                                onClick = onReplayTour
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = CardBorderColor.copy(alpha = 0.4f)
                            )

                            CompactActionRow(
                                icon = Icons.Default.Restore,
                                title = "Sample Lab Telemetry",
                                subtitle = "Restore 9 demo attempts & 12 mistakes",
                                actionLabel = "Restore",
                                accentColor = IndigoNeon,
                                onClick = {
                                    onRestoreDemoData()
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Sample diagnostic history restored.")
                                    }
                                }
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = CardBorderColor.copy(alpha = 0.4f)
                            )

                            CompactActionRow(
                                icon = Icons.Default.DeleteSweep,
                                title = "Clear Local History",
                                subtitle = "Wipe recorded attempts & mistake logs",
                                actionLabel = "Reset",
                                accentColor = ErrorRose,
                                onClick = { showResetConfirmDialog = true }
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = CardBorderColor.copy(alpha = 0.4f)
                            )

                            CompactActionRow(
                                icon = Icons.AutoMirrored.Filled.Logout,
                                title = "Sign Out Account",
                                subtitle = "Sign out of active student session",
                                actionLabel = "Sign Out",
                                accentColor = CyanNeon,
                                onClick = { showLogoutConfirmDialog = true }
                            )
                        }
                    }
                }

                // Compact Structured About Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorderColor.copy(alpha = 0.6f), RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(IndigoNeon.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = CyanNeon,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "About LabRescue",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.5.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            InfoRow("Module", "IBM4203M Mobile Applications")
                            InfoRow("Author", "Teja Ganugula")
                            InfoRow("Stack", "Kotlin • Jetpack Compose • MVVM")
                            InfoRow("Persistence", "Jetpack Room (Entities, DAOs & Flow)")
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

@Composable
private fun CompactActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionLabel: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextLowEmphasis,
                        fontSize = 11.5.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(accentColor.copy(alpha = 0.14f))
                .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                )
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextLowEmphasis,
                fontSize = 12.sp
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextHighEmphasis,
                fontWeight = FontWeight.Medium,
                fontSize = 12.5.sp
            ),
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060A12)
@Composable
private fun ProfileSettingsCompactPreview() {
    LabRescueTheme(darkTheme = true) {
        ProfileSettingsContent(
            userProfile = UserProfile(),
            onSaveProfile = { _, _, _, _ -> },
            initiallyEditing = false
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060A12)
@Composable
private fun ProfileSettingsExpandedEditorPreview() {
    LabRescueTheme(darkTheme = true) {
        ProfileSettingsContent(
            userProfile = UserProfile(),
            onSaveProfile = { _, _, _, _ -> },
            initiallyEditing = true
        )
    }
}
