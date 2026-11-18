package com.example.codelearn.data.repository

import com.example.codelearn.data.remote.ApiClient
import com.example.codelearn.data.remote.CertificateListResponse
import com.example.codelearn.data.remote.ClaimCertificateResponse
import retrofit2.Response

class CertificateRepository {

    private val apiService = ApiClient.apiService

    suspend fun claimCertificate(token: String, courseId: Int): Response<ClaimCertificateResponse> {

        return apiService.claimCertificate("Bearer $token", courseId)
    }

    suspend fun getMyCertificates(token: String): Response<CertificateListResponse> {

        return apiService.getMyCertificates("Bearer $token")
    }
}