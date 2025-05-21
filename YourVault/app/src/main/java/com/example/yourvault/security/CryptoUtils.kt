package com.example.yourvault.security

import android.util.Base64
import java.nio.charset.Charset
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {
    private const val AES_ALGO = "AES/GCM/NoPadding"
    private const val KEY_DERIVATION = "PBKDF2WithHmacSHA256" //el hash
    private const val IV_SIZE = 12 // iv es como un clave secreta que no se repite
    private const val TAG_SIZE = 128
    private const val ITERATIONS = 65536 // las veces que se repite el hash
    private const val KEY_LENGTH = 256

    /** clave AES(128 bits) con contraseña maestra (user) y un salt fijo (de momento en variable). */
    fun deriveKey(masterPassword: String, salt: ByteArray): SecretKey {
        val spec = PBEKeySpec(masterPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance(KEY_DERIVATION)
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    /** encripta y devuelve IV + ciphertext (texto plano cifrado:ilegible) codificados en Base64 (sistema de codificacion) */
    fun encrypt(plain: String, key: SecretKey): String {
        val cipher = Cipher.getInstance(AES_ALGO)
        val iv = ByteArray(IV_SIZE).apply { SecureRandom().nextBytes(this) }
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_SIZE, iv))
        val cipherText = cipher.doFinal(plain.toByteArray(Charset.forName("UTF-8")))
        // concatenamos iv + ciphertext
        return Base64.encodeToString(iv + cipherText, Base64.NO_WRAP)
    }

    /** desencripta el Base64 (iv + ciphertext) y devuelve el texto */
    fun decrypt(encrypted: String, key: SecretKey): String {
        val decoded = Base64.decode(encrypted, Base64.NO_WRAP)
        val iv = decoded.copyOfRange(0, IV_SIZE)
        val cipherText = decoded.copyOfRange(IV_SIZE, decoded.size)
        val cipher = Cipher.getInstance(AES_ALGO)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_SIZE, iv))
        val plain = cipher.doFinal(cipherText)
        return String(plain, Charset.forName("UTF-8"))
    }

    fun keyFromBase64(base64: String): SecretKey { /**desencriptar la masterkey de BD**/
        val keyBytes = Base64.decode(base64, Base64.NO_WRAP)
        return SecretKeySpec(keyBytes, "AES")
    }
}
