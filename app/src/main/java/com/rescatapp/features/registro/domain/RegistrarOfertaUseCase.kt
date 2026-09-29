package com.rescatapp.features.registro.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.core.util.aDecimalOrNull
import com.rescatapp.core.util.aEnteroPositivoOrNull
import javax.inject.Inject

class RegistrarOfertaUseCase @Inject constructor(
    private val repositorio: RepositorioOfertas,
    private val validarOferta: ValidarOfertaUseCase
) {
    operator fun invoke(campos: CamposRegistro): ResultadoOperacion {
        val error = ResultadoOperacion.Error("Revisa los datos de la oferta")
        if (validarOferta(campos).hayErrores) return error

        val categoria = campos.categoria ?: return error
        val peso = campos.pesoKg.aDecimalOrNull() ?: return error
        val precioNormal = campos.precioNormal.aDecimalOrNull() ?: return error
        val precioRescate = campos.precioRescate.aDecimalOrNull() ?: return error
        val cantidad = campos.cantidadDisponible.aEnteroPositivoOrNull() ?: return error

        val oferta = repositorio.agregar(
            Oferta(
                id = 0,
                nombre = campos.nombre.trim(),
                comercio = campos.comercio.trim(),
                categoria = categoria,
                descripcion = campos.descripcion.trim(),
                pesoKg = peso,
                precioNormal = precioNormal,
                precioRescate = precioRescate,
                cantidadDisponible = cantidad,
                horaRetiroDesde = campos.horaRetiroDesde.trim(),
                horaRetiroHasta = campos.horaRetiroHasta.trim()
            )
        )
        return ResultadoOperacion.Exito(oferta)
    }
}
