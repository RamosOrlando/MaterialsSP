package com.materials.features.maker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MakerDao {
    @Query("SELECT * FROM Maker")
    fun getMakers(): Flow<List<MakerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMakers(makers: List<MakerEntity>)

    @Query("DELETE FROM Maker")
    suspend fun clearAll()
}
