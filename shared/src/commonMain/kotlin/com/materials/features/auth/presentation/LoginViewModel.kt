package com.materials.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.materials.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChanged -> {
                _uiState.update { it.copy(email = event.email, error = null) }
            }
            is LoginEvent.OnPasswordChanged -> {
                _uiState.update { it.copy(password = event.password, error = null) }
            }
            LoginEvent.OnSignInClicked -> {
                signIn()
            }
            LoginEvent.OnSignUpClicked -> {
                signUp()
            }
            LoginEvent.ClearSuccess -> {
                _uiState.update { LoginUiState() }
            }
            // Forgot Password Handlers
            LoginEvent.OnForgotPasswordClicked -> {
                _uiState.update { it.copy(showForgotPasswordDialog = true, forgotPasswordStep = ForgotPasswordStep.EMAIL, forgotPasswordError = null) }
            }
            is LoginEvent.OnForgotPasswordEmailChanged -> {
                _uiState.update { it.copy(forgotPasswordEmail = event.email, forgotPasswordError = null) }
            }
            is LoginEvent.OnForgotPasswordOtpChanged -> {
                _uiState.update { it.copy(forgotPasswordOtp = event.otp, forgotPasswordError = null) }
            }
            is LoginEvent.OnForgotPasswordNewPasswordChanged -> {
                _uiState.update { it.copy(forgotPasswordNewPassword = event.pass, forgotPasswordError = null) }
            }
            is LoginEvent.OnForgotPasswordConfirmPasswordChanged -> {
                _uiState.update { it.copy(forgotPasswordConfirmPassword = event.pass, forgotPasswordError = null) }
            }
            LoginEvent.OnSendForgotPasswordEmailClicked -> {
                sendForgotPasswordEmail()
            }
            LoginEvent.OnVerifyForgotPasswordOtpClicked -> {
                verifyForgotPasswordOtp()
            }
            LoginEvent.OnResetPasswordClicked -> {
                resetPassword()
            }
            LoginEvent.OnDismissForgotPassword -> {
                _uiState.update { it.copy(showForgotPasswordDialog = false, forgotPasswordSuccess = false) }
            }
        }
    }

    private fun sendForgotPasswordEmail() {
        val email = uiState.value.forgotPasswordEmail.trim()
        if (email.isEmpty()) {
            _uiState.update { it.copy(forgotPasswordError = "Ingresa tu correo electrónico") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(forgotPasswordLoading = true, forgotPasswordError = null) }
            val result = authRepository.resetPassword(email)
            result.onSuccess {
                _uiState.update { it.copy(forgotPasswordLoading = false, forgotPasswordStep = ForgotPasswordStep.OTP) }
            }.onFailure { e ->
                _uiState.update { it.copy(forgotPasswordLoading = false, forgotPasswordError = e.message ?: "Error al enviar correo") }
            }
        }
    }

    private fun verifyForgotPasswordOtp() {
        val state = uiState.value
        if (state.forgotPasswordOtp.length != 8) {
            _uiState.update { it.copy(forgotPasswordError = "El código debe ser de 8 dígitos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(forgotPasswordLoading = true, forgotPasswordError = null) }
            val result = authRepository.verifyRecoveryOtp(state.forgotPasswordEmail, state.forgotPasswordOtp)
            result.onSuccess {
                _uiState.update { it.copy(forgotPasswordLoading = false, forgotPasswordStep = ForgotPasswordStep.NEW_PASSWORD) }
            }.onFailure { e ->
                _uiState.update { it.copy(forgotPasswordLoading = false, forgotPasswordError = e.message ?: "Código inválido") }
            }
        }
    }

    private fun resetPassword() {
        val state = uiState.value
        if (state.forgotPasswordNewPassword.isEmpty()) {
            _uiState.update { it.copy(forgotPasswordError = "Ingresa la nueva contraseña") }
            return
        }
        if (state.forgotPasswordNewPassword != state.forgotPasswordConfirmPassword) {
            _uiState.update { it.copy(forgotPasswordError = "Las contraseñas no coinciden") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(forgotPasswordLoading = true, forgotPasswordError = null) }
            val result = authRepository.updatePassword(state.forgotPasswordNewPassword)
            result.onSuccess {
                _uiState.update { it.copy(forgotPasswordLoading = false, forgotPasswordSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(forgotPasswordLoading = false, forgotPasswordError = e.message ?: "Error al actualizar contraseña") }
            }
        }
    }

    fun resetState() {
        _uiState.update { LoginUiState() }
    }

    private fun signIn() {
        val email = uiState.value.email
        val password = uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Por favor completa todos los campos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.signInWithEmail(email, password)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al iniciar sesión") }
            }
        }
    }

    private fun signUp() {
        val email = uiState.value.email
        val password = uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Por favor completa todos los campos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.signUpWithEmail(email, password, "")
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al registrarse") }
            }
        }
    }
}
