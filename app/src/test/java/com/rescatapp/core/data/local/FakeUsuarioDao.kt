package com.rescatapp.core.data.local

class FakeUsuarioDao(usuariosIniciales: List<UsuarioEntity> = emptyList()) : UsuarioDao {

    private val usuarios = usuariosIniciales.toMutableList()

    override suspend fun insertar(usuario: UsuarioEntity) {
        check(buscarPorCorreo(usuario.correo) == null)
        usuarios.add(usuario)
    }

    override suspend fun insertarTodos(usuarios: List<UsuarioEntity>) {
        usuarios.filter { buscarPorCorreo(it.correo) == null }.forEach { this.usuarios.add(it) }
    }

    override suspend fun buscarPorCorreo(correo: String): UsuarioEntity? =
        usuarios.firstOrNull { it.correo.equals(correo, ignoreCase = true) }
}
