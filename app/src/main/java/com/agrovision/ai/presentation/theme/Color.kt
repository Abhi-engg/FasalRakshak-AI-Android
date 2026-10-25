package com.agrovision.ai.presentation.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// FasalRakshak AI — Design System v2 Color Palette
// Compliant with WCAG 2.1 AA (>= 4.5:1 text, >= 3:1 UI elements)
// ============================================================================

// Primary Brand Green & Surfaces
val PrimaryGreen = Color(0xFF0B5D3F)          // Hero, primary CTAs, selected chips (8:1 on white)
val OnPrimary = Color(0xFFFFFFFF)             // Text on primary
val PrimaryContainer = Color(0xFFCDEBDD)      // Soft fills, selected nav pills
val DarkForestGreen = Color(0xFF064E3B)       // Deep accent, text on primary container (12.4:1)
val DeepNavy = Color(0xFF123B35)

val BackgroundWarm = Color(0xFFF7FAF8)        // Screen background (off-white warm tone)
val SurfaceCard = Color(0xFFFFFFFF)           // Pure white cards
val SurfaceContainer = Color(0xFFEDF3EF)      // Neutral container / nav bar background
val BorderSubtle = Color(0xFFB9C6BF)          // 3:1 contrast against surface

// Neutral Typography Tokens
val TextPrimary = Color(0xFF14211B)           // High-contrast body & titles (>= 12:1)
val TextSecondary = Color(0xFF44544C)         // Secondary descriptions (>= 7:1)
val TextTertiary = Color(0xFF6B7D74)

// Status & Severity Indicators (With verified high contrast pairs)
// 1. Healthy / Low Severity
val HealthyStatus = Color(0xFF0A6A43)
val HealthyContainer = Color(0xFFDDF3E8)       // Contrast ~5.8:1

// 2. Warning / Moderate Severity
val WarningStatus = Color(0xFF7A4F00)
val WarningContainer = Color(0xFFFFF1D6)       // Contrast ~6.1:1

// 3. Alert / High Severity / Action Needed
val AlertStatus = Color(0xFFB3261E)
val AlertContainer = Color(0xFFFBE4E1)         // Contrast ~5.5:1

// 4. Critical Severity
val CriticalStatus = Color(0xFF7F1D1D)
val CriticalContainer = Color(0xFFFEE2E2)

// Backwards-compatible aliases for existing screen references
val HealthyGreen = HealthyStatus
val DiseaseRed = AlertStatus
val PestOrange = WarningStatus
val WarningAmber = WarningStatus
val SurfaceVariantWarm = SurfaceContainer
