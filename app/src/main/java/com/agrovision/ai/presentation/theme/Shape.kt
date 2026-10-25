package com.agrovision.ai.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Corner Radii per Design System v2:
// Card: 20dp, Chip: 12dp, Button: 16dp, Thumbnail: 12dp
val AgroVisionShapes = Shapes(
    small = RoundedCornerShape(12.dp),      // Chips & Badges
    medium = RoundedCornerShape(16.dp),     // Buttons & Action Tiles
    large = RoundedCornerShape(20.dp),      // Cards & Containers
    extraLarge = RoundedCornerShape(28.dp)  // Hero Sheets & Dialogs
)
