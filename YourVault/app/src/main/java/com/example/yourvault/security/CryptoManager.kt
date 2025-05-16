package com.example.yourvault.security

//para compartir la clave maestra en memoria
object CryptoManager {
    lateinit var masterKey: javax.crypto.SecretKey
}
