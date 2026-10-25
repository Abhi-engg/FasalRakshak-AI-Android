package com.agrovision.ai.presentation.history

import android.content.Intent
import android.graphics.BitmapFactory
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.agrovision.ai.presentation.theme.bounceClickable
import com.agrovision.ai.presentation.theme.interactiveCard
import com.agrovision.ai.presentation.theme.animatedRotation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agrovision.ai.R
import com.agrovision.ai.domain.model.Diagnosis
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Clean, Minimalist Agricultural Palette
private val PrimaryGreenBrand = Color(0xFF087A3B)
private val DarkGreenBrand = Color(0xFF065F46)
private val PaleGreenBrand = Color(0xFFE8F7EE)
private val ScreenBg = Color(0xFFF8FAF8)
private val CardBorderSubtle = Color(0xFFE2E8F0)
private val DividerHairline = Color(0xFFF1F5F9)
private val TextDark = Color(0xFF17212B)
private val TextMuted = Color(0xFF64748B)

// Severity Badges
private val CriticalRed = Color(0xFFDC2626)
private val CriticalRedBg = Color(0xFFFEE2E2)
private val WarningAmber = Color(0xFFD97706)
private val WarningAmberBg = Color(0xFFFEF3C7)
private val HealthyGreen = Color(0xFF059669)
private val HealthyGreenBg = Color(0xFFE8F7EE)
private val TotalBlue = Color(0xFF2563EB)
private val PestTeal = Color(0xFF0D9488)

/**
 * Clean & Minimalist Scan History Screen for FasalRakshak AI.
 * Delivers an uncluttered, high-contrast, farmer-friendly interface with:
 * - Streamlined bilingual header with status-bar inset awareness
 * - Unified single-card statistical dashboard (Zero visual clutter)
 * - Clean pill filter chips with intuitive active states
 * - Spacious specimen diagnostic cards optimized for outdoor sunlight reading
 * - Reliable Room database persistence with undoable deletion
 */
@Composable
fun HistoryScreen(
    onNavigateToResult: (String) -> Unit,
    onNavigateToCamera: () -> Unit = {},
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var pendingDeletedScan by remember { mutableStateOf<Diagnosis?>(null) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ScreenBg,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .background(ScreenBg)
        ) {
            // ==============================================================================
            // 1. STREAMLINED TOP BAR (Clean, Compact, Minimalist)
            // ==============================================================================
            ScanHistoryTopBar(
                recordCount = uiState.totalCount,
                onClearAll = {
                    if (uiState.totalCount > 0) {
                        showClearConfirmDialog = true
                    }
                },
                onMenuClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Offline Storage • ${uiState.totalCount} diagnostic records saved on device",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            )

            // ==============================================================================
            // 2. MAIN CONTENT (Scrollable list with clean cards)
            // ==============================================================================
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search and Filter Row
                item(key = "search_and_filter") {
                    ScanSearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = { viewModel.onSearchQueryChanged(it) },
                        onFilterClick = { showFilterSheet = true }
                    )
                }

                // Filter Chips (Clean, low-noise pills)
                item(key = "filter_chips") {
                    ScanFilterChips(
                        selectedStatus = uiState.selectedStatus,
                        onStatusSelected = { viewModel.onStatusSelected(it) }
                    )
                }

                // Unified Minimalist Summary Grid (Single card with 4 sleek segments)
                item(key = "summary_grid") {
                    ScanSummaryGrid(
                        totalCount = uiState.totalCount,
                        needsAttentionCount = uiState.actionNeededCount,
                        healthyCount = uiState.healthyCount,
                        pestCount = uiState.pestCount
                    )
                }

                // Section Header with Sort Toggle
                item(key = "recent_scans_header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp, bottom = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Scans • हाल के स्कैन",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDark
                        )

                        // Sort toggle button
                        Box {
                            var sortMenuExpanded by remember { mutableStateOf(false) }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .border(1.dp, CardBorderSubtle, RoundedCornerShape(8.dp))
                                    .clickable { sortMenuExpanded = true }
                                    .padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapVert,
                                    contentDescription = "Sort",
                                    tint = DarkGreenBrand,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (uiState.sortOrder == SortOrder.NEWEST_FIRST) "Newest First" else "Oldest First",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkGreenBrand
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = DarkGreenBrand,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = sortMenuExpanded,
                                onDismissRequest = { sortMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Newest First • सबसे नया पहले", fontSize = 13.sp) },
                                    onClick = {
                                        viewModel.onSortOrderChanged(SortOrder.NEWEST_FIRST)
                                        sortMenuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Oldest First • सबसे पुराना पहले", fontSize = 13.sp) },
                                    onClick = {
                                        viewModel.onSortOrderChanged(SortOrder.OLDEST_FIRST)
                                        sortMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Scan History Cards or Clean Empty State
                if (uiState.scans.isEmpty()) {
                    item(key = "empty_state") {
                        ScanHistoryEmptyState(
                            isSearchActive = uiState.searchQuery.isNotBlank() || uiState.selectedStatus != StatusFilter.ALL,
                            onScanNow = onNavigateToCamera,
                            modifier = Modifier.padding(vertical = 28.dp)
                        )
                    }
                } else {
                    items(uiState.scans, key = { it.id }) { scan ->
                        ScanHistoryCard(
                            scan = scan,
                            onClick = {
                                val destinationId = scan.imagePath.ifBlank { scan.id }
                                onNavigateToResult(destinationId)
                            },
                            onShare = {
                                val cropHi = getCropHindi(scan.cropName ?: "Rice")
                                val (titleEn, titleHi) = getDiagnosisDisplayNames(scan)
                                val shareText = "🌾 FasalRakshak AI — Scan Record\n" +
                                        "🌱 Crop: ${scan.cropName ?: "Rice"} ($cropHi)\n" +
                                        "🍃 Diagnosis: $titleEn ($titleHi)\n" +
                                        "📅 Date: ${formatDateString(scan.timestamp)}\n" +
                                        "🛡️ 100% On-Device Offline AI Specimen"
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(intent, "Share Scan Record"))
                            },
                            onDelete = {
                                pendingDeletedScan = scan
                                viewModel.deleteScan(scan.id)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Scan deleted • स्कैन हटा दिया गया",
                                        actionLabel = "Undo • वापस लाएं",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        pendingDeletedScan?.let { restored ->
                                            viewModel.restoreScan(restored)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Confirmation dialog before clearing all scans
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = {
                Text(
                    text = "Clear History? • सभी हटाएं?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete all offline scan records?\nक्या आप वाकई सभी ऑफ़लाइन स्कैन रिकॉर्ड हटाना चाहते हैं?",
                    fontSize = 13.5.sp,
                    color = Color(0xFF475569)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllScans()
                        showClearConfirmDialog = false
                    }
                ) {
                    Text("Clear All • सभी हटाएं", color = CriticalRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel • रद्द करें", color = TextDark)
                }
            }
        )
    }

    // Filter Dialog Sheet
    if (showFilterSheet) {
        AlertDialog(
            onDismissRequest = { showFilterSheet = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FilterList, contentDescription = null, tint = PrimaryGreenBrand)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Filter Scans • फ़िल्टर चुनें", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select Crop • फसल चुनें:", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        uiState.availableCrops.forEach { crop ->
                            val isSelected = uiState.selectedCrop == crop
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) DarkGreenBrand else Color(0xFFF1F5F9))
                                    .clickable { viewModel.onCropSelected(crop) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (crop == "All") "All • सभी" else "$crop • ${getCropHindi(crop)}",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Status Filter • स्थिति:", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    StatusFilter.values().forEach { filter ->
                        val isSelected = uiState.selectedStatus == filter
                        val label = when (filter) {
                            StatusFilter.ALL -> "All Scans • सभी स्कैन"
                            StatusFilter.ACTION_NEEDED -> "Needs Attention • ध्यान आवश्यक"
                            StatusFilter.HEALTHY -> "Healthy Foliage • स्वस्थ फसल"
                            StatusFilter.PEST -> "Pest Detected • कीट पहचान"
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PaleGreenBrand else Color.Transparent)
                                .clickable {
                                    viewModel.onStatusSelected(filter)
                                    showFilterSheet = false
                                }
                                .padding(horizontal = 8.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) DarkGreenBrand else TextDark
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterSheet = false }) {
                    Text("Done • संपन्न", fontWeight = FontWeight.Bold, color = DarkGreenBrand)
                }
            }
        )
    }
}

// ==============================================================================
// 1. SCAN HISTORY TOP BAR (Clean & Compact)
// ==============================================================================
@Composable
fun ScanHistoryTopBar(
    recordCount: Int,
    onClearAll: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF065F46),
                        Color(0xFF087A3B)
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // History Icon Badge
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.20f))
                    .border(1.dp, Color.White.copy(alpha = 0.30f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title & Subtitle
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Scan History • स्कैन इतिहास",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "$recordCount Scans Saved • $recordCount रिकॉर्ड सुरक्षित",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.90f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Trash action button (Subtle translucent white circle with tactile bounce)
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f))
                    .bounceClickable(scaleDown = 0.90f, onClick = onClearAll),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Clear History",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // 3-dots menu
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f))
                    .bounceClickable(scaleDown = 0.90f, onClick = onMenuClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ==============================================================================
// 2. SCAN SEARCH BAR (Sleek Minimalist Search + Filter)
// ==============================================================================
@Composable
fun ScanSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search field
        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, CardBorderSubtle, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = PrimaryGreenBrand,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Search crop, disease… • खोजें…",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    androidx.compose.foundation.text.BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 12.5.sp,
                            color = TextDark,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = query.isNotEmpty(),
                    enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.scaleIn(),
                    exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.scaleOut()
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = TextMuted,
                        modifier = Modifier
                            .size(18.dp)
                            .bounceClickable(scaleDown = 0.88f) { onQueryChange("") }
                    )
                }
            }
        }

        // Filter button
        Box(
            modifier = Modifier
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, CardBorderSubtle, RoundedCornerShape(12.dp))
                .bounceClickable(scaleDown = 0.94f, onClick = onFilterClick)
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = PrimaryGreenBrand,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Filter · फ़िल्टर",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }
        }
    }
}

// ==============================================================================
// 3. SCAN FILTER CHIPS (Minimalist Clean Pills)
// ==============================================================================
@Composable
fun ScanFilterChips(
    selectedStatus: StatusFilter,
    onStatusSelected: (StatusFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Chip 1: All · सभी
        FilterChipItem(
            title = "All • सभी",
            icon = Icons.Default.Eco,
            iconTint = if (selectedStatus == StatusFilter.ALL) Color.White else PrimaryGreenBrand,
            isSelected = selectedStatus == StatusFilter.ALL,
            onClick = { onStatusSelected(StatusFilter.ALL) }
        )

        // Chip 2: Needs Attention · ध्यान आवश्यक
        FilterChipItem(
            title = "Needs Attention • ध्यान आवश्यक",
            icon = Icons.Default.Warning,
            iconTint = if (selectedStatus == StatusFilter.ACTION_NEEDED) Color.White else CriticalRed,
            isSelected = selectedStatus == StatusFilter.ACTION_NEEDED,
            onClick = { onStatusSelected(StatusFilter.ACTION_NEEDED) }
        )

        // Chip 3: Healthy · स्वस्थ
        FilterChipItem(
            title = "Healthy • स्वस्थ",
            icon = Icons.Default.Eco,
            iconTint = if (selectedStatus == StatusFilter.HEALTHY) Color.White else HealthyGreen,
            isSelected = selectedStatus == StatusFilter.HEALTHY,
            onClick = { onStatusSelected(StatusFilter.HEALTHY) }
        )

        // Chip 4: Pest · कीट
        FilterChipItem(
            title = "Pest • कीट",
            icon = Icons.Default.BugReport,
            iconTint = if (selectedStatus == StatusFilter.PEST) Color.White else WarningAmber,
            isSelected = selectedStatus == StatusFilter.PEST,
            onClick = { onStatusSelected(StatusFilter.PEST) }
        )
    }
}

@Composable
private fun FilterChipItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedBg by androidx.compose.animation.animateColorAsState(
        targetValue = if (isSelected) DarkGreenBrand else Color.White,
        animationSpec = androidx.compose.animation.core.tween(200, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "chip_bg"
    )
    val animatedBorder by androidx.compose.animation.animateColorAsState(
        targetValue = if (isSelected) DarkGreenBrand else CardBorderSubtle,
        animationSpec = androidx.compose.animation.core.tween(200, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "chip_border"
    )
    val animatedTextColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isSelected) Color.White else TextDark,
        animationSpec = androidx.compose.animation.core.tween(200, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "chip_text"
    )

    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(animatedBg)
            .border(1.dp, animatedBorder, RoundedCornerShape(10.dp))
            .bounceClickable(scaleDown = 0.94f, onClick = onClick)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = animatedTextColor
            )
        }
    }
}

// ==============================================================================
// 4. SCAN SUMMARY GRID (Single Unified Minimalist Card)
// ==============================================================================
@Composable
fun ScanSummaryGrid(
    totalCount: Int,
    needsAttentionCount: Int,
    healthyCount: Int,
    pestCount: Int,
    modifier: Modifier = Modifier
) {
    // Single unified white card with 4 clean segments separated by hairline dividers
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, CardBorderSubtle, RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Total
            SummarySegment(
                count = totalCount,
                labelEn = "Total",
                labelHi = "कुल",
                valueColor = TotalBlue,
                modifier = Modifier.weight(1f)
            )

            Box(modifier = Modifier.width(1.dp).height(32.dp).background(DividerHairline))

            // 2. Needs Attention
            SummarySegment(
                count = needsAttentionCount,
                labelEn = "Attention",
                labelHi = "ध्यान दें",
                valueColor = CriticalRed,
                modifier = Modifier.weight(1f)
            )

            Box(modifier = Modifier.width(1.dp).height(32.dp).background(DividerHairline))

            // 3. Healthy
            SummarySegment(
                count = healthyCount,
                labelEn = "Healthy",
                labelHi = "स्वस्थ",
                valueColor = HealthyGreen,
                modifier = Modifier.weight(1f)
            )

            Box(modifier = Modifier.width(1.dp).height(32.dp).background(DividerHairline))

            // 4. Pest Detected
            SummarySegment(
                count = pestCount,
                labelEn = "Pests",
                labelHi = "कीट",
                valueColor = WarningAmber,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummarySegment(
    count: Int,
    labelEn: String,
    labelHi: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$count",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = valueColor
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = "$labelEn • $labelHi",
            fontSize = 9.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

// ==============================================================================
// 5. SCAN HISTORY CARD (Spacious & Clean Diagnostic Specimen Row)
// ==============================================================================
@Composable
fun ScanHistoryCard(
    scan: Diagnosis,
    onClick: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cropName = scan.cropName ?: "Rice"
    val cropHindi = getCropHindi(cropName)
    val cropEmoji = getCropEmoji(cropName)
    val (titleEn, titleHi) = getDiagnosisDisplayNames(scan)
    val (severityLabel, severityHindi, severityColor, severityBg, severityIcon) = getSeverityDetails(scan)

    // Load photo bitmap from file or fallback to authentic pathological specimen
    val thumbnailBitmap = remember(scan.imagePath) {
        val file = File(scan.imagePath)
        if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            val resId = getSpecimenFallbackDrawable(scan.imagePath, cropName, titleEn)
            BitmapFactory.decodeResource(context.resources, resId)
        }
    }

    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, CardBorderSubtle, RoundedCornerShape(14.dp))
            .interactiveCard(onClick = onClick, pressedScale = 0.985f)
            .padding(11.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clean Photo Thumbnail
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE2E8F0))
            ) {
                if (thumbnailBitmap != null) {
                    Image(
                        bitmap = thumbnailBitmap.asImageBitmap(),
                        contentDescription = titleEn,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Middle Column: Crop Pill + Titles + Timestamp
            Column(modifier = Modifier.weight(1f)) {
                // Header row inside card: Crop pill on left, Status badge on right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Crop pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PaleGreenBrand)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$cropEmoji $cropName • $cropHindi",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreenBrand
                        )
                    }

                    // Status Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(severityBg)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = severityIcon,
                                contentDescription = null,
                                tint = severityColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$severityLabel • $severityHindi",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = severityColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Diagnostic Title in English & Hindi (Spacious & Bold)
                Text(
                    text = "$titleEn • $titleHi",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Date & Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatDateString(scan.timestamp),
                            fontSize = 10.5.sp,
                            color = TextMuted
                        )
                    }

                    // 3-dots menu for quick actions
                    Box {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { menuExpanded = true }
                        )

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Share, contentDescription = null, tint = DarkGreenBrand, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Share • साझा करें", fontSize = 13.sp)
                                    }
                                },
                                onClick = {
                                    menuExpanded = false
                                    onShare()
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = CriticalRed, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Delete • हटाएं", fontSize = 13.sp, color = CriticalRed)
                                    }
                                },
                                onClick = {
                                    menuExpanded = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==============================================================================
// 6. SCAN HISTORY EMPTY STATE
// ==============================================================================
@Composable
fun ScanHistoryEmptyState(
    isSearchActive: Boolean,
    onScanNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(PaleGreenBrand),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSearchActive) Icons.Default.SearchOff else Icons.Default.History,
                contentDescription = null,
                tint = PrimaryGreenBrand,
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isSearchActive) "No matching scans • कोई मिलान नहीं मिला" else "No scans yet • अभी कोई स्कैन नहीं",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isSearchActive) {
                "Try searching with a different crop or disease name.\nअन्य फसल या रोग नाम से खोज कर देखें।"
            } else {
                "Diagnose your crops using on-device AI. All reports are safely stored offline.\nअपनी फसल की पत्तियों की जांच करें। सभी रिपोर्ट यहां सुरक्षित रहेंगी।"
            },
            fontSize = 12.sp,
            color = TextMuted,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        if (!isSearchActive) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryGreenBrand)
                    .clickable(onClick = onScanNow)
                    .padding(horizontal = 20.dp, vertical = 11.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Scan Now • अभी स्कैन करें",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ==============================================================================
// 7. HELPER UTILITIES
// ==============================================================================
private fun getCropHindi(crop: String): String {
    return when (crop.lowercase()) {
        "rice" -> "धान"
        "wheat" -> "गेहूँ"
        "tomato" -> "टमाटर"
        "maize", "corn" -> "मक्का"
        "cotton" -> "कपास"
        else -> "फसल"
    }
}

private fun getCropEmoji(crop: String): String {
    return when (crop.lowercase()) {
        "rice" -> "🌾"
        "wheat" -> "🌾"
        "tomato" -> "🍅"
        "maize", "corn" -> "🌽"
        "cotton" -> "🌱"
        else -> "🌿"
    }
}

private fun getDiagnosisDisplayNames(scan: Diagnosis): Pair<String, String> {
    val isPestPositive = !scan.pestPrediction.isHealthy &&
            scan.pestPrediction.className.isNotBlank() &&
            !scan.pestPrediction.className.contains("no pest", ignoreCase = true)

    if (isPestPositive) {
        val pestName = scan.pestPrediction.className
        val pestHi = when {
            pestName.contains("armyworm", ignoreCase = true) -> "फॉल आर्मीवॉर्म"
            pestName.contains("borer", ignoreCase = true) -> "तना छेदक"
            pestName.contains("hopper", ignoreCase = true) -> "भूरा फुदका"
            else -> "कीट प्रकोप"
        }
        return pestName to pestHi
    }

    if (scan.diseasePrediction.isHealthy) {
        return "Healthy" to "स्वस्थ फसल"
    }

    val diseaseName = scan.diseasePrediction.className
    val diseaseClean = when {
        diseaseName.contains("blast", ignoreCase = true) -> "Rice Blast"
        diseaseName.contains("bacterial", ignoreCase = true) -> "Bacterial Blight"
        diseaseName.contains("rust", ignoreCase = true) -> "Leaf Rust"
        diseaseName.contains("brown", ignoreCase = true) -> "Brown Spot"
        diseaseName.contains("sheath", ignoreCase = true) -> "Sheath Blight"
        else -> diseaseName
    }

    val diseaseHi = when {
        diseaseClean.contains("blast", ignoreCase = true) -> "धान का ब्लास्ट रोग"
        diseaseClean.contains("bacterial", ignoreCase = true) -> "बैक्टीरियल ब्लाइट"
        diseaseClean.contains("rust", ignoreCase = true) -> "लीफ रस्ट"
        diseaseClean.contains("brown", ignoreCase = true) -> "भूरा धब्बा रोग"
        diseaseClean.contains("sheath", ignoreCase = true) -> "शीथ ब्लाइट"
        else -> "रोग लक्षण"
    }

    return diseaseClean to diseaseHi
}

private data class SeverityUIDetails(
    val labelEn: String,
    val labelHi: String,
    val color: Color,
    val bg: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private fun getSeverityDetails(scan: Diagnosis): SeverityUIDetails {
    val isPestPositive = !scan.pestPrediction.isHealthy &&
            scan.pestPrediction.className.isNotBlank() &&
            !scan.pestPrediction.className.contains("no pest", ignoreCase = true)

    if (isPestPositive) {
        return SeverityUIDetails(
            labelEn = "Pest",
            labelHi = "कीट",
            color = WarningAmber,
            bg = WarningAmberBg,
            icon = Icons.Default.BugReport
        )
    }

    if (scan.diseasePrediction.isHealthy) {
        return SeverityUIDetails(
            labelEn = "Healthy",
            labelHi = "स्वस्थ",
            color = HealthyGreen,
            bg = HealthyGreenBg,
            icon = Icons.Default.Eco
        )
    }

    val disease = scan.diseasePrediction.className
    return if (disease.contains("blast", true) || disease.contains("rust", true)) {
        SeverityUIDetails(
            labelEn = "High",
            labelHi = "उच्च",
            color = CriticalRed,
            bg = CriticalRedBg,
            icon = Icons.Default.Warning
        )
    } else {
        SeverityUIDetails(
            labelEn = "Medium",
            labelHi = "मध्यम",
            color = WarningAmber,
            bg = WarningAmberBg,
            icon = Icons.Default.ErrorOutline
        )
    }
}

private fun formatDateString(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.ENGLISH)
    return sdf.format(Date(timestamp))
}

private fun getSpecimenFallbackDrawable(imagePath: String, crop: String, title: String): Int {
    return when {
        imagePath.contains("fall_armyworm") || title.contains("Armyworm", true) -> R.drawable.ic_insect_pest
        imagePath.contains("rice_blast") || title.contains("Blast", true) -> R.drawable.disease_sample1
        imagePath.contains("bacterial") || title.contains("Bacterial", true) -> R.drawable.disease_sample2
        crop.equals("Tomato", true) -> R.drawable.crop_tomato
        crop.equals("Wheat", true) -> R.drawable.crop_wheat
        crop.equals("Maize", true) -> R.drawable.crop_maize
        else -> R.drawable.disease_sample1
    }
}
