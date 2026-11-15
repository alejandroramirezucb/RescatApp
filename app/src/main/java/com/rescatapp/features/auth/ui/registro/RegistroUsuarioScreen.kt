package com.rescatapp.features.auth.ui.registro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.BotonPrincipal
import com.rescatapp.core.designsystem.theme.FondoPantalla
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.designsystem.theme.Verde
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.domain.model.SesionUsuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroUsuarioScreen(
    onRegistroExitoso: (SesionUsuario) -> Unit,
    onIrALogin: () -> Unit,
    onVolver: () -> Unit,
    viewModel: RegistroUsuarioViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(estado.sesionExitosa) {
        estado.sesionExitosa?.let { onRegistroExitoso(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = FondoPantalla
    ) { espacioInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacioInterno)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SelectorRol(
                rolSeleccionado = estado.datos.rol,
                onRolSeleccionado = viewModel::onRolCambiado
            )

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (estado.errorGeneral != null) {
                        BannerError(mensaje = estado.errorGeneral!!)
                    }

                    CampoFormulario(
                        etiqueta = if (estado.esNegocio) "Nombre del responsable" else "Nombre completo",
                        valor = estado.datos.nombre,
                        ejemplo = if (estado.esNegocio) "Ej. Roberto Gómez" else "Ej. María González",
                        icono = Icons.Default.Person,
                        error = estado.errores.nombre,
                        onValorCambiado = viewModel::onNombreCambiado
                    )

                    if (estado.esNegocio) {
                        CampoFormulario(
                            etiqueta = "Nombre del comercio o negocio",
                            valor = estado.datos.nombreNegocio,
                            ejemplo = "Ej. Pastelería La Delicia",
                            icono = Icons.Default.Storefront,
                            error = estado.errores.nombreNegocio,
                            onValorCambiado = viewModel::onNombreNegocioCambiado
                        )
                    }

                    CampoFormulario(
                        etiqueta = "Correo electrónico",
                        valor = estado.datos.correo,
                        ejemplo = "ejemplo@correo.com",
                        icono = Icons.Outlined.Email,
                        error = estado.errores.correo,
                        tipoTeclado = KeyboardType.Email,
                        onValorCambiado = viewModel::onCorreoCambiado
                    )

                    CampoFormulario(
                        etiqueta = "Teléfono de contacto",
                        valor = estado.datos.telefono,
                        ejemplo = "+56 9 1234 5678",
                        icono = Icons.Default.Phone,
                        tipoTeclado = KeyboardType.Phone,
                        onValorCambiado = viewModel::onTelefonoCambiado
                    )

                    CampoFormulario(
                        etiqueta = if (estado.esNegocio) "Dirección del local" else "Dirección (opcional)",
                        valor = estado.datos.direccion,
                        ejemplo = "Av. Las Palmeras 123",
                        icono = Icons.Default.LocationOn,
                        onValorCambiado = viewModel::onDireccionCambia
                    )

                    CampoContrasena(
                        etiqueta = "Contraseña",
                        valor = estado.datos.contrasena,
                        ejemplo = "Mínimo 6 caracteres",
                        visible = estado.contrasenaVisible,
                        error = estado.errores.contrasena,
                        onValorCambiado = viewModel::onContrasenaCambia,
                        onToggleVisibilidad = viewModel::onToggleVisibilidadContrasena,
                        imeAction = ImeAction.Next
                    )

                    CampoContrasena(
                        etiqueta = "Confirmar contraseña",
                        valor = estado.datos.confirmarContrasena,
                        ejemplo = "Repite tu contraseña",
                        visible = estado.confirmarContrasenaVisible,
                        error = estado.errores.confirmarContrasena,
                        onValorCambiado = viewModel::onConfirmarContrasenaCambia,
                        onToggleVisibilidad = viewModel::onToggleVisibilidadConfirmarContrasena,
                        imeAction = ImeAction.Done,
                        onImeAction = {
                            focusManager.clearFocus()
                            viewModel.registrarUsuario(onRegistroExitoso)
                        }
                    )

                    Spacer(Modifier.height(4.dp))

                    if (estado.cargando) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        BotonPrincipal(
                            texto = if (estado.esNegocio) "Registrar mi negocio" else "Registrarme como cliente",
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.registrarUsuario(onRegistroExitoso)
                            },
                            habilitado = estado.botonHabilitado
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Ya tienes una cuenta?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onIrALogin) {
                    Text(
                        text = "Inicia sesión",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectorRol(
    rolSeleccionado: RolUsuario,
    onRolSeleccionado: (RolUsuario) -> Unit
) {
    val indice = if (rolSeleccionado == RolUsuario.CLIENTE) 0 else 1

    TabRow(
        selectedTabIndex = indice,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Tab(
            selected = rolSeleccionado == RolUsuario.CLIENTE,
            onClick = { onRolSeleccionado(RolUsuario.CLIENTE) },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = if (rolSeleccionado == RolUsuario.CLIENTE) Verde else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Cliente")
                }
            }
        )
        Tab(
            selected = rolSeleccionado == RolUsuario.NEGOCIO,
            onClick = { onRolSeleccionado(RolUsuario.NEGOCIO) },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Storefront,
                        contentDescription = null,
                        tint = if (rolSeleccionado == RolUsuario.NEGOCIO) Naranja else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Negocio / Comercio")
                }
            }
        )
    }
}

@Composable
private fun CampoFormulario(
    etiqueta: String,
    valor: String,
    ejemplo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    error: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    onValorCambiado: (String) -> Unit
) {
    Column {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onValorCambiado,
            placeholder = { Text(ejemplo) },
            leadingIcon = { Icon(icono, contentDescription = null) },
            isError = error != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Composable
private fun CampoContrasena(
    etiqueta: String,
    valor: String,
    ejemplo: String,
    visible: Boolean,
    error: String?,
    onValorCambiado: (String) -> Unit,
    onToggleVisibilidad: () -> Unit,
    imeAction: ImeAction = ImeAction.Default,
    onImeAction: () -> Unit = {}
) {
    Column {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onValorCambiado,
            placeholder = { Text(ejemplo) },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = onToggleVisibilidad) {
                    Icon(
                        imageVector = if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (visible) "Ocultar" else "Mostrar"
                    )
                }
            },
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = error != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(onDone = { onImeAction() }),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Composable
private fun BannerError(mensaje: String) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(12.dp)
        )
    }
}
