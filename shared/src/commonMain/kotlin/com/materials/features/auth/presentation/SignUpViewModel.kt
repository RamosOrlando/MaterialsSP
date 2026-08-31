package com.materials.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.materials.core.domain.util.Resource
import com.materials.features.auth.domain.repository.AuthRepository
import com.materials.features.user.domain.model.User
import com.materials.features.user.domain.model.UserProfession
import com.materials.features.user.domain.model.UserRole
import com.materials.features.user.domain.model.UserPlan
import com.materials.features.user.domain.repository.UserRepository
import com.materials.core.util.date.getCurrentIsoDate
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SignUpUiState(
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val cellphone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val roleId: Int? = null,
    val professionId: Int? = null,
    val roles: List<UserRole> = emptyList(),
    val professions: List<UserProfession> = emptyList(),
    val activePlans: List<UserPlan> = emptyList(),
    val selectedPlanId: Int? = null,
    val showSubscriptionDialog: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isCancelled: Boolean = false,
    val error: String? = null
)

sealed interface SignUpEvent {
    data class OnNameChanged(val name: String) : SignUpEvent
    data class OnLastNameChanged(val lastName: String) : SignUpEvent
    data class OnEmailChanged(val email: String) : SignUpEvent
    data class OnCellphoneChanged(val cellphone: String) : SignUpEvent
    data class OnPasswordChanged(val password: String) : SignUpEvent
    data class OnConfirmPasswordChanged(val confirmPassword: String) : SignUpEvent
    data class OnRoleSelected(val roleId: Int) : SignUpEvent
    data class OnProfessionSelected(val professionId: Int) : SignUpEvent
    data class OnPlanSelected(val planId: Int) : SignUpEvent
    object OnConfirmPlan : SignUpEvent
    object OnCancelSignUp : SignUpEvent
    object OnSignUpClicked : SignUpEvent
    object ClearSuccess : SignUpEvent
}

class SignUpViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    private var registeredUserId: String? = null

    init {
        loadMetadata()
    }

    private fun loadMetadata() {
        // Observar Roles
        userRepository.getRolesFlow()
            .onEach { res ->
                if (res is Resource.Success<List<UserRole>>) {
                    _uiState.update { state ->
                        val roles = res.data
                        val defaultRoleId = if (state.roleId == null) {
                            roles.find { it.name.trim().equals("Cliente", ignoreCase = true) }?.roleId
                        } else state.roleId
                        state.copy(roles = roles, roleId = defaultRoleId)
                    }
                }
            }.launchIn(viewModelScope)

        // Observar Profesiones
        userRepository.getProfessionsFlow()
            .onEach { res ->
                if (res is Resource.Success<List<UserProfession>>) {
                    _uiState.update { state ->
                        val professions = res.data
                        val defaultProfId = if (state.professionId == null) {
                            professions.find { it.name.contains("Ingeniero Civil", ignoreCase = true) }?.professionId
                        } else state.professionId
                        state.copy(professions = professions, professionId = defaultProfId)
                    }
                }
            }.launchIn(viewModelScope)

        // Observar Planes Activos
        userRepository.getActivePlansFlow()
            .onEach { res ->
                if (res is Resource.Success<List<UserPlan>>) {
                    _uiState.update { it.copy(activePlans = res.data) }
                }
            }.launchIn(viewModelScope)

        // Disparar sincronización desde el servidor
        viewModelScope.launch {
            val result = userRepository.refreshMetadata()
            if (result is Resource.Error) {
                _uiState.update { it.copy(error = "Error al cargar catálogos: ${result.message}") }
            }
        }
    }

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.OnNameChanged -> {
                _uiState.update { it.copy(name = event.name, error = null) }
            }
            is SignUpEvent.OnLastNameChanged -> {
                _uiState.update { it.copy(lastName = event.lastName, error = null) }
            }
            is SignUpEvent.OnEmailChanged -> {
                _uiState.update { it.copy(email = event.email, error = null) }
            }
            is SignUpEvent.OnCellphoneChanged -> {
                _uiState.update { it.copy(cellphone = event.cellphone, error = null) }
            }
            is SignUpEvent.OnPasswordChanged -> {
                _uiState.update { it.copy(password = event.password, error = null) }
            }
            is SignUpEvent.OnConfirmPasswordChanged -> {
                _uiState.update { it.copy(confirmPassword = event.confirmPassword, error = null) }
            }
            is SignUpEvent.OnRoleSelected -> {
                _uiState.update { it.copy(roleId = event.roleId, error = null) }
            }
            is SignUpEvent.OnProfessionSelected -> {
                _uiState.update { it.copy(professionId = event.professionId, error = null) }
            }
            is SignUpEvent.OnPlanSelected -> {
                _uiState.update { it.copy(selectedPlanId = event.planId) }
            }
            SignUpEvent.OnConfirmPlan -> {
                uiState.value.selectedPlanId?.let { subscribeToPlan(it) }
            }
            SignUpEvent.OnCancelSignUp -> {
                _uiState.update { it.copy(showSubscriptionDialog = false, isCancelled = true) }
            }
            SignUpEvent.OnSignUpClicked -> {
                signUp()
            }
            SignUpEvent.ClearSuccess -> {
                _uiState.update { 
                    SignUpUiState(
                        roles = it.roles,
                        professions = it.professions,
                        activePlans = it.activePlans,
                        roleId = it.roles.find { r -> r.name.trim().equals("Cliente", ignoreCase = true) }?.roleId,
                        professionId = it.professions.find { p -> p.name.contains("Ingeniero Civil", ignoreCase = true) }?.professionId
                    )
                }
            }
        }
    }

    private fun signUp() {
        val state = uiState.value
        
        val trimmedName = state.name.trim()
        val trimmedLastName = state.lastName.trim()
        val trimmedEmail = state.email.trim()
        val trimmedCellphone = state.cellphone.trim()

        if (trimmedName.isEmpty() || trimmedLastName.isEmpty() || trimmedEmail.isEmpty() || 
            state.password.isEmpty() || state.roleId == null || state.professionId == null) {
            _uiState.update { it.copy(error = "Por favor completa todos los campos obligatorios") }
            return
        }

        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // 1. Create Auth User
            val authResult = authRepository.signUpWithEmail(trimmedEmail, state.password, trimmedName)
            
            authResult.onSuccess { userId ->
                registeredUserId = userId
                // 2. Create Public User Profile
                val newUser = User(
                    userId = userId,
                    name = trimmedName,
                    lastName = trimmedLastName,
                    email = trimmedEmail,
                    roleId = state.roleId,
                    professionId = state.professionId,
                    createdAt = getCurrentIsoDate(),
                    cellphone = trimmedCellphone.toIntOrNull()
                )
                
                val userResult = userRepository.saveUser(newUser)
                
                if (userResult is Resource.Success<*>) {
                    _uiState.update { it.copy(isLoading = false, showSubscriptionDialog = true) }
                } else if (userResult is Resource.Error) {
                    _uiState.update { it.copy(isLoading = false, error = userResult.message) }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al registrarse") }
            }
        }
    }

    private fun subscribeToPlan(planId: Int) {
        val userId = registeredUserId ?: return
        val plan = uiState.value.activePlans.find { it.planId == planId } ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Calcular fecha de fin basada en durationDays
            val now = Clock.System.now()
            val timeZone = TimeZone.currentSystemDefault()
            val endDate = now.plus(plan.durationDays, DateTimeUnit.DAY, timeZone)

            val history = com.materials.features.user.domain.model.SubscriptionHistory(
                subHistoryId = com.materials.core.util.randomUUID(),
                userId = userId,
                planId = planId,
                startDate = now.toString(),
                endDate = endDate.toString(),
                state = "ACTIVE",
                pricePaid = plan.price,
                discountAmount = 0f
            )

            val result = userRepository.saveSubscriptionHistory(history)
            if (result is Resource.Success<*>) {
                _uiState.update { it.copy(isLoading = false, showSubscriptionDialog = false, isSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al suscribirse: ${(result as Resource.Error).message}") }
            }
        }
    }
}
