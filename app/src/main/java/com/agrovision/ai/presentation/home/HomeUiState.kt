package com.agrovision.ai.presentation.home

import com.agrovision.ai.core.network.NetworkStatus
import com.agrovision.ai.domain.model.Diagnosis

data class HomeUiState(
    val networkStatus: NetworkStatus = NetworkStatus.OFFLINE,
    val isModelReady: Boolean = true,
    val recentDiagnoses: List<Diagnosis> = emptyList(),
    val totalScansCount: Int = 0,
    val needsAttentionCount: Int = 0,
    val healthyCount: Int = 0,
    val lastSyncTime: String? = null
)
