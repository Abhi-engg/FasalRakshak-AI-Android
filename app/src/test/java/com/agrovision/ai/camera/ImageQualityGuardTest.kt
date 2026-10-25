package com.agrovision.ai.camera

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ImageQualityGuardTest {

    @Test
    fun qualityCheckResult_sealedSubtypes_areHandledCorrectly() {
        val passed: QualityCheckResult = QualityCheckResult.Passed
        val blurry: QualityCheckResult = QualityCheckResult.Blurry(42.5)
        val dark: QualityCheckResult = QualityCheckResult.TooDark(22.0)
        val bright: QualityCheckResult = QualityCheckResult.TooBright(248.0)

        assertTrue(passed is QualityCheckResult.Passed)
        assertTrue(blurry is QualityCheckResult.Blurry)
        assertTrue(dark is QualityCheckResult.TooDark)
        assertTrue(bright is QualityCheckResult.TooBright)

        assertEquals(42.5, (blurry as QualityCheckResult.Blurry).variance, 0.01)
        assertEquals(22.0, (dark as QualityCheckResult.TooDark).brightness, 0.01)
        assertEquals(248.0, (bright as QualityCheckResult.TooBright).brightness, 0.01)
    }

    @Test
    fun cameraUiState_defaultState_hasExpectedDefaults() {
        val state = CameraUiState()
        assertEquals(false, state.hasCameraPermission)
        assertEquals(false, state.isFlashOn)
        assertEquals(false, state.isCapturing)
        assertEquals("All Crops", state.selectedCrop)
        assertEquals(null, state.qualityWarning)
        assertEquals(null, state.capturedImagePath)
    }
}
