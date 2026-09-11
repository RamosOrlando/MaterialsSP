package com.materials.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.materials.core.database.category.CategoryDao
import com.materials.core.database.category.CategoryEntity
import com.materials.core.database.section.SectionDao
import com.materials.core.database.section.SectionEntity
import com.materials.core.database.maker.MakerDao
import com.materials.core.database.maker.MakerEntity
import com.materials.core.database.material.MaterialDao
import com.materials.core.database.material.MaterialEntity
import com.materials.core.database.price_history.PriceHistoryDao
import com.materials.core.database.price_history.PriceHistoryEntity
import com.materials.core.database.provider.ProviderDao
import com.materials.core.database.provider.ProviderEntity
import com.materials.core.database.auth.ProfileDao
import com.materials.core.database.auth.ProfileEntity
import com.materials.core.database.user.*

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

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : androidx.room.RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
