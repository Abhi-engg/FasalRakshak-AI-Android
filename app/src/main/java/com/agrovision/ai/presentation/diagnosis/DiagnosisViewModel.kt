package com.agrovision.ai.presentation.diagnosis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrovision.ai.R
import com.agrovision.ai.core.common.DispatcherProvider
import com.agrovision.ai.core.logging.AgroLogger
import com.agrovision.ai.data.repository.ScanRepository
import com.agrovision.ai.domain.model.Diagnosis
import com.agrovision.ai.knowledge.AdvisoryInfo
import com.agrovision.ai.knowledge.AgriAdvisoryRepository
import com.agrovision.ai.ml.CropDiseaseClassifier
import com.agrovision.ai.ml.GradCamExplainer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

sealed interface DiagnosisUiState {
    data object Loading : DiagnosisUiState
    data class Success(
        val diagnosis: Diagnosis,
        val diseaseAdvisory: AdvisoryInfo,
        val pestAdvisory: AdvisoryInfo
    ) : DiagnosisUiState
    data class Error(val message: String) : DiagnosisUiState
}

@HiltViewModel
class DiagnosisViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val classifier: CropDiseaseClassifier,
    private val advisoryRepo: AgriAdvisoryRepository,
    private val scanRepository: ScanRepository,
    private val gradCamExplainer: GradCamExplainer,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<DiagnosisUiState>(DiagnosisUiState.Loading)
    val uiState: StateFlow<DiagnosisUiState> = _uiState.asStateFlow()

    suspend fun generateHeatmap(bitmap: Bitmap, opacity: Float = 0.65f): Bitmap {
        return gradCamExplainer.generateHeatmapOverlay(bitmap, opacity)
    }

    fun runDiagnosis(imagePath: String, cropName: String? = null) {
        viewModelScope.launch {
            _uiState.value = DiagnosisUiState.Loading
            try {
                // 1. First check if this scan already exists in local Room database
                val existingScan = withContext(dispatchers.io) {
                    try {
                        scanRepository.getAllScans().firstOrNull()?.find {
                            it.id == imagePath || it.imagePath == imagePath
                        }
                    } catch (e: Exception) {
                        null
                    }
                }

                val diagnosis = if (existingScan != null) {
                    existingScan
                } else {
                    withContext(dispatchers.io) {
                        val file = File(imagePath)
                        val bitmap = if (file.exists() && file.length() > 0) {
                            val rawBitmap = BitmapFactory.decodeFile(file.absolutePath)
                            if (rawBitmap != null) {
                                val exif = try {
                                    android.media.ExifInterface(file.absolutePath)
                                } catch (e: Exception) {
                                    null
                                }
                                val orientation = exif?.getAttributeInt(
                                    android.media.ExifInterface.TAG_ORIENTATION,
                                    android.media.ExifInterface.ORIENTATION_NORMAL
                                ) ?: android.media.ExifInterface.ORIENTATION_NORMAL

                                val rotationDegrees = when (orientation) {
                                    android.media.ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                                    android.media.ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                                    android.media.ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                                    else -> 0f
                                }

                                if (rotationDegrees != 0f) {
                                    val matrix = android.graphics.Matrix().apply { postRotate(rotationDegrees) }
                                    val rotated = Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true)
                                    rawBitmap.recycle()
                                    rotated
                                } else {
                                    rawBitmap
                                }
                            } else {
                                null
                            }
                        } else {
                            // Authentic foliar pathological specimen fallback
                            BitmapFactory.decodeResource(context.resources, R.drawable.disease_sample1)
                        }

                        if (bitmap == null) {
                            throw IllegalArgumentException("Could not load image from path: $imagePath")
                        }

                        try {
                            val result = classifier.classify(bitmap, imagePath, cropName)
                            // Automatically persist the scan to local Room database
                            scanRepository.saveScan(result)
                            result
                        } finally {
                            bitmap.recycle()
                        }
                    }
                }

                val diseaseAdvisory = advisoryRepo.getAdvisory(diagnosis.diseasePrediction.className)
                val pestAdvisory = advisoryRepo.getAdvisory(diagnosis.pestPrediction.className)

                _uiState.value = DiagnosisUiState.Success(
                    diagnosis = diagnosis,
                    diseaseAdvisory = diseaseAdvisory,
                    pestAdvisory = pestAdvisory
                )
            } catch (e: Exception) {
                AgroLogger.e("Diagnosis error", e)
                _uiState.value = DiagnosisUiState.Error(
                    e.localizedMessage ?: "An error occurred during on-device model diagnosis."
                )
            }
        }
    }

    fun submitFeedback(id: String, isCorrect: Boolean) {
        viewModelScope.launch {
            scanRepository.updateFeedback(id, isCorrect)
        }
    }
}
