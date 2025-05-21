package com.example.yourvault.network.models

data class LoginResponse(val userId: Int?,
                         val masterKeyHash: String? = null,
                         val error: String? = null) //para recoger las respuestas del php