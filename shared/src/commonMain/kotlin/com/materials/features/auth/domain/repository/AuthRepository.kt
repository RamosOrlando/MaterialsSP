package com.materials.features.auth.domain.repository

import com.materials.features.auth.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signInWithEmail(email: String, password: String): Result<Unit>
    suspend fun signUpWithEmail(email: String, password: String, name: String): Result<String>
    suspend fun signOut(): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun updatePassword(newPassword: String): Result<Unit>
    suspend fun isUserLoggedIn(): Boolean
    suspend fun getCurrentProfile(): Result<UserProfile?>
    fun getCurrentUserEmail(): String?
    fun getCurrentUserId(): String?
    fun getUserIdFlow(): Flow<String?>
    suspend fun awaitInitialization()
}
