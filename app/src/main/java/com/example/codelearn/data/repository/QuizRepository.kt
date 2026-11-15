package com.example.codelearn.data.repository

import com.example.codelearn.data.remote.ApiClient
import com.example.codelearn.data.remote.QuizResponse
import com.example.codelearn.data.remote.QuizResultResponse
import com.example.codelearn.data.remote.QuizSubmitRequest
import com.example.codelearn.data.remote.QuizSubmitResponse
import retrofit2.Response

class QuizRepository {

    private val apiService =
        ApiClient.apiService

    suspend fun getQuiz(
        token: String,
        sessionId: Int
    ): Response<QuizResponse> {

        return apiService.getQuiz(
            "Bearer $token",
            sessionId
        )
    }

    suspend fun getQuizResult(
        token: String,
        sessionId: Int
    ): Response<QuizResultResponse> {

        return apiService.getQuizResult(
            "Bearer $token",
            sessionId
        )
    }

    suspend fun submitQuiz(
        token: String,
        sessionId: Int,
        request: QuizSubmitRequest
    ): Response<QuizSubmitResponse> {

        return apiService.submitQuiz(
            "Bearer $token",
            sessionId,
            request
        )
    }
}