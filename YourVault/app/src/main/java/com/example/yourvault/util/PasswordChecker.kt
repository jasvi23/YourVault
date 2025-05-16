package com.example.yourvault.util

data class StrengthResult(val score: Int, val warnings: List<String>)

object PasswordChecker {
    fun check(password: String): StrengthResult {
        var score = 0
        val warns = mutableListOf<String>()

        fun check(password: String): StrengthResult {
            var score = 0
            val warns = mutableListOf<String>()

            // Longitud: +2 si ≥16, +1 si ≥8
            when {
                password.length >= 16 -> score += 2
                password.length >= 8  -> { score += 1; warns += "Longitud <16" }
                else                  -> warns += "Muy corta (<8)"
            }

            // Mayúsculas
            if (password.any { it.isUpperCase() }) score += 2
            else warns += "Sin mayúsculas"

            // Minúsculas
            if (password.any { it.isLowerCase() }) score += 2
            else warns += "Sin minúsculas"

            // Dígitos
            if (password.any { it.isDigit() }) score += 2
            else warns += "Sin dígitos"

            // Símbolos
            if (password.any { !it.isLetterOrDigit() }) score += 2
            else warns += "Sin símbolos"

            return StrengthResult(score, warns)
        }


        return StrengthResult(score, warns)
    }
}
