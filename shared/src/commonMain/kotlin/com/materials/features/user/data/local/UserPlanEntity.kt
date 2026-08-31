package com.materials.features.user.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.materials.features.user.domain.model.UserPlan

@Entity(tableName = "UserPlan")
data class UserPlanEntity(
    @PrimaryKey val planId: Int,
    val name: String,
    val price: Float,
    val discountPrice: Float?,
    val discountStartDate: String?,
    val discountEndDate: String?,
    val durationDays: Int,
    val isActive: Boolean,
    val createdAt: String
)

fun UserPlanEntity.toDomain() = UserPlan(
    planId = planId,
    name = name,
    price = price,
    discountPrice = discountPrice,
    discountStartDate = discountStartDate,
    discountEndDate = discountEndDate,
    durationDays = durationDays,
    isActive = isActive,
    createdAt = createdAt
)

fun UserPlan.toEntity() = UserPlanEntity(
    planId = planId ?: 0,
    name = name,
    price = price,
    discountPrice = discountPrice,
    discountStartDate = discountStartDate,
    discountEndDate = discountEndDate,
    durationDays = durationDays,
    isActive = isActive,
    createdAt = createdAt
)
