package com.materials.features.material.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.materials.features.maker.data.local.MakerEntity
import com.materials.features.section.data.local.SectionEntity
import com.materials.features.material.domain.model.Material

@Entity(
    tableName = "Material",
    foreignKeys = [
        ForeignKey(
            entity = MakerEntity::class,
            parentColumns = ["makerId"],
            childColumns = ["makerId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = SectionEntity::class,
            parentColumns = ["sectionId"],
            childColumns = ["sectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["makerId"]),
        Index(value = ["sectionId"])
    ]
)
data class MaterialEntity(
    @PrimaryKey val materialId: String,
    val name: String,
    val unit: String,
    val makerId: String,
    val sectionId: String,
    val specId: String?,
    val historyId: String?,
    val providerId: String?,
    val price: Double?,
    val quoteDate: String?
)

fun MaterialEntity.toDomain() = Material(
    materialId = materialId,
    name = name,
    unit = unit,
    makerId = makerId,
    sectionId = sectionId,
    specId = specId,
    historyId = historyId,
    providerId = providerId,
    price = price,
    quoteDate = quoteDate
)

fun Material.toEntity() = MaterialEntity(
    materialId = materialId,
    name = name,
    unit = unit,
    makerId = makerId,
    sectionId = sectionId,
    specId = specId,
    historyId = historyId,
    providerId = providerId,
    price = price,
    quoteDate = quoteDate
)
