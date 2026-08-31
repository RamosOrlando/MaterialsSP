package com.materials.features.price_history.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceHistoryDao {
    @Query("SELECT * FROM PriceHistory")
    fun getAllPriceHistory(): Flow<List<PriceHistoryEntity>>

    @Upsert
    suspend fun insertPriceHistories(histories: List<PriceHistoryEntity>)

    @Upsert
    suspend fun insertPriceHistory(history: PriceHistoryEntity)

    @Query("DELETE FROM PriceHistory")
    suspend fun clearAll()
}
