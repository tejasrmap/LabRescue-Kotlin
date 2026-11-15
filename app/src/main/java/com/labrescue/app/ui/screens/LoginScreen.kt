package com.labrescue.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labrescue.app.data.db.DatabaseSeedData
import com.labrescue.app.data.security.SecurityUtils
import com.labrescue.app.ui.components.AdaptiveContainer
import com.labrescue.app.ui.theme.CardBorderColor
import com.labrescue.app.ui.theme.CyanNeon
import com.labrescue.app.ui.theme.ErrorRose
import com.labrescue.app.ui.theme.ErrorRoseBg
import com.labrescue.app.ui.theme.IndigoNeon
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

@Composable
fun LoginScreen(
    viewModel: DashboardViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }

    val emailError = remember(email, emailTouched) {
        if (!emailTouched) null
        else if (email.isBlank()) "Email address is required"
        else if (!SecurityUtils.isValidEmail(email)) "Please enter a valid email address"
        else null
    }

    val passwordError = remember(password, passwordTouched) {
        if (!passwordTouched) null
        else if (password.isEmpty()) "Password is required"
        else null
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

    fun attemptLogin() {
        emailTouched = true
        passwordTouched = true
        errorMessage = null

        if (email.isBlank() || !SecurityUtils.isValidEmail(email) || password.isEmpty()) {
            errorMessage = "Please enter a valid email and password"
            return
        }

        isLoading = true
        coroutineScope.launch {
            val result = viewModel.login(email.trim(), password)
            isLoading = false
            result.onSuccess {
                onLoginSuccess()
            }.onFailure { ex ->
                errorMessage = ex.message ?: "Authentication failed. Please verify your credentials."
            }
        }
    }

    fun loginAsDemo() {
        isLoading = true
        errorMessage = null
        coroutineScope.launch {
            val result = viewModel.loginAsDemoStudent()
            isLoading = false
            result.onSuccess {
                onLoginSuccess()
            }.onFailure { ex ->
                errorMessage = ex.message ?: "Failed to log in as demo student"
            }
        }
    }

    AdaptiveContainer(maxWidth = 520.dp) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBg)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Brand Header & Logo
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(CyanNeon.copy(alpha = 0.28f), Color.Transparent)
                        )
                    )
                    .border(1.5.dp, CyanNeon.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = "LabRescue Logo",
                    tint = CyanNeon,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "LabRescue",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = TextWhite,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            )

            Text(
                text = "Practical Diagnostic Troubleshooting Workspace",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextLowEmphasis,
                    fontSize = 12.5.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Main Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderColor.copy(alpha = 0.7f), RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "STUDENT SIGN IN",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = CyanNeon,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            fontSize = 11.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Access your coursework lab sessions",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextMediumEmphasis,
                            fontSize = 13.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

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

                    // Email Field
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
                            placeholder = {
                                Text("student@university.ac.uk", color = TextLowEmphasis, fontSize = 13.sp)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = if (emailError != null) ErrorRose else CyanNeon,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            isError = emailError != null,
                            supportingText = emailError?.let { err ->
                                {
                                    Text(text = err, color = ErrorRose, style = MaterialTheme.typography.labelSmall)
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            colors = tfColors,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Password *",
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
                            placeholder = {
                                Text("Enter your account password", color = TextLowEmphasis, fontSize = 13.sp)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Password",
                                    tint = if (passwordError != null) ErrorRose else CyanNeon,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                        tint = TextMediumEmphasis,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = passwordError != null,
                            supportingText = passwordError?.let { err ->
                                {
                                    Text(text = err, color = ErrorRose, style = MaterialTheme.typography.labelSmall)
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    attemptLogin()
                                }
                            ),
                            colors = tfColors,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Remember Me Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = CyanNeon,
                                uncheckedColor = CardBorderColor,
                                checkmarkColor = ObsidianBg
                            )
                        )
                        Text(
                            text = "Keep me signed in on this device",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMediumEmphasis,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary Sign In Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            attemptLogin()
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
                                text = "Sign In",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = ObsidianBg,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = CardBorderColor.copy(alpha = 0.5f)
                        )
                        Text(
                            text = " OR QUICK DEMO ",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextLowEmphasis,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = CardBorderColor.copy(alpha = 0.5f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Demo Sign In Button (For Assessment / Marker)
                    OutlinedButton(
                        onClick = {
                            email = DatabaseSeedData.DEMO_EMAIL
                            password = DatabaseSeedData.DEMO_PASSWORD
                            loginAsDemo()
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = IndigoNeon
                        ),
                        border = BorderStroke(1.dp, IndigoNeon.copy(alpha = 0.55f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = IndigoNeon,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Sign In as Demo Student (1-Tap)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = IndigoNeon,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.5.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(IndigoNeon.copy(alpha = 0.08f))
                            .border(1.dp, IndigoNeon.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Seed: ${DatabaseSeedData.DEMO_EMAIL} • ${DatabaseSeedData.DEMO_PASSWORD}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextLowEmphasis,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Navigation to Sign Up
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have a LabRescue account?",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextLowEmphasis,
                        fontSize = 12.5.sp
                    )
                )
                TextButton(onClick = onNavigateToSignUp) {
                    Text(
                        text = "Sign Up",
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
                    text = "PBKDF2-HMAC-SHA256 Salted Encryption • Isolated Local Store",
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

