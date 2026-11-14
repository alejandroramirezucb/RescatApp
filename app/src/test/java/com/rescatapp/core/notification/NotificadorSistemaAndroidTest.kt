package com.rescatapp.core.notification

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.rescatapp.core.model.TipoNotificacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class NotificadorSistemaAndroidTest {

    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificador: NotificadorSistemaAndroid

    @Before
    fun configurar() {
        context = ApplicationProvider.getApplicationContext()
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificador = NotificadorSistemaAndroid(context)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.Q]) // Android 10 (API 29)
    fun canalesCreadosCorrectamenteEnAndroid10() {
        val canales = notificationManager.notificationChannels
        assertEquals(3, canales.size)

        val canalCliente = notificationManager.getNotificationChannel(
            NotificadorSistemaAndroid.CANAL_PEDIDOS_CLIENTE
        )
        assertNotNull(canalCliente)
        assertEquals(NotificationManager.IMPORTANCE_HIGH, canalCliente.importance)
        assertTrue(canalCliente.shouldVibrate())

        val canalVendedor = notificationManager.getNotificationChannel(
            NotificadorSistemaAndroid.CANAL_PEDIDOS_VENDEDOR
        )
        assertNotNull(canalVendedor)
        assertEquals(NotificationManager.IMPORTANCE_HIGH, canalVendedor.importance)
        assertTrue(canalVendedor.shouldVibrate())

        val canalOfertas = notificationManager.getNotificationChannel(
            NotificadorSistemaAndroid.CANAL_OFERTAS
        )
        assertNotNull(canalOfertas)
        assertEquals(NotificationManager.IMPORTANCE_DEFAULT, canalOfertas.importance)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.R]) // Android 11 (API 30)
    fun emiteNotificacionEnAndroid11SinRequerirPermisoRuntime() {
        notificador.mostrarNotificacion(
            id = 42,
            titulo = "Pedido en preparación",
            mensaje = "Tu orden está siendo preparada",
            tipo = TipoNotificacion.PEDIDO_EN_PREPARACION
        )

        val activas = notificationManager.activeNotifications
        assertEquals(1, activas.size)

        val notif = activas.first()
        assertEquals(42, notif.id)
        assertEquals(
            NotificadorSistemaAndroid.CANAL_PEDIDOS_CLIENTE,
            notif.notification.channelId
        )
        assertEquals(
            "Pedido en preparación",
            notif.notification.extras.getString("android.title")
        )
        assertEquals(
            "Tu orden está siendo preparada",
            notif.notification.extras.getString("android.text")
        )
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.R]) // Android 11 (API 30)
    fun emiteNotificacionVendedorEnCanalCorrespondiente() {
        notificador.mostrarNotificacion(
            id = 100,
            titulo = "Nuevo pedido recibido",
            mensaje = "Un cliente reservó un paquete",
            tipo = TipoNotificacion.NUEVO_PEDIDO_VENDEDOR
        )

        val notif = notificationManager.activeNotifications.firstOrNull { it.id == 100 }
        assertNotNull(notif)
        assertEquals(
            NotificadorSistemaAndroid.CANAL_PEDIDOS_VENDEDOR,
            notif?.notification?.channelId
        )
    }
}
