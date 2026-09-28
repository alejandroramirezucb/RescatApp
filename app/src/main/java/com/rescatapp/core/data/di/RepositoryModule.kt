package com.rescatapp.core.data.di

import com.rescatapp.core.data.repository.OfertasRepositoryImpl
import com.rescatapp.core.domain.repository.OfertasRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindOfertasRepository(
        ofertasRepositoryImpl: OfertasRepositoryImpl
    ): OfertasRepository
}
