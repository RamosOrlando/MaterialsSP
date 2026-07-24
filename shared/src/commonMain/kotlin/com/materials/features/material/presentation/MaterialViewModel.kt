package com.materials.features.material.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.materials.core.domain.util.Resource
import com.materials.features.material.domain.model.Material
import com.materials.features.material.domain.use_case.GetMaterialsUseCase
import com.materials.features.maker.domain.use_case.GetMakersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class MaterialItem(
    val material: Material,
    val makerName: String?
)

sealed interface MaterialUiState {
    object Loading : MaterialUiState
    data class Success(val materials: List<MaterialItem>) : MaterialUiState
    data class Error(val message: String) : MaterialUiState
}

sealed interface MaterialEvent {
    data class OnSearchQueryChanged(val query: String) : MaterialEvent
    data class SetSection(val sectionId: String?) : MaterialEvent
    data class ToggleMaterialSelection(val materialId: String) : MaterialEvent
    data class UpdateMaterial(val material: Material) : MaterialEvent
    object ClearSelection : MaterialEvent
    object Refresh : MaterialEvent
}

class MaterialViewModel(
    private val getMaterialsUseCase: GetMaterialsUseCase,
    private val getMakersUseCase: GetMakersUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMaterialIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedMaterialIds: StateFlow<Set<String>> = _selectedMaterialIds.asStateFlow()

    private val _sectionId = MutableStateFlow<String?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val nextIndex: StateFlow<Int> = _sectionId
        .flatMapLatest { id ->
            if (id != null) getMaterialsUseCase.getNextIndexFlow(id)
            else emptyFlow()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 1
        )

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _refreshError = MutableStateFlow<String?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
    val uiState: StateFlow<MaterialUiState> = combine(
        combine(_searchQuery, _sectionId) { query, sectionId ->
            query to sectionId
        }.debounce(300.milliseconds)
            .flatMapLatest { (query, sectionId) ->
                getMaterialsUseCase.executeFlow(query, sectionId)
            },
        getMakersUseCase.executeFlow("")
    ) { materialsResource, makersResource ->
        when {
            materialsResource is Resource.Loading || makersResource is Resource.Loading -> MaterialUiState.Loading
            materialsResource is Resource.Error -> MaterialUiState.Error(materialsResource.message)
            makersResource is Resource.Error -> MaterialUiState.Error(makersResource.message)
            materialsResource is Resource.Success && makersResource is Resource.Success -> {
                val makersMap = makersResource.data.associateBy { it.makerId }
                val materialItems = materialsResource.data.map { material ->
                    MaterialItem(
                        material = material,
                        makerName = makersMap[material.makerId]?.name
                    )
                }
                MaterialUiState.Success(materialItems)
            }
            else -> MaterialUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MaterialUiState.Loading
    )

    fun onEvent(event: MaterialEvent) {
        when (event) {
            is MaterialEvent.OnSearchQueryChanged -> {
                _searchQuery.value = event.query
            }
            is MaterialEvent.SetSection -> {
                _sectionId.value = event.sectionId
            }
            is MaterialEvent.ToggleMaterialSelection -> {
                _selectedMaterialIds.value = _selectedMaterialIds.value.let { current ->
                    if (current.contains(event.materialId)) current - event.materialId
                    else current + event.materialId
                }
            }
            MaterialEvent.ClearSelection -> {
                _selectedMaterialIds.value = emptySet()
            }
            is MaterialEvent.UpdateMaterial -> {
                viewModelScope.launch {
                    _isRefreshing.value = true
                    _refreshError.value = null
                    val result = getMaterialsUseCase.updateMaterial(event.material)
                    if (result is Resource.Error) {
                        _refreshError.value = result.message
                    }
                    _isRefreshing.value = false
                }
            }
            MaterialEvent.Refresh -> {
                viewModelScope.launch {
                    _isRefreshing.value = true
                    _refreshError.value = null
                    val result = getMaterialsUseCase.refresh()
                    if (result is Resource.Error) {
                        _refreshError.value = result.message
                    }
                    _isRefreshing.value = false
                }
            }
        }
    }
}
