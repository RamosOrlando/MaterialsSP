package com.materials.core.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.materials.core.domain.util.Resource
import com.materials.features.auth.domain.repository.AuthRepository
import com.materials.features.user.domain.model.User
import com.materials.features.user.domain.model.UserRole
import com.materials.features.user.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.daysUntil

data class UserHeaderState(
    val fullName: String = "",
    val roleName: String = "",
    val email: String = "",
    val planName: String = "",
    val daysRemaining: Int = 0
)

class MainViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userHeaderState = MutableStateFlow(UserHeaderState())
    val userHeaderState = _userHeaderState.asStateFlow()

    init {
        observeUserSession()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeUserSession() {
        authRepository.getUserIdFlow()
            .onEach { userId ->
                println("MainViewModel: User session changed: $userId")
                if (userId == null) {
                    _userHeaderState.update { UserHeaderState() }
                } else {
                    val email = authRepository.getCurrentUserEmail() ?: ""
                    _userHeaderState.update { it.copy(email = email) }
                    
                    // Trigger refreshes for new user
                    viewModelScope.launch {
                        userRepository.getUser(userId)
                        userRepository.refreshSubscriptionHistory(userId)
                        userRepository.refreshMetadata()
                    }
                }
            }
            .filterNotNull()
            .flatMapLatest { userId ->
                combine(
                    userRepository.getUserFlow(userId),
                    userRepository.getRolesFlow(),
                    userRepository.getSubscriptionHistoryFlow(userId),
                    userRepository.getActivePlansFlow()
                ) { userRes, rolesRes, subsRes, plansRes ->
                    if (userRes is Resource.Success && rolesRes is Resource.Success && 
                        subsRes is Resource.Success && plansRes is Resource.Success) {
                        
                        val user = userRes.data
                        val roles = rolesRes.data
                        val subs = subsRes.data
                        val plans = plansRes.data
                        
                        if (user != null) {
                            val role = roles.find { it.roleId == user.roleId }
                            val latestSub = subs.maxByOrNull { it.startDate }
                            val plan = latestSub?.let { sub ->
                                plans.find { it.planId == sub.planId }
                            }

                            val daysRemaining = latestSub?.let { sub ->
                                try {
                                    val now = Instant.parse(com.materials.core.util.date.getCurrentIsoDate()).toLocalDateTime(TimeZone.currentSystemDefault()).date
                                    val end = LocalDate.parse(sub.endDate.substringBefore("T"))
                                    now.daysUntil(end)
                                } catch (e: Exception) {
                                    -1
                                }
                            } ?: -999

                            UserHeaderState(
                                fullName = "${user.name} ${user.lastName}",
                                roleName = role?.name ?: "Usuario",
                                email = authRepository.getCurrentUserEmail() ?: "",
                                planName = plan?.name ?: if (latestSub != null) "Plan #${latestSub.planId}" else "Sin Plan",
                                daysRemaining = daysRemaining
                            )
                        } else {
                            UserHeaderState(email = authRepository.getCurrentUserEmail() ?: "", planName = "Sin Plan", daysRemaining = -999)
                        }
                    } else {
                        _userHeaderState.value // Keep current if loading or error
                    }
                }
            }
            .onEach { newState ->
                _userHeaderState.value = newState
            }
            .launchIn(viewModelScope)
    }
}
