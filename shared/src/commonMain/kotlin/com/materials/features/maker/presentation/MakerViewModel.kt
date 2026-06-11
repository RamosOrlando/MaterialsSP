package com.materials.features.maker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.materials.core.domain.util.Resource
import com.materials.features.maker.domain.model.Maker
import com.materials.features.maker.domain.use_case.GetMakersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface MakerUiState {
    object Loading : MakerUiState
    data class Success(val makers: List<Maker>) : MakerUiState
    data class Error(val message: String) : MakerUiState
}

sealed interface MakerEvent {
    data class OnSearchQueryChanged(val query: String) : MakerEvent
    object Refresh : MakerEvent
}

class MakerViewModel(
    private val getMakersUseCase: GetMakersUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

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
        }
    }
}
