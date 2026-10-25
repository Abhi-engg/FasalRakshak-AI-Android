package com.agrovision.ai.core.result

sealed interface Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>
    data class Error(val error: AppError) : Resource<Nothing>
    data object Loading : Resource<Nothing>
}

sealed interface AppError {
    data class ModelNotLoaded(val message: String = "Diagnostic model is not available.") : AppError
    data class CameraError(val message: String = "Unable to access camera hardware.") : AppError
    data class LowQualityImage(val reason: String) : AppError
    data class TaxonomyNotFound(val message: String = "Taxonomy definition file is missing.") : AppError
    data class StorageError(val message: String = "Failed to save diagnostic record.") : AppError
    data class Unknown(val cause: Throwable? = null) : AppError
}
