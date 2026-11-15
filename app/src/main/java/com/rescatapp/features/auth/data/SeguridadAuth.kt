package com.rescatapp.features.auth.data

import java.security.MessageDigest

object SeguridadAuth {

    private const val SALT_DEFAULT = "RescatApp_Salt_Segura_2026"

    fun hashearContrasena(contrasena: String, salt: String = SALT_DEFAULT): String {
        val entrada = "$salt:$contrasena"
        val bytes = MessageDigest.getInstance("SHA-256").digest(entrada.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verificarContrasena(
        contrasenaPlana: String,
        hashAlmacenado: String,
        salt: String = SALT_DEFAULT
    ): Boolean {
        val nuevoHash = hashearContrasena(contrasenaPlana, salt)
        return MessageDigest.isEqual(
            nuevoHash.toByteArray(Charsets.UTF_8),
            hashAlmacenado.toByteArray(Charsets.UTF_8)
        )
    }
}
