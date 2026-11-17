package com.rescatapp.features.auth.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.CampoConError

@Composable
fun RecuperarAccesoScreen(
    onVolver: () -> Unit,
    viewModel: SupabaseAuthViewModel = hiltViewModel()
) {
    var correo by rememberSaveable { mutableStateOf("") }
    val ocupado by viewModel.ocupado.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()
    PantallaAuth(
        "Recuperar acceso",
        onVolver,
        "Te enviaremos un enlace para crear una nueva contrasena."
    ) {
        CampoConError("Correo electronico", correo, {
            correo = it
        }, ejemplo = "ejemplo@correo.com", tipoTeclado = KeyboardType.Email)
        mensaje?.let { Text(it) }
        BotonEnviarAuth(
            "Enviar enlace",
            ocupado,
            !ocupado && android.util.Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()
        ) {
            viewModel.recuperar(correo)
        }
    }
}

@Composable
fun DialogoNuevaContrasena(viewModel: SupabaseAuthViewModel) {
    val recuperando by viewModel.recuperando.collectAsStateWithLifecycle()
    val ocupado by viewModel.ocupado.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()
    if (!recuperando) return
    // No guardar contrasenas en SavedState ni en el bundle del proceso.
    var clave by androidx.compose.runtime.remember { mutableStateOf("") }
    var confirmacion by androidx.compose.runtime.remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { if (!ocupado) viewModel.cerrarRecuperacion() },
        title = { Text("Nueva contrasena") },
        text = {
            Column {
                CampoConError("Contrasena", clave, {
                    clave = it
                }, ejemplo = "", tipoTeclado = KeyboardType.Password)
                CampoConError("Repite la contrasena", confirmacion, {
                    confirmacion = it
                }, ejemplo = "", tipoTeclado = KeyboardType.Password)
                Text("Usa al menos 8 caracteres.")
                mensaje?.let { Text(it) }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { viewModel.cambiarContrasena(clave) },
                enabled =
                    !ocupado && clave.length >= 8 && clave == confirmacion
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = viewModel::cerrarRecuperacion, enabled = !ocupado) {
                Text("Volver")
            }
        }
    )
}
