package com.rescatapp.core.data.supabase

import com.rescatapp.BuildConfig
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ModuloSupabase {
    @Binds abstract fun almacen(impl: AlmacenSesionSeguro): AlmacenCredenciales

    @Binds abstract fun cache(impl: CacheSupabase): CacheDeCuenta

    companion object {
        @Provides
        @Singleton
        fun http(): SupabaseHttp =
            SupabaseHttp(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_PUBLISHABLE_KEY)
    }
}
