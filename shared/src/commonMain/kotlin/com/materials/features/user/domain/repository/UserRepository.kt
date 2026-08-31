package com.materials.features.user.domain.repository

import com.materials.core.domain.util.Resource
import com.materials.features.user.domain.model.*
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun refreshMetadata(): Resource<Unit> // Roles, Professions, Plans
    
    fun getRolesFlow(): Flow<Resource<List<UserRole>>>
    fun getProfessionsFlow(): Flow<Resource<List<UserProfession>>>
    fun getActivePlansFlow(): Flow<Resource<List<UserPlan>>>
    
    fun getUserFlow(userId: String): Flow<Resource<User?>>
    suspend fun getUser(userId: String): Resource<User?>
    suspend fun saveUser(user: User): Resource<Unit>
    
    suspend fun refreshSubscriptionHistory(userId: String): Resource<Unit>
    fun getSubscriptionHistoryFlow(userId: String): Flow<Resource<List<SubscriptionHistory>>>
    suspend fun saveSubscriptionHistory(history: SubscriptionHistory): Resource<Unit>
}
