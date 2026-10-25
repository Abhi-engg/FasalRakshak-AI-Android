package com.agrovision.ai.camera

sealed interface QualityCheckResult {
    data object Passed : QualityCheckResult
    data class Blurry(val variance: Double, val message: String = "Image appears blurry. Please hold your phone steady and retake.") : QualityCheckResult
    data class TooDark(val brightness: Double, val message: String = "Image is too dark. Move to a well-lit area or turn on flash.") : QualityCheckResult
    data class TooBright(val brightness: Double, val message: String = "Image is overexposed. Avoid direct harsh glare on the leaf.") : QualityCheckResult
}

data class CameraUiState(
    val hasCameraPermission: Boolean = false,
    val isFlashOn: Boolean = false,
    val isCapturing: Boolean = false,
    val selectedCrop: String = "All Crops",
    val qualityWarning: QualityCheckResult? = null,
    val capturedImagePath: String? = null
)
