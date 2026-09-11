package com.materials.core.database.category

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM Category")
    fun getCategories(): Flow<List<CategoryEntity>>

    @Upsert
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("DELETE FROM Category")
    suspend fun clearAll()
}
