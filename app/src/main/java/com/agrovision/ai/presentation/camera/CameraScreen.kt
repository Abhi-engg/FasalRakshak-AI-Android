package com.agrovision.ai.presentation.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agrovision.ai.camera.QualityCheckResult
import com.agrovision.ai.presentation.components.PrimaryButton
import com.agrovision.ai.presentation.components.ScanButton
import com.agrovision.ai.presentation.components.SecondaryButton
import com.agrovision.ai.presentation.theme.DarkForestGreen
import com.agrovision.ai.presentation.theme.PrimaryGreen
import com.agrovision.ai.presentation.theme.SurfaceCard
import com.agrovision.ai.presentation.theme.TextPrimary
import com.agrovision.ai.presentation.theme.TextSecondary
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import com.agrovision.ai.presentation.theme.WarningAmber

@Composable
fun CameraScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResult: (String, String?) -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var cropDropdownExpanded by remember { mutableStateOf(false) }
    val crops = listOf("All Crops", "Tomato", "Apple", "Potato", "Corn", "Rice", "Banana", "Grape", "Soybean", "Bell Pepper")

    // Interactive Tap-to-Focus states
    var focusPoint by remember { mutableStateOf<Offset?>(null) }
    val haptic = LocalHapticFeedback.current
    var activePreviewView by remember { mutableStateOf<PreviewView?>(null) }

    LaunchedEffect(focusPoint) {
        if (focusPoint != null) {
            kotlinx.coroutines.delay(1100)
            focusPoint = null
        }
    }

    // Camera Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.updateCameraPermission(isGranted)
    }

    // Gallery Import Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.handleGalleryUri(it) }
    }

    LaunchedEffect(Unit) {
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        viewModel.updateCameraPermission(isGranted)
        if (!isGranted) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Handle Navigation Events with Crop Prior
    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is CameraNavigationEvent.NavigateToDiagnosis -> {
                    onNavigateToResult(event.imagePath, event.cropName)
                }
            }
        }
    }

    // Quality Alert Dialog (Blur / Lighting)
    if (uiState.qualityWarning != null) {
        val warning = uiState.qualityWarning
        val warningMessage = when (warning) {
            is QualityCheckResult.Blurry -> warning.message
            is QualityCheckResult.TooDark -> warning.message
            is QualityCheckResult.TooBright -> warning.message
            else -> "Quality check failed."
        }

        AlertDialog(
            onDismissRequest = { viewModel.dismissQualityWarning() },
            icon = {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = WarningAmber,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Image Quality Notice",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = warningMessage,
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissQualityWarning() }) {
                    Text(text = "Retake Photo", fontWeight = FontWeight.Bold, color = PrimaryGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.proceedDespiteWarning() }) {
                    Text(text = "Analyze Anyway", color = TextSecondary)
                }
            },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (!uiState.hasCameraPermission) {
        // Permission Denied Rational Screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7FAF8))
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(PrimaryGreen.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = DarkForestGreen,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Camera Access Needed",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "To diagnose crop foliar diseases and identify pests on-device, AgroVision AI requires permission to use your camera.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                PrimaryButton(
                    text = "Grant Camera Access",
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SecondaryButton(
                    text = "Go Back",
                    onClick = onNavigateBack
                )
            }
        }
    } else {
        // Active CameraX Viewfinder
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // CameraX PreviewView with Tap-to-Focus
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        setOnTouchListener { v, event ->
                            if (event.action == android.view.MotionEvent.ACTION_UP) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                focusPoint = Offset(event.x, event.y)
                                viewModel.cameraManager.focus(event.x, event.y, this)
                                v.performClick()
                            }
                            true
                        }
                    }
                    viewModel.cameraManager.bindCamera(
                        lifecycleOwner = lifecycleOwner,
                        previewView = previewView
                    )
                    activePreviewView = previewView
                    previewView
                },
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            focusPoint = offset
                            activePreviewView?.let { pv ->
                                viewModel.cameraManager.focus(offset.x, offset.y, pv)
                            }
                        }
                    }
            )

            // Dynamic Touch Focus Ring (Flagship Spring Reticle)
            focusPoint?.let { pt ->
                CameraFocusReticle(point = pt)
            }

            // Center: Leaf Framing Guide Overlay
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(310.dp)
                    .border(2.dp, PrimaryGreen.copy(alpha = 0.85f), RoundedCornerShape(36.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = PrimaryGreen.copy(alpha = 0.45f),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Place one affected leaf inside the frame",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Top Bar Controls: Back, Crop Filter, Flash
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                // Crop Prior Selector (Pathological Prior)
                Box {
                    Row(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                            .clickable { cropDropdownExpanded = true }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Crop: ${uiState.selectedCrop}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    DropdownMenu(
                        expanded = cropDropdownExpanded,
                        onDismissRequest = { cropDropdownExpanded = false }
                    ) {
                        crops.forEach { crop ->
                            DropdownMenuItem(
                                text = { Text(text = crop) },
                                onClick = {
                                    viewModel.selectCrop(crop)
                                    cropDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { viewModel.toggleFlash() },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (uiState.isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = if (uiState.isFlashOn) Color(0xFFFACC15) else Color.White
                    )
                }
            }

            // Guidance Banner
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp)
                    .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "💡 Natural light ensures highest diagnostic accuracy",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Bottom Shutter Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery Import
                IconButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    enabled = !uiState.isCapturing,
                    modifier = Modifier
                        .size(52.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Pick from Gallery",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Shutter / Scan Button with Capture Progress
                if (uiState.isCapturing) {
                    Box(
                        modifier = Modifier.size(76.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryGreen,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(70.dp)
                        )
                    }
                } else {
                    ScanButton(
                        onClick = { viewModel.capturePhoto() }
                    )
                }

                Spacer(modifier = Modifier.size(52.dp))
            }
        }
    }
}

/**
 * Flagship Tap-to-Focus Reticle:
 * Springs from 1.35x down to 1.0x with bouncy physical spring, highlights center reticle dot,
 * and smoothly fades out with zero UI blocking.
 */
@Composable
private fun CameraFocusReticle(
    point: Offset,
    modifier: Modifier = Modifier
) {
    val scale = remember(point) { Animatable(1.35f) }
    val alpha = remember(point) { Animatable(1.0f) }

    LaunchedEffect(point) {
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        kotlinx.coroutines.delay(450)
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 350)
        )
    }

    val density = LocalDensity.current
    val reticleSize = 64.dp
    val xDp = with(density) { point.x.toDp() } - (reticleSize / 2)
    val yDp = with(density) { point.y.toDp() } - (reticleSize / 2)

    Box(
        modifier = modifier
            .offset(x = xDp, y = yDp)
            .size(reticleSize)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                this.alpha = alpha.value
            },
        contentAlignment = Alignment.Center
    ) {
        // Outer glowing rounded target
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, PrimaryGreen, RoundedCornerShape(14.dp))
        )
        // Center precision micro-dot
        Box(
            modifier = Modifier
                .size(5.dp)
                .background(PrimaryGreen, CircleShape)
        )
    }
}
