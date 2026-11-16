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
}
