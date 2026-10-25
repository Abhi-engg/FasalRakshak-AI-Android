package com.agrovision.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.agrovision.ai.domain.model.ConfidenceLevel
import com.agrovision.ai.domain.model.Diagnosis
import com.agrovision.ai.domain.model.Prediction

@Entity(tableName = "scans")
data class ScanEntity(
    @PrimaryKey
    val id: String,
    val imagePath: String,
    val timestamp: Long,
    val cropName: String?,
    val diseaseClassId: Int,
    val diseaseClassName: String,
    val diseaseConfidence: Float,
    val isDiseaseHealthy: Boolean,
    val pestClassId: Int,
    val pestClassName: String,
    val pestConfidence: Float,
    val isPestHealthy: Boolean,
    val confidenceLevel: String,
    val inferenceTimeMs: Long,
    val preprocessingTimeMs: Long,
    val modelVersion: String,
    val feedbackSubmitted: Boolean = false
) {
    fun toDomain(): Diagnosis {
        return Diagnosis(
            id = id,
            imagePath = imagePath,
            timestamp = timestamp,
            cropName = cropName,
            diseasePrediction = Prediction(
                classId = diseaseClassId,
                className = diseaseClassName,
                confidence = diseaseConfidence,
                isHealthy = isDiseaseHealthy
            ),
            pestPrediction = Prediction(
                classId = pestClassId,
                className = pestClassName,
                confidence = pestConfidence,
                isHealthy = isPestHealthy
            ),
            topKDiseases = listOf(
                Prediction(diseaseClassId, diseaseClassName, diseaseConfidence, isHealthy = isDiseaseHealthy)
            ),
            topKPests = listOf(
                Prediction(pestClassId, pestClassName, pestConfidence, isHealthy = isPestHealthy)
            ),
            confidenceLevel = when (confidenceLevel.uppercase()) {
                "HIGH" -> ConfidenceLevel.HIGH
                "MEDIUM" -> ConfidenceLevel.MEDIUM
                else -> ConfidenceLevel.LOW
            },
            inferenceTimeMs = inferenceTimeMs,
            preprocessingTimeMs = preprocessingTimeMs,
            modelVersion = modelVersion,
            feedbackSubmitted = feedbackSubmitted
        )
    }

    companion object {
        fun fromDomain(diagnosis: Diagnosis): ScanEntity {
            return ScanEntity(
                id = diagnosis.id,
                imagePath = diagnosis.imagePath,
                timestamp = diagnosis.timestamp,
                cropName = diagnosis.cropName,
                diseaseClassId = diagnosis.diseasePrediction.classId,
                diseaseClassName = diagnosis.diseasePrediction.className,
                diseaseConfidence = diagnosis.diseasePrediction.confidence,
                isDiseaseHealthy = diagnosis.diseasePrediction.isHealthy,
                pestClassId = diagnosis.pestPrediction.classId,
                pestClassName = diagnosis.pestPrediction.className,
                pestConfidence = diagnosis.pestPrediction.confidence,
                isPestHealthy = diagnosis.pestPrediction.isHealthy,
                confidenceLevel = diagnosis.confidenceLevel.name,
                inferenceTimeMs = diagnosis.inferenceTimeMs,
                preprocessingTimeMs = diagnosis.preprocessingTimeMs,
                modelVersion = diagnosis.modelVersion,
                feedbackSubmitted = diagnosis.feedbackSubmitted
            )
        }
    }
}
