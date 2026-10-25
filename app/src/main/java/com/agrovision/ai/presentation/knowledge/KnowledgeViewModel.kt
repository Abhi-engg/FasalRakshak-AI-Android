package com.agrovision.ai.presentation.knowledge

import androidx.lifecycle.ViewModel
import com.agrovision.ai.knowledge.AgriAdvisoryRepository
import com.agrovision.ai.knowledge.KnowledgeCardItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class KnowledgeUiState(
    val items: List<KnowledgeCardItem> = emptyList(),
    val selectedCrop: String = "All",
    val searchQuery: String = "",
    val availableCrops: List<String> = listOf("All")
)

@HiltViewModel
class KnowledgeViewModel @Inject constructor(
    private val advisoryRepo: AgriAdvisoryRepository
) : ViewModel() {

    private val allItems = advisoryRepo.getAllKnowledgeItems()

    private val _uiState = MutableStateFlow(
        KnowledgeUiState(
            items = allItems,
            availableCrops = listOf("All") + allItems.map { it.crop }.distinct().sorted()
        )
    )
    val uiState: StateFlow<KnowledgeUiState> = _uiState.asStateFlow()

    fun onCropSelected(crop: String) {
        _uiState.update { current ->
            current.copy(
                selectedCrop = crop,
                items = filterItems(crop, current.searchQuery)
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                items = filterItems(current.selectedCrop, query)
            )
        }
    }

    private fun filterItems(crop: String, query: String): List<KnowledgeCardItem> {
        return allItems.filter { item ->
            val matchesCrop = crop == "All" || item.crop.equals(crop, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.organism.contains(query, ignoreCase = true) ||
                    item.symptoms.contains(query, ignoreCase = true)
            matchesCrop && matchesQuery
        }
    }
}
