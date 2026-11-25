package com.rescatapp.features.perfil.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.rescatapp.core.designsystem.components.BotonPrincipal
import com.rescatapp.core.designsystem.components.MapaUbicacion

@Composable
internal fun UbicacionTienda(
    estado: PerfilUiState,
    onElegirUbicacion: (Double, Double) -> Unit,
    onGuardarUbicacion: () -> Unit
) {
    TarjetaPerfil(titulo = "Ubicación de mi tienda") {
        Text(
            text = "Toca el mapa para marcar dónde retiran los pedidos.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        MapaUbicacion(
            latitud = estado.latitudTienda,
            longitud = estado.longitudTienda,
            onPuntoElegido = onElegirUbicacion
        )
        BotonPrincipal(
            texto = "Guardar ubicación",
            onClick = onGuardarUbicacion,
            habilitado = estado.ubicacionPendiente
        )
    }
}
