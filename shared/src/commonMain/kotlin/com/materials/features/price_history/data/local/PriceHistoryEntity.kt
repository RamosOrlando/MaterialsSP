package com.materials.features.price_history.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.materials.features.material.data.local.MaterialEntity
import com.materials.features.price_history.domain.model.PriceHistory
import com.materials.features.provider.data.local.ProviderEntity

@Entity(
    tableName = "PriceHistory",
    indices = [
        Index(value = ["materialId"]),
        Index(value = ["providerId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = MaterialEntity::class,
            parentColumns = ["materialId"],
            childColumns = ["materialId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProviderEntity::class,
            parentColumns = ["providerId"],
            childColumns = ["providerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PriceHistoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "historyId")
    val historyId: String,
    @ColumnInfo(name = "materialId")
    val materialId: String,
    @ColumnInfo(name = "providerId")
    val providerId: String,
    @ColumnInfo(name = "price")
    val price: Double,
    @ColumnInfo(name = "quoteDate")
    val quoteDate: String?,
    @ColumnInfo(name = "username")
    val username: String
)

fun PriceHistoryEntity.toDomain() = PriceHistory(
    historyId = historyId,
    materialId = materialId,
    providerId = providerId,
    price = price,
    quoteDate = quoteDate,
    username = username
)

fun PriceHistory.toEntity() = PriceHistoryEntity(
    historyId = historyId,
    materialId = materialId,
    providerId = providerId,
    price = price,
    quoteDate = quoteDate,
    username = username
)
