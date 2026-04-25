package com.application.echo.features.country.di

import com.application.echo.features.country.repository.CountryRepository
import com.application.echo.features.country.repository.CountryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CountryModule {

    @Binds
    @Singleton
    abstract fun bindCountryRepository(
        impl: CountryRepositoryImpl,
    ): CountryRepository
}