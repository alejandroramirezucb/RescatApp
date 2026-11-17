package com.rescatapp.core.data.supabase

import com.rescatapp.core.data.remote.FuenteOfertasRemotas
import com.rescatapp.core.model.Oferta
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class FuenteOfertasSupabase @Inject constructor(private val conexion: SupabaseConexion) :
    FuenteOfertasRemotas {
    override suspend fun obtenerOfertasRemotas(): List<Oferta> {
        conexion.sincronizar()
        return conexion.ofertas.first().filter {
            it.propietarioId ==
                conexion.sesion.value?.usuarioId
        }
    }

    override suspend fun crearOferta(oferta: Oferta): Oferta {
        // Todas las ofertas ya se guardan en el servidor. Publicar de nuevo no duplica registros.
        conexion.cambiarEstadoOferta(oferta.id, "ACTIVA")
        return oferta.copy(estadoPublicacion = "ACTIVA")
    }

    override suspend fun editarOferta(id: Int, ofertaActualizada: Oferta): Oferta =
        conexion.guardarOferta(ofertaActualizada.copy(id = id), nueva = false)

    override suspend fun eliminarOferta(id: Int) = conexion.eliminarOferta(id)
}
