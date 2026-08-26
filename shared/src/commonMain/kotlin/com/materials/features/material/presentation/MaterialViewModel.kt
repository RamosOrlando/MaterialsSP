package com.materials.features.material.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.materials.core.domain.util.Resource
import com.materials.features.material.domain.model.Material
import com.materials.features.material.domain.use_case.GetMaterialsUseCase
import com.materials.features.maker.domain.use_case.GetMakersUseCase
import com.materials.features.price_history.domain.repository.PriceHistoryRepository
import com.materials.features.auth.domain.repository.AuthRepository
import com.materials.features.price_history.domain.model.PriceHistory
import com.materials.core.util.getCurrentDate
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
    val makerName: String,
    val providerName: String? = null
)

sealed interface MaterialUiState {
    object Loading : MaterialUiState
    data class Success(
        val materials: List<MaterialItem>,
        val providers: List<com.materials.features.provider.domain.model.Provider> = emptyList(),
        val errorMessage: String? = null
    ) : MaterialUiState
    data class Error(val message: String) : MaterialUiState
}

sealed interface MaterialEvent {
    data class OnSearchQueryChanged(val query: String) : MaterialEvent
    data class SetSection(val sectionId: String?) : MaterialEvent
    data class ToggleMaterialSelection(val materialId: String) : MaterialEvent
    data class UpdateMaterial(val material: Material) : MaterialEvent
    data class BulkUpdateMaterials(val updatedMaterials: List<Material>) : MaterialEvent
    object ClearError : MaterialEvent
    object ClearSelection : MaterialEvent
    object Refresh : MaterialEvent
}

class MaterialViewModel(
    private val getMaterialsUseCase: GetMaterialsUseCase,
    private val getMakersUseCase: GetMakersUseCase,
    private val getProvidersUseCase: com.materials.features.provider.domain.use_case.GetProvidersUseCase,
    private val priceHistoryRepository: PriceHistoryRepository,
    private val authRepository: AuthRepository
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

    val userEmail: String? = authRepository.getCurrentUserEmail()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
    val uiState: StateFlow<MaterialUiState> = combine(
        combine(_searchQuery, _sectionId) { query, sectionId ->
            query to sectionId
        }.debounce(300.milliseconds)
            .flatMapLatest { (query, sectionId) ->
                getMaterialsUseCase.executeFlow(query, sectionId)
            },
        getMakersUseCase.executeFlow(""),
        getProvidersUseCase.executeFlow(""),
        _refreshError
    ) { materialsResource, makersResource, providersResource, refreshError ->
        when {
            materialsResource is Resource.Loading || makersResource is Resource.Loading || providersResource is Resource.Loading -> MaterialUiState.Loading
            materialsResource is Resource.Error -> MaterialUiState.Error(materialsResource.message)
            makersResource is Resource.Error -> MaterialUiState.Error(makersResource.message)
            providersResource is Resource.Error -> MaterialUiState.Error(providersResource.message)
            materialsResource is Resource.Success && makersResource is Resource.Success && providersResource is Resource.Success -> {
                val makersMap = makersResource.data.associateBy { it.makerId }
                val providersMap = providersResource.data.associateBy { it.providerId }
                val materialItems = materialsResource.data.map { material ->
                    MaterialItem(
                        material = material,
                        makerName = makersMap[material.makerId]?.name ?: "Desconocido",
                        providerName = providersMap[material.providerId]?.name
                    )
                }
                MaterialUiState.Success(
                    materials = materialItems, 
                    providers = providersResource.data,
                    errorMessage = refreshError
                )
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
            MaterialEvent.ClearError -> {
                _refreshError.value = null
            }
            is MaterialEvent.UpdateMaterial -> {
                viewModelScope.launch {
                    _isRefreshing.value = true
                    _refreshError.value = null
                    
                    try {
                        // 1. Primero intentamos guardar en el historial
                        event.material.historyId?.let { hId ->
                            val historyEntry = PriceHistory(
                                historyId = hId,
                                materialId = event.material.materialId,
                                providerId = event.material.providerId ?: "PROV-DEFAULT",
                                price = event.material.price ?: 0.0,
                                quoteDate = event.material.quoteDate ?: getCurrentDate(),
                                username = authRepository.getCurrentUserEmail() ?: "anon"
                            )
                            val historyResult = priceHistoryRepository.upsertPriceHistory(historyEntry)
                            if (historyResult is Resource.Error) {
                                throw Exception(historyResult.message)
                            }
                        }

                        // 2. Si el historial se guardó bien, actualizamos el material
                        val materialResult = getMaterialsUseCase.updateMaterial(event.material)
                        if (materialResult is Resource.Error) {
                            throw Exception(materialResult.message)
                        }
                    } catch (e: Exception) {
                        _refreshError.value = e.message
                    } finally {
                        _isRefreshing.value = false
                    }
                }
            }
            is MaterialEvent.BulkUpdateMaterials -> {
                viewModelScope.launch {
                    _isRefreshing.value = true
                    _refreshError.value = null
                    
                    for (material in event.updatedMaterials) {
                        val result = getMaterialsUseCase.updateMaterial(material)
                        if (result is Resource.Error) {
                            _refreshError.value = result.message
                        } else {
                            material.historyId?.let { hId ->
                                val historyEntry = PriceHistory(
                                    historyId = hId,
                                    materialId = material.materialId,
                                    providerId = material.providerId ?: "PROV-DEFAULT",
                                    price = material.price ?: 0.0,
                                    quoteDate = material.quoteDate?.ifBlank { null } ?: getCurrentDate(),
                                    username = authRepository.getCurrentUserEmail() ?: "anon"
                                )
                                val historyResult = priceHistoryRepository.upsertPriceHistory(historyEntry)
                                if (historyResult is Resource.Error) {
                                    _refreshError.value = historyResult.message
                                }
                            }
                        }
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
