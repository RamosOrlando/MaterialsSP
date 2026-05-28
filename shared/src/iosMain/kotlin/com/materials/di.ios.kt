package com.materials

import androidx.room.Room
import androidx.room.RoomDatabase
import com.materials.core.data.local.AppDatabase
import platform.Foundation.NSHomeDirectory
import org.koin.dsl.module

actual val platformModule = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        val dbFile = NSHomeDirectory() + "/materials.db"
        Room.databaseBuilder<AppDatabase>(
            name = dbFile
        )
    }
}
