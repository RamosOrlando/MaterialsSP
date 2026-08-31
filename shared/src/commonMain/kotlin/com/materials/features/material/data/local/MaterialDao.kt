package com.materials.features.material.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
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

    @Upsert
    suspend fun insertMaterials(materials: List<MaterialEntity>)

    @Upsert
    suspend fun insertMaterial(material: MaterialEntity)

    @Query("SELECT COUNT(DISTINCT SUBSTR(materialId, 1, LENGTH(:sectionId) + 4)) FROM Material WHERE sectionId = :sectionId")
    suspend fun getMaterialCount(sectionId: String): Int

    @Query("SELECT COUNT(DISTINCT SUBSTR(materialId, 1, LENGTH(:sectionId) + 4)) FROM Material WHERE sectionId = :sectionId")
    fun getMaterialCountFlow(sectionId: String): Flow<Int>

    @Query("DELETE FROM Material")
    suspend fun clearAll()
}
