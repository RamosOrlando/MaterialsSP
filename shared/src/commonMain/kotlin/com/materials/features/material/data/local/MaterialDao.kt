package com.materials.features.material.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {
    @Query("SELECT * FROM Material")
    fun getMaterials(): Flow<List<MaterialEntity>>

    @Query("""
        SELECT DISTINCT m.* FROM Material m
        LEFT JOIN Maker mk ON m.makerId = mk.makerId
        LEFT JOIN PriceHistory ph ON m.materialId = ph.materialId
        LEFT JOIN Provider p ON ph.providerId = p.providerId
        WHERE (:sectionId IS NULL OR m.sectionId = :sectionId)
        AND (:query = '' 
             OR m.name LIKE '%' || :query || '%' 
             OR mk.name LIKE '%' || :query || '%'
             OR p.name LIKE '%' || :query || '%')
    """)
    fun getMaterialsFiltered(query: String, sectionId: String?): Flow<List<MaterialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterials(materials: List<MaterialEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity)

    @Query("DELETE FROM Material")
    suspend fun clearAll()
}
