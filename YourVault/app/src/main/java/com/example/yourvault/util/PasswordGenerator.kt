package com.example.yourvault.util

import kotlin.random.Random // libreria para generar numeros aleatorios

object PasswordGenerator {
    private const val UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val LOWER = "abcdefghijklmnopqrstuvwxyz"
    private const val DIGITS = "0123456789"
    private const val SYMBOLS = "!@#\$%^&*()-_+=<>?"

    /** combinando tipos si estan en true */
    fun generate(
        length: Int = 16,
        useUpper: Boolean = true,
        useLower: Boolean = true,
        useDigits: Boolean = true,
        useSymbols: Boolean = true
    ): String {
        val pool = buildString {
            if (useUpper) append(UPPER)
            if (useLower) append(LOWER)
            if (useDigits) append(DIGITS)
            if (useSymbols) append(SYMBOLS)
        }
        require(pool.isNotEmpty()) { "Debes habilitar al menos un tipo de carácter" }
        return (1..length)
            .map { pool[Random.nextInt(pool.length)] }
            .joinToString("")
    }
}
