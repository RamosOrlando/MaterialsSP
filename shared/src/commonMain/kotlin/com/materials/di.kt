package com.materials

import com.materials.core.data.local.AppDatabase
import com.materials.core.data.local.getRoomDatabase
import com.materials.core.data.remote.SupabaseClient
import com.materials.core.domain.RealtimeSyncManager
import com.materials.core.domain.repository.SyncRepository
import com.materials.core.data.repository.SyncRepositoryImpl
import com.materials.core.domain.use_case.PerformFullSyncUseCase
import com.materials.core.presentation.navigation.MainViewModel
import com.materials.core.presentation.navigation.NavigationViewModel
import com.materials.features.auth.data.repository.AuthRepositoryImpl
import com.materials.features.auth.domain.repository.AuthRepository
import com.materials.features.auth.presentation.LoginViewModel
import com.materials.features.auth.presentation.SignUpViewModel
import com.materials.features.category.data.remote.CategoryRemoteDataSource
import com.materials.features.category.data.remote.SupabaseCategoryDataSource
import com.materials.features.category.data.repository.CategoryRepositoryImpl
import com.materials.features.category.domain.repository.CategoryRepository
import com.materials.features.category.domain.use_case.GetCategoriesUseCase
import com.materials.features.category.presentation.CategoryViewModel
import com.materials.features.section.data.remote.SectionRemoteDataSource
import com.materials.features.section.data.remote.SupabaseSectionDataSource
import com.materials.features.section.data.repository.SectionRepositoryImpl
import com.materials.features.section.domain.repository.SectionRepository
import com.materials.features.section.domain.use_case.GetSectionsUseCase
import com.materials.features.section.presentation.SectionViewModel
import com.materials.features.maker.data.remote.MakerRemoteDataSource
import com.materials.features.maker.data.remote.SupabaseMakerDataSource
import com.materials.features.maker.data.repository.MakerRepositoryImpl
import com.materials.features.maker.domain.repository.MakerRepository
import com.materials.features.maker.domain.use_case.GetMakersUseCase
import com.materials.features.maker.domain.use_case.SaveMakerUseCase
import com.materials.features.maker.presentation.MakerViewModel
import com.materials.features.material.data.remote.MaterialRemoteDataSource
import com.materials.features.material.data.remote.SupabaseMaterialDataSource
import com.materials.features.material.data.repository.MaterialRepositoryImpl
import com.materials.features.material.domain.repository.MaterialRepository
import com.materials.features.material.domain.use_case.GetMaterialsUseCase
import com.materials.features.material.presentation.MaterialViewModel
import com.materials.features.material.presentation.MaterialsSelectedViewModel
import com.materials.features.price_history.data.remote.PriceHistoryRemoteDataSource
import com.materials.features.price_history.data.remote.SupabasePriceHistoryDataSource
import com.materials.features.price_history.data.repository.PriceHistoryRepositoryImpl
import com.materials.features.price_history.domain.repository.PriceHistoryRepository
import com.materials.features.price_history.domain.use_case.GetPriceHistoryUseCase
import com.materials.features.price_history.presentation.PriceHistoryScreen
import com.materials.features.price_history.presentation.PriceHistoryViewModel
import com.materials.features.provider.data.remote.ProviderRemoteDataSource
import com.materials.features.provider.data.remote.SupabaseProviderDataSource
import com.materials.features.provider.data.repository.ProviderRepositoryImpl
import com.materials.features.provider.domain.repository.ProviderRepository
import com.materials.features.provider.domain.use_case.GetProvidersUseCase
import com.materials.features.provider.domain.use_case.SaveProviderUseCase
import com.materials.features.provider.presentation.ProviderViewModel
import com.materials.features.user.data.local.UserDao
import com.materials.features.user.data.remote.SupabaseUserDataSource
import com.materials.features.user.data.remote.UserRemoteDataSource
import com.materials.features.user.data.repository.UserRepositoryImpl
import com.materials.features.user.domain.repository.UserRepository
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModelOf

val dataModule = module {
    single { SupabaseClient.client }
    single { getRoomDatabase(get()) }
    single { get<AppDatabase>().categoryDao() }
    single { get<AppDatabase>().sectionDao() }
    single { get<AppDatabase>().makerDao() }
    single { get<AppDatabase>().materialDao() }
    single { get<AppDatabase>().providerDao() }
    single { get<AppDatabase>().priceHistoryDao() }
    single { get<AppDatabase>().profileDao() }
    single { get<AppDatabase>().userDao() }
    
    factory<CategoryRemoteDataSource> { SupabaseCategoryDataSource(get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get(), get()) }

    factory<SectionRemoteDataSource> { SupabaseSectionDataSource(get()) }
    single<SectionRepository> { SectionRepositoryImpl(get(), get()) }

    factory<MakerRemoteDataSource> { SupabaseMakerDataSource(get()) }
    single<MakerRepository> { MakerRepositoryImpl(get(), get()) }

    factory<MaterialRemoteDataSource> { SupabaseMaterialDataSource(get()) }
    factory<PriceHistoryRemoteDataSource> { SupabasePriceHistoryDataSource(get()) }
    
    single<MaterialRepository> { 
        MaterialRepositoryImpl(
            materialDao = get(),
            providerDao = get(),
            priceHistoryDao = get(),
            makerDao = get(),
            remoteDataSource = get()
        )
    }
    single<PriceHistoryRepository> { PriceHistoryRepositoryImpl(get(), get(), get(), get()) }

    factory<ProviderRemoteDataSource> { SupabaseProviderDataSource(get()) }
    single<ProviderRepository> { ProviderRepositoryImpl(get(), get()) }
    
    factory<UserRemoteDataSource> { SupabaseUserDataSource(get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    single<SyncRepository> { SyncRepositoryImpl(get(), get(), get(), get(), get(), get(), get()) }
    
    single { RealtimeSyncManager(get(), get(), get(), get(), get(), get(), get()) }
}

val domainModule = module {
    factory { GetCategoriesUseCase(get()) }
    factory { GetSectionsUseCase(get()) }
    factory { GetMakersUseCase(get()) }
    factory { SaveMakerUseCase(get()) }
    factory { GetMaterialsUseCase(get()) }
    factory { GetPriceHistoryUseCase(get()) }
    factory { GetProvidersUseCase(get()) }
    factory { SaveProviderUseCase(get()) }
    factory { PerformFullSyncUseCase(get()) }
}

val viewModelModule = module {
    viewModelOf(::CategoryViewModel)
    viewModelOf(::SectionViewModel)
    viewModelOf(::MakerViewModel)
    viewModelOf(::MaterialViewModel)
    viewModelOf(::PriceHistoryViewModel)
    viewModelOf(::ProviderViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::NavigationViewModel)
    viewModelOf(::MainViewModel)
    factory { (materialIds: List<String>, initialQuantities: Map<String, Double>?) -> 
        MaterialsSelectedViewModel(materialIds, initialQuantities ?: emptyMap(), get()) 
    }
}

expect val platformModule: Module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(dataModule, domainModule, viewModelModule, platformModule)
    }
}
