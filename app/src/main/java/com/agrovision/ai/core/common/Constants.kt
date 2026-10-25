package com.agrovision.ai.core.common

object Constants {
    const val MODEL_ASSET_PATH = "models/crop_teacher_efficientnet_b3.tflite"
    const val MODEL_NAME = "EfficientNet-B3 Dual-Head (Edge FP16)"
    const val DISEASE_LABELS_PATH = "taxonomy/disease_labels_b3.txt"
    const val PEST_LABELS_PATH = "taxonomy/pest_labels_b3.txt"
    const val CROP_TAXONOMY_PATH = "taxonomy/crop_taxonomy.json"
    const val AGRI_ADVISORY_PATH = "knowledge/agri_advisory.json"

    // Model Taxonomy Contract
    const val DISEASE_CLASSES_COUNT = 83
    const val PEST_CLASSES_COUNT = 113 // 113 classes for EfficientNet-B3 Teacher model
    const val INPUT_IMAGE_SIZE = 224

    // Confidence Engine Thresholds (derived from validation experiments)
    const val HIGH_CONFIDENCE_THRESHOLD = 0.75f
    const val MEDIUM_CONFIDENCE_THRESHOLD = 0.45f

    // Quality Guard Limits
    const val MIN_LAPLACIAN_VARIANCE = 80.0
    const val MIN_BRIGHTNESS = 35.0
    const val MAX_BRIGHTNESS = 245.0
}
