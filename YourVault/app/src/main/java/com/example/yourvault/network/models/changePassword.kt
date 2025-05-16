package com.example.yourvault.network.models

data class ChangePasswordRequest(
    val email: String,
    val new_password: String
)
