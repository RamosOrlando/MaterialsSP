package com.materials.features.maker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.materials.core.domain.util.Resource
import com.materials.features.maker.domain.model.Maker
import com.materials.features.maker.domain.use_case.GetMakersUseCase
import com.materials.features.maker.domain.use_case.SaveMakerUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CreateMakerUiState(
    val showAddDialog: Boolean = false,
    val newName: String = "",
    val newImagePath: String = "",
    val error: String? = null,
    val isLoading: Boolean = false
)

sealed interface MakerUiState {
    object Loading : MakerUiState
    data class Success(val makers: List<Maker>) : MakerUiState
    data class Error(val message: String) : MakerUiState
}

sealed interface MakerEvent {
    data class OnSearchQueryChanged(val query: String) : MakerEvent
    object Refresh : MakerEvent
    object OnShowAddDialog : MakerEvent
    object OnDismissAddDialog : MakerEvent
    data class OnNewNameChanged(val name: String) : MakerEvent
    data class OnNewImagePathChanged(val path: String) : MakerEvent
    object OnSaveMaker : MakerEvent
}

class MakerViewModel(
    private val getMakersUseCase: GetMakersUseCase,
    private val saveMakerUseCase: SaveMakerUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _createMakerState = MutableStateFlow(CreateMakerUiState())
    val createMakerState = _createMakerState.asStateFlow()

    private val _refreshError = MutableStateFlow<String?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<MakerUiState> = _searchQuery
        .flatMapLatest { query ->
            getMakersUseCase.executeFlow(query)
        }
        .map { resource ->
            when (resource) {
                is Resource.Loading -> MakerUiState.Loading
                is Resource.Error -> MakerUiState.Error(resource.message)
                is Resource.Success<List<Maker>> -> MakerUiState.Success(resource.data)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MakerUiState.Loading
        )

    init {
        onEvent(MakerEvent.Refresh)
    }

    fun onEvent(event: MakerEvent) {
        when (event) {
            is MakerEvent.OnSearchQueryChanged -> {
                _searchQuery.value = event.query
            }
            MakerEvent.Refresh -> {
                viewModelScope.launch {
                    _refreshError.value = null
                    val result = getMakersUseCase.refresh()
                    if (result is Resource.Error) {
                        _refreshError.value = result.message
                    }
                }
            }
            MakerEvent.OnShowAddDialog -> {
                _createMakerState.update { it.copy(showAddDialog = true, error = null) }
            }
            MakerEvent.OnDismissAddDialog -> {
                _createMakerState.update { CreateMakerUiState() }
            }
            is MakerEvent.OnNewNameChanged -> {
                _createMakerState.update { it.copy(newName = event.name, error = null) }
            }
            is MakerEvent.OnNewImagePathChanged -> {
                _createMakerState.update { it.copy(newImagePath = event.path, error = null) }
            }
            MakerEvent.OnSaveMaker -> {
                saveMaker()
            }
        }
    }

    private fun saveMaker() {
        val state = createMakerState.value
        val name = state.newName.trim()
        val imagePath = state.newImagePath.trim()

        if (name.isEmpty()) {
            _createMakerState.update { it.copy(error = "El nombre no puede estar vacío") }
            return
        }

        // Check for duplicates
        val currentMakers = (uiState.value as? MakerUiState.Success)?.makers ?: emptyList()
        if (currentMakers.any { it.name.trim().equals(name, ignoreCase = true) }) {
            _createMakerState.update { it.copy(error = "El fabricante ya existe") }
            return
        }

        viewModelScope.launch {
            _createMakerState.update { it.copy(isLoading = true) }
            
            // Calculate next correlative ID
            val nextId = (currentMakers.mapNotNull { it.makerId.toIntOrNull() }.maxOrNull() ?: 0) + 1
            
            val newMaker = Maker(
                makerId = nextId.toString(),
                name = name,
                imagePath = if (imagePath.isEmpty()) null else imagePath
            )

            val result = saveMakerUseCase.execute(newMaker)
            when (result) {
                is Resource.Success -> {
                    _createMakerState.update { CreateMakerUiState() }
                }
                is Resource.Error -> {
                    _createMakerState.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> {}
            }
        }
    }
}
