package com.agrovision.ai.knowledge

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

data class AdvisoryInfo(
    val organism: String,
    val symptoms: String,
    val management: String,
    val severity: String,
    val cultural: String = "",
    val biological: String = "",
    val chemical: String = "",
    val etl: String = ""
)

data class KnowledgeCardItem(
    val id: String,
    val name: String,
    val crop: String,
    val organism: String,
    val symptoms: String,
    val management: String,
    val severity: String,
    val cultural: String = "",
    val biological: String = "",
    val chemical: String = "",
    val etl: String = ""
)

@Singleton
class AgriAdvisoryRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val advisoryMap = mutableMapOf<String, AdvisoryInfo>()
    private val knowledgeList = mutableListOf<KnowledgeCardItem>()

    init {
        loadAdvisoryData()
    }

    private fun loadAdvisoryData() {
        try {
            val content = context.assets.open("knowledge/agri_advisory.json").use { stream ->
                BufferedReader(InputStreamReader(stream)).readText()
            }
            val root = JSONObject(content)
            val keys = root.keys()
            var index = 1
            while (keys.hasNext()) {
                val key = keys.next()
                val item = root.getJSONObject(key)
                val organism = item.optString("organism", "Crop Entity")
                val symptoms = item.optString("symptoms", "Visual symptoms detected by on-device model.")
                val management = item.optString("management", "Consult local KVK or agricultural officer for guidance.")
                val severity = item.optString("severity", "Moderate")
                val cultural = item.optString("cultural", "")
                val biological = item.optString("biological", "")
                val chemical = item.optString("chemical", "")
                val etl = item.optString("etl", "")

                val advisory = AdvisoryInfo(
                    organism = organism,
                    symptoms = symptoms,
                    management = management,
                    severity = severity,
                    cultural = cultural,
                    biological = biological,
                    chemical = chemical,
                    etl = etl
                )
                advisoryMap[normalizeKey(key)] = advisory

                val (crop, title) = deriveCropAndTitle(key)
                knowledgeList.add(
                    KnowledgeCardItem(
                        id = "kb_${index++}",
                        name = title,
                        crop = crop,
                        organism = organism,
                        symptoms = symptoms,
                        management = management,
                        severity = severity,
                        cultural = cultural,
                        biological = biological,
                        chemical = chemical,
                        etl = etl
                    )
                )
            }
        } catch (e: Exception) {
            // Fallback will serve
        }
    }

    fun getAllKnowledgeItems(): List<KnowledgeCardItem> {
        return knowledgeList
    }

    fun getAdvisory(className: String): AdvisoryInfo {
        val normalized = normalizeKey(className)

        // Direct match
        advisoryMap[normalized]?.let { return it }

        // Partial match
        for ((key, value) in advisoryMap) {
            if (normalized.contains(key) || key.contains(normalized)) {
                return value
            }
        }

        // Healthy match
        if (normalized.contains("healthy")) {
            return advisoryMap["healthy"] ?: AdvisoryInfo(
                organism = "Intact Plant Foliage",
                symptoms = "Vibrant green leaves with regular venation. No visible pathogen or pest lesions.",
                management = "Maintain scheduled irrigation and balanced nutrient management.",
                severity = "Optimal Crop Health",
                cultural = "Maintain scheduled drip or furrow irrigation, balanced split N-P-K nutrient application, and regular bi-weekly field scouting.",
                biological = "Prophylactic soil application of Trichoderma viride (@ 2.5 kg/ha) enriched in FYM.",
                chemical = "No chemical intervention needed. Avoid unnecessary prophylactic spraying.",
                etl = "Optimal crop health."
            )
        }

        // Default clinical fallback
        return AdvisoryInfo(
            organism = className,
            symptoms = "Foliar and pathological patterns consistent with on-device model diagnosis.",
            management = "Recommended: Field scout adjacent plants. Consult your local Krishi Vigyan Kendra (KVK) for location-specific IPM recommendations.",
            severity = "Field Monitoring Recommended",
            cultural = "Maintain good field drainage, remove weed hosts, and monitor adjacent plants.",
            biological = "Apply Neem seed kernel extract (NSKE 5%) or Trichoderma viride (@ 2.5 kg/ha).",
            chemical = "Consult local Krishi Vigyan Kendra (KVK) for CIBRC-registered formulations.",
            etl = "Scout 20 plants across diagonals."
        )
    }

    private fun deriveCropAndTitle(key: String): Pair<String, String> {
        val lower = key.lowercase()
        val title = lower.split(" ", "_", "-")
            .filter { it.isNotBlank() }
            .joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }

        val crop = when {
            lower.contains("rice") || lower.contains("paddy") -> "Rice"
            lower.contains("wheat") -> "Wheat"
            lower.contains("tomato") -> "Tomato"
            lower.contains("potato") -> "Potato"
            lower.contains("chili") || lower.contains("pepper") -> "Chili / Pepper"
            lower.contains("cotton") -> "Cotton"
            lower.contains("groundnut") || lower.contains("peanut") -> "Groundnut"
            lower.contains("citrus") -> "Citrus"
            lower.contains("banana") -> "Banana"
            lower.contains("corn") || lower.contains("maize") -> "Corn / Maize"
            lower.contains("grape") -> "Grape"
            lower.contains("sugarcane") -> "Sugarcane"
            lower.contains("cauliflower") || lower.contains("radish") || lower.contains("cabbage") -> "Brassicas"
            lower.contains("apple") -> "Apple"
            lower.contains("papaya") -> "Papaya"
            lower.contains("cashew") -> "Cashew"
            lower.contains("cassava") -> "Cassava"
            lower.contains("soybean") -> "Soybean"
            lower.contains("alfalfa") -> "Alfalfa"
            lower.contains("healthy") -> "General Foliage"
            else -> "Insect Pest"
        }
        return Pair(crop, title)
    }

    private fun normalizeKey(raw: String): String {
        return raw.trim().lowercase().replace("_", " ").replace("-", " ")
    }
}
