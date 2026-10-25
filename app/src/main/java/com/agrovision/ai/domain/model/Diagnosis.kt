package com.agrovision.ai.domain.model

enum class ConfidenceLevel {
    HIGH,
    MEDIUM,
    LOW
}

data class Prediction(
    val classId: Int,
    val className: String,
    val confidence: Float,
    val scientificName: String? = null,
    val isHealthy: Boolean = false
)

data class Diagnosis(
    val id: String,
    val imagePath: String,
    val timestamp: Long,
    val cropName: String?,
    val diseasePrediction: Prediction,
    val pestPrediction: Prediction,
    val topKDiseases: List<Prediction>,
    val topKPests: List<Prediction>,
    val confidenceLevel: ConfidenceLevel,
    val inferenceTimeMs: Long,
    val preprocessingTimeMs: Long,
    val modelVersion: String,
    val feedbackSubmitted: Boolean = false
)
