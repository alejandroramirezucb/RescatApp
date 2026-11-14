package com.rescatapp.features.pago.domain

import com.rescatapp.core.model.Oferta
import com.rescatapp.features.pago.model.ComprobantePago
import com.rescatapp.features.pago.model.DatosQrPago
import com.rescatapp.features.pago.model.EstadoPago
import com.rescatapp.features.pago.model.MetodoPago
import com.rescatapp.features.pago.model.ResultadoPago
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay

@Singleton
class PasarelaPagoSimulada @Inject constructor() {

    var tiempoEsperaMs: Long = TIEMPO_SIMULACION_MS

    constructor(tiempoEsperaMs: Long) : this() {
        this.tiempoEsperaMs = tiempoEsperaMs
    }

    private val secuenciaTransaccion = AtomicInteger(1000)
    private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

    fun generarQrParaOferta(
        oferta: Oferta,
        expiracionSegundos: Int = TIEMPO_EXPIRACION_DEFAULT_SEGUNDOS
    ): DatosQrPago {
        val idTransaccion = secuenciaTransaccion.incrementAndGet()
        val referencia = "QR-$idTransaccion"
        val fechaActual = LocalDateTime.now().format(formatoFecha)
        val payload = "SIMPLE-QR|BOL|RESCATAPP|REF-$idTransaccion|OFERTA-${oferta.id}|" +
            "${oferta.comercio}|${oferta.precioRescate}|$fechaActual"

        return DatosQrPago(
            referenciaTransaccion = referencia,
            codigoQrPayload = payload,
            monto = oferta.precioRescate,
            comercio = oferta.comercio,
            concepto = "Reserva de ${oferta.nombre}",
            expiracionSegundos = expiracionSegundos,
            fechaHoraEmision = fechaActual
        )
    }

    suspend fun procesarPagoQr(
        datosQr: DatosQrPago,
        simularExito: Boolean = true,
        tiempoEsperaMs: Long = this.tiempoEsperaMs
    ): ResultadoPago {
        if (tiempoEsperaMs > 0) {
            delay(tiempoEsperaMs)
        }

        if (datosQr.estaExpirado) {
            return ResultadoPago.Rechazado("El código QR ha expirado. Por favor genera uno nuevo.")
        }

        return if (simularExito) {
            val fechaActual = LocalDateTime.now().format(formatoFecha)
            val randomSuffix = System.currentTimeMillis() % 10000
            val numeroComprobante = "TRX-${datosQr.referenciaTransaccion}-$randomSuffix"
            ResultadoPago.Exito(
                comprobante = ComprobantePago(
                    numeroTransaccion = numeroComprobante,
                    referencia = datosQr.referenciaTransaccion,
                    monto = datosQr.monto,
                    comercio = datosQr.comercio,
                    concepto = datosQr.concepto,
                    metodoPago = MetodoPago.QR_SIMPLE,
                    fechaHora = fechaActual,
                    estado = EstadoPago.APROBADO
                )
            )
        } else {
            ResultadoPago.Rechazado(
                "La entidad bancaria rechazó la transacción simulada " +
                    "(fondos insuficientes o límite excedido)."
            )
        }
    }

    suspend fun procesarPagoEfectivo(oferta: Oferta, tiempoEsperaMs: Long = 0L): ResultadoPago {
        if (tiempoEsperaMs > 0) {
            delay(tiempoEsperaMs)
        }
        val idTransaccion = secuenciaTransaccion.incrementAndGet()
        val fechaActual = LocalDateTime.now().format(formatoFecha)
        return ResultadoPago.Exito(
            comprobante = ComprobantePago(
                numeroTransaccion = "EFEC-$idTransaccion",
                referencia = "EFEC-$idTransaccion",
                monto = oferta.precioRescate,
                comercio = oferta.comercio,
                concepto = "Pago en local: ${oferta.nombre}",
                metodoPago = MetodoPago.EFECTIVO,
                fechaHora = fechaActual,
                estado = EstadoPago.APROBADO
            )
        )
    }

    companion object {
        const val TIEMPO_EXPIRACION_DEFAULT_SEGUNDOS = 300
        const val TIEMPO_SIMULACION_MS = 1200L
    }
}
