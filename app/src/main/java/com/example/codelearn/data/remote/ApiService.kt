package com.example.codelearn.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("api/health")
    suspend fun checkHealth(): Response<HealthResponse>
}

data class HealthResponse(
    val message: String
)