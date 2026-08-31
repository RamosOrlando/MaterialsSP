package com.materials.features.auth.data.repository

import com.materials.features.auth.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import com.materials.features.auth.domain.model.UserProfile
import com.materials.features.auth.domain.model.UserRole
import com.materials.features.auth.data.local.ProfileDao
import com.materials.features.auth.data.local.toDomain
import com.materials.features.auth.data.local.toEntity

class AuthRepositoryImpl(
    private val supabaseClient: SupabaseClient,
    private val profileDao: ProfileDao
) : AuthRepository {

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        return try {
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String, name: String): Result<String> {
        return try {
            val user = supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("full_name", name)
                }
            }
            val userId = user?.id ?: throw Exception("Error al obtener ID de usuario tras registro")
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            supabaseClient.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            profileDao.clearProfile()
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            supabaseClient.auth.resetPasswordForEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            supabaseClient.auth.updateUser {
                password = newPassword
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        supabaseClient.auth.awaitInitialization()
        return supabaseClient.auth.currentSessionOrNull() != null
    }

    override suspend fun getCurrentProfile(): Result<UserProfile?> {
        return try {
            val user = supabaseClient.auth.currentUserOrNull() ?: return Result.success(null)
            
            val localProfile = profileDao.getProfile()
            if (localProfile != null && localProfile.id == user.id) {
                return Result.success(localProfile.toDomain())
            }

            // If ID mismatch or no local profile, clear and fetch from remote
            profileDao.clearProfile()
            
            val remoteProfile = supabaseClient.postgrest["profiles"]
                .select {
                    filter {
                        UserProfile::id eq user.id
                    }
                }
                .decodeSingle<UserProfile>()
            
            profileDao.insertProfile(remoteProfile.toEntity())
            Result.success(remoteProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUserEmail(): String? {
        return supabaseClient.auth.currentUserOrNull()?.email
    }

    override fun getCurrentUserId(): String? {
        return supabaseClient.auth.currentUserOrNull()?.id
    }

    override fun getUserIdFlow(): Flow<String?> {
        return supabaseClient.auth.sessionStatus
            .filter { it !is SessionStatus.Initializing }
            .map { status ->
                when (status) {
                    is SessionStatus.Authenticated -> status.session.user?.id
                    else -> null
                }
            }
    }

    override suspend fun awaitInitialization() {
        supabaseClient.auth.awaitInitialization()
    }
}
