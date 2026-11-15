package com.rescatapp.core.data.local

import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.domain.model.SesionUsuario
import org.junit.Assert.assertEquals
import org.junit.Test

class SesionEntityTest {

    @Test
    fun conversionHaciaDomainYHaciaEntityPreservaValores() {
        val original = SesionUsuario(
            usuarioId = "usr-1",
            nombre = "Ana Morales",
            correo = "ana@test.com",
            rol = RolUsuario.CLIENTE,
            nombreNegocio = null,
            token = "token-abc",
            fechaInicio = 5000L
        )

        val entity = original.toEntity()
        assertEquals(SesionEntity.ID_SESION_UNICA, entity.id)

        val domain = entity.toDomain()
        assertEquals(original, domain)
    }
}
