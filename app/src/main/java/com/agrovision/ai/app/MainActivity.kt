package com.agrovision.ai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.agrovision.ai.app.navigation.AppNavigation
import com.agrovision.ai.presentation.theme.AgroVisionTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AgroVisionTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}
