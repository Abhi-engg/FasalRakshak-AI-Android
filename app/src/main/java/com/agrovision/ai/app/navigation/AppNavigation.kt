package com.agrovision.ai.app.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.agrovision.ai.presentation.camera.CameraScreen
import com.agrovision.ai.presentation.diagnosis.DiagnosisResultScreen
import com.agrovision.ai.presentation.history.HistoryScreen
import com.agrovision.ai.presentation.home.HomeScreen
import com.agrovision.ai.presentation.knowledge.KnowledgeScreen
import com.agrovision.ai.presentation.settings.SettingsScreen

/**
 * App Navigation Root.
 * Resolves full-screen immersion by hiding the BottomNavigationBar on focus/camera
 * and diagnosis result screens, while keeping it anchored on primary root tabs.
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Only display bottom navigation dock on primary root tabs.
    // Removed entirely during camera scan / focus mode and diagnosis inspection.
    val isMainTab = currentRoute in listOf(
        Screen.Home.route,
        Screen.History.route,
        Screen.Guide.route,
        Screen.More.route,
        "learn",
        "settings"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (isMainTab) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (isMainTab) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            // 1. Home
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToCamera = { navController.navigate(Screen.Camera.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) },
                    onNavigateToGuide = { navController.navigate(Screen.Guide.route) },
                    onNavigateToMore = { navController.navigate(Screen.More.route) },
                    onNavigateToResult = { scanId ->
                        navController.navigate(Screen.DiagnosisResult.createRoute(scanId))
                    }
                )
            }

            // 2. Scan History
            composable(Screen.History.route) {
                HistoryScreen(
                    onNavigateToResult = { scanId ->
                        navController.navigate(Screen.DiagnosisResult.createRoute(scanId))
                    },
                    onNavigateToCamera = { navController.navigate(Screen.Camera.route) }
                )
            }

            // 3. Scan (Camera Focus Mode) - Bottom bar removed for full immersion
            composable(Screen.Camera.route) {
                CameraScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToResult = { scanId, crop ->
                        navController.navigate(Screen.DiagnosisResult.createRoute(scanId, crop))
                    }
                )
            }

            // 4. Crop Guide (Primary: "guide", Alias: "learn")
            composable(Screen.Guide.route) {
                KnowledgeScreen()
            }
            composable("learn") {
                KnowledgeScreen()
            }

            // 5. More / Settings (Primary: "more", Alias: "settings")
            composable(Screen.More.route) {
                SettingsScreen()
            }
            composable("settings") {
                SettingsScreen()
            }

            // 6. Diagnosis Result - Bottom bar removed for maximum readability
            composable(
                route = Screen.DiagnosisResult.route,
                arguments = listOf(
                    navArgument("scanId") { type = NavType.StringType },
                    navArgument("crop") {
                        type = NavType.StringType
                        defaultValue = ""
                        nullable = true
                    }
                )
            ) { backStackEntry ->
                val rawScanId = backStackEntry.arguments?.getString("scanId") ?: ""
                val scanId = try {
                    java.net.URLDecoder.decode(rawScanId, java.nio.charset.StandardCharsets.UTF_8.toString())
                } catch (e: Exception) {
                    rawScanId
                }
                val rawCrop = backStackEntry.arguments?.getString("crop") ?: ""
                val cropName = try {
                    if (rawCrop.isNotBlank()) {
                        java.net.URLDecoder.decode(rawCrop, java.nio.charset.StandardCharsets.UTF_8.toString())
                    } else null
                } catch (e: Exception) {
                    null
                }

                DiagnosisResultScreen(
                    scanId = scanId,
                    cropName = cropName,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToGuide = { navController.navigate(Screen.Guide.route) },
                    onScanAnother = {
                        navController.navigate(Screen.Camera.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }
        }
    }
}
