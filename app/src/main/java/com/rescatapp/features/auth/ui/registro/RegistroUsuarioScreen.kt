package com.rescatapp.features.auth.ui.registro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.CampoConError
import com.rescatapp.core.designsystem.components.SelectorPestanas
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.ui.BotonEnviarAuth
import com.rescatapp.features.auth.ui.EnlaceAuth
import com.rescatapp.features.auth.ui.EtiquetaSeccionAuth
import com.rescatapp.features.auth.ui.MensajeErrorAuth
import com.rescatapp.features.auth.ui.PantallaAuth

@Composable
fun RegistroUsuarioScreen(
    onRegistroExitoso: () -> Unit,
    onIrALogin: () -> Unit,
    onVolver: () -> Unit,
    viewModel: RegistroUsuarioViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val datos = estado.datos
    val errores = estado.errores
    val actualizar = viewModel::actualizarDatos

    LaunchedEffect(estado.sesionIniciada) {
        if (estado.sesionIniciada) onRegistroExitoso()
    }

    PantallaAuth(
        titulo = "Crear cuenta",
        onVolver = onVolver,
        descripcion = "Únete como cliente o comparte las ofertas de tu negocio."
    ) {
        EtiquetaSeccionAuth("Elige tu perfil")
        SelectorPestanas(
            titulos = listOf("Cliente", "Negocio / Comercio"),
            indiceSeleccionado = datos.rol.ordinal,
            onSeleccion = { actualizar(datos.copy(rol = RolUsuario.entries[it])) }
        )
        MensajeErrorAuth(estado.errorGeneral)
        if (estado.confirmacionPendiente) {
            androidx.compose.material3.Text(
                "Revisa tu correo y confirma tu cuenta. Despues podras iniciar sesion."
            )
        }
        EtiquetaSeccionAuth("Tus datos")
        CampoConError(
            etiqueta = if (estado.esNegocio) "Nombre del responsable" else "Nombre completo",
            valor = datos.nombre,
            onValorCambiado = { actualizar(datos.copy(nombre = it)) },
            ejemplo = "Ej. María González",
            error = errores.nombre
        )
        if (estado.esNegocio) {
            CampoConError(
                etiqueta = "Nombre del comercio o negocio",
                valor = datos.nombreNegocio,
                onValorCambiado = { actualizar(datos.copy(nombreNegocio = it)) },
                ejemplo = "Ej. Pastelería La Delicia",
                error = errores.nombreNegocio
            )
        }
        CampoConError(
            etiqueta = "Correo electrónico",
            valor = datos.correo,
            onValorCambiado = { actualizar(datos.copy(correo = it)) },
            ejemplo = "ejemplo@correo.com",
            error = errores.correo,
            tipoTeclado = KeyboardType.Email
        )
        CampoConError(
            etiqueta = "Teléfono de contacto",
            valor = datos.telefono,
            onValorCambiado = { actualizar(datos.copy(telefono = it)) },
            ejemplo = "Ej. 71234567",
            tipoTeclado = KeyboardType.Phone
        )
        CampoConError(
            etiqueta = if (estado.esNegocio) "Dirección del local" else "Dirección (opcional)",
            valor = datos.direccion,
            onValorCambiado = { actualizar(datos.copy(direccion = it)) },
            ejemplo = "Av. Las Palmeras 123"
        )
        EtiquetaSeccionAuth("Acceso a tu cuenta")
        CampoConError(
            etiqueta = "Contraseña",
            valor = datos.contrasena,
            onValorCambiado = { actualizar(datos.copy(contrasena = it)) },
            ejemplo = "Mínimo 6 caracteres",
            error = errores.contrasena,
            tipoTeclado = KeyboardType.Password
        )
        CampoConError(
            etiqueta = "Confirmar contraseña",
            valor = datos.confirmarContrasena,
            onValorCambiado = { actualizar(datos.copy(confirmarContrasena = it)) },
            ejemplo = "Repite tu contraseña",
            error = errores.confirmarContrasena,
            tipoTeclado = KeyboardType.Password
        )
        BotonEnviarAuth(
            texto = if (estado.esNegocio) "Registrar mi negocio" else "Registrarme como cliente",
            cargando = estado.cargando,
            habilitado = estado.botonHabilitado,
            onClick = viewModel::registrarUsuario
        )
        EnlaceAuth("¿Ya tienes una cuenta?", "Inicia sesión", onIrALogin)
    }
}
