package com.rescatapp.features.registro.domain

import com.rescatapp.core.data.repository.RepositorioOfertas
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.aDecimalOrNull
import com.rescatapp.core.util.aEnteroPositivoOrNull

class RegistrarOfertaUseCase(private val repositorio: RepositorioOfertas) {
    operator fun invoke(campos: CamposRegistro): Oferta? {
        if (ValidarOfertaUseCase()(campos).hayErrores) return null

        val categoria = campos.categoria ?: return null
        val peso = campos.pesoKg.aDecimalOrNull() ?: return null
        val precioNormal = campos.precioNormal.aDecimalOrNull() ?: return null
        val precioRescate = campos.precioRescate.aDecimalOrNull() ?: return null
        val cantidad = campos.cantidadDisponible.aEnteroPositivoOrNull() ?: return null

        return repositorio.agregar(
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
    }
}
