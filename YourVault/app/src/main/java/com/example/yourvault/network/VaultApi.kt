package com.example.yourvault.network

import com.example.yourvault.network.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface VaultApi {
    @POST("register.php")
    suspend fun register(@Body req: RegisterRequest): Response<RegisterResponse>

    @POST("login.php")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>

    @POST("change_password.php")
    suspend fun changePassword(@Body req: RegisterRequest): Response<RegisterResponse> //pide mismos parametros, reutilizacion
}
