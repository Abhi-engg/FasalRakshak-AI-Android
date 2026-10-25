package com.agrovision.ai.ml

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import com.agrovision.ai.core.common.DispatcherProvider
import com.agrovision.ai.core.logging.AgroLogger
import com.agrovision.ai.domain.model.ConfidenceLevel
import com.agrovision.ai.domain.model.Diagnosis
import com.agrovision.ai.domain.model.Prediction
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * On-Device Dual-Head ML Classifier powering AgroVision AI.
 * Runs the EfficientNet-B3 Dual-Head TFLite Model (Edge Architecture)
 * evaluating both Foliar Crop Pathology (83 classes) and Insect Pest Taxonomy (113 classes).
 * Includes Pathological Crop Masking Prior to eliminate cross-species false positives.
 */
@Singleton
class CropDiseaseClassifier @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatcherProvider
) {

    private var interpreter: Interpreter? = null
    private val diseaseLabels = mutableListOf<String>()
    private val pestLabels = mutableListOf<String>()

    private val cropToAllowedDiseaseIndices = mutableMapOf<String, MutableList<Int>>()
    private var healthyDiseaseIndex: Int = -1

    private val lock = Any()

    val isReady: Boolean
        get() = synchronized(lock) { interpreter != null && diseaseLabels.isNotEmpty() }

    init {
        initModelAndLabels()
    }

    private fun initModelAndLabels() {
        try {
            // Load Taxonomy Labels
            loadLabels("taxonomy/disease_labels_b3.txt", "taxonomy/disease_labels.txt", diseaseLabels)
            loadLabels("taxonomy/pest_labels_b3.txt", "taxonomy/pest_labels.txt", pestLabels)

            buildCropIndices()

            // Load TFLite Model
            val modelBuffer = loadModelFile("models/crop_teacher_efficientnet_b3.tflite")
            val options = Interpreter.Options().apply {
                setNumThreads(4)
                setUseNNAPI(false)
            }
            interpreter = Interpreter(modelBuffer, options)
            AgroLogger.i("EfficientNet-B3 Dual-Head TFLite Classifier initialized successfully with ${diseaseLabels.size} disease classes and ${pestLabels.size} pest classes.")
        } catch (e: Exception) {
            AgroLogger.e("Failed to initialize EfficientNet-B3 TFLite Interpreter", e)
        }
    }

    private fun buildCropIndices() {
        cropToAllowedDiseaseIndices.clear()
        healthyDiseaseIndex = diseaseLabels.indexOfFirst { it.equals("healthy", ignoreCase = true) }

        diseaseLabels.forEachIndexed { index, label ->
            val lower = label.lowercase().trim()
            if (lower == "healthy") return@forEachIndexed

            val cropKey = when {
                lower.startsWith("bell pepper") -> "bell pepper"
                lower.startsWith("rice") || lower.startsWith("paddy") -> "rice"
                lower.startsWith("wheat") -> "wheat"
                lower.startsWith("tomato") -> "tomato"
                lower.startsWith("potato") -> "potato"
                lower.startsWith("corn") || lower.startsWith("maize") -> "corn"
                lower.startsWith("cotton") -> "cotton"
                lower.startsWith("citrus") -> "citrus"
                lower.startsWith("chili") || lower.startsWith("chilli") -> "chili"
                lower.startsWith("banana") -> "banana"
                lower.startsWith("apple") -> "apple"
                lower.startsWith("grape") -> "grape"
                lower.startsWith("soybean") -> "soybean"
                lower.startsWith("sugarcane") -> "sugarcane"
                lower.startsWith("cassava") -> "cassava"
                lower.startsWith("cauliflower") -> "cauliflower"
                lower.startsWith("radish") -> "radish"
                lower.startsWith("cabbage") -> "cabbage"
                lower.startsWith("papaya") -> "papaya"
                lower.startsWith("groundnut") -> "groundnut"
                lower.startsWith("cashew") -> "cashew"
                lower.startsWith("strawberry") -> "strawberry"
                lower.startsWith("raspberry") -> "raspberry"
                lower.startsWith("blueberry") -> "blueberry"
                lower.startsWith("peach") -> "peach"
                else -> lower.split(" ").firstOrNull() ?: ""
            }

            if (cropKey.isNotBlank()) {
                cropToAllowedDiseaseIndices.getOrPut(cropKey) { mutableListOf() }.add(index)
            }
        }
    }

    private fun normalizeCropKey(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val lower = raw.trim().lowercase()
        if (lower in listOf("all crops", "all", "universal", "any", "agricultural crop", "select crop")) {
            return null
        }
        return when {
            lower.contains("bell pepper") -> "bell pepper"
            lower.contains("rice") || lower.contains("paddy") -> "rice"
            lower.contains("wheat") -> "wheat"
            lower.contains("tomato") -> "tomato"
            lower.contains("potato") -> "potato"
            lower.contains("corn") || lower.contains("maize") -> "corn"
            lower.contains("cotton") -> "cotton"
            lower.contains("citrus") || lower.contains("orange") || lower.contains("lemon") -> "citrus"
            lower.contains("chili") || lower.contains("chilli") || lower.contains("pepper") -> "chili"
            lower.contains("banana") -> "banana"
            lower.contains("apple") -> "apple"
            lower.contains("grape") -> "grape"
            lower.contains("soybean") -> "soybean"
            lower.contains("sugarcane") -> "sugarcane"
            lower.contains("cassava") -> "cassava"
            lower.contains("cauliflower") -> "cauliflower"
            lower.contains("radish") -> "radish"
            lower.contains("cabbage") -> "cabbage"
            lower.contains("papaya") -> "papaya"
            lower.contains("groundnut") || lower.contains("peanut") -> "groundnut"
            else -> lower
        }
    }

    private fun loadLabels(primaryPath: String, fallbackPath: String, targetList: MutableList<String>) {
        targetList.clear()
        val path = try {
            context.assets.open(primaryPath).close()
            primaryPath
        } catch (e: Exception) {
            fallbackPath
        }

        context.assets.open(path).use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).useLines { lines ->
                lines.forEach { line ->
                    val trimmed = line.trim()
                    if (trimmed.isNotEmpty()) {
                        targetList.add(trimmed)
                    }
                }
            }
        }
    }

    private fun loadModelFile(modelPath: String): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = context.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    /**
     * Executes end-to-end on-device inference on the provided Bitmap.
     */
    suspend fun classify(
        bitmap: Bitmap,
        imagePath: String = "",
        cropName: String? = null
    ): Diagnosis = withContext(dispatchers.default) {
        val activeInterpreter = interpreter ?: synchronized(lock) {
            initModelAndLabels()
            interpreter ?: throw IllegalStateException("TFLite Interpreter is not initialized.")
        }

        // 1. Preprocessing Stage (Raw RGB [0.0, 255.0] scaling matching EfficientNet contract)
        val startPreprocess = System.currentTimeMillis()
        val inputBuffer = ImagePreprocessor.preprocess(bitmap)
        val preprocessTimeMs = System.currentTimeMillis() - startPreprocess

        // 2. Dual-Head Inference Stage
        val dClassesCount = if (diseaseLabels.isNotEmpty()) diseaseLabels.size else 83
        val pClassesCount = if (pestLabels.isNotEmpty()) pestLabels.size else 113

        val diseaseOutput = Array(1) { FloatArray(dClassesCount) }
        val pestOutput = Array(1) { FloatArray(pClassesCount) }
        val outputs = mapOf(
            0 to diseaseOutput,
            1 to pestOutput
        )

        val startInference = System.currentTimeMillis()
        synchronized(lock) {
            activeInterpreter.runForMultipleInputsOutputs(arrayOf(inputBuffer), outputs)
        }
        val inferenceTimeMs = System.currentTimeMillis() - startInference

        // 3. Output Processing & Pathological Crop Masking Prior
        val dProbs = diseaseOutput[0].copyOf()
        val pProbs = pestOutput[0]

        val targetCropKey = normalizeCropKey(cropName)
        if (targetCropKey != null && cropToAllowedDiseaseIndices.containsKey(targetCropKey)) {
            val allowedIndices = cropToAllowedDiseaseIndices[targetCropKey] ?: emptyList()
            var sum = 0f
            for (i in dProbs.indices) {
                if (i !in allowedIndices && i != healthyDiseaseIndex) {
                    dProbs[i] = 0f
                } else {
                    sum += dProbs[i]
                }
            }
            if (sum > 1e-6f) {
                for (i in dProbs.indices) {
                    dProbs[i] /= sum
                }
            }
        }

        // 4. Top-K Ranking
        val topKDiseases = dProbs.indices
            .map { idx ->
                val name = if (idx < diseaseLabels.size) diseaseLabels[idx] else "Disease #$idx"
                Prediction(
                    classId = idx,
                    className = formatDisplayName(name),
                    confidence = dProbs[idx],
                    isHealthy = name.contains("healthy", ignoreCase = true)
                )
            }
            .sortedByDescending { it.confidence }
            .take(3)

        val topKPests = pProbs.indices
            .map { idx ->
                val name = if (idx < pestLabels.size) pestLabels[idx] else "Pest #$idx"
                Prediction(
                    classId = idx,
                    className = formatDisplayName(name),
                    confidence = pProbs[idx],
                    isHealthy = false
                )
            }
            .sortedByDescending { it.confidence }
            .take(3)

        val primaryDisease = topKDiseases.firstOrNull() ?: Prediction(0, "Healthy Foliage", 1.0f, isHealthy = true)
        val rawPest = topKPests.firstOrNull()

        // PEST DETECTION LOGIC:
        // In a 113-class softmax, uniform chance is 0.88%.
        // A top candidate with confidence >= 15% (or >= 12% if disease is healthy) represents a strong positive pest signal.
        val isPestSignal = rawPest != null && (
            (primaryDisease.isHealthy && rawPest.confidence >= 0.12f) ||
            (rawPest.confidence >= 0.18f)
        )

        val primaryPest = if (isPestSignal) {
            rawPest!!
        } else {
            Prediction(
                classId = -1,
                className = "No Pest Observed",
                confidence = if (rawPest != null) (1.0f - rawPest.confidence).coerceIn(0.6f, 1.0f) else 1.0f,
                isHealthy = true
            )
        }

        // CROP INFERENCE: Extract genuine crop name from user selection or predicted pathology
        val detectedCrop = if (!cropName.isNullOrBlank() && cropName !in listOf("All Crops", "Universal", "Any", "Agricultural Crop")) {
            cropName
        } else {
            inferCropFromFinding(primaryDisease.className)
        }

        val maxConfidence = maxOf(primaryDisease.confidence, if (primaryPest.isHealthy) primaryDisease.confidence else primaryPest.confidence)
        val confidenceLevel = when {
            maxConfidence >= 0.65f -> ConfidenceLevel.HIGH
            maxConfidence >= 0.35f -> ConfidenceLevel.MEDIUM
            else -> ConfidenceLevel.LOW
        }

        Diagnosis(
            id = UUID.randomUUID().toString(),
            imagePath = imagePath,
            timestamp = System.currentTimeMillis(),
            cropName = detectedCrop,
            diseasePrediction = primaryDisease,
            pestPrediction = primaryPest,
            topKDiseases = topKDiseases,
            topKPests = topKPests,
            confidenceLevel = confidenceLevel,
            inferenceTimeMs = inferenceTimeMs,
            preprocessingTimeMs = preprocessTimeMs,
            modelVersion = "EfficientNet-B3 Dual-Head (Edge FP16)"
        )
    }

    private fun inferCropFromFinding(diseaseName: String): String {
        val lower = diseaseName.lowercase()
        return when {
            lower.contains("bell pepper") -> "Bell Pepper"
            lower.contains("rice") || lower.contains("paddy") -> "Rice"
            lower.contains("wheat") -> "Wheat"
            lower.contains("tomato") -> "Tomato"
            lower.contains("potato") -> "Potato"
            lower.contains("cotton") -> "Cotton"
            lower.contains("maize") || lower.contains("corn") -> "Maize"
            lower.contains("banana") -> "Banana"
            lower.contains("apple") -> "Apple"
            lower.contains("grape") -> "Grape"
            lower.contains("soybean") -> "Soybean"
            lower.contains("sugarcane") -> "Sugarcane"
            lower.contains("citrus") || lower.contains("orange") || lower.contains("lemon") -> "Citrus"
            lower.contains("tea") -> "Tea"
            lower.contains("coffee") -> "Coffee"
            lower.contains("chili") || lower.contains("chilli") || lower.contains("pepper") -> "Chili"
            lower.contains("groundnut") || lower.contains("peanut") -> "Groundnut"
            lower.contains("cassava") -> "Cassava"
            lower.contains("cauliflower") || lower.contains("cabbage") || lower.contains("radish") -> "Brassica"
            lower.contains("papaya") -> "Papaya"
            lower.contains("strawberry") -> "Strawberry"
            lower.contains("healthy") -> "Healthy Foliage"
            else -> "Agricultural Crop"
        }
    }

    private fun formatDisplayName(rawName: String): String {
        return rawName.split(" ", "_", "-")
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
    }
}
