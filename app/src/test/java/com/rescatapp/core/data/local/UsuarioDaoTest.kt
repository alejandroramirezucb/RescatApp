package com.rescatapp.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rescatapp.core.model.RolUsuario
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UsuarioDaoTest {

    private lateinit var database: RescatAppDatabase
    private lateinit var usuarioDao: UsuarioDao

    @Before
    fun crearBaseDatos() {
        val contexto: Context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(contexto, RescatAppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        usuarioDao = database.usuarioDao()
    }

    @After
    fun cerrarBaseDatos() {
        database.close()
    }

    @Test
    fun insertarYBuscarPorCorreoEsInsensibleAMayusculas() = runTest {
        val usuario = UsuarioEntity(
            id = "u1",
            nombre = "Carlos Diaz",
            correo = "carlos@rescatapp.com",
            contrasenaHash = "hash123",
            rol = RolUsuario.CLIENTE
        )
        usuarioDao.insertar(usuario)

        val encontrado = usuarioDao.buscarPorCorreo("CARLOS@RESCATAPP.COM")
        assertNotNull(encontrado)
        assertEquals("Carlos Diaz", encontrado?.nombre)
    }

    @Test
    fun buscarPorCorreoInexistenteRetornaNull() = runTest {
        val encontrado = usuarioDao.buscarPorCorreo("nadie@rescatapp.com")
        assertNull(encontrado)
    }

    @Test
    fun buscarPorIdRetornaUsuarioCorrecto() = runTest {
        val usuario = UsuarioEntity(
            id = "u-negocio-xyz",
            nombre = "Panadería Moderna",
            correo = "pan@moderna.com",
            contrasenaHash = "hash999",
            rol = RolUsuario.NEGOCIO,
            nombreNegocio = "Panadería Moderna"
        )
        usuarioDao.insertar(usuario)

        val encontrado = usuarioDao.buscarPorId("u-negocio-xyz")
        assertNotNull(encontrado)
        assertEquals(RolUsuario.NEGOCIO, encontrado?.rol)
        assertEquals("Panadería Moderna", encontrado?.nombreNegocio)
    }

    @Test
    fun eliminarPorIdEliminaRegistro() = runTest {
        val usuario = UsuarioEntity(
            id = "u-eliminar",
            nombre = "Para Eliminar",
            correo = "eliminar@rescatapp.com",
            contrasenaHash = "hash",
            rol = RolUsuario.CLIENTE
        )
        usuarioDao.insertar(usuario)

        val filasEliminadas = usuarioDao.eliminarPorId("u-eliminar")
        assertEquals(1, filasEliminadas)
        assertNull(usuarioDao.buscarPorId("u-eliminar"))
    }
}
