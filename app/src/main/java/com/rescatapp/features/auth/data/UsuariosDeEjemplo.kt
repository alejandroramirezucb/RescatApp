package com.rescatapp.features.auth.data

import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.domain.model.Usuario

object UsuariosDeEjemplo {

    val clienteDemo = Usuario(
        id = "usuario-cliente-demo",
        nombre = "Invitado",
        correo = "invitado@example.com",
        contrasenaHash = SeguridadAuth.hashearContrasena("cliente123"),
        rol = RolUsuario.CLIENTE,
        telefono = "+56 9 1234 5678",
        direccion = "Av. Providencia 1234, Santiago",
        nombreNegocio = null
    )

    val negocioDemo = Usuario(
        id = "usuario-negocio-demo",
        nombre = "Panadería La Central",
        correo = "contacto@lacentral.com",
        contrasenaHash = SeguridadAuth.hashearContrasena("negocio123"),
        rol = RolUsuario.NEGOCIO,
        telefono = "+56 9 8765 4321",
        direccion = "Calle Moneda 567, Santiago",
        nombreNegocio = "Panadería La Central"
    )

    val todos: List<Usuario> = listOf(clienteDemo, negocioDemo)
}
