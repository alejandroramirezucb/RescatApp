package com.rescatapp.core.data.local

import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.domain.model.Usuario
import org.junit.Assert.assertEquals
import org.junit.Test

class UsuarioEntityTest {

    @Test
    fun conversionHaciaDomainYHaciaEntityPreservaCampos() {
        val original = Usuario(
            id = "u-123",
            nombre = "María Lopez",
            correo = "maria@example.com",
            contrasenaHash = "hash123",
            rol = RolUsuario.CLIENTE,
            telefono = "12345678",
            direccion = "Calle Principal 1",
            nombreNegocio = null,
            fechaCreacion = 1000L
        )

        val entity = original.toEntity()
        val recuperado = entity.toDomain()

        assertEquals(original, recuperado)
        assertEquals("u-123", entity.id)
        assertEquals("maria@example.com", entity.correo)
        assertEquals(RolUsuario.CLIENTE, entity.rol)
    }

    @Test
    fun usuarioNegocioPreservaNombreNegocio() {
        val original = Usuario(
            id = "u-negocio-1",
            nombre = "Juan Perez",
            correo = "pan@example.com",
            contrasenaHash = "hash456",
            rol = RolUsuario.NEGOCIO,
            telefono = "87654321",
            direccion = "Av Central 45",
            nombreNegocio = "Panadería Del Sol",
            fechaCreacion = 2000L
        )

        val entity = original.toEntity()
        assertEquals("Panadería Del Sol", entity.nombreNegocio)
        assertEquals(original, entity.toDomain())
    }
}
