package com.example.yourvault.data.models

data class PasswordEntry(
    val id: Int = 0,
    val name: String,
    val username: String,
    val passwordHash: String
)
