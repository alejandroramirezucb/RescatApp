package com.rescatapp.features.registro.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.core.notification.GestorNotificaciones
import com.rescatapp.core.util.aDecimalPositivoOrNull
import com.rescatapp.core.util.aEnteroPositivoOrNull
import javax.inject.Inject

class RegistrarOfertaUseCase @Inject constructor(
    private val repositorioOfertas: RepositorioOfertas,
    private val gestorNotificaciones: GestorNotificaciones? = null
) {
    suspend operator fun invoke(campos: CamposRegistro): ResultadoOperacion {
        val oferta = campos.aOfertaOrNull()
            ?: return ResultadoOperacion.Error("Revisa los datos de la oferta")
        val ofertaAgregada = repositorioOfertas.agregar(oferta)
        gestorNotificaciones?.notificarOfertaPublicada(ofertaAgregada)
        return ResultadoOperacion.Exito(ofertaAgregada)
    }

    private fun CamposRegistro.aOfertaOrNull(): Oferta? {
        return Oferta(
            id = 0,
            nombre = nombre.trim(),
            comercio = comercio.trim(),
            categoria = categoria ?: return null,
            descripcion = descripcion.trim(),
            pesoKg = pesoKg.aDecimalPositivoOrNull() ?: return null,
            precioNormal = precioNormal.aDecimalPositivoOrNull() ?: return null,
            precioRescate = precioRescate.aDecimalPositivoOrNull() ?: return null,
            cantidadDisponible = cantidadDisponible.aEnteroPositivoOrNull() ?: return null,
            horaRetiroDesde = horaRetiroDesde.trim(),
            horaRetiroHasta = horaRetiroHasta.trim(),
            imagenUri = imagenUri
        )
    }
}
