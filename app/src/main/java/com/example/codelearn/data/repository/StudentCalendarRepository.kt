package com.example.codelearn.data.repository

import com.example.codelearn.data.remote.ApiClient

class StudentCalendarRepository {

    private val api =
        ApiClient.apiService

    suspend fun getStudentMentorings(
        token: String
    ) =
        api.getStudentMentorings(
            "Bearer $token"
        )

    suspend fun getCourseProgress(
        token: String,
        courseId: Int
    ) =
        api.getCourseProgress(
            "Bearer $token",
            courseId
        )
}