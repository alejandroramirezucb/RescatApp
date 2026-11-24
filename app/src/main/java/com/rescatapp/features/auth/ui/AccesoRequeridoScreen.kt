package com.rescatapp.features.auth.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.rescatapp.core.designsystem.components.BotonPrincipal
import com.rescatapp.core.designsystem.components.BotonSecundario

@Composable
fun AccesoRequeridoScreen(
    onIrALogin: () -> Unit,
    onIrARegistro: () -> Unit,
    onVolver: () -> Unit,
    requiereNegocio: Boolean = false
) {
    val titulo = if (requiereNegocio) "Acceso para comercios" else "Accede a tu cuenta"
    val descripcion = if (requiereNegocio) {
        "Inicia sesión con una cuenta de negocio o registra tu comercio para publicar " +
            "y gestionar ofertas."
    } else {
        "Inicia sesión si ya tienes una cuenta o regístrate para ver tus pedidos y " +
            "rescatar ofertas."
    }

    PantallaAuth(
        titulo = titulo,
        onVolver = onVolver,
        descripcion = descripcion
    ) {
        Text(
            text = "Elige cómo deseas continuar",
            style = MaterialTheme.typography.titleSmall
        )
        BotonPrincipal(texto = "Iniciar sesión", onClick = onIrALogin)
        BotonSecundario(texto = "Registrarse", onClick = onIrARegistro)
    }
}
