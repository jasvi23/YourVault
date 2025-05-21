package com.example.yourvault.util

data class StrengthResult(val score: Int, val warnings: List<String>)

object PasswordChecker { //solo puntuacion
    fun check(password: String): StrengthResult {
        var score = 0
        val warns = mutableListOf<String>()
        //longitud
        when {
            password.length >= 16 -> score += 2
            password.length >= 8 -> {
                score += 1; warns += "Longitud <16"
            }
            else -> warns += "Muy corta (<8)"
        }
        //mayus
        val uppers = password.count { it.isUpperCase() }
        score += when {
            uppers >= 2 -> 2
            uppers == 1 -> 1
            else -> { warns += "Sin mayúsculas"; 0 }
        }
        //minus
        val lowers = password.count { it.isLowerCase() }
        score += when {
            lowers >= 2 -> 2
            lowers == 1 -> 1
            else -> { warns += "Sin minúsculas"; 0 }
        }
        //digitos
        val digits = password.count { it.isDigit() }
        score += when {
            digits >= 2 -> 2
            digits == 1 -> 1
            else -> { warns += "Sin dígitos"; 0 }
        }
        //simbolos
        val symbols = password.count { !it.isLetterOrDigit() }
        score += when {
            symbols >= 2 -> 2
            symbols == 1 -> 1
            else -> { warns += "Sin símbolos"; 0 }
        }
        //devuelve puntuacion y aviso (implementar o no)
        return StrengthResult(score, warns)
    }
}
