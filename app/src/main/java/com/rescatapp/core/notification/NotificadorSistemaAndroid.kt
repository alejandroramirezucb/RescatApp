package com.rescatapp.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.rescatapp.MainActivity
import com.rescatapp.R
import com.rescatapp.core.model.TipoNotificacion
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class NotificadorSistemaAndroid @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    companion object {
        const val CANAL_PEDIDOS_CLIENTE = "canal_pedidos_cliente"
        const val CANAL_PEDIDOS_VENDEDOR = "canal_pedidos_vendedor"
        const val CANAL_OFERTAS = "canal_ofertas"
    }

    init {
        crearCanalesDeNotificacion()
    }

    open fun crearCanalesDeNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                    ?: return

            val canalCliente = NotificationChannel(
                CANAL_PEDIDOS_CLIENTE,
                "Pedidos Cliente",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones sobre el estado de tus pedidos de rescate"
                enableVibration(true)
            }

            val canalVendedor = NotificationChannel(
                CANAL_PEDIDOS_VENDEDOR,
                "Ventas y Pedidos Recientes",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones para comercios sobre nuevos pedidos y reservas"
                enableVibration(true)
            }

            val canalOfertas = NotificationChannel(
                CANAL_OFERTAS,
                "Ofertas y Rescates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Avisos sobre disponibilidad de ofertas y publicaciones"
            }

            notificationManager.createNotificationChannels(
                listOf(canalCliente, canalVendedor, canalOfertas)
            )
        }
    }

    open fun mostrarNotificacion(id: Int, titulo: String, mensaje: String, tipo: TipoNotificacion) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permisoConcedido = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!permisoConcedido) return
        }

        val canalId = when (tipo) {
            TipoNotificacion.NUEVO_PEDIDO_VENDEDOR -> CANAL_PEDIDOS_VENDEDOR

            TipoNotificacion.OFERTA_AGOTADA,
            TipoNotificacion.OFERTA_PUBLICADA -> CANAL_OFERTAS

            else -> CANAL_PEDIDOS_CLIENTE
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, canalId)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        runCatching {
            NotificationManagerCompat.from(context).notify(id, notification)
        }
    }
}
