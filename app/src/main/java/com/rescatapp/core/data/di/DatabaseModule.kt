package com.rescatapp.core.data.di

import android.content.Context
import com.rescatapp.core.data.local.RescatAppDatabase
import com.rescatapp.core.data.local.dao.OfertaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabaseScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        scope: CoroutineScope
    ): RescatAppDatabase {
        return RescatAppDatabase.getDatabase(context, scope)
    }

    @Provides
    @Singleton
    fun provideOfertaDao(database: RescatAppDatabase): OfertaDao {
        return database.ofertaDao()
    }
}
