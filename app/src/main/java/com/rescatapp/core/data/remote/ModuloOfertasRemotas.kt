package com.rescatapp.core.data.remote

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ModuloOfertasRemotas {
    @Binds
    abstract fun proveerFuenteOfertasRemotas(
        repositorio: RepositorioOfertasRemotas
    ): FuenteOfertasRemotas
}
