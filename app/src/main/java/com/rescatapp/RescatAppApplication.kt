package com.rescatapp

import android.app.Application
import com.rescatapp.core.data.RepositorioAutenticacion
import com.rescatapp.core.notification.NotificadorSistemaAndroid
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class RescatAppApplication : Application() {

    @Inject
    lateinit var notificadorSistema: NotificadorSistemaAndroid

    @Inject
    lateinit var repositorioAutenticacion: RepositorioAutenticacion

    override fun onCreate() {
        super.onCreate()
        notificadorSistema.crearCanalesDeNotificacion()
        CoroutineScope(Dispatchers.IO).launch {
            repositorioAutenticacion.sincronizarSesion()
        }
    }
}
