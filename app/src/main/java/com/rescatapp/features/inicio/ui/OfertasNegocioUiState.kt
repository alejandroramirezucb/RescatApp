package com.rescatapp.features.inicio.ui

import com.rescatapp.core.model.EstadoRemoto
import com.rescatapp.core.model.Oferta

enum class PestanaOfertas(val titulo: String) {
    ACTIVAS("Activas"),
    FINALIZADAS("Finalizadas"),
    EN_LINEA("En línea")
}

enum class TipoOperacionOferta { PUBLICAR, EDITAR, ELIMINAR }

data class OperacionOferta(val tipo: TipoOperacionOferta, val oferta: Oferta)

data class OfertasNegocioUiState(
    val pestana: PestanaOfertas = PestanaOfertas.ACTIVAS,
    val activas: List<Oferta> = emptyList(),
    val finalizadas: List<Oferta> = emptyList(),
    val guardandoLocal: Boolean = false,
    val eliminandoLocal: Boolean = false,
    val errorLocal: String? = null,
    val estadoRemoto: EstadoRemoto<List<Oferta>> = EstadoRemoto.Exito(emptyList()),
    val operacionEnCurso: OperacionOferta? = null,
    val operacionFallida: OperacionOferta? = null,
    val errorOperacion: String? = null,
    val mensaje: String? = null
) {
    val ocupado: Boolean
        get() = guardandoLocal || eliminandoLocal ||
            estadoRemoto is EstadoRemoto.Cargando || operacionEnCurso != null
}
