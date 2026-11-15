package com.example.codelearn.data.repository

import com.example.codelearn.data.remote.ApiClient
import com.example.codelearn.data.remote.AuthResponse
import com.example.codelearn.data.remote.LoginRequest
import com.example.codelearn.data.remote.RegisterRequest
import retrofit2.Response

class AuthRepository {

    private val apiService = ApiClient.apiService

    suspend fun register(
        email: String,
        password: String,
        role: String,
        name: String,
        campus: String
    ): Response<AuthResponse> {

        val request = RegisterRequest(
            email = email,
            password = password,
            role = role,
            name = name,
            campus = campus
        )

        return apiService.register(request)
    }

    suspend fun login(
        email: String,
        password: String
    ): Response<AuthResponse> {

        val request = LoginRequest(
            email = email,
            password = password
        )

        return apiService.login(request)
    }
}