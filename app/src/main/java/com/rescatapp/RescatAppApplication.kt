package com.rescatapp

import android.app.Application
import com.rescatapp.core.notification.NotificadorSistemaAndroid
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class RescatAppApplication : Application() {

    @Inject
    lateinit var notificadorSistema: NotificadorSistemaAndroid

    override fun onCreate() {
        super.onCreate()
        notificadorSistema.crearCanalesDeNotificacion()
    }
}
