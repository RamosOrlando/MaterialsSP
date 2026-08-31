package com.materials.core.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.materials.features.category.data.local.CategoryDao
import com.materials.features.category.data.local.CategoryEntity
import com.materials.features.section.data.local.SectionDao
import com.materials.features.section.data.local.SectionEntity
import com.materials.features.maker.data.local.MakerDao
import com.materials.features.maker.data.local.MakerEntity
import com.materials.features.material.data.local.MaterialDao
import com.materials.features.material.data.local.MaterialEntity
import com.materials.features.price_history.data.local.PriceHistoryDao
import com.materials.features.price_history.data.local.PriceHistoryEntity
import com.materials.features.provider.data.local.ProviderDao
import com.materials.features.provider.data.local.ProviderEntity
import com.materials.features.auth.data.local.ProfileDao
import com.materials.features.auth.data.local.ProfileEntity
import com.materials.features.user.data.local.*

@Database(entities = [CategoryEntity::class, SectionEntity::class,
    MakerEntity::class, MaterialEntity::class, ProviderEntity::class,
    PriceHistoryEntity::class, ProfileEntity::class,
    UserEntity::class, UserRoleEntity::class, UserProfessionEntity::class,
    UserPlanEntity::class, SubscriptionHistoryEntity::class], version = 16)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun sectionDao(): SectionDao
    abstract fun makerDao(): MakerDao
    abstract fun materialDao(): MaterialDao
    abstract fun providerDao(): ProviderDao
    abstract fun priceHistoryDao(): PriceHistoryDao
    abstract fun profileDao(): ProfileDao
    abstract fun userDao(): UserDao
}

// Expect object for the database builder that will be implemented in each platform
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : androidx.room.RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
