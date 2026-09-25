package com.materials.features.auth.di

import com.materials.features.auth.data.repository.AuthRepositoryImpl
import com.materials.features.auth.domain.repository.AuthRepository
import com.materials.features.auth.presentation.LoginViewModel
import com.materials.features.auth.presentation.SignUpViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    viewModelOf(::LoginViewModel)
    viewModelOf(::SignUpViewModel)
}
