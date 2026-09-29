package com.rescatapp.core.data.di

import com.rescatapp.core.data.repository.OfertaRepositoryEnMemoria
import com.rescatapp.core.data.repository.PedidoRepositoryEnMemoria
import com.rescatapp.core.data.repository.Repositorio
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.Pedido
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriosModule {

    @Binds
    abstract fun bindOfertaRepository(
        impl: OfertaRepositoryEnMemoria
    ): Repositorio<Oferta>

    @Binds
    abstract fun bindPedidoRepository(
        impl: PedidoRepositoryEnMemoria
    ): Repositorio<Pedido>
}
