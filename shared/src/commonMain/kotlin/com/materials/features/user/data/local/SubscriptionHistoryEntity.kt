package com.materials.features.user.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.materials.features.user.domain.model.SubscriptionHistory

@Entity(
    tableName = "SubscriptionHistory",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = UserPlanEntity::class,
            parentColumns = ["planId"],
            childColumns = ["planId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("userId"),
        Index("planId")
    ]
)
data class SubscriptionHistoryEntity(
    @PrimaryKey val subHistoryId: String,
    val userId: String,
    val planId: Int,
    val startDate: String,
    val endDate: String,
    val state: String,
    val pricePaid: Float,
    val discountAmount: Float
)

fun SubscriptionHistoryEntity.toDomain() = SubscriptionHistory(
    subHistoryId = subHistoryId,
    userId = userId,
    planId = planId,
    startDate = startDate,
    endDate = endDate,
    state = state,
    pricePaid = pricePaid,
    discountAmount = discountAmount
)

fun SubscriptionHistory.toEntity() = SubscriptionHistoryEntity(
    subHistoryId = subHistoryId,
    userId = userId,
    planId = planId,
    startDate = startDate,
    endDate = endDate,
    state = state,
    pricePaid = pricePaid,
    discountAmount = discountAmount
)
