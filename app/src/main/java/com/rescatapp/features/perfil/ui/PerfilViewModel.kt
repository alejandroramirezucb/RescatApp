package com.rescatapp.features.perfil.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioAutenticacion
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.domain.CalcularImpactoUseCase
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.util.suscripcionPantalla
import com.rescatapp.features.auth.domain.model.SesionUsuario
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class PerfilViewModel @Inject constructor(
    repositorioPedidos: RepositorioPedidos,
    private val calcularImpacto: CalcularImpactoUseCase,
    private val repositorioAutenticacion: RepositorioAutenticacion,
    private val repositorioOfertas: RepositorioOfertas
) : ViewModel() {
    private val puntoElegido = MutableStateFlow<Pair<Double, Double>?>(null)
    private val mensaje = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PerfilUiState> = combine(
        repositorioPedidos.pedidos,
        repositorioAutenticacion.sesionActual,
        repositorioOfertas.propias,
        puntoElegido,
        mensaje
    ) { pedidos, sesion, propias, punto, mensaje ->
        val guardada = propias.ubicacionGuardada()
        crearEstado(pedidos, sesion, punto ?: guardada)
            .copy(ubicacionPendiente = punto != null && punto != guardada, mensaje = mensaje)
    }.stateIn(
        scope = viewModelScope,
        started = suscripcionPantalla,
        initialValue = crearEstado(emptyList(), null, null)
    )

    fun cerrarSesion() {
        viewModelScope.launch { repositorioAutenticacion.cerrarSesion() }
    }

    fun elegirUbicacion(latitud: Double, longitud: Double) {
        puntoElegido.value = latitud to longitud
    }

    fun guardarUbicacion() {
        val (latitud, longitud) = puntoElegido.value ?: return
        viewModelScope.launch {
            mensaje.value = try {
                repositorioOfertas.guardarUbicacion(latitud, longitud)
                "Ubicación de la tienda guardada."
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                "No se pudo guardar la ubicación. Intenta nuevamente."
            }
        }
    }

    fun limpiarMensaje() {
        mensaje.value = null
    }

    private fun List<Oferta>.ubicacionGuardada(): Pair<Double, Double>? =
        firstNotNullOfOrNull { oferta ->
            oferta.latitud?.let { latitud -> oferta.longitud?.let { latitud to it } }
        }

    private fun crearEstado(
        pedidos: List<Pedido>,
        sesion: SesionUsuario?,
        ubicacion: Pair<Double, Double>?
    ): PerfilUiState {
        val rol = sesion?.rol ?: RolUsuario.CLIENTE
        return PerfilUiState(
            impacto = calcularImpacto(pedidos),
            nombre = sesion?.let { it.nombreNegocio ?: it.nombre } ?: "Invitado",
            correo = sesion?.correo.orEmpty(),
            esNegocio = rol == RolUsuario.NEGOCIO,
            sesion = sesion,
            latitudTienda = ubicacion?.first,
            longitudTienda = ubicacion?.second
        )
    }
}
