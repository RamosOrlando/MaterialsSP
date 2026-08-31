package com.materials.core.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.materials.core.domain.RealtimeSyncManager
import com.materials.features.auth.domain.repository.AuthRepository
import com.materials.features.auth.domain.model.UserRole
import com.materials.features.auth.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NavigationViewModel(
    private val authRepository: AuthRepository,
    private val syncManager: RealtimeSyncManager
) : ViewModel() {

    private val _initialScreen = MutableStateFlow<Screen?>(null)
    val initialScreen = _initialScreen.asStateFlow()

    private val _userRole = MutableStateFlow<UserRole?>(null)
    val userRole = _userRole.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.awaitInitialization()
            authRepository.getUserIdFlow().collect { userId ->
                if (userId != null) {
                    fetchProfile()
                    syncManager.startSyncing()
                    if (_initialScreen.value != Screen.Category) {
                        _initialScreen.value = Screen.Category
                    }
                } else {
                    syncManager.stopSyncing()
                    _userRole.value = null
                    if (_initialScreen.value != Screen.Login) {
                        _initialScreen.value = Screen.Login
                    }
                }
            }
        }
    }

    private suspend fun fetchProfile() {
        authRepository.getCurrentProfile().onSuccess { profile ->
            _userRole.value = profile?.role
        }
    }

    fun onLoginSuccess() {
        viewModelScope.launch {
            fetchProfile()
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}
