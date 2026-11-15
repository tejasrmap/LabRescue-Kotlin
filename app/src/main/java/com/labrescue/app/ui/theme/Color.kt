package com.labrescue.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Premium Obsidian & Cyber Dark Backgrounds
val ObsidianBg = Color(0xFF070B14)
val ObsidianSurface = Color(0xFF0D1424)
val ObsidianCard = Color(0xFF131D31)
val ObsidianCardElevated = Color(0xFF1A2742)
val CardBorderColor = Color(0xFF243352)
val CardBorderGlow = Color(0xFF38BDF8).copy(alpha = 0.25f)

// Brand Accents
val CyanNeon = Color(0xFF00F2FE)
val CyanGlow = Color(0xFF4FACFE)
val IndigoNeon = Color(0xFF6366F1)
val PurpleGlow = Color(0xFF8B5CF6)
val BlueCobalt = Color(0xFF2563EB)

// Functional Feedback Colors
val SuccessEmerald = Color(0xFF10B981)
val SuccessEmeraldBg = Color(0xFF064E3B)
val WarningAmber = Color(0xFFF59E0B)
val WarningAmberBg = Color(0xFF78350F)
val ErrorRose = Color(0xFFFF4D6D)
val ErrorRoseBg = Color(0xFF881337)
val InfoSky = Color(0xFF38BDF8)

// Typography & Content
val TextWhite = Color(0xFFFFFFFF)
val TextHighEmphasis = Color(0xFFF1F5F9)
val TextMediumEmphasis = Color(0xFF94A3B8)
val TextLowEmphasis = Color(0xFF64748B)

// Terminal Specific
val TerminalDeepBg = Color(0xFF040711)
val TerminalHeaderBg = Color(0xFF0A0F1D)
val TerminalCyan = Color(0xFF38BDF8)
val TerminalGreen = Color(0xFF34D399)
val TerminalYellow = Color(0xFFFCD34D)

// Gradients
val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(CyanNeon, IndigoNeon)
)

val CardGlowGradient = Brush.linearGradient(
    colors = listOf(CyanNeon.copy(alpha = 0.15f), Color.Transparent)
)

val HeroBannerGradient = Brush.verticalGradient(
    colors = listOf(ObsidianCardElevated, ObsidianCard)
)
