package com.agrovision.ai.core.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConstantsTest {

    @Test
    fun taxonomyContract_verifiesExactClassCounts() {
        // Assert strict ML training contract: 83 diseases, 113 pests (for EfficientNet-B3 Teacher testing model)
        assertEquals(83, Constants.DISEASE_CLASSES_COUNT)
        assertEquals(113, Constants.PEST_CLASSES_COUNT)
    }

    @Test
    fun modelInputContract_verifiesDimensions() {
        assertEquals(224, Constants.INPUT_IMAGE_SIZE)
    }

    @Test
    fun confidenceThresholds_areMonotonicallyOrdered() {
        assertTrue(Constants.HIGH_CONFIDENCE_THRESHOLD > Constants.MEDIUM_CONFIDENCE_THRESHOLD)
        assertTrue(Constants.MEDIUM_CONFIDENCE_THRESHOLD > 0.0f)
        assertTrue(Constants.HIGH_CONFIDENCE_THRESHOLD < 1.0f)
    }
}
