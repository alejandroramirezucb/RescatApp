package com.rescatapp.features.auth.ui.login

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.BotonPrincipal
import com.rescatapp.core.designsystem.theme.FondoPantalla
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.designsystem.theme.Verde
import com.rescatapp.features.auth.domain.model.SesionUsuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginExitoso: (SesionUsuario) -> Unit,
    onIrARegistro: () -> Unit,
    onVolver: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(estado.sesionExitosa) {
        estado.sesionExitosa?.let { sesion ->
            viewModel.consumirSesionExitosa()
            onLoginExitoso(sesion)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión", fontWeight = FontWeight.Bold) },
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
            EncabezadoLogin()

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (estado.errorGeneral != null) {
                        BannerError(mensaje = estado.errorGeneral!!)
                    }

                    CampoTextoCorreo(
                        valor = estado.credenciales.correo,
                        error = estado.errores.correo,
                        onValorCambiado = viewModel::onCorreoCambiado
                    )

                    CampoTextoContrasena(
                        valor = estado.credenciales.contrasena,
                        error = estado.errores.contrasena,
                        visible = estado.contrasenaVisible,
                        onValorCambiado = viewModel::onContrasenaCambia,
                        onToggleVisibilidad = viewModel::onToggleVisibilidadContrasena,
                        onImeAction = {
                            focusManager.clearFocus()
                            viewModel.iniciarSesion()
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
                            texto = "Iniciar sesión",
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.iniciarSesion()
                            },
                            habilitado = estado.botonHabilitado
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            FilaDivisor(texto = "O accede con cuentas demo")

            Spacer(Modifier.height(14.dp))

            BotonesAccesoRapidoDemo(
                onAccesoDemo = { esNegocio ->
                    focusManager.clearFocus()
                    viewModel.iniciarSesionDemo(esNegocio)
                },
                habilitado = !estado.cargando
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes una cuenta?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onIrARegistro) {
                    Text(
                        text = "Regístrate aquí",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun EncabezadoLogin() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Verde.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = null,
                tint = Verde,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = "¡Bienvenido a RescatApp!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Inicia sesión para rescatar alimentos o gestionar tu comercio",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CampoTextoCorreo(valor: String, error: String?, onValorCambiado: (String) -> Unit) {
    Column {
        Text(
            text = "Correo electrónico",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onValorCambiado,
            placeholder = { Text("ejemplo@correo.com") },
            leadingIcon = {
                Icon(Icons.Outlined.Email, contentDescription = null)
            },
            isError = error != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
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
private fun CampoTextoContrasena(
    valor: String,
    error: String?,
    visible: Boolean,
    onValorCambiado: (String) -> Unit,
    onToggleVisibilidad: () -> Unit,
    onImeAction: () -> Unit
) {
    Column {
        Text(
            text = "Contraseña",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onValorCambiado,
            placeholder = { Text("Mínimo 6 caracteres") },
            leadingIcon = {
                Icon(Icons.Outlined.Lock, contentDescription = null)
            },
            trailingIcon = {
                IconButton(onClick = onToggleVisibilidad) {
                    val icono = if (visible) {
                        Icons.Outlined.VisibilityOff
                    } else {
                        Icons.Outlined.Visibility
                    }
                    Icon(
                        imageVector = icono,
                        contentDescription = if (visible) "Ocultar" else "Mostrar"
                    )
                }
            },
            visualTransformation = if (visible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            isError = error != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
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

@Composable
private fun FilaDivisor(texto: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text = texto,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun BotonesAccesoRapidoDemo(
    onAccesoDemo: (esNegocio: Boolean) -> Unit,
    habilitado: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = { onAccesoDemo(false) },
            enabled = habilitado,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Verde)
            Spacer(Modifier.width(8.dp))
            Text("Entrar como Cliente Demo")
        }

        OutlinedButton(
            onClick = { onAccesoDemo(true) },
            enabled = habilitado,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Storefront, contentDescription = null, tint = Naranja)
            Spacer(Modifier.width(8.dp))
            Text("Entrar como Negocio Demo")
        }
    }
}
