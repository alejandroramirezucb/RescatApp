package com.rescatapp.core.data.supabase

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/** Los tokens y el verificador PKCE nunca se guardan en Room ni en texto plano. */
interface AlmacenCredenciales {
    fun guardar(nombre: String, valor: String?)
    fun leer(nombre: String): String?
}

@Singleton
class AlmacenSesionSeguro @Inject constructor(@ApplicationContext context: Context) :
    AlmacenCredenciales {
    private val preferencias = context.getSharedPreferences("supabase_auth", Context.MODE_PRIVATE)

    private fun clave(): SecretKey {
        val almacen = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (almacen.getKey(ALIAS, null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(
                KeyGenParameterSpec.Builder(
                    ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
            )
        }.generateKey()
    }

    @Synchronized
    override fun guardar(nombre: String, valor: String?) {
        if (valor == null) {
            check(preferencias.edit().remove(nombre).commit())
            return
        }
        val cifrador = Cipher.getInstance("AES/GCM/NoPadding")
        cifrador.init(Cipher.ENCRYPT_MODE, clave())
        val cifrado = cifrador.doFinal(valor.toByteArray(Charsets.UTF_8))
        val texto = Base64.encodeToString(cifrador.iv + cifrado, Base64.NO_WRAP)
        check(preferencias.edit().putString(nombre, texto).commit())
    }

    @Synchronized
    override fun leer(nombre: String): String? {
        val texto = preferencias.getString(nombre, null) ?: return null
        return try {
            val bytes = Base64.decode(texto, Base64.NO_WRAP)
            val cifrador = Cipher.getInstance("AES/GCM/NoPadding")
            cifrador.init(
                Cipher.DECRYPT_MODE,
                clave(),
                GCMParameterSpec(128, bytes.copyOfRange(0, 12))
            )
            String(cifrador.doFinal(bytes.copyOfRange(12, bytes.size)), Charsets.UTF_8)
        } catch (_: Exception) {
            preferencias.edit().remove(nombre).commit()
            null
        }
    }

    private companion object {
        const val ALIAS = "rescatapp_supabase_auth_v1"
    }
}
