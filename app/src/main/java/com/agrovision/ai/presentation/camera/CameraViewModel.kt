package com.agrovision.ai.presentation.camera

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrovision.ai.camera.CameraManager
import com.agrovision.ai.camera.CameraUiState
import com.agrovision.ai.camera.ImageQualityGuard
import com.agrovision.ai.camera.QualityCheckResult
import com.agrovision.ai.core.common.DispatcherProvider
import com.agrovision.ai.core.logging.AgroLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

sealed interface CameraNavigationEvent {
    data class NavigateToDiagnosis(val imagePath: String, val cropName: String) : CameraNavigationEvent
}

@HiltViewModel
class CameraViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<CameraNavigationEvent>()
    val navigationEvents: SharedFlow<CameraNavigationEvent> = _navigationEvents.asSharedFlow()

    val cameraManager = CameraManager(context)
    private val qualityGuard = ImageQualityGuard()

    fun updateCameraPermission(granted: Boolean) {
        _uiState.update { it.copy(hasCameraPermission = granted) }
    }

    fun toggleFlash() {
        val newFlashState = !_uiState.value.isFlashOn
        _uiState.update { it.copy(isFlashOn = newFlashState) }
        cameraManager.setTorchEnabled(newFlashState)
    }

    fun selectCrop(crop: String) {
        _uiState.update { it.copy(selectedCrop = crop) }
    }

    fun capturePhoto() {
        if (_uiState.value.isCapturing) return
        _uiState.update { it.copy(isCapturing = true, qualityWarning = null) }

        viewModelScope.launch(dispatchers.io) {
            try {
                val photoFile = cameraManager.capturePhoto()
                processCapturedFile(photoFile)
            } catch (e: Exception) {
                AgroLogger.e("Capture failed", e)
                _uiState.update { it.copy(isCapturing = false) }
            }
        }
    }

    fun handleGalleryUri(uri: Uri) {
        _uiState.update { it.copy(isCapturing = true, qualityWarning = null) }

        viewModelScope.launch(dispatchers.io) {
            try {
                val outputDir = File(context.cacheDir, "scans").apply { if (!exists()) mkdirs() }
                val targetFile = File(outputDir, "gallery_import_${System.currentTimeMillis()}.jpg")

                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }

                processCapturedFile(targetFile)
            } catch (e: Exception) {
                AgroLogger.e("Failed to import image from gallery", e)
                _uiState.update { it.copy(isCapturing = false) }
            }
        }
    }

    private suspend fun processCapturedFile(file: File) = withContext(dispatchers.io) {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        if (bitmap == null) {
            _uiState.update { it.copy(isCapturing = false) }
            return@withContext
        }

        val quality = qualityGuard.checkQuality(bitmap)
        bitmap.recycle()

        if (quality is QualityCheckResult.Passed) {
            _uiState.update { it.copy(isCapturing = false, capturedImagePath = file.absolutePath) }
            _navigationEvents.emit(
                CameraNavigationEvent.NavigateToDiagnosis(
                    imagePath = file.absolutePath,
                    cropName = _uiState.value.selectedCrop
                )
            )
        } else {
            // Quality guard flagged blur or lighting issue
            _uiState.update {
                it.copy(
                    isCapturing = false,
                    qualityWarning = quality,
                    capturedImagePath = file.absolutePath
                )
            }
        }
    }

    fun proceedDespiteWarning() {
        val path = _uiState.value.capturedImagePath ?: return
        viewModelScope.launch {
            _navigationEvents.emit(
                CameraNavigationEvent.NavigateToDiagnosis(
                    imagePath = path,
                    cropName = _uiState.value.selectedCrop
                )
            )
        }
    }

    fun dismissQualityWarning() {
        _uiState.update { it.copy(qualityWarning = null) }
    }

    override fun onCleared() {
        super.onCleared()
        cameraManager.release()
    }
}
