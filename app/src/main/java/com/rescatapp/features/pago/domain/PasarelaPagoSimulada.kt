package com.rescatapp.features.pago.domain

import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.features.pago.model.ComprobantePago
import com.rescatapp.features.pago.model.DatosQrPago
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay

@Singleton
class PasarelaPagoSimulada(private val esperaMs: Long) {

    @Inject
    constructor() : this(ESPERA_MS)

    private var ultimaTransaccion = 1000

    fun generarQrParaOferta(oferta: Oferta): DatosQrPago {
        ultimaTransaccion++
        val referencia = "QR-$ultimaTransaccion"
        return DatosQrPago(
            referenciaTransaccion = referencia,
            codigoQrPayload = "SIMPLE-QR|BOL|RESCATAPP|$referencia|OFERTA-${oferta.id}|" +
                "${oferta.comercio}|${oferta.precioRescate}",
            monto = oferta.precioRescate,
            comercio = oferta.comercio
        )
    }

    suspend fun procesarPagoQr(datosQr: DatosQrPago, aprobado: Boolean): ResultadoOperacion {
        delay(esperaMs)
        if (!aprobado) {
            return ResultadoOperacion.Error(
                "La entidad bancaria rechazó la transacción simulada " +
                    "(fondos insuficientes o límite excedido)."
            )
        }
        return ResultadoOperacion.Exito(
            ComprobantePago(
                numeroTransaccion = "TRX-${datosQr.referenciaTransaccion}",
                referencia = datosQr.referenciaTransaccion,
                monto = datosQr.monto,
                comercio = datosQr.comercio,
                fechaHora = LocalDateTime.now().format(FORMATO_FECHA)
            )
        )
    }

    private companion object {
        const val ESPERA_MS = 1200L
        val FORMATO_FECHA: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    }
}
