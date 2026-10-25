package com.agrovision.ai.presentation.home

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agrovision.ai.R
import com.agrovision.ai.domain.model.Diagnosis
import com.agrovision.ai.presentation.theme.AlertStatus
import com.agrovision.ai.presentation.theme.BackgroundWarm
import com.agrovision.ai.presentation.theme.BorderSubtle
import com.agrovision.ai.presentation.theme.DarkForestGreen
import com.agrovision.ai.presentation.theme.HealthyStatus
import com.agrovision.ai.presentation.theme.PrimaryContainer
import com.agrovision.ai.presentation.theme.PrimaryGreen
import com.agrovision.ai.presentation.theme.SurfaceCard
import com.agrovision.ai.presentation.theme.SurfaceVariantWarm
import com.agrovision.ai.presentation.theme.TextPrimary
import com.agrovision.ai.presentation.theme.TextSecondary
import com.agrovision.ai.presentation.theme.bounceClickable
import com.agrovision.ai.presentation.theme.interactiveCard
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToGuide: () -> Unit,
    onNavigateToMore: () -> Unit,
    onNavigateToResult: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentLang = viewModel.languageManager.currentLanguage
    var selectedCrop by remember { mutableStateOf("Any crop") }
    var tipDismissed by remember { mutableStateOf(false) }

    val cropsList = listOf(
        CropItemData("Any crop", "कोई भी फसल", null, true),
        CropItemData("Rice", "धान", R.drawable.crop_rice, false),
        CropItemData("Wheat", "गेहूँ", R.drawable.crop_wheat, false),
        CropItemData("Maize", "मक्का", R.drawable.crop_maize, false),
        CropItemData("Tomato", "टमाटर", R.drawable.crop_tomato, false),
        CropItemData("Cotton", "कपास", R.drawable.crop_cotton, false)
    )

    Scaffold(
        containerColor = Color(0xFFF8FBF8)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // ==========================================
            // 1. TOP HEADER & SUNLIGHT-READY HERO BANNER (COVERS ~HALF SCREEN)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                // Background Farmer Photo Banner (Subtle ambient landscape)
                Image(
                    painter = painterResource(id = R.drawable.hero_farmer_banner),
                    contentDescription = "Farmer in field background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.CenterEnd
                )

                // Top light scrim so status bar icons & FasalRakshak AI title are crisp
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.95f),
                                    Color.White.copy(alpha = 0.70f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // High-contrast dark left scrim for outdoor sunlight legibility (stops before the farmer on right)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.75f),
                                    Color.Black.copy(alpha = 0.45f),
                                    Color.Transparent
                                ),
                                endX = 600f
                            )
                        )
                )

                // Bottom subtle scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.60f)
                                ),
                                startY = 400f
                            )
                        )
                )

                // Top Bar: Official SVG Vector Logo, Name & Language + Live Model status
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Official Vector Logo & Brand Name from download.svg
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_fasal_leaf_vector),
                                contentDescription = "FasalRakshak AI Logo",
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "FasalRakshak AI",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF065F46) // Official SVG Brand Green
                                )
                                Text(
                                    text = "फसल सुरक्षा एवं रोग निदान",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF254D3D) // Official SVG Tagline Green
                                )
                            }
                        }

                        // Right: Language Selector & Verified Offline Model Badge
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.95f))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .clickable {
                                        viewModel.cycleLanguage()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                val pillLabel = when (currentLang) {
                                    com.agrovision.ai.core.common.AppLanguage.HINDI -> "हिं | EN"
                                    com.agrovision.ai.core.common.AppLanguage.MARATHI -> "मरा | EN"
                                    com.agrovision.ai.core.common.AppLanguage.ENGLISH -> "EN | हिं"
                                }
                                Text(
                                    text = pillLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF065F46)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Dynamic Offline Model Status (Verified against Classifier)
                            val modelReady = uiState.isModelReady
                            val statusDotColor = if (modelReady) Color(0xFF22C55E) else Color(0xFFF59E0B)
                            val statusTitle = if (modelReady) "Offline Ready" else "Loading AI..."
                            val statusSub = if (modelReady) "ऑफ़लाइन तैयार" else "मॉडल लोड हो रहा है"

                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .background(statusDotColor, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = statusTitle,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E3A2F)
                                    )
                                }
                                Text(
                                    text = statusSub,
                                    fontSize = 9.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Grand, Sunlight-Visible Headline on the left
                    Text(
                        text = "अपनी फसल\nको रखें स्वस्थ",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        lineHeight = 34.sp,
                        modifier = Modifier
                            .fillMaxWidth(0.68f)
                            .padding(start = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "AI रोग और कीट पहचान • 100% बिना इंटरनेट",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.95f),
                        lineHeight = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                            .padding(start = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Compact Trust Strip (Works Offline · Fast · High Accuracy)
                    Row(
                        modifier = Modifier.padding(start = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CompactTrustPill(icon = Icons.Default.WifiOff, label = "Offline AI")
                        CompactTrustPill(icon = Icons.Default.Bolt, label = "Instant Results")
                        CompactTrustPill(icon = Icons.Default.CheckCircle, label = "Field Tested")
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                }
            }

            // ==========================================
            // 2. HERO "SCAN NOW • अभी स्कैन करें" BUTTON (CTA)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-24).dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF0A7F3F), Color(0xFF055F2E))
                        )
                    )
                    .bounceClickable(scaleDown = 0.96f) { onNavigateToCamera() }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Leaf decorative background watermark on left
                Image(
                    painter = painterResource(id = R.drawable.leaf_bg),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .offset(x = (-6).dp, y = (-2).dp)
                        .alpha(0.30f),
                    contentScale = ContentScale.Fit
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // White round camera icon
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera",
                                tint = Color(0xFF0A7F3F),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Scan Now • अभी स्कैन करें",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Photo a leaf or insect • पत्ते या कीट की फोटो लें",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.90f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Green circular forward arrow
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.22f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Go",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // ==========================================
            // 3. WHICH CROP? • कौन सी फसल? (HORIZONTAL SCROLL)
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-10).dp)
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Which crop? • कौन सी फसल?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateToGuide() }
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "View All →",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46)
                            )
                            Text(
                                text = "सभी देखें",
                                fontSize = 10.sp,
                                color = Color(0xFF065F46)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    cropsList.forEach { crop ->
                        val isSelected = selectedCrop == crop.name
                        CropCategoryCard(
                            crop = crop,
                            isSelected = isSelected,
                            onClick = { selectedCrop = crop.name }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 4. THIS WEEK • इस सप्ताह की स्थिति (METRIC CARDS)
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "This Week • इस सप्ताह की स्थिति",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateToHistory() }
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "View All →",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46)
                            )
                            Text(
                                text = "सभी देखें",
                                fontSize = 10.sp,
                                color = Color(0xFF065F46)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 1: Needs Attention (Red Alert #EF4444)
                    WeeklyStatCard(
                        count = uiState.needsAttentionCount,
                        title = "Needs Attention",
                        sub = "ध्यान आवश्यक",
                        icon = Icons.Default.WarningAmber,
                        iconTint = Color(0xFFEF4444),
                        bgColor = Color(0xFFFEECEC),
                        borderColor = Color(0xFFFCA5A5),
                        textColor = Color(0xFFEF4444),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToHistory
                    )

                    // Card 2: Healthy (Primary Green #0A7F3F / Light Green #E8F7EE)
                    WeeklyStatCard(
                        count = uiState.healthyCount,
                        title = "Healthy",
                        sub = "स्वस्थ",
                        icon = Icons.Default.LocalFlorist,
                        iconTint = Color(0xFF0A7F3F),
                        bgColor = Color(0xFFE8F7EE),
                        borderColor = Color(0xFFBBF7D0),
                        textColor = Color(0xFF0A7F3F),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToHistory
                    )

                    // Card 3: Total Scans (Blue #3B82F6)
                    WeeklyStatCard(
                        count = uiState.totalScansCount,
                        title = "Total Scans",
                        sub = "कुल स्कैन",
                        icon = Icons.Default.Task,
                        iconTint = Color(0xFF3B82F6),
                        bgColor = Color(0xFFEFF6FF),
                        borderColor = Color(0xFFBFDBFE),
                        textColor = Color(0xFF3B82F6),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToHistory
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // 5. RECENT SCANS • हाल के स्कैन
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Scans • हाल के स्कैन",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateToHistory() }
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "View All (${uiState.recentDiagnoses.size}) →",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46)
                            )
                            Text(
                                text = "सभी देखें",
                                fontSize = 10.sp,
                                color = Color(0xFF065F46)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.recentDiagnoses.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = DarkForestGreen,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No Scans Yet • कोई स्कैन नहीं",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Tap 'Scan Now' to check your first crop leaf.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        uiState.recentDiagnoses.take(4).forEach { scan ->
                            MockupScanRowCard(
                                scan = scan,
                                onClick = {
                                    val targetId = if (scan.imagePath.isNotBlank()) scan.imagePath else "sample_scan_rice_blast"
                                    onNavigateToResult(targetId)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 6. TIP BANNER (NATURAL DAYLIGHT)
            // ==========================================
            if (!tipDismissed) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(Color(0xFFFEFCE8), RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFFFEF08A), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "💡", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Tip: Scan in natural daylight for best results.",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF713F12)
                                )
                                Text(
                                    text = "सुझाव: बेहतर परिणाम के लिए प्राकृतिक रोशनी में स्कैन करें।",
                                    fontSize = 11.sp,
                                    color = Color(0xFF854D0E)
                                )
                            }
                        }

                        IconButton(
                            onClick = { tipDismissed = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color(0xFF854D0E),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Comfortable bottom spacing above bottom navigation bar
            Spacer(modifier = Modifier.height(110.dp))
        }
    }
}

// ==========================================
// SUB-COMPONENTS
// ==========================================

data class CropItemData(
    val name: String,
    val hindiName: String,
    val drawableRes: Int?,
    val isAny: Boolean
)

@Composable
fun HeroFeatureBadge(
    icon: ImageVector,
    title: String,
    sub: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color.White.copy(alpha = 0.90f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF15803D),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            lineHeight = 13.sp
        )
        Text(
            text = sub,
            fontSize = 9.sp,
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}

@Composable
fun CropCategoryCard(
    crop: CropItemData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val cardBg = if (isSelected) Color(0xFF064E3B) else Color.White
    val titleColor = if (isSelected) Color.White else Color(0xFF0F172A)
    val subColor = if (isSelected) Color.White.copy(alpha = 0.85f) else Color(0xFF64748B)

    Box(
        modifier = Modifier
            .width(82.dp)
            .height(106.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFF064E3B) else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(16.dp)
            )
            .bounceClickable(scaleDown = 0.94f) { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (crop.isAny) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFF047857), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFlorist,
                        contentDescription = "All",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else if (crop.drawableRes != null) {
                Image(
                    painter = painterResource(id = crop.drawableRes),
                    contentDescription = crop.name,
                    modifier = Modifier.size(44.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = crop.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                maxLines = 1
            )
            Text(
                text = crop.hindiName,
                fontSize = 10.sp,
                color = subColor,
                maxLines = 1
            )
        }
    }
}

@Composable
fun WeeklyStatCard(
    count: Int,
    title: String,
    sub: String,
    icon: ImageVector,
    iconTint: Color,
    bgColor: Color,
    borderColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .interactiveCard(onClick = onClick, pressedScale = 0.97f)
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "$count",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = iconTint.copy(alpha = 0.6f),
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = 1
            )
            Text(
                text = sub,
                fontSize = 10.sp,
                color = textColor.copy(alpha = 0.85f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun CompactTrustPill(
    icon: ImageVector,
    label: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.42f))
            .border(0.5.dp, Color.White.copy(alpha = 0.30f), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF86EFAC),
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun MockupScanRowCard(
    scan: Diagnosis,
    onClick: () -> Unit
) {
    val formattedTime = remember(scan.timestamp) {
        val sdf = SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault())
        sdf.format(Date(scan.timestamp))
    }

    val displayCrop = remember(scan.cropName, scan.diseasePrediction.className) {
        val dName = scan.diseasePrediction.className.lowercase()
        when {
            dName.contains("corn") || dName.contains("maize") -> "Corn"
            dName.contains("cotton") -> "Cotton"
            dName.contains("tomato") -> "Tomato"
            dName.contains("potato") -> "Potato"
            dName.contains("citrus") || dName.contains("lemon") || dName.contains("orange") -> "Citrus"
            dName.contains("chili") || dName.contains("chilli") || dName.contains("pepper") -> "Chili"
            dName.contains("wheat") -> "Wheat"
            dName.contains("banana") -> "Banana"
            dName.contains("apple") -> "Apple"
            dName.contains("grape") -> "Grape"
            dName.contains("soybean") -> "Soybean"
            dName.contains("sugarcane") -> "Sugarcane"
            dName.contains("rice") || dName.contains("paddy") -> "Rice"
            dName.contains("sorghum") -> "Sorghum"
            dName.contains("cassava") -> "Cassava"
            dName.contains("cauliflower") || dName.contains("cabbage") || dName.contains("radish") -> "Brassica"
            dName.contains("papaya") -> "Papaya"
            !scan.cropName.isNullOrBlank() && scan.cropName != "Agricultural Crop" -> scan.cropName
            else -> "Agricultural Crop"
        }
    }

    val bitmap = remember(scan.imagePath) {
        if (scan.imagePath.isNotBlank()) {
            val file = File(scan.imagePath)
            if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
        } else null
    }

    val isHealthy = scan.diseasePrediction.isHealthy
    val confPercent = (scan.diseasePrediction.confidence * 100).toInt()
    val isLowConf = scan.diseasePrediction.confidence < 0.50f
    val hasPest = !scan.pestPrediction.isHealthy && 
                  scan.pestPrediction.className.isNotBlank() &&
                  !scan.pestPrediction.className.contains("No Pest", ignoreCase = true)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            .interactiveCard(onClick = onClick, pressedScale = 0.985f)
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leaf Photo Thumbnail (56dp compact)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F5F9))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Specimen Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.disease_sample1),
                        contentDescription = "Specimen Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Finding & Date Metadata
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scan.diseasePrediction.className,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$displayCrop • $formattedTime",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Badges Row: Severity + AI Confidence
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Severity or Uncertain/Rescan Badge
                    if (isLowConf) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFEF3C7))
                                .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "⚠ Rescan / पुनः स्कैन",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isHealthy) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                                .border(
                                    1.dp,
                                    if (isHealthy) Color(0xFF86EFAC) else Color(0xFFFCA5A5),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isHealthy) "✓ Healthy" else "⚠ High Alert",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isHealthy) Color(0xFF15803D) else Color(0xFFDC2626)
                            )
                        }
                    }

                    // Model Confidence Percentage
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$confPercent% Conf.",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF475569)
                        )
                    }
                }

                // Dedicated Pest Detection Badge (Clean horizontal badge with full column width, never squished!)
                if (hasPest) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFEF3C7))
                            .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_insect_pest),
                                contentDescription = "Pest",
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Pest: ${scan.pestPrediction.className}",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF92400E),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Chevron Arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Details",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
