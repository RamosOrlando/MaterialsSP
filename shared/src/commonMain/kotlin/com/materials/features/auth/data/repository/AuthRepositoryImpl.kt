package com.materials.features.auth.data.repository

import com.materials.features.auth.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
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

    override suspend fun signUpWithEmail(email: String, password: String, name: String): Result<Unit> {
        return try {
            supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("full_name", name)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            supabaseClient.auth.signOut()
            profileDao.clearProfile()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
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
            val localProfile = profileDao.getProfile()
            if (localProfile != null) {
                return Result.success(localProfile.toDomain())
            }

            val user = supabaseClient.auth.currentUserOrNull() ?: return Result.success(null)
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
}
