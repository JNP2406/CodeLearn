package com.example.codelearn.data.repository

import com.example.codelearn.data.remote.ApiClient
import com.example.codelearn.data.remote.CaseStudyResponse
import retrofit2.Response

class CaseStudyRepository {

    private val apiService =
        ApiClient.apiService

    suspend fun getCaseStudy(
        token: String,
        courseId: Int
    ): Response<CaseStudyResponse> {

        return apiService.getCaseStudy(
            "Bearer $token",
            courseId
        )
    }
}