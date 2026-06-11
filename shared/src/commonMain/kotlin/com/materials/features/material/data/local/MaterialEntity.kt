package com.materials.features.material.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.materials.features.maker.data.local.MakerEntity
import com.materials.features.material.domain.model.Material

@Entity(
    tableName = "materials",
    foreignKeys = [
        ForeignKey(
            entity = MakerEntity::class,
            parentColumns = ["makerId"],
            childColumns = ["makerId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["makerId"])]
)
data class MaterialEntity(
    @PrimaryKey val materialId: String,
    val code: String,
    val name: String,
    val unit: String?,
    val makerId: String?,
    val sectionId: Int
)

fun MaterialEntity.toDomain() = Material(
    materialId = materialId,
    code = code,
    name = name,
    unit = unit,
    makerId = makerId,
    sectionId = sectionId
)

fun Material.toEntity() = MaterialEntity(
    materialId = materialId,
    code = code,
    name = name,
    unit = unit,
    makerId = makerId,
    sectionId = sectionId
)
