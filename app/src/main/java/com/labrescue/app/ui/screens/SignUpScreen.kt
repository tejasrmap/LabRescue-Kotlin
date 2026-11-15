package com.labrescue.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labrescue.app.data.db.SampleData
import com.labrescue.app.data.security.SecurityUtils
import com.labrescue.app.ui.components.AdaptiveContainer
import com.labrescue.app.ui.theme.CardBorderColor
import com.labrescue.app.ui.theme.CyanNeon
import androidx.compose.ui.semantics.Role
import com.labrescue.app.ui.theme.ErrorRose
import com.labrescue.app.ui.theme.ErrorRoseBg
import com.labrescue.app.ui.theme.IndigoNeon
import com.labrescue.app.ui.theme.ObsidianBg
import com.labrescue.app.ui.theme.ObsidianCard
import com.labrescue.app.ui.theme.SuccessEmerald
import com.labrescue.app.ui.theme.TextHighEmphasis
import com.labrescue.app.ui.theme.TextLowEmphasis
import com.labrescue.app.ui.theme.TextMediumEmphasis
import com.labrescue.app.ui.theme.TextWhite
import com.labrescue.app.ui.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch

private val AVAILABLE_PROGRAMMES = listOf(
    "BSc (Hons) Computer Science",
    "BSc (Hons) Software Engineering",
    "BSc (Hons) Computer Networks & Security",
    "BSc (Hons) Artificial Intelligence & Data Science",
    "BSc (Hons) Cyber Security",
    "MSc Advanced Computer Science"
)

private val ACADEMIC_YEARS = listOf("Year 1", "Year 2", "Year 3", "Postgraduate")

private val GOAL_PRESETS = listOf(
    "Master Systems & Database Troubleshooting",
    "Conquer Concurrency & Deadlock Race Conditions",
    "Zero-Mistake Diagnostic Accuracy Target",
    "Network Protocol & Packet Flow Mastery"
)

@Composable
fun SignUpScreen(
    viewModel: DashboardViewModel,
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var programme by remember { mutableStateOf(AVAILABLE_PROGRAMMES[0]) }
    var isProgrammeDropdownOpen by remember { mutableStateOf(false) }
    var academicYear by remember { mutableStateOf(ACADEMIC_YEARS[1]) }
    var targetGoal by remember { mutableStateOf(GOAL_PRESETS[0]) }

    val subjectsList = remember { SampleData.subjects }
    var selectedSubjectIds by remember {
        mutableStateOf(setOf("db", "net", "prog", "os"))
    }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var nameTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var confirmTouched by remember { mutableStateOf(false) }

    val nameError = remember(fullName, nameTouched) {
        if (!nameTouched) null else SecurityUtils.validateFullName(fullName)
    }

    val emailError = remember(email, emailTouched) {
        if (!emailTouched) null
        else if (email.isBlank()) "Email address is required"
        else if (!SecurityUtils.isValidEmail(email)) "Please provide a valid academic email address"
        else null
    }

    val passwordError = remember(password, passwordTouched) {
        if (!passwordTouched) null else SecurityUtils.validatePasswordStrength(password)
    }

    val confirmError = remember(password, confirmPassword, confirmTouched) {
        if (!confirmTouched) null else SecurityUtils.validateConfirmPassword(password, confirmPassword)
    }

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

    fun attemptSignUp() {
        nameTouched = true
        emailTouched = true
        passwordTouched = true
        confirmTouched = true
        errorMessage = null

        val currentNameErr = SecurityUtils.validateFullName(fullName)
        val currentEmailErr = if (!SecurityUtils.isValidEmail(email)) "Please provide a valid academic email address" else null
        val currentPassErr = SecurityUtils.validatePasswordStrength(password)
        val currentConfirmErr = SecurityUtils.validateConfirmPassword(password, confirmPassword)

        if (currentNameErr != null || currentEmailErr != null || currentPassErr != null || currentConfirmErr != null) {
            errorMessage = currentNameErr ?: currentEmailErr ?: currentPassErr ?: currentConfirmErr
            return
        }

        if (selectedSubjectIds.isEmpty()) {
            errorMessage = "Please select at least one coursework subject module"
            return
        }

        isLoading = true
        coroutineScope.launch {
            val result = viewModel.signUp(
                name = fullName.trim(),
                email = email.trim(),
                password = password,
                programme = programme,
                year = academicYear,
                targetGoal = targetGoal.trim(),
                enrolledSubjects = selectedSubjectIds.joinToString(",")
            )
            isLoading = false
            result.onSuccess {
                onSignUpSuccess()
            }.onFailure { ex ->
                errorMessage = ex.message ?: "Registration failed. Please check your details."
            }
        }
    }

    AdaptiveContainer(maxWidth = 580.dp) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBg)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Logo Header
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(IndigoNeon.copy(alpha = 0.35f), Color.Transparent)
                        )
                    )
                    .border(1.5.dp, IndigoNeon.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Sign Up",
                    tint = IndigoNeon,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Join LabRescue",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = TextWhite,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            )

            Text(
                text = "Create your student diagnostic profile",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextLowEmphasis,
                    fontSize = 12.5.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Main Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderColor.copy(alpha = 0.7f), RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated Error Banner
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        errorMessage?.let { msg ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ErrorRoseBg)
                                    .border(1.dp, ErrorRose.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = "Error",
                                    tint = ErrorRose,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = ErrorRose,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    // Field 1: Full Name
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Full Name *",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (nameError != null) ErrorRose else TextMediumEmphasis,
                                fontSize = 11.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = {
                                fullName = it
                                nameTouched = true
                                errorMessage = null
                            },
                            placeholder = { Text("e.g. Teja Ganugula", color = TextLowEmphasis, fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (nameError != null) ErrorRose else CyanNeon,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            isError = nameError != null,
                            supportingText = nameError?.let { err ->
                                { Text(err, color = ErrorRose, style = MaterialTheme.typography.labelSmall) }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            colors = tfColors,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Field 2: Email
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Academic Email *",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (emailError != null) ErrorRose else TextMediumEmphasis,
                                fontSize = 11.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                emailTouched = true
                                errorMessage = null
                            },
                            placeholder = { Text("e.g. s1234567@university.ac.uk", color = TextLowEmphasis, fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = if (emailError != null) ErrorRose else CyanNeon,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            isError = emailError != null,
                            supportingText = emailError?.let { err ->
                                { Text(err, color = ErrorRose, style = MaterialTheme.typography.labelSmall) }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            colors = tfColors,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Field 3: Degree Programme (Dropdown)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Degree Programme *",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMediumEmphasis,
                                fontSize = 11.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF070B14))
                                    .border(1.dp, CardBorderColor.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
                                    .clickable { isProgrammeDropdownOpen = true }
                                    .padding(horizontal = 14.dp, vertical = 13.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = CyanNeon,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = programme,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = TextWhite,
                                            fontSize = 13.sp
                                        )
                                    )
                                }
                                Icon(
                                    imageVector = if (isProgrammeDropdownOpen) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = CyanNeon
                                )
                            }

                            DropdownMenu(
                                expanded = isProgrammeDropdownOpen,
                                onDismissRequest = { isProgrammeDropdownOpen = false },
                                modifier = Modifier.background(Color(0xFF0B1120))
                            ) {
                                AVAILABLE_PROGRAMMES.forEach { prog ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = prog,
                                                color = if (prog == programme) CyanNeon else TextWhite,
                                                fontSize = 13.sp,
                                                fontWeight = if (prog == programme) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            programme = prog
                                            isProgrammeDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Field 4: Academic Year (Segmented Radio)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Academic Year *",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMediumEmphasis,
                                fontSize = 11.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ACADEMIC_YEARS.forEach { yr ->
                                val isSelected = yr == academicYear
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) CyanNeon.copy(alpha = 0.16f) else Color(0xFF070B14))
                                        .border(
                                            1.dp,
                                            if (isSelected) CyanNeon else CardBorderColor.copy(alpha = 0.5f),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { academicYear = yr }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = yr,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) CyanNeon else TextLowEmphasis,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Field 5: Enrolled Coursework Subjects (Selectable Modules Grid)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Enrolled Coursework Subjects *",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (selectedSubjectIds.isEmpty()) ErrorRose else TextMediumEmphasis,
                                    fontSize = 11.5.sp
                                )
                            )
                            Text(
                                text = "${selectedSubjectIds.size} of ${subjectsList.size} selected",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (selectedSubjectIds.isEmpty()) ErrorRose else CyanNeon,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Quick Selector Chips
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (selectedSubjectIds.size == subjectsList.size) CyanNeon.copy(alpha = 0.16f) else Color(0xFF070B14))
                                    .border(1.dp, if (selectedSubjectIds.size == subjectsList.size) CyanNeon else CardBorderColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .clickable { selectedSubjectIds = subjectsList.map { it.id }.toSet() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Select All", color = if (selectedSubjectIds.size == subjectsList.size) CyanNeon else TextMediumEmphasis, fontSize = 10.5.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF070B14))
                                    .border(1.dp, CardBorderColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .clickable { selectedSubjectIds = setOf("db", "net", "prog", "os").filter { id -> subjectsList.any { it.id == id } }.toSet() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Core 4", color = CyanNeon, fontSize = 10.5.sp)
                            }
                            if (selectedSubjectIds.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF070B14))
                                        .border(1.dp, CardBorderColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                        .clickable { selectedSubjectIds = emptySet() }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Clear", color = TextLowEmphasis, fontSize = 10.5.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 2-column grid of subject tiles
                        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                            subjectsList.chunked(2).forEach { rowSubjects ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowSubjects.forEach { subject ->
                                        val isSelected = subject.id in selectedSubjectIds
                                        Row(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    if (isSelected) CyanNeon.copy(alpha = 0.12f)
                                                    else Color(0xFF070B14)
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = if (isSelected) CyanNeon.copy(alpha = 0.75f)
                                                    else CardBorderColor.copy(alpha = 0.5f),
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .clickable {
                                                    selectedSubjectIds = if (isSelected) {
                                                        selectedSubjectIds - subject.id
                                                    } else {
                                                        selectedSubjectIds + subject.id
                                                    }
                                                }
                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(if (isSelected) CyanNeon else ObsidianBg)
                                                    .border(1.dp, if (isSelected) CyanNeon else CardBorderColor, RoundedCornerShape(4.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = ObsidianBg,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = subject.code,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = if (isSelected) CyanNeon else TextLowEmphasis,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.sp
                                                    )
                                                )
                                                Text(
                                                    text = subject.name,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = if (isSelected) TextWhite else TextMediumEmphasis,
                                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                        fontSize = 11.5.sp
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    if (rowSubjects.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Field 6: Target Learning Goal (With Chips)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Diagnostic Troubleshooting Goal *",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMediumEmphasis,
                                fontSize = 11.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = targetGoal,
                            onValueChange = { targetGoal = it },
                            placeholder = { Text("What do you want to master?", color = TextLowEmphasis, fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = null,
                                    tint = CyanNeon,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            singleLine = true,
                            colors = tfColors,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(GOAL_PRESETS) { preset ->
                                FilterChip(
                                    selected = targetGoal == preset,
                                    onClick = { targetGoal = preset },
                                    label = { Text(preset, fontSize = 10.5.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = IndigoNeon.copy(alpha = 0.22f),
                                        selectedLabelColor = IndigoNeon,
                                        containerColor = Color(0xFF070B14),
                                        labelColor = TextLowEmphasis
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = targetGoal == preset,
                                        borderColor = CardBorderColor.copy(alpha = 0.45f),
                                        selectedBorderColor = IndigoNeon
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Field 6: Password
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Password (min 6 chars, letters & numbers) *",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (passwordError != null) ErrorRose else TextMediumEmphasis,
                                fontSize = 11.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                passwordTouched = true
                                errorMessage = null
                            },
                            placeholder = { Text("Create a secure password", color = TextLowEmphasis, fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (passwordError != null) ErrorRose else CyanNeon,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = TextMediumEmphasis,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = passwordError != null,
                            supportingText = passwordError?.let { err ->
                                { Text(err, color = ErrorRose, style = MaterialTheme.typography.labelSmall) }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            colors = tfColors,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Field 7: Confirm Password
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Confirm Password *",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (confirmError != null) ErrorRose else TextMediumEmphasis,
                                fontSize = 11.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                confirmTouched = true
                                errorMessage = null
                            },
                            placeholder = { Text("Re-enter your password", color = TextLowEmphasis, fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (confirmError != null) ErrorRose else CyanNeon,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = TextMediumEmphasis,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = confirmError != null,
                            supportingText = confirmError?.let { err ->
                                { Text(err, color = ErrorRose, style = MaterialTheme.typography.labelSmall) }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    attemptSignUp()
                                }
                            ),
                            colors = tfColors,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Live Password Requirement Indicators
                    val hasMinLength = password.length >= 6
                    val hasLetter = password.any { it.isLetter() }
                    val hasDigit = password.any { it.isDigit() }
                    val isMatch = confirmPassword.isNotEmpty() && password == confirmPassword

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PasswordRequirementPill(
                            label = "6+ chars",
                            met = hasMinLength,
                            modifier = Modifier.weight(1f)
                        )
                        PasswordRequirementPill(
                            label = "Letter",
                            met = hasLetter,
                            modifier = Modifier.weight(1f)
                        )
                        PasswordRequirementPill(
                            label = "Number",
                            met = hasDigit,
                            modifier = Modifier.weight(1f)
                        )
                        PasswordRequirementPill(
                            label = "Match",
                            met = isMatch,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            attemptSignUp()
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanNeon,
                            disabledContainerColor = CyanNeon.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = ObsidianBg,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = "Create Account & Start Labs",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = ObsidianBg,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Navigation to Login
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already registered on LabRescue?",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextLowEmphasis,
                        fontSize = 12.5.sp
                    )
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CyanNeon,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            // Security Hardening Info Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF070C16))
                    .border(1.dp, CardBorderColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Security Info",
                    tint = SuccessEmerald,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Zero plaintext passwords • Salted PBKDF2 hash",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextLowEmphasis,
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PasswordRequirementPill(
    label: String,
    met: Boolean,
    modifier: Modifier = Modifier
) {
    val pillBg = if (met) SuccessEmerald.copy(alpha = 0.16f) else Color(0xFF0C1322)
    val pillBorder = if (met) SuccessEmerald.copy(alpha = 0.5f) else CardBorderColor.copy(alpha = 0.4f)
    val pillText = if (met) SuccessEmerald else TextLowEmphasis

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(pillBg)
            .border(1.dp, pillBorder, RoundedCornerShape(8.dp))
            .padding(vertical = 5.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (met) Icons.Default.Check else Icons.Default.Lock,
                contentDescription = null,
                tint = pillText,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                maxLines = 1,
                softWrap = false,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = pillText,
                    fontWeight = if (met) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 10.sp
                )
            )
        }
    }
}

