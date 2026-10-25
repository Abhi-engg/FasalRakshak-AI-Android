package com.agrovision.ai.presentation.knowledge

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.agrovision.ai.presentation.theme.bounceClickable
import com.agrovision.ai.presentation.theme.interactiveCard
import com.agrovision.ai.presentation.theme.animatedRotation
import com.agrovision.ai.presentation.theme.agroAccordionEnter
import com.agrovision.ai.presentation.theme.agroAccordionExit
import com.agrovision.ai.presentation.theme.pressScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agrovision.ai.knowledge.KnowledgeCardItem
import com.agrovision.ai.presentation.components.AgroCard
import com.agrovision.ai.presentation.components.EmptyState
import com.agrovision.ai.presentation.components.SeverityChip
import com.agrovision.ai.presentation.components.StatusDot
import com.agrovision.ai.presentation.theme.AlertContainer
import com.agrovision.ai.presentation.theme.AlertStatus
import com.agrovision.ai.presentation.theme.BackgroundWarm
import com.agrovision.ai.presentation.theme.BorderSubtle
import com.agrovision.ai.presentation.theme.DarkForestGreen
import com.agrovision.ai.presentation.theme.HealthyContainer
import com.agrovision.ai.presentation.theme.HealthyStatus
import com.agrovision.ai.presentation.theme.PrimaryContainer
import com.agrovision.ai.presentation.theme.PrimaryGreen
import com.agrovision.ai.presentation.theme.SurfaceCard
import com.agrovision.ai.presentation.theme.SurfaceContainer
import com.agrovision.ai.presentation.theme.TextPrimary
import com.agrovision.ai.presentation.theme.TextSecondary
import com.agrovision.ai.presentation.theme.WarningContainer
import com.agrovision.ai.presentation.theme.WarningStatus

/**
 * Crop Guide Screen (Spec Section 5.5).
 * Resolves P0-1 (squeezed text column), P0-2 (clipped title), P1-1 (duplicate filters),
 * P1-4 (jargon removed), and P1-5 (no back arrow on top-level tab).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeScreen(
    viewModel: KnowledgeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = BackgroundWarm,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Crop guide",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    StatusDot()
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWarm)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Input (Farmer-friendly, no jargon)
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                placeholder = {
                    Text(
                        text = "Search disease, pest, crop...",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard,
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = BorderSubtle
                ),
                singleLine = true
            )

            // Crop Filter Chips (Horizontal Scroll, 48dp touch targets, no duplicates)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                uiState.availableCrops.forEach { crop ->
                    val isSelected = uiState.selectedCrop == crop
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onCropSelected(crop) },
                        label = {
                            Text(
                                text = crop,
                                fontSize = 13.sp,
                                maxLines = 1,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier
                            .heightIn(min = 44.dp)
                            .pressScale(0.95f),
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DarkForestGreen,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceCard,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) DarkForestGreen else BorderSubtle
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Knowledge List
            if (uiState.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        title = "No Guide Found",
                        description = "No treatment guides matched your search. Try searching for a crop like 'Wheat', 'Rice', or an insect like 'Aphids'.",
                        icon = Icons.Default.LocalFlorist
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
                ) {
                    items(uiState.items, key = { it.id }) { item ->
                        GuideCard(item = item)
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

/**
 * GuideCard: Accessible, full-width card layout resolving P0-1.
 * Title and metadata receive the full remaining width (weight = 1f).
 * Severity chip & ICAR source badge render below text without squeezing.
 */
@Composable
private fun GuideCard(item: KnowledgeCardItem) {
    var expanded by remember { mutableStateOf(false) }
    val isPest = item.crop == "Insect Pest" || item.organism.contains("Insect", ignoreCase = true)
    val badgeColor = if (isPest) WarningStatus else AlertStatus
    val badgeBg = if (isPest) WarningContainer else AlertContainer
    val icon = if (isPest) Icons.Default.BugReport else Icons.Default.LocalFlorist

    AgroCard(
        modifier = Modifier.interactiveCard(onClick = { expanded = !expanded }, pressedScale = 0.985f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Specimen/Category Icon (48dp)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Main Content Column (Takes 100% of remaining width — P0-1 fix!)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${item.crop} • ${item.organism}",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Severity and Source Badges Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SeverityChip(severity = item.severity)

                    Box(
                        modifier = Modifier
                            .background(SurfaceContainer, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ICAR Standard",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkForestGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Expand Indicator with smooth 180° rotation
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = TextSecondary,
                modifier = Modifier
                    .size(24.dp)
                    .animatedRotation(expanded, 180f)
            )
        }

        // Expandable Detail Treatment & Scouting Content
        AnimatedVisibility(
            visible = expanded,
            enter = agroAccordionEnter(),
            exit = agroAccordionExit()
        ) {
            Column(modifier = Modifier.padding(top = 14.dp)) {
                // Symptoms
                Text(
                    text = "Observed Symptoms:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.symptoms,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 19.sp
                )

                // Cultural Field Measures
                if (item.cultural.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "🌿 1. Cultural Field Measures:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkForestGreen
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = item.cultural,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 19.sp
                    )
                }

                // Biological Biocontrol
                if (item.biological.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "🧫 2. Biological Biocontrol (ICAR Approved):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = item.biological,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 19.sp
                    )
                }

                // Chemical Dosage
                if (item.chemical.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "🧪 3. Chemical Formulation & Dosage (DPPQS Standard):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlertStatus
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = item.chemical,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 19.sp
                    )
                }

                // Economic Threshold Level (ETL)
                if (item.etl.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "⚖️ Economic Threshold Level (ETL):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.etl,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}
