package com.materials.core.database.material

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.materials.core.database.maker.MakerEntity
import com.materials.core.database.section.SectionEntity

@Entity(
    tableName = "Material",
    foreignKeys = [
        ForeignKey(
            entity = MakerEntity::class,
            parentColumns = ["makerId"],
            childColumns = ["makerId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SectionEntity::class,
            parentColumns = ["sectionId"],
            childColumns = ["sectionId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
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
