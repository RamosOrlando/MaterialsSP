package com.materials.core.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.materials.core.domain.RealtimeSyncManager
import com.materials.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NavigationViewModel(
    private val authRepository: AuthRepository,
    private val syncManager: RealtimeSyncManager
) : ViewModel() {

    private val _initialScreen = MutableStateFlow<Screen?>(null)
    val initialScreen = _initialScreen.asStateFlow()

    init {
        checkAuth()
    }

    private fun checkAuth() {
        viewModelScope.launch {
            if (authRepository.isUserLoggedIn()) {
                syncManager.startSyncing()
                _initialScreen.value = Screen.Category
            } else {
                _initialScreen.value = Screen.Login
            }
        }
    }

    fun onLoginSuccess() {
        syncManager.startSyncing()
    }
}
