package com.rescatapp.core.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeUsuarioDao(usuariosIniciales: List<UsuarioEntity> = emptyList()) : UsuarioDao {

    private val usuarios = MutableStateFlow(usuariosIniciales)

    override suspend fun insertar(usuario: UsuarioEntity): Long {
        if (usuarios.value.any { it.correo.equals(usuario.correo, ignoreCase = true) }) {
            throw IllegalStateException("Unique constraint violated for correo: ${usuario.correo}")
        }
        usuarios.update { it + usuario }
        return 1L
    }

    override suspend fun insertarTodos(usuarios: List<UsuarioEntity>) {
        this.usuarios.update { lista ->
            lista + usuarios.filterNot { nuevo ->
                lista.any { it.id == nuevo.id || it.correo.equals(nuevo.correo, ignoreCase = true) }
            }
        }
    }

    override suspend fun buscarPorCorreo(correo: String): UsuarioEntity? =
        usuarios.value.firstOrNull { it.correo.equals(correo, ignoreCase = true) }

    override suspend fun buscarPorId(id: String): UsuarioEntity? =
        usuarios.value.firstOrNull { it.id == id }

    override fun observarTodos(): Flow<List<UsuarioEntity>> = usuarios.asStateFlow()

    override suspend fun obtenerTodos(): List<UsuarioEntity> = usuarios.value

    override suspend fun eliminarPorId(id: String): Int {
        val antes = usuarios.value.size
        usuarios.update { lista -> lista.filterNot { it.id == id } }
        return antes - usuarios.value.size
    }

    override suspend fun eliminarTodos(): Int {
        val total = usuarios.value.size
        usuarios.value = emptyList()
        return total
    }
}
