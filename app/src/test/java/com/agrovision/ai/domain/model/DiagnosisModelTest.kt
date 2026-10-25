package com.agrovision.ai.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DiagnosisModelTest {

    @Test
    fun prediction_creationAndProperties_areCorrect() {
        val pred = Prediction(
            classId = 12,
            className = "Tomato Late Blight",
            confidence = 0.93f,
            scientificName = "Phytophthora infestans",
            isHealthy = false
        )

        assertEquals(12, pred.classId)
        assertEquals("Tomato Late Blight", pred.className)
        assertEquals(0.93f, pred.confidence, 0.001f)
        assertEquals("Phytophthora infestans", pred.scientificName)
        assertFalse(pred.isHealthy)
    }

    @Test
    fun diagnosis_creationAndDecoupledProperties_areCorrect() {
        val diseasePred = Prediction(12, "Tomato Late Blight", 0.93f)
        val pestPred = Prediction(0, "No Pest", 0.98f, isHealthy = true)

        val diagnosis = Diagnosis(
            id = "test_001",
            imagePath = "/storage/emulated/0/test.jpg",
            timestamp = 1710000000000L,
            cropName = "Tomato",
            diseasePrediction = diseasePred,
            pestPrediction = pestPred,
            topKDiseases = listOf(diseasePred),
            topKPests = listOf(pestPred),
            confidenceLevel = ConfidenceLevel.HIGH,
            inferenceTimeMs = 24L,
            preprocessingTimeMs = 6L,
            modelVersion = "student_v1.0_int8"
        )

        assertEquals("test_001", diagnosis.id)
        assertEquals("Tomato", diagnosis.cropName)
        assertEquals(ConfidenceLevel.HIGH, diagnosis.confidenceLevel)
        assertEquals(24L, diagnosis.inferenceTimeMs)
        assertTrue(diagnosis.pestPrediction.isHealthy)
        assertFalse(diagnosis.diseasePrediction.isHealthy)
    }
}
