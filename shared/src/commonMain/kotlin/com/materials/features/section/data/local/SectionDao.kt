package com.materials.features.section.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionDao {
    @Query("SELECT * FROM Section")
    fun getSections(): Flow<List<SectionEntity>>

    @Upsert
    suspend fun insertSections(sections: List<SectionEntity>)

    @Query("DELETE FROM Section")
    suspend fun clearAll()
}
