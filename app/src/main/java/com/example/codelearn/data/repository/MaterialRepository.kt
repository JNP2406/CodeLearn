package com.example.codelearn.data.repository

import com.example.codelearn.data.remote.ApiClient
import com.example.codelearn.data.remote.MaterialListResponse
import retrofit2.Response

class MaterialRepository {

    private val apiService = ApiClient.apiService

    suspend fun getSessionMaterials(sessionId: Int): Response<MaterialListResponse> {

        return apiService.getSessionMaterials(sessionId)
    }
}