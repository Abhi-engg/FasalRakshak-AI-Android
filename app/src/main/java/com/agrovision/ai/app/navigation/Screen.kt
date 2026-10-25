package com.agrovision.ai.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Single source of truth for Navigation Routes per FasalRakshak AI Spec.
 * Symmetrical 2 + Scan + 2 architecture:
 * [Home] | [History] | ( SCAN ) | [Guide] | [More]
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object History : Screen("history", "History", Icons.Default.History)
    data object Camera : Screen("camera", "Scan", Icons.Default.CameraAlt)
    data object Guide : Screen("guide", "Guide", Icons.AutoMirrored.Filled.MenuBook)
    data object More : Screen("more", "More", Icons.Default.MoreHoriz)

    // Backwards-compatible aliases
    data object Learn : Screen("guide", "Guide", Icons.AutoMirrored.Filled.MenuBook)
    data object Settings : Screen("more", "More", Icons.Default.MoreHoriz)

    data object DiagnosisResult : Screen("diagnosis/{scanId}?crop={crop}", "Result") {
        fun createRoute(scanId: String, crop: String? = null): String {
            val encoded = java.net.URLEncoder.encode(scanId, java.nio.charset.StandardCharsets.UTF_8.toString())
            val cropParam = if (!crop.isNullOrBlank() && crop != "All Crops") {
                val encodedCrop = java.net.URLEncoder.encode(crop, java.nio.charset.StandardCharsets.UTF_8.toString())
                "?crop=$encodedCrop"
            } else ""
            return "diagnosis/$encoded$cropParam"
        }
    }
}
