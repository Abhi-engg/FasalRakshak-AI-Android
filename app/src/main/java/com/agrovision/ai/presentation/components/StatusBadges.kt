package com.agrovision.ai.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agrovision.ai.domain.model.ConfidenceLevel
import com.agrovision.ai.presentation.theme.AlertContainer
import com.agrovision.ai.presentation.theme.AlertStatus
import com.agrovision.ai.presentation.theme.BorderSubtle
import com.agrovision.ai.presentation.theme.CriticalContainer
import com.agrovision.ai.presentation.theme.CriticalStatus
import com.agrovision.ai.presentation.theme.DarkForestGreen
import com.agrovision.ai.presentation.theme.HealthyContainer
import com.agrovision.ai.presentation.theme.HealthyStatus
import com.agrovision.ai.presentation.theme.PrimaryGreen
import com.agrovision.ai.presentation.theme.SurfaceCard
import com.agrovision.ai.presentation.theme.SurfaceContainer
import com.agrovision.ai.presentation.theme.TextPrimary
import com.agrovision.ai.presentation.theme.TextSecondary
import com.agrovision.ai.presentation.theme.WarningContainer
import com.agrovision.ai.presentation.theme.WarningStatus

/**
 * StatusDot: Compact 10dp pulse dot for top bar.
 * Replaces wide offline badges that wrap or steal header width (P0-2, P2-2).
 * Clicking opens a detailed system status dialog.
 */
@Composable
fun StatusDot(
    isReady: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    val dotColor = if (isReady) HealthyStatus else AlertStatus

    Box(
        modifier = modifier
            .size(36.dp)
            .clickable { showDialog = true },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = DarkForestGreen,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = if (isReady) "AI Engine Offline Ready" else "AI Engine Attention",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "• Model: EfficientNet-B3 Dual-Head",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "• Taxonomy: 83 Foliar Diseases + 113 Insect Pests",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "• Execution: 100% on-device edge inference",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "• Connectivity: Works completely without internet",
                        fontSize = 13.sp,
                        color = HealthyStatus,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK", color = DarkForestGreen, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/**
 * SeverityChip: Multi-signal indicator (Icon + Word + 4-segment visual bar).
 * Complies with Section 4.1 & 4.5 of Spec. Never wraps text mid-word.
 */
@Composable
fun SeverityChip(
    severity: String,
    modifier: Modifier = Modifier
) {
    val clean = severity.trim().lowercase()
    val (label, fgColor, bgColor, activeBars) = when {
        clean.contains("low") || clean.contains("optimal") || clean.contains("normal") ->
            Quad("Low", HealthyStatus, HealthyContainer, 1)
        clean.contains("mod") || clean.contains("med") ->
            Quad("Moderate", WarningStatus, WarningContainer, 2)
        clean.contains("crit") ->
            Quad("Critical", CriticalStatus, CriticalContainer, 4)
        else ->
            Quad("High", AlertStatus, AlertContainer, 3)
    }

    Row(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(1.dp, fgColor.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (activeBars <= 1) Icons.Default.Check else Icons.Default.Warning,
            contentDescription = null,
            tint = fgColor,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = fgColor
        )
        Spacer(modifier = Modifier.width(6.dp))

        // 4-segment mini severity bar
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..4) {
                Box(
                    modifier = Modifier
                        .size(width = 4.dp, height = 8.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(if (i <= activeBars) fgColor else fgColor.copy(alpha = 0.2f))
                )
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

/**
 * Backwards compatibility components
 */
@Composable
fun OfflineBadge(modifier: Modifier = Modifier) {
    StatusDot(isReady = true, modifier = modifier)
}

@Composable
fun ConfidenceBadge(
    confidenceLevel: ConfidenceLevel,
    confidencePercentage: Float,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (confidenceLevel) {
        ConfidenceLevel.HIGH -> Triple(HealthyContainer, HealthyStatus, "High")
        ConfidenceLevel.MEDIUM -> Triple(WarningContainer, WarningStatus, "Moderate")
        ConfidenceLevel.LOW -> Triple(AlertContainer, AlertStatus, "Uncertain")
    }

    Row(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(1.dp, textColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label (${(confidencePercentage * 100).toInt()}%)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun SeverityBadge(
    severity: String,
    modifier: Modifier = Modifier
) {
    SeverityChip(severity = severity, modifier = modifier)
}
