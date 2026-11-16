package com.rescatapp.core.notification

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.R])
class NotificadorSistemaAndroidTest {

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificador: NotificadorSistemaAndroid

    @Before
    fun configurar() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        notificationManager = context.getSystemService(NotificationManager::class.java)
        notificador = NotificadorSistemaAndroid(context)
        notificador.crearCanal()
    }

    @Test
    fun crearCanalRegistraElCanalDeRescatApp() {
        val canal = notificationManager.getNotificationChannel(
            NotificadorSistemaAndroid.CANAL_RESCATAPP
        )

        assertNotNull(canal)
        assertEquals(NotificationManager.IMPORTANCE_HIGH, canal.importance)
    }

    @Test
    fun mostrarNotificacionLaPublicaEnElCanalConTituloYMensaje() {
        notificador.mostrarNotificacion(
            id = 42,
            titulo = "Pedido en preparación",
            mensaje = "Tu orden está siendo preparada"
        )

        val notificacion = notificationManager.activeNotifications.single()
        assertEquals(42, notificacion.id)
        assertEquals(NotificadorSistemaAndroid.CANAL_RESCATAPP, notificacion.notification.channelId)
        assertEquals(
            "Pedido en preparación",
            notificacion.notification.extras.getString("android.title")
        )
        assertEquals(
            "Tu orden está siendo preparada",
            notificacion.notification.extras.getString("android.text")
        )
    }
}
