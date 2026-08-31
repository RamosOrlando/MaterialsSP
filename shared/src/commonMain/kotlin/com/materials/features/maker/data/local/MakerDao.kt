package com.materials.features.maker.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MakerDao {
    @Query("SELECT * FROM Maker")
    fun getMakers(): Flow<List<MakerEntity>>

    @Upsert
    suspend fun insertMakers(makers: List<MakerEntity>)

    @Query("DELETE FROM Maker")
    suspend fun clearAll()
}
