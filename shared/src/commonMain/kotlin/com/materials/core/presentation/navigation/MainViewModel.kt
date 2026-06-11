package com.materials.core.presentation.navigation

import androidx.lifecycle.ViewModel
import com.materials.features.auth.domain.repository.AuthRepository

class MainViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    val userEmail: String = authRepository.getCurrentUserEmail() ?: ""
}
