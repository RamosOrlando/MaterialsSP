package com.materials.features.category.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.materials.core.domain.util.Resource
import com.materials.features.auth.domain.repository.AuthRepository
import com.materials.features.category.domain.model.Category
import com.materials.features.category.domain.use_case.GetCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface CategoryUiState {
    object Loading : CategoryUiState
    data class Success(val categories: List<Category>) : CategoryUiState
    data class Error(val message: String) : CategoryUiState
}

sealed interface CategoryEvent {
    data class OnSearchQueryChanged(val query: String) : CategoryEvent
    object Refresh : CategoryEvent
    object SignOut : CategoryEvent
}

class CategoryViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isSignedOut = MutableStateFlow(false)
    val isSignedOut = _isSignedOut.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _refreshError = MutableStateFlow<String?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CategoryUiState> = _searchQuery
        .flatMapLatest { query ->
            getCategoriesUseCase.executeFlow(query)
        }
        .map { resource ->
            when (resource) {
                is Resource.Loading -> CategoryUiState.Loading
                is Resource.Error -> CategoryUiState.Error(resource.message)
                is Resource.Success<List<Category>> -> CategoryUiState.Success(resource.data)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CategoryUiState.Loading
        )

    init {
        onEvent(CategoryEvent.Refresh)
    }

    fun onEvent(event: CategoryEvent) {
        when (event) {
            is CategoryEvent.OnSearchQueryChanged -> {
                _searchQuery.value = event.query
            }
            CategoryEvent.Refresh -> {
                viewModelScope.launch {
                    _refreshError.value = null
                    val result = getCategoriesUseCase.refresh()
                    if (result is Resource.Error) {
                        _refreshError.value = result.message
                    }
                }
            }
            CategoryEvent.SignOut -> {
                viewModelScope.launch {
                    authRepository.signOut()
                    _isSignedOut.value = true
                }
            }
        }
    }
}
