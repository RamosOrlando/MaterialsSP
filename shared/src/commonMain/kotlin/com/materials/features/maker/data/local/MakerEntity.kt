package com.materials.features.maker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.materials.features.maker.domain.model.Maker

@Entity(tableName = "Maker")
data class MakerEntity(
    @PrimaryKey val makerId: String,
    val name: String,
    val imagePath: String?
)

fun MakerEntity.toDomain() = Maker(
    makerId = makerId,
    name = name,
    imagePath = imagePath
)

fun Maker.toEntity() = MakerEntity(
    makerId = makerId,
    name = name,
    imagePath = imagePath
)
