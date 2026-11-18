package com.rescatapp.core.data.remote

import com.rescatapp.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object ModuloRed {

    @Provides
    @Singleton
    fun proveerSupabaseApi(): SupabaseApi = crearSupabaseApi(
        url = BuildConfig.SUPABASE_URL,
        clavePublica = BuildConfig.SUPABASE_PUBLISHABLE_KEY
    )

    @Provides
    fun proveerImagenesOfertas(imagenes: ImagenesOfertasSupabase): ImagenesOfertas = imagenes
}

fun crearSupabaseApi(url: String, clavePublica: String): SupabaseApi {
    val cliente = OkHttpClient.Builder()
        .addInterceptor { cadena ->
            cadena.proceed(
                cadena.request().newBuilder().header("apikey", clavePublica).build()
            )
        }
        .build()
    return Retrofit.Builder()
        .baseUrl(url.trimEnd('/') + "/")
        .client(cliente)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SupabaseApi::class.java)
}
