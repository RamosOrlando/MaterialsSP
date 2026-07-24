package com.materials.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.materials.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

sealed interface SignUpEvent {
    data class OnNameChanged(val name: String) : SignUpEvent
    data class OnEmailChanged(val email: String) : SignUpEvent
    data class OnPasswordChanged(val password: String) : SignUpEvent
    data class OnConfirmPasswordChanged(val confirmPassword: String) : SignUpEvent
    object OnSignUpClicked : SignUpEvent
}

class SignUpViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.OnNameChanged -> {
                _uiState.update { it.copy(name = event.name, error = null) }
            }
            is SignUpEvent.OnEmailChanged -> {
                _uiState.update { it.copy(email = event.email, error = null) }
            }
            is SignUpEvent.OnPasswordChanged -> {
                _uiState.update { it.copy(password = event.password, error = null) }
            }
            is SignUpEvent.OnConfirmPasswordChanged -> {
                _uiState.update { it.copy(confirmPassword = event.confirmPassword, error = null) }
            }
            SignUpEvent.OnSignUpClicked -> {
                signUp()
            }
        }
    }

    private fun signUp() {
        val state = uiState.value
        if (state.name.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Por favor completa todos los campos") }
            return
        }

        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.signUpWithEmail(state.email, state.password, state.name)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al registrarse") }
            }
        }
    }
}
