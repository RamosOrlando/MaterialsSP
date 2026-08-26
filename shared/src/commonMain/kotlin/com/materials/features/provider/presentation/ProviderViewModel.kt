package com.materials.features.provider.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.materials.core.domain.util.Resource
import com.materials.features.provider.domain.model.Provider
import com.materials.features.provider.domain.use_case.GetProvidersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

sealed interface ProviderUiState {
    object Loading : ProviderUiState
    data class Success(val providers: List<Provider>) : ProviderUiState
    data class Error(val message: String) : ProviderUiState
}

sealed interface ProviderEvent {
    data class OnSearchQueryChanged(val query: String) : ProviderEvent
    object Refresh : ProviderEvent
}

class ProviderViewModel(
    private val getProvidersUseCase: GetProvidersUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _refreshError = MutableStateFlow<String?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
    val uiState: StateFlow<ProviderUiState> = combine(
        _searchQuery.debounce(300.milliseconds).flatMapLatest { query ->
            getProvidersUseCase.executeFlow(query)
        },
        _refreshError
    ) { resource, refreshError ->
        when {
            refreshError != null -> ProviderUiState.Error(refreshError)
            resource is Resource.Loading -> ProviderUiState.Loading
            resource is Resource.Error -> ProviderUiState.Error(resource.message)
            resource is Resource.Success -> ProviderUiState.Success(resource.data)
            else -> ProviderUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProviderUiState.Loading
    )

    init {
        onEvent(ProviderEvent.Refresh)
    }

    fun onEvent(event: ProviderEvent) {
        when (event) {
            is ProviderEvent.OnSearchQueryChanged -> {
                _searchQuery.value = event.query
            }
            ProviderEvent.Refresh -> {
                viewModelScope.launch {
                    _refreshError.value = null
                    val result = getProvidersUseCase.refresh()
                    if (result is Resource.Error) {
                        _refreshError.value = result.message
                    }
                }
            }
        }
    }
}
