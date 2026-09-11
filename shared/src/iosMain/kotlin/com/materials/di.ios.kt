package com.materials

import androidx.room.Room
import androidx.room.RoomDatabase
import com.materials.core.database.AppDatabase
import com.materials.core.common.util.pdf.IosPdfGenerator
import com.materials.core.common.util.pdf.PdfGenerator
import com.materials.core.common.util.share.IosShareManager
import com.materials.core.common.util.share.ShareManager
import platform.Foundation.NSHomeDirectory
import org.koin.dsl.module

actual val platformModule = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        val dbFile = NSHomeDirectory() + "/materials.db"
        Room.databaseBuilder<AppDatabase>(
            name = dbFile
        )
    }

    single<PdfGenerator> { IosPdfGenerator() }
    single<ShareManager> { IosShareManager() }
}
