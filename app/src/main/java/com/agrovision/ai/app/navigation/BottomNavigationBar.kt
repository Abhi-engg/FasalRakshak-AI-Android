package com.agrovision.ai.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

private val BrandGreen = Color(0xFF097940)
private val DeepForestGreen = Color(0xFF044828)
private val ActiveTextGreen = Color(0xFF065F46)
private val ActivePillMint = Color(0xFFDCF5E8)
private val TextSlateMuted = Color(0xFF64748B)
private val TextScanLabel = Color(0xFF475569)
private val DockBorderSubtle = Color(0xFFE6EDF2)

/**
 * Custom Floating Pill Shape with smooth organic crest around the raised Scan FAB.
 */
class FloatingDockShape(
    private val cornerRadius: Dp = 28.dp,
    private val bumpWidth: Dp = 80.dp,
    private val bumpHeight: Dp = 8.dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val r = with(density) { cornerRadius.toPx() }
        val bw = with(density) { bumpWidth.toPx() }
        val bh = with(density) { bumpHeight.toPx() }
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val topY = bh // base top edge at y = bumpHeight

        val path = Path().apply {
            moveTo(r, topY)

            val bumpLeft = cx - bw / 2f
            val bumpRight = cx + bw / 2f
            lineTo(bumpLeft, topY)

            // Smooth cubic bezier wave up to peak (0f) and back down to topY
            cubicTo(
                x1 = bumpLeft + bw * 0.28f, y1 = topY,
                x2 = cx - bw * 0.20f, y2 = 0f,
                x3 = cx, y3 = 0f
            )
            cubicTo(
                x1 = cx + bw * 0.20f, y1 = 0f,
                x2 = bumpRight - bw * 0.28f, y2 = topY,
                x3 = bumpRight, y3 = topY
            )

            // Continue to top-right
            lineTo(w - r, topY)

            // Top-right corner
            arcTo(
                rect = Rect(w - 2 * r, topY, w, topY + 2 * r),
                startAngleDegrees = -90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Right edge
            lineTo(w, h - r)

            // Bottom-right corner
            arcTo(
                rect = Rect(w - 2 * r, h - 2 * r, w, h),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Bottom edge
            lineTo(r, h)

            // Bottom-left corner
            arcTo(
                rect = Rect(0f, h - 2 * r, 2 * r, h),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Left edge
            lineTo(0f, topY + r)

            // Top-left corner
            arcTo(
                rect = Rect(0f, topY, 2 * r, topY + 2 * r),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * Premium Floating Dock Bottom Navigation Bar.
 * Faithfully matches user visual specification:
 * - Floating rounded dock with soft drop shadow and organic center crest
 * - Raised Emerald Gradient Scan FAB with 2.5dp white ring
 * - Distinct active pill indicator with dark circular icon badge
 * - Perfectly balanced baseline alignment across all destinations
 */
@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide bottom bar on camera screen or result detail screen
    if (currentRoute == Screen.Camera.route || currentRoute?.startsWith("diagnosis") == true) {
        return
    }

    val dockShape = remember { FloatingDockShape() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating Dock Surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .shadow(
                    elevation = 14.dp,
                    shape = dockShape,
                    spotColor = Color(0x33000000),
                    ambientColor = Color(0x18000000)
                )
                .clip(dockShape)
                .background(Color.White)
                .border(1.dp, DockBorderSubtle, dockShape)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .align(Alignment.BottomCenter),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Home
                BottomNavItem(
                    screen = Screen.Home,
                    title = "Home",
                    icon = Icons.Default.Home,
                    isSelected = currentRoute == Screen.Home.route,
                    onClick = { navigateTo(navController, Screen.Home.route, currentRoute) },
                    modifier = Modifier.weight(1f)
                )

                // 2. Guide
                BottomNavItem(
                    screen = Screen.Guide,
                    title = "Guide",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    isSelected = currentRoute == Screen.Guide.route || currentRoute == "learn",
                    onClick = { navigateTo(navController, Screen.Guide.route, currentRoute) },
                    modifier = Modifier.weight(1f)
                )

                // Center gap reserved for raised Scan FAB
                Spacer(modifier = Modifier.width(66.dp))

                // 3. History
                BottomNavItem(
                    screen = Screen.History,
                    title = "History",
                    icon = Icons.Default.Schedule,
                    isSelected = currentRoute == Screen.History.route,
                    onClick = { navigateTo(navController, Screen.History.route, currentRoute) },
                    modifier = Modifier.weight(1f)
                )

                // 4. More
                BottomNavItem(
                    screen = Screen.More,
                    title = "More",
                    icon = Icons.Default.MoreHoriz,
                    isSelected = currentRoute == Screen.More.route || currentRoute == "settings",
                    onClick = { navigateTo(navController, Screen.More.route, currentRoute) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Center Elevated Scan Action (Rich Emerald Gradient + 2.5dp White Ring)
        val scanInteractionSource = remember { MutableInteractionSource() }
        val isScanPressed by scanInteractionSource.collectIsPressedAsState()
        val isScanHovered by scanInteractionSource.collectIsHoveredAsState()
        val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

        val scanScale by androidx.compose.animation.core.animateFloatAsState(
            targetValue = when {
                isScanPressed -> 0.90f
                isScanHovered -> 1.06f
                else -> 1.0f
            },
            animationSpec = androidx.compose.animation.core.spring(
                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
            ),
            label = "scan_press_scale"
        )
        val scanElevation by androidx.compose.animation.core.animateDpAsState(
            targetValue = when {
                isScanPressed -> 3.dp
                isScanHovered -> 11.dp
                else -> 7.dp
            },
            animationSpec = androidx.compose.animation.core.tween(130, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            label = "scan_elevation"
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-14).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .graphicsLayer {
                        scaleX = scanScale
                        scaleY = scanScale
                    }
                    .shadow(elevation = scanElevation, shape = CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(BrandGreen, DeepForestGreen)
                        )
                    )
                    .border(2.5.dp, Color.White, CircleShape)
                    .clickable(
                        interactionSource = scanInteractionSource,
                        indication = androidx.compose.material3.ripple(bounded = true, radius = 28.dp)
                    ) {
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        if (currentRoute != Screen.Camera.route) {
                            navController.navigate(Screen.Camera.route)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Scan Crop",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Scan",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isScanHovered || isScanPressed) Color(0xFF0F172A) else TextScanLabel
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    screen: Screen,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    val itemScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = when {
            isPressed -> 0.91f
            isHovered -> 1.05f
            else -> 1.0f
        },
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
        ),
        label = "nav_item_press_scale"
    )

    val pillWidth by androidx.compose.animation.core.animateDpAsState(
        targetValue = when {
            isSelected -> 58.dp
            isHovered -> 52.dp
            isPressed -> 50.dp
            else -> 40.dp
        },
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "nav_pill_width"
    )

    val pillBgColor by androidx.compose.animation.animateColorAsState(
        targetValue = when {
            isSelected && (isPressed || isHovered) -> Color(0xFFC7F2DC)
            isSelected -> ActivePillMint
            isPressed -> Color(0xFFE2E8F0)
            isHovered -> Color(0xFFF1F5F9)
            else -> Color.Transparent
        },
        animationSpec = androidx.compose.animation.core.tween(150),
        label = "nav_pill_bg"
    )

    val contentColor by androidx.compose.animation.animateColorAsState(
        targetValue = when {
            isSelected -> ActiveTextGreen
            isHovered || isPressed -> Color(0xFF0F172A)
            else -> TextSlateMuted
        },
        animationSpec = androidx.compose.animation.core.tween(160),
        label = "nav_content_color"
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Active / Hover Pill Indicator
        Box(
            modifier = Modifier
                .width(pillWidth)
                .height(32.dp)
                .graphicsLayer {
                    scaleX = itemScale
                    scaleY = itemScale
                }
                .clip(RoundedCornerShape(16.dp))
                .background(pillBgColor),
            contentAlignment = Alignment.Center
        ) {
            if (screen == Screen.History && isSelected) {
                FilledClockIcon(
                    circleColor = ActiveTextGreen,
                    handsColor = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected || isHovered) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
    }
}

private fun navigateTo(navController: NavController, targetRoute: String, currentRoute: String?) {
    if (currentRoute != targetRoute) {
        navController.navigate(targetRoute) {
            popUpTo(Screen.Home.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}

/**
 * Custom Filled Clock Icon with crisp white hands matching the exact UI design mockup.
 */
@Composable
private fun FilledClockIcon(
    modifier: Modifier = Modifier,
    circleColor: Color = ActiveTextGreen,
    handsColor: Color = Color.White
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f
        val centerPoint = Offset(size.width / 2f, size.height / 2f)

        // Solid green circle
        drawCircle(
            color = circleColor,
            radius = radius,
            center = centerPoint
        )

        // Clock hands: 12:00 and 3:00
        val strokeWidth = 2.2.dp.toPx()
        // Hour hand pointing up (12:00)
        drawLine(
            color = handsColor,
            start = centerPoint,
            end = Offset(centerPoint.x, centerPoint.y - radius * 0.52f),
            strokeWidth = strokeWidth,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        // Minute hand pointing right (3:00)
        drawLine(
            color = handsColor,
            start = centerPoint,
            end = Offset(centerPoint.x + radius * 0.48f, centerPoint.y),
            strokeWidth = strokeWidth,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        // Center pivot dot
        drawCircle(
            color = handsColor,
            radius = strokeWidth * 0.7f,
            center = centerPoint
        )
    }
}
