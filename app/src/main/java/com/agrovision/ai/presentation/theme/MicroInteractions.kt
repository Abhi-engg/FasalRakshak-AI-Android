package com.agrovision.ai.presentation.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.delay

/**
 * FasalRakshak AI Unified Micro-Interactions System.
 * Consistent 200–300 ms ease-in-out animations across touchable components:
 * - Fluid press-scale feedback without layout recalculations (RenderNode graphicsLayer)
 * - Accessible ripples & subtle haptics
 * - Smooth expand/collapse accordion transitions
 * - Dynamic color & progress transitions
 */

/**
 * Subtle press scale response for buttons and interactive controls.
 */
fun Modifier.pressScale(
    targetScale: Float = 0.95f,
    durationMs: Int = 150,
    interactionSource: MutableInteractionSource? = null
): Modifier = composed {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) targetScale else 1.0f,
        animationSpec = tween(durationMillis = durationMs, easing = FastOutSlowInEasing),
        label = "press_scale"
    )
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Clickable with tactile bounce feedback and Material 3 ripple.
 */
fun Modifier.bounceClickable(
    scaleDown: Float = 0.96f,
    enabled: Boolean = true,
    hapticFeedback: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) scaleDown else 1.0f,
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
        label = "bounce_click_scale"
    )
    val haptic = LocalHapticFeedback.current

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = ripple(),
            enabled = enabled,
            onClick = {
                if (hapticFeedback) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                onClick()
            }
        )
}

/**
 * Card press micro-interaction with subtle scale and bounded ripple.
 */
fun Modifier.interactiveCard(
    onClick: () -> Unit,
    pressedScale: Float = 0.985f,
    enabled: Boolean = true,
    hapticFeedback: Boolean = true
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) pressedScale else 1.0f,
        animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
        label = "interactive_card_scale"
    )
    val haptic = LocalHapticFeedback.current

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = ripple(bounded = true),
            enabled = enabled,
            onClick = {
                if (hapticFeedback) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                onClick()
            }
        )
}

/**
 * Animated rotation for chevrons, expanders, and sort toggles.
 */
fun Modifier.animatedRotation(
    isRotated: Boolean,
    rotatedAngle: Float = 180f,
    durationMs: Int = 240
): Modifier = composed {
    val angle by animateFloatAsState(
        targetValue = if (isRotated) rotatedAngle else 0f,
        animationSpec = tween(durationMs, easing = FastOutSlowInEasing),
        label = "animated_rotation"
    )
    this.graphicsLayer {
        rotationZ = angle
    }
}

/**
 * Standard 250ms ease-in-out accordion enter transition.
 */
fun agroAccordionEnter(durationMs: Int = 260): EnterTransition =
    expandVertically(
        animationSpec = tween(durationMs, easing = FastOutSlowInEasing),
        expandFrom = Alignment.Top
    ) + fadeIn(
        animationSpec = tween(durationMs, easing = LinearOutSlowInEasing)
    )

/**
 * Standard 220ms ease-in-out accordion exit transition.
 */
fun agroAccordionExit(durationMs: Int = 220): ExitTransition =
    shrinkVertically(
        animationSpec = tween(durationMs, easing = FastOutSlowInEasing),
        shrinkTowards = Alignment.Top
    ) + fadeOut(
        animationSpec = tween(durationMs, easing = FastOutLinearInEasing)
    )

/**
 * Smooth 240ms color animation for states (selected, unselected, active, warning).
 */
@Composable
fun animatedColorTransition(
    targetColor: Color,
    durationMs: Int = 240
): Color {
    val color by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMs, easing = FastOutSlowInEasing),
        label = "color_transition"
    )
    return color
}

/**
 * Smooth progress bar fill animation for confidence and severity meters.
 */
@Composable
fun animateFloatProgress(
    targetValue: Float,
    durationMs: Int = 600,
    delayMs: Int = 80
): Float {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(targetValue) {
        delay(delayMs.toLong())
        started = true
    }
    val progress by animateFloatAsState(
        targetValue = if (started) targetValue else 0f,
        animationSpec = tween(durationMs, easing = FastOutSlowInEasing),
        label = "progress_animation"
    )
    return progress
}
