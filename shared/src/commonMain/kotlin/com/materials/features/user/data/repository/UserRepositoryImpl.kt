package com.materials.features.user.data.repository

import com.materials.core.domain.util.Resource
import com.materials.features.user.data.local.*
import com.materials.features.user.data.remote.UserRemoteDataSource
import com.materials.features.user.domain.model.*
import com.materials.features.user.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun refreshMetadata(): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            println("UserRepository: Refreshing metadata (Roles, Professions, Plans)...")
            
            val roles = remoteDataSource.getRoles()
            println("UserRepository: Fetched ${roles.size} roles from Supabase")
            
            val professions = remoteDataSource.getProfessions()
            println("UserRepository: Fetched ${professions.size} professions from Supabase")
            
            val plans = remoteDataSource.getActivePlans()
            println("UserRepository: Fetched ${plans.size} plans from Supabase")
            
            userDao.insertRoles(roles.map { it.toEntity() })
            userDao.insertProfessions(professions.map { it.toEntity() })
            userDao.insertPlans(plans.map { it.toEntity() })
            
            println("UserRepository: Metadata successfully synchronized to Room")
            Resource.Success(Unit)
        } catch (e: Exception) {
            println("UserRepository: ERROR refreshing metadata: ${e.message}")
            e.printStackTrace()
            Resource.Error(e.message ?: "Error refreshing metadata")
        }
    }

    override fun getRolesFlow(): Flow<Resource<List<UserRole>>> = 
        userDao.getRoles()
            .map { entities -> Resource.Success(entities.map { it.toDomain() }) as Resource<List<UserRole>> }
            .onStart { emit(Resource.Loading) }
            .catch { emit(Resource.Error(it.message ?: "Error loading roles")) }

    override fun getProfessionsFlow(): Flow<Resource<List<UserProfession>>> =
        userDao.getProfessions()
            .map { entities -> Resource.Success(entities.map { it.toDomain() }) as Resource<List<UserProfession>> }
            .onStart { emit(Resource.Loading) }
            .catch { emit(Resource.Error(it.message ?: "Error loading professions")) }

    override fun getActivePlansFlow(): Flow<Resource<List<UserPlan>>> =
        userDao.getActivePlans()
            .map { entities -> Resource.Success(entities.map { it.toDomain() }) as Resource<List<UserPlan>> }
            .onStart { emit(Resource.Loading) }
            .catch { emit(Resource.Error(it.message ?: "Error loading plans")) }

    override fun getUserFlow(userId: String): Flow<Resource<User?>> =
        userDao.getUser(userId)
            .map { entity -> Resource.Success(entity?.toDomain()) as Resource<User?> }
            .onStart { emit(Resource.Loading) }
            .catch { emit(Resource.Error(it.message ?: "Error loading user")) }

    override suspend fun getUser(userId: String): Resource<User?> = withContext(Dispatchers.IO) {
        try {
            val remoteUser = remoteDataSource.getUser(userId)
            remoteUser?.let { 
                userDao.insertUser(it.toEntity())
            }
            Resource.Success(remoteUser)
        } catch (e: Exception) {
            // Fallback to local
            val localUser = userDao.getUser(userId).firstOrNull()?.toDomain()
            Resource.Success(localUser)
        }
    }

    override suspend fun saveUser(user: User): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            remoteDataSource.upsertUser(user)
            userDao.insertUser(user.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error saving user")
        }
    }

    override suspend fun refreshSubscriptionHistory(userId: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val history = remoteDataSource.getSubscriptionHistory(userId)
            history.forEach { 
                userDao.insertSubscriptionHistory(it.toEntity())
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error refreshing subscription history")
        }
    }

    override fun getSubscriptionHistoryFlow(userId: String): Flow<Resource<List<SubscriptionHistory>>> =
        userDao.getSubscriptionHistory(userId)
            .map { entities -> Resource.Success(entities.map { it.toDomain() }) as Resource<List<SubscriptionHistory>> }
            .onStart { emit(Resource.Loading) }
            .catch { emit(Resource.Error(it.message ?: "Error loading subscription history")) }

    override suspend fun saveSubscriptionHistory(history: SubscriptionHistory): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            remoteDataSource.upsertSubscriptionHistory(history)
            userDao.insertSubscriptionHistory(history.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error saving subscription history")
        }
    }
}
