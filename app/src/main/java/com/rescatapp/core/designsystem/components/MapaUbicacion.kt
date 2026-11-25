package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

private val CENTRO_SANTA_CRUZ = GeoPoint(-17.7833, -63.1821)

@Composable
fun MapaUbicacion(
    latitud: Double?,
    longitud: Double?,
    modifier: Modifier = Modifier,
    onPuntoElegido: ((Double, Double) -> Unit)? = null
) {
    val elegirPunto by rememberUpdatedState(onPuntoElegido)
    AndroidView(
        modifier = modifier.fillMaxWidth().height(220.dp).clip(MaterialTheme.shapes.large),
        factory = { contexto ->
            Configuration.getInstance().userAgentValue = contexto.packageName
            MapView(contexto).apply {
                setMultiTouchControls(true)
                controller.setZoom(16.0)
                controller.setCenter(
                    if (latitud != null && longitud != null) {
                        GeoPoint(latitud, longitud)
                    } else {
                        CENTRO_SANTA_CRUZ
                    }
                )
                overlays.add(
                    MapEventsOverlay(object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(punto: GeoPoint): Boolean {
                            elegirPunto?.invoke(punto.latitude, punto.longitude)
                            return elegirPunto != null
                        }

                        override fun longPressHelper(punto: GeoPoint) = false
                    })
                )
            }
        },
        update = { mapa ->
            mapa.overlays.removeAll { it is Marker }
            if (latitud != null && longitud != null) {
                mapa.overlays.add(
                    Marker(mapa).apply {
                        position = GeoPoint(latitud, longitud)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                )
            }
            mapa.invalidate()
        },
        onRelease = { it.onDetach() }
    )
}
