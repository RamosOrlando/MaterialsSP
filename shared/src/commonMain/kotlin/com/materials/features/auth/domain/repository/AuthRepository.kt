package com.materials.features.auth.domain.repository

import com.materials.features.auth.domain.model.UserProfile

interface AuthRepository {
    suspend fun signInWithEmail(email: String, password: String): Result<Unit>
    suspend fun signUpWithEmail(email: String, password: String, name: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun updatePassword(newPassword: String): Result<Unit>
    suspend fun isUserLoggedIn(): Boolean
    suspend fun getCurrentProfile(): Result<UserProfile?>
    fun getCurrentUserEmail(): String?
}
