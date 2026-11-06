package com.materials.core.database.resource

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.materials.core.database.maker.MakerEntity
import com.materials.core.database.section.SectionEntity

@Entity(
    tableName = "Resource",
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
data class ResourceEntity(
    @PrimaryKey
    @ColumnInfo(name = "resourceId")
    val resourceId: String,
    val name: String,
    val unit: String,
    val makerId: String,
    val sectionId: String,
    val specId: String?,
    val providerId: String?,
    val price: Double?,
    val quoteDate: String?
)
