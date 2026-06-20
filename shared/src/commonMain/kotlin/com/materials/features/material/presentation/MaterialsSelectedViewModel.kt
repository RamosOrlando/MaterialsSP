package com.materials.features.material.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.materials.core.domain.util.Resource
import com.materials.features.material.domain.model.MaterialWithPrices
import com.materials.features.material.domain.use_case.GetMaterialsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

sealed interface MaterialsSelectedUiState {
    object Loading : MaterialsSelectedUiState
    data class Success(val materials: List<MaterialWithPrices>) : MaterialsSelectedUiState
    data class Error(val message: String) : MaterialsSelectedUiState
}

class MaterialsSelectedViewModel(
    private val materialIds: List<String>,
    private val initialQuantities: Map<String, Double> = emptyMap(),
    private val getMaterialsUseCase: GetMaterialsUseCase
) : ViewModel() {

    private val _quantities = MutableStateFlow<Map<String, Double>>(
        if (initialQuantities.isEmpty()) materialIds.associateWith { 1.0 } else initialQuantities
    )
    val quantities = _quantities.asStateFlow()

    fun updateQuantity(id: String, quantity: Double) {
        _quantities.update { current ->
            current + (id to quantity)
        }
    }

    val uiState: StateFlow<MaterialsSelectedUiState> = getMaterialsUseCase.executeFlow()
        .map { resource ->
            when (resource) {
                is Resource.Loading -> MaterialsSelectedUiState.Loading
                is Resource.Error -> MaterialsSelectedUiState.Error(resource.message)
                is Resource.Success<List<MaterialWithPrices>> -> {
                    val filtered = resource.data.filter { it.material.materialId in materialIds }
                    MaterialsSelectedUiState.Success(filtered)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MaterialsSelectedUiState.Loading
        )
}
