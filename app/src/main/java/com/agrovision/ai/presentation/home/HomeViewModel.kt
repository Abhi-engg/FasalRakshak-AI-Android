package com.agrovision.ai.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrovision.ai.core.common.AppLanguage
import com.agrovision.ai.core.common.LanguageManager
import com.agrovision.ai.core.network.NetworkMonitor
import com.agrovision.ai.data.repository.ScanRepository
import com.agrovision.ai.ml.CropDiseaseClassifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val scanRepository: ScanRepository,
    val languageManager: LanguageManager,
    private val classifier: CropDiseaseClassifier
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isModelReady = classifier.isReady))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeNetwork()
        observeScans()
    }

    fun cycleLanguage() {
        val next = when (languageManager.currentLanguage) {
            AppLanguage.HINDI -> AppLanguage.MARATHI
            AppLanguage.MARATHI -> AppLanguage.ENGLISH
            AppLanguage.ENGLISH -> AppLanguage.HINDI
        }
        languageManager.setLanguage(next)
    }

    private fun observeNetwork() {
        networkMonitor.networkStatus
            .onEach { status ->
                _uiState.update { it.copy(networkStatus = status) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeScans() {
        scanRepository.getAllScans()
            .onEach { scans ->
                val sevenDaysAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L
                val weekScans = scans.filter { it.timestamp >= sevenDaysAgo }
                val activePool = if (weekScans.isNotEmpty()) weekScans else scans

                val attentionCount = activePool.count { 
                    !it.diseasePrediction.isHealthy || (!it.pestPrediction.isHealthy && !it.pestPrediction.className.contains("No Pest", ignoreCase = true))
                }
                val healthyCount = activePool.count { 
                    it.diseasePrediction.isHealthy && (it.pestPrediction.isHealthy || it.pestPrediction.className.contains("No Pest", ignoreCase = true))
                }
                _uiState.update {
                    it.copy(
                        isModelReady = classifier.isReady,
                        recentDiagnoses = scans.take(4),
                        totalScansCount = scans.size,
                        needsAttentionCount = attentionCount,
                        healthyCount = healthyCount
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
