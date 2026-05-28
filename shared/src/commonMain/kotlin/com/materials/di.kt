package com.materials

import com.materials.core.data.local.AppDatabase
import com.materials.core.data.local.getRoomDatabase
import com.materials.core.data.remote.SupabaseClient
import com.materials.features.category.data.remote.CategoryRemoteDataSource
import com.materials.features.category.data.remote.SupabaseCategoryDataSource
import com.materials.features.category.data.repository.CategoryRepositoryImpl
import com.materials.features.category.domain.repository.CategoryRepository
import com.materials.features.category.domain.use_case.GetCategoriesUseCase
import com.materials.features.category.presentation.CategoryViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModelOf

val dataModule = module {
    single { SupabaseClient.client }
    single { getRoomDatabase(get()) }
    single { get<AppDatabase>().categoryDao() }
    
    factory<CategoryRemoteDataSource> { SupabaseCategoryDataSource(get()) }
    factory<CategoryRepository> { CategoryRepositoryImpl(get(), get()) }
}

val domainModule = module {
    factory { GetCategoriesUseCase(get()) }
}

val viewModelModule = module {
    viewModelOf(::CategoryViewModel)
}

expect val platformModule: Module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(dataModule, domainModule, viewModelModule, platformModule)
    }
}
